package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.FileDetails;

public class DoctorMultipleDataAddEditRequest {

	private String doctorId;

    private String title;

    private String firstName;

    private String experience;
    
    private List<String> specialities;
    
    private FileDetails profileImage;

    private FileDetails coverImage;

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

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public List<String> getSpeciality() {
		return specialities;
	}

	public void setSpeciality(List<String> specialities) {
		this.specialities = specialities;
	}

	public FileDetails getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(FileDetails profileImage) {
		this.profileImage = profileImage;
	}

	public FileDetails getCoverImage() {
		return coverImage;
	}

	public void setCoverImage(FileDetails coverImage) {
		this.coverImage = coverImage;
	}

	@Override
	public String toString() {
		return "DoctorMultipleDataAddEditRequest [doctorId=" + doctorId + ", title=" + title + ", firstName="
				+ firstName + ", experience=" + experience + ", specialities=" + specialities + ", profileImage="
				+ profileImage + ", coverImage=" + coverImage + "]";
	}
}
