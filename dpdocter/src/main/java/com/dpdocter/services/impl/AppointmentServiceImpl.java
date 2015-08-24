package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.City;
import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Landmark;
import com.dpdocter.beans.Locality;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.CityCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LandmarkCollection;
import com.dpdocter.collections.LocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserLocationCollection;
import com.dpdocter.enums.CitySearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LandmarkRepository;
import com.dpdocter.repository.LocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserLocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AppointmentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private CityRepository cityRepository;

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

    @Override
    public City addCity(City city) {
	try {
	    CityCollection cityCollection = new CityCollection();
	    BeanUtil.map(city, cityCollection);
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
	    return true;
	} catch (BusinessException be) {
	    throw be;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error occured while Activating Deactivating City");
	}

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
    public Locality addLocality(Locality locality) {
	try {
	    LocalityCollection localityCollection = new LocalityCollection();
	    BeanUtil.map(locality, localityCollection);
	    localityCollection = localityRepository.save(localityCollection);
	    BeanUtil.map(localityCollection, locality);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return locality;
    }

    @Override
    public Landmark addLandmark(Landmark landmark) {
	try {
	    LandmarkCollection landmarkCollection = new LandmarkCollection();
	    BeanUtil.map(landmark, landmarkCollection);
	    landmarkCollection = landmarkRepository.save(landmarkCollection);
	    BeanUtil.map(landmarkCollection, landmark);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return landmark;
    }

    @Override
    public List<Object> getLandmarkLocality(String cityId, String type) {
	List<Object> response = new ArrayList<Object>();
	List<LandmarkCollection> landmarkCollection = null;
	List<LocalityCollection> localityCollection = null;
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
}
