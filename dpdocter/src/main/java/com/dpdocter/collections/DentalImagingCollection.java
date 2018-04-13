package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DentalDiagnosticServiceRequest;

@Document(collection = "dental_imaging_cl")
public class DentalImagingCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String requestId;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId locationId;
	@Field
	private String referringDoctor;
	@Field
	private String clinicalNotes;
	@Field
	private Boolean reportsRequired;
	@Field
	private String specialInstructions;
	@Field
	private List<DentalDiagnosticServiceRequest> services;
	@Field
	private Boolean discarded = false;

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

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public String getClinicalNotes() {
		return clinicalNotes;
	}

	public void setClinicalNotes(String clinicalNotes) {
		this.clinicalNotes = clinicalNotes;
	}

	public Boolean getReportsRequired() {
		return reportsRequired;
	}

	public void setReportsRequired(Boolean reportsRequired) {
		this.reportsRequired = reportsRequired;
	}

	public String getSpecialInstructions() {
		return specialInstructions;
	}

	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}

	public List<DentalDiagnosticServiceRequest> getServices() {
		return services;
	}

	public void setServices(List<DentalDiagnosticServiceRequest> services) {
		this.services = services;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

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

	@Override
	public String toString() {
		return "DentalImagingCollection [patientId=" + patientId + ", doctorId=" + doctorId + ", hospitalId="
				+ hospitalId + ", locationId=" + locationId + ", referringDoctor=" + referringDoctor
				+ ", clinicalNotes=" + clinicalNotes + ", reportsRequired=" + reportsRequired + ", specialInstructions="
				+ specialInstructions + ", services=" + services + ", discarded=" + discarded + "]";
	}

}
