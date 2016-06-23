package com.dpdocter.elasticsearch.beans;
//package com.dpdocter.solr.beans;
//
//import java.util.List;
//
//import com.dpdocter.beans.ConsultationFee;
//import com.dpdocter.beans.DOB;
//import com.dpdocter.beans.DoctorExperience;
//
//public class SolrDoctor extends DoctorLocation {
//
//    private String userId;
//
//    private String firstName;
//
//    private String gender;
//
//    private String emailAddress;
//
//    private String mobileNumber;
//
//    private String imageUrl;
//
//    private ConsultationFee consultationFee;
//
//    private List<SolrWorkingSchedule> workingSchedules;
//
//    private List<String> specialities;
//
//    private DoctorExperience experience;
//
//    private String facility;
//
//    private Boolean isActive = false;
//
//    private Boolean isVerified = false;
//
//    private String coverImageUrl;
//
//    private String colorCode;
//
//    private String userState;
//
//    private DOB dob;
//
//    private Double distance;
//
//    private String locationId;
//
//    private String hospitalId;
//
//    private String locationName;
//
//    private String country;
//
//    private String state;
//
//    private String city;
//
//    private String postalCode;
//
//    private String websiteUrl;
//
//    private Double latitude;
//
//    private Double longitude;
//
//    private String landmarkDetails;
//
//    private String locationEmailAddress;
//
//    private String streetAddress;
//
//    private String locality;
//
//    private String clinicNumber;
//
//    private List<String> alternateClinicNumbers;
//
//    private List<String> specialization;
//
//    private Boolean isClinic = true;
//
//    private Boolean isLab = false;
//
//    private Boolean isOnlineReportsAvailable = false;
//
//    private Boolean isNABLAccredited = false;
//
//    private Boolean isHomeServiceAvailable = false;
//
//    private List<String> images;
//
//    private String logoUrl;
//
//    public String getUserId() {
//	return userId;
//    }
//
//    public void setUserId(String userId) {
//	this.userId = userId;
//    }
//
//    public String getFirstName() {
//	return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//	this.firstName = firstName;
//    }
//
//    public String getGender() {
//	return gender;
//    }
//
//    public void setGender(String gender) {
//	this.gender = gender;
//    }
//
//    public String getEmailAddress() {
//	return emailAddress;
//    }
//
//    public void setEmailAddress(String emailAddress) {
//	this.emailAddress = emailAddress;
//    }
//
//    public String getMobileNumber() {
//	return mobileNumber;
//    }
//
//    public void setMobileNumber(String mobileNumber) {
//	this.mobileNumber = mobileNumber;
//    }
//
//    public String getImageUrl() {
//	return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//	this.imageUrl = imageUrl;
//    }
//
//    public ConsultationFee getConsultationFee() {
//	return consultationFee;
//    }
//
//    public void setConsultationFee(ConsultationFee consultationFee) {
//	this.consultationFee = consultationFee;
//    }
//
//    public List<SolrWorkingSchedule> getWorkingSchedules() {
//	return workingSchedules;
//    }
//
//    public void setWorkingSchedules(List<SolrWorkingSchedule> workingSchedules) {
//	this.workingSchedules = workingSchedules;
//    }
//
//    public List<String> getSpecialities() {
//	return specialities;
//    }
//
//    public void setSpecialities(List<String> specialities) {
//	this.specialities = specialities;
//    }
//
//    public DoctorExperience getExperience() {
//	return experience;
//    }
//
//    public void setExperience(DoctorExperience experience) {
//	this.experience = experience;
//    }
//
//    public String getFacility() {
//	return facility;
//    }
//
//    public void setFacility(String facility) {
//	this.facility = facility;
//    }
//
//    public Boolean getIsActive() {
//	return isActive;
//    }
//
//    public void setIsActive(Boolean isActive) {
//	this.isActive = isActive;
//    }
//
//    public Boolean getIsVerified() {
//	return isVerified;
//    }
//
//    public void setIsVerified(Boolean isVerified) {
//	this.isVerified = isVerified;
//    }
//
//    public String getCoverImageUrl() {
//	return coverImageUrl;
//    }
//
//    public void setCoverImageUrl(String coverImageUrl) {
//	this.coverImageUrl = coverImageUrl;
//    }
//
//    public String getColorCode() {
//	return colorCode;
//    }
//
//    public void setColorCode(String colorCode) {
//	this.colorCode = colorCode;
//    }
//
//    public String getUserState() {
//	return userState;
//    }
//
//    public void setUserState(String userState) {
//	this.userState = userState;
//    }
//
//    public DOB getDob() {
//	return dob;
//    }
//
//    public void setDob(DOB dob) {
//	this.dob = dob;
//    }
//
//    public Double getDistance() {
//	return distance;
//    }
//
//    public void setDistance(Double distance) {
//	this.distance = distance;
//    }
//
//    @Override
//    public String getLocationId() {
//	return locationId;
//    }
//
//    @Override
//    public void setLocationId(String locationId) {
//	this.locationId = locationId;
//    }
//
//    @Override
//    public String getHospitalId() {
//	return hospitalId;
//    }
//
//    @Override
//    public void setHospitalId(String hospitalId) {
//	this.hospitalId = hospitalId;
//    }
//
//    @Override
//    public String getLocationName() {
//	return locationName;
//    }
//
//    @Override
//    public void setLocationName(String locationName) {
//	this.locationName = locationName;
//    }
//
//    @Override
//    public String getCountry() {
//	return country;
//    }
//
//    @Override
//    public void setCountry(String country) {
//	this.country = country;
//    }
//
//    @Override
//    public String getState() {
//	return state;
//    }
//
//    @Override
//    public void setState(String state) {
//	this.state = state;
//    }
//
//    @Override
//    public String getCity() {
//	return city;
//    }
//
//    @Override
//    public void setCity(String city) {
//	this.city = city;
//    }
//
//    @Override
//    public String getPostalCode() {
//	return postalCode;
//    }
//
//    @Override
//    public void setPostalCode(String postalCode) {
//	this.postalCode = postalCode;
//    }
//
//    @Override
//    public String getWebsiteUrl() {
//	return websiteUrl;
//    }
//
//    @Override
//    public void setWebsiteUrl(String websiteUrl) {
//	this.websiteUrl = websiteUrl;
//    }
//
//    @Override
//    public Double getLatitude() {
//	return latitude;
//    }
//
//    @Override
//    public void setLatitude(Double latitude) {
//	this.latitude = latitude;
//    }
//
//    @Override
//    public Double getLongitude() {
//	return longitude;
//    }
//
//    @Override
//    public void setLongitude(Double longitude) {
//	this.longitude = longitude;
//    }
//
//    @Override
//    public String getLandmarkDetails() {
//	return landmarkDetails;
//    }
//
//    @Override
//    public void setLandmarkDetails(String landmarkDetails) {
//	this.landmarkDetails = landmarkDetails;
//    }
//
//    @Override
//    public String getLocationEmailAddress() {
//	return locationEmailAddress;
//    }
//
//    @Override
//    public void setLocationEmailAddress(String locationEmailAddress) {
//	this.locationEmailAddress = locationEmailAddress;
//    }
//
//    @Override
//    public String getStreetAddress() {
//	return streetAddress;
//    }
//
//    @Override
//    public void setStreetAddress(String streetAddress) {
//	this.streetAddress = streetAddress;
//    }
//
//    @Override
//    public String getLocality() {
//	return locality;
//    }
//
//    @Override
//    public void setLocality(String locality) {
//	this.locality = locality;
//    }
//
//    public String getClinicNumber() {
//		return clinicNumber;
//	}
//
//	public void setClinicNumber(String clinicNumber) {
//		this.clinicNumber = clinicNumber;
//	}
//
//	public List<String> getAlternateClinicNumbers() {
//		return alternateClinicNumbers;
//	}
//
//	public void setAlternateClinicNumbers(List<String> alternateClinicNumbers) {
//		this.alternateClinicNumbers = alternateClinicNumbers;
//	}
//
//	@Override
//    public List<String> getSpecialization() {
//	return specialization;
//    }
//
//    @Override
//    public void setSpecialization(List<String> specialization) {
//	this.specialization = specialization;
//    }
//
//    @Override
//    public Boolean getIsLab() {
//	return isLab;
//    }
//
//    @Override
//    public void setIsLab(Boolean isLab) {
//	this.isLab = isLab;
//    }
//
//    @Override
//    public Boolean getIsOnlineReportsAvailable() {
//	return isOnlineReportsAvailable;
//    }
//
//    @Override
//    public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
//	this.isOnlineReportsAvailable = isOnlineReportsAvailable;
//    }
//
//    @Override
//    public Boolean getIsNABLAccredited() {
//	return isNABLAccredited;
//    }
//
//    @Override
//    public void setIsNABLAccredited(Boolean isNABLAccredited) {
//	this.isNABLAccredited = isNABLAccredited;
//    }
//
//    @Override
//    public Boolean getIsHomeServiceAvailable() {
//	return isHomeServiceAvailable;
//    }
//
//    @Override
//    public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
//	this.isHomeServiceAvailable = isHomeServiceAvailable;
//    }
//
//    @Override
//    public List<String> getImages() {
//	return images;
//    }
//
//    @Override
//    public void setImages(List<String> images) {
//	this.images = images;
//    }
//
//    @Override
//    public String getLogoUrl() {
//	return logoUrl;
//    }
//
//    @Override
//    public void setLogoUrl(String logoUrl) {
//	this.logoUrl = logoUrl;
//    }
//
//    @Override
//    public Boolean getIsClinic() {
//	return isClinic;
//    }
//
//    @Override
//    public void setIsClinic(Boolean isClinic) {
//	this.isClinic = isClinic;
//    }
//
//	@Override
//	public String toString() {
//		return "SolrDoctor [userId=" + userId + ", firstName=" + firstName + ", gender=" + gender + ", emailAddress="
//				+ emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl + ", consultationFee="
//				+ consultationFee + ", workingSchedules=" + workingSchedules + ", specialities=" + specialities
//				+ ", experience=" + experience + ", facility=" + facility + ", isActive=" + isActive + ", isVerified="
//				+ isVerified + ", coverImageUrl=" + coverImageUrl + ", colorCode=" + colorCode + ", userState="
//				+ userState + ", dob=" + dob + ", distance=" + distance + ", locationId=" + locationId + ", hospitalId="
//				+ hospitalId + ", locationName=" + locationName + ", country=" + country + ", state=" + state
//				+ ", city=" + city + ", postalCode=" + postalCode + ", websiteUrl=" + websiteUrl + ", latitude="
//				+ latitude + ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails
//				+ ", locationEmailAddress=" + locationEmailAddress + ", streetAddress=" + streetAddress + ", locality="
//				+ locality + ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
//				+ ", specialization=" + specialization + ", isClinic=" + isClinic + ", isLab=" + isLab
//				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
//				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", images=" + images + ", logoUrl=" + logoUrl
//				+ "]";
//	}
//
//}
