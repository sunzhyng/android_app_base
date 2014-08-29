package net.ifie.app.bean;

public class News extends Entity {

	public final static int NEWSTYPE_NEWS = 0x00;

	private String title;
	private String url;
	private String body;
	private String pubDate;

	public String getPubDate() {
		return this.pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
