package com.dpdocter.beans;

import java.util.Map;

public class ProcedureConsentForm {

	private Map<String, String> headerFields;
	private String body;
	private Map<String, String> footerFields;

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, String> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getFooterFields() {
		return footerFields;
	}

	public void setFooterFields(Map<String, String> footerFields) {
		this.footerFields = footerFields;
	}

	@Override
	public String toString() {
		return "ProcudereConsentForm [headerFields=" + headerFields + ", body=" + body + ", footerFields="
				+ footerFields + "]";
	}

}
