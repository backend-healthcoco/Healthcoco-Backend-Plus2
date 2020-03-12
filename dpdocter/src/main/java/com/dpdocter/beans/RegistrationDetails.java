package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ProfileType;

public class RegistrationDetails extends GenericCollection {

	private String id;

	private String userId;

	private String firstName;

	private String localPatientName;

	private String mobileNumber;

	private String uniqueId;

	private String gender;

	private DOB dob;

	private Integer age;

	private String rollNo;

	private ProfileType type = ProfileType.STUDENT;

	private String emailAddress;

	private String bloodGroup;

	private String secMobile;

	private String adhaarId;

	private String panCardNumber;

	private String drivingLicenseId;

	private String insuranceId;

	private String insuranceName;

	private Address address;

	private Long dateOfVisit;

	private String patientNumber;

	private String schoolId;

	private String branchId;

	private String classId;

	private String sectionId;

	private String acadamicClass;

	private String acadamicSection;

	private List<QuestionAnswers> medicalQuestionAnswers;

	private List<QuestionAnswers> lifestyleQuestionAnswers;

	private String imageUrl;

	private String thumbnailUrl;

	private PersonalInformation personalInformation;

	private String fatherName;

	private String motherName;

	private Boolean isSuperStar = false;

	private Date admissionDate;

	private Boolean isGrowthAssessmentPresent = false;
	
	private Boolean isPhysicalAssessmentPresent = false;
	
	private Boolean isEyeAssessmentPresent = false;
	
	private Boolean isDentalAssessmentPresent = false;
	
	private Boolean isENTAssessmentPresent = false;
	
	private Boolean isNutritionalAssessmentPresent = false;
	
	private Boolean isDietPlanPresent = false;

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public ProfileType getType() {
		return type;
	}

	public void setType(ProfileType type) {
		this.type = type;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
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

	public String getPatientNumber() {
		return patientNumber;
	}

	public void setPatientNumber(String patientNumber) {
		this.patientNumber = patientNumber;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
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

	public PersonalInformation getPersonalInformation() {
		return personalInformation;
	}

	public void setPersonalInformation(PersonalInformation personalInformation) {
		this.personalInformation = personalInformation;
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

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getAcadamicClass() {
		return acadamicClass;
	}

	public void setAcadamicClass(String acadamicClass) {
		this.acadamicClass = acadamicClass;
	}

	public String getAcadamicSection() {
		return acadamicSection;
	}

	public void setAcadamicSection(String acadamicSection) {
		this.acadamicSection = acadamicSection;
	}

	public Boolean getIsGrowthAssessmentPresent() {
		return isGrowthAssessmentPresent;
	}

	public void setIsGrowthAssessmentPresent(Boolean isGrowthAssessmentPresent) {
		this.isGrowthAssessmentPresent = isGrowthAssessmentPresent;
	}

	public Boolean getIsPhysicalAssessmentPresent() {
		return isPhysicalAssessmentPresent;
	}

	public void setIsPhysicalAssessmentPresent(Boolean isPhysicalAssessmentPresent) {
		this.isPhysicalAssessmentPresent = isPhysicalAssessmentPresent;
	}

	public Boolean getIsEyeAssessmentPresent() {
		return isEyeAssessmentPresent;
	}

	public void setIsEyeAssessmentPresent(Boolean isEyeAssessmentPresent) {
		this.isEyeAssessmentPresent = isEyeAssessmentPresent;
	}

	public Boolean getIsDentalAssessmentPresent() {
		return isDentalAssessmentPresent;
	}

	public void setIsDentalAssessmentPresent(Boolean isDentalAssessmentPresent) {
		this.isDentalAssessmentPresent = isDentalAssessmentPresent;
	}

	public Boolean getIsENTAssessmentPresent() {
		return isENTAssessmentPresent;
	}

	public void setIsENTAssessmentPresent(Boolean isENTAssessmentPresent) {
		this.isENTAssessmentPresent = isENTAssessmentPresent;
	}

	public Boolean getIsNutritionalAssessmentPresent() {
		return isNutritionalAssessmentPresent;
	}

	public void setIsNutritionalAssessmentPresent(Boolean isNutritionalAssessmentPresent) {
		this.isNutritionalAssessmentPresent = isNutritionalAssessmentPresent;
	}

	public Boolean getIsDietPlanPresent() {
		return isDietPlanPresent;
	}

	public void setIsDietPlanPresent(Boolean isDietPlanPresent) {
		this.isDietPlanPresent = isDietPlanPresent;
	}

	@Override
	public String toString() {
		return "RegistrationDetails [id=" + id + ", userId=" + userId + ", firstName=" + firstName
				+ ", localPatientName=" + localPatientName + ", mobileNumber=" + mobileNumber + ", uniqueId=" + uniqueId
				+ ", gender=" + gender + ", dob=" + dob + ", age=" + age + ", rollNo=" + rollNo + ", type=" + type
				+ ", emailAddress=" + emailAddress + ", bloodGroup=" + bloodGroup + ", secMobile=" + secMobile
				+ ", adhaarId=" + adhaarId + ", panCardNumber=" + panCardNumber + ", drivingLicenseId="
				+ drivingLicenseId + ", insuranceId=" + insuranceId + ", insuranceName=" + insuranceName + ", address="
				+ address + ", dateOfVisit=" + dateOfVisit + ", patientNumber=" + patientNumber + ", schoolId="
				+ schoolId + ", branchId=" + branchId + ", classId=" + classId + ", sectionId=" + sectionId
				+ ", acadamicClass=" + acadamicClass + ", acadamicSection=" + acadamicSection
				+ ", medicalQuestionAnswers=" + medicalQuestionAnswers + ", lifestyleQuestionAnswers="
				+ lifestyleQuestionAnswers + ", imageUrl=" + imageUrl + ", thumbnailUrl=" + thumbnailUrl
				+ ", personalInformation=" + personalInformation + ", fatherName=" + fatherName + ", motherName="
				+ motherName + ", isSuperStar=" + isSuperStar + ", admissionDate=" + admissionDate
				+ ", isGrowthAssessmentPresent=" + isGrowthAssessmentPresent + ", isPhysicalAssessmentPresent="
				+ isPhysicalAssessmentPresent + ", isEyeAssessmentPresent=" + isEyeAssessmentPresent
				+ ", isDentalAssessmentPresent=" + isDentalAssessmentPresent + ", isENTAssessmentPresent="
				+ isENTAssessmentPresent + ", isNutritionalAssessmentPresent=" + isNutritionalAssessmentPresent
				+ ", isDietPlanPresent=" + isDietPlanPresent + "]";
	}
}
