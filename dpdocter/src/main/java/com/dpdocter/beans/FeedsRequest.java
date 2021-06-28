package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.CommunityType;
import com.dpdocter.enums.UserType;

public class FeedsRequest {

	private String id;

	private List<ArticleDetails> multilingual;

	private CommunityType type;

	private String userId;

	private String userName;
	
	private UserType userType;

	private String userImageUrl;
	
	private String speciality;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ArticleDetails> getMultilingual() {
		return multilingual;
	}

	public void setMultilingual(List<ArticleDetails> multilingual) {
		this.multilingual = multilingual;
	}

	public CommunityType getType() {
		return type;
	}

	public void setType(CommunityType type) {
		this.type = type;
	}

	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

}
