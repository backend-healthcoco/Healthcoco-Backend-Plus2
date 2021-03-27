package com.dpdocter.beans;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class OAuth2AccessTokenCustom {

	private String value;
	private Date expiration;
	private String tokenType;
	private OAuth2RefreshTokenCustom refreshToken;
	Set<String> scope;
	Map<String, Object> additionalInformation;
	boolean isExpired = false;
	private Integer expiresIn;
	
	public boolean isExpired() {
		return isExpired;
	}
	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public OAuth2RefreshTokenCustom getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(OAuth2RefreshTokenCustom refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Set<String> getScope() {
		return scope;
	}
	public void setScope(Set<String> scope) {
		this.scope = scope;
	}
	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}
	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}
	public Integer getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}
	@Override
	public String toString() {
		return "OAuth2AccessTokenCustom [value=" + value + ", expiration=" + expiration + ", tokenType=" + tokenType
				+ ", refreshToken=" + refreshToken + ", scope=" + scope + ", additionalInformation="
				+ additionalInformation + ", isExpired=" + isExpired + ", expiresIn=" + expiresIn + "]";
	}
}

