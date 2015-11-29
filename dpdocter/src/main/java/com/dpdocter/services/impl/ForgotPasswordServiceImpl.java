package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import common.util.web.DPDoctorUtils;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    private static Logger logger = Logger.getLogger(ForgotPasswordServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailService mailService;

    @Value(value = "${mail.forgotPassword.subject}")
    private String forgotUsernamePasswordSub;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Override
    public ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request) {
	try {
	    UserCollection userCollection = null;
	    ForgotPasswordResponse response = null;

	    if (request.getUsername() != null) {
		userCollection = userRepository.findByUserName(request.getUsername());
	    }
	    if (userCollection != null) {
		if (userCollection.getEmailAddress().trim().equals(request.getEmailAddress().trim())) {
		    String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			    userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId());
		    mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
		    response = new ForgotPasswordResponse(userCollection.getUserName(), userCollection.getMobileNumber(), userCollection.getEmailAddress(),
			    RoleEnum.DOCTOR);
		} else {
		    logger.warn("Email address is empty.");
		    throw new BusinessException(ServiceError.Unknown, "Email address is empty.");
		}
	    } else {
		logger.warn("User not Found.");
		throw new BusinessException(ServiceError.Unknown, "User not Found.");
	    }
	    return response;
	} catch (BusinessException be) {
	    logger.error(be + " User not Found.");
	    throw new BusinessException(ServiceError.Unknown, "User not Found.");
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
	Boolean flag = false;
	try {
	    UserCollection userCollection = null;

	    if (request.getUsername() != null) {
		userCollection = userRepository.findByUserName(request.getUsername());
	    }

	    if (userCollection != null) {
		if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
		    String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			    userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId());
		    mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
		    flag = true;
		} else if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
		    // SMS logic will go here.
		    flag = true;
		} else {
		    logger.warn("Email address or mobile number should be provided");
		    throw new BusinessException(ServiceError.Unknown, "Email address or mobile number should be provided");
		}
	    } else {
		logger.warn("User not Found.");
		throw new BusinessException(ServiceError.Unknown, "User not Found.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return flag;
    }

    @Override
    public ForgotPasswordResponse getEmailAndMobNumberOfPatient(String username) {
	try {
	    UserCollection userCollection = null;
	    ForgotPasswordResponse response = null;

	    if (username != null && !username.isEmpty()) {
		userCollection = userRepository.findByUserName(username);
		if (userCollection != null) {
		    response = new ForgotPasswordResponse(username, userCollection.getMobileNumber(), userCollection.getEmailAddress(), RoleEnum.PATIENT);
		} else {
		    logger.warn("User not Found.");
		    throw new BusinessException(ServiceError.Unknown, "User not Found.");
		}
	    } else {
		logger.warn("Username cannot be empty");
		throw new BusinessException(ServiceError.Unknown, "Username cannot be empty");
	    }
	    return response;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public String resetPassword(ResetPasswordRequest request) {
	try {
	    UserCollection userCollection = userRepository.findOne(request.getUserId());
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(request.getPassword()));
	    userCollection.setIsTempPassword(false);
	    userRepository.save(userCollection);
	    return "Password Changed Successfully";
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public Boolean forgotUsername(ForgotUsernamePasswordRequest request) {
	boolean flag = false;
	try {
	    List<UserCollection> userCollections = null;
	    if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
		userCollections = userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
		if (userCollections != null) {
		    String body = mailBodyGenerator.generateForgotUsernameEmailBody(userCollections);
		    mailService.sendEmail(request.getEmailAddress(), forgotUsernamePasswordSub, body, null);
		    flag = true;
		}
	    } else if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
		userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
		if (userCollections != null) {
		    // SMS logic will go here.
		    flag = true;
		}
	    } else {
		logger.warn("Email Address or mobile should be provided!");
		throw new BusinessException(ServiceError.Unknown, "Email Address or mobile should be provided!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return flag;
    }

    @Override
    public String resetPassword(String userId, String password) {
	try {
	    UserCollection userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
		userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(password));
		userCollection.setIsTempPassword(false);
		userRepository.save(userCollection);
		return "Password Changed Successfully";
	    } else {
		return "Not Valid";
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }
}
