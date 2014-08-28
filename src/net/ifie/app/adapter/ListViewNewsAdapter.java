package net.ifie.app.adapter;

import java.util.List;

import net.ifie.app.bean.News;
import net.ifie.app.common.StringUtils;
import net.ifie.app.R;
import android.content.Context;
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

	static class ListItemView {
		public TextView title;
		public TextView date;
		public ImageView flag;
	}

	public ListViewNewsAdapter(Context context, List<News> data, int resource) {

		this.listContainer = LayoutInflater.from(context);
		this.itemViewResource = resource;
		this.listItems = data;
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
			listItemView.title = (TextView) convertView.findViewById(R.id.news_listitem_title);
			listItemView.date = (TextView) convertView.findViewById(R.id.news_listitem_date);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		News news = listItems.get(position);
		listItemView.title.setText(news.getTitle());
		listItemView.title.setTag(news);
		listItemView.date.setText(StringUtils.getDateStr(news.getPubDate()));

		return convertView;
	}
}