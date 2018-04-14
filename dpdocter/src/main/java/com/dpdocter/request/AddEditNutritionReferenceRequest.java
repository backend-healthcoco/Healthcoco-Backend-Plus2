package com.dpdocter.request;

import java.util.List;

import com.dpdocter.enums.GoalStatus;
import com.dpdocter.enums.RegularityStatus;
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
	private String regularityStatus = RegularityStatus.NO_ACTION.getType();
	private String goalStatus = GoalStatus.REFERRED.getType();
	private String referredDoctorId;
	private String referredLocationId;
	private String referredHospitalId;
	private String localPatientName;
	private String mobileNumber;

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

	public String getRegularityStatus() {
		return regularityStatus;
	}

	public void setRegularityStatus(String regularityStatus) {
		this.regularityStatus = regularityStatus;
	}

	public String getGoalStatus() {
		return goalStatus;
	}

	public void setGoalStatus(String goalStatus) {
		this.goalStatus = goalStatus;
	}

	public String getReferredDoctorId() {
		return referredDoctorId;
	}

	public void setReferredDoctorId(String referredDoctorId) {
		this.referredDoctorId = referredDoctorId;
	}

	public String getReferredLocationId() {
		return referredLocationId;
	}

	public void setReferredLocationId(String referredLocationId) {
		this.referredLocationId = referredLocationId;
	}

	public String getReferredHospitalId() {
		return referredHospitalId;
	}

	public void setReferredHospitalId(String referredHospitalId) {
		this.referredHospitalId = referredHospitalId;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "AddEditNutritionReferenceRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", details=" + details
				+ ", durationInMonths=" + durationInMonths + ", reports=" + reports + ", regularityStatus="
				+ regularityStatus + ", goalStatus=" + goalStatus + ", referredDoctorId=" + referredDoctorId
				+ ", referredLocationId=" + referredLocationId + ", referredHospitalId=" + referredHospitalId + "]";
	}

}
