package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientInfoCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.PatientInfoRepository;
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
	private PatientInfoRepository patientInfoRepository;
	@Autowired
	private PatientRepository patientRepository;
	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;
	@Autowired
	private MailService mailService;
	@Autowired
	private MailBodyGenerator mailBodyGenerator;
	
	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Override
	public User checkIfPatientExist(PatientRegistrationRequest request) {
		try {
			UserCollection userCollection = userRepository.checkPatient(
					request.getFirstName(), request.getMiddleName(),
					request.getLastName(), request.getEmailAddress(),
					request.getPhoneNumber());
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
	public User registerNewPatient(PatientRegistrationRequest request) {
		User user = null;
		try {
			//get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
			if(roleCollection == null){
				throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
			}
			//save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			user = new User();
			BeanUtil.map(request, user);
			String uniqueUserName = generateUniqueUserNameService.generate(user);
			userCollection.setUserName(uniqueUserName);
			userCollection = userRepository.save(userCollection);
			
			//assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
			userRoleRepository.save(userRoleCollection);
			
			//save address
			AddressCollection addressCollection = new AddressCollection();
			BeanUtil.map(request.getAddress(), addressCollection);
			addressCollection.setUserId(userCollection.getId());
			addressRepository.save(addressCollection);
			
			//save Patient Info
			PatientInfoCollection patientInfoCollection  = new PatientInfoCollection();
			BeanUtil.map(request, patientInfoCollection);
			patientInfoRepository.save(patientInfoCollection);
			
			//save Patient visit.
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setUserId(userCollection.getId());
			patientRepository.save(patientCollection);
			
			//send activation email
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(), userCollection.getMiddleName(), userCollection.getLastName());
			mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
		    user = new User();
			BeanUtil.map(userCollection, user);
			user.setPassword(null);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return user;
	}

	@Override
	public boolean registerExistingPatient(PatientRegistrationRequest request,String patientId) {
		boolean isSaved = false;
		try {
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setUserId(patientId);
			patientRepository.save(patientCollection);
			isSaved = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return isSaved;
	}
	
	

}
