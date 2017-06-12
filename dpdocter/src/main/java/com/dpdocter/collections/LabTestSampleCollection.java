package com.dpdocter.collections;

import java.util.List;

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
	private String patientName;
	@Field
	private Integer age;
	@Field
	private String gender;
	@Field
	private String sampleType;
	@Field
	private List<RateCardTestAssociation> rateCardTestAssociation;
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

	@Override
	public String toString() {
		return "LabTestSampleCollection [id=" + id + ", patientName=" + patientName + ", age=" + age + ", gender="
				+ gender + ", sampleType=" + sampleType + ", rateCardTestAssociation=" + rateCardTestAssociation
				+ ", isUrgent=" + isUrgent + ", urgentTime=" + urgentTime + ", isCollected=" + isCollected
				+ ", isHardCopyRequired=" + isHardCopyRequired + ", isHardCopyGiven=" + isHardCopyGiven + ", status="
				+ status + ", sampleId=" + sampleId + "]";
	}

}
