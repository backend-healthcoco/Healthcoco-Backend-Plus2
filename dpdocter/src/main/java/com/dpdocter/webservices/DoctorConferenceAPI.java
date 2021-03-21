package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.SessionQuestion;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.SessionDateResponse;
import com.dpdocter.services.ConferenceService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.CONFERENCE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.CONFERENCE_URL, description = "Endpoint for Conference")
public class DoctorConferenceAPI {

	private static Logger logger = LogManager.getLogger(DoctorConferenceAPI.class.getName());

	@Autowired
	private ConferenceService conferenceService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.ConferenceUrls.GET_SESSION_TOPICS)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_TOPICS, notes = PathProxy.ConferenceUrls.GET_SESSION_TOPICS)
	public Response<SessionTopic> getTopics(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") boolean discarded, @QueryParam("searchTerm") String searchTerm) {

		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setDataList(conferenceService.getTopics(size, page, discarded, searchTerm));
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_SESSION_TOPIC)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_TOPIC, notes = PathProxy.ConferenceUrls.GET_SESSION_TOPIC)
	public Response<SessionTopic> getTopic(@PathParam("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SessionTopic topic = conferenceService.getTopic(id);
		Response<SessionTopic> response = new Response<SessionTopic>();
		response.setData(topic);
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES, notes = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILES)
	public Response<SpeakerProfile> getSpeakerProfiles(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") boolean discarded, @QueryParam("searchTerm") String searchTerm) {

		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setDataList(conferenceService.getSpeakerProfiles(size, page, discarded, searchTerm));
		return response;

	}

	@Path(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE, notes = PathProxy.ConferenceUrls.GET_SPEAKER_PROFILE)
	public Response<SpeakerProfile> getSpeakerProfile(@PathParam("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		SpeakerProfile profile = conferenceService.getSpeakerProfile(id);
		Response<SpeakerProfile> response = new Response<SpeakerProfile>();
		response.setData(profile);
		return response;

	}

	@Path(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSIONS)
	public Response<DoctorConferenceSession> getConferenceSessions(@QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("discarded") boolean discarded, @QueryParam("city") String city,
			@QueryParam("fromtime") @DefaultValue("0") Integer fromtime,
			@QueryParam("toTime") @DefaultValue("0") Integer toTime, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm,
			@PathParam("conferenceId") String conferenceId, @MatrixParam("topics") List<String> topics) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setDataList(conferenceService.getConferenceSessions(size, page, discarded, city, fromtime, toTime,
				fromDate, toDate, searchTerm, conferenceId, topics));
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION)
	public Response<DoctorConferenceSession> getConferenceSession(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceSession session = conferenceService.getConferenceSession(id);
		Response<DoctorConferenceSession> response = new Response<DoctorConferenceSession>();
		response.setData(session);
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES, notes = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCES)
	public Response<DoctorConference> getDoctorConference(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") boolean discarded, @QueryParam("city") String city,
			@QueryParam("speciality") String speciality, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm) {
		List<DoctorConference> coference = conferenceService.getDoctorConference(size, page, discarded, city,
				speciality, fromDate, toDate, searchTerm);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setDataList(coference);
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE, notes = PathProxy.ConferenceUrls.GET_DOCTOR_CONFERENCE)
	public Response<DoctorConference> getDoctorConference(@PathParam("id") String id) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConference coference = conferenceService.getDoctorConference(id);
		Response<DoctorConference> response = new Response<DoctorConference>();
		response.setData(coference);
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDAS)
	public Response<DoctorConferenceAgenda> getConferenceAgenda(@QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("discarded") boolean discarded, @QueryParam("city") String city,
			@QueryParam("fromtime") @DefaultValue("0") Integer fromtime,
			@QueryParam("toTime") @DefaultValue("0") Integer toTime, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm,
			@PathParam("conferenceId") String conferenceId) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		List<DoctorConferenceAgenda> agenda = conferenceService.getConferenceAgenda(size, page, discarded, fromDate,
				toDate, searchTerm, conferenceId);
		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setDataList(agenda);
		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_AGENDA)
	public Response<DoctorConferenceAgenda> getConferenceAgenda(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		DoctorConferenceAgenda agenda = conferenceService.getConferenceAgenda(id);

		Response<DoctorConferenceAgenda> response = new Response<DoctorConferenceAgenda>();
		response.setData(agenda);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES, notes = PathProxy.ConferenceUrls.GET_CONFERENCE_SESSION_DATES)
	public Response<SessionDateResponse> getConferenceSessionDate(@PathParam("conferenceId") String conferenceId) {
		if (DPDoctorUtils.anyStringEmpty(conferenceId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		List<SessionDateResponse> dates = conferenceService.getConferenceSessionDate(conferenceId);

		Response<SessionDateResponse> response = new Response<SessionDateResponse>();
		response.setDataList(dates);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_QUESTION)
	@POST
	@ApiOperation(value = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_QUESTION, notes = PathProxy.ConferenceUrls.ADD_EDIT_SESSION_QUESTION)
	public Response<SessionQuestion> addEditQuestion(SessionQuestion request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		if (DPDoctorUtils.anyStringEmpty(request.getQuestionerId(), request.getSessionId())) {
			logger.warn("questionerId and sessionId should not null or empty");
			throw new BusinessException(ServiceError.InvalidInput,
					"questionerId and sessionId should not null or empty");

		}
		SessionQuestion question = conferenceService.addeditQuestion(request);

		Response<SessionQuestion> response = new Response<SessionQuestion>();
		response.setData(question);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_SESSION_QUESTIONS)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_QUESTIONS, notes = PathProxy.ConferenceUrls.GET_SESSION_QUESTIONS)
	public Response<SessionQuestion> getConferenceSessionQuestion(@QueryParam("size") int size,
			@QueryParam("page") int page, @QueryParam("discarded") boolean discarded,
			@PathParam("sessionId") String sessionId, @QueryParam("userId") String userId,
			@QueryParam("topLiked") boolean topLiked) {

		if (DPDoctorUtils.anyStringEmpty(sessionId)) {
			logger.warn("sessionId should not null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "sessionId should not null or empty");

		}
		List<SessionQuestion> questions = conferenceService.getQuestion(page, size, sessionId, discarded, userId,
				topLiked);

		Response<SessionQuestion> response = new Response<SessionQuestion>();
		response.setDataList(questions);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.GET_SESSION_QUESTION)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.GET_SESSION_QUESTION, notes = PathProxy.ConferenceUrls.GET_SESSION_QUESTION)
	public Response<SessionQuestion> getConferenceSessionQuestion(@PathParam("id") String id,
			@QueryParam("userId") String userId) {

		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("id should not null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "id should not null or empty");

		}
		SessionQuestion question = conferenceService.getQuestion(id, userId);

		Response<SessionQuestion> response = new Response<SessionQuestion>();
		response.setData(question);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.DELETE_SESSION_QUESTION)
	@DELETE
	@ApiOperation(value = PathProxy.ConferenceUrls.DELETE_SESSION_QUESTION, notes = PathProxy.ConferenceUrls.DELETE_SESSION_QUESTION)
	public Response<SessionQuestion> deleteConferenceSessionQuestion(@PathParam("id") String id,
			@QueryParam("userId") String userId, @QueryParam("discarded") boolean discarded) {

		if (DPDoctorUtils.anyStringEmpty(id, userId)) {
			logger.warn("id should not null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "id and userId should not null or empty");

		}
		SessionQuestion question = conferenceService.deleteQuestion(id, userId, discarded);

		Response<SessionQuestion> response = new Response<SessionQuestion>();
		response.setData(question);

		return response;
	}

	@Path(value = PathProxy.ConferenceUrls.LIKE_SESSION_QUESTION)
	@GET
	@ApiOperation(value = PathProxy.ConferenceUrls.LIKE_SESSION_QUESTION, notes = PathProxy.ConferenceUrls.LIKE_SESSION_QUESTION)
	public Response<Boolean> likeQuestion(@PathParam("questionId") String questionId,
			@QueryParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId, questionId)) {
			logger.warn("userId,questionId should not null or empty");
			throw new BusinessException(ServiceError.InvalidInput, "userId,questionId should not null or empty");

		}
		Response<Boolean> response = new Response<Boolean>();

		response.setData(conferenceService.likeQuestion(questionId, userId));
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

}
