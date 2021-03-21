package com.dpdocter.services.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.EmailTrack;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.EmailTrackRepository;
import com.dpdocter.services.EmailTackService;

import common.util.web.DPDoctorUtils;

@Service
public class EmailTrackServiceImpl implements EmailTackService {

    private static Logger logger = LogManager.getLogger(EmailTrackServiceImpl.class.getName());

    @Autowired
    private EmailTrackRepository emailTrackRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    @Transactional
    public List<EmailTrack> getEmailDetails(String patientId, String doctorId, String locationId, String hospitalId, long page, int size) {
	List<EmailTrack> response = null;
	try {
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null , hospitalObjectId= null;
		if(!DPDoctorUtils.anyStringEmpty(patientId))patientObjectId = new ObjectId(patientId);
		if(!DPDoctorUtils.anyStringEmpty(doctorId))doctorObjectId = new ObjectId(doctorId);
    	if(!DPDoctorUtils.anyStringEmpty(locationId))locationObjectId = new ObjectId(locationId);
    	if(!DPDoctorUtils.anyStringEmpty(hospitalId))hospitalObjectId = new ObjectId(hospitalId);
    	
		Criteria criteria = new Criteria("type").in("APPOINTMENT", ComponentType.PRESCRIPTIONS.getType(), ComponentType.VISITS.getType(), ComponentType.CLINICAL_NOTES.getType(), ComponentType.REPORTS.getType());
		
		if (doctorObjectId == null) {
			if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
			    criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("smsDetails.userId").is(patientObjectId);
			}
		    } else {
				if (DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))criteria.and("doctorId").is(doctorObjectId).and("patientId").is(patientObjectId);
				else criteria.and("doctorId").is(doctorObjectId).and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("patientId").is(patientObjectId);
		    }
		    Aggregation aggregation = null;
		    if(size > 0){
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "sentTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
			}else{
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "sentTime")));
			}
		    AggregationResults<EmailTrack> aggregationResults = mongoTemplate.aggregate(aggregation, EmailTrackCollection.class, EmailTrack.class);
		    response = aggregationResults.getMappedResults();
		    
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return response;
    }

    @Override
    @Transactional
    public void saveEmailTrack(EmailTrackCollection emailTrack) {
	try {
	    emailTrackRepository.save(emailTrack);
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

}
