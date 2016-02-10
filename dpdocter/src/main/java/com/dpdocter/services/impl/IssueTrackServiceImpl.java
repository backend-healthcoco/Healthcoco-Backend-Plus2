package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.IssueTrack;
import com.dpdocter.collections.IssueTrackCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.IssueStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.IssueTrackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.IssueTrackService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;

import common.util.web.DPDoctorUtils;

@Service
public class IssueTrackServiceImpl implements IssueTrackService {

    private static Logger logger = Logger.getLogger(IssueTrackServiceImpl.class.getName());

    @Autowired
    private IssueTrackRepository issueTrackRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private MailBodyGenerator mailBodyGenerator;

    @Autowired
    private UserRepository userRepository;

    @Override
    public IssueTrack addEditIssue(IssueTrack request) {
	IssueTrack response = null;
	IssueTrackCollection issueTrackCollection = new IssueTrackCollection();
	BeanUtil.map(request, issueTrackCollection);
	try {
	    if (request.getId() == null) {
		List<IssueTrackCollection> issueTrackCollections = issueTrackRepository.findAll(request.getDoctorId(), request.getLocationId(),
			request.getHospitalId());
		if (issueTrackCollections.isEmpty())
		    issueTrackCollection.setIssueCode("ISH" + 1);
		else {
		    int counter = issueTrackCollections.size() + 1;
		    issueTrackCollection.setIssueCode("ISH" + counter);
		}

		issueTrackCollection.setCreatedTime(new Date());
	    } else {
		IssueTrackCollection oldIssueTrackCollection = issueTrackRepository.findOne(request.getId());
		issueTrackCollection.setCreatedTime(oldIssueTrackCollection.getCreatedTime());
		issueTrackCollection.setCreatedBy(oldIssueTrackCollection.getCreatedBy());
		issueTrackCollection.setDiscarded(oldIssueTrackCollection.getDiscarded());
		issueTrackCollection.setStatus(oldIssueTrackCollection.getStatus());
		issueTrackCollection.setIssueCode(oldIssueTrackCollection.getIssueCode());
	    }

	    if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
		UserCollection userCollection = userRepository.findOne(request.getDoctorId());
		String body = mailBodyGenerator.generateIssueTrackEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			userCollection.getMiddleName(), userCollection.getLastName());
		mailService.sendEmail(userCollection.getEmailAddress(), "Issue Track", body, null);

	    }
	    issueTrackCollection = issueTrackRepository.save(issueTrackCollection);

	    if (issueTrackCollection != null) {
		response = new IssueTrack();
		BeanUtil.map(issueTrackCollection, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Add Edit Issue");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Add Edit Issue");
	}
	return response;
    }

    @Override
    public List<IssueTrack> getIssues(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
	    List<String> scope) {
	List<IssueTrack> response = null;
	List<IssueTrackCollection> issueTrackCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    if (discarded)
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(updatedTime);
	    if (scope.isEmpty()) {
		if (doctorId == null) {
		    if (size > 0)
			issueTrackCollections = issueTrackRepository.findAll(new Date(createdTimeStamp), discards,
				new PageRequest(page, size, Direction.DESC, "updatedTime"));
		    else
			issueTrackCollections = issueTrackRepository.findAll(new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "updatedTime"));
		} else {
		    if (locationId == null && hospitalId == null) {
			if (size > 0)
			    issueTrackCollections = issueTrackRepository.findAll(doctorId, new Date(createdTimeStamp), discards,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    issueTrackCollections = issueTrackRepository.findAll(doctorId, new Date(createdTimeStamp), discards,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    issueTrackCollections = issueTrackRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    issueTrackCollections = issueTrackRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		issueTrackCollections = new ArrayList<IssueTrackCollection>();
		for (String status : scope) {
		    List<IssueTrackCollection> localCollection = null;
		    status = status.toUpperCase();
		    if (doctorId == null) {
			if (size > 0)
			    localCollection = issueTrackRepository.findByStatus(status, new Date(createdTimeStamp), discards,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    localCollection = issueTrackRepository.findByStatus(status, new Date(createdTimeStamp), discards,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (locationId == null && hospitalId == null) {
			    if (size > 0)
				localCollection = issueTrackRepository.findAll(doctorId, status, new Date(createdTimeStamp), discards,
					new PageRequest(page, size, Direction.DESC, "updatedTime"));
			    else
				localCollection = issueTrackRepository.findAll(doctorId, status, new Date(createdTimeStamp), discards,
					new Sort(Sort.Direction.DESC, "updatedTime"));
			} else {
			    if (size > 0)
				localCollection = issueTrackRepository.findAll(doctorId, locationId, hospitalId, status, new Date(createdTimeStamp), discards,
					new PageRequest(page, size, Direction.DESC, "updatedTime"));
			    else
				localCollection = issueTrackRepository.findAll(doctorId, locationId, hospitalId, status, new Date(createdTimeStamp), discards,
					new Sort(Sort.Direction.DESC, "updatedTime"));
			}
		    }
		    issueTrackCollections.addAll(localCollection);
		}
	    }

	    if (issueTrackCollections != null) {
		response = new ArrayList<IssueTrack>();
		BeanUtil.map(issueTrackCollections, response);
	    } else {
		logger.warn("No Issues Found");
		throw new BusinessException(ServiceError.NotFound, "No Issues Found");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Issue");
	    throw new BusinessException(ServiceError.Forbidden, "Error Occurred While Getting Issue");
	}
	return response;
    }

    @Override
    public Boolean updateIssueStatus(String issueId, String status, String doctorId, String locationId, String hospitalId) {
	Boolean response = false;
	IssueTrackCollection issueTrackCollection = null;
	try {
	    issueTrackCollection = issueTrackRepository.findOne(issueId);
	    if (issueTrackCollection != null) {
		if (issueTrackCollection.getDoctorId() != null && issueTrackCollection.getHospitalId() != null
			&& issueTrackCollection.getLocationId() != null) {
		    if (issueTrackCollection.getDoctorId().equals(doctorId) && issueTrackCollection.getHospitalId().equals(hospitalId)
			    && issueTrackCollection.getLocationId().equals(locationId)) {
			if (issueTrackCollection.getStatus().equals(IssueStatus.COMPLETED)) {
			    if (IssueStatus.valueOf(status.toUpperCase()).equals(IssueStatus.REOPEN)) {
				issueTrackCollection.setStatus(IssueStatus.valueOf(status.toUpperCase()));
				issueTrackRepository.save(issueTrackCollection);
				response = true;
			    } else {
				logger.warn("Doctor can only reopen the issue");
				throw new BusinessException(ServiceError.NotAcceptable, "Doctor can only reopen the issue");
			    }
			} else {
			    logger.warn("Doctor can only reopen the issue if issue is Completed");
			    throw new BusinessException(ServiceError.NotAcceptable, "Doctor can only reopen the issue if issue is Completed");
			}
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
		    throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
		}
	    } else {
		logger.warn("Issue not found!");
		throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while updating status");
	    throw new BusinessException(ServiceError.Forbidden, "Error while updating status");
	}
	return response;
    }

    @Override
    public Boolean updateIssueStatus(String issueId, String status) {
	Boolean response = false;
	IssueTrackCollection issueTrackCollection = null;
	try {
	    issueTrackCollection = issueTrackRepository.findOne(issueId);
	    if (issueTrackCollection != null) {
		if (!IssueStatus.valueOf(status.toUpperCase()).equals(IssueStatus.REOPEN)) {
		    issueTrackCollection.setStatus(IssueStatus.valueOf(status.toUpperCase()));
		    issueTrackRepository.save(issueTrackCollection);
		    response = true;
		} else {
		    logger.warn("Only Doctor can reopen the issue");
		    throw new BusinessException(ServiceError.NotAcceptable, "Only Doctor can reopen the issue");
		}

	    } else {
		logger.warn("Issue not found!");
		throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while updating status");
	    throw new BusinessException(ServiceError.Forbidden, "Error while updating status");
	}
	return response;

    }

    @Override
    public Boolean deleteIssue(String issueId, String doctorId, String locationId, String hospitalId, Boolean discarded) {
	try {
	    IssueTrackCollection issueTrackCollection = issueTrackRepository.findOne(issueId);
	    if (issueTrackCollection != null) {
		if (issueTrackCollection.getDoctorId() != null && issueTrackCollection.getHospitalId() != null
			&& issueTrackCollection.getLocationId() != null) {
		    if (issueTrackCollection.getDoctorId().equals(doctorId) && issueTrackCollection.getHospitalId().equals(hospitalId)
			    && issueTrackCollection.getLocationId().equals(locationId)) {
			issueTrackCollection.setDiscarded(discarded);
			issueTrackCollection.setUpdatedTime(new Date());
			issueTrackRepository.save(issueTrackCollection);
			return true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
		    throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id, Hospital Id, Or Location Id");
		}

	    } else {
		logger.warn("Issue not found!");
		throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

}
