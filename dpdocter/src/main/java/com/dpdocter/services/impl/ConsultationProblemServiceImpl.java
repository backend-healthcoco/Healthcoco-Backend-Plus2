package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.collections.ConsultationProblemDetailsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ConsultationProblemDetailsRepository;
import com.dpdocter.request.ConsultationProblemDetailsRequest;
import com.dpdocter.services.ConsultationProblemDetailsService;

import common.util.web.DPDoctorUtils;


@Service
public class ConsultationProblemServiceImpl implements ConsultationProblemDetailsService{
	
	private static Logger logger = LogManager.getLogger(ConsultationProblemServiceImpl.class.getName());

	@Autowired
	private ConsultationProblemDetailsRepository consultationProblemDetailsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public ConsultationProblemDetails addEditProblemDetails(ConsultationProblemDetailsRequest request) {
		ConsultationProblemDetails response=null;
		ConsultationProblemDetailsCollection consultationProblemDetailsCollection=null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				consultationProblemDetailsCollection = consultationProblemDetailsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (consultationProblemDetailsCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "bankDetailsId Not found");
				}
			request.setUpdatedTime(new Date());
			BeanUtil.map(request, consultationProblemDetailsCollection);
			
			
		}
		else{
			consultationProblemDetailsCollection=new ConsultationProblemDetailsCollection();
			consultationProblemDetailsCollection.setCreatedTime(new Date());
			consultationProblemDetailsCollection.setUpdatedTime(new Date());
			BeanUtil.map(request, consultationProblemDetailsCollection);
		}
			consultationProblemDetailsRepository.save(consultationProblemDetailsCollection);
		response=new ConsultationProblemDetails();
		BeanUtil.map(consultationProblemDetailsCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while add/edit consultation problem Details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit consultation problem Details " + e.getMessage());

		}
		
		return response;
	}


	@Override
	public ConsultationProblemDetails getProblemDetails(String problemDetailsId) {
		ConsultationProblemDetails response = null;
		try {
			
		ConsultationProblemDetailsCollection problemDetailsCollection=consultationProblemDetailsRepository.findById(new ObjectId(problemDetailsId)).orElse(null);
			
		if(problemDetailsCollection ==null)
		 {
	    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
	    }
		
			response = new ConsultationProblemDetails();
			BeanUtil.map(problemDetailsCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while getting consultation problem details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting consultation problem details " + e.getMessage());

		}
		return response;

	}
	
	 @Override
		public Integer countConsultationProblemDetails(Boolean discarded, String searchTerm) {
			Integer response=null;
			try {
				Criteria criteria = new Criteria("discarded").is(discarded);
			    criteria = criteria.orOperator(new Criteria("problemDetail").regex("^" + searchTerm, "i"),
					new Criteria("problemDetail").regex("^" + searchTerm));
		
		response = (int) mongoTemplate.count(new Query(criteria), ConsultationProblemDetailsCollection.class);
	} catch (BusinessException e) {
		logger.error("Error while counting Consultation Problem Details " + e.getMessage());
		e.printStackTrace();
		throw new BusinessException(ServiceError.Unknown, "Error while counting Consultation Problem Details " + e.getMessage());

	}
			return response;
		}


}
