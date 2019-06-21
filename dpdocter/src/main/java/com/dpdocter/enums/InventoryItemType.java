package com.dpdocter.enums;

public enum InventoryItemType {

	DRUGS("DRUGS"), SUPPLIES("SUPPLIES"), EQUIPMENTS("EQUIPMENTS");

	private String type;

	public String getType() {
		return type;
	}

	private InventoryItemType(String type) {
		this.type = type;
	}

}
