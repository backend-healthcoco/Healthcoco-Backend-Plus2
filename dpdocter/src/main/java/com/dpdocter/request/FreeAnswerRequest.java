package com.dpdocter.request;


public class FreeAnswerRequest {
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String questionId;
	private String nextStep;
	private String answerDesc;
	private String helpfulTips;
	private Long time;

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

	@Override
	public String toString() {
		return "FreeAnswerRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", questionId=" + questionId + ", nextStep=" + nextStep + ", answerDesc=" + answerDesc
				+ ", helpfulTips=" + helpfulTips + ", time=" + time + "]";
	}

}
