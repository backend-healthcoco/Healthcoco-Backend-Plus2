package com.dpdocter.services.v2.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.SearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.DiagnosticTestsAnalyticsData;
import com.dpdocter.response.DrugsAnalyticsData;
import com.dpdocter.services.v2.PrescriptionAnalyticsV2Service;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PrescriptionAnalyticsV2ServiceImpl implements PrescriptionAnalyticsV2Service {

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(PrescriptionAnalyticsV2ServiceImpl.class);

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
	public List<?> getMostPrescripedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page,
			int size) {
		List<?> response = null;
		try {
			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

			case DRUGS: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescripedDrugs(doctorId, locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
					break;
				} else
					response = getMostPrescripedDrugsByDate(doctorId, locationId, hospitalId, fromDate, toDate,
							searchType, page, size);
				break;
			}
			case DIAGNOSTICTEST: {
				if (!DPDoctorUtils.anyStringEmpty(queryType) && queryType.equalsIgnoreCase("TOP")) {
					response = getMostPrescripedLabTests(doctorId, locationId, hospitalId, fromDate, toDate, searchType,
							page, size);
					break;
				} else
					response = getMostPrescripedLabTestsByDate(doctorId, locationId, hospitalId, fromDate, toDate,
							searchType, page, size);
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

	private List<?> getMostPrescripedLabTestsByDate(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, int page, int size) {
		List<DiagnosticTestsAnalyticsData> response = null;
		try {
			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);
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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("tests", new BasicDBObject("$push", "$tests"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("tests", new BasicDBObject("$push", "$tests"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
						Aggregation.unwind("diagnosticTests"),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new Document("$project",
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
						Aggregation.skip((long) page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("diagnosticTests"),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
										.append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("diagnostic_test_cl", "_id", "_id", "test"), Aggregation.unwind("test"),
						new CustomAggregationOperation(new Document("$project",
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

	private List<?> getMostPrescripedDrugsByDate(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DrugsAnalyticsData> response = null;
		try {
			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);
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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id",
									new BasicDBObject("week", "$week").append("month", "$month").append("year",
											"$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("drugs", new BasicDBObject("$push", "$drugs"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));

					break;
				}

				case MONTHLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
									.append("day", new BasicDBObject("$first", "$day"))
									.append("drugs", new BasicDBObject("$push", "$drugs"))
									.append("month", new BasicDBObject("$first", "$month"))
									.append("year", new BasicDBObject("$first", "$year"))
									.append("date", new BasicDBObject("$first", "$date"))));
					break;
				}
				case YEARLY: {
					aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
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
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new Document("$project",
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
						Aggregation.skip((long) page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$items.drugId").append("count", new BasicDBObject("$sum", 1))
										.append("createdTime", new BasicDBObject("$first", "$createdTime")))),
						Aggregation.lookup("drug_cl", "_id", "_id", "drug"), Aggregation.unwind("drug"),
						new CustomAggregationOperation(new Document("$project",
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

	private List<?> getMostPrescripedLabTests(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<DiagnosticTest> response = null;
		try {
			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);
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
						Aggregation.unwind("diagnosticTests"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.unwind("test"),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("test", "$test")
										.append("count", "$diagnosticTests.testId").append("diagnosticTests",
												"$diagnosticTests"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
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
										.append("count", new BasicDBObject("$sum", 1)))),
						Aggregation.sort(Direction.DESC, "count"), Aggregation.skip((long) page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.unwind("diagnosticTests"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.unwind("test"),
						new CustomAggregationOperation(new Document("$project",
								new BasicDBObject("test", "$test")
										.append("count", "$diagnosticTests.testId").append("diagnosticTests",
												"$diagnosticTests"))),
						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("_id", "$diagnosticTests.testId")
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
										.append("count", new BasicDBObject("$sum", 1)))),

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

	private List<Drug> getMostPrescripedDrugs(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchType, int page, int size) {
		List<Drug> response = null;
		try {
			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);

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
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
								Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
								Aggregation.unwind("drug"),
								new CustomAggregationOperation(new Document("$project",
										new BasicDBObject("drug", "$drug").append("count", "$items.drugId")
												.append("drug", "$drug"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$items.drugId")
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
												.append("genericNames",
														new BasicDBObject("$first", "$drug.genericNames"))
												.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
												.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
												.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
												.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
												.append("count", new BasicDBObject("$sum", 1)))),
								Aggregation.sort(Direction.DESC, "count"), Aggregation.skip((long) page * size),
								Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
								Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
								Aggregation.unwind("drug"),
								new CustomAggregationOperation(new Document("$project",
										new BasicDBObject("drug", "$drug").append("count", "$items.drugId")
												.append("drug", "$drug"))),
								new CustomAggregationOperation(new Document("$group",
										new BasicDBObject("_id", "$items.drugId")
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
												.append("genericNames",
														new BasicDBObject("$first", "$drug.genericNames"))
												.append("drugCode", new BasicDBObject("$first", "$drug.drugCode"))
												.append("createdTime", new BasicDBObject("$first", "$drug.createdTime"))
												.append("updatedTime", new BasicDBObject("$first", "$drug.updatedTime"))
												.append("createdBy", new BasicDBObject("$first", "$drug.createdBy"))
												.append("count", new BasicDBObject("$sum", 1)))),

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
	public Integer countPrescripedItems(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String type) {
		Integer response = 0;
		try {
			switch (PrescriptionItems.valueOf(type.toUpperCase())) {

			case DRUGS: {
				response = countPrescripedDrugs(doctorId, locationId, hospitalId, fromDate, toDate);
				break;
			}
			case DIAGNOSTICTEST: {
				response = countPrescripedLabTests(doctorId, locationId, hospitalId, fromDate, toDate);
				break;
			}
			default:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting most prescriped Items");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While countting most prescriped Items");
		}
		return response;

	}

	private Integer countPrescripedLabTests(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate) {
		Integer response = 0;
		try {
			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);
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
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.unwind("diagnosticTests"),
					Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
					Aggregation.unwind("test"), new CustomAggregationOperation(
							new Document("$group", new BasicDBObject("_id", "$diagnosticTests.testId"))));

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, DiagnosticTest.class)
					.getMappedResults().size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting most prescribed lab tests");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While countting most prescribed lab tests");
		}
		return response;

	}

	private Integer countPrescripedDrugs(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate) {
		Integer response = 0;
		try {

			Criteria criteria = getCriteria(doctorId, locationId, hospitalId);

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

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.unwind("items"),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"), Aggregation.unwind("drug"),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$items.drugId"))));

			response = mongoTemplate.aggregate(aggregation, PrescriptionCollection.class, Drug.class).getMappedResults()
					.size();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While counting most prescribed drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While counting most prescribed drugs");
		}
		return response;
	}

}
