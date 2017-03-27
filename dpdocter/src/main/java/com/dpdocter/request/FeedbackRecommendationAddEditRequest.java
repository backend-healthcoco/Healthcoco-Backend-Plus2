package com.dpdocter.request;

import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.RecommendationType;

public class FeedbackRecommendationAddEditRequest {

	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private RecommendationType recommendationType;
	private FeedbackType feedbackType;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public RecommendationType getRecommendationType() {
		return recommendationType;
	}

	public void setRecommendationType(RecommendationType recommendationType) {
		this.recommendationType = recommendationType;
	}

	public FeedbackType getFeedbackType() {
		return feedbackType;
	}

	public void setFeedbackType(FeedbackType feedbackType) {
		this.feedbackType = feedbackType;
	}

	@Override
	public String toString() {
		return "FeedbackRecommendationAddEditRequest [patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", recommendationType="
				+ recommendationType + ", feedbackType=" + feedbackType + "]";
	}

}
