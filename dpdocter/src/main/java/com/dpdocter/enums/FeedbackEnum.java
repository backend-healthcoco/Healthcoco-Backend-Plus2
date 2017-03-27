package com.dpdocter.enums;

public enum FeedbackEnum {

	AVERAGE("AVERAGE"), FAIR("FAIR"), GOOD("GOOD"), VERY_GOOD("GOOD"), EXCELLENT("EXCELLENT");

	private String feedbackType;

	public String getFeedbackType() {
		return feedbackType;
	}

	private FeedbackEnum(String feedbackType) {
		this.feedbackType = feedbackType;
	}

}
