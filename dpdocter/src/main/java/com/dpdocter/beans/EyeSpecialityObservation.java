package com.dpdocter.beans;

import java.util.List;

public class EyeSpecialityObservation {

	private String doctorId;
	 
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private  List<EyeExamination> eyeExamination;
	
	private VisualAcuity rightVisualAcuities;
	
	private VisualAcuity leftVisualAcuities;
	
	private EyeTest leftEyeTest;
	
	private EyeTest rightEyeTest;
	
	private VisionPR vision_PR;
	
	private VisionIOP vision_IOP;
	
	private  List<EyeExamination> investigation;
	
	private EyeFungus fungus;

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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public List<EyeExamination> getEyeExamination() {
		return eyeExamination;
	}

	public void setEyeExamination(List<EyeExamination> eyeExamination) {
		this.eyeExamination = eyeExamination;
	}

	public VisualAcuity getRightVisualAcuities() {
		return rightVisualAcuities;
	}

	public void setRightVisualAcuities(VisualAcuity rightVisualAcuities) {
		this.rightVisualAcuities = rightVisualAcuities;
	}

	public VisualAcuity getLeftVisualAcuities() {
		return leftVisualAcuities;
	}

	public void setLeftVisualAcuities(VisualAcuity leftVisualAcuities) {
		this.leftVisualAcuities = leftVisualAcuities;
	}

	public EyeTest getLeftEyeTest() {
		return leftEyeTest;
	}

	public void setLeftEyeTest(EyeTest leftEyeTest) {
		this.leftEyeTest = leftEyeTest;
	}

	public EyeTest getRightEyeTest() {
		return rightEyeTest;
	}

	public void setRightEyeTest(EyeTest rightEyeTest) {
		this.rightEyeTest = rightEyeTest;
	}
	
	

	public VisionPR getVision_PR() {
		return vision_PR;
	}

	public void setVision_PR(VisionPR vision_PR) {
		this.vision_PR = vision_PR;
	}

	public VisionIOP getVision_IOP() {
		return vision_IOP;
	}

	public void setVision_IOP(VisionIOP vision_IOP) {
		this.vision_IOP = vision_IOP;
	}
	
	

	public List<EyeExamination> getInvestigation() {
		return investigation;
	}

	public void setInvestigation(List<EyeExamination> investigation) {
		this.investigation = investigation;
	}
	
	

	public EyeFungus getFungus() {
		return fungus;
	}

	public void setFungus(EyeFungus fungus) {
		this.fungus = fungus;
	}

	@Override
	public String toString() {
		return "EyeSpecialityObservation [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", eyeExamination=" + eyeExamination
				+ ", rightVisualAcuity=" + rightVisualAcuities + ", leftVisualAcuity=" + leftVisualAcuities
				+ ", leftEyeTest=" + leftEyeTest + ", rightEyeTest=" + rightEyeTest + "]";
	}
	
	
	
	
}
