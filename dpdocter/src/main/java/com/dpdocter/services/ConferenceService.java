package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorConference;
import com.dpdocter.beans.DoctorConferenceAgenda;
import com.dpdocter.beans.DoctorConferenceSession;
import com.dpdocter.beans.SessionQuestion;
import com.dpdocter.beans.SessionTopic;
import com.dpdocter.beans.SpeakerProfile;
import com.dpdocter.response.SessionDateResponse;

public interface ConferenceService {

	public List<SessionTopic> getTopics(int size, int page, boolean discarded, String searchTerm);

	public SessionTopic getTopic(String id);

	public List<SpeakerProfile> getSpeakerProfiles(int size, int page, boolean discarded, String searchTerm);

	public SpeakerProfile getSpeakerProfile(String id);

	public List<DoctorConferenceSession> getConferenceSessions(int size, int page, boolean discarded, String city,
			Integer fromtime, Integer toTime, String fromDate, String toDate, String searchTerm, String conferenceId,
			List<String> topics);

	public DoctorConferenceSession getConferenceSession(String id);

	public List<DoctorConference> getDoctorConference(int size, int page, boolean discarded, String city,
			String speciality, String fromDate, String toDate, String searchTerm);

	public DoctorConference getDoctorConference(String id);

	public List<DoctorConferenceAgenda> getConferenceAgenda(int size, int page, boolean discarded, String fromDate,
			String toDate, String searchTerm, String conferenceId);

	public DoctorConferenceAgenda getConferenceAgenda(String id);

	public List<SessionDateResponse> getConferenceSessionDate(String conferenceId);

	public Boolean likeQuestion(String questionId, String userId);

	public SessionQuestion addeditQuestion(SessionQuestion request);

	public SessionQuestion deleteQuestion(String id, String userId, boolean discarded);

	public List<SessionQuestion> getQuestion(int page, int size, String sessionId, boolean discarded, String userId);

	public SessionQuestion getQuestion(String id, String userId);

}
