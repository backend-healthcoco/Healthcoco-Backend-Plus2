package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

public class ProcedureConsentForm {

	private List<Map<String, String>> headerFields;
	private String body;
	private List<Map<String, String>> footerFields;

	public List<Map<String, String>> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(List<Map<String, String>> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<Map<String, String>> getFooterFields() {
		return footerFields;
	}

	public void setFooterFields(List<Map<String, String>> footerFields) {
		this.footerFields = footerFields;
	}

	@Override
	public String toString() {
		return "ProcudereConsentForm [headerFields=" + headerFields + ", body=" + body + ", footerFields="
				+ footerFields + "]";
	}

}
