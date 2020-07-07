package com.dpdocter.response;

import com.dpdocter.beans.QuetionForDetail;
import com.dpdocter.enums.ForDetailType;

public class FreeAnswerResponse {
	private String id;
	private String userId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private ForDetailType forDetailType;
	private QuetionForDetail forDetail;
	private String problemType;
	private String title;
	private String desc;
	private String questionId;
	private String nextStep;
	private String answerDesc;
	private String helpfulTips;
	private Long time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ForDetailType getForDetailType() {
		return forDetailType;
	}

	public void setForDetailType(ForDetailType forDetailType) {
		this.forDetailType = forDetailType;
	}

	public QuetionForDetail getForDetail() {
		return forDetail;
	}

	public void setForDetail(QuetionForDetail forDetail) {
		this.forDetail = forDetail;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public String getAnswerDesc() {
		return answerDesc;
	}

	public void setAnswerDesc(String answerDesc) {
		this.answerDesc = answerDesc;
	}

	public String getHelpfulTips() {
		return helpfulTips;
	}

	public void setHelpfulTips(String helpfulTips) {
		this.helpfulTips = helpfulTips;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "FreeAnswerResponse [id=" + id + ", userId=" + userId + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", forDetailType=" + forDetailType + ", forDetail="
				+ forDetail + ", problemType=" + problemType + ", title=" + title + ", desc=" + desc + ", questionId="
				+ questionId + ", nextStep=" + nextStep + ", answerDesc=" + answerDesc + ", helpfulTips=" + helpfulTips
				+ ", time=" + time + "]";
	}

}
