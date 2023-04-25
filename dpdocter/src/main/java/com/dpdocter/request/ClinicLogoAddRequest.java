package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class ClinicLogoAddRequest {

	private String id;

	private FileDetails image;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "ClinicLogoAddRequest [id=" + id + ", image=" + image + "]";
	}
}
