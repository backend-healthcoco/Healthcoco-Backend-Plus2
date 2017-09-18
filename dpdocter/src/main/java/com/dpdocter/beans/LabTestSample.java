package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.TestSampleType;

public class LabTestSample extends GenericCollection {

	private String id;
	private String patientName;
	private String mobileNumber;
	private Integer age;
	private String gender;
	private String sampleType;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private List<RateCardTestAssociation> rateCardTestAssociation;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
	private Boolean isCollectedAtLab = false;
	private Boolean isHardCopyRequired;
	private Boolean isHardCopyGiven;
	private String status;
	private String sampleId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<RateCardTestAssociation> getRateCardTestAssociation() {
		return rateCardTestAssociation;
	}

	public void setRateCardTestAssociation(List<RateCardTestAssociation> rateCardTestAssociation) {
		this.rateCardTestAssociation = rateCardTestAssociation;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "LabTestSample [id=" + id + ", patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", age="
				+ age + ", gender=" + gender + ", sampleType=" + sampleType + ", daughterLabLocationId="
				+ daughterLabLocationId + ", parentLabLocationId=" + parentLabLocationId + ", rateCardTestAssociation="
				+ rateCardTestAssociation + ", isUrgent=" + isUrgent + ", urgentTime=" + urgentTime + ", isCollected="
				+ isCollected + ", isCollectedAtLab=" + isCollectedAtLab + ", isHardCopyRequired=" + isHardCopyRequired
				+ ", isHardCopyGiven=" + isHardCopyGiven + ", status=" + status + ", sampleId=" + sampleId + "]";
	}

}
