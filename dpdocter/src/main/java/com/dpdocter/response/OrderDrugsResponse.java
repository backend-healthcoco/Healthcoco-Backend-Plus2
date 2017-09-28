package com.dpdocter.response;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Discount;
import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.enums.WayOfOrder;
import com.dpdocter.request.PrescriptionRequest;

public class OrderDrugsResponse {

	private String id;
	
	private String localeId;

	private String userId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	private WayOfOrder wayOfOrder;
	
	private LocaleWorkingHours pickUpTime;
	
	private String pickUpDay;
	
	private String pickUpDate;
	
	private String pickUpAddress;

	private Discount discount;
	
    private Double discountedPrice;
	
	private Double realPrice;

	private PrescriptionRequest prescriptionRequest;

	private String localeName;
	
	private Address localeAddress;
	
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

	public String getPickUpDay() {
		return pickUpDay;
	}

	public void setPickUpDay(String pickUpDay) {
		this.pickUpDay = pickUpDay;
	}

	public String getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(String pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public String getPickUpAddress() {
		return pickUpAddress;
	}

	public void setPickUpAddress(String pickUpAddress) {
		this.pickUpAddress = pickUpAddress;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public Double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public Double getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(Double realPrice) {
		this.realPrice = realPrice;
	}

	public PrescriptionRequest getPrescriptionRequest() {
		return prescriptionRequest;
	}

	public void setPrescriptionRequest(PrescriptionRequest prescriptionRequest) {
		this.prescriptionRequest = prescriptionRequest;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public Address getLocaleAddress() {
		return localeAddress;
	}

	public void setLocaleAddress(Address localeAddress) {
		this.localeAddress = localeAddress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "OrderDrugsResponse [id=" + id + ", localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + ", wayOfOrder=" + wayOfOrder
				+ ", pickUpTime=" + pickUpTime + ", pickUpDay=" + pickUpDay + ", pickUpDate=" + pickUpDate
				+ ", pickUpAddress=" + pickUpAddress + ", discount=" + discount + ", discountedPrice=" + discountedPrice
				+ ", realPrice=" + realPrice + ", prescriptionRequest=" + prescriptionRequest + ", localeName="
				+ localeName + ", localeAddress=" + localeAddress + "]";
	}

}
