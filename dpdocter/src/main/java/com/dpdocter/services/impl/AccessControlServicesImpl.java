package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AccessControlServices;

@Service
public class AccessControlServicesImpl implements AccessControlServices {

    @Autowired
    private ArosRepository arosRepository;

    @Autowired
    private AcosRepository acosRepository;

    @Autowired
    private ArosAcosRepository arosAcosRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @SuppressWarnings("unchecked")
    @Override
    public AccessControl getAccessControls(String roleOrUserId, String locationId, String hospitalId) {
	AccessControl response = null;
	try {
	    response = new AccessControl();
	    ArosCollection arosCollection = arosRepository.findOne(roleOrUserId, locationId, hospitalId);
	    if (arosCollection != null) {
		ArosAcosCollection arosAcosCollection = arosAcosRepository.findByArosId(arosCollection.getId());
		if (arosAcosCollection != null && !arosAcosCollection.getAcosIds().isEmpty()) {
		    Iterator<AcosCollection> acosCollectionIterator = acosRepository.findAll(arosAcosCollection.getAcosIds()).iterator();

		    List<AcosCollection> acosCollections = IteratorUtils.toList(acosCollectionIterator);

		    List<AccessModule> accessModules = new ArrayList<AccessModule>();

		    for (AcosCollection acosCollection : acosCollections) {
			AccessModule accessModule = new AccessModule();
			BeanUtil.map(acosCollection, accessModule);
			accessModules.add(accessModule);
		    }
		    response.setAccessModules(accessModules);
		    response.setId(arosAcosCollection.getId());
		}
	    }
	    response.setRoleOrUserId(roleOrUserId);
	    response.setLocationId(locationId);
	    response.setHospitalId(hospitalId);
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AccessControl setAccessControls(AccessControl accessControl) {
	AccessControl response = null;
	try {
	    ArosCollection arosCollection = arosRepository.findOne(accessControl.getRoleOrUserId(), accessControl.getLocationId(),
		    accessControl.getHospitalId());
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
			    if (accessModule.getModule() != null && accessModule.getUrl() != null &&
			    		accessModule.getModule().trim().equals(acosCollection.getModule())
				    && accessModule.getUrl().trim().equals(acosCollection.getUrl())) {
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
		acosCollections = acosRepository.save(acosCollections);
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
	    acosCollections = acosRepository.save(acosCollections);
	    arosAcosCollection.setArosId(arosCollection.getId());
	    List<String> acosIds = new ArrayList<String>(CollectionUtils.collect(acosCollections, new BeanToPropertyValueTransformer("id")));
	    List<String> finalAcosIds = arosAcosCollection.getAcosIds();
	    if (finalAcosIds == null)
		finalAcosIds = new ArrayList<String>();
	    finalAcosIds.addAll(acosIds);
	    finalAcosIds = new ArrayList<String>(new LinkedHashSet<String>(finalAcosIds));
	    arosAcosCollection.setAcosIds(finalAcosIds);
	    arosAcosCollection = arosAcosRepository.save(arosAcosCollection);

	    response = new AccessControl();
	    BeanUtil.map(arosCollection, response);
	    List<AccessModule> accessModules = new ArrayList<AccessModule>();
	    BeanUtil.map(acosCollections, accessModules);
	    response.setAccessModules(accessModules);
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }
}
