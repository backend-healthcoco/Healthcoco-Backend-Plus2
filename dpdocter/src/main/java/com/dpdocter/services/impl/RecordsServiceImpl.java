package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Count;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.RecordsDescription;
import com.dpdocter.beans.Tags;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.RecordsTagsRepository;
import com.dpdocter.repository.TagsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RecordsService;
import common.util.web.DPDoctorUtils;

@Service
public class RecordsServiceImpl implements RecordsService {
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
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClinicalNotesService clinicalNotesService;

    @Autowired
    private PrescriptionServices prescriptionService;

    @Autowired
    private HistoryServices historyServices;

    @Value(value = "${IMAGE_RESOURCE}")
    private String imageResource;

    @Override
    public Records addRecord(RecordsAddRequest request) {
	try {
	    String path = request.getPatientId() + File.separator + "records";
	    // save image
	    String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
	    String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
	    String recordPath = imageResource + File.separator + path + File.separator + fileName;

	    // save records
	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);

	    recordsCollection.setCreatedTime(new Date());
	    recordsCollection.setCreatedDate(new Date().getTime());
	    recordsCollection.setRecordsUrl(recordUrl);
	    recordsCollection.setRecordsPath(recordPath);
	    recordsCollection.setRecordsLable(getFileNameFromImageURL(recordUrl));
	    recordsCollection = recordsRepository.save(recordsCollection);
	    Records records = new Records();
	    BeanUtil.map(recordsCollection, records);

	    return records;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}

    }

    @Override
    public Records editRecord(RecordsEditRequest request) {

	Records records = new Records();
	try {
	    RecordsCollection recordsCollection = new RecordsCollection();
	    BeanUtil.map(request, recordsCollection);
	    // recordsCollection.setCreatedTime(new Date());
	    if (request.getFileDetails() != null) {
		String path = request.getPatientId() + File.separator + "records";
		// save image
		String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
		String fileName = request.getFileDetails().getFileName() + "." + request.getFileDetails().getFileExtension();
		String recordPath = imageResource + File.separator + path + File.separator + fileName;

		recordsCollection.setRecordsUrl(recordUrl);
		recordsCollection.setRecordsPath(recordPath);
		recordsCollection.setRecordsLable(getFileNameFromImageURL(recordUrl));

	    }
	    // save records
	    recordsCollection = recordsRepository.save(recordsCollection);
	    BeanUtil.map(recordsCollection, records);
	    return records;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());

	}

    }

    @Override
    public void emailRecordToPatient(String recordId, String emailAddr) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection != null) {
		FileSystemResource file = new FileSystemResource(recordsCollection.getRecordsPath());
		MailAttachment mailAttachment = new MailAttachment();
		mailAttachment.setAttachmentName(recordsCollection.getRecordsLable());
		mailAttachment.setFileSystemResource(file);
		mailService.sendEmail(emailAddr, "Records", "PFA.", mailAttachment);

	    } else {
		throw new BusinessException(ServiceError.Unknown, "Record not found.Please check recordId.");
	    }

	} catch (BusinessException e) {
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    private String getFileNameFromImageURL(String url) {
	String arr[] = url.split("/");
	String imageName = arr[arr.length - 1];
	imageName = imageName.substring(0, imageName.lastIndexOf("."));
	return imageName;
    }

    @Override
    public void tagRecord(TagRecordRequest request) {
	try {
	    List<RecordsTagsCollection> recordsTagsCollections = new ArrayList<RecordsTagsCollection>();
	    for (String tagId : request.getTags()) {
		RecordsTagsCollection recordsTagsCollection = new RecordsTagsCollection();
		recordsTagsCollection.setrecordsId(request.getRecordId());
		recordsTagsCollection.setTagsId(tagId);
		recordsTagsCollections.add(recordsTagsCollection);
	    }
	    recordsTagsRepository.save(recordsTagsCollections);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public void changeReportLabel(String recordId, String label) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection == null) {
		throw new BusinessException(ServiceError.Unknown, "Record not found.Check RecordId !");
	    }
	    recordsCollection.setRecordsLable(label);
	    recordsRepository.save(recordsCollection);
	} catch (BusinessException e) {
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public List<Records> searchRecords(RecordsSearchRequest request) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	try {
	    if (request.getTagId() != null) {
		List<RecordsTagsCollection> recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId());
		@SuppressWarnings("unchecked")
		Collection<String> recordIds = CollectionUtils.collect(recordsTagsCollections, new BeanToPropertyValueTransformer("recordsId"));
		@SuppressWarnings("unchecked")
		List<RecordsCollection> recordCollections = IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());
		records = new ArrayList<Records>();
		BeanUtil.map(recordCollections, records);
	    } else {
		if (request.getCreatedTime() != null) {
		    long createdTimeStamp = Long.parseLong(request.getCreatedTime());
		    recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
			    request.getHospitalId(), new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC, "createdDate"));
		} else {
		    recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId(), request.getLocationId(),
			    request.getHospitalId(), false, new Sort(Sort.Direction.DESC, "createdDate"));
		}
		records = new ArrayList<Records>();
		BeanUtil.map(recordsCollections, records);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		throw new BusinessException(ServiceError.Unknown, "Invalid Input !");
	    }
	} catch (BusinessException e) {
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return tags;
    }

    @Override
    public String getPatientEmailAddress(String patientId) {
	String emailAddress = null;
	try {
	    PatientCollection patientCollection = patientRepository.findByUserId(patientId);
	    if (patientCollection != null) {
		emailAddress = patientCollection.getEmailAddress();
	    } else {
		throw new BusinessException(ServiceError.Unknown, "Invalid PatientId");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		    throw new BusinessException(ServiceError.Unknown, "Record Path for this Record is Empty.");
		}
	    } else {
		throw new BusinessException(ServiceError.Unknown, "Record not found.Please check recordId.");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }

    @Override
    public void deleteRecord(String recordId) {
	try {
	    RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
	    if (recordsCollection == null) {
		throw new BusinessException(ServiceError.Unknown, "Record Not found.Check RecordId");
	    }
	    recordsCollection.setDeleted(true);
	    recordsRepository.save(recordsCollection);
	} catch (BusinessException e) {
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

    @Override
    public void deleteTag(String tagId) {
	try {
	    tagsRepository.delete(tagId);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
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
		BeanUtil.map(recordsCollections, records);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return records;
    }

    @Override
    public List<Records> searchRecords(String doctorId, String locationId, String hospitalId, String createdTime) {
	List<Records> records = null;
	List<RecordsCollection> recordsCollections = null;
	try {
	    if (DPDoctorUtils.anyStringEmpty(createdTime)) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
		    recordsCollections = recordsRepository.findAll(doctorId, false, new Sort(Sort.Direction.DESC, "createdDate"));
		} else {
		    recordsCollections = recordsRepository.findAll(doctorId, locationId, hospitalId, false, new Sort(Sort.Direction.DESC, "createdDate"));
		}
	    } else {
		long createdTimeStamp = Long.parseLong(createdTime);
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
		    recordsCollections = recordsRepository.findAll(doctorId, new Date(createdTimeStamp), false, new Sort(Sort.Direction.DESC, "createdDate"));
		} else {
		    recordsCollections = recordsRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), false, new Sort(
			    Sort.Direction.DESC, "createdDate"));
		}
	    }

	    if (recordsCollections != null && !recordsCollections.isEmpty()) {
		records = new ArrayList<Records>();
		BeanUtil.map(recordsCollections, records);
	    } else {
		throw new BusinessException(ServiceError.Unknown, "No Records Found");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	return records;
    }

    @Override
    public Integer getRecordCount(String doctorId, String patientId, String locationId, String hospitalId) {
	Integer recordCount = 0;
	try {
	    recordCount = recordsRepository.getRecordCount(doctorId, patientId, hospitalId, locationId, false);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting Records Count");
	}
	return recordCount;
    }

    @Override
    public boolean editDescription(RecordsDescription recordsDescription) {
	RecordsCollection record = null;
	boolean response = false;
	try {
	    record = recordsRepository.findOne(recordsDescription.getId());
	    if (record != null) {
		record.setDescription(recordsDescription.getDescription());
		recordsRepository.save(record);
		response = true;
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No record found for the given record id");
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while editing description");
	}
	return response;
    }

    @Override
    public FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts) {
	String doctorId = flexibleCounts.getDoctorId();
	String locationId = flexibleCounts.getLocationId();
	String hospitalId = flexibleCounts.getHospitalId();
	String patientId = flexibleCounts.getPatientId();

	List<Count> counts = flexibleCounts.getCounts();
	try {
	    for (Count count : counts) {
		switch (count.getCountFor()) {
		case PRESCRIPTIONS:
		    count.setValue(prescriptionService.getPrescriptionCount(doctorId, patientId, locationId, hospitalId));
		    break;
		case RECORDS:
		    count.setValue(getRecordCount(doctorId, patientId, locationId, hospitalId));
		    break;
		case NOTES:
		    count.setValue(clinicalNotesService.getClinicalNotesCount(doctorId, patientId, locationId, hospitalId));
		    break;
		case HISTORY:
		    count.setValue(historyServices.getHistoryCount(doctorId, patientId, locationId, hospitalId));
		    break;
		default:
		    break;
		}
	    }
	    flexibleCounts.setCounts(counts);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error while getting counts");
	}

	return flexibleCounts;

    }
}
