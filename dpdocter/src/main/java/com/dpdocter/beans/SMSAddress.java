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

	private List<String> recipients;

	public SMSAddress() {
		// TODO Auto-generated constructor stub
	}

	public SMSAddress(String recipient) {
		super();
		this.recipient = recipient;
	}

	@JsonProperty("recipient")
	public String getRecipient() {
		return recipient;
	}

	@XmlAttribute(name = "TO")
	@JsonProperty("recipient")
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@JsonProperty("recipients")
	public List<String> getRecipients() {
		return recipients;
	}

	@JsonProperty("recipients")
	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	@Override
	public String toString() {
		return "SMSAddress [recipient=" + recipient + "]";
	}

}
