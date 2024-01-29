package com.dpdocter.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AlignerDates;
import com.dpdocter.beans.AlignerProgressDetail;
import com.dpdocter.collections.OrthoCollection;
import com.dpdocter.collections.OrthoProgressCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OrthoProgressRepository;
import com.dpdocter.repository.OrthoRepository;
import com.dpdocter.request.OrthoEditProgressDatesRequest;
import com.dpdocter.request.OrthoEditRequest;
import com.dpdocter.response.OrthoProgressResponse;
import com.dpdocter.response.OrthoResponse;
import com.dpdocter.services.OrthoService;

import common.util.web.DPDoctorUtils;

@Service
public class OrthoServiceImpl implements OrthoService {

	private static Logger logger = Logger.getLogger(OrthoServiceImpl.class.getName());

	@Autowired
	private OrthoRepository orthoRepository;

	@Autowired
	private OrthoProgressRepository orthoProgressRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public OrthoResponse editOrthoPlanningDetails(OrthoEditRequest request) {
		OrthoResponse response = null;
		try {
			OrthoCollection orthoCollection = null;

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				orthoCollection = new OrthoCollection();
				BeanUtil.map(request, orthoCollection);
				orthoCollection.setCreatedTime(new Date());
				orthoCollection.setAdminCreatedTime(new Date());

			} else {
				orthoCollection = orthoRepository.findById(new ObjectId(request.getId())).orElse(null);
				orthoCollection.setUpdatedTime(new Date());
				BeanUtil.map(request, orthoCollection);
			}
			orthoCollection = orthoRepository.save(orthoCollection);

			addEditProgressData(orthoCollection);
			response = new OrthoResponse();
			BeanUtil.map(orthoCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	private void addEditProgressData(OrthoCollection orthoCollection) {

		OrthoProgressCollection orthoProgressCollection = null;
		if (!DPDoctorUtils.anyStringEmpty(orthoCollection.getId())) {
			orthoProgressCollection = orthoProgressRepository.findByPlanId(orthoCollection.getId());
		}
		if (orthoProgressCollection == null) {
			orthoProgressCollection = new OrthoProgressCollection();
		}

		orthoProgressCollection.setPlanId(orthoCollection.getId());
		orthoProgressRepository.save(orthoProgressCollection);

		// Upper
		AlignerProgressDetail upperAligner = new AlignerProgressDetail();
		upperAligner.setStartDate(orthoCollection.getStartDate());
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		c.setTime(orthoCollection.getStartDate());
		c.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
		upperAligner.setEndDate(c.getTime());
		c.add(Calendar.DATE, 1);
		upperAligner.setNextStartDate(c.getTime());
		upperAligner.setProgressId(orthoProgressCollection.getId().toString());
		upperAligner
				.setTreatmentDays(orthoCollection.getNoOfDaysToWearAligner() * orthoCollection.getNoOfUpperAligner());
		upperAligner.setWearingAligner(1);
		upperAligner.setPlanId(orthoCollection.getId().toString());

		LinkedHashMap<Integer, AlignerDates> alignerDates = new LinkedHashMap<>();
		Date startDate = orthoCollection.getStartDate();
		Date endDate = upperAligner.getEndDate();
		AlignerDates alignerDate1 = new AlignerDates();
		alignerDate1.setStartDate(startDate);
		alignerDate1.setEndDate(endDate);
		alignerDates.put(1, alignerDate1);
		for (int i = 2; i <= orthoCollection.getNoOfUpperAligner(); i++) {
			AlignerDates alignerDate = new AlignerDates();
			Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c1.setTime(endDate);
			c1.add(Calendar.DATE, 1);
			alignerDate.setStartDate(c1.getTime());
			Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c2.setTime(c1.getTime());
			c2.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
			alignerDate.setEndDate(c2.getTime());
			endDate = c2.getTime();
			alignerDates.put(i, alignerDate);
		}
		upperAligner.setAlignerDates(alignerDates);
		orthoProgressCollection.setUpperAligner(upperAligner);

		// Lower
		AlignerProgressDetail lowerAligner = new AlignerProgressDetail();

		lowerAligner.setStartDate(orthoCollection.getStartDate());
		Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		c1.setTime(orthoCollection.getStartDate());
		c1.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
		lowerAligner.setEndDate(c1.getTime());
		c1.add(Calendar.DATE, 1);
		lowerAligner.setNextStartDate(c1.getTime());
		lowerAligner.setProgressId(orthoProgressCollection.getId().toString());
		lowerAligner
				.setTreatmentDays(orthoCollection.getNoOfDaysToWearAligner() * orthoCollection.getNoOfUpperAligner());
		lowerAligner.setWearingAligner(1);
		lowerAligner.setPlanId(orthoCollection.getId().toString());
		Date endDateLower = lowerAligner.getEndDate();

		LinkedHashMap<Integer, AlignerDates> alignerDatesLower = new LinkedHashMap<>();
//		Date startDate = orthoCollection.getStartDate();
//		Date endDate = upperAligner.getEndDate();
//		AlignerDates alignerDate1 = new AlignerDates();
//		alignerDate1.setStartDate(startDate);
//		alignerDate1.setEndDate(endDate);
		alignerDatesLower.put(1, alignerDate1);
		for (int i = 2; i < orthoCollection.getNoOfUpperAligner(); i++) {
			AlignerDates alignerDate = new AlignerDates();
			Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c3.setTime(endDateLower);
			c3.add(Calendar.DATE, 1);
			alignerDate.setStartDate(c3.getTime());
			Calendar c4 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c4.setTime(c3.getTime());
			c4.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
			alignerDate.setEndDate(c4.getTime());
			endDateLower = c4.getTime();
			alignerDatesLower.put(i, alignerDate);
		}
		lowerAligner.setAlignerDates(alignerDatesLower);
		orthoProgressCollection.setLowerAligner(lowerAligner);

		orthoProgressRepository.save(orthoProgressCollection);

	}

	@Override
	public Boolean deleteOrthoPlanningDetails(String id, Boolean discarded) {
		Boolean response = false;
		try {
			OrthoCollection orthoCollection = orthoRepository.findById(new ObjectId(id)).orElse(null);
			if (orthoCollection != null) {
				orthoCollection.setDiscarded(discarded);
				orthoCollection.setUpdatedTime(new Date());
				orthoCollection = orthoRepository.save(orthoCollection);
				response = true;
			} else {
				logger.warn("No data found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No data found for the given id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while deleting ortho plan" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while ortho plan" + e);
		}
		return response;
	}

	@Override
	public List<OrthoResponse> getOrthoPlanningDetails(long page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded, boolean b) {
		List<OrthoResponse> responses = null;
		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (discarded != null)
				criteria.and("discarded").is(discarded);

			if (size > 0) {

				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
				responses = mongoTemplate.aggregate(aggregation, OrthoCollection.class, OrthoResponse.class)
						.getMappedResults();
			} else {

				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
				responses = mongoTemplate.aggregate(aggregation, OrthoCollection.class, OrthoResponse.class)
						.getMappedResults();
			}

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting ortho" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting ortho" + e);
		}
		return responses;

	}

	@Override
	public OrthoProgressResponse getOrthoProgressById(String planId) {
		OrthoProgressResponse response = null;
		try {
			OrthoProgressCollection orthoProgressCollection = orthoProgressRepository
					.findByPlanId(new ObjectId(planId));

			response = new OrthoProgressResponse();
			BeanUtil.map(orthoProgressCollection, response);
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting ortho progress" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting ortho progress" + e);
		}
		return response;
	}

	@Override
	public Boolean editOrthoProgressDetailsDates(OrthoEditProgressDatesRequest request) {
		Boolean response = null;
		try {
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (request.getEndDate().after(calendar.getTime())) {
				OrthoProgressCollection orthoProgressCollection = orthoProgressRepository
						.findById(new ObjectId(request.getProgressId())).orElse(null);
				switch (request.getTypeOfAligner()) {
				case Upper:
					changeUpperAligerDate(request, orthoProgressCollection);
					break;
				case Lower:
					changeLowerAligerDate(request, orthoProgressCollection);
					break;
				default:
					break;
				}
				response = true;
			}
			response = false;
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting ortho progress dates" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting ortho progress dates" + e);
		}
		return response;

	}

	private void changeLowerAligerDate(OrthoEditProgressDatesRequest request,
			OrthoProgressCollection orthoProgressCollection) {

		OrthoCollection orthoCollection = orthoRepository.findById(orthoProgressCollection.getPlanId()).orElse(null);
		Date endDate = request.getEndDate();
		AlignerProgressDetail lowerAligner = orthoProgressCollection.getLowerAligner();
		LinkedHashMap<Integer, AlignerDates> alignerDatesLower = lowerAligner.getAlignerDates();
		AlignerDates alignerDates = alignerDatesLower.get(request.getAlignerNo());
		alignerDates.setEndDate(request.getEndDate());
		alignerDatesLower.put(request.getAlignerNo(), alignerDates);

		for (int i = request.getAlignerNo() + 1; i < orthoCollection.getNoOfUpperAligner(); i++) {
			AlignerDates alignerDate = new AlignerDates();
			Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c3.setTime(endDate);
			c3.add(Calendar.DATE, 1);
			alignerDate.setStartDate(c3.getTime());
			Calendar c4 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c4.setTime(c3.getTime());
			c4.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
			alignerDate.setEndDate(c4.getTime());
			endDate = c4.getTime();
			alignerDatesLower.put(i, alignerDate);
		}
		lowerAligner.setAlignerDates(alignerDatesLower);
		orthoProgressCollection.setLowerAligner(lowerAligner);

		orthoProgressRepository.save(orthoProgressCollection);

	}

	private void changeUpperAligerDate(OrthoEditProgressDatesRequest request,
			OrthoProgressCollection orthoProgressCollection) {

		OrthoCollection orthoCollection = orthoRepository.findById(orthoProgressCollection.getPlanId()).orElse(null);
		Date endDate = request.getEndDate();
		AlignerProgressDetail upperAligner = orthoProgressCollection.getUpperAligner();
		LinkedHashMap<Integer, AlignerDates> alignerDatesLower = upperAligner.getAlignerDates();
		AlignerDates alignerDates = alignerDatesLower.get(request.getAlignerNo());
		alignerDates.setEndDate(request.getEndDate());
		alignerDatesLower.put(request.getAlignerNo(), alignerDates);

		for (int i = request.getAlignerNo() + 1; i < orthoCollection.getNoOfUpperAligner(); i++) {
			AlignerDates alignerDate = new AlignerDates();
			Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c3.setTime(endDate);
			c3.add(Calendar.DATE, 1);
			alignerDate.setStartDate(c3.getTime());
			Calendar c4 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			c4.setTime(c3.getTime());
			c4.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
			alignerDate.setEndDate(c4.getTime());
			endDate = c4.getTime();
			alignerDatesLower.put(i, alignerDate);
		}

		upperAligner.setAlignerDates(alignerDatesLower);
		orthoProgressCollection.setUpperAligner(upperAligner);
		orthoProgressRepository.save(orthoProgressCollection);

	}

	@Scheduled(cron = "0 0 5 * * ?", zone = "IST")
	private void updateAligerDataScehduler() {
		try {
			List<OrthoProgressCollection> responses = null;
			Criteria criteria = new Criteria();

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			responses = mongoTemplate
					.aggregate(aggregation, OrthoProgressCollection.class, OrthoProgressCollection.class)
					.getMappedResults();

			for (OrthoProgressCollection orthoProgressCollection : responses) {
				AlignerProgressDetail upperAligner = orthoProgressCollection.getUpperAligner();
				LinkedHashMap<Integer, AlignerDates> alignerDatesLower = upperAligner.getAlignerDates();
				for (OrthoProgressCollection orthoProgressCollection2 : responses) {
					
				}
				
				AlignerProgressDetail lowerAligner = orthoProgressCollection.getLowerAligner();
				LinkedHashMap<Integer, AlignerDates> alignerDatesUpper = lowerAligner.getAlignerDates();

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While updateSmsStatus");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While updateSmsStatus");
		}
	}

}
