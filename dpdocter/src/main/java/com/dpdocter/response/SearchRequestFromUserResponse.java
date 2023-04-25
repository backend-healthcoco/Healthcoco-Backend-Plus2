package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.request.PrescriptionRequest;

public class SearchRequestFromUserResponse extends GenericCollection {

	private String id;

	private String userId;

	private String uniqueRequestId;

	private PrescriptionRequest prescriptionRequest;

	private String localeId;

	private String pharmacyName;

	private String location;

	private Double latitude;

	private Double longitude;

	private Integer countForNo;

	private Integer countForYes;

	private Boolean isCancelled = false;

	private String localeName;

	private Address localeAddress;

	private String localeFormattedAddress;

	private Boolean isTwentyFourSevenOpen = false;

	private List<String> pharmacyType;

	private Long noOfLocaleRecommendation;

	private Boolean isHomeDeliveryAvailable = false;

	private Double homeDeliveryRadius;

	private String paymentInfo;

	private List<String> paymentInfos;

	private Boolean isOrdered = false;

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

	public PrescriptionRequest getPrescriptionRequest() {
		return prescriptionRequest;
	}

	public void setPrescriptionRequest(PrescriptionRequest prescriptionRequest) {
		this.prescriptionRequest = prescriptionRequest;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getPharmacyName() {
		return pharmacyName;
	}

	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getCountForNo() {
		return countForNo;
	}

	public void setCountForNo(Integer countForNo) {
		this.countForNo = countForNo;
	}

	public Integer getCountForYes() {
		return countForYes;
	}

	public void setCountForYes(Integer countForYes) {
		this.countForYes = countForYes;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
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

	public Boolean getIsTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setIsTwentyFourSevenOpen(Boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	public List<String> getPharmacyType() {
		return pharmacyType;
	}

	public void setPharmacyType(List<String> pharmacyType) {
		this.pharmacyType = pharmacyType;
	}

	public Long getNoOfLocaleRecommendation() {
		return noOfLocaleRecommendation;
	}

	public void setNoOfLocaleRecommendation(Long noOfLocaleRecommendation) {
		this.noOfLocaleRecommendation = noOfLocaleRecommendation;
	}

	public Boolean getIsHomeDeliveryAvailable() {
		return isHomeDeliveryAvailable;
	}

	public void setIsHomeDeliveryAvailable(Boolean isHomeDeliveryAvailable) {
		this.isHomeDeliveryAvailable = isHomeDeliveryAvailable;
	}

	public Double getHomeDeliveryRadius() {
		return homeDeliveryRadius;
	}

	public void setHomeDeliveryRadius(Double homeDeliveryRadius) {
		this.homeDeliveryRadius = homeDeliveryRadius;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public List<String> getPaymentInfos() {
		return paymentInfos;
	}

	public void setPaymentInfos(List<String> paymentInfos) {
		this.paymentInfos = paymentInfos;
	}

	public Boolean getIsOrdered() {
		return isOrdered;
	}

	public void setIsOrdered(Boolean isOrdered) {
		this.isOrdered = isOrdered;
	}

	public String getLocaleFormattedAddress() {
		return localeFormattedAddress;
	}

	public void setLocaleFormattedAddress(String localeFormattedAddress) {
		this.localeFormattedAddress = localeFormattedAddress;
	}

	@Override
	public String toString() {
		return "SearchRequestFromUserResponse [id=" + id + ", userId=" + userId + ", uniqueRequestId=" + uniqueRequestId
				+ ", prescriptionRequest=" + prescriptionRequest + ", localeId=" + localeId + ", pharmacyName="
				+ pharmacyName + ", location=" + location + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", countForNo=" + countForNo + ", countForYes=" + countForYes + ", isCancelled=" + isCancelled
				+ ", localeName=" + localeName + ", localeAddress=" + localeAddress + ", localeFormattedAddress="
				+ localeFormattedAddress + ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen + ", pharmacyType="
				+ pharmacyType + ", noOfLocaleRecommendation=" + noOfLocaleRecommendation + ", isHomeDeliveryAvailable="
				+ isHomeDeliveryAvailable + ", homeDeliveryRadius=" + homeDeliveryRadius + ", paymentInfo="
				+ paymentInfo + ", paymentInfos=" + paymentInfos + ", isOrdered=" + isOrdered + "]";
	}
}
