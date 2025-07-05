package common.util.web;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import okhttp3.*;
import okhttp3.Response;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class FCMSender {

	private static final String PROJECT_ID = "healthcocoplus-1383";
	private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
	private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/" + PROJECT_ID
			+ "/messages:send";

	public static String getAccessToken() throws IOException {
		GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(
				"/home/ubuntu/dpdoctor-data/resource/healthcocoplus-1383-firebase-adminsdk-5rgsw-6797fdc4fd.json"))
				.createScoped(Collections.singleton(MESSAGING_SCOPE));
		credentials.refreshIfExpired();
		return credentials.getAccessToken().getTokenValue();
	}

	public static void sendMessage(String token) throws IOException {
		OkHttpClient client = new OkHttpClient();
		String accessToken = getAccessToken();

		JsonObject message = new JsonObject();
		JsonObject notification = new JsonObject();
		notification.addProperty("title", "Test Notification");
		notification.addProperty("body", "This is a test message");

		JsonObject data = new JsonObject();
		data.addProperty("customKey", "customValue");

		JsonObject messageObject = new JsonObject();
		messageObject.add("notification", notification);
		messageObject.add("data", data);
		messageObject.addProperty("token", token);

		message.add("message", messageObject);

		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), message.toString());

		Request request = new Request.Builder().url(FCM_ENDPOINT).post(body)
				.addHeader("Authorization", "Bearer " + accessToken).addHeader("Content-Type", "application/json")
				.build();

		Response response = client.newCall(request).execute();
	}

}
