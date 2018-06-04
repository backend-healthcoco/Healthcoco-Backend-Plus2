package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;

public class DentalImagingInvoiceItem {

	private String type;
	private ObjectId dentalDiagnosticServiceId;
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

	public ObjectId getDentalDiagnosticServiceId() {
		return dentalDiagnosticServiceId;
	}

	public void setDentalDiagnosticServiceId(ObjectId dentalDiagnosticServiceId) {
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
