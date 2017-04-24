package com.dpdocter.services.impl;

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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DischargeSummary;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DischargeSummaryRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;

@Service
public class DischargeSummaryServiceImpl implements DischargeSummaryService {

	private static Logger logger = Logger.getLogger(DischargeSummaryServiceImpl.class.getName());

	@Autowired
	private DischargeSummaryRepository dischargeSummaryRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientVisitService PatientVisitService;

	@Transactional
	@Override
	public DischargeSummary addEditDischargeSummary(DischargeSummary dischargeSummary) {

		DischargeSummary response = null;
		try {
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					new ObjectId(dischargeSummary.getPatientId()), new ObjectId(dischargeSummary.getDoctorId()),
					new ObjectId(dischargeSummary.getLocationId()), new ObjectId(dischargeSummary.getHospitalId()));
			if (patientCollection != null) {
				DischargeSummaryCollection dischargeSummaryCollection = null;

				if (dischargeSummary.getId() == null) {
					dischargeSummaryCollection = new DischargeSummaryCollection();

				} else {
					dischargeSummaryCollection = dischargeSummaryRepository
							.findOne(new ObjectId(dischargeSummary.getId()));
				}
				if (dischargeSummaryCollection != null) {

					BeanUtil.map(dischargeSummary, dischargeSummaryCollection);
					dischargeSummaryCollection.setUpdatedTime(new Date());

					dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);

					PatientCard patientCard = new PatientCard();
					BeanUtil.map(patientCollection, patientCard);
					response = new DischargeSummary();
					BeanUtil.map(dischargeSummaryCollection, response);
					response.setPatient(patientCard);
				} else {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid  discharge summary Id  ");

				}
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id  ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding  discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding discharge summary : " + e.getCause().getMessage());

		}
		return response;
	}

	/*
	 * @Transactional
	 * 
	 * @Override public List<DischargeSummary> getAllDischargeSummary() {
	 * List<DischargeSummary> response = null; DischargeSummary dischargeSummary
	 * = null; List<DischargeSummaryCollection> dischargeSummaryCollections =
	 * null;
	 * 
	 * dischargeSummaryCollections = dischargeSummaryRepository.findAll(); for
	 * (DischargeSummaryCollection dischargeSummaryCollection :
	 * dischargeSummaryCollections) { dischargeSummary = new DischargeSummary();
	 * BeanUtil.map(dischargeSummaryCollection, dischargeSummary);
	 * response.add(dischargeSummary); }
	 * 
	 * return response; }
	 */

	@Override
	@Transactional
	public List<DischargeSummary> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime) {
		List<DischargeSummary> response = null;
		try {

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DischargeSummary> aggregationResults = mongoTemplate.aggregate(aggregation,
					DischargeSummaryCollection.class, DischargeSummary.class);
			response = aggregationResults.getMappedResults();
			for (DischargeSummary dischargeSummary : response) {
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						new ObjectId(dischargeSummary.getPatientId()), new ObjectId(dischargeSummary.getDoctorId()),
						new ObjectId(dischargeSummary.getLocationId()), new ObjectId(dischargeSummary.getHospitalId()));
				PatientCard patientCard = new PatientCard();
				BeanUtil.map(patientCollection, patientCard);
				dischargeSummary.setPatient(patientCard);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting discharge summary : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DischargeSummary viewDischargeSummary(String dischargeSummeryId) {
		DischargeSummary response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {

				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getDoctorId(),
						dischargeSummaryCollection.getLocationId(), dischargeSummaryCollection.getHospitalId());
				PatientCard patientCard = new PatientCard();
				BeanUtil.map(patientCollection, patientCard);
				response = new DischargeSummary();
				BeanUtil.map(dischargeSummaryCollection, response);
				response.setPatient(patientCard);

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid discharge summaryId ");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while view discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while view discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public Integer getDischargeSummaryCount(String doctorId, String locationId, String hospitalId, String patientId,
			String updatedTime) {
		Integer response = 0;
		try {

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);
			response = (int) mongoTemplate.count(new Query(criteria), DischargeSummaryCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while count discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while count discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

}
