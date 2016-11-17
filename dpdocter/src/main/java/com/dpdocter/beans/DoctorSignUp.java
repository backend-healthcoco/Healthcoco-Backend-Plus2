package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
