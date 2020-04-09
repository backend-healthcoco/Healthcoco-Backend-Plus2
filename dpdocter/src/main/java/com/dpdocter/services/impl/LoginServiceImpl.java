package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DoctorLoginPin;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.NutritionPlan;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SubscriptionNutritionPlan;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorLoginPinCollection;
import com.dpdocter.collections.DoctorSchoolAssociationCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserNutritionSubscriptionCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorLoginPinRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorLoginPinRequest;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.response.UserNutritionSubscriptionResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.OTPService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;

import common.util.web.DPDoctorUtils;

@Service
public class LoginServiceImpl implements LoginService {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private DoctorLoginPinRepository doctorLoginPinRepository;

	@Autowired
	private OTPService otpService;

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

	@Autowired
	private ForgotPasswordService forgotPasswordService;
	/**
	 * This method is used for login purpose.
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public LoginResponse login(LoginRequest request, Boolean isMobileApp, Boolean isNutritionist) {
		LoginResponse response = null;
		try {
			Criteria criteria = new Criteria("userName").is(request.getUsername());
			Query query = new Query();
			query.addCriteria(criteria);
			UserCollection userCollection = userRepository.findByUserName(request.getUsername());
		
			if (userCollection == null) {
				logger.warn(login);
				throw new BusinessException(ServiceError.InvalidInput, login);
			} else {
				if(userCollection.getIsPasswordSet() == null || !userCollection.getIsPasswordSet()) {
					ForgotUsernamePasswordRequest forgotUsernamePasswordRequest = new ForgotUsernamePasswordRequest();
					forgotUsernamePasswordRequest.setEmailAddress(request.getUsername());
					forgotUsernamePasswordRequest.setUsername(request.getUsername());
					ForgotPasswordResponse forgotPasswordResponse = forgotPasswordService.forgotPasswordForDoctor(forgotUsernamePasswordRequest);
					if(forgotPasswordResponse!=null) {
						logger.warn("Please reset your password and check your email to update your password");
						throw new BusinessException(ServiceError.InvalidInput, "Please reset your password and check your email to update your password");
					}
				}
				boolean isPasswordCorrect = new CustomPasswordEncoder().matches(String.valueOf(request.getPassword()), String.valueOf(userCollection.getPassword()));
				
				if(!isPasswordCorrect) {
					logger.warn(login);
					throw new BusinessException(ServiceError.InvalidInput, login);
				}
			}
			User user = new User();
			BeanUtil.map(userCollection, user);
			if (userCollection.getUserState() != null
					&& userCollection.getUserState().equals(UserState.USERSTATEINCOMPLETE)) {
				response = new LoginResponse();
				user.setEmailAddress(user.getUserName());
				response.setUser(user);
				return response;
			}
			List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("userId").is(userCollection.getId()).and("locationId")
									.exists(true).and("roleId").exists(true)),
							Aggregation.lookup("role_cl", "roleId", "_id", "roleCollection"),
							Aggregation.unwind("roleCollection")),
					UserRoleCollection.class, UserRoleLookupResponse.class).getMappedResults();

			for (UserRoleLookupResponse userRoleLookupResponse : userRoleLookupResponses) {
				RoleCollection roleCollection = userRoleLookupResponse.getRoleCollection();
				if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {
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
					criteria = new Criteria("doctorId").is(userCollection.getId()).and("isActivate").is(true)
							.and("hasLoginAccess").ne(false);

					//criteria.and("isNutritionist").is(isNutritionist);
					List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(Aggregation.match(criteria),
											Aggregation.lookup("location_cl", "locationId", "_id", "location"),
											Aggregation.unwind("location"),
											Aggregation.lookup("hospital_cl", "$location.hospitalId", "_id",
													"hospital"),
											Aggregation.unwind("hospital")),
									DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
							.getMappedResults();
					if (doctorClinicProfileLookupResponses == null || doctorClinicProfileLookupResponses.isEmpty()) {

						logger.warn("None of your clinic is active or or you dont have login access,please contact your admin.");
						// user.setUserState(UserState.NOTACTIVATED);
						throw new BusinessException(ServiceError.NotAuthorized, "None of your clinic is active or you dont have login access,please contact your admin.");

					}
					if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
						List<Hospital> hospitals = new ArrayList<Hospital>();
						Map<String, Hospital> checkHospitalId = new HashMap<String, Hospital>();
						for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
							LocationCollection locationCollection = doctorClinicProfileLookupResponse.getLocation();
							HospitalCollection hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
							LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
							BeanUtil.map(locationCollection, locationAndAccessControl);
							locationAndAccessControl
									.setLogoUrl(getFinalImageURL(locationAndAccessControl.getLogoUrl()));
							locationAndAccessControl.setLogoThumbnailUrl(
									getFinalImageURL(locationAndAccessControl.getLogoThumbnailUrl()));
							locationAndAccessControl
									.setImages(getFinalClinicImages(locationAndAccessControl.getImages()));
							locationAndAccessControl.setIsVaccinationModuleOn(
									doctorClinicProfileLookupResponse.getIsVaccinationModuleOn());
							locationAndAccessControl.setIsNutritionist(doctorClinicProfileLookupResponse.getIsAdminNutritionist());
							locationAndAccessControl.setIsAdminNutritionist(doctorClinicProfileLookupResponse.getIsAdminNutritionist());
							List<Role> roles = null;

							Boolean isStaff = false;
							for (UserRoleLookupResponse otherRoleCollection : userRoleLookupResponses) {
								if ((otherRoleCollection.getHospitalId() == null
										&& otherRoleCollection.getLocationId() == null)
										|| (otherRoleCollection.getHospitalId().toString()
												.equalsIgnoreCase(hospitalCollection.getId().toString())
												&& otherRoleCollection.getLocationId().toString()
														.equalsIgnoreCase(locationCollection.getId().toString()))) {
									if (isMobileApp && doctorClinicProfileLookupResponses.size() == 1
											&& !(otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())
													|| otherRoleCollection.getRoleCollection().getRole()
															.equalsIgnoreCase(RoleEnum.CONSULTANT_DOCTOR.getRole())
													|| otherRoleCollection.getRoleCollection().getRole()
															.equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())
													|| otherRoleCollection.getRoleCollection().getRole()
															.equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole())
													|| otherRoleCollection.getRoleCollection().getRole()
															.equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole()))) {
										logger.warn("You are staff member so please login from website.");
										throw new BusinessException(ServiceError.NotAuthorized,
												"You are staff member so please login from website.");
									} else if (isMobileApp && !(otherRoleCollection.getRoleCollection().getRole()
											.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.CONSULTANT_DOCTOR.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole()))) {
										isStaff = true;
									}

									if (otherRoleCollection != null) {
										AccessControl accessControl = accessControlServices.getAccessControls(
												otherRoleCollection.getRoleCollection().getId(),
												otherRoleCollection.getRoleCollection().getLocationId(),
												otherRoleCollection.getRoleCollection().getHospitalId());

										Role role = new Role();
										BeanUtil.map(otherRoleCollection.getRoleCollection(), role);
										role.setAccessModules(accessControl.getAccessModules());
										role.setLocationId(otherRoleCollection.getLocationId());
										role.setHospitalId(otherRoleCollection.getHospitalId());
										if (roles == null)
											roles = new ArrayList<Role>();
										roles.add(role);
									}
									locationAndAccessControl.setRoles(roles);
								}
							}

							if (!isStaff) {
								if (!checkHospitalId.containsKey(locationCollection.getHospitalId().toString())) {
									hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
									Hospital hospital = new Hospital();
									BeanUtil.map(hospitalCollection, hospital);
									hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
									hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
									checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
									hospital.setHospitalUId(hospitalCollection.getHospitalUId());
									hospitals.add(hospital);
								} else {
									Hospital hospital = checkHospitalId.get(locationCollection.getHospitalId().toString());
									hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
									hospital.setHospitalUId(hospitalCollection.getHospitalUId());
									checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
//									hospitals.add(hospital);
								}
							}
						}

						DoctorCollection doctorCollection = doctorRepository.findByUserId(userCollection.getId());

						if (doctorCollection.getSpecialities() != null) {
							List<SpecialityCollection> specialityCollections = (List<SpecialityCollection>) specialityRepository
									.findAllById(doctorCollection.getSpecialities());
							List<String> specialities = (List<String>) CollectionUtils.collect(specialityCollections,
									new BeanToPropertyValueTransformer("superSpeciality"));
							user.setSpecialities(specialities);

							List<String> parentSpecialities = (List<String>) CollectionUtils
									.collect(specialityCollections, new BeanToPropertyValueTransformer("speciality"));
							user.setParentSpecialities(parentSpecialities);

						}
						user.setIsSuperstarAssociated(mongoTemplate.count(new Query(new Criteria("doctorId").is(userCollection.getId())), DoctorSchoolAssociationCollection.class) > 0 ? true : false);
						response = new LoginResponse();
						user.setEmailAddress(user.getUserName());
						response.setUser(user);
						response.setHospitals(hospitals);

					} else {
						logger.warn("None of your clinic is active");
						// user.setUserState(UserState.NOTACTIVATED);
						throw new BusinessException(ServiceError.NotAuthorized, "None of your clinic is active");
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
	public List<RegisteredPatientDetails> loginPatient(LoginPatientRequest request) {
		List<RegisteredPatientDetails> response = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), "USERSTATECOMPLETE");

			for (UserCollection userCollection : userCollections) {
				if (userCollection.getEmailAddress() != null) {
					if (!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						char[] salt = userCollection.getSalt();
						char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
						for (int i = 0; i < request.getPassword().length; i++)
							passwordWithSalt[i] = request.getPassword()[i];
						for (int i = 0; i < salt.length; i++)
							passwordWithSalt[i + request.getPassword().length] = salt[i];
						if (!Arrays.equals(userCollection.getPassword(),
								DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))) {
							logger.warn(loginPatient);
							throw new BusinessException(ServiceError.InvalidInput, loginPatient);
						}
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null, null);
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							BeanUtil.map(patientCollection, user);
							patient.setPatientId(patientCollection.getUserId().toString());
							user.setPatient(patient);
						}
						BeanUtil.map(userCollection, user);
						user.setUserId(userCollection.getId().toString());
						user.setUserNutritionSubscriptions(addUserNutritionSubscriptionResponse(userCollection));
						if (response == null)
							response = new ArrayList<RegisteredPatientDetails>();
						response.add(user);
					}
				} else {
					RegisteredPatientDetails user = new RegisteredPatientDetails();
					char[] salt = userCollection.getSalt();
					if (salt != null && salt.length > 0) {
						char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
						for (int i = 0; i < request.getPassword().length; i++)
							passwordWithSalt[i] = request.getPassword()[i];
						for (int i = 0; i < salt.length; i++)
							passwordWithSalt[i + request.getPassword().length] = salt[i];
						if (!Arrays.equals(userCollection.getPassword(),
								DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))) {
							logger.warn(loginPatient);
							throw new BusinessException(ServiceError.InvalidInput, loginPatient);
						}
					} else {
						logger.warn(loginPatient);
						throw new BusinessException(ServiceError.InvalidInput, loginPatient);
					}
					PatientCollection patientCollection = patientRepository
							.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null, null);
					if (patientCollection != null) {
						Patient patient = new Patient();
						BeanUtil.map(patientCollection, patient);
						BeanUtil.map(patientCollection, user);
						patient.setPatientId(patientCollection.getUserId().toString());
						user.setPatient(patient);
					}
					BeanUtil.map(userCollection, user);
					user.setUserNutritionSubscriptions(addUserNutritionSubscriptionResponse(userCollection));
					user.setUserId(userCollection.getId().toString());

					if (response == null)
						response = new ArrayList<RegisteredPatientDetails>();
					response.add(user);

				}

			}

			if (response == null) {
				logger.warn(loginPatient);
				throw new BusinessException(ServiceError.InvalidInput, loginPatient);
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

	@Override
	@Transactional
	public List<RegisteredPatientDetails> loginPatientByOtp(LoginPatientRequest request) {
		List<RegisteredPatientDetails> response = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), "USERSTATECOMPLETE");
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (userCollection.getEmailAddress() != null) {
						if(!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName()) && !DPDoctorUtils.anyStringEmpty(request.getOtpNumber())) {
							Boolean verifyOTPResponse = false;
							if (!verifyOTPResponse) {
								verifyOTPResponse = otpService.verifyOTP(request.getMobileNumber(),
										request.getOtpNumber());
								if (!verifyOTPResponse) {
									logger.warn(loginPatient);
									throw new BusinessException(ServiceError.InvalidInput, loginPatient);
								}
							}
							RegisteredPatientDetails user = new RegisteredPatientDetails();
							PatientCollection patientCollection = patientRepository
									.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null,
											null);
							if (patientCollection != null) {
								Patient patient = new Patient();
								BeanUtil.map(patientCollection, patient);
								BeanUtil.map(patientCollection, user);
								patient.setPatientId(patientCollection.getUserId().toString());
								user.setPatient(patient);
							}
							BeanUtil.map(userCollection, user);
							user.setUserNutritionSubscriptions(addUserNutritionSubscriptionResponse(userCollection));
							user.setUserId(userCollection.getId().toString());

							if (response == null)
								response = new ArrayList<RegisteredPatientDetails>();
							response.add(user);

						}

					} else {

						RegisteredPatientDetails user = new RegisteredPatientDetails();
						if (!DPDoctorUtils.anyStringEmpty(request.getOtpNumber())) {
							Boolean verifyOTPResponse = false;
							if (!verifyOTPResponse) {
								verifyOTPResponse = otpService.verifyOTP(request.getMobileNumber(),
										request.getOtpNumber());
								if (!verifyOTPResponse) {
									logger.warn(loginPatient);
									throw new BusinessException(ServiceError.InvalidInput, loginPatient);
								}
							}
							PatientCollection patientCollection = patientRepository
									.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(userCollection.getId(), null, null,
											null);
							if (patientCollection != null) {
								Patient patient = new Patient();
								BeanUtil.map(patientCollection, patient);
								BeanUtil.map(patientCollection, user);
								patient.setPatientId(patientCollection.getUserId().toString());
								user.setPatient(patient);
							}
							BeanUtil.map(userCollection, user);
							user.setUserNutritionSubscriptions(addUserNutritionSubscriptionResponse(userCollection));
							user.setUserId(userCollection.getId().toString());

							if (response == null)
								response = new ArrayList<RegisteredPatientDetails>();
							response.add(user);

						}
					}
				}
			} else {
				boolean verifyOTPResponse = otpService.verifyOTP(request.getMobileNumber(), request.getOtpNumber());
				if (!verifyOTPResponse) {
					logger.warn(loginPatient);
					throw new BusinessException(ServiceError.InvalidInput, loginPatient);
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

	@Override
	@Transactional
	public Boolean adminLogin(String mobileNumber) {
		Boolean response = false;
		try {
			/*
			 * RoleCollection roleCollection =
			 * roleRepository.findByRole(RoleEnum.SUPER_ADMIN.getRole()); if (roleCollection
			 * == null) { logger.warn(role); throw new
			 * BusinessException(ServiceError.NoRecord, role); } List<UserRoleCollection>
			 * userRoleCollections =
			 * userRoleRepository.findByRoleId(roleCollection.getId());
			 * 
			 * @SuppressWarnings("unchecked") Collection<String> userIds =
			 * CollectionUtils.collect(userRoleCollections, new
			 * BeanToPropertyValueTransformer("userId"));
			 */
			/*
			 * Criteria criteria = new
			 * Criteria("mobileNumber").is(request.getMobileNumber()).and("id").
			 * in(userIds); Query query = new Query(); query.addCriteria(criteria);
			 * List<UserCollection> userCollections = mongoTemplate.find(query,
			 * UserCollection.class);
			 */
			// UserCollection userCollection = null;
			// if(userCollections != null &&
			// !userCollections.isEmpty())userCollection =
			// userCollections.get(0);
			/*
			 * if (userCollections == null || userCollections.isEmpty()) {
			 * logger.warn("Invalid mobile Number and Password"); throw new
			 * BusinessException(ServiceError.InvalidInput,
			 * "Invalid mobile Number and Password"); }else{
			 * 
			 * for(UserCollection userCollection : userCollections){ char[] salt =
			 * userCollection.getSalt(); char[] passwordWithSalt = new
			 * char[request.getPassword().length + salt.length]; for(int i = 0; i <
			 * request.getPassword().length; i++) passwordWithSalt[i] =
			 * request.getPassword()[i]; for(int i = 0; i < salt.length; i++)
			 * passwordWithSalt[i+request.getPassword().length] = salt[i];
			 * if(Arrays.equals(userCollection.getPassword(),
			 * DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))){
			 * userCollection.setLastSession(new Date()); userCollection =
			 * userRepository.save(userCollection); response = new User();
			 * BeanUtil.map(userCollection, response); } } }
			 */
			/*
			 * if(response == null){ logger.warn(login); throw new
			 * BusinessException(ServiceError.Unknown, login); }
			 */
			UserCollection userCollection = null;
			List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(mobileNumber,
					UserState.ADMIN.getState());
			if(userCollections!= null && !userCollections.isEmpty())userCollection = userCollections.get(0);

			if (userCollection == null) {
				throw new BusinessException(ServiceError.NotAuthorized, "Admin with provided mobile number not found");
			} else {
				response = otpService.otpGenerator(mobileNumber, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while login");
			throw new BusinessException(ServiceError.Unknown, "Error occured while login");
		}
		return response;
	}

	@Override
	public Boolean isLocationAdmin(LoginRequest request) {
		Boolean response = false;
		try {
			Criteria criteria = new Criteria("userName").is(request.getUsername());
			Query query = new Query();
			query.addCriteria(criteria);
			UserCollection userCollection = mongoTemplate.findById(query, UserCollection.class);

			if (userCollection == null) {
				return response;
			} else {
				char[] salt = userCollection.getSalt();
				if (salt != null && salt.length > 0) {
					char[] passwordWithSalt = new char[request.getPassword().length + salt.length];
					for (int i = 0; i < request.getPassword().length; i++)
						passwordWithSalt[i] = request.getPassword()[i];
					for (int i = 0; i < salt.length; i++)
						passwordWithSalt[i + request.getPassword().length] = salt[i];
					if (!Arrays.equals(userCollection.getPassword(),
							DPDoctorUtils.getSHA3SecurePassword(passwordWithSalt))) {
						return response;
					}
				} else {
					return response;
				}
			}

			List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("userId").is(userCollection.getId()).and("locationId")
									.is(new ObjectId(request.getLocationId()))),
							Aggregation.lookup("role_cl", "roleId", "_id", "roleCollection"),
							Aggregation.unwind("roleCollection"),
							Aggregation.match(new Criteria("roleCollection.role").is("LOCATION_ADMIN"))),
					UserRoleCollection.class, UserRoleLookupResponse.class).getMappedResults();
			if (userRoleLookupResponses != null && !userRoleLookupResponses.isEmpty())
				response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while checking is Location Admin");
			throw new BusinessException(ServiceError.Unknown, "Error occured while checking is Location Admin");
		}
		return response;
	}


	@Override
	public DoctorLoginPin AddEditLoginPin(DoctorLoginPin request) {
		DoctorLoginPin response = null;

		DoctorLoginPinCollection olddoctorLoginPinCollection = null;

		try {
			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "invalid DoctorId");
			}
			olddoctorLoginPinCollection = doctorLoginPinRepository.findByDoctorId(new ObjectId(request.getDoctorId()));

			if (olddoctorLoginPinCollection == null) {
				olddoctorLoginPinCollection = new DoctorLoginPinCollection();
				BeanUtil.map(request, olddoctorLoginPinCollection);
				olddoctorLoginPinCollection.setCreatedTime(new Date());
				olddoctorLoginPinCollection
						.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? "Dr." : doctor.getTitle())
								+ doctor.getFirstName());

			} else {

				olddoctorLoginPinCollection.setPin(request.getPin());
				olddoctorLoginPinCollection.setUpdatedTime(new Date());

			}
			doctorLoginPinRepository.save(olddoctorLoginPinCollection);
			response = new DoctorLoginPin();
			BeanUtil.map(olddoctorLoginPinCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while add edit Login Pin");
			throw new BusinessException(ServiceError.Unknown, " Error occured while add edit Login Pin");

		}
		return response;
	}

	@Override
	public DoctorLoginPin getLoginPin(String doctorId) {
		DoctorLoginPin response = null;
		try {
			DoctorLoginPinCollection doctorLoginPinCollection = doctorLoginPinRepository
					.findByDoctorId(new ObjectId(doctorId));
			response = new DoctorLoginPin();
			BeanUtil.map(doctorLoginPinCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while get Doctor Login Pin");
			throw new BusinessException(ServiceError.Unknown, " Error occured while get Doctor Login Pin");

		}
		return response;
	}

	@Override
	public Boolean checkLoginPin(DoctorLoginPinRequest request) {
		Boolean response = false;
		try {
			DoctorLoginPinCollection doctorLoginPinCollection = doctorLoginPinRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));
			if (doctorLoginPinCollection != null) {
				if (request.getPin().equals(doctorLoginPinCollection.getPin()))
					response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while checking Doctor Login Pin");
			throw new BusinessException(ServiceError.Unknown, " Error occured while checking Doctor Login Pin");

		}
		return response;
	}

	private List<UserNutritionSubscriptionResponse> addUserNutritionSubscriptionResponse(
			UserCollection userCollection) {

		Criteria criteria = new Criteria();

		criteria.and("userId").is(userCollection.getId()).and("discarded").is(false).and("isExpired").is(false);
		criteria.and("transactionStatus").is("Success");
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

				Aggregation.lookup("subscription_nutrition_plan_cl", "subscriptionPlanId", "_id", "subscriptionPlan"),
				Aggregation.unwind("subscriptionPlan"),
				Aggregation.lookup("nutrition_plan_cl", "nutritionPlanId", "_id", "NutritionPlan"),
				Aggregation.unwind("NutritionPlan"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

		AggregationResults<UserNutritionSubscriptionResponse> results = mongoTemplate.aggregate(aggregation,
				UserNutritionSubscriptionCollection.class, UserNutritionSubscriptionResponse.class);
		List<UserNutritionSubscriptionResponse> userNutritionSubscriptionResponse = results.getMappedResults();
		if (userNutritionSubscriptionResponse != null) {
			for (UserNutritionSubscriptionResponse nutritionSubscriptionResponse : userNutritionSubscriptionResponse) {
				NutritionPlan nutritionPlan = nutritionSubscriptionResponse.getNutritionPlan();
				if (nutritionPlan != null) {
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getBannerImage())) {
						nutritionPlan.setBannerImage(getFinalImageURL(nutritionPlan.getBannerImage()));
					}
					if (!DPDoctorUtils.anyStringEmpty(nutritionPlan.getPlanImage())) {
						nutritionPlan.setPlanImage(getFinalImageURL(nutritionPlan.getPlanImage()));
					}
					nutritionSubscriptionResponse.setNutritionPlan(nutritionPlan);
				}
				SubscriptionNutritionPlan subscriptionNutritionPlan = nutritionSubscriptionResponse
						.getSubscriptionPlan();
				if (subscriptionNutritionPlan != null) {

					if (!DPDoctorUtils.anyStringEmpty(subscriptionNutritionPlan.getBackgroundImage())) {
						subscriptionNutritionPlan
								.setBackgroundImage(getFinalImageURL(subscriptionNutritionPlan.getBackgroundImage()));
					}
					nutritionSubscriptionResponse.setSubscriptionPlan(subscriptionNutritionPlan);
				}

			}

		}
		return userNutritionSubscriptionResponse;
	}
}
