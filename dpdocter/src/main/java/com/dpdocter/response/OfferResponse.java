package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Amount;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.collections.GenericCollection;

public class OfferResponse extends GenericCollection {
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

	private List<Drug> drugs;

	private List<TreatmentService> treatmentServices;

	private List<NutritionPlan> nutritionPlans;

	private List<SubscriptionNutritionPlan> subscriptionPlans;

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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
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

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Integer getNoOfTime() {
		return noOfTime;
	}

	public void setNoOfTime(Integer noOfTime) {
		this.noOfTime = noOfTime;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public List<TreatmentService> getTreatmentServices() {
		return treatmentServices;
	}

	public void setTreatmentServices(List<TreatmentService> treatmentServices) {
		this.treatmentServices = treatmentServices;
	}

	public List<NutritionPlan> getNutritionPlans() {
		return nutritionPlans;
	}

	public void setNutritionPlans(List<NutritionPlan> nutritionPlans) {
		this.nutritionPlans = nutritionPlans;
	}

	public List<SubscriptionNutritionPlan> getSubscriptionPlans() {
		return subscriptionPlans;
	}

	public void setSubscriptionPlans(List<SubscriptionNutritionPlan> subscriptionPlans) {
		this.subscriptionPlans = subscriptionPlans;
	}

	@Override
	public String toString() {
		return "OfferResponse [id=" + id + ", title=" + title + ", description=" + description + ", promoCode="
				+ promoCode + ", colorCode=" + colorCode + ", type=" + type + ", productType=" + productType
				+ ", discount=" + discount + ", minimumPurchase=" + minimumPurchase + ", discarded=" + discarded
				+ ", titleImage=" + titleImage + ", fromDate=" + fromDate + ", toDate=" + toDate + ", time=" + time
				+ ", rank=" + rank + ", noOfTime=" + noOfTime + ", drugs=" + drugs + ", treatmentServices="
				+ treatmentServices + ", nutritionPlans=" + nutritionPlans + ", subscriptionPlans=" + subscriptionPlans
				+ "]";
	}

}
