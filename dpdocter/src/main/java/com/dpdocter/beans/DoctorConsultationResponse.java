package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.ConsultationType;

public class DoctorConsultationResponse {

	private String consultationType;
	
	private Double cost;
	
	private Double healthcocoCharges;

	public String getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(String consultationType) {
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
	
	
}
