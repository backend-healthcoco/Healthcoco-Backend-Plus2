package com.dpdocter.beans;

import org.bson.types.ObjectId;

public class MedicineOrderItems {

	private ObjectId itemId;
	private Integer quantity;
	private Float amount;
	private DrugInfo drug;
	private Float amountAfterDiscount;
	private String currency;

	public ObjectId getItemId() {
		return itemId;
	}

	public void setItemId(ObjectId itemId) {
		this.itemId = itemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public DrugInfo getDrug() {
		return drug;
	}

	public void setDrug(DrugInfo drug) {
		this.drug = drug;
	}

	public Float getAmountAfterDiscount() {
		return amountAfterDiscount;
	}

	public void setAmountAfterDiscount(Float amountAfterDiscount) {
		this.amountAfterDiscount = amountAfterDiscount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		return "MedicineOrderItems [itemId=" + itemId + ", quantity=" + quantity + ", amount=" + amount + ", drug="
				+ drug + ", amountAfterDiscount=" + amountAfterDiscount + "]";
	}

}
