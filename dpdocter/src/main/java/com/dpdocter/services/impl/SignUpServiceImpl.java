package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.AccessModule;
import com.dpdocter.beans.AccessPermission;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.AccessPermissionType;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.Module;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.DoctorContactsRepository;
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
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.sms.services.SMSServices;
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
    private AddressRepository addressRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorContactsRepository doctorContactsRepository;

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

    private final double NAME_MATCH_REQUIRED = 0.80;

    /**
     * @param UserTemp
     *            Id
     * @return Boolean This method activates the user account.
     */
    @Override
    public String verifyUser(String tokenId) {
	try {
	    TokenCollection tokenCollection = tokenRepository.findOne(tokenId);
	    if (tokenCollection == null || tokenCollection.getIsUsed()) {
		return "Link is already Used";
	    } else {
		UserLocationCollection userLocationCollection = userLocationRepository.findOne(tokenCollection.getUserLocationId());
		if (userLocationCollection == null) {
		    return "Invalid Url.";
		}
		UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
		userCollection.setIsActive(true);
		userRepository.save(userCollection);

		userLocationCollection.setIsActivate(true);
		userLocationRepository.save(userLocationCollection);
		tokenCollection.setIsUsed(true);
		tokenRepository.save(tokenCollection);
		return "Account is Activated";
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
    public Boolean activateUser(String userId) {
	UserCollection userCollection = null;
	Boolean response = false;
	try {
	    userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
		userCollection.setIsVerified(true);
		userRepository.save(userCollection);
		response = true;
	    } else {
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
    public DoctorSignUp doctorSignUp(DoctorSignupRequest request) {
	DoctorSignUp response = null;

	try {
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
	    if (request.getDob() != null && request.getDob().getAge() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.NotAcceptable, "Incorrect Date of Birth");
	    }
	    userCollection.setUserName(request.getEmailAddress());
	    if (request.getImage() != null) {
		String path = "profile-pic";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		userCollection.setImageUrl(imageurl);

		String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		userCollection.setThumbnailUrl(thumbnailUrl);
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
	    // assign role to doctor
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), hospitalAdmin.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), locationAdmin.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    // Save hospital
	    HospitalCollection hospitalCollection = new HospitalCollection();
	    BeanUtil.map(request, hospitalCollection);
	    hospitalCollection.setCreatedTime(new Date());
	    hospitalCollection = hospitalRepository.save(hospitalCollection);

	    // save location for hospital
	    LocationCollection locationCollection = new LocationCollection();
	    BeanUtil.map(request, locationCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
	    		(locationCollection.getLocationName() != null ? locationCollection.getLocationName() : "" )+
	    		(locationCollection.getStreetAddress()!= null ? locationCollection.getStreetAddress() : "" )+
	    		(locationCollection.getCity()!= null ? locationCollection.getCity() : "")+
	    		(locationCollection.getState() != null ? locationCollection.getState() : "")+
	    		(locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));
	    
	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);
	    if (locationCollection.getId() == null) {
		locationCollection.setCreatedTime(new Date());
	    }
	    locationCollection.setHospitalId(hospitalCollection.getId());
	    locationCollection = locationRepository.save(locationCollection);
	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), locationCollection.getId());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setUserLocationId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId());
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
	    List<AccessControl> accessControls = assignAllAccessControl(userCollection.getId(), locationCollection.getId(), locationCollection.getHospitalId(), roleIds);
	    List<Role> roles = new ArrayList<Role>();
	    for(AccessControl accessControl : accessControls){
	    	Role role = new Role();
	    	if(accessControl.getRoleOrUserId().equals(hospitalAdmin.getId()))role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
	    	if(accessControl.getRoleOrUserId().equals(locationAdmin.getId()))role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
	    	if(accessControl.getRoleOrUserId().equals(doctorRole.getId()))role.setRole(RoleEnum.DOCTOR.getRole());
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
	    for(String roleId : roleIds){
	    	AccessControl accessControl= new AccessControl();
	    	
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
    public DoctorSignUp doctorHandheld(DoctorSignupHandheldRequest request) {
	DoctorSignUp response = null;
	try {

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
	    if (request.getDob() != null && request.getDob().getAge() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.NotAcceptable, "Incorrect Date of Birth");
	    }
	    userCollection.setUserName(request.getEmailAddress());

	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection.setUserState(UserState.USERSTATEINCOMPLETE);
	    userCollection = userRepository.save(userCollection);
	    // save doctor specific details
	    DoctorCollection doctorCollection = new DoctorCollection();
	    BeanUtil.map(request, doctorCollection);
	    doctorCollection.setUserId(userCollection.getId());
	    doctorCollection.setCreatedTime(new Date());
	    doctorCollection = doctorRepository.save(doctorCollection);
	    // assign role to doctor
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), hospitalAdmin.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), locationAdmin.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);
	    userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    response = new DoctorSignUp();
	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);

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
    public DoctorSignUp doctorHandheldContinue(DoctorSignupHandheldContinueRequest request) {
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

	    userCollection.setUserState(UserState.USERSTATECOMPLETE);
	    userCollection = userRepository.save(userCollection);

	    HospitalCollection hospitalCollection = new HospitalCollection();
	    BeanUtil.map(request, hospitalCollection);
	    hospitalCollection.setCreatedTime(new Date());
	    hospitalCollection = hospitalRepository.save(hospitalCollection);

	    // save location for hospital
	    LocationCollection locationCollection = new LocationCollection();
	    BeanUtil.map(request, locationCollection);
	    if (locationCollection.getId() == null) {
		locationCollection.setCreatedTime(new Date());
	    }
	    locationCollection.setHospitalId(hospitalCollection.getId());
	    locationCollection = locationRepository.save(locationCollection);
	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), locationCollection.getId());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setUserLocationId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId());
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
	    List<AccessControl> accessControls = assignAllAccessControl(userCollection.getId(), locationCollection.getId(), locationCollection.getHospitalId(), roleIds);
	    List<Role> roles = new ArrayList<Role>();
	    for(AccessControl accessControl : accessControls){
	    	Role role = new Role();
	    	if(accessControl.getRoleOrUserId().equals(hospitalAdmin.getId()))role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
	    	if(accessControl.getRoleOrUserId().equals(locationAdmin.getId()))role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
	    	if(accessControl.getRoleOrUserId().equals(doctorRole.getId()))role.setRole(RoleEnum.DOCTOR.getRole());
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
    public User patientSignUp(PatientSignUpRequest request) {
	User user = null;
	try {
	    // get role of specified type
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	    if (roleCollection == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defined properly");
	    }
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.NotAcceptable, "Incorrect Date of Birth");
	    }
	    if (request.getImage() != null) {
		String path = "profile-image";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		userCollection.setImageUrl(imageurl);

		String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		userCollection.setThumbnailUrl(thumbnailUrl);
	    }
	    userCollection.setIsActive(true);
	    userCollection.setCreatedTime(new Date());
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection = userRepository.save(userCollection);

	    // assign roles
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleRepository.save(userRoleCollection);
	    // save address
	    AddressCollection addressCollection = null;
	    if (request.getAddress() != null) {
		addressCollection = new AddressCollection();
		BeanUtil.map(request.getAddress(), addressCollection);
		addressCollection.setUserId(userCollection.getId());
		addressCollection.setCreatedTime(new Date());
		addressCollection = addressRepository.save(addressCollection);
	    }

	    // save Patient Info
	    PatientCollection patientCollection = new PatientCollection();
	    BeanUtil.map(request, patientCollection);
	    patientCollection.setUserId(userCollection.getId());
	    Date createdTime = new Date();
	    patientCollection.setCreatedTime(createdTime);
	    if (addressCollection != null) {
		patientCollection.setAddressId(addressCollection.getId());
	    }
	    patientCollection = patientRepository.save(patientCollection);

	    // save token
	    // TokenCollection tokenCollection = new TokenCollection();
	    // tokenCollection.setUserLocationId(userCollection.getId());
	    // tokenCollection.setCreatedTime(new Date());
	    // tokenCollection = tokenRepository.save(tokenCollection);
	    //
	    // // send activation email
	    // String body =
	    // mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(),
	    // userCollection.getFirstName(),
	    // userCollection.getMiddleName(), userCollection.getLastName(),
	    // tokenCollection.getId());
	    // mailService.sendEmail(userCollection.getEmailAddress(),
	    // signupSubject, body, null);
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
    public User patientProfilePicChange(PatientProfilePicChangeRequest request) {
	User user = null;
	try {
	    UserCollection userCollection = userRepository.findByUserName(request.getUsername());
	    if (userCollection == null) {
		logger.warn("User not found");
		throw new BusinessException(ServiceError.NotFound, "User not found");
	    } else {
		if (request.getImage() != null) {
		    String path = "profile-image";
		    // save image
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		    userCollection.setImageUrl(imageurl);

		    String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		    userCollection.setThumbnailUrl(thumbnailUrl);
		    userCollection = userRepository.save(userCollection);

		    user = new User();
		    BeanUtil.map(userCollection, user);
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
    public boolean checkMobileNumberSignedUp(String mobileNumber) {
	boolean response = false;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
		    PatientCollection patientCollection = patientRepository.findByUserId(userCollection.getId());
		    if (patientCollection != null && userCollection.isSignedUp()) {
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

    @Override
    public User patientSignUp(PatientSignupRequestMobile request) {
	User user = null;
	try {
	    if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
		if (checkMobileNumberSignedUp(request.getMobileNumber())) {
		    logger.warn("Mobile Number Already Registered");
		    throw new BusinessException(ServiceError.NotAcceptable, "Mobile Number Already Registered");
		} else {
		    List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
		    if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
			    if (matchName(userCollection.getFirstName(), userCollection.getLastName(), request.getName())) {
				userCollection.setPassword(request.getPassword());
				userCollection.setEmailAddress(request.getEmailAddress());
				PatientCollection patientCollection = patientRepository.findByUserId(userCollection.getId());
				if (patientCollection != null) {
				    patientCollection.setEmailAddress(request.getEmailAddress());
				}
				userRepository.save(userCollection);
				patientRepository.save(patientCollection);
				user = new User();
				BeanUtil.map(userCollection, user);
				break;
			    }
			}
		    } else {
			// get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
			if (roleCollection == null) {
			    logger.warn("Role Collection in database is either empty or not defind properly");
			    throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defined properly");
			}
			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			userCollection.setFirstName(request.getName());
			userCollection.setIsActive(true);
			userCollection.setCreatedTime(new Date());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection = userRepository.save(userCollection);

			// assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
			userRoleRepository.save(userRoleCollection);

			// save Patient Info
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setFirstName(request.getName());
			patientCollection.setUserId(userCollection.getId());
			patientCollection.setCreatedTime(new Date());
			patientCollection = patientRepository.save(patientCollection);

			user = new User();
			BeanUtil.map(userCollection, user);
		    }
		}
	    } else {
		logger.error("Mobile Number Cannot Be Empty!");
		throw new BusinessException(ServiceError.NotAcceptable, "Mobile Number Cannot Be Empty!");
	    }

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

    private boolean matchName(String firstName, String lastName, String queryName) {
	boolean result = false;
	if (StringUtils.getJaroWinklerDistance(firstName, queryName) >= NAME_MATCH_REQUIRED
		|| StringUtils.getJaroWinklerDistance(lastName, queryName) >= NAME_MATCH_REQUIRED) {
	    result = true;
	}
	return result;
    }
}
