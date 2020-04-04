package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.AcadamicClassSectionCollection;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ProfileType;
import com.dpdocter.response.AcadamicClassResponse;

public class AcademicProfile extends GenericCollection {

	private String id;

	private String userId;

	private String firstName;

	private String localPatientName;

	private String mobileNumber;

	private String uniqueId;

	private String rollNo;

	private String gender;

	private DOB dob;
	
	private AcadamicClassResponse acadamicClass;

	private String emailAddress;

	private String acadamicSection;
	
	private AcadamicClassSectionCollection acadamicClassSection;
	
	private Date admissionDate;

	private ProfileType type = ProfileType.STUDENT;

	private String imageUrl;

	private String thumbnailUrl;

	private Boolean isSuperStar = false;
	
	private SchoolBranch branch;

	private School school;
	
	private Boolean isGrowthAssessmentPresent = false;
	
	private Boolean isPhysicalAssessmentPresent = false;
	
	private Boolean isEyeAssessmentPresent = false;
	
	private Boolean isDentalAssessmentPresent = false;
	
	private Boolean isENTAssessmentPresent = false;
	
	private Boolean isNutritionalAssessmentPresent = false;
	
	private Boolean isDietPlanPresent = false;
	
	public SchoolBranch getBranch() {
		return branch;
	}

	public void setBranch(SchoolBranch branch) {
		this.branch = branch;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
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

	public Boolean getIsSuperStar() {
		return isSuperStar;
	}

	public void setIsSuperStar(Boolean isSuperStar) {
		this.isSuperStar = isSuperStar;
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

	public ProfileType getType() {
		return type;
	}

	public void setType(ProfileType type) {
		this.type = type;
	}

	public String getRollNo() {
		return rollNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public AcadamicClassResponse getAcadamicClass() {
		return acadamicClass;
	}

	public void setAcadamicClass(AcadamicClassResponse acadamicClass) {
		this.acadamicClass = acadamicClass;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getAcadamicSection() {
		return acadamicSection;
	}

	public void setAcadamicSection(String acadamicSection) {
		this.acadamicSection = acadamicSection;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
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

	public AcadamicClassSectionCollection getAcadamicClassSection() {
		return acadamicClassSection;
	}

	public void setAcadamicClassSection(AcadamicClassSectionCollection acadamicClassSection) {
		this.acadamicClassSection = acadamicClassSection;
	}

	@Override
	public String toString() {
		return "AcademicProfile [id=" + id + ", userId=" + userId + ", firstName=" + firstName + ", localPatientName="
				+ localPatientName + ", mobileNumber=" + mobileNumber + ", uniqueId=" + uniqueId + ", rollNo=" + rollNo
				+ ", gender=" + gender + ", dob=" + dob + ", acadamicClass=" + acadamicClass + ", emailAddress="
				+ emailAddress + ", acadamicSection=" + acadamicSection + ", acadamicClassSection="
				+ acadamicClassSection + ", admissionDate=" + admissionDate + ", type=" + type + ", imageUrl="
				+ imageUrl + ", thumbnailUrl=" + thumbnailUrl + ", isSuperStar=" + isSuperStar + ", branch=" + branch
				+ ", school=" + school + ", isGrowthAssessmentPresent=" + isGrowthAssessmentPresent
				+ ", isPhysicalAssessmentPresent=" + isPhysicalAssessmentPresent + ", isEyeAssessmentPresent="
				+ isEyeAssessmentPresent + ", isDentalAssessmentPresent=" + isDentalAssessmentPresent
				+ ", isENTAssessmentPresent=" + isENTAssessmentPresent + ", isNutritionalAssessmentPresent="
				+ isNutritionalAssessmentPresent + ", isDietPlanPresent=" + isDietPlanPresent + "]";
	}
}
