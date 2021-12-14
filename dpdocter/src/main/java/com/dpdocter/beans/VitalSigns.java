package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VitalSigns {

	private String pulse;

	private String temperature;

	private String breathing;

	private BloodPressure bloodPressure;

	private String height;

	private String weight;

	private String spo2;

	private String bmi;

	private String bsa;

	private String pefr;

	public String getPulse() {
		return pulse;
	}

	public void setPulse(String pulse) {
		this.pulse = pulse;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getBreathing() {
		return breathing;
	}

	public void setBreathing(String breathing) {
		this.breathing = breathing;
	}

	public BloodPressure getBloodPressure() {
		return bloodPressure;
	}

	public void setBloodPressure(BloodPressure bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getSpo2() {
		return spo2;
	}

	public void setSpo2(String spo2) {
		this.spo2 = spo2;
	}

	public String getBmi() {
		return bmi;
	}

	public void setBmi(String bmi) {
		this.bmi = bmi;
	}

	public String getBsa() {
		return bsa;
	}

	public void setBsa(String bsa) {
		this.bsa = bsa;
	}
	
	

	public String getPefr() {
		return pefr;
	}

	public void setPefr(String pefr) {
		this.pefr = pefr;
	}

	@Override
	public String toString() {
		return "VitalSigns [pulse=" + pulse + ", temperature=" + temperature + ", breathing=" + breathing
				+ ", bloodPressure=" + bloodPressure + ", height=" + height + ", weight=" + weight + "]";
	}
}
