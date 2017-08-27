package com.api.urlservice;

public class UrlEncoderJSON {
	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return "url [URL=" + url + "]";
	}
}
