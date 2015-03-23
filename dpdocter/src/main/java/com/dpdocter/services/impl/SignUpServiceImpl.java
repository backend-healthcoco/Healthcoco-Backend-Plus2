package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Locations;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.DoctorCollection;
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
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
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

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;


	/**
	 * @param UserTemp Id
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

	public DoctorSignUp doctorSignUp(DoctorSignupRequest request) {
		DoctorSignUp response = null;
		
		try {
			//get role of specified type
			RoleCollection hospitalAdmin = roleRepository.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());
			if(hospitalAdmin == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			RoleCollection locationAdmin = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
			if(locationAdmin == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			RoleCollection doctorRole = roleRepository.findByRole(RoleEnum.DOCTOR.getRole());
			if(doctorRole == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			//save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			userCollection.setUserName(request.getEmailAddress());
			if(request.getImage() != null){
				String path = "profile-pic";
				//save image
				String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(),path);
				userCollection.setImageUrl(imageurl);
			}
			userCollection = userRepository.save(userCollection);
			//save doctor specific details
			DoctorCollection doctorCollection = new DoctorCollection();
			BeanUtil.map(request, doctorCollection);
			doctorCollection.setUserId(userCollection.getId());
			doctorCollection = doctorRepository.save(doctorCollection);
			//assign role to doctor
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), hospitalAdmin.getId());
			userRoleRepository.save(userRoleCollection);
			userRoleCollection = new UserRoleCollection(userCollection.getId(), locationAdmin.getId());
			userRoleRepository.save(userRoleCollection);
			userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId());
			userRoleRepository.save(userRoleCollection);
			//Save hospital
			HospitalCollection hospitalCollection = new HospitalCollection();
			BeanUtil.map(request, hospitalCollection);
			hospitalCollection = hospitalRepository.save(hospitalCollection);
			
			//save location for hospital
			LocationCollection locationCollection = new LocationCollection();
			BeanUtil.map(request, locationCollection);
			locationCollection.setHospitalId(hospitalCollection.getId());
			locationCollection = locationRepository.save(locationCollection);
			//save user location.
			UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), locationCollection.getId());
			userLocationRepository.save(userLocationCollection);
			//send activation email
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(), userCollection.getMiddleName(), userCollection.getLastName());
			mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
			response = new DoctorSignUp();
			User user = new User();
			userCollection.setPassword(null);
			BeanUtil.map(userCollection, user);
			user.setEmailAddress(userCollection.getEmailAddress());
			response.setUser(user);
			Hospital hospital = new Hospital();
			BeanUtil.map(hospitalCollection, hospital);
			List<Locations> locations = new ArrayList<Locations>();
			Locations location = new Locations();
			BeanUtil.map(locationCollection, location);
			locations.add(location);
			hospital.setLocations(locations);
			response.setHospital(hospital);
			//user.setPassword(null);
		} catch(BusinessException be){
			throw be;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
		}
		return response;
	}

	public User patientSignUp(PatientSignUpRequest request) {
		User user = null;
		try{
			//get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
			if(roleCollection == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			//save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			userCollection = userRepository.save(userCollection);

			//assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
			userRoleRepository.save(userRoleCollection);
			//save address
			AddressCollection addressCollection = null;
			if(request.getAddress() != null){
			addressCollection = new AddressCollection();
			BeanUtil.map(request.getAddress(), addressCollection);
			addressCollection.setUserId(userCollection.getId());
			addressCollection = addressRepository.save(addressCollection);
			}
			
			//save Patient Info
			PatientCollection patientCollection  = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setUserId(userCollection.getId());
			if(addressCollection != null){
				patientCollection.setAddressId(addressCollection.getId());
			}
			patientCollection = patientRepository.save(patientCollection);

			//send activation email
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(), userCollection.getMiddleName(), userCollection.getLastName());
			mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
			user = new User();
			BeanUtil.map(userCollection, user);
			//user.setPassword(null);
		}catch(BusinessException be){
			throw be;
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating user");
		}
		return user;
	}

	@Override
	public Boolean checkUserNameExist(String username) {
		try {
			UserCollection userCollection = userRepository.findByUserName(username);
			if(userCollection == null){
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public Boolean checkMobileNumExist(String mobileNum) {
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNum);
			if(userCollections != null){
				if(!userCollections.isEmpty()){
					return true;
				}else{
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}

	@Override
	public Boolean checkEmailAddressExist(String email) {
		try {
			List<UserCollection> userCollections = userRepository.findByEmailAddress(email);
			if(userCollections != null){
				if(!userCollections.isEmpty()){
					return true;
				}else{
					return false;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

}
