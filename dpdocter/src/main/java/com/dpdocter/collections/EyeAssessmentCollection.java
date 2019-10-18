package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "eye_assessment_cl")
public class EyeAssessmentCollection extends GenericCollection {

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
	private String optometryRightEye;
	@Field
	private String optometryLeftEye;
	@Field
	private String optometryGazeAssymmetryValue;
	@Field
	private Float pupilDistance;
	@Field
	private List<String> clinicalRightEye;
	@Field
	private List<String> clinicalLeftEye;
	@Field
	private Boolean anisometropia = Boolean.FALSE;
	@Field
	private Boolean astigmatism = Boolean.FALSE;
	@Field
	private Boolean hyperopia = Boolean.FALSE;
	@Field
	private Boolean myopia = Boolean.FALSE;
	@Field
	private Boolean gazeAsymmetry = Boolean.FALSE;
	@Field
	private Boolean anisocoria = Boolean.FALSE;
	@Field
	private Boolean wearGlasses = Boolean.FALSE;
	@Field
	private List<String> suggesstedInvestigation;
	@Field
	private Boolean doctorConsultation = Boolean.FALSE;
	@Field
	private String remarks;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private Boolean discarded = Boolean.FALSE;

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

	public String getOptometryRightEye() {
		return optometryRightEye;
	}

	public void setOptometryRightEye(String optometryRightEye) {
		this.optometryRightEye = optometryRightEye;
	}

	public String getOptometryLeftEye() {
		return optometryLeftEye;
	}

	public void setOptometryLeftEye(String optometryLeftEye) {
		this.optometryLeftEye = optometryLeftEye;
	}

	public String getOptometryGazeAssymmetryValue() {
		return optometryGazeAssymmetryValue;
	}

	public void setOptometryGazeAssymmetryValue(String optometryGazeAssymmetryValue) {
		this.optometryGazeAssymmetryValue = optometryGazeAssymmetryValue;
	}

	public Float getPupilDistance() {
		return pupilDistance;
	}

	public void setPupilDistance(Float pupilDistance) {
		this.pupilDistance = pupilDistance;
	}

	public List<String> getClinicalRightEye() {
		return clinicalRightEye;
	}

	public void setClinicalRightEye(List<String> clinicalRightEye) {
		this.clinicalRightEye = clinicalRightEye;
	}

	public List<String> getClinicalLeftEye() {
		return clinicalLeftEye;
	}

	public void setClinicalLeftEye(List<String> clinicalLeftEye) {
		this.clinicalLeftEye = clinicalLeftEye;
	}

	public Boolean getAnisometropia() {
		return anisometropia;
	}

	public void setAnisometropia(Boolean anisometropia) {
		this.anisometropia = anisometropia;
	}

	public Boolean getAstigmatism() {
		return astigmatism;
	}

	public void setAstigmatism(Boolean astigmatism) {
		this.astigmatism = astigmatism;
	}

	public Boolean getHyperopia() {
		return hyperopia;
	}

	public void setHyperopia(Boolean hyperopia) {
		this.hyperopia = hyperopia;
	}

	public Boolean getMyopia() {
		return myopia;
	}

	public void setMyopia(Boolean myopia) {
		this.myopia = myopia;
	}

	public Boolean getGazeAsymmetry() {
		return gazeAsymmetry;
	}

	public void setGazeAsymmetry(Boolean gazeAsymmetry) {
		this.gazeAsymmetry = gazeAsymmetry;
	}

	public Boolean getAnisocoria() {
		return anisocoria;
	}

	public void setAnisocoria(Boolean anisocoria) {
		this.anisocoria = anisocoria;
	}

	public Boolean getWearGlasses() {
		return wearGlasses;
	}

	public void setWearGlasses(Boolean wearGlasses) {
		this.wearGlasses = wearGlasses;
	}

	public List<String> getSuggesstedInvestigation() {
		return suggesstedInvestigation;
	}

	public void setSuggesstedInvestigation(List<String> suggesstedInvestigation) {
		this.suggesstedInvestigation = suggesstedInvestigation;
	}

	public Boolean getDoctorConsultation() {
		return doctorConsultation;
	}

	public void setDoctorConsultation(Boolean doctorConsultation) {
		this.doctorConsultation = doctorConsultation;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

}
