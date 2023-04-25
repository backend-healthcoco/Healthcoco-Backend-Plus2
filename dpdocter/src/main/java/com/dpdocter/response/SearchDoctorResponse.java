package com.dpdocter.response;

import java.util.ArrayList;
import java.util.List;

import com.dpdocter.elasticsearch.beans.ESDoctorWEbSearch;

public class SearchDoctorResponse {

	List<ESDoctorWEbSearch> doctors = new ArrayList<ESDoctorWEbSearch>();

	List<ESDoctorWEbSearch> nearByDoctors = new ArrayList<ESDoctorWEbSearch>();

	private String metaData;
	private String speciality;
	private Integer count = 0;
	private String city;
	private String locality;
	private String slugCity;
	private String slugLocality;
	private String unformattedSpeciality;
	private String service;
	private String unformattedService;
	private String symptomDiseaseCondition;
	private String unformattedSymptomDiseaseCondition;

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public List<ESDoctorWEbSearch> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<ESDoctorWEbSearch> doctors) {
		this.doctors = doctors;
	}

	public List<ESDoctorWEbSearch> getNearByDoctors() {
		return nearByDoctors;
	}

	public void setNearByDoctors(List<ESDoctorWEbSearch> nearByDoctors) {
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

	public String getUnformattedSpeciality() {
		return unformattedSpeciality;
	}

	public void setUnformattedSpeciality(String unformattedSpeciality) {
		this.unformattedSpeciality = unformattedSpeciality;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getUnformattedService() {
		return unformattedService;
	}

	public void setUnformattedService(String unformattedService) {
		this.unformattedService = unformattedService;
	}

	public String getSymptomDiseaseCondition() {
		return symptomDiseaseCondition;
	}

	public void setSymptomDiseaseCondition(String symptomDiseaseCondition) {
		this.symptomDiseaseCondition = symptomDiseaseCondition;
	}

	public String getUnformattedSymptomDiseaseCondition() {
		return unformattedSymptomDiseaseCondition;
	}

	public void setUnformattedSymptomDiseaseCondition(String unformattedSymptomDiseaseCondition) {
		this.unformattedSymptomDiseaseCondition = unformattedSymptomDiseaseCondition;
	}

	public String getSlugCity() {
		return slugCity;
	}

	public void setSlugCity(String slugCity) {
		this.slugCity = slugCity;
	}

	public String getSlugLocality() {
		return slugLocality;
	}

	public void setSlugLocality(String slugLocality) {
		this.slugLocality = slugLocality;
	}

	@Override
	public String toString() {
		return "SearchDoctorResponse [doctors=" + doctors + ", nearByDoctors=" + nearByDoctors + ", metaData="
				+ metaData + ", speciality=" + speciality + ", count=" + count + ", city=" + city + ", locality="
				+ locality + ", slugCity=" + slugCity + ", slugLocality=" + slugLocality + ", unformattedSpeciality="
				+ unformattedSpeciality + ", service=" + service + ", unformattedService=" + unformattedService
				+ ", symptomDiseaseCondition=" + symptomDiseaseCondition + ", unformattedSymptomDiseaseCondition="
				+ unformattedSymptomDiseaseCondition + "]";
	}
}
