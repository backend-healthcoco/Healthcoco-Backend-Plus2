package com.dpdocter.request;

public class RecordsSearchRequest {
	private String patientId;
	private String doctorId;
	private String tagId;
	private String locationId;
	private String hospitalId;
	
	
	
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
	public String getPatientId() {
		return patientId;
	}
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getTagId() {
		return tagId;
	}
	public void setTagId(String tagId) {
		this.tagId = tagId;
	}
	@Override
	public String toString() {
		return "RecordsSearchRequest [patientId=" + patientId + ", doctorId="
				+ doctorId + ", tagId=" + tagId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + "]";
	}
	
	
	
}
