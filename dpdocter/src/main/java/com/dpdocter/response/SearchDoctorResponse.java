package com.dpdocter.response;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;

public class SearchDoctorResponse {

	List<ESDoctorDocument> doctors;
	
	List<ESDoctorDocument> nearByDoctors;

	private String metaData;
	private String speciality;
	private Integer count = 0;
	private String city;
	
	public List<ESDoctorDocument> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<ESDoctorDocument> doctors) {
		this.doctors = doctors;
	}

	public List<ESDoctorDocument> getNearByDoctors() {
		return nearByDoctors;
	}

	public void setNearByDoctors(List<ESDoctorDocument> nearByDoctors) {
		this.nearByDoctors = nearByDoctors;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "SearchDoctorResponse [doctors=" + doctors + ", nearByDoctors=" + nearByDoctors + ", metaData="
				+ metaData + ", speciality=" + speciality + ", count=" + count + ", city=" + city + "]";
	}
}
