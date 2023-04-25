package com.dpdocter.enums;

public enum DirectionEnum {
	WITH_MILK("WITH_MILK"), WITH_WATER("WITH_WATER"), WITH_WATER_OR_MILK("WITH_WATER_OR_MILK");

	private String direction;

	DirectionEnum(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

}
