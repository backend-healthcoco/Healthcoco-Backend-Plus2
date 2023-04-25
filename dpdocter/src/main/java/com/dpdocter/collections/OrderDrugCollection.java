package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.enums.ReplyType;
import com.dpdocter.enums.WayOfOrder;

@Document(collection = "order_drug_cl")
public class OrderDrugCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId localeId;

	@Field
	private ObjectId userId;

	@Field
	private String uniqueRequestId;

	@Field
	private String uniqueResponseId;

	@Field
	private ReplyType replyType = ReplyType.REQUESTED;

	@Field
	private WayOfOrder wayOfOrder;

	@Field
	private UserAddress pickUpAddress;

	@Field
	private Long pickUpDate;

	@Field
	private LocaleWorkingHours pickUpTime;

	@Field
	private Boolean isCancelled = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getLocaleId() {
		return localeId;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

	public void setLocaleId(ObjectId localeId) {
		this.localeId = localeId;
	}

	public String getUniqueRequestId() {
		return uniqueRequestId;
	}

	public void setUniqueRequestId(String uniqueRequestId) {
		this.uniqueRequestId = uniqueRequestId;
	}

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	public ReplyType getReplyType() {
		return replyType;
	}

	public void setReplyType(ReplyType replyType) {
		this.replyType = replyType;
	}

	public WayOfOrder getWayOfOrder() {
		return wayOfOrder;
	}

	public void setWayOfOrder(WayOfOrder wayOfOrder) {
		this.wayOfOrder = wayOfOrder;
	}

	public UserAddress getPickUpAddress() {
		return pickUpAddress;
	}

	public void setPickUpAddress(UserAddress pickUpAddress) {
		this.pickUpAddress = pickUpAddress;
	}

	public Long getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Long pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public LocaleWorkingHours getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(LocaleWorkingHours pickUpTime) {
		this.pickUpTime = pickUpTime;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		return "OrderDrugCollection [id=" + id + ", localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + ", replyType=" + replyType
				+ ", wayOfOrder=" + wayOfOrder + ", pickUpAddress=" + pickUpAddress + ", pickUpDate=" + pickUpDate
				+ ", pickUpTime=" + pickUpTime + ", isCancelled=" + isCancelled + "]";
	}

}