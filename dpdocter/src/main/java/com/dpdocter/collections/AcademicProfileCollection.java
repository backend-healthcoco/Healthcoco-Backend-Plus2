package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.PersonalInformation;
import com.dpdocter.beans.QuestionAnswers;
import com.dpdocter.enums.ProfileType;

@Document(collection = "acadamic_profile_cl")
public class AcademicProfileCollection extends GenericCollection {
	@Id
	private ObjectId id;

	@Field
	private String firstName;

	@Field
	private String localPatientName;

	@Field
	private String mobileNumber;

	@Field
	private String uniqueId;

	@Field
	private String imageUrl;

	@Field
	private String thumbnailUrl;

	@Field
	private String bloodGroup;

	@Field
	private String rollNo;

	@Field
	private ProfileType type = ProfileType.STUDENT;

	@Field
	private String emailAddress;

	@Field
	private ObjectId schoolId;

	@Field
	private ObjectId branchId;

	@Field
	private ObjectId classId;

	@Field
	private ObjectId sectionId;

	@Field
	private String secMobile;

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
	private String gender;

	@Field
	private DOB dob;

	@Field
	private Boolean discarded = false;

	@Field
	private Address address;

	@Field
	private PersonalInformation personalInformation;

	@Field
	private List<QuestionAnswers> medicalQuestionAnswers;

	@Field
	private List<QuestionAnswers> lifestyleQuestionAnswers;

	@Field
	private String fatherName;

	@Field
	private String motherName;

	@Field
	private Boolean isSuperStar = false;

	@Field
	private Date admissionDate;

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
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

	public ObjectId getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(ObjectId schoolId) {
		this.schoolId = schoolId;
	}

	public ObjectId getBranchId() {
		return branchId;
	}

	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
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

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public ObjectId getClassId() {
		return classId;
	}

	public void setClassId(ObjectId classId) {
		this.classId = classId;
	}

	public ObjectId getSectionId() {
		return sectionId;
	}

	public void setSectionId(ObjectId sectionId) {
		this.sectionId = sectionId;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "AcadamicProfileCollection [id=" + id + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", imageUrl=" + imageUrl + ", bloodGroup=" + bloodGroup + ", rollNo=" + rollNo
				+ ", type=" + type + ", emailAddress=" + emailAddress + ", schoolId=" + schoolId + ", branchId="
				+ branchId + ", secMobile=" + secMobile + ", adhaarId=" + adhaarId + ", panCardNumber=" + panCardNumber
				+ ", drivingLicenseId=" + drivingLicenseId + ", insuranceId=" + insuranceId + ", insuranceName="
				+ insuranceName + ", userId=" + userId + ", notes=" + notes + ", gender=" + gender + ", dob=" + dob
				+ ", discarded=" + discarded + ", address=" + address + ", personalInformation=" + personalInformation
				+ ", medicalQuestionAnswers=" + medicalQuestionAnswers + ", lifestyleQuestionAnswers="
				+ lifestyleQuestionAnswers + ", fatherName=" + fatherName + ", motherName=" + motherName + "]";
	}

}
