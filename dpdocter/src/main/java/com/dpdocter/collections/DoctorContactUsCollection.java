package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.GenderType;

@Document(collection = "doctor_contact_us_cl")
public class DoctorContactUsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String firstName;
	@Indexed(unique = true)
	private String userName;
	@Field
	private GenderType gender;
	@Field
	private String emailAddress;
	@Field
	private String countryCode;
	@Field
	private String mobileNumber;
	@Field
	private List<String> specialities;
	@Field
	private DoctorContactStateType contactState;
	@Field
	private Boolean isVerified = Boolean.FALSE;
	@Field
	private Boolean toList = Boolean.FALSE;
	@Field
	private String mrCode;
	@Field
	private String mrCode;
	@Field
	private String city;
	@Field
	private String deviceType;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public GenderType getGender() {
		return gender;
	}

	public void setGender(GenderType gender) {
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

	public List<String> getSpecialities() {
		return specialities;
	}

	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}

	public DoctorContactStateType getContactState() {
		return contactState;
	}

	public void setContactState(DoctorContactStateType contactState) {
		this.contactState = contactState;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getToList() {
		return toList;
	}

	public void setToList(Boolean toList) {
		this.toList = toList;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	@Override
	public String toString() {
		return "DoctorContactUsCollection [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName="
				+ userName + ", gender=" + gender + ", emailAddress=" + emailAddress + ", countryCode=" + countryCode
				+ ", mobileNumber=" + mobileNumber + ", specialities=" + specialities + ", contactState=" + contactState
				+ ", isVerified=" + isVerified + ", toList=" + toList + ", city=" + city + ", deviceType=" + deviceType
				+ "]";
	}

}
