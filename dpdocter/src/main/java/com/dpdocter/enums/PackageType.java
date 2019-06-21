package com.dpdocter.enums;

public enum PackageType {

	FREE("FREE"), BASIC("BASIC"), PRO("PRO"), ADVANCE("ADVANCE");

	private String type;

	public String getType() {
		return type;
	}

	private PackageType(String type) {
		this.type = type;
	}

}
