package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.RecordsFile;
import com.dpdocter.collections.GenericCollection;

public class DoctorLabReportResponse extends GenericCollection {
	private String id;

	private String uniqueReportId;

	private List<RecordsFile> recordsFiles;

	private String recordsLabel;

	private String explanation;

	private String patientName;

	private String mobileNumber;

	private Boolean shareWithPatient = true;

	private Boolean shareWithDoctor = true;

	private String patientId;

	private String doctorId;

	private String doctorName;

	private String locationName;

	private String locationId;

	private String hospitalId;

	private String uploadedByDoctorId;

	private String uploadedByDoctorName;

	private String uploadedByLocationId;

	private String uploadedByLocationName;

	private String uploadedByHospitalId;

	private Boolean discarded = false;

	public String getUploadedByDoctorName() {
		return uploadedByDoctorName;
	}

	public void setUploadedByDoctorName(String uploadedByDoctorName) {
		this.uploadedByDoctorName = uploadedByDoctorName;
	}

	public String getUploadedByLocationName() {
		return uploadedByLocationName;
	}

	public void setUploadedByLocationName(String uploadedByLocationName) {
		this.uploadedByLocationName = uploadedByLocationName;
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

	public String getDoctorName() {
		return doctorName;
	}

	@Override
	public String toString() {
		return "DoctorLabReportResponse [id=" + id + ", uniqueReportId=" + uniqueReportId + ", recordsFiles="
				+ recordsFiles + ", recordsLabel=" + recordsLabel + ", explanation=" + explanation + ", patientName="
				+ patientName + ", mobileNumber=" + mobileNumber + ", shareWithPatient=" + shareWithPatient
				+ ", shareWithDoctor=" + shareWithDoctor + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", doctorName=" + doctorName + ", locationName=" + locationName + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", uploadedByDoctorId=" + uploadedByDoctorId
				+ ", uploadedByDoctorName=" + uploadedByDoctorName + ", uploadedByLocationId=" + uploadedByLocationId
				+ ", uploadedByLocationName=" + uploadedByLocationName + ", uploadedByHospitalId="
				+ uploadedByHospitalId + ", discarded=" + discarded + "]";
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
