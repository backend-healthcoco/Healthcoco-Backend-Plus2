package com.dpdocter.response;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.QuetionForDetail;
import com.dpdocter.collections.FreeAnswersDetailCollection;
import com.dpdocter.enums.ForDetailType;

public class FreeQuestionResponse {
	private String id;
	private String userId;
	private ForDetailType forDetailType;
	private QuetionForDetail forDetail;
	private String problemType;
	private String title;
	private String desc;
	private List<String> imageUrls;
	private Long time;
	private FreeAnswersDetailCollection answersDetails;
	private Boolean isDiscarded = false;

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

	public Boolean getIsDiscarded() {
		return isDiscarded;
	}

	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}

	@Override
	public String toString() {
		return "FreeQuestionResponse [id=" + id + ", userId=" + userId + ", forDetailType=" + forDetailType
				+ ", forDetail=" + forDetail + ", problemType=" + problemType + ", title=" + title + ", desc=" + desc
				+ ", imageUrls=" + imageUrls + ", time=" + time + ", answersDetails=" + answersDetails
				+ ", isDiscarded=" + isDiscarded + ", getId()=" + getId() + ", getUserId()=" + getUserId()
				+ ", getForDetailType()=" + getForDetailType() + ", getForDetail()=" + getForDetail()
				+ ", getProblemType()=" + getProblemType() + ", getTitle()=" + getTitle() + ", getDesc()=" + getDesc()
				+ ", getImageUrls()=" + getImageUrls() + ", getTime()=" + getTime() + ", getAnswersDetails()="
				+ getAnswersDetails() + ", getIsDiscarded()=" + getIsDiscarded() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

}
