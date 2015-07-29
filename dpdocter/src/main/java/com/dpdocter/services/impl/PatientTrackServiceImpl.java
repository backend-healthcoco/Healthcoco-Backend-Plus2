package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.beans.PatientTrack;
import com.dpdocter.collections.PatientTrackCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientTrackRepository;
import com.dpdocter.services.PatientTrackService;

public class PatientTrackServiceImpl implements PatientTrackService {

    @Autowired
    private PatientTrackRepository patientTrackRepository;

    @Override
    public boolean addRecord(PatientTrack request) {
	boolean response = false;
	try {
	    PatientTrackCollection patientTrackCollection = new PatientTrackCollection();
	    BeanUtil.map(request, patientTrackCollection);
	    patientTrackRepository.save(patientTrackCollection);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while saving patient track record : " + e.getCause().getMessage());
	}
	return response;
    }

}
