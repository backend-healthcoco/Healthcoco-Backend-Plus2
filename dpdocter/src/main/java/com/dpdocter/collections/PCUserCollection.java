package com.dpdocter.collections;

import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.DBObject;

@Document(collection = "pc_user_cl")
public class PCUserCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String title;

	@Field
	private String name;

	@Indexed(unique = true)
	private String userName;

	@Field
	private char[] password;

	@Field
	private char[] salt;

	@Indexed
	private String emailAddress;

	@Indexed
	private String mobileNumber;

	@Field
	private String gender;

	@Field
	private String imageUrl;

	@Field
	private String thumbnailUrl;

	@Field
	private Boolean isActive = false;

	@Field
	private Boolean isVerified = false;

	@Field
	private String coverImageUrl;

	@Field
	private String coverThumbnailImageUrl;

	@Field
	private String colorCode;

	@Field
	private String userUId;

	@Field
	private ObjectId companyId;

	@Indexed(unique = true)
	private String mrCode;

	@Field
	private String role;

	@Field
	private List<ObjectId> divisionId;

	public PCUserCollection() {
		super();
	}

	public PCUserCollection(DBObject dbObject) {
		this.title = (String) dbObject.get("title");
		this.name = (String) dbObject.get("name");
		this.userName = (String) dbObject.get("userName");
		this.password = (char[]) dbObject.get("password");
		this.emailAddress = (String) dbObject.get("emailAddress");
		this.isActive = (Boolean) dbObject.get("emailAddress");
		this.userUId = (String) dbObject.get("userUID");
	}

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char[] getPassword() {
		return password;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public char[] getSalt() {
		return salt;
	}

	public void setSalt(char[] salt) {
		this.salt = salt;
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public String getCoverImageUrl() {
		return coverImageUrl;
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getCoverThumbnailImageUrl() {
		return coverThumbnailImageUrl;
	}

	public void setCoverThumbnailImageUrl(String coverThumbnailImageUrl) {
		this.coverThumbnailImageUrl = coverThumbnailImageUrl;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getUserUId() {
		return userUId;
	}

	public void setUserUId(String userUId) {
		this.userUId = userUId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setCompanyId(ObjectId companyId) {
		this.companyId = companyId;
	}

	public String getMrCode() {
		return mrCode;
	}

	public void setMrCode(String mrCode) {
		this.mrCode = mrCode;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<ObjectId> getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(List<ObjectId> divisionId) {
		this.divisionId = divisionId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "UserCollection [id=" + id + ", title=" + title + ", name=" + name + ", userName=" + userName
				+ ", password=" + Arrays.toString(password) + ", salt=" + Arrays.toString(salt) + ", emailAddress="
				+ emailAddress + ", mobileNumber=" + mobileNumber + ", imageUrl=" + imageUrl + ", thumbnailUrl="
				+ thumbnailUrl + ", isActive=" + isActive + ", isVerified=" + isVerified + ", coverImageUrl="
				+ coverImageUrl + ", coverThumbnailImageUrl=" + coverThumbnailImageUrl + ", colorCode=" + colorCode
				+ ", userUId=" + userUId + "]";
	}

}
