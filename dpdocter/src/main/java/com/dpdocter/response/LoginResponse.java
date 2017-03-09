package com.dpdocter.response;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.User;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class LoginResponse {

	private User user;

	private List<Hospital> hospitals = null;

	private Boolean isTempPassword = false;

	private OAuth2TokenResponse tokens;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Hospital> getHospitals() {
		if (hospitals == null) {
			hospitals = new ArrayList<Hospital>();
		}
		return hospitals;
	}

	public void setHospitals(List<Hospital> hospitals) {
		this.hospitals = hospitals;
	}

	public Boolean getIsTempPassword() {
		return isTempPassword;
	}

	public void setIsTempPassword(Boolean isTempPassword) {
		this.isTempPassword = isTempPassword;
	}

	@Override
	public String toString() {
		return "LoginResponse [user=" + user + ", hospitals=" + hospitals + ", isTempPassword=" + isTempPassword + "]";
	}

	public OAuth2TokenResponse getTokens() {
		return tokens;
	}

	public void setTokens(OAuth2TokenResponse tokens) {
		this.tokens = tokens;
	}
}
