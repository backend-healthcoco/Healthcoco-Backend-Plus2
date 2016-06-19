package com.dpdocter.elasticsearch.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.MultiField;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.solr.beans.DoctorLocation;

@Document(indexName = "doctors_in", type = "doctors")
public class ESDoctorDocument extends DoctorLocation{
    @Id
    private String id;

    @Field(type = FieldType.String)
    private String userId;

    @Field(type = FieldType.String)
    private String firstName;

    @Field(type = FieldType.String)
    private String gender;

    @Field(type = FieldType.String)
    private String emailAddress;

    @Field(type = FieldType.String)
    private String mobileNumber;

    @Field(type = FieldType.String)
    private String imageUrl;

    @Field(type = FieldType.Nested)
    private ConsultationFee consultationFee;

    @Field(type = FieldType.Nested)
    private List<WorkingSchedule> workingSchedules;

    @MultiField(mainField = @Field(type = FieldType.String))
    private List<String> specialities;

    @Field(type = FieldType.Nested)
    private DoctorExperience experience;

    @Field(type = FieldType.String)
    private String facility;

    @MultiField(mainField = @Field(type = FieldType.String))
    private List<String> appointmentBookingNumber;

    @Field(type = FieldType.Nested)
    private AppointmentSlot appointmentSlot;

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
    private String userState;

    @Field(type = FieldType.String)
    private String registerNumber;

    @Field(type = FieldType.Nested)
    private DOB dob;

    @Transient
    private Double distance;

    @Field(type = FieldType.String)
    private String userUId;

    @Field(type = FieldType.String)
    private String timeZone = "IST";

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
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

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	}

	public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
	}

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public DoctorExperience getExperience() {
		return experience;
	}

	public void setExperience(DoctorExperience experience) {
		this.experience = experience;
	}

	public String getFacility() {
		return facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
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

	public String getUserState() {
		return userState;
	}

	public void setUserState(String userState) {
		this.userState = userState;
	}

	public String getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@Override
	public String toString() {
		return "ESDoctorDocument [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", gender=" + gender
				+ ", emailAddress=" + emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl
				+ ", consultationFee=" + consultationFee + ", workingSchedules=" + workingSchedules + ", specialities="
				+ specialities + ", experience=" + experience + ", facility=" + facility + ", appointmentBookingNumber="
				+ appointmentBookingNumber + ", appointmentSlot=" + appointmentSlot + ", isActive=" + isActive
				+ ", isVerified=" + isVerified + ", coverImageUrl=" + coverImageUrl + ", coverThumbnailImageUrl="
				+ coverThumbnailImageUrl + ", colorCode=" + colorCode + ", userState=" + userState + ", registerNumber="
				+ registerNumber + ", dob=" + dob + ", distance=" + distance + ", userUId=" + userUId + ", timeZone="
				+ timeZone + "]";
	}
}
