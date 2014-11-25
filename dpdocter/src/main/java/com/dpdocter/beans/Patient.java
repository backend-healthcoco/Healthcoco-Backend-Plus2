package com.dpdocter.beans;
/**
 * @author veeraj
 */
public class Patient {
	private String id;
	private String bloodGroup;
	private String imageUrl;
	private String userId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBloodGroup() {
		return bloodGroup;
	}
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "Patient [id=" + id + ", bloodGroup=" + bloodGroup
				+ ", imageUrl=" + imageUrl + ", userId=" + userId + "]";
	}
	
	
}
