package com.dpdocter.services.impl;

import java.util.ArrayList;
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
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.LoginResponse;
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

    @Value(value = "${IMAGE_PATH}")
    private String imagePath;

    /**
     * This method is used for login purpose.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
	LoginResponse response = null;
	try {
	    /**
	     * Check if user exist.
	     */
	    UserCollection userCollection = userRepository.findByPasswordAndUserNameIgnoreCase(request.getPassword(), request.getUsername());
	    if (userCollection == null) {
		logger.warn("Invalid username and Password");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid username and Password");
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
		    // if (!userCollection.getIsVerified()) {
		    // logger.warn("This user is not verified");
		    // throw new BusinessException(ServiceError.NotAuthorized,
		    // "This user is not verified");
		    // }
		    // if (!userCollection.getIsActive()) {
		    // logger.warn("This user is not activated");
		    // throw new BusinessException(ServiceError.NotAuthorized,
		    // "This user is not activated");
		    // }
		    //
		    // userCollection.setLastSession(new Date());
		    // userCollection = userRepository.save(userCollection);
		    //
		    // response = new LoginResponse();
		    // response.setUser(user);
		    // response.setIsTempPassword(userCollection.getIsTempPassword());
		    // return response;
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
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while login");
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
    public LoginResponse loginPatient(LoginPatientRequest request) {
	LoginResponse response = null;
	try {
	    /**
	     * Check if user exist.
	     */
	    UserCollection userCollection = userRepository.findByPasswordAndMobileNumberIgnoreCase(request.getPassword(), request.getMobileNumber());
	    if (userCollection == null) {
		logger.warn("Invalid mobile Number and Password");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid mobile Number and Password");
	    }
	    User user = new User();
	    BeanUtil.map(userCollection, user);

	    PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(), null, null, null);
	    if (patientCollection != null)
		user.setBloodGroup(patientCollection.getBloodGroup());
	    if (userCollection.getUserState() != null && userCollection.getUserState().equals(UserState.USERSTATEINCOMPLETE)) {
		response = new LoginResponse();
		user.setEmailAddress(user.getUserName());
		response.setUser(user);
		return response;
	    }
	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByUserId(userCollection.getId());

	    for (UserRoleCollection userRoleCollection : userRoleCollections) {
		RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getRoleId());
		if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {
		    userCollection.setLastSession(new Date());
		    userCollection = userRepository.save(userCollection);

		    response = new LoginResponse();
		    response.setUser(user);
		    response.setIsTempPassword(userCollection.getIsTempPassword());
		    return response;
		}
	    }
	} catch (BusinessException be) {
	    logger.error(be);
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while login");
	}
	return response;
    }

    @Override
    public User adminLogin(LoginPatientRequest request) {
	User response = null;
	try {
	    UserCollection userCollection = userRepository.findByPasswordAndMobileNumberIgnoreCase(request.getPassword(), request.getMobileNumber());
	    if (userCollection == null) {
		logger.warn("Invalid mobile Number and Password");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid mobile Number and Password");
	    }
	    User user = new User();
	    BeanUtil.map(userCollection, user);

	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByUserId(userCollection.getId());

	    for (UserRoleCollection userRoleCollection : userRoleCollections) {
		RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getRoleId());
		if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole())) {
		    userCollection.setLastSession(new Date());
		    userCollection = userRepository.save(userCollection);
		    response = new User();
		    BeanUtil.map(userCollection, response);
		    return response;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while login");
	    throw new BusinessException(ServiceError.Forbidden, "Error occured while login");
	}
	return response;
    }
}
