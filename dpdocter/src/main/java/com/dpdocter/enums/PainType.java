package com.dpdocter.enums;

public enum PainType {
	DULL("DULL"), SHARP("SHARP"), SHOOTING("SHOOTING"), THROBBING("THROBBING"), BURNING("BURNING"),
	RAIDIATING("RADIATING");

	private String type;

	public String getType() {
		return type;
	}

	private PainType(String type) {
		this.type = type;
	}

}
