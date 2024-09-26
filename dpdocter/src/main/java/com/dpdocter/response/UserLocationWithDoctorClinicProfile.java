package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DoctorFacility;

public class UserLocationWithDoctorClinicProfile {

	private String doctorId;

	private String locationId;

	private UserCollection user;

	private DoctorCollection doctor;

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

	private Boolean isOnlineReportsAvailable = false;

	private Boolean isNABLAccredited = false;

	private Boolean isHomeServiceAvailable = false;

	private String locality;

	private String timeZone = "IST";

	private List<TreatmentServiceCost> treatmentServiceCosts;

	private Integer noOfServices = 0;

	private List<Role> roles;

	private Boolean isDoctorListed = true;

	private long rankingCount = 0;

	private Boolean isSendBirthdaySMS = true;

	private Boolean isDoctorRecommended = false;

	private Boolean isVaccinationModuleOn = false;

	private Boolean isPidHasDate = true;

	private Boolean isShowPatientNumber = false;

	private Boolean isShowDoctorInCalender = true;

	private String clinicHipId;

	private Boolean isRegisteredNDHMFacility;

	public String getClinicHipId() {
		return clinicHipId;
	}

	public void setClinicHipId(String clinicHipId) {
		this.clinicHipId = clinicHipId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public UserCollection getUser() {
		return user;
	}

	public void setUser(UserCollection user) {
		this.user = user;
	}

	public DoctorCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorCollection doctor) {
		this.doctor = doctor;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getClinicAddress() {
		return clinicAddress;
	}

	public void setClinicAddress(String clinicAddress) {
		this.clinicAddress = clinicAddress;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public ConsultationFee getRevisitConsultationFee() {
		return revisitConsultationFee;
	}

	public void setRevisitConsultationFee(ConsultationFee revisitConsultationFee) {
		this.revisitConsultationFee = revisitConsultationFee;
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

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
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

	public Boolean getIsDoctorRecommended() {
		return isDoctorRecommended;
	}

	public void setIsDoctorRecommended(Boolean isDoctorRecommended) {
		this.isDoctorRecommended = isDoctorRecommended;
	}

	public Boolean getIsVaccinationModuleOn() {
		return isVaccinationModuleOn;
	}

	public void setIsVaccinationModuleOn(Boolean isVaccinationModuleOn) {
		this.isVaccinationModuleOn = isVaccinationModuleOn;
	}

	public Boolean getIsPidHasDate() {
		return isPidHasDate;
	}

	public void setIsPidHasDate(Boolean isPidHasDate) {
		this.isPidHasDate = isPidHasDate;
	}

	public Boolean getIsShowPatientNumber() {
		return isShowPatientNumber;
	}

	public void setIsShowPatientNumber(Boolean isShowPatientNumber) {
		this.isShowPatientNumber = isShowPatientNumber;
	}

	public Boolean getIsShowDoctorInCalender() {
		return isShowDoctorInCalender;
	}

	public void setIsShowDoctorInCalender(Boolean isShowDoctorInCalender) {
		this.isShowDoctorInCalender = isShowDoctorInCalender;
	}
	

	public Boolean getIsRegisteredNDHMFacility() {
		return isRegisteredNDHMFacility;
	}

	public void setIsRegisteredNDHMFacility(Boolean isRegisteredNDHMFacility) {
		this.isRegisteredNDHMFacility = isRegisteredNDHMFacility;
	}

	@Override
	public String toString() {
		return "UserLocationWithDoctorClinicProfile [doctorId=" + doctorId + ", locationId=" + locationId + ", user="
				+ user + ", doctor=" + doctor + ", hospitalId=" + hospitalId + ", clinicAddress=" + clinicAddress
				+ ", locationName=" + locationName + ", country=" + country + ", state=" + state + ", city=" + city
				+ ", postalCode=" + postalCode + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", appointmentBookingNumber=" + appointmentBookingNumber + ", consultationFee=" + consultationFee
				+ ", revisitConsultationFee=" + revisitConsultationFee + ", appointmentSlot=" + appointmentSlot
				+ ", workingSchedules=" + workingSchedules + ", facility=" + facility + ", images=" + images
				+ ", logoUrl=" + logoUrl + ", logoThumbnailUrl=" + logoThumbnailUrl + ", noOfReviews=" + noOfReviews
				+ ", noOfRecommenations=" + noOfRecommenations + ", isClinic=" + isClinic + ", isLab=" + isLab
				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", locality=" + locality + ", timeZone="
				+ timeZone + ", treatmentServiceCosts=" + treatmentServiceCosts + ", noOfServices=" + noOfServices
				+ ", roles=" + roles + ", isDoctorListed=" + isDoctorListed + ", rankingCount=" + rankingCount
				+ ", isSendBirthdaySMS=" + isSendBirthdaySMS + ", isDoctorRecommended=" + isDoctorRecommended + "]";
	}
}
