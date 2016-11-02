package com.dpdocter.services.impl;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dpdocter.beans.Count;
import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.RecordsState;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.RecordsTagsRepository;
import com.dpdocter.repository.TagsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RecordsService;
import com.dpdocter.services.SMSServices;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

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
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;
	@Autowired
	private PatientRepository patientRepository;

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
	private String addecordSMSToPatient;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private SMSServices smsServices;

	@Override
	@Transactional
	public Records addRecord(RecordsAddRequest request) {
		try {

			Date createdTime = new Date();
			UserCollection patientUserCollection = userRepository.findOne(new ObjectId(request.getPatientId()));
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getDoctorId()),
					new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
			RecordsCollection recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())) {
				String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
				recordsCollection.setRecordsUrl(recordsURL);
				recordsCollection.setRecordsPath(recordsURL);
				if (DPDoctorUtils.anyStringEmpty(request.getRecordsLabel()))
					recordsCollection.setRecordsLabel(
							FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length() - 13));
			}
			if (request.getFileDetails() != null) {
				String recordLabel = request.getFileDetails().getFileName();
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
					recordsCollection.setRecordsLabel(recordLabel);
			}
			recordsCollection.setCreatedTime(createdTime);
			recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
			UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
			if (userCollection != null) {
				recordsCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
			}
			LocationCollection locationCollection = locationRepository.findOne(recordsCollection.getLocationId());
			if (locationCollection != null) {
				recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
			}
			PrescriptionCollection prescriptionCollection = null;
			if (recordsCollection.getPrescriptionId() != null) {
				prescriptionCollection = prescriptionRepository.findByUniqueIdAndPatientId(
						recordsCollection.getPrescriptionId(), recordsCollection.getPatientId());
				if (prescriptionCollection != null) {
					recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
					recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
					recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
				}
			}

			recordsCollection = recordsRepository.save(recordsCollection);

			if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null
					|| !prescriptionCollection.getDiagnosticTests().isEmpty())) {
				List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
				for (TestAndRecordData data : prescriptionCollection.getDiagnosticTests()) {
					if (data.getTestId().equals(recordsCollection.getDiagnosticTestId().toString())) {
						data.setRecordId(recordsCollection.getId().toString());
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
					userCollection = userRepository.findOne(prescriptionCollection.getDoctorId());
					if (userCollection != null) {
						String subject = approvedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", patientCollection.getLocalPatientName())
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), patientCollection.getLocalPatientName(),
								recordsCollection.getRecordsLabel(), recordsCollection.getUniqueEmrId(),
								"approvedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				} else if (recordsCollection.getRecordsState()
						.equalsIgnoreCase(RecordsState.APPROVAL_REQUIRED.toString())) {
					userCollection = userRepository.findOne(prescriptionCollection.getDoctorId());
					if (userCollection != null) {
						String subject = notApprovedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", patientCollection.getLocalPatientName())
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), patientCollection.getLocalPatientName(),
								recordsCollection.getRecordsLabel(), recordsCollection.getUniqueEmrId(),
								"notApprovedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState()) && recordsCollection
					.getRecordsState().equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())) {
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
				sendRecordSmsToPatient(patientCollection.getLocalPatientName(), patientUserCollection.getMobileNumber(),
						recordsCollection.getRecordsLabel(), recordsCollection.getUploadedByLocation(),
						recordsCollection.getDoctorId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId(), recordsCollection.getPatientId());
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
		String message = addecordSMSToPatient;
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
			RecordsCollection oldRecord = recordsRepository.findOne(new ObjectId(request.getId()));
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

			recordsCollection = recordsRepository.save(recordsCollection);

			// pushNotificationServices.notifyUser(recordsCollection.getPatientId(),
			// "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by
			// lab", ComponentType.REPORTS.getType(),
			// recordsCollection.getId());

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
			recordsTagsRepository.save(recordsTagsCollections);
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
		List<RecordsCollection> recordsCollections = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			boolean isOTPVerified = otpService.checkOTPVerified(request.getDoctorId(), request.getLocationId(),
					request.getHospitalId(), request.getPatientId());
			if (request.getDiscarded())
				discards[1] = true;
			long createdTimeStamp = Long.parseLong(request.getUpdatedTime());
			ObjectId tagObjectId = null, patientObjectId = null, doctorObjectId = null, locationObjectId = null,
					hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getTagId()))
				tagObjectId = new ObjectId(request.getTagId());
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId()))
				patientObjectId = new ObjectId(request.getPatientId());
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());

			if (request.getTagId() != null) {
				List<RecordsTagsCollection> recordsTagsCollections = null;

				if (request.getSize() > 0)
					recordsTagsCollections = recordsTagsRepository.findByTagsId(tagObjectId,
							new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
				else
					recordsTagsCollections = recordsTagsRepository.findByTagsId(tagObjectId,
							new Sort(Sort.Direction.DESC, "createdTime"));

				Collection<ObjectId> recordIds = CollectionUtils.collect(recordsTagsCollections,
						new BeanToPropertyValueTransformer("recordsId"));
				recordsCollections = IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());

			} else {

				if (isOTPVerified) {
					if (request.getSize() > 0) {
						recordsCollections = recordsRepository.findRecords(patientObjectId, new Date(createdTimeStamp),
								discards,
								new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
					} else {
						recordsCollections = recordsRepository.findRecords(patientObjectId, new Date(createdTimeStamp),
								discards, new Sort(Sort.Direction.DESC, "createdTime"));
					}
				} else {
					if (request.getSize() > 0) {
						recordsCollections = recordsRepository.findRecords(patientObjectId, doctorObjectId,
								locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards,
								new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
					} else {
						recordsCollections = recordsRepository.findRecords(patientObjectId, doctorObjectId,
								locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards,
								new Sort(Sort.Direction.DESC, "createdTime"));
					}
				}

				records = new ArrayList<Records>();
				for (RecordsCollection recordCollection : recordsCollections) {
					Records record = new Records();
					BeanUtil.map(recordCollection, record);
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByRecordId(recordCollection.getId());
					if (patientVisitCollection != null)
						record.setVisitId(patientVisitCollection.getId().toString());
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

			Criteria criteria = new Criteria("doctorId").is(doctorObjectId);
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
			UserCollection userCollection = userRepository.findOne(new ObjectId(patientId));
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
			RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(recordId));
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
			RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(recordId));
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
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
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
			TagsCollection tagsCollection = tagsRepository.findOne(new ObjectId(tagId));
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
	public List<Records> getRecordsByIds(List<ObjectId> recordIds) {
		List<Records> records = null;
		try {
			List<RecordsCollection> recordsCollections = recordsRepository.findAll(recordIds);
			if (recordsCollections != null) {
				records = new ArrayList<Records>();
				for (RecordsCollection recordCollection : recordsCollections) {
					Records record = new Records();
					BeanUtil.map(recordCollection, record);
					record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByRecordId(recordCollection.getId());
					if (patientVisitCollection != null)
						record.setVisitId(patientVisitCollection.getId().toString());
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
	public Integer getRecordCount(String doctorId, String patientId, String locationId, String hospitalId,
			boolean isOTPVerified) {
		Integer recordCount = 0;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			if (isOTPVerified)
				recordCount = recordsRepository.getRecordCount(patientObjectId, false);
			else
				recordCount = recordsRepository.getRecordCount(doctorObjectId, patientObjectId, hospitalObjectId,
						locationObjectId, false);
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
		String doctorId = flexibleCounts.getDoctorId();
		String locationId = flexibleCounts.getLocationId();
		String hospitalId = flexibleCounts.getHospitalId();
		String patientId = flexibleCounts.getPatientId();

		List<Count> counts = flexibleCounts.getCounts();
		try {
			boolean isOTPVerified = otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId);
			for (Count count : counts) {
				switch (count.getCountFor()) {
				case PRESCRIPTIONS:
					count.setValue(prescriptionService.getPrescriptionCount(doctorId, patientId, locationId, hospitalId,
							isOTPVerified));
					break;
				case RECORDS:
					count.setValue(getRecordCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
					break;
				case NOTES:
					count.setValue(clinicalNotesService.getClinicalNotesCount(doctorId, patientId, locationId,
							hospitalId, isOTPVerified));
					break;
				case HISTORY:
					count.setValue(historyServices.getHistoryCount(doctorId, patientId, locationId, hospitalId,
							isOTPVerified));
					break;
				case PATIENTVISITS:
					count.setValue(patientVisitServices.getVisitCount(doctorId, patientId, locationId, hospitalId,
							isOTPVerified));
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
			RecordsCollection recordCollection = recordsRepository.findOne(new ObjectId(recordId));
			if (recordCollection != null) {
				record = new Records();
				BeanUtil.map(recordCollection, record);
				PatientVisitCollection patientVisitCollection = patientVisitRepository
						.findByRecordId(recordCollection.getId());
				if (patientVisitCollection != null)
					record.setVisitId(patientVisitCollection.getId().toString());
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
			RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(recordId));

			if (recordsCollection != null) {

				BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
				AmazonS3 s3client = new AmazonS3Client(credentials);

				S3Object object = s3client
						.getObject(new GetObjectRequest(bucketName, recordsCollection.getRecordsUrl()));
				InputStream objectData = object.getObjectContent();

				mailAttachment = new MailAttachment();
				mailAttachment.setFileSystemResource(null);
				mailAttachment.setInputStream(objectData);
				PatientCollection patient = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						recordsCollection.getPatientId(), new ObjectId(doctorId), new ObjectId(locationId),
						new ObjectId(hospitalId));
				if (patient != null) {

					mailAttachment.setAttachmentName(patient.getLocalPatientName() + new Date() + "REPORTS."
							+ FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
				} else {
					mailAttachment.setAttachmentName(
							new Date() + "REPORTS." + FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
				}

				UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
				LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

				mailResponse = new MailResponse();
				mailResponse.setMailAttachment(mailAttachment);
				mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", " : "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode() : "");

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
			RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(recordId));
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
	public List<Records> getRecords(int page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory) {
		List<Records> records = null;
		List<RecordsCollection> recordsCollections = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;
		inHistorys[1] = true;
		try {
			if (discarded)
				discards[1] = true;
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

			if (isOTPVerified) {
				if (size > 0) {
					recordsCollections = recordsRepository.findRecords(patientObjectId, new Date(createdTimeStamp),
							discards, inHistorys, new PageRequest(page, size, Direction.DESC, "createdTime"));
				} else {
					recordsCollections = recordsRepository.findRecords(patientObjectId, new Date(createdTimeStamp),
							discards, inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));
				}
			} else {
				if (size > 0) {
					recordsCollections = recordsRepository.findRecords(patientObjectId, doctorObjectId,
							locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards, inHistorys,
							new PageRequest(page, size, Direction.DESC, "createdTime"));
				} else {
					recordsCollections = recordsRepository.findRecords(patientObjectId, doctorObjectId,
							locationObjectId, hospitalObjectId, new Date(createdTimeStamp), discards, inHistorys,
							new Sort(Sort.Direction.DESC, "createdTime"));
				}
			}
			records = new ArrayList<Records>();
			for (RecordsCollection recordCollection : recordsCollections) {
				Records record = new Records();
				BeanUtil.map(recordCollection, record);
				record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl()));
				PatientVisitCollection patientVisitCollection = patientVisitRepository
						.findByRecordId(recordCollection.getId());
				if (patientVisitCollection != null)
					record.setVisitId(patientVisitCollection.getId().toString());
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
	public List<Records> getRecordsByPatientId(String patientId, int page, int size, String updatedTime,
			Boolean discarded, Boolean isDoctorApp) {
		List<Records> records = null;
		List<RecordsCollection> recordsCollections = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			long updatedTimeLong = Long.parseLong(updatedTime);
			if (discarded)
				discards[1] = true;

			ObjectId patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);

			if (isDoctorApp) {
				if (size > 0)
					recordsCollections = recordsRepository.findRecordsByPatientId(patientObjectId,
							new Date(updatedTimeLong), discards,
							new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else
					recordsCollections = recordsRepository.findRecordsByPatientId(patientObjectId,
							new Date(updatedTimeLong), discards, new Sort(Sort.Direction.DESC, "createdTime"));
			} else {
				List<String> recordStates = new ArrayList<String>();
				recordStates.add(RecordsState.APPROVAL_NOT_REQUIRED.toString());
				recordStates.add(RecordsState.APPROVED_BY_DOCTOR.toString());

				if (size > 0)
					recordsCollections = recordsRepository.findRecordsByPatientId(patientObjectId,
							new Date(updatedTimeLong), discards, recordStates,
							new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
				else
					recordsCollections = recordsRepository.findRecordsByPatientId(patientObjectId,
							new Date(updatedTimeLong), discards, recordStates,
							new Sort(Sort.Direction.DESC, "createdTime"));
			}

			records = new ArrayList<Records>();
			for (RecordsCollection recordCollection : recordsCollections) {
				Records record = new Records();
				BeanUtil.map(recordCollection, record);
				PatientVisitCollection patientVisitCollection = patientVisitRepository
						.findByRecordId(recordCollection.getId());
				if (patientVisitCollection != null)
					record.setVisitId(patientVisitCollection.getId().toString());
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
			Date createdTime = new Date();
			UserCollection patientUserCollection = userRepository.findOne(new ObjectId(request.getPatientId()));
			PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
					new ObjectId(request.getPatientId()), new ObjectId(request.getDoctorId()),
					new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));

			RecordsCollection recordsCollection = null, oldRecord = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				recordsCollection = recordsRepository.findOne(new ObjectId(request.getId()));
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
				String path = "records" + File.separator + request.getPatientId();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String recordPath = path + File.separator + fileDetail.getFileName().split("[.]")[0]
						+ createdTime.getTime() + fileDetail.getFileName().split("[.]")[1];
				String recordLabel = fileDetail.getFileName();
				fileManager.saveRecord(file, recordPath);
				recordsCollection.setRecordsUrl(recordPath);
				recordsCollection.setRecordsPath(recordPath);
				recordsCollection.setRecordsLabel(recordLabel);
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
			} else {
				recordsCollection
						.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
				recordsCollection.setCreatedTime(createdTime);
				UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
				if (userCollection != null) {
					recordsCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
				}
				LocationCollection locationCollection = locationRepository.findOne(recordsCollection.getLocationId());
				if (locationCollection != null) {
					recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
				}
				PrescriptionCollection prescriptionCollection = null;
				if (recordsCollection.getPrescriptionId() != null) {
					prescriptionCollection = prescriptionRepository.findByUniqueIdAndPatientId(
							recordsCollection.getPrescriptionId(), recordsCollection.getPatientId());
				}
				if (prescriptionCollection != null) {
					recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
					recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
					recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
				}

				if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null
						|| !prescriptionCollection.getDiagnosticTests().isEmpty())) {
					List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
					for (TestAndRecordData data : prescriptionCollection.getDiagnosticTests()) {
						if (data.getTestId().equals(recordsCollection.getDiagnosticTestId())) {
							data.setRecordId(recordsCollection.getId().toString());
						}
						tests.add(data);
					}
					prescriptionCollection.setDiagnosticTests(tests);
					prescriptionCollection.setUpdatedTime(new Date());
					prescriptionRepository.save(prescriptionCollection);
				}

				String body = null;
				if (prescriptionCollection != null
						&& !DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState())) {
					if (recordsCollection.getRecordsState()
							.equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())) {
						String subject = approvedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", patientCollection.getLocalPatientName())
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), patientCollection.getLocalPatientName(),
								recordsCollection.getRecordsLabel(), recordsCollection.getUniqueEmrId(),
								"approvedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					} else if (recordsCollection.getRecordsState()
							.equalsIgnoreCase(RecordsState.APPROVAL_REQUIRED.toString())) {
						String subject = notApprovedRecordToDoctorSubject;
						subject = subject.replace("{patientName}", patientCollection.getLocalPatientName())
								.replace("{reportName}", recordsCollection.getRecordsLabel())
								.replace("{clinicName}", recordsCollection.getUploadedByLocation());
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), subject,
								ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
						body = mailBodyGenerator.generateRecordEmailBody(prescriptionCollection.getCreatedBy(),
								recordsCollection.getCreatedBy(), patientCollection.getLocalPatientName(),
								recordsCollection.getRecordsLabel(), recordsCollection.getUniqueEmrId(),
								"notApprovedRecordToDoctorTemplate.vm");
						mailService.sendEmail(userCollection.getEmailAddress(), subject, body, null);
					}
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(recordsCollection.getRecordsState()) && recordsCollection
					.getRecordsState().equalsIgnoreCase(RecordsState.APPROVAL_NOT_REQUIRED.toString())) {
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
				sendRecordSmsToPatient(patientCollection.getLocalPatientName(), patientUserCollection.getMobileNumber(),
						recordsCollection.getRecordsLabel(), recordsCollection.getUploadedByLocation(),
						recordsCollection.getDoctorId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId(), recordsCollection.getPatientId());
			}

			recordsCollection = recordsRepository.save(recordsCollection);
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
				recordPath = path + File.separator + fileDetail.getFileName().split("[.]")[0] + createdTime.getTime()
						+ fileDetail.getFileName().split("[.]")[1];
				fileManager.saveRecord(file, recordPath);
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
			RecordsCollection recordsCollection = recordsRepository.findOne(new ObjectId(recordId));
			if (recordsCollection == null) {
				logger.warn("Record Not found.Check RecordId");
				throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
			}
			recordsCollection.setRecordsState(recordsState);
			recordsCollection.setUpdatedTime(new Date());
			recordsRepository.save(recordsCollection);
			response = new Records();
			BeanUtil.map(recordsCollection, response);
			if (recordsState.equalsIgnoreCase(RecordsState.APPROVED_BY_DOCTOR.toString())) {
				UserCollection patientUserCollection = userRepository.findOne(recordsCollection.getPatientId());
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						recordsCollection.getPatientId(), recordsCollection.getDoctorId(),
						recordsCollection.getLocationId(), recordsCollection.getHospitalId());
				sendRecordSmsToPatient(patientCollection.getLocalPatientName(), patientUserCollection.getMobileNumber(),
						recordsCollection.getRecordsLabel(), recordsCollection.getUploadedByLocation(),
						recordsCollection.getDoctorId(), recordsCollection.getLocationId(),
						recordsCollection.getHospitalId(), recordsCollection.getPatientId());
				pushNotificationServices.notifyUser(recordsCollection.getPatientId().toString(),
						"Your Report from " + recordsCollection.getUploadedByLocation() + " is here - Tap to view it!",
						ComponentType.REPORTS.getType(), recordsCollection.getId().toString());
			} else if (recordsState.equalsIgnoreCase(RecordsState.DECLINED_BY_DOCTOR.toString())) {
				UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						recordsCollection.getPatientId(), recordsCollection.getDoctorId(),
						recordsCollection.getLocationId(), recordsCollection.getHospitalId());

				PrescriptionCollection prescriptionCollection = prescriptionRepository.findByUniqueIdAndPatientId(
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
}
