package com.dpdocter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MESSAGE")
public class MessageXmlbean {
	@XmlElement(name="AUTHKEY")
	private String authKey;
	@XmlElement(name="TEXT")
	private String message;
	
	@XmlElement(name="TO")
	private List<String>mobileNumber;
	@XmlElement(name="SENDER")
	private String senderId;
	@XmlElement(name="ROUTE")
	private String promotionalRoute;
	@XmlElement(name="COUNTRY")
	private String countryCode;
	@XmlElement(name = "UNICODE")
	private String unicode;
	
	
	public MessageXmlbean() {
		
	}

	public MessageXmlbean(String authKey, String message, List<String> mobileNumber, String senderId,
			String promotionalRoute, String countryCode, String unicode) {
		super();
		this.authKey = authKey;
		this.message = message;
		this.mobileNumber = mobileNumber;
		this.senderId = senderId;
		this.promotionalRoute = promotionalRoute;
		this.countryCode = countryCode;
		this.unicode = unicode;
	}


	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	
	public String getMessage() {
		return message;
	}

	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getPromotionalRoute() {
		return promotionalRoute;
	}

	public void setPromotionalRoute(String promotionalRoute) {
		this.promotionalRoute = promotionalRoute;
	}

	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getUnicode() {
		return unicode;
	}

	public void setUnicode(String unicode) {
		this.unicode = unicode;
	}
	
	public List<String> getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(List<String> mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "MessageXmlbean [authKey=" + authKey + ", message=" + message + ", mobileNumber=" + mobileNumber
				+ ", senderId=" + senderId + ", promotionalRoute=" + promotionalRoute + ", countryCode=" + countryCode
				+ ", unicode=" + unicode + "]";
	}

	
	
}
