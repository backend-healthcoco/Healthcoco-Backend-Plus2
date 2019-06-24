package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.NutritionPlanType;

import common.util.web.JacksonUtil;

public class NutritionPlan extends GenericCollection {

	private String id;

	private String title;

	private NutritionPlanType type;

	private String planDescription;
	
	private String shortPlanDescription;

	private List<String> nutrientDescriptions;

	private List<String> recommendedFoods;

	private Double amount = 0.0;

	private Double discountedAmount = 0.0;

	private String planImage;

	private String bannerImage;

	private String backgroundColor;

	private String secondaryBackgroundColor;

	private Boolean discarded = false;

	private Map<String, PlanPriceDescription> planPriceDescription;

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

	public List<String> getNutrientDescriptions() {
		return nutrientDescriptions;
	}

	public void setNutrientDescriptions(List<String> nutrientDescriptions) {
		this.nutrientDescriptions = nutrientDescriptions;
	}

	public List<String> getRecommendedFoods() {
		return recommendedFoods;
	}

	public void setRecommendedFoods(List<String> recommendedFoods) {
		this.recommendedFoods = recommendedFoods;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	public Map<String, PlanPriceDescription> getPlanPriceDescription() {
		return planPriceDescription;
	}

	public void setPlanPriceDescription(Map<String, PlanPriceDescription> planPriceDescription) {
		this.planPriceDescription = planPriceDescription;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public String getSecondaryBackgroundColor() {
		return secondaryBackgroundColor;
	}

	public void setSecondaryBackgroundColor(String secondaryBackgroundColor) {
		this.secondaryBackgroundColor = secondaryBackgroundColor;
	}

	public String getShortPlanDescription() {
		return shortPlanDescription;
	}

	public void setShortPlanDescription(String shortPlanDescription) {
		this.shortPlanDescription = shortPlanDescription;
	}

	@Override
	public String toString() {
		return "NutritionPlan [id=" + id + ", title=" + title + ", type=" + type + ", planDescription="
				+ planDescription + ", shortPlanDescription=" + shortPlanDescription + ", nutrientDescriptions="
				+ nutrientDescriptions + ", recommendedFoods=" + recommendedFoods + ", amount=" + amount
				+ ", discountnutedAmount=" + discountedAmount + ", planImage=" + planImage + ", bannerImage=" + bannerImage
				+ ", backgroundColor=" + backgroundColor + ", secondaryBackgroundColor=" + secondaryBackgroundColor
				+ ", discarded=" + discarded + ", planPriceDescription=" + planPriceDescription + "]";
	}

	public static void main(String[] args) {
		NutritionPlan nutritionPlan = new NutritionPlan();
		PlanPriceDescription planPriceDescription = new PlanPriceDescription();
		PlanDescriptionPrices planDescriptionPrices = new PlanDescriptionPrices();
		planDescriptionPrices.setAmount(1000.0);
		planDescriptionPrices.setDiscountedAmount(700.0);
		planDescriptionPrices.setDiscount(30.0);
		planPriceDescription.setPlanDescriptionPrices(planDescriptionPrices);
		PlanPrice planPrice = new PlanPrice();
		planPrice.setAmount(600.0);
		planPrice.setDiscountedAmount(500.0);
		planPrice.setAmountInText("Five Hundred Only");
		
		PlanPrice planPrice2 = new PlanPrice();
		planPrice.setAmount(3000.0);
		planPrice.setDiscountedAmount(2500.0);
		planPrice.setAmountInText("Two thousand Five Hundred Only");
		List<PlanPrice> planPrices = new ArrayList<>();
		planPrices.add(planPrice);
		planPrices.add(planPrice2);
		
		planPriceDescription.setPlanPrices(planPrices);
		
		Map<String, PlanPriceDescription> map = new HashMap<>();
		map.put("INR", planPriceDescription);
		
		nutritionPlan.setPlanPriceDescription(map);
		
		System.out.println(JacksonUtil.obj2Json(nutritionPlan));
		
	}
	
}
