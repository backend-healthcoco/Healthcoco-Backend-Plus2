package com.dpdocter.elasticsearch.response.v2;

import java.util.List;

import com.dpdocter.response.VaccineResponse;

public class GroupedVaccineResponse {

	private String dueDate;

	private List<VaccineResponse> vaccineResponses;

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public List<VaccineResponse> getVaccineResponses() {
		return vaccineResponses;
	}

	public void setVaccineResponses(List<VaccineResponse> vaccineResponses) {
		this.vaccineResponses = vaccineResponses;
	}

}
