package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Relations;

public class PatientRegistrationRequest {
	private String userId;

	private String firstName;

	private String middleName;

	private String lastName;

	private String mobileNumber;

	private String gender;

	private DOB dob;

	private FileDetails image;

	private String emailAddress;

	private List<String> groups;

	private String bloodGroup;

	private String profession;

	private List<Relations> relations;

	private String secMobile;

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

	private String referredBy;

	private String locationId;

	private String hospitalId;

	private String doctorId;

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

	@Override
	public String toString() {
		return "PatientRegistrationRequest [userId=" + userId + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", mobileNumber=" + mobileNumber + ", gender=" + gender + ", dob=" + dob + ", emailAddress=" + emailAddress + ", groups=" + groups
				+ ", bloodGroup=" + bloodGroup + ", profession=" + profession + ", relations=" + relations + ", secMobile=" + secMobile + ", adhaarId="
				+ adhaarId + ", panCardNumber=" + panCardNumber + ", drivingLicenseId=" + drivingLicenseId + ", insuranceId=" + insuranceId
				+ ", insuranceName=" + insuranceName + ", notes=" + notes + ", address=" + address + ", dateOfVisit=" + dateOfVisit + ", pastHistoryId="
				+ pastHistoryId + ", medicalHistoryId=" + medicalHistoryId + ", patientNumber=" + patientNumber + ", referredBy=" + referredBy
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + "]";
	}

}
