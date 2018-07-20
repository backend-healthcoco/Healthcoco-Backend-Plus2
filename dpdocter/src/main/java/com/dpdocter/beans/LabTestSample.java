package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class LabTestSample extends GenericCollection {
	private String id;
	private String sampleType;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private String doctorId;
	private RateCardTestAssociation rateCardTestAssociation;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
	private Boolean isCollectedAtLab = false;
	private Boolean isHardCopyRequired;
	private Boolean isHardCopyGiven;
	private String status;
	private String sampleId;
	private String serialNumber;
	private Boolean isCompleted = false;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public RateCardTestAssociation getRateCardTestAssociation() {
		return rateCardTestAssociation;
	}

	public void setRateCardTestAssociation(RateCardTestAssociation rateCardTestAssociation) {
		this.rateCardTestAssociation = rateCardTestAssociation;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public Long getUrgentTime() {
		return urgentTime;
	}

	public void setUrgentTime(Long urgentTime) {
		this.urgentTime = urgentTime;
	}

	public Boolean getIsHardCopyRequired() {
		return isHardCopyRequired;
	}

	public void setIsHardCopyRequired(Boolean isHardCopyRequired) {
		this.isHardCopyRequired = isHardCopyRequired;
	}

	public Boolean getIsHardCopyGiven() {
		return isHardCopyGiven;
	}

	public void setIsHardCopyGiven(Boolean isHardCopyGiven) {
		this.isHardCopyGiven = isHardCopyGiven;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public Boolean getIsCollected() {
		return isCollected;
	}

	public void setIsCollected(Boolean isCollected) {
		this.isCollected = isCollected;
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

	public Boolean getIsCollectedAtLab() {
		return isCollectedAtLab;
	}

	public void setIsCollectedAtLab(Boolean isCollectedAtLab) {
		this.isCollectedAtLab = isCollectedAtLab;
	}
	
	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "LabTestSample [id=" + id + ", sampleType=" + sampleType + ", daughterLabLocationId="
				+ daughterLabLocationId + ", parentLabLocationId=" + parentLabLocationId + ", doctorId=" + doctorId
				+ ", rateCardTestAssociation=" + rateCardTestAssociation + ", isUrgent=" + isUrgent + ", urgentTime="
				+ urgentTime + ", isCollected=" + isCollected + ", isCollectedAtLab=" + isCollectedAtLab
				+ ", isHardCopyRequired=" + isHardCopyRequired + ", isHardCopyGiven=" + isHardCopyGiven + ", status="
				+ status + ", sampleId=" + sampleId + ", serialNumber=" + serialNumber + ", isCompleted=" + isCompleted
				+ "]";
	}

}
