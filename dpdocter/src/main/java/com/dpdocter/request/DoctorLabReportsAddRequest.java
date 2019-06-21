package com.dpdocter.request;

public class DoctorLabReportsAddRequest {

	private String patientId;

	private String doctorName;

	private String mobileNumber;

	private String testName;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private Boolean isSharedToPatient = false;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
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

	public Boolean getIsSharedToPatient() {
		return isSharedToPatient;
	}

	public void setIsSharedToPatient(Boolean isSharedToPatient) {
		this.isSharedToPatient = isSharedToPatient;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	@Override
	public String toString() {
		return "DoctorLabReportsAddRequest [patientId=" + patientId + ", doctorName=" + doctorName + ", mobileNumber="
				+ mobileNumber + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", discarded=" + discarded + ", uploadedByDoctorId=" + uploadedByDoctorId + ", uploadedByLocationId="
				+ uploadedByLocationId + ", uploadedByHospitalId=" + uploadedByHospitalId + ", isSharedToPatient="
				+ isSharedToPatient + "]";
	}
}