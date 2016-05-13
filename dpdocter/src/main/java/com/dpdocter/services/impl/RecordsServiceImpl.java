package com.dpdocter.services.impl;

import java.io.File;
import java.io.InputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
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
    private PatientRepository patientRepository;

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
	
    @Value(value = "${image.path}")
    private String imagePath;

    @Value(value = "${bucket.name}")
    private String bucketName;

    @Value(value = "${mail.aws.key.id}")
    private String AWS_KEY;

    @Value(value = "${mail.aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Override
    @Transactional
    public Records addRecord(RecordsAddRequest request) {
	try {

	    Date createdTime = new Date();

	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    if(!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())){
	    	String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
	    	recordsCollection.setRecordsUrl(recordsURL);
			recordsCollection.setRecordsPath(recordsURL);
			recordsCollection.setRecordsLabel(FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length()-13));
	    }
	    if (request.getFileDetails() != null) {
		String recordLabel = request.getFileDetails().getFileName();
		request.getFileDetails().setFileName(request.getFileDetails().getFileName() + createdTime.getTime());
		String path = "records" + File.separator + request.getPatientId();
		
		String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
		String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
		String recordPath = path + File.separator + fileName;

		recordsCollection.setRecordsUrl(recordUrl);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLabel(recordLabel);
	    }
	    recordsCollection.setCreatedTime(createdTime);
	    recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
	    UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
	    if (userCollection != null) {
		recordsCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
	    }
	    LocationCollection locationCollection = locationRepository.findOne(recordsCollection.getLocationId());
	    if (locationCollection != null) {
		recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
	    }
	    PrescriptionCollection prescriptionCollection = null;
	    if (recordsCollection.getPrescriptionId() != null) {
		prescriptionCollection = prescriptionRepository.findByUniqueIdAndPatientId(recordsCollection.getPrescriptionId(),
			recordsCollection.getPatientId());
	    }
	    if (prescriptionCollection != null) {
		recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
		recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
		recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());

	    }
	    recordsCollection = recordsRepository.save(recordsCollection);

	    if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null || !prescriptionCollection.getDiagnosticTests().isEmpty())) {
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
	    if(prescriptionCollection != null && prescriptionCollection.getDoctorId().equalsIgnoreCase(recordsCollection.getDoctorId()) &&
	    		prescriptionCollection.getLocationId().equalsIgnoreCase(recordsCollection.getLocationId()) && prescriptionCollection.getHospitalId().equalsIgnoreCase(recordsCollection.getHospitalId()))
	    pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId(), "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by lab");

	    pushNotificationServices.notifyUser(recordsCollection.getPatientId(), "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by lab");
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
    public Records editRecord(RecordsEditRequest request) {

	Records records = new Records();
	try {
	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    if(!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())){
	    	String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
	    	recordsCollection.setRecordsUrl(recordsURL);
			recordsCollection.setRecordsPath(recordsURL);
			recordsCollection.setRecordsLabel(FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length()-13));
	    }
	    if (request.getFileDetails() != null) {
		String recordLabel = request.getFileDetails().getFileName();
		request.getFileDetails().setFileName(request.getFileDetails().getFileName() + new Date().getTime());
		String path = request.getPatientId() + File.separator + "records";
		// save image
		String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
		String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
		String recordPath = path + File.separator + fileName;

		recordsCollection.setRecordsUrl(recordUrl);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLabel(recordLabel);

	    }
	    RecordsCollection oldRecord = recordsRepository.findOne(request.getId());
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

	    // PrescriptionCollection prescriptionCollection = null;
	    // if(recordsCollection.getPrescriptionId() != null){
	    // prescriptionCollection =
	    // prescriptionRepository.findByUniqueIdAndPatientId(recordsCollection.getPrescriptionId(),
	    // recordsCollection.getPatientId());
	    // }
	    // if(prescriptionCollection != null){
	    // recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
	    // recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
	    // recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
	    //
	    // }
	    // recordsCollection = recordsRepository.save(recordsCollection);
	    //
	    // if(prescriptionCollection != null &&
	    // (prescriptionCollection.getTests() != null ||
	    // !prescriptionCollection.getTests().isEmpty())){
	    // List<TestAndRecordData> tests = new
	    // ArrayList<TestAndRecordData>();
	    // for(TestAndRecordData data : prescriptionCollection.getTests()){
	    // if(data.getLabTestId().equals(recordsCollection.getTestId())){
	    // data.setRecordId(recordsCollection.getId());
	    // }
	    // tests.add(data);
	    // }
	    // prescriptionCollection.setTests(tests);
	    // prescriptionCollection.setUpdatedTime(new Date());
	    // prescriptionRepository.save(prescriptionCollection);
	    // }

	    recordsCollection = recordsRepository.save(recordsCollection);

	    pushNotificationServices.notifyUser(recordsCollection.getPatientId(), "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by lab");

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
    public void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId, String emailAddress) {
	try {
	    MailAttachment mailAttachment = createMailData(recordId, doctorId, locationId, hospitalId);
	    mailService.sendEmail(emailAddress, "Records", "PFA.", mailAttachment);
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
		recordsTagsCollection.setrecordsId(request.getRecordId());
		recordsTagsCollection.setTagsId(tagId);
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

    @Override
    @Transactional
    public List<Records> searchRecords(RecordsSearchRequest request) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    boolean isOTPVerified = otpService.checkOTPVerified(request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
		    request.getPatientId());
	    if (request.getDiscarded())
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(request.getUpdatedTime());

	    if (request.getTagId() != null) {
		List<RecordsTagsCollection> recordsTagsCollections = null;

		if (request.getSize() > 0)
		    recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId(),
			    new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
		else
		    recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId(), new Sort(Sort.Direction.DESC, "createdTime"));
		@SuppressWarnings("unchecked")
		Collection<String> recordIds = CollectionUtils.collect(recordsTagsCollections, new BeanToPropertyValueTransformer("recordsId"));

		recordsCollections = IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());

	    } else {

		if (isOTPVerified) {
		    if (request.getSize() > 0) {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), new Date(createdTimeStamp), discards,
				new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
		    } else {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), new Date(createdTimeStamp), discards,
				new Sort(Sort.Direction.DESC, "createdTime"));
		    }
		} else {
		    if (request.getSize() > 0) {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), new Date(createdTimeStamp), discards,
				new PageRequest(request.getPage(), request.getSize(), Direction.DESC, "createdTime"));
		    } else {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));
		    }
		}

		records = new ArrayList<Records>();
		for (RecordsCollection recordCollection : recordsCollections) {
		    Records record = new Records();
		    BeanUtil.map(recordCollection, record);
		    PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		    if (patientVisitCollection != null)
			record.setVisitId(patientVisitCollection.getId());
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

	    List<TagsCollection> tagsCollections = null;
	    if (doctorId != null && locationId != null && hospitalId != null) {
		tagsCollections = new ArrayList<TagsCollection>();
		tags = new ArrayList<Tags>();
		tagsCollections = tagsRepository.findByDoctorIdAndlocationIdAndHospitalId(doctorId, locationId, hospitalId);
		BeanUtil.map(tagsCollections, tags);
	    } else if (doctorId != null && locationId != null && hospitalId == null) {
		tagsCollections = new ArrayList<TagsCollection>();
		tags = new ArrayList<Tags>();
		tagsCollections = tagsRepository.findByDoctorIdAndlocationId(doctorId, locationId);
		BeanUtil.map(tagsCollections, tags);
	    } else if (doctorId != null && locationId == null && hospitalId == null) {
		tagsCollections = new ArrayList<TagsCollection>();
		tags = new ArrayList<Tags>();
		tagsCollections = tagsRepository.findByDoctorId(doctorId);
		BeanUtil.map(tagsCollections, tags);
	    } else {
		logger.warn("Invalid Input");
		throw new BusinessException(ServiceError.InvalidInput, "Invalid Input !");
	    }
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
	    UserCollection userCollection = userRepository.findOne(patientId);
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
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection != null) {
		if (recordsCollection.getRecordsPath() != null) {
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
			AmazonS3 s3client = new AmazonS3Client(credentials);

			S3Object object = s3client.getObject(new GetObjectRequest(bucketName, recordsCollection.getRecordsUrl()));
			InputStream objectData = object.getObjectContent();
			if(objectData != null){
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

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    @Transactional
    public void deleteRecord(String recordId, Boolean discarded) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection == null) {
		logger.warn("Record Not found.Check RecordId");
		throw new BusinessException(ServiceError.NoRecord, "Record Not found.Check RecordId");
	    }
	    recordsCollection.setDiscarded(discarded);
	    recordsCollection.setUpdatedTime(new Date());
	    recordsRepository.save(recordsCollection);
	    pushNotificationServices.notifyUser(recordsCollection.getPatientId(), "Report:"+recordsCollection.getUniqueEmrId()+" is discarded");
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public void deleteTag(String tagId) {
	try {
	    tagsRepository.delete(tagId);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    @Transactional
    public List<Records> getRecordsByIds(List<String> recordIds) {
	List<Records> records = null;
	try {
	    Iterable<RecordsCollection> recordsCollectionIterable = recordsRepository.findAll(recordIds);
	    if (recordsCollectionIterable != null) {
		@SuppressWarnings("unchecked")
		List<RecordsCollection> recordsCollections = IteratorUtils.toList(recordsCollectionIterable.iterator());
		records = new ArrayList<Records>();
		for (RecordsCollection recordCollection : recordsCollections) {
		    Records record = new Records();
		    BeanUtil.map(recordCollection, record);
		    PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		    if (patientVisitCollection != null)
			record.setVisitId(patientVisitCollection.getId());
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
    public Integer getRecordCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified) {
	Integer recordCount = 0;
	try {
	    if (isOTPVerified)
		recordCount = recordsRepository.getRecordCount(patientId, false);
	    else
		recordCount = recordsRepository.getRecordCount(doctorId, patientId, hospitalId, locationId, false);
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
		    count.setValue(prescriptionService.getPrescriptionCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
		    break;
		case RECORDS:
		    count.setValue(getRecordCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
		    break;
		case NOTES:
		    count.setValue(clinicalNotesService.getClinicalNotesCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
		    break;
		case HISTORY:
		    count.setValue(historyServices.getHistoryCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
		    break;
		case PATIENTVISITS:
		    count.setValue(patientVisitServices.getVisitCount(doctorId, patientId, locationId, hospitalId, isOTPVerified));
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
	    RecordsCollection recordCollection = recordsRepository.findOne(recordId);
	    if (recordCollection != null) {
		record = new Records();
		BeanUtil.map(recordCollection, record);
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		if (patientVisitCollection != null)
		    record.setVisitId(patientVisitCollection.getId());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error while getting record : " + e.getCause().getMessage());
	    throw new BusinessException(ServiceError.Unknown, "Error while getting record : " + e.getCause().getMessage());
	}
	return record;
    }

    @Override
    @Transactional
    public MailAttachment getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId) {
	return createMailData(recordId, doctorId, locationId, hospitalId);
    }

    private MailAttachment createMailData(String recordId, String doctorId, String locationId, String hospitalId) {
	MailAttachment mailAttachment = null;
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection != null) {

		BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonS3 s3client = new AmazonS3Client(credentials);

		S3Object object = s3client.getObject(new GetObjectRequest(bucketName, recordsCollection.getRecordsUrl()));
		InputStream objectData = object.getObjectContent();

		mailAttachment = new MailAttachment();
		mailAttachment.setFileSystemResource(null);
		mailAttachment.setInputStream(objectData);
		UserCollection patientUserCollection = userRepository.findOne(recordsCollection.getPatientId());
		if (patientUserCollection != null) {
		    mailAttachment.setAttachmentName(
			    patientUserCollection.getFirstName() + new Date() + "REPORTS." + FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
		} else {
		    mailAttachment.setAttachmentName(new Date() + "REPORTS." + FilenameUtils.getExtension(recordsCollection.getRecordsUrl()));
		}
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		emailTrackCollection.setDoctorId(recordsCollection.getDoctorId());
		emailTrackCollection.setHospitalId(recordsCollection.getHospitalId());
		emailTrackCollection.setLocationId(recordsCollection.getLocationId());
		emailTrackCollection.setPatientId(recordsCollection.getPatientId());
		UserCollection userCollection = userRepository.findOne(recordsCollection.getPatientId());
		if (userCollection != null)
		    emailTrackCollection.setPatientName(userCollection.getFirstName());
		emailTrackCollection.setType(ComponentType.REPORTS.getType());
		emailTrackCollection.setSubject("Reports");

		emailTackService.saveEmailTrack(emailTrackCollection);

	    } else {
		logger.warn("Record not found.Please check recordId.");
		throw new BusinessException(ServiceError.NotFound, "Record not found.Please check recordId.");
	    }
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

	return mailAttachment;
    }

    @Override
    @Transactional
    public void changeLabelAndDescription(String recordId, String label, String explanation) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
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
    public List<Records> getRecords(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory) {
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

	    if (isOTPVerified) {
		if (size > 0) {
		    recordsCollections = recordsRepository.findRecords(patientId, new Date(createdTimeStamp), discards, inHistorys,
			    new PageRequest(page, size, Direction.DESC, "createdTime"));
		} else {
		    recordsCollections = recordsRepository.findRecords(patientId, new Date(createdTimeStamp), discards, inHistorys,
			    new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    } else {
		if (size > 0) {
		    recordsCollections = recordsRepository.findRecords(patientId, doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    inHistorys, new PageRequest(page, size, Direction.DESC, "createdTime"));
		} else {
		    recordsCollections = recordsRepository.findRecords(patientId, doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    inHistorys, new Sort(Sort.Direction.DESC, "createdTime"));
		}
	    }
	    records = new ArrayList<Records>();
	    for (RecordsCollection recordCollection : recordsCollections) {
		Records record = new Records();
		BeanUtil.map(recordCollection, record);
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		if (patientVisitCollection != null)
		    record.setVisitId(patientVisitCollection.getId());
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
    public List<Records> getRecordsByPatientId(String patientId, int page, int size, String updatedTime, Boolean discarded) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    long updatedTimeLong = Long.parseLong(updatedTime);
	    if (discarded)
		discards[1] = true;
	    if (size > 0)
		recordsCollections = recordsRepository.findRecordsByPatientId(patientId, new Date(updatedTimeLong), discards,
			new PageRequest(page, size, Sort.Direction.DESC, "createdTime"));
	    else
		recordsCollections = recordsRepository.findRecordsByPatientId(patientId, new Date(updatedTimeLong), discards,
			new Sort(Sort.Direction.DESC, "createdTime"));
	    records = new ArrayList<Records>();
	    for (RecordsCollection recordCollection : recordsCollections) {
		Records record = new Records();
		BeanUtil.map(recordCollection, record);
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		if (patientVisitCollection != null)
		    record.setVisitId(patientVisitCollection.getId());
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
	    RecordsCollection recordsCollection = null, oldRecord = null;
	    if(!DPDoctorUtils.anyStringEmpty(request.getId())){
	    	recordsCollection = recordsRepository.findOne(request.getId());
	    	oldRecord = recordsCollection;
	    }
	    if(recordsCollection == null)recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    if(!DPDoctorUtils.anyStringEmpty(request.getRecordsUrl())){
	    	String recordsURL = request.getRecordsUrl().replaceAll(imagePath, "");
	    	recordsCollection.setRecordsUrl(recordsURL);
			recordsCollection.setRecordsPath(recordsURL);
			recordsCollection.setRecordsLabel(FilenameUtils.getBaseName(recordsURL).substring(0, recordsURL.length()-13));
	    }
	    if (file != null) {
		String path = "records" + File.separator + request.getPatientId();
		FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
		String recordPath = path + File.separator + fileDetail.getFileName().split("[.]")[0] + createdTime.getTime() + fileDetail.getFileName().split("[.]")[1];
		String recordLabel = fileDetail.getFileName();
		fileManager.saveRecord(file, recordPath);
		recordsCollection.setRecordsUrl(recordPath);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLabel(recordLabel);
	    }
	    
	    if(oldRecord != null){
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
	    }
	    else{
	    	recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
	    	recordsCollection.setCreatedTime(createdTime);
		    UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
		    if (userCollection != null) {
			recordsCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
		    }
		    LocationCollection locationCollection = locationRepository.findOne(recordsCollection.getLocationId());
		    if (locationCollection != null) {
			recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
		    }
		    PrescriptionCollection prescriptionCollection = null;
		    if (recordsCollection.getPrescriptionId() != null) {
			prescriptionCollection = prescriptionRepository.findByUniqueIdAndPatientId(recordsCollection.getPrescriptionId(),
				recordsCollection.getPatientId());
		    }
		    if (prescriptionCollection != null) {
			recordsCollection.setPrescribedByDoctorId(prescriptionCollection.getDoctorId());
			recordsCollection.setPrescribedByLocationId(prescriptionCollection.getLocationId());
			recordsCollection.setPrescribedByHospitalId(prescriptionCollection.getHospitalId());
		    }

		    if (prescriptionCollection != null && (prescriptionCollection.getDiagnosticTests() != null || !prescriptionCollection.getDiagnosticTests().isEmpty())) {
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
		    
		    if(prescriptionCollection != null && prescriptionCollection.getDoctorId().equalsIgnoreCase(recordsCollection.getDoctorId()) &&
		    		prescriptionCollection.getLocationId().equalsIgnoreCase(recordsCollection.getLocationId()) && prescriptionCollection.getHospitalId().equalsIgnoreCase(recordsCollection.getHospitalId()))
		    pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId(), "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by lab");

	    }
	    
	    pushNotificationServices.notifyUser(recordsCollection.getPatientId(), "Report:"+recordsCollection.getUniqueEmrId()+" is uploaded by lab");

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
		    	recordPath = path + File.separator + fileDetail.getFileName().split("[.]")[0] + createdTime.getTime() + fileDetail.getFileName().split("[.]")[1];
		    	fileManager.saveRecord(file, recordPath);
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    logger.error(e);
		    throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return recordPath;
	}
}
