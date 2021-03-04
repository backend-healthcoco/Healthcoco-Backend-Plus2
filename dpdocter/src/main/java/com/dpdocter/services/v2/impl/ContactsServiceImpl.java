package com.dpdocter.services.v2.impl;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.User;
import com.dpdocter.beans.v2.DoctorContactsResponse;
import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.beans.v2.RegisteredPatientDetails;
import com.dpdocter.collections.ExportContactsRequestCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.ImportContactsRequestCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ExportContactsRequestRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.ImportContactsRequestRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BulkSMSRequest;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.PatientGroupLookupResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.v2.ContactsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "ContactsServiceImplV2")
public class ContactsServiceImpl implements ContactsService {

	private static Logger logger = Logger.getLogger(ContactsServiceImpl.class.getName());

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private PatientGroupRepository patientGroupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImportContactsRequestRepository importContactsRequestRepository;

	@Autowired
	private ExportContactsRequestRepository exportContactsRequestRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private OTPService otpService;

	@Autowired
	private SMSServices smsServices;

	@Value(value = "${Contacts.checkIfGroupIsExistWithSameName}")
	private String checkIfGroupIsExistWithSameName;

	@Value(value = "${Contacts.GroupNotFound}")
	private String groupNotFound;

	/**
	 * This method returns all unblocked or blocked patients (based on param
	 * blocked) of specified doctor.
	 * 
	 * @param doctorId
	 * @param blocked
	 * @return List of Patient cards
	 */
	@Override
	@Transactional
	public DoctorContactsResponse getDoctorContacts(GetDoctorContactsRequest request) {
		DoctorContactsResponse response = null;
		try {
			Collection<ObjectId> patientIds = null;
			if (request.getGroups() != null && !request.getGroups().isEmpty())
				patientIds = getPatientIdsForGroups(request.getGroups());

			if (patientIds == null || patientIds.isEmpty())
				return null;
			response = getSpecifiedPatientCards(patientIds, request.getDoctorId(), request.getLocationId(),
					request.getHospitalId(), request.getPage(), request.getSize(), request.getUpdatedTime(),
					request.getDiscarded(), false, request.getRole());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactsResponse getDoctorContacts(String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded, int page, int size, String role) {
		DoctorContactsResponse response = null;
		try {
			response = getSpecifiedPatientCards(null, doctorId, locationId, hospitalId, page, size, updatedTime,
					discarded, false, role);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactsResponse getDoctorContactsSortedByName(String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, int page, int size, String role) {
		DoctorContactsResponse response = null;
		try {
			response = getSpecifiedPatientCards(null, doctorId, locationId, hospitalId, page, size, updatedTime,
					discarded, true, role);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	@Transactional
	public DoctorContactsResponse getSpecifiedPatientCards(Collection<ObjectId> patientIds, String doctorId,
			String locationId, String hospitalId, long page, int size, String updatedTime, Boolean discarded,
			Boolean sortByFirstName, String role) throws Exception {
		DoctorContactsResponse response = null;
		List<PatientCard> patientCards = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);

		if (discarded)
			discards.add(true);
		long createdTimestamp = Long.parseLong(updatedTime);

		ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			doctorObjectId = new ObjectId(doctorId);
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			locationObjectId = new ObjectId(locationId);
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			hospitalObjectId = new ObjectId(hospitalId);

		Criteria criteria = new Criteria("discarded").in(discards).and("isPatientDiscarded").ne(true);

		if (createdTimestamp > 0)
			criteria.and("updatedTime").gt(new Date(createdTimestamp));
		if (patientIds != null && !patientIds.isEmpty())
			criteria.and("userId").in(patientIds);
		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
			criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
				criteria.and("consultantDoctorIds").is(doctorObjectId);
			} else
				criteria.and("doctorId").is(doctorObjectId);
		}

		Aggregation aggregation = null;
		if (sortByFirstName) {

			CustomAggregationOperation projectOperations = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("userId", "$userId").append("firstName", "$firstName")
							.append("localPatientName", "$localPatientName")
							.append("insensitiveLocalPatientName", new BasicDBObject("$toLower", "$localPatientName"))
							.append("userName", "$userName").append("emailAddress", "$emailAddress")
							.append("imageUrl", "$imageUrl").append("thumbnailUrl", "$thumbnailUrl")
							.append("bloodGroup", "$bloodGroup").append("PID", "$PID").append("PNUM", "$PNUM")
							.append("gender", "$gender").append("countryCode", "$countryCode")
							.append("mobileNumber", "$mobileNumber").append("secPhoneNumber", "$secPhoneNumber")
							.append("dob", "$dob").append("dateOfVisit", "$dateOfVisit").append("doctorId", "$doctorId")
							.append("locationId", "$locationId").append("hospitalId", "$hospitalId")
							.append("colorCode", "$user.colorCode").append("user", "$user")
							.append("address", "$address").append("patientId", "$userId")
							.append("profession", "$profession").append("relations", "$relations")
							.append("consultantDoctorIds", "$consultantDoctorIds").append("doctorId", "$doctorId")
							.append("registrationDate", "$registrationDate").append("createdTime", "$createdTime")
							.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")));

			CustomAggregationOperation groupOperations = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$_id").append("userId", new BasicDBObject("$first", "$userId"))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("localPatientName", new BasicDBObject("$first", "$localPatientName"))
							.append("userName", new BasicDBObject("$first", "$userName"))
							.append("emailAddress", new BasicDBObject("$first", "$emailAddress"))
							.append("imageUrl", new BasicDBObject("$first", "$imageUrl"))
							.append("thumbnailUrl", new BasicDBObject("$first", "$thumbnailUrl"))
							.append("bloodGroup", new BasicDBObject("$first", "$bloodGroup"))
							.append("PID", new BasicDBObject("$first", "$PID"))
							.append("PNUM", new BasicDBObject("$first", "$PNUM"))
							.append("gender", new BasicDBObject("$first", "$gender"))
							.append("countryCode", new BasicDBObject("$first", "$countryCode"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("secPhoneNumber", new BasicDBObject("$first", "$secPhoneNumber"))
							.append("dob", new BasicDBObject("$first", "$dob"))
							.append("dateOfVisit", new BasicDBObject("$first", "$dateOfVisit"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("colorCode", new BasicDBObject("$first", "$user.colorCode"))
							.append("user", new BasicDBObject("$first", "$user"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("patientId", new BasicDBObject("$first", "$userId"))
							.append("profession", new BasicDBObject("$first", "$profession"))
							.append("relations", new BasicDBObject("$first", "$relations"))
							.append("consultantDoctorIds", new BasicDBObject("$first", "$consultantDoctorIds"))
							.append("registrationDate", new BasicDBObject("$first", "$registrationDate"))
							.append("insensitiveLocalPatientName",
									new BasicDBObject("$first", "$insensitiveLocalPatientName"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						projectOperations, groupOperations,
						new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("insensitiveLocalPatientName", 1))),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						projectOperations, groupOperations, new CustomAggregationOperation(
								new Document("$sort", new BasicDBObject("insensitiveLocalPatientName", 1))));
		} else {
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		}

		AggregationResults<PatientCard> aggregationResults = mongoTemplate.aggregate(aggregation,
				PatientCollection.class, PatientCard.class);
		patientCards = aggregationResults.getMappedResults();
		if (patientCards != null) {
			for (PatientCard patientCard : patientCards) {
				patientCard.setColorCode(patientCard.getUser().getColorCode());
				patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
				patientCard.setDoctorSepecificPatientId(patientCard.getUserId().toString());
				patientCard.setId(patientCard.getUserId());
				patientCard.setUser(null);
				//calculate age of patient upto today
				if (patientCard.getDob() != null) {
					if(patientCard.getDob().getDays() > 0 && patientCard.getDob().getMonths() > 0 && patientCard.getDob().getYears() > 0) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
					LocalDate today = LocalDate.now();
					LocalDate birthday = LocalDate.parse(patientCard.getDob().getDays()+"/"+patientCard.getDob().getMonths()
							+"/"+patientCard.getDob().getYears(), formatter);

					Period p = Period.between(birthday, today);
									
					patientCard.getDob().getAge().setDays(p.getDays());
					patientCard.getDob().getAge().setMonths(p.getMonths());
					patientCard.getDob().getAge().setYears(p.getYears());
					}
				}
			}
			response = new DoctorContactsResponse();
			response.setPatientCards(patientCards);
			response.setTotalSize((int) mongoTemplate.count(new Query(criteria), PatientCollection.class));
		}
		return response;
	}

	private Collection<ObjectId> getPatientIdsForGroups(List<String> groups) throws Exception {
		Query query = new Query();
		List<ObjectId> groupObjectIds = new ArrayList<ObjectId>();
		for (String groupId : groups)
			groupObjectIds.add(new ObjectId(groupId));
		query.addCriteria(Criteria.where("groupId").in(groupObjectIds));
		List<PatientGroupCollection> patientGroupCollections = mongoTemplate.find(query, PatientGroupCollection.class);
		if (patientGroupCollections != null) {
			@SuppressWarnings("unchecked")
			Collection<ObjectId> patientIds = CollectionUtils.collect(patientGroupCollections,
					new BeanToPropertyValueTransformer("patientId"));
			return patientIds;
		}
		return null;
	}

	@Override
	@Transactional
	public void blockPatient(String patientId, String docterId) {
		try {
			PatientCollection patientCollection = patientRepository.findByUserIdAndDoctorId(new ObjectId(patientId),
					new ObjectId(docterId));
			if (patientCollection != null) {
				patientCollection.setDiscarded(true);
				patientRepository.save(patientCollection);
			} else {
				logger.warn("PatientId and DoctorId send is not proper.");
				throw new BusinessException(ServiceError.InvalidInput, "PatientId and DoctorId send is not proper.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Group addEditGroup(Group group) {
		try {
			checkIfGroupIsExistWithSameName(group);
			GroupCollection groupCollection = new GroupCollection();
			BeanUtil.map(group, groupCollection);
			if (DPDoctorUtils.allStringsEmpty(groupCollection.getId())) {
				groupCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findById(groupCollection.getDoctorId()).orElse(null);
				if (userCollection != null) {
					groupCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
			} else {
				GroupCollection oldGroupCollection = groupRepository.findById(groupCollection.getId()).orElse(null);
				groupCollection.setCreatedTime(oldGroupCollection.getCreatedTime());
				groupCollection.setCreatedBy(groupCollection.getCreatedBy());
				groupCollection.setDiscarded(oldGroupCollection.getDiscarded());
			}
			groupCollection = groupRepository.save(groupCollection);
			BeanUtil.map(groupCollection, group);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return group;
	}

	private void checkIfGroupIsExistWithSameName(Group group) {
		int size = 0;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(group.getDoctorId()))
				doctorObjectId = new ObjectId(group.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(group.getLocationId()))
				locationObjectId = new ObjectId(group.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(group.getHospitalId()))
				hospitalObjectId = new ObjectId(group.getHospitalId());

			Criteria criteria = new Criteria("name").is(group.getName()).and("doctorId").is(doctorObjectId)
					.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("discarded")
					.is(false);

			if (!DPDoctorUtils.anyStringEmpty(group.getId())) {
				criteria.and("id").ne(new ObjectId(group.getId()));
			}
			size = (int) mongoTemplate.count(new Query(criteria), GroupCollection.class);
			if (size > 0) {
				logger.error(checkIfGroupIsExistWithSameName);
				throw new BusinessException(ServiceError.NotAcceptable, checkIfGroupIsExistWithSameName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Group deleteGroup(String groupId, Boolean discarded) {
		Group response = null;
		GroupCollection groupCollection = null;
		List<PatientGroupCollection> patientGroupCollection = null;
		try {
			groupCollection = groupRepository.findById(new ObjectId(groupId)).orElse(null);
			if (groupCollection != null) {
				groupCollection.setDiscarded(discarded);
				groupCollection.setUpdatedTime(new Date());
				groupCollection = groupRepository.save(groupCollection);
				patientGroupCollection = patientGroupRepository.findByGroupId(groupCollection.getId());
				if (patientGroupCollection != null) {
					for (PatientGroupCollection patientGroup : patientGroupCollection) {
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(patientGroup.getPatientId(),
										groupCollection.getDoctorId(), groupCollection.getLocationId(),
										groupCollection.getHospitalId());
						if (patientCollection != null) {
							patientCollection.setUpdatedTime(new Date());
							patientCollection = patientRepository.save(patientCollection);
						}
						patientGroupRepository.delete(patientGroup);
					}
				}
				response = new Group();
				BeanUtil.map(groupCollection, response);
			} else {
				logger.error(groupNotFound);
				throw new BusinessException(ServiceError.NotFound, groupNotFound);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Group");
		}
		return response;
	}

	@Override
	@Transactional
	public int getContactsTotalSize(GetDoctorContactsRequest request) {
		int response = 0;
		Collection<ObjectId> patientIds = null;
		try {
			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(request.getUpdatedTime())));
			if (!request.getDiscarded())
				criteria.and("discarded").is(request.getDiscarded());
			if (request.getGroups() != null && !request.getGroups().isEmpty()) {
				patientIds = getPatientIdsForGroups(request.getGroups());
				if (patientIds != null && !patientIds.isEmpty())
					criteria.and("userId").in(patientIds);
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId(), request.getHospitalId()))
				criteria.and("locationId").is(new ObjectId(request.getLocationId())).and("hospitalId")
						.is(new ObjectId(request.getHospitalId()));
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));
			response = (int) mongoTemplate.count(new Query(criteria), PatientCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	/**
	 * This service gives lists of all groups for doctor.
	 */
	@Override
	@Transactional
	public List<Group> getAllGroups(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Group> groups = null;

		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteriafirst = new Criteria();
			Criteria criteriasecond = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteriafirst.and("doctorId").is(new ObjectId(doctorId));
				criteriasecond = criteriasecond.and("doctorClinic.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteriafirst.and("locationId").is(new ObjectId(locationId));
				criteriasecond = criteriasecond.and("doctorClinic.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteriafirst.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (createdTimeStamp > 0) {
				criteriafirst.and("updatedTime").gte(new Date(createdTimeStamp));
			}
			if (!discarded) {
				criteriafirst.and("discarded").is(discarded);
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("name", "$name"), Fields.field("explanation", "$explanation"),
					Fields.field("doctorId", "$doctorId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("discarded", "$discarded"),
					Fields.field("packageType", "$doctorClinic.packageType"),
					Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("createdBy", "$createdBy")));
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteriafirst),
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"), Aggregation.match(criteriasecond), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteriafirst),
						Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorClinic"),
						Aggregation.unwind("doctorClinic"), Aggregation.match(criteriasecond), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<Group> aggregationResults = mongoTemplate.aggregate(aggregation, GroupCollection.class,
					Group.class);
			groups = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return groups;
	}

	@Override
	@Transactional
	public Boolean importContacts(ImportContactsRequest request) {
		Boolean response = false;
		ImportContactsRequestCollection importContactsRequestCollection = null;
		try {
			if (request.getContactsFile() != null) {
				request.getContactsFile().setFileName(request.getContactsFile().getFileName() + new Date().getTime());
				String path = "contacts" + File.separator + request.getDoctorId();
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getContactsFile(),
						path, false);
				request.setContactsFileUrl(imageURLResponse.getImageUrl());

			}
			importContactsRequestCollection = new ImportContactsRequestCollection();
			BeanUtil.map(request, importContactsRequestCollection);
			importContactsRequestCollection = importContactsRequestRepository.save(importContactsRequestCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Importing Contact");
			throw new BusinessException(ServiceError.Unknown, "Error Importing Contact");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean exportContacts(ExportRequest request) {
		Boolean response = false;
		ExportContactsRequestCollection exportContactsRequestCollection = null;
		try {
			exportContactsRequestCollection = new ExportContactsRequestCollection();
			BeanUtil.map(request, exportContactsRequestCollection);
			exportContactsRequestCollection = exportContactsRequestRepository.save(exportContactsRequestCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Exporting Contact");
			throw new BusinessException(ServiceError.Unknown, "Error Exporting Contact");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getDoctorContactsHandheldCount(String doctorId, String locationId, String hospitalId,
			boolean discarded, String role, String searchTerm) {

		Integer count = 0;

		List<PatientCard> patientCards = null;
		Aggregation aggregation = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);

		try {
			if (discarded)
				discards.add(true);
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("discarded").in(discards);
			Criteria criteriaSecond = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
					criteria.and("consultantDoctorIds").is(doctorObjectId);
				} else
					criteria.and("doctorId").is(doctorObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("user.mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm, "i"));
			}
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
					Aggregation.match(criteriaSecond), Aggregation.project("id").and("_id"));

			AggregationResults<PatientCard> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientCollection.class, PatientCard.class);
			patientCards = aggregationResults.getMappedResults();
			count = patientCards.size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return count;
	}

	@Override
	@Transactional
	public PatientGroupAddEditRequest addGroupToPatient(PatientGroupAddEditRequest request) {
		PatientGroupAddEditRequest response = new PatientGroupAddEditRequest();

		try {
			ObjectId patientObjecId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				patientObjecId = new ObjectId(request.getPatientId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientObjecId,
					locationObjectId, hospitalObjectId);
			;
			List<String> groupIds = new ArrayList<String>();
			List<PatientGroupCollection> patientGroupCollections = patientGroupRepository
					.findByPatientIdAndDiscardedIsFalse(patientObjecId);
			if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
				for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
					if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
						groupIds.add(patientGroupCollection.getGroupId().toString());
						if (!request.getGroupIds().contains(patientGroupCollection.getGroupId().toString())) {
							patientGroupRepository.delete(patientGroupCollection);
						}
					} else {
						patientGroupRepository.delete(patientGroupCollection);
					}
				}
			}

			if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
				for (String group : request.getGroupIds()) {
					if (!groupIds.contains(group)) {
						PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
						patientGroupCollection.setGroupId(new ObjectId(group));
						patientGroupCollection.setPatientId(new ObjectId(request.getPatientId()));
						patientGroupRepository.save(patientGroupCollection);
					}
				}
			}
			BeanUtil.map(request, response);
			patientCollection.setUpdatedTime(new Date());
			patientRepository.save(patientCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean sendSMSToGroup(BulkSMSRequest request) {
		List<PatientGroupLookupResponse> patientGroupLookupResponses = null;
		List<PatientCard> patientCards = null;
		User user = null;
		Boolean status = false;
		Aggregation aggregation = null;
		List<String> mobileNumbers = null;
		try {
			String message = request.getMessage() + "-Powered%20by%20Healthcoco";

			if (request.getGroupId() != null) {
				Criteria criteria = new Criteria().and("groupId").is(new ObjectId(request.getGroupId()));
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<PatientGroupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientGroupCollection.class, PatientGroupLookupResponse.class);
				patientGroupLookupResponses = aggregationResults.getMappedResults();
				if (patientGroupLookupResponses != null) {
					mobileNumbers = new ArrayList<>();
					for (PatientGroupLookupResponse patientGroupLookupResponse : patientGroupLookupResponses) {

						mobileNumbers.add(patientGroupLookupResponse.getUser().getMobileNumber());

					}

				}

			} else if (request.getPatientId() != null) {
				Criteria criteria = new Criteria().and("id").is(new ObjectId(request.getPatientId()));
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<User> aggregationResults = mongoTemplate.aggregate(aggregation, UserCollection.class,
						User.class);
				user = aggregationResults.getUniqueMappedResult();
				if (user != null) {
					mobileNumbers = new ArrayList<>();

					mobileNumbers.add(user.getMobileNumber());

				}
			} else {
				Criteria criteria = new Criteria().and("doctorId").is(new ObjectId(request.getDoctorId()));
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));
				criteria.and("hospitalId").is(new ObjectId(request.getHospitalId()));
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "userId", "_id", "user"),
						Aggregation.unwind("user"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				AggregationResults<PatientCard> aggregationResults = mongoTemplate.aggregate(aggregation,
						PatientCollection.class, PatientCard.class);
				patientCards = aggregationResults.getMappedResults();
				if (patientCards != null) {
					mobileNumbers = new ArrayList<>();
					for (PatientCard patientCard : patientCards) {
						mobileNumbers.add(patientCard.getUser().getMobileNumber());
					}

				}
			}
			if (!smsServices.getBulkSMSResponse(mobileNumbers, message,request.getDoctorId(), request.getLocationId(),0L).equalsIgnoreCase("FAILED")) {
				status = true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return status;
	}

	@Override
	@Transactional
	public List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String role, int page, int size,
			String searchTerm) {
		List<RegisteredPatientDetails> registeredPatientDetails = null;
		List<PatientCard> patientCards = null;
		List<Group> groups = null;
		Aggregation aggregation = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		List<String> groupIdList  = null;

		try {
			if (discarded)
				discards.add(true);
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			long createdTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimeStamp)).and("discarded")
					.in(discards);
			Criteria criteriaSecond = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (RoleEnum.CONSULTANT_DOCTOR.getRole().equalsIgnoreCase(role)) {
					criteria.and("consultantDoctorIds").is(doctorObjectId);
				} else
					criteria.and("doctorId").is(doctorObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("user.mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm, "i"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroupCollections"),
						Aggregation.match(criteriaSecond), Aggregation.sort(Direction.DESC, "updatedTime"),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroupCollections"),
						Aggregation.match(criteriaSecond), Aggregation.sort(Direction.DESC, "updatedTime"));

			AggregationResults<PatientCard> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientCollection.class, PatientCard.class);
			patientCards = aggregationResults.getMappedResults();

			if (!patientCards.isEmpty()) {
				registeredPatientDetails = new ArrayList<RegisteredPatientDetails>();
				for (PatientCard patientCard : patientCards) {

					Collection<ObjectId> groupIds = CollectionUtils.collect(patientCard.getPatientGroupCollections(),
							new BeanToPropertyValueTransformer("groupId"));
					patientCard.setPatientGroupCollections(null);
					RegisteredPatientDetails registeredPatientDetail = new RegisteredPatientDetails();
					if (patientCard.getUser() != null) {
						BeanUtil.map(patientCard.getUser(), registeredPatientDetail);
						if (patientCard.getUser().getId() != null) {
							registeredPatientDetail.setUserId(patientCard.getUser().getId().toString());
						}
					}

					BeanUtil.map(patientCard, registeredPatientDetail);
					registeredPatientDetail.setBackendPatientId(patientCard.getId());
					registeredPatientDetail.setDoctorId(patientCard.getDoctorId().toString());
					registeredPatientDetail.setLocationId(patientCard.getLocationId().toString());
					registeredPatientDetail.setHospitalId(patientCard.getHospitalId().toString());
					registeredPatientDetail.setCreatedTime(patientCard.getCreatedTime());
					registeredPatientDetail.setPID(patientCard.getPID());
					registeredPatientDetail.setMobileNumber(patientCard.getUser().getMobileNumber());
					registeredPatientDetail.setBackendPatientId(patientCard.getId());
				//	registeredPatientDetail.setImageUrl(patientCard.getImageUrl());
				//	registeredPatientDetail.setThumbnailUrl(patientCard.getThumbnailUrl());
					registeredPatientDetail.setColorCode(patientCard.getUser().getColorCode());
					if (groupIds != null) {
						groupIdList = new ArrayList<>();
						for (ObjectId groupId : groupIds) {
							groupIdList.add(String.valueOf(groupId));
						}
					}
					registeredPatientDetail.setGroupIds(groupIdList);
					registeredPatientDetails.add(registeredPatientDetail);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return registeredPatientDetails;
	}

}
