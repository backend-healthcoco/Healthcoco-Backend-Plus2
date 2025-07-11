package com.dpdocter.collections;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Document(collection = "oauth_access_token_cl")
public class OAuth2AuthenticationAccessTokenCollection extends GenericCollection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private ObjectId id;
	@Field
	private String tokenId;
	@Field
	private OAuth2AccessToken oAuth2AccessToken;
	@Field
	private String authenticationId;
	@Field
	private String userName;
	@Field
	private String clientId;
	@Field
	private OAuth2Authentication authentication;
	@Field
	private String refreshToken;

	OAuth2AuthenticationAccessTokenCollection() {
	}

	public OAuth2AuthenticationAccessTokenCollection(final OAuth2AccessToken oAuth2AccessToken,
			final OAuth2Authentication authentication, final String authenticationId) {
		this.tokenId = oAuth2AccessToken.getValue();
		this.oAuth2AccessToken = oAuth2AccessToken;
		this.authenticationId = authenticationId;
		this.userName = authentication.getName();
		this.clientId = authentication.getOAuth2Request().getClientId();
		this.authentication = authentication;
		this.refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
	}

	public String getTokenId() {
		return tokenId;
	}

	public OAuth2AccessToken getoAuth2AccessToken() {
		return oAuth2AccessToken;
	}

	public String getAuthenticationId() {
		return authenticationId;
	}

	public String getUserName() {
		return userName;
	}

	public String getClientId() {
		return clientId;
	}

	public OAuth2Authentication getAuthentication() {
		return authentication;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		OAuth2AuthenticationAccessTokenCollection that = (OAuth2AuthenticationAccessTokenCollection) o;

		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}