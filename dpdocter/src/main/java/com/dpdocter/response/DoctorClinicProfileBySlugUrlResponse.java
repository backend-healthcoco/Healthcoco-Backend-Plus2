package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.Feedback;
import com.dpdocter.enums.DoctorFacility;

public class DoctorClinicProfileBySlugUrlResponse {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String clinicAddress;

	private String locationName;

	private String country;

	private String city;

	private String postalCode;

	private Double latitude;

	private Double longitude;

	private ConsultationFee consultationFee;

	private AppointmentSlot appointmentSlot;

	private DoctorFacility facility;

	private List<String> images;

	private Integer noOfReviews = 0;

	private Integer noOfRecommenations = 0;

	private String locality;

	private List<Feedback> feedbacks;

	private Integer noOfFeedbacks = 0;

	private String doctorSlugURL;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public DoctorFacility getFacility() {
		return facility;
	}

	public void setFacility(DoctorFacility facility) {
		this.facility = facility;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
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

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public List<Feedback> getFeedbacks() {
		return feedbacks;
	}

	public void setFeedbacks(List<Feedback> feedbacks) {
		this.feedbacks = feedbacks;
	}

	public Integer getNoOfFeedbacks() {
		return noOfFeedbacks;
	}

	public void setNoOfFeedbacks(Integer noOfFeedbacks) {
		this.noOfFeedbacks = noOfFeedbacks;
	}

	public String getDoctorSlugURL() {
		return doctorSlugURL;
	}

	public void setDoctorSlugURL(String doctorSlugURL) {
		this.doctorSlugURL = doctorSlugURL;
	}

	@Override
	public String toString() {
		return "DoctorClinicProfileBySlugUrlResponse [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", clinicAddress=" + clinicAddress + ", locationName="
				+ locationName + ", country=" + country + ", city=" + city + ", postalCode=" + postalCode
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", consultationFee=" + consultationFee
				+ ", appointmentSlot=" + appointmentSlot + ", facility=" + facility + ", images=" + images
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", locality="
				+ locality + ", feedbacks=" + feedbacks + ", noOfFeedbacks=" + noOfFeedbacks + ", doctorSlugURL="
				+ doctorSlugURL + "]";
	}
}
