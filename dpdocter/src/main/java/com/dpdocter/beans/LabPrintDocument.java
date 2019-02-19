package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.response.ImageURLResponse;

public class LabPrintDocument {

	private String id;

	private String patientName;

	private Location location;

	private User doctor;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<ImageURLResponse> documents;

	private String explanation;

	private Boolean discarded = false;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private Location uploadedByLocation;

	private User uploadedByDoctor;

	private String uploadedByHospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public User getDoctor() {
		return doctor;
	}

	public void setDoctor(User doctor) {
		this.doctor = doctor;
	}

	public Location getUploadedByLocation() {
		return uploadedByLocation;
	}

	public void setUploadedByLocation(Location uploadedByLocation) {
		this.uploadedByLocation = uploadedByLocation;
	}

	public User getUploadedByDoctor() {
		return uploadedByDoctor;
	}

	public void setUploadedByDoctor(User uploadedByDoctor) {
		this.uploadedByDoctor = uploadedByDoctor;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public List<ImageURLResponse> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ImageURLResponse> documents) {
		this.documents = documents;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUploadedByDoctorId() {
		return uploadedByDoctorId;
	}

	public void setUploadedByDoctorId(String uploadedByDoctorId) {
		this.uploadedByDoctorId = uploadedByDoctorId;
	}

	public String getUploadedByLocationId() {
		return uploadedByLocationId;
	}

	public void setUploadedByLocationId(String uploadedByLocationId) {
		this.uploadedByLocationId = uploadedByLocationId;
	}

	public String getUploadedByHospitalId() {
		return uploadedByHospitalId;
	}

	public void setUploadedByHospitalId(String uploadedByHospitalId) {
		this.uploadedByHospitalId = uploadedByHospitalId;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "LabPrintDocument [id=" + id + ", patientName=" + patientName + ", location=" + location
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", documents=" + documents
				+ ", explanation=" + explanation + ", discarded=" + discarded + ", uploadedByDoctorId="
				+ uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId="
				+ uploadedByHospitalId + "]";
	}

}
