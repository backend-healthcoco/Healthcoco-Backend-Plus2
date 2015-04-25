package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.DoctorContactCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.reflections.ReflectionUtil;
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientGroupRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.SearchRequest;
import com.dpdocter.services.ContactsService;

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

	/**
	 * This method returns all unblocked or blocked patients (based on param
	 * blocked) of specified doctor.
	 * 
	 * @param doctorId
	 * @param blocked
	 * @return List of Patient cards
	 */
	public List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request) {
		try {
			List<DoctorContactCollection> doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false,
					new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdDate"));
			if (doctorContactCollections == null) {
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

	private List<PatientCard> getSpecifiedPatientCards(Collection<String> patientIds, String doctorId, String locationId, String hospitalId) throws Exception {
		// getting patients from patient ids
		Query queryForGettingPatientsFromPatientIds = new Query();
		// queryForGettingPatientsFromPatientIds.addCriteria(Criteria.where("id").in(patientIds).andOperator(Criteria.where("doctorId").is(doctorId)).andOperator(Criteria.where("locationId").is(locationId)).andOperator(Criteria.where("hospitalId").is(hospitalId)));
		queryForGettingPatientsFromPatientIds
				.addCriteria(Criteria
						.where("id")
						.in(patientIds)
						.andOperator(Criteria.where("doctorId").is(doctorId), Criteria.where("locationId").is(locationId),
								Criteria.where("hospitalId").is(hospitalId)));
		List<PatientCollection> patientCollections = mongoTemplate.find(queryForGettingPatientsFromPatientIds, PatientCollection.class);
		List<PatientCard> patientCards = new ArrayList<PatientCard>();
		for (PatientCollection patientCollection : patientCollections) {
			UserCollection userCollection = userRepository.findOne(patientCollection.getUserId());
			PatientCard patientCard = new PatientCard();
			BeanUtil.map(patientCollection, patientCard);
			BeanUtil.map(userCollection, patientCard);
			patientCard.setAge(String.valueOf(userCollection.getDob().getAge()));
			patientCards.add(patientCard);
		}

		/*	//getting usewrIds from patients
			@SuppressWarnings("unchecked")
			Collection<String> userIds =  CollectionUtils.collect(patientCollections, new BeanToPropertyValueTransformer("userId"));
			//getting users from userids
			Query queryForGettingUserFromUserIds = new Query();
			queryForGettingUserFromUserIds.addCriteria(Criteria.where("id").in(userIds));
			List<UserCollection> userCollections = mongoTemplate.find(queryForGettingUserFromUserIds, UserCollection.class);
			List<PatientCard> patientCards = null;
			if(userCollections != null){
				patientCards = new ArrayList<PatientCard>();
				for(UserCollection userCollection : userCollections){
					//PatientAdmissionCollection patientAdmissionCollection = patientAdmissionRepository.findByUserId(userCollection.getId());
					PatientCard patientCard = new PatientCard();
					//BeanUtil.map(patientAdmissionCollection, patientCard);
					BeanUtil.map(userCollection, patientCard);
					
					patientCards.add(patientCard);
				}
			}*/

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

	public void blockPatient(String patientId, String docterId) {
		try {
			/*Query query = new Query();
			query.addCriteria(Criteria.where("docterId").is(docterId)).addCriteria(Criteria.where("contactId").is(patientId));
			Update update = new Update();
			update.set("isBlocked", true);
			mongoTemplate.updateFirst(query, update, DoctorContactCollection.class);*/

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
			groupCollection = groupRepository.save(groupCollection);
			BeanUtil.map(groupCollection, group);
			return group;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	public void deleteGroup(String groupId) {
		try {
			groupRepository.delete(groupId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
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
				@SuppressWarnings("unchecked")
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

	public int getcontactsTotalSize(GetDoctorContactsRequest request) {
		try {
			List<DoctorContactCollection> doctorContactCollections = doctorContactsRepository.findByDoctorIdAndIsBlocked(request.getDoctorId(), false);
			if (doctorContactCollections == null) {
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
	public List<Group> getAllGroups(String doctorId, String locationId, String hospitalId) {
		List<Group> groups = null;
		try {
			List<GroupCollection> groupCollections = groupRepository.findByDoctorIdPatientIdHospitalId(doctorId, locationId, hospitalId);
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

}
