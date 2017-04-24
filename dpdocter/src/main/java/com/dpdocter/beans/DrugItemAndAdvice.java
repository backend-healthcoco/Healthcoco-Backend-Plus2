package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.response.TestAndRecordDataResponse;

public class DrugItemAndAdvice {
	private List<TestAndRecordDataResponse> diagnosticTests;

	private String advice;

	public List<TestAndRecordDataResponse> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<TestAndRecordDataResponse> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}
	
	
	

}
