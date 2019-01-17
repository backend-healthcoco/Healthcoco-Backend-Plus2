package com.dpdocter.response;

import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.enums.DoctorFacility;

public class WebClinicResponse {

	private String locationId;

	private String hospitalId;

	private String country;

	private String state;

	private String city;

	private String postalCode;
	
	private String locality;

	private String locationName;
	
	private String streetAddress;

	private ConsultationFee consultationFee;
	
	private DoctorFacility facility;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public ConsultationFee getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(ConsultationFee consultationFee) {
		this.consultationFee = consultationFee;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public DoctorFacility getFacility() {
		return facility;
	}

	public void setFacility(DoctorFacility facility) {
		this.facility = facility;
	}

	@Override
	public String toString() {
		return "WebClinicResponse [locationId=" + locationId + ", hospitalId=" + hospitalId + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", postalCode=" + postalCode + ", locality=" + locality
				+ ", locationName=" + locationName + ", streetAddress=" + streetAddress + ", consultationFee="
				+ consultationFee + ", facility=" + facility + "]";
	}
}
