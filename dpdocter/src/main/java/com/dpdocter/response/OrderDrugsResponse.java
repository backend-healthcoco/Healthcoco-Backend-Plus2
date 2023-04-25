package com.dpdocter.response;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Discount;
import com.dpdocter.beans.LocaleWorkingHours;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.WayOfOrder;
import com.dpdocter.request.PrescriptionRequest;

public class OrderDrugsResponse extends GenericCollection {

	private String id;

	private String localeId;

	private String userId;

	private String uniqueRequestId;

	private String uniqueResponseId;

	private WayOfOrder wayOfOrder;

	private LocaleWorkingHours pickUpTime;

	private Long pickUpDate;

	private UserAddress pickUpAddress;

	private String pickUpFormattedAddress;

	private Discount discount;

	private Double discountedPrice;

	private Double realPrice;

	private PrescriptionRequest prescriptionRequest;

	private String localeName;

	private Address localeAddress;

	private String localeFormattedAddress;

	private Boolean isCancelled = false;

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

	public Long getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(Long pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public UserAddress getPickUpAddress() {
		return pickUpAddress;
	}

	public void setPickUpAddress(UserAddress pickUpAddress) {
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

	public String getPickUpFormattedAddress() {
		return pickUpFormattedAddress;
	}

	public void setPickUpFormattedAddress(String pickUpFormattedAddress) {
		this.pickUpFormattedAddress = pickUpFormattedAddress;
	}

	public String getLocaleFormattedAddress() {
		return localeFormattedAddress;
	}

	public void setLocaleFormattedAddress(String localeFormattedAddress) {
		this.localeFormattedAddress = localeFormattedAddress;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	@Override
	public String toString() {
		return "OrderDrugsResponse [id=" + id + ", localeId=" + localeId + ", userId=" + userId + ", uniqueRequestId="
				+ uniqueRequestId + ", uniqueResponseId=" + uniqueResponseId + ", wayOfOrder=" + wayOfOrder
				+ ", pickUpTime=" + pickUpTime + ", pickUpDate=" + pickUpDate + ", pickUpAddress=" + pickUpAddress
				+ ", pickUpFormattedAddress=" + pickUpFormattedAddress + ", discount=" + discount + ", discountedPrice="
				+ discountedPrice + ", realPrice=" + realPrice + ", prescriptionRequest=" + prescriptionRequest
				+ ", localeName=" + localeName + ", localeAddress=" + localeAddress + ", localeFormattedAddress="
				+ localeFormattedAddress + ", isCancelled=" + isCancelled + "]";
	}

}
