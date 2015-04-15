package com.dpdocter.enums;

public enum DrugTypeEnum {
	TABLET("TABLET"), SYRUP("SYRUP"), INJECTION("INJECTION"), LOTION("LOTION");

	private String type;

	DrugTypeEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
