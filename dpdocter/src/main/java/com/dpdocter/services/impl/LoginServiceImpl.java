package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Locations;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.User;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
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
import com.dpdocter.services.LoginService;

/**
 * @author veeraj
 */
@Service
public class LoginServiceImpl implements LoginService {

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

	/**
	 * This method is used for login purpose.
	 */
	public LoginResponse login(LoginRequest request) {
		LoginResponse response = null;
		try {
			/**
			 * Check if user exist.
			 */
			UserCollection userCollection = userRepository.findByUserNameAndPass(request.getUsername(), request.getPassword());
			if (userCollection == null) {
				userCollection = userRepository.findByEmailAddressAndPass(request.getUsername(), request.getPassword());
				if (userCollection == null) {
					throw new BusinessException(ServiceError.Unknown, "Invalid username and Password");
				}

			}
			User user = new User();
			BeanUtil.map(userCollection, user);
			/**
			 * Now fetch hospitals and locations for doctor,locationadmin and
			 * hospitaladmin. For patient send user details.
			 */
			List<UserRoleCollection> userRoleCollections = userRoleRepository.findByUserId(userCollection.getId());
			for (UserRoleCollection userRoleCollection : userRoleCollections) {
				RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getRoleId());
				if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())
						|| roleCollection.getRole().equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole())) {

					response = new LoginResponse();
					response.setUser(user);
					response.setRole(roleCollection.getRole());
					response.setTempPassword(userCollection.getTempPassword());
					return response;
				} else {
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
							Locations location = new Locations();
							BeanUtil.map(locationCollection, location);
							if (!checkHospitalId.containsKey(locationCollection.getHospitalId())) {
								hospitalCollection = hospitalRepository.findOne(locationCollection.getHospitalId());
								Hospital hospital = new Hospital();
								BeanUtil.map(hospitalCollection, hospital);
								hospital.getLocations().add(location);
								checkHospitalId.put(locationCollection.getHospitalId(), hospital);
								hospitals.add(hospital);
							} else {
								Hospital hospital = checkHospitalId.get(locationCollection.getHospitalId());
								hospital.getLocations().add(location);
								hospitals.add(hospital);
							}
						}
						response = new LoginResponse();
						user.setEmailAddress(user.getUserName());
						response.setUser(user);
						response.setHospitals(hospitals);
						response.setRole(roleCollection.getRole());
					}
				}
			}
		} catch (BusinessException be) {
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while login");
		}
		return response;
	}

}
