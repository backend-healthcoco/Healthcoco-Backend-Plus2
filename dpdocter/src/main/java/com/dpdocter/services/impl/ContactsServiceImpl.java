package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
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
import com.dpdocter.request.SearchRequest;
import com.dpdocter.services.ContactsService;
import com.dpdocter.services.FileManager;
import common.util.web.DPDoctorUtils;

@Service
public class ContactsServiceImpl implements ContactsService {

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
    public List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request) {
	List<DoctorContactCollection> doctorContactCollections = null;
	try {
	    doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false, new PageRequest(request.getPage(),
		    request.getSize(), Direction.DESC, "createdDate"));
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    public List<PatientCard> getDoctorContacts(String doctorId, String createdTime, boolean isDeleted) {
	List<DoctorContactCollection> doctorContactCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if(isDeleted)doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, new Sort(Sort.Direction.DESC, "createdTime"));
		
		if(isDeleted)doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		long createdTimestamp = Long.parseLong(createdTime);
		if(isDeleted)
			doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, new Date(createdTimestamp), new Sort(
			Sort.Direction.DESC, "createdTime"));
		else
			doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(doctorId, false, isDeleted, new Date(createdTimestamp), new Sort(
					Sort.Direction.DESC, "createdTime"));
	    }

	    if (doctorContactCollections.isEmpty()) {
		return null;
	    }
	    @SuppressWarnings("unchecked")
	    Collection<String> patientIds = CollectionUtils.collect(doctorContactCollections, new BeanToPropertyValueTransformer("contactId"));

	    /*
	     * List<PatientGroupCollection> patientGroupCollections =
	     * (List<PatientGroupCollection>) patientGroupRepository
	     * .findByPatientId((List<String>) patientIds);
	     */

	    /*
	     * @SuppressWarnings("unchecked") List<String> groupIds =
	     * (List<String>) CollectionUtils.collect(patientGroupCollections,
	     * new BeanToPropertyValueTransformer("groupId"));
	     * 
	     * doctorContactCollections = filterContactsByGroup(groupIds,
	     * doctorContactCollections);
	     */

	    List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds, doctorId, null, null);
	    return patientCards;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    private List<PatientCard> getSpecifiedPatientCards(Collection<String> patientIds, String doctorId, String locationId, String hospitalId) throws Exception {
	// getting patients from patient ids
	Query queryForGettingPatientsFromPatientIds = new Query();
	// queryForGettingPatientsFromPatientIds.addCriteria(Criteria.where("id").in(patientIds).andOperator(Criteria.where("doctorId").is(doctorId)).andOperator(Criteria.where("locationId").is(locationId)).andOperator(Criteria.where("hospitalId").is(hospitalId)));
	if (!DPDoctorUtils.anyStringEmpty(doctorId, locationId, hospitalId) && patientIds != null && !patientIds.isEmpty()) {
	    queryForGettingPatientsFromPatientIds.addCriteria(Criteria
		    .where("id")
		    .in(patientIds)
		    .andOperator(Criteria.where("doctorId").is(doctorId), Criteria.where("locationId").is(locationId),
			    Criteria.where("hospitalId").is(hospitalId)));
	} else if (patientIds != null && !patientIds.isEmpty() && !DPDoctorUtils.anyStringEmpty(doctorId)) {
	    queryForGettingPatientsFromPatientIds.addCriteria(Criteria.where("id").in(patientIds).andOperator(Criteria.where("doctorId").is(doctorId)));
	} else {
	    return new ArrayList<PatientCard>(0);
	}
	List<PatientCollection> patientCollections = mongoTemplate.find(queryForGettingPatientsFromPatientIds, PatientCollection.class);
	List<PatientCard> patientCards = new ArrayList<PatientCard>();
	for (PatientCollection patientCollection : patientCollections) {
	    UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
	    if (userCollection != null) {
		List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getId());
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
		patientCard.setDoctorSepecificPatientId(patientCollection.getId());
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

    public void blockPatient(String patientId, String docterId) {
	try {
	    DoctorContactCollection doctorContactCollection = doctorContactsRepository.findByDoctorIdAndContactId(docterId, patientId);
	    if (doctorContactCollection != null) {
		doctorContactCollection.setIsBlocked(true);
		doctorContactsRepository.save(doctorContactCollection);
	    } else {
		throw new BusinessException(ServiceError.Unknown, "PatientId and DoctorId send is not proper.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    public Group addEditGroup(Group group) {
	try {
	    GroupCollection groupCollection = new GroupCollection();
	    BeanUtil.map(group, groupCollection);
	    groupCollection.setCreatedTime(new Date());
	    groupCollection = groupRepository.save(groupCollection);
	    BeanUtil.map(groupCollection, group);
	    return group;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    public Boolean deleteGroup(String groupId) {
	Boolean response = false;
	GroupCollection groupCollection = null;
	try {
	    groupCollection = groupRepository.findOne(groupId);
	    if (groupCollection != null) {
		groupCollection.setDeleted(true);
		groupCollection = groupRepository.save(groupCollection);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Group");
	}
	return response;
    }

    public List<PatientCard> searchPatients(SearchRequest request) {
	try {

	} catch (Exception e) {
	    // TODO: handle exception
	}
	return null;
    }

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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return null;
    }

    public List<PatientCard> getDoctorsMostVisitedContacts(String doctorId, int size, int page) {
	try {

	} catch (Exception e) {
	    // TODO: handle exception
	}
	return null;
    }

    public int getContactsTotalSize(GetDoctorContactsRequest request) {
	List<DoctorContactCollection> doctorContactCollections = null;
	try {
	    doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false, new Sort(Sort.Direction.DESC,
		    "createdTime"));
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    /**
     * This service gives lists of all groups for doctor.
     */
    public List<Group> getAllGroups(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<Group> groups = null;
	List<GroupCollection> groupCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
	    	if(isDeleted)
	    		groupCollections = groupRepository.findByDoctorIdPatientIdHospitalId(doctorId, locationId, hospitalId, new Sort(Sort.Direction.DESC,
	    			"createdTime"));
	    	else 
	    		groupCollections = groupRepository.findByDoctorIdPatientIdHospitalId(doctorId, locationId, hospitalId, isDeleted, new Sort(Sort.Direction.DESC,
	    			"createdTime"));
	    } else {
		long createdTimestamp = Long.parseLong(createdTime);
			if(isDeleted)
				groupCollections = groupRepository.findByDoctorIdPatientIdHospitalId(doctorId, locationId, hospitalId, new Date(createdTimestamp),
						new Sort(Sort.Direction.DESC, "createdTime"));
			else
				groupCollections = groupRepository.findByDoctorIdPatientIdHospitalId(doctorId, locationId, hospitalId, isDeleted, new Date(createdTimestamp),
						new Sort(Sort.Direction.DESC, "createdTime"));
	    }

	    if (groupCollections != null) {
		groups = new ArrayList<Group>();
		for (GroupCollection groupCollection : groupCollections) {
		    Group group = new Group();
		    ReflectionUtil.copy(group, groupCollection);
		    groups.add(group);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return groups;
    }

    @Override
    public Boolean importContacts(ImportContactsRequest request) {
	Boolean response = false;
	ImportContactsRequestCollection importContactsRequestCollection = null;
	try {
	    String path = "contacts" + File.separator + request.getDoctorId();
	    String contactsFileUrl = fileManager.saveImageAndReturnImageUrl(request.getContactsFile(), path);
	    request.setContactsFileUrl(contactsFileUrl);
	    importContactsRequestCollection = new ImportContactsRequestCollection();
	    BeanUtil.map(request, importContactsRequestCollection);
	    importContactsRequestCollection = importContactsRequestRepository.save(importContactsRequestCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
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
	    throw new BusinessException(ServiceError.Unknown, "Error Exporting Contact");
	}
	return response;
    }

    @Override
    public List<Group> getAllGroups(String doctorId, String createdTime, boolean isDeleted) {
	List<Group> groups = null;
	List<GroupCollection> groupCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
	    	if(isDeleted)groupCollections = groupRepository.findByDoctorId(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
	    	else groupCollections = groupRepository.findByDoctorId(doctorId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
			if(isDeleted)groupCollections = groupRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
			else groupCollections = groupRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
	    }

	    if (groupCollections != null) {
		groups = new ArrayList<Group>();
		for (GroupCollection groupCollection : groupCollections) {
		    Group group = new Group();
		    BeanUtil.map(groupCollection, group);
		    groups.add(group);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error While Retrieving Groups");
	}
	return groups;
    }

    @Override
    public List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId, String hospitalId, String createdTime, boolean isDeleted) {
	List<RegisteredPatientDetails> registeredPatientDetails = null;
	List<PatientCollection> patientCollections = null;
	List<GroupCollection> groupCollections = null;
	List<Group> groups = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (locationId == null && hospitalId == null) {
			if(isDeleted) patientCollections = patientRepository.findByDoctorId(doctorId, new Sort(Sort.Direction.DESC, "createdTime"));
			else patientCollections = patientRepository.findByDoctorId(doctorId, isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		   if(isDeleted)
			   patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, new Sort(
					    Sort.Direction.DESC, "createdTime"));
		   else 
			   patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, isDeleted, new Sort(
					    Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (locationId == null && hospitalId == null) {
		    if(isDeleted)
		    	patientCollections = patientRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC, "createdTime"));
		    else 
		    	patientCollections = patientRepository.findByDoctorId(doctorId, new Date(createdTimeStamp), isDeleted, new Sort(Sort.Direction.DESC, "createdTime"));
		} else {
		    if(isDeleted)
		    	patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
					    new Sort(Sort.Direction.DESC, "createdTime"));
		    else 
		    	patientCollections = patientRepository.findByDoctorIdLocationIdAndHospitalId(doctorId, locationId, hospitalId, new Date(createdTimeStamp), isDeleted,
					    new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    }

	    if (!patientCollections.isEmpty()) {
		registeredPatientDetails = new ArrayList<RegisteredPatientDetails>();
		for (PatientCollection patientCollection : patientCollections) {
		    UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
		    AddressCollection addressCollection = new AddressCollection();
		    if (patientCollection.getAddressId() != null) {
			addressCollection = addressRepository.findOne(patientCollection.getAddressId());
		    }
		    List<PatientGroupCollection> patientGroupCollections = patientGroupRepository.findByPatientId(patientCollection.getId());
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
		    groupCollections = (List<GroupCollection>) groupRepository.findAll((List<String>) groupIds);
		    groups = new ArrayList<Group>();
		    BeanUtil.map(groupCollections, groups);
		    registeredPatientDetail.setGroups(groups);

		    registeredPatientDetail.setDoctorId(patientCollection.getDoctorId());
		    registeredPatientDetail.setLocationId(patientCollection.getLocationId());
		    registeredPatientDetail.setHospitalId(registeredPatientDetail.getHospitalId());
		    registeredPatientDetail.setCreatedTime(patientCollection.getCreatedTime());

		    registeredPatientDetails.add(registeredPatientDetail);
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return registeredPatientDetails;
    }

}
