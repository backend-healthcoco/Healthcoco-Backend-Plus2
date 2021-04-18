package com.dpdocter.request;

public class DoctorProfilePictureAddEditRequest {
    private String doctorId;

    public String getDoctorId() {
	return doctorId;
    }

    public void setDoctorId(String doctorId) {
	this.doctorId = doctorId;
    }

    @Override
    public String toString() {
	return "DoctorProfilePictureAddEditRequest [doctorId=" + doctorId + "]";
    }

}
