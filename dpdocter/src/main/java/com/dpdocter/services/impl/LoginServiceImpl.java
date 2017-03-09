package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
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
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.LoginPatientRequest;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.LoginResponse;
import com.dpdocter.response.OAuth2TokenResponse;
import com.dpdocter.response.OauthRefreshTokenRequest;
import com.dpdocter.response.PatientLoginResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.LoginService;
import com.dpdocter.services.OTPService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private OTPService otpService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Login.login}")
	private String login;

	@Value(value = "${Login.loginPatient}")
	private String loginPatient;

	@Value(value = "${Login.server.url}")
	private String serverIp;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${Signup.role}")
	private String role;

	/**
	 * This method is used for login purpose.
	 */

	private String getLoginResponse(LoginRequest loginRequest) {

		StringBuffer response = new StringBuffer();
		try {
			String password = new String(loginRequest.getPassword());
			String url = serverIp + "/dpdoctor/oauth/token?grant_type=" + loginRequest.getGrantType() + "&client_id="
					+ loginRequest.getGrantType() + "+&client_secret=" + loginRequest.getClientSecret() + "&username="
					+ loginRequest.getUsername() + "&password=" + new String(loginRequest.getPassword());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is POST
			con.setRequestMethod("POST");

			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */

			while ((inputLine = in.readLine()) != null) {

				response.append(inputLine);

			}
			in.close();

		} catch (Exception e) {

			e.printStackTrace();
			return "Failed";
		}

		return response.toString();

	}

	@Override
	@Transactional
	public LoginResponse login(LoginRequest request, Boolean isMobileApp) {
		LoginResponse response = null;
		try {
			String tokenResponse = getLoginResponse(request);
			OAuth2TokenResponse oauth2TokenResponse = new OAuth2TokenResponse();
			if (tokenResponse.equalsIgnoreCase("Failed")) {
				logger.warn(login);
				throw new BusinessException(ServiceError.InvalidInput, login);

			}
			ObjectMapper mapper = new ObjectMapper();
			oauth2TokenResponse = mapper.readValue(tokenResponse, OAuth2TokenResponse.class);

			Criteria criteria = new Criteria("userName").regex(request.getUsername(), "i");
			Query query = new Query();
			query.addCriteria(criteria);
			List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
			UserCollection userCollection = null;
			if (userCollections != null && !userCollections.isEmpty())
				userCollection = userCollections.get(0);

			if (userCollection == null) {
				logger.warn(login);
				throw new BusinessException(ServiceError.InvalidInput, login);
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
					Aggregation.newAggregation(Aggregation.match(new Criteria("userId").is(userCollection.getId())),
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

					List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(
											Aggregation.match(new Criteria("doctorId").is(userCollection.getId())
													.and("isActivate").is(true)),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.lookup("hospital_cl", "$location.hospitalId", "_id", "hospital"),
									Aggregation.unwind("hospital")), DoctorClinicProfileCollection.class,
									DoctorClinicProfileLookupResponse.class)
							.getMappedResults();
					if (doctorClinicProfileLookupResponses != null) {
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
							List<Role> roles = null;

							Boolean isStaff = false;
							for (UserRoleLookupResponse otherRoleCollection : userRoleLookupResponses) {
								if ((otherRoleCollection.getRoleCollection().getHospitalId() == null
										&& otherRoleCollection.getRoleCollection().getLocationId() == null)
										|| (otherRoleCollection.getRoleCollection().getHospitalId().toString()
												.equalsIgnoreCase(hospitalCollection.getId().toString())
												&& otherRoleCollection.getRoleCollection().getLocationId().toString()
														.equalsIgnoreCase(locationCollection.getId().toString()))) {
									if (isMobileApp && doctorClinicProfileLookupResponses.size() == 1
											&& !(otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())
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
													.equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole())
											|| otherRoleCollection.getRoleCollection().getRole()
													.equalsIgnoreCase(RoleEnum.SUPER_ADMIN.getRole()))) {
										isStaff = true;
									}

									if (otherRoleCollection != null) {
										AccessControl accessControl = accessControlServices.getAccessControls(
												new ObjectId(otherRoleCollection.getId()), locationCollection.getId(),
												locationCollection.getHospitalId());

										Role role = new Role();
										BeanUtil.map(otherRoleCollection.getRoleCollection(), role);
										role.setAccessModules(accessControl.getAccessModules());

										if (roles == null)
											roles = new ArrayList<Role>();
										roles.add(role);
									}
									locationAndAccessControl.setRoles(roles);
								}
							}
							if (!isStaff) {
								if (!checkHospitalId.containsKey(locationCollection.getHospitalId())) {
									hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
									Hospital hospital = new Hospital();
									BeanUtil.map(hospitalCollection, hospital);
									hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
									hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
									checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
									hospitals.add(hospital);
								} else {
									Hospital hospital = checkHospitalId.get(locationCollection.getHospitalId());
									hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
									hospitals.add(hospital);
								}
							}
						}
						response = new LoginResponse();
						response.setTokens(oauth2TokenResponse);
						user.setEmailAddress(user.getUserName());
						response.setUser(user);
						response.setHospitals(hospitals);

					} else {
						logger.warn("None of your clinic is active");
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
	public PatientLoginResponse loginPatient(LoginPatientRequest request) {
		PatientLoginResponse response = null;
		List<RegisteredPatientDetails> detail = null;

		try {
			LoginRequest loginRequest = new LoginRequest();
			BeanUtil.map(request, loginRequest);
			loginRequest.setUsername(request.getMobileNumber());
			String tokenResponse = getLoginResponse(loginRequest);
			OAuth2TokenResponse oauth2TokenResponse = new OAuth2TokenResponse();
			if (tokenResponse.equalsIgnoreCase("Failed")) {
				logger.warn(login);
				throw new BusinessException(ServiceError.InvalidInput, login);
			}
			ObjectMapper mapper = new ObjectMapper();
			oauth2TokenResponse = mapper.readValue(tokenResponse, OAuth2TokenResponse.class);
			Criteria criteria = new Criteria("mobileNumber").is(request.getMobileNumber());
			Query query = new Query();
			query.addCriteria(criteria);
			List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);

			for (UserCollection userCollection : userCollections) {

				RegisteredPatientDetails user = new RegisteredPatientDetails();

				PatientCollection patientCollection = patientRepository
						.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(), null, null, null);
				if (patientCollection != null) {
					Patient patient = new Patient();
					BeanUtil.map(patientCollection, patient);
					BeanUtil.map(patientCollection, user);
					patient.setPatientId(patientCollection.getUserId().toString());
					user.setPatient(patient);
				}
				BeanUtil.map(userCollection, user);
				user.setUserId(userCollection.getId().toString());
				if (detail == null)
					detail = new ArrayList<RegisteredPatientDetails>();
				detail.add(user);
			}
			if (detail == null) {
				logger.warn(loginPatient);
				throw new BusinessException(ServiceError.InvalidInput, loginPatient);
			}
			response = new PatientLoginResponse();
			response.setDetail(detail);
			response.setTokens(oauth2TokenResponse);
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
			 * roleRepository.findByRole(RoleEnum.SUPER_ADMIN.getRole()); if
			 * (roleCollection == null) { logger.warn(role); throw new
			 * BusinessException(ServiceError.NoRecord, role); }
			 * List<UserRoleCollection> userRoleCollections =
			 * userRoleRepository.findByRoleId(roleCollection.getId());
			 * 
			 * @SuppressWarnings("unchecked") Collection<String> userIds =
			 * CollectionUtils.collect(userRoleCollections, new
			 * BeanToPropertyValueTransformer("userId"));
			 */
			/*
			 * Criteria criteria = new
			 * Criteria("mobileNumber").is(request.getMobileNumber()).and("id").
			 * in(userIds); Query query = new Query();
			 * query.addCriteria(criteria); List<UserCollection> userCollections
			 * = mongoTemplate.find(query, UserCollection.class);
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
			 * for(UserCollection userCollection : userCollections){ char[] salt
			 * = userCollection.getSalt(); char[] passwordWithSalt = new
			 * char[request.getPassword().length + salt.length]; for(int i = 0;
			 * i < request.getPassword().length; i++) passwordWithSalt[i] =
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
			UserCollection userCollection = userRepository.findAdminByMobileNumber(mobileNumber,
					UserState.ADMIN.getState());
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
	public String refreshToken(OauthRefreshTokenRequest request) {

		StringBuffer response = new StringBuffer();
		try {

			String url = serverIp + "/dpdocter/oauth/token?grant_type=" + request.getGrantType() + "&client_id="
					+ request.getGrantType() + "+&client_secret=" + request.getClientSecret() + "&refresh_token=="
					+ request.getRefreshToken();
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is POST
			con.setRequestMethod("POST");

			// add request header
			// con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			/* response = new StringBuffer(); */

			while ((inputLine = in.readLine()) != null) {

				response.append(inputLine);

			}
			in.close();

		} catch (Exception e) {

			e.printStackTrace();
			return "Failed";
		}

		return response.toString();

	}
}
