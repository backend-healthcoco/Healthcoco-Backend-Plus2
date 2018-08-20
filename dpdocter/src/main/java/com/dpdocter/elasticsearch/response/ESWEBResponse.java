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
	private String city;
	private String locality;

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "ESWEBResponse [doctors=" + doctors + ", pharmacies=" + pharmacies + ", labs=" + labs + ", metaData="
				+ metaData + ", Speciality=" + Speciality + ", count=" + count + ", city=" + city + "]";
	}

}
