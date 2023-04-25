package com.dpdocter.request;

public class DoctorGenderAddEditRequest {

	private String doctorId;

	private String gender;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "DoctorGenderAddEditRequest [doctorId=" + doctorId + ", gender=" + gender + "]";
	}
}
