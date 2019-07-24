package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.EyeObservation;
import com.dpdocter.collections.EyeObservationCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.EyeObservationRepository;
import com.dpdocter.services.OphthalmologyService;

import common.util.web.DPDoctorUtils;

@Service
public class OphthalmologyServiceImpl implements OphthalmologyService {
	
	@Autowired
	EyeObservationRepository eyeObservationRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	@Transactional
	public EyeObservation addEditEyeObservation(EyeObservation eyeObservation) {
		EyeObservation response = null;
		EyeObservationCollection eyeObservationCollection = null;
		if(eyeObservation.getId() != null)
		{
			eyeObservationCollection = eyeObservationRepository.findById(new ObjectId(eyeObservation.getId())).orElse(null);
			BeanUtil.map(eyeObservation, eyeObservationCollection);
			eyeObservationCollection.setVisualAcuities(eyeObservation.getVisualAcuities());
			eyeObservationCollection.setEyeTests(eyeObservation.getEyeTests());
			eyeObservationCollection = eyeObservationRepository.save(eyeObservationCollection);
			response = new EyeObservation();
			BeanUtil.map(eyeObservationCollection, response);
		}
		else
		{
			eyeObservationCollection = new EyeObservationCollection();
			BeanUtil.map(eyeObservation, eyeObservationCollection);
			eyeObservationCollection = eyeObservationRepository.save(eyeObservationCollection);
			response = new EyeObservation();
			BeanUtil.map(eyeObservationCollection, response);
		}

		return response;
	}

	@Override
	@Transactional
	public EyeObservation deleteEyeObservation(String id , Boolean discarded) {
		
		EyeObservationCollection eyeObservationCollection = null;
		EyeObservation eyeObservation = null;
		if(id == null || id.isEmpty())
		{
			throw new BusinessException(ServiceError.NoRecord,"Record not found");
		}
		
		eyeObservationCollection = eyeObservationRepository.findById(new ObjectId(id)).orElse(null);
		eyeObservationCollection.setDiscarded(discarded);
		eyeObservationCollection.setUpdatedTime(new Date());
		eyeObservationCollection = eyeObservationRepository.save(eyeObservationCollection);
		eyeObservation = new EyeObservation();
		BeanUtil.map(eyeObservationCollection, eyeObservation);
		
		return eyeObservation;
	}

	@Override
	@Transactional
	public EyeObservation getEyeObservation(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory) {
		
		EyeObservation eyeObservation = null;
		EyeObservationCollection eyeObservationCollection = null;
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
		if (!DPDoctorUtils.anyStringEmpty(patientId))
			patientObjectId = new ObjectId(patientId);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			doctorObjectId = new ObjectId(doctorId);
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			locationObjectId = new ObjectId(locationId);
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			hospitalObjectId = new ObjectId(hospitalId);
		
		long createdTimestamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId").is(patientObjectId);
		if(!discarded)criteria.and("discarded").is(discarded);
		if(inHistory)criteria.and("inHistory").is(inHistory);
		
		if(!isOTPVerified){
			if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(doctorObjectId);	
		}
		
		Aggregation aggregation = null;
		
		if (size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size), Aggregation.limit(size));
		else aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		
		AggregationResults<EyeObservation> aggregationResults = mongoTemplate.aggregate(aggregation, EyeObservationCollection.class, EyeObservation.class);
		eyeObservationCollection = new EyeObservationCollection();
		eyeObservation = new EyeObservation();
		eyeObservation = aggregationResults.getUniqueMappedResult();
		
		//BeanUtil.map(eyeObservationCollection, eyeObservation);
		return eyeObservation;
	}

	@Override
	public List<EyeObservation> getEyeObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded , Boolean isOTPVerified
			) {
		
		List<EyeObservation> eyeObservations = null;
		//List<EyeObservationCollection> eyeObservationCollections = null;
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
		if (!DPDoctorUtils.anyStringEmpty(patientId))
			patientObjectId = new ObjectId(patientId);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			doctorObjectId = new ObjectId(doctorId);
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			locationObjectId = new ObjectId(locationId);
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			hospitalObjectId = new ObjectId(hospitalId);

		long createdTimestamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId").is(patientObjectId);
		if(!discarded)criteria.and("discarded").is(discarded);
		//if(inHistory)criteria.and("inHistory").is(inHistory);
		
		if(!isOTPVerified){
			if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(doctorObjectId);	
		}
		
		Aggregation aggregation = null;
		
		if (size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size), Aggregation.limit(size));
		else aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		
		AggregationResults<EyeObservation> aggregationResults = mongoTemplate.aggregate(aggregation, EyeObservationCollection.class, EyeObservation.class);
		eyeObservations = aggregationResults.getMappedResults();
		return eyeObservations;
	}

}
