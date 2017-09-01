package com.dpdocter.beans;

import com.dpdocter.enums.FeelingTypeEnum;

public class DailyPatientFeedback {

	private String explanation;
	private FeelingTypeEnum feelingType;
	private Long submissionDate;
	private Integer day;

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

	@Override
	public String toString() {
		return "DailyPatientFeedback [explanation=" + explanation + ", feelingType=" + feelingType + ", submissionDate="
				+ submissionDate + ", day=" + day + "]";
	}

}
