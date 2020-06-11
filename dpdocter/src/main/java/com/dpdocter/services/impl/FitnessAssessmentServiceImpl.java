package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
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

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.FitnessAssessment;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.StructuredCardiorespiratoryProgram;
import com.dpdocter.beans.TreatmentAndDiagnosis;
import com.dpdocter.collections.FitnessAssessmentCollection;
import com.dpdocter.enums.StressAreaOfLife;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.FitnessAssessmentRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.services.FitnessAssessmentService;
import com.dpdocter.services.JasperReportService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class FitnessAssessmentServiceImpl implements FitnessAssessmentService {
	private static Logger logger = Logger.getLogger(RecipeServiceImpl.class.getName());
	@Autowired
	private FitnessAssessmentRepository fitnessAssessmentRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private JasperReportService jasperReportService;

//	@Value(value = "${jasper.print.fitnessAssessment.a4.fileName}")
//	private String fitnessAssessmentA4FileName;

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
			Criteria criteria = new Criteria();

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
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
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
							Aggregation.lookup("physical_activity_medical_history_cl",
									"physicalActivityAndMedicalHistoryId", "_id", "physicalActivityAndMedicalHistory"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$physicalActivityAndMedicalHistory")
											.append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),

							Aggregation.lookup("treatment_diagnosis_cl", "treatmentAndDiagnosisId", "_id",
									"treatmentAndDiagnosis"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$treatmentAndDiagnosis")
											.append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),

							Aggregation.lookup("exercise_movement_cl", "exerciseAndMovementId", "_id",
									"exerciseAndMovement"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$exerciseAndMovementId")
											.append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
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
						if (!DPDoctorUtils
								.isNullOrEmptyList(physicalActivityAndMedicalHistory.getStressAreaOfLifeList())) {
							List<StressAreaOfLife> stressAreaOfLifeList = new ArrayList<StressAreaOfLife>();
							stressAreaOfLifeList.addAll(physicalActivityAndMedicalHistory.getStressAreaOfLifeList());
							physicalActivityAndMedicalHistory.setStressAreaOfLifeList(stressAreaOfLifeList);
						}
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
										.forEach((key, value) -> treatmentAndDiagnosisList.put(key, new ArrayList()));

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
								.forEach((key, value) -> exerciseMovementList.put(key, new ArrayList()));

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
//
//		try {
//			FitnessAssessmentCollection fitnessAssessmentCollection = fitnessAssessmentRepository
//					.findById(new ObjectId(fitnessId)).orElse(null);
//			if (fitnessAssessmentCollection != null) {
//				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
//						fitnessAssessmentCollection.getPatientId(), fitnessAssessmentCollection.getLocationId(),
//						fitnessAssessmentCollection.getHospitalId());
//
//				UserCollection user = userRepository.findById(fitnessAssessmentCollection.getPatientId()).orElse(null);
//				JasperReportResponse jasperReportResponse = createJasper(fitnessAssessmentCollection, patient, user);
//				if (jasperReportResponse != null)
//					response = getFinalImageURL(jasperReportResponse.getPath());
//				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
//					if (jasperReportResponse.getFileSystemResource().getFile().exists())
//						jasperReportResponse.getFileSystemResource().getFile().delete();
//			} else {
//				logger.warn("Invoice Id does not exist");
//				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error(e);
//			throw new BusinessException(ServiceError.Unknown, "Exception in download Discharge Summary ");
//		}
		return response;
	}

//	private JasperReportResponse createJasper(FitnessAssessmentCollection fitnessAssessmentCollection,
//			PatientCollection patient, UserCollection user) throws NumberFormatException, IOException, ParseException {
//		JasperReportResponse response = null;
//		Map<String, Object> parameters = new HashMap<String, Object>();
//		String pattern = "dd/MM/yyyy";
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
//		Boolean show = false;
//
//		PrintSettingsCollection printSettings = printSettingsRepository
//				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(fitnessAssessmentCollection.getDoctorId(),
//						fitnessAssessmentCollection.getLocationId(), fitnessAssessmentCollection.getHospitalId(),
//						ComponentType.ALL.getType());
//
//		if (printSettings == null) {
//			printSettings = new PrintSettingsCollection();
//			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
//			BeanUtil.map(defaultPrintSettings, printSettings);
//		}
//		String pdfName = (user != null ? user.getFirstName() : "") + "FITNESS-ASSESSMENT-"
//				+ fitnessAssessmentCollection.getId() + new Date().getTime();
//
//		String layout = printSettings != null
//				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
//				: "PORTRAIT";
//		String pageSize = printSettings != null
//				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
//				: "A4";
//		Integer topMargin = printSettings != null
//				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
//				: 20;
//		Integer bottonMargin = printSettings != null
//				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
//				: 20;
//		Integer leftMargin = printSettings != null
//				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
//						? printSettings.getPageSetup().getLeftMargin()
//						: 20)
//				: 20;
//		Integer rightMargin = printSettings != null
//				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
//						? printSettings.getPageSetup().getRightMargin()
//						: 20)
//				: 20;
//		response = jasperReportService.createPDF(ComponentType.FITNESS_ASSESSMENT, parameters,
//				fitnessAssessmentA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
//				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
//
//		return response;
//
//	}
//
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public Integer countFitnessAssessment(Boolean isDiscarded) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();
			criteria.and("isDiscarded").is(isDiscarded);
			response = (int) mongoTemplate.count(new Query(criteria), FitnessAssessmentCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting employees " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while counting Fitness Collection " + e.getMessage());
		}
		return response;
	}

}
