package com.dpdocter.beans;

/**
 * @author veeraj
 */

public class Doctor {
	private String id;

	private String imageUrl;

	private String specialization;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	@Override
	public String toString() {
		return "Doctor [id=" + id + ", imageUrl=" + imageUrl + ", specialization=" + specialization + "]";
	}

}
