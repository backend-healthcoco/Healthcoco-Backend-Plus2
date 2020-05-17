package com.dpdocter.beans;

import com.dpdocter.enums.ConsultationType;

public class DoctorConsultation {

	private ConsultationType consultationType;
	
	private Double cost;
	
	private Double healthcocoCharges;

	public ConsultationType getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(ConsultationType consultationType) {
		this.consultationType = consultationType;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getHealthcocoCharges() {
		return healthcocoCharges;
	}

	public void setHealthcocoCharges(Double healthcocoCharges) {
		this.healthcocoCharges = healthcocoCharges;
	}

	@Override
	public String toString() {
		return "DoctorConsultation [consultationType=" + consultationType + ", cost=" + cost
				+ ", healthcocoCharges=" + healthcocoCharges + "]";
	}
}
