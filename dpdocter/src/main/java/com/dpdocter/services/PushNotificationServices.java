package com.dpdocter.services;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.request.BroadcastNotificationRequest;

public interface PushNotificationServices {

	UserDevice addDevice(UserDevice request);

	void notifyUser(String userId, String message, String componentType, String componentTypeId);

	void broadcastNotification(BroadcastNotificationRequest request);
	
	void readNotification(String deviceId, Integer count);

}
