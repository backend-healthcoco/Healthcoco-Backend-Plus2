package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Locations;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.DocterCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.DocterRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.SignUpService;

/**
 * @author veeraj
 */
@Service
public class SignUpServiceImpl implements SignUpService{

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
	private DocterRepository docterRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;
	
	/**
	 * sign up for patient and docter
	 * @param user : user obj
	 * @param signUpType : signuptype ex:DOCTER,PATIENT
	 * @return User : save user
	 */
	public User signUp(User user,String signUpType) {
		try {
			//get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(signUpType);
			if(roleCollection == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			//save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(user, userCollection);
			//generate a unique username for each user.
			String userName = generateUniqueUserNameService.generate(user);
			userCollection.setUserName(userName);
	     	userCollection = userRepository.save(userCollection);
	     	user.setId(userCollection.getId());
	     	//save user role mapping
			userRoleRepository.save(new UserRoleCollection(userCollection.getId(),roleCollection.getId()));
			//save hospital for the user
			HospitalCollection hospitalCollection = new HospitalCollection();
			BeanUtil.map(user.getHospital(), hospitalCollection);
			hospitalCollection = hospitalRepository.save(hospitalCollection);
			user.getHospital().setId(hospitalCollection.getId());
			//save locations for the  hospital       
			for(Locations location : user.getHospital().getLocations()){
				LocationCollection locationCollection = new LocationCollection();
				BeanUtil.map(location, locationCollection);
				locationCollection = locationRepository.save(locationCollection);
				location.setId(locationCollection.getId());
				//save user location mapping
				userLocationRepository.save(new UserLocationCollection(userCollection.getId(), locationCollection.getId()));
			}
			//save address for user
			AddressCollection addressCollection = new AddressCollection();
			BeanUtil.map(user.getAddress(), addressCollection);
			addressCollection.setUserId(userCollection.getId());
			addressCollection = addressRepository.save(addressCollection);
			user.getAddress().setId(addressCollection.getId());
			//save docter if docter signup
			if(signUpType.equals(RoleEnum.DOCTER.getRole())){
				DocterCollection docterCollection = new DocterCollection();
				BeanUtil.map(user.getDocter(), docterCollection);
				docterCollection.setUserId(userCollection.getId());
				docterCollection = docterRepository.save(docterCollection);
				user.getDocter().setId(docterCollection.getId());
			}//save patient if patient signup
			else if(signUpType.equals(RoleEnum.PATIENT.getRole())){
				PatientCollection patientCollection = new PatientCollection();
				BeanUtil.map(user.getPatient(), patientCollection);
				patientCollection.setUserId(userCollection.getId());
				patientCollection = patientRepository.save(patientCollection);
				user.getPatient().setId(patientCollection.getId());
			}
			
		}catch(BusinessException be){
			throw be;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return user;
	}
	/**
	 * @param User Id
	 * @return Boolean
	 * This method activates the user account.
	 */
	public Boolean activateUser(String userId) {
		try {
			UserCollection userCollection = userRepository.findOne(userId);
			if(userCollection == null){
				throw new BusinessException(ServiceError.NotVerified, "Invalid Url.");
			}
			userCollection.setIsActive(true);
			userRepository.save(userCollection);
			return true;
		} catch(BusinessException be){
			throw be;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while Activating user");
		}
		
	}
	

}
