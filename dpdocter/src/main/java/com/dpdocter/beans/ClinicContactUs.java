package com.dpdocter.beans;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.Type;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicContactUs extends GenericCollection {

	private String id;

	private String locationName;

	private String emailAddress;

	private Type type = Type.CLINIC;

	private String doctorId;

	private String hospitalId;

	private String country;

	private String state;

	private String city;

	private String clinicNumber;

	private String streetAddress;

	private DoctorContactStateType contactState = DoctorContactStateType.APPROACH;

	private Date contactLaterOnDate;

    private String googleMapShortUrl;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getGoogleMapShortUrl() {
		return googleMapShortUrl;
	}

	public void setGoogleMapShortUrl(String googleMapShortUrl) {
		this.googleMapShortUrl = googleMapShortUrl;
	}

	@Override
	public String toString() {
		return "ClinicContactUs [id=" + id + ", locationName=" + locationName + ", emailAddress=" + emailAddress
				+ ", type=" + type + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", clinicNumber=" + clinicNumber + ", streetAddress="
				+ streetAddress + ", contactState=" + contactState + ", contactLaterOnDate=" + contactLaterOnDate
				+ ", googleMapShortUrl=" + googleMapShortUrl + "]";
	}

}
