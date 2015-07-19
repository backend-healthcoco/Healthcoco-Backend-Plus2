package com.dpdocter.beans;

public class DrugDurationUnit {
	private String id;

	private String unit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "DrugDurationUnit [id=" + id + ", unit=" + unit + "]";
	}

}
