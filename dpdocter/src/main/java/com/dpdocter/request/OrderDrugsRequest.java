package com.dpdocter.request;

import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.enums.WayOfOrder;

public class OrderDrugsRequest {

	private String localeId;

	private String userId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	private WayOfOrder wayOfOrder;
	
	private LocaleWorkingHours time;
	
	private String day;
	
	private String address;
	
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

	public LocaleWorkingHours getTime() {
		return time;
	}

	public void setTime(LocaleWorkingHours time) {
		this.time = time;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "OrderDrugsRequest [localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId=" + uniqueRequestId
				+ ", uniqueResponseId=" + uniqueResponseId + ", wayOfOrder=" + wayOfOrder + ", time=" + time + ", day="
				+ day + ", address=" + address + "]";
	}

}
