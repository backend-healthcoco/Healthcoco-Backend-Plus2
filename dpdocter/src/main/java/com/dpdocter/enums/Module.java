package com.dpdocter.enums;

public enum Module {

	CONTACT("CONTACT"), EMR("EMR"), CALENDER("CALENDER");

	private String module;

	private Module(String module) {
		this.module = module;
	}

	public String getModule() {
		return module;
	}
}
