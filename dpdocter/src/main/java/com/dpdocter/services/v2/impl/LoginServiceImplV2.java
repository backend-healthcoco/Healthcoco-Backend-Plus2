package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.LoginResponse;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.AuditActionType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ForgotUsernamePasswordRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.ForgotPasswordResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.AuditService;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.v2.LoginService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;

@Service
public class LoginServiceImplV2 implements LoginService {

	private static Logger logger = Logger.getLogger(LoginServiceImplV2.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

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
	@Autowired
	private AuditService auditService;

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
				if (userCollection.getIsPasswordSet() == null || !userCollection.getIsPasswordSet()) {
					ForgotUsernamePasswordRequest forgotUsernamePasswordRequest = new ForgotUsernamePasswordRequest();
					forgotUsernamePasswordRequest.setEmailAddress(request.getUsername());
					forgotUsernamePasswordRequest.setUsername(request.getUsername());
					ForgotPasswordResponse forgotPasswordResponse = forgotPasswordService
							.forgotPasswordForDoctor(forgotUsernamePasswordRequest);
					if (forgotPasswordResponse != null) {
						logger.warn("Please reset your password and check your email to update your password");
						throw new BusinessException(ServiceError.InvalidInput,
								"Please reset your password and check your email to update your password");
					}
				}
				boolean isPasswordCorrect = new CustomPasswordEncoder().matches(String.valueOf(request.getPassword()),
						String.valueOf(userCollection.getPassword()));

				if (!isPasswordCorrect) {
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
					userCollection.setLastSession(new Date());
					userCollection = userRepository.save(userCollection);
					criteria = new Criteria("doctorId").is(userCollection.getId())

							.and("isActivate").is(true).and("hasLoginAccess").ne(false);
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

						logger.warn(
								"None of your clinic is active or or you dont have login access,please contact your admin.");
						// user.setUserState(UserState.NOTACTIVATED);
						throw new BusinessException(ServiceError.NotAuthorized,
								"None of your clinic is active or you dont have login access,please contact your admin.");

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
							locationAndAccessControl
									.setIsNutritionist(doctorClinicProfileLookupResponse.getIsAdminNutritionist());
							locationAndAccessControl
									.setIsAdminNutritionist(doctorClinicProfileLookupResponse.getIsAdminNutritionist());
							locationAndAccessControl.setIsRegisteredNDHMFacility(
									doctorClinicProfileLookupResponse.getIsRegisteredNDHMFacility());
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
									user.setIsSuperAdmin(doctorClinicProfileLookupResponse.getIsSuperAdmin());
									if (otherRoleCollection != null) {
										AccessControl accessControl = accessControlServices.getAccessControls(
												otherRoleCollection.getRoleCollection().getId(),
												otherRoleCollection.getRoleCollection().getLocationId(),
												otherRoleCollection.getRoleCollection().getHospitalId());
										// set is show patient number true for super admin
										if (doctorClinicProfileLookupResponse.getIsSuperAdmin()) {
											user.setIsShowPatientNumber(true);
											user.setIsShowDoctorInCalender(true);
										} else {
											user.setIsShowPatientNumber(
													doctorClinicProfileLookupResponse.getIsShowPatientNumber());
											user.setIsShowDoctorInCalender(
													doctorClinicProfileLookupResponse.getIsShowDoctorInCalender());
										}
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
									Hospital hospital = checkHospitalId
											.get(locationCollection.getHospitalId().toString());
									hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
									hospital.setHospitalUId(hospitalCollection.getHospitalUId());
									checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
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
							user.setIsTransactionalSms(doctorCollection.getIsTransactionalSms());
						}

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
			String doctorId = userCollection.getId().toString();
			String locationId = response.getUser().getLocationId();
			String hospitalId = response.getUser().getHospitalId();

			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					auditService.addAuditData(AuditActionType.LOGIN, null, doctorId, null, doctorId, locationId,
							hospitalId);

				}
			});
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while login");
			throw new BusinessException(ServiceError.Unknown, "Error occured while login" + e.getMessage());
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
}
