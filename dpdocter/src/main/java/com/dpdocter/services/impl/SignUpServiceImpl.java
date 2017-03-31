package com.dpdocter.services.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class SignUpServiceImpl implements SignUpService {

    private static Logger logger = Logger.getLogger(SignUpServiceImpl.class.getName());

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private DoctorClinicProfileRepository doctorClinicProfileRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    @Value(value = "${mail.account.activate.subject}")
    private String accountActivateSubject;

    @Value(value = "${web.link}")
    private String LOGIN_WEB_LINK;

    private final double NAME_MATCH_REQUIRED = 0.80;

    @Value(value = "${patient.count}")
    private String patientCount;
    
    @Autowired
    private GenerateUniqueUserNameService generateUniqueUserNameService;

    @Autowired
    private ESRegistrationService esRegistrationService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Value(value = "${Signup.role}")
    private String role;
    
    @Value(value = "${Signup.DOB}")
    private String DOB;
    
    @Value(value = "${Signup.verifyPatientBasedOn80PercentMatchOfName}")
    private String verifyPatientBasedOn80PercentMatchOfName;
    
    @Value(value = "${Signup.unlockPatientBasedOn80PercentMatch}")
    private String unlockPatientBasedOn80PercentMatch;
    
    @Override
    @Transactional
    public String verifyUser(String tokenId) {
	try {
	    TokenCollection tokenCollection = tokenRepository.findOne(new ObjectId(tokenId));
	    if (tokenCollection == null) {
	    	return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
	    } else if(tokenCollection.getIsUsed()){
	    	return "Your verification link has already been used."+
	    			" Please contact support@healthcoco.com for completing your email verification";
	    }
	    else {
	    if (!forgotPasswordService.isLinkValid(tokenCollection.getCreatedTime()))
	    	return "We were unable to verify your Healthcoco+ account."
	    			+ " Please contact support@healthcoco.com for completing your account verification.";
		DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository.findOne(tokenCollection.getResourceId());
		if (doctorClinicProfileRepository == null) {
		    return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
		}
		UserCollection userCollection = userRepository.findOne(doctorClinicProfileCollection.getDoctorId());
		userCollection.setIsVerified(true);
		userCollection.setUserState(UserState.NOTACTIVATED);
		userRepository.save(userCollection);

		doctorClinicProfileCollection.setIsVerified(true);
		doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		tokenCollection.setIsUsed(true);
		tokenRepository.save(tokenCollection);
		return "You have successfully verified your email address."
				+ "If you haven't already done so, download the Healthcoco+ app - Every Doctor's Pocket Clinic."
				+ "Stay Healthy and Happy!";
	    }
	} catch (IllegalArgumentException argumentException) {
		return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while Activating user");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while Activating user");
	}

    }

    @Override
    @Transactional
    public User patientSignUp(PatientSignUpRequest request) {
	User user = null;
	try {
	    // get role of specified type
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge().getYears() < 0) {
		logger.warn(DOB);
		throw new BusinessException(ServiceError.NotAcceptable, DOB);
	    }
	    char[] salt = DPDoctorUtils.generateSalt();
	    userCollection.setSalt(salt);
	    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
	    for(int i = 0; i < request.getPassword().length; i++)
	        passwordWithSalt[i] = request.getPassword()[i];
	    for(int i = 0; i < salt.length; i++)
	    	passwordWithSalt[i+request.getPassword().length] = salt[i];
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
	    userCollection.setIsActive(true);
	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	    userCollection = userRepository.save(userCollection);

	    // assign roles
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId(), null, null);
	    userRoleRepository.save(userRoleCollection);

	    // save Patient Info
	    PatientCollection patientCollection = new PatientCollection();
	    BeanUtil.map(request, patientCollection);
	    patientCollection.setUserId(userCollection.getId());
	    Date createdTime = new Date();
	    patientCollection.setCreatedTime(createdTime);
	    if (request.getImage() != null) {
			String path = "profile-image";
			// save image
			request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
			ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
			patientCollection.setImageUrl(imageURLResponse.getImageUrl());
			patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
		    }

	    patientCollection = patientRepository.save(patientCollection);
	    user = new User();
	    BeanUtil.map(userCollection, user);
	} catch (BusinessException be) {
	    logger.warn(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating user");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while creating user");
	}
	return user;
    }

    @Override
    @Transactional
    public Boolean checkUserNameExist(String username) {
	try {
	    UserCollection userCollection = userRepository.findByUserName(username);
	    if (userCollection == null) {
		return false;
	    }
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public Boolean checkMobileNumExist(String mobileNum) {
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNum);
	    if (userCollections != null) {
		if (!userCollections.isEmpty()) {
		    return true;
		} else {
		    return false;
		}
	    }
	    return false;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public Boolean checkEmailAddressExist(String email) {
	try {
	    List<UserCollection> userCollections = userRepository.findByEmailAddressIgnoreCase(email);
	    if (userCollections != null) {
		if (!userCollections.isEmpty()) {
		    return true;
		} else {
		    return false;
		}
	    }
	    return false;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public User patientProfilePicChange(PatientProfilePicChangeRequest request) {
	User user = null;
	try {
	    UserCollection userCollection = userRepository.findByUserName(request.getUsername());
	    if (userCollection == null) {
		logger.warn("User not found");
		throw new BusinessException(ServiceError.NotFound, "User not found");
	    } else {
	    	ObjectId locationObjectId = null , hospitalObjectId= null;
			if(!DPDoctorUtils.anyStringEmpty(request.getLocationId()))locationObjectId = new ObjectId(request.getLocationId());
	    	if(!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))hospitalObjectId = new ObjectId(request.getHospitalId());
	    	
	    PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(userCollection.getId(), locationObjectId, hospitalObjectId);	
		if(patientCollection != null){
			if (request.getImage() != null) {
			    String path = "profile-image";
			    // save image
			    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
			    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
			    patientCollection.setImageUrl(imageURLResponse.getImageUrl());
			    patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
			    patientCollection.setUpdatedTime(new Date());
			    patientCollection = patientRepository.save(patientCollection);

			    user = new User();
			    BeanUtil.map(userCollection, user);
			    user.setImageUrl(imageURLResponse.getImageUrl());
			    user.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
			}
		}else{
			logger.warn("No patient found for this doctor");
			throw new BusinessException(ServiceError.NotFound, "No patient found for this doctor");
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return user;
    }

    @Override
    @Transactional
    public PateientSignUpCheckResponse checkMobileNumberSignedUp(String mobileNumber) {
    	PateientSignUpCheckResponse response = new PateientSignUpCheckResponse();
	try {
		List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
			if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
			if (userCollection.isSignedUp())
			    throw new BusinessException(ServiceError.NotAcceptable, "Mobile Number is already signed up. Please Login");
			else{
				if(!response.getIsPatientExistWithMobileNumber())response.setIsPatientExistWithMobileNumber(true);
			}
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public boolean checkMobileNumberExistForPatient(String mobileNumber) {
	boolean response = false;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
			response = true;
			break;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    /**
     * This service implementation checks if name/names for mobileNumber matches
     * 80% with the queryName.
     * 
     * @return boolean
     */

    @Override
    @Transactional
    public boolean verifyPatientBasedOn80PercentMatchOfName(String name, String mobileNumber) {
	boolean checkMatch = false;

	if (!checkMobileNumberExistForPatient(mobileNumber)) {
	    logger.error(verifyPatientBasedOn80PercentMatchOfName+" " + mobileNumber);
	    throw new BusinessException(ServiceError.NoRecord,verifyPatientBasedOn80PercentMatchOfName+ " " + mobileNumber);
	} else {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equals(userCollection.getEmailAddress()))
			if (!userCollection.isSignedUp() && matchName(userCollection.getFirstName(), name)) {
			    checkMatch = true;
			    break;
			}
		}
	    }
	}
	return checkMatch;
    }

    /**
     * This Service Implementation will check for 80% match of name,if matched
     * then unlock all the locked users for the given mobile number.
     */

    @Override
    @Transactional
    public boolean unlockPatientBasedOn80PercentMatch(String name, String mobileNumber) {
	boolean isUnlocked = false;

	if (!checkMobileNumberSignedUp(mobileNumber).getIsPatientExistWithMobileNumber()) {
	    logger.error(unlockPatientBasedOn80PercentMatch+" " + mobileNumber);
	    throw new BusinessException(ServiceError.NoRecord, unlockPatientBasedOn80PercentMatch+" " + mobileNumber);
	} else {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.isSignedUp() && matchName(userCollection.getFirstName(), name)) {
			userCollection.setSignedUp(true);
		    }
		}
		// This batch update will unlock all the locked users.(make
		// signedup flag =true)
		userRepository.save(userCollections);
		isUnlocked = true;
	    }
	}
	return isUnlocked;
    }

    /**
     * This utility method checks for 80% match of name.
     * 
     * @param actualName
     * @param queryName
     * @return boolean
     */
    private boolean matchName(String actualName, String queryName) {
	boolean result = false;
	if (StringUtils.getJaroWinklerDistance(actualName, queryName) >= NAME_MATCH_REQUIRED) {
	    result = true;
	}
	return result;
    }

    /**
     * This service implementation will signup the new patient into DB. This
     * newly created patient will be in unlock state.
     */
    @Override
    @Transactional
    public RegisteredPatientDetails signupNewPatient(PatientSignupRequestMobile request) {
    	RegisteredPatientDetails user = null;
    try {
    		RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	if (roleCollection == null) {
	    logger.warn(role);
	    throw new BusinessException(ServiceError.NoRecord, role);
	}
	// save user
	UserCollection userCollection = new UserCollection();
	BeanUtil.map(request, userCollection);
	char[] salt = DPDoctorUtils.generateSalt();
	userCollection.setSalt(salt);
    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
    for(int i = 0; i < request.getPassword().length; i++)
        passwordWithSalt[i] = request.getPassword()[i];
    for(int i = 0; i < salt.length; i++)
    	passwordWithSalt[i+request.getPassword().length] = salt[i];
    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
	userCollection.setFirstName(request.getName());
	userCollection.setIsActive(true);
	userCollection.setCreatedTime(new Date());
	userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	userCollection.setSignedUp(true);
	userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	
	userCollection.setUserName(generateUniqueUserNameService.generate(new User(userCollection.getFirstName(), request.getMobileNumber())));
	userCollection = userRepository.save(userCollection);
	
	// assign roles
	UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId(), null, null);
	userRoleRepository.save(userRoleCollection);

	// save Patient Info
	PatientCollection patientCollection = new PatientCollection();
	BeanUtil.map(request, patientCollection);
	patientCollection.setFirstName(request.getName());
	patientCollection.setUserId(userCollection.getId());
	patientCollection.setDoctorId(null);
	patientCollection.setLocationId(null);
	patientCollection.setHospitalId(null);
	patientCollection.setCreatedTime(new Date());
	patientCollection = patientRepository.save(patientCollection);
	
	user = new RegisteredPatientDetails();
	if(patientCollection != null){
    	Patient patient = new Patient();
    	BeanUtil.map(patientCollection, patient);
    	BeanUtil.map(patientCollection, user);
    	BeanUtil.map(userCollection, user);
    	patient.setPatientId(patientCollection.getUserId().toString());
    	user.setPatient(patient);
    }
    user.setUserId(userCollection.getId().toString());
	ESPatientDocument esPatientDocument = new ESPatientDocument();
    if (patientCollection.getAddress() != null) {
	BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
    }
    BeanUtil.map(userCollection, esPatientDocument);
    BeanUtil.map(patientCollection, esPatientDocument);
    esPatientDocument.setUserId(userCollection.getId().toString());
    
    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (NoSuchProviderException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
    return user;
 }

    /**
     * This Service Impl will signup the already registered patients into the
     * DB. If 80% match is found for the fname then it will unlock all the
     * patients in the DB.
     */
    @Override
    @Transactional
    public List<RegisteredPatientDetails> signupAlreadyRegisteredPatient(PatientSignupRequestMobile request) {

	List<RegisteredPatientDetails> users = new ArrayList<RegisteredPatientDetails>();
	try{
	List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
	char[] salt = DPDoctorUtils.generateSalt();
	
    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
    for(int i = 0; i < request.getPassword().length; i++)
        passwordWithSalt[i] = request.getPassword()[i];
    for(int i = 0; i < salt.length; i++)
    	passwordWithSalt[i+request.getPassword().length] = salt[i];
    char[] password = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);
	
	if (userCollections != null && !userCollections.isEmpty()) {
	    for (UserCollection userCollection : userCollections) {
	    	if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
	    		RegisteredPatientDetails user = new RegisteredPatientDetails();
	    		userCollection.setSalt(salt);
	    		userCollection.setPassword(password);
		        userCollection.setSignedUp(true);
		        userRepository.save(userCollection);

		    	PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(), null, null, null);
		    	if(patientCollection == null){
		    		patientCollection = new PatientCollection();
		    		BeanUtil.map(userCollection, patientCollection);
		    		patientCollection.setId(null);
			    	patientCollection.setUserId(userCollection.getId());
			    	patientCollection.setDoctorId(null);
			    	patientCollection.setLocationId(null);
			    	patientCollection.setHospitalId(null);
			    	patientCollection.setCreatedTime(new Date());
			    	patientCollection = patientRepository.save(patientCollection);
			    	if(patientCollection != null){
				    	Patient patient = new Patient();
				    	BeanUtil.map(patientCollection, patient);
				    	BeanUtil.map(patientCollection, user);
				    	BeanUtil.map(userCollection, user);
				    	patient.setPatientId(patientCollection.getUserId().toString());
				    	user.setPatient(patient);
				    }
				    user.setUserId(userCollection.getId().toString());
			    	ESPatientDocument esPatientDocument = new ESPatientDocument();
			        if (patientCollection.getAddress() != null) {
			    	BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
			        }
			        BeanUtil.map(userCollection, esPatientDocument);
			        BeanUtil.map(patientCollection, esPatientDocument);
			        esPatientDocument.setUserId(userCollection.getId().toString());
			        transnationalService.addResource(patientCollection.getUserId(), Resource.PATIENT, false);
				    esRegistrationService.addPatient(esPatientDocument);
		    	}
		    	users.add(user);
	    	}
		}
	} else {
	    RegisteredPatientDetails user = signupNewPatient(request);
	    users.add(user);
	}
    } catch (NoSuchAlgorithmException e) {
		e.printStackTrace();
	} catch (NoSuchProviderException e) {
		e.printStackTrace();
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	return users;

    }

}
