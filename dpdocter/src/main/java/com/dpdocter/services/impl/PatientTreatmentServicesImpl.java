package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.PatientTreatmentJasperDetails;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.TreatmentServicesCostCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.PatientTreatmentService;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TreatmentServicesCostRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.request.TreatmentRequest;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TreatmentResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PatientTreatmentServicesImpl implements PatientTreatmentServices {
	private static Logger logger = Logger.getLogger(PatientTreatmentServicesImpl.class);

	@Autowired
	private TreatmentServicesRepository treatmentServicesRepository;

	@Autowired
	private TreatmentServicesCostRepository treatmentServicesCostRepository;

	@Autowired
	private PatientTreamentRepository patientTreamentRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESTreatmentService esTreatmentService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MailService mailService;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private PatientVisitService patientVisitService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESTreatmentServiceRepository esTreatmentServiceRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Override
	@Transactional
	public TreatmentService addEditService(TreatmentService treatmentService) {
		TreatmentService response = null;
		TreatmentServicesCollection treatmentServicesCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(treatmentService.getId())) {
				treatmentServicesCollection = new TreatmentServicesCollection();
				BeanUtil.map(treatmentService, treatmentServicesCollection);
				treatmentServicesCollection.setCreatedTime(new Date());
				treatmentServicesCollection.setUpdatedTime(new Date());
				treatmentServicesCollection.setTreatmentCode("TR" + DPDoctorUtils.generateRandomId());

				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(treatmentServicesCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						treatmentServicesCollection.setRankingCount(1);

						treatmentServicesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					treatmentServicesCollection.setCreatedBy("ADMIN");
				}
				treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
			} else {
				treatmentServicesCollection = treatmentServicesRepository
						.findById(new ObjectId(treatmentService.getId())).orElse(null);
				if (treatmentServicesCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(treatmentService.getName())) {
						treatmentServicesCollection.setName(treatmentService.getName());
					}
					treatmentServicesCollection.setCost(treatmentService.getCost());
					treatmentServicesCollection.setSpeciality(treatmentService.getSpeciality());
					treatmentServicesCollection.setCategory(treatmentService.getCategory());
					treatmentServicesCollection.setUpdatedTime(new Date());
					treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
				} else {
					logger.error("No service found for the given Id");
					throw new BusinessException(ServiceError.NotFound, "No service found for the given Id");
				}
			}
			if (treatmentServicesCollection != null) {
				response = new TreatmentService();
				BeanUtil.map(treatmentServicesCollection, response);
			}
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing services", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing services");
		}
		return response;
	}

	@Override
	@Transactional
	public TreatmentServiceCost addEditServiceCost(TreatmentServiceCost request) {
		TreatmentServiceCost response = null;
		TreatmentServicesCostCollection treatmentServicesCostCollection = null;
		try {
			UserCollection userCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			}
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				treatmentServicesCostCollection = new TreatmentServicesCostCollection();
				BeanUtil.map(request, treatmentServicesCostCollection);
				treatmentServicesCostCollection.setCreatedTime(new Date());
				treatmentServicesCostCollection.setUpdatedTime(new Date());

				if (userCollection != null)
					treatmentServicesCostCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				else
					treatmentServicesCostCollection.setCreatedBy("ADMIN");

			} else {
				treatmentServicesCostCollection = treatmentServicesCostRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				if (treatmentServicesCostCollection != null) {
					treatmentServicesCostCollection.setCost(request.getCost());
					treatmentServicesCostCollection.setUpdatedTime(new Date());
				} else {
					logger.error("No service found for the given Id");
					throw new BusinessException(ServiceError.NotFound, "No service found for the given Id");
				}
			}
			if (treatmentServicesCostCollection != null) {
				TreatmentServicesCollection treatmentServicesCollection = null;
				TreatmentService treatmentService = null;
				if (request.getTreatmentService().getId() != null)
					treatmentServicesCollection = treatmentServicesRepository
							.findById(new ObjectId(request.getTreatmentService().getId())).orElse(null);
				if (treatmentServicesCollection == null) {

					if (request.getTreatmentService().getName() == null) {
						logger.error("Cannot add services without treatment service");
						throw new BusinessException(ServiceError.Unknown,
								"Cannot add services without treatment service");
					}
					treatmentServicesCollection = new TreatmentServicesCollection();
					treatmentServicesCollection.setLocationId(new ObjectId(request.getLocationId()));
					treatmentServicesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					treatmentServicesCollection.setName(request.getTreatmentService().getName());
					treatmentServicesCollection.setCreatedTime(new Date());
					if (userCollection != null)
						treatmentServicesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					else
						treatmentServicesCollection.setCreatedBy("ADMIN");

					treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
					if (treatmentServicesCollection != null) {
						transactionalManagementService.addResource(treatmentServicesCollection.getId(),
								Resource.TREATMENTSERVICE, false);
						ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
						BeanUtil.map(treatmentServicesCollection, esTreatmentServiceDocument);
						esTreatmentService.addEditService(esTreatmentServiceDocument);
					}
				}

				if (treatmentServicesCollection != null) {
					treatmentService = new TreatmentService();
					BeanUtil.map(treatmentServicesCollection, treatmentService);
				}
				treatmentServicesCostCollection.setTreatmentServiceId(treatmentServicesCollection.getId());
				treatmentServicesCostCollection = treatmentServicesCostRepository.save(treatmentServicesCostCollection);
				response = new TreatmentServiceCost();
				BeanUtil.map(treatmentServicesCostCollection, response);
				response.setTreatmentService(treatmentService);
			}
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing cost for treatment services", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing cost for treatment services");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request,
			Boolean isAppointmentAdd, String createdBy, Appointment appointment) {
		PatientTreatmentResponse response;
		PatientTreatmentCollection patientTreatmentCollection = new PatientTreatmentCollection();
		try {
			if (request.getAppointmentRequest() != null && isAppointmentAdd) {
				appointment = addTreatmentAppointment(request.getAppointmentRequest());
				if (appointment != null) {
					request.setAppointmentId(appointment.getAppointmentId());
					request.setTime(appointment.getTime());
					request.setFromDate(appointment.getFromDate());
				}
			}

			patientTreatmentCollection = new PatientTreatmentCollection();
			BeanUtil.map(request, patientTreatmentCollection);

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {

				if (request.getCreatedTime() != null) {
					patientTreatmentCollection.setCreatedTime(request.getCreatedTime());
				} else {
					patientTreatmentCollection.setCreatedTime(new Date());
				}
				patientTreatmentCollection.setAdminCreatedTime(new Date());
				patientTreatmentCollection
						.setUniqueEmrId(UniqueIdInitial.TREATMENT.getInitial() + DPDoctorUtils.generateRandomId());

				if (DPDoctorUtils.anyStringEmpty(createdBy)) {
					UserCollection userCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
						userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
					}
					if (userCollection != null)
						createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName();
					else {
						throw new BusinessException(ServiceError.NotFound, "No Doctor Found");
					}
				}
				patientTreatmentCollection.setCreatedBy(createdBy);
			} else {
				PatientTreatmentCollection oldPatientTreatmentCollection = patientTreamentRepository
						.findByIdAndDoctorIdAndLocationIdAndHospitalId(new ObjectId(request.getId()),
								new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
								new ObjectId(request.getHospitalId()));

				if (oldPatientTreatmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "No treatment found for the given ids");
				} else {

					createdBy = oldPatientTreatmentCollection.getCreatedBy();

					BeanUtil.map(request, patientTreatmentCollection);
					if (request.getCreatedTime() != null) {
						patientTreatmentCollection.setCreatedTime(new Date());
					} else {
						patientTreatmentCollection.setCreatedTime(oldPatientTreatmentCollection.getCreatedTime());
					}
					patientTreatmentCollection.setAdminCreatedTime(oldPatientTreatmentCollection.getAdminCreatedTime());
					patientTreatmentCollection.setUpdatedTime(new Date());
					patientTreatmentCollection.setCreatedBy(createdBy);
					patientTreatmentCollection.setUniqueEmrId(oldPatientTreatmentCollection.getUniqueEmrId());
					patientTreatmentCollection.setDiscarded(oldPatientTreatmentCollection.getDiscarded());
					patientTreatmentCollection.setInHistory(oldPatientTreatmentCollection.getInHistory());
					patientTreatmentCollection
							.setIsPatientDiscarded(oldPatientTreatmentCollection.getIsPatientDiscarded());
				}
			}
			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			List<Treatment> treatments = new ArrayList<Treatment>();
			if (request.getTreatments() != null && !request.getTreatments().isEmpty())
				for (TreatmentRequest treatmentRequest : request.getTreatments()) {

					if (treatmentRequest.getStatus() == null) {
						treatmentRequest.setStatus("NOT_STARTED");
					}
					Treatment treatment = new Treatment();
					TreatmentResponse treatmentResponse = new TreatmentResponse();
					BeanUtil.map(treatmentRequest, treatment);
					BeanUtil.map(treatmentRequest, treatmentResponse);
					TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
							.findById(new ObjectId(treatmentRequest.getTreatmentServiceId())).orElse(null);
					if (treatmentServicesCollection != null) {
						TreatmentService treatmentService = new TreatmentService();
						BeanUtil.map(treatmentServicesCollection, treatmentService);

						treatmentResponse.setTreatmentService(treatmentService);
						treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
						treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
						treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
						treatmentService.setCost(treatmentRequest.getCost());
						addFavouritesToService(treatmentService, createdBy);
					}
					treatments.add(treatment);
					treatmentResponses.add(treatmentResponse);
				}
			patientTreatmentCollection.setTreatments(treatments);

			patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

			response = new PatientTreatmentResponse();
			BeanUtil.map(patientTreatmentCollection, response);
			response.setTreatments(treatmentResponses);

			pushNotificationServices.notifyUser(patientTreatmentCollection.getDoctorId().toString(), "Treament Added",
					ComponentType.TREATMENTS_REFRESH.getType(), patientTreatmentCollection.getPatientId().toString(),
					null);

		} catch (Exception e) {
			logger.error("Error occurred while adding or editing treatment for patients", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing treatment for patients");
		}
		return response;
	}

	@Override
	public PatientTreatmentResponse changePatientTreatmentStatus(String treatmentId, String doctorId, String locationId,
			String hospitalId, Treatment treatment) {
		PatientTreatmentResponse response = null;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
					.findByIdAndDoctorIdAndLocationIdAndHospitalId(new ObjectId(treatmentId), new ObjectId(doctorId),
							new ObjectId(locationId), new ObjectId(hospitalId));
			if (patientTreatmentCollection == null) {
				logger.warn("No treatment found for the given treatmentId");
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given treatmentId");
			}

			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			for (Treatment treatmentObj : patientTreatmentCollection.getTreatments()) {
				if (treatmentObj.getTreatmentServiceId().toString()
						.equalsIgnoreCase(treatment.getTreatmentServiceId().toString())) {
					if (treatment.getStatus() == null)
						treatmentObj.setStatus("NOT_STARTED");
					else
						treatmentObj.setStatus(treatment.getStatus());
				}

				TreatmentResponse treatmentResponse = new TreatmentResponse();
				BeanUtil.map(treatment, treatmentResponse);
				TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
						.findById(treatment.getTreatmentServiceId()).orElse(null);
				if (treatmentServicesCollection != null) {
					TreatmentService treatmentService = new TreatmentService();
					BeanUtil.map(treatmentServicesCollection, treatmentService);
					treatmentResponse.setTreatmentService(treatmentService);
				}
				treatmentResponses.add(treatmentResponse);
			}
			patientTreatmentCollection.setUpdatedTime(new Date());

			patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

			response = new PatientTreatmentResponse();
			BeanUtil.map(patientTreatmentCollection, response);
			response.setTreatments(treatmentResponses);

		} catch (Exception e) {
			logger.error("Error occurred while adding or editing treatment for patients", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing treatment for patients");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse deletePatientTreatment(String treatmentId, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		PatientTreatmentResponse response = null;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
					.findByIdAndDoctorIdAndLocationIdAndHospitalId(new ObjectId(treatmentId), new ObjectId(doctorId),
							new ObjectId(locationId), new ObjectId(hospitalId));

			if (patientTreatmentCollection != null) {
				patientTreatmentCollection.setDiscarded(discarded);
				patientTreatmentCollection.setUpdatedTime(new Date());
				patientTreamentRepository.save(patientTreatmentCollection);
				response = getPatientTreatmentById(treatmentId);

			}

			else {
				logger.warn("No treatment found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while deleting treatment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deleting treatment");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse deletePatientTreatmentForWeb(String treatmentId, Boolean discarded) {
		PatientTreatmentResponse response = null;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
					.findById(new ObjectId(treatmentId)).orElse(null);

			if (patientTreatmentCollection != null) {
				patientTreatmentCollection.setDiscarded(discarded);
				patientTreatmentCollection.setUpdatedTime(new Date());
				patientTreamentRepository.save(patientTreatmentCollection);
				response = getPatientTreatmentById(treatmentId);

			}

			else {
				logger.warn("No treatment found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while deleting treatment", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while deleting treatment");
		}
		return response;
	}

	private Appointment addTreatmentAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment, false);
		} else {
			response = appointmentService.updateAppointment(appointment, false, false);
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse getPatientTreatmentById(String treatmentId) {
		PatientTreatmentResponse response;
		try {
			CustomAggregationOperation projectList = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("doctorId", "$doctorId")
							.append("visitId", "$patientVisit._id").append("uniqueEmrId", "$uniqueEmrId")
							.append("totalCost", "$totalCost").append("totalDiscount", "$totalDiscount")
							.append("grandTotal", "$grandTotal").append("discarded", "$discarded")
							.append("inHistory", "$inHistory").append("appointmentId", "$appointmentId")
							.append("time", "$time").append("fromDate", "$fromDate")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy")
							.append("treatments.treatmentService", "$treatmentService")
							.append("treatments.treatmentServiceId", "$treatments.treatmentServiceId")
							.append("treatments.doctorId", "$treatments.doctorId")
							.append("treatments.doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$treatmentDoctor.title", " ", "$treatmentDoctor.firstName")))
							.append("treatments.status", "$treatments.status")
							.append("treatments.cost", "$treatments.cost").append("treatments.note", "$treatments.note")
							.append("treatments.discount", "$treatments.discount")
							.append("treatments.finalCost", "$treatments.finalCost")
							.append("treatments.quantity", "$treatments.quantity")
							.append("treatments.treatmentFields", "$treatments.treatmentFields")
							.append("appointmentRequest", "$appointmentRequest")));
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(
							new Criteria("_id").is(new ObjectId(treatmentId)).and("isPatientDiscarded").ne(true)),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),
					Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
							"treatmentService"),
					Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
					Aggregation.unwind("treatmentService"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
									true))),
					Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
					Aggregation.unwind("patientVisit"),

					Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$treatmentDoctor").append("preserveNullAndEmptyArrays", true))),
					projectList,

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("id", "$_id")
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
									.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))
									.append("treatments", new BasicDBObject("$push", "$treatments")))));

			AggregationResults<PatientTreatmentResponse> groupResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentResponse.class);
			List<PatientTreatmentResponse> patientDetailsresponse = groupResults.getMappedResults();
			response = patientDetailsresponse.get(0);

		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatment> getPatientTreatmentByIds(List<ObjectId> treatmentId, ObjectId visitId) {

		List<PatientTreatment> response = null;

		try {
			CustomAggregationOperation projectList = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("doctorId", "$doctorId")
							.append("visitId", "$patientVisit._id").append("uniqueEmrId", "$uniqueEmrId")
							.append("totalCost", "$totalCost").append("totalDiscount", "$totalDiscount")
							.append("grandTotal", "$grandTotal").append("discarded", "$discarded")
							.append("inHistory", "$inHistory").append("appointmentId", "$appointmentId")
							.append("time", "$time").append("fromDate", "$fromDate")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy")
							.append("treatments.treatmentService", "$treatmentService")
							.append("treatments.treatmentServiceId", "$treatments.treatmentServiceId")
							.append("treatments.doctorId", "$treatments.doctorId")
							.append("treatments.doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$treatmentDoctor.title", " ", "$treatmentDoctor.firstName")))
							.append("treatments.status", "$treatments.status")
							.append("treatments.cost", "$treatments.cost").append("treatments.note", "$treatments.note")
							.append("treatments.discount", "$treatments.discount")
							.append("treatments.finalCost", "$treatments.finalCost")
							.append("treatments.quantity", "$treatments.quantity")
							.append("treatments.treatmentFields", "$treatments.treatmentFields")));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").in(treatmentId).and("isPatientDiscarded").ne(true)),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),

					Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
							"treatmentService"),
					Aggregation.unwind("treatmentService"),
					Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
					Aggregation.unwind("patientVisit"),
					Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$treatmentDoctor").append("preserveNullAndEmptyArrays", true))),

					projectList,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("id", "$_id")
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("totalCost", new BasicDBObject("$first", "$totalCost"))
									.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
									.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy"))
									.append("treatments", new BasicDBObject("$push", "$treatments")))));

			response = mongoTemplate.aggregate(aggregation, PatientTreatmentCollection.class, PatientTreatment.class)
					.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatmentResponse> getPatientTreatments(long page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory, String status) {
		List<PatientTreatmentResponse> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);

			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);
			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("treatments.status").is(status);
			Aggregation aggregation = null;
			CustomAggregationOperation projectList = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("doctorId", "$doctorId")
							.append("visitId", "$patientVisit._id").append("uniqueEmrId", "$uniqueEmrId")
							.append("totalCost", "$totalCost").append("totalDiscount", "$totalDiscount")
							.append("grandTotal", "$grandTotal").append("discarded", "$discarded")
							.append("inHistory", "$inHistory").append("appointmentId", "$appointmentId")
							.append("time", "$time").append("fromDate", "$fromDate")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy")
							.append("treatments.treatmentService", "$treatmentService")
							.append("treatments.treatmentServiceId", "$treatments.treatmentServiceId")
							.append("treatments.doctorId", "$treatments.doctorId")
							.append("treatments.doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$treatmentDoctor.title", " ", "$treatmentDoctor.firstName")))
							.append("treatments.status", "$treatments.status")
							.append("treatments.cost", "$treatments.cost").append("treatments.note", "$treatments.note")
							.append("treatments.discount", "$treatments.discount")
							.append("treatments.finalCost", "$treatments.finalCost")
							.append("treatments.quantity", "$treatments.quantity")
							.append("treatments.treatmentFields", "$treatments.treatmentFields")));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),
						Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
								"treatmentService"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.unwind("treatmentService"),
						Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path",
										"$treatmentDoctor").append("preserveNullAndEmptyArrays",
												true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("totalCost", new BasicDBObject("$first", "$totalCost"))
								.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
								.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))
								.append("treatments", new BasicDBObject("$push", "$treatments")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),
						Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
								"treatmentService"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),

						Aggregation.unwind("treatmentService"),
						Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),

						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatmentDoctor").append("preserveNullAndEmptyArrays",
										true))),

						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("totalCost", new BasicDBObject("$first", "$totalCost"))
								.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
								.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))
								.append("treatments", new BasicDBObject("$push", "$treatments")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientTreatmentResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatmentResponse> getPatientTreatmentByPatientId(long page, int size, String doctorId,
			String locationId, String hospitalId, String patientId, String updatedTime, Boolean discarded,
			Boolean inHistory, String status) {
		List<PatientTreatmentResponse> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientObjectId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);

			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);
			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("treatments.status").is(status);
			Aggregation aggregation = null;
			CustomAggregationOperation projectList = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("patientId", "$patientId").append("locationId", "$locationId")
							.append("hospitalId", "$hospitalId").append("doctorId", "$doctorId")
							.append("visitId", "$patientVisit._id").append("uniqueEmrId", "$uniqueEmrId")
							.append("totalCost", "$totalCost").append("totalDiscount", "$totalDiscount")
							.append("grandTotal", "$grandTotal").append("discarded", "$discarded")
							.append("inHistory", "$inHistory").append("appointmentId", "$appointmentId")
							.append("time", "$time").append("fromDate", "$fromDate")
							.append("createdTime", "$createdTime").append("updatedTime", "$updatedTime")
							.append("createdBy", "$createdBy")
							.append("treatments.treatmentService", "$treatmentService")
							.append("treatments.treatmentServiceId", "$treatments.treatmentServiceId")
							.append("treatments.doctorId", "$treatments.doctorId")
							.append("treatments.doctorName",
									new BasicDBObject("$concat",
											Arrays.asList("$treatmentDoctor.title", " ", "$treatmentDoctor.firstName")))
							.append("treatments.status", "$treatments.status")
							.append("treatments.cost", "$treatments.cost").append("treatments.note", "$treatments.note")
							.append("treatments.discount", "$treatments.discount")
							.append("treatments.finalCost", "$treatments.finalCost")
							.append("treatments.quantity", "$treatments.quantity")
							.append("treatments.treatmentFields", "$treatments.treatmentFields")));
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),
						Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
								"treatmentService"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),

						Aggregation.lookup("user_cl", "treatment.doctorId", "_id", "treatmentDoctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatmentDoctor").append("preserveNullAndEmptyArrays",
										true))),
						projectList, Aggregation.unwind("treatment"),
						Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("totalCost", new BasicDBObject("$first", "$totalCost"))
								.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
								.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))
								.append("treatments", new BasicDBObject("$push", "$treatments")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$treatments").append("includeArrayIndex", "arrayIndex"))),
						Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id",
								"treatmentService"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),

						Aggregation.unwind("treatmentService"),
						Aggregation.lookup("patient_visit_cl", "_id", "treatmentId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.lookup("user_cl", "treatments.doctorId", "_id", "treatmentDoctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path",
										"$treatmentDoctor").append("preserveNullAndEmptyArrays",
												true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("totalCost", new BasicDBObject("$first", "$totalCost"))
								.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
								.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy"))
								.append("treatments", new BasicDBObject("$push", "$treatments")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientTreatmentResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
		}
		return response;
	}

	@Override
	public TreatmentService deleteService(String treatmentServiceId, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		TreatmentService response = null;
		try {
			TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
					.findById(new ObjectId(treatmentServiceId)).orElse(null);
			if (treatmentServicesCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId(),
						treatmentServicesCollection.getHospitalId(), treatmentServicesCollection.getLocationId())) {
					if (treatmentServicesCollection.getDoctorId().toString().equals(doctorId)
							&& treatmentServicesCollection.getHospitalId().toString().equals(hospitalId)
							&& treatmentServicesCollection.getLocationId().toString().equals(locationId)) {
						treatmentServicesCollection.setDiscarded(discarded);
						treatmentServicesCollection.setUpdatedTime(new Date());
						treatmentServicesRepository.save(treatmentServicesCollection);
						response = new TreatmentService();
						BeanUtil.map(treatmentServicesCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Treatment Service not found!");
				throw new BusinessException(ServiceError.NoRecord, "Treatment Service not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public TreatmentServiceCost deleteServiceCost(String treatmentServiceId, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		TreatmentServiceCost response = null;
		try {
			TreatmentServicesCostCollection treatmentServicesCostCollection = treatmentServicesCostRepository
					.findById(new ObjectId(treatmentServiceId)).orElse(null);
			if (treatmentServicesCostCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCostCollection.getDoctorId(),
						treatmentServicesCostCollection.getHospitalId(),
						treatmentServicesCostCollection.getLocationId())) {
					if (treatmentServicesCostCollection.getDoctorId().toString().equals(doctorId)
							&& treatmentServicesCostCollection.getHospitalId().toString().equals(hospitalId)
							&& treatmentServicesCostCollection.getLocationId().toString().equals(locationId)) {
						treatmentServicesCostCollection.setDiscarded(discarded);
						treatmentServicesCostCollection.setUpdatedTime(new Date());
						treatmentServicesCostRepository.save(treatmentServicesCostCollection);
						response = new TreatmentServiceCost();
						BeanUtil.map(treatmentServicesCostCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Treatment Service not found!");
				throw new BusinessException(ServiceError.NoRecord, "Treatment Service not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<?> getServices(String type, String range, long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<?> response = new ArrayList<Object>();

		switch (PatientTreatmentService.valueOf(type.toUpperCase())) {

		case SERVICE: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalServices(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomServices(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalServices(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;

			}
			break;
		}
		case SERVICECOST: {
			response = getCustomServicesCost(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
			break;
		}

		case SERVICEBYSPECIALITY: {
			response = getServicesBySpeciality(doctorId, locationId, hospitalId, updatedTime, discarded);
			break;
		}
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getServicesBySpeciality(String doctorId, String locationId, String hospitalId, String updatedTime,
			Boolean discarded) {
		List<TreatmentService> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(0, 0, doctorId, locationId, hospitalId, updatedTime,
							discarded, "category", null, specialities, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Treatment Services");
		}
		return response;
	}

	private List<?> getCustomServicesCost(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<TreatmentServiceCost> treatmentServicesCosts = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("updatedTime")
					.gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			}

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("treatment_services_cl", "treatmentServiceId", "_id",
								"treatmentServicesList"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("treatment_services_cl", "treatmentServiceId", "_id",
								"treatmentServicesList"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<TreatmentServiceCost> aggregationResults = mongoTemplate.aggregate(aggregation,
					TreatmentServicesCostCollection.class, TreatmentServiceCost.class);
			treatmentServicesCosts = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Treatment Services With Cost");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Treatment Services With Cost");
		}
		return treatmentServicesCosts;
	}

	@SuppressWarnings("unchecked")
	private List<?> getCustomGlobalServices(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<TreatmentService> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Treatment Services");
		}
		return response;
	}

	private List<?> getCustomServices(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<TreatmentService> response = null;
		try {
			AggregationResults<TreatmentService> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Treatment Services");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<?> getGlobalServices(long page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<TreatmentService> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAllById(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Treatment Services");
		}
		return response;
	}

	@Override
	public void emailPatientTreatment(String treatmentId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = null;
			if (doctorId != null && locationId != null && hospitalId != null) {
				mailResponse = createMailData(treatmentId, doctorId, locationId, hospitalId);
			} else {
				mailResponse = createMailDataForWeb(treatmentId, doctorId, locationId, hospitalId);
			}
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Treatment", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Treatment",
					body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public String downloadPatientTreatment(String treatmentId, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA) {
		String response = null;
		HistoryCollection historyCollection = null;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
					.findById(new ObjectId(treatmentId)).orElse(null);

			if (patientTreatmentCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						patientTreatmentCollection.getPatientId(), patientTreatmentCollection.getLocationId(),
						patientTreatmentCollection.getHospitalId());
				UserCollection user = userRepository.findById(patientTreatmentCollection.getPatientId()).orElse(null);

				if (showPH || showPLH || showFH || showDA) {
					List<HistoryCollection> historyCollections = historyRepository
							.findByLocationIdAndHospitalIdAndPatientId(patientTreatmentCollection.getLocationId(),
									patientTreatmentCollection.getHospitalId(),
									patientTreatmentCollection.getPatientId());
					if (historyCollections != null)
						historyCollection = historyCollections.get(0);
				}
				JasperReportResponse jasperReportResponse = createJasper(patientTreatmentCollection, patient, user,
						historyCollection, showPH, showPLH, showFH, showDA,PrintSettingType.EMR.getType());
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private MailResponse createMailData(String treatmentId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		PatientTreatmentCollection patientTreatmentCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			patientTreatmentCollection = patientTreamentRepository.findById(new ObjectId(treatmentId)).orElse(null);
			if (patientTreatmentCollection != null) {
				if (patientTreatmentCollection.getDoctorId() != null
						&& patientTreatmentCollection.getHospitalId() != null
						&& patientTreatmentCollection.getLocationId() != null) {
					if (patientTreatmentCollection.getDoctorId().toString().equals(doctorId)
							&& patientTreatmentCollection.getHospitalId().toString().equals(hospitalId)
							&& patientTreatmentCollection.getLocationId().toString().equals(locationId)) {

						user = userRepository.findById(patientTreatmentCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								patientTreatmentCollection.getPatientId(), patientTreatmentCollection.getLocationId(),
								patientTreatmentCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(patientTreatmentCollection.getDoctorId());
						emailTrackCollection.setHospitalId(patientTreatmentCollection.getHospitalId());
						emailTrackCollection.setLocationId(patientTreatmentCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.TREATMENT.getType());
						emailTrackCollection.setSubject("Treatment");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(patientTreatmentCollection, patient,
								user, null, false, false, false, false,PrintSettingType.EMAIL.getType());
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId))
								.orElse(null);

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						response.setClinicAddress(address);
						response.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						response.setMailRecordCreatedDate(sdf.format(patientTreatmentCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Prescription Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Prescription Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private JasperReportResponse createJasper(PatientTreatmentCollection patientTreatmentCollection,
			PatientCollection patient, UserCollection user, HistoryCollection historyCollection, Boolean showPH,
			Boolean showPLH, Boolean showFH, Boolean showDA, String printSettingType) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		List<PatientTreatmentJasperDetails> patientTreatmentJasperDetails = null;
		if (patientTreatmentCollection.getTreatments() != null
				&& !patientTreatmentCollection.getTreatments().isEmpty()) {
			Boolean showTreatmentQuantity = false, showTreatmentDiscount = false;
			int no = 0;
			patientTreatmentJasperDetails = new ArrayList<PatientTreatmentJasperDetails>();
			for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
				PatientTreatmentJasperDetails patientTreatments = new PatientTreatmentJasperDetails();
				TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
						.findById(treatment.getTreatmentServiceId()).orElse(null);
				patientTreatments.setNo(++no);
				if (!DPDoctorUtils.anyStringEmpty(treatment.getStatus())) {
					String status = treatment.getStatus().replaceAll("_", " ");
					status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
					patientTreatments.setStatus(status);
				} else {
					patientTreatments.setStatus("--");
				}

				String serviceName = treatmentServicesCollection.getName() != null
						? treatmentServicesCollection.getName()
						: "";
				String fieldName = "";
				if (treatment.getTreatmentFields() != null && !treatment.getTreatmentFields().isEmpty()) {
					String key = "";
					for (Fields treatmentFile : treatment.getTreatmentFields()) {
						key = treatmentFile.getKey();
						if (!DPDoctorUtils.anyStringEmpty(key)) {
							if (key.equalsIgnoreCase("toothNumber")) {
								key = "Tooth No :";
							}
							if (key.equalsIgnoreCase("material")) {
								key = "Material :";
							}

							if (!DPDoctorUtils.anyStringEmpty(treatmentFile.getValue())) {
								fieldName = fieldName + "<br><font size='1'><i>" + key + treatmentFile.getValue()
										+ "</i></font>";
							}
						}
					}
				}
				serviceName = serviceName == "" ? "--" : serviceName + fieldName;
				patientTreatments.setTreatmentServiceName(serviceName);
				if (treatment.getQuantity() != null && treatment.getQuantity().getValue() > 0) {
					showTreatmentQuantity = true;
					String quantity = treatment.getQuantity().getValue() + " ";
					if (treatment.getQuantity().getType() != null)
						quantity = quantity + treatment.getQuantity().getType().getDuration();
					patientTreatments.setQuantity(quantity);
				}
				if (treatment.getDiscount() != null && treatment.getDiscount().getValue() > 0)
					showTreatmentDiscount = true;
				patientTreatments.setNote(
						treatment.getNote() != null ? "<font size='1'><b>Note :</b> " + treatment.getNote() + "</font>"
								: "");
				patientTreatments.setCost(treatment.getCost() + "");
				patientTreatments.setDiscount((treatment.getDiscount() != null)
						? treatment.getDiscount().getValue() + " " + treatment.getDiscount().getUnit().getUnit()
						: "");
				patientTreatments.setFinalCost(treatment.getFinalCost() + "");
				patientTreatmentJasperDetails.add(patientTreatments);
			}
			parameters.put("showTreatmentDiscount", showTreatmentDiscount);
			parameters.put("showTreatmentQuantity", showTreatmentQuantity);
			parameters.put("services", patientTreatmentJasperDetails);

			String total = "";
			if (patientTreatmentCollection.getTotalCost() > 0)
				total = "<b>Total Cost:</b> " + patientTreatmentCollection.getTotalCost() + "₹   ";

			if (patientTreatmentCollection.getTotalDiscount() != null
					&& patientTreatmentCollection.getTotalDiscount().getValue() > 0.0) {
				total = total + "<b>Total Discount:</b> " + patientTreatmentCollection.getTotalDiscount().getValue()
						+ " " + patientTreatmentCollection.getTotalDiscount().getUnit().getUnit() + "   ";
			}

			if (patientTreatmentCollection.getGrandTotal() != 0)
				total = total + "<b>Grand Total:</b> " + patientTreatmentCollection.getGrandTotal() + "₹";
			parameters.put("grandTotal", total);
			parameters.put("patienttreatmentId", patientTreatmentCollection.getId().toString());
			if (parameters.get("followUpAppointment") == null
					&& !DPDoctorUtils.anyStringEmpty(patientTreatmentCollection.getAppointmentId())
					&& patientTreatmentCollection.getTime() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
				String _24HourTime = String.format("%02d:%02d", patientTreatmentCollection.getTime().getFromTime() / 60,
						patientTreatmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

				Date _24HourDt = _24HourSDF.parse(_24HourTime);
				String dateTime = _12HourSDF.format(_24HourDt) + ", "
						+ sdf.format(patientTreatmentCollection.getFromDate());
				parameters.put("followUpAppointment", "Next Review on " + dateTime);
			}
			PrintSettingsCollection printSettings = null;
			printSettings = printSettingsRepository
					.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							patientTreatmentCollection.getDoctorId(), patientTreatmentCollection.getLocationId(),
							patientTreatmentCollection.getHospitalId(), ComponentType.ALL.getType(),
							printSettingType);
			if (printSettings == null)
				printSettings = printSettingsRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
								patientTreatmentCollection.getDoctorId(), patientTreatmentCollection.getLocationId(),
								patientTreatmentCollection.getHospitalId(), ComponentType.ALL.getType(),
								PrintSettingType.DEFAULT.getType());

			if (printSettings == null) {
				printSettings = new PrintSettingsCollection();
				DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
				BeanUtil.map(defaultPrintSettings, printSettings);

			}

			if (printSettings.getContentSetup() != null) {
				parameters.put("isEnableTreatmentcost", printSettings.getContentSetup().getShowTreatmentcost());
			} else {
				parameters.put("isEnableTreatmentcost", true);
			}

			if (historyCollection != null) {
				parameters.put("showHistory", true);
				patientVisitService.includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
			}

			patientVisitService.generatePatientDetails(
					(printSettings != null && printSettings.getHeaderSetup() != null
							? printSettings.getHeaderSetup().getPatientDetails()
							: null),
					patient, "<b>TID: </b>" + patientTreatmentCollection.getUniqueEmrId(),
					patient.getLocalPatientName(), user.getMobileNumber(), parameters,
					patientTreatmentCollection.getCreatedTime() != null ? patientTreatmentCollection.getCreatedTime()
							: new Date(),
					printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
			patientVisitService.generatePrintSetup(parameters, printSettings, patientTreatmentCollection.getDoctorId());
			String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "PATIENTTREARMENT-"
					+ patientTreatmentCollection.getUniqueEmrId() + new Date().getTime();
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
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
							? printSettings.getPageSetup().getLeftMargin()
							: 20)
					: 20;
			Integer rightMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
							? printSettings.getPageSetup().getRightMargin()
							: 20)
					: 20;

			response = jasperReportService.createPDF(ComponentType.TREATMENT, parameters, null, layout, pageSize,
					topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public int getTreatmentsCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int count;
		try {
			if (isOTPVerified)
				count = patientTreamentRepository.countByPatientId(patientObjectId);
			else
				count = patientTreamentRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return count;
	}

	@Override
	public Integer genrateTreatmentCode() {
		Integer count = 0;
		try {
			List<TreatmentServicesCollection> treatmentServicesCollections = treatmentServicesRepository.findAll();
			int i = 0;
			for (TreatmentServicesCollection treatmentServicesCollection : treatmentServicesCollections) {
				treatmentServicesCollection.setTreatmentCode("TR" + DPDoctorUtils.generateRandomId() + i);
				i++;
				ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
				BeanUtil.map(treatmentServicesCollection, esTreatmentServiceDocument);
				esTreatmentServiceRepository.save(esTreatmentServiceDocument);
			}
			treatmentServicesRepository.saveAll(treatmentServicesCollections);
			count = treatmentServicesCollections.size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return count;
	}

	@Override
	public TreatmentService addFavouritesToService(TreatmentService request, String createdBy) {

		TreatmentService response = null;
		TreatmentServicesCollection treatmentServicesCollection = new TreatmentServicesCollection();
		try {
			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				BeanUtil.map(request, treatmentServicesCollection);

				treatmentServicesCollection.setTreatmentCode("TR" + DPDoctorUtils.generateRandomId());
				if (DPDoctorUtils.anyStringEmpty(createdBy)
						&& !DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(treatmentServicesCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null)
						createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName();
				}
				treatmentServicesCollection.setCreatedBy(createdBy);
				Date createdTime = new Date();
				treatmentServicesCollection.setCreatedTime(createdTime);
				treatmentServicesCollection.setRankingCount(1);

				treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
			} else {

				TreatmentServicesCollection originalTreatmentServicesCollection = treatmentServicesRepository
						.findById(new ObjectId(request.getId())).orElse(null);

				if (originalTreatmentServicesCollection == null) {
					logger.error("Invalid treatmentService Id");
					throw new BusinessException(ServiceError.Unknown, "Invalid treatmentService Id");
				}
				treatmentServicesCollection = treatmentServicesRepository.findByTreatmentCodeAndDoctorId(
						originalTreatmentServicesCollection.getTreatmentCode(),
						originalTreatmentServicesCollection.getDoctorId());
				if (treatmentServicesCollection == null) {
					treatmentServicesCollection = originalTreatmentServicesCollection;

					treatmentServicesCollection.setLocationId(new ObjectId(request.getLocationId()));
					treatmentServicesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					treatmentServicesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
					treatmentServicesCollection.setRankingCount(1);
					treatmentServicesCollection.setId(null);
				} else {
					treatmentServicesCollection.setLocationId(new ObjectId(request.getLocationId()));
					treatmentServicesCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					treatmentServicesCollection.setRankingCount(treatmentServicesCollection.getRankingCount() + 1);
				}

				treatmentServicesCollection.setUpdatedTime(new Date());
				treatmentServicesCollection.setCost(request.getCost());

				treatmentServicesCollection = treatmentServicesRepository.save(treatmentServicesCollection);
			}

			transactionalManagementService.addResource(treatmentServicesCollection.getId(), Resource.TREATMENTSERVICE,
					false);
			if (treatmentServicesCollection != null) {
				ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
				BeanUtil.map(treatmentServicesCollection, esTreatmentServiceDocument);

				esTreatmentService.addEditService(esTreatmentServiceDocument);
			}
			response = new TreatmentService();
			BeanUtil.map(treatmentServicesCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving treatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving treatmentService");
		}
		return response;
	}

	@Override
	public TreatmentService makeServiceFavourite(String serviceId, String doctorId, String locationId,
			String hospitalId) {
		TreatmentService response = null;

		try {
			ObjectId serviceObjectId = new ObjectId(serviceId), doctorObjectId = new ObjectId(doctorId),
					locationObjectId = new ObjectId(locationId), hospitalObjectId = new ObjectId(hospitalId);
			TreatmentServicesCollection originalService = treatmentServicesRepository.findById(serviceObjectId)
					.orElse(null);
			if (originalService == null) {
				logger.error("Invalid Service Id");
				throw new BusinessException(ServiceError.Unknown, "Invalid Service Id");
			}
			TreatmentServicesCollection servicesCollection = treatmentServicesRepository
					.findByTreatmentCodeAndDoctorId(originalService.getTreatmentCode(), doctorObjectId);
			if (servicesCollection == null) {
				servicesCollection = originalService;

				servicesCollection.setLocationId(locationObjectId);
				servicesCollection.setHospitalId(hospitalObjectId);
				servicesCollection.setDoctorId(doctorObjectId);
				servicesCollection.setRankingCount(1);
				servicesCollection.setId(null);
			} else {
				servicesCollection.setLocationId(locationObjectId);
				servicesCollection.setHospitalId(hospitalObjectId);
				servicesCollection.setRankingCount(servicesCollection.getRankingCount() + 1);
				servicesCollection.setUpdatedTime(new Date());
			}
			servicesCollection = treatmentServicesRepository.save(servicesCollection);
			response = new TreatmentService();
			BeanUtil.map(servicesCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While making Service Favourite");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While making Service Favourite");
		}
		return response;
	}

	@Override
	@Transactional
	public List<TreatmentService> getListBySpeciality(String speciality) {
		List<TreatmentService> response = null;
		Aggregation aggregation = null;
		Criteria criteria = new Criteria().and("speciality").in(speciality);
		criteria.and("category").exists(true);
		aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		AggregationResults<TreatmentService> aggregationResults = mongoTemplate.aggregate(aggregation,
				TreatmentServicesCollection.class, TreatmentService.class);
		response = aggregationResults.getMappedResults();
		return response;
	}

	private MailResponse createMailDataForWeb(String treatmentId, String doctorId, String locationId,
			String hospitalId) {
		MailResponse response = null;
		PatientTreatmentCollection patientTreatmentCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			patientTreatmentCollection = patientTreamentRepository.findById(new ObjectId(treatmentId)).orElse(null);
			if (patientTreatmentCollection != null) {

				user = userRepository.findById(patientTreatmentCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						patientTreatmentCollection.getPatientId(), patientTreatmentCollection.getLocationId(),
						patientTreatmentCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(patientTreatmentCollection.getDoctorId());
				emailTrackCollection.setHospitalId(patientTreatmentCollection.getHospitalId());
				emailTrackCollection.setLocationId(patientTreatmentCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.TREATMENT.getType());
				emailTrackCollection.setSubject("Treatment");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createJasper(patientTreatmentCollection, patient, user,
						null, false, false, false, false,PrintSettingType.EMAIL.getType());
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(patientTreatmentCollection.getDoctorId())
						.orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(patientTreatmentCollection.getLocationId()).orElse(null);

				response = new MailResponse();
				response.setMailAttachment(mailAttachment);
				response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				response.setClinicAddress(address);
				response.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				response.setMailRecordCreatedDate(sdf.format(patientTreatmentCollection.getCreatedTime()));
				response.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			} else {
				logger.warn("Treatment not found.Please check treatmentId.");
				throw new BusinessException(ServiceError.NoRecord, "Treatment not found.Please check treatmentId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<TreatmentService> getTreatmentServices(List<ObjectId> idList) {
		List<TreatmentService> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(idList).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<TreatmentService> results = mongoTemplate.aggregate(aggregation,

					TreatmentServicesCollection.class, TreatmentService.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting TreatmentService");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting TreatmentService");
		}
		return response;
	}

}
