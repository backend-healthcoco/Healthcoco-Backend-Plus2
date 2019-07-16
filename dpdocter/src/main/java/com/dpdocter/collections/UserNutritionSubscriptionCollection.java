package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_nutrition_subscription_cl")
public class UserNutritionSubscriptionCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId userId;
	@Field
	private ObjectId nutritionPlanId;
	@Field
	private ObjectId subscriptionPlanId;
	@Field
	private String orderId;
	@Field
	private String transactionStatus;
	@Field
	private Double discount = 0.0;
	@Field
	private Double amount = 0.0;
	@Field
	private Double discountAmount = 0.0;
	@Field
	private Date fromDate = new Date();
	@Field
	private Date toDate;
	@Field
	private Boolean discarded = false;
	@Field
	private Boolean isExpired = false;
	@Field
	private String countryCode;
	@Field
	private Double discountedAmount = 0.0;

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public ObjectId getNutritionPlanId() {
		return nutritionPlanId;
	}

	public void setNutritionPlanId(ObjectId nutritionPlanId) {
		this.nutritionPlanId = nutritionPlanId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(ObjectId subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	@Override
	public String toString() {
		return "UserNutritionSubscriptionCollection [id=" + id + ", userId=" + userId + ", nutritionPlanId="
				+ nutritionPlanId + ", orderId=" + orderId + ", transactionStatus=" + transactionStatus + ", discount="
				+ discount + ", amount=" + amount + ", discountAmount=" + discountAmount + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", discarded=" + discarded + "]";
	}

}
