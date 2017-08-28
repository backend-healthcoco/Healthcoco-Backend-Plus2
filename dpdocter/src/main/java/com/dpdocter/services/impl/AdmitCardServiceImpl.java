package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
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

import com.dpdocter.beans.Patient;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdmitCardRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.response.AdmitCardResponse;
import com.dpdocter.services.AdmitCardService;
import com.dpdocter.services.FileManager;

import common.util.web.DPDoctorUtils;

@Service
public class AdmitCardServiceImpl implements AdmitCardService {

	private static Logger logger = Logger.getLogger(AdmitCardServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private AdmitCardRepository admitCardRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Override
	@Transactional
	public AdmitCardResponse addEditAdmitcard(AdmitCardRequest request) {
		AdmitCardResponse response = null;
		try {
			Patient patientdetail = new Patient();
			UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid DoctorId");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getDoctorId()),
					new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
			if (patientCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid patient");
			}
			BeanUtil.map(patientCollection, patientdetail);

			AdmitCardCollection admitCardCollection = new AdmitCardCollection();

			BeanUtil.map(request, admitCardCollection);
			if (admitCardCollection.getId() == null) {
				admitCardCollection.setCreatedTime(new Date());
				admitCardCollection.setCreatedBy(doctor.getTitle() + " " + doctor.getFirstName());
				admitCardCollection.setUniqueEmrId(
						UniqueIdInitial.ADMIT_CARD.getInitial() + "-" + DPDoctorUtils.generateRandomId());
			} else {
				AdmitCardCollection oldAdmitCardCollection = admitCardRepository.findOne(admitCardCollection.getId());
				admitCardCollection.setCreatedTime(oldAdmitCardCollection.getCreatedTime());
				admitCardCollection.setCreatedBy(oldAdmitCardCollection.getCreatedBy());
				admitCardCollection.setUniqueEmrId(oldAdmitCardCollection.getUniqueEmrId());
			}
			admitCardCollection = admitCardRepository.save(admitCardCollection);
			response = new AdmitCardResponse();
			BeanUtil.map(admitCardCollection, response);
			response.setPatient(patientdetail);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While add edit Admit card ");
		}
		return response;
	}

	@Override
	@Transactional
	public AdmitCardResponse getAdmitCard(String cardId) {
		AdmitCardResponse response = null;
		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findOne(new ObjectId(cardId));

			if (admitCardCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Id");
			}
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					admitCardCollection.getPatientId(), admitCardCollection.getDoctorId(),
					admitCardCollection.getLocationId(), admitCardCollection.getHospitalId());
			response = new AdmitCardResponse();
			BeanUtil.map(admitCardCollection, response);
			Patient patient = new Patient();
			BeanUtil.map(patientCollection, patient);
			response.setPatient(patient);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Admit card by Id ");
		}
		return response;

	}

	@Override
	@Transactional
	public List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, long updatedTime, Boolean discarded) {
		List<AdmitCardResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria = criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (updatedTime > 0) {
				criteria = criteria.and("createdTime").is(new Date(updatedTime));
			}
			if (discarded) {
				criteria = criteria.and("discarded").is(discarded);
			}

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<AdmitCardResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					AdmitCardCollection.class, AdmitCardResponse.class);
			response = aggregationResults.getMappedResults();

			PatientCollection patientCollection = null;
			Patient patient = null;

			for (AdmitCardResponse admitCardResponse : response) {
				patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						new ObjectId(admitCardResponse.getPatientId()), new ObjectId(admitCardResponse.getDoctorId()),
						new ObjectId(admitCardResponse.getLocationId()),
						new ObjectId(admitCardResponse.getHospitalId()));
				patient = new Patient();
				BeanUtil.map(patientCollection, patient);
				admitCardResponse.setPatient(patient);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Admit cards ");
		}
		return response;
	}

	@Override
	@Transactional
	public AdmitCardResponse deleteAdmitCard(String cardId, String doctorId, String hospitalId, String locationId,
			Boolean discarded) {

		AdmitCardResponse response = null;
		try {
			AdmitCardCollection admitCardCollection = admitCardRepository.findOne(new ObjectId(cardId));
			if (admitCardCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(admitCardCollection.getDoctorId(),
						admitCardCollection.getHospitalId(), admitCardCollection.getLocationId())) {
					if (admitCardCollection.getDoctorId().toString().equals(doctorId)
							&& admitCardCollection.getHospitalId().toString().equals(hospitalId)
							&& admitCardCollection.getLocationId().toString().equals(locationId)) {
						admitCardCollection.setDiscarded(discarded);
						admitCardCollection.setUpdatedTime(new Date());
						admitCardRepository.save(admitCardCollection);
						response = new AdmitCardResponse();
						BeanUtil.map(admitCardCollection, response);
						PatientCollection patientCollection = patientRepository
								.findByUserIdDoctorIdLocationIdAndHospitalId(admitCardCollection.getPatientId(),
										admitCardCollection.getDoctorId(), admitCardCollection.getLocationId(),
										admitCardCollection.getHospitalId());
						Patient patient = new Patient();
						BeanUtil.map(patientCollection, patient);
						response.setPatient(patient);

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Discharge Summary not found!");
				throw new BusinessException(ServiceError.NoRecord, "Admit card  not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

}
