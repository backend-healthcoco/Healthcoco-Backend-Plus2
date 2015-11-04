package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.beans.AccessPermission;

@Document(collection = "acos_cl")
public class AcosCollection {
    @Id
    private String id;

    private String module;

    private String url;

    private List<AccessPermission> accessPermissions;

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

    public List<AccessPermission> getAccessPermissions() {
	return accessPermissions;
    }

    public void setAccessPermissions(List<AccessPermission> accessPermissions) {
	this.accessPermissions = accessPermissions;
    }

    @Override
    public String toString() {
	return "AcosCollection [id=" + id + ", module=" + module + ", url=" + url + ", accessPermissions=" + accessPermissions + "]";
    }

}
