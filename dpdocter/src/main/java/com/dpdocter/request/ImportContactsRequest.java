package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class ImportContactsRequest {
	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String specialComments;

	private FileDetails contactsFile;

	private String contactsFileUrl;

	private String emailAddress;

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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getSpecialComments() {
		return specialComments;
	}

	public void setSpecialComments(String specialComments) {
		this.specialComments = specialComments;
	}

	public FileDetails getContactsFile() {
		return contactsFile;
	}

	public void setContactsFile(FileDetails contactsFile) {
		this.contactsFile = contactsFile;
	}

	public String getContactsFileUrl() {
		return contactsFileUrl;
	}

	public void setContactsFileUrl(String contactsFileUrl) {
		this.contactsFileUrl = contactsFileUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "ImportContactsRequest [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", specialComments=" + specialComments + ", contactsFile=" + contactsFile
				+ ", contactsFileUrl=" + contactsFileUrl + ", emailAddress=" + emailAddress + "]";
	}

}
