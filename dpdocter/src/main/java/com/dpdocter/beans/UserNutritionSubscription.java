package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;

public class UserNutritionSubscription extends GenericCollection {

	private String id;

	private String userId;

	private String nutritioinPlanId;

	private String subscriptionPlanId;

	private String orderId;

	private String transactionStatus;

	private Double discount = 0.0;

	private Double amount = 0.0;

	private Double discountAmount = 0.0;

	private Date fromDate = new Date();

	private Date toDate;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	

	public String getNutritioinPlanId() {
		return nutritioinPlanId;
	}

	public void setNutritioinPlanId(String nutritioinPlanId) {
		this.nutritioinPlanId = nutritioinPlanId;
	}

	public String getSubscriptionPlanId() {
		return subscriptionPlanId;
	}

	public void setSubscriptionPlanId(String subscriptionPlanId) {
		this.subscriptionPlanId = subscriptionPlanId;
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

	@Override
	public String toString() {
		return "UserNutritionSubscription [id=" + id + ", userId=" + userId + ", nutritioinPlanId=" + nutritioinPlanId
				+ ", subscriptionPlanId=" + subscriptionPlanId + ", orderId=" + orderId + ", transactionStatus="
				+ transactionStatus + ", discount=" + discount + ", amount=" + amount + ", discountAmount="
				+ discountAmount + ", fromDate=" + fromDate + ", toDate=" + toDate + ", discarded=" + discarded + "]";
	}

}
