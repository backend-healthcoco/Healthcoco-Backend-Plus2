package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Document(collection = "oauth_refresh_token_cl")
public class OAuth2AuthenticationRefreshTokenCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String tokenId;
	@Field
	private OAuth2RefreshToken oAuth2RefreshToken;
	@Field
	private OAuth2Authentication authentication;

	public OAuth2AuthenticationRefreshTokenCollection(OAuth2RefreshToken oAuth2RefreshToken,
			OAuth2Authentication authentication) {
		this.oAuth2RefreshToken = oAuth2RefreshToken;
		this.authentication = authentication;
		this.tokenId = oAuth2RefreshToken.getValue();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public OAuth2RefreshToken getoAuth2RefreshToken() {
		return oAuth2RefreshToken;
	}

	public void setoAuth2RefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
		this.oAuth2RefreshToken = oAuth2RefreshToken;
	}

	public OAuth2Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(OAuth2Authentication authentication) {
		this.authentication = authentication;
	}

}
