package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DoctorLabReport extends GenericCollection {

	private String id;

	private String uniqueReportId;

	private List<RecordsFile> recordsFiles;

	private String recordsLabel;

	private String explanation;

	private String patientName;

	private String mobileNumber;

	private Boolean shareWithPatient = false;

	private Boolean shareWithDoctor = false;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String doctorName;

	private String doctorMobileNumber;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private Boolean discarded = false;

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorMobileNumber() {
		return doctorMobileNumber;
	}

	public void setDoctorMobileNumber(String doctorMobileNumber) {
		this.doctorMobileNumber = doctorMobileNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueReportId() {
		return uniqueReportId;
	}

	public void setUniqueReportId(String uniqueReportId) {
		this.uniqueReportId = uniqueReportId;
	}

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Boolean getShareWithPatient() {
		return shareWithPatient;
	}

	public void setShareWithPatient(Boolean shareWithPatient) {
		this.shareWithPatient = shareWithPatient;
	}

	public Boolean getShareWithDoctor() {
		return shareWithDoctor;
	}

	public void setShareWithDoctor(Boolean shareWithDoctor) {
		this.shareWithDoctor = shareWithDoctor;
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

	@Override
	public String toString() {
		return "DoctorLabReport [id=" + id + ", uniqueReportId=" + uniqueReportId + ", recordsFiles=" + recordsFiles
				+ ", recordsLabel=" + recordsLabel + ", explanation=" + explanation + ", patientName=" + patientName
				+ ", mobileNumber=" + mobileNumber + ", shareWithPatient=" + shareWithPatient + ", shareWithDoctor="
				+ shareWithDoctor + ", patientId=" + patientId + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", uploadedByDoctorId=" + uploadedByDoctorId
				+ ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId=" + uploadedByHospitalId
				+ ", discarded=" + discarded + "]";
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
