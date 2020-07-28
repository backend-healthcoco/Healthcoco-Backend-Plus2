package com.dpdocter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType (XmlAccessType.FIELD)
@XmlRootElement(name="SMS")
public class XmlMessage {

	@XmlAttribute(name = "TEXT")
	private String message;
	
	@XmlElementRef(name = "ADDRESS")
	private List<XMLMobile> xmlMobile;
	
	

	public XmlMessage() {
		
	}

	

	public XmlMessage(String message, List<XMLMobile> xmlMobile) {
		super();
		this.message = message;
		this.xmlMobile = xmlMobile;
	}



	public List<XMLMobile> getXmlMobile() {
		return xmlMobile;
	}



	public void setXmlMobile(List<XMLMobile> xmlMobile) {
		this.xmlMobile = xmlMobile;
	}



	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
	
	
	
}
