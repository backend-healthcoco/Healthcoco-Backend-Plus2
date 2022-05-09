package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorConsultation;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.RegularCheckUpTypeEnum;

@Document(collection = "doctor_clinic_profile_cl")
public class DoctorClinicProfileCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Indexed
	private ObjectId userLocationId;

	@Indexed
	private ObjectId doctorId;

	@Indexed
	private ObjectId locationId;

	@Field
	private Boolean isActivate = false;

	@Field
	private Boolean isVerified = true;

	@Field
	private Boolean discarded = false;

	@Field
	private List<String> appointmentBookingNumber;

	@Field
	private ConsultationFee consultationFee;

	@Field
	private ConsultationFee revisitConsultationFee;

	@Field
	private AppointmentSlot appointmentSlot = new AppointmentSlot();

	@Field
	private List<WorkingSchedule> workingSchedules;
	
	@Field
	private List<WorkingSchedule> onlineWorkingSchedules;

	@Field
	private DoctorFacility facility = DoctorFacility.CALL;

	@Field
	private Integer noOfReviews = 0;

	@Field
	private Integer noOfRecommenations = 0;

	@Field
	private String timeZone = "IST";

	@Field
	private Boolean isDoctorListed = false;

	@Field
	private long rankingCount = 1000;

	@Field
	private Boolean isSendBirthdaySMS = true;

	@Field
	private Boolean isAutoSMS = false;

	@Field
	private Boolean isSendRegularCheckupSMS;

	@Field
	private Integer regularCheckUpMonths;

	@Field
	private RegularCheckUpTypeEnum checkUpTypeEnum;

	@Field
	private String packageType = PackageType.ADVANCE.getType();

	@Field
	private String doctorSlugURL;

	@Field
	private boolean showInventoryCount = false;

	@Field
	private boolean showInventory = true;

	@Field
	private boolean saveToInventory = false;

	@Field
	private boolean iskiosk = false;

	@Field
	private String labType = LabType.DIAGNOSTIC.getType();

	@Field
	private Boolean hasLoginAccess = true;

	@Field
	private Boolean hasBillingAccess = true;

	@Field
	private Boolean isNutritionist = false;

	@Field
	private Boolean isSuperAdmin = false;

	@Field
	private Boolean isAdminNutritionist = false;

	@Field
	private String mrCode;

	@Field
	private List<ObjectId> divisionIds;

	@Field
	private ObjectId cityId;

	@Field
	private Boolean isVaccinationModuleOn = false;

	// This Id Use For set default doctor in Get User API At doctor level
	@Field
	private ObjectId defaultDoctorId;

	@Field
	private Boolean isPidHasDate = true;

	@Field
	private String feedbackURL;

	@Field
	private List<String> departments;
	
	@Field
	private List<DoctorConsultation> consultationType;

//	@Field
//	private List<ConsultationType> onlineConsultationType;
	@Field
	private String clinicOwnershipImageUrl;

	@Field
	private Boolean isOnlineConsultationAvailable = false;
	
	@Field
	private AppointmentSlot onlineConsultationSlot;
	
	@Field
	private Boolean isPatientWelcomeMessageOn = false;
	
	@Field
	private Boolean isShowPatientNumber = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public Boolean getIsActivate() {
		return isActivate;
	}

	public void setIsActivate(Boolean isActivate) {
		this.isActivate = isActivate;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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

	public Boolean getIsSendBirthdaySMS() {
		return isSendBirthdaySMS;
	}

	public void setIsSendBirthdaySMS(Boolean isSendBirthdaySMS) {
		this.isSendBirthdaySMS = isSendBirthdaySMS;
	}

	public ObjectId getUserLocationId() {
		return userLocationId;
	}

	public void setUserLocationId(ObjectId userLocationId) {
		this.userLocationId = userLocationId;
	}

	public Boolean getIsAutoSMS() {
		return isAutoSMS;
	}

	public void setIsAutoSMS(Boolean isAutoSMS) {
		this.isAutoSMS = isAutoSMS;
	}

	public Boolean getIsSendRegularCheckupSMS() {
		return isSendRegularCheckupSMS;
	}

	public void setIsSendRegularCheckupSMS(Boolean isSendRegularCheckupSMS) {
		this.isSendRegularCheckupSMS = isSendRegularCheckupSMS;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	public RegularCheckUpTypeEnum getCheckUpTypeEnum() {
		return checkUpTypeEnum;
	}

	public void setCheckUpTypeEnum(RegularCheckUpTypeEnum checkUpTypeEnum) {
		this.checkUpTypeEnum = checkUpTypeEnum;
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

	public boolean getIskiosk() {
		return iskiosk;
	}

	public void setIskiosk(boolean iskiosk) {
		this.iskiosk = iskiosk;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
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

	public String getLabType() {
		return labType;
	}

	public void setLabType(String labType) {
		this.labType = labType;
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

	public Boolean getIsNutritionist() {
		return isNutritionist;
	}

	public void setIsNutritionist(Boolean isNutritionist) {
		this.isNutritionist = isNutritionist;
	}

	public Boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(Boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public ObjectId getDefaultDoctorId() {
		return defaultDoctorId;
	}

	public void setDefaultDoctorId(ObjectId defaultDoctorId) {
		this.defaultDoctorId = defaultDoctorId;
	}

	public Boolean getIsPidHasDate() {
		return isPidHasDate;
	}

	public void setIsPidHasDate(Boolean isPidHasDate) {
		this.isPidHasDate = isPidHasDate;
	}

	public String getFeedbackURL() {
		return feedbackURL;
	}

	public void setFeedbackURL(String feedbackURL) {
		this.feedbackURL = feedbackURL;
	}

	public Boolean getIsVaccinationModuleOn() {
		return isVaccinationModuleOn;
	}

	public void setIsVaccinationModuleOn(Boolean isVaccinationModuleOn) {
		this.isVaccinationModuleOn = isVaccinationModuleOn;
	}

	public Boolean getIsAdminNutritionist() {
		return isAdminNutritionist;
	}

	public void setIsAdminNutritionist(Boolean isAdminNutritionist) {
		this.isAdminNutritionist = isAdminNutritionist;
	}

	public List<String> getDepartments() {
		return departments;
	}

	public void setDepartments(List<String> departments) {
		this.departments = departments;
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
	}
	

	public Boolean getIsOnlineConsultationAvailable() {
		return isOnlineConsultationAvailable;
	}

	public void setIsOnlineConsultationAvailable(Boolean isOnlineConsultationAvailable) {
		this.isOnlineConsultationAvailable = isOnlineConsultationAvailable;
	}
	
	

	public AppointmentSlot getOnlineConsultationSlot() {
		return onlineConsultationSlot;
	}

	public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
		this.onlineConsultationSlot = onlineConsultationSlot;
	}

	public String getClinicOwnershipImageUrl() {
		return clinicOwnershipImageUrl;
	}

	public void setClinicOwnershipImageUrl(String clinicOwnershipImageUrl) {
		this.clinicOwnershipImageUrl = clinicOwnershipImageUrl;
	}

	public Boolean getIsPatientWelcomeMessageOn() {
		return isPatientWelcomeMessageOn;
	}

	public void setIsPatientWelcomeMessageOn(Boolean isPatientWelcomeMessageOn) {
		this.isPatientWelcomeMessageOn = isPatientWelcomeMessageOn;
	}

	public Boolean getIsShowPatientNumber() {
		return isShowPatientNumber;
	}

	public void setIsShowPatientNumber(Boolean isShowPatientNumber) {
		this.isShowPatientNumber = isShowPatientNumber;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfileCollection [id=" + id + ", userLocationId=" + userLocationId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", isActivate=" + isActivate + ", isVerified=" + isVerified
				+ ", discarded=" + discarded + ", appointmentBookingNumber=" + appointmentBookingNumber
				+ ", consultationFee=" + consultationFee + ", revisitConsultationFee=" + revisitConsultationFee
				+ ", appointmentSlot=" + appointmentSlot + ", workingSchedules=" + workingSchedules
				+ ", onlineWorkingSchedules=" + onlineWorkingSchedules + ", facility=" + facility + ", noOfReviews="
				+ noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", timeZone=" + timeZone
				+ ", isDoctorListed=" + isDoctorListed + ", rankingCount=" + rankingCount + ", isSendBirthdaySMS="
				+ isSendBirthdaySMS + ", isAutoSMS=" + isAutoSMS + ", isSendRegularCheckupSMS="
				+ isSendRegularCheckupSMS + ", regularCheckUpMonths=" + regularCheckUpMonths + ", checkUpTypeEnum="
				+ checkUpTypeEnum + ", packageType=" + packageType + ", doctorSlugURL=" + doctorSlugURL
				+ ", showInventoryCount=" + showInventoryCount + ", showInventory=" + showInventory
				+ ", saveToInventory=" + saveToInventory + ", iskiosk=" + iskiosk + ", labType=" + labType
				+ ", hasLoginAccess=" + hasLoginAccess + ", hasBillingAccess=" + hasBillingAccess + ", isNutritionist="
				+ isNutritionist + ", isSuperAdmin=" + isSuperAdmin + ", isAdminNutritionist=" + isAdminNutritionist
				+ ", mrCode=" + mrCode + ", divisionIds=" + divisionIds + ", cityId=" + cityId
				+ ", isVaccinationModuleOn=" + isVaccinationModuleOn + ", defaultDoctorId=" + defaultDoctorId
				+ ", isPidHasDate=" + isPidHasDate + ", feedbackURL=" + feedbackURL + ", departments=" + departments
				 + ", isOnlineConsultationAvailable="
				+ isOnlineConsultationAvailable + "]";
	}
}