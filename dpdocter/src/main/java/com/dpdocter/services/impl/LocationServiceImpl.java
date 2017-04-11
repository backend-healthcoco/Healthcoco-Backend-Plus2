package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorStats;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.Location;
import com.dpdocter.collections.DoctorProfileViewCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

@Service
public class LocationServiceImpl implements LocationServices {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

	@Value("${geocoding.services.api.key}")
	private String GEOCODING_SERVICES_API_KEY;
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public List<GeocodedLocation> geocodeLocation(String address) {
		List<GeocodedLocation> response = null;
		GeoApiContext context = new GeoApiContext().setApiKey(GEOCODING_SERVICES_API_KEY);
		GeocodingResult[] results = null;
		try {
			results = GeocodingApi.geocode(context, address).await();
			if (results != null && results.length != 0) {
				response = new ArrayList<GeocodedLocation>();
				for (GeocodingResult result : results) {
					GeocodedLocation geocodedLocation = new GeocodedLocation();
					geocodedLocation.setFormattedAddress(result.formattedAddress);
					geocodedLocation.setLatitude(result.geometry.location.lat);
					geocodedLocation.setLongitude(result.geometry.location.lng);
					response.add(geocodedLocation);
				}
			}
		} catch (Exception e) {
			throw new BusinessException(ServiceError.Unknown, "Couldn't Geocode the location");
		}
		return response;
	}

	@Override
	public List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude) {
		List<GeocodedLocation> response = null;
		GeoApiContext context = new GeoApiContext().setApiKey(GEOCODING_SERVICES_API_KEY);
		GeocodingResult[] results = null;
		try {
			results = GeocodingApi.newRequest(context).latlng(new LatLng(latitude, longitude)).await();
			if (results != null && results.length != 0) {
				response = new ArrayList<GeocodedLocation>();
				for (GeocodingResult result : results) {
					GeocodedLocation geocodedLocation = new GeocodedLocation();
					geocodedLocation.setFormattedAddress(result.formattedAddress);
					geocodedLocation.setLatitude(result.geometry.location.lat);
					geocodedLocation.setLongitude(result.geometry.location.lng);
					response.add(geocodedLocation);
				}
			}
		} catch (Exception e) {
			throw new BusinessException(ServiceError.Unknown, "Couldn't Geocode the location");
		}
		return response;
	}

	@Override
	public Location addEditRecommedation(String locationId, String patientId) {
		Location response;

		try {

			ObjectId locationObjectId = new ObjectId(locationId);
			ObjectId patientObjectId = new ObjectId(patientId);
			RecommendationsCollection recommendationsCollection = null;

			LocationCollection locationCollection = locationRepository.findOne(locationObjectId);

			UserCollection userCollection = userRepository.findOne(patientObjectId);

			if (userCollection != null & locationCollection != null) {
				recommendationsCollection = recommendationsRepository.findByDoctorIdLocationIdAndPatientId(null,
						locationObjectId, patientObjectId);

				if (recommendationsCollection != null) {
					if (!recommendationsCollection.getDiscarded()) {
						locationCollection
								.setNoOfClinicRecommendations(locationCollection.getNoOfClinicRecommendations() - 1);
						recommendationsCollection.setDiscarded(true);
					} else {
						locationCollection
								.setNoOfClinicRecommendations(locationCollection.getNoOfClinicRecommendations() + 1);
						recommendationsCollection.setDiscarded(false);
					}
				} else {

					recommendationsCollection = new RecommendationsCollection();
					recommendationsCollection.setLocationId(locationObjectId);
					recommendationsCollection.setPatientId(patientObjectId);
					locationCollection
							.setNoOfClinicRecommendations(locationCollection.getNoOfClinicRecommendations() + 1);

				}
				recommendationsCollection = recommendationsRepository.save(recommendationsCollection);
				locationCollection = locationRepository.save(locationCollection);
				response = new Location();
				BeanUtil.map(locationCollection, response);
				response.setIsClinicRecommended(!recommendationsCollection.getDiscarded());

			} else {
				throw new BusinessException(ServiceError.Unknown, "Error  location  not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error recommending");
		}

		return response;
	}

	@Override
	@Transactional
	public Boolean setDefaultLab(String locationId, String defaultLabId) {
		Boolean status = false;
		/*try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			locationCollection.setDefaultParentLabId(new ObjectId(defaultLabId));
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}*/
		return status;
	}
	
	public List<Location> getAssociatedLabs(String locationId, Boolean isAssociated){
		
		List<Location> response = null;
		
		/*try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			List<ObjectId> associatedLabs = locationCollection.getAssociatedLabs();
			Criteria criteria = new Criteria("isParent").in(false);
			if (isAssociated.equals(true)) {
				criteria.and("_id").in(associatedLabs);
			} else if (isAssociated.equals(false)) {
				criteria.and("_id").nin(associatedLabs);
			}
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<Location> results = mongoTemplate.aggregate(aggregation, LocationCollection.class,
					Location.class);
			response = new ArrayList<Location>();
			response = results.getMappedResults();
			for(Location location : response)
			{
				if(associatedLabs.contains(new ObjectId(location.getId())))
				{
					
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}*/
		return response;
		
	}
	
	

}
