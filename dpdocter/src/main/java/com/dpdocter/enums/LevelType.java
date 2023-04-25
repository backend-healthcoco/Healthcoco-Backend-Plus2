package com.dpdocter.enums;

public enum LevelType {

	LOW("LOW"), MEDIUM("MEDIUM"), HIGH("HIGH"), ALL("ALL");

	private String type;

	public String getType() {
		return type;
	}

	private LevelType(String type) {
		this.type = type;
	}

}
