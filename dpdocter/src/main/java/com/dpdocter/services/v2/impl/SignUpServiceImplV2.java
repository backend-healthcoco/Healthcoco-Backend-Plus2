package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.v2.DoctorSignupRequest;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.DoctorOtpSignUpCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.PCUserCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.repository.DoctorOtpSignUpRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.PCUserRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.DoctorOtpRequest;
import com.dpdocter.response.DoctorRegisterResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.ForgotPasswordService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.v2.SignUpService;
import com.dpdocter.tokenstore.CustomPasswordEncoder;
import com.mongodb.DuplicateKeyException;

import common.util.web.DPDoctorUtils;
import common.util.web.LoginUtils;
@Service
public class SignUpServiceImplV2 implements SignUpService{
	
	private static Logger logger = Logger.getLogger(SignUpServiceImplV2.class.getName());
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CustomPasswordEncoder passwordEncoder;
	
	@Autowired
	private AccessControlServices accessControlServices;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private SpecialityRepository specialityRepository;
	
	@Autowired
	private PCUserRepository pcUserRepository;
	
	@Autowired
	private DoctorRepository doctorRepository;
	
	@Autowired
	private HospitalRepository hospitalRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private TokenRepository tokenRepository;
	
	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
	@Autowired
	private DoctorOtpSignUpRepository doctorOtpSignUpRepository;


	@Autowired
	private OTPRepository otpRepository;
	
	@Autowired
	private SMSServices smsServices;

	@Autowired
	PushNotificationServices pushNotificationServices;
	
	@Autowired
	OTPService otpServices;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Value(value = "${mail.signup.request.subject}")
	private String signupRequestSubject;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	

	@Override
	public DoctorRegisterResponse DoctorRegister(DoctorOtpRequest request) {
		DoctorRegisterResponse response=null;
		try {
		
		DoctorOtpSignUpCollection doctorOtpSignUCollection=new DoctorOtpSignUpCollection();
		doctorOtpSignUCollection.setCreatedTime(new Date());
		doctorOtpSignUCollection.setMobileNumber(request.getMobileNumber());
		doctorOtpSignUCollection.setCountryCode(request.getCountryCode());
		doctorOtpSignUpRepository.save(doctorOtpSignUCollection);
			
				 response=new DoctorRegisterResponse();
				 BeanUtil.map(doctorOtpSignUCollection, response);
				 
				 System.out.println(doctorOtpSignUCollection);
				 otpGenerator(request.getMobileNumber(),request.getCountryCode());
				 
			 
		}
	 catch (Exception e) {
		e.printStackTrace();
		logger.error(e + " Error occured while generating otp through mobile number");
		throw new BusinessException(ServiceError.Unknown, "Error occured while generating mobile number "+e.getMessage());
	}
		return response;
	}
	
	  public Boolean otpGenerator(String mobileNumber,String countryCode) {
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
			smsTrackDetail.setTemplateId("1307161191067443701");
			smsServices.sendOTPSMS(smsTrackDetail, true);

		    OTPCollection otpCollection = new OTPCollection();
		    otpCollection.setCreatedTime(new Date());
		    otpCollection.setOtpNumber(OTP);
		    otpCollection.setGeneratorId(mobileNumber);
		    otpCollection.setMobileNumber(mobileNumber);
		    otpCollection.setCountryCode(countryCode);
		    otpCollection.setCreatedBy(mobileNumber);
		    otpCollection = otpRepository.save(otpCollection);

		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e + " Error While Generating OTP");
		    throw new BusinessException(ServiceError.Unknown, "Error While Generating OTP "+e.getMessage());
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
			List<UserCollection>userCollections=userRepository.findByEmailAddressIgnoreCase(request.getEmailAddress());
			
			if(userCollections!=null && !userCollections.isEmpty())
				if(userCollections.get(0).getEmailAddress() !=null && userCollections.get(0).getPassword()!=null)
				{
					throw new BusinessException(ServiceError.Unknown,
							"Account with this emailId "+ userCollections.get(0).getEmailAddress()+" already exists,Please login or use forgot password.");
			
				}
			if (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())) {
				logger.warn("Email Address cannot be null");
				throw new BusinessException(ServiceError.InvalidInput, "Email Address cannot be null");
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

			userCollection.setUserState(UserState.NOTVERIFIED);
			userCollection.setIsVerified(false);
			if(request.getPassword()!=null&&request.getPassword().length>0)
			userCollection.setPassword(passwordEncoder.encode(String.valueOf(request.getPassword())).toCharArray());
//			userCollection.setPassword(request.getPassword());
			userCollection.setIsPasswordSet(true);
			userCollection.setSignedUp(true);
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
			locationCollection.setLocationName(request.getLocationName());
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
			doctorClinicProfileCollection.setIsSuperAdmin(true);
			doctorClinicProfileCollection.setIsActivate(true);
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
			
					userRoleRepository.saveAll(userRoleCollections);

			
			// save token
			TokenCollection tokenCollection = new TokenCollection();
			tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
			tokenCollection.setCreatedTime(new Date());
			tokenCollection = tokenRepository.save(tokenCollection);

			
			//verify user
			
			
			// send activation email
			String body = mailBodyGenerator.verifyEmailBody(
					(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")+ userCollection.getFirstName(),
					tokenCollection.getId(), "verifyDoctor.vm");
	Boolean mail=	mailService.sendEmail(userCollection.getEmailAddress(),signupRequestSubject, body, null);
			System.out.println(mail);
			
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
//			pushNotificationServices.notifyUser(userCollection.getId().toString(),
//					"Your emailId has been verified successfully.", ComponentType.EMAIL_VERIFICATION.getType(), null, null);

			List<User>users=notificationToAdmin();
			System.out.println("usersAdmin"+users);
				for(User userr:users)
				{
					pushNotificationServices.notifyUser(userr.getId().toString(),
							"NEW Doctor has been Signed up ", ComponentType.SIGNED_UP.getType(), null, null);

							}
			
			
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Email address already registerd. Please login");
		} catch (BusinessException be) {
			logger.error(be);
			//throw be;
			be.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, be.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while creating doctor");
			throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor" + e.getMessage());
		}
		return response;
	}

	
	List<User> notificationToAdmin()
	{
		
		Criteria criteria = new Criteria("userState").is("ADMIN");
		criteria.and("isAnonymousAppointment").is(true);
	//	criteria.and("signedUp").is(true);
		
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.sort(Sort.Direction.DESC, "createdTime"));
		
		System.out.println("AdminAggregation"+aggregation);
		List<User> user=mongoTemplate.aggregate(aggregation, UserCollection.class, User.class).getMappedResults();
		return user;

	}

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
				
				pushNotificationServices.notifyUser(userCollection.getId().toString(),
						"Your Email has been verified by healthcoco", ComponentType.EMAIL_VERIFICATION.getType(), null, null);


				
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
	public Boolean resendVerificationEmail(String emailaddress) {
		UserCollection userCollection = null;
		Boolean response = false;
		try {
			Criteria criteria = new Criteria("userName").regex(emailaddress, "i");
			Query query = new Query();
			query.addCriteria(criteria);
			List<UserCollection> userCollections = mongoTemplate.find(query, UserCollection.class);
			if (userCollections != null && !userCollections.isEmpty())
				userCollection = userCollections.get(0);

			if (userCollection != null) {
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByDoctorId(userCollection.getId());
				DoctorClinicProfileCollection doctorClinicProfileCollection = null;
				if (doctorClinicProfileCollections != null && !doctorClinicProfileCollections.isEmpty()) {
					doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);
					// save token
					TokenCollection tokenCollection = new TokenCollection();
					tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
					tokenCollection.setCreatedTime(new Date());
					tokenCollection = tokenRepository.save(tokenCollection);

					// send activation email
					String body = mailBodyGenerator.verifyEmailBody(
							(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")+ userCollection.getFirstName(),
							tokenCollection.getId(), "verifyDoctor.vm");
			Boolean mail=	mailService.sendEmail(userCollection.getEmailAddress(),signupRequestSubject, body, null);
					System.out.println(mail);
					response = true;
				}

			} else {
				logger.error("User Not Found For The Given User Id");
				throw new BusinessException(ServiceError.NotFound, "User Not Found For The Given User Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While sending verification email");
			throw new BusinessException(ServiceError.Unknown, "Error While sending verification email");
		}
		return response;
	}


	
}
