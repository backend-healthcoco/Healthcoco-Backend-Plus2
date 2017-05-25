package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
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

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.LabTestSample;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyLabAssociationCollection;
import com.dpdocter.collections.LabAssociationCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LabTestSampleCollection;
import com.dpdocter.collections.LandmarkLocalityCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardCollection;
import com.dpdocter.collections.RateCardLabAssociationCollection;
import com.dpdocter.collections.RateCardTestAssociationCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyLabAssociationRepository;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.LabAssociationRepository;
import com.dpdocter.repository.LabTestPickupRepository;
import com.dpdocter.repository.LabTestSampleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardLabAssociationRepository;
import com.dpdocter.repository.RateCardRepository;
import com.dpdocter.repository.RateCardTestAssociationRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.response.CBLabAssociationLookupResponse;
import com.dpdocter.response.CollectionBoyLabAssociationLookupResponse;
import com.dpdocter.response.LabAssociationLookupResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
import com.dpdocter.services.LocationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import common.util.web.DPDoctorUtils;

@Service
public class LocationServiceImpl implements LocationServices {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

	@Autowired
	private LabTestPickupRepository labTestPickupRepository;

	@Autowired
	private CRNRepository crnRepository;

	@Autowired
	private CollectionBoyLabAssociationRepository collectionBoyLabAssociationRepository;

	@Autowired
	private RateCardRepository rateCardRepository;

	@Autowired
	private RateCardTestAssociationRepository rateCardTestAssociationRepository;
	
	@Autowired
	private CollectionBoyRepository collectionBoyRepository;
	
	@Autowired
	private RateCardLabAssociationRepository rateCardLabAssociationRepository;
	
	@Autowired
	private LabAssociationRepository labAssociationRepository;
	
	@Autowired
	private LabTestSampleRepository labTestSampleRepository;

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
		/*
		 * try { LocationCollection locationCollection =
		 * locationRepository.findOne(new ObjectId(locationId)); if
		 * (locationCollection == null) { throw new
		 * BusinessException(ServiceError.NoRecord, "location not found"); }
		 * locationCollection.setDefaultParentLabId(new ObjectId(defaultLabId));
		 * status = true; } catch (Exception e) { // TODO: handle exception
		 * logger.warn(e); e.printStackTrace(); }
		 */
		return status;
	}
	
	@Override
	@Transactional
	public CollectionBoy discardCB(String collectionBoyId , Boolean discarded)
	{
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		collectionBoyCollection = collectionBoyRepository.findOne(new ObjectId(collectionBoyId));
		if(collectionBoyCollection == null)
		{
			throw new BusinessException(ServiceError.NoRecord , "Collection Boy record not found");
		}
		collectionBoyCollection.setDiscarded(discarded);
		collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
		if(collectionBoyCollection != null)
		{
			response = new CollectionBoy();
			BeanUtil.map(collectionBoyCollection, response);
		}
		return response;
	}
	
	@Override
	@Transactional
	public CollectionBoy changeAvailability(String collectionBoyId , Boolean isAvailable)
	{
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		collectionBoyCollection = collectionBoyRepository.findOne(new ObjectId(collectionBoyId));
		if(collectionBoyCollection == null)
		{
			throw new BusinessException(ServiceError.NoRecord , "Collection Boy record not found");
		}
		collectionBoyCollection.setIsAvailable(isAvailable);
		collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
		if(collectionBoyCollection != null)
		{
			response = new CollectionBoy();
			BeanUtil.map(collectionBoyCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public LabTestPickup getLabTestPickupById(String id)
	{
		LabTestPickupCollection labTestPickupCollection = null;
		LabTestPickup response = null;
		try {
			labTestPickupCollection = labTestPickupRepository.findOne(new ObjectId(id));
			if (labTestPickupCollection != null) {
				response = new LabTestPickup();
				BeanUtil.map(labTestPickupCollection, response);
			} 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public LabTestPickup getLabTestPickupByRequestId(String requestId)
	{
		LabTestPickupCollection labTestPickupCollection = null;
		LabTestPickup response = null;
		try {
			labTestPickupCollection = labTestPickupRepository.getByRequestId(requestId);
			if (labTestPickupCollection != null) {
				response = new LabTestPickup();
				BeanUtil.map(labTestPickupCollection, response);
			} 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<LabTestPickup> getRequestForCB(String collectionBoyId , int size , int page) {
		
		List<LabTestPickup> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			criteria.and("isCompleted").is(false);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<LabTestPickup> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickup.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys Pickup Request");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys Pickup Request");
		}
		return response; 
		
	}

	@Override
	@Transactional
	public LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request) {
		LabTestPickup response = null;
		List<ObjectId> labTestSampleIds = new ArrayList<ObjectId>();
		LabTestPickupCollection labTestPickupCollection = null;
		String requestId = null;
		try {

			if (request.getId() != null) {
				labTestPickupCollection = labTestPickupRepository.findOne(new ObjectId(request.getId()));
				if (labTestPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, labTestPickupCollection);
				for (LabTestSample labTestSample : request.getLabTestSamples()) {
					
					if(labTestSample.getId() != null)
					{
						labTestSampleIds.add(new ObjectId(labTestSample.getId()));
					}
					else
					{
						labTestSample.setSampleId(UniqueIdInitial.LAB_PICKUP_SAMPLE.getInitial() + DPDoctorUtils.generateRandomId());
						LabTestSampleCollection labTestSampleCollection = new LabTestSampleCollection();
						BeanUtil.map(labTestSample, labTestSampleCollection);
						labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
						labTestSampleIds.add(labTestSampleCollection.getId());
					}
					
				} 
				labTestPickupCollection.setLabTestSampleIds(labTestSampleIds);
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			} else {
				requestId = UniqueIdInitial.LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setDaughterLabCRN(saveCRN(request.getDaughterLabLocationId(), requestId, 5));
				for (LabTestSample labTestSample : request.getLabTestSamples()) {
					labTestSample.setSampleId(UniqueIdInitial.LAB_PICKUP_SAMPLE.getInitial() + DPDoctorUtils.generateRandomId());
					LabTestSampleCollection labTestSampleCollection = new LabTestSampleCollection();
					BeanUtil.map(labTestSample, labTestSampleCollection);
					labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
					labTestSampleIds.add(labTestSampleCollection.getId());
				} 
				labTestPickupCollection = new LabTestPickupCollection();
				BeanUtil.map(request, labTestPickupCollection);
				labTestPickupCollection.setRequestId(requestId);
				labTestPickupCollection.setLabTestSampleIds(labTestSampleIds);
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);

			}
			response = new LabTestPickup();
			BeanUtil.map(labTestPickupCollection, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	private String saveCRN(String locationId, String requestId, Integer length) {
		CRNCollection crnCollection = new CRNCollection();
		String crnNumber = DPDoctorUtils.randomString(length);
		crnCollection.setCrnNumber(crnNumber);
		crnCollection.setLocationId(new ObjectId(locationId));
		crnCollection.setRequestId(requestId);
		crnCollection.setCreatedAt(System.currentTimeMillis());
		crnCollection.setIsUsed(false);
		crnCollection = crnRepository.save(crnCollection);
		return crnNumber;
	}

	@Override
	@Transactional
	public Boolean verifyCRN(String locationId, String crn, String requestId) {
		boolean status = false;
		CRNCollection crnCollection = crnRepository.getbylocationIdandCRN(new ObjectId(locationId), crn, requestId);
		if (crnCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "CRN not found");
		} else {
			if (crnCollection.getIsUsed().equals(Boolean.TRUE)) {
				throw new BusinessException(ServiceError.NotAcceptable, "CRN already used");
			} else {
				crnCollection.setIsUsed(true);
				crnCollection.setUsedAt(System.currentTimeMillis());
				crnRepository.save(crnCollection);
				status = true;
			}
		}
		return status;
	}

	@Override
	@Transactional
	public List<CollectionBoy> getCollectionBoyList(int size, int page, String locationId, String searchTerm) {
		List<CollectionBoy> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm),
						new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}

			criteria.and("locationId").is(new ObjectId(locationId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<CollectionBoy> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyCollection.class, CollectionBoy.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Location> addCollectionBoyAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
		List<Location> locations = null;
		
		List<CollectionBoyLabAssociationLookupResponse> lookupResponses = null;
		CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = null;
		try {
			for (CollectionBoyLabAssociation collectionBoyLabAssociation : collectionBoyLabAssociations) {
				if (DPDoctorUtils.anyStringEmpty(collectionBoyLabAssociation.getCollectionBoyId(),
						collectionBoyLabAssociation.getParentLabId(), collectionBoyLabAssociation.getDaughterLabId())) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Input - Parent & Daughter Lab ID cannot be null");
				}
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
						.findbyParentIdandDaughterId(new ObjectId(collectionBoyLabAssociation.getCollectionBoyId()),
								new ObjectId(collectionBoyLabAssociation.getParentLabId()),
								new ObjectId(collectionBoyLabAssociation.getDaughterLabId()));
				if (collectionBoyLabAssociationCollection == null) {
					collectionBoyLabAssociationCollection = new CollectionBoyLabAssociationCollection();
				} else {
					collectionBoyLabAssociationCollection
							.setId(collectionBoyLabAssociationCollection.getId());
				}
				BeanUtil.map(collectionBoyLabAssociation, collectionBoyLabAssociationCollection);
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
						.save(collectionBoyLabAssociationCollection);
			}
			
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyLabAssociations.get(0).getParentLabId())) {
				criteria.and("parentLabId").is(new ObjectId(collectionBoyLabAssociations.get(0).getParentLabId()));
			}
			criteria.and("isActive").is(true);
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<CollectionBoyLabAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, CollectionBoyLabAssociationCollection.class,
					CollectionBoyLabAssociationLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();
			if (lookupResponses != null) {
				locations = new ArrayList<Location>();
				for (CollectionBoyLabAssociationLookupResponse lookupResponse : lookupResponses) {
					locations.add(lookupResponse.getLocation());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return locations;
	}

	@Override
	@Transactional
	public List<Location> getCBAssociatedLabs(String parentLabId, String daughterLabId, String collectionBoyId,
			int size, int page) {
		List<Location> locations = null;
		List<CBLabAssociationLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(parentLabId)) {
				criteria.and("parentLabId").is(new ObjectId(parentLabId));
			}
			if (!DPDoctorUtils.anyStringEmpty(daughterLabId)) {
				criteria.and("daughterLabId").is(new ObjectId(daughterLabId));
			}
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyId)) {
				criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<CBLabAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyLabAssociationCollection.class, CBLabAssociationLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();
			if (lookupResponses != null) {
				locations = new ArrayList<Location>();
				for (CBLabAssociationLookupResponse lookupResponse : lookupResponses) {
					locations.add(lookupResponse.getLocation());
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return locations;
	}

	@Override
	@Transactional
	public List<Location> getAssociatedLabs(String locationId, Boolean isParent) {

		List<LabAssociationLookupResponse> lookupResponses = null;
		List<Location> locations = null;
		ObjectId locationObjectId = new ObjectId(locationId);
		try {
			LocationCollection locationCollection = locationRepository.findOne(locationObjectId);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			Criteria criteria = new Criteria();
			criteria.and("isActive").is(Boolean.TRUE);
			Aggregation aggregation = null;
			if (isParent) {
				criteria.and("parentLabId").is(locationObjectId);
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			} else {
				criteria.and("daughterLabId").is(locationObjectId);
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "parentLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			}
			AggregationResults<LabAssociationLookupResponse> results = mongoTemplate.aggregate(aggregation,
					LabAssociationCollection.class, LabAssociationLookupResponse.class);
			lookupResponses = new ArrayList<LabAssociationLookupResponse>();
			lookupResponses = results.getMappedResults();
			if (lookupResponses != null) {
				locations = new ArrayList<Location>();
				for (LabAssociationLookupResponse response : lookupResponses) {
					locations.add(response.getLocation());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return locations;

	}

	@Override
	@Transactional
	public RateCard addEditRateCard(RateCard request) {
		RateCard response = null;
		RateCardCollection rateCardCollection = null;
		try {
			if (request.getId() != null) {
				rateCardCollection = rateCardRepository.findOne(new ObjectId(request.getId()));
			} else {
				rateCardCollection = new RateCardCollection();
			}
			BeanUtil.map(request, rateCardCollection);
			rateCardCollection = rateCardRepository.save(rateCardCollection);
			response = new RateCard();
			BeanUtil.map(rateCardCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error adding / editing ratecard");
			throw new BusinessException(ServiceError.Unknown, "Error adding / editing ratecard");
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCard> getRateCards(int page, int size, String searchTerm, String locationId) {
		List<RateCard> rateCards = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			criteria.and("locationId").is(new ObjectId(locationId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<RateCard> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardCollection.class, RateCard.class);
			rateCards = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return rateCards;
	}

	@Override
	@Transactional
	public RateCardTestAssociation addEditRateCardTestAssociation(RateCardTestAssociation request) {
		RateCardTestAssociation response = null;
		RateCardTestAssociationCollection rateCardTestAssociationCollection = null;
		try {
			if (request.getId() != null) {
				rateCardTestAssociationCollection = rateCardTestAssociationRepository
						.findOne(new ObjectId(request.getId()));
			} else {
				rateCardTestAssociationCollection = new RateCardTestAssociationCollection();
			}
			BeanUtil.map(request, rateCardTestAssociationCollection);
			rateCardTestAssociationCollection = rateCardTestAssociationRepository
					.save(rateCardTestAssociationCollection);
			response = new RateCardTestAssociation();
			BeanUtil.map(rateCardTestAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error adding / editing ratecard");
			throw new BusinessException(ServiceError.Unknown, "Error adding / editing ratecard");
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardTestAssociationLookupResponse> getRateCardTests(int page, int size, String searchTerm, String rateCardId,
			String labId) {
		List<RateCardTestAssociationLookupResponse> rateCardTests = null;
		RateCardTestAssociationLookupResponse rateCardTestAssociation = null;
		List<RateCardTestAssociationLookupResponse> specialRateCardsTests = null;
		//List<RateCardTestAssociationLookupResponse> responses = null;
		List<RateCardTestAssociationLookupResponse> rateCardTestAssociationLookupResponses = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("diagnosticTest.testName").regex("^" + searchTerm, "i"),
						new Criteria("diagnosticTest.testName").regex("^" + searchTerm));
			}
			criteria.and("rateCardId").is(new ObjectId(rateCardId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<RateCardTestAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, RateCardTestAssociationCollection.class, RateCardTestAssociationLookupResponse.class);
			rateCardTestAssociationLookupResponses = aggregationResults.getMappedResults();
			if (!DPDoctorUtils.anyStringEmpty(labId)) {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),
						Aggregation.match(criteria.and("labId").is(new ObjectId(labId))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<RateCardTestAssociationLookupResponse> results = mongoTemplate.aggregate(aggregation,
						RateCardTestAssociationCollection.class, RateCardTestAssociationLookupResponse.class);
				specialRateCardsTests = results.getMappedResults();
			}
			if (rateCardTestAssociationLookupResponses != null) {
				rateCardTests = new ArrayList<RateCardTestAssociationLookupResponse>();
				for (RateCardTestAssociationLookupResponse lookupResponse : rateCardTestAssociationLookupResponses) {
					if(specialRateCardsTests != null)
					{
						for (RateCardTestAssociationLookupResponse specialRateCard : specialRateCardsTests) {
							if (lookupResponse.getDiagnosticTestId().equals(specialRateCard.getDiagnosticTestId()))
							{
								BeanUtil.map(specialRateCard, lookupResponse);
							}
						}
					}
					rateCardTestAssociation = new RateCardTestAssociationLookupResponse();
					BeanUtil.map(lookupResponse, rateCardTestAssociation);
					rateCardTests.add(rateCardTestAssociation);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return rateCardTests;
	}
	
	@Override
	@Transactional
	public RateCardLabAssociation addEditRateCardAssociatedLab(RateCardLabAssociation rateCardLabAssociation) {
		RateCardLabAssociation response = null;
		RateCardLabAssociationCollection rateCardLabAssociationCollection = null;
		try{
			rateCardLabAssociationCollection = rateCardLabAssociationRepository.getByLocationAndRateCard(new ObjectId(rateCardLabAssociation.getLocationId()), new ObjectId(rateCardLabAssociation.getRateCardId()));
		if (rateCardLabAssociationCollection == null) {
			rateCardLabAssociationCollection = new RateCardLabAssociationCollection();
		} else {
			rateCardLabAssociationCollection.setId(rateCardLabAssociationCollection.getId());
		}
		BeanUtil.map(rateCardLabAssociation, rateCardLabAssociationCollection);
		rateCardLabAssociationCollection = rateCardLabAssociationRepository
				.save(rateCardLabAssociationCollection);
		response = new RateCardLabAssociation();
		BeanUtil.map(rateCardLabAssociation, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}
	
	@Override
	@Transactional
	public RateCardLabAssociation getRateCardAssociatedLab(String daughterLabId , String parentLabId) {
		RateCardLabAssociation response = null;
		RateCardLabAssociationCollection rateCardLabAssociationCollection = null;
		RateCardCollection rateCardCollection = null;
		try{
			rateCardLabAssociationCollection = rateCardLabAssociationRepository.getByLocation(new ObjectId(daughterLabId ));
		if (rateCardLabAssociationCollection != null) {
			rateCardCollection = rateCardRepository.findOne(rateCardLabAssociationCollection.getId());
		}
		else
		{
			rateCardCollection = rateCardRepository.getDefaultRateCard(new ObjectId(parentLabId));
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

}
