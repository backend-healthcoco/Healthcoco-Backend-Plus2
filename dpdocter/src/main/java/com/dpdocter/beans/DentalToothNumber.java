package com.dpdocter.beans;

public class DentalToothNumber {

	private String toothNumber;
	private String type;

	public String getToothNumber() {
		return toothNumber;
	}

	public void setToothNumber(String toothNumber) {
		this.toothNumber = toothNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DentalToothNumber [toothNumber=" + toothNumber + ", type=" + type + "]";
	}

}
