package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.request.UserSearchRequest;

public interface PushNotificationServices {

	UserDevice addDevice(UserDevice request);

	void notifyUser(String userId, String message, String componentType, String componentTypeId, List<UserDeviceCollection> userDeviceCollections);

	void broadcastNotification(BroadcastNotificationRequest request);
	
	void readNotification(String deviceId, Integer count);

	void notifyUser(String id, UserSearchRequest userSearchRequest, RoleEnum role, String message);

}
