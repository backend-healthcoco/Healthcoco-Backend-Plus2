package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AssessmentPersonalDetail;
import com.dpdocter.beans.PatientAssesentmentFormHistory;
import com.dpdocter.beans.PatientFoodAndExcercise;
import com.dpdocter.beans.PatientLifeStyle;
import com.dpdocter.beans.PatientMeasurementInfo;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.collections.AssessmentPersonalDetailCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientFoodAndExcerciseCollection;
import com.dpdocter.collections.PatientLifeStyleCollection;
import com.dpdocter.collections.PatientMeasurementCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AssessmentPersonalDetailRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.PatientFoodAndExerciseRepository;
import com.dpdocter.repository.PatientLifeStyleRepository;
import com.dpdocter.repository.PatientMeasurementRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.services.AssessmentFormService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class AssessmentFormServiceImpl implements AssessmentFormService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AssessmentPersonalDetailRepository assessmentPersonalDetailRepository;

	@Autowired
	private PatientFoodAndExerciseRepository patientFoodAndExerciseRepository;

	@Autowired
	private PatientLifeStyleRepository patientLifeStyleRepository;

	@Autowired
	private PatientMeasurementRepository patientMeasurementRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private PatientRepository patientRepository;

	@Transactional
	@Override
	public AssessmentPersonalDetail addEditAssessmentPersonalDetail(AssessmentPersonalDetail request) {
		AssessmentPersonalDetail response = null;
		try {
			AssessmentPersonalDetailCollection assessmentPersonalDetailCollection = null;
			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutritionist found with doctorId");
				}
				if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
					assessmentPersonalDetailCollection = new AssessmentPersonalDetailCollection();
					request.setPatientId(registerPatientIfNotRegistered(request).toString());
				}
				BeanUtil.map(request, assessmentPersonalDetailCollection);
				assessmentPersonalDetailCollection.setCreatedTime(new Date());
				assessmentPersonalDetailCollection.setUpdatedTime(new Date());
				assessmentPersonalDetailCollection.setCreatedBy(doctor.getFirstName());
				assessmentPersonalDetailCollection.setUniqueId("NAF-" + DPDoctorUtils.generateRandomId());
			} else {
				assessmentPersonalDetailCollection = assessmentPersonalDetailRepository
						.findOne(new ObjectId(request.getId()));
				BeanUtil.map(request, assessmentPersonalDetailCollection);
				assessmentPersonalDetailCollection.setUpdatedTime(new Date());
			}
			assessmentPersonalDetailCollection = assessmentPersonalDetailRepository
					.save(assessmentPersonalDetailCollection);
			response = new AssessmentPersonalDetail();

			if (!DPDoctorUtils.anyStringEmpty(response.getPatientId())) {
				PatientCollection patientCollection = patientRepository.findLastRegisteredPatientWithPNUM(
						new ObjectId(response.getLocationId()), new ObjectId(response.getHospitalId()),
						new Sort(Direction.DESC, "createdTime"));
				BeanUtil.map(patientCollection, response);
				BeanUtil.map(assessmentPersonalDetailCollection, response);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Transactional
	@Override
	public PatientFoodAndExcercise addEditFoodAndExcercise(PatientFoodAndExcercise request) {
		PatientFoodAndExcercise response = null;
		try {
			PatientFoodAndExcerciseCollection patientFoodAndExcerciseCollection = null;

			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutritionist found with doctorId");
				}
				patientFoodAndExcerciseCollection = new PatientFoodAndExcerciseCollection();
				BeanUtil.map(request, patientFoodAndExcerciseCollection);
				patientFoodAndExcerciseCollection.setCreatedTime(new Date());
				patientFoodAndExcerciseCollection.setUpdatedTime(new Date());
				patientFoodAndExcerciseCollection.setCreatedBy(doctor.getFirstName());

			} else {
				patientFoodAndExcerciseCollection = patientFoodAndExerciseRepository
						.findOne(new ObjectId(request.getId()));
				BeanUtil.map(request, patientFoodAndExcerciseCollection);
				patientFoodAndExcerciseCollection.setUpdatedTime(new Date());
			}
			patientFoodAndExerciseRepository.save(patientFoodAndExcerciseCollection);
			response = new PatientFoodAndExcercise();
			BeanUtil.map(patientFoodAndExcerciseCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Transactional
	@Override
	public PatientAssesentmentFormHistory addEditAssessmentHistory(PatientAssesentmentFormHistory request) {
		PatientAssesentmentFormHistory response = null;
		try {
			HistoryCollection historyCollection = null;

			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutritionist found with doctorId");
				}
				historyCollection = new HistoryCollection();

				BeanUtil.map(request, historyCollection);
				historyCollection.setCreatedTime(new Date());
				historyCollection.setUpdatedTime(new Date());
				historyCollection.setCreatedBy(doctor.getFirstName());

			} else {
				historyCollection = historyRepository.findOne(new ObjectId(request.getId()));
				BeanUtil.map(request, historyCollection);
				historyCollection.setUpdatedTime(new Date());
			}
			historyCollection = historyRepository.save(historyCollection);
			response = new PatientAssesentmentFormHistory();
			BeanUtil.map(historyCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Transactional
	@Override
	public PatientLifeStyle addEditAssessmentLifeStyle(PatientLifeStyle request) {
		PatientLifeStyle response = null;
		try {
			PatientLifeStyleCollection patientLifeStyleCollection = null;

			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutritionist not found with doctorId");
				}
				patientLifeStyleCollection = new PatientLifeStyleCollection();
				BeanUtil.map(request, patientLifeStyleCollection);
				patientLifeStyleCollection.setCreatedTime(new Date());
				patientLifeStyleCollection.setUpdatedTime(new Date());
				patientLifeStyleCollection.setCreatedBy(doctor.getFirstName());

			} else {
				patientLifeStyleCollection = patientLifeStyleRepository.findOne(new ObjectId(request.getId()));
				BeanUtil.map(request, patientLifeStyleCollection);
				patientLifeStyleCollection.setUpdatedTime(new Date());
			}
			patientLifeStyleCollection = patientLifeStyleRepository.save(patientLifeStyleCollection);
			response = new PatientLifeStyle();
			BeanUtil.map(patientLifeStyleCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Transactional
	@Override
	public PatientMeasurementInfo addEditPatientMeasurementInfo(PatientMeasurementInfo request) {
		PatientMeasurementInfo response = null;
		try {
			PatientMeasurementCollection patientMeasurementCollection = null;

			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutritionist not found with doctorId");
				}
				patientMeasurementCollection = new PatientMeasurementCollection();
				BeanUtil.map(request, patientMeasurementCollection);
				patientMeasurementCollection.setCreatedTime(new Date());
				patientMeasurementCollection.setUpdatedTime(new Date());
				patientMeasurementCollection.setCreatedBy(doctor.getFirstName());

			} else {
				patientMeasurementCollection = patientMeasurementRepository.findOne(new ObjectId(request.getId()));
				BeanUtil.map(request, patientMeasurementCollection);
				patientMeasurementCollection.setUpdatedTime(new Date());
			}
			patientMeasurementCollection = patientMeasurementRepository.save(patientMeasurementCollection);
			response = new PatientMeasurementInfo();
			BeanUtil.map(patientMeasurementCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private ObjectId registerPatientIfNotRegistered(AssessmentPersonalDetail request) {
		ObjectId patientId = null;

		if (DPDoctorUtils.anyStringEmpty(request.getFirstName())) {
			throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
		}
		PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
		patientRegistrationRequest.setFirstName(request.getFirstName());
		patientRegistrationRequest.setLocalPatientName(request.getFirstName());
		patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
		patientRegistrationRequest.setDoctorId(request.getDoctorId());
		patientRegistrationRequest.setLocationId(request.getLocationId());
		patientRegistrationRequest.setHospitalId(request.getHospitalId());
		RegisteredPatientDetails patientDetails = null;
		patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
		if (patientDetails != null) {
			request.setPatientId(patientDetails.getUserId());
		}
		transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
		esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
		patientId = new ObjectId(request.getPatientId());

		return patientId;
	}

	@Transactional
	@Override
	public List<AssessmentPersonalDetail> getAssessmentPatientDetail(int page, int size, boolean discarded,
			long updateTime, String patientId, String doctorId, String locationId, String hospitalId) {
		List<AssessmentPersonalDetail> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria secondCriteria = new Criteria();
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$_id"),
					Fields.field("firstName", "$patient.firstName"),
					Fields.field("mobileNumber", "$$patient.mobileNumber"), Fields.field("doctorId", "$doctorId"),
					Fields.field("patientId", "$patientId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("bloodGroup", "$patient.bloodGroup"),
					Fields.field("gender", "$patient.gender"), Fields.field("uniqueId", "$uniqueId"),
					Fields.field("physicalStatusType", "$physicalStatusType"), Fields.field("goal", "$goal"),
					Fields.field("discarded", "$discarded"), Fields.field("dob", "$patient.dob"),
					Fields.field("address", "$patient.address"), Fields.field("community", "$community")));

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));

			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				secondCriteria.and("patient.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				secondCriteria.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				secondCriteria.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}
			if (updateTime > 0)
				criteria.and("createdTime").lte(new Date(updateTime));
			Aggregation aggregation = null;
			if (size > 0)

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(secondCriteria), projectList,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(secondCriteria), projectList,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<AssessmentPersonalDetail> results = mongoTemplate.aggregate(aggregation,
					AssessmentPersonalDetailCollection.class, AssessmentPersonalDetail.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PatientLifeStyle getAssessmentLifeStyle(String assessmentId) {
		PatientLifeStyle response = null;
		try {
			PatientLifeStyleCollection lifeStyleCollection = patientLifeStyleRepository
					.findByassessmentId(new ObjectId(assessmentId));
			response = new PatientLifeStyle();
			BeanUtil.map(lifeStyleCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PatientAssesentmentFormHistory getAssessmentHistory(String assessmentId) {
		PatientAssesentmentFormHistory response = null;
		try {

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public PatientMeasurementInfo getPatientMeasurementInfo(String assessmentId) {
		PatientMeasurementInfo response = null;
		try {
			PatientMeasurementCollection measurementCollection = patientMeasurementRepository
					.findByassessmentId(new ObjectId(assessmentId));
			response = new PatientMeasurementInfo();
			BeanUtil.map(measurementCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public PatientFoodAndExcercise getPatientFoodAndExcercise(String assessmentId) {
		PatientFoodAndExcercise response = null;
		try {
			PatientFoodAndExcerciseCollection foodAndExcerciseCollection = patientFoodAndExerciseRepository
					.findByassessmentId(new ObjectId(assessmentId));
			response = new PatientFoodAndExcercise();
			BeanUtil.map(foodAndExcerciseCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
