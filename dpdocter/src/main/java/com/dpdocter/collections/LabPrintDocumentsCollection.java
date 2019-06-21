package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "lab_print_documents_cl")
public class LabPrintDocumentsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String patientName;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private List<ImageURLResponse> documents;

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	public List<ImageURLResponse> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ImageURLResponse> documents) {
		this.documents = documents;
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

	@Override
	public String toString() {
		return "LabPrintDocumentsCollection [id=" + id + ", patientName=" + patientName + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", documents=" + documents + ", explanation=" + explanation
				+ ", discarded=" + discarded + ", uploadedByDoctorId=" + uploadedByDoctorId + ", uploadedByLocationId="
				+ uploadedByLocationId + ", uploadedByHospitalId=" + uploadedByHospitalId + "]";
	}

}
