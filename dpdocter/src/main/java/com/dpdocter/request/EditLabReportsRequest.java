package com.dpdocter.request;

import java.util.List;

import com.dpdocter.response.ImageURLResponse;

public class EditLabReportsRequest {

	private String id;

	private List<ImageURLResponse> labReports;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ImageURLResponse> getLabReports() {
		return labReports;
	}

	public void setLabReports(List<ImageURLResponse> labReports) {
		this.labReports = labReports;
	}

}
