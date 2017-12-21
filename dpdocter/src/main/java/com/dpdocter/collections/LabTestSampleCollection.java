package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RateCardTestAssociation;

@Document(collection = "lab_test_sample_cl")
public class LabTestSampleCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String sampleType;
	@Field
	private RateCardTestAssociation rateCardTestAssociation;
	@Field
	private Boolean isUrgent;
	@Field
	private Long urgentTime;
	@Field
	private Boolean isCollected = false;
	@Field
	private Boolean isHardCopyRequired;
	@Field
	private Boolean isHardCopyGiven;
	@Field
	private String status;
	@Field
	private String sampleId;
	@Field
	private ObjectId labTestPickUpId;
	@Field
	private ObjectId daughterLabLocationId;
	@Field
	private ObjectId parentLabLocationId;
	@Field
	private Boolean isCompleted = false;
	@Field
	private String serialNumber;
	@Field
	private Boolean isCollectedAtLab = false;

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

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public RateCardTestAssociation getRateCardTestAssociation() {
		return rateCardTestAssociation;
	}

	public void setRateCardTestAssociation(RateCardTestAssociation rateCardTestAssociation) {
		this.rateCardTestAssociation = rateCardTestAssociation;
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

	public Boolean getIsCollected() {
		return isCollected;
	}

	public void setIsCollected(Boolean isCollected) {
		this.isCollected = isCollected;
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

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Boolean getIsCollectedAtLab() {
		return isCollectedAtLab;
	}

	public void setIsCollectedAtLab(Boolean isCollectedAtLab) {
		this.isCollectedAtLab = isCollectedAtLab;
	}

	public ObjectId getLabTestPickUpId() {
		return labTestPickUpId;
	}

	public void setLabTestPickUpId(ObjectId labTestPickUpId) {
		this.labTestPickUpId = labTestPickUpId;
	}

	@Override
	public String toString() {
		return "LabTestSampleCollection [id=" + id + ", sampleType=" + sampleType + ", rateCardTestAssociation="
				+ rateCardTestAssociation + ", isUrgent=" + isUrgent + ", urgentTime=" + urgentTime + ", isCollected="
				+ isCollected + ", isHardCopyRequired=" + isHardCopyRequired + ", isHardCopyGiven=" + isHardCopyGiven
				+ ", status=" + status + ", sampleId=" + sampleId + ", labTestPickUpId=" + labTestPickUpId
				+ ", daughterLabLocationId=" + daughterLabLocationId + ", parentLabLocationId=" + parentLabLocationId
				+ ", isCompleted=" + isCompleted + ", isCollectedAtLab=" + isCollectedAtLab + "]";
	}

}
