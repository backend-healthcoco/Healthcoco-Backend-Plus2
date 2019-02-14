package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DishType;

@Document(collection = "assessment_personal_detail_cl")
public class AssessmentPersonalDetailCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private String assessmentUniqueId;
	@Field
	private List<String> physicalStatusType;
	@Field
	private String goal;
	@Field
	private String community;
	@Field
	private Boolean discarded = false;
	@Field
	private Integer noOfAdultMember = 0;
	@Field
	private Integer noOfChildMember = 0;
	@Field
	private String profession;
	@Field
	private String mobileNumber;
	@Field
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getAssessmentUniqueId() {
		return assessmentUniqueId;
	}

	public void setAssessmentUniqueId(String assessmentUniqueId) {
		this.assessmentUniqueId = assessmentUniqueId;
	}

	public List<String> getPhysicalStatusType() {
		return physicalStatusType;
	}

	public void setPhysicalStatusType(List<String> physicalStatusType) {
		this.physicalStatusType = physicalStatusType;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

}
