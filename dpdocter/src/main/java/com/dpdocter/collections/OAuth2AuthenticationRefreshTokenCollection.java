package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.OAuth2AuthenticationCustom;
import com.dpdocter.beans.OAuth2RefreshTokenCustom;

@Document(collection = "oauth_refresh_token_cl")
public class OAuth2AuthenticationRefreshTokenCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String tokenId;
	@Field
	private OAuth2RefreshTokenCustom oAuth2RefreshToken;
	@Field
	private OAuth2AuthenticationCustom authentication;

	public OAuth2AuthenticationRefreshTokenCollection(OAuth2RefreshTokenCustom oAuth2RefreshToken, OAuth2AuthenticationCustom authentication) {
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

	public OAuth2RefreshTokenCustom getoAuth2RefreshToken() {
		return oAuth2RefreshToken;
	}

	public void setoAuth2RefreshToken(OAuth2RefreshTokenCustom oAuth2RefreshToken) {
		this.oAuth2RefreshToken = oAuth2RefreshToken;
	}

	public OAuth2AuthenticationCustom getAuthentication() {
		return authentication;
	}

	public void setAuthentication(OAuth2AuthenticationCustom authentication) {
		this.authentication = authentication;
	}

}
