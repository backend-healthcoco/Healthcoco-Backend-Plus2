package com.dpdocter.request;

public class DoctorProfessionalStatementAddEditRequest {

	private String doctorId;

	private String professionalStatement;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getProfessionalStatement() {
		return professionalStatement;
	}

	public void setProfessionalStatement(String professionalStatement) {
		this.professionalStatement = professionalStatement;
	}

	@Override
	public String toString() {
		return "DoctorProfessionalStatementAddEditRequest [doctorId=" + doctorId + ", professionalStatement="
				+ professionalStatement + "]";
	}

}
