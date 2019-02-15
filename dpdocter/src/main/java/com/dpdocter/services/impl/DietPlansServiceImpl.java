package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.beans.DietplanItem;
import com.dpdocter.collections.DietPlanCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DietPlanRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.DietPlansService;

import common.util.web.DPDoctorUtils;

@Service
public class DietPlansServiceImpl implements DietPlansService {

	private static Logger logger = Logger.getLogger(DietPlansServiceImpl.class.getName());
	@Autowired
	private DietPlanRepository dietPlanRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public DietPlan addEditDietPlan(DietPlan request) {
		DietPlan response = null;
		try {
			DietPlanCollection dietPlanCollection = null;
			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				dietPlanCollection = dietPlanRepository.findOne(new ObjectId(request.getId()));
				if (dietPlanCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " No Diet Plan found with Id ");
				}
				request.setCreatedBy(dietPlanCollection.getCreatedBy());
				request.setCreatedTime(dietPlanCollection.getCreatedTime());
				request.setUniquePlanId(dietPlanCollection.getUniquePlanId());
				request.setUpdatedTime(new Date());
				dietPlanCollection.setItems(new ArrayList<DietplanItem>());
				BeanUtil.map(request, dietPlanCollection);

			} else {
				dietPlanCollection = new DietPlanCollection();
				BeanUtil.map(request, dietPlanCollection);
				dietPlanCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
				dietPlanCollection.setCreatedTime(new Date());
				dietPlanCollection.setUniquePlanId(
						UniqueIdInitial.DIET_PLAN.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				dietPlanCollection.setUpdatedTime(new Date());

			}
			dietPlanCollection = dietPlanRepository.save(dietPlanCollection);
			response = new DietPlan();
			BeanUtil.map(dietPlanCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<DietPlan> getDietPlans(int page, int size, String patientId, String doctorId, String hospitalId,
			String locationId, long updatedTime, boolean discarded) {
		List<DietPlan> response = null;
		try {

			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTime));
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);
			if (!DPDoctorUtils.anyStringEmpty(patientObjectId))
				criteria.and("patientId").is(patientObjectId);

			if (!discarded) {
				criteria.and("discarded").is(discarded);
			}

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<DietPlan> aggregationResults = mongoTemplate.aggregate(aggregation,
					DietPlanCollection.class, DietPlan.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plans : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plans : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DietPlan getDietPlanById(String planId) {
		DietPlan response = null;
		try {
			DietPlanCollection dietPlanCollection = dietPlanRepository.findOne(new ObjectId(planId));
			response = new DietPlan();
			BeanUtil.map(dietPlanCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public DietPlan discardDietPlan(String planId, Boolean discarded) {
		DietPlan response = null;
		try {
			DietPlanCollection dietPlanCollection = dietPlanRepository.findOne(new ObjectId(planId));
			if (dietPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Diet Plan not found with Id");
			}
			dietPlanCollection.setAdminCreatedTime(new Date());
			dietPlanCollection.setDiscarded(discarded);
			dietPlanCollection = dietPlanRepository.save(dietPlanCollection);
			response = new DietPlan();
			BeanUtil.map(dietPlanCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

}
