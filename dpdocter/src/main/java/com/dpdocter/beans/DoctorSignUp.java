package com.dpdocter.beans;

public class DoctorSignUp {

    private User user;

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

    @Override
    public String toString() {
	return "DoctorSignUp [user=" + user + ", hospital=" + hospital + "]";
    }

}
