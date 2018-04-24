package com.dpdocter.response;

public class DurationResponse {
	
	private String value;

	// @Field(type = FieldType.Nested)
	private String durationUnit;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDurationUnit() {
		return durationUnit;
	}

	public void setDurationUnit(String durationUnit) {
		this.durationUnit = durationUnit;
	}
	
	

}
