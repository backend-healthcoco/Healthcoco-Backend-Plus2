package com.dpdocter.beans;

import org.json.JSONObject;

public class NdhmOauthResponse {

	private String accessToken;
	private String tokenType;
	private String refreshToken;
	private Integer expiresIn;
	private String refreshExpiresIn;
	private NdhmErrorObject error;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public Integer getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getRefreshExpiresIn() {
		return refreshExpiresIn;
	}
	public void setRefreshExpiresIn(String refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}
	public NdhmErrorObject getError() {
		return error;
	}
	public void setError(NdhmErrorObject error) {
		this.error = error;
	}
	
	
	
	
	
	
	
}
