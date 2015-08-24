package com.dpdocter.solr.beans;

import com.dpdocter.solr.enums.AppointmentResponseType;

public class AppointmentSearchResponse {
    private String id;

    private String response;

    private AppointmentResponseType responseType;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getResponse() {
	return response;
    }

    public void setResponse(String response) {
	this.response = response;
    }

    public AppointmentResponseType getResponseType() {
	return responseType;
    }

    public void setResponseType(AppointmentResponseType responseType) {
	this.responseType = responseType;
    }

    @Override
    public String toString() {
	return "AppointmentSearchResponse [id=" + id + ", response=" + response + ", responseType=" + responseType + "]";
    }

}
