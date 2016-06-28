package com.dpdocter.response;

import com.dpdocter.beans.MailAttachment;

public class MailResponse {

	private MailAttachment mailAttachment;
	
	private String doctorName;
	
	private String patientName;
	
	private String clinicAddress;
	
	private String clinicName;
	
	private String mailRecordCreatedDate;

	public MailAttachment getMailAttachment() {
		return mailAttachment;
	}

	public void setMailAttachment(MailAttachment mailAttachment) {
		this.mailAttachment = mailAttachment;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public String getMailRecordCreatedDate() {
		return mailRecordCreatedDate;
	}

	public void setMailRecordCreatedDate(String mailRecordCreatedDate) {
		this.mailRecordCreatedDate = mailRecordCreatedDate;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	@Override
	public String toString() {
		return "MailResponse [mailAttachment=" + mailAttachment + ", doctorName=" + doctorName + ", patientName="
				+ patientName + ", clinicAddress=" + clinicAddress + ", clinicName=" + clinicName
				+ ", mailRecordCreatedDate=" + mailRecordCreatedDate + "]";
	}
}
