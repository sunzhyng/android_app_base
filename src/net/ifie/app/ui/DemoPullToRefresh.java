package net.ifie.app.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.ifie.app.AppContext;
import net.ifie.app.AppException;
import net.ifie.app.adapter.ListViewNewsAdapter;
import net.ifie.app.bean.News;
import net.ifie.app.bean.NewsList;
import net.ifie.app.bean.Notice;
import net.ifie.app.common.StringUtils;
import net.ifie.app.common.UIHelper;
import net.ifie.app.widget.NewDataToast;
import net.ifie.app.widget.PullToRefreshListView;
import net.ifie.app.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint({ "HandlerLeak", "InflateParams" })
public class DemoPullToRefresh extends BaseActivity {

	private PullToRefreshListView lvNews;
	private ListViewNewsAdapter lvNewsAdapter;
	private View lvNews_footer;
	private TextView lvNews_foot_more;
	private ProgressBar lvNews_foot_progress;

	private List<News> lvNewsData = new ArrayList<News>();

	private Handler lvNewsHandler;
	private int lvNewsLastTime;

	private AppContext appContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		appContext = (AppContext) getApplication();

		initFrameListView();
	}

	private void initFrameListView() {
		initNewsListView();
		initFrameListViewData();
	}

	private void initFrameListViewData() {
		lvNewsHandler = this.getLvHandler(lvNews, lvNewsAdapter, lvNews_foot_more, lvNews_foot_progress, AppContext.PAGE_SIZE);
		if (lvNewsData.isEmpty()) {
			loadLvNewsData(0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_INIT);
		}
	}

	private void initNewsListView() {
		lvNewsAdapter = new ListViewNewsAdapter(this, lvNewsData, R.layout.news_listitem);
		lvNews_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvNews_foot_more = (TextView) lvNews_footer.findViewById(R.id.listview_foot_more);
		lvNews_foot_progress = (ProgressBar) lvNews_footer.findViewById(R.id.listview_foot_progress);
		lvNews = (PullToRefreshListView) findViewById(R.id.frame_listview_news);
		lvNews.addFooterView(lvNews_footer);
		lvNews.setAdapter(lvNewsAdapter);
		lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 || view == lvNews_footer)
					return;

				News news = null;
				if (view instanceof TextView) {
					news = (News) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.news_listitem_title);
					news = (News) tv.getTag();
				}
				if (news == null)
					return;

				//UIHelper.showNewsDetail(view.getContext(), news.getId(), news.getTitle(), news.getUrl());
			}
		});
		lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvNews.onScrollStateChanged(view, scrollState);

				if (lvNewsData.isEmpty())
					return;

				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvNews_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(lvNews.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvNews_foot_more.setText(R.string.load_ing);
					lvNews_foot_progress.setVisibility(View.VISIBLE);

					loadLvNewsData(lvNewsLastTime, lvNewsHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}

			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lvNews.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvNewsData(0, lvNewsHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}

	private Handler getLvHandler(final PullToRefreshListView lv, final BaseAdapter adapter, final TextView more, final ProgressBar progress, final int pageSize) {
		return new Handler() {
			@SuppressWarnings("deprecation")
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					@SuppressWarnings("unused")
					Notice notice = handleLvData(msg.what, msg.obj, msg.arg2, msg.arg1);

					if (msg.what < pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_full);
					} else if (msg.what == pageSize) {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(DemoPullToRefresh.this);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}
				progress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
	}

	private Notice handleLvData(int what, Object obj, int objtype, int actiontype) {
		Notice notice = null;
		switch (actiontype) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newdata = 0;
			NewsList nlist = (NewsList) obj;
			notice = nlist.getNotice();
			lvNewsLastTime = nlist.getLastTime();
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				if (lvNewsData.size() > 0) {
					for (News news1 : nlist.getNewslist()) {
						boolean b = false;
						for (News news2 : lvNewsData) {
							if (news1.getId() == news2.getId()) {
								b = true;
								break;
							}
						}
						if (!b)
							newdata++;
					}
				} else {
					newdata = what;
				}
			}
			lvNewsData.clear();
			lvNewsData.addAll(nlist.getNewslist());
			if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
				if (newdata > 0) {
					NewDataToast.makeText(this, getString(R.string.new_data_toast_message, newdata), appContext.isAppSound()).show();
				} else {
					NewDataToast.makeText(this, getString(R.string.new_data_toast_none), false).show();
				}
			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			NewsList list = (NewsList) obj;
			notice = list.getNotice();
			lvNewsLastTime = list.getLastTime();
			if (lvNewsData.size() > 0) {
				for (News news1 : list.getNewslist()) {
					boolean b = false;
					for (News news2 : lvNewsData) {
						if (news1.getId() == news2.getId()) {
							b = true;
							break;
						}
					}
					if (!b)
						lvNewsData.add(news1);
				}
			} else {
				lvNewsData.addAll(list.getNewslist());
			}
			break;
		}
		return notice;
	}

	private void loadLvNewsData(final int pageIndex, final Handler handler, final int action) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					NewsList list = appContext.getNewsList(pageIndex, isRefresh);
					msg.what = list.getPageSize();
					msg.obj = list;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				msg.arg1 = action;
				msg.arg2 = UIHelper.LISTVIEW_DATATYPE_NEWS;
				handler.sendMessage(msg);
			}
		}.start();
	}

}
