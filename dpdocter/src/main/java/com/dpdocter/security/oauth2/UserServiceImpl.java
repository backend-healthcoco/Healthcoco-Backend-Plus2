package com.dpdocter.security.oauth2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.security.oauth2.user.UserPrincipal;

@Service
public class UserServiceImpl implements UserDetailsService {

	private static Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());
	@Autowired
	UserRepository userRepository;

	public Optional<UserCollection> findByEmail(String email) {
		return userRepository.findByEmailAddress(email);
	}

	public Optional<UserCollection> findById(String userId) {
		return userRepository.findById(new ObjectId(userId));
	}

	public UserCollection findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	public void saveUser(UserCollection user) {
		userRepository.save(user);
	}

	
	@Override
	public UserDetails loadUserByUsername(String mobileNumber) throws UsernameNotFoundException {
		String password = "password";
		Boolean enabled = true;
		Boolean accountNonExpired = true;
		Boolean credentialsNonExpired = true;
		Boolean accountNonLocked = true;
		List<String> authorities = new ArrayList<>();
		authorities.add("ADMIN");
		UserCollection userCollection = userRepository.findByMobileNumberAndUserState(mobileNumber, "ADMIN").get(0);
		if (userCollection.getPassword() != null) {
			password = new String(userCollection.getPassword());
		}
		if (!userCollection.getIsActive()) {
			throw new BusinessException(ServiceError.NotAuthorized, "user deactivate");

		}
		User user = new User(userCollection.getMobileNumber(), password, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, getAuthorities(authorities));
		return user;

	}

	@Transactional
    public UserDetails loadUserById(String id) {
        Optional<UserCollection> user = userRepository.findById(new ObjectId(id));
        if(!user.isPresent()) {
        	throw new UsernameNotFoundException("User not found with id : " + id);
        }

        return UserPrincipal.create(user.get());
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
