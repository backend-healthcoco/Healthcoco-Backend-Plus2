package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.fielddata.RamAccountingTermsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorksSample;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabPickupCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.RateCardDoctorAssociationCollection;
import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;
import com.dpdocter.collections.RateCardLabAssociationCollection;
import com.dpdocter.collections.RateCardTestAssociationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.DentalLabDoctorAssociationRepository;
import com.dpdocter.repository.DentalLabTestPickupRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.RateCardDentalWorkAssociationRepository;
import com.dpdocter.repository.RateCardDoctorAssociationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
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
			// TODO: handle exception
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
	public List<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociations(String locationId ,int page, int size, String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria().and("dentalLabId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabDoctorAssociationCollection.class, DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return customWorks;
	}
	
	@Override
	@Transactional
	public DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request)
	{
		DentalLabPickup response = null;
		DentalLabPickupCollection dentalLabPickupCollection = null;
		String requestId = null;

		try {

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
					dentalWorksSample.setUniqueWorkId("");
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
					// responseId, role, message);

				}*/
				dentalLabPickupCollection.setCreatedTime(new Date());
				dentalLabPickupCollection.setIsCompleted(false);
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);

			}

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
		boolean response = false;
		RateCardDentalWorkAssociationCollection rateCardDentalWorkAssociationCollection = null;
		List<RateCardDentalWorkAssociationCollection> rateCardDentalWorkAssociationCollections = null;
		try {
			rateCardDentalWorkAssociationCollections = new ArrayList<RateCardDentalWorkAssociationCollection>();
			for (RateCardDentalWorkAssociation workAssociation : request) {
				if (workAssociation.getId() != null) {
					rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
							.findOne(new ObjectId(workAssociation.getId()));
					rateCardDentalWorkAssociationCollection.setCreatedTime(new Date());
				} else {
					rateCardDentalWorkAssociationCollection = new RateCardDentalWorkAssociationCollection();
					workAssociation.setCreatedTime(new Date());
					workAssociation.setUpdatedTime(new Date());
				}

				BeanUtil.map(workAssociation, rateCardDentalWorkAssociationCollection);
			}
			rateCardDentalWorkAssociationRepository.save(rateCardDentalWorkAssociationCollection);
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
			criteria.and("discarded").is(discarded);
			

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
	public Boolean addEditRateCardDoctorAssociation(List<RateCardDoctorAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = null;
		try {
			for(RateCardDoctorAssociation rateCardDoctorAssociation : request)
			{
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.getByLocationDoctor(new ObjectId(rateCardDoctorAssociation.getDentalLabId()), new ObjectId(rateCardDoctorAssociation.getDoctorId()));
			if (rateCardDoctorAssociationCollection == null) {
				rateCardDoctorAssociationCollection = new RateCardDoctorAssociationCollection();
			} else {
				oldId = rateCardDoctorAssociationCollection.getId();
				// rateCardLabAssociationCollection.setId(rateCardLabAssociationCollection.getId());
			}

			BeanUtil.map(rateCardDoctorAssociation, rateCardDoctorAssociationCollection);
			rateCardDoctorAssociationCollection.setId(oldId);
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.save(rateCardDoctorAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput , "Invalid Input" + e);
		}
		return response;
	}


}
