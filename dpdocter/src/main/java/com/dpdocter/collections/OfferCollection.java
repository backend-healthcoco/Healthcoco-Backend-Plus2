package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Amount;
import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.response.ImageURLResponse;

@Document(collection = "offer_cl")
public class OfferCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String description;
	@Field
	private String promoCode;
	@Field
	private String colorCode;
	@Field
	private List<String> type;
	@Field
	private List<String> productType;
	@Field
	private Amount discount;
	@Field
	private Amount minimumPurchase;
	@Field
	private Boolean discarded = false;
	@Field
	private ImageURLResponse titleImage;
	@Field
	private Date fromDate;
	@Field
	private Date toDate;
	@Field
	private List<OfferSchedule> time;
	@Field
	private Integer rank = 0;
	@Field
	private Integer noOfTime = 0;
	@Field
	private List<ObjectId> drugIds;
	@Field
	private List<ObjectId> treatmentServiceIds;
	@Field
	private List<ObjectId> nutritionPlanIds;
	@Field
	private List<ObjectId> subscriptionPlanIds;

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

	public List<ObjectId> getDrugIds() {
		return drugIds;
	}

	public void setDrugIds(List<ObjectId> drugIds) {
		this.drugIds = drugIds;
	}

	public List<ObjectId> getTreatmentServiceIds() {
		return treatmentServiceIds;
	}

	public void setTreatmentServiceIds(List<ObjectId> treatmentServiceIds) {
		this.treatmentServiceIds = treatmentServiceIds;
	}

	public List<ObjectId> getNutritionPlanIds() {
		return nutritionPlanIds;
	}

	public void setNutritionPlanIds(List<ObjectId> nutritionPlanIds) {
		this.nutritionPlanIds = nutritionPlanIds;
	}

	public List<ObjectId> getSubscriptionPlanIds() {
		return subscriptionPlanIds;
	}

	public void setSubscriptionPlanIds(List<ObjectId> subscriptionPlanIds) {
		this.subscriptionPlanIds = subscriptionPlanIds;
	}

}
