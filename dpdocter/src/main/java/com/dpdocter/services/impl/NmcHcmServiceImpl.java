package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.NmcHcm;
import com.dpdocter.collections.NmcHcmCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.NmcHcmRepository;
import com.dpdocter.services.NmcHcmServices;

import common.util.web.DPDoctorUtils;

@Service
public class NmcHcmServiceImpl implements NmcHcmServices {

	private static Logger logger = LogManager.getLogger(NmcHcmServiceImpl.class.getName());

	@Autowired
	private NmcHcmRepository nmcHcmRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public NmcHcm addDetails(NmcHcm request) {
		NmcHcm response = null;
		try {
			NmcHcmCollection nmcHcmCollection = new NmcHcmCollection();

			BeanUtil.map(request, nmcHcmCollection);
			nmcHcmCollection.setCreatedTime(new Date());
			nmcHcmCollection.setUpdatedTime(new Date());
			nmcHcmRepository.save(nmcHcmCollection);

			response = new NmcHcm();
			BeanUtil.map(nmcHcmCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while add Nmc details  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add Nmc details " + e.getMessage());
		}

		return response;

	}

	@Override
	public List<NmcHcm> getDetails(int page, int size, String searchTerm, String type) {

		List<NmcHcm> response = null;
		try {

			Criteria criteria = new Criteria();
			if (type != null)
				criteria = criteria.and("type").is(type);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("nameOfFacility").regex("^" + searchTerm, "i"),
						new Criteria("nameOfFacility").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NmcHcmCollection.class, NmcHcm.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Nmc details  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Nmc details " + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countNmcData(String type, String searchTerm, Boolean discarded) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			if (type != null)
				criteria = criteria.and("type").is(type);
			if (discarded != null)
				criteria = criteria.and("discarded").is(discarded);

			if (searchTerm != null)
				criteria = criteria.orOperator(new Criteria("nameOfFacility").regex("^" + searchTerm, "i"),
						new Criteria("nameOfFacility").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), NmcHcmCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting nmc data " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting nmc data " + e.getMessage());

		}
		return response;
	}

}
