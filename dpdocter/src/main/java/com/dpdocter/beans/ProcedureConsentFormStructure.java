package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

public class ProcedureConsentFormStructure {

	private List<Map<String, ProcedureConsentFormFields>> headerFields;
	private String body;
	private List<Map<String, ProcedureConsentFormFields>> footerFields;

	public List<Map<String, ProcedureConsentFormFields>> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(List<Map<String, ProcedureConsentFormFields>> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Map<String, ProcedureConsentFormFields>> getFooterFields() {
		return footerFields;
	}

	public void setFooterFields(List<Map<String, ProcedureConsentFormFields>> footerFields) {
		this.footerFields = footerFields;
	}

}
