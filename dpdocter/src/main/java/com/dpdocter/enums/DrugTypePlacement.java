package com.dpdocter.enums;

public enum DrugTypePlacement {

	PREFIX("PREFIX"), SUFFIX("SUFFIX");

	private String placement;

	public String getPlacement() {
		return placement;
	}

	private DrugTypePlacement(String placement) {
		this.placement = placement;
	}

}
