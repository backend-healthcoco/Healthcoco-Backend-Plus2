package com.dpdocter.request;

import com.dpdocter.beans.ConsultationFee;

public class DoctorConsultationFeeAddEditRequest {

	private String id;

	private String doctorId;

	private String locationId;

	private ConsultationFee consultationFee;

	private ConsultationFee revisitConsultationFee;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public ConsultationFee getRevisitConsultationFee() {
		return revisitConsultationFee;
	}

	public void setRevisitConsultationFee(ConsultationFee revisitConsultationFee) {
		this.revisitConsultationFee = revisitConsultationFee;
	}

	@Override
	public String toString() {
		return "DoctorConsultationFeeAddEditRequest [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", consultationFee=" + consultationFee + "]";
	}
}
