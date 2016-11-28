package com.dpdocter.elasticsearch.beans;

import java.util.List;

public class AdvancedSearch {

    private String locationId;

    private String hospitalId;

    private List<AdvancedSearchParameter> searchParameters;

    private int page;

    private int size;

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

    public List<AdvancedSearchParameter> getSearchParameters() {
	return searchParameters;
    }

    public void setSearchParameters(List<AdvancedSearchParameter> searchParameters) {
	this.searchParameters = searchParameters;
    }

    public int getPage() {
	return page;
    }

    public void setPage(int page) {
	this.page = page;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

    @Override
    public String toString() {
	return "AdvancedSearch [locationId=" + locationId + ", hospitalId=" + hospitalId + ", searchParameters=" + searchParameters
		+ ", page=" + page + ", size=" + size + "]";
    }
}
