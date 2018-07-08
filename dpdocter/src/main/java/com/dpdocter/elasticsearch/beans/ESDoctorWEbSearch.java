package com.dpdocter.elasticsearch.beans;

import java.util.List;

import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorExperience;

public class ESDoctorWEbSearch {

	private String colorCode;

	private String userId;

	private String title;

	private String firstName;

	private String gender;

	private String thumbnailUrl;

	private ConsultationFee consultationFee;

	private ConsultationFee revisitConsultationFee;

	private List<String> specialities;

	private DoctorExperience experience;

	private String streetAddress;

	private String country;

	private String state;

	private String city;

	private String postalCode;

	private Double latitude;

	private Double longitude;

	private String landmarkDetails;

	private String locality;

	private String clinicNumber;

	private String locationName;

	private String facility;

	private String userUId;

	private Integer noOfRecommenations = 0;

	private String doctorSlugURL;

	private long rankingCount = 0;

	private List<String> appointmentBookingNumber;
	
	private String locationId;

	private String hospitalId;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
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

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public Integer getNoOfRecommenations() {
		return noOfRecommenations;
	}

	public void setNoOfRecommenations(Integer noOfRecommenations) {
		this.noOfRecommenations = noOfRecommenations;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
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

	public String getLandmarkDetails() {
		return landmarkDetails;
	}

	public void setLandmarkDetails(String landmarkDetails) {
		this.landmarkDetails = landmarkDetails;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public List<String> getAppointmentBookingNumber() {
		return appointmentBookingNumber;
	}

	public void setAppointmentBookingNumber(List<String> appointmentBookingNumber) {
		this.appointmentBookingNumber = appointmentBookingNumber;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	@Override
	public String toString() {
		return "ESDoctorWEbSearch [colorCode=" + colorCode + ", userId=" + userId + ", title=" + title + ", firstName="
				+ firstName + ", gender=" + gender + ", thumbnailUrl=" + thumbnailUrl + ", consultationFee=" + consultationFee
				+ ", revisitConsultationFee=" + revisitConsultationFee + ", specialities=" + specialities
				+ ", experience=" + experience + ", streetAddress=" + streetAddress + ", country=" + country
				+ ", state=" + state + ", city=" + city + ", postalCode=" + postalCode + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails + ", locality=" + locality
				+ ", clinicNumber=" + clinicNumber + ", locationName=" + locationName + ", facility=" + facility
				+ ", userUId=" + userUId + ", noOfRecommenations=" + noOfRecommenations + ", doctorSlugURL="
				+ doctorSlugURL + ", rankingCount=" + rankingCount + ", appointmentBookingNumber="
				+ appointmentBookingNumber + ", locationId=" + locationId + ", hospitalId=" + hospitalId + "]";
	}


}
