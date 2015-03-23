package com.dpdocter.request;

import java.util.List;

public class GetDoctorContactsRequest {
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private Boolean blocked=false;
	private int page;
	private int size;
	private List<String> groups;
	
	
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
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public Boolean getBlocked() {
		return blocked;
	}
	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
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
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	@Override
	public String toString() {
		return "GetDoctorContactsRequest [doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", blocked=" + blocked + ", page=" + page + ", size=" + size
				+ ", groups=" + groups + "]";
	}
	
	
}
