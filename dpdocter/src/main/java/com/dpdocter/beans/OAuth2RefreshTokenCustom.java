package com.dpdocter.beans;

import java.util.Date;

public class OAuth2RefreshTokenCustom {

	private Date expiration;
	private String value;
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "OAuth2RefreshTokenCustom [expiration=" + expiration + ", value=" + value + "]";
	}
	
	
}
