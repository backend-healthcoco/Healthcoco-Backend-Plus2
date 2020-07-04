package com.dpdocter.services;

public interface UnifiedCommunicationServices {

	String createChatAccessToken(String userId);

	String createVideoAccessToken(String userId, String room);

	Boolean twilioPushNotification();

	Boolean createpushNotification(String userId, String room, String title);
	
	
}
