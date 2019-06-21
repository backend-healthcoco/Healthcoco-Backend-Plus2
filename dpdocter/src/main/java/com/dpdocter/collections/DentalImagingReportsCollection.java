package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "dental_imaging_reports_collection")
public class DentalImagingReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId patientId;

	@Field
	private String serviceName;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ImageURLResponse report;

	@Field
	private ObjectId hospitalId;

	@Field
	private Boolean discarded = false;

	@Field
	private ObjectId uploadedByDoctorId;

	@Field
	private ObjectId uploadedByLocationId;

	@Field
	private ObjectId uploadedByHospitalId;

	@Field
	private Boolean isSharedToPatient = false;

	@Field
	private ObjectId requestId;

	@Field
	private String title;

	@Field
	private String note;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
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

	public Boolean getIsSharedToPatient() {
		return isSharedToPatient;
	}

	public void setIsSharedToPatient(Boolean isSharedToPatient) {
		this.isSharedToPatient = isSharedToPatient;
	}

	public ObjectId getRequestId() {
		return requestId;
	}

	public void setRequestId(ObjectId requestId) {
		this.requestId = requestId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ImageURLResponse getReport() {
		return report;
	}

	public void setReport(ImageURLResponse report) {
		this.report = report;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DentalImagingReportsCollection [id=" + id + ", patientId=" + patientId + ", serviceName=" + serviceName
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", report=" + report + ", hospitalId="
				+ hospitalId + ", discarded=" + discarded + ", uploadedByDoctorId=" + uploadedByDoctorId
				+ ", uploadedByLocationId=" + uploadedByLocationId + ", uploadedByHospitalId=" + uploadedByHospitalId
				+ ", isSharedToPatient=" + isSharedToPatient + ", requestId=" + requestId + ", title=" + title
				+ ", note=" + note + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}

}
