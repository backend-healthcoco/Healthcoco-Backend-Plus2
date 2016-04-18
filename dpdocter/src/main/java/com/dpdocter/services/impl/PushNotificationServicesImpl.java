package com.dpdocter.services.impl;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Notification;
import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.services.PushNotificationServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import common.util.web.DPDoctorUtils;

@Service
public class PushNotificationServicesImpl implements PushNotificationServices{

	private static Logger logger = Logger.getLogger(PushNotificationServicesImpl.class.getName());
	
	@Autowired
    private UserDeviceRepository userDeviceRepository;

	public void pushPUCReminderOnAndroidDevices() {
//		LOGGER.info("I am sending notification on android devices.");
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = new Sender("AIzaSyDzkYx7pgyraw9GZXmys92XyjNsdnkAWT8");

				Notification notification = new Notification();
				notification.setNotificationType("Healthcoco");
					notification.setText("To test");

					String jsonOutput = mapper.writeValueAsString(notification);
					System.out.println("Json Output : " + jsonOutput);
					Message message = new Message.Builder().timeToLive(30)
							.delayWhileIdle(true)
							.addData("message", jsonOutput).build();

					Result result = sender.send(message, 
							"f5lX0LN05ek:APA91bGTjZlbO5EdY4w_CvBtXmxcecJuczmr2hEOa8Ca-GQ1qlP6Vgv-L6PDH1Q47SyUBy8oHQlD0coLclQTpjPBU7a-RmYmfyJq_Ltd1g8-Er-P3cHzHryjLjKivpqvzzXzaKI9RoxB", 1);
//					LOGGER.info("Message Result: " + result.toString());
					// essarNotifications.add(notification);
				
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public UserDevice addDevice(UserDevice request) {
		UserDevice response = null;
		try{
			UserDeviceCollection userDeviceCollection = null;
			if(!DPDoctorUtils.anyStringEmpty(request.getUserId())){
				userDeviceCollection = userDeviceRepository.findByUserId(request.getUserId());
				if(userDeviceCollection == null){
					userDeviceCollection = new UserDeviceCollection();
					BeanUtil.map(request, userDeviceCollection);
					userDeviceCollection.setCreatedTime(new Date());
				}else{
					userDeviceCollection.setDeviceId(request.getDeviceId());
					userDeviceCollection.setDeviceType(request.getDeviceType());
					userDeviceCollection.setUpdatedTime(new Date());
				}
				userDeviceRepository.save(userDeviceCollection);
				response = new UserDevice();
				BeanUtil.map(userDeviceCollection, response);
			}else{
				logger.error("User ID cannot be null");
			    throw new BusinessException(ServiceError.InvalidInput, "User ID cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error(e + " Error while adding device : " + e.getCause().getMessage());
		    throw new BusinessException(ServiceError.Unknown, "Error while adding device : " + e.getCause().getMessage());
		}
		return response;
	}

}
