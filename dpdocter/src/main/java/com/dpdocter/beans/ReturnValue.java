package com.dpdocter.beans;

public class ReturnValue {
	private HeadersRequest headers;
	private NotifyRequest method;
	private String path;
	private String url;

	public HeadersRequest getHeaders() {
		return headers;
	}

	public void setHeaders(HeadersRequest headers) {
		this.headers = headers;
	}

	public NotifyRequest getMethod() {
		return method;
	}

	public void setMethod(NotifyRequest method) {
		this.method = method;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
