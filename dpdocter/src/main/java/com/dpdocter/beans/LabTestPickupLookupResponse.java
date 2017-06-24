package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class LabTestPickupLookupResponse extends GenericCollection {

	private String id;
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
	private Location parentLab;
	private Location daughterLab;
	private LabTestSample labTestSample;

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

	public List<String> getLabTestSampleIds() {
		return labTestSampleIds;
	}

	public void setLabTestSampleIds(List<String> labTestSampleIds) {
		this.labTestSampleIds = labTestSampleIds;
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

	public Location getParentLab() {
		return parentLab;
	}

	public void setParentLab(Location parentLab) {
		this.parentLab = parentLab;
	}

	public Location getDaughterLab() {
		return daughterLab;
	}

	public void setDaughterLab(Location daughterLab) {
		this.daughterLab = daughterLab;
	}

	public LabTestSample getLabTestSample() {
		return labTestSample;
	}

	public void setLabTestSample(LabTestSample labTestSample) {
		this.labTestSample = labTestSample;
	}

	@Override
	public String toString() {
		return "LabTestPickupLookupResponse [id=" + id + ", daughterLabCRN=" + daughterLabCRN + ", parentLabCRN="
				+ parentLabCRN + ", pickupTime=" + pickupTime + ", deliveryTime=" + deliveryTime + ", labTestSampleIds="
				+ labTestSampleIds + ", labTestSamples=" + labTestSamples + ", status=" + status + ", doctorId="
				+ doctorId + ", daughterLabLocationId=" + daughterLabLocationId + ", parentLabLocationId="
				+ parentLabLocationId + ", discarded=" + discarded + ", numberOfSamplesRequested="
				+ numberOfSamplesRequested + ", numberOfSamplesPicked=" + numberOfSamplesPicked + ", requestId="
				+ requestId + ", isCompleted=" + isCompleted + ", collectionBoyId=" + collectionBoyId + ", parentLab="
				+ parentLab + ", daughterLab=" + daughterLab + "]";
	}

}
