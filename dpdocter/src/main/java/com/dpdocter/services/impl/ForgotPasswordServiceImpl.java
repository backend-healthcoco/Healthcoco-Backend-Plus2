package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.TokenRepository;
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

    @Autowired
    private TokenRepository tokenRepository;

    @Value(value = "${FORGOT_PASSWORD_VALID_TIME_IN_MINS}")
    private String forgotPasswordValidTime;

    @Override
    public ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request, UriInfo uriInfo) {
	try {
	    UserCollection userCollection = null;
	    ForgotPasswordResponse response = null;

	    if (request.getUsername() == null)
		request.setUsername(request.getEmailAddress());

	    if (request.getUsername() != null)
		userCollection = userRepository.findByUserName(request.getUsername());
	    if (userCollection != null) {
		if (userCollection.getEmailAddress().trim().equals(request.getEmailAddress().trim())) {
			TokenCollection tokenCollection = new TokenCollection();
		    tokenCollection.setResourceId(userCollection.getId());
		    tokenCollection.setCreatedTime(new Date());
		    tokenCollection = tokenRepository.save(tokenCollection);

		    String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId(), uriInfo);
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
    public Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request, UriInfo uriInfo) {
	Boolean flag = false;
	try {
	    UserCollection userCollection = null;

	    if (request.getUsername() != null) {
		userCollection = userRepository.findByUserName(request.getUsername());
	    }

	    if (userCollection != null) {
		if (request.getEmailAddress() != null && !request.getEmailAddress().isEmpty()) {
		    String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			    userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId(), uriInfo);
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
		
	    String startText = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'><html><head><META http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body>"
						+"<div><div style='margin-top:130px'><div style='padding:20px 30px;border-radius:3px;background-color:#fefefe;border:1px solid #f1f1f1;line-height:30px;margin-bottom:30px;font-family:&#39;Open Sans&#39;,sans-serif;margin:0px auto;min-width:200px;max-width:500px'>"
						+"<div align='center'><h2 style='font-size:20px;color:#2c3335;text-align:center;letter-spacing:1px'>Reset Password</h2><br><p style='color:#2c3335;font-size:15px;text-align:left'>";
		
		String endText = "</p><br><p style='color:#8a6d3b;font-size:15px;text-align:left'>lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum</p>"
		        +"</div></div></div></div></body></html>";
		
		TokenCollection tokenCollection = tokenRepository.findOne(userId);
		if (tokenCollection == null || tokenCollection.getIsUsed()) {
			return startText+"Link is already Used"+endText;
		    } else {
		    	if(!isLinkValid(tokenCollection.getCreatedTime()))
					return startText+"Link is Expired"+endText;
		    	UserCollection userCollection = userRepository.findOne(tokenCollection.getResourceId());
			if (userCollection == null) {
			    return startText+"Invalid Url."+endText;
			}
			userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(password));
			userCollection.setIsTempPassword(false);
			userRepository.save(userCollection);
			
			tokenCollection.setIsUsed(true);
			tokenRepository.save(tokenCollection);
			return startText+"Password Changed Successfully"+endText;
		    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

	@Override
	public String checkLinkIsAlreadyUsed(String userId) {
		try {			
			TokenCollection tokenCollection = tokenRepository.findOne(userId);
			if (tokenCollection == null || tokenCollection.getIsUsed()) {
				return "ALREADY_USED";
			} else {
					if(!isLinkValid(tokenCollection.getCreatedTime()))
						return "EXPIRED";
			    	UserCollection userCollection = userRepository.findOne(tokenCollection.getResourceId());
			    	if (userCollection == null) {
			    		return "INVALID";
			    	}
			    	return "VALID";
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}
	
	private boolean isLinkValid(Date createdTime) {
    	return Minutes.minutesBetween(new DateTime(createdTime), new DateTime()).isLessThan(Minutes.minutes(Integer.parseInt(forgotPasswordValidTime)));
	}
}
