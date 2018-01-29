package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorksSample;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabPickupCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardDoctorAssociationCollection;
import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyDoctorAssociationRepository;
import com.dpdocter.repository.DentalLabDoctorAssociationRepository;
import com.dpdocter.repository.DentalLabTestPickupRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.RateCardDentalWorkAssociationRepository;
import com.dpdocter.repository.RateCardDoctorAssociationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.services.DentalLabService;

import common.util.web.DPDoctorUtils;

@Service
public class DentalLabServiceImpl implements DentalLabService {

	@Autowired
	private DentalWorkRepository dentalWorkRepository;
	
	@Autowired
	private DentalLabTestPickupRepository dentalLabTestPickupRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private CRNRepository crnRepository;
	
	@Autowired
	private RateCardDentalWorkAssociationRepository rateCardDentalWorkAssociationRepository;
	
	@Autowired
	private RateCardDoctorAssociationRepository rateCardDoctorAssociationRepository;
	
	@Autowired
	private DentalLabDoctorAssociationRepository dentalLabDoctorAssociationRepository;
	
	@Autowired
	private CollectionBoyDoctorAssociationRepository collectionBoyDoctorAssociationRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	private static Logger logger = Logger.getLogger(DentalLabServiceImpl.class.getName());

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
					UserCollection userCollection = userRepository.findOne(dentalWorkCollection.getDoctorId());
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
						.findOne(dentalWorkCollection.getId());
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
	public List<DentalWork> getCustomWorks(int page, int size, String searchTerm) {
		List<DentalWork> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("workName").regex("^" + searchTerm, "i"),
						new Criteria("workName").regex("^" + searchTerm));
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
			logger.error(e + " Error Getting custom works");
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
				customWorkCollection = dentalWorkRepository.findOne(new ObjectId(id));
			}
			if (customWorkCollection != null) {
				customWorkCollection.setDiscarded(discarded);
				customWorkCollection = dentalWorkRepository.save(customWorkCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalWork();
			BeanUtil.map(customWorkCollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public Boolean changeLabType(String doctorId, String locationId, LabType labType) {
		Boolean response = false;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if(doctorClinicProfileCollection != null)
			{
				doctorClinicProfileCollection.setLabType(labType.getType());
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public DentalLabDoctorAssociation addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		DentalLabDoctorAssociation response = null;
		try {
			DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection= new DentalLabDoctorAssociationCollection();
			BeanUtil.map(request, dentalLabDoctorAssociationCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
				dentalLabDoctorAssociationCollection.setCreatedTime(new Date());
				
			} else {
				DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
						.findOne(dentalLabDoctorAssociationCollection.getId());
				dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
				dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
			}
			dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository.save(dentalLabDoctorAssociationCollection);
			response = new DentalLabDoctorAssociation();
			BeanUtil.map(dentalLabDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@Override
	@Transactional
	public Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request) {
		Boolean response = null;
		try {
			for(DentalLabDoctorAssociation dentalLabDoctorAssociation : request)
			{
			DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection= new DentalLabDoctorAssociationCollection();
			BeanUtil.map(dentalLabDoctorAssociation, dentalLabDoctorAssociationCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
				dentalLabDoctorAssociationCollection.setCreatedTime(new Date());
				
			} else {
				DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
						.findOne(dentalLabDoctorAssociationCollection.getId());
				dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
				dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
			}
			dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository.save(dentalLabDoctorAssociationCollection);
			/*response = new DentalLabDoctorAssociation();
			BeanUtil.map(dentalLabDoctorAssociationCollection, response);*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<User> getDentalLabDoctorAssociations(String locationId , String doctorId,int page, int size, String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		List<User> users = new ArrayList<>();
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId)) 
			{
				criteria = new Criteria().and("dentalLabId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) 
			{
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabDoctorAssociationCollection.class, DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();
			
			for(DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : customWorks)
			{
				users.add(doctorAssociationLookupResponse.getDoctor());
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return users;
	}
	
	@Override
	@Transactional
	public List<Location> getDentalLabDoctorAssociationsForDoctor(String doctorId,int page, int size, String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		List<Location> locations = new ArrayList<>();
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) 
			{
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabDoctorAssociationCollection.class, DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();
			
			for(DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : customWorks)
			{
				locations.add(doctorAssociationLookupResponse.getDentalLab());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return locations;
	}
	
	@Override
	@Transactional
	public DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request)
	{
		DentalLabPickup response = null;
		DentalLabPickupCollection dentalLabPickupCollection = null;
		String requestId = null;
		String initials = "";

		try {

			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getDentalLabId())); 
			if(locationCollection != null)
			{
				String locationName = locationCollection.getLocationName();
				
				for (String firstChar : locationName.split(" ")) {
					initials+= firstChar.charAt(0);
				}
			}
			
			if (request.getId() != null) {
				dentalLabPickupCollection = dentalLabTestPickupRepository.findOne(new ObjectId(request.getId()));
				if (dentalLabPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}

				
				
				BeanUtil.map(request, dentalLabPickupCollection);
				dentalLabPickupCollection.setDentalWorksSamples(request.getDentalWorksSamples());
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setCrn(saveCRN(request.getDentalLabId(), requestId, 5));
				
				dentalLabPickupCollection = new DentalLabPickupCollection();
				BeanUtil.map(request, dentalLabPickupCollection);
				dentalLabPickupCollection.setRequestId(requestId);
				
				for (DentalWorksSample dentalWorksSample : request.getDentalWorksSamples()) {
					dentalWorksSample.setUniqueWorkId(initials);
				}
				
				dentalLabPickupCollection.setCreatedTime(new Date());
				dentalLabPickupCollection.setIsCompleted(false);
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);

			}
			
			//for(request.get)
			/*CollectionBoyLabAssociationCollection collectionBoyLabAssociationCollection = collectionBoyLabAssociationRepository
					.findbyParentIdandDaughterIdandIsActive(new ObjectId(request.getParentLabLocationId()),
							new ObjectId(request.getDaughterLabLocationId()), true);*/
			/*if (collectionBoyLabAssociationCollection != null) {
				dentalLabPickupCollection
						.setCollectionBoyId(collectionBoyLabAssociationCollection.getCollectionBoyId());
				CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
						.findOne(collectionBoyLabAssociationCollection.getCollectionBoyId());
				pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null, null,
						RoleEnum.COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);

				// pushNotificationServices.notifyPharmacy(id, requestId,
				//DentalLabDoctorAssociation responseId, role, message);

			}*/

			response = new DentalLabPickup();
			BeanUtil.map(dentalLabPickupCollection, response);
		//	response.setPatientLabTestSamples(patientLabTestSamples);
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
	public Boolean addEditRateCardDentalWorkAssociation(List<RateCardDentalWorkAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		RateCardDentalWorkAssociationCollection rateCardDentalWorkAssociationCollection = null;
		try {
			for (RateCardDentalWorkAssociation rateCardDentalWorkAssociation : request) {
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.getByLocationWorkRateCard(new ObjectId(rateCardDentalWorkAssociation.getLocationId()),
								new ObjectId(rateCardDentalWorkAssociation.getDentalWorkId()),
								new ObjectId(rateCardDentalWorkAssociation.getRateCardId()));
				if (rateCardDentalWorkAssociationCollection == null) {
					rateCardDentalWorkAssociationCollection = new RateCardDentalWorkAssociationCollection();
				} else {
					oldId = rateCardDentalWorkAssociationCollection.getId();
				}
				BeanUtil.map(rateCardDentalWorkAssociation, rateCardDentalWorkAssociationCollection);
				rateCardDentalWorkAssociationCollection.setId(oldId);
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.save(rateCardDentalWorkAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm,
			String rateCardId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
						new Criteria("dentalWork.workName").regex("^" + searchTerm));
			}
			criteria.and("rateCardId").is(new ObjectId(rateCardId));
			criteria.and("isAvailable").is(true);
		//	criteria.and("discarded").is(discarded);
			

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"),
						/*
						 * Aggregation.lookup("specimen_cl",
						 * "diagnosticTest.specimenId", "_id", "specimen"),
						 * Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDentalWorkAssociation> aggregationResults = mongoTemplate.aggregate(
					aggregation, RateCardDentalWorkAssociationCollection.class, RateCardDentalWorkAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}
	
	@Override
	@Transactional
	public RateCardDoctorAssociation addEditRateCardDoctorAssociation(RateCardDoctorAssociation request) {
		RateCardDoctorAssociation response = null;
		ObjectId oldId = null;
		RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = null;
		try {
			
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.getByLocationDoctor(new ObjectId(request.getDentalLabId()), new ObjectId(request.getDoctorId()));
			if (rateCardDoctorAssociationCollection == null) {
				rateCardDoctorAssociationCollection = new RateCardDoctorAssociationCollection();
			} else {
				oldId = rateCardDoctorAssociationCollection.getId();
				// rateCardLabAssociationCollection.setId(rateCardLabAssociationCollection.getId());
			}

			BeanUtil.map(request, rateCardDoctorAssociationCollection);
			rateCardDoctorAssociationCollection.setId(oldId);
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.save(rateCardDoctorAssociationCollection);
			response = new RateCardDoctorAssociation();
			BeanUtil.map(rateCardDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput , "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardDoctorAssociation> getRateCards(int page, int size, String searchTerm,
			String doctorId, String dentalLabId, Boolean discarded) {
		List<RateCardDoctorAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("rateCard.name").regex("^" + searchTerm, "i"),
						new Criteria("rateCard.name").regex("^" + searchTerm));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			criteria.and("discarded").is(discarded);
			

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"),
						/*
						 * Aggregation.lookup("specimen_cl",
						 * "diagnosticTest.specimenId", "_id", "specimen"),
						 * Aggregation.unwind("specimen"),
						 */
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDoctorAssociation> aggregationResults = mongoTemplate.aggregate(
					aggregation, RateCardDoctorAssociationCollection.class, RateCardDoctorAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}
	
	@Override
	@Transactional
	public Boolean addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request) {
		Boolean response = null;
		try {
			for (CollectionBoyDoctorAssociation collectionBoyDoctorAssociation : request) {
				CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = new CollectionBoyDoctorAssociationCollection();
				BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
				if (DPDoctorUtils.anyStringEmpty(collectionBoyDoctorAssociationCollection.getId())) {
					collectionBoyDoctorAssociationCollection.setCreatedTime(new Date());

				} else {
					CollectionBoyDoctorAssociationCollection oldCollectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
							.getByLocationDoctor(new ObjectId(collectionBoyDoctorAssociation.getDentalLabId()),
									new ObjectId(collectionBoyDoctorAssociation.getDoctorId()));
					if (oldCollectionBoyDoctorAssociationCollection
							.getCollectionBoyId() == collectionBoyDoctorAssociationCollection.getCollectionBoyId()) {
						collectionBoyDoctorAssociationCollection
								.setCreatedBy(oldCollectionBoyDoctorAssociationCollection.getCreatedBy());
						collectionBoyDoctorAssociationCollection
								.setCreatedTime(oldCollectionBoyDoctorAssociationCollection.getCreatedTime());
					} else {
						throw new BusinessException(ServiceError.NotAcceptable,
								"Another Collection boy already assigned");
					}
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.save(collectionBoyDoctorAssociationCollection);
			}
			response =true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	
	
	@Override
	@Transactional
	public List<CBDoctorAssociationLookupResponse> getCBAssociatedDoctors(String doctorId, String dentalLabId, String collectionBoyId,
			int size, int page) {
		List<User> users = null;
		List<CBDoctorAssociationLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyId)) {
				criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			}
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),  Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					CollectionBoyDoctorAssociationCollection.class, CBDoctorAssociationLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();
			/*if (lookupResponses != null) {
				users = new ArrayList<User>();
				for (CBDoctorAssociationLookupResponse lookupResponse : lookupResponses) {
					users.add(lookupResponse.getDoctor());
				}
			}*/

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return lookupResponses;
	}

	
	@Override
	@Transactional
	public List<DentalLabPickupResponse> getRequests(String dentalLabId, String doctorId, Long from,
			Long to, String searchTerm, String status, Boolean isAcceptedAtLab , Boolean isCompleted, int size, int page) {
		
		List<DentalLabPickupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
			if (isAcceptedAtLab != null) {
				criteria.and("isAcceptedAtLab").is(isAcceptedAtLab);
			}

			if (isCompleted != null) {
				criteria.and("isCompleted").is(isCompleted);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteria.and("status").is(status);
			}
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lt(DPDoctorUtils.getEndTime(new Date(to)));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));

			}
			
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						Aggregation.unwind("collectionBoy"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						Aggregation.unwind("collectionBoy"), Aggregation.match(criteria), 
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabPickupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupResponse.class);
			response = aggregationResults.getMappedResults();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	

}
