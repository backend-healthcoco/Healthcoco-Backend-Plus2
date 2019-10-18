package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class EyeAssessment extends GenericCollection{

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private String optometryRightEye;
	private String optometryLeftEye;
	private String optometryGazeAssymmetryValue;
	private Float pupilDistance;
	private List<String> clinicalRightEye;
	private List<String> clinicalLeftEye;
	private Boolean anisometropia= Boolean.FALSE;
	private Boolean astigmatism = Boolean.FALSE;
	private Boolean hyperopia= Boolean.FALSE;
	private Boolean myopia = Boolean.FALSE;
	private Boolean gazeAsymmetry= Boolean.FALSE;
	private Boolean anisocoria = Boolean.FALSE;
	private Boolean wearGlasses = Boolean.FALSE;
	private List<String> suggesstedInvestigation;
	private Boolean doctorConsultation = Boolean.FALSE; 
	private String remarks;
	private List<ImageURLResponse> images;
	private Boolean discarded = Boolean.FALSE;

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
