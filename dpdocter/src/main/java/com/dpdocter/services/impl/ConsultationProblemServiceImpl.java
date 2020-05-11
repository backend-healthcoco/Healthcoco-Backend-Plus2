package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.ConsultationProblemDetails;
import com.dpdocter.beans.UserSymptom;
import com.dpdocter.collections.ConsultationProblemDetailsCollection;
import com.dpdocter.collections.UserSymptomCollection;
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
	public List<ConsultationProblemDetails> getProblemDetails(int page, int size, String searchTerm,Boolean discarded) {
		List<ConsultationProblemDetails> response = null;
		try {
			
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("problemDetail").regex("^" + searchTerm, "i"),
						new Criteria("problemDetail").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,ConsultationProblemDetailsCollection.class, ConsultationProblemDetails.class)
					.getMappedResults();
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
