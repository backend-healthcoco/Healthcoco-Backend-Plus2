package com.dpdocter.response;

import java.util.Map;

import com.dpdocter.beans.PlanPriceDescription;
import com.dpdocter.enums.NutritionPlanType;

public class NutritionPlanShortResponse {

	private String id;

	private String title;

	private NutritionPlanType type;

	private String planDescription;

	private String shortPlanDescription;

	private Double amount = 0.0;

	private Double discountedAmount = 0.0;

	private String planImage;

	private String bannerImage;

	private String backgroundColor;

	private String secondaryBackgroundColor;

	private Boolean discarded = false;

	private Map<String, PlanPriceDescription> planPriceDescription;

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

	public NutritionPlanType getType() {
		return type;
	}

	public void setType(NutritionPlanType type) {
		this.type = type;
	}

	public String getPlanDescription() {
		return planDescription;
	}

	public void setPlanDescription(String planDescription) {
		this.planDescription = planDescription;
	}

	public String getShortPlanDescription() {
		return shortPlanDescription;
	}

	public void setShortPlanDescription(String shortPlanDescription) {
		this.shortPlanDescription = shortPlanDescription;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public String getPlanImage() {
		return planImage;
	}

	public void setPlanImage(String planImage) {
		this.planImage = planImage;
	}

	public String getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(String bannerImage) {
		this.bannerImage = bannerImage;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getSecondaryBackgroundColor() {
		return secondaryBackgroundColor;
	}

	public void setSecondaryBackgroundColor(String secondaryBackgroundColor) {
		this.secondaryBackgroundColor = secondaryBackgroundColor;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Map<String, PlanPriceDescription> getPlanPriceDescription() {
		return planPriceDescription;
	}

	public void setPlanPriceDescription(Map<String, PlanPriceDescription> planPriceDescription) {
		this.planPriceDescription = planPriceDescription;
	}

	@Override
	public String toString() {
		return "NutritionPlanShortResponse [id=" + id + ", title=" + title + ", type=" + type + ", planDescription="
				+ planDescription + ", shortPlanDescription=" + shortPlanDescription + ", amount=" + amount
				+ ", discountedAmount=" + discountedAmount + ", planImage=" + planImage + ", bannerImage=" + bannerImage
				+ ", backgroundColor=" + backgroundColor + ", secondaryBackgroundColor=" + secondaryBackgroundColor
				+ ", discarded=" + discarded + "]";
	}

}
