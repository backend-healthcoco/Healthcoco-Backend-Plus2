package com.dpdocter.collections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "oauth_client_detail_cl")
public class OAuth2ClientDetailsCollection extends GenericCollection {

	/**
	 * @Harry
	 **/
	@Id
	private ObjectId id;
	@Field
	private String clientId;
	@Field
	private String clientSecret;
	@Field
	private String scope;
	@Field
	private String resourceIds;
	@Field
	private String authorizedGrantTypes;
	@Field
	private String registeredRedirectUris;
	@Field
	private String authorities;
	@Field
	private Integer accessTokenValiditySeconds;
	@Field
	private Integer refreshTokenValiditySeconds;
	@Field
	private Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
	@Field
	private Set<String> autoApproveScopes;

	public OAuth2ClientDetailsCollection() {
	}

	public OAuth2ClientDetailsCollection(ObjectId id, String clientId, String clientSecret, String scope,
			String resourceIds, String authorizedGrantTypes, String registeredRedirectUris, String authorities,
			Integer accessTokenValiditySeconds, Integer refreshTokenValiditySeconds,
			Map<String, Object> additionalInformation, Set<String> autoApproveScopes) {
		super();
		this.id = id;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.scope = scope;
		this.resourceIds = resourceIds;
		this.authorizedGrantTypes = authorizedGrantTypes;
		this.registeredRedirectUris = registeredRedirectUris;
		this.authorities = authorities;
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
		this.additionalInformation = additionalInformation;
		this.autoApproveScopes = autoApproveScopes;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getAuthorizedGrantTypes() {
		return authorizedGrantTypes;
	}

	public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
		this.authorizedGrantTypes = authorizedGrantTypes;
	}

	public String getRegisteredRedirectUris() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUris(String registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris;
	}

	public String getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String authorities) {
		this.authorities = authorities;
	}

	public Integer getAccessTokenValiditySeconds() {
		return accessTokenValiditySeconds;
	}

	public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}

	public Integer getRefreshTokenValiditySeconds() {
		return refreshTokenValiditySeconds;
	}

	public void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
		this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
	}

	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public Set<String> getAutoApproveScopes() {
		return autoApproveScopes;
	}

	public void setAutoApproveScopes(Set<String> autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

}