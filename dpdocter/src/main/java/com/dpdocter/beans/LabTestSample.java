package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.TestSampleType;

public class LabTestSample extends GenericCollection {

	private String id;
	private String patientName;
	private Integer age;
	private String Gender;
	private String sampleType;
	private List<String> labTestIds;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
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

	public List<String> getLabTestIds() {
		return labTestIds;
	}

	public void setLabTestIds(List<String> labTestIds) {
		this.labTestIds = labTestIds;
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
		return Gender;
	}

	public void setGender(String gender) {
		Gender = gender;
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

	@Override
	public String toString() {
		return "LabTestSample [patientName=" + patientName + ", age=" + age + ", Gender=" + Gender + ", sampleType="
				+ sampleType + ", labTestIds=" + labTestIds + ", isUrgent=" + isUrgent + ", urgentTime=" + urgentTime
				+ ", isCollected=" + isCollected + ", isHardCopyRequired=" + isHardCopyRequired + ", isHardCopyGiven="
				+ isHardCopyGiven + ", status=" + status + ", sampleId=" + sampleId + "]";
	}

}
