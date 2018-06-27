package com.dpdocter.beans;

import java.util.List;

public class ProcedureConsentFormStructure {

	private List<String> headerFields;
	private String body;
	private List<String> footerFields;

	public List<String> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(List<String> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<String> getFooterFields() {
		return footerFields;
	}

	public void setFooterFields(List<String> footerFields) {
		this.footerFields = footerFields;
	}

	@Override
	public String toString() {
		return "ProcedureConsentFormStructure [headerFields=" + headerFields + ", body=" + body + ", footerFields="
				+ footerFields + "]";
	}

s}
