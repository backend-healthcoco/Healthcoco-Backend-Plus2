package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.enums.GenderType;

@Document(collection = "doctor_contact_us_cl")
public class DoctorContactUsCollection {

	@Id
	private ObjectId id;
	@Field
	private String title;
	@Field
	private String firstName;
	@Indexed(unique = true)
	private String userName;
	@Field
	private GenderType gender;
	@Indexed
	private String email;
	@Indexed
	private String mobileNo;
	@Field
	private List<String> specialities;
	@Field
	private DoctorContactStateType contactState;
	@Field
	private Boolean isVerified;
	@Field
	private Boolean toList;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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
		return "DoctorContactUsCollection [id=" + id + ", title=" + title + ", firstName=" + firstName + ", userName="
				+ userName + ", gender=" + gender + ", email=" + email + ", mobileNo=" + mobileNo + ", specialities="
				+ specialities + ", doctorContactState=" + contactState + ", isVerified=" + isVerified
				+ ", toList=" + toList + "]";
	}

}
