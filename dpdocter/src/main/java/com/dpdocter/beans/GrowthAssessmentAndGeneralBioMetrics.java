package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class GrowthAssessmentAndGeneralBioMetrics extends GenericCollection {

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private Integer height;
	private Double weight;
	private Double bmiPercentile;
	private String bmiClassification;
	private Double bmi;
	private Integer pulseRate;
	private Double oxymetry;
	private Double spirometry;
	private List<ImageURLResponse> images;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAcademicProfileId() {
		return academicProfileId;
	}

	public void setAcademicProfileId(String academicProfileId) {
		this.academicProfileId = academicProfileId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getBmiPercentile() {
		return bmiPercentile;
	}

	public void setBmiPercentile(Double bmiPercentile) {
		this.bmiPercentile = bmiPercentile;
	}

	public String getBmiClassification() {
		return bmiClassification;
	}

	public void setBmiClassification(String bmiClassification) {
		this.bmiClassification = bmiClassification;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Integer getPulseRate() {
		return pulseRate;
	}

	public void setPulseRate(Integer pulseRate) {
		this.pulseRate = pulseRate;
	}

	public Double getOxymetry() {
		return oxymetry;
	}

	public void setOxymetry(Double oxymetry) {
		this.oxymetry = oxymetry;
	}

	public Double getSpirometry() {
		return spirometry;
	}

	public void setSpirometry(Double spirometry) {
		this.spirometry = spirometry;
	}

	public List<ImageURLResponse> getImages() {
		return images;
	}

	public void setImages(List<ImageURLResponse> images) {
		this.images = images;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "GrowthAssessmentAndGeneralBioMetrics [id=" + id + ", academicProfileId=" + academicProfileId
				+ ", branchId=" + branchId + ", schoolId=" + schoolId + ", doctorId=" + doctorId + ", height=" + height
				+ ", weight=" + weight + ", bmiPercentile=" + bmiPercentile + ", bmiClassification=" + bmiClassification
				+ ", bmi=" + bmi + ", pulseRate=" + pulseRate + ", oxymetry=" + oxymetry + ", spirometry=" + spirometry
				+ ", images=" + images + ", discarded=" + discarded + "]";
	}

}
