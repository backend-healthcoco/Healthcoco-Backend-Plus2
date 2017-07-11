package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.LocaleType;
import com.dpdocter.enums.UserState;

public class Locale extends GenericCollection {

	private String id;
	private String localeName;
	private String registeredOwnerName;
	private String licenseNumber;
	private List<LocaleImage> localeImages;
	private String logoUrl;
	private String logoThumbnailUrl;
	private String contactNumber;
	private String callingNumber;
	private List<String> alternateContactNumbers;
	private List<LocaleWorkingSchedule> localeWorkingSchedules;
	private Address address;
	private String localeAddress;
	private String websiteUrl;
	private String localeEmailAddress;
	private Boolean isTwentyFourSevenOpen = false;
	private List<String> pharmacyType;
	private Boolean isGenericMedicineAvailable = false;
	private String localeUId;
	private int openSince;
	private UserState userState;
	private Boolean isActivate = false;
	private Boolean isLocaleListed = true;
	private long localeRankingCount = 0;
	private long noOfLocaleRecommendation = 0;
	private Boolean isHomeDeliveryAvailable = false;
	private int homeDeliveryRadius;
	private String paymentInfo;
	private String localeType = LocaleType.PHARMACY.getType();
	private Boolean isPasswordVerified = false;
	private Boolean isLocaleRecommended;
	private Boolean isAcceptRequest = true;

	public List<String> getPharmacyType() {
		return pharmacyType;
	}

	public Boolean getIsGenericMedicineAvailable() {
		return isGenericMedicineAvailable;
	}

	public void setPharmacyType(List<String> pharmacyType) {
		this.pharmacyType = pharmacyType;
	}

	public void setIsGenericMedicineAvailable(Boolean isGenericMedicineAvailable) {
		this.isGenericMedicineAvailable = isGenericMedicineAvailable;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public String getRegisteredOwnerName() {
		return registeredOwnerName;
	}

	public void setRegisteredOwnerName(String registeredOwnerName) {
		this.registeredOwnerName = registeredOwnerName;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public List<LocaleImage> getLocaleImages() {
		return localeImages;
	}

	public void setLocaleImages(List<LocaleImage> localeImages) {
		this.localeImages = localeImages;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getLogoThumbnailUrl() {
		return logoThumbnailUrl;
	}

	public void setLogoThumbnailUrl(String logoThumbnailUrl) {
		this.logoThumbnailUrl = logoThumbnailUrl;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public List<String> getAlternateContactNumbers() {
		return alternateContactNumbers;
	}

	public void setAlternateContactNumbers(List<String> alternateContactNumbers) {
		this.alternateContactNumbers = alternateContactNumbers;
	}

	public List<LocaleWorkingSchedule> getLocaleWorkingSchedules() {
		return localeWorkingSchedules;
	}

	public void setLocaleWorkingSchedules(List<LocaleWorkingSchedule> localeWorkingSchedules) {
		this.localeWorkingSchedules = localeWorkingSchedules;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getLocaleAddress() {
		return localeAddress;
	}

	public void setLocaleAddress(String localeAddress) {
		this.localeAddress = localeAddress;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getLocaleEmailAddress() {
		return localeEmailAddress;
	}

	public void setLocaleEmailAddress(String localeEmailAddress) {
		this.localeEmailAddress = localeEmailAddress;
	}

	public Boolean getIsTwentyFourSevenOpen() {
		return isTwentyFourSevenOpen;
	}

	public void setIsTwentyFourSevenOpen(Boolean isTwentyFourSevenOpen) {
		this.isTwentyFourSevenOpen = isTwentyFourSevenOpen;
	}

	public String getLocaleUId() {
		return localeUId;
	}

	public void setLocaleUId(String localeUId) {
		this.localeUId = localeUId;
	}

	public int getOpenSince() {
		return openSince;
	}

	public void setOpenSince(int openSince) {
		this.openSince = openSince;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getIsLocaleListed() {
		return isLocaleListed;
	}

	public void setIsLocaleListed(Boolean isLocaleListed) {
		this.isLocaleListed = isLocaleListed;
	}

	public long getLocaleRankingCount() {
		return localeRankingCount;
	}

	public void setLocaleRankingCount(long localeRankingCount) {
		this.localeRankingCount = localeRankingCount;
	}

	public long getNoOfLocaleRecommendation() {
		return noOfLocaleRecommendation;
	}

	public void setNoOfLocaleRecommendation(long noOfLocaleRecommendation) {
		this.noOfLocaleRecommendation = noOfLocaleRecommendation;
	}

	public Boolean getIsHomeDeliveryAvailable() {
		return isHomeDeliveryAvailable;
	}

	public void setIsHomeDeliveryAvailable(Boolean isHomeDeliveryAvailable) {
		this.isHomeDeliveryAvailable = isHomeDeliveryAvailable;
	}

	public int getHomeDeliveryRadius() {
		return homeDeliveryRadius;
	}

	public void setHomeDeliveryRadius(int homeDeliveryRadius) {
		this.homeDeliveryRadius = homeDeliveryRadius;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public String getLocaleType() {
		return localeType;
	}

	public void setLocaleType(String localeType) {
		this.localeType = localeType;
	}

	public Boolean getIsPasswordVerified() {
		return isPasswordVerified;
	}

	public void setIsPasswordVerified(Boolean isPasswordVerified) {
		this.isPasswordVerified = isPasswordVerified;
	}

	public Boolean getIsLocaleRecommended() {
		return isLocaleRecommended;
	}

	public void setIsLocaleRecommended(Boolean isLocaleRecommended) {
		this.isLocaleRecommended = isLocaleRecommended;
	}

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	@Override
	public String toString() {
		return "Locale [id=" + id + ", localeName=" + localeName + ", registeredOwnerName=" + registeredOwnerName
				+ ", licenseNumber=" + licenseNumber + ", localeImages=" + localeImages + ", logoUrl=" + logoUrl
				+ ", logoThumbnailUrl=" + logoThumbnailUrl + ", contactNumber=" + contactNumber + ", callingNumber="
				+ callingNumber + ", alternateContactNumbers=" + alternateContactNumbers + ", localeWorkingSchedules="
				+ localeWorkingSchedules + ", address=" + address + ", localeAddress=" + localeAddress + ", websiteUrl="
				+ websiteUrl + ", localeEmailAddress=" + localeEmailAddress + ", isTwentyFourSevenOpen="
				+ isTwentyFourSevenOpen + ", localeUId=" + localeUId + ", openSince=" + openSince + ", userState="
				+ userState + ", isActivate=" + isActivate + ", isLocaleListed=" + isLocaleListed
				+ ", localeRankingCount=" + localeRankingCount + ", noOfLocaleRecommendation="
				+ noOfLocaleRecommendation + ", isHomeDeliveryAvailable=" + isHomeDeliveryAvailable
				+ ", homeDeliveryRadius=" + homeDeliveryRadius + ", paymentInfo=" + paymentInfo + ", localeType="
				+ localeType + ", isPasswordVerified=" + isPasswordVerified + ", isLocaleRecommended="
				+ isLocaleRecommended + "]";
	}

	public Boolean getIsAcceptRequest() {
		return isAcceptRequest;
	}

	public void setIsAcceptRequest(Boolean isAcceptRequest) {
		this.isAcceptRequest = isAcceptRequest;
	}

}