package com.dpdocter.services.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.User;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.GenerateUniqueUserNameService;
/**
 * Generates a unique username for each user.
 * @author veeraj
 */

@Service
public class GenerateUniqueUserNameServiceImpl implements
		GenerateUniqueUserNameService {
	
	@Autowired
	private UserRepository userRepository;

	public String generate(User user) {
		//UserCollection userCollection = userRepository.findByUserName(user.get)
		return UUID.randomUUID().toString();
	}

}
