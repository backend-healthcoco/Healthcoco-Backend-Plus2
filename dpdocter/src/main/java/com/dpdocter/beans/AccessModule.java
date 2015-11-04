package com.dpdocter.beans;

import java.util.List;

public class AccessModule {
    private String module;

    private String url;

    private List<AccessPermission> accessPermissions;

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
	return "AccessModule [module=" + module + ", url=" + url + ", accessPermissions=" + accessPermissions + "]";
    }

}
