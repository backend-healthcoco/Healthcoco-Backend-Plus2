package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.DoctorContactCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.RegistrationService;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private PatientAdmissionRepository patientAdmissionRepository;
	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;
	@Autowired
	private MailService mailService;
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	/*@Autowired
	private GroupRepository groupRepository;*/
	@Autowired
	private PatientGroupRepository patientGroupRepository;
	@Autowired
	private DoctorContactsRepository doctorContactsRepository;
	
	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Override
	public User checkIfPatientExist(PatientRegistrationRequest request) {
		try {
			UserCollection userCollection = userRepository.checkPatient(
					request.getFirstName(), request.getMiddleName(),
					request.getLastName(), request.getEmailAddress(),
					request.getMobileNumber());
			if (userCollection != null) {
				User user = new User();
				BeanUtil.map(userCollection, user);
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return null;
		
	}

	@Override
	public RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request) {
		RegisteredPatientDetails registeredPatientDetails = null;
		try {
			//get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
			if(roleCollection == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			//save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			User user = new User();
			BeanUtil.map(request, user);
			String uniqueUserName = generateUniqueUserNameService.generate(user);
			userCollection.setUserName(uniqueUserName);
			userCollection.setPassword(generateRandomAlphanumericString(6));
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
			
			//save Patient visit.
			PatientAdmissionCollection patientAdmissionCollection = new PatientAdmissionCollection();
			BeanUtil.map(request, patientAdmissionCollection);
			patientAdmissionCollection.setUserId(userCollection.getId());
			patientAdmissionCollection.setPatientId(patientCollection.getId());
			patientAdmissionCollection.setDoctorId(request.getDoctorId());
			patientAdmissionRepository.save(patientAdmissionCollection);
			
			//assign groups
			if(request.getGroups()!= null){
				for(String group : request.getGroups()){
					PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
					patientGroupCollection.setGroupId(group);
					patientGroupCollection.setPatientId(patientCollection.getId());
					patientGroupRepository.save(patientGroupCollection);
				}
			}
			//add into doctor contact
			if(request.getDoctorId() != null){
				DoctorContactCollection doctorContactCollection = new DoctorContactCollection();
				doctorContactCollection.setDoctorId(request.getDoctorId());
				doctorContactCollection.setContactId(patientCollection.getId());
				doctorContactsRepository.save(doctorContactCollection);
			}
			if(patientCollection.getEmailAddress() != null){
				//send activation email
				String body = mailBodyGenerator.generatePatientRegistrationEmailBody(userCollection.getUserName(),userCollection.getPassword(), userCollection.getFirstName(), userCollection.getLastName());
				mailService.sendEmail(patientCollection.getEmailAddress(), signupSubject, body, null);
			}
			//send SMS logic
			//TODO
		    registeredPatientDetails = new RegisteredPatientDetails();
			BeanUtil.map(userCollection, registeredPatientDetails);
			registeredPatientDetails.setUserId(userCollection.getId());
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			patient.setPatientId(patientCollection.getId());
			registeredPatientDetails.setPatient(patient);
			Address address = new Address();
			if(addressCollection != null){
				BeanUtil.map(addressCollection, address);
				registeredPatientDetails.setAddress(address);
			}
			registeredPatientDetails.setGroups(request.getGroups());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Override
	public RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request) {
		RegisteredPatientDetails registeredPatientDetails = null;
		PatientCollection patientCollection = null;
		try {
			//save address
			AddressCollection addressCollection = null;
			if(request.getAddress() != null){
			addressCollection = new AddressCollection();
			BeanUtil.map(request.getAddress(), addressCollection);
			addressCollection.setUserId(request.getUserId());
			addressCollection = addressRepository.save(addressCollection);
			}
			//save Patient Info
			 patientCollection = patientRepository.findByUserIdAndDoctorId(request.getUserId(), request.getDoctorId());
			 if(patientCollection != null){
				 String patientId = patientCollection.getId();
				 BeanUtil.map(request, patientCollection);
				 patientCollection.setId(patientId);
			 }else{
				 patientCollection  = new PatientCollection();
				 BeanUtil.map(request, patientCollection);
			 }
			if(addressCollection != null){
				patientCollection.setAddressId(addressCollection.getId());
			}
			patientCollection = patientRepository.save(patientCollection);

			
			//save patient admission
			PatientAdmissionCollection patientAdmissionCollection = null;
			patientAdmissionCollection = patientAdmissionRepository.findByPatientIdAndDoctorId(patientCollection.getId(), request.getDoctorId());
			if(patientAdmissionCollection == null){
				patientAdmissionCollection = new PatientAdmissionCollection();
				BeanUtil.map(request, patientAdmissionCollection);
				patientAdmissionCollection.setUserId(request.getUserId());
		   		patientAdmissionCollection.setPatientId(patientCollection.getId());
				patientAdmissionRepository.save(patientAdmissionCollection);
			}
			//assign groups
  			if(request.getGroups()!= null){
  				List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getId());
  				if(patientGroupCollections != null){
  					for(PatientGroupCollection patientGroupCollection : patientGroupCollections){
  						patientGroupRepository.delete(patientGroupCollection);
  					}
  				}
				for(String group : request.getGroups()){
					    PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
						patientGroupCollection.setGroupId(group);
						patientGroupCollection.setPatientId(patientCollection.getId());
						patientGroupRepository.save(patientGroupCollection);
					}
				
			}
			//add into doctor contact
			if(request.getDoctorId() != null){
				DoctorContactCollection doctorContactCollection = null;
				doctorContactCollection = doctorContactsRepository.findByDoctorIdAndContactId(request.getDoctorId(), patientCollection.getId());
				if(doctorContactCollection == null){
					doctorContactCollection = new DoctorContactCollection();
					doctorContactCollection.setDoctorId(request.getDoctorId());
					doctorContactCollection.setContactId(patientCollection.getId());
					doctorContactsRepository.save(doctorContactCollection);
				}
				
			}
			UserCollection userCollection = userRepository.findOne(request.getUserId());
			registeredPatientDetails = new RegisteredPatientDetails();
			BeanUtil.map(userCollection, registeredPatientDetails);
			registeredPatientDetails.setUserId(userCollection.getId());
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			patient.setPatientId(patientCollection.getId());
			registeredPatientDetails.setPatient(patient);
			Address address = new Address();
			if(addressCollection != null){
				BeanUtil.map(addressCollection, address);
				registeredPatientDetails.setAddress(address);
			}
			registeredPatientDetails.setGroups(request.getGroups());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Override
	public List<User> getUsersByPhoneNumber(String phoneNumber) {
		List<User> users = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumber(phoneNumber);
			if(userCollections != null){
				users = new ArrayList<User>();
				for(UserCollection userCollection : userCollections){
					User user = new User();
					BeanUtil.map(userCollection, user);
					users.add(user);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}
	
	private String generateRandomAlphanumericString(int count){
		return RandomStringUtils.randomAlphabetic(count);
	}

	@Override
	public RegisteredPatientDetails getPatientProfileByUserId(String userId,String doctorId) {
		RegisteredPatientDetails registeredPatientDetails = null;
		try {
			UserCollection userCollection = userRepository.findOne(userId);
			if(userCollection != null){
				PatientCollection patientCollection = patientRepository.findByUserIdAndDoctorId(userId, doctorId);
				if(patientCollection != null){
					AddressCollection addressCollection = new AddressCollection();
					if(patientCollection.getAddressId() != null){
						 addressCollection = addressRepository.findOne(patientCollection.getAddressId());
					}
					List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getId());
					Collection<String> groupIds =  CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
					registeredPatientDetails = new RegisteredPatientDetails();
					BeanUtil.map(userCollection, registeredPatientDetails);
					registeredPatientDetails.setUserId(userCollection.getId());
					Patient patient = new Patient();
					BeanUtil.map(patientCollection, patient);
					patient.setPatientId(patientCollection.getId());
					registeredPatientDetails.setPatient(patient);
					Address address = new Address();
					BeanUtil.map(addressCollection, address);
					registeredPatientDetails.setAddress(address);
					
					registeredPatientDetails.setGroups((List<String>) groupIds);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}
	
	

}
