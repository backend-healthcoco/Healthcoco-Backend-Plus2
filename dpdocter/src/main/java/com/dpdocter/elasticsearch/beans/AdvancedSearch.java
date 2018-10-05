package com.dpdocter.elasticsearch.beans;

import java.util.List;

public class AdvancedSearch {

	private String doctorId;
	
    private String locationId;

    private String hospitalId;

    private List<AdvancedSearchParameter> searchParameters;

    private long page;

    private int size;

    private String role;
    
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

    public long getPage() {
	return page;
    }

    public void setPage(long page) {
	this.page = page;
    }

    public int getSize() {
	return size;
    }

    public void setSize(int size) {
	this.size = size;
    }

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "AdvancedSearch [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", searchParameters=" + searchParameters + ", page=" + page + ", size=" + size + ", role=" + role
				+ "]";
	}

}
