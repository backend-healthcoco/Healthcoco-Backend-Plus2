package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.dpdocter.beans.DailyImprovementFeedback;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.PatientFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.collections.AppointmentGeneralFeedbackCollection;
import com.dpdocter.collections.DailyImprovementFeedbackCollection;
import com.dpdocter.collections.PatientFeedbackCollection;
import com.dpdocter.collections.PharmacyFeedbackCollection;
import com.dpdocter.collections.PrescriptionFeedbackCollection;
import com.dpdocter.enums.DurationUnitEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentGeneralFeedbackRepository;
import com.dpdocter.repository.DailyImprovementFeedbackRepository;
import com.dpdocter.repository.PatientFeedbackRepository;
import com.dpdocter.repository.PharmacyFeedbackRepository;
import com.dpdocter.repository.PrescritptionFeedbackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientFeedbackRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.request.PharmacyFeedbackRequest;
import com.dpdocter.services.FeedbackService;

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

	@Override
	@Transactional
	public AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback) {
		AppointmentGeneralFeedback response = null;
		AppointmentGeneralFeedbackCollection appointmentGeneralFeedbackCollection = null;
		appointmentGeneralFeedbackCollection = appointmentGeneralFeedbackRepository
				.findOne(new ObjectId(feedback.getId()));
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
			 * otReportsLookupResponses) { OTReports otReports = new
			 * OTReports(); BeanUtil.map(collection, otReports); if
			 * (collection.getDoctorId() != null) { UserCollection doctor =
			 * collection.getDoctor(); if (doctor != null)
			 * otReports.setDoctorName(doctor.getFirstName()); }
			 * LocationCollection locationCollection = collection.getLocation();
			 * if (locationCollection != null) {
			 * otReports.setLocationName(locationCollection.getLocationName());
			 * 
			 * } HospitalCollection hospitalCollection =
			 * collection.getHospital(); if (hospitalCollection != null) {
			 * otReports.setHospitalName(hospitalCollection.getHospitalName());
			 * } if (collection.getPatientId() != null) { PatientCollection
			 * patientCollection =
			 * patientRepository.findByUserIdLocationIdAndHospitalId( new
			 * ObjectId(collection.getPatientId()), new
			 * ObjectId(collection.getLocationId()), new
			 * ObjectId(collection.getHospitalId())); if (patientCollection !=
			 * null) { Patient patient = new Patient();
			 * BeanUtil.map(patientCollection, patient);
			 * otReports.setPatient(patient); } } if
			 * (collection.getSurgery().getEndTime() != null &&
			 * collection.getSurgery().getStartTime() != null) { Long diff =
			 * collection.getSurgery().getStartTime() -
			 * collection.getSurgery().getEndTime(); TimeDuration timeDuration =
			 * new TimeDuration();
			 * 
			 * Long diffSeconds = diff / 1000 % 60; Long diffMinutes = diff /
			 * (60 * 1000) % 60; Long diffHours = diff / (60 * 60 * 1000);
			 * Integer diffInDays = (int) ((collection.getSurgery().getEndTime()
			 * - collection.getSurgery().getStartTime()) / (1000 * 60 * 60 *
			 * 24));
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
			 * ObjectId(doctorId), new ObjectId(patientId)); otReportsResponse =
			 * new OTReportsResponse();
			 * otReportsResponse.setOtReports(response);
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
	
	private Integer getDurationDays(List<Duration> durations)
	{
		Integer maxDays = 0;
		
		for(Duration duration : durations)
		{
			Integer days = 0;
			if(duration.getDurationUnit().getUnit().equals(DurationUnitEnum.DAY.getDurationUnit()))
			{
				days = Integer.parseInt(duration.getValue());
			}
			else if(duration.getDurationUnit().getUnit().equals(DurationUnitEnum.WEEK.getDurationUnit()))
			{
				days = Integer.parseInt(duration.getValue()) * 7;
			}
			else if(duration.getDurationUnit().getUnit().equals(DurationUnitEnum.MONTH.getDurationUnit()))
			{
				days = Integer.parseInt(duration.getValue()) * 30;
			}
			
			if(days > maxDays)
			{
				maxDays = days;
			}
		}
		return maxDays;
	}
	
	
	@Override
	@Transactional
	public DailyImprovementFeedback addEditDailyImprovementFeedback(DailyImprovementFeedback feedback) {
		DailyImprovementFeedback response = null;
		DailyImprovementFeedbackCollection dailyImprovementFeedbackCollection = new DailyImprovementFeedbackCollection();

		BeanUtil.map(feedback, dailyImprovementFeedbackCollection);
		dailyImprovementFeedbackCollection.setCreatedTime(new Date());

		dailyImprovementFeedbackCollection = dailyImprovementFeedbackRepository.save(dailyImprovementFeedbackCollection);
		response = new DailyImprovementFeedback();
		if (dailyImprovementFeedbackCollection != null) {
			BeanUtil.map(dailyImprovementFeedbackCollection, response);
		}
		return response;
	}

	
	@Override
	@Transactional
	public List<DailyImprovementFeedback> getDailyImprovementFeedbackList(String prescriptionId , int page , int size) {
		List<DailyImprovementFeedback> dailyImprovementFeedbacks = null;

		try {

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(prescriptionId))
			{
				criteria.and("prescriptionId").is(new ObjectId(prescriptionId));
			}
		

			if (size > 0)
				dailyImprovementFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(page * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), DailyImprovementFeedbackCollection.class,
						DailyImprovementFeedback.class).getMappedResults();
			else
				dailyImprovementFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						DailyImprovementFeedbackCollection.class, DailyImprovementFeedback.class).getMappedResults();
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

		patientFeedbackCollection = patientFeedbackRepository.save(patientFeedbackCollection);
		response = new PatientFeedback();
		if (patientFeedbackCollection != null) {
			BeanUtil.map(patientFeedbackCollection, response);
		}
		return response;
	}
	
	
	@Override
	@Transactional
	public List<PatientFeedbackResponse> getPatientFeedbackList(FeedbackGetRequest request , String type) {
		List<PatientFeedbackResponse> feedbackResponses= null;

		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(request.getLocaleId()))
				criteria.and("localeId").is(new ObjectId(request.getLocaleId()));
			
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				criteria.and("hospitalId").is(new ObjectId(request.getHospitalId()));
			
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));
			
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				criteria.and("locationId").is(new ObjectId(request.getLocationId()));

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				criteria.and("patientId").is(new ObjectId(request.getPatientId()));
			
			//criteria.and("discarded").is(false);
			criteria.and("isApproved").is(true);

			if (request.getSize() > 0)
				feedbackResponses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), PatientFeedbackCollection.class,
						PatientFeedbackResponse.class).getMappedResults();
			else
				feedbackResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PatientFeedbackCollection.class, PatientFeedbackResponse.class).getMappedResults();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return feedbackResponses;
	}
	
	
	private List<Integer> getDaysForNotification(Integer maxDays)
	{
		List<Integer> notificationDays = null;
		
		if (maxDays <= 1)
		{
			return notificationDays;
		}
		else if(isBetween(maxDays, 2, 5))
		{
			Double pivot = maxDays.doubleValue() / 2;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}
		
		else if(isBetween(maxDays, 6, 10))
		{
			Double pivot = maxDays.doubleValue() / 3;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}
		
		else if(isBetween(maxDays, 11, 30))
		{
			Double pivot = maxDays.doubleValue() / 4;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}
		
		else if(isBetween(maxDays, 31, 100))
		{
			Double pivot = maxDays.doubleValue() / 4;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}
		
		else 
		{
			Double pivot = maxDays.doubleValue() / 7;
			Double maxN = Math.ceil(pivot);
			notificationDays = calcNotificationDays(maxDays, maxN);
		}
		
		return notificationDays;
	}
	
	private boolean isBetween(Integer value, Integer lower, Integer upper) {
		  return lower <= value && value <= upper;
		}
	
	private List<Integer> calcNotificationDays(Integer maxDays , Double maxN)
	{
		List<Integer> notificationDays = new ArrayList<>();
		Double dayDiff =Math.ceil((maxDays - 2) / maxN);
		Integer day = 2;
		notificationDays.add(day);
		while(day <= maxDays)
		{
			day = day + dayDiff.intValue();
			notificationDays.add(day);
		}
		return notificationDays;
	}
	

}
