package com.dpdocter.request;

public class ConsentFrequencyRequest {

	private String unit = "HOUR";
	private int value = 0;
	private int repeats = 0;
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getRepeats() {
		return repeats;
	}
	public void setRepeats(int repeats) {
		this.repeats = repeats;
	}
	
	
}
