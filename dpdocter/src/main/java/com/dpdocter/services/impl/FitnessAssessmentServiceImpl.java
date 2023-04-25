package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.FitnessAssessment;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.StructuredCardiorespiratoryProgram;
import com.dpdocter.beans.TreatmentAndDiagnosis;
import com.dpdocter.collections.FitnessAssessmentCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.FitnessAssessmentRepository;
import com.dpdocter.services.FitnessAssessmentService;

import common.util.web.DPDoctorUtils;

@Service
public class FitnessAssessmentServiceImpl implements FitnessAssessmentService {
	private static Logger logger = Logger.getLogger(RecipeServiceImpl.class.getName());
	@Autowired
	private FitnessAssessmentRepository fitnessAssessmentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public Boolean discardFitnessAssessment(String id, Boolean discarded) {
		Boolean response = true;
		FitnessAssessmentCollection fitnessAssessmentCollection = null;
		try {
			fitnessAssessmentCollection = fitnessAssessmentRepository.findById(new ObjectId(id)).orElse(null);
			if (fitnessAssessmentCollection != null) {

				fitnessAssessmentCollection.setDiscarded(discarded);
				fitnessAssessmentCollection.setUpdatedTime(new Date());
				fitnessAssessmentRepository.save(fitnessAssessmentCollection);

				response = true;
			} else {
				logger.warn("Fitness Assessment not found!");
				throw new BusinessException(ServiceError.NoRecord, "Fitness Assessment not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<?> getFitnessAssessmentList(int size, int page, boolean discarded, String doctorId, String locationId,
			String hospitalId, String patientId, long updatedTime) {
		List<FitnessAssessment> response = null;

		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {

				criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {

				criteria.and("locationId").is(new ObjectId(locationId));

			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {

				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (updatedTime > 0) {
				criteria = criteria.and("updatedTime").is(new Date(updatedTime));
			}

			if (size > 0) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long) page * size), Aggregation.limit(size)),
						FitnessAssessmentCollection.class, FitnessAssessment.class).getMappedResults();
			} else {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						FitnessAssessmentCollection.class, FitnessAssessment.class).getMappedResults();
			}
			for (FitnessAssessment fitnessAssessment : response) {

				if (fitnessAssessment.getPhysicalActivityAndMedicalHistory() != null) {

				}

				if (fitnessAssessment.getTreatmentAndDiagnosis() != null) {

				}

				if (fitnessAssessment.getExerciseAndMovement() != null) {

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Fitness Assessment " + e.getMessage());
		}
		return response;
	}

	@Override
	public FitnessAssessment getFitnessAssessmentById(String id) {
		FitnessAssessment response = null;
		try {
			FitnessAssessmentCollection fitnessAssessmentCollection = fitnessAssessmentRepository
					.findById(new ObjectId(id)).orElse(null);
			if (fitnessAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}

			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(id)),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					FitnessAssessmentCollection.class, FitnessAssessment.class).getUniqueMappedResult();
			response = new FitnessAssessment();
			BeanUtil.map(fitnessAssessmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Fitness Assessment by Id ");
		}
		return response;

	}

	@Override
	@Transactional
	public FitnessAssessment addEditFitnessAssessment(FitnessAssessment request) {
		FitnessAssessment response = null;
		FitnessAssessmentCollection fitnessAssessmentCollection = null;
		PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory = null;
		TreatmentAndDiagnosis treatmentAndDiagnosis = null;
		ExerciseAndMovement exerciseMovement = null;

		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				fitnessAssessmentCollection = fitnessAssessmentRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (fitnessAssessmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Fitness Assessment Not found with Id");
				}
				BeanUtil.map(request, fitnessAssessmentCollection);
				fitnessAssessmentCollection.setUpdatedTime(new Date());
				if (request.getCreatedTime() != null)
					fitnessAssessmentCollection.setCreatedTime(request.getCreatedTime());
				else
					fitnessAssessmentCollection.setCreatedTime(new Date());
			} else {
				fitnessAssessmentCollection = new FitnessAssessmentCollection();
				BeanUtil.map(request, fitnessAssessmentCollection);
				fitnessAssessmentCollection.setUpdatedTime(new Date());
				fitnessAssessmentCollection.setCreatedTime(new Date());
			}

			if (fitnessAssessmentCollection != null) {
				if (fitnessAssessmentCollection.getPhysicalActivityAndMedicalHistory() != null) {
					Map<String, Boolean> physicalMedicalHistoryBoolean = new HashMap<String, Boolean>();

					Map<String, String> physicalMedicalHistory = new HashMap<String, String>();

					Map<String, List<String>> physicalMedicalHistoryList = new HashMap<String, List<String>>();

					physicalActivityAndMedicalHistory = fitnessAssessmentCollection
							.getPhysicalActivityAndMedicalHistory();
					if (physicalActivityAndMedicalHistory == null)
						physicalActivityAndMedicalHistory = new PhysicalActivityAndMedicalHistory();
					if (physicalActivityAndMedicalHistory != null) {
						if (physicalActivityAndMedicalHistory.getPhysicalMedicalHistory() != null)
							physicalActivityAndMedicalHistory.getPhysicalMedicalHistory()
									.forEach((key, value) -> physicalMedicalHistory.put(key, value));

						if (physicalActivityAndMedicalHistory.getPhysicalMedicalHistoryBoolean() != null)
							physicalActivityAndMedicalHistory.getPhysicalMedicalHistoryBoolean()
									.forEach((key, value) -> physicalMedicalHistoryBoolean.put(key, value));

						if (!DPDoctorUtils
								.isNullOrEmptyList(physicalActivityAndMedicalHistory.getPhysicalMedicalHistoryList()))
							physicalActivityAndMedicalHistory.getPhysicalMedicalHistoryList().forEach(
									(key, value) -> physicalMedicalHistoryList.put(key, new ArrayList<String>()));
						if (physicalActivityAndMedicalHistory.getStressAreaOfLifeList() != null)
//							List<StressAreaOfLife> stressAreaOfLifeList = new ArrayList<StressAreaOfLife>();
//							stressAreaOfLifeList.addAll(physicalActivityAndMedicalHistory.getStressAreaOfLifeList());
							physicalActivityAndMedicalHistory.getStressAreaOfLifeList().clear();
						physicalActivityAndMedicalHistory.setStressAreaOfLifeList(
								request.getPhysicalActivityAndMedicalHistory().getStressAreaOfLifeList());

						fitnessAssessmentCollection
								.setPhysicalActivityAndMedicalHistory(physicalActivityAndMedicalHistory);
					}
				}
				if (fitnessAssessmentCollection.getTreatmentAndDiagnosis() != null) {
					treatmentAndDiagnosis = fitnessAssessmentCollection.getTreatmentAndDiagnosis();
					Map<String, Boolean> treatmentAndDiagnosisBoolean = new HashMap<String, Boolean>();

					Map<String, String> treatmentAndDiagnosisString = new HashMap<String, String>();

					Map<String, List<String>> treatmentAndDiagnosisList = new HashMap<String, List<String>>();

					if (treatmentAndDiagnosis == null) {
						treatmentAndDiagnosis = new TreatmentAndDiagnosis();
						if (treatmentAndDiagnosis != null) {
							if (treatmentAndDiagnosis.getTreatmentAndDiagnosisString() != null)
								treatmentAndDiagnosis.getTreatmentAndDiagnosisString()
										.forEach((key, value) -> treatmentAndDiagnosisString.put(key, value));

							if (treatmentAndDiagnosis.getTreatmentAndDiagnosisBoolen() != null)
								treatmentAndDiagnosis.getTreatmentAndDiagnosisBoolen()
										.forEach((key, value) -> treatmentAndDiagnosisBoolean.put(key, value));

							if (!DPDoctorUtils.isNullOrEmptyList(treatmentAndDiagnosis.getTreatmentAndDiagnosisList()))
								treatmentAndDiagnosis.getTreatmentAndDiagnosisList()
										.forEach((key, value) -> treatmentAndDiagnosisList.put(key, new ArrayList<String>()));

							fitnessAssessmentCollection.setTreatmentAndDiagnosis(treatmentAndDiagnosis);
						}
					}
				}
				if (fitnessAssessmentCollection.getExerciseAndMovement() != null) {
					exerciseMovement = fitnessAssessmentCollection.getExerciseAndMovement();
					Map<String, Boolean> exerciseMovementBoolean = new HashMap<String, Boolean>();

					Map<String, String> exerciseMovementString = new HashMap<String, String>();

					Map<String, List<String>> exerciseMovementList = new HashMap<String, List<String>>();

					if (exerciseMovement == null) {
						exerciseMovement = new ExerciseAndMovement();
						exerciseMovement.getExerciseAndMovementString()
								.forEach((key, value) -> exerciseMovementString.put(key, value));

						exerciseMovement.getExerciseAndMovementBoolen()
								.forEach((key, value) -> exerciseMovementBoolean.put(key, value));

						exerciseMovement.getExerciseAndMovementList()
								.forEach((key, value) -> exerciseMovementList.put(key, new ArrayList<String>()));

						if (exerciseMovement.getIsPartInStructuredCardiorespiratoryProgram()) {
							StructuredCardiorespiratoryProgram cardiorespiratoryProgram = new StructuredCardiorespiratoryProgram();
							if (exerciseMovement.getStructuredCardiorespiratoryProgram() != null
									&& exerciseMovement.getStructuredCardiorespiratoryProgram().getDaysPerWeek() > 0)
								cardiorespiratoryProgram.setDaysPerWeek(
										exerciseMovement.getStructuredCardiorespiratoryProgram().getDaysPerWeek());
							if (exerciseMovement.getStructuredCardiorespiratoryProgram() != null
									&& exerciseMovement.getStructuredCardiorespiratoryProgram().getMinutesPerDay() > 0)
								cardiorespiratoryProgram.setMinutesPerDay(
										exerciseMovement.getStructuredCardiorespiratoryProgram().getMinutesPerDay());

							exerciseMovement.setStructuredCardiorespiratoryProgram(cardiorespiratoryProgram);
						}
					}
					fitnessAssessmentCollection.setExerciseAndMovement(exerciseMovement);
				}
			}
			fitnessAssessmentCollection = fitnessAssessmentRepository.save(fitnessAssessmentCollection);
			response = new FitnessAssessment();
			BeanUtil.map(fitnessAssessmentCollection, response);
		} catch (BusinessException e) {
			logger.error("Error while addedit Fitness Assessment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while addedit Fitness Assessment " + e.getMessage());

		}
		return response;
	}

	@Override
	public String getFitnessAssessmentFile(String fitnessId) {
		String response = null;
		return response;
	}

	@Override
	public Integer countFitnessAssessment(Boolean discarded, String patientId) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			criteria.and("discarded").is(discarded);
			response = (int) mongoTemplate.count(new Query(criteria), FitnessAssessmentCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting Fitness Collection " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting Fitness Collection " + e.getMessage());
		}
		return response;
	}

}
