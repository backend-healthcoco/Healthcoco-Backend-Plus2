package com.dpdocter.beans;

import com.dpdocter.enums.Range;

public class DischargeSummaryDynamicFields {

	private String operationNotes = Range.BOTH.getRange();
	private String labourNotes = Range.BOTH.getRange();
	private String babyNotes = Range.BOTH.getRange();
	private String cement = Range.BOTH.getRange();
	private String implant = Range.BOTH.getRange();
	private String operationName = Range.BOTH.getRange();

	public String getOperationNotes() {
		return operationNotes;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public String getLabourNotes() {
		return labourNotes;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
	}

	public String getBabyNotes() {
		return babyNotes;
	}

	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
	}

	public String getCement() {
		return cement;
	}

	public void setCement(String cement) {
		this.cement = cement;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getImplant() {
		return implant;
	}

	public void setImplant(String implant) {
		this.implant = implant;
	}

}
