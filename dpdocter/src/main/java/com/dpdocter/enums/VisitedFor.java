package com.dpdocter.enums;

public enum VisitedFor {
    REPORTS("REPORTS"), PRESCRIPTION("PRESCRIPTION"), CLINICAL_NOTES("CLINICAL_NOTES"), FAMILY_HISTORY("FAMILY_HISTORY"), PERSONAL_HISTORY("PERSONAL_HISTORY");

    private String visitedFor;

    VisitedFor(String visitedFor) {
	this.visitedFor = visitedFor;
    }

    public String getVisitedFor() {
	return visitedFor;
    }

}
