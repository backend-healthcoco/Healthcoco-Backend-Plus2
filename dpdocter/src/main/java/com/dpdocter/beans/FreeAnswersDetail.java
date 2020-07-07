package com.dpdocter.beans;

public class FreeAnswersDetail {
	private String id;
	private String questionId;
	private String nextStep;
	private String answerDesc;
	private String helpfulTips;
	private DoctorDetail docDetail;
	private Long time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public DoctorDetail getDocDetail() {
		return docDetail;
	}

	public void setDocDetail(DoctorDetail docDetail) {
		this.docDetail = docDetail;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "FreeAnswersDetail [id=" + id + ", nextStep=" + nextStep + ", answerDesc=" + answerDesc
				+ ", helpfulTips=" + helpfulTips + ", docDetail=" + docDetail + ", time=" + time + "]";
	}
}
