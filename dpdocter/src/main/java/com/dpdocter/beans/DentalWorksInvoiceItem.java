package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.request.DentalStageRequest;

public class DentalWorksInvoiceItem {

	private DentalWork dentalWork;
	private List<DentalToothNumber> dentalToothNumbers;
	private Integer quantity;
	private Double cost = 0.0;
	private Discount discount;
	private Tax tax;
	private Double finalCost = 0.0;

	public DentalWork getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
	}

	public List<DentalToothNumber> getDentalToothNumbers() {
		return dentalToothNumbers;
	}

	public void setDentalToothNumbers(List<DentalToothNumber> dentalToothNumbers) {
		this.dentalToothNumbers = dentalToothNumbers;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public Double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(Double finalCost) {
		this.finalCost = finalCost;
	}

}
