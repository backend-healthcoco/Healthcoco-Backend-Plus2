package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EyeObservation {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private List<VisualAcuity> visualAcuities;

	private List<EyeTest> eyeTests;

	private Boolean inHistory = false;

	private Boolean discarded = false;
	
	private Boolean isOTPVerified= false;

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

	public List<VisualAcuity> getVisualAcuities() {
		return visualAcuities;
	}

	public void setVisualAcuities(List<VisualAcuity> visualAcuities) {
		this.visualAcuities = visualAcuities;
	}

	public List<EyeTest> getEyeTests() {
		return eyeTests;
	}

	public void setEyeTests(List<EyeTest> eyeTests) {
		this.eyeTests = eyeTests;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public Boolean getIsOTPVerified() {
		return isOTPVerified;
	}

	public void setIsOTPVerified(Boolean isOTPVerified) {
		this.isOTPVerified = isOTPVerified;
	}

	@Override
	public String toString() {
		return "EyeObservation [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", visualAcuities=" + visualAcuities + ", eyeTests="
				+ eyeTests + ", inHistory=" + inHistory + ", discarded=" + discarded + ", isOTPVerified="
				+ isOTPVerified + "]";
	}

}
