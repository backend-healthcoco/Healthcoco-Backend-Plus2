package com.dpdocter.request;

public class DoctorNameAddEditRequest {

	private String doctorId;

	private String title;

	private String firstName;

	private String lastName;

	private String middleName;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Override
	public String toString() {
		return "DoctorNameAddEditRequest [doctorId=" + doctorId + ", title=" + title + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", middleName=" + middleName + "]";
	}

}
