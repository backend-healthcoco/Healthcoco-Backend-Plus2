package com.dpdocter.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Notification;
import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.PushNotificationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PushNotificationRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BroadcastNotificationRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.PushNotificationServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;

import common.util.web.DPDoctorUtils;
import common.util.web.FCMSender;

@Service
public class PushNotificationServicesImpl implements PushNotificationServices{

	private static Logger logger = Logger.getLogger(PushNotificationServicesImpl.class.getName());
	
	@Value("${doctor.android.google.services.api.key}")
    private String DOCTOR_GEOCODING_SERVICES_API_KEY;

	@Value("${patient.android.google.services.api.key}")
    private String PATIENT_GEOCODING_SERVICES_API_KEY;

	@Value("${ios.certificate.password.doctorApp}")
	private String iosCertificatePasswordDoctorApp;

	@Value("${ios.certificate.filename.doctorApp}")
	private String iosCertificateFileNameDoctorApp;

	@Value("${ios.certificate.password.patientApp}")
	private String iosCertificatePasswordPatientApp;

	@Value("${ios.certificate.filename.patientApp}")
	private String iosCertificateFileNamePatientApp;

	@Value("${ipad.certificate.password.doctorApp}")
	private String ipadCertificatePasswordDoctorApp;

	@Value("${ipad.certificate.filename.doctorApp}")
	private String ipadCertificateFileNameDoctorApp;

	@Value("${ipad.certificate.password.patientApp}")
	private String ipadCertificatePasswordPatientApp;

	@Value("${ipad.certificate.filename.patientApp}")
	private String ipadCertificateFileNamePatientApp;

	@Value("${ios.notification.sound.filepath}")
	private String iosNotificationSoundFilepath;
	
	@Value("${is.env.production}")
    private Boolean isEnvProduction;

	@Autowired
    private FileManager fileManager;
	
	@Autowired
    private UserDeviceRepository userDeviceRepository;

	@Autowired
    private PushNotificationRepository pushNotificationRepository;

	@Autowired
    private UserRepository userRepository;

	@Autowired
    private MongoTemplate mongoTemplate;
		
	@Value(value = "${image.path}")
    private String imagePath;

	@Override
	@Transactional
	public UserDevice addDevice(UserDevice request) {
		UserDevice response = null;
		try{
			if(!DPDoctorUtils.anyStringEmpty(request.getDeviceId())){
			UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(request.getDeviceId());
				if(userDeviceCollection == null){
					userDeviceCollection = new UserDeviceCollection();
					BeanUtil.map(request, userDeviceCollection);
					userDeviceCollection.setCreatedTime(new Date());
				}else{
					if(request.getUserIds() != null && !request.getUserIds().isEmpty()){
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for(String userId : request.getUserIds()){
							userIds.add(new ObjectId(userId));
						}
						userDeviceCollection.setUserIds(userIds);
					}else{
						userDeviceCollection.setUserIds(null);
					}
					userDeviceCollection.setDeviceId(request.getDeviceId());
					userDeviceCollection.setPushToken(request.getPushToken());
					userDeviceCollection.setDeviceType(request.getDeviceType());
					userDeviceCollection.setRole(request.getRole());
					userDeviceCollection.setUpdatedTime(new Date());
				}
				if(request.getRole().getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole()) && !DPDoctorUtils.anyStringEmpty(request.getMobileNumber())){
					List<UserCollection> userCollections = userRepository.findByMobileNumber(request.getMobileNumber());
					if(userCollections != null){
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for(UserCollection userCollection : userCollections){
							if(userCollection.getEmailAddress() == null)userIds.add(userCollection.getId());
							else if(!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())){
								userIds.add(userCollection.getId());
							}
						}
						userDeviceCollection.setUserIds(userIds);
					}
				}
				userDeviceRepository.save(userDeviceCollection);
				response = new UserDevice();
				BeanUtil.map(userDeviceCollection, response);
			}else{
				logger.error("Device ID cannot be null");
			    throw new BusinessException(ServiceError.InvalidInput, "Device ID cannot be null");
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
	public void notifyUser(String receiverId, String receiverLocationId, String receiverHospitalId, String message, String componentType, String componentTypeId, List<UserDeviceCollection> userDevices, String senderId,
			 String senderLocationId, String senderHospitalId) {
		List<UserDeviceCollection> userDeviceCollections = userDevices;
		try{
			if(userDeviceCollections == null || userDeviceCollections.isEmpty()){
				ObjectId userObjectId = new ObjectId(receiverId);	
				userDeviceCollections = userDeviceRepository.findByUserId(userObjectId);
			}
			if(userDeviceCollections != null && !userDeviceCollections.isEmpty()){
				for(UserDeviceCollection userDeviceCollection : userDeviceCollections){
					if(userDeviceCollection.getDeviceType() != null){
						if(userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.ANDROID.getType()))
							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(), userDeviceCollection.getPushToken(), message, componentType, componentTypeId, userDeviceCollection.getRole().getRole(), receiverId);
						else if(userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.IOS.getType()) || userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.IPAD.getType())){
							pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(), userDeviceCollection.getPushToken(), message, componentType, componentTypeId, userDeviceCollection.getDeviceType().getType(), userDeviceCollection.getRole().getRole(), receiverId);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount()+1);
							userDeviceRepository.save(userDeviceCollection);
						}	
					}
				}
				
			PushNotificationCollection notificationCollection = new PushNotificationCollection(null, 
						                                        !DPDoctorUtils.anyStringEmpty(senderId) ? new ObjectId(senderId) : null, 
						                                        !DPDoctorUtils.anyStringEmpty(senderLocationId) ? new ObjectId(senderLocationId) : null, 
						                                        !DPDoctorUtils.anyStringEmpty(senderHospitalId) ? new ObjectId(senderHospitalId) : null, 
						                                        !DPDoctorUtils.anyStringEmpty(receiverId) ? new ObjectId(receiverId) : null, 
						                                        !DPDoctorUtils.anyStringEmpty(receiverLocationId) ? new ObjectId(receiverLocationId) : null, 
						                                        !DPDoctorUtils.anyStringEmpty(receiverHospitalId) ? new ObjectId(receiverHospitalId) : null, null, null, message, componentType, componentTypeId);
			notificationCollection.setCreatedTime(new Date());notificationCollection.setUpdatedTime(new Date());
			pushNotificationRepository.save(notificationCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
	}
	
	public void pushNotificationOnAndroidDevices(String deviceId, String pushToken, String message, String componentType, String componentTypeId, String role, String userId) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;
			
			
			if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
				sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
			}else{
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}
			Notification notification = new Notification();
//			notification.setTitle("Healthcoco");
			notification.setText(message);
			notification.setTypeId(componentTypeId);notification.setType(componentType);
			notification.setReceiverId(userId);
			
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder()
							.delayWhileIdle(true)
							.addData("message", jsonOutput).build();

			Result result = sender.send(messageObj, pushToken, 1);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastPushNotificationOnAndroidDevices(List<String> deviceIds, List<String> pushTokens, String message, String imageURL, String role) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;
			
			if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
				sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
			}else{
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}
			
			Notification notification = new Notification();
			notification.setText(message);
			
			if(!DPDoctorUtils.anyStringEmpty(imageURL))notification.setImageURL(imageURL);
			
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			MulticastResult result = sender.send(messageObj, pushTokens, 1);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pushNotificationOnIosDevices(String deviceId, String pushToken, String message, String componentType, String componentTypeId, String deviceType, String role, String userId) {
		try {
			ApnsService service = null;
			if(isEnvProduction){
				if(deviceType.equalsIgnoreCase(DeviceType.IOS.getType())){
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
						.withProductionDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withProductionDestination()
								.build();
					}
				}else{
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
						.withProductionDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withProductionDestination()
								.build();
					}
				}
			}else{
				if(deviceType.equalsIgnoreCase(DeviceType.IOS.getType())){
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
						.withSandboxDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withSandboxDestination()
								.build();
					}
				}else{
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
						.withSandboxDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withSandboxDestination()
								.build();
					}
				}
			}
			
			
			Map<String, Object> customValues = new HashMap<String, Object>();
			if(!DPDoctorUtils.anyStringEmpty(componentType)){
				if(componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())){
					customValues.put("I", componentTypeId);customValues.put("T", "X");
					customValues.put("PI", userId);
				}
				else if(componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())){
					customValues.put("I", componentTypeId);customValues.put("T", "R");
					customValues.put("PI", userId);
				}
				else if(componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())){
					customValues.put("I", componentTypeId);customValues.put("T", "P");
				}
				else if(componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())){
					customValues.put("I", componentTypeId);customValues.put("T", "D");
				}
				else if(componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())){
					customValues.put("I", componentTypeId);customValues.put("T", "A");
				}
				else if(componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())){
					customValues.put("T", "C");
				}
			}
					String payload = APNS.newPayload()
							.alertBody(message)
							.sound("default")
							.customFields(customValues).build();
					service.push(pushToken, payload);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void broadcastPushNotificationOnIosDevices(List<String> deviceIds, List<String> pushToken, String message, String imageURL, String deviceType, String role) {
		try {
			ApnsService service = null;
			if(isEnvProduction){
				if(deviceType.equalsIgnoreCase(DeviceType.IOS.getType())){
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
						.withProductionDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withProductionDestination()
								.build();
					}
				}else{
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
						.withProductionDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withProductionDestination()
								.build();
					}
				}
			}else{
				if(deviceType.equalsIgnoreCase(DeviceType.IOS.getType())){
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
						.withSandboxDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withSandboxDestination()
								.build();
					}
				}else{
					if(role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
						service = APNS
						.newService()
						.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
						.withSandboxDestination()
						.build();
					}else{
						service = APNS
								.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withSandboxDestination()
								.build();
					}
				}
			}
			
			
			Map<String, Object> customValues = new HashMap<String, Object>();
			if(!DPDoctorUtils.anyStringEmpty(imageURL))customValues.put("img", imageURL);
			String payload = APNS.newPayload()
							.alertBody(message)
							.sound(iosNotificationSoundFilepath)
							.customFields(customValues).build();
			service.push(pushToken, payload);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void broadcastNotification(BroadcastNotificationRequest request) {
//		Boolean response = false;
		try{
			String imageUrl = null;
			if (request.getImage() != null) {
				String path = "broadcastImages";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path, true);
				imageUrl = getFinalImageURL(imageURLResponse.getImageUrl());
			    }
			Collection<String> pushTokens = null;
			Collection<String> deviceIds = null;
			List<UserDeviceCollection> deviceCollections = null;
			if(request.getUserType().equalsIgnoreCase(RoleEnum.DOCTOR.getRole())){
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
			    pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));	
			    deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, RoleEnum.DOCTOR.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.IOS.getType());			
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));		
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(), RoleEnum.DOCTOR.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}
			}else if(request.getUserType().equalsIgnoreCase(RoleEnum.PATIENT.getRole())){
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
			    pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
			    deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, RoleEnum.PATIENT.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.IOS.getType());			
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));	
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(), RoleEnum.PATIENT.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			}else{
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
			    pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));	
			    deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, RoleEnum.DOCTOR.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.IOS.getType());			
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(), RoleEnum.DOCTOR.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.DOCTOR.getRole(), DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));		
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
			    pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));		
			    deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, RoleEnum.PATIENT.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.IOS.getType());			
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(), RoleEnum.PATIENT.getRole());
				}
				
				deviceCollections = userDeviceRepository.findByRoleAndType(RoleEnum.PATIENT.getRole(), DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if(pushTokens != null && !pushTokens.isEmpty()){
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds), new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional
	public void readNotification(String deviceId, Integer count) {
		try{
			UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(deviceId);
			if(userDeviceCollection != null){
				if(count > 0 && userDeviceCollection.getBatchCount() > 0) userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount()-count);
				else if(count == 0)userDeviceCollection.setBatchCount(0);
				userDeviceRepository.save(userDeviceCollection);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void notifyUser(String receiverId, UserSearchRequest userSearchRequest, RoleEnum role, String message) {
		List<UserDeviceCollection> userDeviceCollections = null;

		try{
			if(role.equals(RoleEnum.PATIENT))
			{
				userDeviceCollections = userDeviceRepository.findByUserId(new ObjectId(receiverId));
			}
			if(userDeviceCollections != null && !userDeviceCollections.isEmpty()){
				for(UserDeviceCollection userDeviceCollection : userDeviceCollections){
					if(userDeviceCollection.getDeviceType() != null){
						if(userDeviceCollection.getDeviceType().getType().equalsIgnoreCase(DeviceType.ANDROID.getType())){
							userSearchRequest.setLatitude(null);
							userSearchRequest.setLocation(null);
							userSearchRequest.setLongitude(null);
							userSearchRequest.setUserId(null);
							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(), userDeviceCollection.getPushToken(), userSearchRequest, RoleEnum.PHARMIST,
									message);
						}
							
					}
				}
				PushNotificationCollection notificationCollection = new PushNotificationCollection(null, 
                        null, null, null, 
                        !DPDoctorUtils.anyStringEmpty(receiverId) ? new ObjectId(receiverId) : null, null, null, null, null, 
                        		message, "PHARMACY", null);
				notificationCollection.setCreatedTime(new Date());notificationCollection.setUpdatedTime(new Date());
				pushNotificationRepository.save(notificationCollection);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
	}
	
	private void pushNotificationOnAndroidDevices(String deviceId, String pushToken,
			UserSearchRequest userSearchRequest, RoleEnum role, String message) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			
			String jsonOutput = mapper.writeValueAsString(userSearchRequest);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			Result result = sender.send(messageObj, pushToken, 1);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
		    return imagePath + imageURL;
		} else
		    return null;
	    }

	@Override
	public List<Notification> getNotifications(int page, int size, String userId, String locationId, String hospitalId,	String updatedTime) {
		List<Notification> response = null;
		try {
			
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("receiverId").is(new ObjectId(userId));
			if(!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)){
				criteria.and("receiverLocationId").is(new ObjectId(locationId)).and("receiverHospitalId").is(new ObjectId(hospitalId));
			}
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			AggregationResults<Notification> results = mongoTemplate.aggregate(aggregation, PushNotificationCollection.class, Notification.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}
}
