package com.dpdocter.services.v2.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.v2.PatientTreatment;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.v2.PatientTreatmentResponse;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.v2.PatientTreatmentServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "PatientTreatmentServicesImplV2")
public class PatientTreatmentServicesImpl implements PatientTreatmentServices {
	private static Logger logger = Logger.getLogger(PatientTreatmentServicesImpl.class);

	@Autowired
	MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	PushNotificationServices pushNotificationServices;
	
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
					Aggregation.match(new Criteria("_id").in(treatmentId).and("isPatientDiscarded").is(false)),
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
	
}
