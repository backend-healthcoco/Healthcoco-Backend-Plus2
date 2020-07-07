package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DoctorDetail;


@Document(collection = "free_ans_cl")
public class FreeAnswersDetailCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId questionId;
	@Field
	private String nextStep;
	@Field
	private String answerDesc;
	@Field
	private String helpfulTips;
	@Field
	private Long time;
	@Field
	private DoctorDetail docDetail;
	@Field
	private Boolean isHelpful = false;
	@Field
	private String reasonForflag;
	@Field
	private Boolean isPrivateConsultationAllow = false;
	@Field
	private String reasonForNotHelpful;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getQuestionId() {
		return questionId;
	}

	public void setQuestionId(ObjectId questionId) {
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

	public Boolean getIsHelpful() {
		return isHelpful;
	}

	public void setIsHelpful(Boolean isHelpful) {
		this.isHelpful = isHelpful;
	}

	public String getReasonForflag() {
		return reasonForflag;
	}

	public void setReasonForflag(String reasonForflag) {
		this.reasonForflag = reasonForflag;
	}

	public Boolean getIsPrivateConsultationAllow() {
		return isPrivateConsultationAllow;
	}

	public void setIsPrivateConsultationAllow(Boolean isPrivateConsultationAllow) {
		this.isPrivateConsultationAllow = isPrivateConsultationAllow;
	}

	public String getReasonForNotHelpful() {
		return reasonForNotHelpful;
	}

	public void setReasonForNotHelpful(String reasonForNotHelpful) {
		this.reasonForNotHelpful = reasonForNotHelpful;
	}

	public DoctorDetail getDocDetail() {
		return docDetail;
	}

	public void setDocDetail(DoctorDetail docDetail) {
		this.docDetail = docDetail;
	}

	@Override
	public String toString() {
		return "FreeAnswersDetailCollection [id=" + id + ", questionId=" + questionId + ", nextStep=" + nextStep
				+ ", answerDesc=" + answerDesc + ", helpfulTips=" + helpfulTips + ", time=" + time + ", docDetail="
				+ docDetail + ", isHelpful=" + isHelpful + ", reasonForflag=" + reasonForflag
				+ ", isPrivateConsultationAllow=" + isPrivateConsultationAllow + ", reasonForNotHelpful="
				+ reasonForNotHelpful + "]";
	}

}
