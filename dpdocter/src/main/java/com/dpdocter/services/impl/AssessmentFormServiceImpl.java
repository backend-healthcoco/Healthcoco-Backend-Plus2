package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AssessmentPersonalDetail;
import com.dpdocter.beans.Count;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.PatientAssesentmentHistoryRequest;
import com.dpdocter.beans.PatientFoodAndExcercise;
import com.dpdocter.beans.PatientLifeStyle;
import com.dpdocter.beans.PatientMeasurementInfo;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.collections.AssessmentPersonalDetailCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DrugCollection;
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
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.PatientFoodAndExerciseRepository;
import com.dpdocter.repository.PatientLifeStyleRepository;
import com.dpdocter.repository.PatientMeasurementRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.AssessmentFormHistoryResponse;
import com.dpdocter.response.DiseaseListResponse;
import com.dpdocter.services.AssessmentFormService;
import com.dpdocter.services.PrescriptionServices;
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

	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private PrescriptionServices prescriptionservice;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Transactional
	@Override
	public AssessmentPersonalDetail addEditAssessmentPersonalDetail(AssessmentPersonalDetail request) {
		AssessmentPersonalDetail response = null;
		try {
			AssessmentPersonalDetailCollection assessmentPersonalDetailCollection = null;
			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutritionist found with doctorId");
				}
				if (request.getDob() != null && request.getDob().getAge() != null
						&& request.getDob().getAge().getYears() < 0) {

					throw new BusinessException(ServiceError.InvalidInput, DOB);
				} else if (request.getDob() == null && request.getAge() != null) {
					Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
					int currentDay = localCalendar.get(Calendar.DATE);
					int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
					int currentYear = localCalendar.get(Calendar.YEAR) - request.getAge();
					request.setDob(new DOB(currentDay, currentMonth, currentYear));
				}
				if (DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

					request.setPatientId(registerPatientIfNotRegistered(request).toString());
				}
				assessmentPersonalDetailCollection = new AssessmentPersonalDetailCollection();

				BeanUtil.map(request, assessmentPersonalDetailCollection);
				assessmentPersonalDetailCollection.setCreatedTime(new Date());
				assessmentPersonalDetailCollection.setUpdatedTime(new Date());
				assessmentPersonalDetailCollection.setCreatedBy(doctor.getFirstName());
				assessmentPersonalDetailCollection.setAssessmentUniqueId("NAF-" + DPDoctorUtils.generateRandomId());
			} else {
				assessmentPersonalDetailCollection = assessmentPersonalDetailRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				BeanUtil.map(request, assessmentPersonalDetailCollection);
				assessmentPersonalDetailCollection.setUpdatedTime(new Date());
			}
			assessmentPersonalDetailCollection = assessmentPersonalDetailRepository
					.save(assessmentPersonalDetailCollection);
			response = new AssessmentPersonalDetail();

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						new ObjectId(request.getPatientId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()));
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
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
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
						.findById(new ObjectId(request.getId())).orElse(null);
				patientFoodAndExcerciseCollection.setMealTimeAndPattern(null);
				patientFoodAndExcerciseCollection.setFoodCravings(null);
				patientFoodAndExcerciseCollection.setExercise(null);
				patientFoodAndExcerciseCollection.setFoodPrefer(null);
				patientFoodAndExcerciseCollection.setDrugs(null);
				
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
	public AssessmentFormHistoryResponse addEditAssessmentHistory(PatientAssesentmentHistoryRequest request) {
		AssessmentFormHistoryResponse response = null;
		try {
			HistoryCollection historyCollection = null;

			List<DiseaseListResponse> diseaseListResponses = null;

			List<DiseasesCollection> diseasesCollections = null;
			DiseaseListResponse diseaseListResponse = null;

			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutritionist found with doctorId");
				}
				historyCollection = new HistoryCollection();

				BeanUtil.map(request, historyCollection);
				historyCollection.setCreatedTime(new Date());
				historyCollection.setUpdatedTime(new Date());
				historyCollection
						.setCreatedBy((DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? "Dr." : doctor.getTitle())
								+ " " + doctor.getFirstName());

			} else {
				historyCollection = historyRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setUpdatedTime(new Date());
				request.setCreatedBy(historyCollection.getCreatedBy());
				request.setCreatedTime(historyCollection.getCreatedTime());
				
				historyCollection.setAddiction(null);
				historyCollection.setDiesease(null);
				historyCollection.setExistingMedication(null);
				historyCollection.setFamilyhistory(null);
				historyCollection.setGeneralRecords(null);
				historyCollection.setMedicalhistory(null);
				historyCollection.setReasons(null);
				historyCollection.setSpecialNotes(null);
				historyCollection.setDrugsAndAllergies(null);
				
				BeanUtil.map(request, historyCollection);
				historyCollection.setUpdatedTime(new Date());
			}
			if (historyCollection.getExistingMedication() != null) {
				List<PrescriptionItem> items = null;
				DrugCollection drugCollection = null;
				for (PrescriptionItem item : historyCollection.getExistingMedication()) {
					PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
					List<DrugDirection> directions = null;
					if (item.getDirection() != null && !item.getDirection().isEmpty()) {
						for (DrugDirection drugDirection : item.getDirection()) {
							if (drugDirection != null && !DPDoctorUtils.anyStringEmpty(drugDirection.getId())) {
								if (directions == null)
									directions = new ArrayList<DrugDirection>();
								directions.add(drugDirection);
							}
						}
						item.setDirection(directions);
					}
					if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
						if (item.getDuration().getDurationUnit().getId() == null)
							item.setDuration(null);
					} else {
						item.setDuration(null);
					}
					if (items == null) {
						items = new ArrayList<PrescriptionItem>();

					}

					BeanUtil.map(item, prescriptionItemDetail);
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugId())) {
						drugCollection = drugRepository.findById(item.getDrugId()).orElse(null);
					} else {
						drugCollection = new DrugCollection();
					}
					Drug drug = new Drug();
					DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
					if (drugCollection != null) {
						BeanUtil.map(drugCollection, drugAddEditRequest);
					}
					drugAddEditRequest.setDoctorId(request.getDoctorId());
					drugAddEditRequest.setHospitalId(request.getHospitalId());
					drugAddEditRequest.setLocationId(request.getLocationId());
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugName())) {
						drugAddEditRequest.setDrugName(item.getDrugName());
					}
					if (item.getDrugType() != null) {
						drugAddEditRequest.setDrugType(item.getDrugType());
					}
					if (!DPDoctorUtils.anyStringEmpty(item.getInstructions())) {
						drugAddEditRequest.setExplanation(item.getInstructions());
						drugCollection.setExplanation(item.getInstructions());
					}
					drugAddEditRequest.setDirection(item.getDirection());
					drugAddEditRequest.setDuration(item.getDuration());
					drugAddEditRequest.setDosage(item.getDosage());
					drugAddEditRequest.setDosageTime(item.getDosageTime());
					drug = prescriptionservice.addFavouriteDrug(drugAddEditRequest, drugCollection,
							historyCollection.getCreatedBy());
					item.setDrugId(new ObjectId(drug.getId()));

					prescriptionItemDetail.setDrug(drug);
					items.add(item);
					historyCollection.setExistingMedication(items);
				}
			}
			historyCollection = historyRepository.save(historyCollection);
			response = new AssessmentFormHistoryResponse();
			BeanUtil.map(historyCollection, response);

			if (historyCollection.getFamilyhistory() != null && !historyCollection.getFamilyhistory().isEmpty()) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				diseasesCollections = diseasesRepository.findAllById(historyCollection.getFamilyhistory());
				if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
					for (DiseasesCollection diseasesCollection : diseasesCollections) {
						diseaseListResponse = new DiseaseListResponse();
						BeanUtil.map(diseasesCollection, diseaseListResponse);
						diseaseListResponses.add(diseaseListResponse);
					}
				}
				response.setFamilyhistory(diseaseListResponses);
			}else response.setFamilyhistory(null);
			
			if (historyCollection.getMedicalhistory() != null && !historyCollection.getMedicalhistory().isEmpty()) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				diseasesCollections = diseasesRepository.findAllById(historyCollection.getMedicalhistory());
				if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
					for (DiseasesCollection diseasesCollection : diseasesCollections) {
						diseaseListResponse = new DiseaseListResponse();
						BeanUtil.map(diseasesCollection, diseaseListResponse);
						diseaseListResponses.add(diseaseListResponse);
					}
				}
				response.setMedicalhistory(diseaseListResponses);
			}else response.setMedicalhistory(null);
			

			if (historyCollection.getDiesease() != null && !historyCollection.getDiesease().isEmpty()) {
				diseaseListResponses = new ArrayList<DiseaseListResponse>();
				diseasesCollections = diseasesRepository.findAllById(historyCollection.getDiesease());
				if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
					for (DiseasesCollection diseasesCollection : diseasesCollections) {
						diseaseListResponse = new DiseaseListResponse();
						BeanUtil.map(diseasesCollection, diseaseListResponse);
						diseaseListResponses.add(diseaseListResponse);
					}
				}
				response.setDiesease(diseaseListResponses);
			}else response.setDiesease(null);
			

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
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutritionist not found with doctorId");
				}
				patientLifeStyleCollection = new PatientLifeStyleCollection();
				BeanUtil.map(request, patientLifeStyleCollection);
				patientLifeStyleCollection.setCreatedTime(new Date());
				patientLifeStyleCollection.setUpdatedTime(new Date());
				patientLifeStyleCollection.setCreatedBy(doctor.getFirstName());

			} else {
				patientLifeStyleCollection = patientLifeStyleRepository.findById(new ObjectId(request.getId())).orElse(null);
				patientLifeStyleCollection.setOffDays(null);
				patientLifeStyleCollection.setPregnancyCategory(null);
				patientLifeStyleCollection.setSleepPatterns(null);
				patientLifeStyleCollection.setTrivalingPeriod(null);
				patientLifeStyleCollection.setWorkingschedules(null);
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
				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NotFound, "Nutritionist not found with doctorId");
				}
				patientMeasurementCollection = new PatientMeasurementCollection();
				BeanUtil.map(request, patientMeasurementCollection);
				patientMeasurementCollection.setCreatedTime(new Date());
				patientMeasurementCollection.setUpdatedTime(new Date());
				patientMeasurementCollection.setCreatedBy(doctor.getFirstName());

			} else {
				patientMeasurementCollection = patientMeasurementRepository.findById(new ObjectId(request.getId())).orElse(null);
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
		BeanUtil.map(request, patientRegistrationRequest);
		patientRegistrationRequest.setFirstName(request.getFirstName());
		patientRegistrationRequest.setLocalPatientName(request.getFirstName());

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
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("firstName", "$patient.firstName"), Fields.field("mobileNumber", "$mobileNumber"),
					Fields.field("doctorId", "$doctorId"), Fields.field("patientId", "$patientId"),
					Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
					Fields.field("bloodGroup", "$patient.bloodGroup"), Fields.field("gender", "$patient.gender"),
					Fields.field("assessmentUniqueId", "$assessmentUniqueId"),
					Fields.field("physicalStatusType", "$physicalStatusType"), Fields.field("goal", "$goal"),
					Fields.field("discarded", "$discarded"), Fields.field("dob", "$patient.dob"),
					Fields.field("address", "$patient.address"), Fields.field("profession", "$patient.profession"),
					Fields.field("community", "$community"), Fields.field("noOfAdultMember", "$noOfAdultMember"),
					Fields.field("noOfChildMember", "$noOfChildMember"), Fields.field("createdTime", "$createdTime"),
					Fields.field("createdBy", "$createdBy"), Fields.field("dietType", "$dietType")));

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
				secondCriteria.and("patient.userId").is(new ObjectId(patientId));

			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				secondCriteria.and("patient.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				secondCriteria.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				secondCriteria.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}
			if (updateTime > 0) {
				criteria.and("createdTime").lte(new Date(updateTime));
			}
            if(!discarded) {
            	criteria.and("discarded").is(discarded);
            }
			Aggregation aggregation = null;
			if (size > 0)

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(secondCriteria), projectList,
						Aggregation.sort(Sort.Direction.DESC, "createdTime"),

						Aggregation.skip((long)(page) * size), Aggregation.limit(size));
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
	public Integer getAssessmentPatientDetailCount(int page, int size, boolean discarded, long updateTime,
			String patientId, String doctorId, String locationId, String hospitalId) {
		Integer count = 0;
		try {
			Criteria criteria = new Criteria();
			Criteria secondCriteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
				secondCriteria.and("patient.userId").is(new ObjectId(patientId));

			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				secondCriteria.and("patient.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				secondCriteria.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				secondCriteria.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}
			if (updateTime > 0) {
				criteria.and("createdTime").lte(new Date(updateTime));
			}
            if(!discarded) {
            	criteria.and("discarded").is(discarded);
            }
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(secondCriteria), 
						Aggregation.group("id").count().as("value"));

			List<Count> results = mongoTemplate.aggregate(aggregation,
					AssessmentPersonalDetailCollection.class, Count.class).getMappedResults();
			count = (results!=null) ? results.size() : 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return count;
	}
	
	@Override
	public PatientLifeStyle getAssessmentLifeStyle(String assessmentId) {
		PatientLifeStyle response = null;
		try {
			PatientLifeStyleCollection lifeStyleCollection = patientLifeStyleRepository
					.findByAssessmentId(new ObjectId(assessmentId));
			if (lifeStyleCollection != null) {
				response = new PatientLifeStyle();
				BeanUtil.map(lifeStyleCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public AssessmentFormHistoryResponse getAssessmentHistory(String assessmentId) {
		AssessmentFormHistoryResponse response = null;
		try {
			List<DiseaseListResponse> diseaseListResponses = null;

			List<DiseasesCollection> diseasesCollections = null;
			DiseaseListResponse diseaseListResponse = null;
			HistoryCollection historyCollection = historyRepository.findByAssessmentId(new ObjectId(assessmentId));
			if (historyCollection != null) {
				response = new AssessmentFormHistoryResponse();
				BeanUtil.map(historyCollection, response);

				if (historyCollection.getFamilyhistory() != null && !historyCollection.getFamilyhistory().isEmpty()) {
					diseaseListResponses = new ArrayList<DiseaseListResponse>();
					diseasesCollections = diseasesRepository.findAllById(historyCollection.getFamilyhistory());
					if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
						for (DiseasesCollection diseasesCollection : diseasesCollections) {
							diseaseListResponse = new DiseaseListResponse();
							BeanUtil.map(diseasesCollection, diseaseListResponse);
							diseaseListResponses.add(diseaseListResponse);
						}
					}
					response.setFamilyhistory(diseaseListResponses);	
				}else response.setFamilyhistory(null);
				
				if (historyCollection.getMedicalhistory() != null && !historyCollection.getMedicalhistory().isEmpty()) {
					diseaseListResponses = new ArrayList<DiseaseListResponse>();
					diseasesCollections = diseasesRepository.findAllById(historyCollection.getMedicalhistory());
					if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
						for (DiseasesCollection diseasesCollection : diseasesCollections) {
							diseaseListResponse = new DiseaseListResponse();
							BeanUtil.map(diseasesCollection, diseaseListResponse);
							diseaseListResponses.add(diseaseListResponse);
						}
					}
					response.setMedicalhistory(diseaseListResponses);
				}else response.setMedicalhistory(null);
				

				if (historyCollection.getDiesease() != null && !historyCollection.getDiesease().isEmpty()) {
					diseaseListResponses = new ArrayList<DiseaseListResponse>();
					diseasesCollections = diseasesRepository.findAllById(historyCollection.getDiesease());
					if (diseasesCollections != null && !diseasesCollections.isEmpty()) {
						for (DiseasesCollection diseasesCollection : diseasesCollections) {
							diseaseListResponse = new DiseaseListResponse();
							BeanUtil.map(diseasesCollection, diseaseListResponse);
							diseaseListResponses.add(diseaseListResponse);
						}
					}
					response.setDiesease(diseaseListResponses);
				}
			}
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
					.findByAssessmentId(new ObjectId(assessmentId));
			if (measurementCollection != null) {
				response = new PatientMeasurementInfo();
				BeanUtil.map(measurementCollection, response);
			}
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
					.findByAssessmentId(new ObjectId(assessmentId));
			if (foodAndExcerciseCollection != null) {
				response = new PatientFoodAndExcercise();
				BeanUtil.map(foodAndExcerciseCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
