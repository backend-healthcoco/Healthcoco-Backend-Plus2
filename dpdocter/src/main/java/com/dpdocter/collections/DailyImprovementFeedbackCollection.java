package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.FeelingTypeEnum;

public class DailyImprovementFeedbackCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String prescriptionId;
	@Field
	private String explanation;
	@Field
	private FeelingTypeEnum feelingType;
	@Field
	private Long submissionDate;
	@Field
	private Integer day;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public FeelingTypeEnum getFeelingType() {
		return feelingType;
	}

	public void setFeelingType(FeelingTypeEnum feelingType) {
		this.feelingType = feelingType;
	}

	public Long getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Long submissionDate) {
		this.submissionDate = submissionDate;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getPrescriptionId() {
		return prescriptionId;
	}

	public void setPrescriptionId(String prescriptionId) {
		this.prescriptionId = prescriptionId;
	}

	@Override
	public String toString() {
		return "DailyImprovementFeedbackCollection [id=" + id + ", prescriptionId=" + prescriptionId + ", explanation="
				+ explanation + ", feelingType=" + feelingType + ", submissionDate=" + submissionDate + ", day=" + day
				+ "]";
	}

}
