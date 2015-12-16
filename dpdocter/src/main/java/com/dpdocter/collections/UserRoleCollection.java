package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_role_cl")
public class UserRoleCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String userId;

    @Field
    private String roleId;

    public UserRoleCollection(String userId, String roleId) {
	this.userId = userId;
	this.roleId = roleId;
    }

    public UserRoleCollection() {
	}

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

    public String getRoleId() {
	return roleId;
    }

    public void setRoleId(String roleId) {
	this.roleId = roleId;
    }

    @Override
    public String toString() {
	return "UserRoleCollection [id=" + id + ", userId=" + userId + ", roleId=" + roleId + "]";
    }

}
