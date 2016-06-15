package com.dpdocter.enums;

public enum Type {
    ROLE("ROLE"), DOCTOR("DOCTOR");

    private String type;

    private Type(String type) {
	this.type = type;
    }

    public String getType() {
	return type;
    }

}
