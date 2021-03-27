package com.dpdocter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.dpdocter.security.oauth2.UserServiceImpl;
import com.dpdocter.tokenstore.CustomPasswordEncoder;
import com.dpdocter.tokenstore.OAuth2ClientServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMongoRepositories
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserServiceImpl userDetailsService;
	
	@Autowired
	private OAuth2ClientServiceImpl clientDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.anonymous().disable()
		.authorizeRequests().antMatchers("/oauth/token").permitAll();
	}
		
	@Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(new CustomPasswordEncoder());
        authenticationManagerBuilder.userDetailsService(new ClientDetailsUserDetailsService(clientDetailsService)).passwordEncoder(new CustomPasswordEncoder());

	}

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
	public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
	    StrictHttpFirewall firewall = new StrictHttpFirewall();
	    firewall.setAllowUrlEncodedSlash(true);
	    firewall.setAllowSemicolon(true);
	    return firewall;
	}
}
