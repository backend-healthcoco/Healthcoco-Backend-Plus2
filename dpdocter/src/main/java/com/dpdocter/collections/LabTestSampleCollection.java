package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.enums.TestSampleType;

@Document(collection = "lab_test_sample_cl")
public class LabTestSampleCollection extends GenericCollection {

	private ObjectId id;
	private String patientName;
	private Integer age;
	private String Gender;
	private TestSampleType sampleType;
	private List<ObjectId> labTestId;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
	private Boolean isHardCopyRequired;
	private Boolean isHardCopyGiven;
	private String status;
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
		return Gender;
	}

	public void setGender(String gender) {
		Gender = gender;
	}

	public TestSampleType getSampleType() {
		return sampleType;
	}

	public void setSampleType(TestSampleType sampleType) {
		this.sampleType = sampleType;
	}

	public List<ObjectId> getLabTestId() {
		return labTestId;
	}

	public void setLabTestId(List<ObjectId> labTestId) {
		this.labTestId = labTestId;
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

}
