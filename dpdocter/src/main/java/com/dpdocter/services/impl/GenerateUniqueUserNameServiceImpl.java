package com.dpdocter.services.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.GenerateUniqueUserNameService;

/**
 * Generates a unique username for each user.
 */

@Service
public class GenerateUniqueUserNameServiceImpl implements GenerateUniqueUserNameService {

	private static Logger logger=Logger.getLogger(GenerateUniqueUserNameServiceImpl.class.getName());
	
    @Autowired
    private UserRepository userRepository;

    @Override
    public String generate(User user) {
	// UserCollection userCollection =
	// userRepository.findByUserName(user.get)
	String userName = null;
	try {
	    userName = user.getMobileNumber() + user.getFirstName().substring(0, 2);
	    UserCollection userCollection = userRepository.findByUserName(userName);
	    if (userCollection != null) {
		userName = userName + RandomStringUtils.randomNumeric(4);
	    }
	    /*
	     * List<UserCollection> userCollections =
	     * userRepository.findByFirstNameLastNameMobileNumber
	     * (user.getFirstName(), user.getLastName(),
	     * user.getMobileNumber()); if (userCollections != null &&
	     * userCollections.size() > 1) { userName = user.getMobileNumber() +
	     * user.getFirstName().substring(0, 2) + "0" +
	     * userCollections.size(); }
	     */
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return userName;
    }

}
