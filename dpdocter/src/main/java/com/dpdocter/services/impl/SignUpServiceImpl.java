package com.dpdocter.services.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.AccessModule;
import com.dpdocter.beans.AccessPermission;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AccessPermissionType;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.Module;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DoctorSignupHandheldContinueRequest;
import com.dpdocter.request.DoctorSignupHandheldRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

/**
 * @author veeraj
 */
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
    private HospitalRepository hospitalRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private AccessControlServices accessControlServices;

    @Autowired
    private LocationServices locationServices;

    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    @Value(value = "${mail.account.activate.subject}")
    private String accountActivateSubject;

    @Value(value = "${web.link}")
    private String LOGIN_WEB_LINK;

    private final double NAME_MATCH_REQUIRED = 0.80;

    @Value(value = "${patient.count}")
    private String patientCount;
    
//    @Autowired
//    private SMSFormatRepository sMSFormatRepository;

    @Autowired
    private GenerateUniqueUserNameService generateUniqueUserNameService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ESRegistrationService esRegistrationService;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Value(value = "${Signup.role}")
    private String role;
    
    @Value(value = "${Signup.DOB}")
    private String DOB;
    
    @Value(value = "${Signup.verifyPatientBasedOn80PercentMatchOfName}")
    private String verifyPatientBasedOn80PercentMatchOfName;
    
    @Value(value = "${Signup.unlockPatientBasedOn80PercentMatch}")
    private String unlockPatientBasedOn80PercentMatch;
    
    /**
     * @param UserTemp
     *            Id
     * @return Boolean This method activates the user account.
     */
    @Override
    @Transactional
    public String verifyUser(String tokenId) {
	try {
//	    String startText = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'><html><head><title>verification</title><meta charset='utf-8'>"
//		    + "<meta name='viewport' content='width=device-width, initial-scale=1'><link href='https://fonts.googleapis.com/css?family=Open+Sans:400,600' rel='stylesheet' type='text/css'>"
//		    + "</head><body>"
//		    + "<div><div style='margin-top:130px'><div style='padding:20px 30px;border-radius:3px;background-color:#fefefe;border:1px solid #f1f1f1;line-height:30px;margin-bottom:30px;font-family:&#39;Open Sans&#39;,sans-serif;margin:0px auto;min-width:200px;max-width:500px'>"
//		    + "<div align='center'><h2 style='font-size:20px;color:#2c3335;text-align:center;letter-spacing:1px'>Account Verification</h2><br><p style='color:#2c3335;font-size:15px;text-align:left'>";
//
//	    String endText = "</p><br><p style='color:#8a6d3b;font-size:15px;text-align:left'>lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum</p>"
//		    + "<br/><a href='" + LOGIN_WEB_LINK
//		    + "' style='border-radius: 3px;color: white;font-size: 15px;font-weight:bold;padding: 11px 7px;max-width: 200px;border: 1px #1373b5 solid;text-align: center;text-decoration: none;width: 200px;margin: 10px auto;display: block;background-color: #007ee6;'>"
//		    + "Click here to Login</a>" + "</div></div></div></div></body></html>";

	    TokenCollection tokenCollection = tokenRepository.findOne(tokenId);
	    if (tokenCollection == null) {
	    	return "Invalid token";
	    } else if(tokenCollection.getIsUsed()){
	    	return "Link is already Used";
	    }
	    else {
		UserLocationCollection userLocationCollection = userLocationRepository.findOne(tokenCollection.getResourceId());
		if (userLocationCollection == null) {
		    return "Invalid Url.";
		}
		UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
		userCollection.setIsVerified(true);
		userCollection.setUserState(UserState.NOTACTIVATED);
		userRepository.save(userCollection);

		userLocationCollection.setIsVerified(true);
		userLocationRepository.save(userLocationCollection);
		tokenCollection.setIsUsed(true);
		tokenRepository.save(tokenCollection);
		return "Account is Verfied";
	    }

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
    public Boolean activateUser(String userId, Boolean activate) {
	UserCollection userCollection = null;
	Boolean response = false;
	try {
	    userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
	    
	    if(userCollection.getUserState().getState().equalsIgnoreCase(UserState.USERSTATEINCOMPLETE.getState())){
	    	logger.error("User State is incomplete so user cannot be activated");
			throw new BusinessException(ServiceError.Unknown, "User State is incomplete so user cannot be activated");
	    }
	    else if(userCollection.getUserState().getState().equalsIgnoreCase(UserState.NOTVERIFIED.getState())){
	    	logger.error("User has not verified his mail so user cannot be activated");
			throw new BusinessException(ServiceError.Unknown, "User has not verified his mail so user cannot be activated");
	    }
	    	userCollection.setIsActive(activate);
			if(activate)userCollection.setUserState(UserState.USERSTATECOMPLETE);
			else userCollection.setUserState(UserState.NOTACTIVATED);
			userRepository.save(userCollection);
			response = true;
			List<UserRoleCollection> userRoleCollection = userRoleRepository.findByUserId(userCollection.getId());
			@SuppressWarnings("unchecked")
		    Collection<String> roleIds = CollectionUtils.collect(userRoleCollection, new BeanToPropertyValueTransformer("roleId"));
		    if(roleIds != null && !roleIds.isEmpty()){
		    	List<RoleCollection> roleCollections = roleRepository.findByIdAndRole(roleIds, RoleEnum.SUPER_ADMIN.getRole());
		    	if(roleCollections != null && !roleCollections.isEmpty()){
		    		for(RoleCollection roleCollection : roleCollections){
		    			List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(roleCollection.getLocationId());
		    			if(userLocationCollections != null && !userLocationCollections.isEmpty()){
		    				for(UserLocationCollection userLocationCollection : userLocationCollections){
		    					userLocationCollection.setIsActivate(activate);
		    					userLocationRepository.save(userLocationCollection);
		    				}
		    			}
		    		}
		    	}
		    }
			if (activate) {
			    SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

			    smsTrackDetail.setType("AFTER_VERIFICATION_TO_DOCTOR");
			    SMSDetail smsDetail = new SMSDetail();
			    smsDetail.setUserId(userCollection.getId());
			    smsDetail.setUserName(userCollection.getFirstName());
			    SMS sms = new SMS();
			    sms.setSmsText("Healthcoco "+(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName()+", Your Healthcoco+ account has been activated,Download the Healthcoco+ app now: https://healthcoco.com/doctors/app. For queries, please feel free to contact us at support@healthcoco.com.");

			    SMSAddress smsAddress = new SMSAddress();
			    smsAddress.setRecipient(userCollection.getMobileNumber());
			    sms.setSmsAddress(smsAddress);

			    smsDetail.setSms(sms);
			    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			    smsDetails.add(smsDetail);
			    smsTrackDetail.setSmsDetails(smsDetails);
			    sMSServices.sendSMS(smsTrackDetail, true);
			    
			    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getFirstName(), null, "accountActivateTemplate.vm", null, null);
				mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
			}

		} 
	    else {
			logger.error("User Not Found For The Given User Id");
			throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
		    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error While Verifying User");
	    throw new BusinessException(ServiceError.Unknown, "Error While Verifying User");
	}
	return response;
    }

	@Override
	public Boolean activateLocation(String locationId, Boolean activate) {
		LocationCollection locationCollection = null;
		Boolean response = false;
		try {
			locationCollection = locationRepository.findOne(locationId);
		    if (locationCollection != null) {
		    
		    List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(locationId);
		    if(userLocationCollections != null && !userLocationCollections.isEmpty()){
		    	for(UserLocationCollection userLocationCollection : userLocationCollections){
		    		userLocationCollection.setIsActivate(activate);
		    		userLocationRepository.save(userLocationCollection);
		    	}
		    }
		    } else {
			logger.error("Location Not Found For The Given Id");
			throw new BusinessException(ServiceError.Unknown, "Location Not Found For The Given Id");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While Verifying User");
		    throw new BusinessException(ServiceError.Unknown, "Error While Verifying User");
		}
		return response;
	}

    @Override
    @Transactional
    public DoctorSignUp doctorSignUp(DoctorSignupRequest request, UriInfo uriInfo) {
	DoctorSignUp response = null;

	try {
		if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
			logger.warn("Email Address cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
		 }
	    // get role of specified type
	    RoleCollection hospitalAdmin = roleRepository.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());
	    if (hospitalAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection locationAdmin = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
	    if (locationAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection doctorRole = roleRepository.findByRole(RoleEnum.DOCTOR.getRole());
	    if (doctorRole == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge() != null && request.getDob().getAge().getYears() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.NotAcceptable, "Incorrect Date of Birth");
	    }
	    char[] salt = DPDoctorUtils.generateSalt();
	    userCollection.setSalt(salt);
	    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
	    for(int i = 0; i < request.getPassword().length; i++)
	        passwordWithSalt[i] = request.getPassword()[i];
	    for(int i = 0; i < salt.length; i++)
	    	passwordWithSalt[i+request.getPassword().length] = salt[i];
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
	    userCollection.setUserName(request.getEmailAddress());
	    userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	    userCollection.setTitle("Dr.");
	    if (request.getImage() != null) {
		String path = "profile-pic";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
		userCollection.setImageUrl(imageURLResponse.getImageUrl());
		userCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
	    }
	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection.setUserState(UserState.USERSTATECOMPLETE);
	    userCollection = userRepository.save(userCollection);
	    // save doctor specific details
	    DoctorCollection doctorCollection = new DoctorCollection();
	    BeanUtil.map(request, doctorCollection);
	    doctorCollection.setUserId(userCollection.getId());
	    doctorCollection.setCreatedTime(new Date());
	    doctorCollection = doctorRepository.save(doctorCollection);

	    // Save hospital
	    HospitalCollection hospitalCollection = new HospitalCollection();
	    BeanUtil.map(request, hospitalCollection);
	    hospitalCollection.setHospitalUId(UniqueIdInitial.HOSPITAL.getInitial()+DPDoctorUtils.generateRandomId());
	    hospitalCollection.setCreatedTime(new Date());
	    hospitalCollection = hospitalRepository.save(hospitalCollection);

	    // save location for hospital
	    LocationCollection locationCollection = new LocationCollection();
	    BeanUtil.map(request, locationCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices
		    .geocodeLocation((locationCollection.getLocationName() != null ? locationCollection.getLocationName() : "")
			    + (locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress() : "")
			    + (locationCollection.getCity() != null ? locationCollection.getCity() : "")
			    + (locationCollection.getState() != null ? locationCollection.getState() : "")
			    + (locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);
	    if (locationCollection.getId() == null) {
		locationCollection.setCreatedTime(new Date());
	    }
	    locationCollection.setHospitalId(hospitalCollection.getId());
	    locationCollection.setLocationUId(UniqueIdInitial.LOCATION.getInitial()+DPDoctorUtils.generateRandomId());
	    locationCollection = locationRepository.save(locationCollection);
	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), locationCollection.getId());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    // assign role to doctor
	    RoleCollection roleCollection = new RoleCollection(hospitalAdmin.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    roleCollection = new RoleCollection(locationAdmin.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    roleCollection = new RoleCollection(doctorRole.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setResourceId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm", null, null);
	    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);

	    // user.setPassword(null);

	    if (userCollection.getMobileNumber() != null) {
		// SMSTrackDetail smsTrackDetail =
		// sMSServices.createSMSTrackDetail(doctorCollection.getUserId(),
		// locationCollection.getId(),
		// hospitalCollection.getId(), doctorCollection.getUserId(),
		// "OTP Verification", userCollection.getMobileNumber());
		// sMSServices.sendSMS(smsTrackDetail, false);
	    }

	    List<String> roleIds = new ArrayList<String>();
	    roleIds.add(hospitalAdmin.getId());
	    roleIds.add(locationAdmin.getId());
	    roleIds.add(doctorRole.getId());

	    response = new DoctorSignUp();
	    List<AccessControl> accessControls = assignAllAccessControl(userCollection.getId(), locationCollection.getId(), locationCollection.getHospitalId(),
		    roleIds);
	    List<Role> roles = new ArrayList<Role>();
	    for (AccessControl accessControl : accessControls) {
		Role role = new Role();
		if (accessControl.getRoleOrUserId().equals(hospitalAdmin.getId()))
		    role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
		if (accessControl.getRoleOrUserId().equals(locationAdmin.getId()))
		    role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
		if (accessControl.getRoleOrUserId().equals(doctorRole.getId()))
		    role.setRole(RoleEnum.DOCTOR.getRole());
		BeanUtil.map(accessControl, role);
		roles.add(role);
	    }

	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);
	    Hospital hospital = new Hospital();
	    BeanUtil.map(hospitalCollection, hospital);
	    List<LocationAndAccessControl> locations = new ArrayList<LocationAndAccessControl>();

	    LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
	    BeanUtil.map(locationCollection, locationAndAccessControl);
	    locationAndAccessControl.setRoles(roles);

	    locations.add(locationAndAccessControl);
	    hospital.setLocationsAndAccessControl(locations);
	    response.setHospital(hospital);
	} catch (DuplicateKeyException de) {
	    logger.error(de);
	    throw new BusinessException(ServiceError.Unknown, "Email address already registerd. Please login");
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating doctor");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
	}
	return response;
    }

    private List<AccessControl> assignAllAccessControl(String doctorId, String locationId, String hospitalId, List<String> roleIds) {
	List<AccessControl> accessControls = new ArrayList<AccessControl>();
	try {
	    for (String roleId : roleIds) {
		AccessControl accessControl = new AccessControl();

		accessControl.setHospitalId(hospitalId);
		accessControl.setRoleOrUserId(roleId);
		accessControl.setLocationId(locationId);
		accessControl.setType(Type.ROLE);
		List<AccessModule> accessModules = new ArrayList<AccessModule>();
		for (Module module : Module.values()) {
		    AccessModule accessModule = new AccessModule();
		    accessModule.setModule(module.getModule());
		    List<AccessPermission> accessPermissions = new ArrayList<AccessPermission>();
		    for (AccessPermissionType accessPermissionType : AccessPermissionType.values()) {
			AccessPermission accessPermission = new AccessPermission();
			accessPermission.setAccessPermissionType(accessPermissionType);
			accessPermission.setAccessPermissionValue(true);
			accessPermissions.add(accessPermission);
		    }
		    accessModule.setAccessPermissions(accessPermissions);
		    accessModules.add(accessModule);
		}
		accessControl.setAccessModules(accessModules);
		accessControl = accessControlServices.setAccessControls(accessControl);
		accessControls.add(accessControl);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while assigning access control");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while assigning access control");
	}
	return accessControls;
    }

    @Override
    @Transactional
    public DoctorSignUp doctorHandheld(DoctorSignupHandheldRequest request) {
	DoctorSignUp response = null;
	try {

		if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
			logger.warn("Email Address cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
		 }
		
	    // get role of specified type
	    RoleCollection hospitalAdmin = roleRepository.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());
	    if (hospitalAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection locationAdmin = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
	    if (locationAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection doctorRole = roleRepository.findByRole(RoleEnum.DOCTOR.getRole());
	    if (doctorRole == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge() != null & request.getDob().getAge().getYears() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.InvalidInput, "Incorrect Date of Birth");
	    }
	    char[] salt = DPDoctorUtils.generateSalt();
	    userCollection.setSalt(salt);
	    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
	    for(int i = 0; i < request.getPassword().length; i++)
	        passwordWithSalt[i] = request.getPassword()[i];
	    for(int i = 0; i < salt.length; i++)
	    	passwordWithSalt[i+request.getPassword().length] = salt[i];
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
	    userCollection.setUserName(request.getEmailAddress());
	    userCollection.setTitle("Dr.");
	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	    userCollection.setUserState(UserState.USERSTATEINCOMPLETE);
	    
	    userCollection = userRepository.save(userCollection);
	    // save doctor specific details
	    DoctorCollection doctorCollection = new DoctorCollection();
	    BeanUtil.map(request, doctorCollection);
	    doctorCollection.setUserId(userCollection.getId());
	    doctorCollection.setCreatedTime(new Date());
	    doctorCollection = doctorRepository.save(doctorCollection);

	    response = new DoctorSignUp();
	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);

	} catch (DuplicateKeyException de) {
	    logger.error(de);
	    throw new BusinessException(ServiceError.Unknown, "An account already exists with this email address.Please use another email address to register.");
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating doctor");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
	}
	return response;
    }

    @Override
    @Transactional
    public DoctorSignUp doctorHandheldContinue(DoctorSignupHandheldContinueRequest request, UriInfo uriInfo) {
	DoctorSignUp response = null;

	try {

	    RoleCollection hospitalAdmin = roleRepository.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());
	    if (hospitalAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection locationAdmin = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
	    if (locationAdmin == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    RoleCollection doctorRole = roleRepository.findByRole(RoleEnum.DOCTOR.getRole());
	    if (doctorRole == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    // save user
	    UserCollection userCollection = userRepository.findOne(request.getUserId());
	    userCollection.setUserState(UserState.NOTVERIFIED);
	    userCollection = userRepository.save(userCollection);

	    DoctorCollection doctorCollection = doctorRepository.findByUserId(request.getUserId());
	    if(doctorCollection != null){
	    	doctorCollection.setRegisterNumber(request.getRegisterNumber());
	    	doctorCollection = doctorRepository.save(doctorCollection);
	    }
	    HospitalCollection hospitalCollection = new HospitalCollection();
	    BeanUtil.map(request, hospitalCollection);
	    hospitalCollection.setCreatedTime(new Date());
	    hospitalCollection.setHospitalUId(UniqueIdInitial.HOSPITAL.getInitial()+DPDoctorUtils.generateRandomId());
	    hospitalCollection = hospitalRepository.save(hospitalCollection);

	    // save location for hospital
	    LocationCollection locationCollection = new LocationCollection();
	    BeanUtil.map(request, locationCollection);
	    if (locationCollection.getId() == null) {
		locationCollection.setCreatedTime(new Date());
	    }
	    locationCollection.setLocationUId(UniqueIdInitial.LOCATION.getInitial()+DPDoctorUtils.generateRandomId());
	    locationCollection.setHospitalId(hospitalCollection.getId());
	    locationCollection = locationRepository.save(locationCollection);
	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), locationCollection.getId());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    RoleCollection roleCollection = new RoleCollection(hospitalAdmin.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    roleCollection = new RoleCollection(locationAdmin.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    roleCollection = new RoleCollection(doctorRole.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    roleCollection.setCreatedTime(new Date());
	    roleRepository.save(roleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    
	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setResourceId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm", null ,null);
	    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);

	    // user.setPassword(null);

	    if (userCollection.getMobileNumber() != null) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

		smsTrackDetail.setType("BEFORE_VERIFICATION_TO_DOCTOR");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(userCollection.getId());
		smsDetail.setUserName(userCollection.getFirstName());
		SMS sms = new SMS();
		sms.setSmsText("Welcome "+(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName()+" to Healthcoco.We will contact you shortly to get you started.Download the Healthcoco+ app now:https://healthcoco.com/doctors/app. For queries, please feel free to contact us at support@healthcoco.com.");

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(userCollection.getMobileNumber());
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		sMSServices.sendSMS(smsTrackDetail, true);
	    }

	    List<String> roleIds = new ArrayList<String>();
	    roleIds.add(hospitalAdmin.getId());
	    roleIds.add(locationAdmin.getId());
	    roleIds.add(doctorRole.getId());

	    response = new DoctorSignUp();
	    List<AccessControl> accessControls = assignAllAccessControl(userCollection.getId(), locationCollection.getId(), locationCollection.getHospitalId(),
		    roleIds);
	    List<Role> roles = new ArrayList<Role>();
	    for (AccessControl accessControl : accessControls) {
		Role role = new Role();
		if (accessControl.getRoleOrUserId().equals(hospitalAdmin.getId()))
		    role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
		if (accessControl.getRoleOrUserId().equals(locationAdmin.getId()))
		    role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
		if (accessControl.getRoleOrUserId().equals(doctorRole.getId()))
		    role.setRole(RoleEnum.DOCTOR.getRole());
		BeanUtil.map(accessControl.getAccessModules(), role);
		roles.add(role);
	    }

	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);
	    Hospital hospital = new Hospital();
	    BeanUtil.map(hospitalCollection, hospital);
	    List<LocationAndAccessControl> locations = new ArrayList<LocationAndAccessControl>();

	    LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
	    BeanUtil.map(locationCollection, locationAndAccessControl);
	    locationAndAccessControl.setRoles(roles);

	    locations.add(locationAndAccessControl);
	    hospital.setLocationsAndAccessControl(locations);
	    response.setHospital(hospital);
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating doctor");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
	}
	return response;
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
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
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
	    // user.setPassword(null);
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
	    PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(),
	    		request.getDoctorId(), request.getLocationId(), request.getHospitalId());	
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
		int count = 0;
		List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
			if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
			count++;
			if (userCollection.isSignedUp())
			    throw new BusinessException(ServiceError.NotAcceptable, "Already Signed Up Please Login");
			else{
				if(!response.getIsPatientExistWithMobileNumber())response.setIsPatientExistWithMobileNumber(true);
			}
		    }
		}
		if (count >= Integer.parseInt(patientCount))response.setCanAddNewPatient(false);
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
    public User signupNewPatient(PatientSignupRequestMobile request) {
    User user = null;
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
	userCollection.setUserName(generateUniqueUserNameService.generate(user));
	userCollection = userRepository.save(userCollection);
	user = new User();
	BeanUtil.map(userCollection, user);
	
	// assign roles
	UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
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

	BeanUtil.map(userCollection, user);
	ESPatientDocument esPatientDocument = new ESPatientDocument();
    if (patientCollection.getAddress() != null) {
	BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
    }
    BeanUtil.map(userCollection, esPatientDocument);
    BeanUtil.map(patientCollection, esPatientDocument);
    esPatientDocument.setUserId(userCollection.getId());
    
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
    public List<User> signupAlreadyRegisteredPatient(PatientSignupRequestMobile request) {

	List<User> users = new ArrayList<User>();
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
			    	ESPatientDocument esPatientDocument = new ESPatientDocument();
			        if (patientCollection.getAddress() != null) {
			    	BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
			        }
			        BeanUtil.map(userCollection, esPatientDocument);
			        BeanUtil.map(patientCollection, esPatientDocument);
			        esPatientDocument.setUserId(userCollection.getId());
			        transnationalService.addResource(esPatientDocument.getUserId(), Resource.PATIENT, false);
				    esRegistrationService.addPatient(esPatientDocument);
		    	}
		    User user = new User();
		    BeanUtil.map(userCollection, user);
		    users.add(user);
	    	}
		}
	} else {
	    User user = signupNewPatient(request);
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

    @Override
    @Transactional
    public User adminSignUp(PatientSignUpRequest request) {
	User user = null;
	try {
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.SUPER_ADMIN.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }

	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);

	    if (request.getImage() != null) {
		String path = "profile-image";
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
		userCollection.setImageUrl(imageURLResponse.getImageUrl());
		userCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
	    }
	    char[] salt = DPDoctorUtils.generateSalt();
		userCollection.setSalt(salt);
	    char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
	    for(int i = 0; i < request.getPassword().length; i++)
	        passwordWithSalt[i] = request.getPassword()[i];
	    for(int i = 0; i < salt.length; i++)
	    	passwordWithSalt[i+request.getPassword().length] = salt[i];
	    userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
		userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	    userCollection.setIsVerified(true);
	    userCollection.setIsActive(true);
	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection = userRepository.save(userCollection);

	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleRepository.save(userRoleCollection);

	    user = new User();
	    BeanUtil.map(userCollection, user);
	    // user.setPassword(null);
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
	public Boolean resendVerificationEmail(String emailaddress, UriInfo uriInfo) {
		UserCollection userCollection = null;
		Boolean response = false;
		try {
			Criteria criteria = new Criteria("userName").regex(emailaddress, "i");
			Query query = new Query(); query.addCriteria(criteria);
			List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
			if(userCollections != null && !userCollections.isEmpty())userCollection = userCollections.get(0);

			if (userCollection != null) {
				List<UserLocationCollection> userLocationCollections = userLocationRepository.findByUserId(userCollection.getId());
				UserLocationCollection userLocationCollection = null;
				if(userLocationCollections != null && !userLocationCollections.isEmpty())userLocationCollection = userLocationCollections.get(0);
			    // save token
			    TokenCollection tokenCollection = new TokenCollection();
			    tokenCollection.setResourceId(userLocationCollection.getId());
			    tokenCollection.setCreatedTime(new Date());
			    tokenCollection = tokenRepository.save(tokenCollection);

			    // send activation email
			    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm", null, null);
			    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
				
			    response = true;
		    } else {
			logger.error("User Not Found For The Given User Id");
			throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While sending verification email");
		    throw new BusinessException(ServiceError.Unknown, "Error While sending verification email");
		}
		return response;
	}
}
