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
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId questionId;
	@Field
	private ObjectId userId;
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
	@Field
	private Integer views;
	
	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getQuestionId() {
		return questionId;
	}

	public void setQuestionId(ObjectId questionId) {
		this.questionId = questionId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	@Override
	public String toString() {
		return "FreeQuestionAnswerCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", questionId=" + questionId + ", userId=" + userId
				+ ", forDetailType=" + forDetailType + ", forDetail=" + forDetail + ", problemType=" + problemType
				+ ", title=" + title + ", desc=" + desc + ", imageUrls=" + imageUrls + ", time=" + time
				+ ", answersDetails=" + answersDetails + ", isDiscarded=" + isDiscarded + ", views=" + views + "]";
	}

}
