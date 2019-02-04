package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "dental_lab_reports_cl")
public class DentalLabReportsCollection extends GenericCollection {

	private ObjectId id;

	private String requestId;

	private ObjectId patientId;

	private ObjectId doctorId;

	private ObjectId locationId;

	private ObjectId hospitalId;

	private List<ImageURLResponse> dentalLabReports;

	private String explanation;

	private Boolean discarded = false;

	private ObjectId uploadedByDoctorId;

	private ObjectId uploadedByLocationId;

	private ObjectId uploadedByHospitalId;

	private String recordsState;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public List<ImageURLResponse> getDentalLabReports() {
		return dentalLabReports;
	}

	public void setDentalLabReports(List<ImageURLResponse> dentalLabReports) {
		this.dentalLabReports = dentalLabReports;
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

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DentalLabReportsCollection [id=" + id + ", requestId=" + requestId + ", patientId=" + patientId
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", dentalLabReports=" + dentalLabReports + ", explanation=" + explanation + ", discarded=" + discarded
				+ ", uploadedByDoctorId=" + uploadedByDoctorId + ", uploadedByLocationId=" + uploadedByLocationId
				+ ", uploadedByHospitalId=" + uploadedByHospitalId + ", recordsState=" + recordsState
				+ ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
