
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
import org.bson.types.ObjectId;
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
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FeedbackType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.Type;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FeedbackRepository;
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
import com.dpdocter.response.ImageURLResponse;
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
import com.dpdocter.services.TransactionalManagementService;

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

//    @Autowired
//    private GroupRepository groupRepository;

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

    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    @Value(value = "${mail.forgotPassword.subject}")
    private String forgotUsernamePasswordSub;

    @Value(value = "${mail.staffmember.account.verify.subject}")
    private String staffmemberAccountVerifySub;
    
    @Value(value = "${mail.add.existing.doctor.to.clinic.subject}")
    private String addExistingDoctorToClinicSub;
    
    @Value(value = "${mail.add.doctor.to.clinic..verify.subject}")
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
	List<Group> groups = null;
	try {
	    // get role of specified type
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	    if (roleCollection == null) {
		logger.warn(role);
		throw new BusinessException(ServiceError.NoRecord, role);
	    }
	    
	    LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
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
		    patientCollection.setProfession(request.getProfession());
	    }
	    patientCollection.setNotes(request.getNotes());
	    if (request.getImage() != null) {
		String path = "profile-images";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
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
		    referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
		    referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
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
	    patient.setPatientId(patientCollection.getId().toString());

	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());

	    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
		    || (recordsCount != null && recordsCount > 0))
		patient.setIsDataAvailableWithOtherDoctor(true);

	    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(), patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(), userCollection.getId().toString()));

	    registeredPatientDetails.setPatient(patient);
	    registeredPatientDetails.setDob(patientCollection.getDob());
	    registeredPatientDetails.setGender(patientCollection.getGender());
	    registeredPatientDetails.setPID(patientCollection.getPID());
	    registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
	    registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
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
			for(String groupId : request.getGroups())groupObjectIds.add(new ObjectId(groupId));
			groups = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))), GroupCollection.class, Group.class).getMappedResults();
	    }
	    registeredPatientDetails.setGroups(groups);
	    pushNotificationServices.notifyUser(patientCollection.getUserId().toString(), "Welcome to "+locationCollection.getLocationName()+", let us know about your visit. We will be happy to serve you again.", ComponentType.PATIENT.getType(), patientCollection.getUserId().toString());
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
	List<Group> groups = null;
	try {

		ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(request.getUserId()))userObjectId = new ObjectId(request.getUserId());
		if(!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))doctorObjectId = new ObjectId(request.getDoctorId());
    	if(!DPDoctorUtils.anyStringEmpty(request.getLocationId()))locationObjectId = new ObjectId(request.getLocationId());
    	if(!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))hospitalObjectId = new ObjectId(request.getHospitalId());
    	
	    // save Patient Info
	    if (DPDoctorUtils.anyStringEmpty(doctorObjectId, hospitalObjectId, locationObjectId)) {
		UserCollection userCollection = userRepository.findOne(userObjectId);
		if (userCollection == null) {
		    logger.error("Incorrect User Id");
		    throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id");
		}
		BeanUtil.map(userCollection, registeredPatientDetails);
		
		userCollection.setIsActive(true);
		userCollection.setEmailAddress(request.getEmailAddress());
		userCollection = userRepository.save(userCollection);
		patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userObjectId, doctorObjectId, locationObjectId, hospitalObjectId);
		if (patientCollection != null) {
		    patientCollection.setBloodGroup(request.getBloodGroup());
		    patientCollection.setGender(request.getGender());
		    patientCollection.setEmailAddress(request.getEmailAddress());
		    patientCollection.setDob(request.getDob());
		}else{
			logger.error("Incorrect User Id, DoctorId, LocationId, HospitalId");
		    throw new BusinessException(ServiceError.InvalidInput, "Incorrect User Id, DoctorId, LocationId, HospitalId");
		}
		if (request.getImage() != null) {
		    String path = "profile-images";
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
		    patientCollection.setImageUrl(imageURLResponse.getImageUrl());
		    userCollection.setImageUrl(null);
		    registeredPatientDetails.setImageUrl(imageURLResponse.getImageUrl());
		    patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
		    userCollection.setThumbnailUrl(null);
		    registeredPatientDetails.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
		}
		

		Patient patient = new Patient();
		BeanUtil.map(patientCollection, patient);
		patient.setPatientId(patientCollection.getId().toString());
		registeredPatientDetails.setPatient(patient);
		registeredPatientDetails.setDob(patientCollection.getDob());
		registeredPatientDetails.setUserId(userCollection.getId().toString());
		registeredPatientDetails.setGender(patientCollection.getGender());
		registeredPatientDetails.setPID(patientCollection.getPID());
		registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
		registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
		registeredPatientDetails.setHospitalId(patientCollection.getHospitalId().toString());
		registeredPatientDetails.setCreatedTime(patientCollection.getCreatedTime());
	    registeredPatientDetails.setAddress(patientCollection.getAddress());
	    
	    patientCollection = patientRepository.save(patientCollection);
	    } else {
		patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userObjectId, doctorObjectId, locationObjectId, hospitalObjectId);
		if (patientCollection != null) {
		    ObjectId patientId = patientCollection.getId();

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
			patientCollection.setProfession(request.getProfession());
		}
		

		ReferencesCollection referencesCollection = null;
		if (request.getReferredBy() != null) {
		    if (request.getReferredBy().getId() != null) {
			referencesCollection = referrenceRepository.findOne(new ObjectId(request.getReferredBy().getId()));
		    }
		    if (referencesCollection == null) {
			referencesCollection = new ReferencesCollection();
			BeanUtil.map(request.getReferredBy(), referencesCollection);
			BeanUtil.map(request, referencesCollection);
			referencesCollection.setId(null);
			referencesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			referencesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
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
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
			for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
				if(request.getGroups() != null && !request.getGroups().isEmpty()){
					groupIds.add(patientGroupCollection.getGroupId().toString());
				    if(!request.getGroups().contains(patientGroupCollection.getGroupId().toString())){
				    	patientGroupCollection.setDiscarded(true);
				    	patientGroupRepository.save(patientGroupCollection);
				    }
				}else{
					patientGroupCollection.setDiscarded(true);
			    	patientGroupRepository.save(patientGroupCollection);
				}
			  }
		    }

		    if (request.getGroups() != null && !request.getGroups().isEmpty()) {
			for (String group : request.getGroups()) {
			    if(!groupIds.contains(group)){
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
		/*registeredPatientDetails = new RegisteredPatientDetails();*/
		BeanUtil.map(userCollection, registeredPatientDetails);
		if (request.getImage() != null) {
		    String path = "profile-images";
		    // save image
		    request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
		    patientCollection.setImageUrl(imageURLResponse.getImageUrl());
		    userCollection.setImageUrl(null);
		    registeredPatientDetails.setImageUrl(imageURLResponse.getImageUrl());
		    patientCollection.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
		    userCollection.setThumbnailUrl(null);
		    registeredPatientDetails.setThumbnailUrl(imageURLResponse.getThumbnailUrl());
		}
		registeredPatientDetails.setUserId(userCollection.getId().toString());
		patientCollection = patientRepository.save(patientCollection);
		registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
		registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
		Patient patient = new Patient();
		BeanUtil.map(patientCollection, patient);
		patient.setPatientId(patientCollection.getId().toString());

		Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
		Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
		Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());

		if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			|| (recordsCount != null && recordsCount > 0))
		    patient.setIsDataAvailableWithOtherDoctor(true);

		patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(), patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(), userCollection.getId().toString()));

		registeredPatientDetails.setPatient(patient);
		registeredPatientDetails.setDob(patientCollection.getDob());
		registeredPatientDetails.setGender(patientCollection.getGender());
		registeredPatientDetails.setPID(patientCollection.getPID());
		registeredPatientDetails.setDoctorId(patientCollection.getDoctorId().toString());
		registeredPatientDetails.setLocationId(patientCollection.getLocationId().toString());
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
			for(String groupId : request.getGroups())groupObjectIds.add(new ObjectId(groupId));
			groups = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupObjectIds))), GroupCollection.class, Group.class).getMappedResults();
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
    public List<RegisteredPatientDetails> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId, String hospitalId) {
	List<RegisteredPatientDetails> users = null;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(phoneNumber);
	    if (userCollections != null) {
		users = new ArrayList<RegisteredPatientDetails>();
		for (UserCollection userCollection : userCollections) {
		    if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())) {
		    	RegisteredPatientDetails user = new RegisteredPatientDetails();
			
//			if (locationId != null && hospitalId != null) {
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
				    }else{
				    	Patient patient = new Patient();
				    	BeanUtil.map(patientCollection, patient);
				    	BeanUtil.map(patientCollection, user);
				    	BeanUtil.map(userCollection, user);
				    	patient.setPatientId(patientCollection.getUserId().toString());
				    	user.setPatient(patient);
				    }
				}
			    }
			    if(!isPartOfClinic){
			    	user.setUserId(userCollection.getId().toString());
			    	user.setFirstName(userCollection.getFirstName());
			    	user.setMobileNumber(userCollection.getMobileNumber());
			    }else{
			    	BeanUtil.map(userCollection, user);
			    	user.setUserId(userCollection.getId().toString());
			    }
			    user.setIsPartOfClinic(isPartOfClinic);
//			}
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
	List<Group> groups = null;
	try {
		ObjectId userObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(userId))userObjectId = new ObjectId(userId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    UserCollection userCollection = userRepository.findOne(userObjectId);
	    if (userCollection != null) {
		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userObjectId, doctorObjectId, locationObjectId, hospitalObjectId);
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
		    
		    registeredPatientDetails = new RegisteredPatientDetails();
		    BeanUtil.map(patientCollection, registeredPatientDetails);
		    BeanUtil.map(userCollection, registeredPatientDetails);
		    registeredPatientDetails.setImageUrl(patientCollection.getImageUrl());
		    registeredPatientDetails.setThumbnailUrl(patientCollection.getThumbnailUrl());
		    
		    registeredPatientDetails.setUserId(userCollection.getId().toString());
		    registeredPatientDetails.setReferredBy(reference);
		    Patient patient = new Patient();
		    BeanUtil.map(patientCollection, patient);
		    patient.setPatientId(patientCollection.getId().toString());

		    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
		    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
		    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());

		    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			    || (recordsCount != null && recordsCount > 0))
			patient.setIsDataAvailableWithOtherDoctor(true);

		    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId().toString(), patientCollection.getLocationId().toString(), patientCollection.getHospitalId().toString(), userCollection.getId().toString()));
		    registeredPatientDetails.setPatient(patient);
		    registeredPatientDetails.setAddress(patientCollection.getAddress());
		    @SuppressWarnings("unchecked")
		    Collection<ObjectId> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		    if(groupIds != null && !groupIds.isEmpty()){
		    	groups = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(groupIds))), GroupCollection.class, Group.class).getMappedResults();
			    registeredPatientDetails.setGroups(groups);
		    }
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
			referrencesCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		}else referrencesCollection.setCreatedBy("ADMIN");
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
    public List<ReferenceDetail> getReferences(String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded) {
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
		response = getCustomGlobalReferences(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
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
		AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, "reference", null, null), ReferencesCollection.class, ReferenceDetail.class);
		response = aggregationResults.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<ReferenceDetail> getCustomReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<ReferenceDetail> response = null;
	try {
		AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "reference", null, null, null), ReferencesCollection.class, ReferenceDetail.class);
		response = aggregationResults.getMappedResults();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<ReferenceDetail> getCustomGlobalReferences(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<ReferenceDetail> response = null;
	try {
		AggregationResults<ReferenceDetail> aggregationResults = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, "reference", null, null), ReferencesCollection.class, ReferenceDetail.class);
		response = aggregationResults.getMappedResults();
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
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);
	    
	    ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
	    
	    DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
	    DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
	    Integer patientSize = patientRepository.findTodaysRegisteredPatient(doctorObjectId, locationObjectId, hospitalObjectId, start, end);
	    if(patientCount == null)patientSize = 0;
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorObjectId, locationObjectId);
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

		if(patientCounter <= patientSize)patientCounter =  patientCounter + patientSize;
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
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
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
    public Boolean updatePatientInitialAndCounter(String doctorId, String locationId, String patientInitial, int patientCounter) {
	Boolean response = false;
	try {
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
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
		    response = true;
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

    private Boolean checkIfPatientInitialAndCounterExist(String doctorId, String locationId, String patientInitial, int patientCounter) {
    	Boolean response = false;
	try {
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);

	    String date = DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
		    + DPDoctorUtils.getPrefixedNumber(currentYear % 100);
	    String generatedId = patientInitial + date;

	    Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId)).and("PID").regex(generatedId + ".*");

	    Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
		    Aggregation.limit(1));

	    AggregationResults<PatientCollection> groupResults = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientCollection.class);
	    List<PatientCollection> results = groupResults.getMappedResults();
	    if (results != null && !results.isEmpty()) {
		String PID = results.get(0).getPID();
		PID = PID.substring((patientInitial + date).length());
		if (patientCounter <= Integer.parseInt(PID)) {
			logger.warn("Patient already exist for Prefix: " + patientInitial + " , Date: " + date + " Id Number: " + patientCounter
				    + ". Please enter Id greater than " + PID);
			throw new BusinessException(ServiceError.InvalidInput, "Patient already exist for Prefix: " + patientInitial + " , Date: " + date + " Id Number: " + patientCounter
				    + ". Please enter Id greater than " + PID);
		}
		else response = true;
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
	    locationCollection = locationRepository.findOne(new ObjectId(clinicId));
	    if (locationCollection != null) {
		location = new Location();
		BeanUtil.map(locationCollection, location);
		
		String address = 
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress()) ? locationCollection.getStreetAddress()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails()) ? locationCollection.getLandmarkDetails()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality()) ? locationCollection.getLocality()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCity()) ? locationCollection.getCity()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getState()) ? locationCollection.getState()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry()) ? locationCollection.getCountry()+", ":"")+
    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode()) ? locationCollection.getPostalCode():"");
    	
	    if(address.charAt(address.length() - 2) == ','){
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
	    if (locationCollection != null){
	    	locationName = locationCollection.getLocationName();
	    	BeanUtil.map(request, locationCollection);
	    }
		
	    List<GeocodedLocation> geocodedLocations = locationServices
		    .geocodeLocation((!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress()) ? locationCollection.getStreetAddress()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails()) ? locationCollection.getLandmarkDetails()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality()) ? locationCollection.getLocality()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCity()) ? locationCollection.getCity()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getState()) ? locationCollection.getState()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry()) ? locationCollection.getCountry()+", ":"")+
	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode()) ? locationCollection.getPostalCode():""));

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
		if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))), Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")), Aggregation.skip((page) * size), Aggregation.limit(size));
		else aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("updatedTime").gte(new Date(createdTimeStamp))), Aggregation.sort(new Sort(Sort.Direction.ASC, "profession")));
		AggregationResults<Profession> aggregationResults = mongoTemplate.aggregate(aggregation, ProfessionCollection.class, Profession.class);
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
		    ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
		    locationCollection.setLogoUrl(imageURLResponse.getImageUrl());
		    locationCollection.setLogoThumbnailUrl(imageURLResponse.getThumbnailUrl());
		    locationCollection = locationRepository.save(locationCollection);

		    List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository.findByLocationId(new ObjectId(request.getId()));
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
    public RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request, UriInfo uriInfo) {
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
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), doctorRole.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleCollection = userRoleRepository.save(userRoleCollection);

	    // save user location.
	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), new ObjectId(request.getLocationId()));
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationCollection.setIsActivate(request.getIsActivate());
	    userLocationRepository.save(userLocationCollection);

	    // save token
	    TokenCollection tokenCollection = new TokenCollection();
	    tokenCollection.setResourceId(userLocationCollection.getId());
	    tokenCollection.setCreatedTime(new Date());
	    tokenCollection = tokenRepository.save(tokenCollection);

	    // send activation email
//	    String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getFirstName(), tokenCollection.getId(), "mailTemplate.vm");
//	    mailService.sendEmail(userCollection.getEmailAddress(), signupSubject, body, null);
//
//	    body = mailBodyGenerator.generateForgotPasswordEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
//		    userCollection.getMiddleName(), userCollection.getLastName(), userCollection.getId(), uriInfo);
//	    mailService.sendEmail(userCollection.getEmailAddress(), forgotUsernamePasswordSub, body, null);

	    LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
	    RoleCollection adminRoleCollection = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    String admindoctorName= "";
	    if(adminRoleCollection != null){
	    	List<UserRoleCollection> roleCollections = userRoleRepository.findByRoleId(adminRoleCollection.getId());
	    	UserRoleCollection roleCollection = null;
	    	if(roleCollections != null && !roleCollections.isEmpty()){
	    		roleCollection = roleCollections.get(0);
	    		UserCollection doctorUser = userRepository.findOne(roleCollection.getUserId());
	    		admindoctorName = doctorUser.getTitle()+" "+doctorUser.getFirstName();
	    	}
	    }
	    if(doctorRole.getRole().equals(RoleEnum.DOCTOR) || doctorRole.getRole().equals(RoleEnum.SUPER_ADMIN) || doctorRole.getRole().equals(RoleEnum.HOSPITAL_ADMIN) || doctorRole.getRole().equals(RoleEnum.LOCATION_ADMIN)){
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getTitle()+" "+userCollection.getFirstName(), null, "addDoctorToClinicVerifyTemplate.vm", admindoctorName, locationCollection.getLocationName());
		    mailService.sendEmail(userCollection.getEmailAddress(), addDoctorToClinicVerifySub, body, null);
		}else{
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getTitle()+" "+userCollection.getFirstName(), tokenCollection.getId(), "verifyStaffMemberEmailTemplate.vm", admindoctorName, locationCollection.getLocationName());
		    mailService.sendEmail(userCollection.getEmailAddress(), staffmemberAccountVerifySub, body, null);
		}
	    response = new RegisterDoctorResponse();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, response);
	    response.setHospitalId(request.getHospitalId());
	    response.setLocationId(request.getLocationId());
	    response.setUserId(userCollection.getId().toString());

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
		AccessControl accessControl = accessControlServices.getAccessControls(doctorRole.getId(), doctorRole.getLocationId(), doctorRole.getHospitalId());
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

    @SuppressWarnings("unchecked")
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

	    UserLocationCollection userLocationCollection = new UserLocationCollection(userCollection.getId(), new ObjectId(request.getLocationId()));
	    userLocationCollection.setIsActivate(request.getIsActivate());
	    userLocationCollection.setCreatedTime(new Date());
	    userLocationRepository.save(userLocationCollection);

	    List<RoleCollection> roleCollections = roleRepository.findByLocationIdAndHospitalId(new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
	    List<ObjectId> roleIds = (List<ObjectId>) CollectionUtils.collect(roleCollections, new BeanToPropertyValueTransformer("id"));

	    UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdAndRoleId(userCollection.getId(), roleIds);
	    if (userRoleCollection == null) {
		userRoleCollection = new UserRoleCollection();
		userRoleCollection.setCreatedTime(new Date());
		userRoleCollection.setUserId(userCollection.getId());
		userRoleCollection.setRoleId(new ObjectId(request.getRoleId()));
		userRoleCollection = userRoleRepository.save(userRoleCollection);
	    } else {
		userRoleCollection.setRoleId(new ObjectId(request.getRoleId()));
		userRoleCollection = userRoleRepository.save(userRoleCollection);
	    }
	    response = new RegisterDoctorResponse();
	    userCollection.setPassword(null);
	    BeanUtil.map(userCollection, response);
	    response.setHospitalId(request.getHospitalId());
	    response.setLocationId(request.getLocationId());
	    response.setUserId(userCollection.getId().toString());

	    if (doctorRole != null) {
		Role role = new Role();
		BeanUtil.map(doctorRole, role);
		AccessControl accessControl = accessControlServices.getAccessControls(doctorRole.getId(), doctorRole.getLocationId(), doctorRole.getHospitalId());
		if (accessControl != null)
		    role.setAccessModules(accessControl.getAccessModules());
		response.setRole(role);
		
		LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
	    RoleCollection adminRoleCollection = roleRepository.findByRole(RoleEnum.LOCATION_ADMIN.getRole(), locationCollection.getId(), locationCollection.getHospitalId());
	    String admindoctorName= "";
	    if(adminRoleCollection != null){
	    	List<UserRoleCollection> userRoleCollections = userRoleRepository.findByRoleId(adminRoleCollection.getId());
	    	UserRoleCollection roleCollection = null;
	    	if(userRoleCollections != null && !userRoleCollections.isEmpty()){
	    		roleCollection = userRoleCollections.get(0);
	    		UserCollection doctorUser = userRepository.findOne(roleCollection.getUserId());
	    		admindoctorName = doctorUser.getTitle()+" "+doctorUser.getFirstName();
	    	}
	    }
		if(doctorRole.getRole().equals(RoleEnum.DOCTOR) || doctorRole.getRole().equals(RoleEnum.SUPER_ADMIN) || doctorRole.getRole().equals(RoleEnum.HOSPITAL_ADMIN) || doctorRole.getRole().equals(RoleEnum.LOCATION_ADMIN)){
			String body = mailBodyGenerator.generateActivationEmailBody(userCollection.getTitle()+" "+userCollection.getFirstName(), null, "addExistingDoctorToClinicTemplate.vm", admindoctorName, locationCollection.getLocationName());
		    mailService.sendEmail(userCollection.getEmailAddress(), addExistingDoctorToClinicSub+" "+locationCollection.getLocationName(), body, null);
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
		if (size > 0) roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
		else roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		if (size > 0)roleCollections = roleRepository.findCustomGlobal(new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
		else roleCollections = roleRepository.findCustomGlobal(new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    }
	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(), roleCollection.getHospitalId());
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
	    if (size > 0)roleCollections = roleRepository.findCustom(new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else roleCollections = roleRepository.findCustom(new ObjectId(locationId), new ObjectId(hospitalId), new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(), roleCollection.getHospitalId());
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
	    if (size > 0)roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));

	    if (roleCollections != null) {
		response = new ArrayList<Role>();
		for (RoleCollection roleCollection : roleCollections) {
		    Role role = new Role();
		    AccessControl accessControl = accessControlServices.getAccessControls(roleCollection.getId(), roleCollection.getLocationId(), roleCollection.getHospitalId());
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
	    if (size > 0)userLocationCollections = userLocationRepository.findByLocationId(new ObjectId(locationId), new PageRequest(page, size, Direction.DESC, "createdTime"));
	    else userLocationCollections = userLocationRepository.findByLocationId(new ObjectId(locationId), new Sort(Sort.Direction.DESC, "createdTime"));

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
			clinicDoctorResponse.setUserId(userCollection.getId().toString());
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
	    LocationCollection locationCollection = locationRepository.findOne(new ObjectId(doctorResponse.getLocationId()));
	    if (locationCollection != null) {
		BeanUtil.map(locationCollection, esDoctorDocument);
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
    public void deleteUser(String userId, String locationId, Boolean discarded) {
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(new ObjectId(userId), new ObjectId(locationId));
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
	    locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
	    if (locationCollection != null) {
	    if(request.getIsClinic().equals(false) && request.getIsLab().equals(false)){
	    	logger.error("Location has to be either Clinic or Lab or Both");
		    throw new BusinessException(ServiceError.Unknown, "Location has to be either Clinic or Lab or Both");
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
	    feedbackCollection.setUniqueFeedbackId(UniqueIdInitial.FEEDBACK.getInitial()+DPDoctorUtils.generateRandomId());
	    feedbackCollection.setCreatedTime(new Date());
	    feedbackCollection = feedbackRepository.save(feedbackCollection);
	    if (feedbackCollection != null && (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType())
		    || feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType())
		    || feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType()))) {
		if (feedbackCollection.getType().getType().equals(FeedbackType.PRESCRIPTION.getType()) && request.getResourceId() != null) {
		    PrescriptionCollection prescriptionCollection = prescriptionRepository.findOne(new ObjectId(request.getResourceId()));
		    if (prescriptionCollection != null) {
			prescriptionCollection.setIsFeedbackAvailable(true);
			prescriptionCollection.setUpdatedTime(new Date());
			prescriptionRepository.save(prescriptionCollection);
		    }
		}
		if (feedbackCollection.getType().getType().equals(FeedbackType.APPOINTMENT.getType()) && request.getResourceId() != null) {
		    AppointmentCollection appointmentCollection = appointmentRepository.findOne(new ObjectId(request.getResourceId()));
		    if (appointmentCollection != null) {
			appointmentCollection.setIsFeedbackAvailable(true);
			appointmentCollection.setUpdatedTime(new Date());
			appointmentRepository.save(appointmentCollection);
		    }
		}
		if (feedbackCollection.getType().getType().equals(FeedbackType.REPORT.getType()) && request.getResourceId() != null) {
		    RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(request.getResourceId()));
		    if (recordsCollection != null) {
			recordsCollection.setIsFeedbackAvailable(true);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);
		    }
		}else{
			feedbackCollection.setIsVisible(true);
		}
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(feedbackCollection.getDoctorId(), feedbackCollection.getLocationId());
		if (userLocationCollection != null) {
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		    if (doctorClinicProfileCollection != null) {
			if (feedbackCollection.getIsRecommended())
			    doctorClinicProfileCollection.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() + 1);
			else
			    doctorClinicProfileCollection.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations() - 1);
			doctorClinicProfileRepository.save(doctorClinicProfileCollection);
		    }
		}
		feedbackCollection = feedbackRepository.findOne(feedbackCollection.getId());
		UserCollection patient = userRepository.findOne(feedbackCollection.getUserId());
		UserCollection doctor = userRepository.findOne(feedbackCollection.getDoctorId());
		LocationCollection locationCollection = locationRepository.findOne(feedbackCollection.getLocationId());
		if(patient.getEmailAddress() != null){
			String body = mailBodyGenerator.generateFeedbackEmailBody(patient.getFirstName(), doctor.getTitle()+" "+doctor.getFirstName(), locationCollection.getLocationName(), feedbackCollection.getUniqueFeedbackId(), "feedbackUserToDoctorTemplate.vm");
			mailService.sendEmail(patient.getEmailAddress(), addFeedbackForDoctorSubject, body, null);
		}
	    }else{
	    	if(feedbackCollection.getEmailAddress() != null){
	    		String body = mailBodyGenerator.generateFeedbackEmailBody(feedbackCollection.getCreatedBy(), null, null, null, "feedbackTemplate.vm");
				mailService.sendEmail(feedbackCollection.getEmailAddress(), addFeedbackSubject, body, null);
	    	}
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
	    locationCollection = locationRepository.findOne(new ObjectId(request.getId()));
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
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorObjectId, patientObjectId, hospitalObjectId, locationObjectId);
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorObjectId, patientObjectId, hospitalObjectId, locationObjectId);
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorObjectId, patientObjectId, hospitalObjectId, locationObjectId);

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
	    FeedbackCollection feedbackCollection = feedbackRepository.findOne(new ObjectId(feedbackId));
	    if (feedbackCollection != null) {
		feedbackCollection.setUpdatedTime(new Date());
		feedbackCollection.setIsVisible(isVisible);
		feedbackCollection = feedbackRepository.save(feedbackCollection);
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(feedbackCollection.getDoctorId(), feedbackCollection.getLocationId());
		if (userLocationCollection != null) {
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		    if (doctorClinicProfileCollection != null) {
			if (isVisible)doctorClinicProfileCollection.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() + 1);
			else doctorClinicProfileCollection.setNoOfReviews(doctorClinicProfileCollection.getNoOfReviews() - 1);
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
		ObjectId doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    List<FeedbackCollection> feedbackCollections = null;
	    if (DPDoctorUtils.anyStringEmpty(doctorObjectId)){
	    	//THis is for ADMIN so isVisible = false
	    	if(DPDoctorUtils.anyStringEmpty(type)){
	    		if (size > 0)feedbackCollections = feedbackRepository.find(new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else feedbackCollections = feedbackRepository.find(new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
	    	}else{
	    		if (size > 0)feedbackCollections = feedbackRepository.findByType(type, new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else feedbackCollections = feedbackRepository.findByType(type, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
	    	}
	    }
	    else {
		if (DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
		    if (size > 0)feedbackCollections = feedbackRepository.find(doctorObjectId, true, new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
		    else feedbackCollections = feedbackRepository.find(doctorObjectId, true, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
		} else {
		    if (size > 0)
			feedbackCollections = feedbackRepository.find(doctorObjectId, locationObjectId, hospitalObjectId, true, new Date(createdTimeStamp), new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
		    else feedbackCollections = feedbackRepository.find(doctorObjectId, locationObjectId, hospitalObjectId, true, new Date(createdTimeStamp), new Sort(Direction.DESC, "createdTime"));
		}
	    }
	    if (feedbackCollections != null) {
		response = new ArrayList<Feedback>();
		for(FeedbackCollection feedbackCollection : feedbackCollections){
			Feedback feedback = new Feedback();
			BeanUtil.map(feedbackCollection, feedback);
			UserCollection userCollection = userRepository.findOne(new ObjectId(feedback.getUserId()));
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(feedbackCollection.getUserId(), feedbackCollection.getDoctorId(), feedbackCollection.getLocationId(), feedbackCollection.getHospitalId());
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
