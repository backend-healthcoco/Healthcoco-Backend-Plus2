package com.dpdocter.request;

import java.util.List;

import com.dpdocter.response.ImageURLResponse;

public class AddEditNutritionReferenceRequest {

	private String id;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private String details;
	private Integer durationInMonths;
	private List<ImageURLResponse> reports;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Integer getDurationInMonths() {
		return durationInMonths;
	}

	public void setDurationInMonths(Integer durationInMonths) {
		this.durationInMonths = durationInMonths;
	}

	public List<ImageURLResponse> getReports() {
		return reports;
	}

	public void setReports(List<ImageURLResponse> reports) {
		this.reports = reports;
	}

	@Override
	public String toString() {
		return "AddEditNutritionReferenceRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", details=" + details
				+ ", durationInMonths=" + durationInMonths + ", reports=" + reports + "]";
	}

}
