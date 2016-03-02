package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Resume extends GenericCollection{

    private String id;

    private String type;

    private String name;

    private String emailAddress;

    private String mobileNumber;

    private String path;

    private FileDetails fileDetails;
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FileDetails getFileDetails() {
		return fileDetails;
	}

	public void setFileDetails(FileDetails fileDetails) {
		this.fileDetails = fileDetails;
	}

	@Override
	public String toString() {
		return "Resume [id=" + id + ", type=" + type + ", name=" + name + ", emailAddress=" + emailAddress
				+ ", mobileNumber=" + mobileNumber + ", path=" + path + ", fileDetails=" + fileDetails + "]";
	}
}

