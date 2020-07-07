package com.dpdocter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name="ADDRESS")
public class XMLMobile {

	@XmlAttribute(name = "TO")
	private String mobileNumber;
	
	
	

	public XMLMobile() {
		super();
	}

	
	public String getMobileNumber() {
		return mobileNumber;
	}


	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}


	


	public XMLMobile(String mobileNumber) {
		super();
		this.mobileNumber = mobileNumber;
	}


	@Override
	public String toString() {
		return "XMLMobile [mobileNumber=" + mobileNumber + "]";
	}
	
	
}
