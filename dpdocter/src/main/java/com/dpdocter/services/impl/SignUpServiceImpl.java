package com.dpdocter.services.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.ConfexUserCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.PCUserCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESCollectionBoyDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.ConfexUserRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.PCUserRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientProfilePicChangeRequest;
import com.dpdocter.request.PatientSignUpRequest;
import com.dpdocter.request.PatientSignupRequestMobile;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PateientSignUpCheckResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;
import com.mongodb.DuplicateKeyException;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;

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
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private CustomPasswordEncoder passwordEncoder;

	@Autowired
	private CollectionBoyRepository collectionBoyRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private SMSServices smsServices;
	
	@Autowired
	private DoctorContactUsRepository doctorContactUsRepository;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.account.activate.subject}")
	private String accountActivateSubject;

	@Value(value = "${web.link}")
	private String LOGIN_WEB_LINK;

	private final double NAME_MATCH_REQUIRED = 0.80;

	@Value(value = "${patient.count}")
	private String patientCount;

	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private ConfexUserRepository confexUserRepository;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private PCUserRepository pcUserRepository;
	
	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${Signup.verifyPatientBasedOn80PercentMatchOfName}")
	private String verifyPatientBasedOn80PercentMatchOfName;

	@Value(value = "${Signup.unlockPatientBasedOn80PercentMatch}")
	private String unlockPatientBasedOn80PercentMatch;
		
	@Value(value = "${welcome.link}")
	private String welcomeLink;
	
	@Autowired
	private OTPRepository otpRepository;

	@Override
	@Transactional
	public String verifyUser(String tokenId) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(tokenId)).orElse(null);
			if (tokenCollection == null) {
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
			} else if (tokenCollection.getIsUsed()) {
				return "Your verification link has already been used."
						+ " Please contact support@healthcoco.com for completing your email verification";
			} else {
				if (!forgotPasswordService.isLinkValid(tokenCollection.getCreatedTime()))
					return "We were unable to verify your Healthcoco+ account."
							+ " Please contact support@healthcoco.com for completing your account verification.";
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findById(tokenCollection.getResourceId()).orElse(null);
				if (doctorClinicProfileRepository == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
				}
				UserCollection userCollection = userRepository.findById(doctorClinicProfileCollection.getDoctorId()).orElse(null);
				userCollection.setIsVerified(true);
				userCollection.setUserState(UserState.NOTACTIVATED);
				userRepository.save(userCollection);

				doctorClinicProfileCollection.setIsVerified(true);
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);
				return "You have successfully verified your email address."
						+ "Download the Healthcoco+ app - Every Doctor's Pocket Clinic."
						+ "Stay Healthy and Happy!";
			}
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification email that we sent you.";
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
	@Transactional
	public String verifyLocale(String tokenId) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(tokenId)).orElse(null);
			if (tokenCollection == null) {
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification SMS that we sent you.";
			} else if (tokenCollection.getIsUsed()) {
				return "Your verification link has already been used."
						+ " Please contact support@healthcoco.com for completing your SMS verification";
			} else {
				if (!forgotPasswordService.isLinkValid(tokenCollection.getCreatedTime()))
					return "We were unable to verify your Healthcoco account."
							+ " Please contact support@healthcoco.com for completing your account verification.";

				UserCollection userCollection = userRepository.findById(tokenCollection.getResourceId()).orElse(null);
				LocaleCollection localeCollection = localeRepository
						.findByContactNumber(userCollection.getMobileNumber());
				userCollection.setIsVerified(true);
				userRepository.save(userCollection);
				localeCollection.setIsVerified(true);
				localeRepository.save(localeCollection);
				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);

				return "You have successfully verified your Mobile No."
						+ "If you haven't already done so, download the Healthcoco app." + "Stay Healthy and Happy!";
			}
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification SMS that we sent you.";
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
	@Transactional
	public User patientSignUp(PatientSignUpRequest request) {
		User user = null;
		try {
			// get role of specified type
			RoleCollection roleCollection = roleRepository.findByRoleAndLocationIdIsNullAndHospitalIdIsNull(RoleEnum.PATIENT.getRole());
			if (roleCollection == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}
			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			if (request.getDob() != null && request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.NotAcceptable, DOB);
			}
			char[] salt = DPDoctorUtils.generateSalt();
			userCollection.setSalt(salt);
			char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
			for (int i = 0; i < request.getPassword().length; i++)
				passwordWithSalt[i] = request.getPassword()[i];
			for (int i = 0; i < salt.length; i++)
				passwordWithSalt[i + request.getPassword().length] = salt[i];
			userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
			userCollection.setIsActive(true);
			userCollection.setCreatedTime(new Date());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection = userRepository.save(userCollection);

			// assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(),
					roleCollection.getId(), null, null);
			userRoleRepository.save(userRoleCollection);

			// save Patient Info
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setUserId(userCollection.getId());
			Date createdTime = new Date();
			patientCollection.setCreatedTime(createdTime);
			if (request.getImage() != null) {
				String path = "profile-image";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				patientCollection.setImageUrl(imageURLResponse.getImageUrl());
				patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
			}

			patientCollection = patientRepository.save(patientCollection);
			user = new User();
			BeanUtil.map(userCollection, user);
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
	@Transactional
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
	@Transactional
	public Boolean checkMobileNumExist(String mobileNum) {
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNum, UserState.USERSTATECOMPLETE.getState());
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
	@Transactional
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
	@Transactional
	public User patientProfilePicChange(PatientProfilePicChangeRequest request) {
		User user = null;
		try {
			UserCollection userCollection = userRepository.findByUserName(request.getUsername());
			if (userCollection == null) {
				logger.warn("User not found");
				throw new BusinessException(ServiceError.NotFound, "User not found");
			} else {
				ObjectId locationObjectId = null, hospitalObjectId = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
					locationObjectId = new ObjectId(request.getLocationId());
				if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
					hospitalObjectId = new ObjectId(request.getHospitalId());

				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						userCollection.getId(), locationObjectId, hospitalObjectId);
				if (patientCollection != null) {
					if (request.getImage() != null) {
						String path = "profile-image";
						// save image
						request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
						ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(),
								path, true);
						patientCollection.setImageUrl(imageURLResponse.getImageUrl());
						patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						patientCollection.setUpdatedTime(new Date());
						patientCollection = patientRepository.save(patientCollection);

						user = new User();
						BeanUtil.map(userCollection, user);
						user.setImageUrl(imageURLResponse.getImageUrl());
						user.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					}
				} else {
					logger.warn("No patient found for this doctor");
					throw new BusinessException(ServiceError.NotFound, "No patient found for this doctor");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return user;
	}

	@Override
	@Transactional
	public PateientSignUpCheckResponse checkMobileNumberSignedUp(String mobileNumber) {
		PateientSignUpCheckResponse response = new PateientSignUpCheckResponse();
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNumber, UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
						if (userCollection.isSignedUp())
							throw new BusinessException(ServiceError.NotAcceptable,
									"Mobile Number is already signed up. Please Login");
						else {
							if (!response.getIsPatientExistWithMobileNumber())
								response.setIsPatientExistWithMobileNumber(true);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public boolean checkMobileNumberExistForPatient(String mobileNumber) {
		boolean response = false;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNumber, UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
						response = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	/**
	 * This service implementation checks if name/names for mobileNumber matches 80%
	 * with the queryName.
	 * 
	 * @return boolean
	 */

	@Override
	@Transactional
	public boolean verifyPatientBasedOn80PercentMatchOfName(String name, String mobileNumber) {
		boolean checkMatch = false;

		if (!checkMobileNumberExistForPatient(mobileNumber)) {
			logger.error(verifyPatientBasedOn80PercentMatchOfName + " " + mobileNumber);
			throw new BusinessException(ServiceError.NoRecord,
					verifyPatientBasedOn80PercentMatchOfName + " " + mobileNumber);
		} else {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNumber, UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equals(userCollection.getEmailAddress()))
						if (!userCollection.isSignedUp() && matchName(userCollection.getFirstName(), name)) {
							checkMatch = true;
							break;
						}
				}
			}
		}
		return checkMatch;
	}

	/**
	 * This Service Implementation will check for 80% match of name,if matched then
	 * unlock all the locked users for the given mobile number.
	 */

	@Override
	@Transactional
	public boolean unlockPatientBasedOn80PercentMatch(String name, String mobileNumber) {
		boolean isUnlocked = false;

		if (!checkMobileNumberSignedUp(mobileNumber).getIsPatientExistWithMobileNumber()) {
			logger.error(unlockPatientBasedOn80PercentMatch + " " + mobileNumber);
			throw new BusinessException(ServiceError.NoRecord, unlockPatientBasedOn80PercentMatch + " " + mobileNumber);
		} else {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNumber, UserState.USERSTATECOMPLETE.getState());
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.isSignedUp() && matchName(userCollection.getFirstName(), name)) {
						userCollection.setSignedUp(true);
					}
				}
				// This batch update will unlock all the locked users.(make
				// signedup flag =true)
				userRepository.saveAll(userCollections);
				isUnlocked = true;
			}
		}
		return isUnlocked;
	}

	/**
	 * This utility method checks for 80% match of name.
	 * 
	 * @param actualName
	 * @param queryName
	 * @return boolean
	 */
	private boolean matchName(String actualName, String queryName) {
		boolean result = false;
		if (StringUtils.getJaroWinklerDistance(actualName, queryName) >= NAME_MATCH_REQUIRED) {
			result = true;
		}
		return result;
	}

	/**
	 * This service implementation will signup the new patient into DB. This newly
	 * created patient will be in unlock state.
	 */
	@Override
	@Transactional
	public RegisteredPatientDetails signupNewPatient(PatientSignupRequestMobile request) {
		RegisteredPatientDetails user = null;
		try {
			RoleCollection roleCollection = roleRepository.findByRoleAndLocationIdIsNullAndHospitalIdIsNull(RoleEnum.PATIENT.getRole());
			if (roleCollection == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}
			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			char[] salt = DPDoctorUtils.generateSalt();
			userCollection.setSalt(salt);
			char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
			for (int i = 0; i < request.getPassword().length; i++)
				passwordWithSalt[i] = request.getPassword()[i];
			for (int i = 0; i < salt.length; i++)
				passwordWithSalt[i + request.getPassword().length] = salt[i];
			userCollection.setPassword(DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt));
			userCollection.setFirstName(request.getName());
			userCollection.setIsActive(true);
			userCollection.setCreatedTime(new Date());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection.setSignedUp(true);
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());

			userCollection.setUserName(generateUniqueUserNameService
					.generate(new User(userCollection.getFirstName(), request.getMobileNumber())));
			userCollection = userRepository.save(userCollection);

			// assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(),
					roleCollection.getId(), null, null);
			userRoleRepository.save(userRoleCollection);

			// save Patient Info
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setFirstName(request.getName());
			patientCollection.setUserId(userCollection.getId());
			patientCollection.setDoctorId(null);
			patientCollection.setLocationId(null);
			patientCollection.setHospitalId(null);
			patientCollection.setCreatedTime(new Date());
			patientCollection = patientRepository.save(patientCollection);

			user = new RegisteredPatientDetails();
			if (patientCollection != null) {
				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				BeanUtil.map(patientCollection, user);
				BeanUtil.map(userCollection, user);
				patient.setPatientId(patientCollection.getUserId().toString());
				user.setPatient(patient);
			}
			user.setUserId(userCollection.getId().toString());
			ESPatientDocument esPatientDocument = new ESPatientDocument();
			if (patientCollection.getAddress() != null) {
				BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
			}
			BeanUtil.map(userCollection, esPatientDocument);
			BeanUtil.map(patientCollection, esPatientDocument);
			esPatientDocument.setUserId(userCollection.getId().toString());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * This Service Impl will signup the already registered patients into the DB. If
	 * 80% match is found for the fname then it will unlock all the patients in the
	 * DB.
	 */
	@Override
	@Transactional
	public List<RegisteredPatientDetails> signupAlreadyRegisteredPatient(PatientSignupRequestMobile request) {

		List<RegisteredPatientDetails> users = new ArrayList<RegisteredPatientDetails>();
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.USERSTATECOMPLETE.getState());
			char[] salt = DPDoctorUtils.generateSalt();

			char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
			for (int i = 0; i < request.getPassword().length; i++)
				passwordWithSalt[i] = request.getPassword()[i];
			for (int i = 0; i < salt.length; i++)
				passwordWithSalt[i + request.getPassword().length] = salt[i];
			char[] password = DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt);

			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						userCollection.setSalt(salt);
						userCollection.setPassword(password);
						userCollection.setSignedUp(true);
						userCollection.setColorCode(request.getCountryCode());
						userRepository.save(userCollection);

						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null, null);
						if (patientCollection == null) {
							patientCollection = new PatientCollection();
							BeanUtil.map(userCollection, patientCollection);
							patientCollection.setId(null);
							patientCollection.setUserId(userCollection.getId());
							patientCollection.setDoctorId(null);
							patientCollection.setLocationId(null);
							patientCollection.setHospitalId(null);
							patientCollection.setCreatedTime(new Date());
							patientCollection = patientRepository.save(patientCollection);
							if (patientCollection != null) {
								Patient patient = new Patient();
								BeanUtil.map(patientCollection, patient);
								BeanUtil.map(patientCollection, user);
								BeanUtil.map(userCollection, user);
								patient.setPatientId(patientCollection.getUserId().toString());
								user.setPatient(patient);
							}
							user.setUserId(userCollection.getId().toString());
							ESPatientDocument esPatientDocument = new ESPatientDocument();
							if (patientCollection.getAddress() != null) {
								BeanUtil.map(patientCollection.getAddress(), esPatientDocument);
							}
							BeanUtil.map(userCollection, esPatientDocument);
							BeanUtil.map(patientCollection, esPatientDocument);
							esPatientDocument.setUserId(userCollection.getId().toString());
							transnationalService.addResource(patientCollection.getUserId(), Resource.PATIENT, false);
							esRegistrationService.addPatient(esPatientDocument);
						}
						users.add(user);
					}
				}
			} else {
				RegisteredPatientDetails user = signupNewPatient(request);
				users.add(user);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return users;

	}

	@Override
	@Transactional
	public CollectionBoyResponse signupCollectionBoys(CollectionBoy collectionBoy) {
		CollectionBoyResponse response = null;
		ESCollectionBoyDocument esCollectionBoyDocument = null;
		try {

			UserCollection userCollection = new UserCollection();
			userCollection.setUserName(UniqueIdInitial.PHARMACY.getInitial() + collectionBoy.getMobileNumber());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setIsVerified(true);
			userCollection.setIsActive(true);
			userCollection.setFirstName(collectionBoy.getName());
			userCollection.setCreatedTime(new Date());
			userCollection.setMobileNumber(collectionBoy.getMobileNumber());
			userCollection.setUserState(UserState.COLLECTION_BOY);

//			userCollection.setPassword(collectionBoy.getPassword());
			userCollection.setPassword(passwordEncoder.encode(collectionBoy.getPassword().toString()).toCharArray());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection = userRepository.save(userCollection);

			CollectionBoyCollection collectionBoyCollection = new CollectionBoyCollection();
			BeanUtil.map(collectionBoy, collectionBoyCollection);
			collectionBoyCollection.setUserId(userCollection.getId());
			// localeCollection.setLocaleUId(UniqueIdInitial.PHARMACY.getInitial()
			// + DPDoctorUtils.generateRandomId());

			/*
			 * if (collectionBoyCollection.getAddress() != null) { Address address =
			 * collectionBoyCollection.getAddress(); List<GeocodedLocation>
			 * geocodedLocations = locationServices
			 * .geocodeLocation((!DPDoctorUtils.anyStringEmpty(address. getStreetAddress())
			 * ? address.getStreetAddress() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality()
			 * + ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCity()) ?
			 * address.getCity() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() +
			 * ", " : "") + (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ?
			 * address.getCountry() + ", " : "") +
			 * (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ?
			 * address.getPostalCode() : ""));
			 * 
			 * if (geocodedLocations != null && !geocodedLocations.isEmpty())
			 * BeanUtil.map(geocodedLocations.get(0), collectionBoyCollection); }
			 */

			collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
			if (collectionBoyCollection != null) {
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("CB_SIGNUP");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				if (userCollection != null)
					smsDetail.setUserName(collectionBoyCollection.getName());
				SMS sms = new SMS();
				sms.setSmsText("Hi ," + collectionBoyCollection.getName()
						+ " your registration with Healthcoco is completed. Please use provided contact number for login. Your password is "
						+ collectionBoy.getPassword().toString());

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(collectionBoyCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
				response = new CollectionBoyResponse();
				BeanUtil.map(collectionBoyCollection, response);
				// response.setPassword(null);
			}
			esCollectionBoyDocument = new ESCollectionBoyDocument();
			BeanUtil.map(collectionBoyCollection, esCollectionBoyDocument);
			transnationalService.addResource(collectionBoyCollection.getId(), Resource.COLLECTION_BOY, false);
			esRegistrationService.addCollectionBoy(esCollectionBoyDocument);
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Mobile number already registerd. Please login");
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while contacting Healthcoco");
			throw new BusinessException(ServiceError.Unknown,
					" Error occured while contacting Healthcoco. Please Check mobile number or contact administration");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public DoctorSignUp doctorSignUp(DoctorSignupRequest request) {
		DoctorSignUp response = null;
		PCUserCollection pcUserCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
				logger.warn("Email Address cannot be null");
				throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
			}
			
			List<UserCollection>userCollections=userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
			
			if(userCollections!=null && !userCollections.isEmpty())
				if(userCollections.get(0).getEmailAddress() !=null && userCollections.get(0).getPassword()!=null)
				{
					throw new BusinessException(ServiceError.Unknown,
							"Account with this emailId "+ userCollections.get(0).getEmailAddress()+" already exists,Please login or use forgot password.");
			
				}

			List<RoleCollection> roleCollections = roleRepository
					.findByRoleInAndLocationIdAndHospitalId(Arrays.asList(RoleEnum.HOSPITAL_ADMIN.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
							RoleEnum.DOCTOR.getRole(), RoleEnum.SUPER_ADMIN.getRole()), null, null);
			if (roleCollections == null || roleCollections.isEmpty() || roleCollections.size() < 4) {
				logger.warn("Role Collection in database is either empty or not defind properly");
				throw new BusinessException(ServiceError.NoRecord,
						"Role Collection in database is either empty or not defind properly");
			}

			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			if (request.getDob() != null
					&& request.getDob().getAge() != null & request.getDob().getAge().getYears() < 0) {
				logger.warn("Incorrect Date of Birth");
				throw new BusinessException(ServiceError.InvalidInput, "Incorrect Date of Birth");
			}
			userCollection.setUserName(request.getEmailAddress());
			if (DPDoctorUtils.allStringsEmpty(request.getTitle()))
				userCollection.setTitle("Dr.");
			userCollection.setCreatedTime(new Date());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());

			userCollection.setUserState(UserState.NOTACTIVATED);
			userCollection.setIsVerified(true);
			if(request.getPassword()!=null&&request.getPassword().length>0)
			userCollection.setPassword(passwordEncoder.encode(String.valueOf(request.getPassword())).toCharArray());
//			userCollection.setPassword(request.getPassword());
			userCollection.setIsPasswordSet(true);
			userCollection = userRepository.save(userCollection);
			// save doctor specific details
			DoctorCollection doctorCollection = new DoctorCollection();
			List<String> specialities = request.getSpecialities();
			request.setSpecialities(null);
			BeanUtil.map(request, doctorCollection);
			if (specialities != null && !specialities.isEmpty()) {
				List<SpecialityCollection> specialityCollections = specialityRepository
						.findBySuperSpecialityIn(specialities);
				Collection<ObjectId> specialityIds = CollectionUtils.collect(specialityCollections,
						new BeanToPropertyValueTransformer("id"));
				if (specialityIds != null && !specialityIds.isEmpty())
					doctorCollection.setSpecialities(new ArrayList<>(specialityIds));
				else
					doctorCollection.setSpecialities(null);
			} else {
				doctorCollection.setSpecialities(null);
			}
			doctorCollection.setRegisterNumber(request.getRegisterNumber());
			doctorCollection.setUserId(userCollection.getId());
			doctorCollection.setCreatedTime(new Date());
			if (request.getMrCode() != null) {
				pcUserCollection = pcUserRepository.findByMrCode(request.getMrCode());
				if (pcUserCollection != null) {
					doctorCollection.setDivisionIds(pcUserCollection.getDivisionId());
				}
			}
			doctorCollection = doctorRepository.save(doctorCollection);

			userCollection = userRepository.save(userCollection);

			HospitalCollection hospitalCollection = new HospitalCollection();
			BeanUtil.map(request, hospitalCollection);
			hospitalCollection.setCreatedTime(new Date());
			hospitalCollection.setHospitalUId(UniqueIdInitial.HOSPITAL.getInitial() + DPDoctorUtils.generateRandomId());
			hospitalCollection = hospitalRepository.save(hospitalCollection);

			// save location for hospital
			LocationCollection locationCollection = new LocationCollection();
			BeanUtil.map(request, locationCollection);
			if (locationCollection.getId() == null) {
				locationCollection.setCreatedTime(new Date());
			}
			locationCollection.setLocationUId(UniqueIdInitial.LOCATION.getInitial() + DPDoctorUtils.generateRandomId());
			locationCollection.setHospitalId(hospitalCollection.getId());
			locationCollection.setIsActivate(true);
//			List<GeocodedLocation> geocodedLocations = locationServices
//					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
//							? locationCollection.getStreetAddress() + ", "
//							: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
//									? locationCollection.getLandmarkDetails() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
//									? locationCollection.getLocality() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
//									? locationCollection.getCity() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
//									? locationCollection.getState() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
//									? locationCollection.getCountry() + ", "
//									: "")
//							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
//									? locationCollection.getPostalCode()
//									: ""));
//
//			if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), locationCollection);

			locationCollection = locationRepository.save(locationCollection);
			// save user location.
			
			DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
			doctorClinicProfileCollection.setDoctorId(userCollection.getId());
			doctorClinicProfileCollection.setLocationId(locationCollection.getId());
			doctorClinicProfileCollection.setMrCode(request.getMrCode());
			doctorClinicProfileCollection.setCreatedTime(new Date());
			if(request.getMrCode() != null){
				pcUserCollection = pcUserRepository.findByMrCode(request.getMrCode());
				if(pcUserCollection != null){
					doctorClinicProfileCollection.setDivisionIds(pcUserCollection.getDivisionId());
					doctorClinicProfileCollection.setMrCode(pcUserCollection.getMrCode());
				}
			}
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);

			Collection<ObjectId> roleIds = CollectionUtils.collect(roleCollections,
					new BeanToPropertyValueTransformer("id"));
			List<UserRoleCollection> userRoleCollections = new ArrayList<>();
			for (ObjectId roleId : roleIds) {
				UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleId,
						locationCollection.getId(), locationCollection.getHospitalId());
				userRoleCollection.setCreatedTime(new Date());
				userRoleCollections.add(userRoleCollection);
			}
			
		/*	if(request.getMrCode() != null)
			{
				pcUserCollection = pcUserRepository.findByMRCode(request.getMrCode());
				List<PharmaLicenseResponse> pharmaLicenseResponses = pharmaService.getLicenses(pcUserCollection.getCompanyId().toString(), 0, 0);
				for(PharmaLicenseResponse pharmaLicenseResponse : pharmaLicenseResponses)
				{
					if(pharmaLicenseResponse.getAvailable() > 0)
					{
						licenseResponse = pharmaLicenseResponse;
						break;
					}
				}
			}
			
			// Subscribe Doctor with Clinic
			SubscriptionDetail detail = new SubscriptionDetail();
			detail.setCreatedBy("Admin");
			detail.setDoctorId(userCollection.getId().toString());
			detail.setIsDemo(true);
			detail.setMonthsforSms(1);
			detail.setMonthsforSuscrption(1);
			detail.setNoOfsms(500);
			Set<String> locationSet = new HashSet<String>();
			locationSet.add(locationCollection.getId().toString());
			detail.setLocationIds(locationSet);
			if(licenseResponse != null)
			{
				detail.setIsDemo(false);
				detail.setFromDate(new Date());
				detail.setToDate(DateUtils.addMonths(new Date(), licenseResponse.getDuration()));
				detail.setLicenseId(licenseResponse.getId());
				licenseResponse.setAvailable(licenseResponse.getAvailable() - 1);
				licenseResponse.setConsumed(licenseResponse.getConsumed() + 1);
				PharmaLicenseCollection pharmaLicenseCollection = new PharmaLicenseCollection();
				BeanUtil.map(licenseResponse, pharmaL"isVerified" : falseicenseCollection);
				pharmaLicenseRepository.save(pharmaLicenseCollection);
				
			}
			subscriptionService.activate(detail);*/
			userRoleRepository.saveAll(userRoleCollections);

			/*
			 * if(request.getMrCode() != null) { pcUserCollection =
			 * pcUserRepository.findByMRCode(request.getMrCode());
			 * List<PharmaLicenseResponse> pharmaLicenseResponses =
			 * pharmaService.getLicenses(pcUserCollection.getCompanyId().toString(), 0, 0);
			 * for(PharmaLicenseResponse pharmaLicenseResponse : pharmaLicenseResponses) {
			 * if(pharmaLicenseResponse.getAvailable() > 0) { licenseResponse =
			 * pharmaLicenseResponse; break; } } }
			 * 
			 * // Subscribe Doctor with Clinic SubscriptionDetail detail = new
			 * SubscriptionDetail(); detail.setCreatedBy("Admin");
			 * detail.setDoctorId(userCollection.getId().toString());
			 * detail.setIsDemo(true); detail.setMonthsforSms(1);
			 * detail.setMonthsforSuscrption(1); detail.setNoOfsms(500); Set<String>
			 * locationSet = new HashSet<String>();
			 * locationSet.add(locationCollection.getId().toString());
			 * detail.setLocationIds(locationSet); if(licenseResponse != null) {
			 * detail.setIsDemo(false); detail.setFromDate(new Date());
			 * detail.setToDate(DateUtils.addMonths(new Date(),
			 * licenseResponse.getDuration()));
			 * detail.setLicenseId(licenseResponse.getId());
			 * licenseResponse.setAvailable(licenseResponse.getAvailable() - 1);
			 * licenseResponse.setConsumed(licenseResponse.getConsumed() + 1);
			 * PharmaLicenseCollection pharmaLicenseCollection = new
			 * PharmaLicenseCollection(); BeanUtil.map(licenseResponse, pharmaL"isVerified"
			 * : falseicenseCollection);
			 * pharmaLicenseRepository.save(pharmaLicenseCollection);
			 * 
			 * } subscriptionService.activate(detail);
			 */

			// Subscribe Doctor with Clinic
			/*
			 * SubscriptionDetail detail = new SubscriptionDetail();
			 * detail.setCreatedBy("Admin");
			 * detail.setDoctorId(userCollection.getId().toString());
			 * detail.setIsDemo(true); detail.setMonthsforSms(1);
			 * detail.setMonthsforSuscrption(1); detail.setNoOfsms(500); Set<String>
			 * locationSet = new HashSet<String>();
			 * locationSet.add(locationCollection.getId().toString());
			 * detail.setLocationIds(locationSet);
			 * subscriptionDetailServices.activate(detail);
			 */

			// save token
			TokenCollection tokenCollection = new TokenCollection();
			tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
			tokenCollection.setCreatedTime(new Date());
			tokenCollection = tokenRepository.save(tokenCollection);

			// send activation email
			/*
			 * String body = mailBodyGenerator .generateActivationEmailBody(
			 * (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") +
			 * userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm",
			 * null, null); mailService.sendEmail(userCollection.getEmailAddress(),
			 * signupSubject, body, null);
			 */

			/*
			 * String body = mailBodyGenerator.generateActivationEmailBody(
			 * (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") +
			 * userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm",
			 * null, null); mailService.sendEmail(userCollection.getEmailAddress(),
			 * signupSubject, body, null);
			 */

			// user.setPassword(null);
			/*
			 * if (userCollection.getMobileNumber() != null) { SMSTrackDetail smsTrackDetail
			 * = new SMSTrackDetail();
			 * 
			 * smsTrackDetail.setType("BEFORE_VERIFICATION_TO_DOCTOR"); SMSDetail smsDetail
			 * = new SMSDetail(); smsDetail.setUserId(userCollection.getId());
			 * smsDetail.setUserName(userCollection.getFirstName()); SMS sms = new SMS();
			 * sms.setSmsText("Welcome " + (userCollection.getTitle() != null ?
			 * userCollection.getTitle() + " " : "") + userCollection.getFirstName() +
			 * " to Healthcoco. We will contact you shortly to get you started. Download the Healthcoco+ app now: "
			 * + doctorAppLink +
			 * ". For queries, please feel free to contact us at support@healthcoco.com");
			 * 
			 * SMSAddress smsAddress = new SMSAddress();
			 * smsAddress.setRecipient(userCollection.getMobileNumber());
			 * sms.setSmsAddress(smsAddress);
			 * 
			 * smsDetail.setSms(sms); smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			 * List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			 * smsDetails.add(smsDetail); smsTrackDetail.setSmsDetails(smsDetails);
			 * sMSServices.sendSMS(smsTrackDetail, true); }
			 */
			response = new DoctorSignUp();
			User user = new User();
			userCollection.setPassword(null);
			BeanUtil.map(userCollection, user);
			user.setEmailAddress(userCollection.getEmailAddress());
			user.setSpecialities(specialities);
			response.setUser(user);

			List<AccessControl> accessControls = accessControlServices.getAllAccessControls(roleIds, null, null);
			List<Role> roles = new ArrayList<Role>();
			if (accessControls != null && !accessControls.isEmpty())
				for (AccessControl accessControl : accessControls) {
					Role role = new Role();
					for (RoleCollection roleCollection : roleCollections) {
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.HOSPITAL_ADMIN.getRole());
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.LOCATION_ADMIN.getRole());
						if (accessControl.getRoleOrUserId().equals(roleCollection.getId().toString()))
							role.setRole(RoleEnum.DOCTOR.getRole());
					}
					BeanUtil.map(accessControl.getAccessModules(), role);
					roles.add(role);
				}

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
			throw new BusinessException(ServiceError.Unknown, "Email address already registerd. Please login");
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
	@Transactional
	public DoctorContactUs welcomeUser(String tokenId) {
		DoctorContactUs doctorContactUs = null;
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(tokenId)).orElse(null);
			if (tokenCollection == null) {
				throw new BusinessException(ServiceError.NoRecord , "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the welcome email that we sent you.");
			}/* else if (tokenCollection.getIsUsed()) {
				throw new BusinessException(ServiceError.Forbidden , "Your welcome link has already been used."
						+ " Please contact support@healthcoco.com for completing your email verification");
						
			} */else {
				DoctorContactUsCollection doctorContactUsCollection = doctorContactUsRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if(doctorContactUsCollection != null)
				{
					doctorContactUs = new DoctorContactUs();
					BeanUtil.map(doctorContactUsCollection, doctorContactUs);
				}
				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);
				return doctorContactUs;
			}
		} catch (IllegalArgumentException argumentException) {
			throw new BusinessException(ServiceError.Forbidden ,"Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the welcome email that we sent you.");
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while registering user");
			throw new BusinessException(ServiceError.Unknown, "Error occured while registerings user");
		}

	}


	@Override
	@Transactional
	public String verifyConfexAdmin(String tokenId) {
		try {
			TokenCollection tokenCollection = tokenRepository.findById(new ObjectId(tokenId)).orElse(null);
			if (tokenCollection == null) {
				return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification  that we sent you.";
			} else if (tokenCollection.getIsUsed()) {
				return "Your verification link has already been used."
						+ " Please contact support@healthcoco.com for completing your  verification";
			} else {
				if (!forgotPasswordService.isLinkValid(tokenCollection.getCreatedTime()))
					return "We were unable to verify your Healthcoco+ account."
							+ " Please contact support@healthcoco.com for completing your account verification.";
				ConfexUserCollection userCollection = confexUserRepository.findById(tokenCollection.getResourceId()).orElse(null);
				if (userCollection == null) {
					return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification  that we sent you.";
				}
				userCollection.setIsVerified(true);
				confexUserRepository.save(userCollection);

				tokenCollection.setIsUsed(true);
				tokenRepository.save(tokenCollection);
				return "You have successfully verified your Account."
						+ "If you haven't already done so, download the Healthcoco+ app - Every Doctor's Pocket Clinic."
						+ "Stay Healthy and Happy!";
			}
		} catch (IllegalArgumentException argumentException) {
			return "Incorrect link. If you copied and pasted the link into a browser, please confirm that you didn't change or add any characters. You must click the link exactly as it appears in the verification  that we sent you.";
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
	public Boolean DoctorRegister(String mobileNumber) {
		Boolean response=false;
//		try {
//		
//		DoctorContactUsCollection doctorContactUsCollection = doctorContactUsRepository.findByMobileNumber(mobileNumber);
//
//	//	if(DPDoctorUtils.anyStringEmpty(doctorContactUsCollection.getMobileNumber()))
//		//if(doctorContactUsCollection.getMobileNumber().equals(null))
//	if(doctorContactUsCollection!=null)
//	{
//		throw new BusinessException(ServiceError.Unknown, "Please Signup you have registered already.");
//
//	}
//	else {
//			doctorContactUsCollection=new DoctorContactUsCollection();
//				 doctorContactUsCollection.setCreatedTime(new Date());
//				 doctorContactUsCollection.setUserName("aman.gmail.com");
//				 doctorContactUsCollection.setMobileNumber(mobileNumber);
//			
//	}		 
//				 doctorContactUsRepository.save(doctorContactUsCollection);
//				 response=otpGenerator(mobileNumber);
//				 
//			 
//		}
//	 catch (Exception e) {
//		e.printStackTrace();
//		logger.error(e + " Error occured while generating otp through mobile number");
//		throw new BusinessException(ServiceError.Unknown, "Error occured while generating mobile number "+e.getMessage());
//	}
		return response;
	}
	
	  public Boolean otpGenerator(String mobileNumber) {
	    	Boolean response = false;
		String OTP = null;
		try {
		    OTP = LoginUtils.generateOTP();
		    SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			
			smsTrackDetail.setType(ComponentType.SIGNED_UP.getType());
			SMSDetail smsDetail = new SMSDetail();
			
		//	smsDetail.setUserName(doctorContactUs.getFirstName());
			SMS sms = new SMS();
		
		//	String link = welcomeLink + "/" + tokenCollection.getId()+"/";
		//	String shortUrl = DPDoctorUtils.urlShortner(link);
			sms.setSmsText(OTP+" is your Healthcoco OTP. Code is valid for 30 minutes only, one time use. Stay Healthy and Happy! OTPVerification");

				SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(mobileNumber);
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			smsServices.sendSMS(smsTrackDetail, true);

		    OTPCollection otpCollection = new OTPCollection();
		    otpCollection.setCreatedTime(new Date());
		    otpCollection.setOtpNumber(OTP);
		    otpCollection.setGeneratorId(mobileNumber);
		    otpCollection.setMobileNumber(mobileNumber);
		    otpCollection.setCreatedBy(mobileNumber);
		    otpCollection = otpRepository.save(otpCollection);

		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While Generating OTP");
		    throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP");
		}
		return response;
	    }

	@Override
	@Transactional
	public Boolean verifyEmailAddress(String email) {
	
			try {
				DoctorContactUsCollection userCollections = doctorContactUsRepository.findByEmailAddressIgnoreCase(email);
				if (userCollections != null) {
						return true;
					} else {
						return false;
					}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e);
				throw new BusinessException(ServiceError.Unknown, e.getMessage());
			}
		}
	


}
