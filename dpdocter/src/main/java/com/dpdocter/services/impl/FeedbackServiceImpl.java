package com.dpdocter.services.impl;

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
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.collections.AppointmentGeneralFeedbackCollection;
import com.dpdocter.collections.PharmacyFeedbackCollection;
import com.dpdocter.collections.PrescriptionFeedbackCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentGeneralFeedbackRepository;
import com.dpdocter.repository.PharmacyFeedbackRepository;
import com.dpdocter.repository.PrescritptionFeedbackRepository;
import com.dpdocter.request.FeedbackGetRequest;
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
	private MongoTemplate mongoTemplate;

	@Override
	@Transactional
	public AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback) {
		AppointmentGeneralFeedback response = null;
		AppointmentGeneralFeedbackCollection appointmentGeneralFeedbackCollection = null;
		appointmentGeneralFeedbackCollection = appointmentGeneralFeedbackRepository
				.findOne(new ObjectId(feedback.getId()));
		if (appointmentGeneralFeedbackCollection == null) {
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
	public PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedback feedback) {
		PrescriptionFeedback response = null;
		PrescriptionFeedbackCollection prescriptionFeedbackCollection = null;
		prescriptionFeedbackCollection = prescritptionFeedbackRepository.findOne(new ObjectId(feedback.getId()));
		if (prescriptionFeedbackCollection == null) {
			prescriptionFeedbackCollection = new PrescriptionFeedbackCollection();
		}
		BeanUtil.map(feedback, prescriptionFeedbackCollection);
		prescriptionFeedbackCollection = prescritptionFeedbackRepository.save(prescriptionFeedbackCollection);
		if (prescriptionFeedbackCollection != null) {
			BeanUtil.map(prescriptionFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public PharmacyFeedback addEditPharmacyFeedback(PharmacyFeedback feedback)
	{
		PharmacyFeedback response = null;
		PharmacyFeedbackCollection pharmacyFeedbackCollection = null;
		pharmacyFeedbackCollection = pharmacyFeedbackRepository.findOne(new ObjectId(feedback.getId()));
		if(pharmacyFeedbackCollection == null)
		{
			pharmacyFeedbackCollection = new PharmacyFeedbackCollection();
		}
		if(feedback.getExperienceWithPharmacy() != null)
		{
			feedback.setAdminUpdatedExperienceWithPharmacy(feedback.getExperienceWithPharmacy());
		}
		BeanUtil.map(feedback, pharmacyFeedbackCollection);
		pharmacyFeedbackCollection = pharmacyFeedbackRepository.save(pharmacyFeedbackCollection);
		if(pharmacyFeedbackCollection != null)
		{
			BeanUtil.map(pharmacyFeedbackCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request )
	{
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
				appointmentGeneralFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								 Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						AppointmentGeneralFeedbackCollection.class, AppointmentGeneralFeedback.class).getMappedResults();
			else
				appointmentGeneralFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						AppointmentGeneralFeedbackCollection.class, AppointmentGeneralFeedback.class).getMappedResults();

			/*if (otReportsLookupResponses != null) {
				response = new ArrayList<OTReports>();
				for (OTReportsLookupResponse collection : otReportsLookupResponses) {
					OTReports otReports = new OTReports();
					BeanUtil.map(collection, otReports);
					if (collection.getDoctorId() != null) {
						UserCollection doctor = collection.getDoctor();
						if (doctor != null)
							otReports.setDoctorName(doctor.getFirstName());
					}
					LocationCollection locationCollection = collection.getLocation();
					if (locationCollection != null) {
						otReports.setLocationName(locationCollection.getLocationName());

					}
					HospitalCollection hospitalCollection = collection.getHospital();
					if (hospitalCollection != null) {
						otReports.setHospitalName(hospitalCollection.getHospitalName());
					}
					if (collection.getPatientId() != null) {
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								new ObjectId(collection.getPatientId()), new ObjectId(collection.getLocationId()),
								new ObjectId(collection.getHospitalId()));
						if (patientCollection != null) {
							Patient patient = new Patient();
							BeanUtil.map(patientCollection, patient);
							otReports.setPatient(patient);
						}
					}
					if (collection.getSurgery().getEndTime() != null
							&& collection.getSurgery().getStartTime() != null) {
						Long diff = collection.getSurgery().getStartTime() - collection.getSurgery().getEndTime();
						TimeDuration timeDuration = new TimeDuration();

						Long diffSeconds = diff / 1000 % 60;
						Long diffMinutes = diff / (60 * 1000) % 60;
						Long diffHours = diff / (60 * 60 * 1000);
						Integer diffInDays = (int) ((collection.getSurgery().getEndTime()
								- collection.getSurgery().getStartTime()) / (1000 * 60 * 60 * 24));

						timeDuration.setSeconds(diffSeconds.intValue());
						timeDuration.setMinutes(diffMinutes.intValue());
						timeDuration.setHours(diffHours.intValue());
						timeDuration.setDays(diffInDays);

						otReports.setTimeDuration(timeDuration);
					}

					response.add(otReports);
				}
			}
			int count = otReportsRepository.getReportsCount(new ObjectId(locationId), new ObjectId(doctorId),
					new ObjectId(patientId));
			otReportsResponse = new OTReportsResponse();
			otReportsResponse.setOtReports(response);
			otReportsResponse.setCount(count);*/
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return appointmentGeneralFeedbacks;
	}
	
	@Override
	@Transactional
	public List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request )
	{
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
				prescriptionFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								 Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PrescriptionFeedbackCollection.class, PrescriptionFeedback.class).getMappedResults();
			else
				prescriptionFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PrescriptionFeedbackCollection.class, PrescriptionFeedback.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return prescriptionFeedbacks;
	}
	
	@Override
	@Transactional
	public List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request )
	{
		List<PharmacyFeedback> pharmacyFeedbacks = null;
		
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
				pharmacyFeedbacks = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								 Aggregation.skip(request.getPage() * request.getSize()), Aggregation.limit(request.getSize()),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PharmacyFeedbackCollection.class, PharmacyFeedback.class).getMappedResults();
			else
				pharmacyFeedbacks = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
						PharmacyFeedbackCollection.class, PharmacyFeedback.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pharmacyFeedbacks;
	}


	
}
