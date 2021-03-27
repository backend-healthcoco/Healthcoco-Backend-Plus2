package com.dpdocter.beans;

public class OAuth2AuthenticationCustom {
	
	private OAuth2RequestCustom storedRequest;

	private UserAuthentication userAuthentication;

	public OAuth2AuthenticationCustom() {
		super();
	}

	public OAuth2AuthenticationCustom(OAuth2RequestCustom storedRequest, UserAuthentication userAuthentication) {
		super();
		this.storedRequest = storedRequest;
		this.userAuthentication = userAuthentication;
	}

	public OAuth2RequestCustom getStoredRequest() {
		return storedRequest;
	}

	public void setStoredRequest(OAuth2RequestCustom storedRequest) {
		this.storedRequest = storedRequest;
	}

	public UserAuthentication getUserAuthentication() {
		return userAuthentication;
	}

	public void setUserAuthentication(UserAuthentication userAuthentication) {
		this.userAuthentication = userAuthentication;
	}

	@Override
	public String toString() {
		return "OAuth2AuthenticationCustom [storedRequest=" + storedRequest + ", userAuthentication="
				+ userAuthentication + "]";
	}
	
}
