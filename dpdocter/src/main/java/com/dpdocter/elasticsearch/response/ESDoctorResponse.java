package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;

public class ESDoctorResponse {
	private List<ESDoctorWEbSearch> doctors;
	private String metaData;

	public List<ESDoctorWEbSearch> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<ESDoctorWEbSearch> doctors) {
		this.doctors = doctors;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

}
