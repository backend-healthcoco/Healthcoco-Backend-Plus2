package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientQueue;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Slot;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.AppointmentWorkFlowCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.SMSContent;
import com.dpdocter.enums.SMSFormatType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.AppointmentWorkFlowRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientQueueRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private LandmarkLocalityRepository landmarkLocalityRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    UserLocationRepository userLocationRepository;

    @Autowired
    private DoctorClinicProfileRepository doctorClinicProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private LocationServices locationServices;

    @Autowired
    private LabTestRepository labTestRepository;

    @Autowired
    private AppointmentBookedSlotRepository appointmentBookedSlotRepository;
    
    @Autowired
    private AppointmentWorkFlowRepository appointmentWorkFlowRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private SMSFormatRepository sMSFormatRepository;

    @Autowired
    private SMSServices sMSServices;

    @Autowired
    private PatientQueueRepository patientQueueRepository;

    @Autowired
    private SpecialityRepository specialityRepository;
    
    @Autowired
    private DiagnosticTestRepository diagnosticTestRepository;
    
    @Value(value = "${Appointment.timeSlotIsBooked}")
    private String timeSlotIsBooked;

    @Value(value = "${Appointment.incorrectAppointmentId}")
    private String incorrectAppointmentId;

    @Value(value = "${Appoinment.appointmentDoesNotExist}")
    private String appointmentDoesNotExist;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
	PushNotificationServices pushNotificationServices;

    @Value(value = "${mail.appointment.cancel.subject}")
    private String appointmentCancelMailSubject;

//    @Value(value = "${Appoinment.appointmentDoesNotExist}")
//    private String appointmentDoesNotExist;
//
//    @Value(value = "${Appoinment.appointmentDoesNotExist}")
//    private String appointmentDoesNotExist;
//
//    @Value(value = "${Appoinment.appointmentDoesNotExist}")
//    private String appointmentDoesNotExist;
//
//    @Value(value = "${Appoinment.appointmentDoesNotExist}")
//    private String appointmentDoesNotExist;
//
//    @Value(value = "${Appoinment.appointmentDoesNotExist}")
//    private String appointmentDoesNotExist;
//
//    
    @Override
    @Transactional
    public City addCity(City city) {
	try {
	    CityCollection cityCollection = new CityCollection();
	    BeanUtil.map(city, cityCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(city.getCity() + " "
		    + (cityCollection.getState() != null ? cityCollection.getState() : "")+ " "
				    + (cityCollection.getCountry() != null ? cityCollection.getCountry() : ""));

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), cityCollection);

	    cityCollection = cityRepository.save(cityCollection);
	    BeanUtil.map(cityCollection, city);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return city;
    }

    @Override
    @Transactional
    public Boolean activateDeactivateCity(String cityId, boolean activate) {
	try {
	    CityCollection cityCollection = cityRepository.findOne(cityId);
	    if (cityCollection == null) {
		throw new BusinessException(ServiceError.InvalidInput, "Invalid city Id");
	    }
	    cityCollection.setIsActivated(activate);
	    cityRepository.save(cityCollection);
	} catch (BusinessException be) {
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return true;
    }

    @Override
    @Transactional
    public List<City> getCities(String state) {
	List<City> response = new ArrayList<City>();
	try {
	    List<CityCollection> cities = null;
	    if(DPDoctorUtils.allStringsEmpty(state))cities = cityRepository.findAll(new Sort(Sort.Direction.ASC, "city"));
	    else cities = cityRepository.findAll(state, new Sort(Sort.Direction.ASC, "city"));
	    if (cities != null) {
		BeanUtil.map(cities, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public City getCity(String cityId) {
	City response = new City();
	try {
	    CityCollection city = cityRepository.findOne(cityId);
	    if (city != null) {
		BeanUtil.map(city, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public LandmarkLocality addLandmaklLocality(LandmarkLocality landmarkLocality) {
	CityCollection cityCollection = null;
	try {
	    LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
	    BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
	    if (landmarkLocality.getCityId() != null) {
		cityCollection = cityRepository.findOne(landmarkLocality.getCityId());
	    }
	   
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(landmarkLocality.getLandmark() != null ? landmarkLocality.getLandmark()
		    + " " : "" + landmarkLocality.getLocality() != null ? landmarkLocality.getLocality() + " "
		    : "" + cityCollection.getCity() != null ? cityCollection.getCity() + " " : "" + 
		    		cityCollection.getState() != null ? cityCollection.getState(): "" +
		    		cityCollection.getCountry() != null ? cityCollection.getCountry(): "");

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), landmarkLocalityCollection);

	    landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
	    BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return landmarkLocality;
    }

    @Override
    @Transactional
    public Clinic getClinic(String locationId) {
	Clinic response = new Clinic();
	LocationCollection localtionCollection = null;
	Location location = new Location();
	HospitalCollection hospitalCollection = null;
	Hospital hospital = new Hospital();

	List<Doctor> doctors = new ArrayList<Doctor>();
	try {
	    localtionCollection = locationRepository.findOne(locationId);
	    if (localtionCollection == null) {
		return null;
	    } else {
		BeanUtil.map(localtionCollection, location);
		response.setLocation(location);

		hospitalCollection = hospitalRepository.findOne(localtionCollection.getHospitalId());
		if (hospitalCollection != null) {
		    BeanUtil.map(hospitalCollection, hospital);
		    response.setHospital(hospital);
		}

		List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(localtionCollection.getId());
		for (Iterator<UserLocationCollection> iterator = userLocationCollections.iterator(); iterator.hasNext();) {
		    UserLocationCollection userLocationCollection = iterator.next();
		    DoctorCollection doctorCollection = doctorRepository.findByUserId(userLocationCollection.getUserId());
		    UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());

		    if (doctorCollection != null) {
			Doctor doctor = new Doctor();
			BeanUtil.map(doctorCollection, doctor);
			if (userCollection != null) {
			    BeanUtil.map(userCollection, doctor);
			}

			if (doctorClinicProfileCollection != null) {
			    DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
			    BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
			    doctorClinicProfile.setLocationId(userLocationCollection.getLocationId());
			    doctorClinicProfile.setDoctorId(userLocationCollection.getUserId());
			    doctor.setDoctorClinicProfile(doctorClinicProfile);
			}
			if(doctor.getSpecialities() != null && !doctor.getSpecialities().isEmpty()){
				List<String> specialities = (List<String>) CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctor.getSpecialities()),new BeanToPropertyValueTransformer("superSpeciality"));
				doctor.setSpecialities(specialities);
			}
			doctors.add(doctor);
		    }
		}
		response.setDoctors(doctors);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public Appointment updateAppointment(AppointmentRequest request) {
	Appointment response = null;
	try {
		AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(request.getAppointmentId());
		if (appointmentCollection != null) {
			UserCollection userCollection = userRepository.findOne(appointmentCollection.getDoctorId());
	        LocationCollection locationCollection = locationRepository.findOne(appointmentCollection.getLocationId());
	        UserCollection patient = userRepository.findOne(appointmentCollection.getPatientId());
	    
	    if (userCollection != null && locationCollection != null && patient != null) {
			UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(appointmentCollection.getDoctorId(), appointmentCollection.getLocationId());
	        DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
	    
		    AppointmentCollection appointmentCollectionToCheck = null;
		    if (request.getState().equals(AppointmentState.RESCHEDULE))
			appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(appointmentCollection.getDoctorId(),
				appointmentCollection.getLocationId(), request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(),request.getToDate(),
				AppointmentState.CANCEL.getState());
		    if (appointmentCollectionToCheck == null) {
			AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
			BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
			appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

			appointmentCollection.setState(request.getState());
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			
			String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60, appointmentCollection.getTime().getFromTime() % 60);
	        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
	        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
	        if(clinicProfileCollection != null){
	        	sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
	        	_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
	        	_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
	        }
			else{
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
			}
				        
	        Date _24HourDt = _24HourSDF.parse(_24HourTime);
	        
		    String patientName = patient.getFirstName() != null?patient.getFirstName().split(" ")[0] :"", appointmentId= appointmentCollection.getAppointmentId(), 
					dateTime= _12HourSDF.format(_24HourDt)+", "+sdf.format(appointmentCollection.getFromDate()),
					doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getClinicNumber() != null ? locationCollection.getClinicNumber() :"";
					
			if(request.getState().getState().equals(AppointmentState.CANCEL.getState())){
				if(request.getCancelledBy() != null){
					if(request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType()))
						appointmentCollection.setCancelledBy(userCollection.getTitle()+" "+userCollection.getFirstName());
					else
						appointmentCollection.setCancelledBy(patient.getFirstName());
				}
		    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(request.getAppointmentId());
		    	if(bookedSlotCollection != null) appointmentBookedSlotRepository.delete(bookedSlotCollection);
		    }
		    else {
		    	if(request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())){
		    		appointmentCollection.setFromDate(request.getFromDate());
			    	appointmentCollection.setToDate(request.getToDate());
				    appointmentCollection.setTime(request.getTime());
			    	appointmentCollection.setIsRescheduled(true);
			    	appointmentCollection.setState(AppointmentState.NEW);
			    	dateTime= String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60, appointmentCollection.getTime().getFromTime() % 60)+" "+new SimpleDateFormat("MMM dd,yyyy").format(appointmentCollection.getFromDate());
			    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(request.getAppointmentId());
			    	if(bookedSlotCollection != null) {
			    		bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
			    		bookedSlotCollection.setToDate(appointmentCollection.getToDate());
			    		bookedSlotCollection.setTime(request.getTime());
					    bookedSlotCollection.setUpdatedTime(new Date());
			    		appointmentBookedSlotRepository.save(bookedSlotCollection);
			    	}
			    }
		    }
			appointmentCollection.setExplanation(request.getExplanation());
		    appointmentCollection.setNotifyDoctorByEmail(request.getNotifyDoctorByEmail());
		    appointmentCollection.setNotifyDoctorBySms(request.getNotifyDoctorBySms());
		    appointmentCollection.setNotifyPatientByEmail(request.getNotifyPatientByEmail());
		    appointmentCollection.setNotifyPatientBySms(request.getNotifyPatientByEmail());
		    appointmentCollection.setUpdatedTime(new Date());
		    appointmentCollection = appointmentRepository.save(appointmentCollection);
		  //sendSMS after appointment is saved	

		    if(request.getState().getState().equals(AppointmentState.CANCEL.getState())){
		    	if(request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())){
			    	if(request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail());
			    	if(request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()){
			    		if(appointmentCollection.getState().getState().equals(AppointmentState.CANCEL.getState()))
			    			sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR",request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    	if(request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail() && patient.getEmailAddress() != null)
			    		sendEmail(doctorName, patientName, dateTime, clinicName,"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR",patient.getEmailAddress());
			    	if(request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()){
			    		if(appointmentCollection.getState().getState().equals(AppointmentState.CANCEL.getState()))
			    			sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		}
			    }
			    else{
			    	if(request.getState().getState().equals(AppointmentState.CANCEL.getState())){
			    		sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		if(DPDoctorUtils.anyStringEmpty(patient.getEmailAddress()))
			    			sendEmail(doctorName, patientName, dateTime, clinicName,"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT",patient.getEmailAddress());
			    	}
			    }
		    }else{
		    	if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR.getType())){
			    	if(request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
			    		if(appointmentCollection.getState().getState().equals(AppointmentState.CONFIRM.getState()))
			    			sendEmail(doctorName, patientName, dateTime, clinicName,"CONFIRMED_APPOINTMENT_TO_DOCTOR",userCollection.getEmailAddress());
			    		else
			    			sendEmail(doctorName, patientName, dateTime, clinicName,"RESCHEDULE_APPOINTMENT_TO_DOCTOR",userCollection.getEmailAddress());
			    	
			    	if(request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()){
			    		if(appointmentCollection.getState().getState().equals(AppointmentState.CONFIRM.getState()))
			    			sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
				    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		else
			    			sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
				    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    	if(request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail())System.out.println("send email to patient");
			    	if(request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()){
			    		if(appointmentCollection.getState().getState().equals(AppointmentState.CONFIRM.getState()))
			    			sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
				    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		else
			    			sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
				    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    }
		    }
		    response = new Appointment();
		    BeanUtil.map(appointmentCollection, response);
			PatientCard patientCard = new PatientCard();
	    	BeanUtil.map(patient, patientCard);
	    	patientCard.setUserId(patient.getId());
	    	response.setPatient(patientCard);
		     
		    if(appointmentCollection.getState().getState().equalsIgnoreCase(AppointmentState.CONFIRM.getState())){
		    	updateQueue(appointmentCollection.getAppointmentId(), appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), appointmentCollection.getFromDate(), appointmentCollection.getTime().getFromTime(), null, false);
		    }
		    else if(appointmentCollection.getState().getState().equalsIgnoreCase(AppointmentState.CANCEL.getState())){
		    	updateQueue(appointmentCollection.getAppointmentId(), appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), appointmentCollection.getFromDate(), null, 0, false);
		    }
		} else {
		    logger.error(timeSlotIsBooked);
		    throw new BusinessException(ServiceError.InvalidInput, timeSlotIsBooked);
		}
	    } else {
	    	logger.error("Incorrect DoctorId or locationId or patientId");
			throw new BusinessException(ServiceError.InvalidInput, "Incorrect DoctorId or locationId or patientId");
	    }
	}else {
		logger.error(incorrectAppointmentId);
		throw new BusinessException(ServiceError.InvalidInput, incorrectAppointmentId);
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public Appointment addAppointment(AppointmentRequest request) {
	Appointment response = null;
	UserLocationCollection userLocationCollection = null;
	DoctorClinicProfileCollection clinicProfileCollection = null;
	try {
		UserCollection userCollection = userRepository.findOne(request.getDoctorId());
		LocationCollection locationCollection = locationRepository.findOne(request.getLocationId());
		UserCollection patient = userRepository.findOne(request.getPatientId());
	    AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(request.getDoctorId(), request.getLocationId(), request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(), request.getToDate(), AppointmentState.CANCEL.getState());

	    userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
        clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
    	
	    if(userCollection != null && locationCollection != null && patient != null){
			
			if(appointmentCollection == null) {			
			    appointmentCollection = new AppointmentCollection();
			    BeanUtil.map(request, appointmentCollection);
			    appointmentCollection.setCreatedTime(new Date());
			    appointmentCollection.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial()+DPDoctorUtils.generateRandomId());
			    
			    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
				
			    String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60, appointmentCollection.getTime().getFromTime() % 60);
		        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
		        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
		        if(clinicProfileCollection != null){
		        	sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
		        	_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
		        	_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
		        }
				else{
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}
				
		        Date _24HourDt = _24HourSDF.parse(_24HourTime);
		        
			    String patientName = patient.getFirstName() != null?patient.getFirstName().split(" ")[0] :"", appointmentId= appointmentCollection.getAppointmentId(), 
						dateTime= _12HourSDF.format(_24HourDt)+", "+sdf.format(appointmentCollection.getFromDate()),
						doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getClinicNumber() != null ? locationCollection.getClinicNumber() :"";
						
			    if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)){
			    	appointmentCollection.setState(AppointmentState.CONFIRM);
			    	appointmentCollection.setCreatedBy(userCollection.getTitle()+" "+userCollection.getFirstName());
			    }
			    else{
			    	appointmentCollection.setCreatedBy(patient.getFirstName());
			    	if(clinicProfileCollection != null && clinicProfileCollection.getFacility() != null && (clinicProfileCollection.getFacility().getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))){
			    		appointmentCollection.setState(AppointmentState.CONFIRM);
			    	}else{
			    		appointmentCollection.setState(AppointmentState.NEW);
			    	}
			    }  
			    appointmentCollection = appointmentRepository.save(appointmentCollection);
			    
			    AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
			    BeanUtil.map(appointmentCollection, bookedSlotCollection);
			    bookedSlotCollection.setId(null);
		    	appointmentBookedSlotRepository.save(bookedSlotCollection);
		    	
		    	//sendSMS after appointment is saved	
		    	if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)){
			    	if(request.getNotifyDoctorByEmail()!=null && request.getNotifyDoctorByEmail())
			    		sendEmail(doctorName, patientName, dateTime, clinicName,"CONFIRMED_APPOINTMENT_TO_DOCTOR",userCollection.getEmailAddress());
			    	if(request.getNotifyDoctorBySms()!=null && request.getNotifyDoctorBySms()){
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    	if(request.getNotifyPatientByEmail()!=null && request.getNotifyPatientByEmail() && patient.getEmailAddress() != null)
			    		sendEmail(doctorName, patientName, dateTime, clinicName,"CONFIRMED_APPOINTMENT_TO_PATIENT",patient.getEmailAddress());
			    	if(request.getNotifyPatientBySms()!=null && request.getNotifyPatientBySms()){
			    		sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(),"CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    }
			    else{
			    	if(clinicProfileCollection != null && clinicProfileCollection.getFacility() != null && (clinicProfileCollection.getFacility().getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))){
			    		sendEmail(doctorName, patientName, dateTime, clinicName,"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT",userCollection.getEmailAddress());
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}else{
			    		sendEmail(doctorName, patientName, dateTime, clinicName,"CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR",userCollection.getEmailAddress());
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(),"TENTATIVE_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    }
			    if (appointmentCollection != null) {
				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
				PatientCard patientCard = new PatientCard();
		    	BeanUtil.map(patient, patientCard);
		    	patientCard.setUserId(patient.getId());
		    	response.setPatient(patientCard);
			    }
			    
			    if(appointmentCollection.getState().getState().equalsIgnoreCase(AppointmentState.CONFIRM.getState())){
			    	updateQueue(appointmentCollection.getAppointmentId(), appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), appointmentCollection.getFromDate(), appointmentCollection.getTime().getFromTime(),null, false);
			    }
			} else {
			    logger.error(timeSlotIsBooked);
			    throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
			}
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

	private void sendEmail(String doctorName, String patientName, String dateTime, String clinicName, String type, String emailAddress) throws MessagingException {
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "confirmAppointmentToPatient.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);		}
		break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "confirmAppointmentToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentRequestToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
//		case "TENTATIVE_APPOINTMENT_TO_PATIENT" :{
//			text = "Your appointment "+appointmentId+" @ "+dateTime+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+" has been sent for confirmation.";
//			smsDetail.setUserName(patientName);
//			pushNotificationServices.notifyUser(userId, text, null, null);
//		}
//		break;
//		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentCancelByPatientToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentCancelToPatientByDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT" :{
			 String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentCancelByDoctorToDoctor.vm");
			 mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentCancelToPatientByPatient.vm");
			 mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
//		case "APPOINTMENT_REMINDER_TO_PATIENT" :{
//			text = "You have an upcoming appointment "+appointmentId+" @ "+dateTime+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+".";
//			smsDetail.setUserName(patientName);
//			pushNotificationServices.notifyUser(userId, text, null, null);
//		}
//		break;
//		
//		case "RESCHEDULE_APPOINTMENT_TO_PATIENT" :{
//			text = "Your appointment "+appointmentId+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+" has been rescheduled @ "+dateTime+".";
//			smsDetail.setUserName(patientName);
//			pushNotificationServices.notifyUser(userId, text, null, null);
//		}
//		break;
		
		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR" :{
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName, "appointmentRescheduleByDoctorToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
		break;
		
		default:break;
		}
		
	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,String userId, String mobileNumber, String patientName, String appointmentId, String dateTime, String doctorName, String clinicName, String clinicContactNum) {
		SMSFormatCollection smsFormatCollection = null;
		if(formatType != null){
			smsFormatCollection = sMSFormatRepository.find(doctorId, locationId, hospitalId, formatType);
		}
		
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(doctorId);
	    smsTrackDetail.setHospitalId(hospitalId);
	    smsTrackDetail.setLocationId(locationId);
	    smsTrackDetail.setType("APPOINTMENT");
	    SMSDetail smsDetail = new SMSDetail();
	    smsDetail.setUserId(userId);
	    SMS sms = new SMS();
	    
	    if(DPDoctorUtils.anyStringEmpty(patientName))patientName="";
	    if(DPDoctorUtils.anyStringEmpty(appointmentId))appointmentId="";
	    if(DPDoctorUtils.anyStringEmpty(dateTime))dateTime="";
	    if(DPDoctorUtils.anyStringEmpty(doctorName))doctorName="";
	    if(DPDoctorUtils.anyStringEmpty(clinicName))clinicName="";
	    if(DPDoctorUtils.anyStringEmpty(clinicContactNum))clinicContactNum="";
	    if(smsFormatCollection != null){	    		 
	    		 if(type.equalsIgnoreCase("CONFIRMED_APPOINTMENT_TO_PATIENT") || type.equalsIgnoreCase("TENTATIVE_APPOINTMENT_TO_PATIENT") ||
	    				 type.equalsIgnoreCase("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR") || type.equalsIgnoreCase("APPOINTMENT_REMINDER_TO_PATIENT")||
	    				 type.equalsIgnoreCase("RESCHEDULE_APPOINTMENT_TO_PATIENT")){
	    			 if(!smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent()) || clinicName == null)clinicName ="";
	    			 if(!smsFormatCollection.getContent().equals(SMSContent.CLINIC_CONTACT_NUMBER.getContent()) || clinicContactNum == null)clinicContactNum = "";
	    		 }
	    	}
	    String text = "";
	    switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT" :{
			text = "Your appointment "+appointmentId+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+" has been confirmed @ "+dateTime+".";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR" :{
			text = "Healthcoco! Your appointment with "+patientName+" has been scheduled @ "+dateTime + (clinicName!= ""?" at "+clinicName:"")+".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR" :{
			text = "Healthcoco! You have an appointment request from "+patientName+" for "+dateTime+" at "+clinicName+".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "TENTATIVE_APPOINTMENT_TO_PATIENT" :{
			text = "Your appointment "+appointmentId+" @ "+dateTime+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+" has been sent for confirmation.";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR" :{
			text = "Your appointment"+" with "+patientName+" for "+dateTime+" at " +clinicName+" has been cancelled as per your request.";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR" :{
			text = "Your appointment "+appointmentId+" @ "+dateTime+" has been cancelled by "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+".Request you to book again.";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT" :{
			text = "Healthcoco! Your appointment"+" with "+patientName+" @ "+dateTime+" at "+clinicName+", has been cancelled by patient.";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT" :{
			text = "Your appointment "+appointmentId+" for "+dateTime+" with "+doctorName+" has been cancelled as per your request";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "APPOINTMENT_REMINDER_TO_PATIENT" :{
			text = "You have an upcoming appointment "+appointmentId+" @ "+dateTime+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+".";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "RESCHEDULE_APPOINTMENT_TO_PATIENT" :{
			text = "Your appointment "+appointmentId+" with "+doctorName+(clinicName!= ""?", "+clinicName:"")+(clinicContactNum!= ""?", "+clinicContactNum:"")+" has been rescheduled @ "+dateTime+".";
			smsDetail.setUserName(patientName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR" :{
			text = "Your appointment with "+patientName+"has been rescheduled to "+dateTime+" at "+clinicName+".";
			smsDetail.setUserName(doctorName);
			pushNotificationServices.notifyUser(userId, text, null, null);
		}
		break;
		
		default:break;
		}
	    
	    sms.setSmsText(text);

	    SMSAddress smsAddress = new SMSAddress();
	    smsAddress.setRecipient(mobileNumber);
	    sms.setSmsAddress(smsAddress);

	    smsDetail.setSms(sms);
	    smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
	    List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
	    smsDetails.add(smsDetail);
	    smsTrackDetail.setSmsDetails(smsDetails);
	    sMSServices.sendSMS(smsTrackDetail, true);
	}

	@Override
	@Transactional
    public List<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from, String to, int page, int size, String updatedTime) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	try {
		long updatedTimeStamp = Long.parseLong(updatedTime);
		
		
		Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
	    if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(locationId);
	    
	    if(doctorId != null && !doctorId.isEmpty())criteria.and("doctorId").in(doctorId);
	    
	    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(patientId);
	    
	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
	        	
	    if(!DPDoctorUtils.anyStringEmpty(from)){
	    	localCalendar.setTime(new Date(Long.parseLong(from)));
		    int currentDay = localCalendar.get(Calendar.DATE);
		    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		    int currentYear = localCalendar.get(Calendar.YEAR);

		    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
		    
	    	criteria.and("fromDate").gte(fromTime);
	    }
	    else if(!DPDoctorUtils.anyStringEmpty(to)){
	    	localCalendar.setTime(new Date(Long.parseLong(to)));
		    int currentDay = localCalendar.get(Calendar.DATE);
		    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		    int currentYear = localCalendar.get(Calendar.YEAR);

		    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
	    	
	    	criteria.and("toDate").lte(toTime);
	    }
	    Query query = new Query(criteria);
	    if(size > 0) appointmentCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.ASC, "fromDate","time.from")), AppointmentCollection.class);
	    else appointmentCollections = mongoTemplate.find(query.with(new Sort(Direction.ASC, "fromDate","time.from")), AppointmentCollection.class);
		
	    if (appointmentCollections != null) {
		    response = new ArrayList<Appointment>();
		    
		    for(AppointmentCollection collection : appointmentCollections){
		    	Appointment appointment = new Appointment();
		    	PatientCard patient = null;
		    	if(collection.getType().equals(AppointmentType.APPOINTMENT)){
		    		UserCollection userCollection = userRepository.findOne(collection.getPatientId());
		    		patient = new PatientCard();
			    	BeanUtil.map(userCollection, patient);
			    	patient.setUserId(patient.getId());
		    	}		    	
		    	BeanUtil.map(collection, appointment);
		    	appointment.setPatient(patient);
		    	if(collection.getDoctorId() != null){
		    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
		    		if(doctor != null)appointment.setDoctorName(doctor.getFirstName());
		    	}
		    	if(collection.getLocationId() != null){
		    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
		    		if(locationCollection != null){
		    			appointment.setLocationName(locationCollection.getLocationName());
		    			appointment.setClinicNumber(locationCollection.getClinicNumber());
		    			
		    			String address = 
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress()) ? locationCollection.getStreetAddress()+", ":"")+
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality()) ? locationCollection.getLocality()+", ":"")+
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCity()) ? locationCollection.getCity()+", ":"")+
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getState()) ? locationCollection.getState()+", ":"")+
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry()) ? locationCollection.getCountry()+", ":"")+
		    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode()) ? locationCollection.getPostalCode():"");
		    	    	
		    		    if(address.charAt(address.length() - 2) == ','){
		    		    	address = address.substring(0, address.length() - 2);
		    		    }
		    		    
		    			appointment.setClinicAddress(address);
		    			appointment.setLatitude(locationCollection.getLatitude());
		    			appointment.setLongitude(locationCollection.getLongitude());
		    		}
		    	}
		    	response.add(appointment);
		    }
		}
	    } catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

	@Override
	@Transactional
    public List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, String from, String to, int page, int size, String updatedTime) {
		List<Appointment> response = null;
		List<AppointmentCollection> appointmentCollections = null;
		try {		
			
			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp));
		    if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(locationId);
		    
		    if(doctorId != null && !doctorId.isEmpty())criteria.and("doctorId").in(doctorId);
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(patientId);
		    
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	localCalendar.setTime(new Date(Long.parseLong(from)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime fromTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
			    
		    	criteria.and("fromDate").gte(fromTime);
		    }
		    else if(!DPDoctorUtils.anyStringEmpty(to)){
		    	localCalendar.setTime(new Date(Long.parseLong(to)));
			    int currentDay = localCalendar.get(Calendar.DATE);
			    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			    int currentYear = localCalendar.get(Calendar.YEAR);

			    DateTime toTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    	
		    	criteria.and("toDate").lte(toTime);
		    }

		    
		    Query query = new Query(criteria);
		    if(size > 0) appointmentCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "fromDate")), AppointmentCollection.class);
		    else appointmentCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "fromDate")), AppointmentCollection.class);
			
		    if (appointmentCollections != null) {
			    response = new ArrayList<Appointment>();   
			    for(AppointmentCollection collection : appointmentCollections){
			    	Appointment appointment = new Appointment();
			     	BeanUtil.map(collection, appointment);
			    	if(collection.getDoctorId() != null){
			    		UserCollection doctor = userRepository.findOne(collection.getDoctorId());
			    		if(doctor != null)appointment.setDoctorName(doctor.getFirstName());
			    	}
			    	if(collection.getLocationId() != null){
			    		LocationCollection locationCollection = locationRepository.findOne(collection.getLocationId());
			    		if(locationCollection != null){
			    			appointment.setLocationName(locationCollection.getLocationName());
			    			appointment.setClinicNumber(locationCollection.getClinicNumber());
			    			String address = 
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress()) ? locationCollection.getStreetAddress()+", ":"")+
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality()) ? locationCollection.getLocality()+", ":"")+
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCity()) ? locationCollection.getCity()+", ":"")+
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getState()) ? locationCollection.getState()+", ":"")+
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry()) ? locationCollection.getCountry()+", ":"")+
			    	    			(!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode()) ? locationCollection.getPostalCode():"");
			    	    	
			    		    if(address.charAt(address.length() - 2) == ','){
			    		    	address = address.substring(0, address.length() - 2);
			    		    }
			    		    appointment.setClinicAddress(address);
			    			appointment.setLatitude(locationCollection.getLatitude());
			    			appointment.setLongitude(locationCollection.getLongitude());
			    		}
			    	}
			    	response.add(appointment);
			    }
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
 }

	@Override
	@Transactional
	public Lab getLab(String locationId) {
		Lab response = new Lab();
		LocationCollection localtionCollection = null;
		Location location = new Location();
		HospitalCollection hospitalCollection = null;
		Hospital hospital = new Hospital();

		List<Doctor> doctors = new ArrayList<Doctor>();
		try {
		    localtionCollection = locationRepository.findOne(locationId);
		    if (localtionCollection == null) {
		    	return null;
		    } else if(!localtionCollection.getIsLab()){
		    	return null;
		    }else {
			BeanUtil.map(localtionCollection, location);
			response.setLocation(location);

			hospitalCollection = hospitalRepository.findOne(localtionCollection.getHospitalId());
			if (hospitalCollection != null) {
			    BeanUtil.map(hospitalCollection, hospital);
			    response.setHospital(hospital);
			}

			List<UserLocationCollection> userLocationCollections = userLocationRepository.findByLocationId(localtionCollection.getId());
			for (Iterator<UserLocationCollection> iterator = userLocationCollections.iterator(); iterator.hasNext();) {
			    UserLocationCollection userLocationCollection = iterator.next();
			    DoctorCollection doctorCollection = doctorRepository.findByUserId(userLocationCollection.getUserId());
			    UserCollection userCollection = userRepository.findOne(userLocationCollection.getUserId());
			    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection
				    .getId());

			    if (doctorCollection != null) {
				Doctor doctor = new Doctor();
				BeanUtil.map(doctorCollection, doctor);
				if (userCollection != null) {
				    BeanUtil.map(userCollection, doctor);
				}

				if (doctorClinicProfileCollection != null) {
				    DoctorClinicProfile doctorClinicProfile = new DoctorClinicProfile();
				    BeanUtil.map(doctorClinicProfileCollection, doctorClinicProfile);
				    doctorClinicProfile.setLocationId(userLocationCollection.getLocationId());
				    doctorClinicProfile.setDoctorId(userLocationCollection.getUserId());
				    doctor.setDoctorClinicProfile(doctorClinicProfile);
				}
				if(doctor.getSpecialities() != null && !doctor.getSpecialities().isEmpty()){
					List<String> specialities = (List<String>) CollectionUtils.collect((Collection<?>) specialityRepository.findAll(doctor.getSpecialities()),new BeanToPropertyValueTransformer("speciality"));
					doctor.setSpecialities(specialities);
				}
				doctors.add(doctor);
			    }
			}
			response.setDoctors(doctors);
			
			List<LabTestCollection> labTestCollections = labTestRepository.findByLocationId(localtionCollection.getId());
			List<LabTest> labTests = null;
			if(labTestCollections != null && !labTestCollections.isEmpty()){
				labTests = new ArrayList<LabTest>();
				for(LabTestCollection labTestCollection : labTestCollections){
					LabTest labTest = new LabTest();
					BeanUtil.map(labTestCollection, labTest);
					if(labTestCollection.getTestId() != null){
						DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findOne(labTestCollection.getTestId());
						if(diagnosticTestCollection != null){
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							labTest.setTest(diagnosticTest);
						}
					}
					labTests.add(labTest);
				}
				response.setLabTests(labTests);
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getCountries() {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("country").first("country").as("country"),
					Aggregation.project("country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "country"));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class, City.class);
			response = groupResults.getMappedResults();

		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<City> getStates(String country) {
		List<City> response = null;
		try {
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("state").first("state").as("state").first("country").as("country"),
					Aggregation.project("state","country").andExclude("_id"), Aggregation.sort(Sort.Direction.ASC, "state"));
			if(!DPDoctorUtils.anyStringEmpty(country))aggregation.match(Criteria.where("country").is(country));
			AggregationResults<City> groupResults = mongoTemplate.aggregate(aggregation, CityCollection.class, City.class);
			response = groupResults.getMappedResults();
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

    @Override
    @Transactional
    public SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	List<Slot> slotResponse = null;
	SlotDataResponse response = null;
	try {
		UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
	    if (doctorClinicProfileCollection != null) {
	    	
			SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
			sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
			String day = sdf.format(date);
		if (doctorClinicProfileCollection.getWorkingSchedules() != null && doctorClinicProfileCollection.getAppointmentSlot() != null) {
			response = new SlotDataResponse();
			response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
			slotResponse = new ArrayList<Slot>();
		    for(WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()){
		    	if(workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)){
		    		List<WorkingHours> workingHours = workingSchedule.getWorkingHours();
		    		if(workingHours != null && !workingHours.isEmpty()){
		    			for(WorkingHours workingHour : workingHours){
		    				if(workingHour.getFromTime() != null && workingHour.getToTime() != null && doctorClinicProfileCollection.getAppointmentSlot().getTime() > 0){
		    					List<Slot> slots = DateAndTimeUtility.sliceTime(workingHour.getFromTime(), workingHour.getToTime(), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    					if(slots != null)slotResponse.addAll(slots);
		    				}	    				
		    			}
		    		}  		
		    	}
		    }
		    List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository.findByDoctorLocationId(doctorId, locationId, date);
		    if(bookedSlots != null && !bookedSlots.isEmpty())
		    for(AppointmentBookedSlotCollection bookedSlot : bookedSlots){
		    	if(bookedSlot.getTime() != null){
		    		if(!bookedSlot.getFromDate().equals(bookedSlot.getToDate())){
		    			if(bookedSlot.getIsAllDayEvent()){
		    				if(bookedSlot.getFromDate().equals(date))bookedSlot.getTime().setToTime(719);
		    				if(bookedSlot.getToDate().equals(date))bookedSlot.getTime().setFromTime(0);
		    			}
		    		}
		    		List<Slot> slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(), bookedSlot.getTime().getToTime(), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    		for(Slot slot : slots){
		    			if(slotResponse.contains(slot)){
			    			slot.setIsAvailable(false);
			    			slotResponse.set(slotResponse.indexOf(slot), slot);
			    		}
		    		}
		    	}
		    }
		response.setSlots(slotResponse);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
	}
	return response;
    }

	@Override
	@Transactional
	public Appointment addEvent(EventRequest request) {
		Appointment response = null;
		try {
			UserCollection userCollection = userRepository.findOne(request.getDoctorId());
			
		    AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(request.getDoctorId(), request.getLocationId(), request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(), request.getToDate(), AppointmentState.CANCEL.getState());
			
		    if(userCollection != null){
				if (appointmentCollection == null || !request.getIsCalenderBlocked()) {
				    appointmentCollection = new AppointmentCollection();
				    BeanUtil.map(request, appointmentCollection);
				    appointmentCollection.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial()+DPDoctorUtils.generateRandomId());
				    appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setType(AppointmentType.EVENT);
					appointmentCollection.setCreatedTime(new Date());
					appointmentCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle()+" ":"") +userCollection.getFirstName());
				    appointmentCollection.setId(null);
				    appointmentCollection = appointmentRepository.save(appointmentCollection);
				    
				    if(request.getIsCalenderBlocked()){
				    	AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
					    BeanUtil.map(appointmentCollection, bookedSlotCollection);
					    bookedSlotCollection.setId(null);
					    bookedSlotCollection.setAppointmentId(appointmentCollection.getAppointmentId());
				    	appointmentBookedSlotRepository.save(bookedSlotCollection);
				    }
			    	
				    if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);
				    }
				} else {
				    logger.error(timeSlotIsBooked);
				    throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment updateEvent(EventRequest request) {
		Appointment response = null;
		try {
		    AppointmentCollection appointmentCollection = appointmentRepository.findOne(request.getId());
		    if (appointmentCollection != null) {
		    	AppointmentCollection appointmentCollectionToCheck = null;
		    	if(request.getState().equals(AppointmentState.RESCHEDULE)){
		    		appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(), request.getToDate(), AppointmentState.CANCEL.getState());
		    		if(appointmentCollectionToCheck != null)
		    			if(!request.getIsCalenderBlocked()) appointmentCollectionToCheck = null;
		    	}		    		
			if (appointmentCollectionToCheck == null) {			
				AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
				BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
				appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);
				
				appointmentCollection.setState(request.getState());
				
				if(request.getState().equals(AppointmentState.CANCEL)){
			    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(appointmentCollection.getAppointmentId());
			    	if(bookedSlotCollection != null) appointmentBookedSlotRepository.delete(bookedSlotCollection);
			    }
			    else {
			    	appointmentCollection.setFromDate(request.getFromDate());
			    	appointmentCollection.setToDate(request.getToDate());
				    appointmentCollection.setTime(request.getTime());
				    appointmentCollection.setIsCalenderBlocked(request.getIsCalenderBlocked());
				    appointmentCollection.setExplanation(request.getExplanation());
			    	if(request.getState().equals(AppointmentState.RESCHEDULE)){
				    	appointmentCollection.setIsRescheduled(true);
				    	appointmentCollection.setState(AppointmentState.CONFIRM);
				    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(appointmentCollection.getAppointmentId());
		    			
				    	if(request.getIsCalenderBlocked()){
				    		if(bookedSlotCollection != null) {
				    			bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
				    			bookedSlotCollection.setToDate(appointmentCollection.getToDate());
							    bookedSlotCollection.setTime(appointmentCollection.getTime());
							    bookedSlotCollection.setUpdatedTime(new Date());
					    		appointmentBookedSlotRepository.save(bookedSlotCollection);	
					    	}
			    		}else{
			    			if(bookedSlotCollection != null) appointmentBookedSlotRepository.delete(bookedSlotCollection);
			    		}
				      }
			    }
			    appointmentCollection.setUpdatedTime(new Date());
			    appointmentCollection = appointmentRepository.save(appointmentCollection);
			    response = new Appointment();
			    BeanUtil.map(appointmentCollection, response);
			} else {
			    logger.error(timeSlotIsBooked);
			    throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
			}
		    } else {
			logger.error("Incorrect Id");
			throw new BusinessException(ServiceError.InvalidInput, "Incorrect Id");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean sendReminderToPatient(String appointmentId) {
		Boolean response = false;
		try {
			AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(appointmentId);
			if(appointmentCollection != null){
				if(appointmentCollection.getPatientId() != null){
					UserCollection userCollection = userRepository.findOne(appointmentCollection.getDoctorId());
					UserCollection patient = userRepository.findOne(appointmentCollection.getPatientId());
					LocationCollection locationCollection = locationRepository.findOne(appointmentCollection.getLocationId());
					if(userCollection != null && locationCollection != null && patient != null){
						UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(appointmentCollection.getDoctorId(), appointmentCollection.getLocationId());
				        DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
				    	
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
						
					    String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60, appointmentCollection.getTime().getFromTime() % 60);
				        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				        if(clinicProfileCollection != null){
				        	sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				        	_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				        	_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				        }
						else{
							sdf.setTimeZone(TimeZone.getTimeZone("IST"));
							_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
							_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						}
						
				        Date _24HourDt = _24HourSDF.parse(_24HourTime);
						String patientName=patient.getFirstName(),  
								dateTime= _12HourSDF.format(_24HourDt)+", "+sdf.format(appointmentCollection.getFromDate()),
								doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getClinicNumber() != null ? locationCollection.getClinicNumber() :"";
						sendMsg(SMSFormatType.APPOINTMENT_REMINDER.getType(), "APPOINTMENT_REMINDER_TO_PATIENT", appointmentCollection.getDoctorId(),appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
						response = true;
					}
				}
			}else{
				logger.error(appointmentDoesNotExist);
			    throw new BusinessException(ServiceError.InvalidInput, appointmentDoesNotExist);
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request) {
		List<PatientQueue> response = null;
		try{
			response = updateQueue(null, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId(), new Date(), null, null, true);
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error while adding patient In Queue");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId, String patientId, String appointmentId, int sequenceNo) {
		List<PatientQueue> response = null;
		try{
			response = updateQueue(appointmentId, doctorId, locationId, hospitalId, patientId, new Date(), null, sequenceNo, true);
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error while rearranging patient In Queue");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientQueue> getPatientQueue(String doctorId, String locationId, String hospitalId) {
		List<PatientQueue> response = null;
		try{
		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		    localCalendar.setTime(new Date());
		    int currentDay = localCalendar.get(Calendar.DATE);
		    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		    int currentYear = localCalendar.get(Calendar.YEAR);

		    DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
	    	DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    
	    	List<PatientQueueCollection> patientQueueCollections = patientQueueRepository.find(doctorId, locationId, hospitalId, start, end, false, new Sort(Direction.DESC,"sequenceNo"));
	    	if(patientQueueCollections != null){
	    		response = new ArrayList<PatientQueue>();
	    		for(PatientQueueCollection collection : patientQueueCollections){
	    			PatientQueue patientQueue = new PatientQueue();
	    			BeanUtil.map(collection, patientQueue);
	    			PatientCard patientCard = new PatientCard();
	    			UserCollection userCollection = userRepository.findOne(collection.getPatientId());
	    			if(userCollection!=null)BeanUtil.map(userCollection, patientCard);
	    			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), doctorId, locationId, hospitalId);
	    			if(patientCollection!=null)BeanUtil.map(patientCollection, patientCard);
	    			patientCard.setId(collection.getPatientId());
	    			patientQueue.setPatient(patientCard);
	    			response.add(patientQueue);
	    		}
	    	}
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error while rearranging patient In Queue");
		}
		return response;
	}
	
	private List<PatientQueue> updateQueue(String appointmentId, String doctorId, String locationId, String hospitalId, String patientId, Date date, Integer startTime, Integer sequenceNo, Boolean isPatientDetailRequire) {
		List<PatientQueue> response = null;
		List<PatientQueueCollection> patientQueueCollections = null; 
		try{
    		PatientQueueCollection patientQueueCollection = new PatientQueueCollection();
    		patientQueueCollection.setAppointmentId(appointmentId);
    		patientQueueCollection.setDoctorId(doctorId);
    		patientQueueCollection.setLocationId(locationId);
    		patientQueueCollection.setHospitalId(hospitalId);
    		patientQueueCollection.setPatientId(patientId);
    		patientQueueCollection.setDate(date);
    		patientQueueCollection.setStartTime(startTime);

		    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		    localCalendar.setTime(date);
		    int currentDay = localCalendar.get(Calendar.DATE);
		    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		    int currentYear = localCalendar.get(Calendar.YEAR);

		    DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0);
	    	DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59);
		    
	    	patientQueueCollections = patientQueueRepository.find(doctorId, locationId, hospitalId, start, end, false, new Sort(Direction.DESC,"sequenceNo"));
	    	if(startTime != null){
		    	if(patientQueueCollections == null || patientQueueCollections.isEmpty()){
		    		patientQueueCollection.setSequenceNo(1);
		    		patientQueueRepository.save(patientQueueCollection);
		    	}else{
		    		for(PatientQueueCollection queueCollection : patientQueueCollections){
		    			int seq = queueCollection.getSequenceNo();
		    			if(queueCollection.getStartTime()>startTime){
		    				queueCollection.setSequenceNo(seq+1);
		    				patientQueueRepository.save(queueCollection);
		    			}else{
		    				patientQueueCollection.setSequenceNo(seq+1);
		    				patientQueueRepository.save(patientQueueCollection);
		    				break;
		    			}
		    		}
		    	}
	    	}else if(sequenceNo != null){
	    			if(sequenceNo == 0){
	    				for(PatientQueueCollection queueCollection : patientQueueCollections){
			    				int seq = queueCollection.getSequenceNo();
			    				if(appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())){
			    					queueCollection.setDiscarded(true);
					    			patientQueueRepository.save(queueCollection);
					    			break;
			    				}else{
			    					queueCollection.setSequenceNo(seq-1);
					    			patientQueueRepository.save(queueCollection);
			    				}
				    			
			    		}
	    			}else{
	    				Integer toCheck = patientQueueRepository.find(appointmentId, doctorId, locationId, hospitalId, patientId, start, end, sequenceNo, false);
	    				if(toCheck == null || toCheck == 0){
	    					PatientQueueCollection temp = null;
	    					int oldSeqNum = 0; int newStartTime = 0;
	    					for(PatientQueueCollection queueCollection : patientQueueCollections){
				    			if(appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())){
				    				oldSeqNum = queueCollection.getSequenceNo();
				    			}
				    			if(oldSeqNum > 0)break;
				    		}
	    					for(PatientQueueCollection queueCollection : patientQueueCollections){
				    			if(oldSeqNum < sequenceNo){
				    				if(queueCollection.getSequenceNo() >= oldSeqNum && queueCollection.getSequenceNo() <= sequenceNo){
				    					if(oldSeqNum == queueCollection.getSequenceNo()){
				    						queueCollection.setStartTime(newStartTime+1);
				    						queueCollection.setSequenceNo(sequenceNo);
						    				patientQueueRepository.save(queueCollection);
				    					
				    				}else{
				    					queueCollection.setSequenceNo(queueCollection.getSequenceNo()-1);
					    				patientQueueRepository.save(queueCollection);
				    				}
				    				}
				    				newStartTime = queueCollection.getStartTime();
				    			}else if(oldSeqNum > sequenceNo){
				    				if(queueCollection.getSequenceNo() <= oldSeqNum && queueCollection.getSequenceNo() >= sequenceNo){
				    					if(oldSeqNum == queueCollection.getSequenceNo()){
				    						queueCollection.setSequenceNo(sequenceNo);
				    						temp = new PatientQueueCollection();
						    				BeanUtil.map(queueCollection, temp);
				    					}else{
				    						queueCollection.setSequenceNo(queueCollection.getSequenceNo()+1);
				    						patientQueueRepository.save(queueCollection);
				    					}
				    				}
				    				newStartTime = queueCollection.getStartTime();
				    			}
				    		}
	    					if(temp != null){
	    						temp.setStartTime(newStartTime+1);
			    				patientQueueRepository.save(temp);
	    					}
	    				}
	    				
	    			}
	    	}else{
	    			patientQueueCollection.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial()+DPDoctorUtils.generateRandomId());
	    			if(patientQueueCollections == null || patientQueueCollections.isEmpty()){
			    		patientQueueCollection.setSequenceNo(1);
			    		patientQueueCollection.setStartTime(0);
			    	}
	    			else{
	    				for(PatientQueueCollection queueCollection : patientQueueCollections){
			    			int seq = queueCollection.getSequenceNo();
			    			patientQueueCollection.setSequenceNo(seq+1);
			    			patientQueueCollection.setStartTime(queueCollection.getStartTime()+1);
			    			break;
			    		}
	    			}
	    			patientQueueRepository.save(patientQueueCollection);
	    	}
	    	
	    	patientQueueCollections = patientQueueRepository.find(doctorId, locationId, hospitalId, start, end, false, new Sort(Direction.ASC,"sequenceNo"));
	    	if(patientQueueCollections != null){
	    		response = new ArrayList<PatientQueue>();
	    		if(isPatientDetailRequire){
	    			for(PatientQueueCollection collection : patientQueueCollections){
		    			PatientQueue patientQueue = new PatientQueue();
		    			BeanUtil.map(collection, patientQueue);
		    			PatientCard patientCard = new PatientCard();
		    			UserCollection userCollection = userRepository.findOne(collection.getPatientId());
		    			if(userCollection!=null)BeanUtil.map(userCollection, patientCard);
		    			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(collection.getPatientId(), doctorId, locationId, hospitalId);
		    			if(patientCollection!=null)BeanUtil.map(patientCollection, patientCard);
		    			patientQueue.setPatient(patientCard);
		    			patientCard.setId(collection.getPatientId());
		    			response.add(patientQueue);
		    		}
	    		}else{
	    			BeanUtil.map(patientQueueCollections, response);
	    		}	    		
	    	}
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}
