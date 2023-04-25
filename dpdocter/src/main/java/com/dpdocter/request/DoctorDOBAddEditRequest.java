package com.dpdocter.request;

import com.dpdocter.beans.DOB;

public class DoctorDOBAddEditRequest {

	private String doctorId;

	private DOB dob;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	@Override
	public String toString() {
		return "DoctorDOBAddEditRequest [doctorId=" + doctorId + ", dob=" + dob + "]";
	}
}
