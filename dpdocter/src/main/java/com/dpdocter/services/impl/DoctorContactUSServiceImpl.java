package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.services.DoctorContactUsService;

@Service
public class DoctorContactUSServiceImpl implements DoctorContactUsService {
	
	private static Logger logger = Logger.getLogger(DoctorContactUSServiceImpl.class.getName());
	
	@Autowired
	DoctorContactUsRepository doctorContactUsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Override
	@Transactional
	public String submitDoctorContactUSInfo(DoctorContactUs doctorContactUs) {
		String response = null;
		DoctorContactUsCollection doctorContactUsCollection = new DoctorContactUsCollection();
		if(doctorContactUs != null)
		{
			BeanUtil.map(doctorContactUs, doctorContactUsCollection);
			try {
				doctorContactUsCollection.setUserName(doctorContactUs.getEmailAddress());
				doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
				if(doctorContactUsCollection != null)
				{
					response = doctorWelcomeMessage;
				}
			} catch (DuplicateKeyException de) {
			    logger.error(de);
			    throw new BusinessException(ServiceError.Unknown, "An account already exists with this email address.Please use another email address to register.");
			} catch (BusinessException be) {
			    logger.error(be);
			    throw be;
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating doctor");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<DoctorContactUs> getDoctorContactList(int page, int size) {
		List<DoctorContactUs> response = null;
		try{
			Aggregation aggregation = null;
			if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
			else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation, DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DoctorContactUs updateDoctorContactState(String contactId, DoctorContactStateType contactState)
	{
		DoctorContactUs response = null;
		if(contactId != null || !(contactId.isEmpty()))
		{
			try {
				DoctorContactUsCollection doctorContactUsCollection = doctorContactUsRepository.findByContactId(contactId);
				if(doctorContactUsCollection != null)
				{
					doctorContactUsCollection.setContactState(contactState);
					doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
					if(doctorContactUsCollection != null)
					{
						response = new DoctorContactUs();
						BeanUtil.map(doctorContactUsCollection, response);
					}
				}
			} catch (Exception e) {
				logger.warn("Error while updating contact state :: "+e);
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while updating doctor contact state " + e.getMessage());
			}
		}	
		return response;
	}
}
