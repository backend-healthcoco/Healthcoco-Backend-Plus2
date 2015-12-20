package com.dpdocter.services.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.LandmarkLocality;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.CountryCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LandmarkCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.CitySearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.CountryRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LandmarkRepository;
import com.dpdocter.repository.LocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppoinmentRequest;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.solr.beans.Country;
import common.util.web.DPDoctorUtils;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

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
	    CountryCollection countryCollection = countryRepository.findOne(city.getCountryId());
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(city.getCity() + " "
		    + (countryCollection != null ? countryCollection.getCountry() : ""));

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
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error occured while Activating Deactivating City");
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
	try {
	    LandmarkLocalityCollection landmarkLocalityCollection = new LandmarkLocalityCollection();
	    BeanUtil.map(landmarkLocality, landmarkLocalityCollection);
	    if (landmarkLocality.getCityId() != null) {
		cityCollection = cityRepository.findOne(landmarkLocality.getCityId());
	    }
	    if (cityCollection != null) {
		countryCollection = countryRepository.findOne(cityCollection.getCountryId());
	    }
	    List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(landmarkLocality.getLandmark() != null ? landmarkLocality.getLandmark()
		    + " " : "" + landmarkLocality.getLocality() != null ? landmarkLocality.getLocality() + " "
		    : "" + cityCollection.getCity() != null ? cityCollection.getCity() + " " : "" + countryCollection != null ? countryCollection.getCountry()
			    : "");

	    if (geocodedLocations != null && !geocodedLocations.isEmpty())
		BeanUtil.map(geocodedLocations.get(0), landmarkLocalityCollection);

	    landmarkLocalityCollection = localityRepository.save(landmarkLocalityCollection);
	    BeanUtil.map(landmarkLocalityCollection, landmarkLocality);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return landmarkLocality;
    }

    @Override
    public List<Object> getLandmarkLocality(String cityId, String type) {
	List<Object> response = new ArrayList<Object>();
	List<LandmarkCollection> landmarkCollection = null;
	List<LandmarkLocalityCollection> localityCollection = null;
	try {
	    if (type == null) {
		landmarkCollection = landmarkRepository.findByCityId(cityId);
		if (landmarkCollection != null)
		    BeanUtil.map(landmarkCollection, response);

		localityCollection = localityRepository.findByCityId(cityId);
		if (localityCollection != null)
		    BeanUtil.map(localityCollection, response);
	    } else {
		if (type.equalsIgnoreCase(CitySearchType.LANDMARK.getType())) {
		    landmarkCollection = landmarkRepository.findByCityId(cityId);
		    if (landmarkCollection != null)
			BeanUtil.map(landmarkCollection, response);
		}
		if (type.equalsIgnoreCase(CitySearchType.LOCALITY.getType())) {
		    localityCollection = localityRepository.findByCityId(cityId);
		    if (localityCollection != null)
			BeanUtil.map(localityCollection, response);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
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
    public Appointment appointment(AppoinmentRequest request) {
	Appointment response = null;
	try {
	    switch (request.getType()) {
	    case NEW:
		response = addAppointment(request);
		break;
	    case CONFIRM:
		response = confirmAppointment(request.getId());
		break;
	    case RESCHEDULE:
		response = rescheduleAppointment(request);
		break;
	    case CANCEL:
		response = cancelAppointment(request.getId());
		break;
	    default:
		break;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;

    }

    private Appointment cancelAppointment(String id) {
	Appointment response = null;
	try {
	    AppointmentCollection appointmentCollection = appointmentRepository.findOne(id);
	    if (appointmentCollection != null) {
		appointmentCollection.setIsCanceled(true);
		appointmentCollection.setUpdatedTime(new Date());
		appointmentCollection = appointmentRepository.save(appointmentCollection);
		response = new Appointment();
		BeanUtil.map(appointmentCollection, response);
	    } else {
		logger.error("Incorrect appointment Id");
		throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private Appointment rescheduleAppointment(AppoinmentRequest request) {
	Appointment response = null;
	try {
	    AppointmentCollection appointmentCollection = appointmentRepository.findOne(request.getId());
	    if (appointmentCollection != null) {

		AppointmentCollection appointmentCollectionToCheck = appointmentRepository.findConfirmedAppointmentbyUserLocationIdTimeDate(
			appointmentCollection.getUserLocationId(), appointmentCollection.getTime(), appointmentCollection.getDate(), true);
		if (appointmentCollectionToCheck == null) {

		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(request.getDate());
		    int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		    int month = Calendar.getInstance().get(Calendar.MONTH);
		    int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);

		    appointmentCollection.setUpdatedTime(new Date());
		    appointmentCollection.setTime(request.getTime());
		    appointmentCollection.setDate(request.getDate());
		    appointmentCollection.setDay(day);
		    appointmentCollection.setMonth(month);
		    appointmentCollection.setWeek(week);

		    appointmentCollection.setIsReschduled(true);
		    appointmentCollection = appointmentRepository.save(appointmentCollection);
		    response = new Appointment();
		    BeanUtil.map(appointmentCollection, response);
		} else {
		    logger.error("Time slot is already booked");
		    throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
		}
	    } else {
		logger.error("Incorrect appointment Id");
		throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private Appointment confirmAppointment(String id) {
	Appointment response = null;
	try {
	    AppointmentCollection appointmentCollection = appointmentRepository.findOne(id);
	    if (appointmentCollection != null) {
		AppointmentCollection appointmentCollectionToCheck = appointmentRepository.findConfirmedAppointmentbyUserLocationIdTimeDate(
			appointmentCollection.getUserLocationId(), appointmentCollection.getTime(), appointmentCollection.getDate(), true);
		if (appointmentCollectionToCheck == null) {
		    appointmentCollection.setIsConfirmed(true);
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
		throw new BusinessException(ServiceError.Unknown, "Time slot is already booked");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    private Appointment addAppointment(AppoinmentRequest request) {
	Appointment response = null;
	try {
	    UserLocationCollection userLocationCollection = userLocationRepository.findByUserIdAndLocationId(request.getDoctorId(), request.getLocationId());
	    if (userLocationCollection != null) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(request.getDate());
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
		AppointmentCollection appointmentCollection = appointmentRepository.findConfirmedAppointmentbyUserLocationIdTimeDate(
			userLocationCollection.getId(), request.getTime(), request.getDate(), true);
		if (appointmentCollection == null) {
		    appointmentCollection = new AppointmentCollection();
		    BeanUtil.map(request, appointmentCollection);
		    appointmentCollection.setUserLocationId(userLocationCollection.getId());
		    appointmentCollection.setCreatedTime(new Date());
		    appointmentCollection.setDay(day);
		    appointmentCollection.setMonth(month);
		    appointmentCollection.setWeek(week);
		    appointmentCollection.setAppointmentId(RandomStringUtils.randomAlphanumeric(7).toUpperCase());
		    appointmentCollection = appointmentRepository.save(appointmentCollection);
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
    public List<Appointment> getClinicAppointments(String locationId, String doctorId, int day, int month, int week, int page, int size, String updatedTime) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (DPDoctorUtils.anyStringEmpty(locationId))
		;
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
		    Collection<String> userLocationIdCollections = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		    String[] userLocationIds = (String[]) userLocationIdCollections.toArray();
		    if (size > 0) {
			if (day == 0 && month == 0 && week == 0)
			    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, new Date(createdTimeStamp), new PageRequest(
				    page, size, Direction.DESC, "updatedTime"));
			else if (month > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, month, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else if (week > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, week, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else if (day > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, day, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    } else {
			if (day == 0 && month == 0 && week == 0)
			    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
			else if (month > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, month, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
			else if (week > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, week, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
			else if (day > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, day, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
		if (appointmentCollections != null) {
		    response = new ArrayList<Appointment>();
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
    public List<Appointment> getDoctorAppointments(String locationId, String doctorId, int day, int month, int week, int page, int size, String updatedTime) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	List<UserLocationCollection> userLocationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);

	    if (DPDoctorUtils.anyStringEmpty(locationId)) {
		if (DPDoctorUtils.anyStringEmpty(doctorId))
		    ;
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
		Collection<String> userLocationIdCollections = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		String[] userLocationIds = (String[]) userLocationIdCollections.toArray();
		if (size > 0) {
		    if (day == 0 && month == 0 && week == 0)
			appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, new Date(createdTimeStamp), new PageRequest(page,
				size, Direction.DESC, "updatedTime"));
		    else if (month > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, month, new Date(createdTimeStamp),
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else if (week > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, week, new Date(createdTimeStamp),
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else if (day > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, day, new Date(createdTimeStamp),
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		} else {
		    if (day == 0 && month == 0 && week == 0)
			appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "updatedTime"));
		    else if (month > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, month, new Date(createdTimeStamp),
				new Sort(Sort.Direction.DESC, "updatedTime"));
		    else if (week > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, week, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "updatedTime"));
		    else if (day > 0)
			appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, day, new Date(createdTimeStamp), new Sort(
				Sort.Direction.DESC, "updatedTime"));
		}
	    }
	    if (appointmentCollections != null) {
		response = new ArrayList<Appointment>();
		BeanUtil.map(appointmentCollections, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public List<Appointment> getPatientAppointments(String locationId, String doctorId, String patientId, int day, int month, int week, int page, int size,
	    String updatedTime) {
	List<Appointment> response = null;
	List<AppointmentCollection> appointmentCollections = null;
	List<UserLocationCollection> userLocationCollections = null;
	try {
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (!DPDoctorUtils.anyStringEmpty(patientId)) {
		if (DPDoctorUtils.anyStringEmpty(locationId)) {
		    if (DPDoctorUtils.anyStringEmpty(doctorId))
			;
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
		    Collection<String> userLocationIdCollections = CollectionUtils.collect(userLocationCollections, new BeanToPropertyValueTransformer("id"));
		    String[] userLocationIds = (String[]) userLocationIdCollections.toArray();
		    if (size > 0) {
			if (day == 0 && month == 0 && week == 0)
			    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, patientId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else if (month > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, patientId, month, new Date(
				    createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else if (week > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, patientId, week, new Date(
				    createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else if (day > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, patientId, day, new Date(
				    createdTimeStamp), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    } else {
			if (day == 0 && month == 0 && week == 0)
			    appointmentCollections = appointmentRepository.findByUserlocationId(userLocationIds, patientId, new Date(createdTimeStamp),
				    new Sort(Sort.Direction.DESC, "updatedTime"));
			else if (month > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndMonth(userLocationIds, patientId, month, new Date(
				    createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
			else if (week > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndWeek(userLocationIds, patientId, week, new Date(
				    createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
			else if (day > 0)
			    appointmentCollections = appointmentRepository.findByUserlocationIdAndDay(userLocationIds, patientId, day, new Date(
				    createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
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
}
