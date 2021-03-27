package com.dpdocter.tokenstore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.dpdocter.beans.OAuth2AccessTokenCustom;
import com.dpdocter.beans.OAuth2AuthenticationCustom;
import com.dpdocter.beans.OAuth2RefreshTokenCustom;
import com.dpdocter.beans.OAuth2RequestCustom;
import com.dpdocter.beans.OAuth2SimpleGrantedAuthority;
import com.dpdocter.collections.OAuth2AuthenticationAccessTokenCollection;
import com.dpdocter.collections.OAuth2AuthenticationRefreshTokenCollection;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;

public class OAuth2RepositoryTokenStore implements TokenStore {
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
	    	OAuth2AuthenticationCustom auth2AuthenticationCustom = oAuth2AccessTokenRepository.findByTokenId(tokenId).getAuthentication();
	    	
	    	UsernamePasswordAuthenticationToken userAuthentication= new UsernamePasswordAuthenticationToken(auth2AuthenticationCustom.getUserAuthentication().getPrincipal(),
	    			auth2AuthenticationCustom.getUserAuthentication().getDetails());
	    	userAuthentication.setDetails(auth2AuthenticationCustom.getUserAuthentication().getDetails());
	    	OAuth2RequestCustom oAuth2RequestCustom = auth2AuthenticationCustom.getStoredRequest();
	    	Collection<GrantedAuthority> authorities = new  HashSet<GrantedAuthority>();
	    	
	    	if(oAuth2RequestCustom.getAuthorities() != null) {
	    		for(OAuth2SimpleGrantedAuthority auth2SimpleGrantedAuthority : oAuth2RequestCustom.getAuthorities()) {
	    			GrantedAuthority grantedAuthority = new GrantedAuthority() {
						
						@Override
						public String getAuthority() {
							return auth2SimpleGrantedAuthority.getAuthority();
						}
					};
					authorities.add(grantedAuthority);
	    		}
	    	}
	    	
	    	OAuth2Request oAuth2Request = new OAuth2Request(oAuth2RequestCustom.getRequestParameters(),
	    			oAuth2RequestCustom.getClientId(), authorities, oAuth2RequestCustom.isApproved(), 
	    			oAuth2RequestCustom.getScope(), oAuth2RequestCustom.getResourceIds(), oAuth2RequestCustom.getRedirectUri(), 
	    			oAuth2RequestCustom.getResponseTypes(), oAuth2RequestCustom.getExtensions());
	    	OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
	    	auth2Authentication.setAuthenticated(auth2Authentication.isAuthenticated());
	    	auth2Authentication.setDetails(auth2AuthenticationCustom.getUserAuthentication().getDetails());
	        return auth2Authentication;
	    }

	    @Override
	    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
	    	
	    	OAuth2AccessTokenCustom oAuth2AccessToken = new OAuth2AccessTokenCustom();
	    	BeanUtil.map(token, oAuth2AccessToken);
	    	
	    	OAuth2AuthenticationCustom authenticationCustom = new OAuth2AuthenticationCustom();
	    	BeanUtil.map(authentication, authenticationCustom);

	    	OAuth2Request oAuth2Request = authentication.getOAuth2Request();

	    	Collection<OAuth2SimpleGrantedAuthority> authorities = new  HashSet<OAuth2SimpleGrantedAuthority>();
	    	
	    	if(oAuth2Request.getAuthorities() != null) {
	    		for(GrantedAuthority grantedAuthority : oAuth2Request.getAuthorities()) {
	    			OAuth2SimpleGrantedAuthority auth2SimpleGrantedAuthority = new OAuth2SimpleGrantedAuthority();
	    			auth2SimpleGrantedAuthority.setAuthority(grantedAuthority.getAuthority());
					authorities.add(auth2SimpleGrantedAuthority);
	    		}
	    	}
	    	OAuth2RequestCustom oAuth2RequestCustom = new OAuth2RequestCustom(oAuth2Request.getResourceIds(),
	    			 authorities, oAuth2Request.isApproved(), oAuth2Request.getResponseTypes(), oAuth2Request.getExtensions(),
	    			 oAuth2Request.getClientId(), oAuth2Request.getScope(), oAuth2Request.getRequestParameters(), oAuth2Request.getRedirectUri());
	    	authenticationCustom.setStoredRequest(oAuth2RequestCustom);

	    	OAuth2AuthenticationAccessTokenCollection oAuth2AuthenticationAccessToken = new OAuth2AuthenticationAccessTokenCollection(oAuth2AccessToken,
	        		authenticationCustom, authenticationKeyGenerator.extractKey(authentication));
	      if( this.readAccessToken(token.getValue())!=null)	{
	    	  return;
	      }        
	        oAuth2AccessTokenRepository.save(oAuth2AuthenticationAccessToken);
	    }

	    @Override
	    public OAuth2AccessToken readAccessToken(String tokenValue) {
	    	OAuth2AuthenticationAccessTokenCollection token = oAuth2AccessTokenRepository.findByTokenId(tokenValue);
	        if(token == null) {
	            return null; //let spring security handle the invalid token
	        }
	        OAuth2AccessToken accessToken = getOAuth2AccessToken(token.getoAuth2AccessToken());
			return accessToken;
	    }

	    @Override
	    public void removeAccessToken(OAuth2AccessToken token) {
	    	OAuth2AuthenticationAccessTokenCollection accessToken = oAuth2AccessTokenRepository.findByTokenId(token.getValue());
	        if(accessToken != null) {
	            oAuth2AccessTokenRepository.delete(accessToken);
	        }
	    }

	    @Override
	    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
	    	
	    	OAuth2RefreshTokenCustom oAuth2RefreshTokenCustom = new OAuth2RefreshTokenCustom();
	    	BeanUtil.map(refreshToken, oAuth2RefreshTokenCustom);
	    	
	    	OAuth2AuthenticationCustom authenticationCustom = new OAuth2AuthenticationCustom();
	    	BeanUtil.map(authentication, authenticationCustom);

	    	OAuth2Request oAuth2Request = authentication.getOAuth2Request();

	    	Collection<OAuth2SimpleGrantedAuthority> authorities = new  HashSet<OAuth2SimpleGrantedAuthority>();
	    	
	    	if(oAuth2Request.getAuthorities() != null) {
	    		for(GrantedAuthority grantedAuthority : oAuth2Request.getAuthorities()) {
	    			OAuth2SimpleGrantedAuthority auth2SimpleGrantedAuthority = new OAuth2SimpleGrantedAuthority();
	    			auth2SimpleGrantedAuthority.setAuthority(grantedAuthority.getAuthority());
					authorities.add(auth2SimpleGrantedAuthority);
	    		}
	    	}
	    	OAuth2RequestCustom oAuth2RequestCustom = new OAuth2RequestCustom(oAuth2Request.getResourceIds(),
	    			 authorities, oAuth2Request.isApproved(), oAuth2Request.getResponseTypes(), oAuth2Request.getExtensions(),
	    			 oAuth2Request.getClientId(), oAuth2Request.getScope(), oAuth2Request.getRequestParameters(), oAuth2Request.getRedirectUri());
	    	authenticationCustom.setStoredRequest(oAuth2RequestCustom);
	    	
	         oAuth2RefreshTokenRepository.save(new OAuth2AuthenticationRefreshTokenCollection(oAuth2RefreshTokenCustom, authenticationCustom));
	    }

	    @Override
	    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
	    	OAuth2AuthenticationRefreshTokenCollection auth2AuthenticationRefreshTokenCollection = oAuth2RefreshTokenRepository.findByTokenId(tokenValue);
	    	
	    	OAuth2RefreshToken refreshToken = getOAuth2RefereshToken(auth2AuthenticationRefreshTokenCollection.getoAuth2RefreshToken()); 
	    	
	        return refreshToken;
	    }

	    private OAuth2RefreshToken getOAuth2RefereshToken(OAuth2RefreshTokenCustom oAuth2RefreshToken) {
			return new OAuth2RefreshToken() {
				
				@Override
				public String getValue() {
					return oAuth2RefreshToken.getValue();
				}
			};
		}

		@Override
	    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {	        
	        
	        OAuth2AuthenticationCustom auth2AuthenticationCustom = oAuth2RefreshTokenRepository.findByTokenId(token.getValue()).getAuthentication();
	    	
	    	UsernamePasswordAuthenticationToken userAuthentication= new UsernamePasswordAuthenticationToken(auth2AuthenticationCustom.getUserAuthentication().getPrincipal(),
	    			auth2AuthenticationCustom.getUserAuthentication().getDetails());
	    	userAuthentication.setDetails(auth2AuthenticationCustom.getUserAuthentication().getDetails());
	    	OAuth2RequestCustom oAuth2RequestCustom = auth2AuthenticationCustom.getStoredRequest();
	    	Collection<GrantedAuthority> authorities = new  HashSet<GrantedAuthority>();
	    	
	    	if(oAuth2RequestCustom.getAuthorities() != null) {
	    		for(OAuth2SimpleGrantedAuthority auth2SimpleGrantedAuthority : oAuth2RequestCustom.getAuthorities()) {
	    			GrantedAuthority grantedAuthority = new GrantedAuthority() {
						
						@Override
						public String getAuthority() {
							return auth2SimpleGrantedAuthority.getAuthority();
						}
					};
					authorities.add(grantedAuthority);
	    		}
	    	}
	    	
	    	OAuth2Request oAuth2Request = new OAuth2Request(oAuth2RequestCustom.getRequestParameters(),
	    			oAuth2RequestCustom.getClientId(), authorities, oAuth2RequestCustom.isApproved(), 
	    			oAuth2RequestCustom.getScope(), oAuth2RequestCustom.getResourceIds(), oAuth2RequestCustom.getRedirectUri(), 
	    			oAuth2RequestCustom.getResponseTypes(), oAuth2RequestCustom.getExtensions());
	    	OAuth2Authentication auth2Authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
	    	auth2Authentication.setAuthenticated(auth2Authentication.isAuthenticated());
	    	auth2Authentication.setDetails(auth2AuthenticationCustom.getUserAuthentication().getDetails());
	        return auth2Authentication;
	        
	    }

	    @Override
	    public void removeRefreshToken(OAuth2RefreshToken token) {
	        oAuth2RefreshTokenRepository.delete(oAuth2RefreshTokenRepository.findByTokenId(token.getValue()));
	    }

	    @Override
	    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
	    	 if(oAuth2AccessTokenRepository.findByRefreshToken(refreshToken.getValue())!=null){
	        oAuth2AccessTokenRepository.delete(oAuth2AccessTokenRepository.findByRefreshToken(refreshToken.getValue()));
	    }
	    return;
	    }

	    @Override
	    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
	        OAuth2AuthenticationAccessTokenCollection token =  oAuth2AccessTokenRepository.findByAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
	        
	        return token == null ? null : getOAuth2AccessToken(token.getoAuth2AccessToken());
	    }

	    private OAuth2AccessToken getOAuth2AccessToken(OAuth2AccessTokenCustom oAuth2AccessToken) {
	    		return new OAuth2AccessToken() {
				
				@Override
				public boolean isExpired() {
					return oAuth2AccessToken.isExpired();
				}
				
				@Override
				public String getValue() {
					return oAuth2AccessToken.getValue();
				}
				
				@Override
				public String getTokenType() {
					return oAuth2AccessToken.getTokenType();
				}
				
				@Override
				public Set<String> getScope() {
					return oAuth2AccessToken.getScope();
				}
				
				@Override
				public OAuth2RefreshToken getRefreshToken() {
					OAuth2RefreshToken auth2RefreshToken = new OAuth2RefreshToken() {
						
						@Override
						public String getValue() {
							return oAuth2AccessToken.getRefreshToken() != null ? oAuth2AccessToken.getRefreshToken().getValue():null;
						}
					};
					return auth2RefreshToken;
				}
				
				@Override
				public int getExpiresIn() {
					return oAuth2AccessToken.getExpiresIn();
				}
				
				@Override
				public Date getExpiration() {
					return oAuth2AccessToken.getExpiration();
				}
				
				@Override
				public Map<String, Object> getAdditionalInformation() {
					return oAuth2AccessToken.getAdditionalInformation();
				}
			};
		}

		@Override
	    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
	        List<OAuth2AuthenticationAccessTokenCollection> tokens = oAuth2AccessTokenRepository.findByClientId(clientId);
	        return extractAccessTokens(tokens);
	    }

	    @Override
	    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
	        List<OAuth2AuthenticationAccessTokenCollection> tokens = oAuth2AccessTokenRepository.findByClientIdAndUserName(clientId, userName);
	        return extractAccessTokens(tokens);
	    }

	    private Collection<OAuth2AccessToken> extractAccessTokens(List<OAuth2AuthenticationAccessTokenCollection> tokens) {
	        List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
	        for(OAuth2AuthenticationAccessTokenCollection token : tokens) {
	            accessTokens.add(getOAuth2AccessToken(token.getoAuth2AccessToken()));
	        }
	        return accessTokens;
	    }

	}