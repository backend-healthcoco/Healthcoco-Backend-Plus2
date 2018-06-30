package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.MailAttachment;

public class MailResponse {

	private MailAttachment mailAttachment;
	 
	private List<MailAttachment> mailAttachments;
	
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

	public List<MailAttachment> getMailAttachments() {
		return mailAttachments;
	}

	public void setMailAttachments(List<MailAttachment> mailAttachments) {
		this.mailAttachments = mailAttachments;
	}

	@Override
	public String toString() {
		return "MailResponse [mailAttachment=" + mailAttachment + ", mailAttachments=" + mailAttachments
				+ ", doctorName=" + doctorName + ", patientName=" + patientName + ", clinicAddress=" + clinicAddress
				+ ", clinicName=" + clinicName + ", mailRecordCreatedDate=" + mailRecordCreatedDate + "]";
	}
}
