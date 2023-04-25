package com.dpdocter.services.impl;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Records;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.LabService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class LabServiceImpl implements LabService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<Clinic> getClinicWithReportCount(String doctorId, String locationId, String hospitalId) {
		List<Clinic> response = null;
		try {
			Criteria criteria = new Criteria("location.isClinic").is(true).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
					.and("records.locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("records.doctorId")
						.is(new ObjectId(doctorId));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("hospital_cl", "prescribedByHospitalId", "_id", "hospital"),
					Aggregation.unwind("hospital"),
					Aggregation.lookup("location_cl", "prescribedByLocationId", "_id", "location"),
					Aggregation.unwind("location"),
					Aggregation.lookup("records_cl", "prescribedByLocationId", "prescribedByLocationId", "records"),
					Aggregation.unwind("records"), Aggregation.lookup("user_cl", "prescribedByDoctorId", "_id", "user"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
							.append("location", new BasicDBObject("$first", "$location"))
							.append("hospital", new BasicDBObject("$first", "$hospital"))
							.append("records", new BasicDBObject("$push", "$records"))
							.append("prescribedByDoctorId", new BasicDBObject("$first", "$prescribedByDoctorId"))
							.append("prescribedByLocationId", new BasicDBObject("$first", "$prescribedByLocationId"))
							.append("user", new BasicDBObject("$first", "$user")))),
					Aggregation.unwind("user"),

					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("id1", "$prescribedByLocationId").append("id2",
											"$prescribedByDoctorId"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("hospital", new BasicDBObject("$first", "$hospital"))
									.append("records", new BasicDBObject("$first", "$records"))
									.append("users", new BasicDBObject("$push", "$user"))
									.append("user", new BasicDBObject("$first", "$user")))),
					new CustomAggregationOperation(
							new Document("$project",
									new BasicDBObject("location", "$location").append("hospital", "$hospital")
											.append("reportCount", new BasicDBObject("$size", "$records"))
											.append("user", new BasicDBObject("firstName", "$user.firstName")
													.append("id", "$user._id").append("title", "$user.title")
													.append("reportCount", new BasicDBObject("$size", "$users"))))),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id.id1")
									.append("location", new BasicDBObject("$first", "$location"))
									.append("hospital", new BasicDBObject("$first", "$hospital"))
									.append("doctors", new BasicDBObject("$push", "$user"))
									.append("reportCount", new BasicDBObject("$first", "$reportCount")))));
			response = mongoTemplate.aggregate(aggregation, RecordsCollection.class, Clinic.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<Records> getReports(String doctorId, String locationId, String hospitalId, String prescribedByDoctorId,
			String prescribedByLocationId, String prescribedByHospitalId, int size, long page) {
		List<Records> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(prescribedByDoctorId))
				criteria = criteria.and("prescribedByDoctorId").is(new ObjectId(prescribedByDoctorId));
			if (!DPDoctorUtils.anyStringEmpty(prescribedByLocationId))
				criteria = criteria.and("prescribedByLocationId").is(new ObjectId(prescribedByLocationId));
			if (!DPDoctorUtils.anyStringEmpty(prescribedByHospitalId))
				criteria = criteria.and("prescribedByHospitalId").is(new ObjectId(prescribedByHospitalId));

			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			response = mongoTemplate.aggregate(aggregation, RecordsCollection.class, Records.class).getMappedResults();

		} catch (Exception e) {

		}
		return response;
	}

	@Override
	public List<Clinic> getLabWithReportCount(String doctorId, String locationId, String hospitalId) {
		List<Clinic> response = null;
		try {
			Criteria criteria = new Criteria("location.isLab").is(true).and("prescribedByLocationId")
					.is(new ObjectId(locationId)).and("prescribedByHospitalId").is(new ObjectId(hospitalId))
					.and("records.prescribedByLocationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = new Criteria("prescribedByDoctorId").is(new ObjectId(doctorId))
						.and("records.prescribedByDoctorId").is(new ObjectId(doctorId));
			}
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.lookup("hospital_cl", "hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital"),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("records_cl", "locationId", "locationId", "records"),
							Aggregation.unwind("records"), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$_id")
											.append("location", new BasicDBObject("$first", "$location"))
											.append("hospital", new BasicDBObject("$first", "$hospital"))
											.append("records", new BasicDBObject("$push", "$records"))
											.append("loctorId", new BasicDBObject("$first", "$doctorId"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("user", new BasicDBObject("$first", "$user")))),
							Aggregation.unwind("user"),

							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id",
											new BasicDBObject("id1", "$locationId").append("id2", "$doctorId"))
											.append("location", new BasicDBObject("$first", "$location"))
											.append("hospital", new BasicDBObject("$first", "$hospital"))
											.append("records", new BasicDBObject("$first", "$records"))
											.append("users", new BasicDBObject("$push", "$user"))
											.append("user", new BasicDBObject("$first", "$user")))),
							new CustomAggregationOperation(new Document("$project",
									new BasicDBObject("location", "$location").append("hospital", "$hospital")
											.append("reportCount", new BasicDBObject("$size", "$records"))
											.append("user", new BasicDBObject("firstName", "$user.firstName")
													.append("id", "$user._id").append("title", "$user.title")
													.append("reportCount", new BasicDBObject("$size", "$users"))))),
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$_id.id1")
											.append("location", new BasicDBObject("$first", "$location"))
											.append("hospital", new BasicDBObject("$first", "$hospital"))
											.append("doctors", new BasicDBObject("$push", "$user"))
											.append("reportCount", new BasicDBObject("$first", "$reportCount")))));
			response = mongoTemplate.aggregate(aggregation, RecordsCollection.class, Clinic.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
