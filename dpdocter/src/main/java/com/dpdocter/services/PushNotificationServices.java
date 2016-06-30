package com.dpdocter.services;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.request.BroadcastNotificationRequest;

public interface PushNotificationServices {

	UserDevice addDevice(UserDevice request);

	Boolean notifyUser(String patientId, String message, String componentType, String componentTypeId);

	Boolean broadcastNotification(BroadcastNotificationRequest request);
	
	Boolean readNotification(String deviceId, Integer count);

}
