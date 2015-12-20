package com.dpdocter.beans;

public class DoctorSignUp {

    private User user;

    private AccessControl accessControl;

    private Hospital hospital;

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public Hospital getHospital() {
	return hospital;
    }

    public void setHospital(Hospital hospital) {
	this.hospital = hospital;
    }

    public AccessControl getAccessControl() {
	return accessControl;
    }

    public void setAccessControl(AccessControl accessControl) {
	this.accessControl = accessControl;
    }

    @Override
    public String toString() {
	return "DoctorSignUp [user=" + user + ", accessControl=" + accessControl + ", hospital=" + hospital + "]";
    }

}
