package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorStats;
import com.dpdocter.collections.DoctorProfileViewCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.response.DoctorStatisticsResponse;
import com.dpdocter.services.DoctorStatsService;

@Service
public class DoctorStatsServiceImpl implements DoctorStatsService{

	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(DoctorStatsServiceImpl.class);

	@Override
	@Transactional
	public DoctorStatisticsResponse getDoctorStats(String doctorId, String locationId, String type) {
		DoctorStatisticsResponse doctorStatisticsResponse = null;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		switch (type) {
		case "WEEK":
			int week = calendar.get(Calendar.WEEK_OF_YEAR);
			doctorStatisticsResponse = new DoctorStatisticsResponse();
			doctorStatisticsResponse.setCurrentProfileViewCount(getProfileViewCountsByWeek(doctorId, week));
			doctorStatisticsResponse.setPreviousProfileViewCount(getProfileViewCountsByWeek(doctorId, week - 1));
			doctorStatisticsResponse.setCurrentVisitCount(getPatientVisitStatsByWeek(doctorId, locationId, week));
			doctorStatisticsResponse.setPreviousVisitCount(getPatientVisitStatsByWeek(doctorId, locationId, week - 1));
			doctorStatisticsResponse.setCurrentRecommendationCount(getRecommendationCountByWeek(doctorId, week));
			doctorStatisticsResponse.setPreviousRecommendationCount(getRecommendationCountByWeek(doctorId, week - 1));
			break;

		case "MONTH":
			int month = calendar.get(Calendar.MONTH) + 1;
			doctorStatisticsResponse = new DoctorStatisticsResponse();
			doctorStatisticsResponse.setCurrentProfileViewCount(getProfileViewCountsByMonth(doctorId, month));
			doctorStatisticsResponse.setPreviousProfileViewCount(getProfileViewCountsByMonth(doctorId, month - 1));
			doctorStatisticsResponse.setCurrentVisitCount(getPatientVisitStatsByMonth(doctorId, locationId, month));
			doctorStatisticsResponse
					.setPreviousVisitCount(getPatientVisitStatsByMonth(doctorId, locationId, month - 1));
			doctorStatisticsResponse.setCurrentRecommendationCount(getRecommendationCountByMonth(doctorId, month));
			doctorStatisticsResponse.setPreviousRecommendationCount(getRecommendationCountByMonth(doctorId, month -1));
			break;

		case "YEAR":
			int year = calendar.get(Calendar.YEAR);
			doctorStatisticsResponse = new DoctorStatisticsResponse();
			doctorStatisticsResponse.setCurrentProfileViewCount(getProfileViewCountsByYear(doctorId, year));
			doctorStatisticsResponse.setPreviousProfileViewCount(getProfileViewCountsByYear(doctorId, year - 1));
			doctorStatisticsResponse.setCurrentVisitCount(getPatientVisitStatsByYear(doctorId, locationId, year));
			doctorStatisticsResponse.setPreviousVisitCount(getPatientVisitStatsByYear(doctorId, locationId, year - 1));
			doctorStatisticsResponse.setCurrentRecommendationCount(getRecommendationCountByYear(doctorId, year));
			doctorStatisticsResponse.setPreviousRecommendationCount(getRecommendationCountByYear(doctorId, year - 1));
			break;

		default:
			int interval = calendar.get(Calendar.WEEK_OF_YEAR);
			doctorStatisticsResponse = new DoctorStatisticsResponse();
			doctorStatisticsResponse.setCurrentProfileViewCount(getProfileViewCountsByWeek(doctorId, interval));
			doctorStatisticsResponse.setPreviousProfileViewCount(getProfileViewCountsByWeek(doctorId, interval - 1));
			doctorStatisticsResponse.setCurrentVisitCount(getPatientVisitStatsByWeek(doctorId, locationId, interval));
			doctorStatisticsResponse
					.setPreviousVisitCount(getPatientVisitStatsByWeek(doctorId, locationId, interval - 1));
			doctorStatisticsResponse.setCurrentRecommendationCount(getRecommendationCountByWeek(doctorId, interval));
			doctorStatisticsResponse.setPreviousRecommendationCount(getRecommendationCountByWeek(doctorId, interval - 1));
			break;
		}

		return doctorStatisticsResponse;
	}

	public Integer getPatientVisitStatsByWeek(String doctorId, String locationId, int week) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("locationId", "$locationId"),
							Fields.field("createdTime", "$createdTime"), Fields.field("discarded", "$discarded")))
									.andExpression("week(createdTime)").as("week");
			Criteria criteria = new Criteria("discarded").is(false);
			criteria.and("doctorId").is(new ObjectId(doctorId));
			if (locationId != null) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			criteria.and("week").is(week);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class,
					DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();

		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}

		return (doctorStats.size());
	}

	public Integer getPatientVisitStatsByMonth(String doctorId, String locationId, int month) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("locationId", "$locationId"),
							Fields.field("createdTime", "$createdTime"), Fields.field("discarded", "$discarded")))
									.andExpression("month(createdTime)").as("month");
			Criteria criteria = new Criteria("discarded").is(false);
			criteria.and("doctorId").is(new ObjectId(doctorId));
			if (locationId != null) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			criteria.and("month").is(month);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class,
					DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return (doctorStats.size());
	}

	public Integer getPatientVisitStatsByYear(String doctorId, String locationId, int year) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("locationId", "$locationId"),
							Fields.field("createdTime", "$createdTime"), Fields.field("discarded", "$discarded")))
									.andExpression("year(createdTime)").as("year");
			Criteria criteria = new Criteria("discarded").is(false);
			criteria.and("doctorId").is(new ObjectId(doctorId));
			if (locationId != null) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			criteria.and("year").is(year);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation, PatientVisitCollection.class,
					DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return (doctorStats.size());
	}

	public Integer getProfileViewCountsByMonth(String doctorId, int month) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("month(createdTime)").as("month");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
			criteria.and("month").is(month);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					DoctorProfileViewCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}

	public Integer getProfileViewCountsByWeek(String doctorId, int week) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("week(createdTime)").as("week");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
			criteria.and("week").is(week);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					DoctorProfileViewCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}

	public Integer getProfileViewCountsByYear(String doctorId, int year) {

		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("year(createdTime)").as("year");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
			criteria.and("year").is(year);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					DoctorProfileViewCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}
	
	public Integer getRecommendationCountByWeek(String doctorId , int week)
	{
		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("week(createdTime)").as("week");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
		//criteria.and("createdTime").ne(null);
			criteria.and("week").is(week);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					RecommendationsCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}

	public Integer getRecommendationCountByMonth(String doctorId , int month)
	{
		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("month(createdTime)").as("month");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
		//criteria.and("createdTime").ne(null);
			criteria.and("month").is(month);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					RecommendationsCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}

	public Integer getRecommendationCountByYear(String doctorId , int year)
	{
		List<DoctorStats> doctorStats = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("doctorId", "$doctorId"), Fields.field("createdTime", "$createdTime")))
							.andExpression("year(createdTime)").as("year");
			Criteria criteria = new Criteria("doctorId").is(new ObjectId(doctorId));
		//criteria.and("createdTime").ne(null);
			criteria.and("year").is(year);
			Aggregation aggregation = Aggregation.newAggregation(projectList, Aggregation.match(criteria),
					Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			AggregationResults<DoctorStats> results = mongoTemplate.aggregate(aggregation,
					RecommendationsCollection.class, DoctorStats.class);
			doctorStats = new ArrayList<DoctorStats>();
			doctorStats = results.getMappedResults();
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return doctorStats.size();
	}

}
