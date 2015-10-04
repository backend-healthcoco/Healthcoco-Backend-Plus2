package com.dpdocter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MESSAGE")
public class Message {

    private String authKey;

    private String route;

    private String senderId;

    private String countryCode;

    private List<SMS> sms;

    public String getAuthKey() {
	return authKey;
    }

    @XmlElement(name = "AUTHKEY", defaultValue = "${AUTH_KEY}")
    public void setAuthKey(String authKey) {
	this.authKey = authKey;
    }

    public String getRoute() {
	return route;
    }

    @XmlElement(name = "ROUTE")
    public void setRoute(String route) {
	this.route = route;
    }

    public String getSenderId() {
	return senderId;
    }

    @XmlElement(name = "SENDER")
    public void setSenderId(String senderId) {
	this.senderId = senderId;
    }

    public String getCountryCode() {
	return countryCode;
    }

    @XmlElement(name = "COUNTRY")
    public void setCountryCode(String countryCode) {
	this.countryCode = countryCode;
    }

    public List<SMS> getSms() {
	return sms;
    }

    @XmlElement(name = "SMS")
    public void setSms(List<SMS> sms) {
	this.sms = sms;
    }

    @Override
    public String toString() {
	return "Message [authKey=" + authKey + ", route=" + route + ", senderId=" + senderId + ", countryCode=" + countryCode + ", sms=" + sms + "]";
    }

}
