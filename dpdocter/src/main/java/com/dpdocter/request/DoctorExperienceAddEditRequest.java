package com.dpdocter.request;

public class DoctorExperienceAddEditRequest {

	private String doctorId;

	private int experience;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	@Override
	public String toString() {
		return "DoctorExperienceAddEditRequest [doctorId=" + doctorId + ", experience=" + experience + "]";
	}
}
