package com.dpdocter.aop;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Appointment;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.GoogleTokenIdCollections;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.GoogleTokenIdRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
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

import io.jsonwebtoken.io.IOException;

@Service
public class GoogleCalendarService {

	private static final String APPLICATION_NAME = "HealthCoco Plus App";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Autowired
	private GoogleTokenIdRepository googleTokenIdRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Value("${healthcoco.plus.google.web.client.id}")
	private String GOOGLE_WEB_CLIENT_ID;

	@Value("${healthcoco.plus.google.web.client.secret}")
	private String GOOGLE_WEB_CLIENT_SECRET;

	public void addEventToGoogleCalendar(Appointment appointment, String emailDoctor, String state) {
		AppointmentCollection appointmentCollection;
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

					// Retrieve existing appointment collection
					appointmentCollection = appointmentRepository.findById(new ObjectId(appointment.getId()))
							.orElse(null);

					if (state.equals(AppointmentState.NEW.getState())) {
						// Create the event
						Event event = createEvent(appointment);

						// Insert the event
						event = service.events().insert(email, event).execute();
						// Save event ID
						if (appointmentCollection != null) {
							appointmentCollection.setEventId(event.getId());
							appointmentCollection.setGoogleEventEmail(email);
							appointmentRepository.save(appointmentCollection);
						}
					} else if (state.equals(AppointmentState.CANCEL.getState())) {
						if (appointmentCollection != null && appointmentCollection.getEventId() != null) {
							service.events().delete(email, appointmentCollection.getEventId()).execute();
							appointmentCollection.setEventId(null);
							appointmentCollection.setGoogleEventEmail(null);
							appointmentRepository.save(appointmentCollection);
						}
					} else if (state.equals(AppointmentState.RESCHEDULE.getState())) {
						if (appointmentCollection != null) {
							Event updatedEvent = createEvent(appointment);

							// Try to fetch the existing event
							Event existingEvent = service.events().get(email, appointmentCollection.getEventId())
									.execute();

							if (tokenIdCollections.getEmail()
									.equalsIgnoreCase(existingEvent.getOrganizer().getEmail())) {
								// User is organizer, can update
								Event result = service.events()
										.update(email, appointmentCollection.getEventId(), updatedEvent).execute();
								appointmentCollection.setEventId(result.getId());
								appointmentCollection.setGoogleEventEmail(email);
							} else {
								// Not the organizer - delete old event and create new
								service.events().delete(appointmentCollection.getGoogleEventEmail(),
										appointmentCollection.getEventId()).execute();

								Event newEvent = service.events().insert(email, updatedEvent).execute();
								appointmentCollection.setEventId(newEvent.getId());
								appointmentCollection.setGoogleEventEmail(email);
							}
							appointmentRepository.save(appointmentCollection);

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to process event in calendar: " + e.getMessage());
		}
	}

	private Event createEvent(Appointment appointment) {
		String mobile = appointment.getPatient().getMobileNumber() != null ? appointment.getPatient().getMobileNumber()
				: "--";
		String patientEmail = appointment.getPatient().getEmailAddress() != null
				? appointment.getPatient().getEmailAddress()
				: "--";

		Event event = new Event().setSummary("Appointment with " + appointment.getPatient().getLocalPatientName())
				.setDescription("Appointment at " + appointment.getLocationName() + "\nMobile Number: " + mobile
						+ "\nEmail: " + patientEmail);

		// Time handling
		Date appointmentDate = appointment.getFromDate();
		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
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
				.setDateTime(new com.google.api.client.util.DateTime(calStart.getTime())).setTimeZone("Asia/Kolkata");
		EventDateTime end = new EventDateTime().setDateTime(new com.google.api.client.util.DateTime(calEnd.getTime()))
				.setTimeZone("Asia/Kolkata");
		event.setStart(start).setEnd(end);

		// Set reminders
		EventReminder[] reminderOverrides = { new EventReminder().setMethod("email").setMinutes(30),
				new EventReminder().setMethod("popup").setMinutes(10) };
		event.setReminders(new Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides)));

		return event;
	}

	public Boolean removeAllGoogleCalendarDataOnSignOut(String doctorId, String locationId) {
		Boolean response = false;
		try {
			// 1. Fetch token from DB
			GoogleTokenIdCollections tokenId = googleTokenIdRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));

			if (tokenId != null) {
				// 2. Refresh access token
				GoogleTokenResponse refreshedToken = new GoogleRefreshTokenRequest(new NetHttpTransport(),
						JacksonFactory.getDefaultInstance(), tokenId.getRefreshToken(), GOOGLE_WEB_CLIENT_ID,
						GOOGLE_WEB_CLIENT_SECRET).execute();

				AccessToken token = new AccessToken(refreshedToken.getAccessToken(), null);
				GoogleCredentials credentials = GoogleCredentials.create(token);
				com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
						GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
						new HttpCredentialsAdapter(credentials)).setApplicationName(APPLICATION_NAME).build();

				// 3. Find all appointments with Google events
				List<AppointmentCollection> appointments = appointmentRepository
						.findByDoctorIdLocationIdAndGoogleEventEmailAndEventIdNotNull(new ObjectId(doctorId),
								new ObjectId(locationId));

				for (AppointmentCollection appt : appointments) {
					try {
						if (appt.getEventId() != null && appt.getGoogleEventEmail() != null) {
							service.events().delete(appt.getGoogleEventEmail(), appt.getEventId()).execute();
						}
					} catch (GoogleJsonResponseException e) {
						if (e.getStatusCode() != 404) {
							e.printStackTrace(); // log and continue
						}
					}

					// 4. Clear event info
					appt.setEventId(null);
					appt.setGoogleEventEmail(null);
					appointmentRepository.save(appt);
				}

				// 5. Remove token entry
				googleTokenIdRepository.delete(tokenId);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Failed to remove Google calendar data on sign out: " + e.getMessage());
		}
		return response;
	}

//
//	public void addEventToGoogleCalendar(Appointment appointment, String emailDoctor, String state) {
//		AppointmentCollection appointmentCollection;
//		try {
//			if (appointment != null && appointment.getDoctorId() != null && appointment.getLocationId() != null) {
//				// Get refresh token from DB
//				GoogleTokenIdCollections tokenIdCollections = googleTokenIdRepository.findByDoctorIdAndLocationId(
//						new ObjectId(appointment.getDoctorId()), new ObjectId(appointment.getLocationId()));
//				if (tokenIdCollections != null) {
//					GoogleTokenResponse refreshedToken = new GoogleRefreshTokenRequest(new NetHttpTransport(),
//							JacksonFactory.getDefaultInstance(), tokenIdCollections.getRefreshToken(),
//							GOOGLE_WEB_CLIENT_ID, GOOGLE_WEB_CLIENT_SECRET).execute();
//
//					String email = tokenIdCollections.getEmail();
//
//					AccessToken token = new AccessToken(refreshedToken.getAccessToken(), null);
//					GoogleCredentials credentials = GoogleCredentials.create(token);
//					// Build calendar service with new access token
//					com.google.api.services.calendar.Calendar service = new com.google.api.services.calendar.Calendar.Builder(
//							GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
//							new HttpCredentialsAdapter(credentials)).setApplicationName(APPLICATION_NAME).build();
//
//					// Create the event
//					String mobile = appointment.getPatient().getMobileNumber() != null
//							? appointment.getPatient().getMobileNumber()
//							: "--";
//					String patientEmail = appointment.getPatient().getEmailAddress() != null
//							? appointment.getPatient().getEmailAddress()
//							: "--";
//
//					Event event = new Event()
//							.setSummary("Appointment with " + appointment.getPatient().getLocalPatientName())
//							.setDescription("Appointment at " + appointment.getLocationName() + "\nMobile Number: "
//									+ mobile + "\nEmail: " + patientEmail);
//					// Time handling
//					Date appointmentDate = appointment.getFromDate();
//					Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//					calStart.setTime(appointmentDate);
//
//					int fromMinutes = appointment.getTime() != null && appointment.getTime().getFromTime() != null
//							? appointment.getTime().getFromTime()
//							: 600;
//					int toMinutes = appointment.getTime() != null && appointment.getTime().getToTime() != null
//							? appointment.getTime().getToTime()
//							: 615;
//
//					calStart.set(Calendar.HOUR_OF_DAY, fromMinutes / 60);
//					calStart.set(Calendar.MINUTE, fromMinutes % 60);
//
//					Calendar calEnd = (Calendar) calStart.clone();
//					calEnd.set(Calendar.HOUR_OF_DAY, toMinutes / 60);
//					calEnd.set(Calendar.MINUTE, toMinutes % 60);
//
//					EventDateTime start = new EventDateTime()
//							.setDateTime(new com.google.api.client.util.DateTime(calStart.getTime()))
//							.setTimeZone("Asia/Kolkata");
//					EventDateTime end = new EventDateTime()
//							.setDateTime(new com.google.api.client.util.DateTime(calEnd.getTime()))
//							.setTimeZone("Asia/Kolkata");
//					event.setStart(start).setEnd(end);
//
//					// Set reminders
//					EventReminder[] reminderOverrides = { new EventReminder().setMethod("email").setMinutes(30),
//							new EventReminder().setMethod("popup").setMinutes(10) };
//					event.setReminders(
//							new Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides)));
//					
//					 if (state.equals(AppointmentState.CONFIRM.getState())) {
//		                    // Create the event
//		                    Event event = createEvent(appointment);
//		                    // Insert the event
//		                    event = service.events().insert(email, event).execute();
//		                    // Save event ID
//		                    if (appointmentCollection != null) {
//		                        appointmentCollection.setEventId(event.getId());
//		                        appointmentRepository.save(appointmentCollection);
//		                    }
//		                } else if (state.equals(AppointmentState.CANCEL.getState())) {
//		                    // Delete the event
//		                    if (appointmentCollection != null && appointmentCollection.getEventId() != null) {
//		                        service.events().delete(email, appointmentCollection.getEventId()).execute();
//		                        appointmentCollection.setEventId(null);
//		                        appointmentRepository.save(appointmentCollection);
//		                    }
//		                } else if (state.equals(AppointmentState.RESCHEDULE.getState())) {
//		                    // Update the event
//		                    if (appointmentCollection != null && appointmentCollection.getEventId() != null) {
//		                        Event updatedEvent = createEvent(appointment);
//		                        service.events().update(email, appointmentCollection.getEventId(), updatedEvent).execute();
//		                    }
//		                }
//
//					String eventId = event.getId();
//					appointmentCollection = appointmentRepository.findById(new ObjectId(appointment.getId()))
//							.orElse(null);
//					appointmentCollection.setEventId(eventId);
//					appointmentCollection = appointmentRepository.save(appointmentCollection);
//
//					Colors colors = service.colors().get().execute();
//
//					System.out.println("Event Colors:");
//					for (Map.Entry<String, ColorDefinition> entry : colors.getEvent().entrySet()) {
//						System.out.println(
//								"Color ID: " + entry.getKey() + ", Background: " + entry.getValue().getBackground()
//										+ ", Foreground: " + entry.getValue().getForeground());
//					}
//
//					System.out.println("\nCalendar Colors:");
//					for (Map.Entry<String, ColorDefinition> entry : colors.getCalendar().entrySet()) {
//						System.out.println(
//								"Color ID: " + entry.getKey() + ", Background: " + entry.getValue().getBackground()
//										+ ", Foreground: " + entry.getValue().getForeground());
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("Failed to add event to calendar: " + e.getMessage());
//		}
//	}
}