package com.dpdocter.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MESSAGE")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageXmlbean {
	@XmlElement(name = "AUTHKEY")
	private String authKey;

	@XmlElementRef(name = "SMS")
	private XmlMessage message;

	@XmlElement(name = "SENDER")
	private String senderId;
	@XmlElement(name = "ROUTE")
	private String promotionalRoute;
	@XmlElement(name = "COUNTRY")
	private String countryCode;
	@XmlElement(name = "UNICODE")
	private String unicode;

	public MessageXmlbean() {

	}

	public MessageXmlbean(String authKey, XmlMessage message, String senderId, String promotionalRoute,
			String countryCode, String unicode) {
		super();
		this.authKey = authKey;
		this.message = message;
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

	@Override
	public String toString() {
		return "MessageXmlbean [authKey=" + authKey + ", message=" + message + ", senderId=" + senderId
				+ ", promotionalRoute=" + promotionalRoute + ", countryCode=" + countryCode + ", unicode=" + unicode
				+ "]";
	}

}
