package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.DOB;

public class DoctorMultipleDataAddEditRequest {

	private String doctorId;

	private String title;

	private String firstName;

	private int experience;

	private List<String> specialities;

	private String gender;

	private DOB dob;

	private String freshchatRestoreId;

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

	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public List<String> getSpeciality() {
		return specialities;
	}

	public void setSpeciality(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public String getFreshchatRestoreId() {
		return freshchatRestoreId;
	}

	public void setFreshchatRestoreId(String freshchatRestoreId) {
		this.freshchatRestoreId = freshchatRestoreId;
	}

	@Override
	public String toString() {
		return "DoctorMultipleDataAddEditRequest [doctorId=" + doctorId + ", title=" + title + ", firstName="
				+ firstName + ", experience=" + experience + ", specialities=" + specialities + ", gender=" + gender + ", dob=" + dob + "]";
	}
}
