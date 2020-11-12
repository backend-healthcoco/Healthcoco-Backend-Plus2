package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.MedicalHistoryHandler;
import com.dpdocter.beans.PersonalInformation;
import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.Relations;
import com.dpdocter.enums.ComponentType;

public class PatientRegistrationRequest {
	private String userId;

	private String firstName;

	private String localPatientName;

	private String mobileNumber;

	private String gender;

	private DOB dob;

	private Integer age;

	private FileDetails image;

	private String emailAddress;

	private List<String> groups;

	private String bloodGroup;

	private String profession;

	private List<Relations> relations;

	private String secMobile;

	private String landlineNumber;
	
	private String adhaarId;

	private String panCardNumber;

	private String drivingLicenseId;

	private String insuranceId;

	private String insuranceName;

	private List<String> notes;

	private Address address;

	private Long dateOfVisit;

	private String pastHistoryId;

	private String medicalHistoryId;

	private String patientNumber;

	private Reference referredBy;

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private Integer regularCheckUpMonths;

	private String role;

	private Long registrationDate;

	private List<QuestionAnswers> medicalQuestionAnswers;

	private List<QuestionAnswers> lifestyleQuestionAnswers;

	private PersonalInformation personalInformation;

	private String PNUM;

	private String PID;

	private ComponentType recordType;

	private String recordId;

	private PersonalHistoryAddRequest personalHistoryAddRequest;

	private MedicalHistoryHandler pastMedicalHistoryHandler;

	private MedicalHistoryHandler familyMedicalHistoryHandler;

	private Boolean isChild = false;

	private String fatherName;

	private String motherName;
	
	private String imageUrl;
		
	private List<String> healthId;
	
	private String ndhmToken;

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Long getDateOfVisit() {
		return dateOfVisit;
	}

	public void setDateOfVisit(Long dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public String getPastHistoryId() {
		return pastHistoryId;
	}

	public void setPastHistoryId(String pastHistoryId) {
		this.pastHistoryId = pastHistoryId;
	}

	public String getMedicalHistoryId() {
		return medicalHistoryId;
	}

	public void setMedicalHistoryId(String medicalHistoryId) {
		this.medicalHistoryId = medicalHistoryId;
	}

	public String getPatientNumber() {
		return patientNumber;
	}

	public void setPatientNumber(String patientNumber) {
		this.patientNumber = patientNumber;
	}

	public Reference getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(Reference referredBy) {
		this.referredBy = referredBy;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	public FileDetails getImage() {
		return image;
	}

	public void setImage(FileDetails image) {
		this.image = image;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public Integer getRegularCheckUpMonths() {
		return regularCheckUpMonths;
	}

	public void setRegularCheckUpMonths(Integer regularCheckUpMonths) {
		this.regularCheckUpMonths = regularCheckUpMonths;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
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

	public String getPID() {
		return PID;
	}

	public void setPID(String pID) {
		PID = pID;
	}

	public ComponentType getRecordType() {
		return recordType;
	}

	public void setRecordType(ComponentType recordType) {
		this.recordType = recordType;
	}

	public PersonalHistoryAddRequest getPersonalHistoryAddRequest() {
		return personalHistoryAddRequest;
	}

	public void setPersonalHistoryAddRequest(PersonalHistoryAddRequest personalHistoryAddRequest) {
		this.personalHistoryAddRequest = personalHistoryAddRequest;
	}

	public MedicalHistoryHandler getPastMedicalHistoryHandler() {
		return pastMedicalHistoryHandler;
	}

	public void setPastMedicalHistoryHandler(MedicalHistoryHandler pastMedicalHistoryHandler) {
		this.pastMedicalHistoryHandler = pastMedicalHistoryHandler;
	}

	public MedicalHistoryHandler getFamilyMedicalHistoryHandler() {
		return familyMedicalHistoryHandler;
	}

	public void setFamilyMedicalHistoryHandler(MedicalHistoryHandler familyMedicalHistoryHandler) {
		this.familyMedicalHistoryHandler = familyMedicalHistoryHandler;
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
	
	
	public List<String> getHealthId() {
		return healthId;
	}

	public void setHealthId(List<String> healthId) {
		this.healthId = healthId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	

	public String getNdhmToken() {
		return ndhmToken;
	}

	public void setNdhmToken(String ndhmToken) {
		this.ndhmToken = ndhmToken;
	}

	@Override
	public String toString() {
		return "PatientRegistrationRequest [userId=" + userId + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", dob=" + dob + ", age="
				+ age + ", image=" + image + ", emailAddress=" + emailAddress + ", groups=" + groups + ", bloodGroup="
				+ bloodGroup + ", profession=" + profession + ", relations=" + relations + ", secMobile=" + secMobile
				+ ", landlineNumber=" + landlineNumber + ", adhaarId=" + adhaarId + ", panCardNumber=" + panCardNumber
				+ ", drivingLicenseId=" + drivingLicenseId + ", insuranceId=" + insuranceId + ", insuranceName="
				+ insuranceName + ", notes=" + notes + ", address=" + address + ", dateOfVisit=" + dateOfVisit
				+ ", pastHistoryId=" + pastHistoryId + ", medicalHistoryId=" + medicalHistoryId + ", patientNumber="
				+ patientNumber + ", referredBy=" + referredBy + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", doctorId=" + doctorId + ", regularCheckUpMonths=" + regularCheckUpMonths + ", role="
				+ role + ", registrationDate=" + registrationDate + ", medicalQuestionAnswers=" + medicalQuestionAnswers
				+ ", lifestyleQuestionAnswers=" + lifestyleQuestionAnswers + ", personalInformation="
				+ personalInformation + ", PNUM=" + PNUM + ", PID=" + PID + ", recordType=" + recordType + ", recordId="
				+ recordId + ", personalHistoryAddRequest=" + personalHistoryAddRequest + ", pastMedicalHistoryHandler="
				+ pastMedicalHistoryHandler + ", familyMedicalHistoryHandler=" + familyMedicalHistoryHandler
				+ ", isChild=" + isChild + ", fatherName=" + fatherName + ", motherName=" + motherName + "]";
	}
}
