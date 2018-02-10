package com.dpdocter.elasticsearch.response;

import java.util.List;

import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;

public class ESWEBResponse {
	private List<ESDoctorWEbSearch> doctors;
	private List<ESUserLocaleDocument> pharmacies;
	private List<LabResponse> labs;
	private String metaData;
	private String Speciality;
	private Integer count = 0;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

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

	public List<ESUserLocaleDocument> getPharmacies() {
		return pharmacies;
	}

	public void setPharmacies(List<ESUserLocaleDocument> pharmacies) {
		this.pharmacies = pharmacies;
	}

	public List<LabResponse> getLabs() {
		return labs;
	}

	public void setLabs(List<LabResponse> labs) {
		this.labs = labs;
	}

	public String getSpeciality() {
		return Speciality;
	}

	public void setSpeciality(String speciality) {
		Speciality = speciality;
	}

}
