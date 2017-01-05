package com.dpdocter.enums;

public enum EyeType {

	RIGHT("RIGHT"), LEFT("LEFT");

	private String type;

	private EyeType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
