package com.dpdocter.response;

public class DoctorWithRankingDetailResponse {

	private String doctorId;
	
	private String locationId;
	
	private String resourceName;
	
	private Double experienceInYear = 0.0;
	
	private Double rxCount = 0.0;
	
	private Double patientCount = 0.0;
	
	private Double totalCount = 0.0;
	
	private Double noOfLikes = 0.0;
	
	private int rankingCount = 0;
		
	private Double pointIfActive = 0.0;
	
	private Double pointIfAdvance = 0.0;
	
	private Double pointIfOnlineConsultation = 0.0;
	
	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Double getRxCount() {
		return rxCount;
	}

	public void setRxCount(Double rxCount) {
		this.rxCount = rxCount;
	}

	public Double getPatientCount() {
		return patientCount;
	}

	public void setPatientCount(Double patientCount) {
		this.patientCount = patientCount;
	}

	public Double getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Double totalCount) {
		this.totalCount = totalCount;
	}

	public Double getNoOfLikes() {
		return noOfLikes;
	}

	public void setNoOfLikes(Double noOfLikes) {
		this.noOfLikes = noOfLikes;
	}

	public int getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(int rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Double getExperienceInYear() {
		return experienceInYear;
	}

	public void setExperienceInYear(Double experienceInYear) {
		this.experienceInYear = experienceInYear;
	}

	public Double getPointIfActive() {
		return pointIfActive;
	}

	public void setPointIfActive(Double pointIfActive) {
		this.pointIfActive = pointIfActive;
	}

	public Double getPointIfAdvance() {
		return pointIfAdvance;
	}

	public void setPointIfAdvance(Double pointIfAdvance) {
		this.pointIfAdvance = pointIfAdvance;
	}

	public Double getPointIfOnlineConsultation() {
		return pointIfOnlineConsultation;
	}

	public void setPointIfOnlineConsultation(Double pointIfOnlineConsultation) {
		this.pointIfOnlineConsultation = pointIfOnlineConsultation;
	}

	@Override
	public String toString() {
		return "DoctorWithRankingDetailResponse [doctorId=" + doctorId + ", locationId=" + locationId
				+ ", resourceName=" + resourceName + ", experienceInYear=" + experienceInYear + ", rxCount=" + rxCount
				+ ", patientCount=" + patientCount + ", totalCount=" + totalCount + ", noOfLikes=" + noOfLikes
				+ ", rankingCount=" + rankingCount + ", pointIfActive=" + pointIfActive + ", pointIfAdvance="
				+ pointIfAdvance + ", pointIfOnlineConsultation=" + pointIfOnlineConsultation + "]";
	}
}
