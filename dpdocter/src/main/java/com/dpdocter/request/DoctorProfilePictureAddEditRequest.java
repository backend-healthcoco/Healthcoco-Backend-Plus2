package com.dpdocter.request;

import com.dpdocter.beans.FileDetails;

public class DoctorProfilePictureAddEditRequest {
	private String doctorId;

	private FileDetails image;

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return "DoctorProfilePictureAddEditRequest [doctorId=" + doctorId + ", image=" + image + "]";
	}

}
