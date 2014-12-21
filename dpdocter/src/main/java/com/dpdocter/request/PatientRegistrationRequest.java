package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;

public class PatientRegistrationRequest {

	private String firstName;
	private String middleName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String gender;
	private DOB dob;

	private List<String> groups;
	private String bloodGroup;
	private String profession;
	private List<String> relations;

	private Address address;

	private Long dateOfVisit;
	private String pastHistoryId;
	private String medicalHistoryId;
	private String patientNumber;
	private String referredBy;
	private String locationId;
	
	private String doctorId;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public List<String> getRelations() {
		return relations;
	}

	public void setRelations(List<String> relations) {
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

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
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

	@Override
	public String toString() {
		return "PatientRegistrationRequest [firstName=" + firstName
				+ ", middleName=" + middleName + ", lastName=" + lastName
				+ ", emailAddress=" + emailAddress + ", phoneNumber="
				+ phoneNumber + ", gender=" + gender + ", dob=" + dob
				+ ", groups=" + groups + ", bloodGroup=" + bloodGroup
				+ ", profession=" + profession + ", relations=" + relations
				+ ", address=" + address + ", dateOfVisit=" + dateOfVisit
				+ ", pastHistoryId=" + pastHistoryId + ", medicalHistoryId="
				+ medicalHistoryId + ", patientNumber=" + patientNumber
				+ ", referredBy=" + referredBy + ", locationId=" + locationId
				+ ", doctorId=" + doctorId + "]";
	}

	

}
