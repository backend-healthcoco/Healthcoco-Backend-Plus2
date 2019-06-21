package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceTax;
import com.dpdocter.beans.Tax;

public class DentalImagingInvoiceItemResponse {

	private String type;
	private String dentalDiagnosticServiceId;
	private String serviceName;
	private List<String> toothNumber;
	private String CBCTQuadrant;
	private String CBCTArch;
	private String fov;
	private String instruction;
	private Integer quantity;
	private Double cost;
	private Discount totalDiscount;
	private Double totalCost = 0.0;
	private Tax totalTax;
	private List<InvoiceTax> invoiceTaxes;

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

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
	}

	public List<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
	}

	public String getFov() {
		return fov;
	}

	public void setFov(String fov) {
		this.fov = fov;
	}

	public String getInstruction() {
		return instruction;
	}

	public void setInstruction(String instruction) {
		this.instruction = instruction;
	}

}
