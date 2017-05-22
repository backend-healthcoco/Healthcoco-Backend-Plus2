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

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.LabTestSample;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyLabAssociationCollection;
import com.dpdocter.collections.LabAssociationCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardCollection;
import com.dpdocter.collections.RateCardTestAssociationCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyLabAssociationRepository;
import com.dpdocter.repository.LabTestPickupRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardRepository;
import com.dpdocter.repository.RateCardTestAssociationRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditLabTestPickupRequest;
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
	public LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request) {
		LabTestPickup response = null;
		LabTestPickupCollection labTestPickupCollection = null;
		String requestId = null;
		try {

			if (request.getId() != null) {
				labTestPickupCollection = labTestPickupRepository.findOne(new ObjectId(request.getId()));
				if (labTestPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, labTestPickupCollection);
				labTestPickupCollection.setLabTestSamples(request.getLabTestSamples());
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			} else {
				requestId = UniqueIdInitial.LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setDaughterLabCRN(saveCRN(request.getDaughterLabLocationId(), requestId, 5));
				request.setParentLabCRN(saveCRN(request.getParentLabLocationId(), requestId, 5));
				for (LabTestSample labTestSample : request.getLabTestSamples()) {
					labTestSample.setSampleId(UniqueIdInitial.LAB_PICKUP_SAMPLE + DPDoctorUtils.generateRandomId());
				}
				labTestPickupCollection = new LabTestPickupCollection();
				BeanUtil.map(request, labTestPickupCollection);
				labTestPickupCollection.setRequestId(requestId);
				labTestPickupCollection.setLabTestSamples(request.getLabTestSamples());
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
			}
			else
			{
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
						new Criteria("mobileNumber").regex("^" + searchTerm), new Criteria("name").regex("^" + searchTerm, "i"),new Criteria("name").regex("^" + searchTerm) );
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
	public Location addCollectionBoyAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
		Location response = null;
		CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = null;
		try {
			for (CollectionBoyLabAssociation collectionBoyLabAssociation : collectionBoyLabAssociations) {
				if(DPDoctorUtils.anyStringEmpty(collectionBoyLabAssociation.getCollectionBoyId(),collectionBoyLabAssociation.getParentLabId(),collectionBoyLabAssociation.getDaughterLabId()))
				{
					throw new BusinessException(ServiceError.InvalidInput , "Invalid Input - Parent & Daughter Lab ID cannot be null");
				}
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository.findbyParentIdandDaughterId( new ObjectId(collectionBoyLabAssociation.getCollectionBoyId()),
						new ObjectId(collectionBoyLabAssociation.getParentLabId()), new ObjectId(collectionBoyLabAssociation.getDaughterLabId()));
				//System.out.println(collectionBoyLabAssociationCollection);
				if (collectionBoyLabAssociationCollection == null) {
					collectionBoyLabAssociationCollection = new CollectionBoyLabAssociationCollection();
				}
				else
				{
					collectionBoyLabAssociationCollection.setId(String.valueOf(collectionBoyLabAssociationCollection.getId()));
				}
				BeanUtil.map(collectionBoyLabAssociation, collectionBoyLabAssociationCollection);
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository.save(collectionBoyLabAssociationCollection);
				//System.out.println(collectionBoyLabAssociationCollection);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}
	
	@Override
	@Transactional
	public Location getAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
		Location response = null;
		CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = null;
		try {
			for (CollectionBoyLabAssociation collectionBoyLabAssociation : collectionBoyLabAssociations) {
				if(DPDoctorUtils.anyStringEmpty(collectionBoyLabAssociation.getCollectionBoyId(),collectionBoyLabAssociation.getParentLabId(),collectionBoyLabAssociation.getDaughterLabId()))
				{
					throw new BusinessException(ServiceError.InvalidInput , "Invalid Input - Parent & Daughter Lab ID cannot be null");
				}
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository.findbyParentIdandDaughterId( new ObjectId(collectionBoyLabAssociation.getCollectionBoyId()),
						new ObjectId(collectionBoyLabAssociation.getParentLabId()), new ObjectId(collectionBoyLabAssociation.getDaughterLabId()));
				if (collectionBoyLabAssociationCollection == null) {
					collectionBoyLabAssociationCollection = new CollectionBoyLabAssociationCollection();
				}
				else
				{
					collectionBoyLabAssociationCollection.setId(String.valueOf(collectionBoyLabAssociationCollection.getId()));
				}
				BeanUtil.map(collectionBoyLabAssociation, collectionBoyLabAssociationCollection);
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository.save(collectionBoyLabAssociationCollection);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<Location> getAssociatedLabs(String locationId , Boolean isParent) {

		List<LabAssociationLookupResponse> lookupResponses = null;
		List<Location> locations = null;
		ObjectId locationObjectId = new ObjectId(locationId);
		try {
			LocationCollection locationCollection = locationRepository.findOne(locationObjectId);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			Criteria criteria =  new Criteria();
			criteria.and("isActive").is(Boolean.TRUE);
			Aggregation aggregation = null;
			if(isParent)
			{
				criteria.and("parentLabId").is(locationObjectId);
				 aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
							Aggregation.unwind("location"),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
				
			}
			else
			{
				criteria.and("daughterLabId").is(locationObjectId);
				 aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.lookup("location_cl", "parentLabId", "_id", "location"),
							Aggregation.unwind("location"),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
				
			}
			AggregationResults<LabAssociationLookupResponse> results = mongoTemplate.aggregate(aggregation, LabAssociationCollection.class,
					LabAssociationLookupResponse.class);
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
	public List<RateCard> getRateCards(int page, int size , String searchTerm ,String locationId) {
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
				rateCardTestAssociationCollection = rateCardTestAssociationRepository.findOne(new ObjectId(request.getId()));
			} else {
				rateCardTestAssociationCollection = new RateCardTestAssociationCollection();
			}
			BeanUtil.map(request, rateCardTestAssociationCollection);
			rateCardTestAssociationCollection = rateCardTestAssociationRepository.save(rateCardTestAssociationCollection);
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
	public List<RateCardTestAssociation> getRateCardTests(int page, int size , String searchTerm ,String rateCardId , String labId) {
		List<RateCardTestAssociation> rateCardTests = null;
		
		List<RateCardTestAssociation> specialRateCardsTests = null;
		
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
				aggregation = Aggregation.newAggregation(Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<RateCardTestAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardTestAssociationCollection.class, RateCardTestAssociationLookupResponse.class);
			rateCardTestAssociationLookupResponses = aggregationResults.getMappedResults();
			
			if(!DPDoctorUtils.anyStringEmpty(labId))
			{
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria.and("labId").is(new ObjectId(labId))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<RateCardTestAssociation> results = mongoTemplate.aggregate(aggregation,
					RateCardTestAssociationCollection.class, RateCardTestAssociation.class);
			specialRateCardsTests = results.getMappedResults();
			}
			
			for(RateCardTestAssociationLookupResponse lookupResponse : rateCardTestAssociationLookupResponses){
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return rateCardTests;
	}

}
