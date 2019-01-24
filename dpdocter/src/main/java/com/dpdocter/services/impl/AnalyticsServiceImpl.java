package com.dpdocter.services.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientGroupCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.SearchType;
import com.dpdocter.enums.UnitType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.DiagnosticTestsAnalyticsData;
import com.dpdocter.response.DoctorPatientAnalyticResponse;
import com.dpdocter.response.DoctorPrescriptionItemAnalyticResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.DoctorprescriptionAnalyticResponse;
import com.dpdocter.response.DrugsAnalyticsData;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.services.AnalyticsService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AnalyticsServiceImpl.class);

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
	public DoctorVisitAnalyticResponse getVisitAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DoctorVisitAnalyticResponse data = null;
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0l;
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
			criteria = getCriteria(doctorId, locationId, hospitalId).and("discarded").is(false);

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			data = new DoctorVisitAnalyticResponse();
			data.setTotalVisit((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));
			criteria.and("adminCreatedTime").gte(fromTime).lte(toTime);
			data.setTotalVisitCreated((int) mongoTemplate.count(new Query(criteria), PatientVisitCollection.class));
			// mongoTemplate.aggregate(aggregation,
			// DoctorClinicProfileCollection.class,
			// DoctorprescriptionAnalyticResponse.class).getUniqueMappedResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting visit analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting visit analytic");
		}
		return data;

	}

	@Override
	public List<DoctorPrescriptionItemAnalyticResponse> getPrescriptionItemAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String type, String searchTerm) {
		List<DoctorPrescriptionItemAnalyticResponse> response = null;
		try {
			Criteria criteria = null;
			Criteria itemCriteria = new Criteria();

			Date from = null;
			Date to = null;
			long date = 0l;
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

			criteria = getCriteria(doctorId, locationId, null);

			Aggregation aggregation = null;

			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

			case DRUGS: {
				itemCriteria.and("totalCount.hospitalId").is(new ObjectId(hospitalId)).and("totalCount.locationId")
						.is(new ObjectId(locationId)).and("prescription.hospitalId").is(new ObjectId(hospitalId))
						.and("prescription.locationId").is(new ObjectId(locationId));

				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					itemCriteria = itemCriteria.and("totalCount.drugName").regex(searchTerm, "i");
				}
				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "prescription"),
							Aggregation.unwind("prescription"), Aggregation.unwind("prescription.items"),
							Aggregation.lookup("drug_cl", "doctorId", "doctorId", "totalCount"),
							Aggregation.unwind("totalCount"), Aggregation.match(itemCriteria),
							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("totalCount", "$totalCount").append("itemId",
											"$prescription.items.drugId"))),
							new CustomAggregationOperation(new BasicDBObject(
									"$group",
									new BasicDBObject(
											"_id",
											new BasicDBObject("drugId", "$totalCount._id").append("itemId", "$itemId"))
													.append("name", new BasicDBObject("$first", "$totalCount.drugName"))
													.append("totalCount", new BasicDBObject("$sum", 1)))),

							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("name", "$name").append("totalCount",
											new BasicDBObject("$cond", new BasicDBObject("$if", new BasicDBObject("$eq",
													new Object[] { new BasicDBObject("$itemId", "totalCount._id") })
															.append("then", "$totalCount").append("else", 0)))))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "totalCount")),
							Aggregation.skip((page) * size), Aggregation.limit(size));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "prescription"),
							Aggregation.unwind("prescription"), Aggregation.unwind("prescription.items"),
							Aggregation.lookup("drug_cl", "doctorId", "doctorId", "totalCount"),
							Aggregation.unwind("totalCount"), Aggregation.match(itemCriteria),

							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("totalCount", "$totalCount").append("itemId",
											"$prescription.items.drugId"))),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("drugId", "$totalCount._id").append("itemId", "$itemId"))
													.append("itemId", new BasicDBObject("$first", "$itemId"))
													.append("drug", new BasicDBObject("$first", "$totalCount"))
													.append("Count", new BasicDBObject("$sum", 1)))),

							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("name", "$drug.drugName").append("totalCount",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("itemId", "$drug._id"))
															.append("then", "$Count").append("else", 0))))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "totalCount"))

					);
				}

				break;
			}
			case DIAGNOSTICTEST: {
				itemCriteria.and("totalCount.hospitalId").is(new ObjectId(hospitalId)).and("totalCount.locationId")
						.is(new ObjectId(locationId));

				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					itemCriteria = itemCriteria.and("totalCount.testName").regex(searchTerm, "i");
				}
				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.unwind("diagnosticTests"),
							new CustomAggregationOperation(
									new BasicDBObject("$group", new BasicDBObject("_id", "$diagnosticTests.testId"))
											.append("itemId", new BasicDBObject("$first", "$diagnosticTests.testId"))),

							Aggregation.lookup("diagnostic_test_cl", "doctorid", "doctorid", "totalCount"),

							Aggregation.unwind("totalCount"), Aggregation.match(itemCriteria),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id", "$totalCount._id")
											.append("name", new BasicDBObject("$first", "$totalCount.testName"))

											.append("totalCount", new BasicDBObject("$sum",
													new BasicDBObject("$cond", new Object[] { new BasicDBObject("$eq",
															new Object[] {
																	new BasicDBObject("$itemId", "$totalCount._id") }),
															1, 0 }))))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "totalCount")),
							Aggregation.skip((page) * size), Aggregation.limit(size));
				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.unwind("diagnosticTests"),
							new CustomAggregationOperation(
									new BasicDBObject("$group", new BasicDBObject("_id", "$diagnosticTests.testId"))
											.append("itemId", new BasicDBObject("$first", "$diagnosticTests.testId"))),

							Aggregation.lookup("diagnostic_test_cl", "doctorid", "doctorid", "totalCount"),

							Aggregation.unwind("totalCount"), Aggregation.match(itemCriteria),

							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id", "$totalCount._id")
											.append("name", new BasicDBObject("$first", "$totalCount.testName"))

											.append("totalCount", new BasicDBObject("$sum",
													new BasicDBObject("$cond", new Object[] { new BasicDBObject("$eq",
															new Object[] {
																	new BasicDBObject("$itemId", "$totalCount._id") }),
															1, 0 }))))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "totalCount")));
				}

				break;
			}
			default:
				break;
			}

			response = mongoTemplate.aggregate(aggregation, DoctorClinicProfileCollection.class,
					DoctorPrescriptionItemAnalyticResponse.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Prescription items analytic");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting Prescription items analytic");
		}
		return response;

	}

	@Override
	public DoctorprescriptionAnalyticResponse getPrescriptionAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate) {
		DoctorprescriptionAnalyticResponse data = null;
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
			criteria = getCriteria(doctorId, locationId, hospitalId).and("discarded").is(false);

			fromTime = new DateTime(from);
			toTime = new DateTime(to);
			// it take lot of time

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "totalPrescription"),
					Aggregation.unwind("totalPrescription"),
					Aggregation.match(new Criteria("totalPrescription.locationId").is(new ObjectId(locationId))
							.and("totalPrescription.hospitalId").is(new ObjectId(hospitalId))),
					Aggregation.lookup("prescription_cl", "doctorId", "doctorId", "totalPrescriptionCreated"),

					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", "$_id")
									.append("totalPrescriptionCreated",
											new BasicDBObject("$first", "$totalPrescriptionCreated"))
									.append("totalPrescription", new BasicDBObject("$sum", 1)))),

					Aggregation.unwind("totalPrescriptionCreated"),

					Aggregation.match(new Criteria("totalPrescriptionCreated.locationId").is(new ObjectId(locationId))
							.and("totalPrescriptionCreated.hospitalId").is(new ObjectId(hospitalId))
							.and("totalPrescriptionCreated.adminCreatedTime").gte(fromTime).lte(toTime)),

					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", "$_id")
									.append("totalPrescription", new BasicDBObject("$first", "$totalPrescription"))
									.append("totalPrescriptionCreated", new BasicDBObject("$sum", 1)))));
			// trying with query

			data = new DoctorprescriptionAnalyticResponse();
			data.setTotalPrescription((int) mongoTemplate.count(new Query(criteria), PrescriptionCollection.class));
			criteria.and("adminCreatedTime").gte(fromTime).lte(toTime);
			data.setTotalPrescriptionCreated(
					(int) mongoTemplate.count(new Query(criteria), PrescriptionCollection.class));
			// mongoTemplate.aggregate(aggregation,
			// DoctorClinicProfileCollection.class,
			// DoctorprescriptionAnalyticResponse.class).getUniqueMappedResult();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Prescription analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Prescription analytic");
		}
		return data;

	}

	@Override
	public DoctorPatientAnalyticResponse getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate) {
		DoctorPatientAnalyticResponse data = new DoctorPatientAnalyticResponse();
		try {
			Criteria criteria = null;
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			Date lastdate = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0);
				to = new Date();
			}
			long diff = to.getTime() - from.getTime();
			long diffDays = (diff / (24 * 60 * 60 * 1000));
			lastdate = new Date(from.getTime() - ((diffDays + 1) * (24 * 60 * 60 * 1000)));

			DateTime last = new DateTime(lastdate);
			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria = getCriteria(null, locationId, hospitalId);
			data.setTotalPatient((int) mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user"),
							Aggregation.sort(Direction.DESC, "createdTime")),
					PatientCollection.class, PatientCollection.class).getMappedResults().size());

			criteria = getCriteria(null, locationId, hospitalId).and("createdTime").gte(from).lte(to);
			data.setTotalNewPatient((int) mongoTemplate.count(new Query(criteria), PatientCollection.class));

			// hike in patient
			int total = 0;

			if (data.getTotalNewPatient() > 0) {
				// hike in patient

				criteria = getCriteria(null, locationId, hospitalId).and("createdTime").gte(lastdate).lte(from);
				total = (int) mongoTemplate.count(new Query(criteria), PatientCollection.class);
				data.setChangeInTotalPatientInPercent(total);

			}
			// visited patient
			criteria = getCriteria(null, locationId, hospitalId).and("visit.adminCreatedTime").gte(from).lte(to)
					.and("visit.locationId").is(new ObjectId(locationId)).and("visit.discarded").is(false)
					.and("visit.hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("visit.doctorId").is(new ObjectId(doctorId));
			}

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("patient_visit_cl", "userId", "patientId", "visit"), Aggregation.unwind("visit"),
					Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$_id"))));
			total = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientCollection.class)
					.getMappedResults().size();
			data.setTotalVisitedPatient(total);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting Patient analytic");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting Patient analytic");
		}
		return data;
	}

	

	@Override
	public List<?> getMostPrescribedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<?> response = null;
		try {
			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

			case DRUGS: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescribedDrugs(doctorId, locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
					break;
				} else
					response = getMostPrescribedDrugsByDate(doctorId, locationId, hospitalId, fromDate, toDate,
							searchType, page, size);
				break;
			}
			case DIAGNOSTICTEST: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescribedLabTests(locationId, hospitalId, fromDate, toDate, searchType, page,
							size);
					break;
				} else
					response = getMostPrescribedLabTestsByDate(locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed prescription items");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting most prescribed prescription items");
		}
		return response;
	}

	private List<?> getMostPrescribedLabTestsByDate(String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DiagnosticTestsAnalyticsData> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(0);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(0);
				to = new Date();
			}
			fromTime = new DateTime(from);
			toTime = new DateTime(to);
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			}
			criteria.and("discarded").is(false);
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("tests", new BasicDBObject("$push", "$tests"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("tests", new BasicDBObject("$push", "$tests"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("tests", new BasicDBObject("$push", "$tests"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("tests", new BasicDBObject("$push", "$tests"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

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
										.append("tests", new BasicDBObject("$push", "$tests"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("date", new BasicDBObject("$first", "$date"))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("tests.locationId", "$test.locationId").append("tests.id", "$test.id")
										.append("tests.hospitalId", "$test.hospitalId")
										.append("tests.testName", "$test.testName")
										.append("tests.explanation", "$test.explanation")
										.append("tests.discarded", "$test.discarded")
										.append("tests.specimen", "$test.specimen").append("tests.code", "$test.code")
										.append("tests.createdTime", "$test.createdTime")
										.append("tests.updatedTime", "$test.updatedTime")
										.append("tests.createdBy", "$test.createdBy").append("tests.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("tests.locationId", "$test.locationId").append("tests.id", "$test.id")
										.append("tests.hospitalId", "$test.hospitalId")
										.append("tests.testName", "$test.testName")
										.append("tests.explanation", "$test.explanation")
										.append("tests.discarded", "$test.discarded")
										.append("tests.specimen", "$test.specimen").append("tests.code", "$test.code")
										.append("tests.createdTime", "$test.createdTime")
										.append("tests.updatedTime", "$test.updatedTime")
										.append("tests.createdBy", "$test.createdBy").append("tests.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"));
			}
			response = mongoTemplate
					.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTestsAnalyticsData.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed lab tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed lab tests");
		}
		return response;
	}

	private List<?> getMostPrescribedDrugsByDate(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DrugsAnalyticsData> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
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
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			}
			criteria.and("discarded").is(false);
			AggregationOperation aggregationOperation = null;
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("day", new BasicDBObject("$first", "$day"))
											.append("drugs", new BasicDBObject("$push", "$drugs"))
											.append("month", new BasicDBObject("$first", "$month"))
											.append("year", new BasicDBObject("$first", "$year"))
											.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("day", new BasicDBObject("$first", "$day"))
													.append("drugs", new BasicDBObject("$push", "$drugs"))
													.append("month", new BasicDBObject("$first", "$month"))
													.append("year", new BasicDBObject("$first", "$year"))
													.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("drugs", new BasicDBObject("$push", "$drugs"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("drugs", new BasicDBObject("$push", "$drugs"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

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
										.append("drugs", new BasicDBObject("$push", "$drugs"))
										.append("month", new BasicDBObject("$first", "$month"))
										.append("year", new BasicDBObject("$first", "$year"))
										.append("date", new BasicDBObject("$first", "$date"))));
			}
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("drugs.locationId", "$drug.locationId")
										.append("drugs.hospitalId", "$drug.hospitalId").append("drugs.id", "$drug.id")
										.append("drugs.doctorId", "$drug.doctorId")
										.append("drugs.drugName", "$drug.drugName")
										.append("drugs.drugType", "$drug.drugType")
										.append("drugs.explanation", "$drug.explanation")
										.append("drugs.discarded", "$drug.discarded")
										.append("drugs.duration", "$drug.duration")
										.append("drugs.dosage", "$drug.dosage")
										.append("drugs.dosageTime", "$drug.dosageTime")
										.append("drugs.direction", "$drug.direction")
										.append("drugs.categories", "$drug.categories")
										.append("drugs.rankingCount", "$drug.rankingCount")
										.append("drugs.genericNames", "$drug.genericNames")
										.append("drugs.drugCode", "$drug.drugCode")
										.append("drugs.createdTime", "$drug.createdTime")
										.append("drugs.updatedTime", "$drug.updatedTime")
										.append("drugs.createdBy", "$drug.createdBy").append("drugs.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"),
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new BasicDBObject("$project",
								new BasicDBObject("drugs.locationId", "$drug.locationId").append("drugs.id", "$drug.id")
										.append("drugs.hospitalId", "$drug.hospitalId")
										.append("drugs.doctorId", "$drug.doctorId")
										.append("drugs.drugName", "$drug.drugName")
										.append("drugs.drugType", "$drug.drugType")
										.append("drugs.explanation", "$drug.explanation")
										.append("drugs.discarded", "$drug.discarded")
										.append("drugs.duration", "$drug.duration")
										.append("drugs.dosage", "$drug.dosage")
										.append("drugs.dosageTime", "$drug.dosageTime")
										.append("drugs.direction", "$drug.direction")
										.append("drugs.categories", "$drug.categories")
										.append("drugs.rankingCount", "$drug.rankingCount")
										.append("drugs.genericNames", "$drug.genericNames")
										.append("drugs.drugCode", "$drug.drugCode")
										.append("drugs.createdTime", "$drug.createdTime")
										.append("drugs.updatedTime", "$drug.updatedTime")
										.append("drugs.createdBy", "$drug.createdBy").append("drugs.count", "$count")
										.append("date", "$createdTime")
										.append("day", new BasicDBObject("$dayOfMonth", "$createdTime"))
										.append("month", new BasicDBObject("$month", "$createdTime"))
										.append("year", new BasicDBObject("$year", "$createdTime"))
										.append("week", new BasicDBObject("$week", "$createdTime")))),
						aggregationOperation, Aggregation.sort(Direction.DESC, "createdTime"));
			}

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DrugsAnalyticsData.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

	private List<?> getMostPrescribedLabTests(String locationId, String hospitalId, String fromDate, String toDate,
			String searchType, int page, int size) {
		List<DiagnosticTest> response = null;
		try {
			Criteria criteria = new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));
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
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			}
			criteria.and("discarded").is(false);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId").append("count",
										new BasicDBObject("$sum", 1)))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),

						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$test.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$test.hospitalId"))
										.append("testName", new BasicDBObject("$first", "$test.testName"))
										.append("explanation", new BasicDBObject("$first", "$test.explanation"))
										.append("discarded", new BasicDBObject("$first", "$test.discarded"))
										.append("specimen", new BasicDBObject("$first", "$test.specimen"))
										.append("code", new BasicDBObject("$first", "$test.code"))
										.append("createdTime", new BasicDBObject("$first", "$test.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$test.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$test.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("$diagnosticTests"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId").append("count",
										new BasicDBObject("$sum", 1)))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$test.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$test.hospitalId"))
										.append("testName", new BasicDBObject("$first", "$test.testName"))
										.append("explanation", new BasicDBObject("$first", "$test.explanation"))
										.append("discarded", new BasicDBObject("$first", "$test.discarded"))
										.append("specimen", new BasicDBObject("$first", "$test.specimen"))
										.append("code", new BasicDBObject("$first", "$test.code"))
										.append("createdTime", new BasicDBObject("$first", "$test.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$test.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$test.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"));
			}
			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTest.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed lab tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed lab tests");
		}
		return response;

	}

	private List<Drug> getMostPrescribedDrugs(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<Drug> response = null;
		try {
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));

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
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria = criteria.and("createdTime").gte(fromTime).lte(toTime);
			}

			criteria.and("discarded").is(false);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("$items"),
						Aggregation.group("$items.drugId").count().as("count"),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),

						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$drug.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$drug.hospitalId"))
										.append("doctorId", new BasicDBObject("$first", "$drug.doctorId"))
										.append("drugName", new BasicDBObject("$first", "$drug.drugName"))
										.append("drugType", new BasicDBObject("$first", "$drug.drugType"))
										.append("explanation", new BasicDBObject("$first", "$drug.explanation"))
										.append("discarded", new BasicDBObject("$first", "$drug.discarded"))
										.append("duration", new BasicDBObject("$first", "$drug.duration"))
										.append("dosage", new BasicDBObject("$first", "$drug.dosage"))
										.append("dosageTime", new BasicDBObject("$first", "$drug.dosageTime"))
										.append("direction", new BasicDBObject("$first", "$drug.direction"))
										.append("categories", new BasicDBObject("$first", "$drug.categories"))
										.append("rankingCount", new BasicDBObject("$max", "$drug.rankingCount"))
										.append("genericNames", new BasicDBObject("$first", "$drug.genericNames"))
										.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
										.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
						Aggregation.group("$items.drugId").count().as("count"),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),

						new CustomAggregationOperation(new BasicDBObject("$group",
								new BasicDBObject("id", "$_id")
										.append("locationId", new BasicDBObject("$first", "$drug.locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$drug.hospitalId"))
										.append("doctorId", new BasicDBObject("$first", "$drug.doctorId"))
										.append("drugName", new BasicDBObject("$first", "$drug.drugName"))
										.append("drugType", new BasicDBObject("$first", "$drug.drugType"))
										.append("explanation", new BasicDBObject("$first", "$drug.explanation"))
										.append("discarded", new BasicDBObject("$first", "$drug.discarded"))
										.append("duration", new BasicDBObject("$first", "$drug.duration"))
										.append("dosage", new BasicDBObject("$first", "$drug.dosage"))
										.append("dosageTime", new BasicDBObject("$first", "$drug.dosageTime"))
										.append("direction", new BasicDBObject("$first", "$drug.direction"))
										.append("categories", new BasicDBObject("$first", "$drug.categories"))
										.append("rankingCount", new BasicDBObject("$max", "$drug.rankingCount"))
										.append("genericNames", new BasicDBObject("$first", "$drug.genericNames"))
										.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
										.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
										.append("count", new BasicDBObject("$first", "$count")))),

						Aggregation.sort(Direction.DESC, "count"));
			}

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, Drug.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting most prescribed drugs");
		}
		return response;
	}

	@Override
	public List<InvoiceAnalyticsDataDetailResponse> getIncomeDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<InvoiceAnalyticsDataDetailResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				to = new Date(Long.parseLong(fromDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("invoiceDate").lte(toTime);

			}
			criteria.and("discarded").is(false);
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("uniqueInvoiceId", "$uniqueInvoiceId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
							.append("cost", new BasicDBObject("$sum", "$resultantCost"))
							.append("tax", new BasicDBObject("$sum", "$resultantTax"))
							.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			if (size > 0) {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria2),
						new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject(
								"resultantDiscount",
								new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
										Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
												.append("then", new BasicDBObject("$multiply",
														Arrays.asList(new BasicDBObject("$divide",
																Arrays.asList("$totalDiscount.value", 100)),
																"$totalCost")))
												.append("else", "$totalDiscount.value")))
														.append("resultantCost", "$totalCost")
														.append("invoiceDate", "$invoiceDate")
														.append("resultantTax", new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalTax.unit",
																				UnitType.PERCENT.name()))).append(
																						"then",
																						new BasicDBObject("$multiply",
																								Arrays.asList(
																										new BasicDBObject(
																												"$divide",
																												Arrays.asList(
																														"$totalTax.value",
																														100)),
																										"$totalCost")))
																						.append("else",
																								"$totalTax.value")))
														.append("patientName", "$patient.localPatientName")
														.append("uniqueInvoiceId", "$uniqueInvoiceId")
														.append("resultantInvoiceAmount", "$grandTotal"))),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate")),
						Aggregation.skip((page) * size), Aggregation.limit(size)), DoctorPatientInvoiceCollection.class,
						InvoiceAnalyticsDataDetailResponse.class).getMappedResults();
			} else {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria2),
						new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject(
								"resultantDiscount",
								new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
										Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
												.append("then", new BasicDBObject("$multiply",
														Arrays.asList(new BasicDBObject("$divide",
																Arrays.asList("$totalDiscount.value", 100)),
																"$totalCost")))
												.append("else", "$totalDiscount.value")))
														.append("resultantCost", "$totalCost")
														.append("invoiceDate", "$invoiceDate")
														.append("resultantTax", new BasicDBObject("$cond",
																new BasicDBObject("if", new BasicDBObject("$eq",
																		Arrays.asList("$totalTax.unit",
																				UnitType.PERCENT.name()))).append(
																						"then",
																						new BasicDBObject("$multiply",
																								Arrays.asList(
																										new BasicDBObject(
																												"$divide",
																												Arrays.asList(
																														"$totalTax.value",
																														100)),
																										"$totalCost")))
																						.append("else",
																								"$totalTax.value")))
														.append("patientName", "$patient.localPatientName")
														.append("uniqueInvoiceId", "$uniqueInvoiceId")
														.append("resultantInvoiceAmount", "$grandTotal"))),
						aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "invoiceDate"))),
						DoctorPatientInvoiceCollection.class, InvoiceAnalyticsDataDetailResponse.class)
						.getMappedResults();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting income analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting income analytics data");
		}
		return response;

	}

	@Override
	public List<IncomeAnalyticsDataResponse> getIncomeAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<IncomeAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {

				fromTime = new DateTime(new Date(Long.parseLong(fromDate)));
				toTime = new DateTime(new Date(Long.parseLong(toDate)));
				criteria.and("invoiceDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				fromTime = new DateTime(new Date(Long.parseLong(fromDate)));

				criteria.and("invoiceDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				toTime = new DateTime(new Date(Long.parseLong(toDate)));
				criteria.and("invoiceDate").lte(toTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				switch (queryType) {
				case "DOCTORS": {
					response = getInvoiceIncomeDataByDoctors(searchType, page, size, criteria);
					break;
				}
				case "SERVICES": {
					response = getInvoiceIncomeDataByServices(searchType, page, size, criteria);
					break;
				}
				case "PATIENTGROUP": {
					response = getInvoiceIncomeDataByPatientGroup(searchType, page, size, fromTime, toTime, doctorId,
							locationId, hospitalId);
					break;
				}
				default: {
					response = getInvoiceIncomeDataByDate(searchType, page, size, criteria);
					break;
				}
				}
			} else {
				response = getInvoiceIncomeDataByDate(searchType, page, size, criteria);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting invoice income analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting invoice income analytics data");
		}
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByPatientGroup(String searchType, int page, int size,
			DateTime start, DateTime end, String doctorId, String locationId, String hospitalId) {
		List<IncomeAnalyticsDataResponse> response = null;
		try {
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("groupName", "$groupName"))
							.append("count", new BasicDBObject("$sum", 1))
							.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
							.append("cost", new BasicDBObject("$sum", "$resultantCost"))
							.append("tax", new BasicDBObject("$sum", "$resultantTax"))
							.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
							.append("date", new BasicDBObject("$first", "$invoiceDate"))));

			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("group.doctorId").in(new ObjectId(doctorId));
				criteria2.and("invoice.doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("group.locationId").is(new ObjectId(locationId));
				criteria2.and("invoice.locationId").is(new ObjectId(locationId));
			}

			if (start != null) {
				criteria2.and("invoice.invoiceDate").gt(start).lte(end);
			}
			criteria2.and("invoice.discarded").is(false);
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("group_cl", "groupId", "_id", "group"), Aggregation.unwind("group"),
					Aggregation.match(criteria),
					Aggregation.lookup("doctor_patient_invoice_cl", "patientId", "patientId", "invoice"),
					Aggregation.unwind("invoice"), Aggregation.match(criteria2),
					Aggregation.unwind("invoice.invoiceItems"),
					new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("resultantDiscount",
							new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
									Arrays.asList("$invoice.totalDiscount.unit", UnitType.PERCENT.name()))).append(
											"then",
											new BasicDBObject("$multiply", Arrays.asList(
													new BasicDBObject("$divide",
															Arrays.asList("$invoice.totalDiscount.value", 100)),
													"$invoice.totalCost")))
											.append("else", "$invoice.totalDiscount.value")))
													.append("resultantCost", "$invoice.totalCost")
													.append("invoiceDate", "$invoice.invoiceDate")
													.append("resultantTax", new BasicDBObject("$cond",
															new BasicDBObject("if", new BasicDBObject("$eq", Arrays
																	.asList("$invoice.totalTax.unit", UnitType.PERCENT
																			.name()))).append("then", new BasicDBObject(
																					"$multiply",
																					Arrays.asList(new BasicDBObject(
																							"$divide", Arrays.asList(
																									"$invoice.totalTax.value",
																									100)),
																							"$invoice.totalCost")))
																					.append("else",
																							"$invoice.totalTax.value")))
													.append("resultantInvoiceAmount", "$invoice.grandTotal")
													.append("groupId", "$group._id")
													.append("groupName", "$group.name"))),
					aggregationOperation);
			// , Aggregation.skip(page * size), Aggregation.limit(size)
			// new CustomAggregationOperation(new BasicDBObject("$group", new
			// BasicDBObject("_id", new BasicDBObject("groupName",
			// "$group.name"))
			// .append("count", new BasicDBObject("$sum", 1)))));

			response = mongoTemplate
					.aggregate(aggregation, PatientGroupCollection.class, IncomeAnalyticsDataResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting income analytics data by patient group");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting income analytics data by patient group");
		}
		return response;
	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByServices(String searchType, int page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;
		criteria.and("discarded").is(false);
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("itemId", "$itemId"))
						.append("serviceName", new BasicDBObject("$first", "$serviceName"))
						.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
						.append("cost", new BasicDBObject("$sum", "$resultantCost"))
						.append("tax", new BasicDBObject("$sum", "$resultantTax"))
						.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
						.append("date", new BasicDBObject("$first", "$invoiceDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("invoiceItems"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("itemId", "$invoiceItems.itemId")
															.append("serviceName", "$invoiceItems.name"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("invoiceItems"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("itemId", "$invoiceItems.itemId")
															.append("serviceName", "$invoiceItems.name"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDoctors(String searchType, int page, int size,
			Criteria criteria) {
		List<IncomeAnalyticsDataResponse> response = null;
		criteria.and("discarded").is(false);
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("doctorId", "$doctorId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
						.append("cost", new BasicDBObject("$sum", "$resultantCost"))
						.append("tax", new BasicDBObject("$sum", "$resultantTax"))
						.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
						.append("date", new BasicDBObject("$first", "$invoiceDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("doctorId", "$doctorId")
															.append("title", "$user.title")
															.append("doctorName", "$user.firstName"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("doctorId", "$doctorId")
															.append("title", "$user.title")
															.append("doctorName", "$user.firstName"))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	private List<IncomeAnalyticsDataResponse> getInvoiceIncomeDataByDate(String searchType, int page, int size,
			Criteria criteria) {
		criteria.and("discarded").is(false);
		List<IncomeAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
										.append("cost", new BasicDBObject("$sum", "$resultantCost"))
										.append("tax", new BasicDBObject("$sum", "$resultantTax"))
										.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
										.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;
			}

			case WEEKLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
										.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
										.append("cost", new BasicDBObject("$sum", "$resultantCost"))
										.append("tax", new BasicDBObject("$sum", "$resultantTax"))
										.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
										.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;
			}

			case MONTHLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
								.append("cost", new BasicDBObject("$sum", "$resultantCost"))
								.append("tax", new BasicDBObject("$sum", "$resultantTax"))
								.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
								.append("date", new BasicDBObject("$first", "$invoiceDate"))));
				break;
			}
			case YEARLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("year", "$year"))
								.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
								.append("cost", new BasicDBObject("$sum", "$resultantCost"))
								.append("tax", new BasicDBObject("$sum", "$resultantTax"))
								.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
								.append("date", new BasicDBObject("$first", "$invoiceDate"))));

				break;

			}
			default:
				break;
			}
		else {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("discount", new BasicDBObject("$sum", "$resultantDiscount"))
									.append("cost", new BasicDBObject("$sum", "$resultantCost"))
									.append("tax", new BasicDBObject("$sum", "$resultantTax"))
									.append("invoiceAmount", new BasicDBObject("$sum", "$resultantInvoiceAmount"))
									.append("date", new BasicDBObject("$first", "$invoiceDate"))));
		}

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$invoiceDate"))
															.append("month",
																	new BasicDBObject("$month", "$invoiceDate"))
															.append("year", new BasicDBObject("$year", "$invoiceDate"))
															.append("week",
																	new BasicDBObject("$week", "$invoiceDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("resultantDiscount",
									new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
											Arrays.asList("$totalDiscount.unit", UnitType.PERCENT.name())))
													.append("then", new BasicDBObject("$multiply",
															Arrays.asList(new BasicDBObject("$divide",
																	Arrays.asList("$totalDiscount.value", 100)),
																	"$totalCost")))
													.append("else", "$totalDiscount.value")))
															.append("resultantCost", "$totalCost")
															.append("invoiceDate", "$invoiceDate")
															.append("resultantTax", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$totalTax.unit",
																					UnitType.PERCENT.name()))).append(
																							"then",
																							new BasicDBObject(
																									"$multiply",
																									Arrays.asList(
																											new BasicDBObject(
																													"$divide",
																													Arrays.asList(
																															"$totalTax.value",
																															100)),
																											"$totalCost")))
																							.append("else",
																									"$totalTax.value")))
															.append("resultantInvoiceAmount", "$grandTotal")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$invoiceDate"))
															.append("month",
																	new BasicDBObject("$month", "$invoiceDate"))
															.append("year", new BasicDBObject("$year", "$invoiceDate"))
															.append("week",
																	new BasicDBObject("$week", "$invoiceDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "invoiceDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientInvoiceCollection.class, IncomeAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	@Override
	public List<PaymentDetailsAnalyticsDataResponse> getPaymentDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<PaymentDetailsAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();

			Criteria criteria2 = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {

				DateTime start = new DateTime(new Date(Long.parseLong(fromDate)));
				criteria.and("receivedDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {

				DateTime end = new DateTime(new Date(Long.parseLong(toDate)));
				criteria.and("receivedDate").lte(end);
			}
			criteria.and("discarded").is(false);

			if (size > 0) {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
												Aggregation.unwind("patient"), Aggregation.match(criteria2),
												new CustomAggregationOperation(
														new BasicDBObject("$group",
																new BasicDBObject("_id", "$uniqueReceiptId")
																		.append("date",
																				new BasicDBObject("$first",
																						"$receivedDate"))
																		.append("patientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("uniqueReceiptId",
																				new BasicDBObject("$first",
																						"$uniqueReceiptId"))
																		.append("uniqueInvoiceId",
																				new BasicDBObject("$first",
																						"$uniqueInvoiceId"))
																		.append("amountPaid",
																				new BasicDBObject("$first",
																						"$amountPaid"))
																		.append("modeOfPayment",
																				new BasicDBObject("$first",
																						"$modeOfPayment"))
																		.append("receiptType",
																				new BasicDBObject("$first",
																						"$receiptType")))),
												Aggregation.sort(new Sort(Sort.Direction.DESC, "receivedDate")),
												Aggregation.skip((page) * size), Aggregation.limit(size)),
								DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
						.getMappedResults();
			} else {
				response = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("patient_cl", "userId", "patientId", "patient"),
												Aggregation.unwind("patient"), Aggregation.match(criteria2),
												new CustomAggregationOperation(
														new BasicDBObject("$group",
																new BasicDBObject("_id", "$uniqueReceiptId")
																		.append("date",
																				new BasicDBObject("$first",
																						"$receivedDate"))
																		.append("patientName",
																				new BasicDBObject("$first",
																						"$patient.localPatientName"))
																		.append("uniqueReceiptId",
																				new BasicDBObject("$first",
																						"$uniqueReceiptId"))
																		.append("uniqueInvoiceId",
																				new BasicDBObject("$first",
																						"$uniqueInvoiceId"))
																		.append("amountPaid",
																				new BasicDBObject("$first",
																						"$amountPaid"))
																		.append("modeOfPayment",
																				new BasicDBObject("$first",
																						"$modeOfPayment"))
																		.append("receiptType",
																				new BasicDBObject("$first",
																						"$receiptType")))),
												Aggregation.sort(new Sort(Sort.Direction.DESC, "receivedDate"))),
								DoctorPatientReceiptCollection.class, PaymentDetailsAnalyticsDataResponse.class)
						.getMappedResults();

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting payment details analytics data");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While getting payment details analytics data");
		}
		return response;
	}

	@Override
	public List<PaymentAnalyticsDataResponse> getPaymentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<PaymentAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("receivedDate").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				fromTime = new DateTime(from);
				criteria.and("receivedDate").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("receivedDate").lte(toTime);
			}
			switch (queryType) {
			case "DOCTORS": {
				response = getPaymentDataByDoctors(page, size, criteria);
				break;
			}
			case "PAYMENTMODES": {
				response = getPaymentDataByPaymentModes(page, size, criteria);
				break;
			}
			default: {
				response = getPaymentByDate(searchType, page, size, criteria);
				break;
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting payment analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting payment analytics data");
		}
		return response;
	}

	private List<PaymentAnalyticsDataResponse> getPaymentDataByDoctors(int page, int size, Criteria criteria) {
		criteria.and("discarded").is(false);
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("doctorId", "$doctorId"))
						.append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("cash", new BasicDBObject("$sum", "$cash"))
						.append("card", new BasicDBObject("$sum", "$card"))
						.append("online", new BasicDBObject("$sum", "$online"))
						.append("wallet", new BasicDBObject("$sum", "$wallet"))
						.append("total", new BasicDBObject("$sum", "$total"))
						.append("date", new BasicDBObject("$first", "$receivedDate"))));

		Aggregation aggregation = null;
		if (size > 0) {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("cash",
											new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name()))).append(
															"then", "$amountPaid")
															.append("else", 0))).append("card", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
																	.append("online", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.ONLINE
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("total", "$amountPaid")
																	.append("doctorId", "$doctorId")
																	.append("date", "$receivedDate")
																	.append("title", "$user.title")
																	.append("doctorName", "$user.firstName"))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
							Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
							new CustomAggregationOperation(new BasicDBObject("$project",
									new BasicDBObject("cash",
											new BasicDBObject("$cond", new BasicDBObject("if", new BasicDBObject("$eq",
													Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name()))).append(
															"then", "$amountPaid")
															.append("else", 0))).append("card", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
																	.append("online", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.ONLINE
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("wallet", new BasicDBObject("$cond",
																			new BasicDBObject("if", new BasicDBObject(
																					"$eq",
																					Arrays.asList("$modeOfPayment",
																							ModeOfPayment.WALLET
																									.name()))).append(
																											"then",
																											"$amountPaid")
																											.append("else",
																													0)))
																	.append("total", "$amountPaid")
																	.append("doctorId", "$doctorId")
																	.append("receivedDate", "$receivedDate")
																	.append("title", "$user.title")
																	.append("doctorName", "$user.firstName"))),
							aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	private List<PaymentAnalyticsDataResponse> getPaymentDataByPaymentModes(int page, int size, Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", new BasicDBObject("modeOfPayment", "$modeOfPayment"))
						.append("modeOfPayment", new BasicDBObject("$first", "$modeOfPayment"))
						.append("total", new BasicDBObject("$sum", "$total"))));

		Aggregation aggregation = null;
		criteria.and("discarded").is(false);
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("total", "$amountPaid").append("modeOfPayment", "$modeOfPayment"))),
					aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$project",
							new BasicDBObject("total", "$amountPaid").append("modeOfPayment", "$modeOfPayment"))),
					aggregationOperation, Aggregation.sort(Direction.ASC, "modeOfPayment"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;

	}

	// this service uses for get payment mode info
	private List<PaymentAnalyticsDataResponse> getPaymentByDate(String searchType, int page, int size,
			Criteria criteria) {
		List<PaymentAnalyticsDataResponse> response = null;
		AggregationOperation aggregationOperation = null;
		if (!DPDoctorUtils.anyStringEmpty(searchType))
			switch (SearchType.valueOf(searchType.toUpperCase())) {

			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
										.append("cash", new BasicDBObject("$sum", "$cash"))
										.append("card", new BasicDBObject("$sum", "$card"))
										.append("online", new BasicDBObject("$sum", "$online"))
										.append("wallet", new BasicDBObject("$sum", "$wallet"))
										.append("total", new BasicDBObject("$sum", "$total"))
										.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case WEEKLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id",
								new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
										.append("cash", new BasicDBObject("$sum", "$cash"))
										.append("card", new BasicDBObject("$sum", "$card"))
										.append("online", new BasicDBObject("$sum", "$online"))
										.append("wallet", new BasicDBObject("$sum", "$wallet"))
										.append("total", new BasicDBObject("$sum", "$total"))
										.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;
			}

			case MONTHLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("cash", new BasicDBObject("$sum", "$cash"))
								.append("card", new BasicDBObject("$sum", "$card"))
								.append("online", new BasicDBObject("$sum", "$online"))
								.append("wallet", new BasicDBObject("$sum", "$wallet"))
								.append("total", new BasicDBObject("$sum", "$total"))
								.append("date", new BasicDBObject("$first", "$receivedDate"))));
				break;
			}
			case YEARLY: {
				aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
						new BasicDBObject("_id", new BasicDBObject("year", "$year"))
								.append("cash", new BasicDBObject("$sum", "$cash"))
								.append("card", new BasicDBObject("$sum", "$card"))
								.append("online", new BasicDBObject("$sum", "$online"))
								.append("wallet", new BasicDBObject("$sum", "$wallet"))
								.append("total", new BasicDBObject("$sum", "$total"))
								.append("date", new BasicDBObject("$first", "$receivedDate"))));

				break;

			}
			default:
				break;
			}
		else {
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("cash", new BasicDBObject("$sum", "$cash"))
									.append("card", new BasicDBObject("$sum", "$card"))
									.append("online", new BasicDBObject("$sum", "$online"))
									.append("wallet", new BasicDBObject("$sum", "$wallet"))
									.append("total", new BasicDBObject("$sum", "$total"))
									.append("date", new BasicDBObject("$first", "$receivedDate"))));
		}

		Aggregation aggregation = null;
		criteria.and("discarded").is(false);
		if (size > 0) {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new BasicDBObject("$project", new BasicDBObject("cash", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0))).append(
															"card",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("online", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.ONLINE.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("wallet", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.WALLET.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("day", "$receivedDate")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$receivedDate"))
															.append("month",
																	new BasicDBObject("$month", "$receivedDate"))
															.append("year", new BasicDBObject("$year", "$receivedDate"))
															.append("week",
																	new BasicDBObject("$week", "$receivedDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"),
					Aggregation.skip(page * size), Aggregation.limit(size));
		} else {
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), new CustomAggregationOperation(
					new BasicDBObject("$project", new BasicDBObject("cash", new BasicDBObject("$cond",
							new BasicDBObject("if",
									new BasicDBObject("$eq",
											Arrays.asList("$modeOfPayment", ModeOfPayment.CASH.name())))
													.append("then", "$amountPaid").append("else", 0))).append(
															"card",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.CARD.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("online", new BasicDBObject(
																	"$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.ONLINE.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("wallet", new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$modeOfPayment",
																					ModeOfPayment.WALLET.name())))
																							.append("then",
																									"$amountPaid")
																							.append("else", 0)))
															.append("receivedDate", "$receivedDate")
															.append("day",
																	new BasicDBObject("$dayOfMonth", "$receivedDate"))
															.append("month",
																	new BasicDBObject("$month", "$receivedDate"))
															.append("year", new BasicDBObject("$year", "$receivedDate"))
															.append("week",
																	new BasicDBObject("$week", "$receivedDate")))),
					aggregationOperation, Aggregation.sort(Direction.DESC, "receivedDate"));
		}
		response = mongoTemplate
				.aggregate(aggregation, DoctorPatientReceiptCollection.class, PaymentAnalyticsDataResponse.class)
				.getMappedResults();
		return response;
	}

	@Override
	public List<AmountDueAnalyticsDataResponse> getAmountDueAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<AmountDueAnalyticsDataResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			Criteria criteria2 = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
				criteria2.and("patient.locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
				criteria2.and("patient.hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));
				fromTime = new DateTime(from);
				toTime = new DateTime(to);
				criteria.and("createdTime").gte(fromTime).lte(toTime);

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				fromTime = new DateTime(from);
				criteria.and("createdTime").gte(fromTime);
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				to = new Date(Long.parseLong(toDate));
				toTime = new DateTime(to);
				criteria.and("createdTime").lte(toTime);
			}
			criteria.and("discarded").is(false);
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(queryType)) {
				if (queryType.equalsIgnoreCase("PATIENT")) {
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"), Aggregation.match(criteria2),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount")
												.append("patientId", "$patientId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("patientName", "$patient.localPatientName")
												.append("pid", "$patient.PID"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$patientId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("patientName", new BasicDBObject("$first", "$patientName"))
												.append("pid", new BasicDBObject("$first", "$pid")))),
								Aggregation.skip(page * size), Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"), Aggregation.match(criteria2),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount")
												.append("patientId", "$patientId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("patientName", "$patient.localPatientName")
												.append("pid", "$patient.PID"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$patientId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("patientName", new BasicDBObject("$first", "$patientName"))
												.append("pid", new BasicDBObject("$first", "$pid")))));
					}
				} else if (queryType.equalsIgnoreCase("DOCTORS")) {
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount").append("doctorId", "$doctorId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("doctorName", "$doctor.firstName"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$doctorId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("doctorName", new BasicDBObject("$first", "$doctorName")))),
								Aggregation.skip(page * size), Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
								Aggregation.unwind("doctor"),
								new CustomAggregationOperation(new BasicDBObject("$project",
										new BasicDBObject("debitAmount", "$debitAmount")
												.append("creditAmount", "$creditAmount").append("doctorId", "$doctorId")
												.append("total",
														new BasicDBObject("$subtract",
																Arrays.asList("$debitAmount", "$creditAmount")))
												.append("doctorName", "$doctor.firstName"))),
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("_id", "$doctorId")
												.append("invoiced", new BasicDBObject("$sum", "$debitAmount"))
												.append("received", new BasicDBObject("$sum", "$creditAmount"))
												.append("amountDue", new BasicDBObject("$sum", "$total"))
												.append("doctorName", new BasicDBObject("$first", "$doctorName")))));
					}
				}
			} else {
				throw new BusinessException(ServiceError.Unknown, "Query Type cannot be null");
			}

			response = mongoTemplate
					.aggregate(aggregation, DoctorPatientLedgerCollection.class, AmountDueAnalyticsDataResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While getting amount due analytics data");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While getting amount due analytics data");
		}
		return response;
	}

	
	@Override
	public List<ExpenseCountResponse> getDoctorExpenseAnalytic(String doctorId, String searchType, String locationId,
			String hospitalId, Boolean discarded, String fromDate, String toDate, String expenseType,
			String paymentMode) {
		List<ExpenseCountResponse> response = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
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
				to = new Date(Long.parseLong(fromDate));
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}

			fromTime = new DateTime(from);

			toTime = new DateTime(to);

			Criteria criteria = new Criteria("toDate").gte(fromTime).lte(toTime);

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("expenseType").is(expenseType.toUpperCase());
			}
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			AggregationOperation aggregationOperation = null;
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("cost", "$cost"), Fields.field("toDate", "$toDate")));
			if (!DPDoctorUtils.anyStringEmpty(searchType))
				switch (SearchType.valueOf(searchType.toUpperCase())) {

				case DAILY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
											.append("cost", new BasicDBObject("$sum", "$cost"))
											.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;
				}

				case WEEKLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year")).append("cost", new BasicDBObject("$sum", "$cost"))
													.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("cost", new BasicDBObject("$sum", "$cost"))
									.append("toDate", new BasicDBObject("$first", "$toDate"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("_id", new BasicDBObject("year", "$year"))
									.append("cost", new BasicDBObject("$sum", "$cost"))
									.append("toDate", new BasicDBObject("$first", "$toDate"))));

					break;

				}
				default:
					break;
				}
			projectList.and("toDate").extractDayOfMonth().as("day").and("toDate").extractMonth().as("month")
					.and("toDate").extractYear().as("year").and("toDate").extractWeek().as("week");
			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), projectList, aggregationOperation),
					DoctorExpenseCollection.class, ExpenseCountResponse.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}