package com.dpdocter.beans;

import java.util.Map;

public class ProcedureConsentForm {

	private Map<String, ProcedureConsentFormFields> headerFields;
	private String body;
	private Map<String, ProcedureConsentFormFields> footerFields;

	public Map<String, ProcedureConsentFormFields> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, ProcedureConsentFormFields> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, ProcedureConsentFormFields> getFooterFields() {
		return footerFields;
	}

	public void setFooterFields(Map<String, ProcedureConsentFormFields> footerFields) {
		this.footerFields = footerFields;
	}

	@Override
	public String toString() {
		return "ProcudereConsentForm [headerFields=" + headerFields + ", body=" + body + ", footerFields="
				+ footerFields + "]";
	}

}
