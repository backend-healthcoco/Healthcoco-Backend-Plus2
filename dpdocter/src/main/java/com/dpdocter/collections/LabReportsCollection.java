package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "lab_reports_cl")
public class LabReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String testName;

	@Field
	private ObjectId requestId;

	@Field
	private ObjectId patientId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private List<ImageURLResponse> labReports;

	@Field
	private String explanation;

	@Field
	private Boolean discarded = false;

	@Field
	private ObjectId uploadedByDoctorId;

	@Field
	private ObjectId uploadedByLocationId;

	@Field
	private ObjectId uploadedByHospitalId;

	@Field
	private ObjectId labTestSampleId;

	@Field
	private String recordsState;

	@Field
	private Integer uploadCounts = 0;

	@Field
	private Boolean isSharedToPatient = false;

	@Field
	private String serialNumber;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getRequestId() {
		return requestId;
	}

	public void setRequestId(ObjectId requestId) {
		this.requestId = requestId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
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

	public ObjectId getUploadedByDoctorId() {
		return uploadedByDoctorId;
	}

	public void setUploadedByDoctorId(ObjectId uploadedByDoctorId) {
		this.uploadedByDoctorId = uploadedByDoctorId;
	}

	public ObjectId getUploadedByLocationId() {
		return uploadedByLocationId;
	}

	public void setUploadedByLocationId(ObjectId uploadedByLocationId) {
		this.uploadedByLocationId = uploadedByLocationId;
	}

	public ObjectId getUploadedByHospitalId() {
		return uploadedByHospitalId;
	}

	public void setUploadedByHospitalId(ObjectId uploadedByHospitalId) {
		this.uploadedByHospitalId = uploadedByHospitalId;
	}

	public ObjectId getLabTestSampleId() {
		return labTestSampleId;
	}

	public void setLabTestSampleId(ObjectId labTestSampleId) {
		this.labTestSampleId = labTestSampleId;
	}

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}

	public List<ImageURLResponse> getLabReports() {
		return labReports;
	}

	public void setLabReports(List<ImageURLResponse> labReports) {
		this.labReports = labReports;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public Integer getUploadCounts() {
		return uploadCounts;
	}

	public void setUploadCounts(Integer uploadCounts) {
		this.uploadCounts = uploadCounts;
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "LabReportsCollection [id=" + id + ", testName=" + testName + ", requestId=" + requestId + ", patientId="
				+ patientId + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", labReports=" + labReports + ", explanation=" + explanation + ", discarded=" + discarded
				+ ", uploadedByDoctorId=" + uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId
				+ ", uploadedByHospitalId=" + uploadedByHospitalId + ", labTestSampleId=" + labTestSampleId
				+ ", recordsState=" + recordsState + ", uploadCounts=" + uploadCounts + ", isSharedToPatient="
				+ isSharedToPatient + ", serialNumber=" + serialNumber + ", isPatientDiscarded=" + isPatientDiscarded
				+ "]";
	}

}
