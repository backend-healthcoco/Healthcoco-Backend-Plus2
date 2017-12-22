package com.dpdocter.beans;

import com.dpdocter.enums.Range;

public class PrescriptionDynamicField {

	private String drug = Range.BOTH.getRange();

	private String dosage = Range.BOTH.getRange();

	private String duration = Range.BOTH.getRange();

	private String direction = Range.BOTH.getRange();

	private String diagnosticTest = Range.BOTH.getRange();

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDiagnosticTest() {
		return diagnosticTest;
	}

	public void setDiagnosticTest(String diagnosticTest) {
		this.diagnosticTest = diagnosticTest;
	}

	@Override
	public String toString() {
		return "PrescriptionDynamicField [drug=" + drug + ", dosage=" + dosage + ", duration=" + duration
				+ ", direction=" + direction + "]";
	}

}
