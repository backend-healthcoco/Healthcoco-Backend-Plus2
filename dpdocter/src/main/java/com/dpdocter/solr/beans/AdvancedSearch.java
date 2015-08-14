package com.dpdocter.solr.beans;

import java.util.List;

import com.dpdocter.solr.enums.AdvancedSearchType;

public class AdvancedSearch {
    private String doctorId;

    private String locationId;

    private String hospitalId;

    private List<AdvancedSearchType> searchTypes;

    private List<String> searchValues;

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public List<AdvancedSearchType> getSearchTypes() {
	return searchTypes;
    }

    public void setSearchTypes(List<AdvancedSearchType> searchTypes) {
	this.searchTypes = searchTypes;
    }

    public List<String> getSearchValues() {
	return searchValues;
    }

    public void setSearchValues(List<String> searchValues) {
	this.searchValues = searchValues;
    }

    @Override
    public String toString() {
	return "AdvancedSearch [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", searchTypes=" + searchTypes
		+ ", searchValues=" + searchValues + "]";
    }

}
