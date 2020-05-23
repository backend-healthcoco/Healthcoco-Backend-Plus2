package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.ExerciseAndMovement;
import com.dpdocter.beans.FitnessAssessment;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PhysicalActivityAndMedicalHistory;
import com.dpdocter.beans.StructuredCardiorespiratoryProgram;
import com.dpdocter.beans.TreatmentAndDiagnosis;
import com.dpdocter.collections.ExerciseMovementCollection;
import com.dpdocter.collections.FitnessAssessmentCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PhysicalActivityAndMedicalHistoryCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.TreatmentAndDiagnosisCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ExerciseMovementRepository;
import com.dpdocter.repository.FitnessAssessmentRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PhysicalActivityAndMedicalHistoryRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.TreatmentAndDiagnosisRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.JasperReportResponse;
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
	private PhysicalActivityAndMedicalHistoryRepository physicalActivityAndMedicalHistoryRepository;
	@Autowired
	private TreatmentAndDiagnosisRepository treatmentAndDiagnosisRepository;
	@Autowired
	private ExerciseMovementRepository exerciseMovementRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Value(value = "${jasper.print.fitnessAssessment.a4.fileName}")
	private String fitnessAssessmentA4FileName;

	@Autowired
	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public FitnessAssessment discardFitnessAssessment(String fitnessId, Boolean discarded) {
		FitnessAssessment response = null;
		FitnessAssessmentCollection fitnessAssessmentCollection = null;
		try {
			fitnessAssessmentCollection = fitnessAssessmentRepository.findById(new ObjectId(fitnessId)).orElse(null);
			if (fitnessAssessmentCollection != null) {

				fitnessAssessmentCollection.setDiscarded(discarded);
				fitnessAssessmentCollection.setUpdatedTime(new Date());
				fitnessAssessmentRepository.save(fitnessAssessmentCollection);
				response = new FitnessAssessment();
				BeanUtil.map(fitnessAssessmentCollection, response);
				PatientCollection patientCollection = patientRepository
						.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(fitnessAssessmentCollection.getPatientId(),
								fitnessAssessmentCollection.getDoctorId(), fitnessAssessmentCollection.getLocationId(),
								fitnessAssessmentCollection.getHospitalId());
				Patient patient = new Patient();
				BeanUtil.map(patientCollection, patient);

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
	public List<?> getFitnessAssessmentList(int size, int page, boolean discarded, String searchTerm, String doctorId,
			String locationId, String hospitalId, String patientId, long updatedTime) {
		List<FitnessAssessment> fitnessAssessments = null;

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
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("localPatientName").regex("^" + searchTerm, "i"),
						new Criteria("localPatientName").regex("^" + searchTerm));
			if (size > 0) {
				fitnessAssessments = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("physical_activity_medical_history_cl",
										"physicalActivityAndMedicalHistoryId", "_id",
										"physicalActivityAndMedicalHistory"),
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
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
								Aggregation.skip((long) page * size), Aggregation.limit(size)),
						FitnessAssessmentCollection.class, FitnessAssessment.class).getMappedResults();
			} else {
				fitnessAssessments = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("physical_activity_medical_history_cl",
										"physicalActivityAndMedicalHistoryId", "_id",
										"physicalActivityAndMedicalHistory"),
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
						FitnessAssessmentCollection.class, FitnessAssessment.class).getMappedResults();
			}
			for (FitnessAssessment fitnessAssessment : fitnessAssessments) {

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
					"Error while getting nutrition refference " + e.getMessage());
		}
		return fitnessAssessments;
	}

	@Override
	public FitnessAssessment getFitnessAssessmentById(String fitnessId) {
		FitnessAssessment response = null;
		try {
			FitnessAssessmentCollection fitnessAssessmentCollection = fitnessAssessmentRepository
					.findById(new ObjectId(fitnessId)).orElse(null);
//
//		fitnessAssessments = mongoTemplate.aggregate(
//				Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("physical_activity_medical_history_cl",
//								"physicalActivityAndMedicalHistoryId", "_id", "physicalActivityAndMedicalHistory"),
//						new CustomAggregationOperation(new Document("$unwind",
//								new BasicDBObject("path", "$physicalActivityAndMedicalHistory")
//										.append("preserveNullAndEmptyArrays", true)
//										.append("includeArrayIndex", "arrayIndex1"))),
//
//						Aggregation.lookup("treatment_diagnosis_cl", "treatmentAndDiagnosisId", "_id",
//								"treatmentAndDiagnosis"),
//						new CustomAggregationOperation(new Document("$unwind",
//								new BasicDBObject("path", "$treatmentAndDiagnosis")
//										.append("preserveNullAndEmptyArrays", true)
//										.append("includeArrayIndex", "arrayIndex1"))),
//
//						Aggregation.lookup("exercise_movement_cl", "exerciseAndMovementId", "_id",
//								"exerciseAndMovement"),
//						new CustomAggregationOperation(new Document("$unwind",
//								new BasicDBObject("path", "$exerciseAndMovementId")
//										.append("preserveNullAndEmptyArrays", true)
//										.append("includeArrayIndex", "arrayIndex1"))),
//						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
//				FitnessAssessmentCollection.class, FitnessAssessment.class).getMappedResults();
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
		PhysicalActivityAndMedicalHistoryCollection physicalActivityAndMedicalHistoryCollection = null;
		TreatmentAndDiagnosisCollection treatmentAndDiagnosisCollection = null;
		ExerciseMovementCollection exerciseMovementCollection = null;
		PhysicalActivityAndMedicalHistory physicalActivityAndMedicalHistory = null;
		TreatmentAndDiagnosis treatmentAndDiagnosis = null;
		ExerciseAndMovement exerciseMovement = null;

		try {

			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.NotFound, "doctor Not found with Id");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				fitnessAssessmentCollection = fitnessAssessmentRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (fitnessAssessmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Fitness Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
						+ " " + doctor.getFirstName());
				request.setCreatedTime(fitnessAssessmentCollection.getCreatedTime());
				BeanUtil.map(request, fitnessAssessmentCollection);

			} else {
				fitnessAssessmentCollection = new FitnessAssessmentCollection();
				BeanUtil.map(request, fitnessAssessmentCollection);
				fitnessAssessmentCollection
						.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "Dr.")
								+ " " + doctor.getFirstName());
				fitnessAssessmentCollection.setUpdatedTime(new Date());
				fitnessAssessmentCollection.setCreatedTime(new Date());
			}

			if (!DPDoctorUtils.anyStringEmpty(fitnessAssessmentCollection.getPhysicalActivityAndMedicalHistoryId())) {
				physicalActivityAndMedicalHistoryCollection = physicalActivityAndMedicalHistoryRepository
						.findById(fitnessAssessmentCollection.getPhysicalActivityAndMedicalHistoryId()).orElse(null);
				if (physicalActivityAndMedicalHistoryCollection != null) {
					physicalActivityAndMedicalHistory = new PhysicalActivityAndMedicalHistory();
					BeanUtil.map(physicalActivityAndMedicalHistoryCollection, physicalActivityAndMedicalHistory);

					response.setPhysicalActivityAndMedicalHistory(physicalActivityAndMedicalHistory);
				}

			}
			if (!DPDoctorUtils.anyStringEmpty(fitnessAssessmentCollection.getTreatmentAndDiagnosisId())) {
				treatmentAndDiagnosisCollection = treatmentAndDiagnosisRepository
						.findById(fitnessAssessmentCollection.getTreatmentAndDiagnosisId()).orElse(null);
				if (treatmentAndDiagnosisCollection != null) {
					treatmentAndDiagnosis = new TreatmentAndDiagnosis();
					BeanUtil.map(treatmentAndDiagnosisCollection, treatmentAndDiagnosis);

					response.setTreatmentAndDiagnosis(treatmentAndDiagnosis);
				}

			}
			if (!DPDoctorUtils.anyStringEmpty(fitnessAssessmentCollection.getExerciseAndMovementId())) {
				exerciseMovementCollection = exerciseMovementRepository
						.findById(fitnessAssessmentCollection.getExerciseAndMovementId()).orElse(null);
				if (exerciseMovementCollection != null) {
					exerciseMovement = new ExerciseAndMovement();
					BeanUtil.map(exerciseMovementCollection, exerciseMovement);

					if (exerciseMovementCollection.getIsPartInStructuredCardiorespiratoryProgram()) {
						StructuredCardiorespiratoryProgram cardiorespiratoryProgram = new StructuredCardiorespiratoryProgram();
						if (exerciseMovementCollection.getStructuredCardiorespiratoryProgram() != null
								&& exerciseMovementCollection.getStructuredCardiorespiratoryProgram()
										.getDaysPerWeek() > 0)
							cardiorespiratoryProgram.setDaysPerWeek(exerciseMovementCollection
									.getStructuredCardiorespiratoryProgram().getDaysPerWeek());
						if (exerciseMovementCollection.getStructuredCardiorespiratoryProgram() != null
								&& exerciseMovementCollection.getStructuredCardiorespiratoryProgram()
										.getMinutesPerDay() > 0)
							cardiorespiratoryProgram.setMinutesPerDay(exerciseMovementCollection
									.getStructuredCardiorespiratoryProgram().getMinutesPerDay());

						exerciseMovement.setStructuredCardiorespiratoryProgram(cardiorespiratoryProgram);
					}
				}
				response.setExerciseAndMovement(exerciseMovement);
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

		try {
			FitnessAssessmentCollection fitnessAssessmentCollection = fitnessAssessmentRepository
					.findById(new ObjectId(fitnessId)).orElse(null);
			if (fitnessAssessmentCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						fitnessAssessmentCollection.getPatientId(), fitnessAssessmentCollection.getLocationId(),
						fitnessAssessmentCollection.getHospitalId());

				UserCollection user = userRepository.findById(fitnessAssessmentCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = createJasper(fitnessAssessmentCollection, patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in download Discharge Summary ");
		}
		return response;
	}

	private JasperReportResponse createJasper(FitnessAssessmentCollection fitnessAssessmentCollection,
			PatientCollection patient, UserCollection user) throws NumberFormatException, IOException, ParseException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Boolean show = false;

		PrintSettingsCollection printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentType(fitnessAssessmentCollection.getDoctorId(),
						fitnessAssessmentCollection.getLocationId(), fitnessAssessmentCollection.getHospitalId(),
						ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		String pdfName = (user != null ? user.getFirstName() : "") + "FITNESS-ASSESSMENT-"
				+ fitnessAssessmentCollection.getId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.FITNESS_ASSESSMENT, parameters,
				fitnessAssessmentA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

}
