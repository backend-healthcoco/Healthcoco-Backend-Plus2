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
import org.springframework.stereotype.Service;

import com.dpdocter.beans.NursingCareExam;
import com.dpdocter.collections.InitialAssessmentCollection;
import com.dpdocter.collections.NursesAdmissionCollection;
import com.dpdocter.collections.NursingCareExamCollection;
import com.dpdocter.collections.PreOperationFormCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.InitialAssessmentFormRepository;
import com.dpdocter.repository.NursesAdmissionFormRepository;
import com.dpdocter.repository.NursingCareexamRepository;
import com.dpdocter.repository.PreOperationFormRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.InitialAdmissionRequest;
import com.dpdocter.request.InitialAssessmentRequest;
import com.dpdocter.request.PreOperationAssessmentRequest;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.response.InitialAssessmentResponse;
import com.dpdocter.response.PreOperationAssessmentResponse;
import com.dpdocter.services.InitialAssessmentService;

import common.util.web.DPDoctorUtils;

@Service
public class InitialAssessmentServiceImpl implements InitialAssessmentService{

	private static Logger logger = LogManager.getLogger(InitialAssessmentServiceImpl.class.getName());

	@Autowired
	InitialAssessmentFormRepository  initialAssessmentFormRepository;
	
	@Autowired
	NursesAdmissionFormRepository  nursesAdmissionFormRepository;
	
	@Autowired
	NursingCareexamRepository  nursingCareexamRepository;
	
	@Autowired
	PreOperationFormRepository  preOperationFormRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public InitialAssessmentResponse addEditInitialAssessmentForm(InitialAssessmentRequest request) {
		InitialAssessmentResponse response = null;
		try {
			InitialAssessmentCollection initialAssessmentCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				initialAssessmentCollection = initialAssessmentFormRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (initialAssessmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(initialAssessmentCollection.getCreatedBy());
				BeanUtil.map(request, initialAssessmentCollection);

			} else {
				initialAssessmentCollection = new InitialAssessmentCollection();
				BeanUtil.map(request, initialAssessmentCollection);
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					initialAssessmentCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
				}				
				initialAssessmentCollection.setUpdatedTime(new Date());
			}
			initialAssessmentCollection = initialAssessmentFormRepository.save(initialAssessmentCollection);
			response = new InitialAssessmentResponse();

			BeanUtil.map(initialAssessmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<InitialAssessmentResponse> getInitialAssessmentForm(String doctorId, String locationId,
			String hospitalId, String patientId, int page, int size) {
		List<InitialAssessmentResponse> response = null;
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
			
			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("doctorId").is(doctorObjectId);
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, InitialAssessmentCollection.class, InitialAssessmentResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting assessment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting assessment " + e.getMessage());

		}
		return response;
	}

	@Override
	public InitialAdmissionResponse addEditAdmissionAssessmentForm(InitialAdmissionRequest request) {
		InitialAdmissionResponse response = null;
		try {
			NursesAdmissionCollection nursesAdmissionCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nursesAdmissionCollection = nursesAdmissionFormRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nursesAdmissionCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(nursesAdmissionCollection.getCreatedBy());
				nursesAdmissionCollection.setOldMedication(null);
				nursesAdmissionCollection.setRiskFactor(null);
				BeanUtil.map(request, nursesAdmissionCollection);

			} else {
				nursesAdmissionCollection = new NursesAdmissionCollection();
				BeanUtil.map(request, nursesAdmissionCollection);
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					nursesAdmissionCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
				}				
				nursesAdmissionCollection.setUpdatedTime(new Date());
			}
			nursesAdmissionCollection = nursesAdmissionFormRepository.save(nursesAdmissionCollection);
			response = new InitialAdmissionResponse();
			BeanUtil.map(nursesAdmissionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<InitialAdmissionResponse> getAdmissionAssessmentForms(String doctorId, String locationId,
			String hospitalId, String patientId, int page, int size) {
		List<InitialAdmissionResponse> response = null;
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
			
			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("doctorId").is(doctorObjectId);
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, NursesAdmissionCollection.class, InitialAdmissionResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting assessment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting assessment " + e.getMessage());

		}
		return response;
	}

	@Override
	public PreOperationAssessmentResponse addEditPreOperationAssessmentForm(PreOperationAssessmentRequest request) {
		PreOperationAssessmentResponse response = null;
		try {
			PreOperationFormCollection preOperationFormCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				preOperationFormCollection = preOperationFormRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (preOperationFormCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(preOperationFormCollection.getCreatedBy());
				BeanUtil.map(request, preOperationFormCollection);

			} else {
				preOperationFormCollection = new PreOperationFormCollection();
				BeanUtil.map(request, preOperationFormCollection);
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					preOperationFormCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
				}				
				preOperationFormCollection.setUpdatedTime(new Date());
			}
			preOperationFormCollection = preOperationFormRepository.save(preOperationFormCollection);
			response = new PreOperationAssessmentResponse();
			BeanUtil.map(preOperationFormCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<PreOperationAssessmentResponse> getPreOperationAssessmentForms(String doctorId, String locationId,
			String hospitalId, String patientId, int page, int size) {
		List<PreOperationAssessmentResponse> response = null;
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
			
			Criteria criteria = new Criteria("patientId").is(patientObjectId).and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)
					.and("doctorId").is(doctorObjectId);
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, PreOperationFormCollection.class, PreOperationAssessmentResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting assessment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting assessment " + e.getMessage());

		}
		return response;
	}

	@Override
	public NursingCareExam addEditNursingCareExam(NursingCareExam request) {
		NursingCareExam response = null;
		try {
			NursingCareExamCollection nursingCareExamCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nursingCareExamCollection = nursingCareexamRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nursingCareExamCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Data Not found with Id");
				}
				request.setCreatedTime(nursingCareExamCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
				request.setCreatedBy(nursingCareExamCollection.getCreatedBy());
				BeanUtil.map(request, nursingCareExamCollection);

			} else {
				nursingCareExamCollection = new NursingCareExamCollection();
				BeanUtil.map(request, nursingCareExamCollection);
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					nursingCareExamCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
				}				
				nursingCareExamCollection.setCreatedTime(new Date());
				nursingCareExamCollection.setUpdatedTime(new Date());
			}
			nursingCareExamCollection = nursingCareexamRepository.save(nursingCareExamCollection);
			response = new NursingCareExam();
			BeanUtil.map(nursingCareExamCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteNursingCareExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Boolean response = false;
		try {
			NursingCareExamCollection nursingCareExamCollection = nursingCareexamRepository.findById(new ObjectId(id))
					.orElse(null);
			if (nursingCareExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(nursingCareExamCollection.getDoctorId(),
						nursingCareExamCollection.getHospitalId(), nursingCareExamCollection.getLocationId())) {
					if (nursingCareExamCollection.getDoctorId().toString().equals(doctorId)
							&& nursingCareExamCollection.getHospitalId().toString().equals(hospitalId)
							&& nursingCareExamCollection.getLocationId().toString().equals(locationId)) {

						nursingCareExamCollection.setDiscarded(discarded);
						nursingCareExamCollection.setUpdatedTime(new Date());
						nursingCareexamRepository.save(nursingCareExamCollection);
						response = true;
						BeanUtil.map(nursingCareExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					nursingCareExamCollection.setDiscarded(discarded);
					nursingCareExamCollection.setUpdatedTime(new Date());
					nursingCareexamRepository.save(nursingCareExamCollection);
					response = true;
					BeanUtil.map(nursingCareExamCollection, response);
				}
			} else {
				logger.warn(" not found!");
				throw new BusinessException(ServiceError.NoRecord, " not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	
}
