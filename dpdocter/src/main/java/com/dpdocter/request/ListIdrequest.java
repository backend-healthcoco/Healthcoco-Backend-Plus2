package com.dpdocter.request;

import java.util.List;

public class ListIdrequest {

	private List<String> ids;

	private String emailAddress;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	@Override
	public String toString() {
		return "ListIdrequest [ids=" + ids + ", emailAddress=" + emailAddress + "]";
	}
}
