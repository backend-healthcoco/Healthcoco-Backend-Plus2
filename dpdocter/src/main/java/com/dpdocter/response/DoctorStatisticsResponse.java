package com.dpdocter.response;

public class DoctorStatisticsResponse {

	private Integer currentVisitCount;

	private Integer previousVisitCount;

	private Integer currentProfileViewCount;

	private Integer previousProfileViewCount;

	private Integer previousRecommendationCount;

	private Integer currentRecommendationCount;

	public Integer getCurrentVisitCount() {
		return currentVisitCount;
	}

	public void setCurrentVisitCount(Integer currentVisitCount) {
		this.currentVisitCount = currentVisitCount;
	}

	public Integer getPreviousVisitCount() {
		return previousVisitCount;
	}

	public void setPreviousVisitCount(Integer previousVisitCount) {
		this.previousVisitCount = previousVisitCount;
	}

	public Integer getCurrentProfileViewCount() {
		return currentProfileViewCount;
	}

	public void setCurrentProfileViewCount(Integer currentProfileViewCount) {
		this.currentProfileViewCount = currentProfileViewCount;
	}

	public Integer getPreviousProfileViewCount() {
		return previousProfileViewCount;
	}

	public void setPreviousProfileViewCount(Integer previousProfileViewCount) {
		this.previousProfileViewCount = previousProfileViewCount;
	}

	public Integer getPreviousRecommendationCount() {
		return previousRecommendationCount;
	}

	public void setPreviousRecommendationCount(Integer previousRecommendationCount) {
		this.previousRecommendationCount = previousRecommendationCount;
	}

	public Integer getCurrentRecommendationCount() {
		return currentRecommendationCount;
	}

	public void setCurrentRecommendationCount(Integer currentRecommendationCount) {
		this.currentRecommendationCount = currentRecommendationCount;
	}

	@Override
	public String toString() {
		return "DoctorStatisticsResponse [currentVisitCount=" + currentVisitCount + ", previousVisitCount="
				+ previousVisitCount + ", currentProfileViewCount=" + currentProfileViewCount
				+ ", previousProfileViewCount=" + previousProfileViewCount + ", previousRecommendationCount="
				+ previousRecommendationCount + ", currentRecommendationCount=" + currentRecommendationCount + "]";
	}

}
