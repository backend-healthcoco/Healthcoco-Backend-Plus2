package com.dpdocter.enums;

public enum VaccineRoute {

	INTRAMUSCULAR("INTRAMUSCULAR"), SUBCUTANEOUS("SUBCUTANEOUS"), INTRADERMAL("INTRADERMAL"), ORAL("ORAL"),
	INTRANASAL("INTRANASAL");

	private String route;

	public String getRoute() {
		return route;
	}

	private VaccineRoute(String route) {
		this.route = route;
	}

}
