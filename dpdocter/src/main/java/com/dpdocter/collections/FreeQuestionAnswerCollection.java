package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.QuetionForDetail;
import com.dpdocter.enums.ForDetailType;

@Document(collection = "free_que_ans_cl")
public class FreeQuestionAnswerCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private String doctorId;
	@Field
	private String locationId;
	@Field
	private String hospitalId;
	@Field
	private String questionId;
	@Field
	private String nextStep;
	@Field
	private String answerDesc;
	@Field
	private String helpfulTips;
	@Field
	private String userId;
	@Field
	private ForDetailType forDetailType;
	@Field
	private QuetionForDetail forDetail;
	@Field
	private String problemType;
	@Field
	private String title;
	@Field
	private String desc;
	@Field
	private List<String> imageUrls;
	@Field
	private Long time;
	@Field
	private FreeAnswersDetailCollection answersDetails;
	@Field
	private Boolean isDiscarded = false;

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

	public List<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(List<String> imageUrls) {
		this.imageUrls = imageUrls;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public FreeAnswersDetailCollection getAnswersDetails() {
		return answersDetails;
	}

	public void setAnswersDetails(FreeAnswersDetailCollection answersDetails) {
		this.answersDetails = answersDetails;
	}

	@Override
	public String toString() {
		return "FreeQuestionAnswerCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", questionId=" + questionId + ", nextStep=" + nextStep
				+ ", answerDesc=" + answerDesc + ", helpfulTips=" + helpfulTips + ", userId=" + userId
				+ ", forDetailType=" + forDetailType + ", forDetail=" + forDetail + ", problemType=" + problemType
				+ ", title=" + title + ", desc=" + desc + ", imageUrls=" + imageUrls + ", time=" + time
				+ ", answersDetails=" + answersDetails + "]";
	}

}
