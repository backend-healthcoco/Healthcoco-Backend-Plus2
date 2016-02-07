package com.dpdocter.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Count;
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

    @Value(value = "${IMAGE_RESOURCE}")
    private String imageResource;

    @Value(value = "${IMAGE_URL_ROOT_PATH}")
    private String imageUrlRootPath;

    @Autowired
    private PatientVisitRepository patientVisitRepository;

    @Override
    public Records addRecord(RecordsAddRequest request) {
	try {

	    Date createdTime = new Date();

	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    if (request.getFileDetails() != null) {
		String recordLabel = request.getFileDetails().getFileName();
		request.getFileDetails().setFileName(request.getFileDetails().getFileName() + createdTime.getTime());
		String path = request.getPatientId() + File.separator + "records";
		// save image
		String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
		String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
		String recordPath = imageResource + File.separator + path + File.separator + fileName;

		recordsCollection.setRecordsUrl(recordUrl);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLable(recordLabel);
	    }
	    recordsCollection.setCreatedTime(createdTime);
	    recordsCollection.setUniqueId(DPDoctorUtils.generateRandomId());
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

	    if (prescriptionCollection != null && (prescriptionCollection.getTests() != null || !prescriptionCollection.getTests().isEmpty())) {
		List<TestAndRecordData> tests = new ArrayList<TestAndRecordData>();
		for (TestAndRecordData data : prescriptionCollection.getTests()) {
		    if (data.getLabTestId().equals(recordsCollection.getTestId())) {
			data.setRecordId(recordsCollection.getId());
		    }
		    tests.add(data);
		}
		prescriptionCollection.setTests(tests);
		prescriptionCollection.setUpdatedTime(new Date());
		prescriptionRepository.save(prescriptionCollection);
		String body = mailBodyGenerator.generateRecordsUploadedEmailBody(userCollection.getUserName(), userCollection.getFirstName(),
			userCollection.getMiddleName(), userCollection.getLastName());
		mailService.sendEmail(userCollection.getEmailAddress(), "Records Uploaded", "Record is uploaded", null);

	    }

	    Records records = new Records();
	    BeanUtil.map(recordsCollection, records);

	    return records;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
    public Records editRecord(RecordsEditRequest request) {

	Records records = new Records();
	try {
	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);

	    if (request.getFileDetails() != null) {
		String recordLabel = request.getFileDetails().getFileName();
		request.getFileDetails().setFileName(request.getFileDetails().getFileName() + new Date().getTime());
		String path = request.getPatientId() + File.separator + "records";
		// save image
		String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
		String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
		String recordPath = imageResource + File.separator + path + File.separator + fileName;

		recordsCollection.setRecordsUrl(recordUrl);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLable(recordLabel);

	    }
	    RecordsCollection oldRecord = recordsRepository.findOne(request.getId());
	    recordsCollection.setCreatedTime(oldRecord.getCreatedTime());
	    recordsCollection.setCreatedBy(oldRecord.getCreatedBy());
	    recordsCollection.setUploadedByLocation(oldRecord.getUploadedByLocation());
	    recordsCollection.setDiscarded(oldRecord.getDiscarded());
	    recordsCollection.setInHistory(oldRecord.isInHistory());
	    recordsCollection.setUniqueId(oldRecord.getUniqueId());
	    recordsCollection.setPrescribedByDoctorId(oldRecord.getDoctorId());
	    recordsCollection.setPrescribedByLocationId(oldRecord.getLocationId());
	    recordsCollection.setPrescribedByHospitalId(oldRecord.getHospitalId());
	    recordsCollection.setPrescriptionId(oldRecord.getPrescriptionId());
	    recordsCollection.setTestId(oldRecord.getTestId());

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
	    BeanUtil.map(recordsCollection, records);
	    return records;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());

	}

    }

    @Override
    public void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId, String emailAddress, UriInfo uriInfo) {
	try {
	    MailAttachment mailAttachment = createMailData(recordId, doctorId, locationId, hospitalId, uriInfo);
	    mailService.sendEmail(emailAddress, "Records", "PFA.", mailAttachment);
	} catch (MessagingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
    public List<Records> searchRecords(RecordsSearchRequest request, UriInfo uriInfo) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    boolean isOTPVerified = otpService
		    .checkOTPVerified(request.getDoctorId(), request.getLocationId(), request.getHospitalId(), request.getPatientId());
	    if (request.getDiscarded())
		discards[1] = true;
	    long createdTimeStamp = Long.parseLong(request.getUpdatedTime());

	    if (request.getTagId() != null) {
		List<RecordsTagsCollection> recordsTagsCollections = null;

		if (request.getSize() > 0)
		    recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId(), new PageRequest(request.getPage(), request.getSize(),
			    Direction.DESC, "updatedTime"));
		else
		    recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId(), new Sort(Sort.Direction.DESC, "updatedTime"));
		@SuppressWarnings("unchecked")
		Collection<String> recordIds = CollectionUtils.collect(recordsTagsCollections, new BeanToPropertyValueTransformer("recordsId"));

		recordsCollections = IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());

	    } else {

		if (isOTPVerified) {
		    if (request.getSize() > 0) {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), new Date(createdTimeStamp), discards, new PageRequest(
				request.getPage(), request.getSize(), Direction.DESC, "updatedTime"));
		    } else {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), new Date(createdTimeStamp), discards, new Sort(
				Sort.Direction.DESC, "updatedTime"));
		    }
		} else {
		    if (request.getSize() > 0) {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(), request
				.getHospitalId(), new Date(createdTimeStamp), discards, new PageRequest(request.getPage(), request.getSize(), Direction.DESC,
				"updatedTime"));
		    } else {
			recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
				request.getHospitalId(), new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}

		records = new ArrayList<Records>();
		for (RecordsCollection recordCollection : recordsCollections) {
		    Records record = new Records();
		    BeanUtil.map(recordCollection, record);
		    PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		    if (patientVisitCollection != null)
			record.setVisitId(patientVisitCollection.getId());
		    record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl(), uriInfo));
		    records.add(record);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

	return records;
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    @Override
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
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return tags;
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return emailAddress;
    }

    @Override
    public File getRecordFile(String recordId) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection != null) {
		if (recordsCollection.getRecordsPath() != null) {
		    return new File(recordsCollection.getRecordsPath());
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    @Override
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
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
    public void deleteTag(String tagId) {
	try {
	    tagsRepository.delete(tagId);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return records;
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, "Error while getting Records Count");
	}
	return recordCount;
    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, "Error while getting counts");
	}

	return flexibleCounts;

    }

    @Override
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
	    throw new BusinessException(ServiceError.Forbidden, "Error while getting record : " + e.getCause().getMessage());
	}
	return record;
    }

    @Override
    public MailAttachment getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId, UriInfo uriInfo) {
	return createMailData(recordId, doctorId, locationId, hospitalId, uriInfo);
    }

    private MailAttachment createMailData(String recordId, String doctorId, String locationId, String hospitalId, UriInfo uriInfo) {
	MailAttachment mailAttachment = null;
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection != null) {

		FileSystemResource file = new FileSystemResource(imageResource + "/" + recordsCollection.getRecordsUrl());
		mailAttachment = new MailAttachment();
		mailAttachment.setFileSystemResource(file);

		UserCollection patientUserCollection = userRepository.findOne(recordsCollection.getPatientId());
		if (patientUserCollection != null) {
		    mailAttachment.setAttachmentName(patientUserCollection.getFirstName() + new Date() + "REPORTS."
			    + FilenameUtils.getExtension(file.getFilename()));
		} else {
		    mailAttachment.setAttachmentName(new Date() + "REPORTS." + FilenameUtils.getExtension(file.getFilename()));
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

	return mailAttachment;
    }

    @Override
    public void changeLabelAndDescription(String recordId, String label, String description) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection == null) {
		logger.warn("Record not found.Check RecordId !");
		throw new BusinessException(ServiceError.NoRecord, "Record not found.Check RecordId !");
	    }
	    recordsCollection.setRecordsLable(label);
	    recordsCollection.setDescription(description);
	    recordsCollection.setUpdatedTime(new Date());
	    recordsRepository.save(recordsCollection);
	} catch (BusinessException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    private String getFinalImageURL(String imageURL, UriInfo uriInfo) {
	if (imageURL != null) {
	    String finalImageURL = uriInfo.getBaseUri().toString().replace(uriInfo.getBaseUri().getPath(), imageUrlRootPath);
	    return finalImageURL + imageURL;
	} else
	    return null;
    }

    @Override
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
		    recordsCollections = recordsRepository.findRecords(patientId, new Date(createdTimeStamp), discards, inHistorys, new PageRequest(page, size,
			    Direction.DESC, "updatedTime"));
		} else {
		    recordsCollections = recordsRepository.findRecords(patientId, new Date(createdTimeStamp), discards, inHistorys, new Sort(
			    Sort.Direction.DESC, "updatedTime"));
		}
	    } else {
		if (size > 0) {
		    recordsCollections = recordsRepository.findRecords(patientId, doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    inHistorys, new PageRequest(page, size, Direction.DESC, "updatedTime"));
		} else {
		    recordsCollections = recordsRepository.findRecords(patientId, doctorId, locationId, hospitalId, new Date(createdTimeStamp), discards,
			    inHistorys, new Sort(Sort.Direction.DESC, "updatedTime"));
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
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}

	return records;
    }

    @Override
    public List<Records> getRecordsByPatientId(String patientId, int page, int size, String updatedTime, Boolean discarded, UriInfo uriInfo) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	boolean[] discards = new boolean[2];
	discards[0] = false;
	try {
	    long updatedTimeLong = Long.parseLong(updatedTime);
	    if (discarded)
		discards[1] = true;
	    if (size > 0)
		recordsCollections = recordsRepository.findRecordsByPatientId(patientId, new Date(updatedTimeLong), discards, new PageRequest(page, size,
			Sort.Direction.DESC, "updatedTime"));
	    else
		recordsCollections = recordsRepository.findRecordsByPatientId(patientId, new Date(updatedTimeLong), discards, new Sort(Sort.Direction.DESC,
			"updatedTime"));
	    records = new ArrayList<Records>();
	    for (RecordsCollection recordCollection : recordsCollections) {
		Records record = new Records();
		BeanUtil.map(recordCollection, record);
		PatientVisitCollection patientVisitCollection = patientVisitRepository.findByRecordId(record.getId());
		if (patientVisitCollection != null)
		    record.setVisitId(patientVisitCollection.getId());
		record.setRecordsUrl(getFinalImageURL(record.getRecordsUrl(), uriInfo));
		records.add(record);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
	return records;
    }

    @Override
    public Records addRecordsMultipart(FormDataBodyPart file, RecordsAddRequestMultipart request) {
	try {

	    Date createdTime = new Date();

	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    if (file != null) {

		String recordLabel = request.getFileName();
		String path = request.getPatientId() + File.separator + "records";

		FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
		String recordPath = imageResource + File.separator + path + File.separator;
		writeToFile(file.getEntityAs(InputStream.class), recordPath);

		recordsCollection.setRecordsUrl(path + "/" + fileDetail.getFileName() + createdTime.getTime());
		recordsCollection.setRecordsPath(recordPath + fileDetail.getFileName() + createdTime.getTime());
		recordsCollection.setRecordsLable(recordLabel);
	    }
	    recordsCollection.setCreatedTime(createdTime);
	    UserCollection userCollection = userRepository.findOne(recordsCollection.getDoctorId());
	    if (userCollection != null) {
		recordsCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
	    }
	    LocationCollection locationCollection = locationRepository.findOne(recordsCollection.getLocationId());
	    if (locationCollection != null) {
		recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
	    }
	    recordsCollection = recordsRepository.save(recordsCollection);
	    Records records = new Records();
	    BeanUtil.map(recordsCollection, records);

	    return records;
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Forbidden, e.getMessage());
	}
    }

    private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

	try {
	    OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
	    int read = 0;
	    byte[] bytes = new byte[1024];

	    out = new FileOutputStream(new File(uploadedFileLocation));
	    while ((read = uploadedInputStream.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }
	    out.flush();
	    out.close();
	} catch (IOException e) {

	    e.printStackTrace();
	}

    }
}
