package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.FileDetails;

public class ClinicImageAddRequest {

	private String id;

	private List<FileDetails> images;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<FileDetails> getImages() {
		return images;
	}

	public void setImages(List<FileDetails> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "ClinicImageAddRequest [id=" + id + ", images=" + images + "]";
	}
}
