package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.ImageURLResponse;

public class Offer extends GenericCollection {

	private String id;

	private String title;

	private String description;

	private String promoCode;

	private String colorCode;

	private List<String> type;

	private List<String> productType;

	private Amount discount;

	private Amount minimumPurchase;

	private Boolean discarded = false;

	private ImageURLResponse titleImage;

	private Date fromDate;

	private Date toDate;

	private List<OfferSchedule> time;

	private Integer rank = 0;

	private Integer noOfTime = 0;

	private List<String> drugIds;

	private List<String> treatmentServiceIds;

	private List<String> nutritionPlanIds;

	private List<String> subscriptionPlanIds;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getProductType() {
		return productType;
	}

	public void setProductType(List<String> productType) {
		this.productType = productType;
	}

	public Amount getDiscount() {
		return discount;
	}

	public void setDiscount(Amount discount) {
		this.discount = discount;
	}

	public Amount getMinimumPurchase() {
		return minimumPurchase;
	}

	public void setMinimumPurchase(Amount minimumPurchase) {
		this.minimumPurchase = minimumPurchase;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ImageURLResponse getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(ImageURLResponse titleImage) {
		this.titleImage = titleImage;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<OfferSchedule> getTime() {
		return time;
	}

	public void setTime(List<OfferSchedule> time) {
		this.time = time;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public List<String> getDrugIds() {
		return drugIds;
	}

	public void setDrugIds(List<String> drugIds) {
		this.drugIds = drugIds;
	}

	public List<String> getTreatmentServiceIds() {
		return treatmentServiceIds;
	}

	public void setTreatmentServiceIds(List<String> treatmentServiceIds) {
		this.treatmentServiceIds = treatmentServiceIds;
	}

	public List<String> getNutritionPlanIds() {
		return nutritionPlanIds;
	}

	public void setNutritionPlanIds(List<String> nutritionPlanIds) {
		this.nutritionPlanIds = nutritionPlanIds;
	}

	public List<String> getSubscriptionPlanIds() {
		return subscriptionPlanIds;
	}

	public void setSubscriptionPlanIds(List<String> subscriptionPlanIds) {
		this.subscriptionPlanIds = subscriptionPlanIds;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

}
