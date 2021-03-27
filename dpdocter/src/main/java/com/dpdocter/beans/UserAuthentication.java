package com.dpdocter.beans;

import java.util.Collection;

import com.dpdocter.security.oauth2.user.OAuth2UserInfo;

public class UserAuthentication {

	OAuth2UserInfo principal;
	
	Collection<OAuth2SimpleGrantedAuthority> authorities;
	
	UserAuthenticationDetails details;
	
	boolean authenticated;

	public OAuth2UserInfo getPrincipal() {
		return principal;
	}

	public void setPrincipal(OAuth2UserInfo principal) {
		this.principal = principal;
	}

	public Collection<OAuth2SimpleGrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<OAuth2SimpleGrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public UserAuthenticationDetails getDetails() {
		return details;
	}

	public void setDetails(UserAuthenticationDetails details) {
		this.details = details;
	}

	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	@Override
	public String toString() {
		return "UserAuthentication [principal=" + principal + ", authorities=" + authorities + ", details=" + details
				+ ", authenticated=" + authenticated + "]";
	}
}
