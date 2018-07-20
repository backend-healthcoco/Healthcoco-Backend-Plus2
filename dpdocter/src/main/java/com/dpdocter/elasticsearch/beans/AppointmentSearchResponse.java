package com.dpdocter.elasticsearch.beans;

import com.dpdocter.enums.AppointmentResponseType;

public class AppointmentSearchResponse {
	private String id;

	private Object response;

	private AppointmentResponseType responseType;

	private String slugUrl;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getResponse() {
		return response;
	}

	public void setResponse(Object response) {
		this.response = response;
	}

	public AppointmentResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(AppointmentResponseType responseType) {
		this.responseType = responseType;
	}

	public String getSlugUrl() {
		return slugUrl;
	}

	public void setSlugUrl(String slugUrl) {
		this.slugUrl = slugUrl;
	}

	@Override
	public String toString() {
		return "AppointmentSearchResponse [id=" + id + ", response=" + response + ", responseType=" + responseType
				+ ", slugUrl=" + slugUrl + "]";
	}
}