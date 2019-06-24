package com.dpdocter.beans;

import common.util.web.JacksonUtil;

public class SubscriptionNutritionPlan {

	private String id;

	private String title;

	private String backgroundImage;

	private String amountInText;

	private Double amount = 0.0;

	private String desciprion;

	private Double discount = 0.0;

	private Double discountedAmount = 0.0;

	private Boolean discarded = false;

	private String nutritionPlanId;

	private PlanDuration duration;

	public PlanDuration getDuration() {
		return duration;
	}

	public void setDuration(PlanDuration duration) {
		this.duration = duration;
	}

	public String getNutritionPlanId() {
		return nutritionPlanId;
	}

	public void setNutritionPlanId(String nutritionPlanId) {
		this.nutritionPlanId = nutritionPlanId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getDesciprion() {
		return desciprion;
	}

	public void setDesciprion(String desciprion) {
		this.desciprion = desciprion;
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

	@Override
	public String toString() {
		return "SubscriptionNutritionPlan [id=" + id + ", title=" + title + ", backgroundImage=" + backgroundImage
				+ ", amountInText=" + amountInText + ", amount=" + amount + ", desciprion=" + desciprion + ", discount="
				+ discount + ", discountedAmount=" + discountedAmount + ", discarded=" + discarded
				+ ", nutritionPlanId=" + nutritionPlanId + ", duration=" + duration + "]";
	}
	
	public static void main(String[] args) {
		System.out.println(JacksonUtil.obj2Json(new SubscriptionNutritionPlan()));
	}

}
