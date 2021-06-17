package com.dpdocter.beans;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.CommunityType;

public class FeedsRequest {

	private String id;

	private List<ArticleDetails> multilingual;

	private CommunityType type;

	private String postByAdminId;

	private String postByAdminName;

	private String postByDoctorId;

	private String postByDoctorName;
	
	private String postByUserId;
	
	private String postByUserName;

	private String postByExpertName;

	private String postByExpertId;

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

	public String getPostByAdminId() {
		return postByAdminId;
	}

	public void setPostByAdminId(String postByAdminId) {
		this.postByAdminId = postByAdminId;
	}

	public String getPostByAdminName() {
		return postByAdminName;
	}

	public void setPostByAdminName(String postByAdminName) {
		this.postByAdminName = postByAdminName;
	}

	public String getPostByDoctorId() {
		return postByDoctorId;
	}

	public void setPostByDoctorId(String postByDoctorId) {
		this.postByDoctorId = postByDoctorId;
	}



	public String getPostByDoctorName() {
		return postByDoctorName;
	}

	public void setPostByDoctorName(String postByDoctorName) {
		this.postByDoctorName = postByDoctorName;
	}

	

	public String getPostByUserId() {
		return postByUserId;
	}

	public void setPostByUserId(String postByUserId) {
		this.postByUserId = postByUserId;
	}

	public String getPostByUserName() {
		return postByUserName;
	}

	public void setPostByUserName(String postByUserName) {
		this.postByUserName = postByUserName;
	}

	public String getPostByExpertName() {
		return postByExpertName;
	}

	public void setPostByExpertName(String postByExpertName) {
		this.postByExpertName = postByExpertName;
	}

	public String getPostByExpertId() {
		return postByExpertId;
	}

	public void setPostByExpertId(String postByExpertId) {
		this.postByExpertId = postByExpertId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

}
