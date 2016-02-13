package com.dpdocter.enums;

public enum FeedbackType {

	HELP_US("HELP_US"), REFERRER("REFERRER"), PRESCRIPTION("PRESCRIPTION"), APPOINTMENT("APPOINTMENT"), FORDOCTOR("FORDOCTOR"), RECOMMENDATION("RECOMMENDATION");
	
	private String type;

    private FeedbackType(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }
}
