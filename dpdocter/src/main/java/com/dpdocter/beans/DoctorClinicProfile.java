package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.RegularCheckUpTypeEnum;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorClinicProfile {
	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String clinicAddress;

	private String locationName;

	private String country;

	private String state;

	private String city;

	private String postalCode;

	private Double latitude;

	private Double longitude;

	private List<String> appointmentBookingNumber;

	private ConsultationFee consultationFee;

	private ConsultationFee revisitConsultationFee;

	private AppointmentSlot appointmentSlot;

	private List<WorkingSchedule> workingSchedules;
	

	private DoctorFacility facility;

	private List<ClinicImage> images;

	private String logoUrl;

	private String logoThumbnailUrl;

	private Integer noOfReviews = 0;

	private Integer noOfRecommenations = 0;

	private Boolean isClinic = true;

	private Boolean isLab = false;

	private Boolean isParent = false;

	private Boolean isOnlineReportsAvailable = false;

	private Boolean isNABLAccredited = false;

	private Boolean isHomeServiceAvailable = false;

	private String locality;

	private String timeZone = "IST";

	private List<TreatmentServiceCost> treatmentServiceCosts;

	private Integer noOfServices = 0;

	private List<Feedback> feedbacks;

	private Integer noOfFeedbacks = 0;

	private List<Role> roles;

	private Boolean isDoctorListed = true;

	private long rankingCount = 0;

	private Boolean isSendBirthdaySMS = true;

	private Boolean isDoctorRecommended = false;// is recommended by patient

	private Boolean isFavourite = false; // is patient's Favourite Dr

	private Boolean isAutoSMS = false;

	private Boolean isActivate = false;

	private Boolean isSendRegularCheckupSMS;

	private Integer regularCheckupMonth;

	private RegularCheckUpTypeEnum checkUpTypeEnum;

	private String packageType = PackageType.BASIC.getType();

	private String doctorSlugURL;

	private boolean showInventoryCount = false;

	private boolean showInventory = true;

	private boolean saveToInventory = false;

	private Boolean isMobileNumberOptional = false;

	private Boolean iskiosk = false;

	private Boolean hasLoginAccess = true;

	private Boolean hasBillingAccess = true;

	private String patientInitial = "P";

	private int patientCounter = 0;

	private Boolean isPidHasDate = true;

	private Boolean isNutritionist = false;

	private Boolean isAdminNutritionist = false;

	private Boolean isSuperAdmin = false;

	private String mrCode;

	private List<ObjectId> divisionIds;

	private ObjectId cityId;

	private Boolean isVaccinationModuleOn = false;

	private String feedbackURL;
	
	private List<WorkingSchedule> onlineWorkingSchedules;
	
//<<<<<<< Updated upstream
	private List<DoctorConsultation>  consultationType;

	private Boolean isOnlineConsultationAvailable = false;
	
	private String clinicOwnershipImageUrl;
	
	private BulkSmsCredits bulkSmsCredit;
//=======
//	private Map<DoctorConsultation, String> onlineConsultationFees;

//	private List<ConsultationType> onlineConsultationType;
//>>>>>>> Stashed changes

	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
	}

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	public List<ClinicImage> getImages() {
		return images;
	}

	public void setImages(List<ClinicImage> images) {
		this.images = images;
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

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Boolean getIskiosk() {
		return iskiosk;
	}

	public void setIskiosk(Boolean iskiosk) {
		this.iskiosk = iskiosk;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public DoctorFacility getFacility() {
		return facility;
	}

	public void setFacility(DoctorFacility facility) {
		this.facility = facility;
	}

	public Integer getNoOfReviews() {
		return noOfReviews;
	}

	public void setNoOfReviews(Integer noOfReviews) {
		this.noOfReviews = noOfReviews;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
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

	public Boolean getIsLab() {
		return isLab;
	}

	public void setIsLab(Boolean isLab) {
		this.isLab = isLab;
	}

	public Boolean getIsOnlineReportsAvailable() {
		return isOnlineReportsAvailable;
	}

	public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
		this.isOnlineReportsAvailable = isOnlineReportsAvailable;
	}

	public Boolean getIsNABLAccredited() {
		return isNABLAccredited;
	}

	public void setIsNABLAccredited(Boolean isNABLAccredited) {
		this.isNABLAccredited = isNABLAccredited;
	}

	public Boolean getIsHomeServiceAvailable() {
		return isHomeServiceAvailable;
	}

	public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
		this.isHomeServiceAvailable = isHomeServiceAvailable;
	}

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public List<TreatmentServiceCost> getTreatmentServiceCosts() {
		return treatmentServiceCosts;
	}

	public void setTreatmentServiceCosts(List<TreatmentServiceCost> treatmentServiceCosts) {
		this.treatmentServiceCosts = treatmentServiceCosts;
	}

	public Integer getNoOfServices() {
		return noOfServices;
	}

	public void setNoOfServices(Integer noOfServices) {
		this.noOfServices = noOfServices;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Boolean getIsDoctorListed() {
		return isDoctorListed;
	}

	public void setIsDoctorListed(Boolean isDoctorListed) {
		this.isDoctorListed = isDoctorListed;
	}

	public ConsultationFee getRevisitConsultationFee() {
		return revisitConsultationFee;
	}

	public void setRevisitConsultationFee(ConsultationFee revisitConsultationFee) {
		this.revisitConsultationFee = revisitConsultationFee;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Integer getNoOfFeedbacks() {
		return noOfFeedbacks;
	}

	public void setNoOfFeedbacks(Integer noOfFeedbacks) {
		this.noOfFeedbacks = noOfFeedbacks;
	}

	public Boolean getIsSendBirthdaySMS() {
		return isSendBirthdaySMS;
	}

	public void setIsSendBirthdaySMS(Boolean isSendBirthdaySMS) {
		this.isSendBirthdaySMS = isSendBirthdaySMS;
	}

	public Boolean getIsDoctorRecommended() {
		return isDoctorRecommended;
	}

	public void setIsDoctorRecommended(Boolean isDoctorRecommended) {
		this.isDoctorRecommended = isDoctorRecommended;
	}

	public Boolean getIsAutoSMS() {
		return isAutoSMS;
	}

	public void setIsAutoSMS(Boolean isAutoSMS) {
		this.isAutoSMS = isAutoSMS;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getIsSendRegularCheckupSMS() {
		return isSendRegularCheckupSMS;
	}

	public void setIsSendRegularCheckupSMS(Boolean isSendRegularCheckupSMS) {
		this.isSendRegularCheckupSMS = isSendRegularCheckupSMS;
	}

	public Integer getRegularCheckupMonth() {
		return regularCheckupMonth;
	}

	public void setRegularCheckupMonth(Integer regularCheckupMonth) {
		this.regularCheckupMonth = regularCheckupMonth;
	}

	public RegularCheckUpTypeEnum getCheckUpTypeEnum() {
		return checkUpTypeEnum;
	}

	public void setCheckUpTypeEnum(RegularCheckUpTypeEnum checkUpTypeEnum) {
		this.checkUpTypeEnum = checkUpTypeEnum;
	}

	public Boolean getIsFavourite() {
		return isFavourite;
	}

	public void setIsFavourite(Boolean isFavourite) {
		this.isFavourite = isFavourite;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	public Boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}

	public boolean isShowInventoryCount() {
		return showInventoryCount;
	}

	public void setShowInventoryCount(boolean showInventoryCount) {
		this.showInventoryCount = showInventoryCount;
	}

	public boolean isShowInventory() {
		return showInventory;
	}

	public void setShowInventory(boolean showInventory) {
		this.showInventory = showInventory;
	}

	public boolean isSaveToInventory() {
		return saveToInventory;
	}

	public void setSaveToInventory(boolean saveToInventory) {
		this.saveToInventory = saveToInventory;
	}

	public Boolean getIsMobileNumberOptional() {
		return isMobileNumberOptional;
	}

	public void setIsMobileNumberOptional(Boolean isMobileNumberOptional) {
		this.isMobileNumberOptional = isMobileNumberOptional;
	}

	public String getPatientInitial() {
		return patientInitial;
	}

	public void setPatientInitial(String patientInitial) {
		this.patientInitial = patientInitial;
	}

	public int getPatientCounter() {
		return patientCounter;
	}

	public void setPatientCounter(int patientCounter) {
		this.patientCounter = patientCounter;
	}

	public Boolean getIsPidHasDate() {
		return isPidHasDate;
	}

	public void setIsPidHasDate(Boolean isPidHasDate) {
		this.isPidHasDate = isPidHasDate;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	public List<ObjectId> getDivisionIds() {
		return divisionIds;
	}

	public void setDivisionIds(List<ObjectId> divisionIds) {
		this.divisionIds = divisionIds;
	}

	public ObjectId getCityId() {
		return cityId;
	}

	public void setCityId(ObjectId cityId) {
		this.cityId = cityId;
	}

	public Boolean getIsVaccinationModuleOn() {
		return isVaccinationModuleOn;
	}

	public void setIsVaccinationModuleOn(Boolean isVaccinationModuleOn) {
		this.isVaccinationModuleOn = isVaccinationModuleOn;
	}

	public Boolean getHasLoginAccess() {
		return hasLoginAccess;
	}

	public void setHasLoginAccess(Boolean hasLoginAccess) {
		this.hasLoginAccess = hasLoginAccess;
	}

	public Boolean getHasBillingAccess() {
		return hasBillingAccess;
	}

	public void setHasBillingAccess(Boolean hasBillingAccess) {
		this.hasBillingAccess = hasBillingAccess;
	}
	
	public String getFeedbackURL() {
		return feedbackURL;
	}

	public void setFeedbackURL(String feedbackURL) {
		this.feedbackURL = feedbackURL;
	}

	public Boolean getIsAdminNutritionist() {
		return isAdminNutritionist;
	}

	public void setIsAdminNutritionist(Boolean isAdminNutritionist) {
		this.isAdminNutritionist = isAdminNutritionist;
	}
	
	

	public List<WorkingSchedule> getOnlineWorkingSchedules() {
		return onlineWorkingSchedules;
	}

	public void setOnlineWorkingSchedules(List<WorkingSchedule> onlineWorkingSchedules) {
		this.onlineWorkingSchedules = onlineWorkingSchedules;
	}
//<<<<<<< Updated upstream

	public List<DoctorConsultation> getConsultationType() {
		return consultationType;
	}

	public void setConsultationType(List<DoctorConsultation> consultationType) {
		this.consultationType = consultationType;

	

//	public Map<DoctorConsultation, String> getOnlineConsultationFees() {
//		return onlineConsultationFees;
//	}
//
//	public void setOnlineConsultationFees(Map<DoctorConsultation, String> onlineConsultationFees) {
//		this.onlineConsultationFees = onlineConsultationFees;
//	}

	
//	public List<DoctorConsultation> getOnlineConsultationType() {
//		return onlineConsultationType;
//	}
//
//	public void setOnlineConsultationType(List<DoctorConsultation> onlineConsultationType) {
//		this.onlineConsultationType = onlineConsultationType;
//>>>>>>> Stashed changes
	}
	

//	public List<ConsultationType> getOnlineConsultationType() {
//		return onlineConsultationType;
//	}
//
//	public void setOnlineConsultationType(List<ConsultationType> onlineConsultationType) {
//		this.onlineConsultationType = onlineConsultationType;
//	}
	
	

	public String getClinicOwnershipImageUrl() {
		return clinicOwnershipImageUrl;
	}

	public BulkSmsCredits getBulkSmsCredit() {
		return bulkSmsCredit;
	}

	public void setBulkSmsCredit(BulkSmsCredits bulkSmsCredit) {
		this.bulkSmsCredit = bulkSmsCredit;
	}

	public void setClinicOwnershipImageUrl(String clinicOwnershipImageUrl) {
		this.clinicOwnershipImageUrl = clinicOwnershipImageUrl;
	}

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfile [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", clinicAddress=" + clinicAddress + ", locationName=" + locationName
				+ ", country=" + country + ", state=" + state + ", city=" + city + ", postalCode=" + postalCode
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", appointmentBookingNumber="
				+ appointmentBookingNumber + ", consultationFee=" + consultationFee + ", revisitConsultationFee="
				+ revisitConsultationFee + ", appointmentSlot=" + appointmentSlot + ", workingSchedules="
				+ workingSchedules + ", facility=" + facility + ", images=" + images + ", logoUrl=" + logoUrl
				+ ", logoThumbnailUrl=" + logoThumbnailUrl + ", noOfReviews=" + noOfReviews + ", noOfRecommenations="
				+ noOfRecommenations + ", isClinic=" + isClinic + ", isLab=" + isLab + ", isParent=" + isParent
				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", locality=" + locality + ", timeZone="
				+ timeZone + ", treatmentServiceCosts=" + treatmentServiceCosts + ", noOfServices=" + noOfServices
				+ ", feedbacks=" + feedbacks + ", noOfFeedbacks=" + noOfFeedbacks + ", roles=" + roles
				+ ", isDoctorListed=" + isDoctorListed + ", rankingCount=" + rankingCount + ", isSendBirthdaySMS="
				+ isSendBirthdaySMS + ", isDoctorRecommended=" + isDoctorRecommended + ", isFavourite=" + isFavourite
				+ ", isAutoSMS=" + isAutoSMS + ", isActivate=" + isActivate + ", isSendRegularCheckupSMS="
				+ isSendRegularCheckupSMS + ", regularCheckupMonth=" + regularCheckupMonth + ", checkUpTypeEnum="
				+ checkUpTypeEnum + ", packageType=" + packageType + ", doctorSlugURL=" + doctorSlugURL
				+ ", showInventoryCount=" + showInventoryCount + ", showInventory=" + showInventory
				+ ", saveToInventory=" + saveToInventory + ", isMobileNumberOptional=" + isMobileNumberOptional
				+ ", iskiosk=" + iskiosk + ", hasLoginAccess=" + hasLoginAccess + ", hasBillingAccess="
				+ hasBillingAccess + ", patientInitial=" + patientInitial + ", patientCounter=" + patientCounter
				+ ", isPidHasDate=" + isPidHasDate + ", isNutritionist=" + isNutritionist + ", isAdminNutritionist="
				+ isAdminNutritionist + ", isSuperAdmin=" + isSuperAdmin + ", mrCode=" + mrCode + ", divisionIds="
				+ divisionIds + ", cityId=" + cityId + ", isVaccinationModuleOn=" + isVaccinationModuleOn
				+ ", feedbackURL=" + feedbackURL + ", onlineWorkingSchedules=" + onlineWorkingSchedules
				 + ", isOnlineConsultationAvailable="
				+ isOnlineConsultationAvailable + "]";
	}

}
