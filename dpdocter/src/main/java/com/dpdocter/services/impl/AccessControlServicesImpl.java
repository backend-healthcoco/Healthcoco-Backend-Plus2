package com.dpdocter.services.impl;

import java.util.List;

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

    @Override
    public AccessControl getAccessControls(String roleOrUserId, String locationId, String hospitalId) {
	AccessControl response = null;
	try {
	    ArosCollection arosCollection = arosRepository.findOne(roleOrUserId, locationId, hospitalId);

	    ArosAcosCollection arosAcosCollection = arosAcosRepository.findByArosId(arosCollection.getId());

	    AcosCollection acosCollection = acosRepository.findOne(arosAcosCollection.getAcosId());

	    BeanUtil.map(arosCollection, response);
	    BeanUtil.map(acosCollection, response);
	    BeanUtil.map(arosAcosCollection, response);
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }

    @Override
    public AccessControl setAccessControls(AccessControl accessControl) {
	AccessControl response = null;
	try {
	    ArosCollection arosCollection = arosRepository.findOne(accessControl.getRoleOrUserId(), accessControl.getLocationId(),
		    accessControl.getHospitalId());
	    ArosAcosCollection arosAcosCollection = null;
	    AcosCollection acosCollection = null;
	    if (arosCollection != null) {
		arosAcosCollection = arosAcosRepository.findByArosId(arosCollection.getId());

		acosCollection = acosRepository.findOne(arosAcosCollection.getAcosId());

		if (acosCollection != null) {
		    if (acosCollection.getAccessModules() != null && !acosCollection.getAccessModules().isEmpty()) {
			if (accessControl.getAccessModules() != null && !accessControl.getAccessModules().isEmpty()) {
			    for (AccessModule newAccessModule : accessControl.getAccessModules()) {
				boolean moduleMatch = false;
				for (AccessModule accessModule : acosCollection.getAccessModules()) {
				    if (newAccessModule.getModule().trim().equals(accessModule.getModule())) {
					BeanUtil.map(newAccessModule, accessModule);
					moduleMatch = true;
					break;
				    }
				}
				if (!moduleMatch) {
				    AccessModule accessModule = new AccessModule();
				    BeanUtil.map(newAccessModule, accessModule);
				    List<AccessModule> updatedAccessModules = acosCollection.getAccessModules();
				    updatedAccessModules.add(accessModule);
				    acosCollection.setAccessModules(updatedAccessModules);
				}
			    }
			}

		    }
		}
		acosCollection = acosRepository.save(acosCollection);
	    } else {
		arosCollection = new ArosCollection();
		acosCollection = new AcosCollection();
		arosAcosCollection = new ArosAcosCollection();
		BeanUtil.map(accessControl, arosCollection);
		BeanUtil.map(accessControl, acosCollection);
		arosCollection = arosRepository.save(arosCollection);
		acosCollection = acosRepository.save(acosCollection);
		arosAcosCollection.setArosId(arosCollection.getId());
		arosAcosCollection.setAcosId(acosCollection.getId());
		arosAcosCollection = arosAcosRepository.save(arosAcosCollection);
	    }
	    BeanUtil.map(arosCollection, response);
	    BeanUtil.map(acosCollection, response);
	    BeanUtil.map(arosAcosCollection, response);
	} catch (Exception e) {
	    throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
	}
	return response;
    }
}
