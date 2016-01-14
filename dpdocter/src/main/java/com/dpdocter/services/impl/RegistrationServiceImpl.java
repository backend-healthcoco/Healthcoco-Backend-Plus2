package com.dpdocter.services.impl;

import java.io.File;
import java.text.SimpleDateFormat;
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
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.BloodGroupCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorContactCollection;
import com.dpdocter.collections.FeedbackCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProfessionCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.TokenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.ColorCode;
import com.dpdocter.enums.ColorCode.RandomEnum;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.Type;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.BloodGroupRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FeedbackRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
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
import com.dpdocter.response.RegisterDoctorResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.GenerateUniqueUserNameService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.sms.services.SMSServices;
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

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private PatientGroupRepository patientGroupRepository;

    @Autowired
    private DoctorContactsRepository doctorContactsRepository;

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
    private BloodGroupRepository bloodGroupRepository;

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
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private OTPService otpService;

    @Value(value = "${mail.signup.subject.activation}")
    private String signupSubject;

    @Value(value = "${mail.forgotPassword.subject}")
    private String forgotUsernamePasswordSub;

    @Value(value = "${PATIENT_COUNT}")
    private String patientCount;

    @Override
    public User checkIfPatientExist(PatientRegistrationRequest request) {
	try {
	    UserCollection userCollection = userRepository.checkPatient(request.getFirstName(), request.getMiddleName(), request.getLastName(),
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
    public RegisteredPatientDetails registerNewPatient(PatientRegistrationRequest request) {
	RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {

	    // get role of specified type
	    RoleCollection roleCollection = roleRepository.findByRole(RoleEnum.PATIENT.getRole());
	    if (roleCollection == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    Date createdTime = new Date();
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    if (request.getDob() != null && request.getDob().getAge() < 0) {
		logger.warn("Incorrect Date of Birth");
		throw new BusinessException(ServiceError.NotAcceptable, "Incorrect Date of Birth");
	    }
	    User user = new User();
	    BeanUtil.map(request, user);
	    String uniqueUserName = generateUniqueUserNameService.generate(user);
	    userCollection.setUserName(uniqueUserName);
	    userCollection.setPassword(generateRandomAlphanumericString(10));
	    if (request.getImage() != null) {
		String path = "profile-images";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		String imageUrl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		userCollection.setImageUrl(imageUrl);
	    }
	    userCollection.setCreatedTime(createdTime);
	    userCollection.setColorCode(new RandomEnum<ColorCode>(ColorCode.class).random().getColor());
	    userCollection = userRepository.save(userCollection);

	    // assign roles
	    UserRoleCollection userRoleCollection = new UserRoleCollection(userCollection.getId(), roleCollection.getId());
	    userRoleCollection.setCreatedTime(new Date());
	    userRoleRepository.save(userRoleCollection);

	    // save address
	    AddressCollection addressCollection = null;
	    if (request.getAddress() != null) {
		addressCollection = new AddressCollection();
		BeanUtil.map(request.getAddress(), addressCollection);
		addressCollection.setUserId(userCollection.getId());
		addressCollection.setCreatedTime(new Date());
		addressCollection = addressRepository.save(addressCollection);
	    }
	    // save Patient Info
	    PatientCollection patientCollection = new PatientCollection();
	    BeanUtil.map(request, patientCollection);
	    patientCollection.setUserId(userCollection.getId());
	    patientCollection.setRegistrationDate(request.getDateOfVisit());

	    patientCollection.setCreatedTime(createdTime);
	    patientCollection.setPID(patientIdGenerator(request.getDoctorId(), request.getLocationId(), request.getHospitalId()));
	    
	    if (addressCollection != null) {
		patientCollection.setAddressId(addressCollection.getId());
	    }
	    
	    if(!DPDoctorUtils.anyStringEmpty(request.getProfession())){
	    	ProfessionCollection professionCollection = professionRepository.findOne(request.getProfession());
	    	if(professionCollection != null)patientCollection.setProfession(professionCollection.getProfession());
	    }
	    patientCollection.setNotes(request.getNotes());
	    patientCollection = patientRepository.save(patientCollection);

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
	    }

	    PatientAdmissionCollection patientAdmissionCollection = new PatientAdmissionCollection();
	    BeanUtil.map(request, patientAdmissionCollection);
	    patientAdmissionCollection.setUserId(userCollection.getId());
	    patientAdmissionCollection.setPatientId(patientCollection.getUserId());
	    patientAdmissionCollection.setDoctorId(request.getDoctorId());
	    patientAdmissionCollection.setCreatedTime(new Date());

	    if (referencesCollection != null)
		patientAdmissionCollection.setReferredBy(referencesCollection.getId());
	    patientAdmissionCollection = patientAdmissionRepository.save(patientAdmissionCollection);

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
	    // add into doctor contact
	    if (request.getDoctorId() != null) {
		DoctorContactCollection doctorContactCollection = new DoctorContactCollection();
		doctorContactCollection.setCreatedTime(createdTime);
		doctorContactCollection.setDoctorId(request.getDoctorId());
		doctorContactCollection.setContactId(patientCollection.getUserId());
		doctorContactsRepository.save(doctorContactCollection);
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
	    // TODO

	    BeanUtil.map(userCollection, registeredPatientDetails);
	    registeredPatientDetails.setUserId(userCollection.getId());
	    Patient patient = new Patient();
	    BeanUtil.map(patientCollection, patient);
	    patient.setPatientId(userCollection.getId());

	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    
	    if (prescriptionCount != null || clinicalNotesCount != null || recordsCount != null)patient.setIsDataAvailableWithOtherDoctor(true);
	    
	    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(), patientCollection.getHospitalId(), userCollection.getId()));

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
	    Address address = new Address();
	    if (addressCollection != null) {
		BeanUtil.map(addressCollection, address);
		registeredPatientDetails.setAddress(address);
	    }
	    if (request.getBloodGroup() != null) {
		groupCollections = (List<GroupCollection>) groupRepository.findAll(request.getGroups());
		groups = new ArrayList<Group>();
		BeanUtil.map(groupCollections, groups);
	    }
	    /* registeredPatientDetails.setGroups(request.getGroups()); */
	    registeredPatientDetails.setGroups(groups);

	    if (userCollection.getMobileNumber() != null) {
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(patientCollection.getDoctorId());
		smsTrackDetail.setHospitalId(patientCollection.getHospitalId());
		smsTrackDetail.setLocationId(patientCollection.getLocationId());

		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setPatientId(patientCollection.getUserId());

		SMS sms = new SMS();
		sms.setSmsText("OTP Verification");

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(userCollection.getMobileNumber());
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
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
    public RegisteredPatientDetails registerExistingPatient(PatientRegistrationRequest request) {
	RegisteredPatientDetails registeredPatientDetails = new RegisteredPatientDetails();
	PatientCollection patientCollection = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {

	    // save address
	    AddressCollection addressCollection = null;
	    if (request.getAddress() != null) {
		addressCollection = new AddressCollection();
		BeanUtil.map(request.getAddress(), addressCollection);
		addressCollection.setUserId(request.getUserId());
		addressCollection.setCreatedTime(new Date());
		addressCollection = addressRepository.save(addressCollection);
	    }
	    // save Patient Info
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
	    }
	    if (addressCollection != null) {
		patientCollection.setAddressId(addressCollection.getId());
	    }
	    patientCollection.setRelations(request.getRelations());
	    patientCollection.setNotes(request.getNotes());
	    if (!DPDoctorUtils.anyStringEmpty(request.getPatientNumber())) {
		patientCollection.setPID(request.getPatientNumber());
	    } 
	    else if (!DPDoctorUtils.anyStringEmpty(patientCollection.getPID())) {
		patientCollection.setPID(request.getPatientNumber());
	    } else {
		patientCollection.setPID(patientIdGenerator(request.getDoctorId(), request.getLocationId(), request.getHospitalId()));
	    }
	    if(!DPDoctorUtils.anyStringEmpty(request.getProfession())){
	    	ProfessionCollection professionCollection = professionRepository.findOne(request.getProfession());
	    	if(professionCollection != null)patientCollection.setProfession(professionCollection.getProfession());
	    }
	    patientCollection.setRegistrationDate(request.getDateOfVisit());
	    patientCollection = patientRepository.save(patientCollection);

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
	    }
	    // save patient admission
	    PatientAdmissionCollection patientAdmissionCollection = null;
	    patientAdmissionCollection = patientAdmissionRepository.findByPatientIdAndDoctorId(patientCollection.getUserId(), request.getDoctorId());
	    if (patientAdmissionCollection == null) {
		patientAdmissionCollection = new PatientAdmissionCollection();
		BeanUtil.map(request, patientAdmissionCollection);
		patientAdmissionCollection.setUserId(request.getUserId());
		patientAdmissionCollection.setPatientId(patientCollection.getUserId());
		patientAdmissionCollection.setCreatedTime(new Date());
	    }

	    if (referencesCollection != null)
		patientAdmissionCollection.setReferredBy(referencesCollection.getId());
	    patientAdmissionCollection = patientAdmissionRepository.save(patientAdmissionCollection);

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
	    // add into doctor contact
	    if (request.getDoctorId() != null) {
		DoctorContactCollection doctorContactCollection = null;
		doctorContactCollection = doctorContactsRepository.findByDoctorIdAndContactId(request.getDoctorId(), patientCollection.getUserId());
		if (doctorContactCollection == null) {
		    doctorContactCollection = new DoctorContactCollection();
		    doctorContactCollection.setCreatedTime(new Date());
		    doctorContactCollection.setDoctorId(request.getDoctorId());
		    doctorContactCollection.setContactId(patientCollection.getUserId());
		    doctorContactsRepository.save(doctorContactCollection);
		}

	    }
	    UserCollection userCollection = userRepository.findOne(request.getUserId());
	    if (userCollection == null) {
		logger.error("Incorrect User Id");
		throw new BusinessException(ServiceError.Unknown, "Incorrect User Id");
	    }
	    /*registeredPatientDetails = new RegisteredPatientDetails();*/
	    BeanUtil.map(userCollection, registeredPatientDetails);
	    if (request.getImage() != null) {
		String path = "profile-images";
		// save image
		request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
		String imageUrl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
		userCollection.setImageUrl(imageUrl);
		registeredPatientDetails.setImageUrl(imageUrl);
		userCollection = userRepository.save(userCollection);
	    }
	    registeredPatientDetails.setUserId(userCollection.getId());
	    Patient patient = new Patient();
	    BeanUtil.map(patientCollection, patient);
	    patient.setPatientId(userCollection.getId());

	    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(patientCollection.getDoctorId(), userCollection.getId(), patientCollection.getHospitalId(), patientCollection.getLocationId());
	    
	    if (prescriptionCount != null || clinicalNotesCount != null || recordsCount != null)patient.setIsDataAvailableWithOtherDoctor(true);
	    
	    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(), patientCollection.getHospitalId(), userCollection.getId()));


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
	    Address address = new Address();
	    if (addressCollection != null) {
		BeanUtil.map(addressCollection, address);
		registeredPatientDetails.setAddress(address);
	    }
	    groupCollections = (List<GroupCollection>) groupRepository.findAll(request.getGroups());
	    groups = new ArrayList<Group>();
	    BeanUtil.map(groupCollections, groups);
	    registeredPatientDetails.setGroups(groups);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return registeredPatientDetails;
    }

    @Override
    public void checkPatientCount(String mobileNumber) {

    int count = 0;	
	List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
	if (userCollections != null && !userCollections.isEmpty()) {
		for(UserCollection userCollection : userCollections){
			if(!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
				count++;
		}
	    if (count >= Integer.parseInt(patientCount)) {
		logger.warn("Only Nine patients can register with same mobile number");
		throw new BusinessException(ServiceError.NoRecord, "Only Nine patients can register with same mobile number");
	    }
	}
    }

    @Override
    public List<User> getUsersByPhoneNumber(String phoneNumber, String doctorId, String locationId, String hospitalId) {
	List<User> users = null;
	try {
	    List<UserCollection> userCollections = userRepository.findByMobileNumber(phoneNumber);
	    if (userCollections != null) {
		users = new ArrayList<User>();
		for (UserCollection userCollection : userCollections) {
			if(!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress())){
			    User user = new User();
			    BeanUtil.map(userCollection, user);
			    if (locationId != null && hospitalId != null) {
				List<PatientCollection> patientCollections = patientRepository.findByUserId(userCollection.getId());
				boolean isPartOfClinic = false;
				if (patientCollections != null) {
				    for(PatientCollection patientCollection : patientCollections){
				    	if (patientCollection.getDoctorId() != null && patientCollection.getLocationId() != null && patientCollection.getHospitalId() != null) {
							if (patientCollection.getDoctorId().equals(doctorId) && patientCollection.getLocationId().equals(locationId) && patientCollection.getHospitalId().equals(hospitalId)) {
							    isPartOfClinic = true;
							    break;
							} 
				    	}
				    }
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

    private String generateRandomAlphanumericString(int count) {
	return RandomStringUtils.randomAlphabetic(count);
    }

    @Override
    public RegisteredPatientDetails getPatientProfileByUserId(String userId, String doctorId, String locationId, String hospitalId) {
	RegisteredPatientDetails registeredPatientDetails = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {
	    UserCollection userCollection = userRepository.findOne(userId);
	    if (userCollection != null) {
		PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(userId, doctorId, locationId, hospitalId);
		if (patientCollection != null) {
			PatientAdmissionCollection patientAdmissionCollection = patientAdmissionRepository.findByPatientIdAndDoctorId(userCollection.getId(), doctorId);
			Reference reference = null;
			if(patientAdmissionCollection != null){
				if(patientAdmissionCollection.getReferredBy() != null){
					ReferencesCollection referencesCollection = referrenceRepository.findOne(patientAdmissionCollection.getReferredBy());
					if(referencesCollection != null){
						reference = new Reference();
						BeanUtil.map(referencesCollection, reference);
					}
				}
			}
		    AddressCollection addressCollection = new AddressCollection();
		    if (patientCollection.getAddressId() != null) {
			addressCollection = addressRepository.findOne(patientCollection.getAddressId());
		    }
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    @SuppressWarnings("unchecked")
		    Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		    registeredPatientDetails = new RegisteredPatientDetails();
		    BeanUtil.map(patientCollection, registeredPatientDetails);
		    BeanUtil.map(userCollection, registeredPatientDetails);
		    registeredPatientDetails.setUserId(userCollection.getId());
		    registeredPatientDetails.setReferredBy(reference);
		    Patient patient = new Patient();
		    BeanUtil.map(patientCollection, patient);
		    patient.setPatientId(userCollection.getId());

		    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);
		    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);
		    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);
		    
		    if (prescriptionCount != null || clinicalNotesCount != null || recordsCount != null)patient.setIsDataAvailableWithOtherDoctor(true);

		    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(patientCollection.getDoctorId(), patientCollection.getLocationId(), patientCollection.getHospitalId(), userCollection.getId()));
		    registeredPatientDetails.setPatient(patient);
		    Address address = new Address();
		    BeanUtil.map(addressCollection, address);
		    registeredPatientDetails.setAddress(address);
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
    public Reference addEditReference(Reference reference) {
	try {
	    ReferencesCollection referrencesCollection = new ReferencesCollection();
	    BeanUtil.map(reference, referrencesCollection);
	    if (referrencesCollection.getId() == null) {
		referrencesCollection.setCreatedTime(new Date());
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
    public void deleteReferrence(String referenceId, Boolean discarded) {
	try {
	    ReferencesCollection referrencesCollection = referrenceRepository.findOne(referenceId);
	    if (referrencesCollection != null) {
		referrencesCollection.setDiscarded(discarded);
		referrencesCollection.setUpdatedTime(new Date());
		referrenceRepository.save(referrencesCollection);
	    } else {
		logger.warn("Invalid Referrence Id!");
		throw new BusinessException(ServiceError.Unknown, "Invalid Referrence Id!");
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

    }

    @Override
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
		referrencesCollections = referrenceRepository.findAll(new Date(createdTimeStamp), discards, new PageRequest(page, size, Direction.DESC,
			"updatedTime"));
	    else
		referrencesCollections = referrenceRepository.findAll(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
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
			referrencesCollections = referrenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page, size,
				Direction.DESC, "updatedTime"));
		    else
			referrencesCollections = referrenceRepository.findCustom(doctorId, new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
				"updatedTime"));
		} else {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			referrencesCollections = referrenceRepository.findCustom(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
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
		    referrencesCollections = referrenceRepository.findCustomGlobal(new Date(createdTimeStamp), discards, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    referrencesCollections = referrenceRepository.findCustomGlobal(new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC,
			    "updatedTime"));
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discards, new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (size > 0)
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			referrencesCollections = referrenceRepository.findCustomGlobal(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
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
    public String patientIdGenerator(String doctorId, String locationId, String hospitalId) {
	String generatedId = null;
	try {
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);

//	    String startDate = currentDay + "-" + currentMonth + "-" + currentYear + " 00:00:00";
//	    String endDate = currentDay + "-" + currentMonth + "-" + currentYear + " 23:59:59";
//	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//	    Long from = dateFormat.parse(startDate).getTime();
//	    Long to = dateFormat.parse(endDate).getTime();
//	    
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
		    clinicProfileCollection.setLocationId(userLocation.getId());
		    doctorClinicProfileRepository.save(clinicProfileCollection);
		}
		String patientInitial = clinicProfileCollection.getPatientInitial();
		int patientCounter = clinicProfileCollection.getPatientCounter();

		generatedId = patientInitial + DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
			+ DPDoctorUtils.getPrefixedNumber(currentYear % 100) + DPDoctorUtils.getPrefixedNumber(patientCounter + patientCount + 1);
		System.out.println(patientCount+ ""+ generatedId);
//		updatePatientInitialAndCounter(doctorId, locationId, patientInitial, patientCounter + 1);
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
    public PatientInitialAndCounter getPatientInitialAndCounter(String doctorId, String locationId) {
	PatientInitialAndCounter patientInitialAndCounter = null;
	try {
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocation != null) {
		DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocation.getLocationId());
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
    public String updatePatientInitialAndCounter(String doctorId, String locationId, String patientInitial, int patientCounter) {
    	String response = null;
	try {
	    UserLocationCollection userLocation = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    if (userLocation != null) {
	    	
	    response = checkIfPatientInitialAndCounterExist(doctorId, locationId, patientInitial, patientCounter);	
	    if(response == null){
	    	DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocation.getId());
			if (clinicProfileCollection == null) clinicProfileCollection = new DoctorClinicProfileCollection();
			clinicProfileCollection.setLocationId(userLocation.getId());
			clinicProfileCollection.setPatientInitial(patientInitial);
			clinicProfileCollection.setPatientCounter(patientCounter);
			clinicProfileCollection = doctorClinicProfileRepository.save(clinicProfileCollection);
			response = "true";
		    } 
	    }else {
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
		String generatedId = patientInitial +  date ;


		Criteria criteria = new Criteria("doctorId").is(doctorId).and("locationId").is(locationId).and("PID").regex(generatedId+".*");
			
		Aggregation	aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), 
					Aggregation.limit(1));

		AggregationResults<PatientCollection> groupResults = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientCollection.class);
	    List<PatientCollection> results = groupResults.getMappedResults();
	    String PID = results.get(0).getPID();
	    PID = PID.substring((patientInitial+date).length());
		if(patientCounter <= Integer.parseInt(PID)){
			response = "Patient already exist for Prefix: "+patientInitial+ " , Date: "+ date+ " Id Number: "+patientCounter+". Please enter Id greater than "+PID;
		}
		else{
			
		}
	    }
    	catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
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
    public ClinicProfile updateClinicProfile(ClinicProfile request) {
	ClinicProfile response = null;
	LocationCollection locationCollection = null;
	try {
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null)
		BeanUtil.map(request, locationCollection);
	    BeanUtil.map(request, locationCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
	    		(locationCollection.getLocationName() != null ? locationCollection.getLocationName() : "" )+
	    		(locationCollection.getStreetAddress()!= null ? locationCollection.getStreetAddress() : "" )+
	    		(locationCollection.getCity()!= null ? locationCollection.getCity() : "")+
	    		(locationCollection.getState() != null ? locationCollection.getState() : "")+
	    		(locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));
	    
	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);

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
    public ClinicAddress updateClinicAddress(ClinicAddress request) {
	ClinicAddress response = null;
	LocationCollection locationCollection = null;
	try {
	    locationCollection = locationRepository.findOne(request.getId());
	    if (locationCollection != null)
		BeanUtil.map(request, locationCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(
	    		(locationCollection.getLocationName() != null ? locationCollection.getLocationName() : "" )+
	    		(locationCollection.getStreetAddress()!= null ? locationCollection.getStreetAddress() : "" )+
	    		(locationCollection.getCity()!= null ? locationCollection.getCity() : "")+
	    		(locationCollection.getState() != null ? locationCollection.getState() : "")+
	    		(locationCollection.getCountry() != null ? locationCollection.getCountry() : ""));
	    
	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), locationCollection);
	    
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
    public BloodGroup addBloodGroup(BloodGroup request) {
	BloodGroup bloodGroup = new BloodGroup();
	try {
	    BloodGroupCollection bloodGroupCollection = new BloodGroupCollection();
	    BeanUtil.map(request, bloodGroupCollection);
	    bloodGroupCollection = bloodGroupRepository.save(bloodGroupCollection);
	    BeanUtil.map(bloodGroupCollection, bloodGroup);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return bloodGroup;
    }

    @Override
    public List<BloodGroup> getBloodGroup() {

	List<BloodGroup> bloodGroups = null;
	try {
	    List<BloodGroupCollection> bloodGroupCollections = bloodGroupRepository.findAll();
	    if (bloodGroupCollections != null) {
		bloodGroups = new ArrayList<BloodGroup>();
		BeanUtil.map(bloodGroupCollections, bloodGroups);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return bloodGroups;
    }

    @Override
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
    public List<Profession> getProfession(int page, int size) {
	List<Profession> professions = null;
	List<ProfessionCollection> professionCollections = null;
	try {
	    if (size > 0)
		professionCollections = professionRepository.findAll(new PageRequest(page, size)).getContent();
	    else
		professionCollections = professionRepository.findAll();
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
    public RegisterDoctorResponse registerNewUser(DoctorRegisterRequest request, UriInfo uriInfo) {
	RegisterDoctorResponse response = null;
	try {
	    RoleCollection doctorRole = null;
	    if (request.getRoleId() != null) {
		doctorRole = roleRepository.findOne(request.getRoleId());
	    }

	    if (doctorRole == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
	    }
	    // save user
	    UserCollection userCollection = new UserCollection();
	    BeanUtil.map(request, userCollection);
	    // if (request.getDob() != null && request.getDob().getAge() < 0) {
	    // logger.warn("Incorrect Date of Birth");
	    // throw new BusinessException(ServiceError.NotAcceptable,
	    // "Incorrect Date of Birth");
	    // }
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
    public RegisterDoctorResponse registerExisitingUser(DoctorRegisterRequest request) {
	RegisterDoctorResponse response = null;
	try {

	    RoleCollection doctorRole = null;
	    if (request.getRoleId() != null) {
		doctorRole = roleRepository.findOne(request.getRoleId());
	    }

	    if (doctorRole == null) {
		logger.warn("Role Collection in database is either empty or not defind properly");
		throw new BusinessException(ServiceError.NoRecord, "Role Collection in database is either empty or not defind properly");
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
		    roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		else
		    roleCollections = roleRepository.findCustomGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		if (size > 0)
		    roleCollections = roleRepository.findCustomGlobal(locationId, hospitalId, new Date(createdTimeStamp), new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		else
		    roleCollections = roleRepository.findCustomGlobal(locationId, hospitalId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
			    "updatedTime"));
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
		roleCollections = roleRepository.findCustom(locationId, hospitalId, new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC,
			"updatedTime"));
	    else
		roleCollections = roleRepository.findCustom(locationId, hospitalId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));

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
		roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
	    else
		roleCollections = roleRepository.findGlobal(new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));

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
    public List<ClinicDoctorResponse> getDoctors(int page, int size, String locationId, String hospitalId, String updatedTime) {
	List<ClinicDoctorResponse> response = null;
	try {
	    List<UserLocationCollection> userLocationCollections = null;
	    if (size > 0)
		userLocationCollections = userLocationRepository.findByLocationId(locationId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
	    else
		userLocationCollections = userLocationRepository.findByLocationId(locationId, new Sort(Sort.Direction.DESC, "updatedTime"));

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
	public void deleteRole(String roleId, Boolean discarded) {
		try {
		    RoleCollection roleCollection = roleRepository.findOne(roleId);
		    if (roleCollection != null) {
		    	roleCollection.setDiscarded(discarded);
		    	roleCollection = roleRepository.save(roleCollection);
		    }
		    } catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
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
	public ClinicLabProperties updateLabProperties(ClinicLabProperties request) {
		ClinicLabProperties response = null;
		LocationCollection locationCollection = null;
		try {
		    locationCollection = locationRepository.findOne(request.getId());
		    if (locationCollection != null){
		    	locationCollection.setIsLab(request.getIsLab());
		    	if(request.getIsLab()){
		    		locationCollection.setIsHomeServiceAvailable(request.getIsHomeServiceAvailable());
		    		locationCollection.setIsNABLAccredited(request.getIsNABLAccredited());
		    		locationCollection.setIsNABLAccredited(request.getIsNABLAccredited());
		    	}
		    	else{
		    		locationCollection.setIsHomeServiceAvailable(false);
		    		locationCollection.setIsNABLAccredited(false);
		    		locationCollection.setIsNABLAccredited(false);
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
	public Feedback addFeedback(Feedback request) {
		Feedback response = new Feedback();
		try {
			FeedbackCollection feedbackCollection = new FeedbackCollection();
		    BeanUtil.map(request, feedbackCollection);
		    feedbackCollection.setCreatedTime(new Date());
		    feedbackCollection = feedbackRepository.save(feedbackCollection);
		    BeanUtil.map(feedbackCollection, response);
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, "Error while adding feedback");
		}
		return response;
	}

	@Override
	public ClinicProfile updateClinicProfileHandheld(ClinicProfileHandheld request) {
		ClinicProfile response = null;
		LocationCollection locationCollection = null;
		try {
		    locationCollection = locationRepository.findOne(request.getId());
		    if (locationCollection != null)
			BeanUtil.map(request, locationCollection);
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
}
