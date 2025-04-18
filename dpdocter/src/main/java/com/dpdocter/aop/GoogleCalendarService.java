package com.dpdocter.aop;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Appointment;
import com.dpdocter.collections.GoogleTokenIdCollections;
import com.dpdocter.repository.GoogleTokenIdRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Reminders;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GoogleCalendarService {

	private static final String APPLICATION_NAME = "HealthCoco Plus App";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Autowired
	private GoogleTokenIdRepository googleTokenIdRepository;

	@Value("${healthcoco.plus.google.web.client.id}")
	private String GOOGLE_WEB_CLIENT_ID;

	@Value("${healthcoco.plus.google.web.client.secret}")
	private String GOOGLE_WEB_CLIENT_SECRET;

	public void addEventToGoogleCalendar(Appointment appointment, String emailDoctor) {
		try {
			if (appointment != null && appointment.getDoctorId() != null && appointment.getLocationId() != null) {
				// Get refresh token from DB
				GoogleTokenIdCollections tokenIdCollections = googleTokenIdRepository.findByDoctorIdAndLocationId(
						new ObjectId(appointment.getDoctorId()), new ObjectId(appointment.getLocationId()));
				if (tokenIdCollections != null) {
					GoogleTokenResponse refreshedToken = new GoogleRefreshTokenRequest(new NetHttpTransport(),
							JacksonFactory.getDefaultInstance(), tokenIdCollections.getRefreshToken(),
							GOOGLE_WEB_CLIENT_ID, GOOGLE_WEB_CLIENT_SECRET).execute();

					String email = tokenIdCollections.getEmail();

					AccessToken token = new AccessToken(refreshedToken.getAccessToken(), null);
					GoogleCredentials credentials = GoogleCredentials.create(token);
					// Build calendar service with new access token
					com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
							GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
							new HttpCredentialsAdapter(credentials)).setApplicationName(APPLICATION_NAME).build();

					// Create the event
					String mobile = appointment.getPatient().getMobileNumber() != null
							? appointment.getPatient().getMobileNumber()
							: "--";
					String patientEmail = appointment.getPatient().getEmailAddress() != null
							? appointment.getPatient().getEmailAddress()
							: "--";

					Event event = new Event()
							.setSummary("Appointment with " + appointment.getPatient().getLocalPatientName())
							.setDescription("Appointment at " + appointment.getLocationName() + "\nMobile Number: "
									+ mobile + "\nEmail: " + patientEmail);
					// Time handling
					Date appointmentDate = appointment.getFromDate();
					Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("IST"));
					calStart.setTime(appointmentDate);

					int fromMinutes = appointment.getTime() != null && appointment.getTime().getFromTime() != null
							? appointment.getTime().getFromTime()
							: 600;
					int toMinutes = appointment.getTime() != null && appointment.getTime().getToTime() != null
							? appointment.getTime().getToTime()
							: 615;

					calStart.set(Calendar.HOUR_OF_DAY, fromMinutes / 60);
					calStart.set(Calendar.MINUTE, fromMinutes % 60);

					Calendar calEnd = (Calendar) calStart.clone();
					calEnd.set(Calendar.HOUR_OF_DAY, toMinutes / 60);
					calEnd.set(Calendar.MINUTE, toMinutes % 60);

					EventDateTime start = new EventDateTime()
							.setDateTime(new com.google.api.client.util.DateTime(calStart.getTime()))
							.setTimeZone("Asia/Kolkata");
					EventDateTime end = new EventDateTime()
							.setDateTime(new com.google.api.client.util.DateTime(calEnd.getTime()))
							.setTimeZone("Asia/Kolkata");
					event.setStart(start).setEnd(end);

					// Set reminders
					EventReminder[] reminderOverrides = { new EventReminder().setMethod("email").setMinutes(30),
							new EventReminder().setMethod("popup").setMinutes(10) };
					event.setReminders(
							new Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides)));

					// Insert the event
					event = service.events().insert(email, event).execute();
					Colors colors = service.colors().get().execute();

					System.out.println("Event Colors:");
					for (Map.Entry<String, ColorDefinition> entry : colors.getEvent().entrySet()) {
						System.out.println(
								"Color ID: " + entry.getKey() + ", Background: " + entry.getValue().getBackground()
										+ ", Foreground: " + entry.getValue().getForeground());
					}

					System.out.println("\nCalendar Colors:");
					for (Map.Entry<String, ColorDefinition> entry : colors.getCalendar().entrySet()) {
						System.out.println(
								"Color ID: " + entry.getKey() + ", Background: " + entry.getValue().getBackground()
										+ ", Foreground: " + entry.getValue().getForeground());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to add event to calendar: " + e.getMessage());
		}
	}
}