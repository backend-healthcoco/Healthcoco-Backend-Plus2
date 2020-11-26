package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.PersonalInformation;
import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Relations;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.UserCollection;

public class PatientCollectionResponse {

	private String id;

	private String firstName;
	
	private String localPatientName;

	private String imageUrl;

	private String thumbnailUrl;

	private String bloodGroup;

	private String profession;

	private List<Relations> relations;

	private String emailAddress;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String secMobile;

	private String adhaarId;

	private String panCardNumber;

	private String drivingLicenseId;

	private String insuranceId;

	private String insuranceName;

	private String userId;

	private List<String> notes;

	private String PID;

	private Long registrationDate;

	private String gender;

	private DOB dob;

	private Boolean discarded = false;

	private Long dateOfVisit;

	private String referredBy;

	private Address address;

	private UserCollection user;

	private Reference reference;
	
	private List<PatientGroupCollection> patientGroupCollections;

	private List<QuestionAnswers> medicalQuestionAnswers;
	
	private List<QuestionAnswers> lifestyleQuestionAnswers;

	private PersonalInformation personalInformation;

	private String PNUM;
	
	private String landlineNumber;
	
	private List<String> healthId;
	
	private String ndhmToken;
	
	private String linkToken;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public List<Relations> getRelations() {
		return relations;
	}

	public void setRelations(List<Relations> relations) {
		this.relations = relations;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
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

	public String getSecMobile() {
		return secMobile;
	}

	public void setSecMobile(String secMobile) {
		this.secMobile = secMobile;
	}

	public String getAdhaarId() {
		return adhaarId;
	}

	public void setAdhaarId(String adhaarId) {
		this.adhaarId = adhaarId;
	}

	public String getPanCardNumber() {
		return panCardNumber;
	}

	public void setPanCardNumber(String panCardNumber) {
		this.panCardNumber = panCardNumber;
	}

	public String getDrivingLicenseId() {
		return drivingLicenseId;
	}

	public void setDrivingLicenseId(String drivingLicenseId) {
		this.drivingLicenseId = drivingLicenseId;
	}

	public String getInsuranceId() {
		return insuranceId;
	}

	public void setInsuranceId(String insuranceId) {
		this.insuranceId = insuranceId;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Long getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Long dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public UserCollection getUser() {
		return user;
	}

	public void setUser(UserCollection user) {
		this.user = user;
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public List<PatientGroupCollection> getPatientGroupCollections() {
		return patientGroupCollections;
	}

	public void setPatientGroupCollections(List<PatientGroupCollection> patientGroupCollections) {
		this.patientGroupCollections = patientGroupCollections;
	}

	public List<QuestionAnswers> getMedicalQuestionAnswers() {
		return medicalQuestionAnswers;
	}

	public void setMedicalQuestionAnswers(List<QuestionAnswers> medicalQuestionAnswers) {
		this.medicalQuestionAnswers = medicalQuestionAnswers;
	}

	public List<QuestionAnswers> getLifestyleQuestionAnswers() {
		return lifestyleQuestionAnswers;
	}

	public void setLifestyleQuestionAnswers(List<QuestionAnswers> lifestyleQuestionAnswers) {
		this.lifestyleQuestionAnswers = lifestyleQuestionAnswers;
	}

	public PersonalInformation getPersonalInformation() {
		return personalInformation;
	}

	public void setPersonalInformation(PersonalInformation personalInformation) {
		this.personalInformation = personalInformation;
	}

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}
	
	
	

	public String getNdhmToken() {
		return ndhmToken;
	}

	public void setNdhmToken(String ndhmToken) {
		this.ndhmToken = ndhmToken;
	}

	public List<String> getHealthId() {
		return healthId;
	}

	public void setHealthId(List<String> healthId) {
		this.healthId = healthId;
	}
	
	

	public String getLinkToken() {
		return linkToken;
	}

	public void setLinkToken(String linkToken) {
		this.linkToken = linkToken;
	}

	@Override
	public String toString() {
		return "PatientCollectionResponse [id=" + id + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", bloodGroup="
				+ bloodGroup + ", profession=" + profession + ", relations=" + relations + ", emailAddress="
				+ emailAddress + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", secMobile=" + secMobile + ", adhaarId=" + adhaarId + ", panCardNumber=" + panCardNumber
				+ ", drivingLicenseId=" + drivingLicenseId + ", insuranceId=" + insuranceId + ", insuranceName="
				+ insuranceName + ", userId=" + userId + ", notes=" + notes + ", PID=" + PID + ", registrationDate="
				+ registrationDate + ", gender=" + gender + ", dob=" + dob + ", discarded=" + discarded
				+ ", dateOfVisit=" + dateOfVisit + ", referredBy=" + referredBy + ", address=" + address + ", user="
				+ user + ", reference=" + reference + ", patientGroupCollections=" + patientGroupCollections
				+ ", medicalQuestionAnswers=" + medicalQuestionAnswers + ", lifestyleQuestionAnswers="
				+ lifestyleQuestionAnswers + ", personalInformation=" + personalInformation + ", PNUM=" + PNUM
				+ ", landlineNumber=" + landlineNumber + "]";
	}

}
