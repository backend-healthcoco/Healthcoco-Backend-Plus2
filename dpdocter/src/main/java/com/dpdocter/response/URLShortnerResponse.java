package com.dpdocter.response;

public class URLShortnerResponse {

	private String result_url;

	public String getResult_url() {
		return result_url;
	}

	public void setResult_url(String result_url) {
		this.result_url = result_url;
	}

	@Override
	public String toString() {
		return "URLShortnerResponse [result_url=" + result_url + "]";
	}

}
