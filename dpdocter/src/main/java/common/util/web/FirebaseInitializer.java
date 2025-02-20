package common.util.web;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseInitializer {
	public static void initializeFirebase() throws IOException {
		FileInputStream serviceAccount = new FileInputStream(
				"/home/ubuntu/dpdoctor-data/resource/HealthcocoPlus-1bc5d806cf76.json");

		FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
