package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

public class LoginResponse {

    private User user;

    private List<Hospital> hospitals = null;

    private List<String> role;

    private Boolean isTempPassword = false;

    public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public List<Hospital> getHospitals() {
	if (hospitals == null) {
	    hospitals = new ArrayList<Hospital>();
	}
	return hospitals;
    }

    public void setHospitals(List<Hospital> hospitals) {
	this.hospitals = hospitals;
    }

    public Boolean getIsTempPassword() {
	return isTempPassword;
    }

    public void setIsTempPassword(Boolean isTempPassword) {
	this.isTempPassword = isTempPassword;
    }

    @Override
    public String toString() {
	return "LoginResponse [user=" + user + ", hospitals=" + hospitals + ", role=" + role + ", tempPassword=" + isTempPassword + "]";
    }

}
