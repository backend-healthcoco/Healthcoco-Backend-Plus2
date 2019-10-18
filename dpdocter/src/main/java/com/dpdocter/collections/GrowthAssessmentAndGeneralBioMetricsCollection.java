package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "growth_assessment_and_general_bio_metrics_cl")
public class GrowthAssessmentAndGeneralBioMetricsCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	private ObjectId academicProfileId;
	@Field
	private ObjectId branchId;
	@Field
	private ObjectId schoolId;
	@Field
	private ObjectId doctorId;
	@Field
	private Integer height;
	@Field
	private Double weight;
	@Field
	private Double bmiPercentile;
	@Field
	private String bmiClassification;
	@Field
	private Double bmi;
	@Field
	private Integer pulseRate;
	@Field
	private Double oxymetry;
	@Field
	private Double spirometry;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getAcademicProfileId() {
		return academicProfileId;
	}

	public void setAcademicProfileId(ObjectId academicProfileId) {
		this.academicProfileId = academicProfileId;
	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
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
		return "GrowthAssessmentAndGeneralBioMetricsCollection [id=" + id + ", academicProfileId=" + academicProfileId
				+ ", branchId=" + branchId + ", schoolId=" + schoolId + ", doctorId=" + doctorId + ", height=" + height
				+ ", weight=" + weight + ", bmiPercentile=" + bmiPercentile + ", bmiClassification=" + bmiClassification
				+ ", bmi=" + bmi + ", pulseRate=" + pulseRate + ", oxymetry=" + oxymetry + ", spirometry=" + spirometry
				+ ", images=" + images + ", discarded=" + discarded + "]";
	}

}
