package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
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

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorksSample;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabPickupCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LabTestSampleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.RateCardDoctorAssociationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyDoctorAssociationRepository;
import com.dpdocter.repository.CollectionBoyRepository;
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
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.PushNotificationServices;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

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
	
	@Autowired
	private CollectionBoyRepository collectionBoyRepository;
	
	@Autowired
	private PushNotificationServices pushNotificationServices;
	
	@Autowired
	private LocationServices locationServices;
	
	@Autowired
	private FileManager fileManager;
	
	@Value("${collection.boy.notification}")
	private String COLLECTION_BOY_NOTIFICATION;
	
	@Value(value = "${image.path}")
	private String imagePath;
	
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
		String locationInitials = "";
		Integer serialNo = null;
		Integer count = 1;

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getDentalLabId())); 
			if(locationCollection != null)
			{
				String locationName = locationCollection.getLocationName();
				
				for (String firstChar : locationName.split(" ")) {
					locationInitials+= firstChar.charAt(0);
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
				serialNo = dentalLabTestPickupRepository.findTodaysCompletedReport(new ObjectId(request.getDentalLabId()), DPDoctorUtils.getFormTime(new Date()), DPDoctorUtils.getToTime(new Date()));
				for (DentalWorksSample dentalWorksSample : request.getDentalWorksSamples()) {
					String uniqueWorkId = locationInitials + getInitials(request.getPatientName()) + currentDateGenerator() + DPDoctorUtils.getPrefixedNumber(serialNo + 1) + DPDoctorUtils.getPrefixedNumber(count);
					dentalWorksSample.setUniqueWorkId(uniqueWorkId);
					count++;
				}
				dentalLabPickupCollection.setDentalWorksSamples(request.getDentalWorksSamples());
				dentalLabPickupCollection.setCrn(saveCRN(request.getDentalLabId(), requestId, 5));
				dentalLabPickupCollection.setSerialNumber(String.valueOf(serialNo + 1));
				dentalLabPickupCollection.setCreatedTime(new Date());
				dentalLabPickupCollection.setIsCompleted(false);
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			}
			CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
					.getByLocationDoctorIsActive(new ObjectId(request.getDentalLabId()), new ObjectId(request.getDoctorId()), true);
			if (collectionBoyDoctorAssociationCollection != null) {
				dentalLabPickupCollection
						.setCollectionBoyId(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
				CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
						.findOne(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
				pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null, null,
						RoleEnum.COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
			}
			response = new DentalLabPickup();
			BeanUtil.map(dentalLabPickupCollection, response);
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
			 String dentalLabId,String doctorId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.getByLocationDoctor(new ObjectId(dentalLabId), new ObjectId(doctorId));
			if(rateCardDoctorAssociationCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			}
			else {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
						new Criteria("dentalWork.workName").regex("^" + searchTerm));
			}
			criteria.and("rateCardId").is(rateCardDoctorAssociationCollection.getRateCardId());
			criteria.and("isAvailable").is(true);
		//	criteria.and("discarded").is(discarded);
			
			}
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
		CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = null;
		try {
			for (CollectionBoyDoctorAssociation collectionBoyDoctorAssociation : request) {
				if (DPDoctorUtils.anyStringEmpty(collectionBoyDoctorAssociation.getCollectionBoyId(),
						collectionBoyDoctorAssociation.getDentalLabId(), collectionBoyDoctorAssociation.getDoctorId())) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Input - Doctor & Dental Lab ID cannot be null");
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.getByLocationDoctor(new ObjectId(collectionBoyDoctorAssociation.getDentalLabId()), new ObjectId(collectionBoyDoctorAssociation.getDoctorId()));
				if (collectionBoyDoctorAssociationCollection == null) {
					collectionBoyDoctorAssociationCollection = new CollectionBoyDoctorAssociationCollection();
					BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
				} else {
					if (!collectionBoyDoctorAssociationCollection.getCollectionBoyId()
							.equals(new ObjectId(collectionBoyDoctorAssociation.getCollectionBoyId()))
							&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findOne(collectionBoyDoctorAssociationCollection.getCollectionBoyId());
						LocationCollection locationCollection = locationRepository
								.findOne(collectionBoyDoctorAssociationCollection.getDentalLabId());
						// throw new Exception("Collection boy " +
						// collectionBoyCollection.getName() + " is already
						// assigned to " + locationCollection.getLocationName()
						// + ". Please select another lab / collection boy");
						throw new BusinessException(ServiceError.Unknown,
								"Collection boy " + collectionBoyCollection.getName() + " is already assigned to "
										+ locationCollection.getLocationName()
										+ ". Please select another lab / collection boy");
					}
					ObjectId oldId = collectionBoyDoctorAssociationCollection.getId();

					BeanUtil.map(collectionBoyDoctorAssociationCollection, collectionBoyDoctorAssociationCollection);
					collectionBoyDoctorAssociationCollection.setId(oldId);
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.save(collectionBoyDoctorAssociationCollection);
			}
			
			response =true;
			
			/*Aggregation aggregation = null;
			Criteria criteria = new Criteria();*/
			/*if (!DPDoctorUtils.anyStringEmpty(request.get(0).getParentLabId())) {
				criteria.and("parentLabId").is(new ObjectId(collectionBoyDoctorAssociationCollection.get(0).getParentLabId()));
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
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
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
			Long to, String searchTerm, String status, Boolean isAcceptedAtLab , Boolean isCompleted, Boolean isCollectedAtDoctor, int size, int page) {
		
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
			
			if (isCollectedAtDoctor != null) {
				criteria.and("isCollectedAtDoctor").is(isCollectedAtDoctor);
			}

			if (isCompleted != null) {
				criteria.and("isCompleted").is(isCompleted);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteria.and("status").is(status);
			}
			
			/*if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lt(DPDoctorUtils.getEndTime(new Date(to)));
			}*/

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
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
						 Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
						 Aggregation.match(criteria), 
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			
			//System.out.println(aggregation);
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
	
	private String currentDateGenerator()
	{
		
		String currentDate = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);
			
			currentDate = DPDoctorUtils.getPrefixedNumber(currentDay)
					+ DPDoctorUtils.getPrefixedNumber(currentMonth) + DPDoctorUtils.getPrefixedNumber(currentYear % 100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return currentDate;
	}
	
	private String getInitials(String inputString) {
		String response = "";
		for (String firstChar : inputString.split(" ")) {
			response+= firstChar.charAt(0);
		}
		return response;
	}
	
	@Override
	@Transactional
	public Boolean changeStatus(String dentalLabPickupId, String status ,Boolean isCollectedAtDoctor ,Boolean isCompleted , Boolean isAcceptedAtLab) {
		DentalLabPickupCollection dentalLabPickupCollection = null;
		Boolean response = null;

		try {
			dentalLabPickupCollection = dentalLabTestPickupRepository.findOne(new ObjectId(dentalLabPickupId));
			if (dentalLabPickupCollection != null) {
				if(status != null)
				{
					dentalLabPickupCollection.setStatus(status);
				}
				if(isCollectedAtDoctor != null)
				{
					dentalLabPickupCollection.setIsCollectedAtDoctor(isCollectedAtDoctor);
				}
				if(isCompleted != null)
				{
					dentalLabPickupCollection.setIsCompleted(isCompleted);
				}
				if(isAcceptedAtLab != null)
				{
					dentalLabPickupCollection.setIsAcceptedAtLab(isAcceptedAtLab);
				}
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public ImageURLResponse addDentalImage(FormDataBodyPart file) {
		ImageURLResponse response = null;
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "dental-images";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public ImageURLResponse addDentalImageBase64(FileDetails fileDetails) {
		ImageURLResponse response = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());
				String path = "dental-images";
				String recordPath = path + File.separator + fileDetails.getFileName() + System.currentTimeMillis() + "." + fileDetails.getFileExtension();
				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, recordPath, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}
	
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}


}
