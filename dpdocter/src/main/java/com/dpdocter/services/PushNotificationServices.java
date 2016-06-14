package com.dpdocter.services;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.request.BroadcastNotificationRequest;

public interface PushNotificationServices {

	UserDevice addDevice(UserDevice request);

	Boolean notifyUser(String patientId, String message, String type, String typeId);

	Boolean broadcastNotification(BroadcastNotificationRequest request);

	void pushNotificationOnIosDevices(String pushToken, String message, String type, String typeId);

}
