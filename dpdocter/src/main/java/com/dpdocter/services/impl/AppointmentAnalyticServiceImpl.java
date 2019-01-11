package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AppointmentAnalyticData;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientQueueCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentDeatilAnalyticResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticPieChartResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
import com.dpdocter.services.AppointmentAnalyticsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class AppointmentAnalyticServiceImpl implements AppointmentAnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AppointmentAnalyticServiceImpl.class);

	private Criteria getCriteria(String doctorId, String locationId, String hospitalId) {
		Criteria criteria = new Criteria();

		if (!DPDoctorUtils.anyStringEmpty(locationId)) {
			criteria.and("locationId").is(new ObjectId(locationId));
		}
		if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
		}
		if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
			criteria.and("doctorId").is(new ObjectId(doctorId));
		}
		return criteria;

	}

	@Override
	public DoctorAppointmentAnalyticResponse getAppointmentAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		DoctorAppointmentAnalyticResponse data = new DoctorAppointmentAnalyticResponse();
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			Date lastdate = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			long diff = to.getTime() - from.getTime();
			long diffDays = (diff / (24 * 60 * 60 * 1000));
			lastdate = new Date(from.getTime() - ((diffDays + 1) * (24 * 60 * 60 * 1000)));

			DateTime last = new DateTime(lastdate);
			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			// total
			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");

			data.setTotalNoOfAppointment((int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

			// cancel by doctor
			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");

			data.setCancelBydoctor((int) mongoTemplate.count(new Query(criteria.and("cancelledByProfile")
					.is(AppointmentCreatedBy.DOCTOR.getType()).and("state").is("CANCEL")),
					AppointmentCollection.class));

			// cancel by Patient
			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");
			data.setCancelByPatient((int) mongoTemplate.count(new Query(criteria.and("cancelledByProfile")
					.is(AppointmentCreatedBy.PATIENT.getType()).and("state").is("CANCEL")),
					AppointmentCollection.class));
			if (data.getTotalNoOfAppointment() > 0) {

				criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
						.and("type").is("APPOINTMENT");
				int appointmentCount = (int) mongoTemplate.count(new Query(criteria.and("state").is("CANCEL")),
						AppointmentCollection.class);

				data.setCancelledAppointmentInPercent(
						((100 * (double) appointmentCount) / (double) data.getTotalNoOfAppointment()));

				// Booked percent
				criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
						.and("type").is("APPOINTMENT");
				appointmentCount = (int) mongoTemplate.count(new Query(criteria.and("state").is("CONFIRM")),
						AppointmentCollection.class);

				data.setBookedAppointmentInPercent(
						((100 * (double) appointmentCount) / (double) data.getTotalNoOfAppointment()));

				// Scheduled percent

				criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
						.and("type").is("APPOINTMENT");
				appointmentCount = (int) mongoTemplate.count(
						new Query(criteria.and("status").is("SCHEDULED").and("state").is("NEW")),
						AppointmentCollection.class);

				data.setScheduledAppointmentInPercent(
						(100 * (double) appointmentCount) / (double) data.getTotalNoOfAppointment());

				// hike
				criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(last).lte(fromTime)
						.and("type").is("APPOINTMENT");
				appointmentCount = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);

				data.setChangeInAppointmentPercent(
						(100 * ((double) data.getTotalNoOfAppointment() - (double) appointmentCount))
								/ (double) data.getTotalNoOfAppointment());
				// new Patient Appointment

				criteria = getCriteria(null, locationId, hospitalId).and("appointment.locationId")
						.is(new ObjectId(locationId)).and("appointment.hospitalId").is(new ObjectId(hospitalId))
						.and("appointment.fromDate").gte(fromTime).and("appointment.type").is("APPOINTMENT")
						;
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("appointment.doctorId").is(new ObjectId(doctorId));
				}
				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.lookup("appointment_cl", "userId", "patientId", "appointment"),
						Aggregation.unwind("appointment"),
						new CustomAggregationOperation(new BasicDBObject("$redact",
								new BasicDBObject("$cond",
										new BasicDBObject()
												.append("if",
														new BasicDBObject("$gt",
																Arrays.asList("$appointment.createdTime",
																		"$createdTime")))
												.append("then", "$$KEEP").append("else", "$$PRUNE")))),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$_id")))

				);
				appointmentCount = mongoTemplate
						.aggregate(aggregation, PatientCollection.class, PatientCollection.class).getMappedResults()
						.size();

				data.setNewPatientAppointmentInPercent(
						(100 * (appointmentCount)) / (double) data.getTotalNoOfAppointment());

				// old Patient Appointment count

				data.setOldPatientAppointmentInPercent((100 * (data.getTotalNoOfAppointment() - appointmentCount))
						/ (double) data.getTotalNoOfAppointment());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Appointment analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Appointment analytic");
		}
		return data;

	}

	@Override
	public AppointmentAnalyticResponse getAppointmentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchType, String searchTerm, int page,
			int size) {
		AppointmentAnalyticResponse response = null;
		try {
			Date from = null;
			Date to = null;
			Long date = 0l;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchType)) {
				criteria = criteria.and("state").is(searchType);
			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));

			long count = mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count > 0) {
				response = new AppointmentAnalyticResponse();
				Aggregation aggregation = null;
				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientQueue")
											.append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
							new CustomAggregationOperation(new BasicDBObject(
									"$unwind", new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(new Criteria("patientQueue.discarded").is(false)),
							new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromTime", new BasicDBObject("$first", "$time.fromTime"))
									.append("waitedFor", new BasicDBObject("$max", "$patientQueue.waitedFor"))
									.append("engagedAt", new BasicDBObject("$max", "$patientQueue.engagedAt"))
									.append("checkedInAt", new BasicDBObject("$max", "$patientQueue.checkedInAt"))
									.append("checkedOutAt", new BasicDBObject("$max", "$patientQueue.checkedOutAt"))
									.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
							Aggregation.skip(page * size), Aggregation.limit(size),
							Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$patientQueue").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(new Criteria("patientQueue.discarded").is(false)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),
							new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromTime", new BasicDBObject("$first", "$time.fromTime"))
									.append("waitedFor", new BasicDBObject("$max", "$patientQueue.waitedFor"))
									.append("engagedAt", new BasicDBObject("$max", "$patientQueue.engagedAt"))
									.append("checkedInAt", new BasicDBObject("$max", "$patientQueue.checkedInAt"))
									.append("checkedOutAt", new BasicDBObject("$max", "$patientQueue.checkedOutAt"))
									.append("patientName", new BasicDBObject("$first", "$patient.localPatientName"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorName", new BasicDBObject("$first", "$doctor.firstName")))),
							Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")));
				}
				List<AppointmentDeatilAnalyticResponse> analyticResponses = mongoTemplate
						.aggregate(aggregation, AppointmentCollection.class, AppointmentDeatilAnalyticResponse.class)
						.getMappedResults();
				response.setTotalAppointments(count);

				for (AppointmentDeatilAnalyticResponse appointmentDeatilAnalyticResponse : analyticResponses) {
					List<PatientCard> patientCards = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("userId")
									.is(new ObjectId(appointmentDeatilAnalyticResponse.getPatientId()))
									.and("locationId")
									.is(new ObjectId(appointmentDeatilAnalyticResponse.getLocationId()))
									.and("hospitalId")
									.is(new ObjectId(appointmentDeatilAnalyticResponse.getHospitalId())))),
							PatientCollection.class, PatientCard.class).getMappedResults();
					if (patientCards != null && !patientCards.isEmpty()) {
						appointmentDeatilAnalyticResponse.setPatientName(patientCards.get(0).getLocalPatientName());
					}
				}
				response.setAppointments(analyticResponses);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticsData(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchType, String searchTerm,
			int page, int size) {
		List<AppointmentAverageTimeAnalyticResponse> response = null;
		try {

			Criteria criteria = new Criteria();
			Date from = null;
			Date to = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("date").gte(new DateTime(from)).lte(new DateTime(to));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType)) {
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
											.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
											.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year"))
													.append("averageWaitingTime",
															new BasicDBObject("$avg", "$waitedFor"))
													.append("averageEngagedTime",
															new BasicDBObject("$avg", "$engagedFor"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;

				}
				default:
					break;
				}
			} else {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("locationId", "$locationId").append("hospitalId", "$hospitalId"))
										.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
										.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
										.append("date", new BasicDBObject("$first", "$date"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$date"),
								Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("date").extractDayOfMonth().as("day").and("date").extractMonth()
										.as("month").and("date").extractYear().as("year").and("date").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$date"),
								Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("date").extractDayOfMonth().as("day").and("date").extractMonth()
										.as("month").and("date").extractYear().as("year").and("date").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}
			AggregationResults<AppointmentAverageTimeAnalyticResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, PatientQueueCollection.class, AppointmentAverageTimeAnalyticResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment average time analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment average time analytics data");
		}
		return response;
	}

	@Override
	public List<AnalyticResponse> getAppointmentAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, String queryType, String searchType, String searchTerm,
			int page, int size) {
		List<AnalyticResponse> response = null;

		if (!DPDoctorUtils.anyStringEmpty(queryType)) {
			if (queryType.equals("IN_GROUP")) {
				response = getpatientGroupAppointmentAnalytic(doctorId, locationId, hospitalId, fromDate, toDate, state,
						searchType, page, size);
			} else {
				response = getPatientAppointmentAnalytics(doctorId, locationId, hospitalId, fromDate, toDate, state,
						searchType, searchTerm, page, size);
			}

		}

		return response;
	}

	public List<AnalyticResponse> getPatientAppointmentAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, String searchType, String searchTerm, int page, int size) {
		List<AnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			long date = 0;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);

			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("count", new BasicDBObject("$sum", 1))
											.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("count", new BasicDBObject("$sum", 1))
													.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("day", new BasicDBObject("$first", "$day"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("week", new BasicDBObject("$first", "$week"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("count", new BasicDBObject("$sum", 1))
										.append("date", new BasicDBObject("$first", "$fromDate"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("count", "$appointmentId"))).and("fromDate").extractDayOfMonth().as("day")
										.and("fromDate").extractMonth().as("month").and("fromDate").extractYear()
										.as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("count", "$appointmentId"))).and("fromDate").extractDayOfMonth().as("day")
										.and("fromDate").extractMonth().as("month").and("fromDate").extractYear()
										.as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}
			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					AppointmentCollection.class, AnalyticResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data");
		}
		return response;
	}

	public List<AnalyticResponse> getpatientGroupAppointmentAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state, String searchType, int page, int size) {
		List<AnalyticResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			long date = 0;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("appointment.state").is(state.toUpperCase());

			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").is(new ObjectId(doctorId));
				criteria.and("appointment.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId)).and("appointment.locationId")
						.is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("appointment.fromDate").gte(new DateTime(from)).lte(new DateTime(to));

			criteria = criteria.and("appointment.type").is(AppointmentType.APPOINTMENT);
			criteria.and("group.discarded").is(false).and("discarded").is(false);
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("groupName", new BasicDBObject("$first", "$groupName"))
											.append("count", new BasicDBObject("$sum", 1))
											.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("count", new BasicDBObject("$sum", 1))
													.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groupName", new BasicDBObject("$first", "$groupName"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groupName", new BasicDBObject("$first", "$groupName"))
									.append("count", new BasicDBObject("$sum", 1))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("day", new BasicDBObject("$first", "$day"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("week", new BasicDBObject("$first", "$week"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("groupName", new BasicDBObject("$first", "$groupName"))
										.append("count", new BasicDBObject("$sum", 1))
										.append("date", new BasicDBObject("$first", "$fromDate"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("group_cl", "groupId", "_id", "group"),
						Aggregation.unwind("group"),
						Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
						Aggregation.unwind("appointment"), Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$appointment.fromDate"),
								Fields.field("groupName", "$group.name"))).and("appointment.fromDate")
										.extractDayOfMonth().as("day").and("appointment.fromDate").extractMonth()
										.as("month").and("appointment.fromDate").extractYear().as("year")
										.and("appointment.fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("group_cl", "groupId", "_id", "group"),
						Aggregation.unwind("group"),
						Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
						Aggregation.unwind("appointment"), Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$appointment.fromDate"),
								Fields.field("groupName", "$group.name"))).and("fromDate").extractDayOfMonth().as("day")
										.and("fromDate").extractMonth().as("month").and("fromDate").extractYear()
										.as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}

			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					PatientGroupCollection.class, AnalyticResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentAnalyticGroupWiseResponse> getAppointmentAnalyticPatientGroup(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String state, int page, int size) {
		List<AppointmentAnalyticGroupWiseResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			Long date = 0l;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").in(new ObjectId(doctorId));
				criteria2.and("appointment.doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("appointment.locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria2.and("appointment.fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			criteria2.and("appointment.type").is(AppointmentType.APPOINTMENT);
			criteria.and("group.discarded").is(false).and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
					Aggregation.unwind("appointment"), Aggregation.match(criteria2),
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("Id", "$group._id"))
									.append("groupName", "$group.name")
									.append("count", new BasicDBObject("$sum", 1)))));

			response = mongoTemplate
					.aggregate(aggregation, PatientGroupCollection.class, AppointmentAnalyticGroupWiseResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data by patient group");
		}
		return response;
	}

	@Override
	public Integer countAppointmentAnalyticPatientGroup(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, int page, int size) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			Date from = null;
			Date to = null;
			Long date = 0l;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").is(new ObjectId(doctorId));
				criteria2.and("appointment.doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("appointment.locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria2.and("appointment.fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			criteria2.and("appointment.type").is(AppointmentType.APPOINTMENT);
			criteria.and("group.discarded").is(false).and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
					Aggregation.unwind("appointment"), Aggregation.match(criteria2));

			response = mongoTemplate
					.aggregate(aggregation, PatientGroupCollection.class, AppointmentAnalyticGroupWiseResponse.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data by patient group");
		}
		return response;
	}

	public List<DoctorAppointmentAnalyticPieChartResponse> getDoctorAppointmentAnalyticsForPieChart(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String state, String searchTerm,
			int page, int size) {
		List<DoctorAppointmentAnalyticPieChartResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Date from = null;
			Date to = null;
			Long date = 0l;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);

			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);
			AggregationOperation aggregationOperation = null;

			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$doctorId").append("count", new BasicDBObject("$sum", 1))
							.append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("date", new BasicDBObject("$first", "$fromDate"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("firstName", "$doctor.firstName"),
								Fields.field("count", "$appointmentId"))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("firstName", "$doctor.firstName"),
								Fields.field("count", "$appointmentId"))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "date"));
			}
			AggregationResults<DoctorAppointmentAnalyticPieChartResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, AppointmentCollection.class, DoctorAppointmentAnalyticPieChartResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting appointment Doctor analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While counting appointment Doctor analytics data");
		}
		return response;
	}

	@Override
	public List<AppointmentAnalyticData> getPatientAppointmentAnalyticsDetail(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state, String searchTerm, int page, int size) {
		List<AppointmentAnalyticData> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			Date from = null;
			Date to = null;
			Long date = 0l;

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("profile.locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);

			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "profile"),
						Aggregation.unwind("profile"), Aggregation.match(criteria2),
						new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
								Fields.field("toDate", "$toDate"),
								Fields.field("localPatientName", "$profile.localPatientName"),
								Fields.field("firstName", "$patient.firstName"),
								Fields.field("doctorName", "$doctor.firstName"),
								Fields.field("explanation", "$explanation"), Fields.field("subject", "$subject"),
								Fields.field("appointmentId", "$appointmentId"),
								Fields.field("fromDate", "$fromDate"))),
						Aggregation.sort(Direction.DESC, "fromDate"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient"),
						Aggregation.lookup("patient_cl", "patientId", "userId", "profile"),
						Aggregation.unwind("profile"), Aggregation.match(criteria2),
						new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
								Fields.field("toDate", "$toDate"),
								Fields.field("localPatientName", "$profile.localPatientName"),
								Fields.field("firstName", "$patient.firstName"),
								Fields.field("doctorName", "$doctor.firstName"),
								Fields.field("explanation", "$explanation"), Fields.field("subject", "$subject"),
								Fields.field("appointmentId", "$appointmentId"),
								Fields.field("fromDate", "$fromDate"))),
						Aggregation.sort(Direction.DESC, "fromDate"));
			}
			AggregationResults<AppointmentAnalyticData> aggregationResults = mongoTemplate.aggregate(aggregation,
					AppointmentCollection.class, AppointmentAnalyticData.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting appointment count analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting appointment count analytics data");
		}
		return response;
	}

	@Override
	public Integer countPatientAppointmentAnalyticsDetail(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, String searchTerm, int page, int size) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria();

			Date from = null;
			Date to = null;
			Long date = 0l;

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));

			}

			if (!DPDoctorUtils.anyStringEmpty(state)) {
				criteria.and("state").is(state);

			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria));

			response = mongoTemplate.aggregate(aggregation, AppointmentCollection.class, AppointmentCollection.class)
					.getMappedResults().size();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Count appointment count analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Count appointment count analytics data");
		}
		return response;
	}

}
