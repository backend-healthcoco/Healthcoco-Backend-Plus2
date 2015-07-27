package com.dpdocter.beans;

public class DoctorExperienceDetail {
    private String organization;

    private String city;

    private int from;

    private int to;

    public String getOrganization() {
	return organization;
    }

    public void setOrganization(String organization) {
	this.organization = organization;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public int getFrom() {
	return from;
    }

    public void setFrom(int from) {
	this.from = from;
    }

    public int getTo() {
	return to;
    }

    public void setTo(int to) {
	this.to = to;
    }

    @Override
    public String toString() {
	return "DoctorExperienceDetail [organization=" + organization + ", city=" + city + ", from=" + from + ", to=" + to + "]";
    }

}
