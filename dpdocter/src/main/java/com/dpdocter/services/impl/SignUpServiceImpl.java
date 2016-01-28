package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
import com.dpdocter.collections.AddressCollection;
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
import com.dpdocter.enums.AccessPermissionType;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.Module;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
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
import com.dpdocter.repository.SMSFormatRepository;
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
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.SignUpService;
import com.dpdocter.sms.services.SMSServices;

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
    
    @Autowired
    private SMSFormatRepository sMSFormatRepository;
    
    @Autowired
    private GenerateUniqueUserNameService generateUniqueUserNameService;

    /**
     * @param UserTemp
     *            Id
     * @return Boolean This method activates the user account.
     */
    @Override
    public String verifyUser(String tokenId) {
	try {
		String startText = "<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN'><html><head><META http-equiv='Content-Type' content='text/html; charset=utf-8'></head><body>"
				+"<div><div style='margin-top:130px'><div style='padding:20px 30px;border-radius:3px;background-color:#fefefe;border:1px solid #f1f1f1;line-height:30px;margin-bottom:30px;font-family:&#39;Open Sans&#39;,sans-serif;margin:0px auto;min-width:200px;max-width:500px'>"
				+"<div align='center'><h2 style='font-size:20px;color:#2c3335;text-align:center;letter-spacing:1px'>Account Verification</h2><br><p style='color:#2c3335;font-size:15px;text-align:left'>";

		String endText = "</p><br><p style='color:#8a6d3b;font-size:15px;text-align:left'>lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum lorem ipsum</p>"
						+"</div></div></div></div></body></html>";

		
	    TokenCollection tokenCollection = tokenRepository.findOne(tokenId);
	    if (tokenCollection == null || tokenCollection.getIsUsed()) {
		return startText+"Link is already Used"+endText;
	    } else {
		UserLocationCollection userLocationCollection = userLocationRepository.findOne(tokenCollection.getResourceId());
		if (userLocationCollection == null) {
		    return startText+"Invalid Url."+endText;
		}
		UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
		userCollection.setIsVerified(true);
		userRepository.save(userCollection);

		userLocationCollection.setIsVerified(true);
		userLocationRepository.save(userLocationCollection);
		tokenCollection.setIsUsed(true);
		tokenRepository.save(tokenCollection);
		return startText+"Account is Verfied"+endText;
	    }

	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while Activating user");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while Activating user");
	}

    }

    @Override
    public Boolean activateUser(String userId) {
	UserCollection userCollection = null;
	Boolean response = false;
	try {
	    userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
		userCollection.setIsActive(true);
		userRepository.save(userCollection);
		response = true;
		if (userCollection.getMobileNumber() != null) { 
	    	SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		    
		    smsTrackDetail.setType("AFTER_VERIFICATION_TO_DOCTOR");
		    SMSDetail smsDetail = new SMSDetail();
		    smsDetail.setUserId(userCollection.getId());
		    smsDetail.setUserName(userCollection.getFirstName());
		    SMS sms = new SMS();
		    sms.setSmsText("Healthcoco "+(userCollection.getTitle()!=null?userCollection.getTitle()+" ":"")+userCollection.getFirstName()+",Your Healthcoco+ account has been activated,for any query please mail us at support@healthcoco.com ");

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

	    } else {
		logger.error("User Not Found For The Given User Id");
		throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error While Verifying User");
	    throw new BusinessException(ServiceError.Forbidden, "Error While Verifying User");
	}
	return response;
    }

    @Override
    public DoctorSignUp doctorSignUp(DoctorSignupRequest request, UriInfo uriInfo) {
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
	    userCollection.setTitle("Dr.");
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
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId(), uriInfo);
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
	} catch (DuplicateKeyException de) {
	    logger.error(de);
	    throw new BusinessException(ServiceError.NotAcceptable, "Email address already registerd. Please login");
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating doctor");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while creating doctor");
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
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while assigning access control");
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
	    userCollection.setTitle("Dr.");
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
	
	    response = new DoctorSignUp();
	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);

	} catch (DuplicateKeyException de) {
	    logger.error(de);
	    throw new BusinessException(ServiceError.NotAcceptable, "Email address already registerd. Please login");
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while creating doctor");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while creating doctor");
	}
	return response;
    }

    @Override
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
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId(), uriInfo);
	    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);

	    // user.setPassword(null);

	    if (userCollection.getMobileNumber() != null) { 
	    	SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		    
		    smsTrackDetail.setType("BEFORE_VERIFICATION_TO_DOCTOR");
		    SMSDetail smsDetail = new SMSDetail();
		    smsDetail.setUserId(userCollection.getId());
		    smsDetail.setUserName(userCollection.getFirstName());
		    SMS sms = new SMS();
		    sms.setSmsText("Healthcoco "+(userCollection.getTitle()!=null?userCollection.getTitle()+" ":"")+userCollection.getFirstName()+",Thank you for signing up with Healthcoco.We will contact you shortly to get you started with Healthcoco+.");

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
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while creating doctor");
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
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while creating user");
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
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
		    List<PatientCollection> patientCollection = patientRepository.findByUserId(userCollection.getId());
		    if (patientCollection != null && userCollection.isSignedUp()) {
			response = true;
			break;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }
    
    @Override
    public boolean checkMobileNumberExistForPatient(String mobileNumber) {
	boolean response = false;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	    if (userCollections != null && !userCollections.isEmpty()) {
		for (UserCollection userCollection : userCollections) {
		    List<PatientCollection> patientCollection = patientRepository.findByUserId(userCollection.getId());
		    if (patientCollection != null) {
			response = true;
			break;
		    }
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

   /* @Override
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
				//List<PatientCollection> patientCollections = patientRepository.findByUserId(userCollection.getId());
				if (patientCollections != null) {
				    for(PatientCollection patientCollection : patientCollections){
				    	patientCollection.setEmailAddress(request.getEmailAddress());
				    }
				}
				userRepository.save(userCollection);
//				patientRepository.save(patientCollection);
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
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while creating user");
	}
	return user;
    
    	return null;}*/

  /**
   * This service implementation checks if 
   * name/names for mobileNumber matches 80% with the queryName.
   * @return boolean 
   */

	@Override
	public boolean verifyPatientBasedOn80PercentMatchOfName(String name, String mobileNumber) {
		boolean checkMatch = false;
	
		if (!checkMobileNumberExistForPatient(mobileNumber)) {
		    logger.error("Cannot verify patient as No patient is registered for mobile number " + mobileNumber);
		    throw new BusinessException(ServiceError.NoRecord, "No patient is registered for mobile number " + mobileNumber);
		} else {
		    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
		    if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
				if(!userCollection.getUserName().equals(userCollection.getEmailAddress()))
			    if (userCollection.isSignedUp() && matchName(userCollection.getFirstName(),name)) {
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
	 *  then unlock all the locked users for the given mobile number.
	 */
	
	@Override
	public boolean unlockPatientBasedOn80PercentMatch(String name,
			String mobileNumber) {
		boolean isUnlocked = false;
		
		if (!checkMobileNumberSignedUp(mobileNumber)) {
		    logger.error("Cannot unlock patient as No patient is registered for mobile number " + mobileNumber);
		    throw new BusinessException(ServiceError.NoRecord, "No patient is registered for mobile number " + mobileNumber);
		} else {
		    List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
		    if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
			    if (!userCollection.isSignedUp() && matchName(userCollection.getFirstName(),name)) {
			    	userCollection.setSignedUp(true);
			    }
			  }
			//This batch update will unlock all the locked users.(make signedup flag =true) 
			userRepository.save(userCollections);
			isUnlocked = true;
		    }
		}
			return isUnlocked;
	}
	
	/**
	 * This utility method checks for 80% match of name.
	 * @param actualName
	 * @param queryName
	 * @return boolean
	 */
	  private boolean matchName(String actualName,String queryName) {
		boolean result = false;
			if (StringUtils.getJaroWinklerDistance(actualName, queryName) >= NAME_MATCH_REQUIRED) {
				result = true;
			}
			return result;
		 }
	  
	  /**
	   * This service implementation will signup the new patient into DB.
	   * This newly created patient will be in unlock state.
	   */
	@Override
	public User signupNewPatient(PatientSignupRequestMobile request) {
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
		userCollection.setSignedUp(true);
		
		User user = new User();
		BeanUtil.map(userCollection, user);
		userCollection.setUserName(generateUniqueUserNameService.generate(user));
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
		
	    SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
//	    smsTrackDetail.setDoctorId(doctorId);
//	    smsTrackDetail.setHospitalId(hospitalId);
//	    smsTrackDetail.setLocationId(locationId);
//	    smsTrackDetail.setType("Verfication");
//	    SMSDetail smsDetail = new SMSDetail();
//	    smsDetail.setUserId(prescriptionCollection.getPatientId());
//	    if (userCollection != null)
//		smsDetail.setUserName(userCollection.getFirstName());
//	    SMS sms = new SMS();
//	    sms.setSmsText("PID : " + patientCollection.getPID() + ", " + prescriptionDetails);// location.getLocationName()+
//
//	    SMSAddress smsAddress = new SMSAddress();
//	    smsAddress.setRecipient(mobileNumber);
//	    sms.setSmsAddress(smsAddress);
//
//	    smsDetail.setSms(sms);
//	    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
//	    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
//	    smsDetails.add(smsDetail);
//	    smsTrackDetail.setSmsDetails(smsDetails);
//	    sMSServices.sendSMS(smsTrackDetail, true);

		BeanUtil.map(userCollection, user);
		return user;
	}
	/**
	 * This Service Impl will signup the already registered patients into the DB.
	 * If 80% match is found for the fname then it will unlock all the patients in the DB.
	 */
	@Override
	public List<User> signupAlreadyRegisteredPatient(PatientSignupRequestMobile request) {
		
		List<User> users = new ArrayList<User>();
		List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
	    if (userCollections != null && !userCollections.isEmpty()) {
	    	if(verifyPatientBasedOn80PercentMatchOfName(request.getName(),request.getMobileNumber())){
	    		for (UserCollection userCollection : userCollections) {
	    				userCollection.setPassword(request.getPassword());
	    				userCollection.setSignedUp(true);
	    				userRepository.save(userCollection);
	    				
	    				User user = new User();
		    			BeanUtil.map(userCollection, user);
		    			users.add(user);
	    		    }
	    		    
	    		  }
	    	}else{//In case if no patient is registered for this mobile number.Then signup new patient.
	    		User user = signupNewPatient(request);
	    		users.add(user);
	    	}
	    return users;
		
	    }
	
}
