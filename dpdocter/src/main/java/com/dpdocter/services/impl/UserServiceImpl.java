package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.UserRoleLookupResponse;

@Service
public class UserServiceImpl implements UserDetailsService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
	@Autowired
	UserRepository userRepository;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		UserCollection userCollection = null;
		String password = "password";
		Boolean enabled = true;
		Boolean accountNonExpired = true;
		Boolean credentialsNonExpired = true;
		Boolean accountNonLocked = true;
		List<String> authorities = new ArrayList<>();
		userCollection = userRepository.findByUserName(userName);
		if (userCollection == null) {
			userCollection = userRepository.findByUsername(userName);
		}

		if (userCollection.getPassword() != null && userCollection.getIsActive()) {
			password = new String(userCollection.getPassword());
			List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(userCollection.getId())),
							Aggregation.lookup("role_cl", "roleId", "_id", "roleCollection"),
							Aggregation.unwind("roleCollection")),
					UserRoleCollection.class, UserRoleLookupResponse.class).getMappedResults();
			for (UserRoleLookupResponse userRoleLookupResponse : userRoleLookupResponses) {
				RoleCollection roleCollection = userRoleLookupResponse.getRoleCollection();
				authorities.add(roleCollection.getRole());
			}
		}
		User user = new User(userName, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
				getAuthorities(authorities));
		return user;

	}

	public Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
		List<GrantedAuthority> authList = getGrantedAuthorities(roles);
		return authList;
	}

	public static List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

}
