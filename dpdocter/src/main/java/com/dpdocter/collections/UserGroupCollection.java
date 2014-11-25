package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection="user_group_cl")
public class UserGroupCollection {

	@Id
	private String id;
	
	@Field
	private String userId;
	@Field
	private String groupId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	@Override
	public String toString() {
		return "UserGroupCollection [id=" + id + ", userId=" + userId
				+ ", groupId=" + groupId + "]";
	}
	
	
}
