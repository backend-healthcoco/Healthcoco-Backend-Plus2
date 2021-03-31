package com.dpdocter.security.oauth2;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@RestController
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
      String username = authentication.getName();
      String password = authentication.getCredentials().toString();
      if (username != null && password!=null) {
            return new UsernamePasswordAuthenticationToken(username, password, Collections.emptyList());
       } else {
            throw new BadCredentialsException("Authentication failed");
       }
    }
    @Override
    public boolean supports(Class<?>aClass) {
//      return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    	return true;
    }
}