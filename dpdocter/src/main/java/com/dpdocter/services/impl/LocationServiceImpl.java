package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.LabTestSample;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.PatientLabTestItem;
import com.dpdocter.beans.PatientLabTestSample;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Specimen;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyLabAssociationCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DynamicCollectionBoyAllocationCollection;
import com.dpdocter.collections.FavouriteRateCardTestCollection;
import com.dpdocter.collections.LabAssociationCollection;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LabTestSampleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardCollection;
import com.dpdocter.collections.RateCardLabAssociationCollection;
import com.dpdocter.collections.RateCardTestAssociationCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.SpecimenCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyLabAssociationRepository;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DynamicCollectionBoyAllocationRepository;
import com.dpdocter.repository.FavouriteRateCardTestRepositoy;
import com.dpdocter.repository.LabTestPickupRepository;
import com.dpdocter.repository.LabTestSampleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardLabAssociationRepository;
import com.dpdocter.repository.RateCardRepository;
import com.dpdocter.repository.RateCardTestAssociationRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.request.DynamicCollectionBoyAllocationRequest;
import com.dpdocter.request.PatientLabTestsampleRequest;
import com.dpdocter.response.CBLabAssociationLookupResponse;
import com.dpdocter.response.CollectionBoyLabAssociationLookupResponse;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.DynamicCollectionBoyAllocationResponse;
import com.dpdocter.response.LabAssociationLookupResponse;
import com.dpdocter.response.LabTestGroupResponse;
import com.dpdocter.response.PatientLabTestSampleReportResponse;
import com.dpdocter.response.RateCardTestAssociationByLBResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.PushNotificationServices;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class LocationServiceImpl implements LocationServices {

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

	@Autowired
	private FavouriteRateCardTestRepositoy favouriteRateCardTestRepositoy;

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

//	@Autowired
//	private LabAssociationRepository labAssociationRepository;

	@Autowired
	private LabTestSampleRepository labTestSampleRepository;

//	@Autowired
//	private LabReportsRepository labReportsRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private DentalWorkRepository dentalWorkRepository;

	@Autowired
	private DynamicCollectionBoyAllocationRepository dynamicCollectionBoyAllocationRepository;

	@Value("${geocoding.services.api.key}")
	private String GEOCODING_SERVICES_API_KEY;

	@Value("${collection.boy.notification}")
	private String COLLECTION_BOY_NOTIFICATION;

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

			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);

			UserCollection userCollection = userRepository.findById(patientObjectId).orElse(null);

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
		 * locationRepository.findById(new ObjectId(locationId)); if
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
	public CollectionBoy discardCB(String collectionBoyId, Boolean discarded) {
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		List<CollectionBoyLabAssociationCollection> collectionBoyLabAssociationCollections = null;
		collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(collectionBoyId)).orElse(null);
		if (collectionBoyCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Collection Boy record not found");
		}

		if (discarded == true) {
			collectionBoyLabAssociationCollections = collectionBoyLabAssociationRepository
					.findAllAssociationByCollectionBoyId(new ObjectId(collectionBoyId));
			for (CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection : collectionBoyLabAssociationCollections) {
				collectionBoyLabAssociationCollection.setIsActive(false);
				collectionBoyLabAssociationCollection.setUpdatedTime(new Date());
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
						.save(collectionBoyLabAssociationCollection);
			}
		}

		else if (discarded == false) {
			collectionBoyLabAssociationCollections = collectionBoyLabAssociationRepository
					.findAllAssociationByCollectionBoyId(new ObjectId(collectionBoyId));
			for (CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection : collectionBoyLabAssociationCollections) {
				collectionBoyLabAssociationCollection.setIsActive(true);
				collectionBoyLabAssociationCollection.setUpdatedTime(new Date());
				collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
						.save(collectionBoyLabAssociationCollection);
			}
		}

		UserCollection userCollection = userRepository.findById(collectionBoyCollection.getUserId()).orElse(null);
		if (userCollection != null) {
			userCollection.setIsActive(!discarded);
			userCollection.setUpdatedTime(new Date());
			userCollection = userRepository.save(userCollection);
		}

		collectionBoyCollection.setUpdatedTime(new Date());
		collectionBoyCollection.setDiscarded(discarded);
		collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
		if (collectionBoyCollection != null) {
			response = new CollectionBoy();
			BeanUtil.map(collectionBoyCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public CollectionBoy changeAvailability(String collectionBoyId, Boolean isAvailable) {
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = null;
		collectionBoyCollection = collectionBoyRepository.findById(new ObjectId(collectionBoyId)).orElse(null);
		if (collectionBoyCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Collection Boy record not found");
		}
		collectionBoyCollection.setIsAvailable(isAvailable);
		collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
		if (collectionBoyCollection != null) {
			response = new CollectionBoy();
			BeanUtil.map(collectionBoyCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public LabTestPickupLookupResponse getLabTestPickupById(String id) {
		LabTestPickupLookupResponse response = null;
		Aggregation aggregation = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("daughterLabCRN", "$daughterLabCRN"), Fields.field("pickupTime", "$pickupTime"),
					Fields.field("deliveryTime", "$deliveryTime"),
					Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
					Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
					Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
					Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
					Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
					Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
					Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
					Fields.field("parentLabLocationId", "$parentLabLocationId"),
					Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
					Fields.field("collectionBoyId", "$collectionBoyId"), Fields.field("discarded", "$discarded"),
					Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
					Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
					Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
					Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("patientName", "$patientLabTestSamples.patientName")
									.append("pid", "$patientLabTestSamples.uid"))
											.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
											.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
											.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
											.append("patientLabTestSamples",
													new BasicDBObject("$first", "$patientLabTestSamples"))
											.append("status", new BasicDBObject("$first", "$status"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("parentLabLocationId",
													new BasicDBObject("$first", "$parentLabLocationId"))
											.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
											.append("daughterLabLocationId",
													new BasicDBObject("$first", "$daughterLabLocationId"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("numberOfSamplesRequested",
													new BasicDBObject("$first", "$numberOfSamplesRequested"))
											.append("numberOfSamplesPicked",
													new BasicDBObject("$first", "$numberOfSamplesPicked"))
											.append("requestId", new BasicDBObject("$first", "$requestId"))
											.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy"))
											.append("labTestSamples", new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("id").is(new ObjectId(id))),
					Aggregation.unwind("patientLabTestSamples"),
					Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
					Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
							"labTestSamples"),
					Aggregation.unwind("labTestSamples"), aggregationOperation1, projectList, aggregationOperation2,
					Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
					Aggregation.unwind("daughterLab"),
					Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
					Aggregation.unwind("parentLab"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
			response = aggregationResults.getUniqueMappedResult();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e + " Error Getting Lab Test Pickup  By Id ");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Lab Test Pickup  By Id ");

		}
		return response;
	}

	@Override
	@Transactional
	public LabTestPickupLookupResponse getLabTestPickupByRequestId(String requestId) {
		LabTestPickupLookupResponse response = null;
		Aggregation aggregation = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("id", "$id"), Fields.field("daughterLabCRN", "$daughterLabCRN"),
							Fields.field("pickupTime", "$pickupTime"), Fields.field("deliveryTime", "$deliveryTime"),
							Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
							Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
							Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
							Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
							Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
							Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
							Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
							Fields.field("parentLabLocationId", "$parentLabLocationId"),
							Fields.field("collectionBoyId", "$collectionBoyId"),
							Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
							Fields.field("discarded", "$discarded"),
							Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
							Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
							Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
							Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("patientName", "$patientLabTestSamples.patientName")
									.append("pid", "$patientLabTestSamples.uid"))
											.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
											.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
											.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
											.append("patientLabTestSamples",
													new BasicDBObject("$first", "$patientLabTestSamples"))
											.append("status", new BasicDBObject("$first", "$status"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("parentLabLocationId",
													new BasicDBObject("$first", "$parentLabLocationId"))
											.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
											.append("daughterLabLocationId",
													new BasicDBObject("$first", "$daughterLabLocationId"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("numberOfSamplesRequested",
													new BasicDBObject("$first", "$numberOfSamplesRequested"))
											.append("numberOfSamplesPicked",
													new BasicDBObject("$first", "$numberOfSamplesPicked"))
											.append("requestId", new BasicDBObject("$first", "$requestId"))
											.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy"))
											.append("labTestSamples", new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("requestId").is(requestId)),
					Aggregation.unwind("patientLabTestSamples"),
					Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
					Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
							"labTestSamples"),
					Aggregation.unwind("labTestSamples"), aggregationOperation1, projectList, aggregationOperation2,
					Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
					Aggregation.unwind("daughterLab"),
					Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
					Aggregation.unwind("parentLab"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
			response = aggregationResults.getUniqueMappedResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Lab Test Pickup  By Id ");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Lab Test Pickup  By Id ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabTestPickupLookupResponse> getLabTestPickupByIds(List<ObjectId> ids) {
		List<LabTestPickupLookupResponse> response = null;
		Aggregation aggregation = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("id", "$id"), Fields.field("daughterLabCRN", "$daughterLabCRN"),
							Fields.field("pickupTime", "$pickupTime"), Fields.field("deliveryTime", "$deliveryTime"),
							Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
							Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
							Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
							Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
							Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
							Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
							Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
							Fields.field("parentLabLocationId", "$parentLabLocationId"),
							Fields.field("collectionBoyId", "$collectionBoyId"),
							Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
							Fields.field("discarded", "$discarded"),
							Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
							Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
							Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
							Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("patientName", "$patientLabTestSamples.patientName")
									.append("pid", "$patientLabTestSamples.uid"))
											.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
											.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
											.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
											.append("patientLabTestSamples",
													new BasicDBObject("$first", "$patientLabTestSamples"))
											.append("status", new BasicDBObject("$first", "$status"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("parentLabLocationId",
													new BasicDBObject("$first", "$parentLabLocationId"))
											.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
											.append("daughterLabLocationId",
													new BasicDBObject("$first", "$daughterLabLocationId"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("numberOfSamplesRequested",
													new BasicDBObject("$first", "$numberOfSamplesRequested"))
											.append("numberOfSamplesPicked",
													new BasicDBObject("$first", "$numberOfSamplesPicked"))
											.append("requestId", new BasicDBObject("$first", "$requestId"))
											.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy"))
											.append("labTestSamples", new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria().and("_id").in(ids)),
					Aggregation.unwind("patientLabTestSamples"),
					Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
					Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
							"labTestSamples"),
					Aggregation.unwind("labTestSamples"), aggregationOperation1, projectList, aggregationOperation2,
					Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
					Aggregation.unwind("daughterLab"),
					Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
					Aggregation.unwind("parentLab"), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Lab Test Pickup  By Ids ");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Lab Test Pickup  By Ids ");
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabTestPickupLookupResponse> getRequestForCB(String collectionBoyId, Long from, Long to,
			String searchTerm, int size, long page) {

		List<LabTestPickupLookupResponse> response = null;

		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			criteria.and("isCompleted").is(false);
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lte(DPDoctorUtils.getEndTime(new Date(to)));
			}

			Criteria orOperator = new Criteria();;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				orOperator.orOperator(
						new Criteria("daughterLab.locationName").regex(searchTerm, "i"),
						new Criteria("parentLab.locationName").regex(searchTerm, "i"),
						new Criteria("patientLabTestSamples.patientName").regex(searchTerm, "i"));
			}

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("daughterLabCRN", "$daughterLabCRN"), Fields.field("pickupTime", "$pickupTime"),
					Fields.field("deliveryTime", "$deliveryTime"),
					Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
					Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
					Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
					Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
					Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
					Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
					Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
					Fields.field("parentLab", "$parentLab"), Fields.field("collectionBoyId", "$collectionBoyId"),
					Fields.field("daughterLab", "$daughterLab"), Fields.field("discarded", "$discarded"),
					Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
					Fields.field("parentLabLocationId", "$parentLabLocationId"),
					Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
					Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
					Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
					Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("pId", "$patientLabTestSamples.uid")
									.append("patientName", "$patientLabTestSamples.patientName"))
											.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
											.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
											.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
											.append("patientLabTestSamples",
													new BasicDBObject("$first", "$patientLabTestSamples"))
											.append("status", new BasicDBObject("$first", "$status"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("parentLab", new BasicDBObject("$first", "$parentLab"))
											.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
											.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("parentLabLocationId",
													new BasicDBObject("$first", "$parentLabLocationId"))
											.append("daughterLabLocationId",
													new BasicDBObject("$first", "$daughterLabLocationId"))
											.append("numberOfSamplesRequested",
													new BasicDBObject("$first", "$numberOfSamplesRequested"))
											.append("numberOfSamplesPicked",
													new BasicDBObject("$first", "$numberOfSamplesPicked"))
											.append("requestId", new BasicDBObject("$first", "$requestId"))
											.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy"))
											.append("labTestSamples", new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLab", new BasicDBObject("$first", "$parentLab"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("patientLabTestSamples"),
						Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
								"labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("daughterLab"), Aggregation.unwind("parentLab"), Aggregation.match(orOperator),
						aggregationOperation1, projectList, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("patientLabTestSamples"),
						Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
								"labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("daughterLab"), Aggregation.unwind("parentLab"), Aggregation.match(orOperator),
						aggregationOperation1, projectList, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
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
	public List<LabTestPickupLookupResponse> getRequestForDL(String daughterLabId, Long from, Long to,
			String searchTerm, int size, long page) {

		List<LabTestPickupLookupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("daughterLabLocationId").is(new ObjectId(daughterLabId));
			criteria.and("isCompleted").is(false);
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lt(DPDoctorUtils.getEndTime(new Date(to)));
			}

			Criteria orOperator = new Criteria();;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				orOperator.orOperator(
						new Criteria("parentLab.locationName").regex(searchTerm, "i"),
						new Criteria("patientLabTestSamples.patientName").regex(searchTerm, "i"));
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("daughterLabCRN", "$daughterLabCRN"), Fields.field("pickupTime", "$pickupTime"),
					Fields.field("deliveryTime", "$deliveryTime"),
					Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
					Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
					Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
					Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
					Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
					Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
					Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
					Fields.field("parentLabLocationId", "$parentLabLocationId"),
					Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
					Fields.field("parentLab", "$parentLab"), Fields.field("collectionBoyId", "$collectionBoyId"),
					Fields.field("daughterLab", "$daughterLab"), Fields.field("discarded", "$discarded"),
					Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
					Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
					Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
					Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("patientName", "$patientLabTestSamples.patientName")
									.append("pid", "$patientLabTestSamples.uid"))
											.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
											.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
											.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
											.append("patientLabTestSamples",
													new BasicDBObject("$first", "$patientLabTestSamples"))
											.append("status", new BasicDBObject("$first", "$status"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("parentLab", new BasicDBObject("$first", "$parentLab"))
											.append("parentLabLocationId",
													new BasicDBObject("$first", "$parentLabLocationId"))
											.append("daughterLabLocationId",
													new BasicDBObject("$first", "$daughterLabLocationId"))
											.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
											.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("numberOfSamplesRequested",
													new BasicDBObject("$first", "$numberOfSamplesRequested"))
											.append("numberOfSamplesPicked",
													new BasicDBObject("$first", "$numberOfSamplesPicked"))
											.append("requestId", new BasicDBObject("$first", "$requestId"))
											.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy"))
											.append("labTestSamples", new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLab", new BasicDBObject("$first", "$parentLab"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("patientLabTestSamples"),
						Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
								"labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("parentLab"), Aggregation.match(orOperator),
						aggregationOperation1, projectList, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("patientLabTestSamples"),
						Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
								"labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("parentLab"), Aggregation.match(orOperator),
						aggregationOperation1, projectList, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
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
	public List<LabTestPickupLookupResponse> getRequestForPL(String parentLabId, String daughterLabId, Long from,
			Long to, String searchTerm, int size, long page) {

		List<LabTestPickupLookupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(daughterLabId)) {
				criteria.and("daughterLabLocationId").is(new ObjectId(daughterLabId));
			}

			criteria.and("parentLabLocationId").is(new ObjectId(parentLabId));
			criteria.and("isCompleted").is(false);
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lt(DPDoctorUtils.getEndTime(new Date(to)));
			}

			Criteria orOperator = new Criteria();;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				orOperator.orOperator(
						new Criteria("daughterLab.locationName").regex(searchTerm, "i"),
						new Criteria("patientLabTestSamples.patientName").regex(searchTerm, "i"));
			}

			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("id", "$id"), Fields.field("daughterLabCRN", "$daughterLabCRN"),
							Fields.field("pickupTime", "$pickupTime"), Fields.field("deliveryTime", "$deliveryTime"),
							Fields.field("patientLabTestSamples.patientName", "$patientLabTestSamples.patientName"),
							Fields.field("patientLabTestSamples.uid", "$patientLabTestSamples.uid"),
							Fields.field("patientLabTestSamples.mobileNumber", "$patientLabTestSamples.mobileNumber"),
							Fields.field("patientLabTestSamples.age", "$patientLabTestSamples.age"),
							Fields.field("patientLabTestSamples.gender", "$patientLabTestSamples.gender"),
							Fields.field("patientLabTestSamples.labTestSamples", "$labTestSamples"),
							Fields.field("status", "$status"), Fields.field("doctorId", "$doctorId"),
							Fields.field("parentLabLocationId", "$parentLabLocationId"),
							Fields.field("daughterLabLocationId", "$daughterLabLocationId"),
							Fields.field("parentLab", "$parentLab"), Fields.field("collectionBoy", "$collectionBoy"),
							Fields.field("collectionBoyId", "$collectionBoyId"),
							Fields.field("daughterLab", "$daughterLab"), Fields.field("discarded", "$discarded"),
							Fields.field("numberOfSamplesRequested", "$numberOfSamplesRequested"),
							Fields.field("numberOfSamplesPicked", "$numberOfSamplesPicked"),
							Fields.field("requestId", "$requestId"), Fields.field("isCompleted", "$isCompleted"),
							Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation aggregationOperation1 = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$_id").append("patientName", "$patientLabTestSamples.patientName")
									.append("uid", "$patientLabTestSamples.uid").append("mobileNumber",
											"$patientLabTestSamples.mobileNumber"))
													.append("daughterLabCRN",
															new BasicDBObject("$first", "$daughterLabCRN"))
													.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
													.append("deliveryTime",
															new BasicDBObject("$first", "$deliveryTime"))
													.append("patientLabTestSamples",
															new BasicDBObject("$first", "$patientLabTestSamples"))
													.append("status", new BasicDBObject("$first", "$status"))
													.append("doctorId", new BasicDBObject("$first", "$doctorId"))
													.append("parentLab", new BasicDBObject("$first", "$parentLab"))
													.append("collectionBoy",
															new BasicDBObject("$first", "$collectionBoy"))
													.append("collectionBoyId",
															new BasicDBObject("$first", "$collectionBoyId"))
													.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
													.append("discarded", new BasicDBObject("$first", "$discarded"))
													.append("numberOfSamplesRequested",
															new BasicDBObject("$first", "$numberOfSamplesRequested"))
													.append("numberOfSamplesPicked",
															new BasicDBObject("$first", "$numberOfSamplesPicked"))
													.append("requestId", new BasicDBObject("$first", "$requestId"))
													.append("parentLabLocationId",
															new BasicDBObject("$first", "$parentLabLocationId"))
													.append("daughterLabLocationId",
															new BasicDBObject("$first", "$daughterLabLocationId"))
													.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
													.append("createdTime", new BasicDBObject("$first", "$createdTime"))
													.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
													.append("createdBy", new BasicDBObject("$first", "$createdBy"))
													.append("labTestSamples",
															new BasicDBObject("$push", "$labTestSamples"))));
			CustomAggregationOperation aggregationOperation2 = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id", "$_id.id")
									.append("daughterLabCRN", new BasicDBObject("$first", "$daughterLabCRN"))
									.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
									.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
									.append("patientLabTestSamples",
											new BasicDBObject("$push", "$patientLabTestSamples"))
									.append("status", new BasicDBObject("$first", "$status"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("parentLab", new BasicDBObject("$first", "$parentLab"))
									.append("collectionBoy", new BasicDBObject("$first", "$collectionBoy"))
									.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
									.append("daughterLab", new BasicDBObject("$first", "$daughterLab"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("numberOfSamplesRequested",
											new BasicDBObject("$first", "$numberOfSamplesRequested"))
									.append("numberOfSamplesPicked",
											new BasicDBObject("$first", "$numberOfSamplesPicked"))
									.append("requestId", new BasicDBObject("$first", "$requestId"))
									.append("parentLabLocationId", new BasicDBObject("$first", "$parentLabLocationId"))
									.append("daughterLabLocationId",
											new BasicDBObject("$first", "$daughterLabLocationId"))
									.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("patientLabTestSamples"),
						Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds", "_id",
								"labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("parentLab"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(orOperator), aggregationOperation1, projectList, aggregationOperation2,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.unwind("labTestSampleIds"),
						Aggregation.lookup("lab_test_sample_cl", "labTestSampleIds", "_id", "labTestSamples"),
						Aggregation.unwind("labTestSamples"),
						Aggregation.lookup("location_cl", "daughterLabLocationId", "_id", "daughterLab"),
						Aggregation.unwind("daughterLab"),
						Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLab"),
						Aggregation.unwind("parentLab"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(orOperator), aggregationOperation1, projectList, aggregationOperation2,

						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabTestPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabTestPickupCollection.class, LabTestPickupLookupResponse.class);
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
		LabTestPickupCollection labTestPickupCollection = null;
		List<ObjectId> labTestSampleIds = null;
		List<LabTestSample> labTestSamples = null;
		List<PatientLabTestItem> items = new ArrayList<PatientLabTestItem>();
		List<PatientLabTestSample> patientLabTestSamples = new ArrayList<PatientLabTestSample>();
		PatientLabTestSample patientLabTestSample = null;
		String requestId = null;
		PatientLabTestItem item = null;

		try {

			if (request.getId() != null) {
				labTestPickupCollection = labTestPickupRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (labTestPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}

				BeanUtil.map(request, labTestPickupCollection);

				for (PatientLabTestsampleRequest patientLabTestsampleRequest : request.getPatientLabTestSamples()) {
					item = new PatientLabTestItem();
					patientLabTestSample = new PatientLabTestSample();
					labTestSampleIds = new ArrayList<ObjectId>();
					labTestSamples = new ArrayList<LabTestSample>();
					BeanUtil.map(patientLabTestsampleRequest, item);
					BeanUtil.map(patientLabTestsampleRequest, patientLabTestSample);
					for (LabTestSample labTestSample : patientLabTestsampleRequest.getLabTestSamples()) {
						if (labTestSample.getId() != null) {
							LabTestSampleCollection OldlabTestSampleCollection = labTestSampleRepository
									.findById(new ObjectId(labTestSample.getId())).orElse(null);
							if (OldlabTestSampleCollection == null) {
								throw new BusinessException(ServiceError.InvalidInput, "invalid lab Test sample Id");
							}
							;
							LabTestSampleCollection labTestSampleCollection = new LabTestSampleCollection();

							BeanUtil.map(labTestSample, labTestSampleCollection);
							labTestSampleCollection.setCreatedBy(OldlabTestSampleCollection.getCreatedBy());
							labTestSampleCollection.setCreatedTime(OldlabTestSampleCollection.getCreatedTime());
							labTestSampleCollection
									.setRateCardTestAssociation(labTestSample.getRateCardTestAssociation());

							labTestSampleCollection.setLabTestPickUpId(new ObjectId(request.getId()));
							labTestSampleCollection.setIsCompleted(request.getIsCompleted());
							labTestSampleCollection.setUpdatedTime(new Date());

							if (labTestSampleCollection.getIsCompleted()
									&& labTestSampleCollection.getIsCollectedAtLab()
									&& !DPDoctorUtils.allStringsEmpty(labTestSampleCollection.getParentLabLocationId())
									&& DPDoctorUtils.anyStringEmpty(OldlabTestSampleCollection.getSerialNumber())) {
								String serialNumber = reportSerialNumberGenerator(
										labTestSampleCollection.getParentLabLocationId().toString());
		labTestSampleCollection.setSerialNumber(serialNumber);
							}
							labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
							labTestSampleIds.add(labTestSampleCollection.getId());
							BeanUtil.map(labTestSampleCollection, labTestSample);
						} else {
							labTestSample.setSampleId(
									UniqueIdInitial.LAB_PICKUP_SAMPLE.getInitial() + DPDoctorUtils.generateRandomId());
							LabTestSampleCollection labTestSampleCollection = new LabTestSampleCollection();
							BeanUtil.map(labTestSample, labTestSampleCollection);
							labTestSampleCollection.setLabTestPickUpId(new ObjectId(request.getId()));
							labTestSampleCollection.setCreatedTime(new Date());
							labTestSampleCollection.setUpdatedTime(new Date());
							labTestSampleCollection.setIsCompleted(request.getIsCompleted());
							if (labTestSampleCollection.getIsCompleted()
									&& labTestSampleCollection.getIsCollectedAtLab()
									&& !DPDoctorUtils.allStringsEmpty(labTestSampleCollection.getParentLabLocationId())
									&& DPDoctorUtils.anyStringEmpty(labTestSampleCollection.getSerialNumber())) {
								String serialNumber = reportSerialNumberGenerator(
										labTestSampleCollection.getParentLabLocationId().toString());
								labTestSampleCollection.setSerialNumber(serialNumber);
							}
							labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
							labTestSampleIds.add(labTestSampleCollection.getId());
							BeanUtil.map(labTestSampleCollection, labTestSample);
						}
						labTestSamples.add(labTestSample);

					}
					patientLabTestSample.setLabTestSamples(labTestSamples);
					item.setLabTestSampleIds(labTestSampleIds);
					patientLabTestSamples.add(patientLabTestSample);
					items.add(item);
				}
				labTestPickupCollection.setPatientLabTestSamples(items);
				labTestPickupCollection.setUpdatedTime(new Date());
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			} else {
				requestId = UniqueIdInitial.LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setDaughterLabCRN(saveCRN(request.getDaughterLabLocationId(), requestId, 5));
				for (PatientLabTestsampleRequest patientLabTestsampleRequest : request.getPatientLabTestSamples()) {
					patientLabTestsampleRequest.setUid("PLT" + DPDoctorUtils.generateRandomId());
					item = new PatientLabTestItem();
					patientLabTestSample = new PatientLabTestSample();
					labTestSamples = new ArrayList<LabTestSample>();
					labTestSampleIds = new ArrayList<ObjectId>();
					BeanUtil.map(patientLabTestsampleRequest, item);
					BeanUtil.map(patientLabTestsampleRequest, patientLabTestSample);
					for (LabTestSample labTestSample : patientLabTestsampleRequest.getLabTestSamples()) {
						labTestSample.setSampleId(
								UniqueIdInitial.LAB_PICKUP_SAMPLE.getInitial() + DPDoctorUtils.generateRandomId());
						LabTestSampleCollection labTestSampleCollection = new LabTestSampleCollection();
						BeanUtil.map(labTestSample, labTestSampleCollection);

						labTestSampleCollection.setIsCollected(request.getIsCompleted());
						labTestSampleCollection.setCreatedTime(new Date());
						labTestSampleCollection.setUpdatedTime(new Date());
						if (labTestSampleCollection.getIsCompleted() && labTestSampleCollection.getIsCollectedAtLab()
								&& !DPDoctorUtils.allStringsEmpty(labTestSampleCollection.getParentLabLocationId())
								&& DPDoctorUtils.anyStringEmpty(labTestSampleCollection.getSerialNumber())) {
							String serialNumber = reportSerialNumberGenerator(
									labTestSampleCollection.getParentLabLocationId().toString());
							labTestSampleCollection.setSerialNumber(serialNumber);
						}
						labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
						BeanUtil.map(labTestSampleCollection, labTestSample);
						labTestSampleIds.add(labTestSampleCollection.getId());
						labTestSamples.add(labTestSample);
					}
					patientLabTestSample.setLabTestSamples(labTestSamples);
					item.setLabTestSampleIds(labTestSampleIds);
					patientLabTestSamples.add(patientLabTestSample);
					items.add(item);
				}
				labTestPickupCollection = new LabTestPickupCollection();
				BeanUtil.map(request, labTestPickupCollection);
				labTestPickupCollection.setRequestId(requestId);
				labTestPickupCollection.setPatientLabTestSamples(items);

				DynamicCollectionBoyAllocationCollection dynamicCollectionBoyAllocationCollection = dynamicCollectionBoyAllocationRepository
						.getByAssignorAssignee(new ObjectId(request.getParentLabLocationId()),
								new ObjectId(request.getDaughterLabLocationId()));

				if (dynamicCollectionBoyAllocationCollection != null && (dynamicCollectionBoyAllocationCollection
						.getFromTime() <= System.currentTimeMillis()
						&& System.currentTimeMillis() <= dynamicCollectionBoyAllocationCollection.getToTime())) {
					CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
							.findbyParentIdandDaughterId(dynamicCollectionBoyAllocationCollection.getCollectionBoyId(),
									new ObjectId(request.getParentLabLocationId()),
									new ObjectId(request.getDaughterLabLocationId()));
					if (collectionBoyLabAssociationCollection != null
							&& collectionBoyLabAssociationCollection.getIsActive() == true) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(dynamicCollectionBoyAllocationCollection.getCollectionBoyId()).orElse(null);
						if (collectionBoyCollection != null) {
							labTestPickupCollection
									.setCollectionBoyId(dynamicCollectionBoyAllocationCollection.getCollectionBoyId());

							pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
									null, null, RoleEnum.COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
						}
					}

				} else {
					CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
							.findbyParentIdandDaughterIdandIsActive(new ObjectId(request.getParentLabLocationId()),
									new ObjectId(request.getDaughterLabLocationId()), true);
					if (collectionBoyLabAssociationCollection != null) {
						labTestPickupCollection
								.setCollectionBoyId(collectionBoyLabAssociationCollection.getCollectionBoyId());
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(collectionBoyLabAssociationCollection.getCollectionBoyId()).orElse(null);
						pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null,
								null, RoleEnum.COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);


					}
				}
				labTestPickupCollection.setCreatedTime(new Date());
				labTestPickupCollection.setIsCompleted(false);
				labTestPickupCollection.setStatus(request.getStatus());
				labTestPickupCollection.setUpdatedTime(new Date());
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);

			}

			response = new LabTestPickup();
			BeanUtil.map(labTestPickupCollection, response);
			response.setPatientLabTestSamples(patientLabTestSamples);
		} catch (Exception e) {
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
	public List<CollectionBoyResponse> getCollectionBoyList(int size, long page, String locationId, String searchTerm , String labType) {
		List<CollectionBoyResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("name").regex(searchTerm, "i"));
			}
			 if (!DPDoctorUtils.anyStringEmpty(labType))
			{
				criteria.and("labType").is(labType);
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<CollectionBoyResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyCollection.class, CollectionBoyResponse.class);
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
	public Integer getCBCount(String locationId, String searchTerm , String labType) {
		Integer count = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("mobileNumber").regex(searchTerm, "i"),
						new Criteria("name").regex(searchTerm, "i"));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(labType)) {
				criteria.and("labType").is(labType);
			}
			criteria.and("locationId").is(new ObjectId(locationId));

			
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<CollectionBoy> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyCollection.class, CollectionBoy.class);
			count = aggregationResults.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Collection Boys");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Collection Boys");
		}
		return count;
	}

	@Override
	@Transactional
	public List<Location> addCollectionBoyAssociatedLabs(
			List<CollectionBoyLabAssociation> collectionBoyLabAssociations) {
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
						.findbyParentIdandDaughterId(new ObjectId(collectionBoyLabAssociation.getParentLabId()),
								new ObjectId(collectionBoyLabAssociation.getDaughterLabId()));
				if (collectionBoyLabAssociationCollection == null) {
					collectionBoyLabAssociationCollection = new CollectionBoyLabAssociationCollection();
					BeanUtil.map(collectionBoyLabAssociation, collectionBoyLabAssociationCollection);
				} else {
					if (!collectionBoyLabAssociationCollection.getCollectionBoyId()
							.equals(new ObjectId(collectionBoyLabAssociation.getCollectionBoyId()))
							&& collectionBoyLabAssociationCollection.getIsActive() == true) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(collectionBoyLabAssociationCollection.getCollectionBoyId()).orElse(null);
						LocationCollection locationCollection = locationRepository
								.findById(collectionBoyLabAssociationCollection.getDaughterLabId()).orElse(null);
						// throw new Exception("Collection boy " +
						// collectionBoyCollection.getName() + " is already
						// assigned to " + locationCollection.getLocationName()
						// + ". Please select another lab / collection boy");
						throw new BusinessException(ServiceError.Unknown,
								"Collection boy " + collectionBoyCollection.getName() + " is already assigned to "
										+ locationCollection.getLocationName()
										+ ". Please select another lab / collection boy");
					}
					ObjectId oldId = collectionBoyLabAssociationCollection.getId();

					// collectionBoyLabAssociation.getCollectionBoyId().toString().equals(anObject)
					BeanUtil.map(collectionBoyLabAssociation, collectionBoyLabAssociationCollection);
					collectionBoyLabAssociationCollection.setId(oldId);
				}
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
					Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
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
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return locations;
	}

	@Override
	@Transactional
	public List<Location> getCBAssociatedLabs(String parentLabId, String daughterLabId, String collectionBoyId,
			int size, long page) {
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
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

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
			e.printStackTrace();
			logger.warn(e);
		}
		return locations;
	}

	@Override
	@Transactional
	public List<Location> getAssociatedLabs(String locationId, Boolean isParent, String searchTerm, long page,
			int size) {

		List<LabAssociationLookupResponse> lookupResponses = null;
		List<Location> locations = null;
		ObjectId locationObjectId = new ObjectId(locationId);
		try {
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "location not found");
			}
			Criteria criteria = new Criteria();
			criteria.and("isActive").is(Boolean.TRUE);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("location.locationName").regex(searchTerm, "i"));
			}
			Aggregation aggregation = null;

			if (size > 0) {
				if (isParent) {
					criteria.and("parentLabId").is(locationObjectId);
					aggregation = Aggregation.newAggregation(
							Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "updatedTime"), Aggregation.skip((page) * size),
							Aggregation.limit(size));

				} else {
					criteria.and("daughterLabId").is(locationObjectId);
					aggregation = Aggregation.newAggregation(
							Aggregation.lookup("location_cl", "parentLabId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "updatedTime"), Aggregation.skip((page) * size),
							Aggregation.limit(size));

				}
			} else {
				if (isParent) {
					criteria.and("parentLabId").is(locationObjectId);
					aggregation = Aggregation.newAggregation(
							Aggregation.lookup("location_cl", "daughterLabId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "updatedTime"));

				} else {
					criteria.and("daughterLabId").is(locationObjectId);
					aggregation = Aggregation.newAggregation(
							Aggregation.lookup("location_cl", "parentLabId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "updatedTime"));

				}
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
				rateCardCollection = rateCardRepository.findById(new ObjectId(request.getId())).orElse(null);
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
	public List<RateCard> getRateCards(long page, int size, String searchTerm, String locationId) {
		List<RateCard> rateCards = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex(searchTerm,"i"));
			}
			criteria.and("locationId").is(new ObjectId(locationId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
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
	public Integer getRateCardCount(String searchTerm, String locationId) {
		Integer count = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex(searchTerm, "i"));
			}
			criteria.and("locationId").is(new ObjectId(locationId));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<RateCard> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardCollection.class, RateCard.class);
			count = aggregationResults.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return count;
	}

	@Override
	@Transactional
	public Boolean addEditRateCardTestAssociation(List<RateCardTestAssociation> request) {
		boolean response = false;
		RateCardTestAssociationCollection rateCardTestAssociationCollection = null;
		List<RateCardTestAssociationCollection> rateCardTestAssociationCollections = null;
		try {
			rateCardTestAssociationCollections = new ArrayList<RateCardTestAssociationCollection>();
			for (RateCardTestAssociation rateCardTestAssociation : request) {
				if (rateCardTestAssociation.getId() != null) {
					rateCardTestAssociationCollection = rateCardTestAssociationRepository
							.findById(new ObjectId(rateCardTestAssociation.getId())).orElse(null);
					rateCardTestAssociation.setCreatedTime(new Date());

				} else {
					rateCardTestAssociationCollection = new RateCardTestAssociationCollection();
					rateCardTestAssociation.setCreatedTime(new Date());
					rateCardTestAssociation.setUpdatedTime(new Date());
				}

				BeanUtil.map(rateCardTestAssociation, rateCardTestAssociationCollection);
				rateCardTestAssociationCollections.add(rateCardTestAssociationCollection);
			}
			rateCardTestAssociationCollections = (List<RateCardTestAssociationCollection>) rateCardTestAssociationRepository
					.saveAll(rateCardTestAssociationCollections);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error adding / editing ratecard");
			throw new BusinessException(ServiceError.Unknown, "Error adding / editing ratecard");
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardTestAssociationLookupResponse> getRateCardTests(int page, int size, String searchTerm,
			String rateCardId, String labId, Boolean discarded) {
		List<RateCardTestAssociationLookupResponse> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("diagnosticTest.testName").regex(searchTerm, "i"));
			}
			criteria.and("rateCardId").is(new ObjectId(rateCardId));
			criteria.and("isAvailable").is(true);
			criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(labId)) {
				criteria.and("labId").is(new ObjectId(labId));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),
						/*
						 * Aggregation.lookup("specimen_cl",
						 * "diagnosticTest.specimenId", "_id", "specimen"),
						 * Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
						Aggregation.unwind("diagnosticTest"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardTestAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, RateCardTestAssociationCollection.class, RateCardTestAssociationLookupResponse.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public List<RateCardTestAssociationByLBResponse> getRateCardTests(long page, int size, String searchTerm,
			String daughterLabId, String parentLabId, String labId, String specimen) {
		// List<RateCardTestAssociationLookupResponse> rateCardTests = null;
		// RateCardTestAssociationLookupResponse rateCardTestAssociation = null;
		// List<RateCardTestAssociationLookupResponse> specialRateCardsTests =
		// null;
		ObjectId rateCardId = null;
		// List<RateCardTestAssociationLookupResponse> responses = null;

		List<RateCardTestAssociationByLBResponse> rateCardTestAssociationLookupResponses = null;
		try {
			RateCardLabAssociationCollection rateCardLabAssociationCollection = rateCardLabAssociationRepository
					.getByLocation(new ObjectId(daughterLabId), new ObjectId(parentLabId));
			if (rateCardLabAssociationCollection == null) {
				return rateCardTestAssociationLookupResponses;
			} else {
				if (rateCardLabAssociationCollection.getDiscarded() == true) {
					RateCardCollection rateCardCollection = rateCardRepository
							.getDefaultRateCard(new ObjectId(parentLabId));
					if (rateCardCollection != null) {
						rateCardId = rateCardCollection.getId();
					} else {
						return rateCardTestAssociationLookupResponses;
					}
				} else {
					rateCardId = rateCardLabAssociationCollection.getRateCardId();
				}
			}
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("testName").regex(searchTerm, "i"));
			}
			criteria.and("rateCardTest.rateCardId").is(rateCardId);
			criteria.and("rateCardTest.discarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(specimen)) {
				criteria = criteria
						.andOperator(new Criteria().orOperator(new Criteria("specimen").regex(specimen, "i")));
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("specimen", "$specimen"),
					Fields.field("rateCardTest._id", "$rateCardTest._id"),
					Fields.field("rateCardTest.locationId", "$rateCardTest.locationId"),
					Fields.field("rateCardTest.hospitalId", "$rateCardTest.hospitalId"),
					Fields.field("rateCardTest.rateCardId", "$rateCardTest.rateCardId"),
					Fields.field("rateCardTest.diagnosticTestId", "$rateCardTest.diagnosticTestId"),
					Fields.field("rateCardTest.turnaroundTime", "$rateCardTest.turnaroundTime"),
					Fields.field("rateCardTest.cost", "$rateCardTest.cost"),
					Fields.field("rateCardTest.category", "$rateCardTest.category"),
					Fields.field("rateCardTest.labId", "$rateCardTest.labId"),
					Fields.field("rateCardTest.isAvailable", "$rateCardTest.isAvailable"),
					Fields.field("rateCardTest.discarded", "$rateCardTest.discarded"),
					Fields.field("rateCardTest.diagnosticTest", "$diagnosticTest"),
					Fields.field("createdTime", "$createdTime")));

			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("specimen", "$specimen"))
							.append("rateCards", new BasicDBObject("$push", "$rateCardTest")).append("createdTime",
									new BasicDBObject("$first", "$createdTime"))));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_test_association_cl", "_id", "diagnosticTestId", "rateCardTest"),
						Aggregation.unwind("rateCardTest"),
						Aggregation.lookup("diagnostic_test_cl", "rateCardTest.diagnosticTestId", "_id",
								"diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),
						/*
						 * Aggregation.lookup("specimen_cl",
						 * "diagnosticTest.specimenId", "_id", "specimen"),
						 * Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), projectList, aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			}else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_test_association_cl", "_id", "diagnosticTestId", "rateCardTest"),
						Aggregation.unwind("rateCardTest"),
						Aggregation.lookup("diagnostic_test_cl", "rateCardTest.diagnosticTestId", "_id",
								"diagnosticTest"),
						Aggregation.unwind("diagnosticTest"),
						/*
						 * Aggregation.lookup("specimen_cl",
						 * "diagnosticTest.specimenId", "_id", "specimen"),
						 * Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), projectList, aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<RateCardTestAssociationByLBResponse> aggregationResults = mongoTemplate.aggregate(
						aggregation, DiagnosticTestCollection.class, RateCardTestAssociationByLBResponse.class);
				rateCardTestAssociationLookupResponses = aggregationResults.getMappedResults();
			}
			/*
			 * if (!DPDoctorUtils.anyStringEmpty(labId)) { aggregation =
			 * Aggregation.newAggregation(
			 * Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId",
			 * "_id", "diagnosticTest"), Aggregation.unwind("diagnosticTest"),
			 * Aggregation.match(criteria.and("labId").is(new ObjectId(labId))),
			 * Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			 * AggregationResults<RateCardTestAssociationLookupResponse> results
			 * = mongoTemplate.aggregate(aggregation,
			 * RateCardTestAssociationCollection.class,
			 * RateCardTestAssociationLookupResponse.class);
			 * specialRateCardsTests = results.getMappedResults(); } if
			 * (rateCardTestAssociationLookupResponses != null) { rateCardTests
			 * = new ArrayList<RateCardTestAssociationLookupResponse>(); for
			 * (RateCardTestAssociationLookupResponse lookupResponse :
			 * rateCardTestAssociationLookupResponses) { if
			 * (specialRateCardsTests != null) { for
			 * (RateCardTestAssociationLookupResponse specialRateCard :
			 * specialRateCardsTests) { if
			 * (lookupResponse.getDiagnosticTestId().equals(specialRateCard.
			 * getDiagnosticTestId())) { BeanUtil.map(specialRateCard,
			 * lookupResponse); } } } rateCardTestAssociation = new
			 * RateCardTestAssociationLookupResponse();
			 * BeanUtil.map(lookupResponse, rateCardTestAssociation);
			 * rateCardTests.add(rateCardTestAssociation); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return rateCardTestAssociationLookupResponses;
	}

	@Override
	@Transactional
	public RateCardLabAssociation addEditRateCardAssociatedLab(RateCardLabAssociation rateCardLabAssociation) {
		RateCardLabAssociation response = null;
		ObjectId oldId = null;
		RateCardLabAssociationCollection rateCardLabAssociationCollection = null;
		try {
			rateCardLabAssociationCollection = rateCardLabAssociationRepository.getByLocation(
					new ObjectId(rateCardLabAssociation.getDaughterLabId()),
					new ObjectId(rateCardLabAssociation.getParentLabId()));
			if (rateCardLabAssociationCollection == null) {
				rateCardLabAssociationCollection = new RateCardLabAssociationCollection();
			} else {
				oldId = rateCardLabAssociationCollection.getId();
				// rateCardLabAssociationCollection.setId(rateCardLabAssociationCollection.getId());
			}

			BeanUtil.map(rateCardLabAssociation, rateCardLabAssociationCollection);
			rateCardLabAssociationCollection.setId(oldId);
			rateCardLabAssociationCollection = rateCardLabAssociationRepository.save(rateCardLabAssociationCollection);
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
	public RateCardLabAssociation getRateCardAssociatedLab(String daughterLabId, String parentLabId) {
		RateCardLabAssociation response = null;
		RateCardLabAssociationCollection rateCardLabAssociationCollection = null;
		RateCardCollection rateCardCollection = null;
		try {
			rateCardLabAssociationCollection = rateCardLabAssociationRepository
					.getByLocation(new ObjectId(daughterLabId), new ObjectId(parentLabId));
			if (rateCardLabAssociationCollection != null) {
				rateCardCollection = rateCardRepository.findById(rateCardLabAssociationCollection.getId()).orElse(null);
			} else {
				rateCardCollection = rateCardRepository.getDefaultRateCard(new ObjectId(parentLabId));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<Location> getClinics(long page, int size, String hospitalId, Boolean isClinic, Boolean isLab,
			Boolean isParent,Boolean isDentalWorksLab ,Boolean isDentalImagingLab,  String searchTerm) {
		List<Location> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {

				criteria = criteria.andOperator(new Criteria("hospitalId").is(new ObjectId(hospitalId)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("locationName").regex(searchTerm, "i"));
			}

			if (isClinic != null) {
				criteria.and("isClinic").is(isClinic);
			}

			if (isLab != null) {
				criteria.and("isLab").is(isLab);
			}
			if (isParent != null) {
				criteria.and("isParent").is(isParent);
			}
			
			if (isDentalWorksLab != null) {
				criteria.and("isDentalWorksLab").is(isDentalWorksLab);
			}
			
			if (isDentalImagingLab != null) {
				criteria.and("isDentalImagingLab").is(isDentalImagingLab);
			}

			if (isDentalWorksLab != null) {
				criteria.and("isDentalWorksLab").is(isDentalWorksLab);
			}

			if (isDentalImagingLab != null) {
				criteria.and("isDentalImagingLab").is(isDentalImagingLab);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}

			AggregationResults<Location> results = mongoTemplate.aggregate(aggregation, LocationCollection.class,
					Location.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting doctors " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctors " + e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Specimen> getSpecimenList(long page, int size, String searchTerm) {
		List<Specimen> specimens = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("specimen").regex(searchTerm, "i"));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Specimen> aggregationResults = mongoTemplate.aggregate(aggregation,
					SpecimenCollection.class, Specimen.class);
			specimens = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Specimens");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Specimens");
		}
		return specimens;
	}

	@Override
	@Transactional
	public CollectionBoy editCollectionBoy(CollectionBoy collectionBoy) {
		CollectionBoy response = null;
		CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
				.findById(new ObjectId(collectionBoy.getId())).orElse(null);
		if (collectionBoyCollection != null) {
			collectionBoyCollection.setAddress(collectionBoy.getAddress());
			collectionBoyCollection.setAge(collectionBoy.getAge());
			collectionBoyCollection.setGender(collectionBoy.getGender());
			collectionBoyCollection.setMobileNumber(collectionBoy.getMobileNumber());
			collectionBoyCollection.setName(collectionBoy.getName());
			collectionBoyCollection = collectionBoyRepository.save(collectionBoyCollection);
			response = new CollectionBoy();
			BeanUtil.map(collectionBoyCollection, response);
			response.setPassword(null);
		}
		return response;
	}

	@Override
	public RateCardTestAssociation addEditRateCardTestAssociation(RateCardTestAssociation request) {
		return null;
	}

	@Override
	@Transactional
	public RateCard getDLRateCard(String daughterLabId, String parentLabId) {
		RateCard response = null;
		RateCardCollection rateCardCollection = null;
		RateCardLabAssociationCollection rateCardLabAssociationCollection = rateCardLabAssociationRepository
				.getByLocation(new ObjectId(daughterLabId), new ObjectId(parentLabId));
		if (rateCardLabAssociationCollection != null && rateCardLabAssociationCollection.getDiscarded() == false) {
			rateCardCollection = rateCardRepository.findById(rateCardLabAssociationCollection.getRateCardId()).orElse(null);
		}

		if (rateCardCollection != null) {
			response = new RateCard();
			BeanUtil.map(rateCardCollection, response);
		}
		return response;

	}

	@Override
	@Transactional
	public List<PatientLabTestSampleReportResponse> getLabReports(String locationId, Boolean isParent, Long from,
			Long to, String searchTerm, long page, int size) {
		List<PatientLabTestSampleReportResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (from != 0 && to != 0) {
				criteria.and("labTestSamples.updatedTime").gte(new Date(from))
						.lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("labTestSamples.updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("labTestSamples.updatedTime").lte(DPDoctorUtils.getEndTime(new Date(to)));
			}

			ObjectId locationObjectId = new ObjectId(locationId);
			criteria.and("labTestSamples.isCollected").is(true);
			criteria.and("labTestSamples.isCompleted").is(true);
			criteria.and("labTestSamples.isCollectedAtLab").is(true);
			if (isParent) {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(
							new Criteria("patientLabTestSamples.patientName").regex(searchTerm, "i"),
							new Criteria("daughterLabLocation.locationName").regex(searchTerm, "i"));
				}

				criteria.and("labTestSamples.parentLabLocationId").is(locationObjectId);
			} else {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(
							new Criteria("patientLabTestSamples.patientName").regex(searchTerm, "i"),
							new Criteria("parentLabLocation.locationName").regex(searchTerm, "i"));
				}
				criteria.and("labTestSamples.daughterLabLocationId").is(locationObjectId);

			}
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("uid", "$patientLabTestSamples.uid"),
							Fields.field("patientName", "$patientLabTestSamples.patientName"),
							Fields.field("mobileNumber", "$patientLabTestSamples.mobileNumber"),
							Fields.field("age", "$patientLabTestSamples.age"),
							Fields.field("gender", "$patientLabTestSamples.gender"),
							Fields.field("labTestSamples._id", "$labTestSamples._id"),
							Fields.field("labTestSamples.sampleType", "$labTestSamples.sampleType"),
							Fields.field("labTestSamples.daughterLabLocationId", "$daughterLabLocationId"),
							Fields.field("labTestSamples.parentLabLocationId", "$parentLabLocationId"),
							Fields.field("labTestSamples.doctorId", "$doctorId"),
							Fields.field("labTestSamples.uploadedByDoctorId", "$labReport.uploadedByDoctorId"),
							Fields.field("labTestSamples.uploadedByLocationId", "$labReport.uploadedByLocationId"),
							Fields.field("labTestSamples.uploadedByHospitalId", "$labReport.uploadedByHospitalId"),
							Fields.field("labTestSamples.parentLabLocation", "$parentLabLocation"),
							Fields.field("labTestSamples.daughterLabLocation", "$daughterLabLocation"),
							Fields.field("labTestSamples.rateCardTestAssociation",
									"$labTestSamples.rateCardTestAssociation"),
							Fields.field("labTestSamples.isUrgent", "$labTestSamples.isUrgent"),
							Fields.field("labTestSamples.urgentTime", "$labTestSamples.urgentTime"),
							Fields.field("labTestSamples.isCollectedAtLab", "$labTestSamples.isCollectedAtLab"),
							Fields.field("labTestSamples.isCollected", "$labTestSamples.isCollected"),
							Fields.field("labTestSamples.status", "$labTestSamples.status"),
							Fields.field("labTestSamples.isHardCopyRequired", "$labTestSamples.isHardCopyRequired"),
							Fields.field("labTestSamples.isHardCopyGiven", "$labTestSamples.isHardCopyGiven"),
							Fields.field("labTestSamples.sampleId", "$labTestSamples.sampleId"),
							Fields.field("labTestSamples.labReports", "$labReport.labReports"),
							Fields.field("labTestSamples.serialNumber", "$labTestSamples.serialNumber"),
							Fields.field("labTestSamples.isCompleted", "$labTestSamples.isCompleted"),
							Fields.field("labTestSamples.createdTime", "$labTestSamples.createdTime"),
							Fields.field("labTestSamples.updatedTime", "$labTestSamples.updatedTime"),
							Fields.field("labTestSamples.createdBy", "$labTestSamples.createdBy"),
							Fields.field("createdTime", "$createdTime")));
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(
					new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("uid", "$uid").append("patientName", "$patientName")
											.append("mobileNumber", "$mobileNumber"))
													.append("patientName", new BasicDBObject("$first", "$patientName"))
													.append("mobileNumber",
															new BasicDBObject("$first", "$mobileNumber"))
													.append("age", new BasicDBObject("$first", "$age"))
													.append("gender", new BasicDBObject("$first", "$gender"))
													.append("labTestSamples",
															new BasicDBObject("$push", "$labTestSamples"))
													.append("createdTime",
															new BasicDBObject("$first", "$createdTime"))));
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.unwind("patientLabTestSamples"),
								Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
								Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds",
										"_id", "labTestSamples"),
								Aggregation.unwind("labTestSamples"),
								Aggregation.lookup("lab_reports_cl", "patientLabTestSamples.labTestSampleIds",
										"labTestSampleId", "labReport"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$labReport").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("location_cl", "daughterLabLocationId", "_id",
										"daughterLabLocation"),
								Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLabLocation"),
								Aggregation.unwind("daughterLabLocation"), Aggregation.unwind("parentLabLocation"),
								Aggregation.match(criteria), projectList, aggregationOperation,
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
								Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.unwind("patientLabTestSamples"),
								Aggregation.unwind("patientLabTestSamples.labTestSampleIds"),
								Aggregation.lookup("lab_test_sample_cl", "patientLabTestSamples.labTestSampleIds",
										"_id", "labTestSamples"),
								Aggregation.unwind("labTestSamples"),
								Aggregation.lookup("lab_reports_cl", "patientLabTestSamples.labTestSampleIds",
										"labTestSampleId", "labReport"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$labReport").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("location_cl", "daughterLabLocationId", "_id",
										"daughterLabLocation"),
								Aggregation.lookup("location_cl", "parentLabLocationId", "_id", "parentLabLocation"),
								Aggregation.unwind("daughterLabLocation"), Aggregation.unwind("parentLabLocation"),
								Aggregation.match(criteria), projectList, aggregationOperation,
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}

			AggregationResults<PatientLabTestSampleReportResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, LabTestPickupCollection.class, PatientLabTestSampleReportResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting lab reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting lab reports");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer countLabReports(String locationId, Boolean isParent, Long from, Long to, String searchTerm) {
		Integer response = 0;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (from != 0 && to != 0) {
				criteria.and("labTestSamples.updatedTime").gte(new Date(from))
						.lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.andOperator(Criteria.where("labTestSamples.updatedTime").gte(new Date(from)));
			} else if (to != 0) {
				criteria.andOperator(
						Criteria.where("labTestSamples.updatedTime").lte(DPDoctorUtils.getEndTime(new Date(to))));
			}

			ObjectId locationObjectId = new ObjectId(locationId);
			criteria.and("labTestSamples.isCollected").is(true);
			criteria.and("labTestSamples.isCompleted").is(true);
			criteria.and("labTestSamples.isCollectedAtLab").is(true);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patientName").regex(searchTerm, "i"));
			}

			if (isParent) {
				criteria.and("labTestSamples.parentLabLocationId").is(locationObjectId);

			} else {
				criteria.and("labTestSamples.daughterLabLocationId").is(locationObjectId);

			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$patientId"),
					Fields.field("patientName", "$patient.firstName"),
					Fields.field("mobileNumber", "$patient.mobileNumber"), Fields.field("age", "$patient.age"),
					Fields.field("gender", "$patient.gender"), Fields.field("labTestSamples.id", "$labTestSamples.id"),
					Fields.field("labTestSamples.sampleType", "$labTestSamples.sampleType"),
					Fields.field("labTestSamples.daughterLabLocationId", "$labTestSamples.daughterLabLocationId"),
					Fields.field("labTestSamples.parentLabLocationId", "$labTestSamples.parentLabLocationId"),
					Fields.field("labTestSamples.rateCardTestAssociation", "$labTestSamples.rateCardTestAssociation"),
					Fields.field("labTestSamples.isUrgent", "$labTestSamples.isUrgent"),
					Fields.field("labTestSamples.urgentTime", "$labTestSamples.urgentTime"),
					Fields.field("labTestSamples.isCollectedAtLab", "$labTestSamples.isCollectedAtLab"),
					Fields.field("labTestSamples.isCollected", "$labTestSamples.isCollected"),
					Fields.field("labTestSamples.status", "$labTestSamples.status"),
					Fields.field("labTestSamples.isHardCopyRequired", "$labTestSamples.isHardCopyRequired"),
					Fields.field("labTestSamples.isHardCopyGiven", "$labTestSamples.isHardCopyGiven"),
					Fields.field("labTestSamples.sampleId", "$labTestSamples.sampleId"),
					Fields.field("labTestSamples.labReports", "$labReports"),
					Fields.field("labTestSamples.serialNumber", "$serialNumber"),
					Fields.field("labTestSamples.isCompleted", "$labTestSamples.isCompleted"),
					Fields.field("labTestSamples.createdTime", "$labTestSamples.createdTime"),
					Fields.field("labTestSamples.updatedTime", "$labTestSamples.updatedTime"),
					Fields.field("labTestSamples.createdBy", "$labTestSamples.createdBy")));
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("id", "$id").append("patientName", "$patientName").append("mobileNumber",
									"$mobileNumber")).append("patientName", new BasicDBObject("$first", "$patientName"))
											.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
											.append("age", new BasicDBObject("$first", "$age"))
											.append("gender", new BasicDBObject("$first", "$gender"))
											.append("labTestSamples", new BasicDBObject("$first", "$labTestSamples"))));
			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
					Aggregation.unwind("patient"),
					Aggregation.lookup("lab_test_sample_cl", "labTestSampleId", "_id", "labTestSamples"),
					Aggregation.unwind("labTestSamples"), projectList, Aggregation.match(criteria),
					aggregationOperation, Aggregation.sort(Sort.Direction.ASC, "patientName"));

			AggregationResults<PatientLabTestSampleReportResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, LabReportsCollection.class, PatientLabTestSampleReportResponse.class);
			response = aggregationResults.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting count lab reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting count lab reports");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateRequestStatus(String id, String status) {
		Boolean response = false;
		try {
			LabTestPickupCollection labTestPickupCollection = labTestPickupRepository.findById(new ObjectId(id)).orElse(null);
			labTestPickupCollection.setStatus(status);
			labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabTestGroupResponse> getGroupedLabTests(long page, int size, String searchTerm, String daughterLabId,
			String parentLabId, String labId) {
		List<LabTestGroupResponse> testGroupResponses = null;
		// List<RateCardTestAssociationLookupResponse> responses = null;
		ObjectId rateCardId = null;
		try {

			RateCardLabAssociationCollection rateCardLabAssociationCollection = rateCardLabAssociationRepository
					.getByLocation(new ObjectId(daughterLabId), new ObjectId(parentLabId));
			if (rateCardLabAssociationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			} else {
				if (rateCardLabAssociationCollection.getDiscarded() == true) {
					RateCardCollection rateCardCollection = rateCardRepository
							.getDefaultRateCard(new ObjectId(parentLabId));
					if (rateCardCollection != null) {
						rateCardId = rateCardCollection.getId();
					} else {
						throw new BusinessException(ServiceError.NoRecord, "Association not found");
					}
				} else {
					rateCardId = rateCardLabAssociationCollection.getRateCardId();
				}
			}

			AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("specimen", "$diagnosticTest.specimen"))
							.append("diagnosticTests", new BasicDBObject("$push", "$diagnosticTest")).append("specimen",
									new BasicDBObject("$first", "$diagnosticTest.specimen"))));

			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("diagnosticTest.testName").regex(searchTerm, "i"));
			}
			criteria.and("rateCardId").is(rateCardId);

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("diagnostic_test_cl", "diagnosticTestId", "_id", "diagnosticTest"),
					Aggregation.unwind("diagnosticTest"), Aggregation.match(criteria), aggregationOperation);
			AggregationResults<LabTestGroupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardTestAssociationCollection.class, LabTestGroupResponse.class);
			testGroupResponses = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate cards");
		}
		return testGroupResponses;

	}

	@Override
	@Transactional
	public DentalWork addEditCustomWork(AddEditCustomWorkRequest request) {
		DentalWork response = null;
		try {
			DentalWorkCollection dentalWorkCollection = new DentalWorkCollection();
			BeanUtil.map(request, dentalWorkCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getId())) {
				dentalWorkCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(dentalWorkCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						dentalWorkCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					dentalWorkCollection.setCreatedBy("ADMIN");
				}
			} else {
				DentalWorkCollection oldDentalWorkCollection = dentalWorkRepository
						.findById(dentalWorkCollection.getId()).orElse(null);
				dentalWorkCollection.setCreatedBy(oldDentalWorkCollection.getCreatedBy());
				dentalWorkCollection.setCreatedTime(oldDentalWorkCollection.getCreatedTime());
				dentalWorkCollection.setDiscarded(oldDentalWorkCollection.getDiscarded());
			}
			dentalWorkCollection = dentalWorkRepository.save(dentalWorkCollection);
			response = new DentalWork();
			BeanUtil.map(dentalWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWork> getCustomWorks(long page, int size, String searchTerm) {
		List<DentalWork> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("workName").regex(searchTerm, "i"));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalWork> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorkCollection.class, DentalWork.class);
			customWorks = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate cards");
			throw new BusinessException(ServiceError.Unknown, "Error Getting custom works");
		}
		return customWorks;
	}

	@Override
	@Transactional
	public DentalWork deleteCustomWork(String id, boolean discarded) {
		DentalWork response = null;
		DentalWorkCollection customWorkCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				customWorkCollection = dentalWorkRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (customWorkCollection != null) {
				customWorkCollection.setDiscarded(discarded);
				customWorkCollection = dentalWorkRepository.save(customWorkCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput , "Record not found");
			}
			response = new DentalWork();
			BeanUtil.map(customWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private String reportSerialNumberGenerator(String locationId) {
		String generatedId = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			ObjectId locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			LocationCollection location = locationRepository.findById(locationObjectId).orElse(null);
			if (location == null) {
				throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
			}
			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long startTimeinMillis = start.getMillis();
			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long endTimeinMillis = end.getMillis();
			int reportSize = labTestSampleRepository.findTodaysCompletedReport(locationObjectId, true,
					new Date(startTimeinMillis), new Date(endTimeinMillis));

			generatedId = String.valueOf((reportSize + 1));
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generatedId;
	}

	@Override
	@Transactional
	public DynamicCollectionBoyAllocationResponse allocateCBDynamically(DynamicCollectionBoyAllocationRequest request) {
		DynamicCollectionBoyAllocationCollection dynamicCollectionBoyAllocationCollection = null;
		DynamicCollectionBoyAllocationResponse response = null;
		ObjectId oldId = null;
		try {
			if (request != null) {

				if (request.getRequestId() != null) {
					LabTestPickupCollection labTestPickupCollection = labTestPickupRepository
							.findById(new ObjectId(request.getRequestId())).orElse(null);
					CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
							.findById(labTestPickupCollection.getCollectionBoyId()).orElse(null);
					if (collectionBoyCollection != null) {
						pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null,
								null, RoleEnum.COLLECTION_BOY_REFRESH, COLLECTION_BOY_NOTIFICATION);
					}
					labTestPickupCollection.setCollectionBoyId(new ObjectId(request.getCollectionBoyId()));
					CollectionBoyCollection newCollectionBoyCollection = collectionBoyRepository
							.findById(labTestPickupCollection.getCollectionBoyId()).orElse(null);
					if (collectionBoyCollection != null) {
						pushNotificationServices.notifyPharmacy(newCollectionBoyCollection.getUserId().toString(), null,
								null, RoleEnum.COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
					}
					labTestPickupRepository.save(labTestPickupCollection);
				}
				if (request.getIsFuture() == true) {
					dynamicCollectionBoyAllocationCollection = dynamicCollectionBoyAllocationRepository
							.getByAssignorAssignee(new ObjectId(request.getAssignorId()),
									new ObjectId(request.getAssigneeId()));
					if (dynamicCollectionBoyAllocationCollection == null) {
						dynamicCollectionBoyAllocationCollection = new DynamicCollectionBoyAllocationCollection();
						BeanUtil.map(request, dynamicCollectionBoyAllocationCollection);
						Long toTime = DPDoctorUtils.getEndTimeInMillis(new Date(request.getFromTime()));
						dynamicCollectionBoyAllocationCollection.setToTime(toTime);
						dynamicCollectionBoyAllocationCollection.setCreatedTime(new Date());
					} else {
						oldId = dynamicCollectionBoyAllocationCollection.getId();
						BeanUtil.map(request, dynamicCollectionBoyAllocationCollection);
						Long toTime = DPDoctorUtils.getEndTimeInMillis(new Date(request.getFromTime()));
						dynamicCollectionBoyAllocationCollection.setToTime(toTime);
						dynamicCollectionBoyAllocationCollection.setId(oldId);
					}
					dynamicCollectionBoyAllocationCollection = dynamicCollectionBoyAllocationRepository
							.save(dynamicCollectionBoyAllocationCollection);
					if (dynamicCollectionBoyAllocationCollection != null) {
						response = new DynamicCollectionBoyAllocationResponse();
						BeanUtil.map(dynamicCollectionBoyAllocationCollection, response);
					}
				}
				if (response == null) {
					response = new DynamicCollectionBoyAllocationResponse();
					BeanUtil.map(request, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean makeFavouriteRateCardTest(String locationId, String hospitalId, String diagnosticTestId) {
		Boolean response = false;
		try {

			FavouriteRateCardTestCollection favouriteRateCardTestCollection = favouriteRateCardTestRepositoy
					.findByLocationIdHospitalIdAndTestId(new ObjectId(locationId), new ObjectId(hospitalId),
							new ObjectId(diagnosticTestId));
			if (favouriteRateCardTestCollection != null) {
				favouriteRateCardTestCollection.setDiscarded(!favouriteRateCardTestCollection.getDiscarded());
			} else {
				favouriteRateCardTestCollection = new FavouriteRateCardTestCollection();
				favouriteRateCardTestCollection.setHospitalId(new ObjectId(hospitalId));
				favouriteRateCardTestCollection.setLocationId(new ObjectId(locationId));
				favouriteRateCardTestCollection.setDiagnosticTestId(new ObjectId(diagnosticTestId));
				favouriteRateCardTestCollection.setDiscarded(true);
				favouriteRateCardTestCollection.setCreatedTime(new Date());
				favouriteRateCardTestCollection.setAdminCreatedTime(new Date());
			}
			favouriteRateCardTestCollection.setUpdatedTime(new Date());
			favouriteRateCardTestRepositoy.save(favouriteRateCardTestCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error Getting while make Favourite to Rate Card Test");
			throw new BusinessException(ServiceError.Unknown, "Error Getting while make Favourite to Rate Card Test");
		}
		return response;
	}
}
