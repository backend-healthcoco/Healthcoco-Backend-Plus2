package com.dpdocter.tokenstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;

public class OAuth2RepositoryTokenStore implements TokenStore {
	/**
	 * @Harry
	 **/
	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();
	@Autowired
	private final OAuth2AccessTokenRepository oAuth2AccessTokenRepository;
	@Autowired
	private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

	public OAuth2RepositoryTokenStore(final OAuth2AccessTokenRepository oAuth2AccessTokenRepository,
			final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository) {
		this.oAuth2AccessTokenRepository = oAuth2AccessTokenRepository;
		this.oAuth2RefreshTokenRepository = oAuth2RefreshTokenRepository;
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String tokenId) {
		return oAuth2AccessTokenRepository.findByTokenId(tokenId).getAuthentication();
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		OAuth2AuthenticationAccessTokenCollection oAuth2AuthenticationAccessToken = new OAuth2AuthenticationAccessTokenCollection(
				token, authentication, authenticationKeyGenerator.extractKey(authentication));
		if (this.readAccessToken(token.getValue()) != null) {
			return;
		}
		oAuth2AccessTokenRepository.save(oAuth2AuthenticationAccessToken);
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		OAuth2AuthenticationAccessTokenCollection token = oAuth2AccessTokenRepository.findByTokenId(tokenValue);
		if (token == null) {
			return null; // let spring security handle the invalid token
		}
		OAuth2AccessToken accessToken = token.getoAuth2AccessToken();
		return accessToken;
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		OAuth2AuthenticationAccessTokenCollection accessToken = oAuth2AccessTokenRepository
				.findByTokenId(token.getValue());
		if (accessToken != null) {
			oAuth2AccessTokenRepository.delete(accessToken);
		}
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		oAuth2RefreshTokenRepository.save(new OAuth2AuthenticationRefreshTokenCollection(refreshToken, authentication));
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		return oAuth2RefreshTokenRepository.findByTokenId(tokenValue).getoAuth2RefreshToken();
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return oAuth2RefreshTokenRepository.findByTokenId(token.getValue()).getAuthentication();
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		oAuth2RefreshTokenRepository.delete(oAuth2RefreshTokenRepository.findByTokenId(token.getValue()));
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		if (oAuth2AccessTokenRepository.findByRefreshToken(refreshToken.getValue()) != null) {
			oAuth2AccessTokenRepository.delete(oAuth2AccessTokenRepository.findByRefreshToken(refreshToken.getValue()));
		}
		return;
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		OAuth2AuthenticationAccessTokenCollection token = oAuth2AccessTokenRepository
				.findByAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
		return token == null ? null : token.getoAuth2AccessToken();
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		List<OAuth2AuthenticationAccessTokenCollection> tokens = oAuth2AccessTokenRepository.findByClientId(clientId);
		return extractAccessTokens(tokens);
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		List<OAuth2AuthenticationAccessTokenCollection> tokens = oAuth2AccessTokenRepository
				.findByClientIdAndUserName(clientId, userName);
		return extractAccessTokens(tokens);
	}

	private Collection<OAuth2AccessToken> extractAccessTokens(List<OAuth2AuthenticationAccessTokenCollection> tokens) {
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
		for (OAuth2AuthenticationAccessTokenCollection token : tokens) {
			accessTokens.add(token.getoAuth2AccessToken());
		}
		return accessTokens;
	}

}