package com.dpdocter.enums;

public enum UserState {

    USERSTATECOMPLETE("USERSTATECOMPLETE"), USERSTATEINCOMPLETE("USERSTATEINCOMPLETE"), NOTVERIFIED("NOTVERIFIED"), NOTACTIVATED("NOTACTIVATED");

    String state;

    public String getState() {
	return state;
    }

    private UserState(String state) {
	this.state = state;
    }
}
