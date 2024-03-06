package com.dpdocter.beans;

public class NDHMPrecriptionRecordData {
	private String fullUrl;
	private NDHMRecordDataResource resource;
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public NDHMRecordDataResource getResource() {
		return resource;
	}
	public void setResource(NDHMRecordDataResource resource) {
		this.resource = resource;
	}
	@Override
	public String toString() {
		return "NDHMPrecriptionRecordData [fullUrl=" + fullUrl + ", resource=" + resource + "]";
	}
   
}
