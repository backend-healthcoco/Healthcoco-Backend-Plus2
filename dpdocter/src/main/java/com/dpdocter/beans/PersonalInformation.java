package com.dpdocter.beans;

public class PersonalInformation {

	private Double bodyWeight;
	
	private Double bodyHeight;
	
	private Double waist;
	
	private Double bmi;
	
	private Double bsa;

	public Double getBodyWeight() {
		return bodyWeight;
	}

	public void setBodyWeight(Double bodyWeight) {
		this.bodyWeight = bodyWeight;
	}

	public Double getBodyHeight() {
		return bodyHeight;
	}

	public void setBodyHeight(Double bodyHeight) {
		this.bodyHeight = bodyHeight;
	}

	public Double getWaist() {
		return waist;
	}

	public void setWaist(Double waist) {
		this.waist = waist;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Double getBsa() {
		return bsa;
	}

	public void setBsa(Double bsa) {
		this.bsa = bsa;
	}

	@Override
	public String toString() {
		return "PersonalInformation [bodyWeight=" + bodyWeight + ", bodyHeight=" + bodyHeight + ", waist=" + waist
				+ ", bmi=" + bmi + ", bsa=" + bsa + "]";
	}
}
