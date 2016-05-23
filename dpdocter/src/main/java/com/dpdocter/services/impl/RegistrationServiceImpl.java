package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.BloodGroup;
import com.dpdocter.beans.ClinicAddress;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.ClinicLabProperties;
import com.dpdocter.beans.ClinicLogo;
import com.dpdocter.beans.ClinicProfile;
import com.dpdocter.beans.ClinicSpecialization;
import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Feedback;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Profession;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.ReferenceDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Role;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.FeedbackCollection;
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
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FeedbackRepository;
import com.dpdocter.repository.GroupRepository;
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
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.ClinicImageAddRequest;
import com.dpdocter.request.ClinicLogoAddRequest;
import com.dpdocter.request.ClinicProfileHandheld;
import com.dpdocter.request.DoctorRegisterRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.ClinicDoctorResponse;
import com.dpdocter.response.PatientInitialAndCounter;
import com.dpdocter.response.PatientStatusResponse;
import com.dpdocter.response.RegisterDoctorResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.solr.document.SolrDoctorDocument;

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
    private GenerateUniqueUserNameService generateUniqueUserNameService;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
    private GroupRepository groupRepository;

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
    private SMSServices sMSServices;

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
	
    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    @Value(value = "${mail.forgotPassword.subject}")
    private String forgotUsernamePasswordSub;

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

    @Override
    @Transactional
    public User checkIfPatientExist(PatientRegistrationRequest request) {
	try {
	    UserCollection userCollection = userRepository.checkPatient(request.getFirstName(), request.getEmailAddress(), request.getMobileNumber());
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
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {

		if (DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
			logger.warn("Mobile Number cannot be null");
			throw new BusinessException(ServiceError.InvalidInput, "Mobile Number cannot be null");
		 }
	    // get role of specified type
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    Date createdTime = new Date();
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge().getYears() < 0) {
		logger.warn(DOB);
		throw new BusinessException(ServiceError.InvalidInput, DOB);
	    }
	    User user = new User();
	    BeanUtil.map(request, user);
	    String uniqueUserName = generateUniqueUserNameService.generate(user);
	    userCollection.setUserName(uniqueUserName);
	    userCollection.setPassword(generateRandomAlphanumericString(10));
	    userCollection.setUserUId(UniqueIdInitial.USER.getInitial()+DPDoctorUtils.generateRandomId());
	    userCollection.setIsActive(true);
	    userCollection.setCreatedTime(createdTime);
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection = userRepository.save(userCollection);

	    // assign roles
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    
	    // save Patient Info
	    PatientCollection patientCollection = new PatientCollection();
	    BeanUtil.map(request, patientCollection);
	    patientCollection.setUserId(userCollection.getId());
	    patientCollection.setRegistrationDate(request.getDateOfVisit());

	    patientCollection.setCreatedTime(createdTime);
	    patientCollection.setPID(patientIdGenerator(request.getDoctorId(), request.getLocationId(), request.getHospitalId()));

	    if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
		ProfessionCollection professionCollection = professionRepository.findOne(request.getProfession());
		if (professionCollection != null)
		    patientCollection.setProfession(professionCollection.getProfession());
	    }
	    patientCollection.setNotes(request.getNotes());
	    if (request.getImage() != null) {
		String path = "profile-images";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		String imageUrl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		patientCollection.setImageUrl(imageUrl);
		String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		patientCollection.setThumbnailUrl(thumbnailUrl);

	    }

	    ReferencesCollection referencesCollection = null;
	    if (request.getReferredBy() != null) {
		if (request.getReferredBy().getId() != null) {
		    referencesCollection = referrenceRepository.findOne(request.getReferredBy().getId());
		}
		if (referencesCollection == null) {
		    referencesCollection = new ReferencesCollection();
		    BeanUtil.map(request.getReferredBy(), referencesCollection);
		    referencesCollection.setDoctorId(request.getDoctorId());
		    referencesCollection.setHospitalId(request.getHospitalId());
		    referencesCollection.setLocationId(request.getLocationId());
		    referencesCollection = referrenceRepository.save(referencesCollection);
		}
		patientCollection.setReferredBy(referencesCollection.getId());
	    }
	    patientCollection = patientRepository.save(patientCollection);

	    // assign groups
	    if (request.getGroups() != null) {
		for (String group : request.getGroups()) {
		    PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
		    patientGroupCollection.setGroupId(group);
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
	    registeredPatientDetails.setUserId(userCollection.getId());
	    Patient patient = new Patient();
	    BeanUtil.map(patientCollection, patient);
	    patient.setPatientId(patientCollection.getId());

	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(),
		    patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(),
		    patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(),
		    patientCollection.getHospitalId(), patientCollection.getLocationId());

	    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
		    || (recordsCount != null && recordsCount > 0))
		patient.setIsDataAvailableWithOtherDoctor(true);

	    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(),
		    patientCollection.getHospitalId(), userCollection.getId()));

	    registeredPatientDetails.setPatient(patient);
	    registeredPatientDetails.setDob(patientCollection.getDob());
	    registeredPatientDetails.setGender(patientCollection.getGender());
	    registeredPatientDetails.setPID(patientCollection.getPID());
	    registeredPatientDetails.setDoctorId(patientCollection.getDoctorId());
	    registeredPatientDetails.setLocationId(patientCollection.getLocationId());
	    registeredPatientDetails.setHospitalId(patientCollection.getHospitalId());
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
		groupCollections = (List<GroupCollection>) groupRepository.findAll(request.getGroups());
		groups = new ArrayList<Group>();
		BeanUtil.map(groupCollections, groups);
	    }
	    registeredPatientDetails.setGroups(groups);
	    pushNotificationServices.notifyUser(patientCollection.getId(), "You are register");
	    if (userCollection.getMobileNumber() != null) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(patientCollection.getDoctorId());
		smsTrackDetail.setHospitalId(patientCollection.getHospitalId());
		smsTrackDetail.setLocationId(patientCollection.getLocationId());

		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(patientCollection.getUserId());

		SMS sms = new SMS();
		sms.setSmsText("OTP Verification");

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(userCollection.getMobileNumber());
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
//		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
//		smsDetails.add(smsDetail);
//		smsTrackDetail.setSmsDetails(smsDetails);
		// sMSServices.sendSMS(smsTrackDetail, false);
		
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
    public RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request) {
	RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
	PatientCollection patientCollection = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {

	    // save Patient Info
	    if (DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getLocationId(), request.getHospitalId())) {
		UserCollection userCollection = userRepository.findOne(request.getUserId());
		if (userCollection == null) {
		    logger.error("Incorrect User Id");
		    throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
		}
		BeanUtil.map(userCollection, registeredPatientDetails);
		
		userCollection.setIsActive(true);
		userCollection.setEmailAddress(request.getEmailAddress());
		userCollection = userRepository.save(userCollection);
		patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(request.getUserId(), request.getDoctorId(),
			request.getLocationId(), request.getHospitalId());
		if (patientCollection == null) {
		    patientCollection = new PatientCollection();
		    BeanUtil.map(request, patientCollection);
		    patientCollection.setUserId(userCollection.getId());
		    patientCollection.setDoctorId(null);
		    patientCollection.setLocationId(null);
		    patientCollection.setHospitalId(null);
		    patientCollection.setCreatedTime(new Date());
		} else {
		    patientCollection.setBloodGroup(request.getBloodGroup());
		    patientCollection.setGender(request.getGender());
		    patientCollection.setEmailAddress(request.getEmailAddress());
		    patientCollection.setDob(request.getDob());
		}
		if (request.getImage() != null) {
		    String path = "profile-images";
		    // save image
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    String imageUrl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		    patientCollection.setImageUrl(imageUrl);
		    userCollection.setImageUrl(null);
		    registeredPatientDetails.setImageUrl(imageUrl);
		    String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		    patientCollection.setThumbnailUrl(thumbnailUrl);
		    userCollection.setThumbnailUrl(null);
		    registeredPatientDetails.setThumbnailUrl(thumbnailUrl);
		}
		

		Patient patient = new Patient();
		BeanUtil.map(patientCollection, patient);
		patient.setPatientId(patientCollection.getId());
		registeredPatientDetails.setPatient(patient);
		registeredPatientDetails.setDob(patientCollection.getDob());
		registeredPatientDetails.setUserId(userCollection.getId());
		registeredPatientDetails.setGender(patientCollection.getGender());
		registeredPatientDetails.setPID(patientCollection.getPID());
		registeredPatientDetails.setDoctorId(patientCollection.getDoctorId());
		registeredPatientDetails.setLocationId(patientCollection.getLocationId());
		registeredPatientDetails.setHospitalId(patientCollection.getHospitalId());
		registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
	    registeredPatientDetails.setAddress(patientCollection.getAddress());
	    } else {
		patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(request.getUserId(), request.getDoctorId(),
			request.getLocationId(), request.getHospitalId());
		if (patientCollection != null) {
		    String patientId = patientCollection.getId();

		    BeanUtil.map(request, patientCollection);
		    patientCollection.setId(patientId);
		    patientCollection.setUpdatedTime(new Date());
		} else {
		    patientCollection = new PatientCollection();
		    patientCollection.setCreatedTime(new Date());
		    BeanUtil.map(request, patientCollection);
		    patientCollection.setRegistrationDate(request.getDateOfVisit());
		}
		
		patientCollection.setRelations(request.getRelations());
		patientCollection.setNotes(request.getNotes());

		if (!DPDoctorUtils.anyStringEmpty(patientCollection.getPID())) {
		    patientCollection.setPID(patientCollection.getPID());
		} else {
		    patientCollection.setPID(patientIdGenerator(request.getDoctorId(), request.getLocationId(), request.getHospitalId()));
		}
		if (!DPDoctorUtils.anyStringEmpty(request.getProfession())) {
		    ProfessionCollection professionCollection = professionRepository.findOne(request.getProfession());
		    if (professionCollection != null)
			patientCollection.setProfession(professionCollection.getProfession());
		}
		

		ReferencesCollection referencesCollection = null;
		if (request.getReferredBy() != null) {
		    if (request.getReferredBy().getId() != null) {
			referencesCollection = referrenceRepository.findOne(request.getReferredBy().getId());
		    }
		    if (referencesCollection == null) {
			referencesCollection = new ReferencesCollection();
			BeanUtil.map(request.getReferredBy(), referencesCollection);
			BeanUtil.map(request, referencesCollection);
			referencesCollection.setId(null);
			referencesCollection.setDoctorId(request.getDoctorId());
			referencesCollection.setHospitalId(request.getHospitalId());
			referencesCollection.setLocationId(request.getLocationId());
			referencesCollection = referrenceRepository.save(referencesCollection);
		    }
		    patientCollection.setReferredBy(referencesCollection.getId());
		}
		

		// assign groups
		if (request.getGroups() != null) {
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    if (patientGroupCollections != null) {
			for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
			    patientGroupRepository.delete(patientGroupCollection);
			}
		    }
		    for (String group : request.getGroups()) {
			PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
			patientGroupCollection.setGroupId(group);
			patientGroupCollection.setPatientId(patientCollection.getUserId());
			patientGroupCollection.setCreatedTime(new Date());
			patientGroupRepository.save(patientGroupCollection);
		    }

		}
		UserCollection userCollection = userRepository.findOne(request.getUserId());
		if (userCollection == null) {
		    logger.error("Incorrect User Id");
		    throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
		}
		/*registeredPatientDetails = new RegisteredPatientDetails();*/
		BeanUtil.map(userCollection, registeredPatientDetails);
		if (request.getImage() != null) {
		    String path = "profile-images";
		    // save image
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    String imageUrl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		    patientCollection.setImageUrl(imageUrl);
		    userCollection.setImageUrl(null);
		    registeredPatientDetails.setImageUrl(imageUrl);
		    
		    String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		    patientCollection.setThumbnailUrl(thumbnailUrl);
		    userCollection.setThumbnailUrl(null);
		    registeredPatientDetails.setThumbnailUrl(thumbnailUrl);
		}
		registeredPatientDetails.setUserId(userCollection.getId());
		patientCollection = patientRepository.save(patientCollection);
		registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
		registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
		Patient patient = new Patient();
		BeanUtil.map(patientCollection, patient);
		patient.setPatientId(patientCollection.getId());

		Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(),
			patientCollection.getHospitalId(), patientCollection.getLocationId());
		Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(),
			userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
		Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(),
			patientCollection.getHospitalId(), patientCollection.getLocationId());

		if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			|| (recordsCount != null && recordsCount > 0))
		    patient.setIsDataAvailableWithOtherDoctor(true);

		patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(),
			patientCollection.getHospitalId(), userCollection.getId()));

		registeredPatientDetails.setPatient(patient);
		registeredPatientDetails.setDob(patientCollection.getDob());
		registeredPatientDetails.setGender(patientCollection.getGender());
		registeredPatientDetails.setPID(patientCollection.getPID());
		registeredPatientDetails.setDoctorId(patientCollection.getDoctorId());
		registeredPatientDetails.setLocationId(patientCollection.getLocationId());
		registeredPatientDetails.setHospitalId(patientCollection.getHospitalId());
		registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
		if (referencesCollection != null) {
		    Reference reference = new Reference();
		    BeanUtil.map(referencesCollection, reference);
		    registeredPatientDetails.setReferredBy(reference);
		}
		registeredPatientDetails.setAddress(patientCollection.getAddress());
		if (request.getGroups() != null) {
		    groupCollections = (List<GroupCollection>) groupRepository.findAll(request.getGroups());
		    groups = new ArrayList<Group>();
		    BeanUtil.map(groupCollections, groups);
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
		throw new BusinessException(ServiceError.NotAcceptable, checkPatientCount);
	    }
	}
    }

    @Override
    @Transactional
    public List<User> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId, String hospitalId) {
	List<User> users = null;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(phoneNumber);
	    if (userCollections != null) {
		users = new ArrayList<User>();
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
			User user = new User();
			
			if (locationId != null && hospitalId != null) {
			    List<PatientCollection> patientCollections = patientRepository.findByUserId(userCollection.getId());
			    boolean isPartOfClinic = false;
			    if (patientCollections != null) {
				for (PatientCollection patientCollection : patientCollections) {
				    if (patientCollection.getDoctorId() != null && patientCollection.getLocationId() != null
					    && patientCollection.getHospitalId() != null) {
					if (patientCollection.getDoctorId().equals(doctorId) && patientCollection.getLocationId().equals(locationId)
						&& patientCollection.getHospitalId().equals(hospitalId)) {
					    isPartOfClinic = true;
					    break;
					}
				    }
				}
			    }
			    if(!isPartOfClinic){
			    	user.setId(userCollection.getId());
			    	user.setFirstName(userCollection.getFirstName());
			    	user.setMobileNumber(userCollection.getMobileNumber());
			    }else{
			    	BeanUtil.map(userCollection, user);
			    }
			    user.setIsPartOfClinic(isPartOfClinic);
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

    private char[] generateRandomAlphanumericString(int count) {
	return RandomStringUtils.randomAlphabetic(count).toCharArray();
    }

    @Override
    @Transactional
    public RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId, String hospitalId) {
	RegisteredPatientDetails registeredPatientDetails = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {
	    UserCollection userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userId, doctorId, locationId, hospitalId);
		if (patientCollection != null) {
		    
		    Reference reference = null;
		    if (patientCollection.getReferredBy() != null) {
			    ReferencesCollection referencesCollection = referrenceRepository.findOne(patientCollection.getReferredBy());
			    if (referencesCollection != null) {
				reference = new Reference();
				BeanUtil.map(referencesCollection, reference);
			    }
			}
		    patientCollection.setReferredBy(null);
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    @SuppressWarnings("unchecked")
		    Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		    registeredPatientDetails = new RegisteredPatientDetails();
		    BeanUtil.map(patientCollection, registeredPatientDetails);
		    BeanUtil.map(userCollection, registeredPatientDetails);
		    registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
		    registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
		    
		    registeredPatientDetails.setUserId(userCollection.getId());
		    registeredPatientDetails.setReferredBy(reference);
		    Patient patient = new Patient();
		    BeanUtil.map(patientCollection, patient);
		    patient.setPatientId(patientCollection.getId());

		    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);

		    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			    || (recordsCount != null && recordsCount > 0))
			patient.setIsDataAvailableWithOtherDoctor(true);

		    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(),
			    patientCollection.getHospitalId(), userCollection.getId()));
		    registeredPatientDetails.setPatient(patient);
		    registeredPatientDetails.setAddress(patientCollection.getAddress());
		    groupCollections = (List<GroupCollection>) groupRepository.findAll(groupIds);
		    groups = new ArrayList<Group>();
		    BeanUtil.map(groupCollections, groups);
		    /*
		     * registeredPatientDetails.setGroups((List<String>)
		     * groupIds);
		     */
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
		    UserCollection userCollection = userRepository.findOne(reference.getDoctorId());
		    if (userCollection != null) {
			referrencesCollection
				.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}
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
	    ReferencesCollection referrencesCollection = referrenceRepository.findOne(referenceId);
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
    public List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
	List<ReferenceDetail> response = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded) {
		discards[1] = true;
	    }
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalReferences(page, size, updatedTime, discards);
		break;
	    case CUSTOM:
		response = getCustomReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discards);
		break;
	    case BOTH:
		response = getCustomGlobalReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discards);
		break;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<ReferenceDetail> getGlobalReferences(int page, int size, String updatedTime, boolean[] discards) {
	List<ReferenceDetail> response = null;
	List<ReferencesCollection> referrencesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		referrencesCollections = referrenceRepository.findAll(new Date(createdTimeStamp), discards,
			new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else
		referrencesCollections = referrenceRepository.findAll(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));
	    if (referrencesCollections != null) {
		response = new ArrayList<ReferenceDetail>();
		BeanUtil.map(referrencesCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<ReferenceDetail> getCustomReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean[] discards) {
	List<ReferenceDetail> response = null;
	List<ReferencesCollection> referrencesCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(doctorId))
		;
	    else {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "createdTime"));
		    else
			referrencesCollections = referrenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "createdTime"));
		    else
			referrencesCollections = referrenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    }
	    if (referrencesCollections != null) {
		response = new ArrayList<ReferenceDetail>();
		BeanUtil.map(referrencesCollections, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<ReferenceDetail> getCustomGlobalReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean[] discards) {
	List<ReferenceDetail> response = null;
	List<ReferencesCollection> referrencesCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(doctorId)) {
		if (size > 0)
		    referrencesCollections = referrenceRepository.findCustomGlobal(new Date(createdTimeStamp), discards,
			    new PageRequest(page, size, Direction.DESC, "createdTime"));
		else
		    referrencesCollections = referrenceRepository.findCustomGlobal(new Date(createdTimeStamp), discards,
			    new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "createdTime"));
		    else
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "createdTime"));
		    else
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    }
	    if (referrencesCollections != null) {
		response = new ArrayList<ReferenceDetail>();
		BeanUtil.map(referrencesCollections, response);
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
    public String patientIdGenerator(String doctorId, String locationId, String hospitalId) {
	String generatedId = null;
	try {
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);

	    DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
	    DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
	    List<PatientCollection> patientCollections = patientRepository.findTodaysRegisteredPatient(doctorId, locationId, hospitalId, start, end);
	    int patientCount = 0;
	    if (CollectionUtils.isNotEmpty(patientCollections)) {
		patientCount = patientCollections.size();
	    }

	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocation != null) {
		DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocation.getId());
		if (clinicProfileCollection == null) {
		    clinicProfileCollection = new DoctorClinicProfileCollection();
		    clinicProfileCollection.setCreatedTime(new Date());
		    clinicProfileCollection.setUserLocationId(userLocation.getId());
		    doctorClinicProfileRepository.save(clinicProfileCollection);
		}
		String patientInitial = clinicProfileCollection.getPatientInitial();
		int patientCounter = clinicProfileCollection.getPatientCounter();

		if(patientCount > 0)patientCounter = patientCounter + patientCount + 1;
		generatedId = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
			+ DPDoctorUtils.getPrefixedNumber(currentYear % 100) + DPDoctorUtils.getPrefixedNumber(patientCounter);
		} else {
		logger.warn("Doctor Id and Location Id does not match.");
		throw new BusinessException(ServiceError.NoRecord, "Doctor Id and Location Id does not match.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return generatedId;
    }

    @Override
    @Transactional
    public PatientInitialAndCounter getPatientInitialAndCounter(String doctorId, String locationId) {
	PatientInitialAndCounter patientInitialAndCounter = null;
	try {
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocation != null) {
		DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocation.getId());
		if (clinicProfileCollection != null) {
		    patientInitialAndCounter = new PatientInitialAndCounter();
		    BeanUtil.map(clinicProfileCollection, patientInitialAndCounter);
		    patientInitialAndCounter.setDoctorId(doctorId);
		    patientInitialAndCounter.setLocationId(locationId);
		}
	    } else {
		logger.warn("Doctor Id and Location Id does not match.");
		throw new BusinessException(ServiceError.NoRecord, "Doctor Id and Location Id does not match.");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Updating Patient Initial and Counter");
	}
	return patientInitialAndCounter;
    }

    @Override
    @Transactional
    public String updatePatientInitialAndCounter(String doctorId, String locationId, String patientInitial, int patientCounter) {
	String response = null;
	try {
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocation != null) {

		response = checkIfPatientInitialAndCounterExist(doctorId, locationId, patientInitial, patientCounter);
		if (response == null) {
		    DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocation.getId());
		    if (clinicProfileCollection == null)
			clinicProfileCollection = new DoctorClinicProfileCollection();
		    clinicProfileCollection.setUserLocationId(userLocation.getId());
		    clinicProfileCollection.setPatientInitial(patientInitial);
		    clinicProfileCollection.setPatientCounter(patientCounter);
		    clinicProfileCollection = doctorClinicProfileRepository.save(clinicProfileCollection);
		    response = "true";
		}
	    } else {
		logger.warn("Doctor Id and Location Id does not match.");
		throw new BusinessException(ServiceError.NoRecord, "Doctor Id and Location Id does not match.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error While Updating Patient Initial and Counter");
	    throw new BusinessException(ServiceError.Unknown, "Error While Updating Patient Initial and Counter");
	}
	return response;
    }

    private String checkIfPatientInitialAndCounterExist(String doctorId, String locationId, String patientInitial, int patientCounter) {
	String response = null;
	try {
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);

	    String date = DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
		    + DPDoctorUtils.getPrefixedNumber(currentYear % 100);
	    String generatedId = patientInitial + date;

	    Criteria criteria = new Criteria("doctorId").is(doctorId).and("locationId").is(locationId).and("PID").regex(generatedId + ".*");

	    Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
		    Aggregation.limit(1));

	    AggregationResults<PatientCollection> groupResults = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientCollection.class);
	    List<PatientCollection> results = groupResults.getMappedResults();
	    if (results != null && !results.isEmpty()) {
		String PID = results.get(0).getPID();
		PID = PID.substring((patientInitial + date).length());
		if (patientCounter <= Integer.parseInt(PID)) {
		    response = "Patient already exist for Prefix: " + patientInitial + " , Date: " + date + " Id Number: " + patientCounter
			    + ". Please enter Id greater than " + PID;
		}
	    }
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
	    locationCollection = locationRepository.findOne(clinicId);
	    if (locationCollection != null) {
		location = new Location();
		BeanUtil.map(locationCollection, location);
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
	    locationCollection = locationRepository.findOne(request.getId());
	    String landmarkDetails = null; 
	    if (locationCollection != null){
	    	landmarkDetails = locationCollection.getLandmarkDetails();
	    	BeanUtil.map(request, locationCollection);
	    }
	    List<GeocodedLocation> geocodedLocations = locationServices
		    .geocodeLocation((locationCollection.getLocationName() != null ? locationCollection.getLocationName() : "")
			    + (locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress() : "")
			    + (locationCollection.getCity() != null ? locationCollection.getCity() : "")
			    + (locationCollection.getState() != null ? locationCollection.getState() : "")
			    + (locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);

	    locationCollection.setLandmarkDetails(landmarkDetails);
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
	    locationCollection = locationRepository.findOne(request.getId());
	    String locationName = "";
	    if (locationCollection != null){
	    	locationName = locationCollection.getLocationName();
	    	BeanUtil.map(request, locationCollection);
	    }
		
	    List<GeocodedLocation> geocodedLocations = locationServices
		    .geocodeLocation((locationCollection.getLocationName() != null ? locationCollection.getLocationName() + " " : "")
			    + (locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress() + " " : "")
			    + (locationCollection.getCity() != null ? locationCollection.getCity() + " " : "")
			    + (locationCollection.getState() != null ? locationCollection.getState() + " " : "")
			    + (locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);
	    if(DPDoctorUtils.anyStringEmpty(request.getLocationName()))locationCollection.setLocationName(locationName);
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
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null)
		BeanUtil.map(request, locationCollection);
	    locationCollection.setWorkingSchedules(request.getWorkingSchedules());
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
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null)
		BeanUtil.map(request, locationCollection);
	    locationCollection.setSpecialization(request.getSpecialization());
	    locationCollection.setId(request.getId());
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
	List<ProfessionCollection> professionCollections = null;
	try {
	    long updateTimeStamp = Long.parseLong(updatedTime);

	    if (size > 0)
		professionCollections = professionRepository.find(new Date(updateTimeStamp), new PageRequest(page, size, Direction.DESC, "updateTime"));
	    else
		professionCollections = professionRepository.find(new Date(updateTimeStamp), new Sort(Direction.DESC, "updateTime"));
	    if (professionCollections != null) {
		professions = new ArrayList<Profession>();
		BeanUtil.map(professionCollections, professions);
	    }

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
	    LocationCollection locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection == null) {
		logger.warn("Clinic not found");
		throw new BusinessException(ServiceError.NotFound, "Clinic not found");
	    } else {
		if (request.getImage() != null) {
		    String path = "clinic" + File.separator + "logos";
		    // save image
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		    locationCollection.setLogoUrl(imageurl);

		    String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(request.getImage(), path);
		    locationCollection.setLogoThumbnailUrl(thumbnailUrl);
		    locationCollection = locationRepository.save(locationCollection);

		    List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository.findByLocationId(request.getId());
		    if (printSettingsCollections != null) {
			for (PrintSettingsCollection printSettingsCollection : printSettingsCollections) {
			    printSettingsCollection.setClinicLogoUrl(imageurl);
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
	    LocationCollection locationCollection = locationRepository.findOne(request.getId());
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
			String imageurl = fileManager.saveImageAndReturnImageUrl(image, path);
			String thumbnailUrl = fileManager.saveThumbnailAndReturnThumbNailUrl(image, path);
			ClinicImage clinicImage = new ClinicImage();
			clinicImage.setImageUrl(imageurl);
			clinicImage.setThumbnailUrl(thumbnailUrl);
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
	    LocationCollection locationCollection = locationRepository.findOne(locationId);
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
    public RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request, UriInfo uriInfo) {
	RegisterDoctorResponse response = null;
	try {
	    RoleCollection doctorRole = null;
	    if (request.getRoleId() != null) {
		doctorRole = roleRepository.findOne(request.getRoleId());
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
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection = userRepository.save(userCollection);

	    // save doctor specific details
	    DoctorCollection doctorCollection = new DoctorCollection();
	    BeanUtil.map(request, doctorCollection);
	    doctorCollection.setUserId(userCollection.getId());
	    doctorCollection.setCreatedTime(new Date());
	    doctorCollection = doctorRepository.save(doctorCollection);

	    // assign role to doctor
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleCollection = userRoleRepository.save(userRoleCollection);

	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), request.getLocationId());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationCollection.setIsActivate(request.getIsActivate());
	    userLocationRepository.save(userLocationCollection);

	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setResourceId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), tokenCollection.getId(), uriInfo);
	    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);

	    body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
		    userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId(), uriInfo);
	    mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);

	    response = new RegisterDoctorResponse();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, response);
	    response.setHospitalId(request.getHospitalId());
	    response.setLocationId(request.getLocationId());
	    response.setUserId(userCollection.getId());

	    // if (userCollection.getMobileNumber() != null) {
	    // SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	    // smsTrackDetail.setDoctorId(userCollection.getId());
	    // smsTrackDetail.setHospitalId(request.getHospitalId());
	    // smsTrackDetail.setLocationId(request.getLocationId());
	    //
	    // SMSDetail smsDetail = new SMSDetail();
	    // smsDetail.setPatientId(userCollection.getId());
	    //
	    // SMS sms = new SMS();
	    // sms.setSmsText("OTP Verification");
	    //
	    // SMSAddress smsAddress = new SMSAddress();
	    // smsAddress.setRecipient(userCollection.getMobileNumber());
	    // sms.setSmsAddress(smsAddress);
	    //
	    // smsDetail.setSms(sms);
	    // List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	    // smsDetails.add(smsDetail);
	    // smsTrackDetail.setSmsDetails(smsDetails);
	    // sMSServices.sendSMS(smsTrackDetail, false);
	    // }

	    if (doctorRole != null) {
		Role role = new Role();
		BeanUtil.map(doctorRole, role);
		AccessControl accessControl = accessControlServices.getAccessControls(role.getId(), role.getLocationId(), role.getHospitalId());
		if (accessControl != null)
		    role.setAccessModules(accessControl.getAccessModules());
		response.setRole(role);
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
		doctorRole = roleRepository.findOne(request.getRoleId());
	    }

	    if (doctorRole == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    UserCollection userCollection = userRepository.findByUserNameAndEmailAddress(request.getEmailAddress(), request.getEmailAddress());
	    userCollection.setFirstName(request.getFirstName());

	    DoctorCollection doctorCollection = doctorRepository.findByUserId(userCollection.getId());
	    if (doctorCollection.getAdditionalNumbers() != null)
		if (!doctorCollection.getAdditionalNumbers().contains(request.getMobileNumber()))
		    doctorCollection.getAdditionalNumbers().add(request.getMobileNumber());
		else {
		    List<String> additionalNumbers = new ArrayList<String>();
		    additionalNumbers.add(request.getMobileNumber());
		}

	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), request.getLocationId());
	    userLocationCollection.setIsActivate(request.getIsActivate());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    List<RoleCollection> roleCollections = roleRepository.findByLocationIdAndHospitalId(request.getLocationId(), request.getHospitalId());
	    List<String> roleIds = (List<String>) CollectionUtils.collect(roleCollections, new BeanToPropertyValueTransformer("id"));

	    UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdAndRoleId(userCollection.getId(), roleIds);
	    if (userRoleCollection == null) {
		userRoleCollection = new UserRoleCollection();
		userRoleCollection.setCreatedTime(new Date());
		userRoleCollection.setUserId(userCollection.getId());
		userRoleCollection.setRoleId(request.getRoleId());
		userRoleCollection = userRoleRepository.save(userRoleCollection);
	    } else {
		userRoleCollection.setRoleId(request.getRoleId());
		userRoleCollection = userRoleRepository.save(userRoleCollection);
	    }
	    response = new RegisterDoctorResponse();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, response);
	    response.setHospitalId(request.getHospitalId());
	    response.setLocationId(request.getLocationId());
	    response.setUserId(userCollection.getId());

	    if (doctorRole != null) {
		Role role = new Role();
		BeanUtil.map(doctorRole, role);
		AccessControl accessControl = accessControlServices.getAccessControls(role.getId(), role.getLocationId(), role.getHospitalId());
		if (accessControl != null)
		    role.setAccessModules(accessControl.getAccessModules());
		response.setRole(role);
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
	try {
	    RoleCollection roleCollection = new RoleCollection();
	    BeanUtil.map(request, roleCollection);
	    roleCollection.setCreatedTime(new Date());
	    roleCollection = roleRepository.save(roleCollection);

	    if (request.getAccessModules() != null && !request.getAccessModules().isEmpty()) {
		AccessControl accessControl = new AccessControl();
		BeanUtil.map(request, accessControl);
		accessControl.setType(Type.ROLE);
		accessControl.setRoleOrUserId(roleCollection.getId());
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

    @Override
    @Transactional
    public List<Role> getRole(String range, int page, int size, String locationId, String hospitalId, String updatedTime) {
	List<Role> response = null;

	try {
	    switch (Range.valueOf(range.toUpperCase())) {

	    case GLOBAL:
		response = getGlobalRole(page, size, updatedTime);
		break;
	    case CUSTOM:
		response = getCustomRole(page, size, locationId, hospitalId, updatedTime);
		break;
	    case BOTH:
		response = getCustomGlobalRole(page, size, locationId, hospitalId, updatedTime);
		break;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;

    }

    private List<Role> getCustomGlobalRole(int page, int size, String locationId, String hospitalId, String updatedTime) {
	List<Role> response = null;
	List<RoleCollection> roleCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		if (size > 0)
		    roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
		else
		    roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		if (size > 0)
		    roleCollections = roleRepository.findCustomGlobal(locationId, hospitalId, new Date(createdTimeStamp),
			    new PageRequest(page, size, Direction.DESC, "createdTime"));
		else
		    roleCollections = roleRepository.findCustomGlobal(locationId, hospitalId, new Date(createdTimeStamp),
			    new Sort(Sort.Direction.DESC, "createdTime"));
	    }
	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(),
			    roleCollection.getHospitalId());
		    BeanUtil.map(roleCollection, role);
		    role.setAccessModules(accessControl.getAccessModules());
		    response.add(role);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<Role> getCustomRole(int page, int size, String locationId, String hospitalId, String updatedTime) {
	List<Role> response = null;
	List<RoleCollection> roleCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		roleCollections = roleRepository.findCustom(locationId, hospitalId, new Date(createdTimeStamp),
			new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else
		roleCollections = roleRepository.findCustom(locationId, hospitalId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(),
			    roleCollection.getHospitalId());
		    BeanUtil.map(roleCollection, role);
		    role.setAccessModules(accessControl.getAccessModules());
		    response.add(role);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<Role> getGlobalRole(int page, int size, String updatedTime) {
	List<Role> response = null;
	List<RoleCollection> roleCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0)
		roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else
		roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(),
			    roleCollection.getHospitalId());
		    BeanUtil.map(roleCollection, role);
		    role.setAccessModules(accessControl.getAccessModules());
		    response.add(role);
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
    public List<ClinicDoctorResponse> getDoctors(int page, int size, String locationId, String hospitalId, String updatedTime) {
	List<ClinicDoctorResponse> response = null;
	try {
	    List<UserLocationCollection> userLocationCollections = null;
	    if (size > 0)
		userLocationCollections = userLocationRepository.findByLocationId(locationId, new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else
		userLocationCollections = userLocationRepository.findByLocationId(locationId, new Sort(Sort.Direction.DESC, "createdTime"));

	    if (userLocationCollections != null) {
		response = new ArrayList<ClinicDoctorResponse>();
		for (UserLocationCollection userLocationCollection : userLocationCollections) {
		    ClinicDoctorResponse clinicDoctorResponse = new ClinicDoctorResponse();
		    clinicDoctorResponse.setIsActivate(userLocationCollection.getIsActivate());
		    clinicDoctorResponse.setDiscarded(userLocationCollection.getDiscarded());
		    UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
		    if (userCollection != null) {
			clinicDoctorResponse.setFirstName(userCollection.getFirstName());
			clinicDoctorResponse.setLastSession(userCollection.getLastSession());
			clinicDoctorResponse.setUserId(userCollection.getId());
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
    public SolrDoctorDocument getSolrDoctorDocument(RegisterDoctorResponse doctorResponse) {
	SolrDoctorDocument solrDoctorDocument = null;
	try {
	    solrDoctorDocument = new SolrDoctorDocument();
	    BeanUtil.map(doctorResponse, solrDoctorDocument);
	    LocationCollection locationCollection = locationRepository.findOne(doctorResponse.getLocationId());
	    if (locationCollection != null) {
		BeanUtil.map(locationCollection, solrDoctorDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return solrDoctorDocument;
    }

    @Override
    @Transactional
    public Role deleteRole(String roleId, Boolean discarded) {
    	Role response = null;
	try {
	    RoleCollection roleCollection = roleRepository.findOne(roleId);
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
    public void deleteUser(String userId, String locationId, Boolean discarded) {
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(userId, locationId);
	    if (userLocationCollection != null) {
		userLocationCollection.setDiscarded(discarded);
		userLocationRepository.save(userLocationCollection);
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
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null) {
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
	    feedbackCollection.setCreatedTime(new Date());
	    feedbackCollection = feedbackRepository.save(feedbackCollection);
	    if (feedbackCollection != null && (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
		    || feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
		    || feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType()))) {
		if (feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType()) && request.getResourceId() != null) {
		    PrescriptionCollection prescriptionCollection = prescriptionRepository.findOne(request.getResourceId());
		    if (prescriptionCollection != null) {
			prescriptionCollection.setIsFeedbackAvailable(true);
			prescriptionCollection.setUpdatedTime(new Date());
			prescriptionRepository.save(prescriptionCollection);
		    }
		}
		if (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType()) && request.getResourceId() != null) {
		    AppointmentCollection appointmentCollection = appointmentRepository.findOne(request.getResourceId());
		    if (appointmentCollection != null) {
			appointmentCollection.setIsFeedbackAvailable(true);
			appointmentCollection.setUpdatedTime(new Date());
			appointmentRepository.save(appointmentCollection);
		    }
		}
		if (feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType()) && request.getResourceId() != null) {
		    RecordsCollection recordsCollection = recordsRepository.findOne(request.getResourceId());
		    if (recordsCollection != null) {
			recordsCollection.setIsFeedbackAvailable(true);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);
		    }
		}
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(feedbackCollection.getDoctorId(),
			feedbackCollection.getLocationId());
		if (userLocationCollection != null) {
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
			    .findByLocationId(userLocationCollection.getId());
		    if (doctorClinicProfileCollection != null) {
			if (feedbackCollection.getIsRecommended())
			    doctorClinicProfileCollection.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() + 1);
			else
			    doctorClinicProfileCollection.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() - 1);
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		    }
		}
		visibleFeedback(feedbackCollection.getId(), true);
		feedbackCollection = feedbackRepository.findOne(feedbackCollection.getId());
	    }
	    BeanUtil.map(feedbackCollection, response);
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
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null)	BeanUtil.map(request, locationCollection);
	    else{
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
    public PatientStatusResponse getPatientStatus(String patientId, String doctorId, String locationId, String hospitalId) {
	PatientStatusResponse response = new PatientStatusResponse();
	try {
	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, patientId, hospitalId, locationId);
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, patientId, hospitalId, locationId);
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, patientId, hospitalId, locationId);

	    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
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
	    FeedbackCollection feedbackCollection = feedbackRepository.findOne(feedbackId);
	    if (feedbackCollection != null) {
		feedbackCollection.setUpdatedTime(new Date());
		feedbackCollection.setIsVisible(isVisible);
		feedbackCollection = feedbackRepository.save(feedbackCollection);
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(feedbackCollection.getDoctorId(),
			feedbackCollection.getLocationId());
		if (userLocationCollection != null) {
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
			    .findByLocationId(userLocationCollection.getId());
		    if (doctorClinicProfileCollection != null) {
			if (isVisible)
			    doctorClinicProfileCollection.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() + 1);
			else
			    doctorClinicProfileCollection.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() - 1);
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		    }

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
    public List<Feedback> getFeedback(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, String type) {
	List<Feedback> response = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    List<FeedbackCollection> feedbackCollections = null;
	    if (DPDoctorUtils.anyStringEmpty(doctorId)){
	    	//THis is for ADMIN so isVisible = false
	    	if(DPDoctorUtils.anyStringEmpty(type)){
	    		if (size > 0)
					feedbackCollections = feedbackRepository.find(false, new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else
					feedbackCollections = feedbackRepository.find(false, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
	    	}else{
	    		if (size > 0)
					feedbackCollections = feedbackRepository.findByType(type, false, new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else
					feedbackCollections = feedbackRepository.findByType(type, false, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
	    	}
	    }
	    else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			feedbackCollections = feedbackRepository.find(doctorId, true, new Date(createdTimeStamp),
				new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
		    else
			feedbackCollections = feedbackRepository.find(doctorId, true, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
		} else {
		    if (size > 0)
			feedbackCollections = feedbackRepository.find(doctorId, locationId, hospitalId, true, new Date(createdTimeStamp),
				new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
		    else
			feedbackCollections = feedbackRepository.find(doctorId, locationId, hospitalId, true, new Date(createdTimeStamp),
				new Sort(Direction.DESC, "createdTime"));
		}
	    }
	    if (feedbackCollections != null) {
		response = new ArrayList<Feedback>();
		for(FeedbackCollection feedbackCollection : feedbackCollections){
			Feedback feedback = new Feedback();
			BeanUtil.map(feedbackCollection, feedback);
			UserCollection userCollection = userRepository.findOne(feedback.getUserId());
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(feedback.getUserId(),
					feedback.getDoctorId(), feedback.getLocationId(), feedback.getHospitalId());
			if(userCollection != null && patientCollection != null){
				User user = new User();
				BeanUtil.map(userCollection, user);
				BeanUtil.map(patientCollection, user);
				user.setImageUrl(getFinalImageURL(user.getImageUrl()));
				user.setThumbnailUrl(getFinalImageURL(user.getThumbnailUrl()));
				feedback.setPatient(user);
			}
			response.add(feedback);
		}
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
		try{
			Boolean isPatient = false;
			List<UserCollection> userCollections = userRepository.findByMobileNumber(oldMobileNumber);
			for(UserCollection userCollection : userCollections){
				if(!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())){
					isPatient = true;
					break;
				}
			}
			if(isPatient){
				userCollections = userRepository.findByMobileNumber(newMobileNumber);
				for(UserCollection userCollection : userCollections){
					if(!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())){
						logger.error("Patients already exist with this mobile number");
					    throw new BusinessException(ServiceError.Unknown, "Patients already exist with this mobile number");
					}
				}
				response = otpService.otpGenerator(newMobileNumber);
			}
			else{
				logger.error("No Patients exist with this mobile number");
			    throw new BusinessException(ServiceError.NoRecord, "No Patients exist with this mobile number");
			}
		}catch (Exception e) {
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
		try{
			response = otpService.checkOTPVerifiedForPatient(newMobileNumber, otpNumber);
			if(response){
				List<UserCollection> userCollections = userRepository.findByMobileNumber(oldMobileNumber);
				for(UserCollection userCollection : userCollections){
					if(!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())){
						userCollection.setMobileNumber(newMobileNumber);
						userRepository.save(userCollection);
					}
				}
			}else{
				logger.error("Please verify OTP for new mobile number");
			    throw new BusinessException(ServiceError.Unknown, "Please verify OTP for new mobile number");
			}
		}catch (Exception e) {
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
}
