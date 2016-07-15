package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.GenderType;

/**
 * 
 * @author parag
 *
 */

public class DoctorContactUs {
	
	/*title, firstName, userName,gender, emailAddress, mobileNumber, specialities, city, userCurrentState(value = VERIFIED, APPROACH,
			INTERESTED, NOT INTERESTED, SIGNED UP), Boolean isVerified, Boolean toList*/
	
	private String id;
	private String title;
	private String firstName;
	private String userName;
	private GenderType gender;
	private String email;
	private String mobileNo;
	private List<String> specialities;
	private DoctorContactStateType contactState = DoctorContactStateType.APPROACH;
	private Boolean isVerified;
	private Boolean toList;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public GenderType getGender() {
		return gender;
	}
	public void setGender(GenderType gender) {
		this.gender = gender;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public List<String> getSpecialities() {
		return specialities;
	}
	public void setSpecialities(List<String> specialities) {
		this.specialities = specialities;
	}
	public DoctorContactStateType getContactState() {
		return contactState;
	}
	public void setContactState(DoctorContactStateType contactState) {
		this.contactState = contactState;
	}
	public Boolean getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}
	public Boolean getToList() {
		return toList;
	}
	public void setToList(Boolean toList) {
		this.toList = toList;
	}
	@Override
	public String toString() {
		return "DoctorContactUs [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName=" + userName
				+ ", gender=" + gender + ", email=" + email + ", mobileNo=" + mobileNo + ", specialities="
				+ specialities + ", contactState=" + contactState + ", isVerified=" + isVerified + ", toList=" + toList
				+ "]";
	}
	
	
	

}
