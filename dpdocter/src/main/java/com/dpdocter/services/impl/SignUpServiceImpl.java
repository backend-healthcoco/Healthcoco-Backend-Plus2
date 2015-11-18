package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.LocationAndAccessControl;
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
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
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
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
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

    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    /**
     * @param UserTemp
     *            Id
     * @return Boolean This method activates the user account.
     */
    @Override
    public String activateUser(String tokenId) {
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

		userLocationCollection.setIsActive(true);
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
	    if (userCollection.getDob() != null && userCollection.getDob().getAge() < 0) {
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

	    // if (userCollection.getMobileNumber() != null) {
	    // SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	    // smsTrackDetail.setDoctorId(doctorCollection.getUserId());
	    // smsTrackDetail.setHospitalId(hospitalCollection.getId());
	    // smsTrackDetail.setLocationId(locationCollection.getId());
	    //
	    // SMSDetail smsDetail = new SMSDetail();
	    // smsDetail.setPatientId(doctorCollection.getUserId());
	    //
	    // SMS sms = new SMS();
	    // sms.setSmsText("OTP Verification");
	    //
	    // SMSAddress smsAddress = new SMSAddress();
	    // smsAddress.setRecipient(userCollection.getMobileNumber());
	    // sms.setSmsAddress(smsAddress);
	    //
	    // smsDetail.setSms(sms);
	    // List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	    // smsDetails.add(smsDetail);
	    // smsTrackDetail.setSmsDetails(smsDetails);
	    // sMSServices.sendSMS(smsTrackDetail, false);
	    // }

	    response = new DoctorSignUp();

	    if (request.getAccessModules() != null && !request.getAccessModules().isEmpty()) {
		AccessControl accessControl = new AccessControl();
		BeanUtil.map(request, accessControl);
		accessControl.setType(Type.DOCTOR);
		accessControl.setRoleOrUserId(userCollection.getId());
		accessControl = accessControlServices.setAccessControls(accessControl);
		response.setAccessControl(accessControl);
	    }
	    
	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);
	    Hospital hospital = new Hospital();
	    BeanUtil.map(hospitalCollection, hospital);
	    List<LocationAndAccessControl> locations = new ArrayList<LocationAndAccessControl>();
	    Location location = new Location();
	    BeanUtil.map(locationCollection, location);
	    
	    LocationAndAccessControl locationAndAccessControl =  new LocationAndAccessControl();
		locationAndAccessControl.setAccessControl(response.getAccessControl());
		locationAndAccessControl.setLocation(location);
		
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
	    if (userCollection.getDob() != null && userCollection.getDob().getAge() < 0) {
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

	    // if (userCollection.getMobileNumber() != null) {
	    // SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	    // smsTrackDetail.setDoctorId(doctorCollection.getUserId());
	    // smsTrackDetail.setHospitalId(hospitalCollection.getId());
	    // smsTrackDetail.setLocationId(locationCollection.getId());
	    //
	    // SMSDetail smsDetail = new SMSDetail();
	    // smsDetail.setPatientId(doctorCollection.getUserId());
	    //
	    // SMS sms = new SMS();
	    // sms.setSmsText("OTP Verification");
	    //
	    // SMSAddress smsAddress = new SMSAddress();
	    // smsAddress.setRecipient(userCollection.getMobileNumber());
	    // sms.setSmsAddress(smsAddress);
	    //
	    // smsDetail.setSms(sms);
	    // List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	    // smsDetails.add(smsDetail);
	    // smsTrackDetail.setSmsDetails(smsDetails);
	    // sMSServices.sendSMS(smsTrackDetail, false);
	    // }

	    response = new DoctorSignUp();

	    if (request.getAccessModules() != null && !request.getAccessModules().isEmpty()) {
		AccessControl accessControl = new AccessControl();
		BeanUtil.map(request, accessControl);
		accessControl.setType(Type.DOCTOR);
		accessControl.setRoleOrUserId(userCollection.getId());
		accessControl = accessControlServices.setAccessControls(accessControl);
		response.setAccessControl(accessControl);
	    }
	    
	    User user = new User();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, user);
	    user.setEmailAddress(userCollection.getEmailAddress());
	    response.setUser(user);
	    Hospital hospital = new Hospital();
	    BeanUtil.map(hospitalCollection, hospital);
	    List<LocationAndAccessControl> locations = new ArrayList<LocationAndAccessControl>();
	    Location location = new Location();
	    BeanUtil.map(locationCollection, location);
	    
	    LocationAndAccessControl locationAndAccessControl =  new LocationAndAccessControl();
		locationAndAccessControl.setAccessControl(response.getAccessControl());
		locationAndAccessControl.setLocation(location);
		
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
	    if (userCollection.getDob() != null && userCollection.getDob().getAge() < 0) {
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
}
