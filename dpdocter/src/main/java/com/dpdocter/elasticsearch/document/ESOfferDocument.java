package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.Amount;
import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.response.ImageURLResponse;

@Document(indexName = "offers_in", type = "offers")
public class ESOfferDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String title;

	@Field(type = FieldType.Text)
	private String description;

	@Field(type = FieldType.Text)
	private String promoCode;

	@Field(type = FieldType.Text)
	private String colorCode;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> type;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> productType;

	@Field(type = FieldType.Object)
	private Amount discount;

	@Field(type = FieldType.Object)
	private Amount minimumPurchase;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Text)
	private ImageURLResponse titleImage;

	@Field(type = FieldType.Date)
	private Date fromDate;

	@Field(type = FieldType.Date)
	private Date toDate;

	@Field(type = FieldType.Nested)
	private List<OfferSchedule> time;

	@Field(type = FieldType.Integer)
	private Integer rank = 0;

	@Field(type = FieldType.Date)
	private Integer noOfTime = 0;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> drugIds;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> treatmentServiceIds;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> nutritionPlanIds;

	@MultiField(mainField = @Field(type = FieldType.Text))
	private List<String> subscriptionPlanIds;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public ImageURLResponse getTitleImage() {
		return titleImage;
	}

	public void setTitleImage(ImageURLResponse titleImage) {
		this.titleImage = titleImage;
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

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

}
