package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DailyImprovementFeedback;
import com.dpdocter.beans.DailyPatientFeedback;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.PatientFeedback;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.collections.AppointmentGeneralFeedbackCollection;
import com.dpdocter.collections.DailyImprovementFeedbackCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientFeedbackCollection;
import com.dpdocter.collections.PharmacyFeedbackCollection;
import com.dpdocter.collections.PrescriptionFeedbackCollection;
import com.dpdocter.collections.ServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DurationUnitEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentGeneralFeedbackRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.DailyImprovementFeedbackRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientFeedbackRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PharmacyFeedbackRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrescritptionFeedbackRepository;
import com.dpdocter.repository.ServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DailyImprovementFeedbackRequest;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientFeedbackReplyRequest;
import com.dpdocter.request.PatientFeedbackRequest;
import com.dpdocter.request.PharmacyFeedbackRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.response.DailyImprovementFeedbackResponse;
import com.dpdocter.response.PatientFeedbackIOSResponse;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.FeedbackService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class FeedbackServiceImpl implements FeedbackService {

	@Autowired
	private AppointmentGeneralFeedbackRepository appointmentGeneralFeedbackRepository;

	@Autowired
	private PrescritptionFeedbackRepository prescritptionFeedbackRepository;

	@Autowired
	private PharmacyFeedbackRepository pharmacyFeedbackRepository;

	@Autowired
	private DailyImprovementFeedbackRepository dailyImprovementFeedbackRepository;

	@Autowired
	private PatientFeedbackRepository patientFeedbackRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ServicesRepository servicesRepository;

	@Override
	@Transactional
	public AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback) {
		AppointmentGeneralFeedback response = null;
		AppointmentGeneralFeedbackCollection appointmentGeneralFeedbackCollection = null;
		appointmentGeneralFeedbackCollection = appointmentGeneralFeedbackRepository
				.findById(new ObjectId(feedback.getId())).orElse(null);
		if (appointmentGeneralFeedbackCollection == null) {
			feedback.setCreatedTime(new Date());
			appointmentGeneralFeedbackCollection = new AppointmentGeneralFeedbackCollection();
		}
		if (feedback.getExperienceWithDoctor() != null) {
			feedback.setAdminUpdatedExperienceWithDoctor(feedback.getExperienceWithDoctor());
		}
		BeanUtil.map(feedback, appointmentGeneralFeedbackCollection);
		appointmentGeneralFeedbackCollection = appointmentGeneralFeedbackRepository
				.save(appointmentGeneralFeedbackCollection);
		if (appointmentGeneralFeedbackCollection != null) {
			BeanUtil.map(appointmentGeneralFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedbackRequest feedback) {
		PrescriptionFeedback response = null;
		PrescriptionFeedbackCollection prescriptionFeedbackCollection = new PrescriptionFeedbackCollection();

		BeanUtil.map(feedback, prescriptionFeedbackCollection);
		prescriptionFeedbackCollection.setCreatedTime(new Date());
		prescriptionFeedbackCollection = prescritptionFeedbackRepository.save(prescriptionFeedbackCollection);
		if (prescriptionFeedbackCollection != null) {
			response = new PrescriptionFeedback();
			BeanUtil.map(prescriptionFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public PharmacyFeedback addEditPharmacyFeedback(PharmacyFeedbackRequest feedback) {
		PharmacyFeedback response = null;
		PharmacyFeedbackCollection pharmacyFeedbackCollection = new PharmacyFeedbackCollection();

		BeanUtil.map(feedback, pharmacyFeedbackCollection);
		pharmacyFeedbackCollection.setCreatedTime(new Date());

		if (feedback.getExperienceWithPharmacy() != null) {
			pharmacyFeedbackCollection.setAdminUpdatedExperienceWithPharmacy(feedback.getExperienceWithPharmacy());
		}

		pharmacyFeedbackCollection = pharmacyFeedbackRepository.save(pharmacyFeedbackCollection);
		response = new PharmacyFeedback();
		if (pharmacyFeedbackCollection != null) {
			BeanUtil.map(pharmacyFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request) {
		List<AppointmentGeneralFeedback> appointmentGeneralFeedbacks = null;

		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				criteria.and("hospitalId").is(new ObjectId(request.getHospitalId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));

			if (request.getSize() > 0)
				appointmentGeneralFeedbacks = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.skip(request.getPage() * request.getSize()),
										Aggregation.limit(request.getSize()),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								AppointmentGeneralFeedbackCollection.class, AppointmentGeneralFeedback.class)
						.getMappedResults();
			else
				appointmentGeneralFeedbacks = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								AppointmentGeneralFeedbackCollection.class, AppointmentGeneralFeedback.class)
						.getMappedResults();

			/*
			 * if (otReportsLookupResponses != null) { response = new
			 * ArrayList<OTReports>(); for (OTReportsLookupResponse collection :
			 * otReportsLookupResponses) { OTReports otReports = new OTReports();
			 * BeanUtil.map(collection, otReports); if (collection.getDoctorId() != null) {
			 * UserCollection doctor = collection.getDoctor(); if (doctor != null)
			 * otReports.setDoctorName(doctor.getFirstName()); } LocationCollection
			 * locationCollection = collection.getLocation(); if (locationCollection !=
			 * null) { otReports.setLocationName(locationCollection.getLocationName());
			 * 
			 * } HospitalCollection hospitalCollection = collection.getHospital(); if
			 * (hospitalCollection != null) {
			 * otReports.setHospitalName(hospitalCollection.getHospitalName()); } if
			 * (collection.getPatientId() != null) { PatientCollection patientCollection =
			 * patientRepository.findByUserIdLocationIdAndHospitalId( new
			 * ObjectId(collection.getPatientId()), new
			 * ObjectId(collection.getLocationId()), new
			 * ObjectId(collection.getHospitalId())); if (patientCollection != null) {
			 * Patient patient = new Patient(); BeanUtil.map(patientCollection, patient);
			 * otReports.setPatient(patient); } } if (collection.getSurgery().getEndTime()
			 * != null && collection.getSurgery().getStartTime() != null) { Long diff =
			 * collection.getSurgery().getStartTime() -
			 * collection.getSurgery().getEndTime(); TimeDuration timeDuration = new
			 * TimeDuration();
			 * 
			 * Long diffSeconds = diff / 1000 % 60; Long diffMinutes = diff / (60 * 1000) %
			 * 60; Long diffHours = diff / (60 * 60 * 1000); Integer diffInDays = (int)
			 * ((collection.getSurgery().getEndTime() -
			 * collection.getSurgery().getStartTime()) / (1000 * 60 * 60 * 24));
			 * 
			 * timeDuration.setSeconds(diffSeconds.intValue());
			 * timeDuration.setMinutes(diffMinutes.intValue());
			 * timeDuration.setHours(diffHours.intValue());
			 * timeDuration.setDays(diffInDays);
			 * 
			 * otReports.setTimeDuration(timeDuration); }
			 * 
			 * response.add(otReports); } } int count =
			 * otReportsRepository.getReportsCount(new ObjectId(locationId), new
			 * ObjectId(doctorId), new ObjectId(patientId)); otReportsResponse = new
			 * OTReportsResponse(); otReportsResponse.setOtReports(response);
			 * otReportsResponse.setCount(count);
			 */
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return appointmentGeneralFeedbacks;
	}

	@Override
	@Transactional
	public List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request) {
		List<PrescriptionFeedback> prescriptionFeedbacks = null;

		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				criteria.and("hospitalId").is(new ObjectId(request.getHospitalId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));

			if (request.getSize() > 0)
				prescriptionFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PrescriptionFeedbackCollection.class, PrescriptionFeedback.class).getMappedResults();
			else
				prescriptionFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PrescriptionFeedbackCollection.class, PrescriptionFeedback.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return prescriptionFeedbacks;
	}

	@Override
	@Transactional
	public List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request) {
		List<PharmacyFeedback> pharmacyFeedbacks = null;

		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				criteria.and("localeId").is(new ObjectId(request.getLocationId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));

			if (request.getSize() > 0)
				pharmacyFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), PharmacyFeedbackCollection.class,
						PharmacyFeedback.class).getMappedResults();
			else
				pharmacyFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PharmacyFeedbackCollection.class, PharmacyFeedback.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pharmacyFeedbacks;
	}

	private Integer getDurationDays(List<Duration> durations) {
		Integer maxDays = 0;

		for (Duration duration : durations) {
			Integer days = 0;
			if (duration.getDurationUnit().getUnit().equals(DurationUnitEnum.DAY.getDurationUnit())) {
				days = Integer.parseInt(duration.getValue());
			} else if (duration.getDurationUnit().getUnit().equals(DurationUnitEnum.WEEK.getDurationUnit())) {
				days = Integer.parseInt(duration.getValue()) * 7;
			} else if (duration.getDurationUnit().getUnit().equals(DurationUnitEnum.MONTH.getDurationUnit())) {
				days = Integer.parseInt(duration.getValue()) * 30;
			}

			if (days > maxDays) {
				maxDays = days;
			}
		}
		return maxDays;
	}

	@Override
	@Transactional
	public DailyImprovementFeedback addEditDailyImprovementFeedback(DailyImprovementFeedbackRequest feedback) {
		DailyImprovementFeedback response = null;

		DailyImprovementFeedbackCollection dailyImprovementFeedbackCollection = null;

		dailyImprovementFeedbackCollection = dailyImprovementFeedbackRepository
				.findByPrescriptionId(new ObjectId(feedback.getPrescriptionId()));

		if (dailyImprovementFeedbackCollection == null) {
			dailyImprovementFeedbackCollection = new DailyImprovementFeedbackCollection();
			BeanUtil.map(feedback, dailyImprovementFeedbackCollection);
			dailyImprovementFeedbackCollection.setCreatedTime(new Date());
		}

		if (dailyImprovementFeedbackCollection.getDailyPatientFeedbacks() == null) {
			List<DailyPatientFeedback> dailyPatientFeedbacks = new ArrayList<>();
			dailyImprovementFeedbackCollection.setDailyPatientFeedbacks(dailyPatientFeedbacks);
		}
		dailyImprovementFeedbackCollection.getDailyPatientFeedbacks().add(feedback.getDailyPatientFeedback());

		dailyImprovementFeedbackCollection = dailyImprovementFeedbackRepository
				.save(dailyImprovementFeedbackCollection);
		response = new DailyImprovementFeedback();
		if (dailyImprovementFeedbackCollection != null) {
			BeanUtil.map(dailyImprovementFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DailyImprovementFeedbackResponse> getDailyImprovementFeedbackList(String prescriptionId , String doctorId,
		     String locationId, String hospitalId, long page , int size) {
		List<DailyImprovementFeedbackResponse> dailyImprovementFeedbacks = null;
		LocationCollection locationCollection = null;
		HospitalCollection hospitalCollection = null;
		UserCollection userCollection = null;

		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(prescriptionId)) {
				criteria.and("prescriptionId").is(new ObjectId(prescriptionId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
				userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			}

			criteria.and("discarded").is(false);

			if (size > 0)
				dailyImprovementFeedbacks = mongoTemplate
						.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
								Aggregation.unwind("patientCard"),
								Aggregation.lookup("prescription_cl", "prescriptionId", "_id", "prescription"),
								Aggregation.unwind("prescription"), Aggregation.skip(page * size),
								Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								DailyImprovementFeedbackCollection.class, DailyImprovementFeedbackResponse.class)
						.getMappedResults();
			else
				dailyImprovementFeedbacks = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteria),
										Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
										Aggregation.unwind("patientCard"),
										Aggregation.lookup("prescription_cl", "prescriptionId", "_id", "prescription"),
										Aggregation.unwind("prescription"),
										Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
								DailyImprovementFeedbackCollection.class, DailyImprovementFeedbackResponse.class)
						.getMappedResults();

			for (DailyImprovementFeedbackResponse dailyImprovementFeedbackResponse : dailyImprovementFeedbacks) {

				if (locationCollection != null) {
					dailyImprovementFeedbackResponse.setLocationName(locationCollection.getLocationName());
				}
				if (userCollection != null) {
					dailyImprovementFeedbackResponse.setDoctorName(userCollection.getFirstName());
				}
				if (hospitalCollection != null) {
					dailyImprovementFeedbackResponse.setHospitalName(hospitalCollection.getHospitalName());
				}
				if (dailyImprovementFeedbackResponse.getPrescription() != null) {
					dailyImprovementFeedbackResponse
							.setUniqueEmrId(dailyImprovementFeedbackResponse.getPrescription().getUniqueEmrId());
					dailyImprovementFeedbackResponse.setPrescription(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return dailyImprovementFeedbacks;
	}

	@Override
	@Transactional
	public PatientFeedback addEditPatientFeedback(PatientFeedbackRequest feedback) {
		PatientFeedback response = null;
		PatientFeedbackCollection patientFeedbackCollection = new PatientFeedbackCollection();

		BeanUtil.map(feedback, patientFeedbackCollection);
		patientFeedbackCollection.setCreatedTime(new Date());

		if (feedback.getExperience() != null) {
			patientFeedbackCollection.setAdminUpdatedExperience(feedback.getExperience());
		}
		if (feedback.getServices() != null && !feedback.getServices().isEmpty()) {
			List<ServicesCollection> servicesCollections = servicesRepository.findByServiceIn(feedback.getServices());
			@SuppressWarnings("unchecked")
			Set<ObjectId> serviceIds = (Set<ObjectId>) CollectionUtils.collect(servicesCollections,
					new BeanToPropertyValueTransformer("id"));
			if (serviceIds != null && !serviceIds.isEmpty()) {
				patientFeedbackCollection.setServices(serviceIds);
			} else {
				patientFeedbackCollection.setServices(null);
			}
		} else {
			patientFeedbackCollection.setServices(null);
		}

		patientFeedbackCollection = patientFeedbackRepository.save(patientFeedbackCollection);
		response = new PatientFeedback();
		if (patientFeedbackCollection != null) {
			BeanUtil.map(patientFeedbackCollection, response);
			response.setServices(response.getServices());
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientFeedbackResponse> getPatientFeedbackList(FeedbackGetRequest request, String type) {
		List<PatientFeedbackResponse> feedbackResponses = null;
		LocaleCollection localeCollection = null;
		LocationCollection locationCollection = null;
		HospitalCollection hospitalCollection = null;
		UserCollection userCollection = null;
		PatientCollection patientCollection = null;
		Aggregation aggregation = null;
		int count = 1;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("feedbackType").is(type);
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getLocaleId())) {
				criteria.and("localeId").is(new ObjectId(request.getLocaleId()));
				localeCollection = localeRepository.findById(new ObjectId(request.getLocaleId())).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId())) {
				criteria.and("hospitalId").is(new ObjectId(request.getHospitalId()));
				hospitalCollection = hospitalRepository.findById(new ObjectId(request.getHospitalId())).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));
				locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));
				patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						new ObjectId(request.getPatientId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()));
			}

			// criteria.and("discarded").is(false);
			// criteria.and("isApproved").is(true);

			if (request.getSize() > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("prescription_cl", "prescriptionId", "_id", "prescription"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$prescription").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("appointment_cl", "appointmentId", "_id", "appointment"),

						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointment").append("preserveNullAndEmptyArrays", true))),Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			else {
				aggregation = Aggregation.newAggregation(
				Aggregation.lookup("prescription_cl", "prescriptionId", "_id", "prescription"),
				new CustomAggregationOperation(new Document("$unwind",
						new BasicDBObject("path", "$prescription").append("preserveNullAndEmptyArrays", true))),
				Aggregation.lookup("appointment_cl", "appointmentId", "_id", "appointment"),
				new CustomAggregationOperation(new Document("$unwind",
						new BasicDBObject("path", "$appointment").append("preserveNullAndEmptyArrays", true))),Aggregation.match(criteria),
				Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			
			feedbackResponses = mongoTemplate.aggregate(aggregation, PatientFeedbackCollection.class, PatientFeedbackResponse.class).getMappedResults();
				
		
			
			for (PatientFeedbackResponse patientFeedbackResponse : feedbackResponses) {
				if (localeCollection != null) {
					patientFeedbackResponse.setLocaleName(localeCollection.getLocaleName());
				}
				if (locationCollection != null) {
					patientFeedbackResponse.setLocationName(locationCollection.getLocationName());
				}
				if (userCollection != null) {
					patientFeedbackResponse.setDoctorName(userCollection.getFirstName());
				}
				if (hospitalCollection != null) {
					patientFeedbackResponse.setHospitalName(hospitalCollection.getHospitalName());
				}
				if (patientCollection != null) {
					PatientShortCard patientCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientCard);
					patientFeedbackResponse.setPatientCard(patientCard);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}

	@Override
	@Transactional
	public List<PatientFeedbackIOSResponse> getPatientFeedbackList(int size, int page, String patientId,
			String doctorId, String localeId, String locationId, String hospitalId, String type, List<String> services,
			Boolean discarded, Boolean isApproved) {
		List<PatientFeedbackIOSResponse> feedbackResponses = null;
		Aggregation aggregation = null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("feedbackType").is(type);
			}
			if (!DPDoctorUtils.anyStringEmpty(localeId)) {
				criteria.and("localeId").is(new ObjectId(localeId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (services != null && !services.isEmpty()) {
				criteria.and("services.service").in(services);
			}
			if (discarded != null) {
				criteria.and("isDiscarded").is(discarded);
			}
			if (isApproved != null) {
				criteria.and("isApproved").is(isApproved);
			}
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("localeId", new BasicDBObject("$first", "$localeId"))
							.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
							.append("prescriptionId", new BasicDBObject("$first", "$prescriptionId"))
							.append("isRecommended", new BasicDBObject("$first", "$isRecommended"))
							.append("isAppointmentStartedOnTime",
									new BasicDBObject("$first", "$isAppointmentStartedOnTime"))
							.append("howLateWasAppointmentInMinutes",
									new BasicDBObject("$first", "$howLateWasAppointmentInMinutes"))
							.append("overallExperience", new BasicDBObject("$first", "$overallExperience"))
							.append("isDiscarded", new BasicDBObject("$first", "$isDiscarded"))
							.append("isMedicationOnTime", new BasicDBObject("$first", "$isMedicationOnTime"))
							.append("questionAnswers", new BasicDBObject("$push", "$questionAnswers"))
							.append("medicationEffectType", new BasicDBObject("$first", "$medicationEffectType"))
							.append("reply", new BasicDBObject("$first", "$reply"))
							.append("experience", new BasicDBObject("$first", "$experience"))
							.append("feedbackType", new BasicDBObject("$first", "$feedbackType"))
							.append("services", new BasicDBObject("$push", "$services.service"))
							.append("appointmentTiming", new BasicDBObject("$first", "$appointmentTiming"))
							.append("printPdfProvided", new BasicDBObject("$first", "$printPdfProvided"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("services_cl", "services", "_id", "services"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), aggregationOperation, Aggregation.skip((long)page * size),
								Aggregation.limit(size), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			else {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("services_cl", "services", "_id", "services"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), aggregationOperation,
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			feedbackResponses = mongoTemplate
					.aggregate(aggregation, PatientFeedbackCollection.class, PatientFeedbackIOSResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}

	@Override
	@Transactional
	public Integer countPatientFeedbackList(String patientId, String doctorId, String localeId, String locationId,
			String hospitalId, String type, List<String> services, Boolean discarded, Boolean isApproved) {
		Integer feedbackResponses = null;
		Aggregation aggregation = null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("feedbackType").is(type);
			}
			if (!DPDoctorUtils.anyStringEmpty(localeId)) {
				criteria.and("localeId").is(new ObjectId(localeId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (services != null && !services.isEmpty()) {
				criteria.and("services.service").in(services);
			}
			if (discarded != null) {
				criteria.and("isDiscarded").is(discarded);
			}
			if (isApproved != null) {
				criteria.and("isApproved").is(isApproved);
			}
			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(
					new Document("$group", new BasicDBObject("_id", "$_id")));

			aggregation = Aggregation.newAggregation(
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("services_cl", "services", "_id", "services"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$services").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria), aggregationOperation);

			feedbackResponses = mongoTemplate
					.aggregate(aggregation, PatientFeedbackCollection.class, PatientFeedbackIOSResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}

	@Override
	@Transactional
	public PatientFeedbackResponse addPatientFeedbackReply(PatientFeedbackReplyRequest request) {
		PatientFeedbackResponse patientFeedbackResponse = null;
		LocaleCollection localeCollection = null;
		LocationCollection locationCollection = null;
		HospitalCollection hospitalCollection = null;
		UserCollection userCollection = null;
		List<PatientCollection> patientCollection = null;
		try {

			PatientFeedbackCollection patientFeedbackCollection = patientFeedbackRepository
					.findById(new ObjectId(request.getId())).orElse(null);

			if (patientFeedbackCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			if (patientFeedbackCollection.getLocaleId() != null)
				localeCollection = localeRepository.findById(patientFeedbackCollection.getDoctorId()).orElse(null);

			if (patientFeedbackCollection.getHospitalId() != null)
				hospitalCollection = hospitalRepository.findById(patientFeedbackCollection.getHospitalId()).orElse(null);

			if (patientFeedbackCollection.getDoctorId() != null)
				userCollection = userRepository.findById(patientFeedbackCollection.getDoctorId()).orElse(null);

			if (patientFeedbackCollection.getLocationId() != null)
				locationCollection = locationRepository.findById(patientFeedbackCollection.getLocationId()).orElse(null);

			if (patientFeedbackCollection.getPatientId() != null)
				patientCollection = patientRepository.findByUserId(patientFeedbackCollection.getPatientId());
			patientFeedbackCollection.setReply(request.getReply());
			patientFeedbackCollection = patientFeedbackRepository.save(patientFeedbackCollection);
			patientFeedbackResponse = new PatientFeedbackResponse();
			BeanUtil.map(patientFeedbackCollection, patientFeedbackResponse);
			if (localeCollection != null) {
				patientFeedbackResponse.setLocaleName(localeCollection.getLocaleName());
			}
			if (locationCollection != null) {
				patientFeedbackResponse.setLocationName(locationCollection.getLocationName());
			}
			if (userCollection != null) {
				patientFeedbackResponse.setDoctorName(userCollection.getFirstName());
			}
			if (hospitalCollection != null) {
				patientFeedbackResponse.setHospitalName(hospitalCollection.getHospitalName());
			}

			if (patientCollection != null && patientCollection.size() > 0) {
				PatientShortCard patientShortCard = new PatientShortCard();
				BeanUtil.map(patientCollection.get(0), patientShortCard);
				patientFeedbackResponse.setPatientCard(patientShortCard);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return patientFeedbackResponse;
	}

	private List<Integer> getDaysForNotification(Integer maxDays) {
		List<Integer> notificationDays = null;

		if (maxDays <= 1) {
			return notificationDays;
		} else if (isBetween(maxDays, 2, 5)) {
			Double pivot = maxDays.doubleValue() / 2;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}

		else if (isBetween(maxDays, 6, 10)) {
			Double pivot = maxDays.doubleValue() / 3;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}

		else if (isBetween(maxDays, 11, 30)) {
			Double pivot = maxDays.doubleValue() / 4;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}

		else if (isBetween(maxDays, 31, 100)) {
			Double pivot = maxDays.doubleValue() / 4;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}

		else {
			Double pivot = maxDays.doubleValue() / 7;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}

		return notificationDays;
	}

	private boolean isBetween(Integer value, Integer lower, Integer upper) {
		return lower <= value && value <= upper;
	}

	private List<Integer> calcNotificationDays(Integer maxDays, Double maxN) {
		List<Integer> notificationDays = new ArrayList<>();
		Double dayDiff = Math.ceil((maxDays - 2) / maxN);
		Integer day = 2;
		notificationDays.add(day);
		while (day <= maxDays) {
			day = day + dayDiff.intValue();
			notificationDays.add(day);
		}
		return notificationDays;
	}

}
