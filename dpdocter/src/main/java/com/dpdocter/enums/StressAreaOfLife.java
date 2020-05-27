package com.dpdocter.enums;

public enum StressAreaOfLife {
	DEPRESSION("DEPRESSION"), BIPOLAR("BIPOLAR"), SAD("SAD");

	private String type;

	public String getType() {
		return type;
	}

	private StressAreaOfLife(String type) {
		this.type = type;
	}
}
