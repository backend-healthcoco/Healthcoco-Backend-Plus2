package com.dpdocter.beans;

public class MedicineOrderAddEditItems {

	private String itemId;
	private Integer quantity;
	private Float amount;
	private DrugInfo drug;
	private Float amountAfterDiscount;
	private String currency;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
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
		return "MedicineOrderAddEditItems [itemId=" + itemId + ", quantity=" + quantity + ", amount=" + amount
				+ ", drug=" + drug + ", amountAfterDiscount=" + amountAfterDiscount + "]";
	}

}
