package net.ifie.app.adapter;

import java.util.List;

import net.ifie.app.bean.News;
import net.ifie.app.common.BitmapManager;
import net.ifie.app.common.StringUtils;
import net.ifie.app.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewNewsAdapter extends BaseAdapter {

	private List<News> listItems;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private BitmapManager bmpManager;

	static class ListItemView {
		public TextView title;
		public TextView date;
		public ImageView flag;
	}

	public ListViewNewsAdapter(Context context, List<News> data, int resource) {
		this.listContainer = LayoutInflater.from(context);
		this.itemViewResource = resource;
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}

	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;

		if (convertView == null) {
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			listItemView.flag = (ImageView)convertView.findViewById(R.id.news_listitem_flag);
			listItemView.title = (TextView) convertView.findViewById(R.id.news_listitem_title);
			listItemView.date = (TextView) convertView.findViewById(R.id.news_listitem_date);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		News news = listItems.get(position);
		listItemView.title.setText(news.getTitle());

		String faceURL = news.getUrl();
		if (faceURL.endsWith("portrait.gif") || StringUtils.isEmpty(faceURL)) {
			listItemView.flag.setImageResource(R.drawable.widget_dface);
		} else {
			bmpManager.loadBitmap(faceURL, listItemView.flag);
		}

		listItemView.title.setTag(news);
		listItemView.date.setText(StringUtils.getDateStr(news.getPubDate()));

		return convertView;
	}
}