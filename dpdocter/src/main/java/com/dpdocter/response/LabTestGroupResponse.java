package com.dpdocter.response;

import java.util.List;

public class LabTestGroupResponse {

	private String specimen;

	private List<DiagnosticTestResponse> diagnosticTests;

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	public List<DiagnosticTestResponse> getDiagnosticTests() {
		return diagnosticTests;
	}

	public void setDiagnosticTests(List<DiagnosticTestResponse> diagnosticTests) {
		this.diagnosticTests = diagnosticTests;
	}

	@Override
	public String toString() {
		return "LabTestGroupResponse [specimen=" + specimen + ", diagnosticTests=" + diagnosticTests + "]";
	}

}