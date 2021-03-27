package com.dpdocter.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import com.dpdocter.security.oauth2.CustomAuthenticationProvider;
import com.dpdocter.security.oauth2.UserServiceImpl;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "test";
	
	@Autowired
	private TokenStore tokenStore;
	
	
	@Autowired
	private UserServiceImpl userDetailsService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
    private CustomAuthenticationProvider authProvider;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID)
		.tokenServices(tokenServices());
	}

	@Bean
	@Primary
	@Autowired
    public DefaultTokenServices tokenServices() {
    	
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore);
        defaultTokenServices.setSupportRefreshToken(true);
    	defaultTokenServices.setAuthenticationManager(createPreAuthProvider());
		return defaultTokenServices;  	
    }
	
	private ProviderManager createPreAuthProvider() {
	    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
	    provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userDetailsService));
	    return new ProviderManager(Arrays.asList(authProvider));
	}
	
	/*
	 * config enable protection on all endpoints starting /api. All other endpoints
	 * can be accessed freely.
	 */	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
        .authorizeRequests()
        .antMatchers("/api/**").authenticated()
        .antMatchers("/").permitAll();
		
		
	}
}
