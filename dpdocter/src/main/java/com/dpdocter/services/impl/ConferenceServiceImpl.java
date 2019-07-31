package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.SessionQuestion;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.collections.DoctorConferenceAgendaCollection;
import com.dpdocter.collections.DoctorConferenceCollection;
import com.dpdocter.collections.DoctorConferenceSessionCollection;
import com.dpdocter.collections.QuestionCollection;
import com.dpdocter.collections.QuestionLikeCollection;
import com.dpdocter.collections.SessionTopicCollection;
import com.dpdocter.collections.SpeakerProfileCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorConferenceSessionRepository;
import com.dpdocter.repository.DoctorconferenceAgendaRepository;
import com.dpdocter.repository.QuestionLikeRepository;
import com.dpdocter.repository.QuestionRepository;
import com.dpdocter.repository.SessionTopicRepository;
import com.dpdocter.repository.SpeakerProfileRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.OrganizingCommitteeResponse;
import com.dpdocter.response.SessionDateResponse;
import com.dpdocter.services.ConferenceService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class ConferenceServiceImpl implements ConferenceService {

	private static Logger logger = Logger.getLogger(ConferenceServiceImpl.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SpeakerProfileRepository speakerProfileRepository;

	@Autowired
	private SessionTopicRepository sessionTopicRepository;

	@Autowired
	private DoctorconferenceAgendaRepository doctorConferenceAgendaRepository;

	@Autowired
	private QuestionLikeRepository questionLikeRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private UserRepository userRepsitory;

	@Autowired
	private DoctorConferenceSessionRepository doctorConferenceSessionRepository;

	@Override
	public List<SessionTopic> getTopics(int size, int page, boolean discarded, String searchTerm) {
		List<SessionTopic> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("topic").regex("^" + searchTerm, "i"),
						new Criteria("topic").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "topic")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "topic")));
			}
			response = mongoTemplate.aggregate(aggregation, SessionTopicCollection.class, SessionTopic.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Session Topic " + e.getMessage());

		}
		return response;
	}

	@Override
	public SessionTopic getTopic(String id) {
		SessionTopic response = null;
		try {
			SessionTopicCollection sessionTopicCollection = sessionTopicRepository.findById(new ObjectId(id)).orElse(null);
			response = new SessionTopic();
			if (sessionTopicCollection != null) {
				BeanUtil.map(sessionTopicCollection, response);
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Session Topic " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Session Topic " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<SpeakerProfile> getSpeakerProfiles(int size, int page, boolean discarded, String searchTerm) {
		List<SpeakerProfile> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "firstName")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, SpeakerProfileCollection.class, SpeakerProfile.class)
					.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (SpeakerProfile speaker : response) {
					speaker.setProfileImage(getFinalImageURL(speaker.getProfileImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Speaker Profile " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Speaker Profile " + e.getMessage());

		}
		return response;
	}

	@Override
	public SpeakerProfile getSpeakerProfile(String id) {
		SpeakerProfile response = null;
		try {
			SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository.findById(new ObjectId(id)).orElse(null);
			response = new SpeakerProfile();

			BeanUtil.map(speakerProfileCollection, response);

			if (!DPDoctorUtils.anyStringEmpty(response.getProfileImage())) {
				response.setProfileImage(getFinalImageURL(response.getProfileImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Speaker Profile" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Speaker Profile " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<DoctorConferenceSession> getConferenceSessions(int size, int page, boolean discarded, String city,
			Integer fromtime, Integer toTime, String fromDate, String toDate, String searchTerm, String conferenceId,
			List<String> topics) {
		List<DoctorConferenceSession> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria = criteria.orOperator(new Criteria("address.city").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm));

			if (topics != null && !topics.isEmpty())
				criteria = criteria.and("topics").in(topics);

			if (!DPDoctorUtils.anyStringEmpty(conferenceId))
				criteria.and("conferenceId").is(new ObjectId(conferenceId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate))).lt(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("onDate").lt(new Date(Long.parseLong(toDate)));

			}

			if (fromtime > 0 && toTime > 0) {
				criteria.and("schedule.fromTime").gte(fromtime);
				criteria.and("schedule.toTime").lte(toTime);
			} else if (fromtime > 0) {
				criteria.and("schedule.fromTime").gte(fromtime);

			} else if (toTime > 0) {
				criteria.and("schedule.toTime").lte(toTime);

			}
			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("topics", new BasicDBObject("$push", "$topics.topic"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays",
												true))),
								groupFirst, Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")),
								Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays",
												true))),
								groupFirst, Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, DoctorConferenceSessionCollection.class, DoctorConferenceSession.class)
					.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (DoctorConferenceSession session : response) {
					session.setTitleImage(getFinalImageURL(session.getTitleImage()));
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConferenceSession getConferenceSession(String id) {
		DoctorConferenceSession response = null;
		try {

			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("topics", new BasicDBObject("$push", "$topics.topic"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListThird = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("title", "$title"), Fields.field("titleImage", "$titleImage"),
					Fields.field("description", "$description"), Fields.field("address", "$address"),
					Fields.field("noOfQuestion", "$noOfQuestion"), Fields.field("onDate", "$onDate"),
					Fields.field("schedule", "$schedule"), Fields.field("topics", "$topics"),
					Fields.field("discarded", "$discarded"), Fields.field("conferenceId", "$conferenceId"),
					Fields.field("speakers.speakerId", "$speakers.speakerId"),
					Fields.field("speakers.firstName", "$speaker.firstName"),
					Fields.field("speakers.profileImage", "$speaker.profileImage"),
					Fields.field("speakers.role", "$speakers.role"), Fields.field("createdTime", "$createdTime"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy")));
			CustomAggregationOperation groupThird = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("schedule", new BasicDBObject("$first", "$schedule"))
							.append("onDate", new BasicDBObject("$first", "$onDate"))
							.append("noOfQuestion", new BasicDBObject("$first", "$noOfQuestion"))
							.append("topics", new BasicDBObject("$first", "$topics"))
							.append("speakers", new BasicDBObject("$push", "$speakers"))
							.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			response = mongoTemplate.aggregate(Aggregation.newAggregation(

					Aggregation.match(new Criteria("_id").is(new ObjectId(id)).and("discarded").is(false)),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$topicIds").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("session_topic_cl", "topicIds", "_id", "topics"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$topics").append("preserveNullAndEmptyArrays", true))),
					groupFirst,
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speakers").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("speaker_profile_cl", "$speakers.speakerId", "_id", "speaker"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speaker").append("preserveNullAndEmptyArrays", true))),
					projectListThird, groupThird),

					"doctor_conference_session_cl", DoctorConferenceSession.class).getUniqueMappedResult();

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session" + e.getMessage());

		}
		return response;

	}

	@Override
	public List<SessionDateResponse> getConferenceSessionDate(String conferenceId) {
		List<SessionDateResponse> response = null;
		try {
			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
									.append("onDate", new BasicDBObject("$first", "$onDate"))
									.append("conferenceId", new BasicDBObject("$first", "$conferenceId"))));

			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("onDate", "$onDate"), Fields.field("conferenceId", "$conferenceId")));

			response = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("conferenceId").is(new ObjectId(conferenceId))
									.and("discarded").is(false)),
							projectList.and("onDate").extractDayOfMonth().as("day").and("onDate").extractMonth()
									.as("month").and("onDate").extractYear().as("year").and("onDate").extractWeek()
									.as("week"),
							group, Aggregation.sort(new Sort(Direction.ASC, "onDate"))),
					DoctorConferenceSessionCollection.class, SessionDateResponse.class).getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting Conference Session " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Session" + e.getMessage());

		}
		return response;

	}

	@Override
	public List<DoctorConference> getDoctorConference(int size, int page, boolean discarded, String city,
			String speciality, String fromDate, String toDate, String searchTerm) {
		List<DoctorConference> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(speciality))
				criteria = criteria.orOperator(new Criteria("specialities.speciality").regex("^" + searchTerm, "i"),
						new Criteria("specialities.superSpeciality").regex("^" + searchTerm),
						new Criteria("specialities.superSpeciality").regex("^" + searchTerm, "i"),
						new Criteria("specialities.speciality").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("fromDate").gte(new Date(Long.parseLong(fromDate))).and("toDate")
						.lt(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("fromDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("toDate").lt(new Date(Long.parseLong(toDate)));

			}
			if (!DPDoctorUtils.anyStringEmpty(city))
				criteria = criteria.orOperator(new Criteria("address.city").regex("^" + searchTerm, "i"),
						new Criteria("address.city").regex("^" + searchTerm));

			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
								new CustomAggregationOperation(new Document("$unwind",
										new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria), groupOperation,
								Aggregation.sort(new Sort(Direction.ASC, "fromDate")), Aggregation.skip(page * size),
								Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), groupOperation,
						Aggregation.sort(new Sort(Direction.ASC, "fromDate")));
			}
			response = mongoTemplate.aggregate(aggregation, DoctorConferenceCollection.class, DoctorConference.class)
					.getMappedResults();

			if (response != null && !response.isEmpty()) {
				for (DoctorConference conference : response) {
					conference.setTitleImage(getFinalImageURL(conference.getTitleImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Conference  " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConference getDoctorConference(String id) {
		DoctorConference response = null;
		try {

			CustomAggregationOperation groupFirst = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("specialities", new BasicDBObject("$push", "$specialities.superSpeciality"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListsecond = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("title", "$title"), Fields.field("titleImage", "$titleImage"),
					Fields.field("description", "$description"), Fields.field("fromDate", "$fromDate"),
					Fields.field("toDate", "$toDate"), Fields.field("address", "$address"),
					Fields.field("discarded", "$discarded"), Fields.field("specialities", "$specialities"),
					Fields.field("speakers.speakerId", "$speakers.speakerId"),
					Fields.field("speakers.firstName", "$speaker.firstName"),
					Fields.field("speakers.role", "$speakers.role"),
					Fields.field("speakers.profileImage", "$speaker.profileImage"),
					Fields.field("commiteeMember", "$commiteeMember"), Fields.field("createdTime", "$createdTime"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation groupsecond = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("commiteeMember", new BasicDBObject("$first", "$commiteeMember"))
							.append("speakers", new BasicDBObject("$push", "$speakers"))
							.append("specialities", new BasicDBObject("$first", "$specialities"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			ProjectionOperation projectListThird = new ProjectionOperation(
					Fields.from(Fields.field("id", "$id"), Fields.field("title", "$title"),
							Fields.field("titleImage", "$titleImage"), Fields.field("description", "$description"),
							Fields.field("fromDate", "$fromDate"), Fields.field("toDate", "$toDate"),
							Fields.field("address", "$address"), Fields.field("discarded", "$discarded"),
							Fields.field("specialities", "$specialities"), Fields.field("speakers", "$speakers"),
							Fields.field("member.speakerId", "$commiteeMember.speakerId"),
							Fields.field("commiteeMember.firstName", "$member.firstName"),
							Fields.field("commiteeMember.profileImage", "$member.profileImage"),
							Fields.field("commiteeMember.role", "$commiteeMember.role"),
							Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("createdBy", "$createdBy")));

			CustomAggregationOperation groupthird = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("id", "$_id").append("title", new BasicDBObject("$first", "$title"))
							.append("titleImage", new BasicDBObject("$first", "$titleImage"))
							.append("description", new BasicDBObject("$first", "$description"))
							.append("fromDate", new BasicDBObject("$first", "$fromDate"))
							.append("toDate", new BasicDBObject("$first", "$toDate"))
							.append("commiteeMember", new BasicDBObject("$push", "$commiteeMember"))
							.append("speakers", new BasicDBObject("$first", "$speakers"))
							.append("specialities", new BasicDBObject("$first", "$specialities"))
							.append("address", new BasicDBObject("$first", "$address"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),

					Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$specialities").append("preserveNullAndEmptyArrays", true))),
					groupFirst,
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speakers").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("speaker_profile_cl", "$speakers.speakerId", "_id", "speaker"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$speaker").append("preserveNullAndEmptyArrays", true))),
					projectListsecond, groupsecond);
			response = mongoTemplate.aggregate(aggregation, "doctor_conference_cl", DoctorConference.class)
					.getUniqueMappedResult();

			if (response.getSpeakers() != null && !response.getSpeakers().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getSpeakers()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getProfileImage())) {

						committeeResponse.setProfileImage(getFinalImageURL(committeeResponse.getProfileImage()));
					}
				}
			}
			if (response.getCommiteeMember() != null && !response.getCommiteeMember().isEmpty()) {
				for (OrganizingCommitteeResponse committeeResponse : response.getCommiteeMember()) {
					if (!DPDoctorUtils.anyStringEmpty(committeeResponse.getSpeakerId())) {
						SpeakerProfileCollection speakerProfileCollection = speakerProfileRepository
								.findById(new ObjectId(committeeResponse.getSpeakerId())).orElse(null);
						committeeResponse.setFirstName(speakerProfileCollection.getFirstName());
						if (!DPDoctorUtils.anyStringEmpty(speakerProfileCollection.getProfileImage())) {

							committeeResponse
									.setProfileImage(getFinalImageURL(speakerProfileCollection.getProfileImage()));
						}
					}
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference  " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Conference   " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<DoctorConferenceAgenda> getConferenceAgenda(int size, int page, boolean discarded, String fromDate,
			String toDate, String searchTerm, String conferenceId) {
		List<DoctorConferenceAgenda> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + searchTerm, "i"),
						new Criteria("title").regex("^" + searchTerm));

			if (!DPDoctorUtils.anyStringEmpty(conferenceId))
				criteria.and("conferenceId").is(new ObjectId(conferenceId));

			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				criteria.and("onDate").gt(new Date(Long.parseLong(fromDate))).lte(new Date(Long.parseLong(toDate)));
			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				criteria.and("onDate").gte(new Date(Long.parseLong(fromDate)));

			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				criteria.and("onDate").lt(new Date(Long.parseLong(toDate)));

			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime", "onDate")),
						Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "schedule.fromTime")),
						Aggregation.sort(new Sort(Direction.ASC, "onDate")));
			}
			response = mongoTemplate
					.aggregate(aggregation, DoctorConferenceAgendaCollection.class, DoctorConferenceAgenda.class)
					.getMappedResults();
			if (response != null && !response.isEmpty()) {
				for (DoctorConferenceAgenda agenda : response) {
					agenda.setTitleImage(getFinalImageURL(agenda.getTitleImage()));
				}
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Agenda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Agenda " + e.getMessage());

		}
		return response;
	}

	@Override
	public DoctorConferenceAgenda getConferenceAgenda(String id) {
		DoctorConferenceAgenda response = null;
		try {
			DoctorConferenceAgendaCollection conferenceAgendaCollection = doctorConferenceAgendaRepository
					.findById(new ObjectId(id)).orElse(null);
			response = new DoctorConferenceAgenda();
			BeanUtil.map(conferenceAgendaCollection, response);
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage())) {
				response.setTitleImage(getFinalImageURL(response.getTitleImage()));
			}
		} catch (BusinessException e) {
			logger.error("Error while getting Conference Agenda " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting Conference Agenda  " + e.getMessage());

		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Override
	public SessionQuestion addeditQuestion(SessionQuestion request) {
		SessionQuestion response = null;
		try {
			QuestionCollection questionCollection = null;
			UserCollection user = userRepsitory.findById(new ObjectId(request.getQuestionerId())).orElse(null);
			if (user == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
			}
			DoctorConferenceSessionCollection conferenceSessionCollection = doctorConferenceSessionRepository
					.findById(new ObjectId(request.getSessionId())).orElse(null);

			if (conferenceSessionCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor Conference Session not found");
			}
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				questionCollection = new QuestionCollection();
				request.setCreatedTime(new Date());
				request.setCreatedBy((user.getTitle() != null ? user.getTitle() + " " : "") + user.getFirstName());
				BeanUtil.map(request, questionCollection);
				conferenceSessionCollection.setNoOfQuestion(conferenceSessionCollection.getNoOfQuestion() + 1);
			} else {
				questionCollection = questionRepository.findById(new ObjectId(request.getId())).orElse(null);
				request.setCreatedTime(questionCollection.getCreatedTime());
				request.setCreatedBy((user.getTitle() != null ? user.getTitle() + " " : "") + user.getFirstName());
				BeanUtil.map(request, questionCollection);
			}
			questionCollection = questionRepository.save(questionCollection);
			conferenceSessionCollection = doctorConferenceSessionRepository.save(conferenceSessionCollection);
			response = new SessionQuestion();
			BeanUtil.map(questionCollection, response);
			QuestionLikeCollection likeCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(response.getQuestionerId())) {
				likeCollection = questionLikeRepository.findByQuestionIdAndUserId(new ObjectId(response.getId()),
						new ObjectId(response.getQuestionerId()));
				if (likeCollection != null) {
					response.setIsLiked(!likeCollection.getDiscarded());
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while add edit Question " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add edit Question " + e.getMessage());

		}
		return response;

	}

	@Override
	public SessionQuestion deleteQuestion(String id, String userId, boolean discarded) {
		SessionQuestion response = null;
		try {
			QuestionCollection questionCollection = null;
			UserCollection user = userRepsitory.findById(new ObjectId(userId)).orElse(null);
			if (user == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
			}

			questionCollection = questionRepository.findById(new ObjectId(id)).orElse(null);

			if (questionCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor Question not found");
			}

			if (questionCollection.getQuestionerId().toString().equalsIgnoreCase(userId)) {
				questionCollection.setDiscarded(discarded);
			}

			questionCollection = questionRepository.save(questionCollection);
			DoctorConferenceSessionCollection conferenceSessionCollection = doctorConferenceSessionRepository
					.findById(questionCollection.getSessionId()).orElse(null);
			conferenceSessionCollection.setNoOfQuestion(conferenceSessionCollection.getNoOfQuestion() - 1);
			conferenceSessionCollection = doctorConferenceSessionRepository.save(conferenceSessionCollection);
			response = new SessionQuestion();
			BeanUtil.map(questionCollection, response);
			QuestionLikeCollection likeCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				likeCollection = questionLikeRepository.findByQuestionIdAndUserId(questionCollection.getId(),
						new ObjectId(userId));
				if (likeCollection != null) {
					response.setIsLiked(!likeCollection.getDiscarded());
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while add edit Question " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add edit Question " + e.getMessage());

		}
		return response;

	}

	@Override
	public List<SessionQuestion> getQuestion(int page, int size, String sessionId, boolean discarded, String userId,
			Boolean topLiked) {
		List<SessionQuestion> response = null;
		try {

			Criteria criteria = new Criteria("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(sessionId))
				criteria = criteria.and("sessionId").is(new ObjectId(sessionId));
			Sort sort = null;
			if (topLiked) {
				sort = new Sort(Direction.DESC, "noOfLikes");

			} else {
				sort = new Sort(Direction.DESC, "updatedTime");
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.sort(sort), Aggregation.skip((long)page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(sort));
			}
			response = mongoTemplate.aggregate(aggregation, QuestionCollection.class, SessionQuestion.class)
					.getMappedResults();
			QuestionLikeCollection likeCollection = null;
			if (response != null && !response.isEmpty()) {
				if (!DPDoctorUtils.anyStringEmpty(userId)) {
					for (SessionQuestion sessionQuestion : response) {
						likeCollection = questionLikeRepository
								.findByQuestionIdAndUserId(new ObjectId(sessionQuestion.getId()), new ObjectId(userId));
						if (likeCollection != null) {
							sessionQuestion.setIsLiked(!likeCollection.getDiscarded());
						}
					}
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while getting Question " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Question " + e.getMessage());

		}
		return response;

	}

	@Override
	public SessionQuestion getQuestion(String id, String userId) {
		SessionQuestion response = null;
		try {
			QuestionLikeCollection likeCollection = null;
			QuestionCollection questionCollection = questionRepository.findById(new ObjectId(id)).orElse(null);
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				likeCollection = questionLikeRepository.findByQuestionIdAndUserId(new ObjectId(id), new ObjectId(userId));
			}
			response = new SessionQuestion();
			if (questionCollection != null) {
				BeanUtil.map(questionCollection, response);
				if (likeCollection != null) {
					response.setIsLiked(!likeCollection.getDiscarded());
				}
			}

		} catch (BusinessException e) {
			logger.error("Error while getting Question " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting Question " + e.getMessage());

		}
		return response;

	}

	@Override
	public Boolean likeQuestion(String questionId, String userId) {
		Boolean response = false;
		try {
			QuestionLikeCollection likeCollection = null;
			QuestionCollection questionCollection = null;
			UserCollection user = userRepsitory.findById(new ObjectId(userId)).orElse(null);
			if (user == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found");
			}
			questionCollection = questionRepository.findById(new ObjectId(questionId)).orElse(null);
			if (questionCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Session Question not found");
			}
			likeCollection = questionLikeRepository.findByQuestionIdAndUserId(new ObjectId(questionId),
					new ObjectId(userId));
			if (likeCollection == null) {
				likeCollection = new QuestionLikeCollection();
				likeCollection.setQuestionId(new ObjectId(questionId));
				likeCollection.setUserId(new ObjectId(userId));
				likeCollection.setCreatedTime(new Date());
				likeCollection
						.setCreatedBy((user.getTitle() != null ? user.getTitle() + " " : "") + user.getFirstName());
				questionCollection.setNoOfLikes(questionCollection.getNoOfLikes() + 1);
			} else {
				if (likeCollection.getDiscarded()) {
					questionCollection.setNoOfLikes(questionCollection.getNoOfLikes() + 1);
				} else {
					questionCollection.setNoOfLikes(questionCollection.getNoOfLikes() - 1);
				}
				likeCollection.setDiscarded(!likeCollection.getDiscarded());
			}
			likeCollection = questionLikeRepository.save(likeCollection);
			questionCollection = questionRepository.save(questionCollection);
			response = !likeCollection.getDiscarded();

		} catch (BusinessException e) {
			logger.error("Error while getting Question " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting loke on Question " + e.getMessage());

		}
		return response;
	}

}
