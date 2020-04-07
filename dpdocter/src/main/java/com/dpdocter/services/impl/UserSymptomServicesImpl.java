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

import com.dpdocter.beans.Language;
import com.dpdocter.beans.UserSymptom;
import com.dpdocter.collections.LanguageCollection;
import com.dpdocter.collections.UserSymptomCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserSymptomRepository;
import com.dpdocter.services.UserSymptomService;


import common.util.web.DPDoctorUtils;

@Service
public class UserSymptomServicesImpl implements UserSymptomService{
	
	private static Logger logger = LogManager.getLogger(UserSymptomServicesImpl.class.getName());
	
	@Autowired
	private UserSymptomRepository userSymptomRepository;
	
	@Autowired
    private MongoTemplate mongoTemplate;

	@Override
	public UserSymptom addEditUserSymptoms(UserSymptom request) {
		UserSymptom response = null;
		try {
			UserSymptomCollection userSymptomCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				userSymptomCollection = userSymptomRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (userSymptomCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Id Not found");
				}
				request.setUpdatedTime(new Date());
				
				request.setCreatedTime(userSymptomCollection.getCreatedTime());
				BeanUtil.map(request, userSymptomCollection);

			} else {
			 userSymptomCollection = new UserSymptomCollection();
				BeanUtil.map(request, userSymptomCollection);
			
				userSymptomCollection.setUpdatedTime(new Date());
				userSymptomCollection.setCreatedTime(new Date());
			}
			userSymptomCollection = userSymptomRepository.save(userSymptomCollection);
			response = new UserSymptom();
			BeanUtil.map(userSymptomCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add/edit user Symptoms  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit user Symptoms " + e.getMessage());
		}
	
		return response;
	}
	
	@Override
	public List<UserSymptom> getUserSymptoms(int size, int page, Boolean discarded,String searchTerm) {
		List<UserSymptom> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation,UserSymptomCollection.class, UserSymptom.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting user symptoms " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting user symptoms " + e.getMessage());

		}
		return response;

	}
	
	 @Override
		public Integer countUserSymptom(Boolean discarded, String searchTerm) {
			Integer response=null;
			try {
				Criteria criteria = new Criteria("discarded").is(discarded);
//			    criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
//					new Criteria("name").regex("^" + searchTerm));

		response = (int) mongoTemplate.count(new Query(criteria), UserSymptomCollection.class);
	} catch (BusinessException e) {
		logger.error("Error while counting user symptoms " + e.getMessage());
		e.printStackTrace();
		throw new BusinessException(ServiceError.Unknown, "Error while counting user Symptoms " + e.getMessage());

	}
			return response;
		}
	
	
	

}
