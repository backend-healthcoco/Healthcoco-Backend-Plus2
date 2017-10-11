package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.DiagnosticTest;

public class LabTestGroupResponse {

	private String specimen;

	private List<DiagnosticTest> diagnosticTests;

	

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public List<DiagnosticTest> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTest> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	@Override
	public String toString() {
		return "LabTestGroupResponse [specimen=" + specimen + ", diagnosticTests=" + diagnosticTests + "]";
	}

}
