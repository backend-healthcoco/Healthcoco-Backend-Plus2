package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PlanDuration;

@Document(collection = "subscription_nutrition_plan_cl")
public class SubscriptionNutritionPlanCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String backgroundImage;

	@Field
	private String amountInText;

	@Field
	private Double amount = 0.0;

	@Field
	private String desciprion;

	@Field
	private Double discount = 0.0;

	@Field
	private Double discountedAmount = 0.0;

	@Field
	private ObjectId nutritionPlanId;

	@Field
	private Boolean discarded = false;

	@Field
	private PlanDuration duration;

	@Field
	private String countryCode;

	public PlanDuration getDuration() {
		return duration;
	}

	public void setDuration(PlanDuration duration) {
		this.duration = duration;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getNutritionPlanId() {
		return nutritionPlanId;
	}

	public void setNutritionPlanId(ObjectId nutritionPlanId) {
		this.nutritionPlanId = nutritionPlanId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "SuscriptionNutritionPlanCollection [id=" + id + ", title=" + title + ", backgroundImage="
				+ backgroundImage + ", amountInText=" + amountInText + ", amount=" + amount + ", desciprion="
				+ desciprion + ", discount=" + discount + ", discountedAmount=" + discountedAmount
				+ ", nutritionPlanId=" + nutritionPlanId + "]";
	}

}
