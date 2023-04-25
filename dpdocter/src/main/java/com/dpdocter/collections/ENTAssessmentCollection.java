package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.response.ImageURLResponse;

@Document(collection = "ent_assessment_cl")
public class ENTAssessmentCollection extends GenericCollection {

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
	private List<String> rightEar;
	@Field
	private List<String> leftEar;
	@Field
	private List<String> nose;
	@Field
	private List<String> oralCavityAndThroat;
	@Field
	private String doctorConsultations;
	@Field
	private String otherTests;
	@Field
	private String remarks;
	@Field
	private List<ImageURLResponse> images;
	@Field
	private Boolean discarded = Boolean.FALSE;
	@Field
	private List<String> earTest;

	@Field
	private List<String> noseTest;

	@Field
	private List<String> throatTest;

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getEarTest() {
		return earTest;
	}

	public void setEarTest(List<String> earTest) {
		this.earTest = earTest;
	}

	public List<String> getNoseTest() {
		return noseTest;
	}

	public void setNoseTest(List<String> noseTest) {
		this.noseTest = noseTest;
	}

	public List<String> getThroatTest() {
		return throatTest;
	}

	public void setThroatTest(List<String> throatTest) {
		this.throatTest = throatTest;
	}

}
