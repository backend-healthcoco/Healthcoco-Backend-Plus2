package com.dpdocter.enums;

public enum FeelingTypeEnum {

	GOOD("GOOD"), BETTER("BETTER"), OK("OK"), NOT_WELL("NOT_WELL"), SICK("SICK"), GREAT("GREAT");

	private String feeling;

	private FeelingTypeEnum(String feeling) {
		this.feeling = feeling;
	}

	public String getFeeling() {
		return feeling;
	}

}
