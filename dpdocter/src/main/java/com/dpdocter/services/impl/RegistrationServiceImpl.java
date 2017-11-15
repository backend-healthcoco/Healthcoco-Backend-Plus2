
package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.Address;
import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.ConsentForm;
import com.dpdocter.beans.ConsentFormItemJasperdetails;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.FormContent;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.beans.UserAddress;
import com.dpdocter.beans.UserReminders;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ConsentFormCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.collections.FormContentCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProfessionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserAddressCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRemindersCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.ReminderType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UnitType;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ConsentFormRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FeedbackRepository;
import com.dpdocter.repository.FormContentRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProfessionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.TokenRepository;
import com.dpdocter.repository.UserAddressRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRemindersRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.request.DoctorRegisterRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.CheckPatientSignUpResponse;
import com.dpdocter.response.ClinicDoctorResponse;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientCollectionResponse;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.response.RegisterDoctorResponse;
import com.dpdocter.response.UserLookupResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private static Logger logger = Logger.getLogger(RegistrationServiceImpl.class.getName());

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ESPatientRepository esPatientRepository;
	
	@Autowired
	private GenerateUniqueUserNameService generateUniqueUserNameService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PatientVisitService patientVisitService;

	// @Autowired
	// private GroupRepository groupRepository;

	@Autowired
	private PatientGroupRepository patientGroupRepository;

	@Autowired
	private ReferenceRepository referrenceRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserLocationRepository userLocationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private ProfessionRepository professionRepository;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private FeedbackRepository feedbackRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private OTPService otpService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private ESRegistrationService esRegRistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ConsentFormRepository consentFormRepository;

	@Autowired
	private FormContentRepository formContentRepository;

	@Autowired
	private UserRemindersRepository userRemindersRepository;

	@Autowired
	private UserAddressRepository userAddressRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private EmailTackService emailTackService;

	@Value(value = "${jasper.print.consentForm.a4.fileName}")
	private String consentFormA4FileName;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Value(value = "${mail.staffmember.account.verify.subject}")
	private String staffmemberAccountVerifySub;

	@Value(value = "${mail.add.existing.doctor.to.clinic.subject}")
	private String addExistingDoctorToClinicSub;

	@Value(value = "${mail.add.doctor.to.clinic.verify.subject}")
	private String addDoctorToClinicVerifySub;

	@Value(value = "${mail.add.feedback.subject}")
	private String addFeedbackSubject;

	@Value(value = "${mail.add.feedback.for.doctor.subject}")
	private String addFeedbackForDoctorSubject;

	@Value(value = "${patient.count}")
	private String patientCount;

	@Value(value = "${Register.checkPatientCount}")
	private String checkPatientCount;

	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.role.not.found}")
	private String roleNotFoundException;

	@Value(value = "${user.reminder.not.found}")
	private String reminderNotFoundException;

	@Override
	@Transactional
	public User checkIfPatientExist(PatientRegistrationRequest request) {
		try {
			UserCollection userCollection = userRepository.checkPatient(request.getFirstName(),
					request.getEmailAddress(), request.getMobileNumber());
			if (userCollection != null) {
				User user = new User();
				BeanUtil.map(userCollection, user);
				return user;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return null;

	}

	@Override
	@Transactional
	public RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		List<Group> groups = null;
		try {
			// get role of specified type
			RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
			if (roleCollection == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}
			request.setFirstName(request.getLocalPatientName());
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			Date createdTime = new Date();

			CheckPatientSignUpResponse checkPatientSignUpResponse = checkIfPatientIsSignedUp(request.getMobileNumber());
			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			if (request.getDob() != null && request.getDob().getAge() != null
					&& request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.InvalidInput, DOB);
			} else if (request.getDob() == null && request.getAge() != null) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
				request.setDob(new DOB(currentDay, currentMonth, currentYear));
			}
			User user = new User();
			BeanUtil.map(request, user);
			user.setFirstName(request.getLocalPatientName());
			String uniqueUserName = generateUniqueUserNameService.generate(user);
			userCollection.setUserName(uniqueUserName);
			userCollection.setPassword(generateRandomAlphanumericString(10));
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setIsActive(true);
			userCollection.setCreatedTime(createdTime);
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			if (checkPatientSignUpResponse != null) {
				userCollection.setSignedUp(checkPatientSignUpResponse.isSignedUp());
				userCollection.setPassword(checkPatientSignUpResponse.getPassword());
				userCollection.setSalt(checkPatientSignUpResponse.getSalt());
			}
			userCollection.setFirstName(request.getLocalPatientName());
			userCollection = userRepository.save(userCollection);

			// assign roles
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(),
					roleCollection.getId(), null, null);
			userRoleCollection.setCreatedTime(new Date());
			userRoleRepository.save(userRoleCollection);

			if (checkPatientSignUpResponse != null) {
				PatientCollection patientCollection = new PatientCollection();
				patientCollection.setCreatedTime(new Date());
				patientCollection.setFirstName(request.getLocalPatientName());
				patientCollection.setLocalPatientName(request.getLocalPatientName());
				patientCollection.setUserId(userCollection.getId());
				patientRepository.save(patientCollection);
			}
			// save Patient Info
			PatientCollection patientCollection = new PatientCollection();
			BeanUtil.map(request, patientCollection);
			patientCollection.setFirstName(request.getLocalPatientName());
			patientCollection.setUserId(userCollection.getId());
			if (request.getRegistrationDate() != null)
				patientCollection.setRegistrationDate(request.getRegistrationDate());
			else
				patientCollection.setRegistrationDate(new Date().getTime());

			System.out.println("registerNewPatient"+request.getRegistrationDate()+".."+patientCollection.getRegistrationDate());
			patientCollection.setCreatedTime(createdTime);
			patientCollection.setPID(patientIdGenerator(request.getLocationId(), request.getHospitalId(),
					patientCollection.getRegistrationDate()));

			// if(RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())){
			List<ObjectId> consultantDoctorIds = new ArrayList<ObjectId>();
			consultantDoctorIds.add(new ObjectId(request.getDoctorId()));
			patientCollection.setConsultantDoctorIds(consultantDoctorIds);
			// }
			if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
				patientCollection.setProfession(request.getProfession());
			}
			patientCollection.setNotes(request.getNotes());
			if (request.getImage() != null) {
				String path = "profile-images";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				patientCollection.setImageUrl(imageURLResponse.getImageUrl());
				patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
			}

			ReferencesCollection referencesCollection = null;
			if (request.getReferredBy() != null) {
				if (request.getReferredBy().getId() != null) {
					referencesCollection = referrenceRepository.findOne(new ObjectId(request.getReferredBy().getId()));
				}
				if (referencesCollection == null) {
					referencesCollection = new ReferencesCollection();
					BeanUtil.map(request.getReferredBy(), referencesCollection);
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
						referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
						referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
						referencesCollection.setLocationId(new ObjectId(request.getLocationId()));
					referencesCollection = referrenceRepository.save(referencesCollection);
					transnationalService.addResource(referencesCollection.getId(), Resource.REFERENCE, false);
					ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
					BeanUtil.map(referencesCollection, esReferenceDocument);
					esRegRistrationService.addEditReference(esReferenceDocument);
				}
				patientCollection.setReferredBy(referencesCollection.getId());
			}
			patientCollection = patientRepository.save(patientCollection);

			// assign groups
			if (request.getGroups() != null) {
				for (String group : request.getGroups()) {
					PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
					patientGroupCollection.setGroupId(new ObjectId(group));
					patientGroupCollection.setPatientId(patientCollection.getUserId());
					patientGroupCollection.setCreatedTime(new Date());
					patientGroupCollection = patientGroupRepository.save(patientGroupCollection);
				}
			}
			/*
			 * if (patientCollection.getEmailAddress() != null) { // send
			 * activation email String body =
			 * mailBodyGenerator.generatePatientRegistrationEmailBody
			 * (userCollection.getUserName(), userCollection.getPassword(),
			 * userCollection.getFirstName(), userCollection.getLastName());
			 * mailService.sendEmail(patientCollection.getEmailAddress(),
			 * signupSubject, body, null); }
			 */
			// send SMS logic
			BeanUtil.map(userCollection, registeredPatientDetails);
			registeredPatientDetails.setUserId(userCollection.getId().toString());
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			patient.setPatientId(userCollection.getId().toString());

			Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
			if (!DPDoctorUtils.anyStringEmpty(patient.getDoctorId())) {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
						patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
						patientCollection.getLocationId());
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
						patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
						patientCollection.getLocationId());
				recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
						userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
			} else {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(userCollection.getId(),
						patientCollection.getHospitalId(), patientCollection.getLocationId());
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
						userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
				recordsCount = recordsRepository.getRecordsForOtherLocations(userCollection.getId(),
						patientCollection.getHospitalId(), patientCollection.getLocationId());
			}

			if ((prescriptionCount != null && prescriptionCount > 0)
					|| (clinicalNotesCount != null && clinicalNotesCount > 0)
					|| (recordsCount != null && recordsCount > 0))
				patient.setIsDataAvailableWithOtherDoctor(true);

			patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(),
					patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(),
					userCollection.getId().toString()));

			registeredPatientDetails.setPatient(patient);
			registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
			registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
			registeredPatientDetails.setDob(patientCollection.getDob());
			registeredPatientDetails.setGender(patientCollection.getGender());
			registeredPatientDetails.setPID(patientCollection.getPID());
			registeredPatientDetails.setConsultantDoctorIds(patient.getConsultantDoctorIds());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
				registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
				registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
			if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
				registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
			registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
			registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
			registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
			if (referencesCollection != null) {
				Reference reference = new Reference();
				BeanUtil.map(referencesCollection, reference);
				registeredPatientDetails.setReferredBy(reference);
			}
			registeredPatientDetails.setAddress(patientCollection.getAddress());

			if (request.getGroups() != null) {
				List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
				for (String groupId : request.getGroups())
					groupObjectIds.add(new ObjectId(groupId));
				groups = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))),
								GroupCollection.class, Group.class)
						.getMappedResults();
			}
			registeredPatientDetails.setGroups(groups);
			pushNotificationServices.notifyUser(patientCollection.getUserId().toString(),
					"Welcome to " + locationCollection.getLocationName()
							+ ", let us know about your visit. We will be happy to serve you again.",
					ComponentType.PATIENT.getType(), patientCollection.getUserId().toString(), null);
			if (userCollection.getMobileNumber() != null) {
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(patientCollection.getDoctorId());
				smsTrackDetail.setHospitalId(patientCollection.getHospitalId());
				smsTrackDetail.setLocationId(patientCollection.getLocationId());

				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(patientCollection.getUserId());
				smsDetail.setUserName(patientCollection.getFirstName());
				SMS sms = new SMS();
				sms.setSmsText("OTP Verification");

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				// List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				// smsDetails.add(smsDetail);
				// smsTrackDetail.setSmsDetails(smsDetails);
				// sMSServices.sendSMS(smsTrackDetail, false);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	private CheckPatientSignUpResponse checkIfPatientIsSignedUp(String mobileNumber) {
		CheckPatientSignUpResponse response = null;
		try {
			// Aggregation aggregation =
			// Aggregation.newAggregation(Aggregation.match(new
			// Criteria("mobileNumber").is(MobileNumber)), new
			// CustomAggregationOperation(new BasicDBObject("$redact",new
			// BasicDBObject("$cond",new BasicDBObject()
			// .append("if", new BasicDBObject("$neq",
			// Arrays.asList("$emailAddress", "$userName"))).append("then",
			// "$$KEEP").append("else", "$$PRUNE")))));

			List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
			if (userCollections != null && !userCollections.isEmpty()) {
				for (UserCollection userCollection : userCollections) {
					if (userCollection.getEmailAddress() != null) {
						if (!userCollection.getUserName().equals(userCollection.getEmailAddress())) {
							if (userCollection.isSignedUp()) {
								response = new CheckPatientSignUpResponse(userCollection.getPassword(),
										userCollection.getSalt(), userCollection.isSignedUp());
							}
							break;
						}
					} else {
						if (userCollection.isSignedUp()) {
							response = new CheckPatientSignUpResponse(userCollection.getPassword(),
									userCollection.getSalt(), userCollection.isSignedUp());
						}
						break;
					}
				}

				// mongoTemplate.aggregate(aggregation, UserCollection.class,
				// UserCollection.class).getMappedResults();
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
	public RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request, List<String> infoType) {
		RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
		PatientCollection patientCollection = null;
		List<Group> groups = null;
		try {

			if (request.getDob() != null && request.getDob().getAge() != null
					&& request.getDob().getAge().getYears() < 0) {
				logger.warn(DOB);
				throw new BusinessException(ServiceError.InvalidInput, DOB);
			} else if (request.getDob() == null && request.getAge() != null) {
				Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
				request.setDob(new DOB(currentDay, currentMonth, currentYear));
			}
			ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getUserId()))
				userObjectId = new ObjectId(request.getUserId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			// save Patient Info
			if (DPDoctorUtils.anyStringEmpty(doctorObjectId, hospitalObjectId, locationObjectId)) {
				UserCollection userCollection = userRepository.findOne(userObjectId);
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}

				if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName()))
					userCollection.setFirstName(request.getLocalPatientName());

				userCollection.setIsActive(true);
				userCollection.setUpdatedTime(new Date());

				BeanUtil.map(userCollection, registeredPatientDetails);
				patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);
				if (patientCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
						patientCollection.setLocalPatientName(request.getLocalPatientName());
						patientCollection.setFirstName(request.getLocalPatientName());
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getBloodGroup()))
						patientCollection.setBloodGroup(request.getBloodGroup());
					if (!DPDoctorUtils.anyStringEmpty(request.getGender()))
						patientCollection.setGender(request.getGender());
					if (!DPDoctorUtils.anyStringEmpty(request.getEmailAddress()))
						patientCollection.setEmailAddress(request.getEmailAddress());
					if (request.getDob() != null)
						patientCollection.setDob(request.getDob());
					if(request.getAddress() != null)patientCollection.setAddress(request.getAddress());
					
					if(infoType != null && !infoType.isEmpty()) {
						if(infoType.contains("PERSONALINFO"))
							patientCollection.setPersonalInformation(request.getPersonalInformation());
						if (infoType.contains("LIFESTYLE"))
							patientCollection.setLifestyleQuestionAnswers(request.getLifestyleQuestionAnswers());
						if (infoType.contains("MEDICAL"))
							patientCollection.setMedicalQuestionAnswers(request.getMedicalQuestionAnswers());
					}
				} else {
					logger.error("Incorrect User Id, DoctorId, LocationId, HospitalId");
					throw new BusinessException(ServiceError.InvalidInput,
							"Incorrect User Id, DoctorId, LocationId, HospitalId");
				}
				if (request.getImage() != null) {
					String path = "profile-images";
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
				}

				patientCollection.setUpdatedTime(new Date());
				userCollection = userRepository.save(userCollection);
				patientCollection = patientRepository.save(patientCollection);

				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				patient.setPatientId(userCollection.getId().toString());
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
			} else {
				patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(userObjectId,
						locationObjectId, hospitalObjectId);
				if (patientCollection != null) {
					ObjectId patientId = patientCollection.getId();
					ObjectId patientDoctorId = patientCollection.getDoctorId();
					request.setRegistrationDate(patientCollection.getRegistrationDate());
					BeanUtil.map(request, patientCollection);
					patientCollection.setId(patientId);
					patientCollection.setUpdatedTime(new Date());
					patientCollection.setDoctorId(patientDoctorId);
				} else {
					patientCollection = new PatientCollection();
					patientCollection.setCreatedTime(new Date());
					BeanUtil.map(request, patientCollection);
					if (request.getRegistrationDate() != null)
						patientCollection.setRegistrationDate(request.getRegistrationDate());
					else
						patientCollection.setRegistrationDate(new Date().getTime());
				}

				patientCollection.setRelations(request.getRelations());
				patientCollection.setNotes(request.getNotes());

				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getPID())) {
					patientCollection.setPID(patientCollection.getPID());
				} else {
					patientCollection.setPID(patientIdGenerator(request.getLocationId(), request.getHospitalId(),
							patientCollection.getRegistrationDate()));
				}
				if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
					patientCollection.setProfession(request.getProfession());
				}

				ReferencesCollection referencesCollection = null;
				if (request.getReferredBy() != null) {
					if (request.getReferredBy().getId() != null) {
						referencesCollection = referrenceRepository
								.findOne(new ObjectId(request.getReferredBy().getId()));
					}
					if (referencesCollection == null) {
						referencesCollection = new ReferencesCollection();
						BeanUtil.map(request.getReferredBy(), referencesCollection);
						BeanUtil.map(request, referencesCollection);
						referencesCollection.setId(null);
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
							referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
							referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
						if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
							referencesCollection.setLocationId(new ObjectId(request.getLocationId()));
						referencesCollection = referrenceRepository.save(referencesCollection);
						transnationalService.addResource(referencesCollection.getId(), Resource.REFERENCE, false);
						ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
						BeanUtil.map(referencesCollection, esReferenceDocument);
						esRegRistrationService.addEditReference(esReferenceDocument);
					}
					patientCollection.setReferredBy(referencesCollection.getId());
				}

				// assign groups
				if (request.getGroups() != null) {

					List<String> groupIds = new ArrayList<String>();
					List<PatientGroupCollection> patientGroupCollections = patientGroupRepository
							.findByPatientId(patientCollection.getUserId());
					if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
						for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
							if (request.getGroups() != null && !request.getGroups().isEmpty()) {
								groupIds.add(patientGroupCollection.getGroupId().toString());
								if (!request.getGroups().contains(patientGroupCollection.getGroupId().toString())) {
									patientGroupRepository.delete(patientGroupCollection);
								}
							} else {
								patientGroupRepository.delete(patientGroupCollection);
							}
						}
					}

					if (request.getGroups() != null && !request.getGroups().isEmpty()) {
						for (String group : request.getGroups()) {
							if (!groupIds.contains(group)) {
								PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
								patientGroupCollection.setGroupId(new ObjectId(group));
								patientGroupCollection.setPatientId(patientCollection.getUserId());
								patientGroupRepository.save(patientGroupCollection);
							}
						}
					}
				}
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getUserId()));
				if (userCollection == null) {
					logger.error("Incorrect User Id");
					throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
				}
				/* registeredPatientDetails = new RegisteredPatientDetails(); */
				BeanUtil.map(userCollection, registeredPatientDetails);
				if (request.getImage() != null) {
					String path = "profile-images";
					// save image
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					patientCollection.setImageUrl(imageURLResponse.getImageUrl());
					userCollection.setImageUrl(null);
					registeredPatientDetails.setImageUrl(imageURLResponse.getImageUrl());
					patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
					userCollection.setThumbnailUrl(null);
					registeredPatientDetails.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
				}
				if (!userCollection.isSignedUp()) {
					userCollection.setFirstName(request.getLocalPatientName());
					userCollection.setUpdatedTime(new Date());
					userCollection = userRepository.save(userCollection);
				}
				registeredPatientDetails.setUserId(userCollection.getId().toString());
				patientCollection.setFirstName(userCollection.getFirstName());

				// if(RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(request.getRole())){
				List<ObjectId> consultantDoctorIds = patientCollection.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(new ObjectId(request.getDoctorId())))
					consultantDoctorIds.add(new ObjectId(request.getDoctorId()));
				patientCollection.setConsultantDoctorIds(consultantDoctorIds);
				// }

				patientCollection = patientRepository.save(patientCollection);
				registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				registeredPatientDetails.setBackendPatientId(patientCollection.getId().toString());
				patient.setPatientId(userCollection.getId().toString());

				Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
							patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(),
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
				} else {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
							userCollection.getId(), patientCollection.getHospitalId(),
							patientCollection.getLocationId());
					recordsCount = recordsRepository.getRecordsForOtherLocations(userCollection.getId(),
							patientCollection.getHospitalId(), patientCollection.getLocationId());
				}

				if ((prescriptionCount != null && prescriptionCount > 0)
						|| (clinicalNotesCount != null && clinicalNotesCount > 0)
						|| (recordsCount != null && recordsCount > 0))
					patient.setIsDataAvailableWithOtherDoctor(true);

				patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(),
						patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(),
						userCollection.getId().toString()));

				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setLocalPatientName(patient.getLocalPatientName());
				registeredPatientDetails.setDob(patientCollection.getDob());
				registeredPatientDetails.setGender(patientCollection.getGender());
				registeredPatientDetails.setPID(patientCollection.getPID());
				registeredPatientDetails.setConsultantDoctorIds(patient.getConsultantDoctorIds());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getDoctorId()))
					registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getLocationId()))
					registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
				if (!DPDoctorUtils.anyStringEmpty(patientCollection.getHospitalId()))
					registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
				registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
				if (referencesCollection != null) {
					Reference reference = new Reference();
					BeanUtil.map(referencesCollection, reference);
					registeredPatientDetails.setReferredBy(reference);
				}
				registeredPatientDetails.setAddress(patientCollection.getAddress());
				if (request.getGroups() != null) {
					List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
					for (String groupId : request.getGroups())
						groupObjectIds.add(new ObjectId(groupId));
					groups = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))),
							GroupCollection.class, Group.class).getMappedResults();
					registeredPatientDetails.setGroups(groups);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Override
	@Transactional
	public void checkPatientCount(String mobileNumber) {

		int count = 0;
		List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
		if (userCollections != null && !userCollections.isEmpty()) {
			for (UserCollection userCollection : userCollections) {
				if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
					count++;
			}
			if (count >= Integer.parseInt(patientCount)) {
				logger.warn(checkPatientCount);
				throw new BusinessException(ServiceError.Unknown, checkPatientCount);
			}
		}
	}

	@Override
	@Transactional
	public List<RegisteredPatientDetails> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId,
			String hospitalId, String role) {
		List<RegisteredPatientDetails> users = null;
		try {
			List<UserLookupResponse> userLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("mobileNumber").is(phoneNumber)),
							Aggregation.lookup("patient_cl", "_id", "userId", "patients")),
					UserCollection.class, UserLookupResponse.class).getMappedResults();
			if (userLookupResponses != null) {
				users = new ArrayList<RegisteredPatientDetails>();
				for (UserLookupResponse userLookupResponse : userLookupResponses) {
					if (!userLookupResponse.getUserName().equalsIgnoreCase(userLookupResponse.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						if (locationId != null && hospitalId != null) {
							boolean isPartOfClinic = false, isPartOfConsultantDoctor = true;
							if (userLookupResponse.getPatients() != null) {
								for (PatientCard patientCard : userLookupResponse.getPatients()) {
									if (patientCard.getLocationId() != null && patientCard.getHospitalId() != null) {
										if (patientCard.getLocationId().equals(locationId)
												&& patientCard.getHospitalId().equals(hospitalId)) {
											user.setLocalPatientName(patientCard.getLocalPatientName());
											isPartOfClinic = true;
											if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
												if (patientCard.getConsultantDoctorIds() != null
														&& !patientCard.getConsultantDoctorIds().isEmpty()) {
													if (patientCard.getConsultantDoctorIds().contains(doctorId))
														isPartOfConsultantDoctor = true;
													else
														isPartOfConsultantDoctor = false;
												} else {
													isPartOfConsultantDoctor = false;
												}
											}
											break;
										}
									} else {
										Patient patient = new Patient();
										BeanUtil.map(patientCard, patient);
										BeanUtil.map(patientCard, user);
										BeanUtil.map(userLookupResponse, user);
										user.setImageUrl(patientCard.getImageUrl());
										user.setThumbnailUrl(patientCard.getThumbnailUrl());
										patient.setPatientId(patientCard.getUserId().toString());
										user.setPatient(patient);
									}
								}
							}
							if (!isPartOfClinic) {
								user.setUserId(userLookupResponse.getId().toString());
								user.setFirstName(userLookupResponse.getFirstName());
								user.setMobileNumber(userLookupResponse.getMobileNumber());
							} else {
								BeanUtil.map(userLookupResponse, user);
								user.setUserId(userLookupResponse.getId().toString());
							}
							user.setIsPartOfClinic(isPartOfClinic);
							user.setIsPartOfConsultantDoctor(isPartOfConsultantDoctor);
						}
						users.add(user);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}

	@Override
	public List<RegisteredPatientDetails> getPatientsByPhoneNumber(String mobileNumber) {
		List<RegisteredPatientDetails> users = null;
		try {
			List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
			if (userCollections != null) {
				users = new ArrayList<RegisteredPatientDetails>();
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						RegisteredPatientDetails user = new RegisteredPatientDetails();
						Patient patient = new Patient();
						PatientCollection patientCollection = patientRepository
								.findByUserIdDoctorIdLocationIdAndHospitalId(userCollection.getId(), null, null, null);
						if (patientCollection != null) {
							BeanUtil.map(patientCollection, patient);
							BeanUtil.map(patientCollection, user);
							BeanUtil.map(userCollection, user);
							user.setImageUrl(patientCollection.getImageUrl());
							user.setThumbnailUrl(patientCollection.getThumbnailUrl());
						} else {
							BeanUtil.map(userCollection, user);
						}
						patient.setPatientId(userCollection.getId().toString());
						user.setPatient(patient);
						users.add(user);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return users;
	}

	private char[] generateRandomAlphanumericString(int count) {
		return RandomStringUtils.randomAlphabetic(count).toCharArray();
	}

	@Override
	@Transactional
	public RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId,
			String hospitalId) {
		RegisteredPatientDetails registeredPatientDetails = null;
		PatientCollectionResponse patientCard = null;
		List<Group> groups = null;
		try {
			ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(userId))
				userObjectId = new ObjectId(userId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria();
			criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("userId")
					.is(userObjectId);
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroupCollections"),
							Aggregation.match(new Criteria().orOperator(
									new Criteria("patientGroupCollections.discarded").is(false),
									new Criteria("patientGroupCollections").size(0))),
							Aggregation.lookup("referrences_cl", "referredBy", "_id", "reference"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind", new BasicDBObject("path", "$reference")
											.append("preserveNullAndEmptyArrays", true))));

			List<PatientCollectionResponse> patientCollectionResponses = mongoTemplate
					.aggregate(aggregation, PatientCollection.class, PatientCollectionResponse.class)
					.getMappedResults();
			if (patientCollectionResponses != null && !patientCollectionResponses.isEmpty())
				patientCard = patientCollectionResponses.get(0);
			if (patientCard != null && patientCard.getUser() != null) {
				Reference reference = null;
				if (patientCard.getReference() != null) {

					reference = new Reference();
					BeanUtil.map(patientCard.getReference(), reference);

				}
				patientCard.setReferredBy(null);

				registeredPatientDetails = new RegisteredPatientDetails();

				BeanUtil.map(patientCard, registeredPatientDetails);
				BeanUtil.map(patientCard.getUser(), registeredPatientDetails);
				registeredPatientDetails.setImageUrl(patientCard.getImageUrl());
				registeredPatientDetails.setThumbnailUrl(patientCard.getThumbnailUrl());

				registeredPatientDetails.setUserId(patientCard.getUser().getId().toString());
				registeredPatientDetails.setReferredBy(reference);
				Patient patient = new Patient();
				BeanUtil.map(patientCard, patient);
				patient.setPatientId(patientCard.getUserId());

				Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(
							new ObjectId(patientCard.getDoctorId()), patientCard.getUser().getId(),
							new ObjectId(patientCard.getHospitalId()), new ObjectId(patientCard.getLocationId()));
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(
							new ObjectId(patientCard.getDoctorId()), patientCard.getUser().getId(),
							new ObjectId(patientCard.getHospitalId()), new ObjectId(patientCard.getLocationId()));
					recordsCount = recordsRepository.getRecordsForOtherDoctors(new ObjectId(patientCard.getDoctorId()),
							patientCard.getUser().getId(), new ObjectId(patientCard.getHospitalId()),
							new ObjectId(patientCard.getLocationId()));
				} else {
					prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(
							patientCard.getUser().getId(), new ObjectId(patientCard.getHospitalId()),
							new ObjectId(patientCard.getLocationId()));
					clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(
							patientCard.getUser().getId(), new ObjectId(patientCard.getHospitalId()),
							new ObjectId(patientCard.getLocationId()));
					recordsCount = recordsRepository.getRecordsForOtherLocations(patientCard.getUser().getId(),
							new ObjectId(patientCard.getHospitalId()), new ObjectId(patientCard.getLocationId()));
				}

				if ((prescriptionCount != null && prescriptionCount > 0)
						|| (clinicalNotesCount != null && clinicalNotesCount > 0)
						|| (recordsCount != null && recordsCount > 0))
					patient.setIsDataAvailableWithOtherDoctor(true);

				patient.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId,
						patientCard.getUser().getId().toString()));
				registeredPatientDetails.setPatient(patient);
				registeredPatientDetails.setAddress(patientCard.getAddress());
				@SuppressWarnings("unchecked")
				Collection<ObjectId> groupIds = CollectionUtils.collect(patientCard.getPatientGroupCollections(),
						new BeanToPropertyValueTransformer("groupId"));
				if (groupIds != null && !groupIds.isEmpty()) {
					groups = mongoTemplate
							.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupIds))),
									GroupCollection.class, Group.class)
							.getMappedResults();
					registeredPatientDetails.setGroups(groups);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

	@Override
	@Transactional
	public Reference addEditReference(Reference reference) {
		try {
			ReferencesCollection referrencesCollection = new ReferencesCollection();
			BeanUtil.map(reference, referrencesCollection);
			if (referrencesCollection.getId() == null) {
				referrencesCollection.setCreatedTime(new Date());
				if (reference.getDoctorId() != null) {
					UserCollection userCollection = userRepository.findOne(new ObjectId(reference.getDoctorId()));
					if (userCollection != null) {
						referrencesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else
					referrencesCollection.setCreatedBy("ADMIN");
			}
			referrencesCollection = referrenceRepository.save(referrencesCollection);
			BeanUtil.map(referrencesCollection, reference);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return reference;
	}

	@Override
	@Transactional
	public Reference deleteReferrence(String referenceId, Boolean discarded) {
		Reference response = null;
		try {
			ReferencesCollection referrencesCollection = referrenceRepository.findOne(new ObjectId(referenceId));
			if (referrencesCollection != null) {
				referrencesCollection.setDiscarded(discarded);
				referrencesCollection.setUpdatedTime(new Date());
				referrenceRepository.save(referrencesCollection);
				response = new Reference();
				BeanUtil.map(referrencesCollection, response);
			} else {
				logger.warn("Invalid Referrence Id!");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Referrence Id!");
			}
		} catch (BusinessException be) {
			be.printStackTrace();
			logger.error(be);
			throw new BusinessException(ServiceError.Unknown, be.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ReferenceDetail> response = null;

		try {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalReferences(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalReferences(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getGlobalReferences(int page, int size, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, "reference", null, null, null),
					ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getCustomReferences(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, "reference", null, null),
							ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<ReferenceDetail> getCustomGlobalReferences(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<ReferenceDetail> response = null;
		try {
			AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, "reference", null, null, null),
					ReferencesCollection.class, ReferenceDetail.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String patientIdGenerator(String locationId, String hospitalId, Long registrationDate) {
		String generatedId = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date(registrationDate));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			ObjectId locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long startTimeinMillis = start.getMillis();
			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long endTimeinMillis = end.getMillis();
			Integer patientSize = patientRepository.findTodaysRegisteredPatient(locationObjectId, hospitalObjectId,
					startTimeinMillis, endTimeinMillis);
			
			if (patientCount == null)
				patientSize = 0;
			LocationCollection location = locationRepository.findOne(locationObjectId);
			if (location == null) {
				logger.warn("Invalid Location Id");
				throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
			}
			String patientInitial = location.getPatientInitial();
			int patientCounter = location.getPatientCounter();
			if (patientCounter <= patientSize)
				patientCounter = patientCounter + patientSize;
			generatedId = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay)
					+ DPDoctorUtils.getPrefixedNumber(currentMonth) + DPDoctorUtils.getPrefixedNumber(currentYear % 100)
					+ DPDoctorUtils.getPrefixedNumber(patientCounter);
			
			System.out.println(locationId +".."+patientSize+".."+patientCounter);  
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generatedId;
	}

	@Override
	@Transactional
	public PatientInitialAndCounter getPatientInitialAndCounter(String locationId) {
		PatientInitialAndCounter patientInitialAndCounter = null;
		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection != null) {
				patientInitialAndCounter = new PatientInitialAndCounter();
				BeanUtil.map(locationCollection, patientInitialAndCounter);
				patientInitialAndCounter.setLocationId(locationId);
			} else {
				logger.warn("Invalid Location Id");
				throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error While Getting Patient Initial and Counter");
		}
		return patientInitialAndCounter;
	}

	@Override
	@Transactional
	public Boolean updatePatientInitialAndCounter(String locationId, String patientInitial, int patientCounter) {
		Boolean response = false;
		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection != null) {
				response = checkIfPatientInitialAndCounterExist(locationId, patientInitial, patientCounter);
				if (response) {
					locationCollection.setPatientInitial(patientInitial);
					locationCollection.setPatientCounter(patientCounter);
					locationCollection.setUpdatedTime(new Date());
					locationCollection = locationRepository.save(locationCollection);
					response = true;
				}
			} else {
				logger.warn("Invalid Location Id");
				throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Patient Initial and Counter");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Patient Initial and Counter");
		}
		return response;
	}

	private Boolean checkIfPatientInitialAndCounterExist(String locationId, String patientInitial, int patientCounter) {
		Boolean response = false;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			String date = DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
					+ DPDoctorUtils.getPrefixedNumber(currentYear % 100);
			String generatedId = patientInitial + date;

			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("PID")
					.regex(generatedId + ".*");

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.limit(1));

			AggregationResults<PatientCollection> groupResults = mongoTemplate.aggregate(aggregation,
					PatientCollection.class, PatientCollection.class);
			List<PatientCollection> results = groupResults.getMappedResults();
			if (results != null && !results.isEmpty()) {
				String PID = results.get(0).getPID();
				PID = PID.substring((patientInitial + date).length());
				if (patientCounter <= Integer.parseInt(PID)) {
					logger.warn("Patient already exist for Prefix: " + patientInitial + " , Date: " + date
							+ " Id Number: " + patientCounter + ". Please enter Id greater than " + PID);
					throw new BusinessException(ServiceError.Unknown,
							"Patient already exist for Prefix: " + patientInitial + " , Date: " + date + " Id Number: "
									+ patientCounter + ". Please enter Id greater than " + PID);
				} else
					response = true;

			} else
				response = true;
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Location getClinicDetails(String clinicId) {
		Location location = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(clinicId));
			if (locationCollection != null) {
				location = new Location();
				BeanUtil.map(locationCollection, location);

				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode() : "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				location.setClinicAddress(address);
			} else {
				logger.warn("No Location Found For The Location Id");
				throw new BusinessException(ServiceError.NotFound, "No Location Found For The Location Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Retrieving Location Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Retrieving Location Details");
		}
		return location;
	}

	@Override
	@Transactional
	public ClinicProfile updateClinicProfile(ClinicProfile request) {
		ClinicProfile response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));

			locationCollection.setTagLine(request.getTagLine());
			locationCollection.setWebsiteUrl(request.getWebsiteUrl());
			locationCollection.setLocationName(request.getLocationName());
			locationCollection.setSpecialization(request.getSpecialization());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicProfile();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicAddress updateClinicAddress(ClinicAddress request) {
		ClinicAddress response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			String locationName = "";
			if (locationCollection != null) {
				locationName = locationCollection.getLocationName();
				BeanUtil.map(request, locationCollection);
			}

			List<GeocodedLocation> geocodedLocations = locationServices
					.geocodeLocation((!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
							? locationCollection.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
									? locationCollection.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
									? locationCollection.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
									? locationCollection.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
									? locationCollection.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
									? locationCollection.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
									? locationCollection.getPostalCode() : ""));

			if (geocodedLocations != null && !geocodedLocations.isEmpty())
				BeanUtil.map(geocodedLocations.get(0), locationCollection);
			if (DPDoctorUtils.anyStringEmpty(request.getLocationName()))
				locationCollection.setLocationName(locationName);
			locationCollection.setAlternateClinicNumbers(request.getAlternateClinicNumbers());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicAddress();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicTiming updateClinicTiming(ClinicTiming request) {
		ClinicTiming response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			locationCollection.setClinicWorkingSchedules(request.getClinicWorkingSchedules());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicTiming();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicSpecialization updateClinicSpecialization(ClinicSpecialization request) {
		ClinicSpecialization response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			locationCollection.setSpecialization(request.getSpecialization());
			locationCollection.setId(new ObjectId(request.getId()));
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicSpecialization();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public List<BloodGroup> getBloodGroup() {

		List<BloodGroup> bloodGroups = new ArrayList<BloodGroup>();
		try {
			for (com.dpdocter.enums.BloodGroup group : com.dpdocter.enums.BloodGroup.values()) {
				BloodGroup bloodGroup = new BloodGroup();
				bloodGroup.setBloodGroup(group.getGroup());
				bloodGroups.add(bloodGroup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return bloodGroups;
	}

	@Override
	@Transactional
	public Profession addProfession(Profession request) {
		Profession profession = new Profession();
		try {
			ProfessionCollection professionCollection = new ProfessionCollection();
			BeanUtil.map(request, professionCollection);
			professionCollection = professionRepository.save(professionCollection);
			BeanUtil.map(professionCollection, profession);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return profession;
	}

	@Override
	@Transactional
	public List<Profession> getProfession(int page, int size, String updatedTime) {
		List<Profession> professions = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")));
			AggregationResults<Profession> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProfessionCollection.class, Profession.class);
			professions = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return professions;
	}

	@Override
	@Transactional
	public ClinicLogo changeClinicLogo(ClinicLogoAddRequest request) {
		ClinicLogo response = null;
		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection == null) {
				logger.warn("Clinic not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				if (request.getImage() != null) {
					String path = "clinic" + File.separator + "logos";
					// save image
					request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
					ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
							true);
					locationCollection.setLogoUrl(imageURLResponse.getImageUrl());
					locationCollection.setLogoThumbnailUrl(imageURLResponse.getThumbnailUrl());
					locationCollection = locationRepository.save(locationCollection);

					List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
							.findByLocationId(new ObjectId(request.getId()));
					if (printSettingsCollections != null) {
						for (PrintSettingsCollection printSettingsCollection : printSettingsCollections) {
							printSettingsCollection.setClinicLogoUrl(imageURLResponse.getImageUrl());
							printSettingsRepository.save(printSettingsCollection);
						}
					}
					response = new ClinicLogo();
					BeanUtil.map(locationCollection, response);
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
	public List<ClinicImage> addClinicImage(ClinicImageAddRequest request) {
		List<ClinicImage> response = null;

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection == null) {
				logger.warn("Clinic not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				int counter = locationCollection.getImages() != null ? locationCollection.getImages().size() : 0;
				response = new ArrayList<ClinicImage>();
				if (locationCollection.getImages() != null)
					response.addAll(locationCollection.getImages());
				if (request.getImages() != null) {
					for (FileDetails image : request.getImages()) {
						counter++;
						String path = "clinic" + File.separator + "images";
						image.setFileName(image.getFileName() + new Date().getTime());
						ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(image, path, true);
						ClinicImage clinicImage = new ClinicImage();
						clinicImage.setImageUrl(imageURLResponse.getImageUrl());
						clinicImage.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
						clinicImage.setCounter(counter);
						response.add(clinicImage);
					}
					locationCollection.setImages(response);
					locationCollection.setUpdatedTime(new Date());
					locationCollection = locationRepository.save(locationCollection);
					BeanUtil.map(locationCollection, response);
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
	public Boolean deleteClinicImage(String locationId, int counter) {

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection == null) {
				logger.warn("User not found");
				throw new BusinessException(ServiceError.NotFound, "Clinic not found");
			} else {
				boolean foundImage = false;
				int imgCounter = 0;
				List<ClinicImage> images = locationCollection.getImages();
				List<ClinicImage> copyimages = new ArrayList<ClinicImage>(images);
				for (Iterator<ClinicImage> iterator = copyimages.iterator(); iterator.hasNext();) {
					ClinicImage image = iterator.next();
					if (foundImage) {
						image.setCounter(imgCounter);
						imgCounter++;
					} else if (image.getCounter() == counter) {
						foundImage = true;
						imgCounter = counter;
						images.remove(image);
					}
				}
				locationCollection.setImages(images);
				locationCollection.setUpdatedTime(new Date());
				locationCollection = locationRepository.save(locationCollection);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public Boolean checktDoctorExistByEmailAddress(String emailAddress) {
		try {
			UserCollection userCollections = userRepository.findByUserNameAndEmailAddress(emailAddress, emailAddress);
			if (userCollections == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request) {
		RegisterDoctorResponse response = null;
		try {
			RoleCollection doctorRole = null;
			if (request.getRoleId() != null) {
				doctorRole = roleRepository.findOne(new ObjectId(request.getRoleId()));
			}
			if (doctorRole == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}
			// save user
			UserCollection userCollection = new UserCollection();
			BeanUtil.map(request, userCollection);
			userCollection.setUserName(request.getEmailAddress());
			userCollection.setCreatedTime(new Date());
			userCollection.setUserUId(UniqueIdInitial.USER.getInitial() + DPDoctorUtils.generateRandomId());
			userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
			userCollection.setUserState(UserState.NOTVERIFIED);
			userCollection = userRepository.save(userCollection);

			// save doctor specific details
			DoctorCollection doctorCollection = new DoctorCollection();
			BeanUtil.map(request, doctorCollection);
			doctorCollection.setUserId(userCollection.getId());
			doctorCollection.setCreatedTime(new Date());
			doctorCollection = doctorRepository.save(doctorCollection);

			// assign role to doctor
			UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId(),
					new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
			userRoleCollection.setCreatedTime(new Date());
			userRoleCollection = userRoleRepository.save(userRoleCollection);

			if (doctorRole.getRole().equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole())) {
				RoleCollection userHospitalAdminRole = roleRepository.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());
				if (userHospitalAdminRole != null) {
					UserRoleCollection userHospitalAdminRoleCollection = new UserRoleCollection(userCollection.getId(),
							userHospitalAdminRole.getId(), new ObjectId(request.getLocationId()),
							new ObjectId(request.getHospitalId()));
					userHospitalAdminRoleCollection.setCreatedTime(new Date());
					userHospitalAdminRoleCollection = userRoleRepository.save(userHospitalAdminRoleCollection);
				}
			}
			// save user location.
			DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
			doctorClinicProfileCollection.setDoctorId(userCollection.getId());
			doctorClinicProfileCollection.setLocationId(new ObjectId(request.getLocationId()));
			doctorClinicProfileCollection.setCreatedTime(new Date());
			doctorClinicProfileCollection.setIsActivate(request.getIsActivate());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);

			// save token
			TokenCollection tokenCollection = new TokenCollection();
			tokenCollection.setResourceId(doctorClinicProfileCollection.getId());
			tokenCollection.setCreatedTime(new Date());
			tokenCollection = tokenRepository.save(tokenCollection);

			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			RoleCollection adminRoleCollection = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
			String admindoctorName = "";
			if (adminRoleCollection != null) {
				List<UserRoleCollection> roleCollections = userRoleRepository.findByRoleIdLocationIdHospitalId(
						adminRoleCollection.getId(), locationCollection.getId(), locationCollection.getHospitalId());
				UserRoleCollection roleCollection = null;
				if (roleCollections != null && !roleCollections.isEmpty()) {
					roleCollection = roleCollections.get(0);
					UserCollection doctorUser = userRepository.findOne(roleCollection.getUserId());
					admindoctorName = doctorUser.getTitle() + " " + doctorUser.getFirstName();
				}
			}
			if (doctorRole.getRole().equals(RoleEnum.DOCTOR.getRole())
					|| doctorRole.getRole().equals(RoleEnum.CONSULTANT_DOCTOR.getRole())
					|| doctorRole.getRole().equals(RoleEnum.SUPER_ADMIN.getRole())
					|| doctorRole.getRole().equals(RoleEnum.HOSPITAL_ADMIN.getRole())
					|| doctorRole.getRole().equals(RoleEnum.LOCATION_ADMIN.getRole())) {
				String body = mailBodyGenerator.generateActivationEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName(), tokenCollection.getId(),
						"addDoctorToClinicVerifyTemplate.vm", admindoctorName, locationCollection.getLocationName());
				mailService.sendEmail(userCollection.getEmailAddress(), addDoctorToClinicVerifySub, body, null);
			} else {
				String body = mailBodyGenerator.generateActivationEmailBody(
						userCollection.getTitle() + " " + userCollection.getFirstName(), tokenCollection.getId(),
						"verifyStaffMemberEmailTemplate.vm", admindoctorName, locationCollection.getLocationName());
				mailService.sendEmail(userCollection.getEmailAddress(), staffmemberAccountVerifySub, body, null);
			}
			response = new RegisterDoctorResponse();
			userCollection.setPassword(null);
			BeanUtil.map(userCollection, response);
			response.setHospitalId(request.getHospitalId());
			response.setLocationId(request.getLocationId());
			response.setUserId(userCollection.getId().toString());

			if (doctorRole != null) {
				List<Role> roles = new ArrayList<Role>();
				Role role = new Role();
				BeanUtil.map(doctorRole, role);
				AccessControl accessControl = accessControlServices.getAccessControls(doctorRole.getId(),
						doctorRole.getLocationId(), doctorRole.getHospitalId());
				if (accessControl != null)
					role.setAccessModules(accessControl.getAccessModules());
				roles.add(role);
				response.setRole(roles);
			}
		} catch (DuplicateKeyException de) {
			logger.error(de);
			throw new BusinessException(ServiceError.Unknown, "Email address already registerd. Please login");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public RegisterDoctorResponse registerExisitingUser(DoctorRegisterRequest request) {
		RegisterDoctorResponse response = null;
		try {

			RoleCollection doctorRole = null;
			if (request.getRoleId() != null) {
				doctorRole = roleRepository.findOne(new ObjectId(request.getRoleId()));
			}

			if (doctorRole == null) {
				logger.warn(role);
				throw new BusinessException(ServiceError.NoRecord, role);
			}
			UserCollection userCollection = userRepository.findByUserNameAndEmailAddress(request.getEmailAddress(),
					request.getEmailAddress());

			UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdLocationIdHospitalId(
					userCollection.getId(), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()));
			if (userRoleCollection == null) {
				userRoleCollection = new UserRoleCollection(userCollection.getId(), new ObjectId(request.getRoleId()),
						new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
				userRoleCollection.setCreatedTime(new Date());
				userRoleCollection = userRoleRepository.save(userRoleCollection);
			} else {
				logger.error("User is already added in clinic");
				throw new BusinessException(ServiceError.Unknown, "User is already added in clinic");
			}

			DoctorCollection doctorCollection = doctorRepository.findByUserId(userCollection.getId());
			if (doctorCollection.getAdditionalNumbers() != null) {
				if (!doctorCollection.getAdditionalNumbers().contains(request.getMobileNumber()))
					doctorCollection.getAdditionalNumbers().add(request.getMobileNumber());
				else {
					List<String> additionalNumbers = new ArrayList<String>();
					additionalNumbers.add(request.getMobileNumber());
				}
				doctorCollection = doctorRepository.save(doctorCollection);
			}

			DoctorClinicProfileCollection doctorClinicProfileCollection = new DoctorClinicProfileCollection();
			doctorClinicProfileCollection.setDoctorId(userCollection.getId());
			doctorClinicProfileCollection.setLocationId(new ObjectId(request.getLocationId()));
			doctorClinicProfileCollection.setCreatedTime(new Date());
			doctorClinicProfileCollection.setIsActivate(request.getIsActivate());
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);

			response = new RegisterDoctorResponse();
			userCollection.setPassword(null);
			BeanUtil.map(userCollection, response);
			response.setHospitalId(request.getHospitalId());
			response.setLocationId(request.getLocationId());
			response.setUserId(userCollection.getId().toString());

			if (doctorRole != null) {
				List<Role> roles = new ArrayList<Role>();
				Role role = new Role();
				BeanUtil.map(doctorRole, role);
				AccessControl accessControl = accessControlServices.getAccessControls(doctorRole.getId(),
						doctorRole.getLocationId(), doctorRole.getHospitalId());
				if (accessControl != null)
					role.setAccessModules(accessControl.getAccessModules());
				roles.add(role);
				response.setRole(roles);

				LocationCollection locationCollection = locationRepository
						.findOne(new ObjectId(request.getLocationId()));
				RoleCollection adminRoleCollection = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
				String admindoctorName = "";
				if (adminRoleCollection != null) {
					List<UserRoleCollection> userRoleCollections = userRoleRepository.findByRoleIdLocationIdHospitalId(
							adminRoleCollection.getId(), new ObjectId(request.getLocationId()),
							new ObjectId(request.getHospitalId()));
					UserRoleCollection roleCollection = null;
					if (userRoleCollections != null && !userRoleCollections.isEmpty()) {
						roleCollection = userRoleCollections.get(0);
						UserCollection doctorUser = userRepository.findOne(roleCollection.getUserId());
						admindoctorName = doctorUser.getTitle() + " " + doctorUser.getFirstName();
					}
				}
				if (doctorRole.getRole().equals(RoleEnum.DOCTOR.getRole())
						|| doctorRole.getRole().equals(RoleEnum.CONSULTANT_DOCTOR.getRole())
						|| doctorRole.getRole().equals(RoleEnum.SUPER_ADMIN.getRole())
						|| doctorRole.getRole().equals(RoleEnum.HOSPITAL_ADMIN.getRole())
						|| doctorRole.getRole().equals(RoleEnum.LOCATION_ADMIN.getRole())) {
					String body = mailBodyGenerator.generateActivationEmailBody(
							userCollection.getTitle() + " " + userCollection.getFirstName(), null,
							"addExistingDoctorToClinicTemplate.vm", admindoctorName,
							locationCollection.getLocationName());
					mailService.sendEmail(userCollection.getEmailAddress(),
							addExistingDoctorToClinicSub + " " + locationCollection.getLocationName(), body, null);
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
	public RegisterDoctorResponse editUserInClinic(DoctorRegisterRequest request) {
		RegisterDoctorResponse response = null;
		try {
			RoleCollection doctorRole = null;
			if (request.getRoleId() != null) {
				doctorRole = roleRepository.findOne(new ObjectId(request.getRoleId()));
			}

			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getUserId()));
			if (doctorRole != null) {

				UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdLocationIdHospitalId(
						userCollection.getId(), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()));
				if (userRoleCollection == null) {
					userRoleCollection = new UserRoleCollection(userCollection.getId(),
							new ObjectId(request.getRoleId()), new ObjectId(request.getLocationId()),
							new ObjectId(request.getHospitalId()));
					userRoleCollection.setCreatedTime(new Date());
					userRoleCollection = userRoleRepository.save(userRoleCollection);
				}
			}
			response = new RegisterDoctorResponse();
			BeanUtil.map(userCollection, response);
			response.setHospitalId(request.getHospitalId());
			response.setLocationId(request.getLocationId());
			response.setUserId(userCollection.getId().toString());

			if (doctorRole != null) {
				List<Role> roles = new ArrayList<Role>();
				Role role = new Role();
				BeanUtil.map(doctorRole, role);
				AccessControl accessControl = accessControlServices.getAccessControls(doctorRole.getId(),
						doctorRole.getLocationId(), doctorRole.getHospitalId());
				if (accessControl != null)
					role.setAccessModules(accessControl.getAccessModules());
				roles.add(role);
				response.setRole(roles);
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
	public Role addRole(Role request) {
		Role role = new Role();
		RoleCollection roleCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				checkIfRoleAlreadyExist(request.getRole(), request.getLocationId(), request.getHospitalId());
				roleCollection = new RoleCollection();
				BeanUtil.map(request, roleCollection);
				roleCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.allStringsEmpty(request.getLocationId())) {
					LocationCollection locationCollection = locationRepository
							.findOne(new ObjectId(request.getLocationId()));
					roleCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					roleCollection.setCreatedBy("ADMIN");
				}
			} else {
				roleCollection = roleRepository.findOne(new ObjectId(request.getId()));
				if (roleCollection == null) {
					logger.error(roleNotFoundException);
					throw new BusinessException(ServiceError.Unknown, roleNotFoundException);
				}
				roleCollection.setRole(roleCollection.getRole());
				roleCollection.setUpdatedTime(new Date());
			}
			roleCollection = roleRepository.save(roleCollection);

			if (request.getAccessModules() != null && !request.getAccessModules().isEmpty()) {
				AccessControl accessControl = new AccessControl();
				BeanUtil.map(request, accessControl);
				accessControl.setType(Type.ROLE);
				accessControl.setRoleOrUserId(roleCollection.getId().toString());
				accessControl = accessControlServices.setAccessControls(accessControl);
				if (accessControl != null)
					role.setAccessModules(accessControl.getAccessModules());
			}
			BeanUtil.map(roleCollection, role);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return role;
	}

	private void checkIfRoleAlreadyExist(String role, String locationId, String hospitalId) {
		try {
			Integer count = 0;
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				count = roleRepository.countByRole(role, new ObjectId(locationId), new ObjectId(hospitalId));
			else
				count = roleRepository.countByRole(role);
			if (count != null && count > 0) {
				logger.error("Role already exist with this name");
				throw new BusinessException(ServiceError.Unknown, "Role already exist with this name");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public List<Role> getRole(String range, int page, int size, String locationId, String hospitalId,
			String updatedTime, String role) {
		List<Role> response = null;

		try {
			switch (Range.valueOf(range.toUpperCase())) {

			case CUSTOM:
				response = getCustomRole(page, size, locationId, hospitalId, updatedTime, role);
				break;
			case BOTH:
				response = getCustomGlobalRole(page, size, locationId, hospitalId, updatedTime, role);
				break;
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private List<Role> getCustomGlobalRole(int page, int size, String locationId, String hospitalId, String updatedTime,
			String role) {
		List<Role> response = null;
		List<RoleCollection> roleCollections = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			if (DPDoctorUtils.anyStringEmpty(role)) {
				if (size > 0)
					roleCollections = roleRepository.findCustomGlobalRole(new ObjectId(locationId),
							new ObjectId(hospitalId), new Date(createdTimeStamp),
							new PageRequest(page, size, Direction.DESC, "createdTime"));
				else
					roleCollections = roleRepository.findCustomGlobalRole(new ObjectId(locationId),
							new ObjectId(hospitalId), new Date(createdTimeStamp),
							new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
					if (size > 0)
						roleCollections = roleRepository.findCustomGlobalDoctorRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomGlobalDoctorRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				} else if (role.equalsIgnoreCase(RoleEnum.STAFF.getRole())) {
					if (size > 0)
						roleCollections = roleRepository.findCustomGlobalStaffRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
										RoleEnum.CONSULTANT_DOCTOR.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
										RoleEnum.ADMIN.getRole(), RoleEnum.PATIENT.getRole(),
										RoleEnum.SUPER_ADMIN.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomGlobalStaffRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
										RoleEnum.CONSULTANT_DOCTOR.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
										RoleEnum.ADMIN.getRole(), RoleEnum.PATIENT.getRole(),
										RoleEnum.SUPER_ADMIN.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				}
			}

			if (roleCollections != null) {
				response = new ArrayList<Role>();
				for (RoleCollection roleCollection : roleCollections) {
					Role roleObj = new Role();
					AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(),
							roleCollection.getLocationId(), roleCollection.getHospitalId());
					BeanUtil.map(roleCollection, roleObj);
					if (accessControl != null)
						roleObj.setAccessModules(accessControl.getAccessModules());
					response.add(roleObj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<Role> getCustomRole(int page, int size, String locationId, String hospitalId, String updatedTime,
			String role) {
		List<Role> response = null;
		List<RoleCollection> roleCollections = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			if (DPDoctorUtils.anyStringEmpty(role)) {
				if (size > 0)
					roleCollections = roleRepository.findCustomRole(new ObjectId(locationId), new ObjectId(hospitalId),
							new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
				else
					roleCollections = roleRepository.findCustomRole(new ObjectId(locationId), new ObjectId(hospitalId),
							new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
					if (size > 0)
						roleCollections = roleRepository.findCustomDoctorRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomDoctorRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				} else if (role.equalsIgnoreCase(RoleEnum.STAFF.getRole())) {
					if (size > 0)
						roleCollections = roleRepository.findCustomStaffRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
										RoleEnum.HOSPITAL_ADMIN.getRole(), RoleEnum.ADMIN.getRole(),
										RoleEnum.PATIENT.getRole(), RoleEnum.SUPER_ADMIN.getRole(),
										RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomStaffRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.LOCATION_ADMIN.getRole(),
										RoleEnum.HOSPITAL_ADMIN.getRole(), RoleEnum.ADMIN.getRole(),
										RoleEnum.PATIENT.getRole(), RoleEnum.SUPER_ADMIN.getRole(),
										RoleEnum.CONSULTANT_DOCTOR.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				} else if (role.equalsIgnoreCase(RoleEnum.ADMIN.getRole())) {
					if (size > 0)
						roleCollections = roleRepository.findCustomAdminRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomAdminRole(new ObjectId(locationId),
								new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				} else if (role.equalsIgnoreCase("ALL")) {
					if (size > 0)
						roleCollections = roleRepository.findCustomRoleAndNotLocationHospitalAdmin(
								new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()),
								new PageRequest(page, size, Direction.DESC, "createdTime"));
					else
						roleCollections = roleRepository.findCustomRoleAndNotLocationHospitalAdmin(
								new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp),
								Arrays.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()),
								new Sort(Sort.Direction.DESC, "createdTime"));
				}
			}

			if (roleCollections != null) {
				response = new ArrayList<Role>();
				for (RoleCollection roleCollection : roleCollections) {
					Role roleObj = new Role();
					AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(),
							roleCollection.getLocationId(), roleCollection.getHospitalId());
					BeanUtil.map(roleCollection, roleObj);
					roleObj.setAccessModules(accessControl.getAccessModules());
					response.add(roleObj);
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
	public List<ClinicDoctorResponse> getUsers(int page, int size, String locationId, String hospitalId,
			String updatedTime, String role, Boolean active, String userState) {
		List<ClinicDoctorResponse> response = null;
		try {
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (active) {
				criteria.and("isActivate").is(active);
			}
			if (!DPDoctorUtils.anyStringEmpty(userState)) {
				if (userState.equalsIgnoreCase("COMPLETED")) {
					criteria.and("user.userState").is("USERSTATECOMPLETE");
				} else {
					criteria.and("user.userState").is(userState);
				}
			}

			if (size > 0) {
				doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
								Aggregation.unwind("doctor"),
								Aggregation.lookup("user_role_cl", "doctorId", "userId", "userRoleCollection"),
								Aggregation.unwind("userRoleCollection"),
								Aggregation.match(
										criteria.and("userRoleCollection.locationId").is(new ObjectId(locationId))),
								Aggregation.skip((page) * size), Aggregation.limit(size)),
						DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();
			} else {
				doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
								Aggregation.unwind("doctor"), Aggregation.match(criteria)),
						DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();
			}

			if (doctorClinicProfileLookupResponses != null) {
				response = new ArrayList<ClinicDoctorResponse>();
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					ClinicDoctorResponse clinicDoctorResponse = new ClinicDoctorResponse();
					if (doctorClinicProfileLookupResponse.getUser() != null) {
						BeanUtil.map(doctorClinicProfileLookupResponse.getUser(), clinicDoctorResponse);

						clinicDoctorResponse.setUserId(doctorClinicProfileLookupResponse.getUser().getId().toString());
						clinicDoctorResponse.setIsActivate(doctorClinicProfileLookupResponse.getIsActivate());
						clinicDoctorResponse.setDiscarded(doctorClinicProfileLookupResponse.getDiscarded());
						if (doctorClinicProfileLookupResponse.getDoctor() != null)
							clinicDoctorResponse.setRegisterNumber(
									doctorClinicProfileLookupResponse.getDoctor().getRegisterNumber());

						Criteria roleCriteria = new Criteria();

						if (DPDoctorUtils.anyStringEmpty(role))
							;
						else if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
							roleCriteria = new Criteria("roleCollection.role")
									.in(Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()));
						} else if (role.equalsIgnoreCase(RoleEnum.STAFF.getRole())) {
							roleCriteria = new Criteria("roleCollection.role")
									.nin(Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole(),
											RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
											RoleEnum.ADMIN.getRole(), RoleEnum.PATIENT.getRole(),
											RoleEnum.SUPER_ADMIN.getRole()));
						} else if (role.equalsIgnoreCase(RoleEnum.ADMIN.getRole())) {
							roleCriteria = new Criteria("roleCollection.role").in(Arrays
									.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()));
						} else if (role.equalsIgnoreCase("ALL")) {
							roleCriteria = new Criteria("roleCollection.role").nin(Arrays
									.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()));
						}

						List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate
								.aggregate(
										Aggregation
												.newAggregation(
														Aggregation
																.match(new Criteria("userId")
																		.is(doctorClinicProfileLookupResponse
																				.getDoctorId())
																		.and("locationId").is(new ObjectId(locationId))
																		.and("hospitalId")
																		.is(new ObjectId(hospitalId))),
														Aggregation.lookup("role_cl", "roleId", "_id",
																"roleCollection"),
														Aggregation.unwind("roleCollection"),
														Aggregation.match(roleCriteria)),
										UserRoleCollection.class, UserRoleLookupResponse.class)
								.getMappedResults();

						if (userRoleLookupResponses != null && !userRoleLookupResponses.isEmpty()) {
							List<Role> roles = new ArrayList<>();
							for (UserRoleLookupResponse userRoleLookupResponse : userRoleLookupResponses) {
								Role roleObj = new Role();
								AccessControl accessControl = accessControlServices.getAccessControls(
										userRoleLookupResponse.getRoleCollection().getId(),
										userRoleLookupResponse.getRoleCollection().getLocationId(),
										userRoleLookupResponse.getRoleCollection().getHospitalId());
								BeanUtil.map(userRoleLookupResponse.getRoleCollection(), roleObj);
								roleObj.setAccessModules(accessControl.getAccessModules());
								roles.add(roleObj);
							}
							clinicDoctorResponse.setRole(roles);
							clinicDoctorResponse
									.setColorCode(doctorClinicProfileLookupResponse.getUser().getColorCode());
							response.add(clinicDoctorResponse);
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
	public ESDoctorDocument getESDoctorDocument(RegisterDoctorResponse doctorResponse) {
		ESDoctorDocument esDoctorDocument = null;
		try {
			esDoctorDocument = new ESDoctorDocument();
			BeanUtil.map(doctorResponse, esDoctorDocument);
			LocationCollection locationCollection = locationRepository
					.findOne(new ObjectId(doctorResponse.getLocationId()));
			if (locationCollection != null) {
				BeanUtil.map(locationCollection, esDoctorDocument);
				esDoctorDocument.setClinicWorkingSchedules(locationCollection.getClinicWorkingSchedules());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esDoctorDocument;
	}

	@Override
	@Transactional
	public Role deleteRole(String roleId, Boolean discarded) {
		Role response = null;
		try {
			RoleCollection roleCollection = roleRepository.findOne(new ObjectId(roleId));
			if (roleCollection != null) {
				roleCollection.setDiscarded(discarded);
				roleCollection = roleRepository.save(roleCollection);
				response = new Role();
				BeanUtil.map(roleCollection, response);
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
	public void activateDeactivateUser(String userId, String locationId, Boolean isActivate) {
		try {
			DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdLocationId(new ObjectId(userId), new ObjectId(locationId));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setIsActivate(isActivate);
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public ClinicLabProperties updateLabProperties(ClinicLabProperties request) {
		ClinicLabProperties response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection != null) {
				if (request.getIsClinic().equals(false) && request.getIsLab().equals(false)) {
					logger.error("Location has to be either Clinic or Lab or Both");
					throw new BusinessException(ServiceError.Unknown,
							"Location has to be either Clinic or Lab or Both");
				}
				locationCollection.setIsLab(request.getIsLab());
				locationCollection.setIsClinic(request.getIsClinic());
				if (request.getIsLab()) {
					locationCollection.setIsHomeServiceAvailable(request.getIsHomeServiceAvailable());
					locationCollection.setIsNABLAccredited(request.getIsNABLAccredited());
					locationCollection.setIsOnlineReportsAvailable(request.getIsOnlineReportsAvailable());
				} else {
					locationCollection.setIsHomeServiceAvailable(false);
					locationCollection.setIsNABLAccredited(false);
					locationCollection.setIsOnlineReportsAvailable(false);
				}
				locationCollection = locationRepository.save(locationCollection);
				response = new ClinicLabProperties();
				BeanUtil.map(locationCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic IsLab");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic IsLab");
		}
		return response;
	}

	@Override
	@Transactional
	public Feedback addFeedback(Feedback request) {
		Feedback response = new Feedback();
		try {
			FeedbackCollection feedbackCollection = new FeedbackCollection();
			BeanUtil.map(request, feedbackCollection);
			feedbackCollection
					.setUniqueFeedbackId(UniqueIdInitial.FEEDBACK.getInitial() + DPDoctorUtils.generateRandomId());
			feedbackCollection.setCreatedTime(new Date());

			if (feedbackCollection != null
					&& (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.DOCTOR.getType())
							|| feedbackCollection.getType().getType().equals(FeedbackType.LAB.getType()))) {

				if (feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
						&& request.getResourceId() != null) {
					PrescriptionCollection prescriptionCollection = prescriptionRepository
							.findOne(new ObjectId(request.getResourceId()));
					if (prescriptionCollection != null) {
						prescriptionCollection.setIsFeedbackAvailable(true);
						prescriptionCollection.setUpdatedTime(new Date());
						prescriptionRepository.save(prescriptionCollection);
					}
				}
				if (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
						&& request.getResourceId() != null) {
					AppointmentCollection appointmentCollection = appointmentRepository
							.findOne(new ObjectId(request.getResourceId()));
					if (appointmentCollection != null) {
						appointmentCollection.setIsFeedbackAvailable(true);
						appointmentCollection.setUpdatedTime(new Date());
						appointmentRepository.save(appointmentCollection);
					}
				}
				if (feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType())
						&& request.getResourceId() != null) {
					RecordsCollection recordsCollection = recordsRepository
							.findOne(new ObjectId(request.getResourceId()));
					if (recordsCollection != null) {
						recordsCollection.setIsFeedbackAvailable(true);
						recordsCollection.setUpdatedTime(new Date());
						recordsRepository.save(recordsCollection);
					}
				}

				UserCollection patient = null;
				PatientCollection patientCollection = new PatientCollection();
				if (!DPDoctorUtils.anyStringEmpty(feedbackCollection.getUserId()))
					patient = userRepository.findOne(feedbackCollection.getUserId());
				patientCollection = patientRepository
						.findByUserIdLocationIdAndHospitalId(feedbackCollection.getUserId(), null, null);

				UserCollection doctor = null;
				if (!DPDoctorUtils.anyStringEmpty(feedbackCollection.getDoctorId()))
					doctor = userRepository.findOne(feedbackCollection.getDoctorId());

				LocationCollection locationCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(feedbackCollection.getLocationId()))
					locationCollection = locationRepository.findOne(feedbackCollection.getLocationId());

				feedbackCollection.setCreatedBy(patient.getFirstName());
				feedbackCollection = feedbackRepository.save(feedbackCollection);
				BeanUtil.map(feedbackCollection, response);
				if (patientCollection != null && patient != null) {
					User user = new User();
					BeanUtil.map(patient, user);
					BeanUtil.map(patientCollection, user);
					user.setImageUrl(getFinalImageURL(user.getImageUrl()));
					user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
					response.setPatient(user);
				}
				if (patient != null && doctor != null && locationCollection != null
						&& patient.getEmailAddress() != null) {
					String body = mailBodyGenerator.generateFeedbackEmailBody(patientCollection.getLocalPatientName(),
							(doctor != null ? doctor.getTitle() + " " + doctor.getFirstName() + " and" : ""),
							locationCollection.getLocationName(), feedbackCollection.getUniqueFeedbackId(),
							"feedbackUserToDoctorTemplate.vm");
					mailService.sendEmail(patient.getEmailAddress(), addFeedbackForDoctorSubject, body, null);
				}
			} else {
				feedbackCollection = feedbackRepository.save(feedbackCollection);
				BeanUtil.map(feedbackCollection, response);
				if (feedbackCollection.getEmailAddress() != null) {
					String body = mailBodyGenerator.generateFeedbackEmailBody(feedbackCollection.getCreatedBy(), null,
							null, null, "feedbackTemplate.vm");
					mailService.sendEmail(feedbackCollection.getEmailAddress(), addFeedbackSubject, body, null);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding feedback");
		}
		return response;
	}

	@Override
	@Transactional
	public ClinicProfile updateClinicProfileHandheld(ClinicProfileHandheld request) {
		ClinicProfile response = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
			if (locationCollection != null)
				BeanUtil.map(request, locationCollection);
			else {
				logger.error("No Clinic Found");
				throw new BusinessException(ServiceError.NoRecord, "No Clinic Found");
			}
			locationCollection.setAlternateClinicNumbers(request.getAlternateClinicNumbers());
			locationCollection = locationRepository.save(locationCollection);
			response = new ClinicProfile();
			BeanUtil.map(locationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While Updating Clinic Details");
			throw new BusinessException(ServiceError.Unknown, "Error While Updating Clinic Details");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientStatusResponse getPatientStatus(String patientId, String doctorId, String locationId,
			String hospitalId) {
		PatientStatusResponse response = new PatientStatusResponse();
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Integer prescriptionCount = 0, clinicalNotesCount = 0, recordsCount = 0;
			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorObjectId,
						patientObjectId, hospitalObjectId, locationObjectId);
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorObjectId,
						patientObjectId, hospitalObjectId, locationObjectId);
				recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorObjectId, patientObjectId,
						hospitalObjectId, locationObjectId);
			} else {
				prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherLocations(patientObjectId,
						hospitalObjectId, locationObjectId);
				clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherLocations(patientObjectId,
						hospitalObjectId, locationObjectId);
				recordsCount = recordsRepository.getRecordsForOtherLocations(patientObjectId, hospitalObjectId,
						locationObjectId);
			}

			if ((prescriptionCount != null && prescriptionCount > 0)
					|| (clinicalNotesCount != null && clinicalNotesCount > 0)
					|| (recordsCount != null && recordsCount > 0))
				response.setIsDataAvailableWithOtherDoctor(true);

			response.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error While getting status");
			throw new BusinessException(ServiceError.Unknown, "Error While getting status");
		}
		return response;
	}

	@Override
	@Transactional
	public Feedback visibleFeedback(String feedbackId, Boolean isVisible) {
		Feedback response = new Feedback();
		try {
			FeedbackCollection feedbackCollection = feedbackRepository.findOne(new ObjectId(feedbackId));
			if (feedbackCollection != null) {
				feedbackCollection.setUpdatedTime(new Date());
				feedbackCollection.setIsVisible(isVisible);
				feedbackCollection = feedbackRepository.save(feedbackCollection);
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdLocationId(feedbackCollection.getDoctorId(), feedbackCollection.getLocationId());
				if (doctorClinicProfileCollection != null) {
					if (isVisible)
						doctorClinicProfileCollection
								.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() + 1);
					else
						doctorClinicProfileCollection
								.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() - 1);
					doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				}
				BeanUtil.map(feedbackCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while editing feedback");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Feedback> getFeedback(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String type) {
		List<Feedback> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("type", "$type"), Fields.field("appType", "$appType"),
					Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
					Fields.field("doctorId", "$doctorId"), Fields.field("resourceId", "$resourceId"),
					Fields.field("userId", "$userId"), Fields.field("explanation", "$explanation"),
					Fields.field("deviceType", "$deviceType"), Fields.field("deviceInfo", "$deviceInfo"),
					Fields.field("isVisible", "$isVisible"), Fields.field("isRecommended", "$isRecommended"),
					Fields.field("uniqueFeedbackId", "$uniqueFeedbackId"),
					Fields.field("emailAddress", "$emailAddress"), Fields.field("isUserAnonymous", "$isUserAnonymous"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("patient.id", "$user.id"),
					Fields.field("patient.locationId", "$locationId"),
					Fields.field("patient.hospitalId", "$hospitalId"),
					Fields.field("patient.firstName", "$user.firstName"),
					Fields.field("patient.localPatientName", "$patientCard.localPatientName"),
					Fields.field("patient.emailAddress", "$user.emailAddress"),
					Fields.field("patient.countryCode", "$user.countryCode"),
					Fields.field("patient.mobileNumber", "$user.mobileNumber"),
					Fields.field("patient.gender", "$patientCard.gender"),
					Fields.field("patient.dob", "$patientCard.dob"),
					Fields.field("patient.imageUrl", "$patientCard.imageUrl"),
					Fields.field("patient.thumbnailUrl", "$patientCard.thumbnailUrl"),
					Fields.field("patient.colorCode", "$patientCard.colorCode"),
					Fields.field("patient.userUId", "$patientCard.userUId"),
					Fields.field("patient.bloodGroup", "$patientCard.bloodGroup")));

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("isVisible").is(true);

			Criteria patientCriteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				patientCriteria.and("patientCard.locationId").is(null);
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				patientCriteria.and("patientCard.hospitalId").is(null);
			}
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);

			if (size > 0)
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "userId", "userId", "patientCard"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("user_cl", "userId", "_id", "user"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
								Aggregation.match(patientCriteria), projectList, Aggregation.skip((page) * size),
								Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						FeedbackCollection.class, Feedback.class).getMappedResults();
			else
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "userId", "userId", "patientCard"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$patientCard").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("user_cl", "userId", "_id", "user"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
								Aggregation.match(patientCriteria), projectList,
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						FeedbackCollection.class, Feedback.class).getMappedResults();

			for (Feedback feedbackCollection : response) {
				if (feedbackCollection.getPatient() != null) {
					feedbackCollection.getPatient()
							.setImageUrl(getFinalImageURL(feedbackCollection.getPatient().getImageUrl()));
					feedbackCollection.getPatient()
							.setThumbnailUrl(getFinalImageURL(feedbackCollection.getPatient().getThumbnailUrl()));
					// feedbackCollection.setPatient(user);
				}
				// response.add(feedback);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting feedback");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean checkPatientNumber(String oldMobileNumber, String newMobileNumber) {
		Boolean response = false;
		try {
			Boolean isPatient = false;
			List<UserCollection> userCollections = userRepository.findByMobileNumber(oldMobileNumber);
			for (UserCollection userCollection : userCollections) {
				if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
					isPatient = true;
					break;
				}
			}
			if (isPatient) {
				userCollections = userRepository.findByMobileNumber(newMobileNumber);
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						logger.error("Patients already exist with this mobile number");
						throw new BusinessException(ServiceError.Unknown,
								"Patients already exist with this mobile number");
					}
				}
				response = otpService.otpGenerator(newMobileNumber, true);
			} else {
				logger.error("No Patients exist with this mobile number");
				throw new BusinessException(ServiceError.NoRecord, "No Patients exist with this mobile number");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while checking patient number");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean changePatientNumber(String oldMobileNumber, String newMobileNumber, String otpNumber) {
		Boolean response = false;
		try {
			response = otpService.checkOTPVerifiedForPatient(newMobileNumber, otpNumber);
			if (response) {
				List<UserCollection> userCollections = userRepository.findByMobileNumber(oldMobileNumber);
				for (UserCollection userCollection : userCollections) {
					if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
						userCollection.setMobileNumber(newMobileNumber);
						userRepository.save(userCollection);
					}
				}
			} else {
				logger.error("Please verify OTP for new mobile number");
				throw new BusinessException(ServiceError.Unknown, "Please verify OTP for new mobile number");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while changing patient number");
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public Boolean updateDoctorClinicProfile() {
		Boolean response = false;
		try {
			List<UserLocationCollection> userLocationCollections = userLocationRepository.findAll();
			for (UserLocationCollection userLocationCollection : userLocationCollections) {
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByUserLocationId(userLocationCollection.getId());
				if (doctorClinicProfileCollection == null) {
					doctorClinicProfileCollection = new DoctorClinicProfileCollection();
					doctorClinicProfileCollection.setCreatedTime(new Date());
				}
				doctorClinicProfileCollection.setUpdatedTime(new Date());
				doctorClinicProfileCollection.setDoctorId(userLocationCollection.getUserId());
				doctorClinicProfileCollection.setLocationId(userLocationCollection.getLocationId());
				doctorClinicProfileCollection.setDiscarded(userLocationCollection.getDiscarded());
				doctorClinicProfileCollection.setIsActivate(userLocationCollection.getIsActivate());
				doctorClinicProfileCollection.setIsVerified(userLocationCollection.getIsVerified());
				doctorClinicProfileRepository.save(doctorClinicProfileCollection);
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error");
		}
		return response;
	}

	@Override
	public ESPatientDocument getESPatientDocument(RegisteredPatientDetails patient) {
		ESPatientDocument esPatientDocument = null;
		try {
			esPatientDocument = new ESPatientDocument();
			if (patient.getAddress() != null) {
				BeanUtil.map(patient.getAddress(), esPatientDocument);
			}
			if (patient.getPatient() != null) {
				BeanUtil.map(patient.getPatient(), esPatientDocument);
			}
			BeanUtil.map(patient, esPatientDocument);
			if (patient.getBackendPatientId() != null)
				esPatientDocument.setId(patient.getBackendPatientId());
			if (patient.getReferredBy() != null)
				esPatientDocument.setReferredBy(patient.getReferredBy().getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return esPatientDocument;
	}

	@Override
	public Boolean updateRoleCollectionData() {
		Boolean response = false;
		try {
			RoleCollection doctorGlobalRoleCollection = roleRepository.findByRole(RoleEnum.DOCTOR.getRole());
			RoleCollection locationAdminGlobalRoleCollection = roleRepository
					.findByRole(RoleEnum.LOCATION_ADMIN.getRole());
			RoleCollection hospitalAdminGlobalRoleCollection = roleRepository
					.findByRole(RoleEnum.HOSPITAL_ADMIN.getRole());

			List<RoleCollection> roleCollections = roleRepository.findCustomRoles();
			for (RoleCollection roleCollection : roleCollections) {
				List<UserRoleCollection> userRoleCollections = userRoleRepository.findByRoleId(roleCollection.getId());
				ObjectId roleId = null;
				if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.DOCTOR.getRole()))
					roleId = doctorGlobalRoleCollection.getId();

				if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.LOCATION_ADMIN.getRole()))
					roleId = locationAdminGlobalRoleCollection.getId();

				if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.HOSPITAL_ADMIN.getRole()))
					roleId = hospitalAdminGlobalRoleCollection.getId();
				System.out.println(userRoleCollections.size());
				for (UserRoleCollection userRoleCollection : userRoleCollections) {
					userRoleCollection.setHospitalId(roleCollection.getHospitalId());
					userRoleCollection.setLocationId(roleCollection.getLocationId());
					userRoleCollection.setRoleId(roleId);
					userRoleRepository.save(userRoleCollection);
					response = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public ConsentForm addConcentForm(FormDataBodyPart file, ConsentForm request) {
		ConsentForm response = null;
		try {

			Date createdTime = new Date();

			UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
			if (file != null) {
				if (!DPDoctorUtils.anyStringEmpty(file.getFormDataContentDisposition().getFileName())) {
					String path = "sign" + File.separator + request.getPatientId();
					FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
					String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
					String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
					String imagepath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
					ImageURLResponse imageURLResponse = fileManager.saveImage(file, imagepath, false);
					if (imageURLResponse != null) {
						request.setSignImageURL(imagepath);
					}
				}
			}
			ConsentFormCollection consentFormCollection = new ConsentFormCollection();
			if (DPDoctorUtils.anyStringEmpty(request.getTitle())) {
				request.setTitle("CONSENT FORM");
			}
			BeanUtil.map(request, consentFormCollection);
			consentFormCollection.setCreatedTime(createdTime);
			consentFormCollection
					.setFormId(UniqueIdInitial.CONSENT_FORM.getInitial() + DPDoctorUtils.generateRandomId());
			consentFormCollection.setCreatedBy(doctor.getFirstName());
			consentFormCollection = consentFormRepository.save(consentFormCollection);
			response = new ConsentForm();
			BeanUtil.map(consentFormCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getSignImageURL()))
				response.setSignImageURL(getFinalImageURL(response.getSignImageURL()));

		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in add Consent Form ");
		}
		return response;
	}

	@Override
	public List<ConsentForm> getConcentForm(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, String PID, String searchTerm, boolean discarded, long updatedTime) {
		List<ConsentForm> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria("updatedTime").gt(new Date(updatedTime));
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(PID))
				criteria.and("PID").is(PID);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("localPatientName").regex(searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}
			criteria.and("discarded").is(discarded);
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			AggregationResults<ConsentForm> results = mongoTemplate.aggregate(aggregation, ConsentFormCollection.class,
					ConsentForm.class);
			response = results.getMappedResults();
			for (ConsentForm consentForm : response) {
				if (!DPDoctorUtils.anyStringEmpty(consentForm.getSignImageURL()))
					consentForm.setSignImageURL(getFinalImageURL(consentForm.getSignImageURL()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in getting Consent Form ");
		}
		return response;
	}

	@Override
	public ConsentForm deleteConcentForm(String consentFormId, boolean discarded) {
		ConsentForm response = null;
		try {
			ConsentFormCollection consentFormCollection = consentFormRepository.findOne(new ObjectId(consentFormId));
			if (consentFormCollection != null) {
				consentFormCollection.setDiscarded(discarded);
				consentFormCollection.setUpdatedTime(new Date());
				consentFormRepository.save(consentFormCollection);
				response = new ConsentForm();
				BeanUtil.map(consentFormCollection, response);
				response.setSignImageURL(getFinalImageURL(response.getSignImageURL()));
			} else {
				logger.warn("Invalid Referrence Id!");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid consentForm Id!");
			}
		} catch (BusinessException be) {
			be.printStackTrace();
			logger.error(be);
			throw new BusinessException(ServiceError.Unknown, be.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadConcentForm(String consentFormId) {
		String response = null;

		try {
			ConsentFormCollection consentFormCollection = consentFormRepository.findOne(new ObjectId(consentFormId));
			if (consentFormCollection != null) {
				UserCollection user = userRepository.findOne(consentFormCollection.getPatientId());
				JasperReportResponse jasperReportResponse = createJasper(consentFormCollection, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in download Consent Form ");
		}

		return response;
	}

	private JasperReportResponse createJasper(ConsentFormCollection consentFormCollection, UserCollection user)
			throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		ConsentFormItemJasperdetails consentFormItemJasperdetails = new ConsentFormItemJasperdetails();
		Boolean show = false;
		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getLocalPatientName())) {
			consentFormItemJasperdetails.setName(consentFormCollection.getLocalPatientName());
			show = true;
		}
		parameters.put("showName", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getTitle())) {
			consentFormItemJasperdetails.setTitle(consentFormCollection.getTitle());

		}

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getGender())) {
			consentFormItemJasperdetails.setGender(consentFormCollection.getGender());
			show = true;
		}
		parameters.put("showGender", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getPID())) {
			consentFormItemJasperdetails.setPID("<b>PID : </b>" + consentFormCollection.getPID());
			show = true;
		}
		parameters.put("showPID", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getMobileNumber())) {
			consentFormItemJasperdetails.setMobileNumber(consentFormCollection.getMobileNumber());
			show = true;
		}
		parameters.put("showMbno", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getEmailAddress())) {
			consentFormItemJasperdetails.setEmailAddress(consentFormCollection.getEmailAddress());
			show = true;
		}
		parameters.put("showEmail", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getAddress())) {
			consentFormItemJasperdetails.setAddress(consentFormCollection.getAddress());
			show = true;

		}
		parameters.put("showAddress", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getBloodGroup())) {
			consentFormItemJasperdetails.setBloodGroup(consentFormCollection.getBloodGroup());
			show = true;
		}
		parameters.put("showBloodGroup", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getDeclaration())) {
			consentFormItemJasperdetails.setDeclaration(consentFormCollection.getDeclaration());
			show = true;
		}
		parameters.put("showDeclaration", show);

		show = false;

		if (consentFormCollection.getDateOfSign() != null) {
			consentFormItemJasperdetails.setDateOfSign(simpleDateFormat.format(consentFormCollection.getDateOfSign()));
			show = true;
		}
		parameters.put("showSignDate", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getMedicalHistory())) {
			consentFormItemJasperdetails.setMedicalHistory(consentFormCollection.getMedicalHistory());
			show = true;
		}
		parameters.put("showMedicalHistory", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getLandLineNumber())) {
			consentFormItemJasperdetails.setLandLineNumber(consentFormCollection.getLandLineNumber());
			show = true;
		}

		parameters.put("showLandLineNo", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(consentFormCollection.getSignImageURL())) {
			consentFormItemJasperdetails.setSignImageUrl(imagePath + consentFormCollection.getSignImageURL());
			show = true;
		}

		parameters.put("showSignImage", show);

		show = false;

		if (consentFormCollection.getDob() != null) {
			consentFormItemJasperdetails.setBirthDate(consentFormCollection.getDob().getDays() + "/"
					+ consentFormCollection.getDob().getMonths() + "/" + consentFormCollection.getDob().getYears());
			consentFormItemJasperdetails.setAge(consentFormCollection.getDob().getAge().getYears() + " years : "
					+ consentFormCollection.getDob().getAge().getMonths() + " months :"
					+ consentFormCollection.getDob().getAge().getDays() + " days");
			show = true;
		}

		parameters.put("showDOB", show);

		parameters.put("item", consentFormItemJasperdetails);

		patientVisitService.generatePrintSetup(parameters, null, consentFormCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "CONSENTFORM-" + consentFormCollection.getFormId()
				+ new Date().getTime();

		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		response = jasperReportService.createPDF(ComponentType.CONSENT_FORM, parameters, consentFormA4FileName, layout,
				pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@Override
	public void emailConsentForm(String consentFormId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		ConsentFormCollection consentFormCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			consentFormCollection = consentFormRepository.findOne(new ObjectId(consentFormId));
			if (consentFormCollection != null) {
				if (consentFormCollection.getDoctorId() != null && consentFormCollection.getHospitalId() != null
						&& consentFormCollection.getLocationId() != null) {
					if (consentFormCollection.getDoctorId().equals(doctorId)
							&& consentFormCollection.getHospitalId().equals(hospitalId)
							&& consentFormCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(consentFormCollection.getPatientId());

						user.setFirstName(consentFormCollection.getLocalPatientName());
						emailTrackCollection.setDoctorId(consentFormCollection.getDoctorId());
						emailTrackCollection.setHospitalId(consentFormCollection.getHospitalId());
						emailTrackCollection.setLocationId(consentFormCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.CONSENT_FORM.getType());
						emailTrackCollection.setSubject("Consent Form");
						if (user != null) {
							emailTrackCollection.setPatientName(consentFormCollection.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(consentFormCollection, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse.setMailRecordCreatedDate(sdf.format(consentFormCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("consentForm Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"consentForm Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Consent Form not found.Please check consentFormId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Consent Form not found.Please check consentFormId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Consent Form", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Consent Form", body,
					mailResponse.getMailAttachment());
			if (mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public Integer updateRegisterPID(long createdTime) {
		try {

		List<PatientCollection> patientCollections = mongoTemplate.aggregate(
				Aggregation.newAggregation(Aggregation.match(new Criteria("PID").ne(null)),
						Aggregation.sort(new Sort(Direction.ASC, "createdTime")),
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", 
								new BasicDBObject("locationId", "$locationId").append("PID","$PID")).append("count", new BasicDBObject("$sum", 1)))),
						new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("locationId", "$locationId").append("PID", "$PID")
								.append("keep", new BasicDBObject(
								        "$cond", new BasicDBObject(
										          "if", new BasicDBObject("$gt", Arrays.asList("$count", 1)))
										        .append("then", "$count")
										        .append("else", 0))))),
				Aggregation.match(new Criteria("keep").gt(1))), PatientCollection.class, PatientCollection.class).getMappedResults();

		if(patientCollections != null)
		for (PatientCollection patientCollection : patientCollections) {
			List<PatientCollection> samePIDPatientCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(patientCollection.getLocationId()).and("PID").is(patientCollection.getPID())), Aggregation.sort(new Sort(Direction.ASC, "createdTime"))), PatientCollection.class, PatientCollection.class).getMappedResults();
			
			if(samePIDPatientCollections != null) {
				for (int i=0; i<samePIDPatientCollections.size(); i++) {
					PatientCollection patient = samePIDPatientCollections.get(i);
					
					if(patient.getRegistrationDate() == null) {
						patient.setRegistrationDate(patient.getCreatedTime().getTime());
					}
					if(i > 0) {
						Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
						localCalendar.setTime(new Date(patient.getRegistrationDate()));
						int currentDay = localCalendar.get(Calendar.DATE);
						int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
						int currentYear = localCalendar.get(Calendar.YEAR);

						DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
								DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
						Long startTimeinMillis = start.getMillis();
						DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
								DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
						Long endTimeinMillis = end.getMillis();
						List<PatientCollection> lastPatients = patientRepository.findTodaysRegisteredPatient(patient.getLocationId(), patient.getHospitalId(), startTimeinMillis, endTimeinMillis, new PageRequest(0, 1, Direction.DESC, "PID"));
						 
						PatientCollection lastPatient = lastPatients.get(0);
						Integer patientSize = Integer.parseInt(lastPatient.getPID().substring(lastPatient.getPID().length()-2, lastPatient.getPID().length()));
						LocationCollection location = locationRepository.findOne(patient.getLocationId());
						if (location == null) {
							logger.warn("Invalid Location Id");
							throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
						}
						String patientInitial = location.getPatientInitial();
						int patientCounter = location.getPatientCounter();
						if (patientCounter <= patientSize)
							patientCounter = patientCounter + patientSize;
						String generatedId = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay)
								+ DPDoctorUtils.getPrefixedNumber(currentMonth) + DPDoctorUtils.getPrefixedNumber(currentYear % 100)
								+ DPDoctorUtils.getPrefixedNumber(patientCounter);
						
						patient.setPID(generatedId);
					}
					patient = patientRepository.save(patient);
					ESPatientDocument esPatientDocument = esPatientRepository.findOne(patient.getId().toString());
					if(esPatientDocument != null) {
						esPatientDocument.setPID(patient.getPID());
						esPatientDocument.setRegistrationDate(patient.getRegistrationDate());
						esPatientDocument = esPatientRepository.save(esPatientDocument);
					}
					
				}
			}
		}
		
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return 1;
	}

	@Override
	public FormContent addeditFromContent(FormContent request) {
		FormContent reponse = null;
		try {
			FormContentCollection contentCollection = null;

			contentCollection = new FormContentCollection();
			BeanUtil.map(request, contentCollection);
			UserCollection docter = userRepository.findOne(contentCollection.getDoctorId());
			if (docter != null) {
				if (DPDoctorUtils.anyStringEmpty(request.getId())) {
					if (!DPDoctorUtils.anyStringEmpty(contentCollection.getTitle())) {
						contentCollection.setTitle(contentCollection.getTitle().toUpperCase());
					}
					contentCollection.setCreatedTime(new Date());
					contentCollection.setCreatedBy(docter.getTitle() + docter.getFirstName());
				} else {
					if (!DPDoctorUtils.anyStringEmpty(contentCollection.getTitle())) {
						contentCollection.setTitle(contentCollection.getTitle().toUpperCase());
					}
					if (!DPDoctorUtils.anyStringEmpty(contentCollection.getTitle())) {
						contentCollection.setTitle(contentCollection.getTitle().toUpperCase());
					}
					contentCollection.setUpdatedTime(new Date());

				}
				contentCollection = formContentRepository.save(contentCollection);
				reponse = new FormContent();
				BeanUtil.map(contentCollection, reponse);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "doctor not found by doctor Id");
			}

		} catch (Exception e) {

			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Form Content");
			throw new BusinessException(ServiceError.Unknown, " Error Occurred While Saving Form Content");

		}

		return reponse;
	}

	@Override
	public List<FormContent> getFormContents(int page, int size, String doctorId, String locationId, String hospitalId,
			String type, String title, String updatedTime, boolean discarded) {
		List<FormContent> reponse = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria = criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria = criteria.and("type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(title)) {
				criteria = criteria.and("title").regex("^" + title, "i");
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			criteria = criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<FormContent> aggregationResults = mongoTemplate.aggregate(aggregation,
					FormContentCollection.class, FormContent.class);
			reponse = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While listing  Form Content");
			throw new BusinessException(ServiceError.Unknown, " Error Occurred While listing Form Content");
		}
		return reponse;
	}

	@Override
	public FormContent deleteFormContent(String contentId, Boolean discarded) {
		FormContent reponse = null;
		try {
			FormContentCollection contentCollection = formContentRepository.findOne(new ObjectId(contentId));
			if (contentCollection != null) {
				contentCollection.setDiscarded(discarded);
				contentCollection.setUpdatedTime(new Date());
				contentCollection = formContentRepository.save(contentCollection);
				reponse = new FormContent();
				BeanUtil.map(contentCollection, reponse);
			} else {
				logger.warn("Content not found. Please check content Id");
				throw new BusinessException(ServiceError.NoRecord, "Content not found. Please check content Id");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While discarding  Form Content");
			throw new BusinessException(ServiceError.Unknown, " Error Occurred While discarding Form Content");
		}
		return reponse;
	}

	@Override
	public UserReminders addEditPatientReminders(UserReminders request, String reminderType) {
		UserReminders response = new UserReminders();
		try {
			ObjectId userId = new ObjectId(request.getUserId());
			UserRemindersCollection userRemindersCollection = userRemindersRepository.findByUserId(userId);

			if (userRemindersCollection == null) {
				userRemindersCollection = new UserRemindersCollection();
				userRemindersCollection.setCreatedTime(new Date());
				userRemindersCollection.setUserId(userId);
			}

			if (DPDoctorUtils.allStringsEmpty(reminderType)) {
				userRemindersCollection.setFoodReminder(request.getFoodReminder());
				userRemindersCollection.setMedicineReminder(request.getMedicineReminder());
				userRemindersCollection.setWalkReminder(request.getWalkReminder());
				userRemindersCollection.setWaterReminder(request.getWaterReminder());
				userRemindersCollection.setWorkoutReminder(request.getWorkoutReminder());
			} else {
				switch (ReminderType.valueOf(reminderType.toUpperCase())) {
				case WATER:
					userRemindersCollection.setWaterReminder(request.getWaterReminder());
					break;
				case FOOD:
					userRemindersCollection.setFoodReminder(request.getFoodReminder());
					break;
				case MEDICINE:
					userRemindersCollection.setMedicineReminder(request.getMedicineReminder());
					break;
				case WORKOUT:
					userRemindersCollection.setWorkoutReminder(request.getWorkoutReminder());
					break;
				case WALK:
					userRemindersCollection.setWalkReminder(request.getWalkReminder());
					break;
				default:
					break;
				}
			}

			userRemindersCollection.setUpdatedTime(new Date());
			userRemindersCollection = userRemindersRepository.save(userRemindersCollection);
			BeanUtil.map(userRemindersCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding Patient Reminders");
		}
		return response;
	}

	@Override
	public UserReminders getPatientReminders(String userId, String reminderType) {
		UserReminders response = null;
		try {
			UserRemindersCollection userRemindersCollection = userRemindersRepository
					.findByUserId(new ObjectId(userId));
			if (userRemindersCollection == null) {
				logger.error(reminderNotFoundException);
				throw new BusinessException(ServiceError.Unknown, reminderNotFoundException);
			}
			response = new UserReminders();

			if (DPDoctorUtils.allStringsEmpty(reminderType)) {
				BeanUtil.map(userRemindersCollection, response);
			} else {
				switch (ReminderType.valueOf(reminderType.toUpperCase())) {
				case WATER:
					response.setWaterReminder(userRemindersCollection.getWaterReminder());
					break;
				case FOOD:
					response.setFoodReminder(userRemindersCollection.getFoodReminder());
					break;
				case MEDICINE:
					response.setMedicineReminder(userRemindersCollection.getMedicineReminder());
					break;
				case WORKOUT:
					response.setWorkoutReminder(userRemindersCollection.getWorkoutReminder());
					break;
				case WALK:
					response.setWalkReminder(userRemindersCollection.getWalkReminder());
					break;
				default:
					break;
				}
				response.setId(userRemindersCollection.getId().toString());
				response.setUserId(userRemindersCollection.getUserId().toString());
				response.setCreatedTime(userRemindersCollection.getCreatedTime());
				response.setUpdatedTime(userRemindersCollection.getUpdatedTime());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Reminders");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserAddress addEditUserAddress(UserAddress request) {
		UserAddress response = null;
		try {
			UserAddressCollection userAddressCollection = new UserAddressCollection();

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, userAddressCollection);
				List<UserCollection> users = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
					users = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(
											Aggregation
													.match(new Criteria("mobileNumber").is(request.getMobileNumber())),
											Aggregation.project("id")),
									UserCollection.class, UserCollection.class)
							.getMappedResults();
				} else {
					users = mongoTemplate
							.aggregate(
									Aggregation.newAggregation(
											Aggregation.match(new Criteria("id").in(request.getUserIds())),
											Aggregation.limit(1),
											Aggregation.lookup("user_cl", "mobileNumber", "mobileNumber", "user"),
											Aggregation.unwind("user"),
											new CustomAggregationOperation(
													new BasicDBObject("$project", new BasicDBObject("id", "user.id")))),
									UserCollection.class, UserCollection.class)
							.getMappedResults();
				}
				if (users == null || users.isEmpty()) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid mobileNumber or userID");
				}
				List<ObjectId> userIds = (List<ObjectId>) CollectionUtils.collect(users,
						new BeanToPropertyValueTransformer("id"));
				userAddressCollection.setUserIds(userIds);
				userAddressCollection.setCreatedTime(new Date());
			} else {
				userAddressCollection = userAddressRepository.findOne(new ObjectId(request.getId()));
				userAddressCollection.setUpdatedTime(new Date());
			}
			userAddressCollection.setAddress(request.getAddress());
			userAddressCollection.setFullName(request.getFullName());
			userAddressCollection.setHomeDeliveryMobileNumber(request.getHomeDeliveryMobileNumber());

			Address address = userAddressCollection.getAddress();
			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
					(!DPDoctorUtils.anyStringEmpty(address.getStreetAddress()) ? address.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
									? address.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ? address.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode() : ""));
			if (geocodedLocations != null && !geocodedLocations.isEmpty())
				BeanUtil.map(geocodedLocations.get(0), userAddressCollection.getAddress());

			userAddressCollection = userAddressRepository.save(userAddressCollection);
			response = new UserAddress();
			BeanUtil.map(userAddressCollection, response);
			
			String formattedAddress = (!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
						? address.getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
								? address.getLandmarkDetails() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getLocality())
								? address.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getCity())
								? address.getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getState())
								? address.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getCountry())
								? address.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode())
								? address.getPostalCode() : "");

				if (formattedAddress.charAt(formattedAddress.length() - 2) == ',') {
					formattedAddress = formattedAddress.substring(0, formattedAddress.length() - 2);
				}
				response.setFormattedAddress(formattedAddress);
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding user address");
		}
		return response;
	}

	@Override
	public List<UserAddress> getUserAddress(String userId, String mobileNumber, Boolean discarded) {
		List<UserAddress> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria.and("userIds").is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				criteria.and("mobileNumber").is(mobileNumber);

			if (!discarded)
				criteria.and("discarded").is(discarded);
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)),
					UserAddressCollection.class, UserAddress.class).getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (UserAddress userAddress : response) {
					Address address = userAddress.getAddress();
					String formattedAddress = (!DPDoctorUtils.anyStringEmpty(address.getStreetAddress())
							? address.getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLandmarkDetails())
									? address.getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getLocality()) ? address.getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCity()) ? address.getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getState()) ? address.getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getCountry()) ? address.getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(address.getPostalCode()) ? address.getPostalCode() : "");

					if (formattedAddress.charAt(formattedAddress.length() - 2) == ',') {
						formattedAddress = formattedAddress.substring(0, formattedAddress.length() - 2);
					}
					userAddress.setFormattedAddress(formattedAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting user address");
		}
		return response;
	}

	@Override
	public UserAddress deleteUserAddress(String addressId, String userId, String mobileNumber, Boolean discarded) {
		UserAddress response = null;
		try {
			UserAddressCollection userAddressCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(mobileNumber))
				userAddressCollection = userAddressRepository.find(new ObjectId(addressId), mobileNumber);
			else
				userAddressCollection = userAddressRepository.find(new ObjectId(addressId), new ObjectId(userId));
			if (userAddressCollection != null) {
				userAddressCollection.setDiscarded(discarded);
				userAddressCollection.setUpdatedTime(new Date());
				userAddressCollection = userAddressRepository.save(userAddressCollection);
				response = new UserAddress();
				BeanUtil.map(userAddressCollection, response);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid addressId or userId");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding user address");
		}
		return response;
	}
}
