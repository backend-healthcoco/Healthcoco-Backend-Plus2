package com.dpdocter.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
public class SMS {
    private String smsText;

    private SMSAddress smsAddress;

    @JsonProperty("smsText")
    public String getSmsText() {
	return smsText;
    }

    @XmlAttribute(name = "TEXT")
    @JsonProperty("smsText")
    public void setSmsText(String smsText) {
	this.smsText = smsText;
    }

    @JsonProperty("smsAddress")
    public SMSAddress getSmsAddress() {
	return smsAddress;
    }

    @XmlElement(name = "ADDRESS")
    @JsonProperty("smsAddress")
    public void setSmsAddress(SMSAddress smsAddress) {
	this.smsAddress = smsAddress;
    }

    @Override
    public String toString() {
	return "SMS [smsText=" + smsText + ", smsAddress=" + smsAddress + "]";
    }

}
