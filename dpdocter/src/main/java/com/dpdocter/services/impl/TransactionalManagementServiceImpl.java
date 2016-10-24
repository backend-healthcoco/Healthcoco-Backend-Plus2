package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
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
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorDrugCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.OTPCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TransactionalCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.TreatmentServicesCostCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.elasticsearch.beans.DoctorLocation;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDiseasesDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESLabTestDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESReferenceDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceCostDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.elasticsearch.services.ESMasterService;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
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
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorDrugRepository;
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
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.SMSTrackRepository;
import com.dpdocter.repository.TransnationalRepositiory;
import com.dpdocter.repository.TreatmentServicesCostRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.AppointmentDoctorReminderResponse;
import com.dpdocter.response.AppointmentPatientReminderResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

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
    private ESRegistrationService esRegistrationService;

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private DoctorDrugRepository doctorDrugRepository;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private ESPrescriptionService esPrescriptionService;
	
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
    private ESCityService esCityService;

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
    private ESClinicalNotesService esClinicalNotesService;

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

    @Autowired
    private DiseasesRepository diseasesRepository;

    @Autowired
    private ESMasterService esMasterService; 
    
    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;
    
    @Autowired
    private ESTreatmentService esTreatmentService;
    
    @Autowired
    private TreatmentServicesRepository treatmentServicesRepository;
    
    @Autowired
    private TreatmentServicesCostRepository treatmentServicesCostRepository;
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Autowired
    private SMSTrackRepository smsTrackRepository;
    
    @Value(value = "${mail.appointment.details.subject}")
    private String appointmentDetailsSub;

    @Value(value = "${prescription.add.patient.download.app.message}")
    private String downloadAppMessageToPatient;

    @Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;
    
    @Value("${is.env.production}")
    private Boolean isEnvProduction;

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

			case PATIENT: checkPatient(transactionalCollection.getResourceId()); break;
			case DRUG: checkDrug(transactionalCollection.getResourceId()); break;
			case DOCTORDRUG: checkDoctorDrug(transactionalCollection.getResourceId()); break;
			case LABTEST: checkLabTest(transactionalCollection.getResourceId()); break;
			case COMPLAINT: checkComplaint(transactionalCollection.getResourceId()); break;
			case DIAGNOSIS: checkDiagnosis(transactionalCollection.getResourceId()); break;
			case DIAGRAM: checkDiagrams(transactionalCollection.getResourceId()); break;
			case INVESTIGATION: checkInvestigation(transactionalCollection.getResourceId()); break;
			case NOTES: checkNotes(transactionalCollection.getResourceId()); break;
			case OBSERVATION: checkObservation(transactionalCollection.getResourceId()); break;
			case CITY: checkCity(transactionalCollection.getResourceId()); break;
			case LANDMARKLOCALITY: checkLandmarkLocality(transactionalCollection.getResourceId()); break;
			case DOCTOR: checkDoctor(transactionalCollection.getResourceId(), null); break;
			case LOCATION: checkLocation(transactionalCollection.getResourceId()); break;
			case REFERENCE: checkReference(transactionalCollection.getResourceId()); break;
			case DISEASE: checkDisease(transactionalCollection.getResourceId()); break;
			case DIAGNOSTICTEST: checkDiagnosticTest(transactionalCollection.getResourceId()); break;
			case TREATMENTSERVICE : checkTreatmentService(transactionalCollection.getResourceId()); break;
			case TREATMENTSERVICECOST : checkTreatmentServiceCost(transactionalCollection.getResourceId()); break;
   			default: break;
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
    @Scheduled(cron = "0 0/30 7 * * *", zone = "IST")
    @Override
    @Transactional
    public void sendReminderToDoctor(){
    	System.out.println("Doctor");
    	try{
    		if(isEnvProduction){
    			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
            	
        	    localCalendar.setTime(new Date());
        		int currentDayFromTime = localCalendar.get(Calendar.DATE);
        		int currentMonthFromTime = localCalendar.get(Calendar.MONTH) + 1;
        		int currentYearFromTime = localCalendar.get(Calendar.YEAR);
        		DateTime fromTime = new DateTime(currentYearFromTime, currentMonthFromTime, currentDayFromTime, 0, 0, 0, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

        	    localCalendar.setTime(new Date());
        		int currentDay = localCalendar.get(Calendar.DATE);
        		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
        		int currentYear = localCalendar.get(Calendar.YEAR);
        		DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
    	
        		Aggregation aggregation = Aggregation.newAggregation(
        				Aggregation.match(new Criteria("state").is(AppointmentState.CONFIRM.getState()).
        						and("type").is(AppointmentType.APPOINTMENT.getType()).and("fromDate").gte(fromTime).and("toDate").lte(toTime)),
        				Aggregation.group("doctorId").count().as("total"), Aggregation.project("total").and("doctorId").previousOperation());
        		AggregationResults<AppointmentDoctorReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation, AppointmentCollection.class,
        				AppointmentDoctorReminderResponse.class);

        		List<AppointmentDoctorReminderResponse> appointmentDoctorReminderResponses = aggregationResults.getMappedResults();

        		if(appointmentDoctorReminderResponses != null && !appointmentDoctorReminderResponses.isEmpty())
        		for(AppointmentDoctorReminderResponse appointmentDoctorReminderResponse : appointmentDoctorReminderResponses){
        			UserCollection userCollection = userRepository.findOne(new ObjectId(appointmentDoctorReminderResponse.getDoctorId()));
//        			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
//        			String dateTime = sdf.format(new Date());
        			if(appointmentDoctorReminderResponse.getTotal() > 0){
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
                			    
//                			    String body = mailBodyGenerator.generateAppointmentEmailBody(userCollection.getTitle()+" "+ userCollection.getFirstName(), null, dateTime, null, "appointmentDetailsTemplate.vm");
//                			    mailService.sendEmail(userCollection.getEmailAddress(), appointmentDetailsSub, body, null);
                			}
            			}else{
//            				String body = mailBodyGenerator.generateAppointmentEmailBody(userCollection.getTitle()+" "+ userCollection.getFirstName(), null, dateTime, null, "noAppointmentDetailsTemplate.vm");
//            			    mailService.sendEmail(userCollection.getEmailAddress(), appointmentDetailsSub, body, null);
            			}
            		}
    		}
    		}catch(Exception e){
	     		e.printStackTrace();
	    	    logger.error(e);
    	}
    }
 
//  @Scheduled(cron = "0 0/30 9 * * *", zone = "IST")
  @Override
  @Transactional
  public Boolean sendPromotionalSMSToPatient(){
	  Boolean response = false;
  	try{  		
  		List<PrescriptionCollection> prescriptions = prescriptionRepository.findAll();
  		
  		for(PrescriptionCollection prescriptionCollection : prescriptions){
  			UserCollection userCollection = userRepository.findByIdAndNotSignedUp(prescriptionCollection.getPatientId(), false);			
      			if(userCollection != null){
      				String[] type = {"APP_LINK_THROUGH_PRESCRIPTION"};
      				Calendar cal = Calendar.getInstance();
	    			cal.add(Calendar.DATE, -5);
      				List<SMSTrackDetail> smsTrackDetails = smsTrackRepository.findByDoctorLocationHospitalPatient(prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
      						prescriptionCollection.getHospitalId(), prescriptionCollection.getPatientId(), type, cal.getTime(), new Date(), new PageRequest(0, 1));
      				
      				if(smsTrackDetails == null || smsTrackDetails.isEmpty()){
      					String message = downloadAppMessageToPatient;
      					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
	      				smsTrackDetail.setDoctorId(prescriptionCollection.getDoctorId());
	      				smsTrackDetail.setLocationId(prescriptionCollection.getLocationId());
	      				smsTrackDetail.setHospitalId(prescriptionCollection.getHospitalId());
	      			    smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
	      			    SMSDetail smsDetail = new SMSDetail();
	      			    smsDetail.setUserId(userCollection.getId());
	      			    SMS sms = new SMS();
	      			    smsDetail.setUserName(userCollection.getFirstName());
	      			    sms.setSmsText(message.replace("{doctorName}", prescriptionCollection.getCreatedBy()));
	
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
  		response = true;
  	}catch(Exception e){
  		e.printStackTrace();
  	    logger.error(e);
  	}
  	return response;
  }

	//Appointment Reminder to Patient
  @Scheduled(cron = "${appointment.reminder.to.patient.cron.time}", zone = "IST")
  @Override
  @Transactional
  public void sendReminderToPatient(){
	  System.out.println("done");
  	try{
  		if(isEnvProduction){
  	  		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
  	      	
  	  	    localCalendar.setTime(new Date());
  	  		int currentDayFromTime = localCalendar.get(Calendar.DATE);
  	  		int currentMonthFromTime = localCalendar.get(Calendar.MONTH) + 1;
  	  		int currentYearFromTime = localCalendar.get(Calendar.YEAR);
  	  		DateTime fromTime = new DateTime(currentYearFromTime, currentMonthFromTime, currentDayFromTime, 0, 0, 0, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
  	  		    
  	  	    localCalendar.setTime(new Date());
  	  		int currentDay = localCalendar.get(Calendar.DATE);
  	  		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
  	  		int currentYear = localCalendar.get(Calendar.YEAR);
  	  		DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59, DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
  	  		
  	  		ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("doctorName", "$user.firstName"),
  	  				Fields.field("doctorTitle", "$user.title"),
  					Fields.field("patientMobileNumber", "$patient.mobileNumber"), 
  					Fields.field("appointmentId", "$appointmentId"),
  					Fields.field("clinicNumber", "$location.clinicNumber"),
  					Fields.field("locationName", "$location.locationName"),
  					Fields.field("time", "$time"),
  					Fields.field("fromDate", "$fromDate")
  					));

  	  		Aggregation aggregation = Aggregation.newAggregation(
  	  				Aggregation.match(new Criteria("state").is(AppointmentState.CONFIRM.getState()).and("type").is(AppointmentType.APPOINTMENT.getType()).and("fromDate").gte(fromTime).and("toDate").lte(toTime)),
  	  				Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
  					Aggregation.unwind("user"),
  					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
  					Aggregation.unwind("location"),
  					Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
  					Aggregation.unwind("patient"), projectList);
  	  		AggregationResults<AppointmentPatientReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation, AppointmentCollection.class, AppointmentPatientReminderResponse.class);

  	  		List<AppointmentPatientReminderResponse> appointmentPatientReminderResponses = aggregationResults.getMappedResults();

  	  		if(appointmentPatientReminderResponses != null && !appointmentPatientReminderResponses.isEmpty())
  	  		for(AppointmentPatientReminderResponse appointmentPatientReminderResponse : appointmentPatientReminderResponses){
  	  			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

  				String _24HourTime = String.format("%02d:%02d", appointmentPatientReminderResponse.getTime().getFromTime() / 60, appointmentPatientReminderResponse.getTime().getFromTime() % 60);
  				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
  				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
  				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
  				_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
  				_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
  				
  				Date _24HourDt = _24HourSDF.parse(_24HourTime);
  				String dateTime = _12HourSDF.format(_24HourDt) + ", "+ sdf.format(appointmentPatientReminderResponse.getFromDate());

  				if(!DPDoctorUtils.anyStringEmpty(appointmentPatientReminderResponse.getPatientMobileNumber())){
  	      				
  	      				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
  	      				SMSDetail smsDetail = new SMSDetail();
  	      			    SMS sms = new SMS();
  	      			    sms.setSmsText("You have an upcoming appointment " + appointmentPatientReminderResponse.getAppointmentId()
  	      			    + " @ " + dateTime + " with " + appointmentPatientReminderResponse.getDoctorTitle()+" "+appointmentPatientReminderResponse.getDoctorName()
  	      						+ (!DPDoctorUtils.anyStringEmpty(appointmentPatientReminderResponse.getLocationName()) ? (", " + appointmentPatientReminderResponse.getLocationName()) : "")
  	      						+ (!DPDoctorUtils.anyStringEmpty(appointmentPatientReminderResponse.getClinicNumber()) ? ", " + appointmentPatientReminderResponse.getClinicNumber() : "") 
  	      						+ ". Download Healthcoco App- "+ patientAppBitLink);

  	      			    SMSAddress smsAddress = new SMSAddress();
  	      			    smsAddress.setRecipient(appointmentPatientReminderResponse.getPatientMobileNumber());
  	      			    sms.setSmsAddress(smsAddress);

  	      			    smsDetail.setSms(sms);
  	      			    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
  	      			    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
  	      			    smsDetails.add(smsDetail);
  	      			    smsTrackDetail.setSmsDetails(smsDetails);
  	      			    sMSServices.sendSMS(smsTrackDetail, false);
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
    public void addResource(ObjectId resourceId, Resource resource, boolean isCached) {
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
    public void checkPatient(ObjectId id) {
	try {
	    UserCollection userCollection = userRepository.findOne(id);
	    List<PatientCollection> patientCollections = patientRepository.findByUserId(id);
	    if (userCollection != null && patientCollections != null) {
		for (PatientCollection patientCollection : patientCollections) {
		    ESPatientDocument patientDocument = new ESPatientDocument();

		    BeanUtil.map(userCollection, patientDocument);
		    if (patientCollection != null)
			BeanUtil.map(patientCollection, patientDocument);
	
		    if (patientCollection != null)
			patientDocument.setId(patientCollection.getId().toString());

		    if (patientCollection != null)
			esRegistrationService.addPatient(patientDocument);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDrug(ObjectId id) {
	try {
	    DrugCollection drugCollection = drugRepository.findOne(id);
	    if (drugCollection != null) {
		ESDrugDocument esDrugDocument = new ESDrugDocument();
		BeanUtil.map(drugCollection, esDrugDocument);
		if (drugCollection.getDrugType() != null) {
		    esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
		    esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
		}
		esPrescriptionService.addDrug(esDrugDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDoctorDrug(ObjectId resourceId) {
		try {
		    DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findOne(resourceId);
		    if (doctorDrugCollection != null) {
		    	DrugCollection drugCollection = drugRepository.findOne(doctorDrugCollection.getDrugId());
				if(drugCollection != null){
					ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
					BeanUtil.map(drugCollection, esDoctorDrugDocument);
					BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
					esDoctorDrugDocument.setId(drugCollection.getId().toString());
					esPrescriptionService.addDoctorDrug(esDoctorDrugDocument);
				}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		}
	}

    @Override
    @Transactional
    public void checkLabTest(ObjectId id) {
	try {
	    LabTestCollection labTestCollection = labTestRepository.findOne(id);
	    if (labTestCollection != null) {
		ESLabTestDocument esLabTestDocument = new ESLabTestDocument();
		BeanUtil.map(labTestCollection, esLabTestDocument);
		esPrescriptionService.addLabTest(esLabTestDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkComplaint(ObjectId id) {
	try {
	    ComplaintCollection complaintCollection = complaintRepository.findOne(id);
	    if (complaintCollection != null) {
		ESComplaintsDocument esComplaintsDocument = new ESComplaintsDocument();
		BeanUtil.map(complaintCollection, esComplaintsDocument);
		esClinicalNotesService.addComplaints(esComplaintsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkObservation(ObjectId id) {
	try {
	    ObservationCollection observationCollection = observationRepository.findOne(id);
	    if (observationCollection != null) {
		ESObservationsDocument esObservationsDocument = new ESObservationsDocument();
		BeanUtil.map(observationCollection, esObservationsDocument);
		esClinicalNotesService.addObservations(esObservationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkInvestigation(ObjectId id) {
	try {
	    InvestigationCollection investigationCollection = investigationRepository.findOne(id);
	    if (investigationCollection != null) {
		ESInvestigationsDocument esInvestigationsDocument = new ESInvestigationsDocument();
		BeanUtil.map(investigationCollection, esInvestigationsDocument);
		esClinicalNotesService.addInvestigations(esInvestigationsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDiagnosis(ObjectId id) {
	try {
	    DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(id);
	    if (diagnosisCollection != null) {
		ESDiagnosesDocument esDiagnosesDocument = new ESDiagnosesDocument();
		BeanUtil.map(diagnosisCollection, esDiagnosesDocument);
		esClinicalNotesService.addDiagnoses(esDiagnosesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkNotes(ObjectId id) {
	try {
	    NotesCollection notesCollection = notesRepository.findOne(id);
	    if (notesCollection != null) {
		ESNotesDocument esNotesDocument = new ESNotesDocument();
		BeanUtil.map(notesCollection, esNotesDocument);
		esClinicalNotesService.addNotes(esNotesDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDiagrams(ObjectId id) {
	try {
	    DiagramsCollection diagramsCollection = diagramsRepository.findOne(id);
	    if (diagramsCollection != null) {
		ESDiagramsDocument esDiagramsDocument = new ESDiagramsDocument();
		BeanUtil.map(diagramsCollection, esDiagramsDocument);
		esClinicalNotesService.addDiagrams(esDiagramsDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkLocation(ObjectId resourceId) {
	try {
	    LocationCollection locationCollection = locationRepository.findOne(resourceId);
	    if (locationCollection != null) {
		DoctorLocation doctorLocation = new DoctorLocation();
		BeanUtil.map(locationCollection, doctorLocation);
		doctorLocation.setLocationId(locationCollection.getId().toString());
		if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
		    List<String> images = new ArrayList<String>();
		    for (ClinicImage clinicImage : locationCollection.getImages()) {
			images.add(clinicImage.getImageUrl());
		    }
		    doctorLocation.setImages(images);
		}
		esRegistrationService.editLocation(doctorLocation);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    @Override
    @Transactional
    public void checkDoctor(ObjectId resourceId, ObjectId locationId) {
	try {
	    DoctorCollection doctorCollection = doctorRepository.findByUserId(resourceId);
	    UserCollection userCollection = userRepository.findOne(resourceId);
	    if (doctorCollection != null && userCollection != null) {
		List<UserLocationCollection> userLocationCollections = null;
		if (locationId == null)
		    userLocationCollections = userLocationRepository.findByUserIdAndIsActivate(resourceId);
		else {
		    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(resourceId, locationId);
		    userLocationCollections = new ArrayList<UserLocationCollection>();
		    userLocationCollections.add(userLocationCollection);
		}
		for (UserLocationCollection collection : userLocationCollections) {
		    LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
		    DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(collection.getId());
		    ESDoctorDocument doctorDocument = new ESDoctorDocument();
		    if (locationCollection != null){
		    	BeanUtil.map(locationCollection, doctorDocument);
		    }
		    if (userCollection != null)BeanUtil.map(userCollection, doctorDocument);
		    if (doctorCollection != null)BeanUtil.map(doctorCollection, doctorDocument);
		    if (clinicProfileCollection != null)BeanUtil.map(clinicProfileCollection, doctorDocument);
		    if (locationCollection != null)
			doctorDocument.setLocationId(locationCollection.getId().toString());
		    if (locationCollection.getImages() != null && !locationCollection.getImages().isEmpty()) {
			List<String> images = new ArrayList<String>();
			for (ClinicImage clinicImage : locationCollection.getImages()) {
			    images.add(clinicImage.getImageUrl());
			}
			doctorDocument.setImages(images);
		    }
		    esRegistrationService.addDoctor(doctorDocument);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    private void checkLandmarkLocality(ObjectId resourceId) {
	try {
	    LandmarkLocalityCollection landmarkLocalityCollection = landmarkLocalityRepository.findOne(resourceId);
	    if (landmarkLocalityCollection != null) {
		ESLandmarkLocalityDocument esLocalityLandmarkDocument = new ESLandmarkLocalityDocument();
		BeanUtil.map(landmarkLocalityCollection, esLocalityLandmarkDocument);
		esCityService.addLocalityLandmark(esLocalityLandmarkDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }

    private void checkCity(ObjectId resourceId) {
	try {
	    CityCollection cityCollection = cityRepository.findOne(resourceId);
	    if (cityCollection != null) {
		ESCityDocument esCityDocument = new ESCityDocument();
		BeanUtil.map(cityCollection, esCityDocument);
		esCityService.addCities(esCityDocument);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	}
    }
    
    private void checkReference(ObjectId resourceId) {
    	try {
    	    ReferencesCollection referenceCollection = referenceRepository.findOne(resourceId);
    	    if (referenceCollection != null) {
    		ESReferenceDocument esReferenceDocument = new ESReferenceDocument();
    		BeanUtil.map(referenceCollection, esReferenceDocument);
    		esRegistrationService.addEditReference(esReferenceDocument);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}
	}

	private void checkDisease(ObjectId resourceId) {
		try {
    	    DiseasesCollection diseasesCollection = diseasesRepository.findOne(resourceId);
    	    if (diseasesCollection != null) {
    		ESDiseasesDocument esDiseasesDocument = new ESDiseasesDocument();
    		BeanUtil.map(diseasesCollection, esDiseasesDocument);
    		esMasterService.addEditDisease(esDiseasesDocument);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}
	}

	private void checkDiagnosticTest(ObjectId resourceId) {
		try {
    	    DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(resourceId);
    	    if (diagnosticTestCollection != null) {
    		ESDiagnosticTestDocument esDiagnosticTestDocument = new ESDiagnosticTestDocument();
    		BeanUtil.map(diagnosticTestCollection, esDiagnosticTestDocument);
    		esPrescriptionService.addEditDiagnosticTest(esDiagnosticTestDocument);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}
	}

	private void checkTreatmentService(ObjectId resourceId) {
		try {
    	    TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository.findOne(resourceId);
    	    if (treatmentServicesCollection != null) {
    		ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
    		BeanUtil.map(treatmentServicesCollection, esTreatmentServiceDocument);
    		esTreatmentService.addEditService(esTreatmentServiceDocument);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}
	}

	private void checkTreatmentServiceCost(ObjectId resourceId) {
		try {
    	    TreatmentServicesCostCollection treatmentServicesCostCollection = treatmentServicesCostRepository.findOne(resourceId);
    	    if (treatmentServicesCostCollection != null) {
    		ESTreatmentServiceCostDocument esTreatmentServiceCostDocument = new ESTreatmentServiceCostDocument();
    		BeanUtil.map(treatmentServicesCostCollection, esTreatmentServiceCostDocument);
    		esTreatmentService.addEditServiceCost(esTreatmentServiceCostDocument);
    	    }
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    logger.error(e);
    	}
	}

}
