package com.dpdocter.enums;

public enum Period {
	AM("AM"), PM("PM");

	private String period;

	private Period(String period) {
		this.period = period;
	}

	public String getPeriod() {
		return period;
	}

}
