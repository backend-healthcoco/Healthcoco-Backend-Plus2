package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.collections.GenericCollection;

public class LabTestSampleLookUpResponse extends GenericCollection {

	private String id;
	private String patientName;
	private Integer age;
	private String gender;
	private String sampleType;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private List<RateCardTestAssociation> rateCardTestAssociation;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
	private Boolean isHardCopyRequired;
	private Boolean isHardCopyGiven;
	private String status;
	private String sampleId;
	private Location location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<RateCardTestAssociation> getRateCardTestAssociation() {
		return rateCardTestAssociation;
	}

	public void setRateCardTestAssociation(List<RateCardTestAssociation> rateCardTestAssociation) {
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "LabTestSampleLookUpResponse [id=" + id + ", patientName=" + patientName + ", age=" + age + ", gender="
				+ gender + ", sampleType=" + sampleType + ", daughterLabLocationId=" + daughterLabLocationId
				+ ", parentLabLocationId=" + parentLabLocationId + ", rateCardTestAssociation="
				+ rateCardTestAssociation + ", isUrgent=" + isUrgent + ", urgentTime=" + urgentTime + ", isCollected="
				+ isCollected + ", isHardCopyRequired=" + isHardCopyRequired + ", isHardCopyGiven=" + isHardCopyGiven
				+ ", status=" + status + ", sampleId=" + sampleId + ", location=" + location + "]";
	}

}
