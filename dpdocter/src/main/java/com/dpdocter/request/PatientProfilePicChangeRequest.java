package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class PatientProfilePicChangeRequest {
    private String username;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private FileDetails image;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "PatientProfilePicChangeRequest [username=" + username + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", image=" + image + "]";
	}
}
