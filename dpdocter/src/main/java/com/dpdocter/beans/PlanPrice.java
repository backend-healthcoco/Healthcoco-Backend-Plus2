package com.dpdocter.beans;

public class PlanPrice {

	private String title;

	private String backgroundImage;

	private String amountInText;

	private String description;

	private Double amount = 0.0;

	private Double discount = 0.0;

	private Double discountedAmount = 0.0;

	private PlanDuration duration;
	
	private String currencySymbol;

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public PlanDuration getDuration() {
		return duration;
	}

	public void setDuration(PlanDuration duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getAmountInText() {
		return amountInText;
	}

	public void setAmountInText(String amountInText) {
		this.amountInText = amountInText;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

}
