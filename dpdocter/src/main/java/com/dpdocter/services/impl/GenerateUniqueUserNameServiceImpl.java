package com.dpdocter.services.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.User;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.GenerateUniqueUserNameService;

import common.util.web.DPDoctorUtils;

/**
 * Generates a unique username for each user.
 */

@Service
public class GenerateUniqueUserNameServiceImpl implements GenerateUniqueUserNameService {

    private static Logger logger = Logger.getLogger(GenerateUniqueUserNameServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String generate(User user) {
		String userName = null;
		try {
			if(!DPDoctorUtils.allStringsEmpty(user.getMobileNumber()))
				userName = user.getMobileNumber() + user.getFirstName().substring(0, 2);
			else {
				userName = DPDoctorUtils.generateRandomNumber() + user.getFirstName().substring(0, 2);
			}
		    UserCollection userCollection = userRepository.findByUserName(userName);
		    if (userCollection != null) {
		    		userName = userName + RandomStringUtils.randomNumeric(4);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return userName;
    }
}
