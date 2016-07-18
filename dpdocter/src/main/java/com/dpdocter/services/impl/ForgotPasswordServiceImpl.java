package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.ResetPasswordRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;

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

    @Value(value = "${forgot.password.valid.time.in.mins}")
    private String forgotPasswordValidTime;

    @Value(value = "${mail.resetPasswordSuccess.subject}")
    private String resetPasswordSub;

    @Value(value = "${ForgotPassword.forgotUsername}")
    private String forgotUsername;

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public ForgotPasswordResponse forgotPasswordForDoctor(ForgotUsernamePasswordRequest request) {
	try {
	    UserCollection userCollection = null;
	    ForgotPasswordResponse response = null;

	    if (request.getUsername() == null)
		request.setUsername(request.getEmailAddress());

	    Criteria criteria = new Criteria("userName").regex(request.getUsername(), "i");
		Query query = new Query(); query.addCriteria(criteria);
		List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
		if(userCollections != null && !userCollections.isEmpty())userCollection = userCollections.get(0);

	    if (userCollection != null) {
		if (userCollection.getEmailAddress().trim().equals(request.getEmailAddress().trim())) {
		    TokenCollection tokenCollection = new TokenCollection();
		    tokenCollection.setResourceId(userCollection.getId());
		    tokenCollection.setCreatedTime(new Date());
		    tokenCollection = tokenRepository.save(tokenCollection);

		    String body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getTitle()+" "+userCollection.getFirstName(), tokenCollection.getId());
		    mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);
		    response = new ForgotPasswordResponse(userCollection.getUserName(), userCollection.getMobileNumber(), userCollection.getEmailAddress(), RoleEnum.DOCTOR);
		} else {
		    logger.warn("Email address is empty.");
		    throw new BusinessException(ServiceError.InvalidInput, "Email address is empty.");
		}
	    } else {
		logger.warn("User not Found.");
		throw new BusinessException(ServiceError.Unknown, "User not Found.");
	    }
	    return response;
	} catch (BusinessException be) {
	    logger.error(be + "No account present with email address, please sign up");
	    throw new BusinessException(ServiceError.Unknown, "No account present with email address, please sign up");
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public Boolean forgotPasswordForPatient(ForgotUsernamePasswordRequest request) {
	Boolean flag = false;
	Boolean isPatient = false;
	try {
	    List<UserCollection> userCollections = null;

	    if (request.getMobileNumber() != null) {
		userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
	    }

	    if (userCollections != null) {
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
			isPatient = true;
			break;
		    }
		}
		if (!isPatient) {
		    logger.warn("No account present with mobile number, please sign up");
		    throw new BusinessException(ServiceError.Unknown, "No account present with mobile number, please sign up");
		}
		if (request.getMobileNumber() != null && !request.getMobileNumber().isEmpty()) {
		    String OTP = LoginUtils.generateOTP();
		    SMSTrackDetail smsTrackDetail = sMSServices.createSMSTrackDetail(null, null, null, null, null,
			    "Your Healthcoco account verification number is: " + OTP + ".Enter this in our app to confirm your Healthcoco account.",
			    request.getMobileNumber(), "OTPVerification");
		    sMSServices.sendSMS(smsTrackDetail, false);

		    OTPCollection otpCollection = new OTPCollection();
		    otpCollection.setCreatedTime(new Date());
		    otpCollection.setOtpNumber(OTP);
		    otpCollection.setMobileNumber(request.getMobileNumber());
		    otpCollection.setGeneratorId(request.getMobileNumber());
		    otpCollection.setCreatedBy(request.getMobileNumber());
		    otpCollection = otpRepository.save(otpCollection);

		    flag = true;
		} else {
		    logger.warn("Email address or mobile number should be provided");
		    throw new BusinessException(ServiceError.InvalidInput, "Email address or mobile number should be provided");
		}
	    } else {
		logger.warn("User not Found");
		throw new BusinessException(ServiceError.Unknown, "User not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return flag;
    }

    @Override
    @Transactional
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
		    throw new BusinessException(ServiceError.NoRecord, "User not Found.");
		}
	    } else {
		logger.warn("Username cannot be empty");
		throw new BusinessException(ServiceError.InvalidInput, "Username cannot be empty");
	    }
	    return response;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
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
		logger.warn(forgotUsername);
		throw new BusinessException(ServiceError.InvalidInput, forgotUsername);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return flag;
    }

    @Override
    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
	try {
	    TokenCollection tokenCollection = tokenRepository.findOne(new ObjectId(request.getUserId()));
	    if (tokenCollection == null || tokenCollection.getIsUsed()) {
		return "Link is already Used";
	    } else {
		if (!isLinkValid(tokenCollection.getCreatedTime()))
		    return "Link is Expired";
		UserCollection userCollection = userRepository.findOne(tokenCollection.getResourceId());
		if (userCollection == null) {
		    return "Invalid Url.";
		}
		char[] salt = DPDoctorUtils.generateSalt();
	    userCollection.setSalt(salt);
	    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
	    for(int i = 0; i < request.getPassword().length; i++)
	        passwordWithSalt[i] = request.getPassword()[i];
	    for(int i = 0; i < salt.length; i++)
	    	passwordWithSalt[i+request.getPassword().length] = salt[i];
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
//		userCollection.setIsTempPassword(false);
		userRepository.save(userCollection);

		tokenCollection.setIsUsed(true);
		tokenRepository.save(tokenCollection);

		String body = mailBodyGenerator.generateResetPasswordSuccessEmailBody(userCollection.getTitle()+" "+userCollection.getFirstName());
		mailService.sendEmail(userCollection.getEmailAddress(), resetPasswordSub, body, null);

		return "Password Changed Successfully";
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public String checkLinkIsAlreadyUsed(String userId) {
	try {
	    TokenCollection tokenCollection = tokenRepository.findOne(new ObjectId(userId));
	    if (tokenCollection == null || tokenCollection.getIsUsed()) {
		return "ALREADY_USED";
	    } else {
		if (!isLinkValid(tokenCollection.getCreatedTime()))
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

    @Override
    @Transactional
    public Boolean resetPasswordPatient(ResetPasswordRequest request) {
	Boolean response = false;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
	    if (userCollections != null && !userCollections.isEmpty()) {
	    	char[] salt = DPDoctorUtils.generateSalt();
		    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
		    for(int i = 0; i < request.getPassword().length; i++)
		        passwordWithSalt[i] = request.getPassword()[i];
		    for(int i = 0; i < salt.length; i++)
		    	passwordWithSalt[i+request.getPassword().length] = salt[i];
		    char[] password = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
		    userCollection.setSalt(salt);
			userCollection.setPassword(password);
			userRepository.save(userCollection);
		    }
		}
		response = true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }
}
