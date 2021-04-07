package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.dpdocter.beans.NursingCareExam;
import com.dpdocter.beans.Patient;
import com.dpdocter.collections.DischargeSummaryCollection;
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
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.response.InitialAdmissionResponse;
import com.dpdocter.response.InitialAssessmentResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.PreOperationAssessmentResponse;
import com.dpdocter.services.InitialAssessmentService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;

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
			String hospitalId, String patientId, int page, int size,Boolean discarded) {
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
			criteria = criteria.and("discarded").is(discarded);

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
			String hospitalId, String patientId, int page, int size,Boolean discarded) {
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
			criteria = criteria.and("discarded").is(discarded);

			
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
			String hospitalId, String patientId, int page, int size,Boolean discarded) {
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
			criteria = criteria.and("discarded").is(discarded);

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

	@Override
	public InitialAdmissionResponse getAdmissionFormById(String id) {
		InitialAdmissionResponse response = null;
		try {
			NursesAdmissionCollection nursesAdmissionCollection = nursesAdmissionFormRepository.findById(new ObjectId(id)).orElse(null);
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
			PreOperationFormCollection preOperationFormCollection = preOperationFormRepository.findById(new ObjectId(id)).orElse(null);
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
			InitialAssessmentCollection initialAssessmentCollection = initialAssessmentFormRepository.findById(new ObjectId(id)).orElse(null);
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
			NursesAdmissionCollection nursesAdmissionCollection = nursesAdmissionFormRepository.findById(new ObjectId(nurseAdmissionFormId)).orElse(null);
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
			PreOperationFormCollection preOperationFormCollection = preOperationFormRepository.findById(new ObjectId(preOperationFormId)).orElse(null);
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
			InitialAssessmentCollection initialAssessmentCollection = initialAssessmentFormRepository.findById(new ObjectId(initialAssessmentId)).orElse(null);
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

				jasperReportResponse = createJasper(initialAssessmentCollection, patient, user,PrintSettingType.IPD.getType());

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
			throw new BusinessException(ServiceError.Unknown, "Exception in download initial assessment "+e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasper(InitialAssessmentCollection initialAssessmentCollection,
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
						initialAssessmentCollection.getDoctorId(), initialAssessmentCollection.getLocationId(),
						initialAssessmentCollection.getHospitalId(), ComponentType.ALL.getType(), PrintSettingType.IPD.getType());
		if (printSettings == null){
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							initialAssessmentCollection.getDoctorId(), initialAssessmentCollection.getLocationId(),
							initialAssessmentCollection.getHospitalId(), ComponentType.ALL.getType(), PrintSettingType.DEFAULT.getType(),new Sort(Sort.Direction.DESC, "updatedTime"));
			if(!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
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


		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getPresentComplaint())) {
			show = true;
			parameters.put("complaints", initialAssessmentCollection.getPresentComplaint());
		}
		parameters.put("showcompl", show);
		show = false;
		

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getProvisionalDiagnosis())) {
			show = true;
			parameters.put("diagnosis", initialAssessmentCollection.getProvisionalDiagnosis());
		}
		parameters.put("showDiagnosis", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getInvestigation())) {
			show = true;
			parameters.put("treatmentPlan", initialAssessmentCollection.getInvestigation());
		}
		parameters.put("showTP", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(initialAssessmentCollection.getGeneralExam())) {
			show = true;
			parameters.put("examination", initialAssessmentCollection.getGeneralExam());
		}
		parameters.put("showEx", show);
		show = false;

	
		
		
		
		
		

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient,
				"<b>ADMIT-CARD-ID: </b>"
						+ (initialAssessmentCollection.getUniqueEmrId() != null ? initialAssessmentCollection.getUniqueEmrId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				initialAssessmentCollection.getCreatedTime() != null ? initialAssessmentCollection.getCreatedTime() : new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, initialAssessmentCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "DOCTORASSESSMENT"
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
		response = jasperReportService.createPDF(ComponentType.DOCTOR_INITIAL_ASSESSMENT, parameters, doctorinitialassessmentA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
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
