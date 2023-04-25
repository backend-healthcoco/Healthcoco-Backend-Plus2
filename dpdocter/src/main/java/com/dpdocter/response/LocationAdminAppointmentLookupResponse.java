package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.dpdocter.collections.UserDeviceCollection;

public class LocationAdminAppointmentLookupResponse {

	private String locationId;

	private ObjectId userId;

	private String locationName;

	private List<AppointmentDoctorReminderResponse> drAppointments;

	private String locationAdminName;

	private String locationAdminMobileNumber;

	private String locationAdminEmailAddress;

	private List<UserDeviceCollection> userDevices;

	private Map<String, DoctorAppointmentSMSResponse> doctorAppointmentSMSResponseMap;

	private int totalAppointments = 0;

	private String message;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public List<AppointmentDoctorReminderResponse> getDrAppointments() {
		return drAppointments;
	}

	public void setDrAppointments(List<AppointmentDoctorReminderResponse> drAppointments) {
		this.drAppointments = drAppointments;
	}

	public String getLocationAdminMobileNumber() {
		return locationAdminMobileNumber;
	}

	public void setLocationAdminMobileNumber(String locationAdminMobileNumber) {
		this.locationAdminMobileNumber = locationAdminMobileNumber;
	}

	public String getLocationAdminEmailAddress() {
		return locationAdminEmailAddress;
	}

	public void setLocationAdminEmailAddress(String locationAdminEmailAddress) {
		this.locationAdminEmailAddress = locationAdminEmailAddress;
	}

	public List<UserDeviceCollection> getUserDevices() {
		return userDevices;
	}

	public void setUserDevices(List<UserDeviceCollection> userDevices) {
		this.userDevices = userDevices;
	}

	public Map<String, DoctorAppointmentSMSResponse> getDoctorAppointmentSMSResponseMap() {
		return doctorAppointmentSMSResponseMap;
	}

	public void setDoctorAppointmentSMSResponseMap(
			Map<String, DoctorAppointmentSMSResponse> doctorAppointmentSMSResponseMap) {
		this.doctorAppointmentSMSResponseMap = doctorAppointmentSMSResponseMap;
	}

	public int getTotalAppointments() {
		return totalAppointments;
	}

	public void setTotalAppointments(int totalAppointments) {
		this.totalAppointments = totalAppointments;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public String getLocationAdminName() {
		return locationAdminName;
	}

	public void setLocationAdminName(String locationAdminName) {
		this.locationAdminName = locationAdminName;
	}

	@Override
	public String toString() {
		return "LocationAdminAppointmentLookupResponse [locationId=" + locationId + ", userId=" + userId
				+ ", locationName=" + locationName + ", drAppointments=" + drAppointments + ", locationAdminName="
				+ locationAdminName + ", locationAdminMobileNumber=" + locationAdminMobileNumber
				+ ", locationAdminEmailAddress=" + locationAdminEmailAddress + ", userDevices=" + userDevices
				+ ", doctorAppointmentSMSResponseMap=" + doctorAppointmentSMSResponseMap + ", totalAppointments="
				+ totalAppointments + ", message=" + message + "]";
	}

}
