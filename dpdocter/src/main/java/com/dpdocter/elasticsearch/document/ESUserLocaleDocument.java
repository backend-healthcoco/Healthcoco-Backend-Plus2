package com.dpdocter.elasticsearch.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.beans.LocaleWorkingSchedule;
import com.dpdocter.enums.LocaleType;

@Document(indexName = "userlocale_in", type = "userlocale")
public class ESUserLocaleDocument {

	@Id
	private String id;// This is localeId

	@Field(type = FieldType.String)
	private String title;

	@Field(type = FieldType.String)
	private String firstName;

	@Field(type = FieldType.String)
	private String userName;

	@Field(type = FieldType.String)
	private String emailAddress;

	@Field(type = FieldType.String)
	private String mobileNumber;

	@Field(type = FieldType.String)
	private String imageUrl;

	@Field(type = FieldType.String)
	private String thumbnailUrl;

	@Field(type = FieldType.Boolean)
	private Boolean isActive = false;

	@Field(type = FieldType.Boolean)
	private Boolean isVerified = false;

	@Field(type = FieldType.String)
	private String coverImageUrl;

	@Field(type = FieldType.String)
	private String coverThumbnailImageUrl;

	@Field(type = FieldType.String)
	private String colorCode;

	@Field(type = FieldType.String)
	private String userUId;

	@Field(type = FieldType.String)
	private String userId;

	@Field(type = FieldType.String)
	private String localeId;

	@Field(type = FieldType.String)
	private String localeName;

	@Field(type = FieldType.String)
	private String registeredOwnerName;

	@Field(type = FieldType.String)
	private String licenseNumber;

	@MultiField(mainField = @Field(type = FieldType.String))
	private List<LocaleImage> localeImages;

	@Field(type = FieldType.String)
	private String logoUrl;

	@Field(type = FieldType.String)
	private String logoThumbnailUrl;

	@Field(type = FieldType.String)
	private String contactNumber;

	@MultiField(mainField = @Field(type = FieldType.String))
	private List<String> alternateContactNumbers;

	@Field(type = FieldType.Nested)
	private List<LocaleWorkingSchedule> localeWorkingSchedules;

	@Field(type = FieldType.Nested)
	private Address address;

	@GeoPointField
	private GeoPoint geoPoint;

	@Field(type = FieldType.String)
	private String localeAddress;

	@Field(type = FieldType.String)
	private String websiteUrl;

	@Field(type = FieldType.String)
	private String localeEmailAddress;

	@Field(type = FieldType.Boolean)
	private Boolean isTwentyFourSevenOpen;

	@Field(type = FieldType.String)
	private String localeUId;

	@Field(type = FieldType.Integer)
	private int openSince;

	@Field(type = FieldType.Boolean)
	private Boolean isActivate = false;

	@Field(type = FieldType.Boolean)
	private Boolean isLocaleListed = true;

	@Field(type = FieldType.Long)
	private long localeRankingCount = 0;

	@Field(type = FieldType.Long)
	private long noOfLocaleRecommendation = 0;

	@Field(type = FieldType.Boolean)
	private Boolean isHomeDeliveryAvailable = false;

	@Field(type = FieldType.Double)
	private double homeDeliveryRadius;

	@Field(type = FieldType.String)
	private String paymentInfo;

	@Field(type = FieldType.String)
	private List<String> paymentInfos;

	@Field(type = FieldType.String)
	private String localeType = LocaleType.PHARMACY.getType();

	@Field(type = FieldType.Boolean)
	private Boolean isPasswordVerified = false;

	@Transient
	private Double distance;

	@Field(type = FieldType.Boolean)
	private Boolean isAcceptRequest = true;

	@Field(type = FieldType.String)
	private List<String> pharmacyType;

	@Field(type = FieldType.Boolean)
	private Boolean isGenericMedicineAvailable = false;

	@Field(type = FieldType.Double)
	private Double minimumAmountForDelivery = 0.0;

	@Field(type = FieldType.String)
	private String pharmacySlugUrl;

	public String getPharmacySlugUrl() {
		return pharmacySlugUrl;
	}

	public void setPharmacySlugUrl(String pharmacySlugUrl) {
		this.pharmacySlugUrl = pharmacySlugUrl;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setOpenSince(Integer openSince) {
		this.openSince = openSince;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
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

	public GeoPoint getGeoPoint() {
		return geoPoint;
	}

	public void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
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

	public List<LocaleWorkingSchedule> getLocaleWorkingSchedules() {
		return localeWorkingSchedules;
	}

	public void setLocaleWorkingSchedules(List<LocaleWorkingSchedule> localeWorkingSchedules) {
		this.localeWorkingSchedules = localeWorkingSchedules;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIsAcceptRequest() {
		return isAcceptRequest;
	}

	public void setIsAcceptRequest(Boolean isAcceptRequest) {
		this.isAcceptRequest = isAcceptRequest;
	}

	public List<String> getPaymentInfos() {
		return paymentInfos;
	}

	public void setPaymentInfos(List<String> paymentInfos) {
		this.paymentInfos = paymentInfos;
	}

	@Override
	public String toString() {
		return "ESUserLocaleDocument [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName="
				+ userName + ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl="
				+ imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", isActive=" + isActive + ", isVerified=" + isVerified
				+ ", coverImageUrl=" + coverImageUrl + ", coverThumbnailImageUrl=" + coverThumbnailImageUrl
				+ ", colorCode=" + colorCode + ", userUId=" + userUId + ", userId=" + userId + ", localeId=" + localeId
				+ ", localeName=" + localeName + ", registeredOwnerName=" + registeredOwnerName + ", licenseNumber="
				+ licenseNumber + ", localeImages=" + localeImages + ", logoUrl=" + logoUrl + ", logoThumbnailUrl="
				+ logoThumbnailUrl + ", contactNumber=" + contactNumber + ", alternateContactNumbers="
				+ alternateContactNumbers + ", localeWorkingSchedules=" + localeWorkingSchedules + ", address="
				+ address + ", geoPoint=" + geoPoint + ", localeAddress=" + localeAddress + ", websiteUrl=" + websiteUrl
				+ ", localeEmailAddress=" + localeEmailAddress + ", isTwentyFourSevenOpen=" + isTwentyFourSevenOpen
				+ ", localeUId=" + localeUId + ", openSince=" + openSince + ", isActivate=" + isActivate
				+ ", isLocaleListed=" + isLocaleListed + ", localeRankingCount=" + localeRankingCount
				+ ", noOfLocaleRecommendation=" + noOfLocaleRecommendation + ", isHomeDeliveryAvailable="
				+ isHomeDeliveryAvailable + ", homeDeliveryRadius=" + homeDeliveryRadius + ", paymentInfo="
				+ paymentInfo + ", localeType=" + localeType + ", isPasswordVerified=" + isPasswordVerified
				+ ", distance=" + distance + ", isAcceptRequest=" + isAcceptRequest + ", pharmacyType=" + pharmacyType
				+ ", isGenericMedicineAvailable=" + isGenericMedicineAvailable + ", minimumAmountForDelivery="
				+ minimumAmountForDelivery + "]";
	}
}
