package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

public class DentalImagingInvoiceItemResponse {

	private String type;
	private String dentalDiagnosticServiceId;
	private String serviceName;
	private List<String> toothNumber;
	private String CBCTQuadrant;
	private String CBCTArch;
	private Integer quantity;
	private Double cost;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDentalDiagnosticServiceId() {
		return dentalDiagnosticServiceId;
	}

	public void setDentalDiagnosticServiceId(String dentalDiagnosticServiceId) {
		this.dentalDiagnosticServiceId = dentalDiagnosticServiceId;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

}
