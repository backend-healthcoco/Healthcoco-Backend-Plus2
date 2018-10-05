package com.dpdocter.request;

public class DentalLabPickupChangeStatusRequest {

	private String dentalLabPickupId;
	private String status;
	private Boolean isCollectedAtDoctor;
	private Boolean isCompleted;
	private Boolean isAcceptedAtLab;
	private Integer feedbackRating;
	private String feedbackComment;
	private Boolean discarded;

	public String getDentalLabPickupId() {
		return dentalLabPickupId;
	}

	public void setDentalLabPickupId(String dentalLabPickupId) {
		this.dentalLabPickupId = dentalLabPickupId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsCollectedAtDoctor() {
		return isCollectedAtDoctor;
	}

	public void setIsCollectedAtDoctor(Boolean isCollectedAtDoctor) {
		this.isCollectedAtDoctor = isCollectedAtDoctor;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Boolean getIsAcceptedAtLab() {
		return isAcceptedAtLab;
	}

	public void setIsAcceptedAtLab(Boolean isAcceptedAtLab) {
		this.isAcceptedAtLab = isAcceptedAtLab;
	}

	public Integer getFeedbackRating() {
		return feedbackRating;
	}

	public void setFeedbackRating(Integer feedbackRating) {
		this.feedbackRating = feedbackRating;
	}

	public String getFeedbackComment() {
		return feedbackComment;
	}

	public void setFeedbackComment(String feedbackComment) {
		this.feedbackComment = feedbackComment;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "DentalLabPickupChangeStatusRequest [dentalLabPickupId=" + dentalLabPickupId + ", status=" + status
				+ ", isCollectedAtDoctor=" + isCollectedAtDoctor + ", isCompleted=" + isCompleted + ", isAcceptedAtLab="
				+ isAcceptedAtLab + ", feedbackRating=" + feedbackRating + ", feedbackComment=" + feedbackComment
				+ ", discarded=" + discarded + "]";
	}

}
