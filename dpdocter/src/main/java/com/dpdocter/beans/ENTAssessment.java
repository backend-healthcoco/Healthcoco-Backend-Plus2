package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class ENTAssessment extends GenericCollection {

	private String id;
	private String academicProfileId;
	private String branchId;
	private String schoolId;
	private String doctorId;
	private List<String> rightEar;
	private List<String> leftEar;
	private List<String> nose;
	private List<String> oralCavityAndThroat;
	private String doctorConsultations;
	private String otherTests;
	private String remarks;
	private List<ImageURLResponse> images;

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

	public List<String> getRightEar() {
		return rightEar;
	}

	public void setRightEar(List<String> rightEar) {
		this.rightEar = rightEar;
	}

	public List<String> getLeftEar() {
		return leftEar;
	}

	public void setLeftEar(List<String> leftEar) {
		this.leftEar = leftEar;
	}

	public List<String> getNose() {
		return nose;
	}

	public void setNose(List<String> nose) {
		this.nose = nose;
	}

	public List<String> getOralCavityAndThroat() {
		return oralCavityAndThroat;
	}

	public void setOralCavityAndThroat(List<String> oralCavityAndThroat) {
		this.oralCavityAndThroat = oralCavityAndThroat;
	}

	public String getDoctorConsultations() {
		return doctorConsultations;
	}

	public void setDoctorConsultations(String doctorConsultations) {
		this.doctorConsultations = doctorConsultations;
	}

	public String getOtherTests() {
		return otherTests;
	}

	public void setOtherTests(String otherTests) {
		this.otherTests = otherTests;
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

	@Override
	public String toString() {
		return "ENTAssessment [id=" + id + ", academicProfileId=" + academicProfileId + ", branchId=" + branchId
				+ ", schoolId=" + schoolId + ", doctorId=" + doctorId + ", rightEar=" + rightEar + ", leftEar="
				+ leftEar + ", nose=" + nose + ", oralCavityAndThroat=" + oralCavityAndThroat + ", doctorConsultations="
				+ doctorConsultations + ", otherTests=" + otherTests + ", remarks=" + remarks + ", images=" + images
				+ "]";
	}

}
