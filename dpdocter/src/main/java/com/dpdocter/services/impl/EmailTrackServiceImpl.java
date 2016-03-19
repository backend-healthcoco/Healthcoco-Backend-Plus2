package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.EmailTrackRepository;
import com.dpdocter.services.EmailTackService;

@Service
public class EmailTrackServiceImpl implements EmailTackService {

    private static Logger logger = Logger.getLogger(EmailTrackServiceImpl.class.getName());

    @Autowired
    private EmailTrackRepository emailTrackRepository;

    @Override
    public List<EmailTrack> getEmailDetails(String patientId, String doctorId, String locationId, String hospitalId, int page, int size) {
	List<EmailTrack> response = null;
	List<EmailTrackCollection> emailTrackCollections = null;
	try {
	    if (doctorId == null) {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			emailTrackCollections = emailTrackRepository.findAll(new PageRequest(page, size, Direction.DESC, "sentTime")).getContent();
		    else
			emailTrackCollections = emailTrackRepository.findAll(new Sort(Sort.Direction.DESC, "sentTime"));
		} else {
		    if (size > 0)
			emailTrackCollections = emailTrackRepository.findAll(locationId, hospitalId, patientId,
				new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			emailTrackCollections = emailTrackRepository.findAll(locationId, hospitalId, patientId, new Sort(Sort.Direction.DESC, "sentTime"));
		}
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (size > 0)
			emailTrackCollections = emailTrackRepository.findAll(doctorId, patientId, new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			emailTrackCollections = emailTrackRepository.findAll(doctorId, patientId, new Sort(Sort.Direction.DESC, "sentTime"));
		} else {
		    if (size > 0)
			emailTrackCollections = emailTrackRepository.findAll(doctorId, locationId, hospitalId, patientId,
				new PageRequest(page, size, Direction.DESC, "sentTime"));
		    else
			emailTrackCollections = emailTrackRepository.findAll(doctorId, locationId, hospitalId, patientId,
				new Sort(Sort.Direction.DESC, "sentTime"));
		}
	    }

	    if (emailTrackCollections != null) {
		response = new ArrayList<EmailTrack>();
		BeanUtil.map(emailTrackCollections, response);
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    public void saveEmailTrack(EmailTrackCollection emailTrack) {
	try {
	    emailTrackRepository.save(emailTrack);
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

}
