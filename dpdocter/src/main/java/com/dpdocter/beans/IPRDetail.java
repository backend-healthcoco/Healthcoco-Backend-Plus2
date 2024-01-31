package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class IPRDetail {
	private String toothnumber;
	private String sizeInMM;
	public String getToothnumber() {
		return toothnumber;
	}
	public void setToothnumber(String toothnumber) {
		this.toothnumber = toothnumber;
	}
	public String getSizeInMM() {
		return sizeInMM;
	}
	public void setSizeInMM(String sizeInMM) {
		this.sizeInMM = sizeInMM;
	}

}
