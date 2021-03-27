package com.dpdocter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.dpdocter.repository.OAuth2AccessTokenRepository;
import com.dpdocter.repository.OAuth2RefreshTokenRepository;
import com.dpdocter.tokenstore.CustomPasswordEncoder;
import com.dpdocter.tokenstore.OAuth2ClientServiceImpl;
import com.dpdocter.tokenstore.OAuth2RepositoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter{
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

	@Autowired
	private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
	
	@Autowired
	private OAuth2ClientServiceImpl clientDetailsService;
		
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("isAuthenticated()").checkTokenAccess("isAuthenticated()")
		.passwordEncoder(new CustomPasswordEncoder())
		.allowFormAuthenticationForClients().realm("test/client");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients
		
//		.inMemory()
//				.withClient("healthco2admin@16")
//				.secret("{noop}S5HA45KM5M3QX0KKG1")
//				.authorizedGrantTypes("password", "client_credentials", "refresh_token")
//				.scopes("all")
//				.accessTokenValiditySeconds(3600)
//				.refreshTokenValiditySeconds(86400);
				.withClientDetails(clientDetailsService);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore())
		.userApprovalHandler(userApprovalHandler())
		.authenticationManager(authenticationManager)
		.setClientDetailsService(clientDetailsService);
	}
	
	@Bean
	public TokenStore tokenStore() {
		return new OAuth2RepositoryTokenStore(oAuth2AccessTokenRepository, oAuth2RefreshTokenRepository);
	}
	

	@Bean
	public TokenStoreUserApprovalHandler userApprovalHandler(){
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore());
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}
}