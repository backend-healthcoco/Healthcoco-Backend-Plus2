package com.dpdocter.services.impl;

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
import com.dpdocter.collections.LanguageCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LanguageRepository;
import com.dpdocter.services.LanguageService;

import common.util.web.DPDoctorUtils;

@Service
public class LanguageServiceImpl implements LanguageService{
	
	private static Logger logger = LogManager.getLogger(LanguageServiceImpl.class.getName());

	@Autowired
	LanguageRepository languageRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<Language> getLanguages(int size, int page, Boolean discarded,String searchTerm) {
		List<Language> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
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
			response = mongoTemplate.aggregate(aggregation, LanguageCollection.class, Language.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting language " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting language " + e.getMessage());

		}
		return response;

	}
    
    @Override
	public Integer countLanguage(Boolean discarded, String searchTerm) {
		Integer response=null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
		    criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
				new Criteria("name").regex("^" + searchTerm));
	
	response = (int) mongoTemplate.count(new Query(criteria), LanguageCollection.class);
} catch (BusinessException e) {
	logger.error("Error while counting language " + e.getMessage());
	e.printStackTrace();
	throw new BusinessException(ServiceError.Unknown, "Error while counting language " + e.getMessage());

}
		return response;
	}

    @Override
	public Language getLanguage(String id) {
		Language response=null;
		try {
			LanguageCollection languageCollection=languageRepository.findById(new ObjectId(id)).orElse(null);
		    if(languageCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(languageCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		
		return response;
	}
}