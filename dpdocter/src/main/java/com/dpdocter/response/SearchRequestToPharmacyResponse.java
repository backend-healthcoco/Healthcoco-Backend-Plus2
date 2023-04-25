package com.dpdocter.response;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Locale;
import com.dpdocter.collections.GenericCollection;

public class SearchRequestToPharmacyResponse extends GenericCollection {

	private String id;

	private String userId;

	private String uniqueRequestId;

	private String replyType;

	private String localeId;

	private Locale locale;

	private Discount discount;

	private double discountedPrice;

	private double realPrice;

	private String note;

	private String uniqueResponseId;

	private Double distance;

	private Boolean isAlreadyRequested = false;

	public double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(double realPrice) {
		this.realPrice = realPrice;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

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

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getReplyType() {
		return replyType;
	}

	public void setReplyType(String replyType) {
		this.replyType = replyType;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "SearchRequestToPharmacyResponse [id=" + id + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", replyType=" + replyType + ", localeId=" + localeId + ", locale=" + locale
				+ ", discount=" + discount + ", uniqueResponseId=" + uniqueResponseId + "]";
	}

	public Boolean getIsAlreadyRequested() {
		return isAlreadyRequested;
	}

	public void setIsAlreadyRequested(Boolean isAlreadyRequested) {
		this.isAlreadyRequested = isAlreadyRequested;
	}

}
