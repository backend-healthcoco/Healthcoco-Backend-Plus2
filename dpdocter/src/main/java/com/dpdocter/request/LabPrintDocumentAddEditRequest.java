package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.response.ImageURLResponse;

public class LabPrintDocumentAddEditRequest {

	private String id;

	private String patinetName;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String explanation;

	private List<ImageURLResponse> documents;

	private List<FileDetails> fileDetails;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private Boolean discarded = false;

	public List<ImageURLResponse> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ImageURLResponse> documents) {
		this.documents = documents;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPatinetName() {
		return patinetName;
	}

	public void setPatinetName(String patinetName) {
		this.patinetName = patinetName;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public List<FileDetails> getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(List<FileDetails> fileDetails) {
		this.fileDetails = fileDetails;
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

	@Override
	public String toString() {
		return "LabPrintDocumentAddEditRequest [id=" + id + ", patinetName=" + patinetName + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", explanation=" + explanation
				+ ", documents=" + documents + ", fileDetails=" + fileDetails + ", uploadedByDoctorId="
				+ uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId="
				+ uploadedByHospitalId + ", discarded=" + discarded + "]";
	}

}
