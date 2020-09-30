package com.dpdocter.services;

public interface UnifiedCommunicationServices {

	String createChatAccessToken(String userId);

	String createChatAccessTokenAndroid(String userId, String pushCredentialSID);

	String createVideoAccessToken(String userId, String room);

	Boolean createpushNotification(String userId, String room, String title, String callType);

	Boolean twilioPushNotification(String serviceSID);

	String createUser(String identity);

	

}
