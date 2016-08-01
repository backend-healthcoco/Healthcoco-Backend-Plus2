package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;

public class DoctorMultipleDataAddEditResponse {

	private String id;
	
    private String doctorId;

    private String title;

    private String firstName;

    private DoctorExperience experience;

    private List<String> specialities;

    private String imageUrl;

    private String thumbnailUrl;

    private String coverImageUrl;

    private String coverThumbnailImageUrl;

    private String gender;
    
    private DOB dob;

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

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
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

	@Override
	public String toString() {
		return "DoctorMultipleDataAddEditResponse [id=" + id + ", doctorId=" + doctorId + ", title=" + title
				+ ", firstName=" + firstName + ", experience=" + experience + ", specialities=" + specialities
				+ ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", coverImageUrl=" + coverImageUrl
				+ ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", gender=" + gender + ", dob=" + dob + "]";
	}

}
