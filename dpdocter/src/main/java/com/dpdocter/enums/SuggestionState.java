package com.dpdocter.enums;

public enum SuggestionState {
	PENDING("PENDING"), ACCEPTED("ACCEPTED"), CANCELED("CANCELED");

	String state;

	public String getState() {
		return state;
	}

	private SuggestionState(String state) {
		this.state = state;
	}

}
