package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.UserState;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorProfile extends GenericCollection {
	private String id;

	private String userId;

	private String doctorId;

	private String title;

	private String firstName;

	private String emailAddress;

	private String countryCode;

	private String mobileNumber;

	private String gender;

	private String imageUrl;

	private String thumbnailUrl;

	private DOB dob;

	private String colorCode;

	private String coverImageUrl;

	private String coverThumbnailImageUrl;

	private List<String> additionalNumbers;

	private List<String> otherEmailAddresses;

	private DoctorExperience experience;

	private List<Education> education;

	private List<String> specialities;

	private List<String> services;

	private List<String> parentSpecialities;

	private List<Achievement> achievements;

	private String professionalStatement;

	private List<DoctorRegistrationDetail> registrationDetails;

	private List<DoctorExperienceDetail> experienceDetails;

	private List<String> professionalMemberships;

	private List<DoctorClinicProfile> clinicProfile;

	private Integer regularCheckUpMonths;

	private Boolean isGetDiscardedEMR = false;

	private String metaTitle;

	private String metaDesccription;

	private String metaKeyword;

	private String doctorSlugURL;

	private String packageType = PackageType.BASIC.getType();

	private boolean isPrescriptionSMS = true;

	private String drugTypePlacement = "PREFIX";

	private String freshchatRestoreId;
	
	private List<WorkingSchedule> onlineWorkingSchedules;
	
	private String RegistrationImageUrl;
	
	private String RegistrationThumbnailUrl;
	
	private String photoIdImageUrl;
	
	private UserState userState = UserState.USERSTATECOMPLETE;

	private Subscription subscriptionDetail;

	private BulkSmsCredits bulkSmsCredit;
	
	private Boolean isTransactionalSms=true;
	
	
	//private Map<DoctorConsultation, String> onlineConsultationFees;
	
//	private List<DoctorConsultation> consultationType;
	
	//private List<ConsultationType> onlineConsultationType;


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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public List<String> getAdditionalNumbers() {
		return additionalNumbers;
	}

	public void setAdditionalNumbers(List<String> additionalNumbers) {
		this.additionalNumbers = additionalNumbers;
	}

	public List<String> getOtherEmailAddresses() {
		return otherEmailAddresses;
	}

	public void setOtherEmailAddresses(List<String> otherEmailAddresses) {
		this.otherEmailAddresses = otherEmailAddresses;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public List<Education> getEducation() {
		return education;
	}

	public void setEducation(List<Education> education) {
		this.education = education;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public List<Achievement> getAchievements() {
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements) {
		this.achievements = achievements;
	}

	public String getProfessionalStatement() {
		return professionalStatement;
	}

	public void setProfessionalStatement(String professionalStatement) {
		this.professionalStatement = professionalStatement;
	}

	public List<DoctorRegistrationDetail> getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(List<DoctorRegistrationDetail> registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	public List<DoctorExperienceDetail> getExperienceDetails() {
		return experienceDetails;
	}

	public void setExperienceDetails(List<DoctorExperienceDetail> experienceDetails) {
		this.experienceDetails = experienceDetails;
	}

	public List<String> getProfessionalMemberships() {
		return professionalMemberships;
	}

	public void setProfessionalMemberships(List<String> professionalMemberships) {
		this.professionalMemberships = professionalMemberships;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public List<DoctorClinicProfile> getClinicProfile() {
		return clinicProfile;
	}

	public void setClinicProfile(List<DoctorClinicProfile> clinicProfile) {
		this.clinicProfile = clinicProfile;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	public Boolean getIsGetDiscardedEMR() {
		return isGetDiscardedEMR;
	}

	public void setIsGetDiscardedEMR(Boolean isGetDiscardedEMR) {
		this.isGetDiscardedEMR = isGetDiscardedEMR;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaDesccription() {
		return metaDesccription;
	}

	public void setMetaDesccription(String metaDesccription) {
		this.metaDesccription = metaDesccription;
	}

	public String getMetaKeyword() {
		return metaKeyword;
	}

	public void setMetaKeyword(String metaKeyword) {
		this.metaKeyword = metaKeyword;
	}

	public List<String> getParentSpecialities() {
		return parentSpecialities;
	}

	public void setParentSpecialities(List<String> parentSpecialities) {
		this.parentSpecialities = parentSpecialities;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

	public boolean isPrescriptionSMS() {
		return isPrescriptionSMS;
	}

	public void setPrescriptionSMS(boolean isPrescriptionSMS) {
		this.isPrescriptionSMS = isPrescriptionSMS;
	}

	public String getDrugTypePlacement() {
		return drugTypePlacement;
	}

	public void setDrugTypePlacement(String drugTypePlacement) {
		this.drugTypePlacement = drugTypePlacement;
	}

	public String getFreshchatRestoreId() {
		return freshchatRestoreId;
	}

	public void setFreshchatRestoreId(String freshchatRestoreId) {
		this.freshchatRestoreId = freshchatRestoreId;
	}
	
	

	public List<WorkingSchedule> getOnlineWorkingSchedules() {
		return onlineWorkingSchedules;
	}

	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
		this.onlineWorkingSchedules = onlineWorkingSchedules;
	}

	
	
	
	public String getRegistrationImageUrl() {
		return RegistrationImageUrl;
	}

	public void setRegistrationImageUrl(String registrationImageUrl) {
		RegistrationImageUrl = registrationImageUrl;
	}

	public String getRegistrationThumbnailUrl() {
		return RegistrationThumbnailUrl;
	}

	public void setRegistrationThumbnailUrl(String registrationThumbnailUrl) {
		RegistrationThumbnailUrl = registrationThumbnailUrl;
	}

	
	
	
	public String getPhotoIdImageUrl() {
		return photoIdImageUrl;
	}

	public void setPhotoIdImageUrl(String photoIdImageUrl) {
		this.photoIdImageUrl = photoIdImageUrl;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	
	
	public BulkSmsCredits getBulkSmsCredit() {
		return bulkSmsCredit;
	}

	public void setBulkSmsCredit(BulkSmsCredits bulkSmsCredit) {
		this.bulkSmsCredit = bulkSmsCredit;
	}

	public Subscription getSubscriptionDetail() {
		return subscriptionDetail;
	}

	public void setSubscriptionDetail(Subscription subscriptionDetail) {
		this.subscriptionDetail = subscriptionDetail;
	}
	
	

	public Boolean getIsTransactionalSms() {
		return isTransactionalSms;
	}

	public void setIsTransactionalSms(Boolean isTransactionalSms) {
		this.isTransactionalSms = isTransactionalSms;
	}

	@Override
	public String toString() {
		return "DoctorProfile [id=" + id + ", userId=" + userId + ", doctorId=" + doctorId + ", title=" + title
				+ ", firstName=" + firstName + ", emailAddress=" + emailAddress + ", countryCode=" + countryCode
				+ ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", dob=" + dob + ", colorCode=" + colorCode + ", coverImageUrl=" + coverImageUrl
				+ ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", additionalNumbers=" + additionalNumbers
				+ ", otherEmailAddresses=" + otherEmailAddresses + ", experience=" + experience + ", education="
				+ education + ", specialities=" + specialities + ", services=" + services + ", parentSpecialities="
				+ parentSpecialities + ", achievements=" + achievements + ", professionalStatement="
				+ professionalStatement + ", registrationDetails=" + registrationDetails + ", experienceDetails="
				+ experienceDetails + ", professionalMemberships=" + professionalMemberships + ", clinicProfile="
				+ clinicProfile + ", regularCheckUpMonths=" + regularCheckUpMonths + ", isGetDiscardedEMR="
				+ isGetDiscardedEMR + ", metaTitle=" + metaTitle + ", metaDesccription=" + metaDesccription
				+ ", metaKeyword=" + metaKeyword + ", doctorSlugURL=" + doctorSlugURL + ", packageType=" + packageType
				+ ", isPrescriptionSMS=" + isPrescriptionSMS + "]";
	}

}
