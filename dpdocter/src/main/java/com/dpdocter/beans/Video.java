package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Video extends GenericCollection {

	private String id;

	private String name;

	private String speciality;

	private Boolean discarded = false;

	private String videoUrl;

	private String description;

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

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Video [id=" + id + ", name=" + name + ", speciality=" + speciality + ", discarded=" + discarded
				+ ", videoUrl=" + videoUrl + ", description=" + description + "]";
	}

}
