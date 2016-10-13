package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.AccessPermissionType;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class AccessModule {

    private String id;

    private String module;

    private String url;

    private List<AccessPermissionType> accessPermissionTypes;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getModule() {
	return module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    public String getUrl() {
	return url;
    }

    public void setUrl(String url) {
	this.url = url;
    }

	public List<AccessPermissionType> getAccessPermissionTypes() {
		return accessPermissionTypes;
	}

	public void setAccessPermissionTypes(List<AccessPermissionType> accessPermissionTypes) {
		this.accessPermissionTypes = accessPermissionTypes;
	}

	@Override
	public String toString() {
		return "AccessModule [id=" + id + ", module=" + module + ", url=" + url + ", accessPermissionTypes="
				+ accessPermissionTypes + "]";
	}
}
