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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Notification;
import com.dpdocter.beans.UserDevice;
import com.dpdocter.collections.PushNotificationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DeviceType;
import com.dpdocter.enums.PushNotificationType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UserState;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PushNotificationRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BroadcastNotificationRequest;
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
public class PushNotificationServicesImpl implements PushNotificationServices {

	private static Logger logger = Logger.getLogger(PushNotificationServicesImpl.class.getName());

	@Value("${doctor.android.google.services.api.key}")
	private String DOCTOR_GEOCODING_SERVICES_API_KEY;

	@Value("${pharmist.android.google.services.api.key}")
	private String PHARMIST_GEOCODING_SERVICES_API_KEY;

	@Value("${patient.android.google.services.api.key}")
	private String PATIENT_GEOCODING_SERVICES_API_KEY;

	@Value("${doctor.pad.android.google.services.api.key}")
	private String DOCTOR_PAD_GEOCODING_SERVICES_API_KEY;

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
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getDeviceId())) {
				UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(request.getDeviceId());
				if (userDeviceCollection == null) {
					userDeviceCollection = new UserDeviceCollection();
					BeanUtil.map(request, userDeviceCollection);
					userDeviceCollection.setCreatedTime(new Date());
				} else {
					if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for (String userId : request.getUserIds()) {
							userIds.add(new ObjectId(userId));
						}
						userDeviceCollection.setUserIds(userIds);
					} else {
						userDeviceCollection.setUserIds(null);
					}
					userDeviceCollection.setDeviceId(request.getDeviceId());
					userDeviceCollection.setPushToken(request.getPushToken());
					userDeviceCollection.setDeviceType(request.getDeviceType());
					userDeviceCollection.setRole(request.getRole());
					userDeviceCollection.setUpdatedTime(new Date());
				}
				if (request.getRole().getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())
						&& !DPDoctorUtils.anyStringEmpty(request.getMobileNumber())) {
					List<UserCollection> userCollections = userRepository.findByMobileNumberAndUserState(request.getMobileNumber(), UserState.USERSTATECOMPLETE.getState());
					if (userCollections != null) {
						List<ObjectId> userIds = new ArrayList<ObjectId>();
						for (UserCollection userCollection : userCollections) {
							if (userCollection.getEmailAddress() == null)
								userIds.add(userCollection.getId());
							else if (!userCollection.getEmailAddress().equalsIgnoreCase(userCollection.getUserName())) {
								userIds.add(userCollection.getId());
							}
						}
						userDeviceCollection.setUserIds(userIds);
					}
				}
				userDeviceRepository.save(userDeviceCollection);
				response = new UserDevice();
				BeanUtil.map(userDeviceCollection, response);
			} else {
				logger.error("Device ID cannot be null");
				throw new BusinessException(ServiceError.InvalidInput, "Device ID cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding device : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding device : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	@Async
	public void notifyUser(String userId, String message, String componentType, String componentTypeId,
			List<UserDeviceCollection> userDevices) {
		List<UserDeviceCollection> userDeviceCollections = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				ObjectId userObjectId = new ObjectId(userId);
				userDeviceCollections = userDeviceRepository.findByUserIds(userObjectId);
			} else {
				userDeviceCollections = userDevices;
			}
			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.ANDROID_PAD.getType()))
							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getRole().getRole(), userId,
									userDeviceCollection.getDeviceType().getType());
						else if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.IOS.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.IPAD.getType())) {
							pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getDeviceType().getType(),
									userDeviceCollection.getRole().getRole(), userId);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
							userDeviceRepository.save(userDeviceCollection);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
		// return response;
	}

	public void pushNotificationOnAndroidDevices(String deviceId, String pushToken, String message,
			String componentType, String componentTypeId, String role, String userId, String deviceType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;

			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
					sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				} else {
					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
				}

			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}
			Notification notification = new Notification();
			// notification.setTitle("Healthcoco");
			notification.setText(message);
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					notification.setXi(componentTypeId);
					notification.setNotificationType(componentType);
					notification.setPi(userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
					notification.setPi(userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					notification.setDi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS_CANCELLATION.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					notification.setEi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_CONFIRMED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DELIVERED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DISPATCHED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_OUT_FOR_DELIVERY.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PACKED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PICKED_UP.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PLACED.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else{
					notification.setNotificationType(componentType);
				}
			}
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			Result result = sender.send(messageObj, pushToken, 1);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcastPushNotificationOnAndroidDevices(List<String> deviceIds, List<String> pushTokens,
			String message, String imageURL, String role, String deviceType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;
			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
					sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				} else {
					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
				}

			} else if (role.equalsIgnoreCase(RoleEnum.PHARMIST.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PHARMIST_GEOCODING_SERVICES_API_KEY);
			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}

			Notification notification = new Notification();

			// notification.setTitle("Healthcoco");
			notification.setText(message);

			if (!DPDoctorUtils.anyStringEmpty(imageURL))
				notification.setImg(imageURL);

			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			MulticastResult result = sender.send(messageObj, pushTokens, 1);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, imageURL, PushNotificationType.BROADCAST);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void pushNotificationOnIosDevices(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId) {
		try {
			ApnsService service = null;
			if (isEnvProduction) {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				}
			} else {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withSandboxDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withSandboxDestination().build();
					}
				}
			}

			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					customValues.put("XI", componentTypeId);
					customValues.put("T", "X");
					customValues.put("PI", userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "R");
					customValues.put("PI", userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "P");
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					customValues.put("DI", componentTypeId);
					customValues.put("T", "D");
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "A");
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					customValues.put("T", "C");
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DLR");
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "UR");
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "SI");
					isSilent = true;
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

					customValues.put("RI", componentTypeId);
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDI");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DWR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RX");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RPV");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RCN");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RT");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RDS");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBI");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					customValues.put("AI",componentTypeId);
					customValues.put("T", "AR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
					customValues.put("AI",componentTypeId);
					customValues.put("T", "ASC");
					isSilent = true;
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "BA");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "GC");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "VN");
				}
//				else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RX");
//				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RPV");
//				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RCN");
//				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RT");
//				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RR");
//				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RDS");
//				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RI");
//				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RR");
//				}
			}
			String payload = null;
			
			if(isSilent) {
				payload = APNS.newPayload().customFields(customValues).forNewsstand().build();
			}else {
				payload = APNS.newPayload().alertBody(message).sound("default").customFields(customValues).build();
			}
			service.push(pushToken, payload);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.valueOf(deviceType.toUpperCase()), null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void broadcastPushNotificationOnIosDevices(List<String> deviceIds, List<String> pushToken, String message,
			String imageURL, String deviceType, String role) {
		try {
			ApnsService service = null;
			if (isEnvProduction) {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				}
			} else {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withSandboxDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withSandboxDestination().build();
					}
				}
			}

			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(imageURL))
				customValues.put("img", imageURL);
			String payload = APNS.newPayload().alertBody(message).sound(iosNotificationSoundFilepath)
					.customFields(customValues).build();
			service.push(pushToken, payload);

			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.valueOf(deviceType.toUpperCase()), null, PushNotificationType.BROADCAST);
			pushNotificationRepository.save(pushNotificationCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void broadcastNotification(BroadcastNotificationRequest request) {
		// Boolean response = false;
		try {
			String imageUrl = null;
			if (request.getImage() != null) {
				String path = "broadcastImages";
				// save image
				request.getImage().setFileName(request.getImage().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getImage(), path,
						true);
				imageUrl = getFinalImageURL(imageURLResponse.getImageUrl());
			}
			Collection<String> pushTokens = null;
			Collection<String> deviceIds = null;
			List<UserDeviceCollection> deviceCollections = null;
			if (request.getUserType().equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}
			} else if (request.getUserType().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.PATIENT.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			} else {
				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.DOCTOR.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.DOCTOR.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.DOCTOR.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.ANDROID_PAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							RoleEnum.PATIENT.getRole(), DeviceType.ANDROID_PAD.getType());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IOS.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl, DeviceType.IOS.getType(),
							RoleEnum.PATIENT.getRole());
				}

				deviceCollections = userDeviceRepository.findByRoleAndDeviceType(RoleEnum.PATIENT.getRole(),
						DeviceType.IPAD.getType());
				pushTokens = CollectionUtils.collect(deviceCollections,
						new BeanToPropertyValueTransformer("pushToken"));
				deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
				if (pushTokens != null && !pushTokens.isEmpty()) {
					broadcastPushNotificationOnIosDevices(new ArrayList<String>(deviceIds),
							new ArrayList<String>(pushTokens), request.getMessage(), imageUrl,
							DeviceType.IPAD.getType(), RoleEnum.PATIENT.getRole());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return response;
	}

	@Override
	@Transactional
	public void readNotification(String deviceId, Integer count) {
		// Boolean response = false;
		try {
			UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(deviceId);
			if (userDeviceCollection != null) {
				if (count > 0 && userDeviceCollection.getBatchCount() > 0)
					userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() - count);
				else if (count == 0)
					userDeviceCollection.setBatchCount(0);
				userDeviceRepository.save(userDeviceCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return response;

	}

	@Override
	@Async
	public void notifyPharmacy(String id, String requestId, String responseId, RoleEnum role, String message) {
		List<UserDeviceCollection> userDeviceCollections = null;

		try {

			if (role.equals(RoleEnum.PHARMIST) || role.equals(RoleEnum.COLLECTION_BOY)
					|| role.equals(RoleEnum.DENTAL_COLLECTION_BOY) || role.equals(RoleEnum.REFRESH)
					|| role.equals(RoleEnum.DENTAL_WORK_REFRESH) || role.equals(RoleEnum.DENTAL_WORKS_CANCELLATION)) {
				userDeviceCollections = userDeviceRepository.findByLocaleId(new ObjectId(id));
			}
			if (role.equals(RoleEnum.PATIENT)) {
				userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(id));
			}
			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
							if (role.equals(RoleEnum.COLLECTION_BOY)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message,
										ComponentType.LAB_REQUEST.getType(), null, null, role.getRole());
							} else if (role.equals(RoleEnum.REFRESH)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message, ComponentType.REFRESH.getType(),
										null, null, role.getRole());
							} else if (role.equals(RoleEnum.DENTAL_COLLECTION_BOY)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message,
										ComponentType.DENTAL_LAB_REQUEST.getType(), null, null, role.getRole());
							} else if (role.equals(RoleEnum.COLLECTION_BOY_REFRESH)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message, ComponentType.REFRESH.getType(),
										null, null, role.getRole());
							} else if (role.equals(RoleEnum.DENTAL_WORK_REFRESH)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message,
										ComponentType.DENTAL_WORK_REFRESH.getType(), null, null, role.getRole());
							} else if (role.equals(RoleEnum.DENTAL_WORKS_CANCELLATION)) {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message,
										ComponentType.DENTAL_WORKS_CANCELLATION.getType(), null, null, role.getRole());
							} else {
								pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
										userDeviceCollection.getPushToken(), message,
										ComponentType.USER_ORDER.getType(), requestId, responseId, role.getRole());
							}

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
	}

	public void pushNotificationOnAndroidDevices(String deviceId, String pushToken, String message,
			String componentType, String requestId, String responseId, String role) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;

			if (role.toString().equalsIgnoreCase(RoleEnum.PHARMIST.getRole().toString())
					|| role.equals(RoleEnum.COLLECTION_BOY.getRole()) || role.equals(RoleEnum.REFRESH.getRole())
					|| role.equals(RoleEnum.DENTAL_COLLECTION_BOY.getRole())
					|| role.equals(RoleEnum.DENTAL_WORK_REFRESH.getRole())
					|| role.equals(RoleEnum.DENTAL_WORKS_CANCELLATION.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PHARMIST_GEOCODING_SERVICES_API_KEY);
			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}

			Notification notification = new Notification();
			notification.setTitle("Healthcoco");
			notification.setText(message);

			if (requestId != null) {
				notification.setReq(requestId);
			}

			if (responseId != null) {
				notification.setRes(responseId);
			}

			notification.setNotificationType(componentType);
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();
			Result result = sender.send(messageObj, pushToken, 1);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
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
	public Boolean notifyRefresh(String id, String requestId, String responseId, RoleEnum role, String message,
			ComponentType componentType) {
		Boolean response = false;
		List<UserDeviceCollection> userDeviceCollections = null;

		try {
			if (role.equals(RoleEnum.PHARMIST)) {
				userDeviceCollections = userDeviceRepository.findByLocaleId(new ObjectId(id));
			}
			if (role.equals(RoleEnum.PATIENT)) {
				userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(id));
			}
			if (role.equals(RoleEnum.PATIENT)) {
				userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(id));
			}
			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())) {

							pushNotificationOnAndroidDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType.getType(), requestId,
									responseId, role.getRole(), null);
							response = true;
						} else if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.IOS.getType()) && role.equals(RoleEnum.PATIENT)) {
							pushNotificationOnIosDevices(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType.getType(), requestId,
									responseId, userDeviceCollection.getRole().getRole(), null);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
							userDeviceRepository.save(userDeviceCollection);
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean notifyRefreshAll(RoleEnum role, List<ObjectId> LocaleIds, String message,
			ComponentType componentType) {
		Boolean response = false;
		List<UserDeviceCollection> deviceCollections = null;
		Collection<String> pushTokens = null;
		Collection<String> deviceIds = null;
		if (role.equals(RoleEnum.PHARMIST)) {
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria().and("role").is(RoleEnum.PHARMIST.getRole())
							.and("deviceType").is(DeviceType.ANDROID.getType()).and("localeId").in(LocaleIds)));
			deviceCollections = mongoTemplate
					.aggregate(aggregation, UserDeviceCollection.class, UserDeviceCollection.class).getMappedResults();
		}

		pushTokens = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("pushToken"));
		deviceIds = CollectionUtils.collect(deviceCollections, new BeanToPropertyValueTransformer("deviceId"));
		if (pushTokens != null && !pushTokens.isEmpty() && deviceIds != null && !deviceIds.isEmpty()) {
			broadcastPushNotificationOnAndroidDevices(new ArrayList<String>(deviceIds),
					new ArrayList<String>(pushTokens), message, null, role.toString(), DeviceType.ANDROID.getType());
			response = true;
		}

		return response;
	}
	
	
	@Override
	@Transactional
	@Async
	public void notifyUserTwilio(String userId, String message, String componentType, String componentTypeId,
			String room,String title,List<UserDeviceCollection> userDevices,String callType) {
		List<UserDeviceCollection> userDeviceCollections = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				ObjectId userObjectId = new ObjectId(userId);
				userDeviceCollections = userDeviceRepository.findByUserIds(userObjectId);
			} else {
				userDeviceCollections = userDevices;
			}
			if (userDeviceCollections != null && !userDeviceCollections.isEmpty()) {
				for (UserDeviceCollection userDeviceCollection : userDeviceCollections) {
					if (userDeviceCollection.getDeviceType() != null) {
						if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.ANDROID.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.ANDROID_PAD.getType()))
							pushNotificationOnAndroidDevicesTwilio(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getRole().getRole(), userId,
									userDeviceCollection.getDeviceType().getType(),room,title,callType);
						else if (userDeviceCollection.getDeviceType().getType()
								.equalsIgnoreCase(DeviceType.IOS.getType())
								|| userDeviceCollection.getDeviceType().getType()
										.equalsIgnoreCase(DeviceType.IPAD.getType())) {
							pushNotificationOnIosDevicesTwilio(userDeviceCollection.getDeviceId(),
									userDeviceCollection.getPushToken(), message, componentType, componentTypeId,
									userDeviceCollection.getDeviceType().getType(),
									userDeviceCollection.getRole().getRole(), userId,room,title,callType);
							userDeviceCollection.setBatchCount(userDeviceCollection.getBatchCount() + 1);
							userDeviceRepository.save(userDeviceCollection);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while pushing notification: " + e.getCause().getMessage());
		}
		// return response;
	}

	
	
	
	public void pushNotificationOnIosDevicesTwilio(String deviceId, String pushToken, String message, String componentType,
			String componentTypeId, String deviceType, String role, String userId,String room,String title,String callType) {
		try {
			ApnsService service = null;
			if (isEnvProduction) {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withProductionDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withProductionDestination().build();
					}
				}
			} else {
				if (deviceType.equalsIgnoreCase(DeviceType.IOS.getType())) {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(iosCertificateFileNameDoctorApp, iosCertificatePasswordDoctorApp)
								
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(iosCertificateFileNamePatientApp, iosCertificatePasswordPatientApp)
								
								.withSandboxDestination().build();
					}
				} else {
					if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
						service = APNS.newService()
								.withCert(ipadCertificateFileNameDoctorApp, ipadCertificatePasswordDoctorApp)
								.withSandboxDestination().build();
					} else {
						service = APNS.newService()
								.withCert(ipadCertificateFileNamePatientApp, ipadCertificatePasswordPatientApp)
								.withSandboxDestination().build();
					}
				}
			}

			message=null;
			message=title +"is calling";
			Boolean isSilent = false;
			Map<String, Object> customValues = new HashMap<String, Object>();
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					customValues.put("XI", componentTypeId);
					customValues.put("T", "X");
					customValues.put("PI", userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "R");
					customValues.put("PI", userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
					customValues.put("PI", componentTypeId);
					customValues.put("T", "P");
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					customValues.put("DI", componentTypeId);
					customValues.put("T", "D");
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					customValues.put("AI", componentTypeId);
					customValues.put("T", "A");
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					customValues.put("T", "C");
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DLR");
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "UR");
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "SI");
					isSilent = true;
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {

					customValues.put("RI", componentTypeId);
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "RDI");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DWR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RX");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RPV");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RCN");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RT");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RDS");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBI");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					customValues.put("PI",componentTypeId);
					customValues.put("T", "RBR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_REFRESH.getType())) {
					customValues.put("AI",componentTypeId);
					customValues.put("T", "AR");
					isSilent = true;
				}else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT_STATUS_CHANGE.getType())) {
					customValues.put("AI",componentTypeId);
					customValues.put("T", "ASC");
					isSilent = true;
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					customValues.put("RI", componentTypeId);
					customValues.put("T", "DI");
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					customValues.put("EI", componentTypeId);
					customValues.put("T", "E");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "PR");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DENTAL_IMAGING.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DI");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORK_REFRESH.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "DW");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "BA");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "GC");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					customValues.put("RI", "SILENT");
					customValues.put("T", "VN");
				}
				else if (componentType.equalsIgnoreCase(ComponentType.CONSULTATION_VIDEO_CALL.getType())) {
					customValues.put("CI", room);
					customValues.put("T", "VC");
					customValues.put("PI", userId);
					customValues.put("CT", callType);
				}
//				else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RX");
//				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RPV");
//				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RCN");
//				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RT");
//				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RR");
//				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RDS");
//				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RI");
//				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
//					customValues.put("PI",componentTypeId);
//					customValues.put("T", "RR");
//				}
			}
			String payload = null;
			
			if(isSilent) {
				payload = APNS.newPayload().customFields(customValues).forNewsstand().build();
			}else {
				payload = APNS.newPayload().alertBody(message).sound("default").customFields(customValues).build();
			}
			service.push(pushToken, payload);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.valueOf(deviceType.toUpperCase()), null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void pushNotificationOnAndroidDevicesTwilio(String deviceId, String pushToken, String message,
			String componentType, String componentTypeId, String role, String userId, String deviceType,String room,String title,String callType) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Sender sender = null;

			if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
				// sender = new Sender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				if (deviceType.equalsIgnoreCase(DeviceType.ANDROID.getType())) {
					sender = new FCMSender(DOCTOR_GEOCODING_SERVICES_API_KEY);
				} else {
					sender = new FCMSender(DOCTOR_PAD_GEOCODING_SERVICES_API_KEY);
				}

			} else {
				// sender = new Sender(PATIENT_GEOCODING_SERVICES_API_KEY);
				sender = new FCMSender(PATIENT_GEOCODING_SERVICES_API_KEY);
			}
			Notification notification = new Notification();
			// notification.setTitle("Healthcoco");
			message=null;
			message=title;
			notification.setText(message);
			if (!DPDoctorUtils.anyStringEmpty(componentType)) {
				if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTIONS.getType())) {
					notification.setXi(componentTypeId);
					notification.setNotificationType(componentType);
					notification.setPi(userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
					notification.setPi(userId);
				} else if (componentType.equalsIgnoreCase(ComponentType.PATIENT.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR.getType())) {
					notification.setDi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.APPOINTMENT.getType())) {
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.CALENDAR_REMINDER.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.USER_RECORD.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_WORKS_CANCELLATION.getType())) {
					notification.setCi(componentTypeId);
					notification.setNotificationType(componentType);
				} else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.DENTAL_IMAGING_REQUEST.getType())) {
					notification.setRi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.EVENT.getType())) {
					notification.setEi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.PRESCRIPTION_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.PATIENT_VISIT_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.CLINICAL_NOTES_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.TREATMENTS_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECORDS_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.DISCHARGE_SUMMARY_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.INVOICE_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.RECEIPT_REFRESH.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_GROWTH_CHART.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.REFRESH_VACCINATION.getType())) {
					notification.setPi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_CONFIRMED.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DELIVERED.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.ORDER_DISPATCHED.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_OUT_FOR_DELIVERY.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PACKED.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PICKED_UP.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}else if (componentType.equalsIgnoreCase(ComponentType.ORDER_PLACED.getType())) {
					notification.setPi(userId);
					notification.setAi(componentTypeId);
					notification.setNotificationType(componentType);
				}
				else if (componentType.equalsIgnoreCase(ComponentType.CONSULTATION_VIDEO_CALL.getType())) {
					notification.setPi(userId);
					notification.setXi(room);
					notification.setNotificationType(componentType);
					notification.setRi(callType);
				}
				else{
					notification.setNotificationType(componentType);
					
				}
			}
			String jsonOutput = mapper.writeValueAsString(notification);
			Message messageObj = new Message.Builder().delayWhileIdle(true).addData("message", jsonOutput).build();

			Result result = sender.send(messageObj, pushToken, 1);
			List<String> deviceIds = new ArrayList<String>();
			deviceIds.add(deviceId);
			PushNotificationCollection pushNotificationCollection = new PushNotificationCollection(null, deviceIds,
					message, DeviceType.ANDROID, null, PushNotificationType.INDIVIDUAL);
			pushNotificationRepository.save(pushNotificationCollection);
			logger.info("Message Result: " + result.toString());
		} catch (JsonProcessingException jpe) {
			jpe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




}
