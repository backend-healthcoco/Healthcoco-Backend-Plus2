package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.AccessModule;
import com.dpdocter.collections.AcosCollection;
import com.dpdocter.collections.ArosAcosCollection;
import com.dpdocter.collections.ArosCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AcosRepository;
import com.dpdocter.repository.ArosAcosRepository;
import com.dpdocter.repository.ArosRepository;
import com.dpdocter.response.ArosAcosLookupResponse;
import com.dpdocter.response.ArosLookupResponse;
import com.dpdocter.services.AccessControlServices;

import common.util.web.DPDoctorUtils;

@Service
public class AccessControlServicesImpl implements AccessControlServices {

    @Autowired
    private ArosRepository arosRepository;

    @Autowired
    private AcosRepository acosRepository;

    @Autowired
    private ArosAcosRepository arosAcosRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    @Transactional
    public AccessControl getAccessControls(ObjectId roleOrUserId, ObjectId locationId, ObjectId hospitalId) {
	AccessControl response = null;
	try {	
	    response = new AccessControl();
	    Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("roleOrUserId").is(roleOrUserId).and("locationId").is(locationId).and("hospitalId").is(hospitalId)),
	    		Aggregation.lookup("aros_acos_cl", "_id", "arosId", "arosAcos"), Aggregation.unwind("arosAcos"), Aggregation.unwind("arosAcos.acosIds"),
	    		Aggregation.lookup("acos_cl", "arosAcos.acosIds", "_id", "acos"), Aggregation.unwind("acos"));
	    
	    List<ArosAcosLookupResponse> acosLookupResponses = mongoTemplate.aggregate(aggregation, ArosCollection.class, ArosAcosLookupResponse.class).getMappedResults();
		if (acosLookupResponses != null && !acosLookupResponses.isEmpty()) {
			List<AccessModule> accessModules = new ArrayList<AccessModule>();

		    for (ArosAcosLookupResponse arosAcosLookupResponse : acosLookupResponses) {
			AccessModule accessModule = new AccessModule();
			BeanUtil.map(arosAcosLookupResponse.getAcos(), accessModule);
			accessModules.add(accessModule);
			response.setId(arosAcosLookupResponse.getArosAcos().getId().toString());
		    }
		    response.setAccessModules(accessModules);
		}
	    response.setRoleOrUserId(roleOrUserId.toString());
	    if(!DPDoctorUtils.anyStringEmpty(locationId))response.setLocationId(locationId.toString());
	    if(!DPDoctorUtils.anyStringEmpty(hospitalId))response.setHospitalId(hospitalId.toString());
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public AccessControl setAccessControls(AccessControl accessControl) {
	AccessControl response = null;
	try {
		
		ObjectId roleOrUserObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(accessControl.getRoleOrUserId()))roleOrUserObjectId = new ObjectId(accessControl.getRoleOrUserId());
    	if(!DPDoctorUtils.anyStringEmpty(accessControl.getLocationId()))locationObjectId = new ObjectId(accessControl.getLocationId());
    	if(!DPDoctorUtils.anyStringEmpty(accessControl.getHospitalId()))hospitalObjectId = new ObjectId(accessControl.getHospitalId());
    	
	    ArosCollection arosCollection = arosRepository.find(roleOrUserObjectId, locationObjectId, hospitalObjectId);
	    ArosAcosCollection arosAcosCollection = null;
	    List<AcosCollection> acosCollections = null;
	    if (arosCollection != null) {
		arosAcosCollection = arosAcosRepository.findByArosId(arosCollection.getId());

		Iterator<AcosCollection> acosCollectionIterator = acosRepository.findAll(arosAcosCollection.getAcosIds()).iterator();

		acosCollections = IteratorUtils.toList(acosCollectionIterator);

		if (acosCollections != null) {
		    for (AccessModule accessModule : accessControl.getAccessModules()) {
			    boolean match = false;
				for (AcosCollection acosCollection : acosCollections) {
				    if (accessModule.getModule() != null && accessModule.getUrl() != null  && accessModule.getModule().trim().equals(acosCollection.getModule())  && accessModule.getUrl().trim().equals(acosCollection.getUrl())) {
					BeanUtil.map(accessModule, acosCollection);
					match = true;
					break;
				    }
				}
				if (!match) {
				    AcosCollection acosCollection = new AcosCollection();
				    BeanUtil.map(accessModule, acosCollection);
				    acosCollections.add(acosCollection);
				}
		    }
		} else {
		    acosCollections = new ArrayList<AcosCollection>();
		    if (accessControl.getAccessModules() != null && !accessControl.getAccessModules().isEmpty()) {
			for (AccessModule accessModule : accessControl.getAccessModules()) {
				AcosCollection acosCollection = new AcosCollection();
			    BeanUtil.map(accessModule, acosCollection);
			    acosCollections.add(acosCollection);
			}
		    }
		}
		acosCollections = acosRepository.saveAll(acosCollections);
	    } else {
		arosCollection = new ArosCollection();
		acosCollections = new ArrayList<AcosCollection>();
		arosAcosCollection = new ArosAcosCollection();
		if (accessControl.getAccessModules() != null && !accessControl.getAccessModules().isEmpty()) {
		    for (AccessModule accessModule : accessControl.getAccessModules()) {
		    	AcosCollection acosCollection = new AcosCollection();
				BeanUtil.map(accessModule, acosCollection);
				acosCollections.add(acosCollection);
		    }
		}
		BeanUtil.map(accessControl, arosCollection);
	    }

	    arosCollection = arosRepository.save(arosCollection);
	    acosCollections = acosRepository.saveAll(acosCollections);
	    arosAcosCollection.setArosId(arosCollection.getId());
	    List<ObjectId> acosIds = new ArrayList<ObjectId>(CollectionUtils.collect(acosCollections, new BeanToPropertyValueTransformer("id")));
	    List<ObjectId> finalAcosIds = arosAcosCollection.getAcosIds();
	    if (finalAcosIds == null)
		finalAcosIds = new ArrayList<ObjectId>();
	    finalAcosIds.addAll(acosIds);
	    finalAcosIds = new ArrayList<ObjectId>(new LinkedHashSet<ObjectId>(finalAcosIds));
	    arosAcosCollection.setAcosIds(finalAcosIds);
	    arosAcosCollection = arosAcosRepository.save(arosAcosCollection);

	    response = new AccessControl();
	    BeanUtil.map(arosCollection, response);
	    List<AccessModule> accessModules = new ArrayList<AccessModule>();
	    for(AcosCollection acosCollection : acosCollections){
	    	AccessModule accessModule = new AccessModule();
	    	BeanUtil.map(acosCollection, accessModule);
	    	accessModules.add(accessModule);
	    }
	    response.setAccessModules(accessModules);
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }
    
    @Override
    @Transactional
    public List<AccessControl> getAllAccessControls(Collection<ObjectId> roleIds, ObjectId locationId, ObjectId hospitalId) {
    	List<AccessControl> response = null;
	try {	
		
		Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("roleOrUserId").in(roleIds)
				.and("locationId").is(locationId).and("hospitalId").is(hospitalId)),
				Aggregation.lookup("aros_acos_cl", "id", "arosId", "arosAcosCollection"), Aggregation.unwind("arosAcosCollection"));
	    List<ArosLookupResponse> arosLookupResponses = mongoTemplate.aggregate(aggregation, ArosCollection.class, ArosLookupResponse.class).getMappedResults();
	    if (arosLookupResponses != null && !arosLookupResponses.isEmpty()) {
	    	for(ArosLookupResponse arosCollection : arosLookupResponses){
	    		AccessControl accessControl = new AccessControl();
	    		ArosAcosCollection arosAcosCollection = arosCollection.getArosAcosCollection();
	    		if (arosAcosCollection != null && !arosAcosCollection.getAcosIds().isEmpty()) {
	    			List<AcosCollection> acosCollections = acosRepository.findAll(arosAcosCollection.getAcosIds());
	    			List<AccessModule> accessModules = new ArrayList<AccessModule>();

	    			for (AcosCollection acosCollection : acosCollections) {
	    					AccessModule accessModule = new AccessModule();
	    					BeanUtil.map(acosCollection, accessModule);
	    					accessModules.add(accessModule);
	    			}
	    			accessControl.setAccessModules(accessModules);
	    			accessControl.setId(arosAcosCollection.getId().toString());
	    			accessControl.setRoleOrUserId(arosCollection.getRoleOrUserId().toString());
	    			if(!DPDoctorUtils.anyStringEmpty(locationId))accessControl.setLocationId(locationId.toString());
	    			if(!DPDoctorUtils.anyStringEmpty(hospitalId))accessControl.setHospitalId(hospitalId.toString());
	    		}
	    		response.add(accessControl);
	    	}
	    }
	   
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }
}
