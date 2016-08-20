package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.UserCollection;

public class UserLocationWithDoctorClinicProfile {

    private String userId;

    private String locationId;

    private List<UserCollection> users;

    private List<DoctorCollection> doctors;
    
    private List<DoctorClinicProfile> doctorClinicProfiles;

	public List<UserCollection> getUsers() {
		return users;
	}

	public void setUsers(List<UserCollection> users) {
		this.users = users;
	}

	public List<DoctorCollection> getDoctors() {
		return doctors;
	}

	public void setDoctors(List<DoctorCollection> doctors) {
		this.doctors = doctors;
	}

	public List<DoctorClinicProfile> getDoctorClinicProfiles() {
		return doctorClinicProfiles;
	}

	public void setDoctorClinicProfiles(List<DoctorClinicProfile> doctorClinicProfiles) {
		this.doctorClinicProfiles = doctorClinicProfiles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "UserLocationWithDoctorClinicProfile [userId=" + userId + ", locationId=" + locationId + ", users="
				+ users + ", doctors=" + doctors + ", doctorClinicProfiles=" + doctorClinicProfiles + "]";
	}

}
