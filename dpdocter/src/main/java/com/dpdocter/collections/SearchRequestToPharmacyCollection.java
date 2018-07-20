package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;

@Document(collection = "search_request_to_pharmacy_cl")
public class SearchRequestToPharmacyCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId userId;

	@Field
	private String uniqueRequestId;

	@Field
	private Boolean isAlreadyRequested = false;

	@Field
	private String replyType;

	@Field
	private ObjectId localeId;

	@Field
	private Discount discount;
	
	@Field
	private String uniqueResponseId;
	
	@Field
	private double discountedPrice;

	@Field
	private double realPrice;

	@Field
	private String note;

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

	public ObjectId getLocaleId() {
		return localeId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

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

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	public Boolean getIsAlreadyRequested() {
		return isAlreadyRequested;
	}

	public void setIsAlreadyRequested(Boolean isAlreadyRequested) {
		this.isAlreadyRequested = isAlreadyRequested;
	}

	@Override
	public String toString() {
		return "SearchRequestToPharmacyCollection [id=" + id + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", isAlreadyRequested=" + isAlreadyRequested + ", replyType=" + replyType
				+ ", localeId=" + localeId + ", discount=" + discount + ", uniqueResponseId=" + uniqueResponseId
				+ ", discountedPrice=" + discountedPrice + ", realPrice=" + realPrice + ", note=" + note + "]";
	}

}
