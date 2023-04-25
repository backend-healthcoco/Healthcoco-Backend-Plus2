package com.dpdocter.enums;

public enum OTPState {

	VERIFIED("VERIFIED"), EXPIRED("EXPIRED"), NOTVERIFIED("NOTVERIFIED");

	private String state;

	private OTPState(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}
}
