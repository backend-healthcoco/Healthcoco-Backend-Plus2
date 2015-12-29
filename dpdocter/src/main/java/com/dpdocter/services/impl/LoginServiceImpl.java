package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.UriInfo;

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
import com.dpdocter.beans.Location;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.User;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
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
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
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

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

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

    /**
     * This method is used for login purpose.
     */
    @Override
    public LoginResponse login(LoginRequest request, UriInfo uriInfo) {
	LoginResponse response = null;
	try {
	    /**
	     * Check if user exist.
	     */
	    UserCollection userCollection = userRepository.findByPasswordAndUserNameIgnoreCase(request.getPassword(), request.getUsername());
	    if (userCollection == null) {
		userCollection = userRepository.findByPasswordAndUserNameIgnoreCase(DPDoctorUtils.getSHA3SecurePassword(request.getPassword().trim()),
			request.getUsername());
		if (userCollection == null) {
		    userCollection = userRepository.findByPasswordAndEmailAddressIgnoreCase(request.getPassword(), request.getUsername());
		}
		if (userCollection == null) {
		    userCollection = userRepository.findByPasswordAndEmailAddressIgnoreCase(DPDoctorUtils.getSHA3SecurePassword(request.getPassword().trim()),
			    request.getUsername());
		}
	    }
	    if (userCollection == null) {
		logger.warn("Invalid username and Password");
		throw new BusinessException(ServiceError.Unknown, "Invalid username and Password");
	    }

	    User user = new User();
	    BeanUtil.map(userCollection, user);
	    /**
	     * Now fetch hospitals and locations for doctor, location admin and
	     * hospital admin. For patient send user details.
	     */
	    List<String> roles = new ArrayList<String>();
	    List<UserRoleCollection> userRoleCollections = userRoleRepository.findByUserId(userCollection.getId());
	    for (UserRoleCollection userRoleCollection : userRoleCollections) {
		RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getRoleId());
		if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())
			|| roleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole())) {
		    if (!userCollection.getIsVerified()) {
			logger.warn("This user is not verified");
			throw new BusinessException(ServiceError.NotAuthorized, "This user is not verified");
		    }
		    if (!userCollection.getIsActive()) {
			logger.warn("This user is not activated");
			throw new BusinessException(ServiceError.NotAuthorized, "This user is not activated");
		    }

		    userCollection.setLastSession(new Date());
		    userCollection = userRepository.save(userCollection);

		    response = new LoginResponse();
		    response.setUser(user);
		    roles.add(roleCollection.getRole());
		    response.setRole(roles);
		    response.setIsTempPassword(userCollection.getIsTempPassword());
		    return response;
		} else {
		    roles.add(roleCollection.getRole());
		    if (userCollection.getUserState() != null && userCollection.getUserState().equals(UserState.USERSTATEINCOMPLETE)) {
			response = new LoginResponse();
			user.setEmailAddress(user.getUserName());
			response.setUser(user);
			response.setRole(roles);
			return response;
		    }

		    if (!userCollection.getIsVerified()) {
			response = new LoginResponse();
			user.setUserState(UserState.NOTVERIFIED);
			response.setUser(user);
			response.setRole(roles);
			return response;
		    }
		    if (!userCollection.getIsActive()) {
			response = new LoginResponse();
			user.setUserState(UserState.NOTACTIVATED);
			response.setUser(user);
			response.setRole(roles);
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
			    Location location = new Location();
			    BeanUtil.map(locationCollection, location);
			    location.setLogoUrl(getFinalImageURL(location.getLogoUrl(), uriInfo));
			    location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl(), uriInfo));
			    location.setImages(getFinalClinicImages(location.getImages(), uriInfo));
			    AccessControl accessControl = accessControlServices.getAccessControls(userCollection.getId(), locationCollection.getId(),
				    locationCollection.getHospitalId());
			    LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
			    locationAndAccessControl.setAccessControl(accessControl);
			    locationAndAccessControl.setLocation(location);

			    if (!checkHospitalId.containsKey(locationCollection.getHospitalId())) {
				hospitalCollection = hospitalRepository.findOne(locationCollection.getHospitalId());
				Hospital hospital = new Hospital();
				BeanUtil.map(hospitalCollection, hospital);
				hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl(), uriInfo));
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
			response.setRole(roles);
		    }
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

    private String getFinalImageURL(String imageURL, UriInfo uriInfo) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;

    }

    private List<ClinicImage> getFinalClinicImages(List<ClinicImage> clinicImages, UriInfo uriInfo) {
	if (clinicImages != null && !clinicImages.isEmpty())
	    for (ClinicImage clinicImage : clinicImages) {
		if (clinicImage.getImageUrl() != null) {
		    clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl(), uriInfo));
		}
		if (clinicImage.getThumbnailUrl() != null) {
		    clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl(), uriInfo));
		}
	    }
	return clinicImages;
    }
}
