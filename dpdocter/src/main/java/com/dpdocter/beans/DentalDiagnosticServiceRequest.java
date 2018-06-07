package com.dpdocter.beans;

import java.util.List;

public class DentalDiagnosticServiceRequest {

	private String type;
	private String dentalDiagnosticServiceId;
	private String serviceName;
	private List<String> toothNumber;
	private String CBCTQuadrant;
	private String CBCTArch;
	private Double cost = 0.0;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<String> getToothNumber() {
		return toothNumber;
	}

	public void setToothNumber(List<String> toothNumber) {
		this.toothNumber = toothNumber;
	}

	public String getCBCTQuadrant() {
		return CBCTQuadrant;
	}

	public void setCBCTQuadrant(String cBCTQuadrant) {
		CBCTQuadrant = cBCTQuadrant;
	}

	public String getCBCTArch() {
		return CBCTArch;
	}

	public void setCBCTArch(String cBCTArch) {
		CBCTArch = cBCTArch;
	}

	public String getDentalDiagnosticServiceId() {
		return dentalDiagnosticServiceId;
	}

	public void setDentalDiagnosticServiceId(String dentalDiagnosticServiceId) {
		this.dentalDiagnosticServiceId = dentalDiagnosticServiceId;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}
	
	

}
