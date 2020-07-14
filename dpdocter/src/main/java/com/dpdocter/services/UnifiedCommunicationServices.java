package com.dpdocter.services;

public interface UnifiedCommunicationServices {

	String createChatAccessToken(String userId);

	String createVideoAccessToken(String userId, String room);

	public Boolean createpushNotification(String userId, String room, String title);

	Boolean twilioPushNotification();

}
