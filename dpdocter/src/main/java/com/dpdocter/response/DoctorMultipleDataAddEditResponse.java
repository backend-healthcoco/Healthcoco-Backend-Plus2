package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorExperience;

public class DoctorMultipleDataAddEditResponse {

    private String doctorId;

    private String title;

    private String firstName;

    private DoctorExperience experience;

    private List<String> specialities;

    private String profileImageUrl;

    private String thumbnailProfileImageUrl;

    private String coverImageUrl;

    private String thumbnailCoverImageUrl;

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

    public DoctorExperience getExperience() {
	return experience;
    }

    public void setExperience(DoctorExperience experience) {
	this.experience = experience;
    }

    public List<String> getSpecialities() {
	return specialities;
    }

    public void setSpecialities(List<String> specialities) {
	this.specialities = specialities;
    }

    public String getProfileImageUrl() {
	return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
	this.profileImageUrl = profileImageUrl;
    }

    public String getCoverImageUrl() {
	return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
	this.coverImageUrl = coverImageUrl;
    }

    public String getThumbnailProfileImageUrl() {
	return thumbnailProfileImageUrl;
    }

    public void setThumbnailProfileImageUrl(String thumbnailProfileImageUrl) {
	this.thumbnailProfileImageUrl = thumbnailProfileImageUrl;
    }

    public String getThumbnailCoverImageUrl() {
	return thumbnailCoverImageUrl;
    }

    public void setThumbnailCoverImageUrl(String thumbnailCoverImageUrl) {
	this.thumbnailCoverImageUrl = thumbnailCoverImageUrl;
    }

    @Override
    public String toString() {
	return "DoctorMultipleDataAddEditResponse [doctorId=" + doctorId + ", title=" + title + ", firstName=" + firstName + ", experience=" + experience
		+ ", specialities=" + specialities + ", profileImageUrl=" + profileImageUrl + ", thumbnailProfileImageUrl=" + thumbnailProfileImageUrl
		+ ", coverImageUrl=" + coverImageUrl + ", thumbnailCoverImageUrl=" + thumbnailCoverImageUrl + "]";
    }

}
