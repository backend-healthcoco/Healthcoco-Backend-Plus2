package com.dpdocter.beans;

public class PatientStickerJasperDetails {
	private String localPatientName;

	private String imageUrl;

	private String gender;

	private DOB dob;

	private String barcodeImageUrl;

	private String doctorId;
	private String PID;

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getBarcodeImageUrl() {
		return barcodeImageUrl;
	}

	public void setBarcodeImageUrl(String barcodeImageUrl) {
		this.barcodeImageUrl = barcodeImageUrl;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

}
