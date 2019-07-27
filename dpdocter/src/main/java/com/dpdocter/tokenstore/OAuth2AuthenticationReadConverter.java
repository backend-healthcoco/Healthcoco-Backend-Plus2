package com.dpdocter.tokenstore;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.mongodb.DBObject;

//Hackery to deserialize back into an OAuth2Authentication Object made necessary because Spring Mongo can't map clientAuthentication to authorizationRequest
@ReadingConverter
public class OAuth2AuthenticationReadConverter implements Converter<DBObject, OAuth2Authentication> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public OAuth2Authentication convert(DBObject source) {
		System.out.println("in convert");

		DBObject storedRequest = (DBObject) source.get("storedRequest");
		System.out.println(storedRequest);
		System.out.println(storedRequest.get("requestParameters"));

		OAuth2Request oAuth2Request = new OAuth2Request((Map<String, String>) storedRequest.get("requestParameters"),
				(String) storedRequest.get("clientId"), null, true, new HashSet((List) storedRequest.get("scope")),
				null, null, null, null);
		Authentication userAuthentication = null;
		if (source.get("userAuthentication") != null) {
			DBObject userAuthorization = (DBObject) source.get("userAuthentication");
			Object principal = getPrincipalObject(userAuthorization.get("principal"));
			userAuthentication = new UsernamePasswordAuthenticationToken(principal,
					(String) userAuthorization.get("credentials"),
					getAuthorities((List) userAuthorization.get("authorities")));
		}
		OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request, userAuthentication);
		System.out.println(authentication);

		return authentication;
	}

	private Object getPrincipalObject(Object principal) {
		if (principal instanceof DBObject) {
			DBObject principalDBObject = (DBObject) principal;
			UserCollection userCollection = new UserCollection(principalDBObject);
			// RoleCollection roleCollection = new
			// RoleCollection(principalDBObject);
			User user = new User();
			user.setUserName(userCollection.getUserName());
			user.setRole("PHARMACY");
			return user;

		} else {
			return principal;
		}
	}

	private Collection<GrantedAuthority> getAuthorities(List<Map<String, String>> authorities) {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(authorities.size());
		for (Map<String, String> authority : authorities) {
			grantedAuthorities.add(new SimpleGrantedAuthority(authority.get("role")));
		}
		return grantedAuthorities;
	}

}