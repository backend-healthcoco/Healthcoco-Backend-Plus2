package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.Role;

public class ClinicDoctorResponse {

    private String firstName;

    private Role role;

    private Boolean isActivate;

    private Date lastSession;

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public Role getRole() {
	return role;
    }

    public void setRole(Role role) {
	this.role = role;
    }

    public Boolean getIsActivate() {
	return isActivate;
    }

    public void setIsActivate(Boolean isActivate) {
	this.isActivate = isActivate;
    }

    public Date getLastSession() {
	return lastSession;
    }

    public void setLastSession(Date lastSession) {
	this.lastSession = lastSession;
    }

    @Override
    public String toString() {
	return "ClinicDoctorResponse [firstName=" + firstName + ", role=" + role + ", isActivate=" + isActivate + ", lastSession=" + lastSession + "]";
    }
}
