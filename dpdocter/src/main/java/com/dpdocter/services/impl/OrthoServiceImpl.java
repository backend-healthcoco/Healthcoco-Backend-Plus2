//package com.dpdocter.services.impl;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.TimeZone;
//
//import org.apache.log4j.Logger;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.aggregation.Aggregation;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.stereotype.Service;
//
//import com.dpdocter.beans.AlignerDates;
//import com.dpdocter.beans.AlignerProgressDetail;
//import com.dpdocter.collections.OrthoCollection;
//import com.dpdocter.collections.OrthoProgressCollection;
//import com.dpdocter.exceptions.BusinessException;
//import com.dpdocter.exceptions.ServiceError;
//import com.dpdocter.reflections.BeanUtil;
//import com.dpdocter.repository.OrthoProgressRepository;
//import com.dpdocter.repository.OrthoRepository;
//import com.dpdocter.request.OrthoEditProgressDatesRequest;
//import com.dpdocter.request.OrthoEditRequest;
//import com.dpdocter.response.OrthoProgressResponse;
//import com.dpdocter.response.OrthoResponse;
//import com.dpdocter.webservices.OrthoService;
//
//import common.util.web.DPDoctorUtils;
//
//@Service
//public class OrthoServiceImpl implements OrthoService {
//
//	private static Logger logger = Logger.getLogger(OrthoServiceImpl.class.getName());
//
//	@Autowired
//	private OrthoRepository orthoRepository;
//
//	@Autowired
//	private OrthoProgressRepository orthoProgressRepository;
//
//	@Autowired
//	private MongoTemplate mongoTemplate;
//
//	@Override
//	public OrthoResponse editOrthoPlanningDetails(OrthoEditRequest request) {
//		OrthoResponse response = null;
//		try {
//			OrthoCollection orthoCollection = new OrthoCollection();
//
//			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
//				BeanUtil.map(request, orthoCollection);
//
//				orthoCollection.setCreatedTime(new Date());
//				orthoCollection.setAdminCreatedTime(new Date());
//
//			} else {
//				orthoCollection = orthoRepository.findById(new ObjectId(request.getId())).orElse(null);
//				orthoCollection.setUpdatedTime(new Date());
//
//			}
//			orthoCollection = orthoRepository.save(orthoCollection);
//
//			addEditProgressData(orthoCollection);
//			response = new OrthoResponse();
//			BeanUtil.map(orthoCollection, response);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//			throw new BusinessException(ServiceError.Unknown, e.getMessage());
//		}
//
//		return response;
//	}
//
//	private void addEditProgressData(OrthoCollection orthoCollection) {
//		OrthoProgressCollection orthoProgressCollection = new OrthoProgressCollection();
//		orthoProgressCollection.setPlanId(orthoCollection.getId());
//		orthoProgressRepository.save(orthoProgressCollection);
//		AlignerProgressDetail upperAligner = new AlignerProgressDetail();
//		upperAligner.setStartDate(orthoCollection.getStartDate());
//		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		c.setTime(orthoCollection.getStartDate());
//		c.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
//		upperAligner.setEndDate(c.getTime());
//		c.add(Calendar.DATE, 1);
//		upperAligner.setNextStartDate(c.getTime());
//		upperAligner.setProgressId(orthoProgressCollection.getId().toString());
//		upperAligner
//				.setTreatmentDays(orthoCollection.getNoOfDaysToWearAligner() * orthoCollection.getNoOfUpperAligner());
//		upperAligner.setWearingAligner(1);
//		upperAligner.setPlanId(orthoCollection.getId().toString());
//
//		LinkedHashMap<Integer, AlignerDates> alignerDates = new LinkedHashMap<>();
//		Date startDate = orthoCollection.getStartDate();
//		Date endDate = upperAligner.getEndDate();
//		AlignerDates alignerDate1 = new AlignerDates();
//		alignerDate1.setStartDate(startDate);
//		alignerDate1.setEndDate(endDate);
//		alignerDates.put(1, alignerDate1);
//		for (int i = 2; i < orthoCollection.getNoOfUpperAligner(); i++) {
//			AlignerDates alignerDate = new AlignerDates();
//			Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//			c1.setTime(endDate);
//			c1.add(Calendar.DATE, 1);
//			alignerDate.setStartDate(c1.getTime());
//			Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//			c2.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
//			alignerDate.setEndDate(c2.getTime());
//			endDate = c2.getTime();
//			alignerDates.put(i, alignerDate);
//		}
//		upperAligner.setAlignerDates(alignerDates);
//		orthoProgressCollection.setUpperAligner(upperAligner);
//		AlignerProgressDetail lowerAligner = new AlignerProgressDetail();
//
//		lowerAligner.setStartDate(orthoCollection.getStartDate());
//		Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		c1.setTime(orthoCollection.getStartDate());
//		c1.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
//		lowerAligner.setEndDate(c1.getTime());
//		c1.add(Calendar.DATE, 1);
//		lowerAligner.setNextStartDate(c1.getTime());
//		lowerAligner.setProgressId(orthoProgressCollection.getId().toString());
//		lowerAligner
//				.setTreatmentDays(orthoCollection.getNoOfDaysToWearAligner() * orthoCollection.getNoOfUpperAligner());
//		lowerAligner.setWearingAligner(1);
//		lowerAligner.setPlanId(orthoCollection.getId().toString());
//
//		LinkedHashMap<Integer, AlignerDates> alignerDatesLower = new LinkedHashMap<>();
////		Date startDate = orthoCollection.getStartDate();
////		Date endDate = upperAligner.getEndDate();
////		AlignerDates alignerDate1 = new AlignerDates();
////		alignerDate1.setStartDate(startDate);
////		alignerDate1.setEndDate(endDate);
//		alignerDatesLower.put(1, alignerDate1);
//		for (int i = 2; i < orthoCollection.getNoOfUpperAligner(); i++) {
//			AlignerDates alignerDate = new AlignerDates();
//			Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//			c3.setTime(endDate);
//			c3.add(Calendar.DATE, 1);
//			alignerDate.setStartDate(c3.getTime());
//			Calendar c4 = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//			c4.add(Calendar.DATE, orthoCollection.getNoOfDaysToWearAligner());
//			alignerDate.setEndDate(c4.getTime());
//			endDate = c4.getTime();
//			alignerDatesLower.put(i, alignerDate);
//		}
//		lowerAligner.setAlignerDates(alignerDatesLower);
//		orthoProgressCollection.setLowerAligner(lowerAligner);
//
//		orthoProgressRepository.save(orthoProgressCollection);
//
//	}
//
//	@Override
//	public Boolean deleteOrthoPlanningDetails(String id, Boolean discarded) {
//		Boolean response = false;
//		try {
//			OrthoCollection orthoCollection = orthoRepository.findById(new ObjectId(id)).orElse(null);
//			orthoCollection.setDiscarded(discarded);
//			orthoCollection.setUpdatedTime(new Date());
//			orthoCollection = orthoRepository.save(orthoCollection);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Error while deleting ortho plan" + e);
//			throw new BusinessException(ServiceError.Unknown, "Error while ortho plan" + e);
//		}
//		return response;
//	}
//
//	@Override
//	public List<OrthoResponse> getOrthoPlanningDetails(long page, int size, String doctorId, String locationId,
//			String hospitalId, String patientId, String updatedTime, Boolean discarded, boolean b) {
//		List<OrthoResponse> responses = null;
//		try {
//
//			Criteria criteria = new Criteria();
//			if (!DPDoctorUtils.anyStringEmpty(patientId))
//				criteria.and("patientId").is(new ObjectId(patientId));
//			if (!DPDoctorUtils.anyStringEmpty(doctorId))
//				criteria.and("doctorId").is(new ObjectId(doctorId));
//			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
//				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
//			if (discarded != null)
//				criteria.and("discarded").is(discarded);
//
//			if (size > 0) {
//
//				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
//						Aggregation.limit(size));
//				responses = mongoTemplate.aggregate(aggregation, OrthoCollection.class, OrthoResponse.class)
//						.getMappedResults();
//			} else {
//
//				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
//				responses = mongoTemplate.aggregate(aggregation, OrthoCollection.class, OrthoResponse.class)
//						.getMappedResults();
//			}
//
//		} catch (BusinessException be) {
//			logger.error(be);
//			throw be;
//		} catch (Exception e) {
//			logger.error("Error while getting ortho" + e);
//			throw new BusinessException(ServiceError.Unknown, "Error while getting ortho" + e);
//		}
//		return responses;
//
//	}
//
//	@Override
//	public OrthoProgressResponse getOrthoProgressById(String id) {
//		OrthoProgressResponse response = null;
//		try {
//			OrthoProgressCollection orthoProgressCollection = orthoProgressRepository.findById(new ObjectId(id))
//					.orElse(null);
//			response = new OrthoProgressResponse();
//			BeanUtil.map(orthoProgressCollection, response);
//		} catch (BusinessException be) {
//			logger.error(be);
//			throw be;
//		} catch (Exception e) {
//			logger.error("Error while getting ortho progress" + e);
//			throw new BusinessException(ServiceError.Unknown, "Error while getting ortho progress" + e);
//		}
//		return response;
//	}
//
//	@Override
//	public OrthoProgressResponse editOrthoProgressDetailsDates(OrthoEditProgressDatesRequest request) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
