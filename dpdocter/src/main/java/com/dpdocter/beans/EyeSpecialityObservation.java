package com.dpdocter.beans;

public class EyeSpecialityObservation {

	private String doctorId;
	 
	private String locationId;
	
	private String hospitalId;
	
	private String patientId;
	
	private EyeExamination eyeExamination;
	
	private VisualAcuity rightVisualAcuity;
	
	private VisualAcuity leftVisualAcuity;
	
	private EyeTest leftEyeTest;
	
	private EyeTest rightEyeTest;

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

	public EyeExamination getEyeExamination() {
		return eyeExamination;
	}

	public void setEyeExamination(EyeExamination eyeExamination) {
		this.eyeExamination = eyeExamination;
	}

	public VisualAcuity getRightVisualAcuity() {
		return rightVisualAcuity;
	}

	public void setRightVisualAcuity(VisualAcuity rightVisualAcuity) {
		this.rightVisualAcuity = rightVisualAcuity;
	}

	public VisualAcuity getLeftVisualAcuity() {
		return leftVisualAcuity;
	}

	public void setLeftVisualAcuity(VisualAcuity leftVisualAcuity) {
		this.leftVisualAcuity = leftVisualAcuity;
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

	@Override
	public String toString() {
		return "EyeSpecialityObservation [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", eyeExamination=" + eyeExamination
				+ ", rightVisualAcuity=" + rightVisualAcuity + ", leftVisualAcuity=" + leftVisualAcuity
				+ ", leftEyeTest=" + leftEyeTest + ", rightEyeTest=" + rightEyeTest + "]";
	}
	
	
	
	
}
