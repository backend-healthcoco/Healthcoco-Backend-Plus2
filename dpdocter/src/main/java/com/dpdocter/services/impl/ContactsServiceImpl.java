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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.DoctorContactCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.PatientAdmissionCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactsRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.PatientAdmissionRepository;
import com.dpdocter.repository.PatientRepository;
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
	
	/**
	 * This method returns all unblocked or blocked patients (based on param blocked) of specified doctor.
	 * @param doctorId
	 * @param blocked 
	 * @return List of Patient cards
	 */
	public List<PatientCard> getDoctorContacts(String doctorId,Boolean blocked,int page,int size)  {
		try {
			List<DoctorContactCollection> doctorContactCollections = doctorContactsRepository.findByDoctorId(doctorId,blocked,new PageRequest(page, size, Direction.DESC,"createdDate"));
			if(doctorContactCollections == null){
				return null;
			}
			@SuppressWarnings("unchecked")
			Collection<String> patientIds =  CollectionUtils.collect(doctorContactCollections, new BeanToPropertyValueTransformer("contactId")); 
			List<PatientCard> patientCards = getSpecifiedPatientCards(patientIds);
			return patientCards;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
 	}
	
	
	private List<PatientCard> getSpecifiedPatientCards(Collection<String> patientIds)throws Exception{
		//getting patients from patient ids
		Query queryForGettingPatientsFromPatientIds = new Query();
		queryForGettingPatientsFromPatientIds.addCriteria(Criteria.where("id").in(patientIds));
		List<PatientCollection> patientCollections = mongoTemplate.find(queryForGettingPatientsFromPatientIds, PatientCollection.class);
		//getting usewrIds from patients
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
				PatientAdmissionCollection patientCollection = patientAdmissionRepository.findByUserId(userCollection.getId());
				PatientCard patientCard = new PatientCard();
				BeanUtil.map(patientCollection, patientCard);
				BeanUtil.map(userCollection, patientCard);
				patientCards.add(patientCard);
			}
		}
		
		return patientCards;
	}
	


	public void blockPatient(String patientId, String docterId) {
		try {
			Query query = new Query();
			query.addCriteria(Criteria.where("docterId").is(docterId)).addCriteria(Criteria.where("contactId").is(patientId));
			Update update = new Update();
			update.set("isBlocked", true);
			mongoTemplate.updateFirst(query, update, DoctorContactCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}


	@Override
	public Group addGroup(Group group) {
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


	@Override
	public boolean addToGroup(String userId, String groupId) {
		boolean isAdded = false;
		try {
			GroupCollection groupCollection = groupRepository.findOne(groupId);
			if(groupCollection != null){
				groupCollection.setUserId(userId);
				groupRepository.save(groupCollection);
				isAdded = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return isAdded;
		
	}

}
