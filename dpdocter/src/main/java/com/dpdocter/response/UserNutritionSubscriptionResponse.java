package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.User;
import com.dpdocter.collections.GenericCollection;

public class UserNutritionSubscriptionResponse extends GenericCollection {

	private String id;

	private User user;

	private NutritionPlan NutritionPlan;

	private SubscriptionNutritionPlan subscriptionPlan;

	private String orderId;

	private String transactionStatus;

	private Double discount = 0.0;

	private Double amount = 0.0;

	private Double discountAmount = 0.0;

	private Date fromDate = new Date();

	private Date toDate;

	private Boolean isExpired = false;

	private Boolean discarded = false;

	private String countryCode;
	private Double discountedAmount = 0.0;

	public Boolean getIsExpired() {
		return isExpired;
	}

	public void setIsexpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SubscriptionNutritionPlan getSubscriptionPlan() {
		return subscriptionPlan;
	}

	public void setSubscriptionPlan(SubscriptionNutritionPlan subscriptionPlan) {
		this.subscriptionPlan = subscriptionPlan;
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

	public NutritionPlan getNutritionPlan() {
		return NutritionPlan;
	}

	public void setNutritionPlan(NutritionPlan nutritionPlan) {
		NutritionPlan = nutritionPlan;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public void setIsExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}

	
	public Double getDiscountedAmount() {
		return discountedAmount;
	}

	public void setDiscountedAmount(Double discountedAmount) {
		this.discountedAmount = discountedAmount;
	}

	@Override
	public String toString() {
		return "UserNutritionSubscriptionResponse [id=" + id + ", user=" + user + ", subscriptionPlan="
				+ subscriptionPlan + ", orderId=" + orderId + ", transactionStatus=" + transactionStatus + ", discount="
				+ discount + ", amount=" + amount + ", discountAmount=" + discountAmount + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", discarded=" + discarded + "]";
	}

}
