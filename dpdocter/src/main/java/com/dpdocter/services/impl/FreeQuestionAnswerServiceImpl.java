package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorDetail;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.FreeAnswersDetailCollection;
import com.dpdocter.collections.FreeQuestionAnswerCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.FreeAnswersDetailRepository;
import com.dpdocter.repository.FreeQuestionAnswerRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.FreeAnswerRequest;
import com.dpdocter.response.FreeAnswerResponse;
import com.dpdocter.response.FreeQuestionResponse;
import com.dpdocter.services.FreeQuestionAnswerService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;

@Service
public class FreeQuestionAnswerServiceImpl implements FreeQuestionAnswerService {
	private static Logger logger = LogManager.getLogger(FreeQuestionAnswerServiceImpl.class.getName());
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FreeQuestionAnswerRepository freeQuetionAnswerRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private FreeAnswersDetailRepository answersDetailRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Override
	@Transactional
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
			}

			if (questionAnswerCollection != null) {
				BeanUtil.map(request, questionAnswerCollection);
				questionAnswerCollection.setUpdatedTime(new Date());
				FreeAnswersDetailCollection answerCollection = new FreeAnswersDetailCollection();
				answerCollection.setAnswerDesc(request.getAnswerDesc());
				answerCollection.setNextStep(request.getNextStep());
				answerCollection.setHelpfulTips(request.getHelpfulTips());
				answerCollection.setQuestionId(new ObjectId(request.getQuestionId()));
				answerCollection.setTime(request.getTime());
				DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(new ObjectId(request.getDoctorId()),
								new ObjectId(request.getLocationId()));
				DoctorCollection doctorCollection = doctorRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);

				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getLocationId())).orElse(null);
				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();

				DoctorDetail doctorDetail = new DoctorDetail();
				if (locationCollection != null && locationCollection.getCity() != null)
					doctorDetail.setCity(locationCollection.getCity());
				doctorDetail.setDoctorId(request.getDoctorId());
				doctorDetail.setDoctorName(doctorName);
				if (doctorCollection != null && doctorCollection.getExperience() != null)
					doctorDetail.setExperience(doctorCollection.getExperience());
				doctorDetail.setLocationId(request.getLocationId());
				if (doctorClinicProfileCollection != null)
					doctorDetail.setNoOfRecommenations(doctorClinicProfileCollection.getNoOfRecommenations());
				doctorDetail.setResponseTime("Guaranteed Response");
				if (doctorCollection != null && doctorCollection.getSpecialities() != null
						&& !doctorCollection.getSpecialities().isEmpty()) {
					@SuppressWarnings("unchecked")
					List<String> specialities = (List<String>) CollectionUtils.collect(
							(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
							new BeanToPropertyValueTransformer("superSpeciality"));
					doctorDetail.setSpecialities(specialities);
				}
				answerCollection.setDocDetail(doctorDetail);
				questionAnswerCollection.setAnswersDetails(answerCollection);
				answerCollection = answersDetailRepository.save(answerCollection);
				pushNotificationServices.notifyUser(questionAnswerCollection.getUserId().toString(),
						"Your Question has been answered by " + doctorName, ComponentType.FREE_QUE_ANS.getType(), null,
						null);
//				String body = mailBodyGenerator.generateFreeQuestionAnswerEmailBody(userCollection.getEmailAddress(),locationCollection.getCity(),
//						"confirmAppointmentToPatient.vm", doctorName);
//				mailService.sendEmail(userCollection.getEmailAddress(), "Your Question has been answered", body, null);

				questionAnswerCollection = freeQuetionAnswerRepository.save(questionAnswerCollection);
				response = new FreeAnswerResponse();
				BeanUtil.map(questionAnswerCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error while adding Question" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding Question" + e.getMessage());
		}
		return response;
	}

	@Override
	public Integer countFreeQuestion(Boolean isDiscarded, String doctorId) {
		Integer response = null;
		List<String> specialities = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection != null) {
				System.out.println("doctorCollection" + doctorCollection.getCreatedBy());
				String speciality = null;

				if (doctorCollection.getSpecialities() != null || !doctorCollection.getSpecialities().isEmpty()) {
					specialities = new ArrayList<>();
					for (ObjectId specialityId : doctorCollection.getSpecialities()) {
						SpecialityCollection specialityCollection = specialityRepository.findById(specialityId)
								.orElse(null);
						if (specialityCollection != null) {
							speciality = specialityCollection.getSpeciality();
							System.out.println("doctorCollection " + speciality);
							specialities.add(speciality);
						}
					}
				}
			}
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (isDiscarded != null)
				criteria.and("isDiscarded").is(isDiscarded);
			criteria.andOperator(Criteria.where("answersDetails").is(null));
			criteria.and("problemType").in(specialities);
			response = (int) mongoTemplate.count(new Query(criteria), FreeQuestionAnswerCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Questions " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Questions " + e.getMessage());

		}
		return response;
	}

	@Override
	@Transactional
	public List<FreeQuestionResponse> getUnansweredQuestionList(int size, int page, String searchTerm,
			boolean isDiscarded, String doctorId, long updatedTime) {
		List<String> specialities = null;
		List<FreeQuestionResponse> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection != null) {
				System.out.println("doctorCollection" + doctorCollection.getCreatedBy());
				String speciality = null;

				if (doctorCollection.getSpecialities() != null || !doctorCollection.getSpecialities().isEmpty()) {
					specialities = new ArrayList<>();
					for (ObjectId specialityId : doctorCollection.getSpecialities()) {
						SpecialityCollection specialityCollection = specialityRepository.findById(specialityId)
								.orElse(null);
						if (specialityCollection != null) {
							speciality = specialityCollection.getSpeciality();
							System.out.println("doctorCollection " + speciality);
							specialities.add(speciality);
						}
					}
				}
			}
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			criteria.and("isDiscarded").is(isDiscarded);
			criteria.andOperator(Criteria.where("answersDetails").is(null));
			criteria.and("problemType").in(specialities);
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
	@Transactional
	public List<FreeQuestionResponse> getAnsweredQuestionList(int size, int page, String searchTerm,
			boolean isDiscarded, String doctorId, long updatedTime) {
		List<String> specialities = null;
		List<FreeQuestionResponse> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection != null) {
				String speciality = null;

				if (doctorCollection.getSpecialities() != null || !doctorCollection.getSpecialities().isEmpty()) {
					specialities = new ArrayList<>();
					for (ObjectId specialityId : doctorCollection.getSpecialities()) {
						SpecialityCollection specialityCollection = specialityRepository.findById(specialityId)
								.orElse(null);
						if (specialityCollection != null) {
							speciality = specialityCollection.getSpeciality();
							specialities.add(speciality);
						}
					}
				}
			}
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			criteria.and("isDiscarded").is(isDiscarded);
			criteria.andOperator(Criteria.where("answersDetails").ne(null));
			criteria.and("problemType").in(specialities);

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
	public Boolean addQueView(String questionId) {
		Boolean response = false;
		FreeQuestionAnswerCollection questionAnswerCollection = null;
		if (!DPDoctorUtils.anyStringEmpty(questionId)) {
			questionAnswerCollection = freeQuetionAnswerRepository.findById(new ObjectId(questionId)).orElse(null);
		}
		if (questionAnswerCollection != null) {
			questionAnswerCollection.setViews(questionAnswerCollection.getViews() + 1);
			response = true;
		}
		return response;
	}

}
