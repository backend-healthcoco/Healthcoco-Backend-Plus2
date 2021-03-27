package com.dpdocter.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OAuth2RequestCustom {

	
	private Set<String> resourceIds;

	private Collection<OAuth2SimpleGrantedAuthority> authorities;

	private boolean approved = false;

	private Set<String> responseTypes;
	
	private Map<String, Serializable> extensions;
	
	private String clientId;
	
	Set<String> scope ;
	
	Map<String, String> requestParameters;

	String redirectUri;

	public OAuth2RequestCustom() {
		super();
	}

	public OAuth2RequestCustom(Set<String> resourceIds, Collection<OAuth2SimpleGrantedAuthority> authorities,
			boolean approved, Set<String> responseTypes, Map<String, Serializable> extensions, String clientId,
			Set<String> scope, Map<String, String> requestParameters, String redirectUri) {
		super();
		this.resourceIds = resourceIds;
		this.authorities = authorities;
		this.approved = approved;
		this.responseTypes = responseTypes;
		this.extensions = extensions;
		this.clientId = clientId;
		this.scope = scope;
		this.requestParameters = requestParameters;
		this.redirectUri = redirectUri;
	}

	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	public Collection<OAuth2SimpleGrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<OAuth2SimpleGrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public Set<String> getResponseTypes() {
		return responseTypes;
	}

	public void setResponseTypes(Set<String> responseTypes) {
		this.responseTypes = responseTypes;
	}

	public Map<String, Serializable> getExtensions() {
		return extensions;
	}

	public void setExtensions(Map<String, Serializable> extensions) {
		this.extensions = extensions;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	@Override
	public String toString() {
		return "OAuth2RequestCustom [resourceIds=" + resourceIds + ", authorities=" + authorities + ", approved="
				+ approved + ", responseTypes=" + responseTypes + ", extensions=" + extensions + ", clientId="
				+ clientId + ", scope=" + scope + ", requestParameters=" + requestParameters + ", redirectUri="
				+ redirectUri + "]";
	}
}
