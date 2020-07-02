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

import com.dpdocter.collections.FreeQuestionAnswerCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.FreeAnswersDetailRepository;
import com.dpdocter.repository.FreeQuestionAnswerRepository;
import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;
import com.dpdocter.services.FreeQuestionAnswerService;

import common.util.web.DPDoctorUtils;

@Service
public class FreeQuestionAnswerServiceImpl implements FreeQuestionAnswerService {
	private static Logger logger = LogManager.getLogger(FreeQuestionAnswerServiceImpl.class.getName());
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FreeQuestionAnswerRepository freeQuetionAnswerRepository;

	@Autowired
	private FreeAnswersDetailRepository answersDetailRepository;

	@Override
	public FreeAnswerResponse addFreeAnswer(FreeAnswerRequest request) {
		FreeAnswerResponse response = null;
		try {
			FreeQuestionAnswerCollection questionAnswerCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getQuestionId())) {
				questionAnswerCollection = freeQuetionAnswerRepository.findById(new ObjectId(request.getQuestionId()))
						.orElse(null);
			}
			if (questionAnswerCollection == null) {
				questionAnswerCollection = new FreeQuestionAnswerCollection();
				BeanUtil.map(request, questionAnswerCollection);
				questionAnswerCollection.setCreatedTime(new Date());
			}
			questionAnswerCollection = freeQuetionAnswerRepository.save(questionAnswerCollection);
			response = new FreeAnswerResponse();
			BeanUtil.map(questionAnswerCollection, response);
		} catch (Exception e) {
			logger.error("Error while adding Question" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding Question" + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countFreeQuestion(Boolean isDiscarded) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			if (isDiscarded != null)
				criteria.and("isDiscarded").is(isDiscarded);
			response = (int) mongoTemplate.count(new Query(criteria), FreeQuestionAnswerCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Questions " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Questions " + e.getMessage());

		}
		return response;
	}

	@SuppressWarnings("static-access")
	@Override
	public List<FreeQuestionResponse> getFreeQuestionList(int size, int page, String searchTerm, boolean isDiscarded,
			String doctorId, long updatedTime) {
		List<FreeQuestionResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("userId").is(new ObjectId(doctorId));

			
				criteria.and("isDiscarded").is(isDiscarded);
				criteria.where("answersDetails").equals(null);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm),
						new Criteria("desc").regex("^" + searchTerm, "i"),
						new Criteria("desc").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, FreeQuestionAnswerCollection.class, FreeQuestionResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting Questions" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Questions" + e.getMessage());
		}
		return response;
	}

	@Override
	public List<FreeQuestionResponse> getAnsweredQuestionList(int size, int page, String searchTerm, boolean isDiscarded,
			String doctorId, long updatedTime) {
		List<FreeQuestionResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("userId").is(new ObjectId(doctorId));

				criteria.and("isDiscarded").is(isDiscarded);
				criteria.where("answersDetails").ne(null);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm),
						new Criteria("desc").regex("^" + searchTerm, "i"),
						new Criteria("desc").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, FreeQuestionAnswerCollection.class, FreeQuestionResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			logger.error("Error while getting Questions" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Questions" + e.getMessage());
		}
		return response;
	}

}
