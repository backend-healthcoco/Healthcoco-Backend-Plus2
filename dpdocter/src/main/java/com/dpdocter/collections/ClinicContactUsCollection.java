package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.Type;

@Document(collection = "clinic_contact_us_cl")
public class ClinicContactUsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String locationName;
	@Field
	private String emailAddress;
	@Field
	private Type type = Type.CLINIC;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId hospitalId;
	@Field
	private String country;
	@Field
	private String state;
	@Field
	private String city;
	@Field
	private Date contactLaterOnDate;
	@Field
	private String clinicNumber;
	@Field
	private String streetAddress;
	@Field
	private DoctorContactStateType contactState;
	@Field
	private String mrCode;
	@Field
    private String googleMapShortUrl;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public DoctorContactStateType getContactState() {
		return contactState;
	}

	public void setContactState(DoctorContactStateType contactState) {
		this.contactState = contactState;
	}

	public Date getContactLaterOnDate() {
		return contactLaterOnDate;
	}

	public void setContactLaterOnDate(Date contactLaterOnDate) {
		this.contactLaterOnDate = contactLaterOnDate;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}
	
	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "ClinicContactUsCollection [id=" + id + ", locationName=" + locationName + ", emailAddress="
				+ emailAddress + ", type=" + type + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId
				+ ", country=" + country + ", state=" + state + ", city=" + city + ", contactLaterOnDate="
				+ contactLaterOnDate + ", clinicNumber=" + clinicNumber + ", streetAddress=" + streetAddress
				+ ", contactState=" + contactState + ", googleMapShortUrl=" + googleMapShortUrl + "]";
	}

}
