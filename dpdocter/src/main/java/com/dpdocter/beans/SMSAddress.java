package com.dpdocter.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class SMSAddress {

    private String recipient;
    

    @JsonProperty("recipient")
    public String getRecipient() {
	return recipient;
    }

    @XmlAttribute(name = "TO")
    @JsonProperty("recipient")
    public void setRecipient(String recipient) {
	this.recipient = recipient;
    }
    
    
    
	@Override
    public String toString() {
	return "SMSAddress [recipient=" + recipient + "]";
    }

}
