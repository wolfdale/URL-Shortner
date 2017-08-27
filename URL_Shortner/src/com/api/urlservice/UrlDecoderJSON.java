package com.api.urlservice;

public class UrlDecoderJSON {
	private String url;
	private String shortUrl;
	private String createdOn;
	private String accessedOn;
	private String fetchedVia;
	private String count;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getAccessedOn() {
		return accessedOn;
	}
	public void setAccessedOn(String accessedOn) {
		this.accessedOn = accessedOn;
	}
	public String getFetchedVia() {
		return fetchedVia;
	}
	public void setFetchedVia(String fetchedVia) {
		this.fetchedVia = fetchedVia;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
}
