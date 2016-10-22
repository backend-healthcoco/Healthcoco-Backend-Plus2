package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.TreatmentServicesCostCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
import com.dpdocter.enums.PatientTreatmentService;
import com.dpdocter.enums.PatientTreatmentStatus;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.TreatmentServicesCostRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.request.TreatmentRequest;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.response.TreatmentResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.TransactionalManagementService;

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

				if (!DPDoctorUtils.anyStringEmpty(treatmentServicesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(treatmentServicesCollection.getDoctorId());
					if (userCollection != null) {
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
						.findOne(new ObjectId(treatmentService.getId()));
				if (treatmentServicesCollection != null) {
					if (!DPDoctorUtils.anyStringEmpty(treatmentService.getName())) {
						treatmentServicesCollection.setName(treatmentService.getName());
					}
					treatmentServicesCollection.setSpeciality(treatmentService.getSpeciality());
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
				userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
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
						.findOne(new ObjectId(request.getId()));
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
							.findOne(new ObjectId(request.getTreatmentService().getId()));
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
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing cost for treatment services");
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request) {
		PatientTreatmentResponse response;
		PatientTreatmentCollection patientTreatmentCollection;
		Appointment appointment = null;
		try {
			if (request.getAppointmentRequest() != null) {
				appointment = addTreatmentAppointment(request.getAppointmentRequest());
			}

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				patientTreatmentCollection = new PatientTreatmentCollection();
				if (appointment != null) {
					request.setAppointmentId(appointment.getAppointmentId());
					request.setTime(appointment.getTime());
					request.setFromDate(appointment.getFromDate());
				}
				patientTreatmentCollection.setCreatedTime(new Date());
				BeanUtil.map(request, patientTreatmentCollection);
				UserCollection userCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
					userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				}
				if (userCollection != null)
					patientTreatmentCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				else {
					throw new BusinessException(ServiceError.NotFound, "No Doctor Found");
				}
			} else {
				patientTreatmentCollection = patientTreamentRepository.findOne(new ObjectId(request.getId()),
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()));
				if (patientTreatmentCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "No treatment found for the given ids");
				} else {
					patientTreatmentCollection.setTotalCost(request.getTotalCost());
					patientTreatmentCollection.setUpdatedTime(new Date());
				}
			}
			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			List<Treatment> treatments = new ArrayList<Treatment>();
			for (TreatmentRequest treatmentRequest : request.getTreatments()) {

				if (treatmentRequest.getStatus() == null) {
					treatmentRequest.setStatus(PatientTreatmentStatus.NOT_STARTED);
				}
				Treatment treatment = new Treatment();
				TreatmentResponse treatmentResponse = new TreatmentResponse();
				BeanUtil.map(treatmentRequest, treatment);
				BeanUtil.map(treatmentRequest, treatmentResponse);
				TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
						.findOne(new ObjectId(treatmentRequest.getTreatmentServiceId()));
				if (treatmentServicesCollection != null) {
					TreatmentService treatmentService = new TreatmentService();
					BeanUtil.map(treatmentServicesCollection, treatmentService);
					treatmentResponse.setTreatmentService(treatmentService);
				}
				treatments.add(treatment);
				treatmentResponses.add(treatmentResponse);
			}
			patientTreatmentCollection.setTreatments(treatments);
			patientTreatmentCollection
					.setUniqueEmrId(UniqueIdInitial.TREATMENT.getInitial() + DPDoctorUtils.generateRandomId());
			;
			patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

			response = new PatientTreatmentResponse();
			BeanUtil.map(patientTreatmentCollection, response);
			response.setTreatments(treatmentResponses);
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing treatment for patients", e);
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
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository.findOne(
					new ObjectId(treatmentId), new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId));
			if (patientTreatmentCollection == null) {
				logger.warn("No treatment found for the given treatmentId");
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given treatmentId");
			}

			List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
			for (Treatment treatmentObj : patientTreatmentCollection.getTreatments()) {
				if (treatmentObj.getTreatmentServiceId().toString()
						.equalsIgnoreCase(treatment.getTreatmentServiceId().toString())) {
					if (treatment.getStatus() == null)
						treatmentObj.setStatus(PatientTreatmentStatus.NOT_STARTED);
					else
						treatmentObj.setStatus(treatment.getStatus());
				}

				TreatmentResponse treatmentResponse = new TreatmentResponse();
				BeanUtil.map(treatment, treatmentResponse);
				TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
						.findOne(treatment.getTreatmentServiceId());
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
			throw new BusinessException(ServiceError.Unknown,
					"Error occurred while adding or editing treatment for patients");
		}
		return response;
	}

	@Override
	@Transactional
	public boolean deletePatientTreatment(String treatmentId, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		boolean response = false;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository.findOne(
					new ObjectId(treatmentId), new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId));

			if (patientTreatmentCollection != null) {
				patientTreatmentCollection.setDiscarded(discarded);
				patientTreatmentCollection.setUpdatedTime(new Date());
				patientTreamentRepository.save(patientTreatmentCollection);
				response = true;
			} else {
				logger.warn("No treatment found for the given id");
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while deleting treatment", e);
			throw new BusinessException(ServiceError.Unknown, "Error while deleting treatment");
		}
		return response;
	}

	private Appointment addTreatmentAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment);
		} else {
			response = appointmentService.updateAppointment(appointment);
		}
		return response;
	}

	@Override
	@Transactional
	public PatientTreatmentResponse getPatientTreatmentById(String treatmentId) {
		PatientTreatmentResponse response;
		try {
			PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository
					.findOne(new ObjectId(treatmentId));
			if (patientTreatmentCollection != null) {
				List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
				for (Treatment treatment : patientTreatmentCollection.getTreatments()) {

					TreatmentResponse treatmentResponse = new TreatmentResponse();
					BeanUtil.map(treatment, treatmentResponse);
					TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
							.findOne(treatment.getTreatmentServiceId());
					if (treatmentServicesCollection != null) {
						TreatmentService treatmentService = new TreatmentService();
						BeanUtil.map(treatmentServicesCollection, treatmentService);
						treatmentResponse.setTreatmentService(treatmentService);
					}
					treatmentResponses.add(treatmentResponse);
				}
				response = new PatientTreatmentResponse();
				BeanUtil.map(patientTreatmentCollection, response);
				response.setTreatments(treatmentResponses);
			} else {
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatment> getPatientTreatmentByIds(List<ObjectId> treatmentId) {

		List<PatientTreatment> response = null;
		List<PatientTreatmentCollection> patientTreatmentCollectionList = null;

		try {
			patientTreatmentCollectionList = patientTreamentRepository.findByIds(treatmentId);
			if (patientTreatmentCollectionList != null) {
				response = new ArrayList<PatientTreatment>();
				for (PatientTreatmentCollection patientTreatmentCollection : patientTreatmentCollectionList) {
					PatientTreatment patientTreatment = new PatientTreatment();
					List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
					for (Treatment treatment : patientTreatmentCollection.getTreatments()) {

						TreatmentResponse treatmentResponse = new TreatmentResponse();
						BeanUtil.map(treatment, treatmentResponse);
						TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
								.findOne(treatment.getTreatmentServiceId());
						if (treatmentServicesCollection != null) {
							TreatmentService treatmentService = new TreatmentService();
							BeanUtil.map(treatmentServicesCollection, treatmentService);
							treatmentResponse.setTreatmentService(treatmentService);
						}
						treatmentResponses.add(treatmentResponse);
					}

					BeanUtil.map(patientTreatmentCollection, patientTreatment);
					patientTreatment.setTreatments(treatmentResponses);
					response.add(patientTreatment);
				}

			} else {
				throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
			}
		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatmentResponse> getPatientTreatments(int page, int size, String doctorId, String locationId,
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
					.is(patientObjectId);
			if (!isOTPVerified) {
				criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				}
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);
			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("treatments.status").is(status);
			Aggregation aggregation = null;
			// Aggregation.lookup("treatment_services_cl",
			// "treatments.treatmentServiceId", "_id",
			// "treatments.treatmentServices")
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientTreatmentCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentCollection.class);
			List<PatientTreatmentCollection> patientTreatmentCollections = aggregationResults.getMappedResults();
			if (patientTreatmentCollections != null && !patientTreatmentCollections.isEmpty()) {
				response = new ArrayList<PatientTreatmentResponse>();

				for (PatientTreatmentCollection patientTreatmentCollection : patientTreatmentCollections) {
					PatientTreatmentResponse patientTreatmentResponse = new PatientTreatmentResponse();
					BeanUtil.map(patientTreatmentCollection, patientTreatmentResponse);
					List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
					for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
						TreatmentResponse treatmentResponse = new TreatmentResponse();
						BeanUtil.map(treatment, treatmentResponse);
						TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
								.findOne(treatment.getTreatmentServiceId());
						if (treatmentServicesCollection != null) {
							TreatmentService treatmentService = new TreatmentService();
							BeanUtil.map(treatmentServicesCollection, treatmentService);
							treatmentResponse.setTreatmentService(treatmentService);
						}
						treatmentResponses.add(treatmentResponse);
					}
					patientTreatmentResponse.setTreatments(treatmentResponses);
					response.add(patientTreatmentResponse);
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientTreatmentResponse> getPatientTreatmentByPatientId(int page, int size, String doctorId,
			String locationId, String hospitalId, String patientId, String updatedTime, Boolean discarded,
			Boolean inHistory, String status) {
		List<PatientTreatmentResponse> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			Criteria criteria = new Criteria("patientId").is(patientObjectId);
			patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (!DPDoctorUtils.anyStringEmpty(updatedTime))
				criteria = criteria.and("updatedTime").gte(new Date(createdTimeStamp)).and("patientId")
						.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
				criteria.and("doctorId").is(doctorObjectId);
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				}
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);
			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("treatments.status").is(status);
			Aggregation aggregation = null;
			// Aggregation.lookup("treatment_services_cl",
			// "treatments.treatmentServiceId", "_id",
			// "treatments.treatmentServices")
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientTreatmentCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientTreatmentCollection.class, PatientTreatmentCollection.class);
			List<PatientTreatmentCollection> patientTreatmentCollections = aggregationResults.getMappedResults();
			if (patientTreatmentCollections != null && !patientTreatmentCollections.isEmpty()) {
				response = new ArrayList<PatientTreatmentResponse>();

				for (PatientTreatmentCollection patientTreatmentCollection : patientTreatmentCollections) {
					PatientTreatmentResponse patientTreatmentResponse = new PatientTreatmentResponse();
					BeanUtil.map(patientTreatmentCollection, patientTreatmentResponse);
					List<TreatmentResponse> treatmentResponses = new ArrayList<TreatmentResponse>();
					for (Treatment treatment : patientTreatmentCollection.getTreatments()) {
						TreatmentResponse treatmentResponse = new TreatmentResponse();
						BeanUtil.map(treatment, treatmentResponse);
						TreatmentServicesCollection treatmentServicesCollection = treatmentServicesRepository
								.findOne(treatment.getTreatmentServiceId());
						if (treatmentServicesCollection != null) {
							TreatmentService treatmentService = new TreatmentService();
							BeanUtil.map(treatmentServicesCollection, treatmentService);
							treatmentResponse.setTreatmentService(treatmentService);
						}
						treatmentResponses.add(treatmentResponse);
					}
					patientTreatmentResponse.setTreatments(treatmentResponses);
					response.add(patientTreatmentResponse);
				}
			}
		} catch (Exception e) {
			logger.error("Error while getting patient treatments", e);
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
					.findOne(new ObjectId(treatmentServiceId));
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
					.findOne(new ObjectId(treatmentServiceId));
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
	public List<?> getServices(String type, String range, int page, int size, String doctorId, String locationId,
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
		}
		return response;
	}

	private List<?> getCustomServicesCost(int page, int size, String doctorId, String locationId, String hospitalId,
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
	private List<?> getCustomGlobalServices(int page, int size, String doctorId, String locationId, String hospitalId,
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
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
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

	private List<?> getCustomServices(int page, int size, String doctorId, String locationId, String hospitalId,
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
	private List<?> getGlobalServices(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
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
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
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

}
