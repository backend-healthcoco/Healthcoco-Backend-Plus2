package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class ContactUs extends GenericCollection{

	private String id;

    private String name;

    private String mobileNumber;

    private String emailAddress;

    private String message;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ContactUs [id=" + id + ", name=" + name + ", mobileNumber=" + mobileNumber + ", emailAddress="
				+ emailAddress + ", message=" + message + "]";
	}
}
