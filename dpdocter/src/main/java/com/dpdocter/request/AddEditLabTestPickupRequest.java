package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.LabTestSample;

public class AddEditLabTestPickupRequest {

	private String id;
	private String daughterLabCRN;
	private Long pickupTime;
	private Long deliveryTime;
	private List<LabTestSample> labTestSamples;
	private String status;
	private String doctorId;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private Boolean discarded = false;
	private Integer numberOfSamplesRequested;
	private Integer numberOfSamplesPicked;
	private String requestId;
	private Boolean isCompleted;

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

	public List<LabTestSample> getLabTestSamples() {
		return labTestSamples;
	}

	public void setLabTestSamples(List<LabTestSample> labTestSamples) {
		this.labTestSamples = labTestSamples;
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

	@Override
	public String toString() {
		return "AddEditLabTestPickupRequest [id=" + id + ", daughterLabCRN=" + daughterLabCRN + ", pickupTime="
				+ pickupTime + ", deliveryTime=" + deliveryTime + ", labTestSamples=" + labTestSamples + ", status="
				+ status + ", doctorId=" + doctorId + ", daughterLabLocationId=" + daughterLabLocationId
				+ ", parentLabLocationId=" + parentLabLocationId + ", discarded=" + discarded
				+ ", numberOfSamplesRequested=" + numberOfSamplesRequested + ", numberOfSamplesPicked="
				+ numberOfSamplesPicked + ", requestId=" + requestId + "]";
	}

}
