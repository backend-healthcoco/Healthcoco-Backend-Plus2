package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PatientLabTestItem;

@Document(collection = "lab_test_pickup_cl")
public class LabTestPickupCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String daughterLabCRN;
	@Field
	private Long pickupTime;
	@Field
	private Long deliveryTime;
	@Field
	private List<PatientLabTestItem> patientLabTestSamples;
	@Field
	private String status;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId daughterLabLocationId;
	@Field
	private ObjectId parentLabLocationId;
	@Field
	private Boolean discarded = false;
	@Field
	private Integer numberOfSamplesRequested;
	@Field
	private Integer numberOfSamplesPicked;
	@Field
	private String requestId;
	@Field
	private Boolean isCompleted;
	@Field
	private ObjectId collectionBoyId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDaughterLabCRN() {
		return daughterLabCRN;
	}

	public void setDaughterLabCRN(String daughterLabCRN) {
		this.daughterLabCRN = daughterLabCRN;
	}

	public Long getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(Long pickupTime) {
		this.pickupTime = pickupTime;
	}

	public Long getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(Long deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getDaughterLabLocationId() {
		return daughterLabLocationId;
	}

	public void setDaughterLabLocationId(ObjectId daughterLabLocationId) {
		this.daughterLabLocationId = daughterLabLocationId;
	}

	public ObjectId getParentLabLocationId() {
		return parentLabLocationId;
	}

	public void setParentLabLocationId(ObjectId parentLabLocationId) {
		this.parentLabLocationId = parentLabLocationId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Integer getNumberOfSamplesRequested() {
		return numberOfSamplesRequested;
	}

	public void setNumberOfSamplesRequested(Integer numberOfSamplesRequested) {
		this.numberOfSamplesRequested = numberOfSamplesRequested;
	}

	public Integer getNumberOfSamplesPicked() {
		return numberOfSamplesPicked;
	}

	public void setNumberOfSamplesPicked(Integer numberOfSamplesPicked) {
		this.numberOfSamplesPicked = numberOfSamplesPicked;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public ObjectId getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(ObjectId collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public List<PatientLabTestItem> getPatientLabTestSamples() {
		return patientLabTestSamples;
	}

	public void setPatientLabTestSamples(List<PatientLabTestItem> patientLabTestSamples) {
		this.patientLabTestSamples = patientLabTestSamples;
	}

	@Override
	public String toString() {
		return "LabTestPickupCollection [id=" + id + ", daughterLabCRN=" + daughterLabCRN + ", pickupTime=" + pickupTime
				+ ", deliveryTime=" + deliveryTime + ", patientLabTestSamples=" + patientLabTestSamples + ", status="
				+ status + ", doctorId=" + doctorId + ", daughterLabLocationId=" + daughterLabLocationId
				+ ", parentLabLocationId=" + parentLabLocationId + ", discarded=" + discarded
				+ ", numberOfSamplesRequested=" + numberOfSamplesRequested + ", numberOfSamplesPicked="
				+ numberOfSamplesPicked + ", requestId=" + requestId + ", isCompleted=" + isCompleted
				+ ", collectionBoyId=" + collectionBoyId + "]";
	}

}