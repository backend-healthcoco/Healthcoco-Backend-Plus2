package com.dpdocter.enums;

public enum DistanceEnum {
	KM("KM"), M("M"), CM("CM");

	private String distance;

	public String getDistance() {
		return distance;
	}

	private DistanceEnum(String distance) {
		this.distance = distance;
	}

	

}
