package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.dpdocter.beans.OnlineConsultationAnalytics;
import com.dpdocter.beans.OnlineConsultionPaymentCollection;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.PatientPaymentDetails;
import com.dpdocter.beans.PaymentSettlementItems;
import com.dpdocter.beans.PaymentSettlements;
import com.dpdocter.beans.PaymentSummary;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientPaymentDetailsCollection;
import com.dpdocter.collections.PaymentSettlementCollection;
import com.dpdocter.collections.SettlementCollection;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.OnlineConsultationPaymentRepository;
import com.dpdocter.repository.PatientPaymentSettlementRepository;
import com.dpdocter.repository.PaymentSettlementRepository;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentBookedByCountResponse;
import com.dpdocter.response.AppointmentDetailAnalyticResponse;
import com.dpdocter.response.BookedAndCancelAppointmentCount;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
import com.dpdocter.response.ScheduleAndCheckoutCount;
import com.dpdocter.services.AppointmentAnalyticsService;
import com.dpdocter.beans.OnlineConsultationSettlement;
import com.dpdocter.services.SMSServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class AppointmentAnalyticServiceImpl implements AppointmentAnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${rayzorpay.api.secret}")
	private String secret;

	
	@Value(value = "${rayzorpay.api.key}")
	private String keyId;
	
	@Autowired
	private PaymentSettlementRepository paymentSettlementRepository;
	
	@Autowired
	private OnlineConsultationPaymentRepository onlineConsultationPaymentRepository;
	
	@Autowired
	private PatientPaymentSettlementRepository patientPaymentSettlementRepository;
	

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
	public ScheduleAndCheckoutCount getScheduledAndCheckoutCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		ScheduleAndCheckoutCount data = new ScheduleAndCheckoutCount();
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
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

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");

			criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
					new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
					new Criteria("state").is(AppointmentState.NEW.toString()));

			data.setScheduled((int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("status").is(QueueStatus.CHECKED_OUT);

			criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
					new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
					new Criteria("state").is(AppointmentState.NEW.toString()));

			data.setCheckOut((int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Schedule and CheckOut analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Schedule and CheckOut analytic");
		}
		return data;

	}

	@Override
	public BookedAndCancelAppointmentCount getBookedAndCancelledCount(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		BookedAndCancelAppointmentCount data = new BookedAndCancelAppointmentCount();
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
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

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");

			criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
					new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
					new Criteria("state").is(AppointmentState.NEW.toString()));

			data.setBookedCount(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");

			criteria.and("state").is(AppointmentState.CANCEL.toString());

			data.setCancelledCount(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Schedule and CheckOut analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Schedule and CheckOut analytic");
		}
		return data;

	}

	@Override
	public AppointmentBookedByCountResponse getAppointmentBookedByCount(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state) {
		AppointmentBookedByCountResponse data = new AppointmentBookedByCountResponse();
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
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

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is("APPOINTMENT");
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				if (state.toUpperCase().equals(AppointmentState.CANCEL.toString())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
							new Criteria("state").is(AppointmentState.NEW.toString()));
				}

			}
			data.setTotal(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

			criteria = getCriteria(doctorId, locationId, hospitalId).and("fromDate").gte(fromTime).lte(toTime)
					.and("createdBy").regex("Dr. ").and("type").is("APPOINTMENT");
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				if (state.toUpperCase().equals(AppointmentState.CANCEL.toString())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
							new Criteria("state").is(AppointmentState.NEW.toString()));
				}

			}
			data.setBookedByDoctor(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));

			data.setBookedByPatient(data.getTotal() - data.getBookedByDoctor());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Schedule and CheckOut analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Schedule and CheckOut analytic");
		}
		return data;

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

				criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
						new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
						new Criteria("state").is(AppointmentState.NEW.toString()));

				appointmentCount = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);

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
						.and("appointment.fromDate").gte(fromTime).and("appointment.type").is("APPOINTMENT");
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("appointment.doctorId").is(new ObjectId(doctorId));
				}

				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.lookup("appointment_cl", "userId", "patientId", "appointment"),
						Aggregation.unwind("appointment"),
						new CustomAggregationOperation(new Document("$redact",
								new BasicDBObject("$cond",
										new BasicDBObject()
												.append("if",
														new BasicDBObject("$gt",
																Arrays.asList("$appointment.createdTime",
																		"$createdTime")))
												.append("then", "$$KEEP").append("else", "$$PRUNE")))),
						Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")))

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
			String hospitalId, String fromDate, String toDate, String searchTerm, int page, int size) {
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
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$patientQueue")
											.append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
							new CustomAggregationOperation(new Document(
									"$unwind", new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(new Criteria("patientQueue.discarded").is(false)),
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
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
							Aggregation.skip((long)page * size), Aggregation.limit(size),
							Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime")));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_queue_cl", "appointmentId", "appointmentId", "patientQueue"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$patientQueue").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(new Criteria("patientQueue.discarded").is(false)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays",
													true))),
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("id", "$_id")
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
							Aggregation.sort(new Sort(Direction.DESC, "fromDate", "time.fromTime")));
				}
				List<AppointmentDetailAnalyticResponse> analyticResponses = mongoTemplate
						.aggregate(aggregation, AppointmentCollection.class, AppointmentDetailAnalyticResponse.class)
						.getMappedResults();
				response.setTotalAppointments(count);

				for (AppointmentDetailAnalyticResponse appointmentDeatilAnalyticResponse : analyticResponses) {
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
			criteria.and("fromDate").gte(new DateTime(from)).lte(new DateTime(to));

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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
											.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
											.append("fromDate", new BasicDBObject("$first", "$fromDate"))
											.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year"))
													.append("averageWaitingTime",
															new BasicDBObject("$avg", "$waitedFor"))
													.append("averageEngagedTime",
															new BasicDBObject("$avg", "$engagedFor"))
													.append("fromDate", new BasicDBObject("$first", "$fromDate"))
													.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
									.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("date", new BasicDBObject("$first", "$fromDate"))));

					break;

				}
				default:
					break;
				}
			} else {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("locationId", "$locationId").append("hospitalId", "$hospitalId"))
										.append("averageWaitingTime", new BasicDBObject("$avg", "$waitedFor"))
										.append("averageEngagedTime", new BasicDBObject("$avg", "$engagedFor"))
										.append("fromDate", new BasicDBObject("$first", "$fromDate"))
										.append("date", new BasicDBObject("$first", "$fromDate"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$fromDate"),Fields.field("fromDate", "$fromDate"),
								Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth()
										.as("month").and("fromDate").extractYear().as("year").and("fromDate").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("date", "$fromDate"),Fields.field("fromDate", "$fromDate"),
								Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
								Fields.field("waitedFor", "$waitedFor"), Fields.field("engagedFor", "$engagedFor")))
										.and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth()
										.as("month").and("fromDate").extractYear().as("year").and("fromDate").extractWeek()
										.as("week"),
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"));
			}
			AggregationResults<AppointmentAverageTimeAnalyticResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, AppointmentCollection.class, AppointmentAverageTimeAnalyticResponse.class);
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
				if (state.toUpperCase().equals(AppointmentState.CANCEL.toString())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
							new Criteria("state").is(AppointmentState.NEW.toString()));
				}

			}
			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);
			AggregationOperation aggregationOperation = null;

			ProjectionOperation project = new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
					Fields.field("date", "$fromDate"), Fields.field("count", "$appointmentId")));
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$fromDate"))
											.append("fromDate", new BasicDBObject("$first", "$fromDate"))
											.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$fromDate"))
													.append("fromDate", new BasicDBObject("$first", "$fromDate"))
													.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("day", new BasicDBObject("$first", "$day"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("week", new BasicDBObject("$first", "$week"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("date", new BasicDBObject("$first", "$fromDate"))
										.append("fromDate", new BasicDBObject("$first", "$fromDate"))
										.append("count", new BasicDBObject("$sum", 1))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						project.and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth().as("month")
								.and("fromDate").extractYear().as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"),
						Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						project.and("fromDate").extractDayOfMonth().as("day").and("fromDate").extractMonth().as("month")
								.and("fromDate").extractYear().as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"));
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
				if (state.toUpperCase().equals(AppointmentState.CANCEL.toString())) {
					criteria.and("appointment.state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("appointment.status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("appointment.state").is(AppointmentState.CONFIRM.toString()),
							new Criteria("appointment.state").is(AppointmentState.RESCHEDULE.toString()),
							new Criteria("appointment.state").is(AppointmentState.NEW.toString()));
				}

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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("week", new BasicDBObject("$first", "$week"))
											.append("groupName", new BasicDBObject("$first", "$groupName"))
											.append("date", new BasicDBObject("$first", "$fromDate"))
											.append("fromDate", new BasicDBObject("$first", "$fromDate"))
											.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case WEEKLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("week", new BasicDBObject("$first", "$week"))
													.append("groupName", new BasicDBObject("$first", "$groupName"))
													.append("date", new BasicDBObject("$first", "$fromDate"))
													.append("fromDate", new BasicDBObject("$first", "$fromDate"))
													.append("count", new BasicDBObject("$sum", 1))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groupName", new BasicDBObject("$first", "$groupName"))
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;
				}
				case YEARLY: {

					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("groupName", new BasicDBObject("$first", "$groupName"))
									.append("date", new BasicDBObject("$first", "$fromDate"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("count", new BasicDBObject("$sum", 1))));

					break;

				}
				default:
					break;
				}
			else {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("day", new BasicDBObject("$first", "$day"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("week", new BasicDBObject("$first", "$week"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("groupName", new BasicDBObject("$first", "$groupName"))
										.append("date", new BasicDBObject("$first", "$fromDate"))
										.append("fromDate", new BasicDBObject("$first", "$fromDate"))
										.append("count", new BasicDBObject("$sum", 1))));
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
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"),
						Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("group_cl", "groupId", "_id", "group"),
						Aggregation.unwind("group"),
						Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
						Aggregation.unwind("appointment"), Aggregation.match(criteria),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$appointment.fromDate"),
								Fields.field("groupName", "$group.name"))).and("fromDate").extractDayOfMonth().as("day")
										.and("fromDate").extractMonth().as("month").and("fromDate").extractYear()
										.as("year").and("fromDate").extractWeek().as("week"),
						aggregationOperation, Aggregation.sort(Direction.ASC, "fromDate"));
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
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				if (state.toUpperCase().equals(AppointmentState.CANCEL.toString())) {
					criteria2.and("appointment.state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria2.and("appointment.status").is(state.toUpperCase());
				} else {
					criteria2.orOperator(new Criteria("appointment.state").is(AppointmentState.CONFIRM.toString()),
							new Criteria("appointment.state").is(AppointmentState.RESCHEDULE.toString()),
							new Criteria("appointment.state").is(AppointmentState.NEW.toString()));
				}

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
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("id", "$group._id"))
									.append("groupName", new BasicDBObject("$first", "$group.name"))
									.append("date", new BasicDBObject("$first", "$group.createdTime"))
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
			String fromDate, String toDate, String state) {
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
			if (!DPDoctorUtils.anyStringEmpty(state)) {
				if (state.toUpperCase().equalsIgnoreCase(AppointmentState.CANCEL.getState())) {
					criteria2.and("appointment.state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria2.and("appointment.status").is(state.toUpperCase());
				} else {
					criteria2.orOperator(new Criteria("appointment.state").is(AppointmentState.CONFIRM.getState()),
							new Criteria("appointment.state").is(AppointmentState.RESCHEDULE.getState()),
							new Criteria("appointment.state").is(AppointmentState.NEW.getState()));
				}

			}
			criteria2.and("appointment.fromDate").gte(new DateTime(from)).lte(new DateTime(to));
			criteria2.and("appointment.type").is(AppointmentType.APPOINTMENT);
			criteria.and("group.discarded").is(false).and("discarded").is(false);

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "patientId", "patientId", "appointment"),
					Aggregation.unwind("appointment"), Aggregation.match(criteria2),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("id", "$group._id")))));

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

	@Override
	public List<DoctorAnalyticPieChartResponse> getDoctorAppointmentAnalyticsForPieChart(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String state, String searchTerm,
			int page, int size) {
		List<DoctorAnalyticPieChartResponse> response = null;
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
				if (state.toUpperCase().equalsIgnoreCase(AppointmentState.CANCEL.getState())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.getState()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.getState()),
							new Criteria("state").is(AppointmentState.NEW.getState()));
				}

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

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$doctorId").append("firstName", new BasicDBObject("$first", "$firstName"))
							.append("date", new BasicDBObject("$first", "$fromDate"))
							.append("count", new BasicDBObject("$sum", 1))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("doctorId", "$doctorId"), Fields.field("firstName", "$doctor.firstName"),
								Fields.field("count", "$appointmentId"))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "count"), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						new ProjectionOperation(Fields.from(Fields.field("fromDate", "$fromDate"),
								Fields.field("count", "$appointmentId"), Fields.field("doctorId", "$doctorId"),
								Fields.field("firstName", "$doctor.firstName"))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "count"));
			}
			AggregationResults<DoctorAnalyticPieChartResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					AppointmentCollection.class, DoctorAnalyticPieChartResponse.class);
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
				if (state.toUpperCase().equalsIgnoreCase(AppointmentState.CANCEL.getState())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.getState()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.getState()),
							new Criteria("state").is(AppointmentState.NEW.getState()));
				}

			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("profile.localPatientName").regex(searchTerm, "i"),
						new Criteria("patient.firstName").regex(searchTerm, "i"),
						new Criteria("patient.mobileNumber").regex(searchTerm, "i"));
			}

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
						Aggregation.sort(Direction.DESC, "fromDate"), Aggregation.skip((long)page * size),
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
								Fields.field("appointmentId", "$appointmentId"), Fields.field("dob", "$profile.dob"),
								Fields.field("gender", "$profile.gender"), Fields.field("fromDate", "$fromDate"))),
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
			String fromDate, String toDate, String state, String searchTerm) {
		Integer response = null;
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
				if (state.toUpperCase().equalsIgnoreCase(AppointmentState.CANCEL.getState())) {
					criteria.and("state").is(state);
				} else if (state.toUpperCase().equalsIgnoreCase("CHECKED_OUT")) {
					criteria.and("status").is(state.toUpperCase());
				} else {
					criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.getState()),
							new Criteria("state").is(AppointmentState.RESCHEDULE.getState()),
							new Criteria("state").is(AppointmentState.NEW.getState()));
				}

			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria2.orOperator(new Criteria("profile.localPatientName").regex(searchTerm, "i"),
						new Criteria("patient.firstName").regex(searchTerm, "i"),
						new Criteria("patient.mobileNumber").regex(searchTerm, "i"));
			}

			criteria = criteria.and("type").is(AppointmentType.APPOINTMENT);

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("user_cl", "patientId", "_id", "patient"), Aggregation.unwind("patient"),
					Aggregation.lookup("patient_cl", "patientId", "userId", "profile"), Aggregation.unwind("profile"),
					Aggregation.match(criteria2));

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
	
	
	
	@Override
	public OnlineConsultationAnalytics getConsultationAnalytics(String fromDate, String toDate, String doctorId,
			String locationId, String type) {
		OnlineConsultationAnalytics response=new OnlineConsultationAnalytics();
		try {
			
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			

//			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
//				criteria.and("locationId").is(new ObjectId(locationId));
//			}
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime=null,toDateTime=null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

			//	DateTime
				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
				System.out.println(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

			//	DateTime 
				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				
				System.out.println(toDateTime);

				criteria.and("toDate").lte(toDateTime);
			}
			
//			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date(Long.parseLong(toDate));
//
//			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date();
//			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
//				from = new Date(date);
//				to = new Date(Long.parseLong(toDate));
//			} else {
//				from = new Date(date);
//				to = new Date();
//			}
//
//			fromTime = new DateTime(from);
//			toTime = new DateTime(to);
//
//			criteria.and("fromDate").gte(fromTime).lte(toTime)
					criteria.and("type").is(type);

			
			
//			criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
//					new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
//					new Criteria("state").is(AppointmentState.NEW.toString()));

			Criteria criteria2=new Criteria();
			criteria2.and("doctorId").is(new ObjectId(doctorId));
		//	criteria2.and("locationId").is(new ObjectId(locationId));
		//	criteria2.and("fromDate").gte(fromTime).lte(toTime)
			criteria2.and("type").is(type);
			criteria2.and("consultationType").is(ConsultationType.CHAT.toString());
			
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) 
			criteria2.and("fromDate").gte(fromDateTime);
			
			if (!DPDoctorUtils.anyStringEmpty(toDate)) 
			criteria2.and("toDate").lte(toDateTime);
			
			response.setTotalOnlineConsultation(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));
			criteria.and("consultationType").is(ConsultationType.VIDEO.toString());
			response.setTotalVideoConsultation(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));
			//criteria2.and("consultationType").is(ConsultationType.CHAT.toString());
			response.setTotalChatConsultation(mongoTemplate.count(new Query(criteria2),AppointmentCollection.class));
			
		}catch (BusinessException e) {
			logger.error("Error while getting online Consultation Analytics " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting online Consultation Analytics " + e.getMessage());

		}
		return response;
	}

	@Override
	public PaymentSummary getPaymentSummary(String fromDate, String toDate, String doctorId,String consultationType) {
		PaymentSummary response=null;
		try {
			
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			

		
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(consultationType)) {
				criteria.and("consultationType.consultationType").is(consultationType.trim());
			}
			
//			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date(Long.parseLong(toDate));
//
//			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date();
//			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
//				from = new Date(date);
//				to = new Date(Long.parseLong(toDate));
//			} else {
//				from = new Date(date);
//				to = new Date();
//			}
//
//			fromTime = new DateTime(from);
//			toTime = new DateTime(to);
//
//			criteria.and("fromDate").gte(fromTime).lte(toTime);

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			DateTime fromDateTime = null, toDateTime = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			}
			if (fromDateTime != null && toDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
			} else if (fromDateTime != null) {
				criteria.and("createdTime").gte(fromDateTime);
			} else if (toDateTime != null) {
				criteria.and("createdTime").lte(toDateTime);
			}

			
			Aggregation aggregation = null;
			
			CustomAggregationOperation group= null;
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
					.append("doctorId", "$doctorId")
					.append("totalAmountReceived", "$totalAmountReceived")
					.append("consultationType.consultationType", "$consultationType.consultationType")					
					.append("consultationType.cost", "$consultationType.cost")
					.append("consultationType.healthcocoCharges", "$consultationType.healthcocoCharges")
					.append("createdTime", "$createdTime")					
					.append("updatedTime", "$updatedTime")));
			
			
			group = new CustomAggregationOperation(new Document("$group",
					
							new BasicDBObject("_id", "_id")
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("totalAmountReceived", new BasicDBObject("$sum", "$amount"))
									
									.append("consultationType", new BasicDBObject("$first", "$consultationType"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))));
									

			
//			if(size>0) {
//			aggregation = Aggregation.newAggregation(
//					
//					Aggregation.match(criteria),
//				
//					//Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorData"),
//					group,project,
//					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
//					Aggregation.skip((page) * size), Aggregation.limit(size));
//			
//			} else {
				aggregation = Aggregation.newAggregation( 
						Aggregation.match(criteria),
					
						
						group,
						project,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

		//	}
			System.out.println("aggregation:"+aggregation);
			response=mongoTemplate.aggregate(aggregation,OnlineConsultionPaymentCollection.class,PaymentSummary.class).getUniqueMappedResult();
			
		}
		catch (BusinessException e) {
			logger.error("Error while getting online Consultation Analytics " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting online Consultation Analytics " + e.getMessage());

		}
		return response;

	}
	
	
	
	@SuppressWarnings("deprecation")
	@Override
	public PaymentSettlements fetchSettlement(Integer day,Integer month, Integer year) {
		
		PaymentSettlements response = null;
		try {
	//		RazorpayClient rayzorpayClient = new RazorpayClient(keyId, secret);
		
			JSONObject orderRequest = new JSONObject();
			
		//	System.out.println("from"+from);
		//	Integer fromTime = Integer.parseInt(from);
			
			
//			double amount = (request.getDiscountAmount() * 100);
//			// amount in paise
//			orderRequest.put("amount", (int) amount);
//			orderRequest.put("currency", request.getCurrency());
//			orderRequest.put("receipt",  "-RCPT-"
//					+ bulkSmsPaymentRepository.countByDoctorId(new ObjectId(request.getDoctorId()))
//					+ generateId());
//			orderRequest.put("payment_capture", request.getPaymentCapture());

			String url="https://api.razorpay.com/v1/settlements/recon/combined?year="+year+"&month="+month+"&day="+day;
			 String authStr=keyId+":"+secret;
			 String authStringEnc = Base64.getEncoder().encodeToString(authStr.getBytes());
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			
			con.setDoOutput(true);
			

			con.setDoInput(true);
			// optional default is POST
			con.setRequestMethod("GET");
//			con.setRequestProperty("User-Agent",
//					"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			con.setRequestProperty("Accept-Charset", "UTF-8");
			con.setRequestProperty("Content-Type","application/json");
			con.setRequestProperty("Authorization", "Basic " +  authStringEnc);
//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//			wr.writeBytes(orderRequest.toString());
//			
//			  wr.flush();
//	             wr.close();
	             con.disconnect();
	             BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	           //  InputStream in=con.getInputStream();
	             StringBuffer output = new StringBuffer();
	 			int c = 0;
	 			while ((c=in.read()) !=-1) {

	 				output.append((char) c);
	 				
	 			}
	 			System.out.println("response:"+output.toString());
	 			
	 			  ObjectMapper mapper = new ObjectMapper();
	 			 PaymentSettlements payment=null;
	 			  payment = mapper.readValue(output.toString(), PaymentSettlements.class);
	 			  PatientPaymentDetails patient=new PatientPaymentDetails();
	 			
	 			  PatientPaymentDetailsCollection patientCollection=null;
	 			  
	 			  OnlineConsultionPaymentCollection onlinePayment=new OnlineConsultionPaymentCollection();
	 			  for(PaymentSettlementItems item:payment.getItems())
	 			  {
	 				 onlinePayment = onlineConsultationPaymentRepository.findByOrderId(item.getOrder_id());
	 				 
	 				 
	 				patientCollection=patientPaymentSettlementRepository.findByOrderId(item.getOrder_id());
	 				if(patientCollection !=null) {
	 					patientCollection.setIsSettled(item.getSettled());
	 					patientCollection.setSettlementDate(new Date(item.getSettled_at()));
	 				}
	 				
	 				else {
	 					patientCollection=new PatientPaymentDetailsCollection();
	 				//if(onlinePayment.getUserId()!=null)	
	 			//	patientCollection.setUserId(onlinePayment.getUserId());
	 				patientCollection.setSettlementDate(new Date(item.getSettled_at()));
	 				patientCollection.setIsSettled(item.getSettled());
	 				patientCollection.setOrderId(item.getOrder_id());
	 				patientCollection.setPaymentId(item.getPayment_id());
	 		//		if(onlinePayment.getDoctorId()!=null)	
	 			//	patientCollection.setDoctorId(onlinePayment.getDoctorId());
	 		//		if(onlinePayment.getConsultationType()!=null)	
	 			//	patientCollection.setConsultationType(onlinePayment.getConsultationType());
	 				
	 				patientPaymentSettlementRepository.save(patientCollection);
	 			  
	 				}
	 			  
	 			  }	  
	 			  
	 			  PaymentSettlementCollection paymentSettlements=new PaymentSettlementCollection();
	 			 
	 			  BeanUtil.map(payment, paymentSettlements);
	 			  
	 			  paymentSettlements.setCreatedTime(new Date());
	 			  paymentSettlements.setUpdatedTime(new Date());
	 			 paymentSettlementRepository.save(paymentSettlements);
	 			 
	 			 
	 			 response=new PaymentSettlements();
	 			BeanUtil.map(paymentSettlements,response);
	 		//	 OrderReponse list = mapper.readValue(output.toString(),OrderReponse.class);
	 			//OrderReponse res=list.get(0); 
 			

	//		order = rayzorpayClient.Orders.create(orderRequest);

//			if (user != null) {
//				BulkSmsPaymentCollection collection = new BulkSmsPaymentCollection();
//				BeanUtil.map(request, collection);
//				collection.setCreatedTime(new Date());
//				collection.setCreatedBy(user.getTitle() + " " + user.getFirstName());
//				collection.setOrderId(list.getId().toString());
//				collection.setReciept(list.getReceipt().toString());
//				collection.setTransactionStatus("PENDING");
//				collection = bulkSmsPaymentRepository.save(collection);
//				response = new BulkSmsPaymentResponse();
//				BeanUtil.map(collection, response);
//			}
		}
		//	catch (RazorpayException e) {
//			// Handle Exception
//			
//			logger.error(e.getMessage());
//			e.printStackTrace();
//		}
			catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<OnlineConsultationSettlement> getSettlements(String fromDate, String toDate, String doctorId, int page,
			int size) {
		List<OnlineConsultationSettlement> response=null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			

		
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
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

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria.and("date").gte(fromTime).lte(toTime);

			Aggregation aggregation = null;

			if(size>0) {
			aggregation = Aggregation.newAggregation(
					
					Aggregation.match(criteria),
				
					//Aggregation.lookup("doctor_clinic_profile_cl", "doctorId", "doctorId", "doctorData"),
					
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
					Aggregation.skip((page) * size), Aggregation.limit(size));
			
			} else {
				aggregation = Aggregation.newAggregation( 
						Aggregation.match(criteria),
					
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			System.out.println("aggregation:"+aggregation);
			response=mongoTemplate.aggregate(aggregation,SettlementCollection.class,OnlineConsultationSettlement.class).getMappedResults();

			
		}catch (BusinessException e) {
			logger.error("Error while getting doctor Settlements " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctor Settlements" + e.getMessage());

		}
		return response;

	}

	@Override
	public List<PatientPaymentDetails> getPatientPaymentDetails(String doctorId,int page, int size) {
		List<PatientPaymentDetails> response=null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			

		
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
//			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date(Long.parseLong(toDate));
//
//			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
//				from = new Date(Long.parseLong(fromDate));
//				to = new Date();
//			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
//				from = new Date(date);
//				to = new Date(Long.parseLong(toDate));
//			} else {
//				from = new Date(date);
//				to = new Date();
//			}
			
			criteria.and("type").is("ONLINE_CONSULTATION");

			
			

			Aggregation aggregation = null;
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id")
					.append("userId", "$userId")
					.append("doctorId", "$doctorId")
					.append("localPatientName", "$patient.localPatientName")
					.append("consultationType.consultationType", "$consultationType.consultationType")					
					.append("consultationType.cost", "$consultationType.cost")
					.append("consultationType.healthcocoCharges", "$consultationType.healthcocoCharges")
					.append("appointmentId", "$appointment._id")					
					.append("appointmentDate", "$appointment.appointmentDate")
					.append("status", "$appointment.status")					
					.append("isSettled", "$isSettled")
					.append("settlementDate", "$settlementDate")					
					.append("paymentId", "$paymentId")));

			if(size>0) {
			aggregation = Aggregation.newAggregation(
					
					Aggregation.match(criteria),
					Aggregation.lookup("appointment_cl", "userId", "_id", "appointment"),
					Aggregation.unwind("appointment"),
					Aggregation.lookup("patient_cl", "userId", "_id", "patient"),
					Aggregation.unwind("patient"),
					project,
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
					Aggregation.skip((page) * size), Aggregation.limit(size));
			
			} else {
				aggregation = Aggregation.newAggregation( 
						Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "userId", "_id", "appointment"),
						Aggregation.unwind("appointment"),
						Aggregation.lookup("patient_cl", "userId", "_id", "patient"),
						Aggregation.unwind("patient"),
						project,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			System.out.println("aggregation:"+aggregation);
			response=mongoTemplate.aggregate(aggregation,PatientPaymentDetailsCollection.class,PatientPaymentDetails.class).getMappedResults();


		
		}catch (BusinessException e) {
			logger.error("Error while getting doctor Settlements " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting doctor Settlements" + e.getMessage());

		}
		return response;
	}


}
