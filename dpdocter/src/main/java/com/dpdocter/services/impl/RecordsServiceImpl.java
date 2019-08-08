package com.dpdocter.services.impl;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.Count;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.UserAllowanceDetails;
import com.dpdocter.beans.UserRecords;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.collections.UserAllowanceDetailsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRecordsCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.RecordsState;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.RecordsTagsRepository;
import com.dpdocter.repository.TagsRepository;
import com.dpdocter.repository.UserAllowanceDetailsRepository;
import com.dpdocter.repository.UserRecordsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.RecordsSmsRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.RecordsLookupResponse;
import com.dpdocter.services.AdmitCardService;
import com.dpdocter.services.BillingService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RecordsService;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class RecordsServiceImpl implements RecordsService {

	private static Logger logger = Logger.getLogger(RecordsServiceImpl.class.getName());

	@Autowired
	private FileManager fileManager;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private TagsRepository tagsRepository;

	@Autowired
	private RecordsTagsRepository recordsTagsRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MailService mailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClinicalNotesService clinicalNotesService;

	@Autowired
	private PrescriptionServices prescriptionService;

	@Autowired
	private HistoryServices historyServices;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PatientVisitService patientVisitServices;

	@Autowired
	private OTPService otpService;

	@Autowired
	private PatientTreatmentServices patientTreatmentService;

	@Autowired
	private DischargeSummaryService dischargeSummaryService;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private BillingService BillingService;

	@Autowired
	private AdmitCardService admitCardService;

	@Autowired
	private UserRecordsRepository userRecordsRepository;

	@Autowired
	private UserAllowanceDetailsRepository userAllowanceDetailsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Value(value = "${mail.discard.record.to.lab.subject}")
	private String discardRecordMailToLabSubject;

	@Value(value = "${mail.approved.record.to.doctor.subject}")
	private String approvedRecordToDoctorSubject;

	@Value(value = "${mail.not.approved.record.to.doctor.subject}")
	private String notApprovedRecordToDoctorSubject;

	@Value(value = "${sms.add.record.to.patient}")
	private String addrecordSMSToPatient;

	@Value(value = "${sms.add.user.record.to.patient}")
	private String addUserRecordSMSToPatient;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Override
	@Transactional
	public Records addRecord(RecordsAddRequest request, String createdBy) {
		Records records = null;
		try {
			String localPatientName = null, patientMobileNumber = null;
			PrescriptionCollection prescriptionCollection = null;
			if (request.getPrescriptionId() != null) {
				prescriptionCollection = prescriptionRepository.findByUniqueEmrIdAndPatientId(request.getPrescriptionId(),
						new ObjectId(request.getPatientId()));
			}
			if (request.getRegisterPatient()) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setFirstName(request.getFirstName());
				patientRegistrationRequest.setLocalPatientName(request.getFirstName());
				patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				localPatientName = patientDetails.getLocalPatientName();
				patientMobileNumber = patientDetails.getMobileNumber();

				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<PatientCard> patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
										.and("locationId").is(new ObjectId(request.getLocationId())).and("hospitalId")
										.is(new ObjectId(request.getHospitalId()))),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty()) {
					localPatientName = patientCards.get(0).getLocalPatientName();
					patientMobileNumber = patientCards.get(0).getUser().getMobileNumber();
				}
			}
			Date createdTime = new Date();

			RecordsCollection recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())) {
				String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
				recordsCollection.setRecordsUrl(recordsURL);
				recordsCollection.setRecordsPath(recordsURL);
				if (DPDoctorUtils.anyStringEmpty(request.getRecordsLabel()))
					recordsCollection.setRecordsLabel(request.getFileDetails().getFileName());
			}
			if (request.getFileDetails() != null) {
				String recordLable = request.getFileDetails().getFileName();
				request.getFileDetails().setFileName(request.getFileDetails().getFileName() + createdTime.getTime());
				String path = "records" + File.separator + request.getPatientId();

				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(),
						path, false);
				String fileName = request.getFileDetails().getFileName() + "."
						+ request.getFileDetails().getFileExtension();
				String recordPath = path + File.separator + fileName;

				recordsCollection.setRecordsUrl(imageURLResponse.getImageUrl());
				recordsCollection.setRecordsPath(recordPath);
				if (DPDoctorUtils.anyStringEmpty(request.getRecordsLabel()))
					recordsCollection.setRecordsLabel(request.getFileDetails().getFileName());
			}
			recordsCollection.setCreatedTime(createdTime);
			recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());

			if (DPDoctorUtils.allStringsEmpty(createdBy)) {
				UserCollection userCollection = userRepository.findById(recordsCollection.getDoctorId()).orElse(null);
				if (userCollection != null) {
					createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName();
				}
			}

			recordsCollection.setCreatedBy(createdBy);

			LocationCollection locationCollection = locationRepository.findById(recordsCollection.getLocationId()).orElse(null);
			if (locationCollection != null) {
				recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
			}
			if (prescriptionCollection != null) {
				recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
				recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
				recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
			}

			recordsCollection = recordsRepository.save(recordsCollection);

			if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null
					|| !prescriptionCollection.getDiagnosticTests().isEmpty())) {
				List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
				for (TestAndRecordData data : prescriptionCollection.getDiagnosticTests()) {
					if (data.getTestId().toString().equals(recordsCollection.getDiagnosticTestId().toString())) {
						data.setRecordId(recordsCollection.getId());
					}
					tests.add(data);
				}
				prescriptionCollection.setDiagnosticTests(tests);
				prescriptionCollection.setUpdatedTime(new Date());
				prescriptionRepository.save(prescriptionCollection);
			}
			String body = null;
			if (prescriptionCollection != null && !DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState())) {
				if (recordsCollection.getRecordsState()
						.equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())) {
					UserCollection userCollection = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						String subject = approvedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", localPatientName)
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), localPatientName, recordsCollection.getRecordsLabel(),
								recordsCollection.getUniqueEmrId(), "approvedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				} else if (recordsCollection.getRecordsState()
						.equalsIgnoreCase(RecordsState.APPROVAL_REQUIRED.toString())) {
					UserCollection userCollection = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						String subject = notApprovedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", localPatientName)
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), localPatientName, recordsCollection.getRecordsLabel(),
								recordsCollection.getUniqueEmrId(), "notApprovedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState()) && recordsCollection
					.getRecordsState().equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())
					&& recordsCollection.getShareWithPatient()) {
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
				sendRecordSmsToPatient(localPatientName, patientMobileNumber, recordsCollection.getRecordsLabel(),
						recordsCollection.getUploadedByLocation(), recordsCollection.getDoctorId(),
						recordsCollection.getLocationId(), recordsCollection.getHospitalId(),
						recordsCollection.getPatientId());

			}
			records = new Records();
			BeanUtil.map(recordsCollection, records);

			pushNotificationServices.notifyUser(recordsCollection.getDoctorId().toString(),
					"Records Added",
					ComponentType.RECORDS_REFRESH.getType(), recordsCollection.getPatientId().toString(), null);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return records;
	}

	private void sendRecordSmsToPatient(String patientName, String patientMobileNumber, String recordName,
			String clinicName, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId) {

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(doctorId);
		smsTrackDetail.setHospitalId(hospitalId);
		smsTrackDetail.setLocationId(locationId);
		smsTrackDetail.setType(ComponentType.REPORTS.getType());
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(patientId);
		smsDetail.setUserName(patientName);
		SMS sms = new SMS();
		if (DPDoctorUtils.anyStringEmpty(recordName))
			recordName = "";
		String message = addrecordSMSToPatient;
		sms.setSmsText(message.replace("{patientName}", patientName).replace("{reportName}", recordName)
				.replace("{clinicName}", clinicName));

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(patientMobileNumber);
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		smsServices.sendSMS(smsTrackDetail, true);
	}

	@Override
	@Transactional
	public Records editRecord(RecordsEditRequest request) {

		Records records = new Records();
		try {
			RecordsCollection recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())) {
				String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
				recordsCollection.setRecordsUrl(recordsURL);
				recordsCollection.setRecordsPath(recordsURL);
				recordsCollection
						.setRecordsLabel(FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length() - 13));
			}
			if (request.getFileDetails() != null) {
				request.getFileDetails().setFileName(request.getFileDetails().getFileName() + new Date().getTime());
				String path = request.getPatientId() + File.separator + "records";
				// save image
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(),
						path, false);
				String fileName = request.getFileDetails().getFileName() + "."
						+ request.getFileDetails().getFileExtension();
				String recordPath = path + File.separator + fileName;

				recordsCollection.setRecordsUrl(imageURLResponse.getImageUrl());
				recordsCollection.setRecordsPath(recordPath);
			}
			RecordsCollection oldRecord = recordsRepository.findById(new ObjectId(request.getId())).orElse(null);
			recordsCollection.setCreatedTime(oldRecord.getCreatedTime());
			recordsCollection.setCreatedBy(oldRecord.getCreatedBy());
			recordsCollection.setUploadedByLocation(oldRecord.getUploadedByLocation());
			recordsCollection.setDiscarded(oldRecord.getDiscarded());
			recordsCollection.setInHistory(oldRecord.getInHistory());
			recordsCollection.setUniqueEmrId(oldRecord.getUniqueEmrId());
			recordsCollection.setPrescribedByDoctorId(oldRecord.getDoctorId());
			recordsCollection.setPrescribedByLocationId(oldRecord.getLocationId());
			recordsCollection.setPrescribedByHospitalId(oldRecord.getHospitalId());
			recordsCollection.setPrescriptionId(oldRecord.getPrescriptionId());
			recordsCollection.setRecordsState(oldRecord.getRecordsState());
			recordsCollection.setDiagnosticTestId(oldRecord.getDiagnosticTestId());
			recordsCollection.setIsPatientDiscarded(oldRecord.getIsPatientDiscarded());
			recordsCollection = recordsRepository.save(recordsCollection);

			// pushNotificationServices.notifyUser(recordsCollection.getPatientId(),
			// "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by
			// lab", ComponentType.REPORTS.getType(),
			// recordsCollection.getId());

			BeanUtil.map(recordsCollection, records);
			
			pushNotificationServices.notifyUser(recordsCollection.getDoctorId().toString(),
					"Records Added",
					ComponentType.RECORDS_REFRESH.getType(), recordsCollection.getPatientId().toString(), null);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return records;
	}

	@Override
	@Transactional
	public void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = createMailData(recordId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Report", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Report", body,
					mailResponse.getMailAttachment());
		} catch (MessagingException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public void tagRecord(TagRecordRequest request) {
		try {
			List<RecordsTagsCollection> recordsTagsCollections = new ArrayList<RecordsTagsCollection>();
			for (String tagId : request.getTags()) {
				RecordsTagsCollection recordsTagsCollection = new RecordsTagsCollection();
				recordsTagsCollection.setrecordsId(new ObjectId(request.getRecordId()));
				recordsTagsCollection.setTagsId(new ObjectId(tagId));
				recordsTagsCollection.setCreatedTime(new Date());
				recordsTagsCollections.add(recordsTagsCollection);
			}
			recordsTagsRepository.saveAll(recordsTagsCollections);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<Records> searchRecords(RecordsSearchRequest request) {
		List<Records> records = null;
		List<RecordsLookupResponse> recordsLookupResponses = null;
		try {
			boolean isOTPVerified = otpService.checkOTPVerified(request.getDoctorId(), request.getLocationId(),
					request.getHospitalId(), request.getPatientId());
			ObjectId tagObjectId = null, patientObjectId = null, doctorObjectId = null, locationObjectId = null,
					hospitalObjectId = null;

			if (request.getTagId() != null) {
				tagObjectId = new ObjectId(request.getTagId());

				List<RecordsTagsCollection> recordsTagsCollections = null;

				if (request.getSize() > 0)
					recordsTagsCollections = recordsTagsRepository.findByTagsId(tagObjectId,
							PageRequest.of((int)request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
				else
					recordsTagsCollections = recordsTagsRepository.findByTagsId(tagObjectId,
							new Sort(Sort.Direction.DESC, "createdTime"));

				Collection<ObjectId> recordIds = CollectionUtils.collect(recordsTagsCollections,
						new BeanToPropertyValueTransformer("recordsId"));
				Criteria criteria = new Criteria("id").in(recordIds).and("isPatientDiscarded").ne(true);
				Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
						Aggregation.unwind("patientVisit"));
				AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						RecordsCollection.class, RecordsLookupResponse.class);
				recordsLookupResponses = aggregationResults.getMappedResults();
				// recordsCollections =
				// IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());

			} else {

				long createdTimeStamp = Long.parseLong(request.getUpdatedTime());
				if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
					patientObjectId = new ObjectId(request.getPatientId());
				if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
					doctorObjectId = new ObjectId(request.getDoctorId());
				if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
					locationObjectId = new ObjectId(request.getLocationId());
				if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
					hospitalObjectId = new ObjectId(request.getHospitalId());

				Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimeStamp)).and("patientId")
						.is(patientObjectId).and("isPatientDiscarded").ne(true);
				if (!request.getDiscarded())
					criteria.and("discarded").is(request.getDiscarded());

				if (!isOTPVerified) {
					Criteria ownCriteria = new Criteria(), prescribedByCriteria = new Criteria();
					if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
						ownCriteria = new Criteria("locationId").is(locationObjectId).and("hospitalId")
								.is(hospitalObjectId);
						prescribedByCriteria = new Criteria("prescribedByLocationId").is(locationObjectId)
								.and("prescribedByHospitalId").is(hospitalObjectId);
					}
					if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
						ownCriteria = new Criteria("doctorId").is(doctorObjectId);
						prescribedByCriteria = new Criteria("prescribedByDoctorId").is(doctorObjectId);
					}
					criteria.orOperator(ownCriteria, prescribedByCriteria);
				}

				Aggregation aggregation = null;

				if (request.getSize() > 0)
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
							Aggregation.unwind("patientVisit"),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((long)request.getPage() * request.getSize()),
							Aggregation.limit(request.getSize()));
				else
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
							Aggregation.unwind("patientVisit"),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
						RecordsCollection.class, RecordsLookupResponse.class);
				recordsLookupResponses = aggregationResults.getMappedResults();

				records = new ArrayList<Records>();
				for (RecordsLookupResponse recordsLookupResponse : recordsLookupResponses) {
					Records record = new Records();
					BeanUtil.map(recordsLookupResponse, record);
					/*
					 * PatientVisitCollection patientVisitCollection = patientVisitRepository
					 * .findByRecordId(recordCollection.getId());
					 */
					if (recordsLookupResponse.getPatientVisit() != null)
						record.setVisitId(recordsLookupResponse.getPatientVisit().getId().toString());
					record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
					records.add(record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return records;
	}

	@Override
	@Transactional
	public Tags addEditTag(Tags tags) {
		try {
			TagsCollection tagsCollection = new TagsCollection();
			BeanUtil.map(tags, tagsCollection);
			tagsCollection = tagsRepository.save(tagsCollection);
			BeanUtil.map(tagsCollection, tags);
			return tags;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public List<Tags> getAllTags(String doctorId, String locationId, String hospitalId) {
		List<Tags> tags = null;
		try {
			ObjectId doctorObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			AggregationResults<Tags> aggregationResults = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria)), TagsCollection.class, Tags.class);
			tags = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return tags;
	}

	@Override
	@Transactional
	public String getPatientEmailAddress(String patientId) {
		String emailAddress = null;
		try {
			UserCollection userCollection = userRepository.findById(new ObjectId(patientId)).orElse(null);
			if (userCollection != null) {
				emailAddress = userCollection.getEmailAddress();
			} else {
				logger.warn("Invalid PatientId");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid PatientId");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return emailAddress;
	}

	@Override
	@Transactional
	public FileDownloadResponse getRecordFile(String recordId) {
		FileDownloadResponse response = null;
		try {
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (recordsCollection != null) {
				if (recordsCollection.getRecordsPath() != null) {
					BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
					AmazonS3 s3client = new AmazonS3Client(credentials);

					S3Object object = s3client
							.getObject(new GetObjectRequest(bucketName, recordsCollection.getRecordsUrl()));
					InputStream objectData = object.getObjectContent();
					if (objectData != null) {
						response = new FileDownloadResponse();
						response.setInputStream(objectData);
						response.setFileName(FilenameUtils.getName(recordsCollection.getRecordsUrl()));
					}
					return response;
				} else {
					logger.warn("Record Path for this Record is Empty.");
					throw new BusinessException(ServiceError.NoRecord, "Record Path for this Record is Empty.");
				}
			} else {
				logger.warn("Record not found.Please check recordId.");
				throw new BusinessException(ServiceError.InvalidInput, "Record not found.Please check recordId.");
			}

		} catch (BusinessException e) {
			e.printStackTrace();
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Records deleteRecord(String recordId, Boolean discarded) {
		Records response = null;
		try {
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (recordsCollection == null) {
				logger.warn("Record Not found.Check RecordId");
				throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
			}
			recordsCollection.setDiscarded(discarded);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);

			if (discarded)
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Report:" + recordsCollection.getUniqueEmrId() + " has been removed by "
								+ recordsCollection.getCreatedBy(),
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
			response = new Records();
			BeanUtil.map(recordsCollection, response);

		} catch (BusinessException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Tags deleteTag(String tagId, Boolean discarded) {
		Tags response = null;
		try {
			TagsCollection tagsCollection = tagsRepository.findById(new ObjectId(tagId)).orElse(null);
			if (tagsCollection == null) {
				logger.warn("Tag Not found.Check tag Id");
				throw new BusinessException(ServiceError.NoRecord, "Tag Not found.Check tag Id");
			}
			tagsCollection.setDiscarded(discarded);
			tagsCollection.setUpdatedTime(new Date());
			tagsRepository.save(tagsCollection);
			response = new Tags();
			BeanUtil.map(tagsCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<Records> getRecordsByIds(List<ObjectId> recordIds, ObjectId visitId) {
		List<Records> records = null;
		try {
			Criteria criteria = new Criteria("id").in(recordIds).and("isPatientDiscarded").ne(true);
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
					Aggregation.unwind("patientVisit"));
			AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RecordsCollection.class, RecordsLookupResponse.class);
			List<RecordsLookupResponse> recordsLookupResponses = aggregationResults.getMappedResults();
			if (recordsLookupResponses != null) {
				records = new ArrayList<Records>();
				for (RecordsLookupResponse recordsLookupResponse : recordsLookupResponses) {
					Records record = new Records();
					BeanUtil.map(recordsLookupResponse, record);
					record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));

					/*
					 * PatientVisitCollection patientVisitCollection = patientVisitRepository
					 * .findByRecordId(recordCollection.getId());
					 */
					if (recordsLookupResponse.getPatientVisit() != null) {
						record.setVisitId(recordsLookupResponse.getPatientVisit().getId().toString());
					}

					records.add(record);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return records;
	}

	@Override
	@Transactional
	public Integer getRecordCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		Integer recordCount = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId)
					.and("isPatientDiscarded").ne(true);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			recordCount = (int) mongoTemplate.count(new Query(criteria), RecordsCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Records Count");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Records Count");
		}
		return recordCount;
	}

	@Override
	@Transactional
	public FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts) {
		List<Count> counts = flexibleCounts.getCounts();
		try {

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(flexibleCounts.getPatientId()))
				patientObjectId = new ObjectId(flexibleCounts.getPatientId());
			if (!DPDoctorUtils.anyStringEmpty(flexibleCounts.getDoctorId()))
				doctorObjectId = new ObjectId(flexibleCounts.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(flexibleCounts.getLocationId()))
				locationObjectId = new ObjectId(flexibleCounts.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(flexibleCounts.getHospitalId()))
				hospitalObjectId = new ObjectId(flexibleCounts.getHospitalId());

			boolean isOTPVerified = otpService.checkOTPVerified(flexibleCounts.getDoctorId(),
					flexibleCounts.getLocationId(), flexibleCounts.getHospitalId(), flexibleCounts.getPatientId());
			for (Count count : counts) {
				switch (count.getCountFor()) {
				case PRESCRIPTIONS:
					count.setValue(prescriptionService.getPrescriptionCount(doctorObjectId, patientObjectId,
							locationObjectId, hospitalObjectId, isOTPVerified));
					break;
				case RECORDS:
					count.setValue(getRecordCount(doctorObjectId, patientObjectId, locationObjectId, hospitalObjectId,
							isOTPVerified));
					break;

				case NOTES:
					count.setValue(clinicalNotesService.getClinicalNotesCount(doctorObjectId, patientObjectId,
							locationObjectId, hospitalObjectId, isOTPVerified));
					break;

				case HISTORY:
					count.setValue(historyServices.getHistoryCount(doctorObjectId, patientObjectId, locationObjectId,
							hospitalObjectId, isOTPVerified));
					break;

				case PATIENTVISITS:
					count.setValue(patientVisitServices.getVisitCount(doctorObjectId, patientObjectId, locationObjectId,
							hospitalObjectId, isOTPVerified));
					break;

				case TREATMENTS:
					count.setValue(patientTreatmentService.getTreatmentsCount(doctorObjectId, patientObjectId,
							locationObjectId, hospitalObjectId, isOTPVerified));
					break;

				case EYE_PRESCRIPTION:
					count.setValue(prescriptionService.getEyePrescriptionCount(doctorObjectId, patientObjectId,
							locationObjectId, hospitalObjectId, isOTPVerified));
					break;
				case DISCHARGE_SUMMARY:
					count.setValue(dischargeSummaryService.getDischargeSummaryCount(doctorObjectId, patientObjectId,
							locationObjectId, hospitalObjectId, isOTPVerified));
					break;
				case INVOICE:
					count.setValue(BillingService.getInvoiceCount(doctorObjectId, patientObjectId, locationObjectId,
							hospitalObjectId, isOTPVerified));
					break;

				case RECEIPT:
					count.setValue(BillingService.getReceiptCount(doctorObjectId, patientObjectId, locationObjectId,
							hospitalObjectId, isOTPVerified));
					break;

				case ADMIT_CARD:
					count.setValue(admitCardService.getAdmitCardCount(doctorObjectId, patientObjectId, locationObjectId,
							hospitalObjectId, isOTPVerified));
					break;
				default:
					break;
				}
			}
			flexibleCounts.setCounts(counts);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting counts");
			throw new BusinessException(ServiceError.Unknown, "Error while getting counts");
		}

		return flexibleCounts;

	}

	@Override
	@Transactional
	public Records getRecordById(String recordId) {
		Records record = null;
		try {
			// RecordsCollection recordCollection =
			// recordsRepository.findById(new ObjectId(recordId));
			Criteria criteria = new Criteria("id").is(recordId).and("isPatientDiscarded").ne(true);
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
					Aggregation.unwind("patientVisit"));
			AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RecordsCollection.class, RecordsLookupResponse.class);
			RecordsLookupResponse recordsLookupResponses = aggregationResults.getUniqueMappedResult();
			if (recordsLookupResponses != null) {
				record = new Records();
				BeanUtil.map(recordsLookupResponses, record);
				/*
				 * PatientVisitCollection patientVisitCollection = patientVisitRepository
				 * .findByRecordId(recordCollection.getId());
				 */
				if (recordsLookupResponses.getPatientVisit() != null)
					record.setVisitId(recordsLookupResponses.getPatientVisit().getId().toString());
				record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting record : " + e.getCause().getMessage());
		}
		return record;
	}

	@Override
	@Transactional
	public MailResponse getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId) {
		return createMailData(recordId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String recordId, String doctorId, String locationId, String hospitalId) {
		MailResponse mailResponse = null;
		MailAttachment mailAttachment = null;
		try {
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);

			if (recordsCollection != null) {

				BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
				AmazonS3 s3client = new AmazonS3Client(credentials);

				S3Object object = s3client
						.getObject(new GetObjectRequest(bucketName, recordsCollection.getRecordsUrl()));
				InputStream objectData = object.getObjectContent();

				mailAttachment = new MailAttachment();
				mailAttachment.setFileSystemResource(null);
				mailAttachment.setInputStream(objectData);
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						recordsCollection.getPatientId(), new ObjectId(locationId), new ObjectId(hospitalId));
				if (patient != null) {

					mailAttachment.setAttachmentName(patient.getLocalPatientName() + new Date() + "REPORTS."
							+ FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
				} else {
					mailAttachment.setAttachmentName(
							new Date() + "REPORTS." + FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
				}

				UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
				LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);

				mailResponse = new MailResponse();
				mailResponse.setMailAttachment(mailAttachment);
				mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
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
				mailResponse.setClinicAddress(address);
				mailResponse.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				mailResponse.setMailRecordCreatedDate(sdf.format(recordsCollection.getCreatedTime()));
				mailResponse.setPatientName(patient.getLocalPatientName());

				EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
				emailTrackCollection.setDoctorId(recordsCollection.getDoctorId());
				emailTrackCollection.setHospitalId(recordsCollection.getHospitalId());
				emailTrackCollection.setLocationId(recordsCollection.getLocationId());
				emailTrackCollection.setPatientId(recordsCollection.getPatientId());
				emailTrackCollection.setPatientName(patient.getLocalPatientName());
				emailTrackCollection.setType(ComponentType.REPORTS.getType());
				emailTrackCollection.setSubject("Reports");

				emailTackService.saveEmailTrack(emailTrackCollection);
				// objectData.close();
			} else {
				logger.warn("Record not found.Please check recordId.");
				throw new BusinessException(ServiceError.NotFound, "Record not found.Please check recordId.");
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return mailResponse;
	}

	@Override
	@Transactional
	public void changeLabelAndDescription(String recordId, String label, String explanation) {
		try {
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (recordsCollection == null) {
				logger.warn("Record not found.Check RecordId !");
				throw new BusinessException(ServiceError.NoRecord, "Record not found.Check RecordId !");
			}
			recordsCollection.setRecordsLabel(label);
			recordsCollection.setExplanation(explanation);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	@Transactional
	public List<Records> getRecords(long page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory) {
		List<Records> records = null;
		List<RecordsLookupResponse> recordsLookupResponses = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;
		inHistorys[1] = true;
		try {
			if (discarded)
				discards.add(true);
			if (!inHistory)
				inHistorys[1] = false;
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimeStamp)).and("patientId").is(patientObjectId)
					.and("isPatientDiscarded").ne(true);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				Criteria ownCriteria = new Criteria(), prescribedByCriteria = new Criteria();
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId)) {
					ownCriteria = new Criteria("locationId").is(locationObjectId).and("hospitalId")
							.is(hospitalObjectId);
					prescribedByCriteria = new Criteria("prescribedByLocationId").is(locationObjectId)
							.and("prescribedByHospitalId").is(hospitalObjectId);
				}
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId)) {
					ownCriteria = new Criteria("doctorId").is(doctorObjectId);
					prescribedByCriteria = new Criteria("prescribedByDoctorId").is(doctorObjectId);
				}
				criteria.orOperator(ownCriteria, prescribedByCriteria);
			}

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RecordsCollection.class, RecordsLookupResponse.class);
			recordsLookupResponses = aggregationResults.getMappedResults();

			records = new ArrayList<Records>();
			for (RecordsLookupResponse recordsLookupResponse : recordsLookupResponses) {
				Records record = new Records();
				BeanUtil.map(recordsLookupResponse, record);
				record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
				/*
				 * PatientVisitCollection patientVisitCollection = patientVisitRepository
				 * .findByRecordId(recordCollection.getId());
				 */
				if (recordsLookupResponse.getPatientVisit() != null)
					record.setVisitId(recordsLookupResponse.getPatientVisit().getId().toString());
				records.add(record);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return records;
	}

	@Override
	@Transactional
	public Response<Object> getRecordsByPatientId(String patientId, int page, int size, String updatedTime,
			Boolean discarded, Boolean isDoctorApp, String sortBy) {
		Response<Object> response = new Response<Object>();
		List<Records> records = null;
		List<RecordsLookupResponse> recordsLookupResponses = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			long updatedTimeLong = Long.parseLong(updatedTime);
			if (discarded)
				discards.add(true);

			ObjectId patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);

			SortOperation sortOperation = Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime"));
			if(!DPDoctorUtils.anyStringEmpty(sortBy)) {
				if(sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}
			}
			if (isDoctorApp) {
				Criteria criteria = new Criteria("updatedTime").gt(new Date(updatedTimeLong))
						.and("isPatientDiscarded").ne(true);

				if (!DPDoctorUtils.anyStringEmpty(patientObjectId))
					criteria.and("patientId").is(patientObjectId);
				if (!discarded)
					criteria.and("discarded").is(discarded);

				long count = mongoTemplate.count(new Query(criteria), RecordsCollection.class);
				if (count > 0) {
					response.setData(count);
					response.setCount((int)count);
					Aggregation aggregation = null;

					if (size > 0)
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
								Aggregation.unwind("patientVisit"),
								sortOperation,
								Aggregation.skip((long)(page) * size), Aggregation.limit(size));
					else
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
								Aggregation.unwind("patientVisit"),
								sortOperation);

					AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
							RecordsCollection.class, RecordsLookupResponse.class);
					recordsLookupResponses = aggregationResults.getMappedResults();
				}
			} else {
				List<String> recordStates = new ArrayList<String>();
				recordStates.add(RecordsState.APPROVAL_NOT_REQUIRED.toString());
				recordStates.add(RecordsState.APPROVED_BY_DOCTOR.toString());

				Criteria criteria = new Criteria("updatedTime").gt(new Date(updatedTimeLong)).and("patientId")
						.is(patientObjectId).and("recordsState").in(recordStates).and("shareWithPatient").is(true);
				if (!discarded)
					criteria.and("discarded").is(discarded);

				long count = mongoTemplate.count(new Query(criteria), RecordsCollection.class);
				if (count > 0) {
					response.setData(count);
					response.setCount((int)count);
					Aggregation aggregation = null;

					if (size > 0)
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
								Aggregation.unwind("patientVisit"),
								sortOperation,
								Aggregation.skip((long)page * size), Aggregation.limit(size));
					else
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
								Aggregation.unwind("patientVisit"),
								sortOperation);

					AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
							RecordsCollection.class, RecordsLookupResponse.class);
					recordsLookupResponses = aggregationResults.getMappedResults();
				}
			}
			if (recordsLookupResponses != null && !recordsLookupResponses.isEmpty()) {
				records = new ArrayList<Records>();
				for (RecordsLookupResponse recordsLookupResponse : recordsLookupResponses) {
					Records record = new Records();
					BeanUtil.map(recordsLookupResponse, record);
					if (recordsLookupResponse.getPatientVisit() != null)
						record.setVisitId(recordsLookupResponse.getPatientVisit().getId().toString());
					record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
					records.add(record);
				}
				response.setDataList(records);
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
	public List<Records> getRecordsByDoctorId(String doctorId, long page, int size, String updatedTime,
			Boolean discarded) {
		List<Records> records = null;
		// List<RecordsCollection> recordsCollections = null;
		List<RecordsLookupResponse> recordsLookupResponses = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			long updatedTimeLong = Long.parseLong(updatedTime);
			if (discarded)
				discards.add(true);

			ObjectId doctorObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);

			/*
			 * if (size > 0) recordsCollections =
			 * recordsRepository.findRecordsByPatientId(patientObjectId, new
			 * Date(updatedTimeLong), discards, new PageRequest(page, size,
			 * Sort.Direction.DESC, "createdTime")); else recordsCollections =
			 * recordsRepository.findRecordsByPatientId(patientObjectId, new
			 * Date(updatedTimeLong), discards, new Sort(Sort.Direction.DESC,
			 * "createdTime"));
			 */

			Criteria criteria = new Criteria("updatedTime").gt(new Date(updatedTimeLong)).and("doctorId")
					.is(doctorObjectId);
			if (!discarded)
				criteria.and("discarded").is(discarded);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("patient_visit_cl", "_id", "recordId", "patientVisit"),
						Aggregation.unwind("patientVisit"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<RecordsLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					RecordsCollection.class, RecordsLookupResponse.class);
			recordsLookupResponses = aggregationResults.getMappedResults();

			records = new ArrayList<Records>();
			for (RecordsLookupResponse recordsLookupResponse : recordsLookupResponses) {
				Records record = new Records();
				BeanUtil.map(recordsLookupResponse, record);
				/*
				 * PatientVisitCollection patientVisitCollection = patientVisitRepository
				 * .findByRecordId(recordsLookupResponse.getId());
				 */
				if (recordsLookupResponse.getPatientVisit() != null)
					record.setVisitId(recordsLookupResponse.getPatientVisit().getId().toString());
				record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
				records.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return records;
	}

	@Override
	@Transactional
	public Records addRecordsMultipart(FormDataBodyPart file, RecordsAddRequestMultipart request) {
		try {
			String localPatientName = null, patientMobileNumber = null;
			PrescriptionCollection prescriptionCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPrescriptionId(), request.getPatientId())) {
				prescriptionCollection = prescriptionRepository.findByUniqueEmrIdAndPatientId(request.getPrescriptionId(),
						new ObjectId(request.getPatientId()));
			}
			if (request.getRegisterPatient()) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setFirstName(request.getFirstName());
				patientRegistrationRequest.setLocalPatientName(request.getFirstName());
				patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				localPatientName = patientDetails.getLocalPatientName();
				patientMobileNumber = patientDetails.getMobileNumber();

				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

				List<PatientCard> patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
										.and("locationId").is(new ObjectId(request.getLocationId())).and("hospitalId")
										.is(new ObjectId(request.getHospitalId()))),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty()) {
					localPatientName = patientCards.get(0).getLocalPatientName();
					patientMobileNumber = patientCards.get(0).getUser().getMobileNumber();
				}
			}

			Date createdTime = new Date();

			RecordsCollection recordsCollection = null, oldRecord = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				recordsCollection = recordsRepository.findById(new ObjectId(request.getId())).orElse(null);
				oldRecord = recordsCollection;
			}
			if (recordsCollection == null)
				recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())) {
				String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
				recordsCollection.setRecordsUrl(recordsURL);
				recordsCollection.setRecordsPath(recordsURL);
				recordsCollection
						.setRecordsLabel(FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length() - 13));
			}
			if (file != null) {
				if (!DPDoctorUtils.anyStringEmpty(file.getFormDataContentDisposition().getFileName())) {
					String path = "userRecords";
					FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
					String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
					String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
					String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
					String recordLabel = fileName;
					fileManager.saveRecord(file, recordPath, 0.0, false);
					recordsCollection.setRecordsUrl(recordPath);
					recordsCollection.setRecordsPath(recordPath);
					recordsCollection.setRecordsLabel(recordLabel);
				}
			}

			if (oldRecord != null) {
				recordsCollection.setCreatedTime(oldRecord.getCreatedTime());
				recordsCollection.setCreatedBy(oldRecord.getCreatedBy());
				recordsCollection.setUploadedByLocation(oldRecord.getUploadedByLocation());
				recordsCollection.setDiscarded(oldRecord.getDiscarded());
				recordsCollection.setInHistory(oldRecord.getInHistory());
				recordsCollection.setUniqueEmrId(oldRecord.getUniqueEmrId());
				recordsCollection.setPrescribedByDoctorId(oldRecord.getDoctorId());
				recordsCollection.setPrescribedByLocationId(oldRecord.getLocationId());
				recordsCollection.setPrescribedByHospitalId(oldRecord.getHospitalId());
				recordsCollection.setPrescriptionId(oldRecord.getPrescriptionId());
				recordsCollection.setDiagnosticTestId(oldRecord.getDiagnosticTestId());
				recordsCollection.setIsPatientDiscarded(oldRecord.getIsPatientDiscarded());
			} else {
				recordsCollection
						.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
				recordsCollection.setCreatedTime(createdTime);
				UserCollection userCollection = userRepository.findById(recordsCollection.getDoctorId()).orElse(null);
				if (userCollection != null) {
					recordsCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
				LocationCollection locationCollection = locationRepository.findById(recordsCollection.getLocationId()).orElse(null);
				if (locationCollection != null) {
					recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
				}

				if (prescriptionCollection != null) {
					recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
					recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
					recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
				}
				recordsCollection = recordsRepository.save(recordsCollection);

				if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null
						|| !prescriptionCollection.getDiagnosticTests().isEmpty())) {
					List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
					for (TestAndRecordData data : prescriptionCollection.getDiagnosticTests()) {
						if (data.getTestId().equals(recordsCollection.getDiagnosticTestId())) {
							data.setRecordId(recordsCollection.getId());
						}
						tests.add(data);
					}
					prescriptionCollection.setDiagnosticTests(tests);
					prescriptionCollection.setUpdatedTime(new Date());
					prescriptionRepository.save(prescriptionCollection);
				}

				String body = null;
				if (prescriptionCollection != null && !DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState())
						&& !DPDoctorUtils.anyStringEmpty(recordsCollection.getPatientId())) {
					if (recordsCollection.getRecordsState()
							.equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())) {
						String subject = approvedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", localPatientName)
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), localPatientName, recordsCollection.getRecordsLabel(),
								recordsCollection.getUniqueEmrId(), "approvedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					} else if (recordsCollection.getRecordsState()
							.equalsIgnoreCase(RecordsState.APPROVAL_REQUIRED.toString())) {
						String subject = notApprovedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", localPatientName)
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), localPatientName, recordsCollection.getRecordsLabel(),
								recordsCollection.getUniqueEmrId(), "notApprovedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState())
					&& recordsCollection.getRecordsState().equalsIgnoreCase(
							RecordsState.APPROVAL_NOT_REQUIRED.toString())
					&& recordsCollection.getShareWithPatient()
					&& !DPDoctorUtils.anyStringEmpty(recordsCollection.getPatientId())) {
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
				sendRecordSmsToPatient(localPatientName, patientMobileNumber, recordsCollection.getRecordsLabel(),
						recordsCollection.getUploadedByLocation(), recordsCollection.getDoctorId(),
						recordsCollection.getLocationId(), recordsCollection.getHospitalId(),
						recordsCollection.getPatientId());
				
				pushNotificationServices.notifyUser(recordsCollection.getDoctorId().toString(),
						"Records Added",
						ComponentType.RECORDS_REFRESH.getType(), recordsCollection.getPatientId().toString(), null);
			}

			Records records = new Records();
			BeanUtil.map(recordsCollection, records);
			
			return records;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public String saveRecordsImage(FormDataBodyPart file, String patientIdString) {
		String recordPath = null;
		try {

			Date createdTime = new Date();
			if (file != null) {
				String path = "records" + File.separator + patientIdString;
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");

				recordPath = path + File.separator + fileName + createdTime.getTime() + fileExtension;
				fileManager.saveRecord(file, recordPath, 0.0, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return recordPath;
	}

	@Override
	public Records changeRecordState(String recordId, String recordsState) {
		Records response = null;
		try {
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (recordsCollection == null) {
				logger.warn("Record Not found.Check RecordId");
				throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
			}
			recordsCollection.setRecordsState(recordsState);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);
			response = new Records();
			BeanUtil.map(recordsCollection, response);
			if (recordsState.equalsIgnoreCase(RecordsState.APPROVED_BY_DOCTOR.toString())
					&& recordsCollection.getShareWithPatient()) {
				UserCollection patientUserCollection = userRepository.findById(recordsCollection.getPatientId()).orElse(null);
				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						recordsCollection.getPatientId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId());
				sendRecordSmsToPatient(patientCollection.getLocalPatientName(), patientUserCollection.getMobileNumber(),
						recordsCollection.getRecordsLabel(), recordsCollection.getUploadedByLocation(),
						recordsCollection.getDoctorId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId(), recordsCollection.getPatientId());
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
			} else if (recordsState.equalsIgnoreCase(RecordsState.DECLINED_BY_DOCTOR.toString())) {
				UserCollection userCollection = userRepository.findById(recordsCollection.getDoctorId()).orElse(null);
				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						recordsCollection.getPatientId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId());

				PrescriptionCollection prescriptionCollection = prescriptionRepository.findByUniqueEmrIdAndPatientId(
						recordsCollection.getPrescriptionId(), recordsCollection.getPatientId());
				if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null
						|| !prescriptionCollection.getDiagnosticTests().isEmpty())) {
					List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
					for (TestAndRecordData data : prescriptionCollection.getDiagnosticTests()) {
						if (data.getTestId().equals(recordsCollection.getDiagnosticTestId())) {
							data.setRecordId(null);
						}
						tests.add(data);
					}
					prescriptionCollection.setDiagnosticTests(tests);
					prescriptionCollection.setUpdatedTime(new Date());
					prescriptionRepository.save(prescriptionCollection);
				}
				String body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
						recordsCollection.getUploadedByLocation(), null, recordsCollection.getRecordsLabel(),
						recordsCollection.getUniqueEmrId(), "discardedRecordToLabTemplate.vm");
				String subject = notApprovedRecordToDoctorSubject;
				mailService.sendEmail(userCollection.getEmailAddress(),
						subject.replace("{patientName}", patientCollection.getLocalPatientName())
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{drName}", prescriptionCollection.getCreatedBy())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation()),
						body, null);
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public UserRecords addUserRecords(UserRecords request) {
		UserRecords response = null;
		try {
			Date createdTime = new Date();
			UserCollection userCollection = null;

			UserRecordsCollection userRecordsCollection = null, oldRecord = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				oldRecord = userRecordsRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId(), request.getHospitalId(),
					request.getLocationId())) {
				userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Doctor Id");
				}
				request.setUploadedBy(RoleEnum.DOCTOR);
			}

			else {
				userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
				if (request.getRecordsFiles() != null && !request.getRecordsFiles().isEmpty()) {
					UserAllowanceDetailsCollection userAllowanceDetailsCollection = userAllowanceDetailsRepository
							.findByUserIds(new ObjectId(request.getPatientId()));

					if (userAllowanceDetailsCollection == null) {
						userAllowanceDetailsCollection = new UserAllowanceDetailsCollection();
						Aggregation aggregation = Aggregation.newAggregation(Aggregation
								.match(new Criteria("userName").regex("^" + userCollection.getMobileNumber(), "i")
										.and("userState").is("USERSTATECOMPLETE")));

						List<UserCollection> userCollections = mongoTemplate
								.aggregate(aggregation, UserCollection.class, UserCollection.class).getMappedResults();
						@SuppressWarnings("unchecked")
						Collection<ObjectId> userIds = CollectionUtils.collect(userCollections,
								new BeanToPropertyValueTransformer("id"));
						userAllowanceDetailsCollection.setUserIds(new ArrayList<>(userIds));
					}

					if (oldRecord != null) {
						for (RecordsFile file : oldRecord.getRecordsFiles()) {

							userAllowanceDetailsCollection.setAvailableRecordsSizeInMB(
									userAllowanceDetailsCollection.getAvailableRecordsSizeInMB()
											+ file.getFileSizeInMB());

						}
					}
					for (int index = 0; request.getRecordsFiles().size() > index; index++) {
						if (userAllowanceDetailsCollection.getAllowedRecordsSizeInMB()
								- userAllowanceDetailsCollection.getAvailableRecordsSizeInMB() >= 0) {
							userAllowanceDetailsCollection.setAvailableRecordsSizeInMB(
									userAllowanceDetailsCollection.getAvailableRecordsSizeInMB()
											- request.getRecordsFiles().get(index).getFileSizeInMB());
							request.getRecordsFiles().get(index).setRecordsUrl(
									request.getRecordsFiles().get(index).getRecordsUrl().replace(imagePath, ""));
							request.getRecordsFiles().get(index).setThumbnailUrl(
									request.getRecordsFiles().get(index).getThumbnailUrl().replace(imagePath, ""));

						} else {
							request.getRecordsFiles().remove(index);
						}

					}
					userAllowanceDetailsCollection = userAllowanceDetailsRepository
							.save(userAllowanceDetailsCollection);
					request.setUploadedBy(RoleEnum.PATIENT);
				}
			}

			userRecordsCollection = new UserRecordsCollection();
			BeanUtil.map(request, userRecordsCollection);

			if (oldRecord != null) {
				userRecordsCollection.setCreatedTime(oldRecord.getCreatedTime());
				userRecordsCollection.setCreatedBy(oldRecord.getCreatedBy());
				userRecordsCollection.setDiscarded(oldRecord.getDiscarded());
				userRecordsCollection.setUniqueEmrId(oldRecord.getUniqueEmrId());

			} else {
				userRecordsCollection
						.setUniqueEmrId(UniqueIdInitial.USERREPORTS.getInitial() + DPDoctorUtils.generateRandomId());
				userRecordsCollection.setCreatedTime(createdTime);
				if (userCollection != null) {
					userRecordsCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
				userRecordsCollection.setCreatedTime(new Date());
			}

			userRecordsCollection = userRecordsRepository.save(userRecordsCollection);

			if (!DPDoctorUtils.allStringsEmpty(userRecordsCollection.getDoctorId(),
					userRecordsCollection.getShareWith())) {
				pushNotificationServices.notifyUser(userRecordsCollection.getShareWith().toString(),
						"Dr." + userCollection.getFirstName() + "has shared record with you - Tap to view it!",
						ComponentType.USER_RECORD.getType(), userRecordsCollection.getId().toString(), null);
			}
			response = new UserRecords();
			BeanUtil.map(userRecordsCollection, response);

		} catch (

		BusinessException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "While add edit user record");

		}
		return response;
	}

	@Override
	public UserRecords getUserRecordById(String recordId) {
		UserRecords userRecords = null;
		try {
			UserRecordsCollection userRecordsCollection = userRecordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (userRecordsCollection != null) {
				userRecords = new UserRecords();
				BeanUtil.map(userRecordsCollection, userRecords);
				for (RecordsFile recordsFile : userRecords.getRecordsFiles()) {
					recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
					recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting record : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting record : " + e.getCause().getMessage());
		}
		return userRecords;

	}

	@Override
	public Response<Object> getUserRecordsByuserId(String patientId, String doctorId, String locationId,
			String hospitalId, long page, int size, String updatedTime, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		List<UserRecords> dataList = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId patientObjectId = null;
			ObjectId doctorObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				patientObjectId = new ObjectId(patientId);
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {

				patientObjectId = new ObjectId(patientId);
			}
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimeStamp));
			if (!DPDoctorUtils.anyStringEmpty(patientObjectId)) {
				criteria = criteria.orOperator(new Criteria("patientId").is(patientObjectId),
						new Criteria("shareWith").is(patientObjectId));
			}
			if (!DPDoctorUtils.allStringsEmpty(doctorId, hospitalId, locationId)) {
				doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId).and("locationId").is(new ObjectId(locationId))
						.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);

			long count = mongoTemplate.count(new Query(criteria), UserRecordsCollection.class);

			if (count > 0) {
				response.setData(count);
				Aggregation aggregation = null;

				if (size > 0)
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((page) * size), Aggregation.limit(size));
				else
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				AggregationResults<UserRecords> aggregationResults = mongoTemplate.aggregate(aggregation,
						UserRecordsCollection.class, UserRecords.class);
				dataList = aggregationResults.getMappedResults();
				for (UserRecords userRecords : dataList) {
					if (userRecords.getRecordsFiles() != null) {
						for (RecordsFile recordsFile : userRecords.getRecordsFiles()) {

							if (!DPDoctorUtils.anyStringEmpty(recordsFile.getRecordsUrl())) {
								recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
							}
							if (!DPDoctorUtils.anyStringEmpty(recordsFile.getThumbnailUrl())) {
								recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
							}
						}
					}

				}
				response.setDataList(dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public UserAllowanceDetails getUserRecordAllowance(String userId, String mobileNumber) {
		UserAllowanceDetails response = null;
		try {
			Aggregation aggregation = null;
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				response = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(
										Aggregation.match(new Criteria("userIds").is(new ObjectId(userId)))),
								UserAllowanceDetailsCollection.class, UserAllowanceDetails.class)
						.getUniqueMappedResult();
			} else {
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(
						Fields.field("id", "$userAllowance.id"), Fields.field("userIds", "$userAllowance.userIds"),
						Fields.field("allowedRecordsSizeInMB", "$userAllowance.allowedRecordsSizeInMB"),
						Fields.field("availableRecordsSizeInMB", "$userAllowance.availableRecordsSizeInMB"),
						Fields.field("createdTime", "$userAllowance.createdTime"),
						Fields.field("updatedTime", "$userAllowance.updatedTime")));
				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("mobileNumber")
								.is(mobileNumber)),
						new CustomAggregationOperation(new Document("$redact",
								new BasicDBObject("$cond",
										new BasicDBObject()
												.append("if",
														new BasicDBObject("$eq",
																Arrays.asList("$emailAddress", "$userName")))
												.append("then", "$$PRUNE").append("else", "$$KEEP")))),
						Aggregation.lookup("user_allowance_details_cl", "_id", "userIds", "userAllowance"),
						Aggregation.unwind("userAllowance"), projectList);
				List<UserAllowanceDetailsCollection> userAllowanceDetailsCollections = mongoTemplate
						.aggregate(aggregation, UserAllowanceDetailsCollection.class,
								UserAllowanceDetailsCollection.class)
						.getMappedResults();
				if (userAllowanceDetailsCollections != null && !userAllowanceDetailsCollections.isEmpty()) {
					response = new UserAllowanceDetails();
					BeanUtil.map(userAllowanceDetailsCollections.get(0), response);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public UserRecords deleteUserRecord(String recordId, Boolean discarded, Boolean isVisible) {
		UserRecords response = null;
		try {
			UserRecordsCollection userRecordsCollection = userRecordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (userRecordsCollection == null) {
				logger.warn("User Record Not found.Check Record Id");
				throw new BusinessException(ServiceError.NoRecord, "User Record Not found.Check Record Id");
			}
			if (discarded) {
				if (userRecordsCollection.getUploadedBy().getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {

					UserAllowanceDetailsCollection userAllowanceDetailsCollection = userAllowanceDetailsRepository
							.findByUserIds(userRecordsCollection.getPatientId());
					for (RecordsFile file : userRecordsCollection.getRecordsFiles()) {

						userAllowanceDetailsCollection.setAvailableRecordsSizeInMB(
								userAllowanceDetailsCollection.getAvailableRecordsSizeInMB() + file.getFileSizeInMB());

					}
					userAllowanceDetailsRepository.save(userAllowanceDetailsCollection);

				}

			}
			userRecordsCollection.setDiscarded(discarded);
			userRecordsCollection.setIsVisible(isVisible);
			userRecordsCollection.setUpdatedTime(new Date());
			userRecordsCollection = userRecordsRepository.save(userRecordsCollection);
			response = new UserRecords();
			BeanUtil.map(userRecordsCollection, response);
		} catch (BusinessException e) {
			logger.error(e);
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	public Boolean sendSMSForRecord(RecordsSmsRequest request) {
		Boolean status = false;
		UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
		if (userCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "User not found");
		}
		RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(request.getRecordId())).orElse(null);
		if (recordsCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record not found");
		}

		pushNotificationServices.notifyUser(request.getPatientId(), request.getMessage(),
				ComponentType.REPORTS.getType(), null, null);
		recordsCollection.getMessages().add(request.getMessage());
		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(request.getDoctorId()));
		// smsTrackDetail.setHospitalId(new ObjectId(re));
		// smsTrackDetail.setLocationId(new ObjectId(request.get));
		smsTrackDetail.setType("Reports");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(request.getPatientId()));
		SMS sms = new SMS();
		sms.setSmsText(request.getMessage());

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(userCollection.getMobileNumber());
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		status = smsServices.sendSMS(smsTrackDetail, true);

		return status;
	}

	@Override
	public UserRecords deleteUserRecordsFile(String recordId, List<String> fileIds) {
		UserRecords response = null;
		try {
			UserRecordsCollection userRecordsCollection = userRecordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (userRecordsCollection == null) {
				logger.warn("User Record Not found.Check RecordId");
				throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
			}
			if (userRecordsCollection.getUploadedBy().getRole().equalsIgnoreCase(RoleEnum.PATIENT.getRole())) {
				if (userRecordsCollection.getRecordsFiles() != null
						&& !userRecordsCollection.getRecordsFiles().isEmpty()) {

					UserAllowanceDetailsCollection userAllowanceDetailsCollection = userAllowanceDetailsRepository
							.findByUserIds(userRecordsCollection.getPatientId());
					for (int index = 0; userRecordsCollection.getRecordsFiles().size() > index; index++) {
						for (String fileId : fileIds) {
							if (userRecordsCollection.getRecordsFiles().get(index).getFileId().equals(fileId)) {
								userRecordsCollection.getRecordsFiles().remove(index);
								userAllowanceDetailsCollection.setAvailableRecordsSizeInMB(
										userAllowanceDetailsCollection.getAvailableRecordsSizeInMB()
												+ userRecordsCollection.getRecordsFiles().get(index).getFileSizeInMB());
							}

						}
					}
					userAllowanceDetailsCollection.setUpdatedTime(new Date());
					userAllowanceDetailsRepository.save(userAllowanceDetailsCollection);
				}
			} else {
				for (int index = 0; userRecordsCollection.getRecordsFiles().size() > index; index++) {
					for (String fileId : fileIds) {
						if (userRecordsCollection.getRecordsFiles().get(index).getFileId().equals(fileId)) {
							userRecordsCollection.getRecordsFiles().remove(index);

						}

					}
				}
			}
			userRecordsCollection.setUpdatedTime(new Date());
			userRecordsCollection = userRecordsRepository.save(userRecordsCollection);
			response = new UserRecords();
			BeanUtil.map(userRecordsCollection, response);
			for (RecordsFile recordsFile : response.getRecordsFiles()) {
				recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
				recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public UserRecords shareUserRecordsFile(String recordId, String userId) {
		UserRecords response = null;
		try {
			UserRecordsCollection userRecordsCollection = userRecordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (userRecordsCollection == null) {
				logger.warn("User Record Not found.Check RecordId");
				throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
			}
			userRecordsCollection.setUpdatedTime(new Date());
			userRecordsCollection.setShareWith(new ObjectId(userId));
			userRecordsCollection = userRecordsRepository.save(userRecordsCollection);
			response = new UserRecords();
			BeanUtil.map(userRecordsCollection, response);
			for (RecordsFile recordsFile : response.getRecordsFiles()) {
				recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
			}

			UserCollection doctor = userRepository.findById(userRecordsCollection.getDoctorId()).orElse(null);
			if (doctor != null) {
				PatientCollection patientCollection = patientRepository.findByUserIdAndDoctorIdAndLocationIdAndHospitalId(
						userRecordsCollection.getShareWith(), userRecordsCollection.getDoctorId(),
						userRecordsCollection.getLocationId(), userRecordsCollection.getHospitalId());
				UserCollection patient = userRepository.findById(userRecordsCollection.getPatientId()).orElse(null);

				pushNotificationServices.notifyUser(userRecordsCollection.getShareWith().toString(),
						"Dr." + doctor.getFirstName() + "has shared record with you - Tap to view it!",
						ComponentType.USER_RECORD.getType(), userRecordsCollection.getId().toString(), null);
				sendUserRecordSmsToPatient(patientCollection.getLocalPatientName(), patient.getMobileNumber(),
						userRecordsCollection.getRecordsLabel(), "Dr." + doctor.getFirstName(),
						userRecordsCollection.getDoctorId(), userRecordsCollection.getLocationId(),
						userRecordsCollection.getHospitalId(), userRecordsCollection.getShareWith());
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private void sendUserRecordSmsToPatient(String patientName, String patientMobileNumber, String recordName,
			String doctorName, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId) {

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(doctorId);
		smsTrackDetail.setHospitalId(hospitalId);
		smsTrackDetail.setLocationId(locationId);
		smsTrackDetail.setType(ComponentType.USER_RECORD.getType());
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(patientId);
		smsDetail.setUserName(patientName);
		SMS sms = new SMS();
		if (DPDoctorUtils.anyStringEmpty(recordName))
			recordName = "";
		String message = addUserRecordSMSToPatient;
		sms.setSmsText(message.replace("{patientName}", patientName).replace("{doctorName}", doctorName)
				.replace("{reportName}", recordName));
		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(patientMobileNumber);
		sms.setSmsAddress(smsAddress);
		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		smsServices.sendSMS(smsTrackDetail, true);
	}

	@Override
	public RecordsFile uploadUserRecord(FormDataBodyPart file, MyFiileRequest request) {
		RecordsFile recordsFile = null;
		try {
			UserAllowanceDetailsCollection userAllowanceDetailsCollection = null;
			Date createdTime = new Date();

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
				userAllowanceDetailsCollection = userAllowanceDetailsRepository
						.findByUserIds(new ObjectId(request.getPatientId()));

				if (userAllowanceDetailsCollection == null) {
					userAllowanceDetailsCollection = new UserAllowanceDetailsCollection();
					Aggregation aggregation = Aggregation.newAggregation(Aggregation
							.match(new Criteria("userName").regex("^" + userCollection.getMobileNumber(), "i")
									.and("userState").is("USERSTATECOMPLETE")));

					List<UserCollection> userCollections = mongoTemplate
							.aggregate(aggregation, UserCollection.class, UserCollection.class).getMappedResults();
					@SuppressWarnings("unchecked")
					Collection<ObjectId> userIds = CollectionUtils.collect(userCollections,
							new BeanToPropertyValueTransformer("id"));
					userAllowanceDetailsCollection.setUserIds(new ArrayList<>(userIds));
				}

				if (userAllowanceDetailsCollection.getAvailableRecordsSizeInMB() <= 0) {
					throw new BusinessException(ServiceError.Unknown, "No Space left");
				}
			}
			if (file != null) {
				String path = "userRecords" + File.separator + request.getPatientId();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				String recordfileLabel = fileName;
				Double fileSizeInMB = 0.0;
				if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
					fileSizeInMB = fileManager.saveRecord(file, recordPath,
							userAllowanceDetailsCollection.getAvailableRecordsSizeInMB(), true);

					userAllowanceDetailsRepository.save(userAllowanceDetailsCollection);
				} else {
					fileSizeInMB = fileManager.saveRecord(file, recordPath, fileSizeInMB, false);
				}

				recordsFile = new RecordsFile();
				recordsFile.setFileId("file" + DPDoctorUtils.generateRandomId());
				recordsFile.setFileSizeInMB(fileSizeInMB);
				recordsFile.setRecordsUrl(recordPath);
				recordsFile.setThumbnailUrl(fileManager.saveThumbnailUrl(file, recordPath));
				recordsFile.setRecordsFileLabel(recordfileLabel);
				recordsFile.setRecordsPath(path);
				recordsFile.setRecordsType(request.getRecordsType());

			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading my file record");

		}
		return recordsFile;

	}

	@Override
	public Boolean updateShareWithPatient(String recordId) {
		Boolean response = false;
		try {
			String localPatientName = null;
			String patientMobileNumber = null;
			RecordsCollection recordsCollection = recordsRepository.findById(new ObjectId(recordId)).orElse(null);
			if (recordsCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No record found with recordId");
			}
			if (recordsCollection.getShareWithPatient() == null) {
				{
					recordsCollection.setShareWithPatient(true);
				}
			} else {
				recordsCollection.setShareWithPatient(!recordsCollection.getShareWithPatient());
			}
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);

			List<PatientCard> patientCards = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("userId").is(recordsCollection.getPatientId())
									.and("locationId").is(recordsCollection.getLocationId()).and("hospitalId")
									.is(recordsCollection.getHospitalId())),
							Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
					PatientCollection.class, PatientCard.class).getMappedResults();
			if (patientCards != null && !patientCards.isEmpty()) {
				localPatientName = patientCards.get(0).getLocalPatientName();
				patientMobileNumber = patientCards.get(0).getUser().getMobileNumber();
			}

			if (!DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState()) && recordsCollection
					.getRecordsState().equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())
					&& recordsCollection.getShareWithPatient()) {
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString(), null);
				sendRecordSmsToPatient(localPatientName, patientMobileNumber, recordsCollection.getRecordsLabel(),
						recordsCollection.getUploadedByLocation(), recordsCollection.getDoctorId(),
						recordsCollection.getLocationId(), recordsCollection.getHospitalId(),
						recordsCollection.getPatientId());
			}
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while share record with patient");
		}
		return response;
	}

	@Override
	public Integer getUserRecordsByuserIdCount(String patientId, String doctorId, String locationId, String hospitalId,
			long page, int size, String updatedTime, Boolean discarded) {
		return null;
	}

}