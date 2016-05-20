package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.collections.ExportContactsRequestCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.ImportContactsRequestCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.reflections.ReflectionUtil;
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
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.OTPService;

import common.util.web.DPDoctorUtils;

@Service
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
    public List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request) {
	try {
		Collection<String> patientIds = null;
	    if (request.getGroups() != null  && !request.getGroups().isEmpty())patientIds = getPatientIdsForGroups(request.getGroups());

	    if(patientIds == null || patientIds.isEmpty())return null;
	    List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPage(), request.getSize(), request.getUpdatedTime(), request.getDiscarded());
	    
	    return patientCards;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public DoctorContactsResponse getDoctorContacts(String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded, int page,
	    int size) {
	DoctorContactsResponse response = null;
	try {
		List<PatientCard> patientCards = getSpecifiedPatientCards(null, doctorId, locationId, hospitalId, page, size, updatedTime, discarded);
	    if (patientCards != null) {
		response = new DoctorContactsResponse();
		response.setPatientCards(patientCards);
		response.setTotalSize(getSpecifiedPatientCount(null, doctorId, locationId, hospitalId, updatedTime, discarded));
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
    public DoctorContactsResponse getDoctorContactsSortedByName(String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
	    int page, int size) {
	DoctorContactsResponse response = null;
	try {
	    List<PatientCard> patientCards = getSpecifiedPatientCardsSorted(null, doctorId, locationId, hospitalId, page, size, updatedTime, discarded);
	    if (patientCards != null) {
		response = new DoctorContactsResponse();
		response.setPatientCards(patientCards);
		response.setTotalSize(getSpecifiedPatientCount(null, doctorId, locationId, hospitalId, updatedTime, discarded));
		//doctorContactsRepository.findCountByDoctorIdAndIsBlocked(doctorId, false, discards, new Date(createdTimestamp))
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;

    }

    private int getSpecifiedPatientCount(Collection<String> patientIds, String doctorId, String locationId,String hospitalId, String updatedTime, Boolean discarded) {
    	Integer count = 0;
    	boolean[] discards = new boolean[2];
    	discards[0] = false;
    	if (discarded) discards[1] = true;

    	long createdTimestamp = Long.parseLong(updatedTime);

    	if(patientIds != null && !patientIds.isEmpty()){
    		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
       		 count = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(patientIds, doctorId, locationId, hospitalId,new Date(createdTimestamp), discards);

       	} else if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
       	    count = patientRepository.findByUserIdDoctorId(patientIds, doctorId, new Date(createdTimestamp), discards);
       	}
    	}else{
    		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
       		 count = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(doctorId, locationId, hospitalId,new Date(createdTimestamp), discards);

       	} else if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
       	    count = patientRepository.findByUserIdDoctorId(doctorId, new Date(createdTimestamp), discards);
       	}
    	}
    	if(count != null)return count;
    	else return 0;
	}

	private List<PatientCard> getSpecifiedPatientCardsSorted(Collection<String> patientIds, String doctorId, String locationId, String hospitalId, int page, int size, String updatedTime, Boolean discarded) {
	List<PatientCollection> patientCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	if (discarded) discards[1] = true;

	long createdTimestamp = Long.parseLong(updatedTime);

	if(patientIds != null && !patientIds.isEmpty()){
		if (!DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
		    if (size > 0)
			patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(patientIds, doctorId, locationId, hospitalId, new Date(createdTimestamp), discards,
				new PageRequest(page, size, Direction.ASC, "firstName"));
		    else
			patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(patientIds, doctorId, locationId, hospitalId,new Date(createdTimestamp), discards,
				new Sort(Sort.Direction.ASC, "firstName"));

		} else if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
		    if (size > 0)
			patientCollections = patientRepository.findByUserIdDoctorId(patientIds, doctorId,new Date(createdTimestamp), discards,
				new PageRequest(page, size, new Sort(Direction.ASC, "firstName")));
		    else
			patientCollections = patientRepository.findByUserIdDoctorId(patientIds, doctorId, new Date(createdTimestamp), discards, new Sort(Direction.ASC, "firstName"));
		}
	}else{
		if (!DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId)) {
		    if (size > 0)
			patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(doctorId, locationId, hospitalId, new Date(createdTimestamp), discards,
				new PageRequest(page, size, Direction.ASC, "firstName"));
		    else
			patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(doctorId, locationId, hospitalId,new Date(createdTimestamp), discards,
				new Sort(Sort.Direction.ASC, "firstName"));

		} else if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
		    if (size > 0)
			patientCollections = patientRepository.findByUserIdDoctorId(doctorId,new Date(createdTimestamp), discards,
				new PageRequest(page, size, new Sort(Direction.ASC, "firstName")));
		    else
			patientCollections = patientRepository.findByUserIdDoctorId(doctorId, new Date(createdTimestamp), discards, new Sort(Direction.ASC, "firstName"));
		}
	}

	List<PatientCard> patientCards = new ArrayList<PatientCard>();
	if (patientCollections != null)
	    for (PatientCollection patientCollection : patientCollections) {
		UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
		if (userCollection != null) {
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    @SuppressWarnings("unchecked")
		    Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		    List<Group> groups = new ArrayList<Group>();
		    List<GroupCollection> groupCollections = null;
		    
		    if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, locationId, hospitalId, false);
		    else groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, false);
		   
		    if(groupCollections != null)BeanUtil.map(groupCollections, groups);
		    PatientCard patientCard = new PatientCard();
		    BeanUtil.map(patientCollection, patientCard);
		    BeanUtil.map(userCollection, patientCard);
		    patientCard.setImageUrl(patientCollection.getImageUrl());
		    patientCard.setThumbnailUrl(patientCollection.getThumbnailUrl());
		    patientCard.setGroups(groups);
		    patientCard.setDoctorSepecificPatientId(patientCollection.getUserId());

		    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);

		    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			    || (recordsCount != null && recordsCount > 0))
			patientCard.setIsDataAvailableWithOtherDoctor(true);

		    patientCard.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId, userCollection.getId()));
		    patientCards.add(patientCard);
		}
	    }
	return patientCards;
    }

    public List<PatientCard> getSpecifiedPatientCards(Collection<String> patientIds, String doctorId, String locationId, String hospitalId, int page, int size, String updatedTime, Boolean discarded) throws Exception {
	List<PatientCard> patientCards = new ArrayList<PatientCard>();
	boolean[] discards = new boolean[2];
	discards[0] = false;
	if (discarded) discards[1] = true;

	long createdTimestamp = Long.parseLong(updatedTime);
	
	List<PatientCollection> patientCollections = null;
	if(patientIds != null && !patientIds.isEmpty()){
		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if(size > 0)patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(patientIds, doctorId, locationId, hospitalId, new Date(createdTimestamp), discards, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(patientIds, doctorId, locationId, hospitalId, new Date(createdTimestamp), discards, new Sort(Direction.DESC, "createdTime"));
		}else{
			if(size > 0)patientCollections = patientRepository.findByUserIdDoctorId(patientIds, doctorId, new Date(createdTimestamp), discards, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else patientCollections = patientRepository.findByUserIdDoctorId(patientIds, doctorId, new Date(createdTimestamp), discards, new Sort(Direction.DESC, "createdTime"));
		}
	}else{
		if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
			if(size > 0)patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(doctorId, locationId, hospitalId, new Date(createdTimestamp), discards, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else patientCollections = patientRepository.findByUserIdDoctorIdLocationIdHospitalId(doctorId, locationId, hospitalId, new Date(createdTimestamp), discards, new Sort(Direction.DESC, "createdTime"));
		}else{
			if(size > 0)patientCollections = patientRepository.findByUserIdDoctorId(doctorId, new Date(createdTimestamp), discards, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else patientCollections = patientRepository.findByUserIdDoctorId(doctorId, new Date(createdTimestamp), discards, new Sort(Direction.DESC, "createdTime"));
		}
	}
	if(patientCollections != null){
	for(PatientCollection patientCollection : patientCollections){
	
		    if (patientCollection != null) {
			UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
			if (userCollection != null) {
			    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
			    @SuppressWarnings("unchecked")
			    Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
			    List<Group> groups = new ArrayList<Group>();
			    List<GroupCollection> groupCollections = null;
			    
			    if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, locationId, hospitalId, false);
			    else groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, false);
			   
			    if(groupCollections != null)BeanUtil.map(groupCollections, groups);
			    PatientCard patientCard = new PatientCard();
			    BeanUtil.map(patientCollection, patientCard);
			    BeanUtil.map(userCollection, patientCard);
			    patientCard.setGroups(groups);
			    patientCard.setDoctorSepecificPatientId(patientCollection.getUserId());
			    patientCard.setImageUrl(patientCollection.getImageUrl());
			    patientCard.setThumbnailUrl(patientCollection.getThumbnailUrl());
			    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
				    locationId);
			    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, userCollection.getId(),
				    hospitalId, locationId);
			    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);

			    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
				    || (recordsCount != null && recordsCount > 0))
				patientCard.setIsDataAvailableWithOtherDoctor(true);

			    patientCard.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId, userCollection.getId()));

			    patientCards.add(patientCard);
			}
		    }
		}
	} else {
	    return new ArrayList<PatientCard>(0);
	}

	return patientCards;
    }

    private Collection<String> getPatientIdsForGroups(List<String> groups) throws Exception {
	Query query = new Query();
	query.addCriteria(Criteria.where("groupId").in(groups));
	List<PatientGroupCollection> patientGroupCollections = mongoTemplate.find(query, PatientGroupCollection.class);
	if (patientGroupCollections != null) {
	    @SuppressWarnings("unchecked")
	    Collection<String> patientIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("patientId"));
	    return patientIds;
	}
	return null;
    }

    @Override
    @Transactional
    public void blockPatient(String patientId, String docterId) {
	try {
	    PatientCollection patientCollection = patientRepository.findByUserIdDoctorId(patientId, docterId);
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
	    } else {
		GroupCollection oldGroupCollection = groupRepository.findOne(groupCollection.getId());
		groupCollection.setCreatedTime(oldGroupCollection.getCreatedTime());
		groupCollection.setCreatedBy(groupCollection.getCreatedBy());
		groupCollection.setDiscarded(oldGroupCollection.getDiscarded());
	    }
	    groupCollection = groupRepository.save(groupCollection);
	    BeanUtil.map(groupCollection, group);
	    return group;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    private void checkIfGroupIsExistWithSameName(Group group) {
	List<GroupCollection> groupCollection = null;
	try {
	    if (group.getId() == null) {
		groupCollection = groupRepository.findByName(group.getName(), group.getDoctorId(), group.getLocationId(), group.getHospitalId());
	    } else {
		Query query = new Query();

		Criteria criteria = Criteria.where("id").ne(group.getId()).and("name").is(group.getName()).and("doctorId").is(group.getDoctorId())
			.and("locationId").is(group.getLocationId()).and("hospitalId").is(group.getHospitalId());

		query.addCriteria(criteria);
		groupCollection = mongoTemplate.find(query, GroupCollection.class);
	    }

	    if (groupCollection != null && !groupCollection.isEmpty() && groupCollection.size() > 0) {
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
	    groupCollection = groupRepository.findOne(groupId);
	    if (groupCollection != null) {
		groupCollection.setDiscarded(discarded);
		groupCollection.setUpdatedTime(new Date());
		groupCollection = groupRepository.save(groupCollection);
		patientGroupCollection = patientGroupRepository.findByGroupId(groupCollection.getId());
		if (patientGroupCollection != null) {
		    for (PatientGroupCollection patientGroup : patientGroupCollection) {
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(patientGroup.getPatientId(),
				groupCollection.getDoctorId(), groupCollection.getLocationId(), groupCollection.getHospitalId());
			if (patientCollection != null) {
			    patientCollection.setUpdatedTime(new Date());
			    patientCollection = patientRepository.save(patientCollection);
			}
			patientGroup.setDiscarded(discarded);
			patientGroupRepository.save(patientGroup);
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
	    Collection<String> patientIds = null;
     try {
		if (request.getGroups() != null  && !request.getGroups().isEmpty()){
			patientIds = getPatientIdsForGroups(request.getGroups());
			if(patientIds != null && !patientIds.isEmpty())
				response = getSpecifiedPatientCount(patientIds, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getUpdatedTime(), request.getDiscarded());
		}else{
			response =  getSpecifiedPatientCount(null, request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getUpdatedTime(), request.getDiscarded());
		}
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
    public List<Group> getAllGroups(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
	List<Group> groups = null;
	List<GroupCollection> groupCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded) {
		discards[1] = true;
	    }
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (size > 0) {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    groupCollections = groupRepository.findAll(doctorId, discards, new Date(createdTimeStamp),
			    new PageRequest(page, size, Direction.DESC, "createdTime"));
		} else {
		    groupCollections = groupRepository.findAll(doctorId, locationId, hospitalId, discards, new Date(createdTimeStamp),
			    new PageRequest(page, size, Direction.DESC, "createdTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    groupCollections = groupRepository.findAll(doctorId, discards, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    groupCollections = groupRepository.findAll(doctorId, locationId, hospitalId, discards, new Date(createdTimeStamp),
			    new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    }

	    if (groupCollections != null && !groupCollections.isEmpty()) {
		groups = new ArrayList<Group>();
		for (GroupCollection groupCollection : groupCollections) {
		    Group group = new Group();
		    ReflectionUtil.copy(group, groupCollection);
		    groups.add(group);
		}
	    }
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
		String contactsFileUrl = fileManager.saveImageAndReturnImageUrl(request.getContactsFile(), path);
		request.setContactsFileUrl(contactsFileUrl);

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
    public Boolean exportContacts(ExportContactsRequest request) {
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
    public List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId, String hospitalId, String updatedTime,
	    boolean discarded) {
	List<RegisteredPatientDetails> registeredPatientDetails = null;
	List<PatientCollection> patientCollections = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded)
		discards[1] = true;

	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		patientCollections = patientRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			new Sort(Sort.Direction.DESC, "createdTime"));
	    }

	    if (!patientCollections.isEmpty()) {
		registeredPatientDetails = new ArrayList<RegisteredPatientDetails>();
		for (PatientCollection patientCollection : patientCollections) {
		    UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		    @SuppressWarnings("unchecked")
		    Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		    RegisteredPatientDetails registeredPatientDetail = new RegisteredPatientDetails();
		    if (userCollection != null) {
			BeanUtil.map(userCollection, registeredPatientDetail);
			if (userCollection.getId() != null) {
			    registeredPatientDetail.setUserId(userCollection.getId());
			}
		    }
		    Patient patient = new Patient();
		    BeanUtil.map(patientCollection, patient);
		    patient.setPatientId(patientCollection.getId());
		    String referredBy = patientCollection.getReferredBy();
		    patientCollection.setReferredBy(null);
		    BeanUtil.map(patientCollection, registeredPatientDetail);

		    Integer prescriptionCount = prescriptionRepository.getPrescriptionCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer clinicalNotesCount = clinicalNotesRepository.getClinicalNotesCountForOtherDoctors(doctorId, userCollection.getId(), hospitalId,
			    locationId);
		    Integer recordsCount = recordsRepository.getRecordsForOtherDoctors(doctorId, userCollection.getId(), hospitalId, locationId);

		    if ((prescriptionCount != null && prescriptionCount > 0) || (clinicalNotesCount != null && clinicalNotesCount > 0)
			    || (recordsCount != null && recordsCount > 0))
			patient.setIsDataAvailableWithOtherDoctor(true);
		    patient.setIsPatientOTPVerified(otpService.checkOTPVerified(doctorId, locationId, hospitalId, userCollection.getId()));
		    registeredPatientDetail.setPatient(patient);
		    registeredPatientDetail.setAddress(patientCollection.getAddress());
		    
		    if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, locationId, hospitalId, false);
		    else groupCollections = (List<GroupCollection>) groupRepository.find(groupIds, doctorId, false);
		   
		    groups = new ArrayList<Group>();
		    BeanUtil.map(groupCollections, groups);
		    registeredPatientDetail.setGroups(groups);

		    registeredPatientDetail.setDoctorId(patientCollection.getDoctorId());
		    registeredPatientDetail.setLocationId(patientCollection.getLocationId());
		    registeredPatientDetail.setHospitalId(patientCollection.getHospitalId());
		    registeredPatientDetail.setCreatedTime(patientCollection.getCreatedTime());
		    registeredPatientDetail.setPID(patientCollection.getPID());

		    if (patientCollection.getDob() != null) {
			registeredPatientDetail.setDob(patientCollection.getDob());
		    }
		    
			Reference reference = new Reference();
			if (referredBy != null) {
			    ReferencesCollection referencesCollection = referenceRepository.findOne(referredBy);
			    if (referencesCollection != null)
				BeanUtil.map(referencesCollection, reference);
			}
			registeredPatientDetail.setReferredBy(reference);
		    
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

    @Override
    @Transactional
    public PatientGroupAddEditRequest addGroupToPatient(PatientGroupAddEditRequest request) {
	PatientGroupAddEditRequest response = new PatientGroupAddEditRequest();
	PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(request.getPatientId(), request.getDoctorId(),
		request.getLocationId(), request.getHospitalId());
	try {
	    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(request.getPatientId());
	    if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
		for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
		    patientGroupRepository.delete(patientGroupCollection);
		}
	    }

	    if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
		for (String group : request.getGroupIds()) {
		    PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
		    patientGroupCollection.setGroupId(group);
		    patientGroupCollection.setPatientId(request.getPatientId());
		    patientGroupRepository.save(patientGroupCollection);
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
}
