package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class LabReports extends GenericCollection {

	private String id;

	private String requestId;

	private String patientId;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private List<ImageURLResponse> labReports;

	private String explanation;

	private Boolean discarded = false;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private String labTestSampleId;

	private String recordsState;

	private Boolean isSharedToPatient = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
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

	public List<ImageURLResponse> getLabReports() {
		return labReports;
	}

	public void setLabReports(List<ImageURLResponse> labReports) {
		this.labReports = labReports;
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

	public String getLabTestSampleId() {
		return labTestSampleId;
	}

	public void setLabTestSampleId(String labTestSampleId) {
		this.labTestSampleId = labTestSampleId;
	}

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getIsSharedToPatient() {
		return isSharedToPatient;
	}

	public void setIsSharedToPatient(Boolean isSharedToPatient) {
		this.isSharedToPatient = isSharedToPatient;
	}

	@Override
	public String toString() {
		return "LabReports [id=" + id + ", requestId=" + requestId + ", patientId=" + patientId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", labReports=" + labReports
				+ ", explanation=" + explanation + ", discarded=" + discarded + ", uploadedByDoctorId="
				+ uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId="
				+ uploadedByHospitalId + ", labTestSampleId=" + labTestSampleId + ", recordsState=" + recordsState
				+ "]";
	}

}
