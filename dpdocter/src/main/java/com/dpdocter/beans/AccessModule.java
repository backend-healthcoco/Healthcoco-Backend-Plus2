package com.dpdocter.beans;

import java.util.List;

public class AccessModule {
    private String module;

    private List<String> urls;

    private List<AccessPermission> accessPermissions;

    public String getModule() {
	return module;
    }

    public void setModule(String module) {
	this.module = module;
    }

    public List<String> getUrls() {
	return urls;
    }

    public void setUrls(List<String> urls) {
	this.urls = urls;
    }

    public List<AccessPermission> getAccessPermissions() {
	return accessPermissions;
    }

    public void setAccessPermissions(List<AccessPermission> accessPermissions) {
	this.accessPermissions = accessPermissions;
    }

    @Override
    public String toString() {
	return "AccessModule [module=" + module + ", urls=" + urls + ", accessPermissions=" + accessPermissions + "]";
    }

}
