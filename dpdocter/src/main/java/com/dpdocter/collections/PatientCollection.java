package com.dpdocter.collections;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.PersonalInformation;
import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.beans.Relations;

@Document(collection = "patient_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class PatientCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String firstName;

	@Field
	private String localPatientName;

	@Field
	private String imageUrl;

	@Field
	private String thumbnailUrl;

	@Field
	private String bloodGroup;

	@Field
	private String profession;

	@Field
	private List<Relations> relations;

	@Field
	private String emailAddress;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private String secMobile;

	@Field
	private String landlineNumber;
	
	@Field
	private String adhaarId;

	@Field
	private String panCardNumber;

	@Field
	private String drivingLicenseId;

	@Field
	private String insuranceId;

	@Field
	private String insuranceName;

	@Indexed
	private ObjectId userId;

	@Field
	private List<String> notes;

	@Field
	private String PID;

	@Field
	private Long registrationDate;

	@Field
	private String gender;

	@Field
	private DOB dob;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean isPatientDiscarded = false;

	@Field
	private Long dateOfVisit;

	@Field
	private ObjectId referredBy;

	@Field
	private Address address;

	@Field
	private List<ObjectId> consultantDoctorIds;

	@Field
	private PersonalInformation personalInformation;

	@Field
	private List<QuestionAnswers> medicalQuestionAnswers;

	@Field
	private List<QuestionAnswers> lifestyleQuestionAnswers;

	@Field
	private String PNUM;

	@Field
	private Boolean isNutritionActive = false;

	@Field
	private String fatherName;

	@Field
	private String motherName;

	@Field
	private Boolean isChild = false;
	
	@Field
	private List<String> healthId;
	
	@Field
	private String ndhmToken;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
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

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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

	public void setPID(String PID) {
		this.PID = PID;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		if (firstName != null)
			this.firstName = WordUtils.capitalize(firstName.toLowerCase());
		else
			this.firstName = firstName;
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
		if (this.dob != null)
			dob.getAge();
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public Long getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Long dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public ObjectId getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(ObjectId referredBy) {
		this.referredBy = referredBy;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public List<ObjectId> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<ObjectId> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	public PersonalInformation getPersonalInformation() {
		return personalInformation;
	}

	public void setPersonalInformation(PersonalInformation personalInformation) {
		this.personalInformation = personalInformation;
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

	public String getPNUM() {
		return PNUM;
	}

	public void setPNUM(String pNUM) {
		PNUM = pNUM;
	}

	public Boolean getIsNutritionActive() {
		return isNutritionActive;
	}

	public void setIsNutritionActive(Boolean isNutritionActive) {
		this.isNutritionActive = isNutritionActive;
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

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}
	
	

	public List<String> getHealthId() {
		return healthId;
	}

	public void setHealthId(List<String> healthId) {
		this.healthId = healthId;
	}
	
	

	public String getNdhmToken() {
		return ndhmToken;
	}

	public void setNdhmToken(String ndhmToken) {
		this.ndhmToken = ndhmToken;
	}

	@Override
	public String toString() {
		return "PatientCollection [id=" + id + ", firstName=" + firstName + ", localPatientName=" + localPatientName
				+ ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", bloodGroup=" + bloodGroup
				+ ", profession=" + profession + ", relations=" + relations + ", emailAddress=" + emailAddress
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", secMobile=" + secMobile + ", landlineNumber=" + landlineNumber + ", adhaarId=" + adhaarId
				+ ", panCardNumber=" + panCardNumber + ", drivingLicenseId=" + drivingLicenseId + ", insuranceId="
				+ insuranceId + ", insuranceName=" + insuranceName + ", userId=" + userId + ", notes=" + notes
				+ ", PID=" + PID + ", registrationDate=" + registrationDate + ", gender=" + gender + ", dob=" + dob
				+ ", discarded=" + discarded + ", isPatientDiscarded=" + isPatientDiscarded + ", dateOfVisit="
				+ dateOfVisit + ", referredBy=" + referredBy + ", address=" + address + ", consultantDoctorIds="
				+ consultantDoctorIds + ", personalInformation=" + personalInformation + ", medicalQuestionAnswers="
				+ medicalQuestionAnswers + ", lifestyleQuestionAnswers=" + lifestyleQuestionAnswers + ", PNUM=" + PNUM
				+ ", isNutritionActive=" + isNutritionActive + ", fatherName=" + fatherName + ", motherName="
				+ motherName + ", isChild=" + isChild + "]";
	}
}
