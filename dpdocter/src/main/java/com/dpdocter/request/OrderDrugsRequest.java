package com.dpdocter.request;

import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.enums.WayOfOrder;

public class OrderDrugsRequest {

	private String id;
	
	private String localeId;

	private String userId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	private WayOfOrder wayOfOrder;
	
	private LocaleWorkingHours pickUpTime;
	
	private Long pickUpDate;
	
	private UserAddress pickUpAddress;
	
	private Boolean isCancelled = false;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
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

	public String getUniqueResponseId() {
		return uniqueResponseId;
	}

	public void setUniqueResponseId(String uniqueResponseId) {
		this.uniqueResponseId = uniqueResponseId;
	}

	public WayOfOrder getWayOfOrder() {
		return wayOfOrder;
	}

	public void setWayOfOrder(WayOfOrder wayOfOrder) {
		this.wayOfOrder = wayOfOrder;
	}

	public LocaleWorkingHours getPickUpTime() {
		return pickUpTime;
	}

	public void setPickUpTime(LocaleWorkingHours pickUpTime) {
		this.pickUpTime = pickUpTime;
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

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		return "OrderDrugsRequest [id=" + id + ", localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + ", wayOfOrder=" + wayOfOrder
				+ ", pickUpTime=" + pickUpTime + ", pickUpDate=" + pickUpDate + ", pickUpAddress=" + pickUpAddress
				+ ", isCancelled=" + isCancelled + "]";
	}

}
