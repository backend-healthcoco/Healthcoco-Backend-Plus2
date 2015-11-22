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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.collections.AddressCollection;
import com.dpdocter.collections.DoctorContactCollection;
import com.dpdocter.collections.ExportContactsRequestCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.ImportContactsRequestCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.reflections.ReflectionUtil;
import com.dpdocter.repository.AddressRepository;
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.ExportContactsRequestRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.ImportContactsRequestRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.FileManager;
import common.util.web.DPDoctorUtils;

@Service
public class ContactsServiceImpl implements ContactsService {

    private static Logger logger = Logger.getLogger(ContactsServiceImpl.class.getName());

    @Autowired
    private DoctorContactsRepository doctorContactsRepository;

    @Autowired
    private PatientAdmissionRepository patientAdmissionRepository;

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
    private AddressRepository addressRepository;

    @Autowired
    private FileManager fileManager;

    /**
     * This method returns all unblocked or blocked patients (based on param
     * blocked) of specified doctor.
     * 
     * @param doctorId
     * @param blocked
     * @return List of Patient cards
     */
    @Override
    public List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request) {
	List<DoctorContactCollection> doctorContactCollections = null;
	try {
	    if (request.getSize() > 0)
		doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false, new PageRequest(request.getPage(),
			request.getSize(), Direction.DESC, "updatedTime"));
	    else
		doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false, new Sort(Sort.Direction.DESC,
			"updatedTime"));

	    if (doctorContactCollections.isEmpty()) {
		return null;
	    }
	    if (request.getGroups() != null && !request.getGroups().isEmpty()) {
		doctorContactCollections = filterContactsByGroup(request, doctorContactCollections);
	    }
	    @SuppressWarnings("unchecked")
	    Collection<String> patientIds = CollectionUtils.collect(doctorContactCollections, new BeanToPropertyValueTransformer("contactId"));
	    List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds, request.getDoctorId(), request.getLocationId(), request.getHospitalId());
	    return patientCards;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public List<PatientCard> getDoctorContacts(String doctorId, String updatedTime, boolean discarded, int page, int size) {

	List<DoctorContactCollection> doctorContactCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;

	try {
	    if (discarded) {
		discards[1] = true;
	    }

	    long createdTimestamp = Long.parseLong(updatedTime);
	    if (size > 0)
		doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, discards, new Date(createdTimestamp),
			new PageRequest(page, size, Direction.DESC, "updatedTime"));
	    else
		doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, discards, new Date(createdTimestamp), new Sort(
			Sort.Direction.DESC, "updatedTime"));

	    if (doctorContactCollections.isEmpty()) {
		return null;
	    }
	    @SuppressWarnings("unchecked")
	    Collection<String> patientIds = CollectionUtils.collect(doctorContactCollections, new BeanToPropertyValueTransformer("contactId"));
	    List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds, doctorId, null, null);
	    return patientCards;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    public List<PatientCard> getSpecifiedPatientCards(Collection<String> patientIds, String doctorId, String locationId, String hospitalId) throws Exception {
	// getting patients from patient ids
	Query queryForGettingPatientsFromPatientIds = new Query();
	if (!DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId) && patientIds != null && !patientIds.isEmpty()) {
	    queryForGettingPatientsFromPatientIds.addCriteria(Criteria
		    .where("userId")
		    .in(patientIds)
		    .andOperator(Criteria.where("doctorId").is(doctorId), Criteria.where("locationId").is(locationId),
			    Criteria.where("hospitalId").is(hospitalId)));
	} else if (patientIds != null && !patientIds.isEmpty() && !DPDoctorUtils.anyStringEmpty(doctorId)) {
	    queryForGettingPatientsFromPatientIds.addCriteria(Criteria.where("userId").in(patientIds).andOperator(Criteria.where("doctorId").is(doctorId)));
	} else {
	    return new ArrayList<PatientCard>(0);
	}
	List<PatientCollection> patientCollections = mongoTemplate.find(queryForGettingPatientsFromPatientIds, PatientCollection.class);
	List<PatientCard> patientCards = new ArrayList<PatientCard>();
	for (PatientCollection patientCollection : patientCollections) {
	    UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
	    if (userCollection != null) {
		List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getUserId());
		@SuppressWarnings("unchecked")
		Collection<String> groupIds = CollectionUtils.collect(patientGroupCollections, new BeanToPropertyValueTransformer("groupId"));
		List<Group> groups = new ArrayList<Group>();
		List<GroupCollection> groupCollections = (List<GroupCollection>) groupRepository.findAll(groupIds);
		BeanUtil.map(groupCollections, groups);
		PatientCard patientCard = new PatientCard();
		BeanUtil.map(patientCollection, patientCard);
		BeanUtil.map(userCollection, patientCard);
		patientCard.setGroups(groups);
		patientCard.setDob(userCollection.getDob());
		patientCard.setDoctorSepecificPatientId(patientCollection.getUserId());
		patientCards.add(patientCard);
	    }
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

    private List<DoctorContactCollection> filterContactsByGroup(GetDoctorContactsRequest request, List<DoctorContactCollection> doctorContactCollections)
	    throws Exception {
	Collection<String> patientIds = getPatientIdsForGroups(request.getGroups());
	List<DoctorContactCollection> filteredDoctorContactCollection = new ArrayList<DoctorContactCollection>();
	if (patientIds != null) {
	    for (DoctorContactCollection doctorContactCollection : doctorContactCollections) {
		if (patientIds.contains(doctorContactCollection.getContactId().trim())) {
		    filteredDoctorContactCollection.add(doctorContactCollection);
		}
	    }
	}
	return filteredDoctorContactCollection;
    }

    @SuppressWarnings("unused")
    private List<DoctorContactCollection> filterContactsByGroup(List<String> groupIds, List<DoctorContactCollection> doctorContactCollections) throws Exception {
	Collection<String> patientIds = getPatientIdsForGroups(groupIds);
	List<DoctorContactCollection> filteredDoctorContactCollection = new ArrayList<DoctorContactCollection>();
	if (patientIds != null) {
	    for (DoctorContactCollection doctorContactCollection : doctorContactCollections) {
		if (patientIds.contains(doctorContactCollection.getContactId().trim())) {
		    filteredDoctorContactCollection.add(doctorContactCollection);
		}
	    }
	}
	return filteredDoctorContactCollection;
    }

    @Override
    public void blockPatient(String patientId, String docterId) {
	try {
	    DoctorContactCollection doctorContactCollection = doctorContactsRepository.findByDoctorIdAndContactId(docterId, patientId);
	    if (doctorContactCollection != null) {
		doctorContactCollection.setIsBlocked(true);
		doctorContactsRepository.save(doctorContactCollection);
	    } else {
		logger.warn("PatientId and DoctorId send is not proper.");
		throw new BusinessException(ServiceError.Unknown, "PatientId and DoctorId send is not proper.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
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
		groupCollection = groupRepository.findByName(group.getName());
	    } else {
		Query query = new Query().addCriteria(Criteria.where("id").ne(group.getId()).andOperator(Criteria.where("name").is(group.getName())));
		groupCollection = mongoTemplate.find(query, GroupCollection.class);
	    }

	    if (groupCollection != null && !groupCollection.isEmpty() && groupCollection.size() > 0) {
		logger.error("Group Name already exist.Please try with some other name");
		throw new BusinessException(ServiceError.NotAcceptable, "Group Name already exist.Please try with some other name");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public Boolean deleteGroup(String groupId, Boolean discarded) {
	Boolean response = false;
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
			PatientCollection patientCollection = patientRepository.findByUserId(patientGroup.getPatientId());
			if(patientCollection != null){
				patientCollection.setUpdatedTime(new Date());
				patientCollection = patientRepository.save(patientCollection);
			}
			patientGroup.setDiscarded(discarded);
			patientGroupRepository.save(patientGroup);
		    }
		}
		response = true;
	    } else {
		logger.error("Group Not Found");
		throw new BusinessException(ServiceError.NotFound, "Group Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Group");
	}
	return response;
    }

    @Override
    public List<PatientCard> getDoctorsRecentlyVisitedContacts(String doctorId, int size, int page) {
	try {
	    List<PatientAdmissionCollection> patientAdmissionCollections = patientAdmissionRepository.findDistinctPatientByDoctorId(doctorId, new PageRequest(
		    page, size));
	    if (patientAdmissionCollections != null) {
		@SuppressWarnings({ "unchecked", "unused" })
		Collection<String> patientIds = CollectionUtils.collect(patientAdmissionCollections, new BeanToPropertyValueTransformer("patientId"));
		List<PatientCard> patientCards = null;// getSpecifiedPatientCards(patientIds,doctorId);
		return patientCards;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return null;
    }

    @Override
    public int getContactsTotalSize(GetDoctorContactsRequest request) {
	List<DoctorContactCollection> doctorContactCollections = null;
	try {
	    doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false, new Sort(Sort.Direction.DESC,
		    "updatedTime"));

	    if (doctorContactCollections.isEmpty()) {
		return 0;
	    }
	    if (request.getGroups() != null && !request.getGroups().isEmpty()) {
		doctorContactCollections = filterContactsByGroup(request, doctorContactCollections);
	    }
	    @SuppressWarnings("unchecked")
	    Collection<String> patientIds = CollectionUtils.collect(doctorContactCollections, new BeanToPropertyValueTransformer("contactId"));
	    List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds, request.getDoctorId(), request.getLocationId(), request.getHospitalId());
	    return patientCards.size();
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    /**
     * This service gives lists of all groups for doctor.
     */
    @Override
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
		    groupCollections = groupRepository.findAll(doctorId, discards, new Date(createdTimeStamp), new PageRequest(page, size, Direction.DESC,
			    "updatedTime"));
		} else {
		    groupCollections = groupRepository.findAll(doctorId, locationId, hospitalId, discards, new Date(createdTimeStamp), new PageRequest(page,
			    size, Direction.DESC, "updatedTime"));
		}
	    } else {
		if (DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
		    groupCollections = groupRepository.findAll(doctorId, discards, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    groupCollections = groupRepository.findAll(doctorId, locationId, hospitalId, discards, new Date(createdTimeStamp), new Sort(
			    Sort.Direction.DESC, "updatedTime"));
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
    public List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded) {
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
		patientCollections = patientRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "updatedTime"));
	    } else {
		patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
			new Sort(Sort.Direction.DESC, "updatedTime"));
	    }

	    if (!patientCollections.isEmpty()) {
		registeredPatientDetails = new ArrayList<RegisteredPatientDetails>();
		for (PatientCollection patientCollection : patientCollections) {
		    UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
		    AddressCollection addressCollection = new AddressCollection();
		    if (patientCollection.getAddressId() != null) {
			addressCollection = addressRepository.findOne(patientCollection.getAddressId());
		    }
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
		    registeredPatientDetail.setPatient(patient);
		    Address address = new Address();
		    BeanUtil.map(addressCollection, address);
		    registeredPatientDetail.setAddress(address);
		    groupCollections = (List<GroupCollection>) groupRepository.findAll(groupIds);
		    groups = new ArrayList<Group>();
		    BeanUtil.map(groupCollections, groups);
		    registeredPatientDetail.setGroups(groups);

		    registeredPatientDetail.setDoctorId(patientCollection.getDoctorId());
		    registeredPatientDetail.setLocationId(patientCollection.getLocationId());
		    registeredPatientDetail.setHospitalId(patientCollection.getHospitalId());
		    registeredPatientDetail.setCreatedTime(patientCollection.getCreatedTime());
		    registeredPatientDetail.setPID(patientCollection.getPID());

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
    public PatientGroupAddEditRequest addGroupToPatient(PatientGroupAddEditRequest request) {
	PatientGroupAddEditRequest response = new PatientGroupAddEditRequest();
	PatientCollection patientCollection = patientRepository.findByUserId(request.getPatientId());
	try {
	    if (request.getGroupIds() != null && !request.getGroupIds().isEmpty()) {
		List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(request.getPatientId());
		if (patientGroupCollections != null && !patientGroupCollections.isEmpty()) {
		    for (PatientGroupCollection patientGroupCollection : patientGroupCollections) {
			patientGroupRepository.delete(patientGroupCollection);
		    }
		}
		for (String group : request.getGroupIds()) {
		    PatientGroupCollection patientGroupCollection = new PatientGroupCollection();
		    patientGroupCollection.setGroupId(group);
		    patientGroupCollection.setPatientId(request.getPatientId());
		    patientCollection.setCreatedTime(new Date());
		    patientGroupRepository.save(patientGroupCollection);
		}
		BeanUtil.map(request, response);
	    }
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
