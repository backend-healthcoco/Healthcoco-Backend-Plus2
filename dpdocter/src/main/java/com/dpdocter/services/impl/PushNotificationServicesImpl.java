package com.dpdocter.services.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Notification;
import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.services.PushNotificationServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import common.util.web.DPDoctorUtils;

@Service
public class PushNotificationServicesImpl implements PushNotificationServices{

	private static Logger logger = Logger.getLogger(PushNotificationServicesImpl.class.getName());
	
	@Value("${geocoding.services.api.key}")
    private String GEOCODING_SERVICES_API_KEY;

	@Value("${ios.certificate.password}")
	private String iosCertificatePassword;

	@Value("${ios.cerificate.filename}")
	private String iosCertificateFileName;
	
	@Value("${ios.notification.sound.filepath}")
	private String iosNotificationSoundFilepath;
	
	@Autowired
    private UserDeviceRepository userDeviceRepository;

	@Autowired
    private UserRepository userRepository;

	@Override
	@Transactional
	public UserDevice addDevice(UserDevice request) {
		UserDevice response = null;
		try{
			UserDeviceCollection userDeviceCollection = null;
			if(!DPDoctorUtils.anyStringEmpty(request.getUserId())){
				UserCollection userCollection = userRepository.findOne(request.getUserId());
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
				if(userCollection != null){
					if(userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())) 
						 userDeviceCollection.setRole(RoleEnum.DOCTOR);
					else userDeviceCollection.setRole(RoleEnum.PATIENT);
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

	@Override
	@Transactional
	public Boolean notifyUser(String patientId, String message) {
		Boolean response = false;
		try{
			UserDeviceCollection userDeviceCollection = userDeviceRepository.findByUserId(patientId);
			if(userDeviceCollection != null){
				if(userDeviceCollection.getDeviceType() != null){
					if(userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.ANDROID.getType()))
						pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(), message);
					else if(userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.IOS.getType()))
						pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(), message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
//		    throw new BusinessException(ServiceError.Unknown, "Error while pushing notification: " + e.getCause().getMessage());
		}
		return response;
	}
	
	public void pushNotificationOnAndroidDevices(String deviceId, String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = new Sender(GEOCODING_SERVICES_API_KEY);

				Notification notification = new Notification();
				notification.setNotificationType("Healthcoco");
				notification.setText(message);

					String jsonOutput = mapper.writeValueAsString(notification);
					Message messageObj = new Message.Builder().timeToLive(30)
							.delayWhileIdle(true)
							.addData("message", jsonOutput).build();

					Result result = sender.send(messageObj, deviceId, 1);
					logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pushNotificationOnIosDevices(String deviceId, String message) {
		try {
			ApnsService service = APNS
					.newService()
					.withCert(iosCertificateFileName,iosCertificatePassword)
					.withSandboxDestination()
					.build();
			Map<String, Object> customValues = new HashMap<String, Object>();
			customValues.put("type", "Healthcoco");
			
					String payload = APNS.newPayload()
							.alertBody(message)
							.sound(iosNotificationSoundFilepath)
							.customFields(customValues).build();
					service.push(deviceId, payload);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public Boolean broadcastNotification(BroadcastNotificationRequest request) {
		Boolean response = false;
		try{
			if(request.getUserType() == null){
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
