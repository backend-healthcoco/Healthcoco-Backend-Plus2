package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_role_cl")
public class UserRoleCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Indexed
    private ObjectId userId;

    @Field
    private ObjectId roleId;

    public UserRoleCollection(ObjectId userId, ObjectId roleId) {
	this.userId = userId;
	this.roleId = roleId;
    }

    public UserRoleCollection() {
    }

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public ObjectId getUserId() {
	return userId;
    }

    public void setUserId(ObjectId userId) {
	this.userId = userId;
    }

    public ObjectId getRoleId() {
	return roleId;
    }

    public void setRoleId(ObjectId roleId) {
	this.roleId = roleId;
    }

    @Override
    public String toString() {
	return "UserRoleCollection [id=" + id + ", userId=" + userId + ", roleId=" + roleId + "]";
    }

}
