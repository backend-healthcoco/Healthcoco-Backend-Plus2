package com.dpdocter.security.oauth2.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dpdocter.collections.UserCollection;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author nehakariya
 * The UserPrincipal class represents an authenticated Spring Security principal. It contains the details of the authenticated user -
 */
public class UserPrincipal implements OAuth2User, UserDetails {
    
	private static final long serialVersionUID = 1L;

	private String id;

    private String name;

    private String userName;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private Map<String, Object> attributes;

    public UserPrincipal(String id, String name, String userName, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(UserCollection user) {
        List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(user.getUserState().name()));
 
        return new UserPrincipal(
                user.getId().toString(),
                user.getFirstName(),
                user.getUserName(),
                user.getEmailAddress(),
                user.getPassword().toString(),
                authorities
        );
    }
    
    public static UserPrincipal create(UserCollection user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public String getId() {
        return id;
    }

    public String getName() {
		return name;
	}

	public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    
	
}