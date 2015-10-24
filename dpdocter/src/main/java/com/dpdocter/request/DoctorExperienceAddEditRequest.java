package com.dpdocter.request;

public class DoctorExperienceAddEditRequest {

    private String doctorId;

    private String experience;

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    public String getExperience() {
	return experience;
    }

    public void setExperience(String experience) {
	this.experience = experience;
    }

    @Override
    public String toString() {
	return "DoctorExperienceAddEditRequest [doctorId=" + doctorId + ", experience=" + experience + "]";
    }
}
