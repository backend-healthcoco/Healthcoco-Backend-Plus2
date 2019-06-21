package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DoctorFacility;

public class DoctorClinicProfileLookupResponse extends GenericCollection {

	private ObjectId id;

	private ObjectId doctorId;

	private ObjectId locationId;

	private Boolean isActivate = false;

	private Boolean isVerified = true;

	private Boolean discarded = false;

	private String patientInitial = "P";

	private int patientCounter = 1;

	private String doctorSlugURL;
	
	private List<String> appointmentBookingNumber;

	private ConsultationFee consultationFee;

	private ConsultationFee revisitConsultationFee;

	private AppointmentSlot appointmentSlot = new AppointmentSlot();

	private List<WorkingSchedule> workingSchedules;

	private DoctorFacility facility = DoctorFacility.CALL;

	private Integer noOfReviews = 0;

	private Integer noOfRecommenations = 0;

	private String timeZone = "IST";

	private Boolean isDoctorListed = true;

	private long rankingCount = 0;

	private Boolean isSendBirthdaySMS = true;

	private LocationCollection location;

	private HospitalCollection hospital;

	private DoctorCollection doctor;

	private UserCollection user;

	private String packageType;

	private Boolean hasLoginAccess = true;

	private Boolean hasBillingAccess = true;

	private Boolean iskiosk = false;

	private Boolean isNutritionist = false;

	private Boolean isSuperAdmin = false;

	private Boolean isVaccinationModuleOn = false;

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

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
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

	public LocationCollection getLocation() {
		return location;
	}

	public void setLocation(LocationCollection location) {
		this.location = location;
	}

	public HospitalCollection getHospital() {
		return hospital;
	}

	public void setHospital(HospitalCollection hospital) {
		this.hospital = hospital;
	}

	public DoctorCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorCollection doctor) {
		this.doctor = doctor;
	}

	public UserCollection getUser() {
		return user;
	}

	public void setUser(UserCollection user) {
		this.user = user;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
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
	
	public Boolean getIsVaccinationModuleOn() {
		return isVaccinationModuleOn;
	}

	public void setIsVaccinationModuleOn(Boolean isVaccinationModuleOn) {
		this.isVaccinationModuleOn = isVaccinationModuleOn;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfileLookupResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", isActivate=" + isActivate + ", isVerified=" + isVerified + ", discarded=" + discarded
				+ ", patientInitial=" + patientInitial + ", patientCounter=" + patientCounter + ", doctorSlugURL="
				+ doctorSlugURL + ", appointmentBookingNumber=" + appointmentBookingNumber + ", consultationFee="
				+ consultationFee + ", revisitConsultationFee=" + revisitConsultationFee + ", appointmentSlot="
				+ appointmentSlot + ", workingSchedules=" + workingSchedules + ", facility=" + facility
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", timeZone="
				+ timeZone + ", isDoctorListed=" + isDoctorListed + ", rankingCount=" + rankingCount
				+ ", isSendBirthdaySMS=" + isSendBirthdaySMS + ", location=" + location + ", hospital=" + hospital
				+ ", doctor=" + doctor + ", user=" + user + ", packageType=" + packageType + ", hasLoginAccess="
				+ hasLoginAccess + ", hasBillingAccess=" + hasBillingAccess + ", iskiosk=" + iskiosk
				+ ", isNutritionist=" + isNutritionist + ", isSuperAdmin=" + isSuperAdmin + ", isVaccinationModuleOn="
				+ isVaccinationModuleOn + "]";
	}
}