package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.CBDTArch;
import com.dpdocter.beans.CBDTQuadrant;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalDiagnosticServiceRequest;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingInvoice;
import com.dpdocter.beans.DentalImagingInvoiceItem;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DentalImagingServiceVisitCount;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.FOV;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.CBDTArchCollection;
import com.dpdocter.collections.CBDTQuadrantCollection;
import com.dpdocter.collections.DentalDiagnosticServiceCollection;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.DentalImagingInvoiceCollection;
import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;
import com.dpdocter.collections.DentalImagingReportsCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorHospitalDentalImagingAssociationCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.FOVCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.response.DentalImagingInvoiceResponse;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.SearchType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalImagingInvoiceRepository;
import com.dpdocter.repository.DentalImagingLocationServiceAssociationRepository;
import com.dpdocter.repository.DentalImagingReportsRepository;
import com.dpdocter.repository.DentalImagingRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DoctorHospitalDentalImagingAssociationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DentalImagingLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalImagingReportsAddRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DentalImagingDataResponse;
import com.dpdocter.response.DentalImagingInvoiceItemResponse;
import com.dpdocter.response.DentalImagingInvoiceJasper;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DentalImagingVisitAnalyticsResponse;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PatientDentalImagignVisitAnalyticsResponse;
import com.dpdocter.services.DentalImagingService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SignUpService;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DentalImagingServiceImpl implements DentalImagingService {

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	DentalImagingRepository dentalImagingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PatientRepository patientRepository;

	@Autowired
	DentalImagingLocationServiceAssociationRepository dentalImagingLocationServiceAssociationRepository;

	@Autowired
	FileManager fileManager;

	@Autowired
	DentalImagingReportsRepository dentalImagingReportsRepository;

	@Autowired
	DoctorHospitalDentalImagingAssociationRepository doctorHospitalDentalImagingAssociationRepository;

	@Autowired
	SMSServices smsServices;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	RegistrationService registrationService;

	@Autowired
	ESRegistrationService esRegistrationService;

	@Autowired
	TransactionalManagementService transnationalService;

	@Autowired
	SignUpService signUpService;

	@Autowired
	DentalImagingInvoiceRepository dentalImagingInvoiceRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Value(value = "${jasper.print.imaging.works.invoice.fileName}")
	private String dentalInvoiceA4FileName;

	private static Logger logger = Logger.getLogger(DentalImagingServiceImpl.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Value(value = "${jasper.templates.resource}")
	private String JASPER_TEMPLATES_RESOURCE;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Value(value = "${doctor.app.bit.link}")
	private String DOCTOR_APP_LINK;

	@Override
	@Transactional
	public DentalImagingResponse addEditDentalImagingRequest(DentalImagingRequest request) {
		DentalImagingResponse response = null;
		DentalImagingCollection dentalImagingCollection = null;
		String requestId = null;
		LocationCollection locationCollection = null;
		try {

			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByLocationId(new ObjectId(request.getDentalImagingLocationId()));
			locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
			if (request.getId() != null) {
				dentalImagingCollection = dentalImagingRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (dentalImagingCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setPatientName(request.getLocalPatientName());
				dentalImagingCollection.setServices(request.getServices());
				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(String.valueOf(doctorClinicProfileCollection.getDoctorId()),
							"Request Has been updated.", ComponentType.REFRESH_DENTAL_IMAGING.getType(),
							String.valueOf(dentalImagingCollection.getId()), null);

				}
				pushNotificationServices.notifyUser(request.getDoctorId(), "Dental imaging request has been updated.",
						ComponentType.DENTAL_IMAGING_REQUEST.getType(), String.valueOf(dentalImagingCollection.getId()),
						null);

			} else {
				ObjectId doctorId = new ObjectId(request.getDoctorId()),
						locationId = new ObjectId(request.getLocationId()),
						hospitalId = new ObjectId(request.getHospitalId()), patientId = null;
				patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);
				if (DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
					request.setPatientId(patientId.toString());
				}
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				requestId = UniqueIdInitial.DENTAL_IMAGING.getInitial() + DPDoctorUtils.generateRandomId();
				dentalImagingCollection = new DentalImagingCollection();
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setPatientName(request.getLocalPatientName());
				dentalImagingCollection.setServices(request.getServices());
				dentalImagingCollection.setRequestId(requestId);
				dentalImagingCollection.setCreatedTime(new Date());
				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection.setCreatedBy(userCollection.getFirstName());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);

				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(String.valueOf(doctorClinicProfileCollection.getDoctorId()),
							"You have new dental imaging request.", ComponentType.DENTAL_IMAGING_REQUEST.getType(),
							String.valueOf(dentalImagingCollection.getId()), null);
				}

				pushNotificationServices.notifyUser(request.getDoctorId(),
						"New dental imaging request has been created.", ComponentType.DENTAL_IMAGING_REQUEST.getType(),
						String.valueOf(dentalImagingCollection.getId()), null);

				if (request.getType() != null) {

					if (request.getType().equalsIgnoreCase("DOCTOR")) {
						if (locationCollection != null) {
							String message = "Healthcoco! Hi, {doctorName} has suggested {patientName} ({patientnumber}) for {locationName}.";
							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
							smsTrackDetail.setDoctorId(doctorId);
							smsTrackDetail.setLocationId(locationId);
							smsTrackDetail.setHospitalId(hospitalId);
							smsTrackDetail.setType("DENTAL_IMAGING_REQUEST");
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(userCollection.getId());
							SMS sms = new SMS();
							smsDetail.setUserName(userCollection.getFirstName());
							message = message.replace("{doctorName}",
									userCollection.getTitle() + userCollection.getFirstName());
							message = message.replace("{patientName}", request.getLocalPatientName());
							message = message.replace("{patientnumber}", request.getMobileNumber());
							message = message.replace("{locationName}", locationCollection.getLocationName());
							sms.setSmsText(message);
							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(locationCollection.getClinicNumber());
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							smsServices.sendSMS(smsTrackDetail, true);
						}

						StringBuilder builder = new StringBuilder();
						builder.append("Healthcoco - {patientName} ! {doctorName} has suggested you dental scan(s).\n");
						builder.append("\n");
						for (DentalDiagnosticServiceRequest serviceRequest : request.getServices()) {
							if (serviceRequest.getToothNumber() != null) {
								builder.append(serviceRequest.getServiceName() + "(" + serviceRequest.getType()
										+ ") tooth no." + serviceRequest.getToothNumber() + "\n");
							} else if (serviceRequest.getCBCTQuadrant() != null) {
								builder.append(serviceRequest.getServiceName() + "(" + serviceRequest.getType()
										+ ") CBDT Quadrant : " + serviceRequest.getCBCTQuadrant() + "\n");
							}

							else if (serviceRequest.getCBCTArch() != null) {
								builder.append(serviceRequest.getServiceName() + "(" + serviceRequest.getType()
										+ ") CBDT Arch : " + serviceRequest.getCBCTArch() + "\n");
							}

							else if (serviceRequest.getFov() != null) {
								builder.append(serviceRequest.getServiceName() + "(" + serviceRequest.getType()
										+ ") FOV : " + serviceRequest.getFov() + "\n");
							}

							else if (serviceRequest.getInstruction() != null) {
								builder.append(serviceRequest.getServiceName() + "(" + serviceRequest.getType()
										+ ") Instruction : " + serviceRequest.getInstruction() + "\n");
							}

							else {
								builder.append(
										serviceRequest.getServiceName() + "(" + serviceRequest.getType() + ")" + "\n");
							}

						}
						if (request.getSpecialInstructions() != null) {
							builder.append(request.getSpecialInstructions() + "\n");
						}
						builder.append("\n");
						builder.append(
								"Imaging centre : {locationName} ({clinicNumber} {locationAddress}). {locationMapLink}");
						String text = builder.toString();
						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
						smsTrackDetail.setDoctorId(doctorId);
						smsTrackDetail.setLocationId(locationId);
						smsTrackDetail.setHospitalId(hospitalId);
						smsTrackDetail.setType("DENTAL_IMAGING_REQUEST");
						SMSDetail smsDetail = new SMSDetail();
						smsDetail.setUserId(userCollection.getId());
						SMS sms = new SMS();
						smsDetail.setUserName(request.getLocalPatientName());
						text = text.replace("{patientName}", request.getLocalPatientName());
						text = text.replace("{doctorName}", userCollection.getTitle() + userCollection.getFirstName());
						text = text.replace("{locationName}", locationCollection.getLocationName());

						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}

						if (address != null) {
							text = text.replace("{locationAddress}", address);
						} else {
							text = text.replace("{locationAddress}", "");
						}

						if (locationCollection.getClinicNumber() != null) {
							text = text.replace("{clinicNumber}", locationCollection.getClinicNumber());
						} else {
							text = text.replace("{clinicNumber}", "");
						}
						if (locationCollection.getGoogleMapShortUrl() != null) {
							text = text.replace("{locationMapLink}",
									"Location - " + locationCollection.getGoogleMapShortUrl());
						} else {
							text = text.replace("{locationMapLink}", "");
						}
						sms.setSmsText(text);
						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(request.getMobileNumber());
						sms.setSmsAddress(smsAddress);

						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						smsServices.sendSMS(smsTrackDetail, true);
					}
				}
			}
			response = new DentalImagingResponse();
			BeanUtil.map(dentalImagingCollection, response);

			if (request.getIsPayAndSave().equals(Boolean.TRUE)) {
				DentalImagingInvoice dentalImagingInvoice = new DentalImagingInvoice();
				request.setId(null);
				BeanUtil.map(request, dentalImagingInvoice);
				dentalImagingInvoice.setId(request.getInvoiceId());
				dentalImagingInvoice.setDentalImagingId(String.valueOf(dentalImagingCollection.getId()));
				dentalImagingInvoice.setPatientName(request.getLocalPatientName());
				dentalImagingInvoice.setIsPaid(true);
				dentalImagingInvoice.setReferringDoctor(request.getReferringDoctor());
				addEditInvoice(dentalImagingInvoice, true);
			}

			if (request.getType() != null) {

				locationCollection = locationRepository.findById(new ObjectId(response.getLocationId())).orElse(null);

				if (locationCollection != null) {
					Location location = new Location();
					BeanUtil.map(locationCollection, location);
					response.setLocation(location);
				}

				if (request.getType().equalsIgnoreCase("DOCTOR")) {
					if (!DPDoctorUtils.anyStringEmpty(response.getPatientId(), response.getDoctorId(),
							response.getHospitalId(), response.getLocationId())) {
						PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								new ObjectId(response.getPatientId()), new ObjectId(response.getLocationId()),
								new ObjectId(response.getHospitalId()));
						if (patientCollection != null) {
							PatientShortCard patientShortCard = new PatientShortCard();
							BeanUtil.map(patientCollection, patientShortCard);
							response.setPatient(patientShortCard);

							UserCollection userCollection = userRepository
									.findById(new ObjectId(response.getPatientId())).orElse(null);
							if (userCollection != null) {
								if (response.getPatient() != null) {
									response.getPatient().setMobileNumber(userCollection.getMobileNumber());
								}
							}
						}
					}
				} else {
					if (!DPDoctorUtils.anyStringEmpty(response.getPatientId(), response.getLocationId(),
							response.getHospitalId())) {

						PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								new ObjectId(response.getPatientId()),
								new ObjectId(response.getDentalImagingLocationId()),
								new ObjectId(response.getDentalImagingHospitalId()));
						if (patientCollection != null) {
							PatientShortCard patientShortCard = new PatientShortCard();
							BeanUtil.map(patientCollection, patientShortCard);
							response.setPatient(patientShortCard);

							UserCollection userCollection = userRepository
									.findById(new ObjectId(response.getPatientId())).orElse(null);
							if (userCollection != null) {
								if (response.getPatient() != null) {
									response.getPatient().setMobileNumber(userCollection.getMobileNumber());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalImagingResponse> getRequests(String locationId, String hospitalId, String doctorId, Long from,
			Long to, String searchTerm, int size, long page, String type) {

		List<DentalImagingResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (type.equalsIgnoreCase("DOCTOR")) {

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("doctorId").is(new ObjectId(doctorId));
				}

				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria.and("locationId").is(new ObjectId(locationId));
				}

			} else {
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria.and("dentalImagingLocationId").is(new ObjectId(locationId));
				}

				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria.and("dentalImagingHospitalId").is(new ObjectId(hospitalId));
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patient.firstName").regex("^" + searchTerm, "i"),
						new Criteria("patient.firstName").regex("^" + searchTerm),
						new Criteria("patient.firstName").regex(searchTerm + ".*"),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex(searchTerm + ".*"),
						new Criteria("services.serviceName").regex("^" + searchTerm, "i"),
						new Criteria("services.serviceName").regex("^" + searchTerm),
						new Criteria("services.serviceName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm),
						new Criteria("mobileNumber").regex(searchTerm + ".*"),
						new Criteria("referringDoctor").regex("^" + searchTerm, "i"),
						new Criteria("referringDoctor").regex("^" + searchTerm),
						new Criteria("referringDoctor").regex(searchTerm + ".*"));

			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}

			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("dentalImagingDoctorId", new BasicDBObject("$first", "$dentalImagingDoctorId"))
							.append("dentalImagingHospitalId", new BasicDBObject("$first", "$dentalImagingHospitalId"))
							.append("dentalImagingLocationId", new BasicDBObject("$first", "$dentalImagingLocationId"))
							.append("services", new BasicDBObject("$push", "$services"))
							.append("referringDoctor", new BasicDBObject("$first", "$referringDoctor"))
							.append("clinicalNotes", new BasicDBObject("$first", "$clinicalNotes"))
							.append("reportsRequired", new BasicDBObject("$first", "$reportsRequired"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("patient", new BasicDBObject("$first", "$patient"))
							.append("location", new BasicDBObject("$first", "$location"))
							.append("reports", new BasicDBObject("$first", "$reports"))
							.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("specialInstructions", new BasicDBObject("$first", "$specialInstructions"))
							.append("doctor", new BasicDBObject("$first", "$doctor"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("totalCost", new BasicDBObject("$first", "$totalCost"))
							.append("totalDiscount", new BasicDBObject("$first", "$totalDiscount"))
							.append("totalTax", new BasicDBObject("$first", "$totalTax"))
							.append("grandTotal", new BasicDBObject("$first", "$grandTotal"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("isPaid", new BasicDBObject("$first", "$isPaid"))
							.append("invoiceId", new BasicDBObject("$first", "$invoiceId"))
							.append("isVisited", new BasicDBObject("$first", "$isVisited"))));

			/**/
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.unwind("services"),
						Aggregation.lookup("location_cl", "dentalImagingLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.unwind("services"),
						Aggregation.lookup("location_cl", "dentalImagingLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImagingResponse.class);
			response = aggregationResults.getMappedResults();
			for (DentalImagingResponse dentalImagingResponse : response) {

				if (dentalImagingResponse.getLocation() != null) {
					dentalImagingResponse.getLocation()
							.setLogoUrl(imagePath + dentalImagingResponse.getLocation().getLogoUrl());
					dentalImagingResponse.getLocation()
							.setLogoThumbnailUrl(imagePath + dentalImagingResponse.getLocation().getLogoThumbnailUrl());

					String address = (!DPDoctorUtils
							.anyStringEmpty(dentalImagingResponse.getLocation().getStreetAddress())
									? dentalImagingResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getLandmarkDetails())
									? dentalImagingResponse.getLocation().getLandmarkDetails() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getLocality())
									? dentalImagingResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getCity())
									? dentalImagingResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getState())
									? dentalImagingResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getCountry())
									? dentalImagingResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getLocation().getPostalCode())
									? dentalImagingResponse.getLocation().getPostalCode()
									: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					dentalImagingResponse.getLocation().setClinicAddress(address);
				}

				if (type.equalsIgnoreCase("DOCTOR")) {
					if (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getPatientId(),
							dentalImagingResponse.getDoctorId(), dentalImagingResponse.getHospitalId(),
							dentalImagingResponse.getLocationId())) {
						PatientCollection patientCollection = patientRepository
								.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
										new ObjectId(dentalImagingResponse.getPatientId()),
										new ObjectId(dentalImagingResponse.getDoctorId()),
										new ObjectId(dentalImagingResponse.getLocationId()),
										new ObjectId(dentalImagingResponse.getHospitalId()));
						if (patientCollection != null) {
							PatientShortCard patientShortCard = new PatientShortCard();
							BeanUtil.map(patientCollection, patientShortCard);
							patientShortCard.setBackendPatientId(patientCollection.getId().toString());
							dentalImagingResponse.setPatient(patientShortCard);

							UserCollection userCollection = userRepository
									.findById(new ObjectId(dentalImagingResponse.getPatientId())).orElse(null);
							if (userCollection != null) {
								if (dentalImagingResponse.getPatient() != null) {
									dentalImagingResponse.getPatient()
											.setMobileNumber(userCollection.getMobileNumber());
								}
							}
						}
					}
				} else {
					if (!DPDoctorUtils.anyStringEmpty(dentalImagingResponse.getPatientId(),
							dentalImagingResponse.getLocationId(), dentalImagingResponse.getHospitalId())) {

						PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								new ObjectId(dentalImagingResponse.getPatientId()),
								new ObjectId(dentalImagingResponse.getDentalImagingLocationId()),
								new ObjectId(dentalImagingResponse.getDentalImagingHospitalId()));
						if (patientCollection != null) {
							PatientShortCard patientShortCard = new PatientShortCard();
							BeanUtil.map(patientCollection, patientShortCard);
							dentalImagingResponse.setPatient(patientShortCard);

							UserCollection userCollection = userRepository
									.findById(new ObjectId(dentalImagingResponse.getPatientId())).orElse(null);
							if (userCollection != null) {
								if (dentalImagingResponse.getPatient() != null) {
									dentalImagingResponse.getPatient()
											.setMobileNumber(userCollection.getMobileNumber());
								}
							}
						}
					}
				}

				List<DentalImagingReportsCollection> dentalImagingReportsCollections = dentalImagingReportsRepository
						.findByRequestIdAndDiscarded(new ObjectId(dentalImagingResponse.getId()), false);
				if (dentalImagingReportsCollections != null) {
					List<DentalImagingReports> dentalImagingReports = new ArrayList<>();
					for (DentalImagingReportsCollection dentalImagingReportsCollection : dentalImagingReportsCollections) {
						DentalImagingReports reports = new DentalImagingReports();
						BeanUtil.map(dentalImagingReportsCollection, reports);
						if (reports.getReport() != null) {
							if (reports.getReport().getImageUrl() != null) {
								reports.getReport().setImageUrl(imagePath + reports.getReport().getImageUrl());
							}
							if (reports.getReport().getThumbnailUrl() != null) {
								reports.getReport().setThumbnailUrl(imagePath + reports.getReport().getThumbnailUrl());
							}
						}

						dentalImagingReports.add(reports);
					}
					dentalImagingResponse.setReports(dentalImagingReports);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalDiagnosticService> getServices(String searchTerm, String type, long page, int size) {
		List<DentalDiagnosticService> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("serviceName").regex("^" + searchTerm, "i"),
						new Criteria("serviceName").regex("^" + searchTerm),
						new Criteria("serviceName").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalDiagnosticService> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalDiagnosticServiceCollection.class, DentalDiagnosticService.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalImagingLocationServiceAssociation(
			List<DentalImagingLocationServiceAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DentalImagingLocationServiceAssociationCollection dentalImagingLocationServiceAssociationCollection = null;
		try {
			for (DentalImagingLocationServiceAssociation dentalImagingLocationServiceAssociation : request) {
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.findByDentalDiagnosticServiceIdAndLocationIdAndHospitalId(
								new ObjectId(dentalImagingLocationServiceAssociation.getDentalDiagnosticServiceId()),
								new ObjectId(dentalImagingLocationServiceAssociation.getLocationId()),
								new ObjectId(dentalImagingLocationServiceAssociation.getHospitalId()));
				if (dentalImagingLocationServiceAssociationCollection == null) {
					dentalImagingLocationServiceAssociationCollection = new DentalImagingLocationServiceAssociationCollection();
				} else {
					oldId = dentalImagingLocationServiceAssociationCollection.getId();
				}
				BeanUtil.map(dentalImagingLocationServiceAssociation,
						dentalImagingLocationServiceAssociationCollection);
				dentalImagingLocationServiceAssociationCollection.setId(oldId);
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.save(dentalImagingLocationServiceAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId,
			String hospitalId, String searchTerm, String type, long page, int size, Boolean discarded) {
		List<DentalImagingLocationServiceAssociationLookupResponse> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("service.type").is(type);
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("service.serviceName").regex("^" + searchTerm, "i"),
						new Criteria("service.serviceName").regex("^" + searchTerm),
						new Criteria("service.serviceName").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id",
								"service"),
						Aggregation.unwind("service"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id",
								"service"),
						Aggregation.unwind("service"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingLocationServiceAssociationLookupResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, DentalImagingLocationServiceAssociationCollection.class,
							DentalImagingLocationServiceAssociationLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<Hospital> getHospitalList(String doctorId, String hospitalId) {
		List<Hospital> hospitals = null;

		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);
			Criteria criteria = new Criteria("doctorId").is(userCollection.getId());
			criteria.and("location.hospitalId").is(new ObjectId(hospitalId));
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"),
							Aggregation.lookup("hospital_cl", "$location.hospitalId", "_id", "hospital"),
							Aggregation.unwind("hospital"), Aggregation.match(criteria)),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if (doctorClinicProfileLookupResponses == null || doctorClinicProfileLookupResponses.isEmpty()) {
				logger.warn("None of your clinic is active");
				// user.setUserState(UserState.NOTACTIVATED);
				throw new BusinessException(ServiceError.NotAuthorized, "None of your clinic is active");
			}
			if (doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				hospitals = new ArrayList<Hospital>();
				Map<String, Hospital> checkHospitalId = new HashMap<String, Hospital>();
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					LocationCollection locationCollection = doctorClinicProfileLookupResponse.getLocation();
					HospitalCollection hospitalCollection = doctorClinicProfileLookupResponse.getHospital();
					LocationAndAccessControl locationAndAccessControl = new LocationAndAccessControl();
					BeanUtil.map(locationCollection, locationAndAccessControl);
					locationAndAccessControl.setIsActivate(doctorClinicProfileLookupResponse.getIsActivate());
					locationAndAccessControl.setIsVerified(doctorClinicProfileLookupResponse.getIsVerified());
					locationAndAccessControl.setLogoUrl(getFinalImageURL(locationAndAccessControl.getLogoUrl()));
					locationAndAccessControl
							.setLogoThumbnailUrl(getFinalImageURL(locationAndAccessControl.getLogoThumbnailUrl()));
					locationAndAccessControl.setImages(getFinalClinicImages(locationAndAccessControl.getImages()));
					Hospital hospital = new Hospital();
					BeanUtil.map(hospitalCollection, hospital);

					hospital.setHospitalImageUrl(getFinalImageURL(hospital.getHospitalImageUrl()));
					hospital.getLocationsAndAccessControl().add(locationAndAccessControl);
					checkHospitalId.put(locationCollection.getHospitalId().toString(), hospital);
					hospitals.add(hospital);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Exception occured :: " + e);
		}
		return hospitals;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;

	}

	private List<ClinicImage> getFinalClinicImages(List<ClinicImage> clinicImages) {
		if (clinicImages != null && !clinicImages.isEmpty())
			for (ClinicImage clinicImage : clinicImages) {
				if (clinicImage.getImageUrl() != null) {
					clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
				}
				if (clinicImage.getThumbnailUrl() != null) {
					clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
				}
			}
		return clinicImages;
	}

	@Override
	@Transactional
	public List<DentalImagingLocationResponse> getServiceLocations(List<String> dentalImagingServiceId, String doctorId,
			String searchTerm, int size, long page) {

		List<DentalImagingLocationResponse> dentalImagingLocationResponses = null;
		List<ObjectId> serviceIds = new ArrayList<ObjectId>();
		List<DentalImagingLocationServiceAssociationCollection> dentalImagingLocationServiceAssociationCollections = null;
		List<ObjectId> hospitalObjectIds = new ArrayList<ObjectId>();

		try {
			List<DoctorHospitalDentalImagingAssociation> doctorHospitalDentalImagingAssociations = getDoctorHospitalAssociation(
					doctorId);

			for (DoctorHospitalDentalImagingAssociation doctorHospitalDentalImagingAssociation : doctorHospitalDentalImagingAssociations) {
				hospitalObjectIds.add(new ObjectId(doctorHospitalDentalImagingAssociation.getHospitalId()));
			}

			for (String id : dentalImagingServiceId) {
				serviceIds.add(new ObjectId(id));
			}
			dentalImagingLocationServiceAssociationCollections = dentalImagingLocationServiceAssociationRepository
					.findByHospitalIdIn(hospitalObjectIds);
			if (dentalImagingLocationServiceAssociationCollections == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			}
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.and("service.serviceName").regex(searchTerm, "i");
			}
			aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("hospitalId").in(hospitalObjectIds).and("discarded").is(false)),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id", "service"),
					Aggregation.unwind("service"), Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$locationId")
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("location", new BasicDBObject("$first", "$location"))
									.append("dentalDiagnosticServiceId",
											new BasicDBObject("$push", "$dentalDiagnosticServiceId")))),
					Aggregation.match(new Criteria("dentalDiagnosticServiceId").all(serviceIds)));
			AggregationResults<DentalImagingLocationResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingLocationServiceAssociationCollection.class, DentalImagingLocationResponse.class);
			dentalImagingLocationResponses = aggregationResults.getMappedResults();

			for (DentalImagingLocationResponse dentalImagingLocationResponse : dentalImagingLocationResponses) {
				if (dentalImagingLocationResponse.getLocation() != null) {
					dentalImagingLocationResponse.getLocation()
							.setLogoUrl(imagePath + dentalImagingLocationResponse.getLocation().getLogoUrl());
					dentalImagingLocationResponse.getLocation().setLogoThumbnailUrl(
							imagePath + dentalImagingLocationResponse.getLocation().getLogoThumbnailUrl());

					String address = (!DPDoctorUtils
							.anyStringEmpty(dentalImagingLocationResponse.getLocation().getStreetAddress())
									? dentalImagingLocationResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(dentalImagingLocationResponse.getLocation().getLandmarkDetails())
											? dentalImagingLocationResponse.getLocation().getLandmarkDetails() + ", "
											: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationResponse.getLocation().getLocality())
									? dentalImagingLocationResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationResponse.getLocation().getCity())
									? dentalImagingLocationResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationResponse.getLocation().getState())
									? dentalImagingLocationResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationResponse.getLocation().getCountry())
									? dentalImagingLocationResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(dentalImagingLocationResponse.getLocation().getPostalCode())
											? dentalImagingLocationResponse.getLocation().getPostalCode()
											: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					dentalImagingLocationResponse.getLocation().setClinicAddress(address);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dentalImagingLocationResponses;

	}

	@Override
	@Transactional
	public DentalImagingReports addDentalImagingReportBase64(FileDetails fileDetails,
			DentalImagingReportsAddRequest request) {
		DentalImagingReports response = null;
		DentalImagingReportsCollection dentalImagingReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getUploadedByDoctorId()))
					.orElse(null);
			if (fileDetails != null) {
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "dental-imaging-reports" + File.separator + request.getPatientId();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);

			}

			if (request.getRequestId() != null) {

				DentalImagingCollection dentalImagingCollection = dentalImagingRepository
						.findById(new ObjectId(request.getRequestId())).orElse(null);

				if (imageURLResponse != null) {
					pushNotificationServices.notifyUser(
							String.valueOf(dentalImagingCollection.getDentalImagingDoctorId()),
							"Report have been uploaded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), null, null);

					pushNotificationServices.notifyUser(String.valueOf(dentalImagingCollection.getDoctorId()),
							"Report have been uploaded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), null, null);

					pushNotificationServices.notifyUser(String.valueOf(dentalImagingCollection.getPatientId()),
							"Report have been uploaded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(), null, null);
				}
				dentalImagingCollection.setIsReportsUploaded(true);
				dentalImagingCollection.setIsVisited(true);
				dentalImagingRepository.save(dentalImagingCollection);

				List<DentalImagingReportsCollection> reportsCollections = dentalImagingReportsRepository
						.findByRequestIdAndDiscarded(new ObjectId(request.getRequestId()), false);
				if (reportsCollections == null || reportsCollections.isEmpty()) {
					UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
					LocationCollection locationCollection = locationRepository
							.findById(new ObjectId(request.getUploadedByLocationId())).orElse(null);
					String message = "Hi, {clinicName} has uploaded a report for {patientName} who was referred by you. Now your reports are also available on Healthcoco App. "
							+ DOCTOR_APP_LINK;
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
					smsTrackDetail.setDoctorId(dentalImagingCollection.getDoctorId());
					smsTrackDetail.setLocationId(dentalImagingCollection.getLocationId());
					smsTrackDetail.setHospitalId(dentalImagingCollection.getHospitalId());
					smsTrackDetail.setType("DENTAL_IMAGING_REQUEST");
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(userCollection.getId());
					SMS sms = new SMS();
					smsDetail.setUserName(userCollection.getFirstName());
					message = message.replace("{clinicName}", locationCollection.getLocationName());
					message = message.replace("{patientName}", dentalImagingCollection.getPatientName());
					sms.setSmsText(message);
					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(doctor.getMobileNumber());
					sms.setSmsAddress(smsAddress);

					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					smsServices.sendSMS(smsTrackDetail, true);
				}
			}

			if (dentalImagingReportsCollection == null) {
				dentalImagingReportsCollection = new DentalImagingReportsCollection();
			}

			BeanUtil.map(request, dentalImagingReportsCollection);
			dentalImagingReportsCollection.setReport(imageURLResponse);
			dentalImagingReportsCollection = dentalImagingReportsRepository.save(dentalImagingReportsCollection);

			response = new DentalImagingReports();
			BeanUtil.map(dentalImagingReportsCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DoctorHospitalDentalImagingAssociationCollection doctorHospitalDentalImagingAssociationCollection = null;
		try {
			for (DoctorHospitalDentalImagingAssociation doctorHospitalDentalImagingAssociation : request) {
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.findByDoctorIdAndHospitalId(new ObjectId(doctorHospitalDentalImagingAssociation.getDoctorId()),
								new ObjectId(doctorHospitalDentalImagingAssociation.getHospitalId()));

				if (doctorHospitalDentalImagingAssociationCollection == null) {
					doctorHospitalDentalImagingAssociationCollection = new DoctorHospitalDentalImagingAssociationCollection();
				} else {
					oldId = doctorHospitalDentalImagingAssociationCollection.getId();
				}
				BeanUtil.map(doctorHospitalDentalImagingAssociation, doctorHospitalDentalImagingAssociationCollection);
				doctorHospitalDentalImagingAssociationCollection.setId(oldId);
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.save(doctorHospitalDentalImagingAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DoctorHospitalDentalImagingAssociation> getDoctorHospitalAssociation(String doctorId) {
		List<DoctorHospitalDentalImagingAssociation> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociation> aggregationResults = mongoTemplate.aggregate(
					aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
					DoctorHospitalDentalImagingAssociation.class);

			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImaging discardRequest(String id, boolean discarded) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingCollection = dentalImagingRepository.findById(new ObjectId(id)).orElse(null);
			}

			if (dentalImagingCollection != null) {
				UserCollection userCollection = userRepository.findById(dentalImagingCollection.getDoctorId())
						.orElse(null);
				dentalImagingCollection.setDiscarded(discarded);
				if (dentalImagingCollection.getInvoiceId() != null) {
					DentalImagingInvoiceCollection dentalImagingInvoiceCollection = dentalImagingInvoiceRepository
							.findById(dentalImagingCollection.getInvoiceId()).orElse(null);
					if (dentalImagingInvoiceCollection != null) {
						dentalImagingInvoiceCollection.setDiscarded(discarded);
						dentalImagingInvoiceRepository.save(dentalImagingInvoiceCollection);
					}
				}
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
				pushNotificationServices.notifyUser(String.valueOf(userCollection.getId()),
						"Request has been discarded.", ComponentType.DENTAL_IMAGING_REQUEST.getType(),
						String.valueOf(dentalImagingCollection.getId()), null);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingReports discardReport(String id, boolean discarded) {
		DentalImagingReports response = null;
		DentalImagingReportsCollection dentalImagingReportsCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingReportsCollection = dentalImagingReportsRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (dentalImagingReportsCollection != null) {
				dentalImagingReportsCollection.setDiscarded(discarded);
				dentalImagingReportsCollection = dentalImagingReportsRepository.save(dentalImagingReportsCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImagingReports();
			BeanUtil.map(dentalImagingReportsCollection, response);

			if (dentalImagingReportsCollection.getRequestId() != null) {
				List<DentalImagingReportsCollection> reports = dentalImagingReportsRepository
						.findByRequestIdAndDiscarded(dentalImagingReportsCollection.getRequestId(), false);

				if (reports == null || reports.isEmpty()) {
					DentalImagingCollection dentalImagingCollection = dentalImagingRepository
							.findById(dentalImagingReportsCollection.getRequestId()).orElse(null);
					dentalImagingCollection.setIsReportsUploaded(false);
					dentalImagingRepository.save(dentalImagingCollection);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DoctorHospitalDentalImagingAssociationResponse> getHospitalAssociatedDoctor(String hospitalId,
			String searchTerm, int size, long page) {

		List<DoctorHospitalDentalImagingAssociationResponse> response = new ArrayList<>();
		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex(searchTerm + ".*"));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "doctorLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "doctorLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociationResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
							DoctorHospitalDentalImagingAssociationResponse.class);

			response = aggregationResults.getMappedResults();
			for (DoctorHospitalDentalImagingAssociationResponse doctorHospitalDentalImagingAssociationResponse : response) {
				if (!DPDoctorUtils.anyStringEmpty(doctorHospitalDentalImagingAssociationResponse.getDoctorId(),
						doctorHospitalDentalImagingAssociationResponse.getDoctorLocationId())) {
					DoctorClinicProfileCollection doctorClinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdAndLocationId(
									new ObjectId(doctorHospitalDentalImagingAssociationResponse.getDoctorId()),
									new ObjectId(doctorHospitalDentalImagingAssociationResponse.getDoctorLocationId()));
					if (doctorClinicProfileCollection != null) {
						LocationCollection locationCollection = locationRepository
								.findById(doctorClinicProfileCollection.getLocationId()).orElse(null);
						if (doctorHospitalDentalImagingAssociationResponse.getDoctor() != null) {
							doctorHospitalDentalImagingAssociationResponse.getDoctor()
									.setLocationId(String.valueOf(locationCollection.getId()));
							doctorHospitalDentalImagingAssociationResponse.getDoctor()
									.setHospitalId(String.valueOf(locationCollection.getHospitalId()));
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private ObjectId registerPatientIfNotRegistered(DentalImagingRequest request, ObjectId doctorId,
			ObjectId locationId, ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {

			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
			}
			PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
			patientRegistrationRequest.setFirstName(request.getLocalPatientName());
			patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
			patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
			patientRegistrationRequest.setDoctorId(request.getDoctorId());
			patientRegistrationRequest.setLocationId(request.getLocationId());
			patientRegistrationRequest.setHospitalId(request.getHospitalId());
			RegisteredPatientDetails patientDetails = null;
			patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
			if (patientDetails != null) {
				request.setPatientId(patientDetails.getUserId());
			}
			transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			patientId = new ObjectId(request.getPatientId());
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

			patientId = new ObjectId(request.getPatientId());
			PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientId, locationId,
					hospitalId);
			if (patient == null) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(doctorId))
					consultantDoctorIds.add(doctorId);
				patient.setConsultantDoctorIds(consultantDoctorIds);
				patient.setUpdatedTime(new Date());
				patientRepository.save(patient);
			}
		}

		return patientId;
	}

	@Override
	@Transactional
	public Boolean dentalLabDoctorRegistration(DentalImagingLabDoctorRegistrationRequest request) {
		Boolean response = false;
		LocationAndAccessControl locationAndAccessControl = null;

		try {
			DoctorSignupRequest doctorSignupRequest = new DoctorSignupRequest();
			BeanUtil.map(request, doctorSignupRequest);
			DoctorSignUp doctorSignUp = signUpService.doctorSignUp(doctorSignupRequest);
			if (doctorSignUp != null) {
				DoctorHospitalDentalImagingAssociationCollection doctorHospitalDentalImagingAssociationCollection = new DoctorHospitalDentalImagingAssociationCollection();
				doctorHospitalDentalImagingAssociationCollection
						.setHospitalId(new ObjectId(request.getDentalImagingHospitalId()));
				doctorHospitalDentalImagingAssociationCollection
						.setDoctorId(new ObjectId(doctorSignUp.getUser().getId()));
				if (doctorSignUp.getHospital() != null) {
					if (doctorSignUp.getHospital().getLocationsAndAccessControl().size() > 0) {
						locationAndAccessControl = doctorSignUp.getHospital().getLocationsAndAccessControl().get(0);
						doctorHospitalDentalImagingAssociationCollection
								.setDoctorLocationId(new ObjectId(locationAndAccessControl.getId()));

					}
				}
				doctorHospitalDentalImagingAssociationCollection = doctorHospitalDentalImagingAssociationRepository
						.save(doctorHospitalDentalImagingAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingInvoice addEditInvoice(DentalImagingInvoice request, Boolean fromRequest) {
		DentalImagingInvoice response = null;
		List<DentalImagingInvoiceItem> invoiceItems = null;
		// DentalWorksAmountCollection dentalWorksAmountCollection = null;
		try {
			DentalImagingInvoiceCollection dentalImagingInvoiceCollection = new DentalImagingInvoiceCollection();
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, dentalImagingInvoiceCollection);

				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getDentalImagingLocationId())).orElse(null);
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				dentalImagingInvoiceCollection
						.setUniqueInvoiceId(
								locationCollection.getInvoiceInitial()
										+ ((int) mongoTemplate.count(
												new Query(new Criteria("dentalImagingLocationId")
														.is(dentalImagingInvoiceCollection.getDentalImagingLocationId())
														.and("dentalImagingHospitalId")
														.is(dentalImagingInvoiceCollection
																.getDentalImagingHospitalId())),
												DentalImagingInvoiceCollection.class) + 1));

				dentalImagingInvoiceCollection.setBalanceAmount(request.getGrandTotal());
				if (dentalImagingInvoiceCollection.getInvoiceDate() == null)
					dentalImagingInvoiceCollection.setInvoiceDate(new Date());

				dentalImagingInvoiceCollection.setAdminCreatedTime(new Date());

			} else {
				dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				BeanUtil.map(request, dentalImagingInvoiceCollection);
				dentalImagingInvoiceCollection.setCreatedTime(new Date());
				dentalImagingInvoiceCollection.setUpdatedTime(new Date());
				dentalImagingInvoiceCollection.setTotalCost(request.getTotalCost());
				dentalImagingInvoiceCollection.setTotalDiscount(request.getTotalDiscount());
				dentalImagingInvoiceCollection.setTotalTax(request.getTotalTax());
				dentalImagingInvoiceCollection.setGrandTotal(request.getGrandTotal());
				dentalImagingInvoiceCollection
						.setDentalImagingDoctorId(new ObjectId(request.getDentalImagingDoctorId()));
			}

			DentalImagingCollection dentalImagingCollection = dentalImagingRepository
					.findById(new ObjectId(request.getDentalImagingId())).orElse(null);
			if (dentalImagingCollection != null && fromRequest == true) {
				invoiceItems = new ArrayList<DentalImagingInvoiceItem>();
				for (DentalDiagnosticServiceRequest serviceRequest : dentalImagingCollection.getServices()) {
					DentalImagingInvoiceItem dentalImagingInvoiceItem = new DentalImagingInvoiceItem();
					BeanUtil.map(serviceRequest, dentalImagingInvoiceItem);
					invoiceItems.add(dentalImagingInvoiceItem);
				}
				dentalImagingInvoiceCollection.setInvoiceItems(invoiceItems);
			} else {
				invoiceItems = new ArrayList<DentalImagingInvoiceItem>();
				for (DentalImagingInvoiceItemResponse invoiceItem : request.getInvoiceItems()) {
					DentalImagingInvoiceItem dentalImagingInvoiceItem = new DentalImagingInvoiceItem();
					BeanUtil.map(invoiceItem, dentalImagingInvoiceItem);
					invoiceItems.add(dentalImagingInvoiceItem);
				}
				dentalImagingInvoiceCollection.setInvoiceItems(invoiceItems);
			}

			dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.save(dentalImagingInvoiceCollection);
			dentalImagingCollection.setInvoiceId(dentalImagingInvoiceCollection.getId());
			dentalImagingCollection.setUniqueInvoiceId(dentalImagingInvoiceCollection.getUniqueInvoiceId());
			dentalImagingCollection.setTotalCost(dentalImagingInvoiceCollection.getTotalCost());
			dentalImagingCollection.setTotalDiscount(dentalImagingInvoiceCollection.getTotalDiscount());
			dentalImagingCollection.setTotalTax(dentalImagingInvoiceCollection.getTotalTax());
			dentalImagingCollection.setGrandTotal(dentalImagingInvoiceCollection.getGrandTotal());
			dentalImagingCollection.setIsPaid(dentalImagingInvoiceCollection.getIsPaid());
			dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);

			response = new DentalImagingInvoice();
			BeanUtil.map(dentalImagingInvoiceCollection, response);
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while adding invoice" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalImagingInvoiceResponse> getInvoices(String doctorId, String locationId, String hospitalId,
			String dentalImagingLocationId, String dentalImagingHospitalId, Long from, Long to, String searchTerm,
			int size, long page, Boolean isPaid) {

		List<DentalImagingInvoiceResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if (isPaid != null) {
				criteria.and("isPaid").is(isPaid);
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("dentalLab.locationName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("uniqueInvoiceId").regex("^" + searchTerm, "i"),
						new Criteria("uniqueInvoiceId").regex("^" + searchTerm),
						new Criteria("uniqueInvoiceId").regex(searchTerm + "$", "i"),
						new Criteria("uniqueInvoiceId").regex(searchTerm + "$"),
						new Criteria("uniqueInvoiceId").regex(searchTerm + ".*"));
			}

			/* (SEVEN) */
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalImagingLocationId", "_id", "dentalImagingLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalImagingLab")
										.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			else
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalImagingLocationId", "_id", "dentalImagingLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalImagingLab")
										.append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingInvoiceResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingInvoiceCollection.class, DentalImagingInvoiceResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Double getInvoiceAmount(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String dentalImagingLocationId, String dentalImagingHospitalId, long page, int size) {
		Double response = 0.0;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				localCalendar.setTime(new Date(Long.parseLong(fromDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").gt(start);
			}
			if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				localCalendar.setTime(new Date(Long.parseLong(toDate)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
				criteria.and("invoiceDate").lte(end);
			}

			CustomAggregationOperation customAggregationOperation = new CustomAggregationOperation(new Document(
					"$group",
					new BasicDBObject("_id", "$id").append("totalCost", new BasicDBObject("$sum", "$totalCost"))));

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), customAggregationOperation,
						Aggregation.skip(page * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), customAggregationOperation);
			}

			response = mongoTemplate.aggregate(aggregation, DentalImagingInvoiceCollection.class, Double.class)
					.getUniqueMappedResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingInvoice discardInvoice(String id, boolean discarded) {
		DentalImagingInvoice response = null;
		DentalImagingInvoiceCollection dentalImagingInvoiceCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.findById(new ObjectId(id)).orElse(null);
			}

			if (dentalImagingInvoiceCollection != null) {
				dentalImagingInvoiceCollection.setDiscarded(discarded);
				dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.save(dentalImagingInvoiceCollection);
				if (dentalImagingInvoiceCollection.getDentalImagingId() != null) {
					DentalImagingCollection dentalImagingCollection = dentalImagingRepository
							.findById(dentalImagingInvoiceCollection.getDentalImagingId()).orElse(null);
					if (dentalImagingCollection != null) {
						dentalImagingCollection.setInvoiceId(null);
						dentalImagingCollection.setUniqueInvoiceId(null);
						dentalImagingCollection.setIsPaid(false);
						dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
					}
				}
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImagingInvoice();
			BeanUtil.map(dentalImagingInvoiceCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingInvoice changeInvoicePaymentStatus(String id, boolean isPaid) {
		DentalImagingInvoice response = null;
		DentalImagingInvoiceCollection dentalImagingInvoiceCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.findById(new ObjectId(id)).orElse(null);
			}

			if (dentalImagingInvoiceCollection != null) {
				dentalImagingInvoiceCollection.setIsPaid(isPaid);
				dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.save(dentalImagingInvoiceCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImagingInvoice();
			BeanUtil.map(dentalImagingInvoiceCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingInvoiceResponse getInvoice(String invoiceId) {

		DentalImagingInvoiceResponse response = null;

		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("_id").is(new ObjectId(invoiceId));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("location_cl", "dentalImagingLocationId", "_id", "dentalImagingLab"),
					Aggregation.unwind("dentalImagingLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"));

			AggregationResults<DentalImagingInvoiceResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingInvoiceCollection.class, DentalImagingInvoiceResponse.class);
			response = aggregationResults.getUniqueMappedResult();

		} catch (Exception e) {
			logger.error("Error while getting invoice" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoice" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public String downloadInvoice(String invoiceId) {

		String response = null;
		JasperReportResponse jasperReportResponse = null;
		DentalImagingInvoiceResponse imagingInvoiceResponse = null;
		try {

			imagingInvoiceResponse = getInvoice(invoiceId);

			if (imagingInvoiceResponse == null) {
				throw new BusinessException(ServiceError.NoRecord, " No Dental Imaging Invoivce found with id");
			}

			jasperReportResponse = createDentalImagingInvoiceJasper(imagingInvoiceResponse);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Dental Work Invoice PDF for Parent");
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Dental Work Invoice PDF for Parent");
		}
		return response;

	}

	private JasperReportResponse createDentalImagingInvoiceJasper(DentalImagingInvoiceResponse imagingInvoiceResponse)
			throws NumberFormatException, IOException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		PrintSettingsCollection printSettings = null;
		Double grantTotal = 0.0;
		String leftDetail = "";
		String rightDetail = "";
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		String toothNumbers = "";
		List<DentalImagingInvoiceJasper> dentalImagingInvoiceJaspers = new ArrayList<DentalImagingInvoiceJasper>();
		DentalImagingInvoiceJasper dentalImagingInvoiceJasper = null;
		int i = 1;
		for (DentalImagingInvoiceItemResponse imagingItemResponse : imagingInvoiceResponse.getInvoiceItems()) {
			toothNumbers = "";
			dentalImagingInvoiceJasper = new DentalImagingInvoiceJasper();
			if (imagingItemResponse.getToothNumber() != null && !imagingItemResponse.getToothNumber().isEmpty()) {
				for (String dentalToothNumber : imagingItemResponse.getToothNumber()) {
					if (toothNumbers == "")
						toothNumbers = dentalToothNumber;
					else
						toothNumbers = toothNumbers + "," + dentalToothNumber;

				}
				dentalImagingInvoiceJasper.setToothNumber(toothNumbers);

			} else {
				dentalImagingInvoiceJasper.setToothNumber("--");
			}

			dentalImagingInvoiceJasper.setsNo(i++);
			if (!DPDoctorUtils.anyStringEmpty(imagingItemResponse.getServiceName()))
				dentalImagingInvoiceJasper.setServiceName(imagingItemResponse.getServiceName());
			else
				dentalImagingInvoiceJasper.setServiceName("--");
			if (!DPDoctorUtils.anyStringEmpty(imagingItemResponse.getCBCTArch(),
					imagingItemResponse.getCBCTQuadrant())) {
				dentalImagingInvoiceJasper.setQuadrant(
						imagingItemResponse.getCBCTQuadrant() + "," + imagingItemResponse.getCBCTArch() + "(Arch)");
			} else if (!DPDoctorUtils.anyStringEmpty(imagingItemResponse.getCBCTArch())) {
				dentalImagingInvoiceJasper.setQuadrant(imagingItemResponse.getCBCTArch() + "(Arch)");
			} else if (!DPDoctorUtils.anyStringEmpty(imagingItemResponse.getCBCTQuadrant())) {
				dentalImagingInvoiceJasper.setQuadrant(imagingItemResponse.getCBCTQuadrant());
			} else if (!DPDoctorUtils.anyStringEmpty(imagingItemResponse.getFov())) {
				dentalImagingInvoiceJasper.setQuadrant(imagingItemResponse.getFov() + "(FOV)");
			} else {
				dentalImagingInvoiceJasper.setQuadrant("--");
			}
			dentalImagingInvoiceJaspers.add(dentalImagingInvoiceJasper);
		}
		parameters.put("items", dentalImagingInvoiceJaspers);

		UserCollection dentalImagingDoctor = userRepository
				.findById(new ObjectId(imagingInvoiceResponse.getDentalImagingDoctorId())).orElse(null);
		UserCollection doctor = userRepository.findById(new ObjectId(imagingInvoiceResponse.getDoctorId()))
				.orElse(null);
		PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
				new ObjectId(imagingInvoiceResponse.getPatientId()),
				new ObjectId(imagingInvoiceResponse.getDentalImagingLocationId()),
				new ObjectId(imagingInvoiceResponse.getDentalImagingHospitalId()));

		if (imagingInvoiceResponse.getReferringDoctor() != null) {
			parameters.put("referredby", "Dr. " + imagingInvoiceResponse.getReferringDoctor());
		} else {
			parameters.put("referredby", "Dr. " + doctor.getFirstName());
		}
		parameters.put("title", "INVOICE");
		grantTotal = imagingInvoiceResponse.getGrandTotal();
		parameters.put("total", "Grand Total : " + grantTotal + " INR"
				+ (imagingInvoiceResponse.getIsPaid() ? " (PAID)" : " (UNPAID)"));
		parameters.put("leftDetail", leftDetail);
		parameters.put("rightDetail", rightDetail);
		parameters.put("signature",
				(!DPDoctorUtils.anyStringEmpty(dentalImagingDoctor.getTitle()) ? dentalImagingDoctor.getTitle() + " "
						: "") + dentalImagingDoctor.getFirstName());

		parameters.put("followUpAppointment", null);

		String pdfName = "DENTAL-IMAGE-INVOICE-" + imagingInvoiceResponse.getUniqueInvoiceId() + new Date().getTime();

		printSettings = printSettingsRepository.findByLocationIdAndHospitalId(
				(!DPDoctorUtils.anyStringEmpty(imagingInvoiceResponse.getDentalImagingLocationId())
						? new ObjectId(imagingInvoiceResponse.getDentalImagingLocationId())
						: null),
				(!DPDoctorUtils.anyStringEmpty(imagingInvoiceResponse.getDentalImagingDoctorId())
						? new ObjectId(imagingInvoiceResponse.getDentalImagingHospitalId())
						: null));

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);

		}
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patientCollection, "<b>INVOICE-ID: </b>" + imagingInvoiceResponse.getUniqueInvoiceId(),
				imagingInvoiceResponse.getPatientName(), imagingInvoiceResponse.getMobileNumber(), parameters,
				imagingInvoiceResponse.getCreatedTime() != null ? imagingInvoiceResponse.getCreatedTime() : new Date(),
				printSettings.getHospitalUId(), true);
		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(imagingInvoiceResponse.getDentalImagingDoctorId()));
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.DENTAL_IMAGE_INVOICE, parameters,
				dentalInvoiceA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@Override
	@Transactional
	public DentalImagingVisitAnalyticsResponse getVisitAnalytics(String fromDate, String toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId) {
		DentalImagingVisitAnalyticsResponse response = null;
		List<DentalImagingResponse> dentalImagingResponses = null;
		DentalImagingServiceVisitCount mostVisitedService = null;
		DentalImagingServiceVisitCount leastVisitedService = null;
		try {
			Integer patientCount = 0;
			Aggregation aggregation = null;
			Aggregation mostVisitAggregation = null;
			Aggregation leastVisitAggregation = null;
			Criteria criteria = new Criteria();
			Criteria visitCriteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
				visitCriteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
				visitCriteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}
			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(Long.parseLong(fromDate)))
						.lte(new Date(Long.parseLong(toDate)));
				visitCriteria.and("updatedTime").gte(new Date(Long.parseLong(fromDate)))
						.lte(new Date(Long.parseLong(toDate)));
			} else {
				criteria.and("updatedTime").gte(new Date(Long.parseLong(fromDate)));
				visitCriteria.and("updatedTime").gte(new Date(Long.parseLong(fromDate)));
			}
			visitCriteria.and("isVisited").is(true);
			criteria.and("discarded").is(false);
			visitCriteria.and("discarded").is(false);

			mostVisitAggregation = Aggregation.newAggregation(Aggregation.match(visitCriteria),
					Aggregation.unwind("services"), Aggregation.group("services.serviceName").count().as("count"),
					Aggregation.project("count").and("serviceName").previousOperation(),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "count")), Aggregation.limit(1));
			AggregationResults<DentalImagingServiceVisitCount> mostVisitAggregationResult = mongoTemplate.aggregate(
					mostVisitAggregation, DentalImagingCollection.class, DentalImagingServiceVisitCount.class);

			mostVisitedService = mostVisitAggregationResult.getUniqueMappedResult();

			leastVisitAggregation = Aggregation.newAggregation(Aggregation.match(visitCriteria),
					Aggregation.unwind("services"), Aggregation.group("services.serviceName").count().as("count"),
					Aggregation.project("count").and("serviceName").previousOperation(),
					Aggregation.sort(new Sort(Sort.Direction.ASC, "count")), Aggregation.limit(1));

			AggregationResults<DentalImagingServiceVisitCount> leastVisitAggregationResult = mongoTemplate.aggregate(
					leastVisitAggregation, DentalImagingCollection.class, DentalImagingServiceVisitCount.class);
			leastVisitedService = leastVisitAggregationResult.getUniqueMappedResult();
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			AggregationResults<DentalImagingResponse> aggregationResult = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImagingResponse.class);
			dentalImagingResponses = aggregationResult.getMappedResults();
			for (DentalImagingResponse dentalImagingResponse : dentalImagingResponses) {

				if (dentalImagingResponse.getIsVisited() == true) {
					patientCount++;
				}

			}

			response = new DentalImagingVisitAnalyticsResponse();
			if (dentalImagingResponses != null) {
				response.setTotalCount(dentalImagingResponses.size());
			}
			response.setPatientVisitCount(patientCount);
			response.setMostVisitedService(mostVisitedService);
			response.setLeastVisitedService(leastVisitedService);

		} catch (Exception e) {
			e.printStackTrace();

		}

		return response;
	}

	@Override
	@Transactional
	public List<AnalyticResponse> getPatientVisitAnalytics(Long fromDate, Long toDate, String dentalImagingLocationId,
			String dentalImagingHospitalId, String searchType) {

		List<AnalyticResponse> response = null;
		try {
			Aggregation aggregation = null;
			AggregationOperation aggregationOperation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
				criteria.and("patient.locationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
				criteria.and("patient.hospitalId").is(new ObjectId(dentalImagingHospitalId));
			}

			criteria.and("isVisited").is(true);

			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}
			criteria.and("discarded").is(false);

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					Fields.field("patient.id", "$patient.userId"),
					Fields.field("patient.localPatientName", "$patient.localPatientName"),
					Fields.field("patient.PID", "$patient.PID"),
					Fields.field("patient.registrationDate", "$patient.registrationDate"),
					Fields.field("patient.createdTime", "$createdTime"), Fields.field("createdTime", "$createdTime")));

			switch (SearchType.valueOf(searchType.toUpperCase())) {
			case DAILY: {
				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year"))
								.append("day", new BasicDBObject("$first", "$day"))
								.append("city", new BasicDBObject("$first", "$city"))
								.append("month", new BasicDBObject("$first", "$month"))
								.append("year", new BasicDBObject("$first", "$year"))
								.append("date", new BasicDBObject("$first", "$createdTime"))
								.append("patients", new BasicDBObject("$push", "$patient"))));
				break;
			}

			case WEEKLY: {

				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id",
								new BasicDBObject("week", "$week").append("month", "$month").append("year", "$year"))
								.append("day", new BasicDBObject("$first", "$day"))
								.append("city", new BasicDBObject("$first", "$city"))
								.append("month", new BasicDBObject("$first", "$month"))
								.append("year", new BasicDBObject("$first", "$year"))
								.append("date", new BasicDBObject("$first", "$createdTime"))
								.append("patients", new BasicDBObject("$push", "$patient"))));
				break;
			}

			case MONTHLY: {

				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("month", "$month").append("year", "$year"))
								.append("day", new BasicDBObject("$first", "$day"))
								.append("city", new BasicDBObject("$first", "$city"))
								.append("month", new BasicDBObject("$first", "$month"))
								.append("year", new BasicDBObject("$first", "$year"))
								.append("date", new BasicDBObject("$first", "$createdTime"))
								.append("patients", new BasicDBObject("$push", "$patient"))));

				break;
			}
			case YEARLY: {

				aggregationOperation = new CustomAggregationOperation(new Document("$group",
						new BasicDBObject("_id", new BasicDBObject("year", "$year"))
								.append("day", new BasicDBObject("$first", "$day"))
								.append("city", new BasicDBObject("$first", "$city"))
								.append("month", new BasicDBObject("$first", "$month"))
								.append("year", new BasicDBObject("$first", "$year"))
								.append("date", new BasicDBObject("$first", "$createdTime"))
								.append("patients", new BasicDBObject("$push", "$patient"))));

				break;

			}

			default:
				break;
			}
			aggregation = Aggregation.newAggregation(Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
					Aggregation.unwind("patient"), Aggregation.match(criteria),
					projectList.and("createdTime").extractDayOfMonth().as("day").and("createdTime").extractMonth()
							.as("month").and("createdTime").extractYear().as("year").and("createdTime").extractWeek()
							.as("week"),
					aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<AnalyticResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					"dental_imaging_cl", AnalyticResponse.class);
			response = aggregationResults.getMappedResults();

			for (AnalyticResponse analyticResponse : response) {
				analyticResponse.setCount(analyticResponse.getPatients().size());
				analyticResponse.setPatients(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalImagingReports> getReports(String doctorId, String locationId, String hospitalId,
			String dentalImagingLocationId, String dentalImagingHospitalId, String patientId, Long from, Long to,
			String searchTerm, int size, long page) {

		List<DentalImagingReports> dentalImagingReports = null;

		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(new Date(to));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip(page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			}

			AggregationResults<DentalImagingReports> aggregationResult = mongoTemplate.aggregate(aggregation,
					DentalImagingReportsCollection.class, DentalImagingReports.class);
			dentalImagingReports = aggregationResult.getMappedResults();

			for (DentalImagingReports report : dentalImagingReports) {
				report.getReport().setImageUrl(imagePath + report.getReport().getImageUrl());
				report.getReport().setThumbnailUrl(imagePath + report.getReport().getThumbnailUrl());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dentalImagingReports;

	}

	@Override
	@Transactional
	public List<PatientDentalImagignVisitAnalyticsResponse> getDoctorVisitAnalyticsCount(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType) {

		List<PatientDentalImagignVisitAnalyticsResponse> response = null;

		try {
			Aggregation aggregation = null;
			AggregationOperation aggregationOperation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}

			criteria.and("discarded").is(false);

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					Fields.field("dentalImaging.services", "$services"),
					Fields.field("dentalImaging.patientName", "$patientName"),
					Fields.field("doctorName", "$doctor.firstName"),
					Fields.field("dentalImaging.createdTime", "$createdTime"),
					Fields.field("dentalImaging.doctorId", "$doctorId"),
					Fields.field("dentalImaging.hospitalId", "$hospitalId"),
					Fields.field("dentalImaging.locationId", "$locationId"),
					Fields.field("dentalImaging.dentalImagingDoctorId", "$dentalImagingDoctorId"),
					Fields.field("dentalImaging.dentalImagingHospitalId", "$dentalImagingHospitalId"),
					Fields.field("dentalImaging.dentalImagingLocationId", "$dentalImagingLocationId"),
					Fields.field("dentalImaging.referringDoctor", "$referringDoctor"),
					Fields.field("dentalImaging.clinicalNotes", "$clinicalNotes"),
					Fields.field("dentalImaging.reportsRequired", "$reportsRequired"),
					Fields.field("dentalImaging.specialInstructions", "$specialInstructions"),
					Fields.field("dentalImaging.doctor", "$doctor"),
					Fields.field("dentalImaging.totalCost", "$totalCost"),
					Fields.field("dentalImaging.isPaid", "$isPaid"),
					Fields.field("dentalImaging.invoiceId", "$invoiceId"),
					Fields.field("dentalImaging.isVisited", "$isVisited")));

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$dentalImaging.doctorId")
							.append("doctorId", new BasicDBObject("$first", "$dentalImaging.doctorId"))
							.append("doctor", new BasicDBObject("$first", "$dentalImaging.doctor"))
							.append("responses", new BasicDBObject("$push", "$dentalImaging"))
							.append("doctorName", new BasicDBObject("$first", "$dentalImaging.doctor.firstName"))));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria), projectList, aggregationOperation,
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<PatientDentalImagignVisitAnalyticsResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, "dental_imaging_cl", PatientDentalImagignVisitAnalyticsResponse.class);
			response = aggregationResults.getMappedResults();

			for (PatientDentalImagignVisitAnalyticsResponse patientAnalyticResponse : response) {
				patientAnalyticResponse.setCount(patientAnalyticResponse.getResponses().size());
				List<DentalImagingResponse> paidDentalImagingResponses = new ArrayList<>();
				List<DentalImagingResponse> dentalImagingResponses = new ArrayList<>();
				for (DentalImagingResponse dentalImagingResponse : patientAnalyticResponse.getResponses()) {

					if (dentalImagingResponse.getIsVisited().equals(Boolean.TRUE)) {
						dentalImagingResponses.add(dentalImagingResponse);
					}
					if (dentalImagingResponse.getIsPaid().equals(Boolean.TRUE)) {
						paidDentalImagingResponses.add(dentalImagingResponse);
					}
				}

				/*
				 * patientAnalyticResponse.setVisitedResponses(dentalImagingResponses);
				 * patientAnalyticResponse.setPaidResponses(paidDentalImagingResponses);
				 */
				patientAnalyticResponse.setVisitedCount(dentalImagingResponses.size());
				patientAnalyticResponse.setPaidCount(paidDentalImagingResponses.size());
				patientAnalyticResponse.setResponses(null);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<PatientDentalImagignVisitAnalyticsResponse> getDoctorVisitAnalytics(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType) {

		List<PatientDentalImagignVisitAnalyticsResponse> response = null;
		try {
			Aggregation aggregation = null;
			AggregationOperation aggregationOperation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}

			criteria.and("discarded").is(false);
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					// Fields.field("dentalImaging.id", "$id"),
					Fields.field("dentalImaging.services", "$services"),
					Fields.field("dentalImaging.patientName", "$patientName"),
					Fields.field("doctorName", "$doctor.firstName"),
					Fields.field("dentalImaging.createdTime", "$createdTime"),
					Fields.field("dentalImaging.doctorId", "$doctorId"),
					Fields.field("dentalImaging.hospitalId", "$hospitalId"),
					Fields.field("dentalImaging.locationId", "$locationId"),
					Fields.field("dentalImaging.dentalImagingDoctorId", "$dentalImagingDoctorId"),
					Fields.field("dentalImaging.dentalImagingHospitalId", "$dentalImagingHospitalId"),
					Fields.field("dentalImaging.dentalImagingLocationId", "$dentalImagingLocationId"),
					Fields.field("dentalImaging.referringDoctor", "$referringDoctor"),
					Fields.field("dentalImaging.clinicalNotes", "$clinicalNotes"),
					Fields.field("dentalImaging.reportsRequired", "$reportsRequired"),
					Fields.field("dentalImaging.specialInstructions", "$specialInstructions"),
					Fields.field("dentalImaging.doctor", "$doctor"),
					Fields.field("dentalImaging.totalCost", "$totalCost"),
					Fields.field("dentalImaging.grandTotal", "$grandTotal"),
					Fields.field("dentalImaging.isPaid", "$isPaid"),
					Fields.field("dentalImaging.invoiceId", "$invoiceId"),
					Fields.field("dentalImaging.isVisited", "$isVisited")));

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$dentalImaging.doctorId")
							.append("doctorId", new BasicDBObject("$first", "$dentalImaging.doctorId"))
							.append("doctor", new BasicDBObject("$first", "$dentalImaging.doctor"))
							.append("responses", new BasicDBObject("$push", "$dentalImaging"))
							.append("doctorName", new BasicDBObject("$first", "$dentalImaging.doctor.firstName"))));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					projectList, aggregationOperation, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<PatientDentalImagignVisitAnalyticsResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, "dental_imaging_cl", PatientDentalImagignVisitAnalyticsResponse.class);
			response = aggregationResults.getMappedResults();

			for (PatientDentalImagignVisitAnalyticsResponse patientAnalyticResponse : response) {
				patientAnalyticResponse.setCount(patientAnalyticResponse.getResponses().size());
				List<DentalImagingResponse> paidDentalImagingResponses = new ArrayList<>();
				List<DentalImagingResponse> dentalImagingResponses = new ArrayList<>();
				Double totalAmount = 0.0;
				Double paidAmount = 0.0;
				for (DentalImagingResponse dentalImagingResponse : patientAnalyticResponse.getResponses()) {

					totalAmount = totalAmount + dentalImagingResponse.getGrandTotal();
					if (dentalImagingResponse.getIsVisited().equals(Boolean.TRUE)) {
						dentalImagingResponses.add(dentalImagingResponse);
					}
					if (dentalImagingResponse.getIsPaid().equals(Boolean.TRUE)) {
						paidAmount = paidAmount + dentalImagingResponse.getGrandTotal();
						paidDentalImagingResponses.add(dentalImagingResponse);
					}
				}

				patientAnalyticResponse.setVisitedResponses(dentalImagingResponses);
				patientAnalyticResponse.setPaidResponses(paidDentalImagingResponses);
				patientAnalyticResponse.setTotalAmount(totalAmount);
				patientAnalyticResponse.setPaidAmount(paidAmount);
				patientAnalyticResponse.setVisitedCount(dentalImagingResponses.size());
				patientAnalyticResponse.setPaidCount(paidDentalImagingResponses.size());
				patientAnalyticResponse.setResponses(null);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean emailInvoice(String invoiceId, String emailAddress) {
		MailResponse mailResponse = null;
		Boolean response = false;
		try {
			mailResponse = createMailData(invoiceId);
			String body = mailBodyGenerator.generateDentalImagingInvoiceEmailBody(mailResponse.getDoctorName(),
					mailResponse.getClinicName(), mailResponse.getPatientName(), mailResponse.getMailAttachments(),
					"dentalImagingInvoiceEmailTemplate.vm");
			response = mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Invoice", body,
					mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private MailResponse createMailData(String invoiceId) {
		MailResponse response = null;
		DentalImagingInvoiceCollection dentalImagingInvoiceCollection = null;
		DentalImagingInvoiceResponse dentalImagingInvoiceResponse = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			dentalImagingInvoiceCollection = dentalImagingInvoiceRepository.findById(new ObjectId(invoiceId))
					.orElse(null);

			if (dentalImagingInvoiceCollection != null) {

				user = userRepository.findById(dentalImagingInvoiceCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						dentalImagingInvoiceCollection.getPatientId(), dentalImagingInvoiceCollection.getLocationId(),
						dentalImagingInvoiceCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(dentalImagingInvoiceCollection.getDoctorId());
				emailTrackCollection.setHospitalId(dentalImagingInvoiceCollection.getHospitalId());
				emailTrackCollection.setLocationId(dentalImagingInvoiceCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.DENTAL_IMAGE_INVOICE.getType());
				emailTrackCollection.setSubject("Dental Imaging Invoice");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				dentalImagingInvoiceResponse = getInvoice(invoiceId);

				JasperReportResponse jasperReportResponse = createDentalImagingInvoiceJasper(
						dentalImagingInvoiceResponse);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(dentalImagingInvoiceCollection.getDoctorId())
						.orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(dentalImagingInvoiceCollection.getDentalImagingLocationId()).orElse(null);

				response = new MailResponse();
				response.setMailAttachment(mailAttachment);
				response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				response.setClinicAddress(address);
				response.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				response.setMailRecordCreatedDate(sdf.format(dentalImagingInvoiceCollection.getCreatedTime()));
				response.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean emailReports(String id, String emailAddress) {
		MailResponse mailResponse = null;
		Boolean response = false;
		try {
			mailResponse = createReportMailData(id);
			String body = mailBodyGenerator.generateDentalImagingInvoiceEmailBody(mailResponse.getDoctorName(),
					mailResponse.getClinicName(), mailResponse.getPatientName(), mailResponse.getMailAttachments(),
					"dentalImagingRecordEmailTemplate.vm");
			response = mailService.sendEmailMultiAttach(emailAddress, mailResponse.getClinicName()
					+ " sent you dental imaging reports for your patient " + mailResponse.getPatientName() + ".", body,
					mailResponse.getMailAttachments());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private MailResponse createReportMailData(String id) {
		MailResponse response = null;
		DentalImagingCollection dentalImagingCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		List<MailAttachment> mailAttachments = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			dentalImagingCollection = dentalImagingRepository.findById(new ObjectId(id)).orElse(null);

			if (dentalImagingCollection != null) {

				user = userRepository.findById(dentalImagingCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						dentalImagingCollection.getPatientId(), dentalImagingCollection.getLocationId(),
						dentalImagingCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(dentalImagingCollection.getDoctorId());
				emailTrackCollection.setHospitalId(dentalImagingCollection.getHospitalId());
				emailTrackCollection.setLocationId(dentalImagingCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.DENTAL_IMAGING_REQUEST.getType());
				emailTrackCollection.setSubject("Dental Imaging Report");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				List<DentalImagingReportsCollection> reports = dentalImagingReportsRepository
						.findByRequestIdAndDiscarded(new ObjectId(id), false);

				if (reports != null && !reports.isEmpty()) {
					mailAttachments = new ArrayList<>();
					for (DentalImagingReportsCollection dentalImagingReportsCollection : reports) {

						BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
						AmazonS3 s3client = new AmazonS3Client(credentials);
						S3Object object = s3client.getObject(new GetObjectRequest(bucketName,
								dentalImagingReportsCollection.getReport().getImageUrl()));
						InputStream objectData = object.getObjectContent();
						mailAttachment = new MailAttachment();
						mailAttachment.setFileSystemResource(null);
						mailAttachment.setInputStream(objectData);
						mailAttachment.setAttachmentName(
								FilenameUtils.getName(dentalImagingReportsCollection.getReport().getImageUrl()));
						mailAttachment.setUrl(dentalImagingReportsCollection.getReport().getImageUrl());
						mailAttachments.add(mailAttachment);
					}

				}

				UserCollection doctorUser = userRepository.findById(dentalImagingCollection.getDoctorId()).orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(dentalImagingCollection.getDentalImagingLocationId()).orElse(null);

				response = new MailResponse();
				response.setMailAttachments(mailAttachments);
				response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				response.setClinicAddress(address);
				response.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				response.setMailRecordCreatedDate(sdf.format(dentalImagingCollection.getCreatedTime()));
				response.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImaging changeVisitedStatus(String id, boolean isVisited) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				dentalImagingCollection = dentalImagingRepository.findById(new ObjectId(id)).orElse(null);
			}

			if (dentalImagingCollection != null) {
				dentalImagingCollection.setIsVisited(isVisited);
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public PatientDentalImagignVisitAnalyticsResponse getDetailedDoctorVisitAnalytics(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType,
			long page, int size) {

		PatientDentalImagignVisitAnalyticsResponse response = new PatientDentalImagignVisitAnalyticsResponse();
		List<DentalImagingResponse> dentalImagingResponses = null;

		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingLocationId)) {
				criteria.and("dentalImagingLocationId").is(new ObjectId(dentalImagingLocationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalImagingHospitalId)) {
				criteria.and("dentalImagingHospitalId").is(new ObjectId(dentalImagingHospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (toDate != null) {
				criteria.and("updatedTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else {
				criteria.and("updatedTime").gte(new Date(fromDate));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalImagingResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImagingResponse.class);

			dentalImagingResponses = aggregationResults.getMappedResults();

			Aggregation countAggregation = Aggregation.newAggregation(Aggregation.match(criteria));

			AggregationResults<DentalImagingResponse> countAggregationResults = mongoTemplate
					.aggregate(countAggregation, DentalImagingCollection.class, DentalImagingResponse.class);

			List<DentalImagingResponse> paidDentalImagingResponses = new ArrayList<>();
			List<DentalImagingResponse> visitedDentalImagingResponses = new ArrayList<>();
			for (DentalImagingResponse dentalImagingResponse : dentalImagingResponses) {

				if (dentalImagingResponse.getIsVisited().equals(Boolean.TRUE)) {
					visitedDentalImagingResponses.add(dentalImagingResponse);
				}
				if (dentalImagingResponse.getIsPaid().equals(Boolean.TRUE)) {
					paidDentalImagingResponses.add(dentalImagingResponse);
				}

			}
			response.setCount(countAggregationResults.getMappedResults().size());
			response.setVisitedResponses(visitedDentalImagingResponses);
			response.setPaidResponses(paidDentalImagingResponses);
			response.setVisitedCount(dentalImagingResponses.size());
			response.setPaidCount(paidDentalImagingResponses.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalImagingDataResponse getDentalImagingData() {
		DentalImagingDataResponse response = new DentalImagingDataResponse();
		List<CBDTQuadrant> cbdtQuadrants = null;
		List<CBDTArch> cbdtArchs = null;
		List<FOV> fovs = null;
		try {
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDTQuadrant> cbdtQuadrantAggregationResults = mongoTemplate.aggregate(aggregation,
					CBDTQuadrantCollection.class, CBDTQuadrant.class);
			cbdtQuadrants = cbdtQuadrantAggregationResults.getMappedResults();

			AggregationResults<CBDTArch> cbdtarchAggregationResults = mongoTemplate.aggregate(aggregation,
					CBDTArchCollection.class, CBDTArch.class);
			cbdtArchs = cbdtarchAggregationResults.getMappedResults();

			AggregationResults<FOV> fovAggregationResults = mongoTemplate.aggregate(aggregation, FOVCollection.class,
					FOV.class);
			fovs = fovAggregationResults.getMappedResults();

			response.setCbdtQuadrants(cbdtQuadrants);
			response.setCbdtArchs(cbdtArchs);
			response.setFovs(fovs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	@Override
	@Transactional
	public Integer countHospitalAssociatedDoctor(String hospitalId, String searchTerm) {

		Integer response = 0;
		// List<DoctorHospitalDentalImagingAssociationResponse>
		// doctorHospitalDentalImagingAssociationResponses = null;
		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			criteria.and("discarded").is(false);

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "doctorLocationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DoctorHospitalDentalImagingAssociationResponse> aggregationResults = mongoTemplate
					.aggregate(aggregation, DoctorHospitalDentalImagingAssociationCollection.class,
							DoctorHospitalDentalImagingAssociationResponse.class);

			response = aggregationResults.getMappedResults().size();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

}
