package com.api.utils;

import java.sql.Timestamp;

public class UrlDecoderJSON {
	private String url;
	private String shortUrl;
	private Timestamp createdOn;
	private Timestamp accessedOn;
	private String fetchedVia;
	private int count;
	private boolean entryPresent;
	
	public boolean isEntryPresent() {
		return entryPresent;
	}
	public void setEntryPresent(boolean entryPresent) {
		this.entryPresent = entryPresent;
	}
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
	
	public String getFetchedVia() {
		return fetchedVia;
	}
	public void setFetchedVia(String fetchedVia) {
		this.fetchedVia = fetchedVia;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Timestamp getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}
	public Timestamp getAccessedOn() {
		return accessedOn;
	}
	public void setAccessedOn(Timestamp accessedOn) {
		this.accessedOn = accessedOn;
	}
}
