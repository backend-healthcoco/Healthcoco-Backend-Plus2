package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;

public class LabReportsAddRequest extends GenericCollection {

	private String patientName;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String uploadedByDoctorId;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private String labTestSampleId;

	private String recordsState;

	private String serialNumber;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getPatientName() {
		return patientName;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getUploadedByDoctorId() {
		return uploadedByDoctorId;
	}

	public void setUploadedByDoctorId(String uploadedByDoctorId) {
		this.uploadedByDoctorId = uploadedByDoctorId;
	}

	@Override
	public String toString() {
		return "LabReportsAddRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + ", uploadedByDoctorId=" + uploadedByDoctorId
				+ ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId=" + uploadedByHospitalId
				+ ", labTestSampleId=" + labTestSampleId + ", recordsState=" + recordsState + "]";
	}
}