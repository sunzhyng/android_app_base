package net.ifie.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.szylxy.libs.common.StringUtils;

import net.ifie.app.AppException;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewsList extends Entity {

	private int pageSize;
	private int lastTime;
	private List<News> newslist = new ArrayList<News>();

	public int getPageSize() {
		return pageSize;
	}

	public int getLastTime() {
		return lastTime;
	}

	public List<News> getNewslist() {
		return newslist;
	}

	public static NewsList parseJSON(InputStream inputStream) throws IOException, AppException {
		NewsList newslist = new NewsList();
		News news = null;
		try {
			String str = StringUtils.read(inputStream);
			JSONObject jsonObject = new JSONObject(str);
			JSONArray array = jsonObject.getJSONArray("content");
			int length = array.length();
			for (int i = 0; i < length; i++) {
				JSONObject object = array.getJSONObject(i);
				news = new News();
				news.id = object.getString("id");
				news.setTitle(object.getString("name"));
				news.setUrl(object.getString("thumb_url"));
				news.setPubDate(object.getString("addtime"));
				newslist.getNewslist().add(news);
				news = null;
				newslist.lastTime = object.getInt("addtime");
				newslist.pageSize = 20;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputStream.close();
		}
		return newslist;
	}

}
