package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.OAuth2SaltService;

/**
 * @Harry
 **/
@Service
public class SaltServiceImpl implements OAuth2SaltService {
	@Autowired
	private UserRepository userRepository;

	@SuppressWarnings("finally")
	public String getSalt(UserDetails user) {
		String salt = null;
		try {
			UserCollection userCollection = userRepository.findAdminByMobileNumber(user.getUsername(), "ADMIN");
			if (userCollection.getSalt() != null)
				salt = String.valueOf(userCollection.getSalt());

		} catch (Exception e) {
			System.out.println("error");

		} finally {
			return salt;
		}
	}

}
