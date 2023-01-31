package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.DOB;

@Document(indexName = "patients_in", type = "patients")
public class ESPatientDocument {

	@Id
	private String id;

	@Field(type = FieldType.Text)
	private String userId;

	@Field(type = FieldType.Text, fielddata = true)
	private String pid;

	@Field(type = FieldType.Text, fielddata = true)
	private String pnum;
	
	@Field(type = FieldType.Text)
	private String userName;

	@Field(type = FieldType.Text)
	private String firstName;

	@Field(type = FieldType.Text, fielddata = true)
	private String localPatientName;

	@Field(type = FieldType.Text)
	private String localPatientNameFormatted;
	
	@Field(type = FieldType.Text)
	private String gender;

	@Field(type = FieldType.Text)
	private String bloodGroup;

	@Field(type = FieldType.Text, fielddata = true)
	private String emailAddress;

	@Field(type = FieldType.Nested, fielddata = true)
	private DOB dob;

	@Field(type = FieldType.Text)
	private String city;

	@Field(type = FieldType.Text)
	private String locality;

	@Field(type = FieldType.Text)
	private String postalCode;

	@Field(type = FieldType.Text, fielddata = true)
	private String mobileNumber;

    @Field(type = FieldType.Keyword)
	private String profession;

	@Field(type = FieldType.Text)
	private String doctorId;

	@Field(type = FieldType.Text)
	private String locationId;

	@Field(type = FieldType.Text)
	private String hospitalId;

    @Field(type = FieldType.Keyword)
	private String referredBy;

	@Field(type = FieldType.Date)
	private Date createdTime;

	@Field(type = FieldType.Text)
	private String imageUrl;

	@Field(type = FieldType.Text)
	private String thumbnailUrl;

	@Field(type = FieldType.Text)
	private String colorCode;

	@Field(type = FieldType.Long)
	private Long registrationDate;

	@Field(type = FieldType.Text)
	private String userUId;

	@Field(type = FieldType.Text)
	private List<String> consultantDoctorIds;

	@Field(type = FieldType.Boolean)
	private Boolean isPatientDiscarded = false;

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	@Field(type = FieldType.Boolean)
	private Boolean isChild = false;

	@Field(type = FieldType.Text)
	private String fatherName;

	@Field(type = FieldType.Text)
	private String motherName;
	
	@Field(type = FieldType.Text)
	private String userLanguage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserLanguage() {
		return userLanguage;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getUserId() {
		return userId;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPnum() {
		return pnum;
	}

	public void setPnum(String pnum) {
		this.pnum = pnum;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public DOB getDob() {
		return dob;
	}

	public void setDob(DOB dob) {
		this.dob = dob;
		if (this.dob != null)
			dob.getAge();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
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

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
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

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public Long getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public List<String> getConsultantDoctorIds() {
		return consultantDoctorIds;
	}

	public void setConsultantDoctorIds(List<String> consultantDoctorIds) {
		this.consultantDoctorIds = consultantDoctorIds;
	}

	public String getLocalPatientNameFormatted() {
		return localPatientNameFormatted;
	}

	public void setLocalPatientNameFormatted(String localPatientNameFormatted) {
		this.localPatientNameFormatted = localPatientNameFormatted;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getIsChild() {
		return isChild;
	}

	public void setIsChild(Boolean isChild) {
		this.isChild = isChild;
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

	@Override
	public String toString() {
		return "ESPatientDocument [id=" + id + ", userId=" + userId + ", pid=" + pid + ", pnum=" + pnum + ", userName="
				+ userName + ", firstName=" + firstName + ", localPatientName=" + localPatientName
				+ ", localPatientNameFormatted=" + localPatientNameFormatted + ", gender=" + gender + ", bloodGroup="
				+ bloodGroup + ", emailAddress=" + emailAddress + ", dob=" + dob + ", city=" + city + ", locality="
				+ locality + ", postalCode=" + postalCode + ", mobileNumber=" + mobileNumber + ", profession="
				+ profession + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", referredBy=" + referredBy + ", createdTime=" + createdTime + ", imageUrl=" + imageUrl
				+ ", thumbnailUrl=" + thumbnailUrl + ", colorCode=" + colorCode + ", registrationDate="
				+ registrationDate + ", userUId=" + userUId + ", consultantDoctorIds=" + consultantDoctorIds
				+ ", isPatientDiscarded=" + isPatientDiscarded + ", discarded=" + discarded + ", isChild=" + isChild
				+ ", fatherName=" + fatherName + ", motherName=" + motherName + "]";
	}
}
