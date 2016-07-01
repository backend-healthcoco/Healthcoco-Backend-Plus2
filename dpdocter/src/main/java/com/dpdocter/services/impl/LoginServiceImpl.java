package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.LoginService;

import common.util.web.DPDoctorUtils;

/**
 * @author veeraj
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private AccessControlServices accessControlServices;

    @Autowired
    private PatientRepository patientRepository;

    @Value(value = "${image.path}")
    private String imagePath;

    @Value(value = "${Login.login}")
    private String login;

    @Value(value = "${Login.loginPatient}")
    private String loginPatient;
   
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value(value = "${Signup.role}")
    private String role;
    
    /**
     * This method is used for login purpose.
     */
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
	LoginResponse response = null;
	try {
	    /**
	     * Check if user exist.
	     */
		Criteria criteria = new Criteria("userName").regex(request.getUsername(), "i");
		Query query = new Query(); query.addCriteria(criteria);
		List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
		UserCollection userCollection = null;
		if(userCollections != null && !userCollections.isEmpty())userCollection = userCollections.get(0);

	    if (userCollection == null) {
		logger.warn(login);
		throw new BusinessException(ServiceError.InvalidInput, login);
	    }
	    else{
	    	char[] salt = userCollection.getSalt();
	        char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
		    for(int i = 0; i < request.getPassword().length; i++)
		        passwordWithSalt[i] = request.getPassword()[i];
		    for(int i = 0; i < salt.length; i++)
		    	passwordWithSalt[i+request.getPassword().length] = salt[i];
		    if(!Arrays.equals(userCollection.getPassword(), DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
		    	logger.warn(login);
				throw new BusinessException(ServiceError.InvalidInput, login);
		    }
	    }
	    User user = new User();
	    BeanUtil.map(userCollection, user);
	    /**
	     * Now fetch hospitals and locations for doctor, location admin and
	     * hospital admin. For patient send user details.
	     */
	    if (userCollection.getUserState() != null && userCollection.getUserState().equals(UserState.USERSTATEINCOMPLETE)) {
		response = new LoginResponse();
		user.setEmailAddress(user.getUserName());
		response.setUser(user);
		return response;
	    }
	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByUserId(userCollection.getId());

	    for (UserRoleCollection userRoleCollection : userRoleCollections) {
		RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getRoleId());
		if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())
			|| roleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole())) {
		    logger.warn("Invalid User");
		    throw new BusinessException(ServiceError.NotAuthorized, "Invalid User");

		} else {

		    if (!userCollection.getIsVerified()) {
			response = new LoginResponse();
			user.setUserState(UserState.NOTVERIFIED);
			response.setUser(user);
			return response;
		    }
		    if (!userCollection.getIsActive()) {
			response = new LoginResponse();
			user.setUserState(UserState.NOTACTIVATED);
			response.setUser(user);
			return response;
		    }

		    userCollection.setLastSession(new Date());
		    userCollection = userRepository.save(userCollection);
		    List<UserLocationCollection> userLocationCollections = userLocationRepository.findByUserId(userCollection.getId());
		    if (userLocationCollections != null) {
			@SuppressWarnings("unchecked")
			Collection<String> locationIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("locationId"));
			@SuppressWarnings("unchecked")
			List<LocationCollection> locationCollections = IteratorUtils.toList(locationRepository.findAll(locationIds).iterator());
			List<Hospital> hospitals = new ArrayList<Hospital>();
			Map<String, Hospital> checkHospitalId = new HashMap<String, Hospital>();
			for (LocationCollection locationCollection : locationCollections) {
			    HospitalCollection hospitalCollection = null;
			    LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
			    BeanUtil.map(locationCollection, locationAndAccessControl);
			    locationAndAccessControl.setLogoUrl(getFinalImageURL(locationAndAccessControl.getLogoUrl()));
			    locationAndAccessControl.setLogoThumbnailUrl(getFinalImageURL(locationAndAccessControl.getLogoThumbnailUrl()));
			    locationAndAccessControl.setImages(getFinalClinicImages(locationAndAccessControl.getImages()));
			    List<Role> roles = null;
			    for (UserRoleCollection collection : userRoleCollections) {
				RoleCollection roleCollection2 = roleRepository.find(collection.getRoleId(), locationCollection.getId(),
					locationCollection.getHospitalId());
				if (roleCollection2 != null) {
				    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection2.getId(), locationCollection.getId(),
					    locationCollection.getHospitalId());

				    Role role = new Role();
				    BeanUtil.map(roleCollection2, role);
				    role.setAccessModules(accessControl.getAccessModules());

				    if (roles == null)
					roles = new ArrayList<Role>();
				    roles.add(role);
				}
			    }

			    locationAndAccessControl.setRoles(roles);

			    if (!checkHospitalId.containsKey(locationCollection.getHospitalId())) {
				hospitalCollection = hospitalRepository.findOne(locationCollection.getHospitalId());
				Hospital hospital = new Hospital();
				BeanUtil.map(hospitalCollection, hospital);
				hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
				hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
				checkHospitalId.put(locationCollection.getHospitalId(), hospital);
				hospitals.add(hospital);
			    } else {
				Hospital hospital = checkHospitalId.get(locationCollection.getHospitalId());
				hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
				hospitals.add(hospital);
			    }
			}
			response = new LoginResponse();
			user.setEmailAddress(user.getUserName());
			response.setUser(user);
			response.setHospitals(hospitals);

		    }
		    break;
		}
	    }
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while login");
	}
	return response;
    }

    private String getFinalImageURL(String imageURL) {
	if (imageURL != null) {
	    return imagePath + imageURL;
	} else
	    return null;

    }

    private List<ClinicImage> getFinalClinicImages(List<ClinicImage> clinicImages) {
	if (clinicImages != null && !clinicImages.isEmpty())
	    for (ClinicImage clinicImage : clinicImages) {
		if (clinicImage.getImageUrl() != null) {
		    clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
		}
		if (clinicImage.getThumbnailUrl() != null) {
		    clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
		}
	    }
	return clinicImages;
    }

    @Override
    @Transactional
    public List<RegisteredPatientDetails>  loginPatient(LoginPatientRequest request) {
    	List<RegisteredPatientDetails> response = null;
	try {
		Criteria criteria = new Criteria("mobileNumber").is(request.getMobileNumber());
		Query query = new Query(); query.addCriteria(criteria);
		List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
		
		for(UserCollection userCollection : userCollections){
			if(userCollection.getEmailAddress() != null){
				if(!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())){
					RegisteredPatientDetails user = new RegisteredPatientDetails();
				    char[] salt = userCollection.getSalt();
					char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
					for(int i = 0; i < request.getPassword().length; i++)
						        passwordWithSalt[i] = request.getPassword()[i];
					for(int i = 0; i < salt.length; i++)
						    	passwordWithSalt[i+request.getPassword().length] = salt[i];
					if(!Arrays.equals(userCollection.getPassword(), DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
						    	logger.warn(login);
								throw new BusinessException(ServiceError.InvalidInput, login);
				    }
				    PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(),null,null, null);
				    if(patientCollection != null){
				    	Patient patient = new Patient();
				    	BeanUtil.map(patientCollection, patient);
				    	BeanUtil.map(patientCollection, user);
				    	BeanUtil.map(userCollection, user);
				    	patient.setPatientId(patientCollection.getUserId());
				    	user.setPatient(patient);
				    }
				    user.setUserId(userCollection.getId());
				    if(response == null)response = new ArrayList<RegisteredPatientDetails>();
				    response.add(user);
				}
			}else{
				RegisteredPatientDetails user = new RegisteredPatientDetails();
				char[] salt = userCollection.getSalt();
				char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
				for(int i = 0; i < request.getPassword().length; i++)
					        passwordWithSalt[i] = request.getPassword()[i];
				for(int i = 0; i < salt.length; i++)
					    	passwordWithSalt[i+request.getPassword().length] = salt[i];
				if(!Arrays.equals(userCollection.getPassword(), DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
					    	logger.warn(login);
							throw new BusinessException(ServiceError.InvalidInput, login);
			    }
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(),null,null, null);
				if(patientCollection != null){
					Patient patient = new Patient();
			    	BeanUtil.map(patientCollection, patient);
			    	BeanUtil.map(patientCollection, user);
			    	BeanUtil.map(userCollection, user);
			    	patient.setPatientId(patientCollection.getUserId());
			    	user.setPatient(patient);
			    }
			    user.setUserId(userCollection.getId());
				if(response == null)response = new ArrayList<RegisteredPatientDetails>();
				response.add(user);
			}		
		}
	    if (response == null) {
		logger.warn(loginPatient);
		throw new BusinessException(ServiceError.InvalidInput, loginPatient);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while login");
	}
	return response;
    }

    @Override
    @Transactional
    public User adminLogin(LoginPatientRequest request) {

    User response = null;
    try {
    	RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.SUPER_ADMIN.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByRoleId(roleCollection.getId());
	    @SuppressWarnings("unchecked")
	    Collection<String> userIds = CollectionUtils.collect(userRoleCollections, new BeanToPropertyValueTransformer("userId")); 
	    
		Criteria criteria = new Criteria("mobileNumber").is(request.getMobileNumber()).and("id").in(userIds);
		Query query = new Query(); query.addCriteria(criteria);
		List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
//		UserCollection userCollection = null;
//		if(userCollections != null && !userCollections.isEmpty())userCollection = userCollections.get(0);
	    if (userCollections == null || userCollections.isEmpty()) {
		logger.warn("Invalid mobile Number and Password");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid mobile Number and Password");
	    }else{
	    	
	    	for(UserCollection userCollection : userCollections){
	    		char[] salt = userCollection.getSalt();
				char[] passwordWithSalt = new char[request.getPassword().length + salt.length]; 
				for(int i = 0; i < request.getPassword().length; i++)
					        passwordWithSalt[i] = request.getPassword()[i];
				for(int i = 0; i < salt.length; i++)
					    	passwordWithSalt[i+request.getPassword().length] = salt[i];
				if(Arrays.equals(userCollection.getPassword(), DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
					userCollection.setLastSession(new Date());
				    userCollection = userRepository.save(userCollection);
				    response = new User();
				    BeanUtil.map(userCollection, response);
				    return response;
			    }
	    	}
	    }
	   if(response == null){
		   logger.warn(login);
			throw new BusinessException(ServiceError.Unknown, login);
	   }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while login");
	}
	return response;
	}
}
