package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.MedicineTreatmentSheetCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.MedicineSheetRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.MedicineTreatmentSheetRequest;
import com.dpdocter.response.MedicineTreatmentSheetResponse;
import com.dpdocter.services.MedicineTreatmentService;

import common.util.web.DPDoctorUtils;

@Service
public class MedicineTreatmentServiceImpl implements MedicineTreatmentService {


	private static Logger logger = LogManager.getLogger(MedicineTreatmentServiceImpl.class.getName());

	
	@Autowired
	private MedicineSheetRepository medicineSheetRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public MedicineTreatmentSheetResponse addEditMedicinetreatmentSheet(MedicineTreatmentSheetRequest request) {
		MedicineTreatmentSheetResponse response = null;
		try {
			MedicineTreatmentSheetCollection medicineTreatmentSheetCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				medicineTreatmentSheetCollection = medicineSheetRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (medicineTreatmentSheetCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "Assessment Not found with Id");
				}
				request.setUpdatedTime(new Date());
				request.setCreatedBy(medicineTreatmentSheetCollection.getCreatedBy());
				medicineTreatmentSheetCollection.setMedicine(null);
				BeanUtil.map(request, medicineTreatmentSheetCollection);

			} else {
				medicineTreatmentSheetCollection = new MedicineTreatmentSheetCollection();
				BeanUtil.map(request, medicineTreatmentSheetCollection);
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (userCollection != null) {
					medicineTreatmentSheetCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
				medicineTreatmentSheetCollection.setUpdatedTime(new Date());
			}
			medicineTreatmentSheetCollection = medicineSheetRepository.save(medicineTreatmentSheetCollection);
			response = new MedicineTreatmentSheetResponse();

			BeanUtil.map(medicineTreatmentSheetCollection, response);
		} catch (Exception e) {
			e.printStackTrace();

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Boolean deleteMedicineSheet(String medicineSheetId, String doctorId, String hospitalId,
			String locationId, Boolean discarded) {
		Boolean response = false;
		try {
			MedicineTreatmentSheetCollection medicineTreatmentSheetCollection = medicineSheetRepository.findById(new ObjectId(medicineSheetId)).orElse(null);
			if (medicineTreatmentSheetCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(medicineTreatmentSheetCollection.getDoctorId(),
						medicineTreatmentSheetCollection.getHospitalId(), medicineTreatmentSheetCollection.getLocationId())) {
					if (medicineTreatmentSheetCollection.getDoctorId().toString().equals(doctorId)
							&& medicineTreatmentSheetCollection.getHospitalId().toString().equals(hospitalId)
							&& medicineTreatmentSheetCollection.getLocationId().toString().equals(locationId)) {
						medicineTreatmentSheetCollection.setDiscarded(discarded);
						medicineTreatmentSheetCollection.setUpdatedTime(new Date());
						medicineSheetRepository.save(medicineTreatmentSheetCollection);
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
	public MedicineTreatmentSheetResponse getMedicineSheetById(String id) {
		MedicineTreatmentSheetResponse response = null;
		try {
			MedicineTreatmentSheetCollection medicineTreatmentSheetCollection = medicineSheetRepository.findById(new ObjectId(id)).orElse(null);
			if (medicineTreatmentSheetCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Error no such id");
			}
			response = new MedicineTreatmentSheetResponse();
			BeanUtil.map(medicineTreatmentSheetCollection, response);

		} catch (BusinessException e) {
			logger.error("Error while searching the id " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while searching the id");
		}

		return response;
	}

	@Override
	public List<MedicineTreatmentSheetResponse> getMedicineSheet(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, Boolean discarded) {
		List<MedicineTreatmentSheetResponse> response = null;
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
			response = mongoTemplate.aggregate(aggregation, MedicineTreatmentSheetCollection.class, MedicineTreatmentSheetResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting assessment " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting assessment " + e.getMessage());

		}
		return response;
	}

}
