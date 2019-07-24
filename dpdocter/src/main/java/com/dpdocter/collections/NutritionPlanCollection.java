package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PlanPriceDescription;
import com.dpdocter.enums.NutritionPlanType;

@Document(collection = "nutrition_plan_cl")
public class NutritionPlanCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private NutritionPlanType type;

	@Field
	private String planDescription;

	@Field
	private String shortPlanDescription;

	@Field
	private List<String> nutrientDescriptions;

	@Field
	private List<String> recommendedFoods;

	@Field
	private Double amount = 0.0;

	@Field
	private Double discountedAmount = 0.0;

	@Field
	private String planImage;

	@Field
	private String bannerImage;

	@Field
	private String backgroundColor;

	@Field
	private String secondaryBackgroundColor;

	@Field
	private Boolean discarded = false;

	@Field
	private Map<String, PlanPriceDescription> planPriceDescription;

	@Field
	private Integer rank = 0;

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
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

	public String getSecondaryBackgroundColor() {
		return secondaryBackgroundColor;
	}

	public void setSecondaryBackgroundColor(String secondaryBackgroundColor) {
		this.secondaryBackgroundColor = secondaryBackgroundColor;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	public String getShortPlanDescription() {
		return shortPlanDescription;
	}

	public void setShortPlanDescription(String shortPlanDescription) {
		this.shortPlanDescription = shortPlanDescription;
	}


	@Override
	public String toString() {
		return "NutritionPlanCollection [id=" + id + ", title=" + title + ", type=" + type + ", planDescription="
				+ planDescription + ", shortPlanDescription=" + shortPlanDescription + ", nutrientDescriptions="
				+ nutrientDescriptions + ", recommendedFoods=" + recommendedFoods + ", amount=" + amount
				+ ", discountedAmount=" + discountedAmount + ", planImage=" + planImage + ", bannerImage=" + bannerImage
				+ ", backgroundColor=" + backgroundColor + ", secondaryBackgroundColor=" + secondaryBackgroundColor
				+ ", discarded=" + discarded + ", planPriceDescription=" + planPriceDescription + ", rank=" + rank
				+ "]";
	}

}
