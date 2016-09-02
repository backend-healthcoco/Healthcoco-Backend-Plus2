package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.collections.ClinicContactUsCollection;
import com.dpdocter.enums.DoctorContactStateType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ClinicContactUsRepository;
import com.dpdocter.services.ClinicContactUsService;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicContactUsServiceImpl implements ClinicContactUsService {
	
	private static Logger logger = Logger.getLogger(ClinicContactUsServiceImpl.class.getName());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ClinicContactUsRepository clinicContactUsRepository;
	
	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;	
	
	
	@Override
	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs) {
		String response = null;
		ClinicContactUsCollection clinicContactUsCollection = new ClinicContactUsCollection();
		if(clinicContactUs != null)
		{
			BeanUtil.map(clinicContactUs, clinicContactUsCollection);
			try {
				clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
			    
				if(clinicContactUsCollection != null)
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
	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState) {
		ClinicContactUs response = null;
		if(contactId != null && !(contactId.isEmpty()))
		{
			try {
				ClinicContactUsCollection clinicContactUsCollection = clinicContactUsRepository.findByContactId(contactId);
				if(clinicContactUsCollection != null)
				{
					clinicContactUsCollection.setContactState(contactState);
					clinicContactUsCollection = clinicContactUsRepository.save(clinicContactUsCollection);
					if(clinicContactUsCollection != null)
					{
						response = new ClinicContactUs();
						BeanUtil.map(clinicContactUsCollection, response);
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

	@Override
	public List<ClinicContactUs> getDoctorContactList(int page, int size, String searchTerm) {
		List<ClinicContactUs> response = null;
		//String searchTerm = null;
		Criteria criteria = null;
		try{
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))criteria = new Criteria("locationName").regex("^"+searchTerm,"i");
			Aggregation aggregation = null;
			if(criteria != null)
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			else
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			
			AggregationResults<ClinicContactUs> aggregationResults = mongoTemplate.aggregate(aggregation, ClinicContactUsCollection.class, ClinicContactUs.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}
		return response;
	}

}
