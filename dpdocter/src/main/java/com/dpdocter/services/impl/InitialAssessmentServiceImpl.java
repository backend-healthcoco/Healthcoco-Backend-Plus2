package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Medication;
import com.dpdocter.beans.NursingCareExam;
import com.dpdocter.beans.RiskScore;
import com.dpdocter.collections.InitialAssessmentCollection;
import com.dpdocter.collections.NursesAdmissionCollection;
import com.dpdocter.collections.NursingCareExamCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PreOperationFormCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.InitialAssessmentFormRepository;
import com.dpdocter.repository.NursesAdmissionFormRepository;
import com.dpdocter.repository.NursingCareexamRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PreOperationFormRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.InitialAdmissionRequest;
import com.dpdocter.request.InitialAssessmentRequest;
import com.dpdocter.request.PreOperationAssessmentRequest;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.response.InitialAssessmentResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.PreOperationAssessmentResponse;
import com.dpdocter.services.InitialAssessmentService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class InitialAssessmentServiceImpl implements InitialAssessmentService {

	private static Logger logger = LogManager.getLogger(InitialAssessmentServiceImpl.class.getName());

	@Autowired
	InitialAssessmentFormRepository initialAssessmentFormRepository;

	@Autowired
	NursesAdmissionFormRepository nursesAdmissionFormRepository;

	@Autowired
	NursingCareexamRepository nursingCareexamRepository;

	@Autowired
	PreOperationFormRepository preOperationFormRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Value(value = "${jasper.print.clinicalnotes.a4.fileName}")
	private String doctorinitialassessmentA4FileName;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private JasperReportService jasperReportService;

	@Override
	public InitialAssessmentResponse addEditInitialAssessmentForm(InitialAssessmentRequest request) {
		InitialAssessmentResponse response = null;
		try {
			InitialAssessmentCollection initialAssessmentCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				initialAssessmentCollection = initialAssessmentFormRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (initialAssessmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(initialAssessmentCollection.getCreatedBy());
				BeanUtil.map(request, initialAssessmentCollection);

			} else {
				initialAssessmentCollection = new InitialAssessmentCollection();
				BeanUtil.map(request, initialAssessmentCollection);
				initialAssessmentCollection.setUniqueEmrId(
						UniqueIdInitial.DOCTOR_INITIAL_FORM.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					initialAssessmentCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
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
			String hospitalId, String patientId, int page, int size, Boolean discarded) {
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

			Criteria criteria = new Criteria("patientId").is(patientObjectId);
			criteria = criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, InitialAssessmentCollection.class, InitialAssessmentResponse.class)
					.getMappedResults();
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
				nursesAdmissionCollection = nursesAdmissionFormRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
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
				nursesAdmissionCollection.setUniqueEmrId(
						UniqueIdInitial.NURSE_ADMISSION_FORM.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					nursesAdmissionCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
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
			String hospitalId, String patientId, int page, int size, Boolean discarded) {
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

			Criteria criteria = new Criteria("patientId").is(patientObjectId);
			criteria = criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);


			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, NursesAdmissionCollection.class, InitialAdmissionResponse.class)
					.getMappedResults();

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
				preOperationFormCollection = preOperationFormRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (preOperationFormCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(preOperationFormCollection.getCreatedBy());
				BeanUtil.map(request, preOperationFormCollection);

			} else {
				preOperationFormCollection = new PreOperationFormCollection();
				BeanUtil.map(request, preOperationFormCollection);
				preOperationFormCollection.setUniqueEmrId(
						UniqueIdInitial.PRE_OPERATIONAL_FORM.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					preOperationFormCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
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
			String hospitalId, String patientId, int page, int size, Boolean discarded) {
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

			Criteria criteria = new Criteria("patientId").is(patientObjectId);
			criteria = criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);


			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, PreOperationFormCollection.class, PreOperationAssessmentResponse.class)
					.getMappedResults();

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
				nursingCareExamCollection = nursingCareexamRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
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
					nursingCareExamCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
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

	@Override
	public InitialAdmissionResponse getAdmissionFormById(String id) {
		InitialAdmissionResponse response = null;
		try {
			NursesAdmissionCollection nursesAdmissionCollection = nursesAdmissionFormRepository
					.findById(new ObjectId(id)).orElse(null);
			if (nursesAdmissionCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new InitialAdmissionResponse();
			BeanUtil.map(nursesAdmissionCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public PreOperationAssessmentResponse getPreOprationFormById(String id) {
		PreOperationAssessmentResponse response = null;
		try {
			PreOperationFormCollection preOperationFormCollection = preOperationFormRepository
					.findById(new ObjectId(id)).orElse(null);
			if (preOperationFormCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new PreOperationAssessmentResponse();
			BeanUtil.map(preOperationFormCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public InitialAssessmentResponse getInitialAssessmentFormById(String id) {
		InitialAssessmentResponse response = null;
		try {
			InitialAssessmentCollection initialAssessmentCollection = initialAssessmentFormRepository
					.findById(new ObjectId(id)).orElse(null);
			if (initialAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new InitialAssessmentResponse();
			BeanUtil.map(initialAssessmentCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public Boolean deleteAdmissionAssessment(String nurseAdmissionFormId, String doctorId, String hospitalId,
			String locationId, Boolean discarded) {
		Boolean response = false;
		try {
			NursesAdmissionCollection nursesAdmissionCollection = nursesAdmissionFormRepository
					.findById(new ObjectId(nurseAdmissionFormId)).orElse(null);
			if (nursesAdmissionCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(nursesAdmissionCollection.getDoctorId(),
						nursesAdmissionCollection.getHospitalId(), nursesAdmissionCollection.getLocationId())) {
					if (nursesAdmissionCollection.getDoctorId().toString().equals(doctorId)
							&& nursesAdmissionCollection.getHospitalId().toString().equals(hospitalId)
							&& nursesAdmissionCollection.getLocationId().toString().equals(locationId)) {
						nursesAdmissionCollection.setDiscarded(discarded);
						nursesAdmissionCollection.setUpdatedTime(new Date());
						nursesAdmissionFormRepository.save(nursesAdmissionCollection);
						response = true;

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("data not found!");
				throw new BusinessException(ServiceError.NoRecord, "form  not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deletePreOperationForm(String preOperationFormId, String doctorId, String hospitalId,
			String locationId, Boolean discarded) {
		Boolean response = false;
		try {
			PreOperationFormCollection preOperationFormCollection = preOperationFormRepository
					.findById(new ObjectId(preOperationFormId)).orElse(null);
			if (preOperationFormCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(preOperationFormCollection.getDoctorId(),
						preOperationFormCollection.getHospitalId(), preOperationFormCollection.getLocationId())) {
					if (preOperationFormCollection.getDoctorId().toString().equals(doctorId)
							&& preOperationFormCollection.getHospitalId().toString().equals(hospitalId)
							&& preOperationFormCollection.getLocationId().toString().equals(locationId)) {
						preOperationFormCollection.setDiscarded(discarded);
						preOperationFormCollection.setUpdatedTime(new Date());
						preOperationFormRepository.save(preOperationFormCollection);
						response = true;

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("data not found!");
				throw new BusinessException(ServiceError.NoRecord, "form  not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Boolean deleteInitialAssessment(String initialAssessmentId, String doctorId, String hospitalId,
			String locationId, Boolean discarded) {
		Boolean response = false;
		try {
			InitialAssessmentCollection initialAssessmentCollection = initialAssessmentFormRepository
					.findById(new ObjectId(initialAssessmentId)).orElse(null);
			if (initialAssessmentCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(initialAssessmentCollection.getDoctorId(),
						initialAssessmentCollection.getHospitalId(), initialAssessmentCollection.getLocationId())) {
					if (initialAssessmentCollection.getDoctorId().toString().equals(doctorId)
							&& initialAssessmentCollection.getHospitalId().toString().equals(hospitalId)
							&& initialAssessmentCollection.getLocationId().toString().equals(locationId)) {
						initialAssessmentCollection.setDiscarded(discarded);
						initialAssessmentCollection.setUpdatedTime(new Date());
						initialAssessmentFormRepository.save(initialAssessmentCollection);
						response = true;

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("data not found!");
				throw new BusinessException(ServiceError.NoRecord, "form  not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadInitialAssessmentFormById(String initialAssessmentId) {
		String response = null;

		try {
			InitialAssessmentCollection initialAssessmentCollection = initialAssessmentFormRepository
					.findById(new ObjectId(initialAssessmentId)).orElse(null);
			if (initialAssessmentCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						initialAssessmentCollection.getPatientId(), initialAssessmentCollection.getLocationId(),
						initialAssessmentCollection.getHospitalId());

				UserCollection user = userRepository.findById(initialAssessmentCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = null;

				jasperReportResponse = createJasperForInitialAssessment(initialAssessmentCollection, patient, user,
						PrintSettingType.IPD.getType());

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
			throw new BusinessException(ServiceError.Unknown,
					"Exception in download initial assessment " + e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasperForInitialAssessment(
			InitialAssessmentCollection initialAssessmentCollection, PatientCollection patient, UserCollection user,
			String type) throws NumberFormatException, IOException {

		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Boolean show = false;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						initialAssessmentCollection.getDoctorId(), initialAssessmentCollection.getLocationId(),
						initialAssessmentCollection.getHospitalId(), ComponentType.ALL.getType(),
						PrintSettingType.IPD.getType());
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							initialAssessmentCollection.getDoctorId(), initialAssessmentCollection.getLocationId(),
							initialAssessmentCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getPastHistory())) {
			show = true;
			parameters.put("pastHistory", initialAssessmentCollection.getPastHistory());
		}
		parameters.put("showPH", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getCreatedBy())) {
			show = true;
			parameters.put("treatingDoctor", initialAssessmentCollection.getCreatedBy());
		}
		parameters.put("showTD", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getPresentComplaint())) {
			show = true;
			parameters.put("complaints", initialAssessmentCollection.getPresentComplaint());
		}
		parameters.put("showcompl", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getPsychologicalAssessment())) {
			show = true;
			parameters.put("psychologicalAssessment", initialAssessmentCollection.getPsychologicalAssessment());
		}
		parameters.put("showpsycho", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getInvestigation())) {
			show = true;
			parameters.put("investigation", initialAssessmentCollection.getInvestigation());
		}
		parameters.put("showInvestigation", show);

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getObservation())) {
			show = true;
			parameters.put("observation", initialAssessmentCollection.getObservation());
		}
		parameters.put("showObservation", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getProvisionalDiagnosis())) {
			show = true;
			parameters.put("diagnosis", initialAssessmentCollection.getProvisionalDiagnosis());
		}
		parameters.put("showDiagnosis", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getTreatmentsPlan())) {
			show = true;
			parameters.put("treatmentPlan", initialAssessmentCollection.getTreatmentsPlan());
		}
		parameters.put("showTP", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getGeneralExam())) {
			show = true;
			parameters.put("examination", initialAssessmentCollection.getGeneralExam());
		}
		parameters.put("showEx", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getNoseExam())) {
			show = true;
			parameters.put("noseExam", initialAssessmentCollection.getNoseExam());
		}
		parameters.put("showNoseEx", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getNeckExam())) {
			show = true;
			parameters.put("neckExam", initialAssessmentCollection.getNeckExam());
		}
		parameters.put("showNeckEx", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getEarsExam())) {
			show = true;
			parameters.put("earsExam", initialAssessmentCollection.getEarsExam());
		}
		parameters.put("showEarsEx", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getIpdNumber())) {
			show = true;
			parameters.put("ipdNumber", initialAssessmentCollection.getIpdNumber());
		}
		parameters.put("showIpdNumber", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getOralCavityThroatExam())) {
			show = true;
			parameters.put("oralCavityThroatExam", initialAssessmentCollection.getOralCavityThroatExam());
		}
		parameters.put("showOralCavityThroatEx", show);

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>FORMUNumber: </b>" + (initialAssessmentCollection.getUniqueEmrId() != null
						? initialAssessmentCollection.getUniqueEmrId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				initialAssessmentCollection.getCreatedTime() != null ? initialAssessmentCollection.getCreatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, initialAssessmentCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "DOCTORASSESSMENT"
				+ initialAssessmentCollection.getUniqueEmrId() + new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.DOCTOR_INITIAL_ASSESSMENT, parameters,
				doctorinitialassessmentA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public String downloadPreOprationFormById(String preOperationFormId) {
		String response = null;

		try {
			PreOperationFormCollection preOperationFormCollection = preOperationFormRepository
					.findById(new ObjectId(preOperationFormId)).orElse(null);
			if (preOperationFormCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						preOperationFormCollection.getPatientId(), preOperationFormCollection.getLocationId(),
						preOperationFormCollection.getHospitalId());

				UserCollection user = userRepository.findById(preOperationFormCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = null;

				jasperReportResponse = createJasperForPreOperation(preOperationFormCollection, patient, user,
						PrintSettingType.IPD.getType());

				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn(" Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Exception in download preopration assessment " + e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasperForPreOperation(PreOperationFormCollection preOperationFormCollection,
			PatientCollection patient, UserCollection user, String type) throws NumberFormatException, IOException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Boolean show = false;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						preOperationFormCollection.getDoctorId(), preOperationFormCollection.getLocationId(),
						preOperationFormCollection.getHospitalId(), ComponentType.ALL.getType(),
						PrintSettingType.IPD.getType());
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							preOperationFormCollection.getDoctorId(), preOperationFormCollection.getLocationId(),
							preOperationFormCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getPastHistory())) {
			show = true;
			parameters.put("pastHistory", preOperationFormCollection.getPastHistory());
		}
		parameters.put("showPH", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getCreatedBy())) {
			show = true;
			parameters.put("treatingDoctor", preOperationFormCollection.getCreatedBy());
		}
		parameters.put("showTD", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getComplaint())) {
			show = true;
			parameters.put("complaints", preOperationFormCollection.getComplaint());
		}
		parameters.put("showcompl", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getInvestigation())) {
			show = true;
			parameters.put("investigation", preOperationFormCollection.getInvestigation());
		}
		parameters.put("showInvestigation", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getDiagnosis())) {
			show = true;
			parameters.put("diagnosis", preOperationFormCollection.getDiagnosis());
		}
		parameters.put("showDiagnosis", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getTreatmentsPlan())) {
			show = true;
			parameters.put("treatmentPlan", preOperationFormCollection.getTreatmentsPlan());
		}
		parameters.put("showTP", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getGeneralExam())) {
			show = true;
			parameters.put("examination", preOperationFormCollection.getGeneralExam());
		}
		parameters.put("showEx", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getLocalExam())) {
			show = true;
			parameters.put("localexamination", preOperationFormCollection.getLocalExam());
		}
		parameters.put("showLocalEx", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(preOperationFormCollection.getIpdNumber())) {
			show = true;
			parameters.put("ipdNumber", preOperationFormCollection.getIpdNumber());
		}
		parameters.put("showIpdNumber", show);

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>PREOP-ID: </b>" + (preOperationFormCollection.getUniqueEmrId() != null
						? preOperationFormCollection.getUniqueEmrId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				preOperationFormCollection.getCreatedTime() != null ? preOperationFormCollection.getCreatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, preOperationFormCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "PREOP"
				+ preOperationFormCollection.getUniqueEmrId() + new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.PRE_OPERATION_ASSESSMENT, parameters,
				doctorinitialassessmentA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@Override
	public String downloadNurseAdmissionFormById(String nurseAdmissionFormId) {
		String response = null;

		try {
			NursesAdmissionCollection nursesAdmissionCollection = nursesAdmissionFormRepository
					.findById(new ObjectId(nurseAdmissionFormId)).orElse(null);
			if (nursesAdmissionCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						nursesAdmissionCollection.getPatientId(), nursesAdmissionCollection.getLocationId(),
						nursesAdmissionCollection.getHospitalId());

				UserCollection user = userRepository.findById(nursesAdmissionCollection.getPatientId()).orElse(null);
				JasperReportResponse jasperReportResponse = null;

				jasperReportResponse = createJasperForNurseAssessment(nursesAdmissionCollection, patient, user,
						PrintSettingType.IPD.getType());

				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn(" Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Exception in download nurse assessment " + e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasperForNurseAssessment(NursesAdmissionCollection nursesAdmissionCollection,
			PatientCollection patient, UserCollection user, String type) throws NumberFormatException, IOException {

		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		Boolean show = false;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						nursesAdmissionCollection.getDoctorId(), nursesAdmissionCollection.getLocationId(),
						nursesAdmissionCollection.getHospitalId(), ComponentType.ALL.getType(),
						PrintSettingType.IPD.getType());
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							nursesAdmissionCollection.getDoctorId(), nursesAdmissionCollection.getLocationId(),
							nursesAdmissionCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(), new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getCreatedBy())) {
			show = true;
			parameters.put("treatingDoctor", nursesAdmissionCollection.getCreatedBy());
		}
		parameters.put("showTD", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getNurseName())) {

			show = true;
			parameters.put("nurseName", nursesAdmissionCollection.getNurseName());
		}
		parameters.put("showNurseNm", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getNursingCare())) {
			show = true;
			parameters.put("nurseCare", nursesAdmissionCollection.getNursingCare());
		}
		parameters.put("showNurseCare", show);

		show = false;

		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getCoMorbidities())) {
			show = true;
			parameters.put("coMorbidies", nursesAdmissionCollection.getCoMorbidities());
		}
		parameters.put("showCoMo", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getAdvice())) {
			show = true;
			parameters.put("advice", nursesAdmissionCollection.getAdvice());
		}
		parameters.put("showAdvice", show);

		show = false;
		if (!DPDoctorUtils.allStringsEmpty(nursesAdmissionCollection.getIpdNumber())) {
			show = true;
			parameters.put("ipdNumber", nursesAdmissionCollection.getIpdNumber());
		}
		parameters.put("showIpdNumber", show);

		if (nursesAdmissionCollection.getVitalSigns() != null) {
			String vitalSigns = null;

			String pulse = nursesAdmissionCollection.getVitalSigns().getPulse();
			pulse = (pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse + " " + VitalSignsUnit.PULSE.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(pulse))
				vitalSigns = pulse;

			String temp = nursesAdmissionCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty()
					? "Temperature: " + temp + " " + VitalSignsUnit.TEMPERATURE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + temp;
				else
					vitalSigns = temp;
			}

			String breathing = nursesAdmissionCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty()
					? "Breathing: " + breathing + " " + VitalSignsUnit.BREATHING.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(breathing)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + breathing;
				else
					vitalSigns = breathing;
			}

			String weight = nursesAdmissionCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty() ? "Weight: " + weight + " " + VitalSignsUnit.WEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(weight)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + weight;
				else
					vitalSigns = weight;
			}

			String bloodPressure = "";
			if (nursesAdmissionCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = nursesAdmissionCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic : "";

				String diastolic = nursesAdmissionCollection.getVitalSigns().getBloodPressure().getDiastolic();
				diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic : "";

				if (!DPDoctorUtils.anyStringEmpty(systolic, diastolic))
					bloodPressure = "B.P: " + systolic + "/" + diastolic + " " + VitalSignsUnit.BLOODPRESSURE.getUnit();
				if (!DPDoctorUtils.allStringsEmpty(bloodPressure)) {
					if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
						vitalSigns = vitalSigns + ",  " + bloodPressure;
					else
						vitalSigns = bloodPressure;
				}
			}

			String spo2 = nursesAdmissionCollection.getVitalSigns().getSpo2();
			spo2 = (spo2 != null && !spo2.isEmpty() ? "SPO2: " + spo2 + " " + VitalSignsUnit.SPO2.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(spo2)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + spo2;
				else
					vitalSigns = spo2;
			}
			String height = nursesAdmissionCollection.getVitalSigns().getHeight();
			height = (height != null && !height.isEmpty() ? "Height: " + height + " " + VitalSignsUnit.HEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(height)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + height;
				else
					vitalSigns = spo2;
			}

			String bmi = nursesAdmissionCollection.getVitalSigns().getBmi();
			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				if (bmi.equalsIgnoreCase("nan")) {
					bmi = "";
				}

			} else {
				bmi = "";
			}

			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				bmi = "Bmi: " + String.format("%.3f", Double.parseDouble(bmi));
				if (!DPDoctorUtils.allStringsEmpty(bmi)) {
					vitalSigns = vitalSigns + ",  " + bmi;
				} else {
					vitalSigns = bmi;
				}
			}

			String bsa = nursesAdmissionCollection.getVitalSigns().getBsa();
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				if (bsa.equalsIgnoreCase("nan"))
					bsa = "";

			} else {
				bsa = "";
			}
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				bsa = "Bsa: " + String.format("%.3f", Double.parseDouble(bsa));
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + bsa;
				else
					vitalSigns = bsa;
			}
			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		} else {
			parameters.put("vitalSigns", null);
		}

		if (nursesAdmissionCollection.getOldMedication() != null) {
			parameters.put("MedicineTitle", "Old Medicine List :");
			List<DBObject> dbObjects = new ArrayList<DBObject>();
			for (Medication res : nursesAdmissionCollection.getOldMedication()) {
				DBObject dbObject = new BasicDBObject();
				if (!DPDoctorUtils.allStringsEmpty(res.getDrugName()))
					dbObject.put("drugName", res.getDrugName());
				if (!DPDoctorUtils.allStringsEmpty(res.getFrequency()))
					dbObject.put("frequency", res.getFrequency());
				else
					dbObject.put("frequency", "--");

				dbObjects.add(dbObject);
			}
			parameters.put("vaccination", dbObjects);
		}

		if (nursesAdmissionCollection.getRiskFactor() != null) {
			parameters.put("RiskFactor", "Risk Factor :");

			List<DBObject> dbObjects = new ArrayList<DBObject>();
			for (RiskScore res : nursesAdmissionCollection.getRiskFactor()) {
				DBObject dbObject = new BasicDBObject();
				if (!DPDoctorUtils.allStringsEmpty(res.getFactor()))
					dbObject.put("factor", res.getFactor());
				if (!DPDoctorUtils.allStringsEmpty(res.getValue()))
					dbObject.put("value", res.getValue());
				else
					dbObject.put("value", "--");

				dbObject.put("score", res.getScore());

				dbObjects.add(dbObject);
			}
			parameters.put("riskfactor", dbObjects);
		}

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>NURSEADM-ID: </b>" + (nursesAdmissionCollection.getUniqueEmrId() != null
						? nursesAdmissionCollection.getUniqueEmrId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				nursesAdmissionCollection.getCreatedTime() != null ? nursesAdmissionCollection.getCreatedTime()
						: new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, nursesAdmissionCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "NURSEADM"
				+ nursesAdmissionCollection.getUniqueEmrId() + new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.NURSE_ADMISSION_ASSESSMENT, parameters,
				doctorinitialassessmentA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

}
