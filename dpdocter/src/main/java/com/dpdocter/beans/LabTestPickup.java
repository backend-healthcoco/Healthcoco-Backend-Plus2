package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class LabTestPickup extends GenericCollection {

	private String id;
	private String patientName;
	private String mobileNumber;
	private Integer age;
	private String gender;
	private String daughterLabCRN;
	private String parentLabCRN;
	private Long pickupTime;
	private Long deliveryTime;
	private List<String> labTestSampleIds;
	private List<LabTestSample> labTestSamples;
	private String status;
	private String doctorId;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private Boolean discarded = false;
	private Integer numberOfSamplesRequested;
	private Integer numberOfSamplesPicked;
	private String requestId;
	private Boolean isCompleted = false;
	private String collectionBoyId;
	private String serialNumber;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDaughterLabCRN() {
		return daughterLabCRN;
	}

	public void setDaughterLabCRN(String daughterLabCRN) {
		this.daughterLabCRN = daughterLabCRN;
	}

	public String getParentLabCRN() {
		return parentLabCRN;
	}

	public void setParentLabCRN(String parentLabCRN) {
		this.parentLabCRN = parentLabCRN;
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

	public List<LabTestSample> getLabTestSamples() {
		return labTestSamples;
	}

	public void setLabTestSamples(List<LabTestSample> labTestSamples) {
		this.labTestSamples = labTestSamples;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getDaughterLabLocationId() {
		return daughterLabLocationId;
	}

	public void setDaughterLabLocationId(String daughterLabLocationId) {
		this.daughterLabLocationId = daughterLabLocationId;
	}

	public String getParentLabLocationId() {
		return parentLabLocationId;
	}

	public void setParentLabLocationId(String parentLabLocationId) {
		this.parentLabLocationId = parentLabLocationId;
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

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public List<String> getLabTestSampleIds() {
		return labTestSampleIds;
	}

	public void setLabTestSampleIds(List<String> labTestSampleIds) {
		this.labTestSampleIds = labTestSampleIds;
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "LabTestPickup [id=" + id + ", patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", age="
				+ age + ", gender=" + gender + ", daughterLabCRN=" + daughterLabCRN + ", parentLabCRN=" + parentLabCRN
				+ ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime + ", labTestSampleIds="
				+ labTestSampleIds + ", labTestSamples=" + labTestSamples + ", status=" + status + ", doctorId="
				+ doctorId + ", daughterLabLocationId=" + daughterLabLocationId + ", parentLabLocationId="
				+ parentLabLocationId + ", discarded=" + discarded + ", numberOfSamplesRequested="
				+ numberOfSamplesRequested + ", numberOfSamplesPicked=" + numberOfSamplesPicked + ", requestId="
				+ requestId + ", isCompleted=" + isCompleted + ", collectionBoyId=" + collectionBoyId
				+ ", serialNumber=" + serialNumber + "]";
	}

}
