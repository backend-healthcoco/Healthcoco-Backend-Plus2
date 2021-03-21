package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.DishType;

public class AssessmentPersonalDetail extends GenericCollection {

	private String id;

	private String firstName;

	private String mobileNumber;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String bloodGroup;

	private String gender;

	private String assessmentUniqueId;

	private DOB dob;

	private Integer age;

	private List<String> physicalStatusType;

	private Address address;

	private List<FoodCommunity> communities;
	
	private List<NutrientGoal> nutrientGoals;

	private Boolean discarded = false;

	private Integer noOfAdultMember = 0;

	private Integer noOfChildMember = 0;

	private String profession;

	private DishType dietType = DishType.VEG;

	public DishType getDietType() {
		return dietType;
	}

	public void setDietType(DishType dietType) {
		this.dietType = dietType;
	}

	public Integer getNoOfAdultMember() {
		return noOfAdultMember;
	}

	public void setNoOfAdultMember(Integer noOfAdultMember) {
		this.noOfAdultMember = noOfAdultMember;
	}

	public Integer getNoOfChildMember() {
		return noOfChildMember;
	}

	public void setNoOfChildMember(Integer noOfChildMember) {
		this.noOfChildMember = noOfChildMember;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
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

	public List<String> getPhysicalStatusType() {
		return physicalStatusType;
	}

	public void setPhysicalStatusType(List<String> physicalStatusType) {
		this.physicalStatusType = physicalStatusType;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getAssessmentUniqueId() {
		return assessmentUniqueId;
	}

	public void setAssessmentUniqueId(String assessmentUniqueId) {
		this.assessmentUniqueId = assessmentUniqueId;
	}

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

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public List<FoodCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<FoodCommunity> communities) {
		this.communities = communities;
	}

	public List<NutrientGoal> getNutrientGoals() {
		return nutrientGoals;
	}

	public void setNutrientGoals(List<NutrientGoal> nutrientGoals) {
		this.nutrientGoals = nutrientGoals;
	}

	@Override
	public String toString() {
		return "AssessmentPersonalDetail [id=" + id + ", firstName=" + firstName + ", mobileNumber=" + mobileNumber
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", bloodGroup=" + bloodGroup + ", gender=" + gender
				+ ", assessmentUniqueId=" + assessmentUniqueId + ", dob=" + dob + ", age=" + age
				+ ", physicalStatusType=" + physicalStatusType + ", address=" + address + ", communities=" + communities
				+ ", nutrientGoals=" + nutrientGoals + ", discarded=" + discarded + ", noOfAdultMember="
				+ noOfAdultMember + ", noOfChildMember=" + noOfChildMember + ", profession=" + profession
				+ ", dietType=" + dietType + "]";
	}

}
