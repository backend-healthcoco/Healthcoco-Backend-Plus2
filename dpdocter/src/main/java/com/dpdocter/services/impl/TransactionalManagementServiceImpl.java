package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.OTPState;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.OTPRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.AppointmentDoctorReminderResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.DoctorLocation;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrComplaintsDocument;
import com.dpdocter.solr.document.SolrDiagnosesDocument;
import com.dpdocter.solr.document.SolrDiagramsDocument;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrDrugDocument;
import com.dpdocter.solr.document.SolrInvestigationsDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.document.SolrNotesDocument;
import com.dpdocter.solr.document.SolrObservationsDocument;
import com.dpdocter.solr.document.SolrPatientDocument;
import com.dpdocter.solr.services.SolrCityService;
import com.dpdocter.solr.services.SolrClinicalNotesService;
import com.dpdocter.solr.services.SolrPrescriptionService;
import com.dpdocter.solr.services.SolrRegistrationService;

@Service
public class TransactionalManagementServiceImpl implements TransactionalManagementService {

    private static Logger logger = Logger.getLogger(TransactionalManagementServiceImpl.class.getName());

    @Autowired
    private TransnationalRepositiory transnationalRepositiory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private SolrRegistrationService solrRegistrationService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private SolrPrescriptionService solrPrescriptionService;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private ObservationRepository observationRepository;

    @Autowired
    private InvestigationRepository investigationRepository;

    @Autowired
    private DiagnosisRepository diagnosisRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private DiagramsRepository diagramsRepository;

    @Autowired
    private SolrCityService solrCityService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private LandmarkLocalityRepository landmarkLocalityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserLocationRepository userLocationRepository;

    @Autowired
    private DoctorClinicProfileRepository doctorClinicProfileRepository;

    @Autowired
    private SolrClinicalNotesService solrClinicalNotesService;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SMSServices sMSServices;

    @Scheduled(fixedDelay = 1800000)
    @Override
    @Transactional
    public void checkResources() {
	System.out.println(">>> Scheduled test service <<<");
	List<TransactionalCollection> transactionalCollections = null;
	try {
	    transactionalCollections = transnationalRepositiory.findByIsCached(false);
	    if (transactionalCollections != null) {
		for (TransactionalCollection transactionalCollection : transactionalCollections) {
		    if (transactionalCollection.getResourceId() != null)
			switch (transactionalCollection.getResource()) {

			case PATIENT:
			    checkPatient(transactionalCollection.getResourceId());
			    break;
			case DRUG:
			    checkDrug(transactionalCollection.getResourceId());
			    break;
			case LABTEST:
			    checkLabTest(transactionalCollection.getResourceId());
			    break;
			case COMPLAINT:
			    checkComplaint(transactionalCollection.getResourceId());
			    break;
			case DIAGNOSIS:
			    checkDiagnosis(transactionalCollection.getResourceId());
			    break;
			case DIAGRAM:
			    checkDiagrams(transactionalCollection.getResourceId());
			    break;
			case INVESTIGATION:
			    checkInvestigation(transactionalCollection.getResourceId());
			    break;
			case NOTES:
			    checkNotes(transactionalCollection.getResourceId());
			    break;
			case OBSERVATION:
			    checkObservation(transactionalCollection.getResourceId());
			    break;
			case CITY:
			    checkCity(transactionalCollection.getResourceId());
			    break;
			case LANDMARKLOCALITY:
			    checkLandmarkLocality(transactionalCollection.getResourceId());
			    break;
			case DOCTOR:
			    checkDoctor(transactionalCollection.getResourceId(), null);
			    break;
			case LOCATION:
			    checkLocation(transactionalCollection.getResourceId());
			    break;

			default:
			    break;
			}
		}
	    }
	    //Expire invalid otp
	    checkOTP();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

  //Appointment Reminder to Doctor, if appointment > 0
    @Scheduled(cron = "0 0 7 * * *")
    @Override
    @Transactional
    public void sendReminderToDoctor(){
    	try{
    		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        	
    	    localCalendar.setTime(new Date());
    		int currentDayFromTime = localCalendar.get(Calendar.DATE);
    		int currentMonthFromTime = localCalendar.get(Calendar.MONTH) + 1;
    		int currentYearFromTime = localCalendar.get(Calendar.YEAR);
    		DateTime fromTime = new DateTime(currentYearFromTime, currentMonthFromTime, currentDayFromTime, 0, 0, 0);
    		    
    	    localCalendar.setTime(new Date());
    		int currentDay = localCalendar.get(Calendar.DATE);
    		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
    		int currentYear = localCalendar.get(Calendar.YEAR);
    		DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
    		
    		Aggregation aggregation = Aggregation.newAggregation(
    				Aggregation.match(new Criteria("state").is(AppointmentState.CONFIRM.getState()).and("type").is(AppointmentType.APPOINTMENT.getType()).and("fromDate").gte(fromTime).and("toDate").lte(toTime)),
    				Aggregation.group("doctorId").count().as("total"), Aggregation.project("total").and("doctorId").previousOperation());
    		AggregationResults<AppointmentDoctorReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation, AppointmentCollection.class,
    				AppointmentDoctorReminderResponse.class);

    		List<AppointmentDoctorReminderResponse> appointmentDoctorReminderResponses = aggregationResults.getMappedResults();

    		if(appointmentDoctorReminderResponses != null && !appointmentDoctorReminderResponses.isEmpty())
    		for(AppointmentDoctorReminderResponse appointmentDoctorReminderResponse : appointmentDoctorReminderResponses){
    			if(appointmentDoctorReminderResponse.getTotal() > 0){
    				UserCollection userCollection = userRepository.findOne(appointmentDoctorReminderResponse.getDoctorId());
        			if(userCollection != null){
        				
        				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
        				smsTrackDetail.setDoctorId(userCollection.getId());
        			    smsTrackDetail.setType("APPOINTMENT");
        			    SMSDetail smsDetail = new SMSDetail();
        			    smsDetail.setUserId(userCollection.getId());
        			    SMS sms = new SMS();
        			    smsDetail.setUserName(userCollection.getFirstName());
        			    sms.setSmsText("Healthcoco! You have "+appointmentDoctorReminderResponse.getTotal()+" appointments scheduled today. Have a Healthy and Happy day!!");

        			    SMSAddress smsAddress = new SMSAddress();
        			    smsAddress.setRecipient(userCollection.getMobileNumber());
        			    sms.setSmsAddress(smsAddress);

        			    smsDetail.setSms(sms);
        			    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
        			    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
        			    smsDetails.add(smsDetail);
        			    smsTrackDetail.setSmsDetails(smsDetails);
        			    sMSServices.sendSMS(smsTrackDetail, true);
        			}
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	    logger.error(e);
    	}
    }
    public void checkOTP() {
	try {
	    List<OTPCollection> otpCollections = otpRepository.findNonExpiredOtp(OTPState.EXPIRED.getState());
	    if (otpCollections != null) {
		for (OTPCollection otpCollection : otpCollections) {
		    if (otpCollection.getState().equals(OTPState.VERIFIED)) {
			if (!otpService.isOTPValid(otpCollection.getCreatedTime())) {
			    otpCollection.setState(OTPState.EXPIRED);
			}
		    } else if (otpCollection.getState().equals(OTPState.NOTVERIFIED)) {
			if (!otpService.isNonVerifiedOTPValid(otpCollection.getCreatedTime())) {
			    otpCollection.setState(OTPState.EXPIRED);
			}
		    }
		    otpRepository.save(otpCollection);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void addResource(String resourceId, Resource resource, boolean isCached) {
	TransactionalCollection transactionalCollection = null;
	try {
	    transactionalCollection = transnationalRepositiory.findByResourceIdAndResource(resourceId, resource.getType());
	    if (transactionalCollection == null) {
		transactionalCollection = new TransactionalCollection();
		transactionalCollection.setResourceId(resourceId);
		transactionalCollection.setResource(resource);
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    } else {
		transactionalCollection.setIsCached(isCached);
		transnationalRepositiory.save(transactionalCollection);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public void checkPatient(String id) {
	try {
	    UserCollection userCollection = userRepository.findOne(id);
	    List<PatientCollection> patientCollections = patientRepository.findByUserId(id);
	    if (userCollection != null && patientCollections != null) {
		for (PatientCollection patientCollection : patientCollections) {
		    SolrPatientDocument patientDocument = new SolrPatientDocument();

		    if (patientCollection.getDob() != null) {
			patientDocument.setDays(patientCollection.getDob().getDays() + "");
			patientDocument.setMonths(patientCollection.getDob().getMonths() + "");
			patientDocument.setYears(patientCollection.getDob().getYears() + "");
		    }
		    BeanUtil.map(userCollection, patientDocument);
		    if (patientCollection != null)
			BeanUtil.map(patientCollection, patientDocument);
	
		    if (patientCollection != null)
			patientDocument.setId(patientCollection.getId());

		    if (patientCollection != null)
			solrRegistrationService.editPatient(patientDocument);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDrug(String id) {
	try {
	    DrugCollection drugCollection = drugRepository.findOne(id);
	    if (drugCollection != null) {
		SolrDrugDocument solrDrugDocument = new SolrDrugDocument();
		BeanUtil.map(drugCollection, solrDrugDocument);
		if (drugCollection.getDrugType() != null) {
		    solrDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
		    solrDrugDocument.setDrugType(drugCollection.getDrugType().getType());
		}
		solrPrescriptionService.addDrug(solrDrugDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkLabTest(String id) {
	try {
	    LabTestCollection labTestCollection = labTestRepository.findOne(id);
	    if (labTestCollection != null) {
		SolrLabTestDocument solrLabTestDocument = new SolrLabTestDocument();
		BeanUtil.map(labTestCollection, solrLabTestDocument);
		solrPrescriptionService.addLabTest(solrLabTestDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkComplaint(String id) {
	try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		SolrComplaintsDocument solrComplaintsDocument = new SolrComplaintsDocument();
		BeanUtil.map(complaintCollection, solrComplaintsDocument);
		solrClinicalNotesService.addComplaints(solrComplaintsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkObservation(String id) {
	try {
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		SolrObservationsDocument solrObservationsDocument = new SolrObservationsDocument();
		BeanUtil.map(observationCollection, solrObservationsDocument);
		solrClinicalNotesService.addObservations(solrObservationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkInvestigation(String id) {
	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		SolrInvestigationsDocument solrInvestigationsDocument = new SolrInvestigationsDocument();
		BeanUtil.map(investigationCollection, solrInvestigationsDocument);
		solrClinicalNotesService.addInvestigations(solrInvestigationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDiagnosis(String id) {
	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		SolrDiagnosesDocument solrDiagnosesDocument = new SolrDiagnosesDocument();
		BeanUtil.map(diagnosisCollection, solrDiagnosesDocument);
		solrClinicalNotesService.addDiagnoses(solrDiagnosesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkNotes(String id) {
	try {
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		SolrNotesDocument solrNotesDocument = new SolrNotesDocument();
		BeanUtil.map(notesCollection, solrNotesDocument);
		solrClinicalNotesService.addNotes(solrNotesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDiagrams(String id) {
	try {
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		SolrDiagramsDocument solrDiagramsDocument = new SolrDiagramsDocument();
		BeanUtil.map(diagramsCollection, solrDiagramsDocument);
		solrClinicalNotesService.addDiagrams(solrDiagramsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkLocation(String resourceId) {
	try {
	    LocationCollection locationCollection = locationRepository.findOne(resourceId);
	    if (locationCollection != null) {
		DoctorLocation doctorLocation = new DoctorLocation();
		BeanUtil.map(locationCollection, doctorLocation);
		doctorLocation.setLocationId(locationCollection.getId());
		doctorLocation.setClinicNumber(locationCollection.getClinicNumber());
		if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
		    List<String> images = new ArrayList<String>();
		    for (ClinicImage clinicImage : locationCollection.getImages()) {
			images.add(clinicImage.getImageUrl());
		    }
		    doctorLocation.setImages(images);
		}
		solrRegistrationService.editLocation(doctorLocation);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDoctor(String resourceId, String locationId) {
	try {
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
	    UserCollection userCollection = userRepository.findOne(resourceId);
	    if (doctorCollection != null && userCollection != null) {
		List<UserLocationCollection> userLocationCollections = null;
		if (locationId == null)
		    userLocationCollections = userLocationRepository.findByUserId(resourceId);
		else {
		    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(resourceId, locationId);
		    userLocationCollections = new ArrayList<UserLocationCollection>();
		    userLocationCollections.add(userLocationCollection);
		}
		for (UserLocationCollection collection : userLocationCollections) {
		    LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
		    DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(collection.getId());
		    SolrDoctorDocument doctorDocument = new SolrDoctorDocument();
		    if (locationCollection != null){
		    	locationCollection.setWorkingSchedules(null);
		    	BeanUtil.map(locationCollection, doctorDocument);
		    }
		    if (userCollection != null)BeanUtil.map(userCollection, doctorDocument);
		    if (doctorCollection != null)BeanUtil.map(doctorCollection, doctorDocument);
		    if (clinicProfileCollection != null)BeanUtil.map(clinicProfileCollection, doctorDocument);
		    else {
			doctorDocument.setWorkingSchedules(null);
		    }
		    if (locationCollection != null)
			doctorDocument.setLocationId(locationCollection.getId());
		    if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
			List<String> images = new ArrayList<String>();
			for (ClinicImage clinicImage : locationCollection.getImages()) {
			    images.add(clinicImage.getImageUrl());
			}
			doctorDocument.setImages(images);
		    }
		    solrRegistrationService.addDoctor(doctorDocument);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    private void checkLandmarkLocality(String resourceId) {
	try {
	    LandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository.findOne(resourceId);
	    if (landmarkLocalityCollection != null) {
		SolrLocalityLandmarkDocument solrLocalityLandmarkDocument = new SolrLocalityLandmarkDocument();
		BeanUtil.map(landmarkLocalityCollection, solrLocalityLandmarkDocument);
		solrCityService.addLocalityLandmark(solrLocalityLandmarkDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    private void checkCity(String resourceId) {
	try {
	    CityCollection cityCollection = cityRepository.findOne(resourceId);
	    if (cityCollection != null) {
		SolrCityDocument solrCityDocument = new SolrCityDocument();
		BeanUtil.map(cityCollection, solrCityDocument);
		solrCityService.addCities(solrCityDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }
    }
