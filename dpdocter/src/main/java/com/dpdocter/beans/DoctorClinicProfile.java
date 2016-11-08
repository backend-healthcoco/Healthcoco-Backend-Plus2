package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DoctorFacility;

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
				+ noOfRecommenations + ", isClinic=" + isClinic + ", isLab=" + isLab + ", isOnlineReportsAvailable="
				+ isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited + ", isHomeServiceAvailable="
				+ isHomeServiceAvailable + ", locality=" + locality + ", timeZone=" + timeZone
				+ ", treatmentServiceCosts=" + treatmentServiceCosts + ", noOfServices=" + noOfServices + ", roles="
				+ roles + ", isDoctorListed=" + isDoctorListed + ", rankingCount=" + rankingCount + "]";
	}
}
