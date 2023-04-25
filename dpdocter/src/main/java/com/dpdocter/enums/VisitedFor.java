package com.dpdocter.enums;

public enum VisitedFor {
	REPORTS("REPORTS"), PRESCRIPTION("PRESCRIPTION"), CLINICAL_NOTES("CLINICAL_NOTES"), TREATMENT("TREATMENT"),
	FAMILY_HISTORY("FAMILY_HISTORY"), PERSONAL_HISTORY("PERSONAL_HISTORY"), EYE_PRESCRIPTION("EYE_PRESCRIPTION"),
	ALL("ALL");

	private String visitedFor;

	VisitedFor(String visitedFor) {
		this.visitedFor = visitedFor;
	}

	public String getVisitedFor() {
		return visitedFor;
	}

}
