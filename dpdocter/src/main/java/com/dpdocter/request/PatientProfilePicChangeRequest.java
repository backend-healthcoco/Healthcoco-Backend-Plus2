package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class PatientProfilePicChangeRequest {
	private String id;
	private String username;
	private FileDetails image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "PatientProfilePicChangeRequest [id=" + id + ", username=" + username + ", image=" + image + "]";
	}

}
