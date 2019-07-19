package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.beans.LocaleWorkingSchedule;
import com.dpdocter.enums.LocaleType;

@Document(collection = "locale_cl")
public class LocaleCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String localeName;
	@Field
	private String registeredOwnerName;
	@Field
	private String licenseNumber;
	@Field
	private List<LocaleImage> localeImages;
	@Field
	private String logoUrl;
	@Field
	private String logoThumbnailUrl;
	@Indexed(unique = true)
	private String contactNumber;
	@Field
	private String callingNumber;
	@Field
	private List<String> alternateContactNumbers;
	@Field
	private List<LocaleWorkingSchedule> localeWorkingSchedules;
	@Field
	private Address address;
	@Field
	private String localeAddress;
	@Field
	private String websiteUrl;
	@Field
	private String localeEmailAddress;
	@Field
	private Boolean isTwentyFourSevenOpen = false;
	@Field
	private List<String> pharmacyType;
	@Field
	private Boolean isGenericMedicineAvailable = false;
	@Field
	private String localeUId;
	@Field
	private int openSince;
	@Field
	private Boolean isActivate = false;
	@Field
	private Boolean isLocaleListed = true;
	@Field
	private long localeRankingCount = 1000;
	@Field
	private long noOfLocaleRecommendation = 0;
	@Field
	private Boolean isHomeDeliveryAvailable = false;
	@Field
	private double homeDeliveryRadius;
	@Field
	private String paymentInfo;
	@Field
	private List<String> paymentInfos;

	@Field
	private String localeType = LocaleType.PHARMACY.getType();
	@Field
	private Boolean isPasswordVerified = false;

	@Field
	private Boolean isVerified = false;

	@Field
	private Boolean isAcceptRequest = true;

	@Field
	private Double minimumAmountForDelivery = 0.0;

	@Field
	private String pharmacySlugUrl;

	public String getPharmacySlugUrl() {
		return pharmacySlugUrl;
	}

	public void setPharmacySlugUrl(String pharmacySlugUrl) {
		this.pharmacySlugUrl = pharmacySlugUrl;
	}

	public List<String> getPaymentInfos() {
		return paymentInfos;
	}

	public void setPaymentInfos(List<String> paymentInfos) {
		this.paymentInfos = paymentInfos;
	}

	public Double getMinimumAmountForDelivery() {
		return minimumAmountForDelivery;
	}

	public void setMinimumAmountForDelivery(Double minimumAmountForDelivery) {
		this.minimumAmountForDelivery = minimumAmountForDelivery;
	}

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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public double getHomeDeliveryRadius() {
		return homeDeliveryRadius;
	}

	public void setHomeDeliveryRadius(double homeDeliveryRadius) {
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

	public String getCallingNumber() {
		return callingNumber;
	}

	public void setCallingNumber(String callingNumber) {
		this.callingNumber = callingNumber;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getIsAcceptRequest() {
		return isAcceptRequest;
	}

	public void setIsAcceptRequest(Boolean isAcceptRequest) {
		this.isAcceptRequest = isAcceptRequest;
	}

	@Override
	public String toString() {
		return "LocaleCollection [id=" + id + ", localeName=" + localeName + ", registeredOwnerName="
				+ registeredOwnerName + ", licenseNumber=" + licenseNumber + ", localeImages=" + localeImages
				+ ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl + ", contactNumber=" + contactNumber
				+ ", callingNumber=" + callingNumber + ", alternateContactNumbers=" + alternateContactNumbers
				+ ", localeWorkingSchedules=" + localeWorkingSchedules + ", address=" + address + ", localeAddress="
				+ localeAddress + ", websiteUrl=" + websiteUrl + ", localeEmailAddress=" + localeEmailAddress
				+ ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen + ", localeUId=" + localeUId + ", openSince="
				+ openSince + ", isActivate=" + isActivate + ", isLocaleListed=" + isLocaleListed
				+ ", localeRankingCount=" + localeRankingCount + ", noOfLocaleRecommendation="
				+ noOfLocaleRecommendation + ", isHomeDeliveryAvailable=" + isHomeDeliveryAvailable
				+ ", homeDeliveryRadius=" + homeDeliveryRadius + ", paymentInfo=" + paymentInfo + ", localeType="
				+ localeType + ", isPasswordVerified=" + isPasswordVerified + "]";
	}

}