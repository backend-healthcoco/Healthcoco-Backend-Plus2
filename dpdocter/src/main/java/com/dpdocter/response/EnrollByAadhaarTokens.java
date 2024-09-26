package com.dpdocter.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrollByAadhaarTokens {
	@JsonProperty("token") // This annotation is optional if the names match
	public String token;
	@JsonProperty("expiresIn") // This annotation is optional if the names match
	public String expiresIn;
	@JsonProperty("refreshToken") // This annotation is optional if the names match
	public String refreshToken;
	@JsonProperty("refreshExpiresIn") // This annotation is optional if the names match
	public String refreshExpiresIn;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	public void setRefreshExpiresIn(String refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}

}
