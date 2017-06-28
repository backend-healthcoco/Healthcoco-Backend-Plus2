package com.dpdocter.request;

public class BulkSMSRequest {

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private String groupId;

	private String message;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "BulkSMSRequest [locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", groupId=" + groupId + ", message=" + message + "]";
	}

}
