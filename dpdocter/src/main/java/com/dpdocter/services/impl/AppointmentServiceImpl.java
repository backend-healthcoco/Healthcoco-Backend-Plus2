package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Lab;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
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
import com.dpdocter.collections.EventCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LandmarkCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.StateCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.CitySearchType;
import com.dpdocter.enums.DateFilter;
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
import com.dpdocter.repository.EventRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LandmarkRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.StateRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.response.ClinicAppointmentsResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.solr.beans.Country;
import com.dpdocter.solr.beans.State;
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
    private EventRepository eventRepository;
    
    @Autowired
    private AppointmentWorkFlowRepository appointmentWorkFlowRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return city;
    }

    @Override
    public Boolean activateDeactivateCity(String cityId, boolean activate) {
	try {
	    CityCollection cityCollection = cityRepository.findOne(cityId);
	    if (cityCollection == null) {
		throw new BusinessException(ServiceError.Unknown, "Invalid Url.");
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
    public List<City> getCities() {
	List<City> response = new ArrayList<City>();
	try {
	    List<CityCollection> cities = cityRepository.findAll();
	    if (cities != null) {
		BeanUtil.map(cities, response);
//		for(CityCollection city : cities){
//			SolrCityDocument solrCities = new SolrCityDocument();
//			BeanUtil.map(city, solrCities);
//			solrCities.setGeoLocation(new GeoLocation(city.getLatitude(), city.getLongitude()));
//			solrCityService.addCities(solrCities);
//		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
			    doctor.setDoctorClinicProfile(doctorClinicProfile);
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

    /**
     * This method will return List of DoctorInfo based on specialty ,city
     * ,location Landmark.
     * 
     * @param specialty
     *            : optional param
     * @param city
     *            : mandatory Param
     * @param localityOrLandmark
     *            : optional Param
     */
    @Override
    public List<DoctorInfo> getDoctors(String specialty, String city, String localityOrLandmark) {
	try {

	} catch (Exception e) {

	}
	return null;
    }

    @Override
    public Appointment updateAppointment(AppointmentRequest request) {
	Appointment response = null;
	try {
	    AppointmentCollection appointmentCollection = appointmentRepository.findByAppointmentId(request.getAppointmentId());
	    if (appointmentCollection != null) {
	    	AppointmentCollection appointmentCollectionToCheck = null;
	    	if(request.getState().equals(AppointmentState.RESCHEDULE)) appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(appointmentCollection.getUserLocationId(), request.getTime().getFrom(), request.getTime().getTo(), request.getDate());
		if (appointmentCollectionToCheck == null ) {
			
			AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
			BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
			appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);
			
			appointmentCollection.setState(request.getState());
			
			if(request.getState().equals(AppointmentState.CANCEL)){
		    	AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository.findByAppointmentId(request.getAppointmentId());
		    	if(bookedSlotCollection != null) appointmentBookedSlotRepository.delete(bookedSlotCollection);
		    }
		    else {
		    	appointmentCollection.setDate(request.getDate());
			    appointmentCollection.setTime(request.getTime());
			    
		    	if(request.getState().equals(AppointmentState.RESCHEDULE)){
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
		    response = new Appointment();
		    BeanUtil.map(appointmentCollection, response);
		} else {
		    logger.error("Time slot is already booked");
		    throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
		}
	    } else {
		logger.error("Incorrect appointment Id");
		throw new BusinessException(ServiceError.Unknown, "Incorrect appointment Id");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public Appointment addAppointment(AppointmentRequest request) {
	Appointment response = null;
	try {
		DoctorCollection doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (doctorCollection != null && userLocationCollection != null) {
		AppointmentCollection appointmentCollection = null;//appointmentRepository.findAppointmentbyUserLocationIdTimeDate(userLocationCollection.getId(), request.getTime(), request.getDate());
		if (appointmentCollection == null || appointmentCollection.getState().equals(AppointmentState.CANCEL)) {
		    appointmentCollection = new AppointmentCollection();
		    BeanUtil.map(request, appointmentCollection);
		    appointmentCollection.setUserLocationId(userLocationCollection.getId());
		    appointmentCollection.setCreatedTime(new Date());
		    appointmentCollection.setAppointmentId(RandomStringUtils.randomAlphanumeric(7).toUpperCase());
		    if(request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)){
		    	appointmentCollection.setState(AppointmentState.CONFIRM);
		    	
		    	if(request.getNotifyDoctorByEmail())System.out.println("send email to doctor");
		    	if(request.getNotifyDoctorBySms())System.out.println("send sms to doctor");
		    	if(request.getNotifyPatientByEmail())System.out.println("send email to patient");
		    	if(request.getNotifyDoctorBySms())System.out.println("send sms to patient");
		    			
		    }
		    else{
		    	DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository.findByLocationId(userLocationCollection.getId());
		    	if(clinicProfileCollection != null && clinicProfileCollection.getIsIBSOn() != null && clinicProfileCollection.getIsIBSOn()){
		    		appointmentCollection.setState(AppointmentState.CONFIRM);
		    	}else{
		    		appointmentCollection.setState(AppointmentState.NEW);
		    	}
		    }  
		    appointmentCollection = appointmentRepository.save(appointmentCollection);
		    
		    AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
		    BeanUtil.map(appointmentCollection, bookedSlotCollection);
	    	appointmentBookedSlotRepository.save(bookedSlotCollection);
	    	
		    if (appointmentCollection != null) {
			response = new Appointment();
			BeanUtil.map(appointmentCollection, response);
		    }
		} else {
		    logger.error("Time slot is already booked");
		    throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<ClinicAppointmentsResponse> getClinicAppointments(String locationId, String doctorId, String patientId, String date,	String filterBy) {
	List<ClinicAppointmentsResponse> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	try {
		long dateLong = Long.parseLong(date);
	    if (DPDoctorUtils.anyStringEmpty(locationId));
	    else {
		List<UserLocationCollection> userLocationCollections = null;
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
		    userLocationCollections = userLocationRepository.findByLocationId(locationId);
		} else {
		    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
		    if (userLocationCollection != null) {
			userLocationCollections = new ArrayList<UserLocationCollection>();
			userLocationCollections.add(userLocationCollection);
		    }
		}

		if (userLocationCollections != null) {
		    @SuppressWarnings("unchecked")
		    Collection<String> userLocationIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		    if(DPDoctorUtils.anyStringEmpty(patientId)){
		    	if (DPDoctorUtils.anyStringEmpty(filterBy))
				    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, new Sort(Sort.Direction.DESC, "date"));
				else {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(dateLong));
					if (filterBy.equalsIgnoreCase(DateFilter.MONTH.getFilter()))
						appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
							    new Sort(Sort.Direction.DESC, "date"));
					else if (filterBy.equalsIgnoreCase(DateFilter.WEEK.getFilter()))
					    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, calendar.get(Calendar.WEEK_OF_MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
					else if (filterBy.equalsIgnoreCase(DateFilter.DAY.getFilter()))
					    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
				}
		    }else{
		    	if (DPDoctorUtils.anyStringEmpty(filterBy))
				    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, patientId, new Sort(Sort.Direction.DESC, "date"));
				else {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(dateLong));
					if (filterBy.equalsIgnoreCase(DateFilter.MONTH.getFilter()))
						appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, patientId, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
							    new Sort(Sort.Direction.DESC, "date"));
					else if (filterBy.equalsIgnoreCase(DateFilter.WEEK.getFilter()))
					    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, patientId, calendar.get(Calendar.WEEK_OF_MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
					else if (filterBy.equalsIgnoreCase(DateFilter.DAY.getFilter()))
					    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, patientId, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
				}
		    }
		}
		if (appointmentCollections != null) {
		    response = new ArrayList<ClinicAppointmentsResponse>();
		    BeanUtil.map(appointmentCollections, response);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Appointment> getDoctorAppointments(String locationId, String doctorId, String date, String patientId, List<String> filterBy, int page, int size) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	List<UserLocationCollection> userLocationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(date);

	    if (DPDoctorUtils.anyStringEmpty(locationId)) {
		if (DPDoctorUtils.anyStringEmpty(doctorId));
		else {
		    userLocationCollections = userLocationRepository.findByUserId(doctorId);
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
		    userLocationCollections = userLocationRepository.findByLocationId(locationId);
		} else {
		    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
		    if (userLocationCollection != null) {
			userLocationCollections = new ArrayList<UserLocationCollection>();
			userLocationCollections.add(userLocationCollection);
		    }
		}
	    }
	    if (userLocationCollections != null && !userLocationCollections.isEmpty()) {
		@SuppressWarnings("unchecked")
		Collection<String> userLocationIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		if(DPDoctorUtils.anyStringEmpty(patientId)){
			if (filterBy == null || filterBy.isEmpty()){
				appointmentCollections = getAppointmentsByTodayFuturePast(userLocationIds, patientId, filterBy, page, size);
			}else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(createdTimeStamp));
				if (filterBy.contains(DateFilter.MONTH.getFilter()))
					appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
				else if (filterBy.contains(DateFilter.WEEK.getFilter()))
				    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, calendar.get(Calendar.WEEK_OF_MONTH), calendar.get(Calendar.YEAR),
					    new Sort(Sort.Direction.DESC, "date"));
				else if (filterBy.contains(DateFilter.DAY.getFilter()))
				    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
					    new Sort(Sort.Direction.DESC, "date"));
				else if(filterBy.contains(DateFilter.TODAY.getFilter()) || filterBy.contains(DateFilter.FUTURE.getFilter()) || filterBy.contains(DateFilter.PAST.getFilter())){
					appointmentCollections = getAppointmentsByTodayFuturePast(userLocationIds, patientId, filterBy, page, size);
				}
			}
		}else{
			if (filterBy == null || filterBy.isEmpty()){
				appointmentCollections = getAppointmentsByTodayFuturePast(userLocationIds, patientId, filterBy, page, size);
			}else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(createdTimeStamp));
				if (filterBy.contains(DateFilter.MONTH.getFilter()))
					appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, patientId, calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR),
						    new Sort(Sort.Direction.DESC, "date"));
				else if (filterBy.contains(DateFilter.WEEK.getFilter()))
				    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, patientId, calendar.get(Calendar.WEEK_OF_MONTH), calendar.get(Calendar.YEAR),
					    new Sort(Sort.Direction.DESC, "date"));
				else if (filterBy.contains(DateFilter.DAY.getFilter()))
				    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, patientId, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR),
					    new Sort(Sort.Direction.DESC, "date"));
				else if(filterBy.contains(DateFilter.TODAY.getFilter()) || filterBy.contains(DateFilter.FUTURE.getFilter()) || filterBy.contains(DateFilter.PAST.getFilter()))
					appointmentCollections = getAppointmentsByTodayFuturePast(userLocationIds, patientId, filterBy, page, size);
				}
	    	}
		}
	    if (appointmentCollections != null) {
		response = new ArrayList<Appointment>();
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private List<AppointmentCollection> getAppointmentsByTodayFuturePast(Collection<String> userLocationIds, String patientId, List<String> filterBy, int page, int size) {
    	List<AppointmentCollection> appointmentCollections = new ArrayList<AppointmentCollection>();
    	try {
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(new Date());
    		DateTime start = new DateTime(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
	    	DateTime end = new DateTime(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    		if(userLocationIds != null && !userLocationIds.isEmpty()){
    			List<AppointmentCollection> appointments = new ArrayList<AppointmentCollection>();
    			if(filterBy == null || filterBy.isEmpty()){
    				if(DPDoctorUtils.anyStringEmpty(patientId)){
    					if(size > 0){
    						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, start, end, new PageRequest(page, size, Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						if(appointmentCollections.size() < size){
    							size = size - appointmentCollections.size();
    							appointments = appointmentRepository.findFutureAppointments(userLocationIds, end, new PageRequest(page, size, Direction.ASC, "date"));
    							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						}
    						if(appointmentCollections.size() < size){
    							size = size - appointmentCollections.size();
    							appointments = appointmentRepository.findPastAppointments(userLocationIds, start, new PageRequest(page, size, Direction.ASC, "date"));
    							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						}
    					}
    					else {
    						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, start, end, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						appointments = appointmentRepository.findFutureAppointments(userLocationIds, end, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						appointments = appointmentRepository.findPastAppointments(userLocationIds, start, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    					}
    				}else{
    					if(size > 0){
    						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, patientId, start, end, new PageRequest(page, size, Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						if(appointmentCollections.size() < size){
    							size = size - appointmentCollections.size();
    							appointments = appointmentRepository.findFutureAppointments(userLocationIds, patientId, end, new PageRequest(page, size, Direction.ASC, "date"));
    							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						}
    						if(appointmentCollections.size() < size){
    							size = size - appointmentCollections.size();
    							appointments = appointmentRepository.findPastAppointments(userLocationIds, patientId, start, new PageRequest(page, size, Direction.ASC, "date"));
    							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						}
    					}
    					else {
    						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, patientId, start, end, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						appointments = appointmentRepository.findFutureAppointments(userLocationIds, patientId, end, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    						appointments = appointmentRepository.findPastAppointments(userLocationIds, patientId, end, new Sort(Direction.ASC, "date"));
    						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
    					}
    				}
    			}else{
    				if(filterBy.contains(DateFilter.TODAY)){
    					if(DPDoctorUtils.anyStringEmpty(patientId)){
        					if(size > 0){
        						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, start, end, new PageRequest(page, size, Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        					else {
        						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, start, end, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}else{
        					if(size > 0){
        						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, patientId, start, end, new PageRequest(page, size, Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        					else {
        						appointments = appointmentRepository.findTodaysAppointments(userLocationIds, patientId, start, end, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}
    				}
    				if(filterBy.contains(DateFilter.FUTURE)){
    					if(DPDoctorUtils.anyStringEmpty(patientId)){
        					if(size > 0){
        						if(appointmentCollections.size() < size){
        							size = size - appointmentCollections.size();
        							appointments = appointmentRepository.findFutureAppointments(userLocationIds, end, new PageRequest(page, size, Direction.ASC, "date"));
        							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        						}
        					}
        					else {
        						appointments = appointmentRepository.findFutureAppointments(userLocationIds, end, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}else{
        					if(size > 0){
        						if(appointmentCollections.size() < size){
        							size = size - appointmentCollections.size();
        							appointments = appointmentRepository.findFutureAppointments(userLocationIds, patientId, end, new PageRequest(page, size, Direction.ASC, "date"));
        							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        						}
           					}
        					else {
        						appointments = appointmentRepository.findFutureAppointments(userLocationIds, patientId, end, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}
    				}
    				if(filterBy.contains(DateFilter.PAST)){
    					if(DPDoctorUtils.anyStringEmpty(patientId)){
        					if(size > 0){
        						if(appointmentCollections.size() < size){
        							size = size - appointmentCollections.size();
        							appointments = appointmentRepository.findPastAppointments(userLocationIds, start, new PageRequest(page, size, Direction.ASC, "date"));
        							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        						}
        					}
        					else {
        						appointments = appointmentRepository.findPastAppointments(userLocationIds, start, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}else{
        					if(size > 0){
        						if(appointmentCollections.size() < size){
        							size = size - appointmentCollections.size();
        							appointments = appointmentRepository.findPastAppointments(userLocationIds, patientId, start, new PageRequest(page, size, Direction.ASC, "date"));
        							if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        						}
        					}
        					else {
        						appointments = appointmentRepository.findPastAppointments(userLocationIds, patientId, start, new Sort(Direction.ASC, "date"));
        						if(appointments != null && !appointments.isEmpty())appointmentCollections.addAll(appointments);
        					}
        				}
    				}
    			}
    		}
    	} catch (Exception e) {
    	    e.printStackTrace();
    	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
    	}
    	return appointmentCollections;
	}

	@Override
    public List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, List<String> filterBy, int page, int size) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	List<UserLocationCollection> userLocationCollections = null;
	try {
	    if (!DPDoctorUtils.anyStringEmpty(patientId)) {
		    if (DPDoctorUtils.anyStringEmpty(locationId)) {
			if (DPDoctorUtils.anyStringEmpty(doctorId));
			else {
			    userLocationCollections = userLocationRepository.findByUserId(doctorId);
			}
		    } else {
			if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			    userLocationCollections = userLocationRepository.findByLocationId(locationId);
			} else {
			    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
			    if (userLocationCollection != null) {
				userLocationCollections = new ArrayList<UserLocationCollection>();
				userLocationCollections.add(userLocationCollection);
			    }
			}
		    }
		    if (userLocationCollections != null) {
		    	@SuppressWarnings("unchecked")
		    	Collection<String> userLocationIds = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		    	appointmentCollections = getAppointmentsByTodayFuturePast(userLocationIds, patientId, filterBy, page, size);
		    }else{
		    	appointmentCollections = getAppointmentsByTodayFuturePast(null, patientId, filterBy, page, size);
		    }
		    if (appointmentCollections != null) {
			response = new ArrayList<Appointment>();
			BeanUtil.map(appointmentCollections, response);
		    }
	    } else {
		logger.error("Patient Id cannot be null");
		throw new BusinessException(ServiceError.Unknown, "Patient Id cannot be null");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<Country> getCountries() {
		List<Country> response = new ArrayList<Country>();
		try {
		    List<CountryCollection> countries = countryRepository.findAll();
		    if (countries != null) {
//		    	for(CountryCollection country : countries){
//					SolrCountryDocument solrCountry = new SolrCountryDocument();
//					BeanUtil.map(country, solrCountry);
//					solrCountry.setGeoLocation(new GeoLocation(country.getLatitude(), country.getLongitude()));
//					solrCityService.addCountry(solrCountry);
//				}
		    	BeanUtil.map(countries, response);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<State> getStates() {
		List<State> response = new ArrayList<State>();
		try {
		    List<StateCollection> states = stateRepository.findAll();
		    if (states != null) {
//				for(StateCollection state : states){
//					SolrStateDocument solrState = new SolrStateDocument();
//					BeanUtil.map(state, solrState);
//					solrState.setGeoLocation(new GeoLocation(state.getLatitude(), state.getLongitude()));
//					solrCityService.addState(solrState);
//				}
		    BeanUtil.map(states, response);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		    				if(!DPDoctorUtils.anyStringEmpty(workingHour.getFrom(), workingHour.getTo()) && doctorClinicProfileCollection.getAppointmentSlot().getTime() > 0){
		    					List<Slot> slots = DateAndTimeUtility.sliceTime(Integer.parseInt(workingHour.getFrom()), Integer.parseInt(workingHour.getTo()), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    					if(slots != null)response.addAll(slots);
		    				}	    				
		    			}
		    		}  		
		    	}
		    }
		    List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository.findByUserLocationId(userLocationCollection.getId(), date);
		    if(bookedSlots != null && !bookedSlots.isEmpty())
		    for(AppointmentBookedSlotCollection bookedSlot : bookedSlots){
		    	if(bookedSlot.getTime() != null){
		    		List<Slot> slots = DateAndTimeUtility.sliceTime(Integer.parseInt(bookedSlot.getTime().getFrom()), Integer.parseInt(bookedSlot.getTime().getTo()), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
		    		for(Slot slot : slots){
		    			if(response.contains(slot)){
			    			slot.setIsAvailable(false);
			    			response.set(response.indexOf(slot), slot);
			    		}
		    		}
		    	}
		    }
		    List<EventCollection> eventCollections = eventRepository.findByUserLocationId(userLocationCollection.getId(), date, true);
		    if(eventCollections != null && !eventCollections.isEmpty())
			    for(EventCollection event : eventCollections){
			    	if(event.getTime() != null){
			    		List<Slot> slots = DateAndTimeUtility.sliceTime(Integer.parseInt(event.getTime().getFrom()), Integer.parseInt(event.getTime().getTo()), Math.round(doctorClinicProfileCollection.getAppointmentSlot().getTime()));
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
	    throw new BusinessException(ServiceError.Unknown, "Error whie getting time slots");
	}
	return response;
    }

	@Override
	public Event addEditEvent(EventRequest request) {
		Event response = null;
		try {
			UserCollection userCollection = userRepository.findOne(request.getDoctorId());
		    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
		    if (userCollection != null && userLocationCollection != null) {
			EventCollection eventCollection = null;
			if(request.getId() != null){
				eventCollection = eventRepository.findOne(request.getId());
			}
			if(eventCollection == null){
				request.setId(null);
				eventCollection = new EventCollection();
				eventCollection.setUserLocationId(userLocationCollection.getId());
				eventCollection.setCreatedTime(new Date());
				eventCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle()+" ":"") +userCollection.getFirstName());
			}
			BeanUtil.map(request, eventCollection);
			eventCollection = eventRepository.save(eventCollection);
			response = new Event();
		    BeanUtil.map(eventCollection, response);
		   }
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean cancelEvent(String eventId, String doctorId, String locationId) {
		Boolean response = false;
		try {
			UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(doctorId, locationId);
			EventCollection eventCollection = eventRepository.findOne(eventId);
			if(eventCollection != null & userLocationCollection != null){
				if(eventCollection.getUserLocationId().equals(userLocationCollection.getId())){
					eventRepository.delete(eventCollection);
					response = true;
				}
				else{
					logger.error("EventId, DoctorId adn LocationId does not match");
				    throw new BusinessException(ServiceError.Unknown, "EventId, DoctorId adn LocationId does not match");
				}
			}else{
				logger.error("Event does not exist for this eventId");
			    throw new BusinessException(ServiceError.Unknown, "Event does not exist for this eventId");
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
					UserCollection userCollection = userRepository.findOne(appointmentCollection.getPatientId());
					//TODO : send msg and email
				}
			}else{
				logger.error("Appointment does not exist for this appointmentId");
			    throw new BusinessException(ServiceError.Unknown, "Appointment does not exist for this appointmentId");
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}
