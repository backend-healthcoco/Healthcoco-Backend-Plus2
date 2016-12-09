package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.RoleCollection;

public class UserRoleLookupResponse extends GenericCollection{
	
	private String id;

    private String userId;

    private String roleId;
    
    private RoleCollection roleCollection;

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

	public RoleCollection getRoleCollection() {
		return roleCollection;
	}

	public void setRoleCollection(RoleCollection roleCollection) {
		this.roleCollection = roleCollection;
	}

	@Override
	public String toString() {
		return "UserRoleLookupResponse [id=" + id + ", userId=" + userId + ", roleId=" + roleId + ", roleCollection="
				+ roleCollection + "]";
	}
}
