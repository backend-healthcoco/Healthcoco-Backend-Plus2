package com.dpdocter.beans;

/**
 * @author veeraj
 */
public class User {
	
	private String id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String userName;
	private String password;
	private String emailAddress;
	private String imageUrl;
	private Boolean isActive = false;
	private Hospital hospital;
	private Docter docter;
	private Patient patient;
	private Address address;
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
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
	public Hospital getHospital() {
		return hospital;
	}
	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}
	public Docter getDocter() {
		return docter;
	}
	public void setDocter(Docter docter) {
		this.docter = docter;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName="
				+ lastName + ", middleName=" + middleName + ", userName="
				+ userName + ", password=" + password + ", emailAddress="
				+ emailAddress + ", imageUrl=" + imageUrl + ", isActive="
				+ isActive + ", hospital=" + hospital + ", docter=" + docter
				+ ", patient=" + patient + ", address=" + address + "]";
	}
	
	
}
