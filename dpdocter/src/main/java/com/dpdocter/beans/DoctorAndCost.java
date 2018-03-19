package com.dpdocter.beans;

public class DoctorAndCost {

	private String doctor;
	private Double cost = 0.0;

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	@Override
	public String toString() {
		return "DoctorAndCost [doctor=" + doctor + ", cost=" + cost + "]";
	}

}
