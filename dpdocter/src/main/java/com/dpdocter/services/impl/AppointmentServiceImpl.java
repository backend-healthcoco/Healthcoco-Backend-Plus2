package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.solr.core.geo.GeoLocation;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
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
import com.dpdocter.collections.CountryCollection;
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
import com.dpdocter.collections.StateCollection;
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
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientQueueRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.StateRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientQueueAddEditRequest;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.beans.State;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrStateDocument;
import com.dpdocter.solr.services.SolrCityService;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private CountryRepository countryRepository;

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
    private SolrCityService solrCityService;

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

    @Override
    public Country addCountry(Country country) {
	try {
	    CountryCollection countryCollection = new CountryCollection();
	    BeanUtil.map(country, countryCollection);
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(country.getCountry());
	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), countryCollection);
	    countryCollection = countryRepository.save(countryCollection);
	    BeanUtil.map(countryCollection, country);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return country;
    }

    @Override
    public City addCity(City city) {
	try {
	    CityCollection cityCollection = new CityCollection();
	    BeanUtil.map(city, cityCollection);
	    StateCollection stateCollection = stateRepository.findOne(city.getStateId());
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(city.getCity() + " "
		    + (stateCollection != null ? stateCollection.getState() : ""));

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), cityCollection);

	    cityCollection = cityRepository.save(cityCollection);
	    BeanUtil.map(cityCollection, city);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return city;
    }

    @Override
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
    public List<City> getCities(String stateId) {
	List<City> response = new ArrayList<City>();
	try {
	    List<CityCollection> cities = null;
	    if(DPDoctorUtils.allStringsEmpty(stateId))cities = cityRepository.findAll();
	    else cities = cityRepository.findAll(stateId);
	    if (cities != null) {
		BeanUtil.map(cities, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public City getCity(String cityId) {
	City response = new City();
	try {
	    CityCollection city = cityRepository.findOne(cityId);
	    if (city != null) {
		BeanUtil.map(city, response);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public LandmarkLocality addLandmaklLocality(LandmarkLocality landmarkLocality) {
	CityCollection cityCollection = null;
	CountryCollection countryCollection = null;
	StateCollection stateCollection = null;
	try {
	    LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
	    BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
	    if (landmarkLocality.getCityId() != null) {
		cityCollection = cityRepository.findOne(landmarkLocality.getCityId());
	    }
	    if (cityCollection != null) {
	    	stateCollection = stateRepository.findOne(cityCollection.getStateId());
		}
	    if (stateCollection != null) {
		countryCollection = countryRepository.findOne(stateCollection.getCountryId());
	    }
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(landmarkLocality.getLandmark() != null ? landmarkLocality.getLandmark()
		    + " " : "" + landmarkLocality.getLocality() != null ? landmarkLocality.getLocality() + " "
		    : "" + cityCollection.getCity() != null ? cityCollection.getCity() + " " : "" + 
		    stateCollection != null ? stateCollection.getState(): "" +
		    countryCollection != null ? countryCollection.getCountry(): "");

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), landmarkLocalityCollection);

	    landmarkLocalityCollection = landmarkLocalityRepository.save(landmarkLocalityCollection);
	    BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return landmarkLocality;
    }

    @Override
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
		    DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
			    .findByLocationId(userLocationCollection.getId());

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
			doctors.add(doctor);
		    }
		}
		response.setDoctors(doctors);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public Appointment updateAppointment(AppointmentRequest request) {
	Appointment response = null;
	try {
	    UserCollection userCollection = userRepository.findOne(request.getDoctorId());
	    LocationCollection locationCollection = locationRepository.findOne(request.getLocationId());
	    UserCollection patient = userRepository.findOne(request.getPatientId());
	    AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(request.getAppointmentId());
	    if (userCollection != null && locationCollection != null && patient != null) {
		if (appointmentCollection != null) {
		    AppointmentCollection appointmentCollectionToCheck = null;
		    if (request.getState().equals(AppointmentState.RESCHEDULE))
			appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(appointmentCollection.getDoctorId(),
				appointmentCollection.getLocationId(), request.getTime().getFrom(), request.getTime().getTo(), request.getDate(),
				AppointmentState.CANCEL.getState());
		    if (appointmentCollectionToCheck == null) {
			AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
			BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
			appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

			appointmentCollection.setState(request.getState());
			
			String patientName=patient.getFirstName() != null?patient.getFirstName().split(" ")[0] :"", appointmentId= appointmentCollection.getAppointmentId(), 
					dateTime= String.format("%02d:%02d", appointmentCollection.getTime().getFrom() / 60, appointmentCollection.getTime().getFrom() % 60)+" "+new SimpleDateFormat("MMM dd,yyyy").format(appointmentCollection.getDate()),
					doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getLocationPhoneNumber() != null ? locationCollection.getLocationPhoneNumber() :"";
					
			if(request.getState().getState().equals(AppointmentState.CANCEL.getState())){
		    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(request.getAppointmentId());
		    	if(bookedSlotCollection != null) appointmentBookedSlotRepository.delete(bookedSlotCollection);
		    }
		    else {
		    	appointmentCollection.setDate(request.getDate());
			    appointmentCollection.setTime(request.getTime());
			    
		    	if(request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())){
			    	appointmentCollection.setIsReschduled(true);
			    	appointmentCollection.setState(AppointmentState.NEW);
			    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(request.getAppointmentId());
			    	if(bookedSlotCollection != null) {
			    		bookedSlotCollection.setDate(appointmentCollection.getDate());
					    bookedSlotCollection.setTime(appointmentCollection.getTime());
					    bookedSlotCollection.setUpdatedTime(new Date());
			    		appointmentBookedSlotRepository.save(bookedSlotCollection);
			    	}
			    }
		    }
		    
		    appointmentCollection.setUpdatedTime(new Date());
		    appointmentCollection = appointmentRepository.save(appointmentCollection);
		  //sendSMS after appointment is saved	
	    	if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)){
		    	if(request.getNotifyDoctorByEmail());
		    	if(request.getNotifyDoctorBySms()){
		    		if(appointmentCollection.getState().getState().equals(AppointmentState.CANCEL.getState()))
		    			sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR",request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
		    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    		else if(appointmentCollection.getState().getState().equals(AppointmentState.CONFIRM.getState()))
		    			sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    		else
		    			sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    	}
		    	if(request.getNotifyPatientByEmail())System.out.println("send email to patient");
		    	if(request.getNotifyPatientBySms()){
		    		if(appointmentCollection.getState().getState().equals(AppointmentState.CANCEL.getState()))
		    			sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
		    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    		else if(appointmentCollection.getState().getState().equals(AppointmentState.CONFIRM.getState()))
		    			sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    		else
		    			sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    	}
		    }
		    else{
		    	if(request.getState().getState().equals(AppointmentState.CANCEL.getState())){
		    		sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
		    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    		sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
		    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
		    	}
		    }
		    response = new Appointment();
		    BeanUtil.map(appointmentCollection, response);
		    
		    if(appointmentCollection.getState().getState().equalsIgnoreCase(AppointmentState.CONFIRM.getState())){
		    	updateQueue(appointmentCollection.getAppointmentId(), appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), appointmentCollection.getDate(), appointmentCollection.getTime().getFrom(), null, false);
		    }
		} else {
		    logger.error("Time slot is already booked");
		    throw new BusinessException(ServiceError.NotAcceptable, "Time slot is already booked");
		}
	    } else {
		logger.error("Incorrect appointment Id");
		throw new BusinessException(ServiceError.InvalidInput, "Incorrect appointment Id");
	    }
	}
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

    @Override
    public Appointment addAppointment(AppointmentRequest request) {
	Appointment response = null;
	UserLocationCollection userLocationCollection = null;
	DoctorClinicProfileCollection clinicProfileCollection = null;
	try {
		UserCollection userCollection = userRepository.findOne(request.getDoctorId());
		LocationCollection locationCollection = locationRepository.findOne(request.getLocationId());
		UserCollection patient = userRepository.findOne(request.getPatientId());
	    AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(request.getDoctorId(), request.getLocationId(), request.getTime().getFrom(), request.getTime().getTo(), request.getDate(), AppointmentState.CANCEL.getState());
		if(userCollection != null && locationCollection != null && patient != null){
			
			if(appointmentCollection == null) {			
			    appointmentCollection = new AppointmentCollection();
			    BeanUtil.map(request, appointmentCollection);
			    appointmentCollection.setCreatedTime(new Date());
			    appointmentCollection.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial()+DPDoctorUtils.generateRandomId());
			    
			    String patientName = patient.getFirstName() != null?patient.getFirstName().split(" ")[0] :"", appointmentId= appointmentCollection.getAppointmentId(), 
						dateTime= String.format("%02d:%02d", appointmentCollection.getTime().getFrom() / 60, appointmentCollection.getTime().getFrom() % 60)+" "+new SimpleDateFormat("MMM dd,yyyy").format(appointmentCollection.getDate()),
						doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getLocationPhoneNumber() != null ? locationCollection.getLocationPhoneNumber() :"";
						
			    if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)){
			    	appointmentCollection.setState(AppointmentState.CONFIRM);
			    }
			    else{
			    	userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
			        clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
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
			    	if(request.getNotifyDoctorByEmail()!=null && request.getNotifyDoctorByEmail());
			    	if(request.getNotifyDoctorBySms()!=null && request.getNotifyDoctorBySms()){
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    	if(request.getNotifyPatientByEmail()!=null && request.getNotifyPatientByEmail())System.out.println("send email to patient");
			    	if(request.getNotifyPatientBySms()!=null && request.getNotifyPatientBySms()){
			    		sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(),"CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    }
			    else{
			    	if(clinicProfileCollection != null && clinicProfileCollection.getFacility() != null && (clinicProfileCollection.getFacility().getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))){
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}else{
			    		sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getDoctorId(), 
			    				userCollection.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    		sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(),"TENTATIVE_APPOINTMENT_TO_PATIENT", request.getDoctorId(),request.getLocationId(), request.getHospitalId(), request.getPatientId(), 
			    				patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
			    	}
			    }
			    if (appointmentCollection != null) {
				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
			    }
			    
			    if(appointmentCollection.getState().getState().equalsIgnoreCase(AppointmentState.CONFIRM.getState())){
			    	updateQueue(appointmentCollection.getAppointmentId(), appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), appointmentCollection.getDate(), appointmentCollection.getTime().getFrom(),null, false);
			    }
			} else {
			    logger.error("Time slot is already booked");
			    throw new BusinessException(ServiceError.NotAcceptable, "Time slot is already booked");
			}
		}
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
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
	    
	    if(smsFormatCollection != null){
	    		if(smsFormatCollection.getContent().contains(SMSContent.PATIENT_NAME.getContent()) && patientName != null && patientName != "")
	    			 patientName = " "+patientName;
	    		 else patientName = "";
	    		 if(smsFormatCollection.getContent().contains(SMSContent.APPOINTMENT_ID.getContent()) && appointmentId != null && appointmentId != "")
	    			 appointmentId = " "+appointmentId;
	    		 else appointmentId= "";
	    		 if(smsFormatCollection.getContent().contains(SMSContent.DATE_TIME.getContent()) && dateTime != null && dateTime != "")
	    			 dateTime = " "+dateTime;
	    		 else dateTime ="";
	    		 if(smsFormatCollection.getContent().contains(SMSContent.DOCTOR_NAME.getContent()) && doctorName != null && doctorName != "")
	    			 doctorName = " "+doctorName;
	    		 else doctorName ="";
	    		 if(smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent()) && clinicName != null && clinicName != "");
	    		 else clinicName ="";
	    		 if(smsFormatCollection.getContent().equals(SMSContent.CLINIC_CONTACT_NUMBER.getContent()) && clinicContactNum != null && clinicContactNum != "")
	    			 clinicContactNum = " "+ clinicContactNum;
	    		 else clinicContactNum = "";
	    	}
	    else{
	    	if(patientName != null && patientName != "")patientName = " "+patientName;
	    	if(appointmentId != null && appointmentId != "")appointmentId = " "+appointmentId;
	    	if(dateTime != null && dateTime != "")dateTime = " "+dateTime;
	    	if(doctorName != null && doctorName != "")doctorName = " "+doctorName;
	    	if(clinicContactNum != null && clinicContactNum != "")clinicContactNum = " "+ clinicContactNum;
	    }
	    String text = "";
	    switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT" :{
			text = "Hi"+patientName+",your appointment"+appointmentId+" has been confirmed"+(dateTime!= ""?" for"+dateTime:"")+(doctorName!= ""?" with"+doctorName:"")+ (clinicName!= ""?","+clinicName:"")+(clinicContactNum!= ""?","+clinicContactNum:"");
			smsDetail.setUserName(patientName);
		}
		break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR" :{
			text = "Your appointment with"+patientName+" has been scheduled"+(dateTime!= ""?" for"+dateTime:"")+(clinicName!= ""?" at "+clinicName:"");
			smsDetail.setUserName(doctorName);
		}
		break;
		
		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR" :{
			text = "Healthcoco,You have an appointment request" +(patientName!=""?" from"+patientName :"")+ (dateTime!= ""?" for"+dateTime:"")+(clinicName!= ""?" at "+clinicName:"");
			smsDetail.setUserName(doctorName);
		}
		break;
		
		case "TENTATIVE_APPOINTMENT_TO_PATIENT" :{
			text = "Hi"+patientName+",your appointment"+appointmentId+(dateTime!= ""?" for"+dateTime:"")+(doctorName!= ""?" with"+doctorName:"")+(clinicName!= ""?","+clinicName:"")+(clinicContactNum!= ""?","+clinicContactNum:"")+" has been sent for confirmation";
			smsDetail.setUserName(patientName);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR" :{
			text = "Healthcoco,your appointment"+(patientName !=""?" with"+patientName:"")+(dateTime!= ""?" for"+dateTime:"") +(clinicName!= ""?" at "+clinicName:"")+" has been cancelled";
			smsDetail.setUserName(doctorName);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR" :{
			text = "Hi"+patientName+",your appointment"+appointmentId+(dateTime!= ""?" for"+dateTime:"")+(doctorName!= ""?" with"+doctorName:"")+(clinicName!= ""?","+clinicName:"")+(clinicContactNum!= ""?","+clinicContactNum:"")+" has been cancelled";
			smsDetail.setUserName(patientName);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT" :{
			text = "Healthcoco,your appointment"+(patientName !=""?" with"+patientName:"")+(dateTime!= ""?" for"+dateTime:"") +(clinicName!= ""?" at "+clinicName:"")+", has been cancelled by Patient";
			smsDetail.setUserName(doctorName);
		}
		break;
		
		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT" :{
			text = "Hi"+patientName+",your appointment"+appointmentId+(dateTime!= ""?" for"+dateTime:"")+(doctorName!= ""?" with"+doctorName:"")+", has been cancelled as per your request";
			smsDetail.setUserName(patientName);
		}
		break;
		
		case "APPOINTMENT_REMINDER_TO_PATIENT" :{
			text = "Hi"+patientName+",you have an upcoming appointment"+appointmentId+dateTime+doctorName+clinicName+clinicContactNum;
			smsDetail.setUserName(patientName);
		}
		break;
		
		case "RESCHEDULE_APPOINTMENT_TO_PATIENT" :{
			text = "Hi"+patientName+",your appointment"+appointmentId+(doctorName!= ""?" with"+doctorName:"")+clinicName+clinicContactNum+" has been rescheduled"+(dateTime != ""?" to "+dateTime:"");
			smsDetail.setUserName(patientName);
		}
		break;
		
		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR" :{
			text = "Your appointment with"+patientName+"has been rescheduled to "+dateTime+" at "+clinicName;
			smsDetail.setUserName(doctorName);
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
    public List<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from, String to, int page, int size) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	try {		
		Query query = new Query();
		
		Criteria criteria = new Criteria();
	    if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(locationId);
	    
	    if(doctorId != null && !doctorId.isEmpty())criteria.and("doctorId").in(doctorId);
	    
	    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(patientId);
	    
	    if(!DPDoctorUtils.anyStringEmpty(from,to)){
	    	long fromLong = Long.parseLong(from);
	    	long toLong = Long.parseLong(to);
	    	criteria.and("date").gte(new Date(fromLong)).lte(toLong);
	    }
	    else if(!DPDoctorUtils.anyStringEmpty(from)){
	    	long fromLong = Long.parseLong(from);
	    	criteria.and("date").gte(new Date(fromLong));
	    }
	    else if(!DPDoctorUtils.anyStringEmpty(to)){
	    	long toLong = Long.parseLong(to);
	    	criteria.and("date").lte(new Date(toLong));
	    }
	    
	    query.addCriteria(criteria);
	    if(size > 0) appointmentCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.ASC, "date","time.from")), AppointmentCollection.class);
	    else appointmentCollections = mongoTemplate.find(query.with(new Sort(Direction.ASC, "date","time.from")), AppointmentCollection.class);
		
	    if (appointmentCollections != null) {
		    response = new ArrayList<Appointment>();
		    
		    for(AppointmentCollection collection : appointmentCollections){
		    	Appointment appointment = new Appointment();
		    	PatientCard patient = null;
		    	if(collection.getType().equals(AppointmentType.APPOINTMENT)){
		    		UserCollection userCollection = userRepository.findOne(collection.getPatientId());
		    		patient = new PatientCard();
			    	BeanUtil.map(userCollection, patient);
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
		    			appointment.setLocationPhoneNumber(locationCollection.getLocationPhoneNumber());
		    			String address = locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress() + ", "
		    					: "" + locationCollection.getCity() != null ? locationCollection.getCity() + ", "
		    						: "" + locationCollection.getState() != null ? locationCollection.getState()
		    							: "" + locationCollection.getState() != null && locationCollection.getPostalCode() != null ? " - "
		    								: "" + locationCollection.getPostalCode() != null ? locationCollection.getPostalCode() + ", "
		    									: "" + locationCollection.getState() != null && locationCollection.getPostalCode() == null
		    										? ", "
		    										: "" + locationCollection.getCountry() != null ? locationCollection.getCountry() : "";

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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return response;
    }

	@Override
    public List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, String from, String to, int page, int size) {
		List<Appointment> response = null;
		List<AppointmentCollection> appointmentCollections = null;
		try {		
			Query query = new Query();
			
			Criteria criteria = new Criteria();
		    if (!DPDoctorUtils.anyStringEmpty(locationId))criteria.and("locationId").is(locationId);
		    
		    if(doctorId != null && !doctorId.isEmpty())criteria.and("doctorId").in(doctorId);
		    
		    if(!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(patientId);
		    
		    if(!DPDoctorUtils.anyStringEmpty(from)){
		    	long fromLong = Long.parseLong(from);
		    	criteria.and("date").gte(new Date(fromLong));
		    }
		    if(!DPDoctorUtils.anyStringEmpty(to)){
		    	long toLong = Long.parseLong(to);
		    	criteria.and("date").lte(new Date(toLong));
		    }
		    
		    query.addCriteria(criteria);
		    if(size > 0) appointmentCollections = mongoTemplate.find(query.with(new PageRequest(page, size, Direction.DESC, "date")), AppointmentCollection.class);
		    else appointmentCollections = mongoTemplate.find(query.with(new Sort(Direction.DESC, "date")), AppointmentCollection.class);
			
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
			    			appointment.setLocationPhoneNumber(locationCollection.getLocationPhoneNumber());
			    			String address = locationCollection.getStreetAddress() != null ? locationCollection.getStreetAddress() + ", "
			    					: "" + locationCollection.getCity() != null ? locationCollection.getCity() + ", "
			    						: "" + locationCollection.getState() != null ? locationCollection.getState()
			    							: "" + locationCollection.getState() != null && locationCollection.getPostalCode() != null ? " - "
			    								: "" + locationCollection.getPostalCode() != null ? locationCollection.getPostalCode() + ", "
			    									: "" + locationCollection.getState() != null && locationCollection.getPostalCode() == null
			    										? ", "
			    										: "" + locationCollection.getCountry() != null ? locationCollection.getCountry() : "";

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
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
 }

	@Override
	public State addState(State state) {
		try {
		    StateCollection stateCollection = new StateCollection();
		    BeanUtil.map(state, stateCollection);
		    CountryCollection countryCollection = countryRepository.findOne(state.getCountryId());
		    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(state.getState() + " "
			    + (countryCollection != null ? countryCollection.getCountry() : ""));

		    if (geocodedLocations != null && !geocodedLocations.isEmpty())
			BeanUtil.map(geocodedLocations.get(0), stateCollection);

		    stateCollection = stateRepository.save(stateCollection);
		    BeanUtil.map(stateCollection, state);
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return state;
	}

	@Override
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
				    .getLocationId());

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
				doctors.add(doctor);
			    }
			}
			response.setDoctors(doctors);
			
			List<LabTestCollection> labTestCollections = labTestRepository.findByLocationId(localtionCollection.getId());
			if(labTestCollections != null){
				List<LabTest> labTests = new ArrayList<LabTest>();
				BeanUtil.map(labTestCollections, labTests);
				response.setLabTests(labTests);
			}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Override
	public List<Country> getCountries() {
		List<Country> response = new ArrayList<Country>();
		try {
		    List<CountryCollection> countries = countryRepository.findAll();
		    if (countries != null) {
		    	BeanUtil.map(countries, response);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Override
	public List<State> getStates(String countryId) {
		List<State> response = new ArrayList<State>();
		try {
		    List<StateCollection> states = null;
		    if(DPDoctorUtils.anyStringEmpty(countryId))states = stateRepository.findAll();
		    else states = stateRepository.findAll(countryId);
		    if (states != null) {
		    	BeanUtil.map(states, response);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

    @Override
    public List<Slot> getTimeSlots(String doctorId, String locationId, Date date) {
	DoctorClinicProfileCollection doctorClinicProfileCollection = null;
	List<Slot> response = null;
	try {
		String day = new SimpleDateFormat("EEEEE").format(date);
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
	    doctorClinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
	    if (doctorClinicProfileCollection != null) {
		if (doctorClinicProfileCollection.getWorkingSchedules() != null && doctorClinicProfileCollection.getAppointmentSlot() != null) {
		    response = new ArrayList<Slot>();
		    for(WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()){
		    	if(workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)){
		    		List<WorkingHours> workingHours = workingSchedule.getWorkingHours();
		    		if(workingHours != null && !workingHours.isEmpty()){
		    			for(WorkingHours workingHour : workingHours){
		    				if(workingHour.getFrom() != null && workingHour.getTo() != null && doctorClinicProfileCollection.getAppointmentSlot().getTime() > 0){
		    					List<Slot> slots = DateAndTimeUtility.sliceTime(workingHour.getFrom(), workingHour.getTo(), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    					if(slots != null)response.addAll(slots);
		    				}	    				
		    			}
		    		}  		
		    	}
		    }
		    List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository.findByDoctorLocationId(doctorId, locationId, date);
		    if(bookedSlots != null && !bookedSlots.isEmpty())
		    for(AppointmentBookedSlotCollection bookedSlot : bookedSlots){
		    	if(bookedSlot.getTime() != null){
		    		List<Slot> slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFrom(), bookedSlot.getTime().getTo(), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    		for(Slot slot : slots){
		    			if(response.contains(slot)){
			    			slot.setIsAvailable(false);
			    			response.set(response.indexOf(slot), slot);
			    		}
		    		}
		    	}
		    }
		}
	  }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Forbidden, "Error whie getting time slots");
	}
	return response;
    }

	@Override
	public Appointment addEvent(EventRequest request) {
		Appointment response = null;
		try {
			UserCollection userCollection = userRepository.findOne(request.getDoctorId());
		    AppointmentCollection appointmentCollection = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(request.getDoctorId(), request.getLocationId(), request.getTime().getFrom(), request.getTime().getTo(), request.getDate(), AppointmentState.CANCEL.getState());
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
				    logger.error("Event cannot be added as appointment is already book");
				    throw new BusinessException(ServiceError.NotAcceptable, "Event cannot be added as appointment is already book");
				}
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Override
	public Appointment updateEvent(EventRequest request) {
		Appointment response = null;
		try {
		    AppointmentCollection appointmentCollection = appointmentRepository.findOne(request.getId());
		    if (appointmentCollection != null) {
		    	AppointmentCollection appointmentCollectionToCheck = null;
		    	if(request.getState().equals(AppointmentState.RESCHEDULE)){
		    		appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(), request.getTime().getFrom(), request.getTime().getTo(), request.getDate(), AppointmentState.CANCEL.getState());
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
			    	appointmentCollection.setDate(request.getDate());
				    appointmentCollection.setTime(request.getTime());
				    
			    	if(request.getState().equals(AppointmentState.RESCHEDULE)){
				    	appointmentCollection.setIsReschduled(true);
				    	appointmentCollection.setState(AppointmentState.CONFIRM);
				    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(appointmentCollection.getAppointmentId());
				    	if(bookedSlotCollection != null) {
				    		if(appointmentCollection.getIsCalenderBlocked()){
				    			bookedSlotCollection.setDate(appointmentCollection.getDate());
							    bookedSlotCollection.setTime(appointmentCollection.getTime());
							    bookedSlotCollection.setUpdatedTime(new Date());
					    		appointmentBookedSlotRepository.save(bookedSlotCollection);
				    		}else{
				    			appointmentBookedSlotRepository.delete(bookedSlotCollection);
				    		}
				    	}
				    }
			    }
			    appointmentCollection.setUpdatedTime(new Date());
			    appointmentCollection = appointmentRepository.save(appointmentCollection);
			    response = new Appointment();
			    BeanUtil.map(appointmentCollection, response);
			} else {
			    logger.error("Event cannot be added as appointment is already book");
			    throw new BusinessException(ServiceError.NotAcceptable, "Event cannot be added as appointment is already book");
			}
		    } else {
			logger.error("Incorrect Id");
			throw new BusinessException(ServiceError.InvalidInput, "Incorrect Id");
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean sendReminder(String appointmentId) {
		Boolean response = false;
		try {
			AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(appointmentId);
			if(appointmentCollection != null){
				if(appointmentCollection.getPatientId() != null){
					UserCollection userCollection = userRepository.findOne(appointmentCollection.getDoctorId());
					UserCollection patient = userRepository.findOne(appointmentCollection.getPatientId());
					LocationCollection locationCollection = locationRepository.findOne(appointmentCollection.getLocationId());
					if(userCollection != null && locationCollection != null && patient != null){
						String patientName=patient.getFirstName(),  
								dateTime=appointmentCollection.getDate()+" "+String.format("%02d:%02d", appointmentCollection.getTime().getFrom() / 60, appointmentCollection.getTime().getFrom() % 60),
								doctorName=userCollection.getTitle()+" "+userCollection.getFirstName(),clinicName= locationCollection.getLocationName(),clinicContactNum=locationCollection.getLocationPhoneNumber() != null ? locationCollection.getLocationPhoneNumber() :"";
						sendMsg(SMSFormatType.APPOINTMENT_REMINDER.getType(), "APPOINTMENT_REMINDER_TO_PATIENT", appointmentCollection.getDoctorId(),appointmentCollection.getLocationId(), appointmentCollection.getHospitalId(), appointmentCollection.getPatientId(), patient.getMobileNumber(), patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
						response = true;
					}
				}
			}else{
				logger.error("Appointment does not exist for this appointmentId");
			    throw new BusinessException(ServiceError.InvalidInput, "Appointment does not exist for this appointmentId");
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

	@Override
	public void importMaster() {
//		String csvFile = "/home/suresh/cities.csv";
//		BufferedReader br = null;
//		String line = "";
//		String cvsSplitBy = ",";

		try {
//			CountryCollection countryCollection = countryRepository.findOne("56936211e4b05b2581ba11dd");
//		    br = new BufferedReader(new FileReader(csvFile));
//		    int i = 0;
//		    while ((line = br.readLine()) != null) {
//			System.out.println(i++);
//			String[] obj = line.split(cvsSplitBy);
//
//			StateCollection stateCollection = stateRepository.findByName(obj[2]);
//			if(stateCollection == null){
//				stateCollection = new StateCollection();
//			    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(obj[2] + " "
//				    + (countryCollection != null ? countryCollection.getCountry() : ""));
//
//			    if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), stateCollection);
//			    stateCollection.setCountryId("56936211e4b05b2581ba11dd");
//			    stateCollection.setState(obj[2]);
//			    stateCollection = stateRepository.save(stateCollection);
//			}
//			
//			CityCollection cityCollection = new CityCollection();
//			cityCollection.setCity(obj[1]);
//			cityCollection.setStateId(stateCollection.getId());
//			List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(cityCollection.getCity() + " "
//				    + (stateCollection != null ? stateCollection.getState() : "")+ " "
//						    + (countryCollection != null ? countryCollection.getCountry() : ""));
//
//			    if (geocodedLocations != null && !geocodedLocations.isEmpty())
//				BeanUtil.map(geocodedLocations.get(0), cityCollection);
//
//			    cityCollection = cityRepository.save(cityCollection);
//		    }

				List<CountryCollection> countries = countryRepository.findAll();
		    	if(countries != null){
		    		for(CountryCollection country : countries){
						SolrCountryDocument solrCountry = new SolrCountryDocument();
						BeanUtil.map(country, solrCountry);
						solrCountry.setGeoLocation(new GeoLocation(country.getLatitude(), country.getLongitude()));
						solrCityService.addCountry(solrCountry);
					}
		    	}
		    	List<StateCollection> states = stateRepository.findAll();
		    	if (states != null) {
					for(StateCollection state : states){
						SolrStateDocument solrState = new SolrStateDocument();
						BeanUtil.map(state, solrState);
						solrState.setGeoLocation(new GeoLocation(state.getLatitude(), state.getLongitude()));
						solrCityService.addState(solrState);
					}
			    }
			    List<CityCollection> cities = cityRepository.findAll();
			    if (cities != null) {
				for(CityCollection city : cities){
					SolrCityDocument solrCities = new SolrCityDocument();
					BeanUtil.map(city, solrCities);
					solrCities.setGeoLocation(new GeoLocation(city.getLatitude(), city.getLongitude()));
					solrCityService.addCities(solrCities);
				}
			    }
		} catch (Exception e) {
		    e.printStackTrace();
		} 
//		finally {
//		    if (br != null) {
//			try {
//			    br.close();
//			} catch (IOException e) {
//			    e.printStackTrace();
//			}
//		    }
//		}
		System.out.println("Done");
	}

	@Override
	public List<PatientQueue> addPatientInQueue(PatientQueueAddEditRequest request) {
		List<PatientQueue> response = null;
		try{
			response = updateQueue(null, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId(), new Date(), null, null, true);
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, "Error while adding patient In Queue");
		}
		return response;
	}

	@Override
	public List<PatientQueue> rearrangePatientInQueue(String doctorId, String locationId, String hospitalId, String patientId, String appointmentId, int sequenceNo) {
		List<PatientQueue> response = null;
		try{
			response = updateQueue(appointmentId, doctorId, locationId, hospitalId, patientId, new Date(), null, sequenceNo, true);
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, "Error while rearranging patient In Queue");
		}
		return response;
	}

	@Override
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
	    			patientQueue.setPatient(patientCard);
	    			response.add(patientQueue);
	    		}
	    	}
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, "Error while rearranging patient In Queue");
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
	    	if(startTime !=null){
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
	    					for(PatientQueueCollection queueCollection : patientQueueCollections){
				    			if(appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())){
				    				queueCollection.setSequenceNo(sequenceNo);
				    				temp = queueCollection;
				    			}else{
				    				int seq = queueCollection.getSequenceNo();
					    			if(queueCollection.getSequenceNo() > sequenceNo){
					    				queueCollection.setSequenceNo(seq+1);
					    				patientQueueRepository.save(queueCollection);
					    			}else{
					    				if(queueCollection.getSequenceNo() == sequenceNo){
					    					if(temp != null){
					    						temp.setStartTime(queueCollection.getStartTime()-1);
					    						patientQueueRepository.save(temp);
					    					}
						    				queueCollection.setSequenceNo(seq+1);
						    				patientQueueRepository.save(queueCollection);
						    			}
					    			}
				    			}
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
		    			response.add(patientQueue);
		    		}
	    		}else{
	    			BeanUtil.map(patientQueueCollections, response);
	    		}	    		
	    	}
		}catch(Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
		}
		return response;
	}

}
