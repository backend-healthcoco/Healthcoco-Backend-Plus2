package com.dpdocter.beans;

public class VitalSigns {

	private String pulse;
	
	private String temperature;
	
	private String breathing;
	
	private BloodPressure bloodPressure;

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

	@Override
	public String toString() {
		return "VitalSigns [pulse=" + pulse + ", temperature=" + temperature + ", breathing=" + breathing
				+ ", bloodPressure=" + bloodPressure + "]";
	}
}
