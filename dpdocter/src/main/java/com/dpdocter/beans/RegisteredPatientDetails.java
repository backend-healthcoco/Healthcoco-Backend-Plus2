package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.response.UserNutritionSubscriptionResponse;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class RegisteredPatientDetails extends GenericCollection {

	private String firstName;

	private String localPatientName;

	private String lastName;

	private String middleName;

	private String imageUrl;

	private String thumbnailUrl;

	private DOB dob;

	private String userId;

	private String userName;

	private String countryCode;

	private String mobileNumber;

	private String gender;

	private Patient patient;

	private Address address;

	private List<Group> groups;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String PID;

	private String colorCode;

	private Reference referredBy;

	private Boolean isPartOfClinic;

	private Boolean isPartOfConsultantDoctor = true;

	private String backendPatientId;

	private List<String> consultantDoctorIds;

	private Boolean isPatientDiscarded = false;

	private String PNUM;

	private List<UserNutritionSubscriptionResponse> userNutritionSubscriptions;

	private Boolean isChild = false;

	private String fatherName;

	private String motherName;

	private String landlineNumber;
	
	private String language;

	
	private Boolean isSuperStar = false;
	
	public List<UserNutritionSubscriptionResponse> getUserNutritionSubscriptions() {
		return userNutritionSubscriptions;
	}

	public void setUserNutritionSubscriptions(List<UserNutritionSubscriptionResponse> userNutritionSubscriptions) {
		this.userNutritionSubscriptions = userNutritionSubscriptions;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		if (dob != null)
			dob.getAge();
		this.dob = dob;
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

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Reference getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(Reference referredBy) {
		this.referredBy = referredBy;
	}

	public Boolean getIsPartOfClinic() {
		return isPartOfClinic;
	}

	public void setIsPartOfClinic(Boolean isPartOfClinic) {
		this.isPartOfClinic = isPartOfClinic;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getBackendPatientId() {
		return backendPatientId;
	}

	public void setBackendPatientId(String backendPatientId) {
		this.backendPatientId = backendPatientId;
	}

	public Boolean getIsPartOfConsultantDoctor() {
		return isPartOfConsultantDoctor;
	}

	public void setIsPartOfConsultantDoctor(Boolean isPartOfConsultantDoctor) {
		this.isPartOfConsultantDoctor = isPartOfConsultantDoctor;
	}

	public List<String> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<String> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return "RegisteredPatientDetails [firstName=" + firstName + ", localPatientName=" + localPatientName
				+ ", lastName=" + lastName + ", middleName=" + middleName + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", dob=" + dob + ", userId=" + userId + ", userName=" + userName + ", countryCode="
				+ countryCode + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", patient=" + patient
				+ ", address=" + address + ", groups=" + groups + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", PID=" + PID + ", colorCode=" + colorCode
				+ ", referredBy=" + referredBy + ", isPartOfClinic=" + isPartOfClinic + ", isPartOfConsultantDoctor="
				+ isPartOfConsultantDoctor + ", backendPatientId=" + backendPatientId + ", consultantDoctorIds="
				+ consultantDoctorIds + ", isPatientDiscarded=" + isPatientDiscarded + ", PNUM=" + PNUM
				+ ", userNutritionSubscriptions=" + userNutritionSubscriptions + ", isChild=" + isChild
				+ ", fatherName=" + fatherName + ", motherName=" + motherName + ", landlineNumber=" + landlineNumber
				+ ", isSuperStar=" + isSuperStar + "]";
	}
}
