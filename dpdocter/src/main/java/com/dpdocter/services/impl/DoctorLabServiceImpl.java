package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorLabReport;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.DoctorLabDoctorReferenceCollection;
import com.dpdocter.collections.DoctorLabFavouriteDoctorCollection;
import com.dpdocter.collections.DoctorLabReportCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorLabDoctorReferenceRepository;
import com.dpdocter.repository.DoctorLabFevouriteDoctorRepository;
import com.dpdocter.repository.DoctorLabReportRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorLabDoctorReferenceRequest;
import com.dpdocter.request.DoctorLabFavouriteDoctorRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.response.DoctorLabFavouriteDoctorResponse;
import com.dpdocter.response.DoctorLabReportResponse;
import com.dpdocter.response.DoctorLabSearchDoctorResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.DoctorLabService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LabPrintServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class DoctorLabServiceImpl implements DoctorLabService {

	@Autowired
	private DoctorLabReportRepository doctorLabReportRepository;

	@Autowired
	private LabPrintServices labPrintServices;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ESCityRepository esCityRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private DoctorLabFevouriteDoctorRepository doctorLabFevouriteDoctorRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESSpecialityRepository esSpecialityRepository;

	@Autowired
	private DoctorLabDoctorReferenceRepository doctorLabDoctorReferenceRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private JasperReportService jasperReportService;

	@Value(value = "${mail.signup.request.to}")
	private String mailTo;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private LocationRepository locationRepository;

	@Value(value = "${sms.add.doctor.Lab.report.to.patient}")
	private String patientSMSText;

	@Value(value = "${sms.add.doctor.Lab.report.to.doctor}")
	private String doctorSMSText;

	@Value(value = "${sms.doctor.Lab.report.to.reference.doctor}")
	private String refernceSMSTextToDoctor;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${mail.signup..doctor.refenrence.subject}")
	private String referenceRequestSubject;

	@Value(value = "${jasper.print.doctor.lab.fileName}")
	private String doctorLabA4FileName;

	private static Logger logger = Logger.getLogger(DoctorLabServiceImpl.class.getName());

	@Override
	public DoctorLabReport addDoctorLabReport(DoctorLabReport request) {
		DoctorLabReport response = null;
		try {
			UserCollection doctor = null;
			DoctorLabReportCollection doctorLabReportCollection = new DoctorLabReportCollection();
			LabPrintSetting labPrintSetting = labPrintServices.getLabPrintSetting(request.getUploadedByLocationId(),
					request.getUploadedByHospitalId());
			for (RecordsFile file : request.getRecordsFiles()) {
				if (file.getPdfInImgs() != null && labPrintSetting != null) {
					file.setRecordsUrl(createJasperReport(labPrintSetting, file.getPdfInImgs()));
				}
				file.setRecordsUrl(file.getRecordsUrl().replace(imagePath, ""));
				file.setThumbnailUrl(file.getThumbnailUrl().replace(imagePath, ""));
				file.setPdfInImgs(null);
			}

			BeanUtil.map(request, doctorLabReportCollection);

			LocationCollection locationCollection = locationRepository
					.findById(doctorLabReportCollection.getUploadedByLocationId()).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No Lab found with UploadedByLocationId");
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorId())) {
				doctor = userRepository.findById(doctorLabReportCollection.getDoctorId()).orElse(null);
				if (doctor == null) {
					throw new BusinessException(ServiceError.NoRecord, " Doctor not found with doctorId");
				}
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				DoctorLabReportCollection oldDoctorLabReportCollection = doctorLabReportRepository
						.findById(doctorLabReportCollection.getId()).orElse(null);
				if (oldDoctorLabReportCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "No record found");
				}

				if (request.getCreatedTime() == null) {
					doctorLabReportCollection.setCreatedTime(oldDoctorLabReportCollection.getCreatedTime());
				}
				doctorLabReportCollection.setUpdatedTime(new Date());
				doctorLabReportCollection.setUniqueReportId(oldDoctorLabReportCollection.getUniqueReportId());
				doctorLabReportCollection.setAdminCreatedTime(oldDoctorLabReportCollection.getAdminCreatedTime());
				doctorLabReportCollection.setCreatedBy(oldDoctorLabReportCollection.getCreatedBy());
			} else {
				if (doctor != null) {
					doctorLabReportCollection.setDoctorMobileNumber(doctor.getMobileNumber());
					doctorLabReportCollection.setDoctorName(doctor.getFirstName());
				}
				if (!DPDoctorUtils.anyStringEmpty(request.getUploadedByDoctorId())) {
					UserCollection userCollection = userRepository
							.findById(new ObjectId(request.getUploadedByDoctorId())).orElse(null);
					if (userCollection == null) {
						throw new BusinessException(ServiceError.NoRecord, "No Doctor found by uploadedBydoctorId");
					}

					doctorLabReportCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle()
									: "DR.") + " " + userCollection.getFirstName());
				}
				if (request.getCreatedTime() == null) {
					doctorLabReportCollection.setCreatedTime(new Date());
				}
				doctorLabReportCollection.setUniqueReportId(
						UniqueIdInitial.DOCTOR_LAB_REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
				doctorLabReportCollection.setAdminCreatedTime(new Date());
				doctorLabReportCollection.setUpdatedTime(new Date());
			}
			doctorLabReportCollection = doctorLabReportRepository.save(doctorLabReportCollection);
			if (doctorLabReportCollection.getShareWithDoctor()) {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorMobileNumber())) {
					sendSmsTodoctor(doctorLabReportCollection.getDoctorName(),
							doctorLabReportCollection.getDoctorMobileNumber(),
							doctorLabReportCollection.getRecordsLabel(), locationCollection.getLocationName(),
							doctorLabReportCollection.getUploadedByDoctorId(),
							doctorLabReportCollection.getUploadedByLocationId(),
							doctorLabReportCollection.getUploadedByHospitalId(),
							doctorLabReportCollection.getDoctorId());
					if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorId())) {
						pushNotificationServices.notifyUser(doctorLabReportCollection.getDoctorId().toString(),
								locationCollection.getLocationName() + "Lab has shared report "
										+ doctorLabReportCollection.getRecordsLabel() + " with you - Tap to view it!",
								ComponentType.DOCTOR_LAB_REPORTS.getType(),
								doctorLabReportCollection.getId().toString(), null);
					}
				}
			}
			if (doctorLabReportCollection.getShareWithPatient()) {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getMobileNumber())) {
					sendSmsToPatient(doctorLabReportCollection.getPatientName(), locationCollection.getLocationName(),
							doctorLabReportCollection.getMobileNumber(), doctorLabReportCollection.getRecordsLabel(),
							doctorLabReportCollection.getUploadedByDoctorId(),
							doctorLabReportCollection.getUploadedByLocationId(),
							doctorLabReportCollection.getUploadedByHospitalId(),
							doctorLabReportCollection.getPatientId());
				}
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getPatientId())) {
					pushNotificationServices.notifyUser(doctorLabReportCollection.getPatientId().toString(),
							locationCollection.getLocationName() + "Lab has shared report "
									+ doctorLabReportCollection.getRecordsLabel() + " with you - Tap to view it!",
							ComponentType.DOCTOR_LAB_REPORTS.getType(), doctorLabReportCollection.getId().toString(),
							null);
				}
			}
			response = new DoctorLabReport();
			BeanUtil.map(doctorLabReportCollection, response);
			for (RecordsFile file : response.getRecordsFiles()) {
				file.setRecordsUrl(getFinalImageURL(file.getRecordsUrl()));
				file.setThumbnailUrl(getFinalImageURL(file.getThumbnailUrl()));
				file.setPdfInImgs(null);
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while add Doctor Lab Report");
		}

		return response;
	}

	@Override
	public RecordsFile uploadDoctorLabReport(DoctorLabReportUploadRequest request) {

		RecordsFile recordsFile = null;
		try {
			Date createdTime = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
			}
			FileDetails fileDetails = request.getFileDetails();
			recordsFile = new RecordsFile();
			createdTime = new Date();
			String path = "doctorLabReports" + File.separator
					+ (!DPDoctorUtils.anyStringEmpty(request.getPatientId()) ? request.getPatientId() : "unknown");

			String fileName = fileDetails.getFileName().replaceFirst("." + fileDetails.getFileExtension(), "");
			String recordPath = path + File.separator + fileName + createdTime.getTime() + "."
					+ fileDetails.getFileExtension();
			String recordfileLabel = fileName;
			Double fileSizeInMB = fileManager.saveRecordBase64(fileDetails, recordPath);

			recordsFile = new RecordsFile();
			recordsFile.setFileId("file" + DPDoctorUtils.generateRandomId());
			recordsFile.setFileSizeInMB(fileSizeInMB);
			recordsFile.setRecordsUrl(getFinalImageURL(recordPath));
			recordsFile.setThumbnailUrl(
					getFinalImageURL(fileManager.saveThumbnailAndReturnThumbNailUrl(fileDetails, recordPath)));
			recordsFile.setRecordsFileLabel(recordfileLabel);
			recordsFile.setRecordsPath(path);
			recordsFile.setRecordsType(request.getRecordsType());
			if (recordsFile.getRecordsUrl().toLowerCase().contains(".pdf")) {
				List<String> imResponses = new ArrayList<String>();
				fileDetails.setFileName(fileDetails.getFileName() + new Date().getTime());
				path = "doctorLabReports" + File.separator
						+ (!DPDoctorUtils.anyStringEmpty(request.getPatientId()) ? request.getPatientId() : "unknown");
				imResponses.addAll(fileManager.convertPdfToImage(fileDetails, path, true));
				if (imResponses != null && !imResponses.isEmpty())
					recordsFile.setPdfInImgs(imResponses);
			}

		} catch (

		Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading Doctor Lab Report");

		}
		return recordsFile;

	}

	@Override
	public RecordsFile uploadDoctorLabReportMultipart(FormDataBodyPart file, MyFiileRequest request) {
		RecordsFile recordsFile = null;
		try {
			Date createdTime = new Date();
			Double fileSizeInMB = 0.0;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
			}

			if (file != null) {
				String path = "doctorLabReports" + File.separator
						+ (!DPDoctorUtils.anyStringEmpty(request.getPatientId()) ? request.getPatientId() : "unknown");
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				String recordfileLabel = fileName;

				fileSizeInMB = fileManager.saveRecord(file, recordPath, fileSizeInMB, false);

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
			throw new BusinessException(ServiceError.Unknown, "error while uploading Doctor Lab Report Multipart");

		}
		return recordsFile;

	}

	@Override
	public List<DoctorLabReportResponse> getDoctorLabReport(long page, int size, String patientId, String doctorId,
			String locationId, String hospitalId, String searchTerm, Boolean discarded, Boolean doctorLab) {
		List<DoctorLabReportResponse> response = null;
		ProjectionOperation projectList = null;
		try {
			Criteria criteria = new Criteria();
			if (doctorLab) {
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("uploadedByDoctorId").is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("uploadedByLocationId").is(new ObjectId(locationId));
				}

				if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
					criteria = criteria.and("uploadedByHospitalId").is(new ObjectId(hospitalId));
				}
				if (!DPDoctorUtils.anyStringEmpty(patientId)) {
					criteria = criteria.and("patientId").is(new ObjectId(patientId));
				}

				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(new Criteria("location.locationName").regex("^" + searchTerm, "i"),
							new Criteria("location.locationName").regex("^" + searchTerm),
							new Criteria("doctorName").regex("^" + searchTerm, "i"),
							new Criteria("doctorName").regex("^" + searchTerm),
							new Criteria("patientName").regex("^" + searchTerm, "i"),
							new Criteria("patientName").regex("^" + searchTerm));

				}

			} else {
				if (!DPDoctorUtils.anyStringEmpty(patientId)) {
					criteria = criteria.and("patientId").is(new ObjectId(patientId));
				}

				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("locationId").is(new ObjectId(locationId));
				}
				if (!DPDoctorUtils.anyStringEmpty(locationId)) {
					criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
				}
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(
							new Criteria("uploadedByLocation.locationName").regex("^" + searchTerm, "i"),
							new Criteria("uploadedByLocation.locationName").regex("^" + searchTerm),
							new Criteria("uploadedByDoctor.firstName").regex("^" + searchTerm, "i"),
							new Criteria("uploadedByDoctor.firstName").regex("^" + searchTerm),
							new Criteria("patientName").regex("^" + searchTerm, "i"),
							new Criteria("patientName").regex("^" + searchTerm));
				}
				criteria = criteria.and("shareWithDoctor").is(true);

			}
			if (discarded != null) {
				criteria = criteria.and("discarded").is(discarded);
			}
			projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("uniqueReportId", "$uniqueReportId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("recordsFiles", "$recordsFiles"),
					Fields.field("recordsLabel", "$recordsLabel"), Fields.field("explanation", "$explanation"),
					Fields.field("patientName", "$patientName"), Fields.field("patientId", "$patientId"),
					Fields.field("mobileNumber", "$mobileNumber"),
					Fields.field("shareWithPatient", "$shareWithPatient"),
					Fields.field("shareWithDoctor", "$shareWithDoctor"),
					Fields.field("locationName", "$location.locationName"),
					Fields.field("uploadedByLocationName", "$uploadedByLocation.locationName"),
					Fields.field("uploadedByDoctorName", "$uploadedByDoctor.firstName"),
					Fields.field("uploadedByDoctorId", "$uploadedByDoctorId"),
					Fields.field("uploadedByLocationId", "$uploadedByLocationId"),
					Fields.field("doctorName", "$doctorName"),
					Fields.field("doctorMobileNumber", "$doctorMobileNumber"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("adminCreatedTime", "$adminCreatedTime"),
					Fields.field("uploadedByHospitalId", "$uploadedByHospitalId")));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
						Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
						Aggregation.unwind("uploadedByDoctor"), Aggregation.unwind("uploadedByLocation"),
						Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
						Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),

						Aggregation.unwind("uploadedByDoctor"), Aggregation.unwind("uploadedByLocation"),
						Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<DoctorLabReportResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorLabReportCollection.class, DoctorLabReportResponse.class);
			response = aggregationResults.getMappedResults();
			for (DoctorLabReportResponse doctorLabReportResponse : response) {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportResponse.getPatientId())) {
					PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalIdAndDiscarded(
							new ObjectId(doctorLabReportResponse.getPatientId()), new ObjectId(locationId),
							new ObjectId(hospitalId), false);
					if (patientCollection != null) {
						doctorLabReportResponse.setPatientRegistered(true);
					}

				}

				if (doctorLabReportResponse.getRecordsFiles() != null) {
					for (RecordsFile recordsFile : doctorLabReportResponse.getRecordsFiles()) {

						if (!DPDoctorUtils.anyStringEmpty(recordsFile.getRecordsUrl())) {
							recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
						}
						if (!DPDoctorUtils.anyStringEmpty(recordsFile.getThumbnailUrl())) {
							recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
						}
					}
				}

			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while getting Doctor Lab Report");

		}
		return response;

	}

	@Override
	public DoctorLabReportResponse getDoctorLabReportById(String id) {
		DoctorLabReportResponse response = null;
		ProjectionOperation projectList = null;
		try {
			Criteria criteria = new Criteria("id").is(new ObjectId(id));

			projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("uniqueReportId", "$uniqueReportId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("recordsFiles", "$recordsFiles"),
					Fields.field("recordsLabel", "$recordsLabel"), Fields.field("explanation", "$explanation"),
					Fields.field("patientName", "$patientName"), Fields.field("patientId", "$patientId"),
					Fields.field("mobileNumber", "$mobileNumber"),
					Fields.field("shareWithPatient", "$shareWithPatient"),
					Fields.field("shareWithDoctor", "$shareWithDoctor"),
					Fields.field("locationName", "$location.locationName"),
					Fields.field("uploadedByLocationName", "$uploadedByLocation.locationName"),
					Fields.field("uploadedByDoctorName", "$uploadedByDoctor.firstName"),
					Fields.field("uploadedByDoctorId", "$uploadedByDoctorId"),
					Fields.field("uploadedByLocationId", "$uploadedByLocationId"),
					Fields.field("doctorName", "$doctorName"),
					Fields.field("doctorMobileNumber", "$doctorMobileNumber"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("adminCreatedTime", "$adminCreatedTime"),
					Fields.field("uploadedByHospitalId", "$uploadedByHospitalId")));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
					Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$location").append("preserveNullAndEmptyArrays", true))),
					Aggregation.unwind("uploadedByDoctor"), Aggregation.unwind("uploadedByLocation"),
					Aggregation.match(criteria), projectList);

			AggregationResults<DoctorLabReportResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorLabReportCollection.class, DoctorLabReportResponse.class);
			response = aggregationResults.getUniqueMappedResult();

			if (response.getRecordsFiles() != null) {
				for (RecordsFile recordsFile : response.getRecordsFiles()) {

					if (!DPDoctorUtils.anyStringEmpty(recordsFile.getRecordsUrl())) {
						recordsFile.setRecordsUrl(getFinalImageURL(recordsFile.getRecordsUrl()));
					}
					if (!DPDoctorUtils.anyStringEmpty(recordsFile.getThumbnailUrl())) {
						recordsFile.setThumbnailUrl(getFinalImageURL(recordsFile.getThumbnailUrl()));
					}
				}
			}

		} catch (

		Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while getting Doctor Lab Report");

		}
		return response;

	}

	@Override
	public Boolean addDoctorToFavouriteList(DoctorLabFavouriteDoctorRequest request) {
		Boolean response = false;

		try {
			DoctorLabFavouriteDoctorCollection favouriteDoctorCollection = null;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {

				UserCollection fevDoctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				if (fevDoctor == null) {
					throw new BusinessException(ServiceError.NoRecord, "user not Found");
				}
				Criteria criteria = new Criteria("doctorId").is(new ObjectId(request.getDoctorId())).and("locationId")
						.is(new ObjectId(request.getLocationId())).and("hospitalId")
						.is(new ObjectId(request.getHospitalId())).and("favouriteDoctorId")
						.is(new ObjectId(request.getFavouriteDoctorId())).and("favouriteLocationId")
						.is(new ObjectId(request.getFavouriteDoctorId())).and("favouriteHospitalId")
						.is(new ObjectId(request.getFavouriteHospitalId()));
				favouriteDoctorCollection = mongoTemplate.findById(new Query(criteria),
						DoctorLabFavouriteDoctorCollection.class);
				if (favouriteDoctorCollection == null) {
					favouriteDoctorCollection = new DoctorLabFavouriteDoctorCollection();
					BeanUtil.map(request, favouriteDoctorCollection);
					favouriteDoctorCollection.setAdminCreatedTime(new Date());
					favouriteDoctorCollection.setCreatedTime(new Date());
					favouriteDoctorCollection.setUpdatedTime(new Date());
					favouriteDoctorCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(fevDoctor.getTitle()) ? fevDoctor.getTitle() : "DR.") + " "
									+ fevDoctor.getFirstName());
					favouriteDoctorCollection = doctorLabFevouriteDoctorRepository.save(favouriteDoctorCollection);

				}
			}
			response = true;
		} catch (

		Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while adding fevourite Doctor ");

		}
		return response;
	}

	@Override
	public List<DoctorLabFavouriteDoctorResponse> getFavouriteList(int size, long page, String searchTerm,
			String doctorId, String locationId, String hospitalId, String city) {
		List<DoctorLabFavouriteDoctorResponse> response = null;
		ProjectionOperation projectList = null;
		try {

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(city)) {
				criteria = criteria.and("location.city").regex(city);
			}
			criteria = criteria.and("discarded").is(false);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm));

			}

			projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("locationId", "$location._id"), Fields.field("hospitalId", "$hospital._id"),
					Fields.field("doctorId", "$doctor._id"), Fields.field("discarded", "$discarded"),
					Fields.field("doctorName", "$doctor.firstName"),
					Fields.field("locationName", "$location.locationName"), Fields.field("city", "$location.city")));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "favouriteDoctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "favouriteLocationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "favouriteHospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "doctorName")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("user_cl", "favouriteDoctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "favouriteLocationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("hospital_cl", "favouriteHospitalId", "_id", "hospital"),
						Aggregation.unwind("hospital"), Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "doctorName")));

			}

			AggregationResults<DoctorLabFavouriteDoctorResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DoctorLabFavouriteDoctorCollection.class, DoctorLabFavouriteDoctorResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while getting Fevourite Doctor ");
		}
		return response;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<DoctorLabSearchDoctorResponse> searchDoctor(int size, int page, String searchTerm, String doctorId,
			String locationId, String hospitalId, String speciality, String city) {
		List<DoctorLabSearchDoctorResponse> response = null;
		DoctorLabSearchDoctorResponse doctorSearchResponse = null;
		DoctorLabFavouriteDoctorCollection fevDoctorCollection = null;
		Criteria criteria = null;
		try {
			List<ESDoctorDocument> doctorDocuments = null;
			String latitude = null, longitude = null;
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("isDoctorListed", true));

			if (!DPDoctorUtils.anyStringEmpty(city)) {
				city = city.trim().replace("-", " ");
				ESCityDocument esCityDocument = esCityRepository.findByName(city);
				if (esCityDocument != null) {
					latitude = esCityDocument.getLatitude() + "";
					longitude = esCityDocument.getLongitude() + "";
				}
			}

			Set<String> specialityIdSet = null;
			if (!DPDoctorUtils.anyStringEmpty(speciality)) {
				List<ESSpecialityDocument> esSpecialityDocuments = esSpecialityRepository
						.findByQueryAnnotation(speciality);
				specialityIdSet = new HashSet<String>();
				if (esSpecialityDocuments != null && !esSpecialityDocuments.isEmpty()) {
					Collection<String> specialityIds = CollectionUtils.collect(esSpecialityDocuments,
							new BeanToPropertyValueTransformer("id"));
					if (specialityIds != null) {
						specialityIdSet.addAll(specialityIds);
					}
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("firstName", searchTerm));
			}
			if (!DPDoctorUtils.anyStringEmpty(latitude) && !DPDoctorUtils.anyStringEmpty(longitude)) {
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
						Double.parseDouble(longitude)).distance(30 + "km"));

			}

			if (specialityIdSet != null && !specialityIdSet.isEmpty()) {
				boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIdSet));
			}

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("firstName").order(SortOrder.ASC))
						.withPageable(PageRequest.of(page, size)).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("firstName").order(SortOrder.ASC)).build();

			doctorDocuments = elasticsearchTemplate.queryForList(searchQuery, ESDoctorDocument.class);
			if (doctorDocuments != null && !doctorDocuments.isEmpty()) {
				response = new ArrayList<DoctorLabSearchDoctorResponse>();

				for (ESDoctorDocument doctorDocument : doctorDocuments) {
					doctorSearchResponse = new DoctorLabSearchDoctorResponse();
					doctorSearchResponse.setCity(doctorDocument.getCity());

					doctorSearchResponse.setDoctorId(doctorDocument.getUserId());
					doctorSearchResponse.setLocationId(doctorDocument.getLocationId());
					doctorSearchResponse.setHospitalId(doctorDocument.getHospitalId());
					doctorSearchResponse.setDoctorName(doctorDocument.getFirstName());
					doctorSearchResponse.setLocationName(doctorDocument.getLocationName());
					doctorSearchResponse.setSpecialities(doctorDocument.getSpecialities());
					if (!DPDoctorUtils.anyStringEmpty(doctorId) && !DPDoctorUtils.anyStringEmpty(locationId)
							&& !DPDoctorUtils.anyStringEmpty(hospitalId)
							&& !DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getDoctorId())
							&& !DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getLocationId())
							&& !DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getHospitalId())) {
						criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
								.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
								.and("favouriteDoctorId").is(new ObjectId(doctorSearchResponse.getDoctorId()))
								.and("favouriteLocationId").is(new ObjectId(doctorSearchResponse.getLocationId()))
								.and("favouriteHospitalId").is(new ObjectId(doctorSearchResponse.getHospitalId()))
								.and("discarded").is(false);
						fevDoctorCollection = mongoTemplate.findById(new Query(criteria),
								DoctorLabFavouriteDoctorCollection.class);
						if (fevDoctorCollection != null) {
							doctorSearchResponse.setIsFavourite(true);
						}
					}
					response.add(doctorSearchResponse);
				}
			}

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while search Doctor for Doctor Lab");
		}
		return response;
	}

	@Override
	public Boolean addDoctorReference(DoctorLabDoctorReferenceRequest request) {
		Boolean response = false;
		try {
			DoctorLabDoctorReferenceCollection referenceCollection = new DoctorLabDoctorReferenceCollection();
			BeanUtil.map(request, referenceCollection);
			LocationCollection locationCollection = locationRepository.findById(referenceCollection.getLocationId()).orElse(null);
			UserCollection userCollection = userRepository.findById(referenceCollection.getDoctorId()).orElse(null);
			if (userCollection == null && locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "User not found");
			}
			referenceCollection.setCreatedBy(
					(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "DR.") + " "
							+ userCollection.getFirstName());
			referenceCollection.setCreatedTime(new Date());
			referenceCollection.setAdminCreatedTime(new Date());
			referenceCollection = doctorLabDoctorReferenceRepository.save(referenceCollection);

			SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
			smsTrackDetail.setDoctorId(referenceCollection.getDoctorId());
			smsTrackDetail.setHospitalId(referenceCollection.getHospitalId());
			smsTrackDetail.setLocationId(referenceCollection.getLocationId());
			smsTrackDetail.setType(ComponentType.DOCTOR_REFERNCE.getType());
			SMSDetail smsDetail = new SMSDetail();
			smsDetail.setUserName(referenceCollection.getFirstName());
			SMS sms = new SMS();
			String message = refernceSMSTextToDoctor;
			sms.setSmsText(message.replace("{doctorName}", referenceCollection.getFirstName()).replace("{labName}",
					locationCollection.getLocationName()));
			SMSAddress smsAddress = new SMSAddress();
			smsAddress.setRecipient(referenceCollection.getMobileNumber());
			sms.setSmsAddress(smsAddress);
			smsDetail.setSms(sms);
			smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
			List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
			smsDetails.add(smsDetail);
			smsTrackDetail.setSmsDetails(smsDetails);
			smsServices.sendSMS(smsTrackDetail, true);

			String body = mailBodyGenerator.generateDoctorReferenceEmailBody(referenceCollection.getFirstName(),
					referenceCollection.getMobileNumber(), referenceCollection.getLocationName(),
					locationCollection.getLocationName());
			mailService.sendEmail(mailTo,
					referenceRequestSubject.replace("{labName}", locationCollection.getLocationName()), body, null);

			response = true;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while refer Doctor ");
		}
		return response;
	}

	@Override
	public Boolean updateShareWithDoctor(String reportId) {
		Boolean response = false;
		try {
			DoctorLabReportCollection doctorLabReportCollection = doctorLabReportRepository
					.findById(new ObjectId(reportId)).orElse(null);
			if (doctorLabReportCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No report fount with reportId");
			}
			if (doctorLabReportCollection.getShareWithDoctor() == null) {
				{
					doctorLabReportCollection.setShareWithDoctor(true);
				}
			} else {
				doctorLabReportCollection.setShareWithDoctor(!doctorLabReportCollection.getShareWithDoctor());
			}
			LocationCollection locationCollection = locationRepository
					.findById(doctorLabReportCollection.getUploadedByLocationId()).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No Lab found with UploadedByLocationId");
			}
			doctorLabReportCollection.setUpdatedTime(new Date());
			doctorLabReportRepository.save(doctorLabReportCollection);
			if (doctorLabReportCollection.getShareWithDoctor()) {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorMobileNumber())) {
					sendSmsTodoctor(doctorLabReportCollection.getDoctorName(),
							doctorLabReportCollection.getDoctorMobileNumber(),
							doctorLabReportCollection.getRecordsLabel(), locationCollection.getLocationName(),
							doctorLabReportCollection.getUploadedByDoctorId(),
							doctorLabReportCollection.getUploadedByLocationId(),
							doctorLabReportCollection.getUploadedByHospitalId(),
							doctorLabReportCollection.getDoctorId());
					if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorId())) {
						pushNotificationServices.notifyUser(doctorLabReportCollection.getDoctorId().toString(),
								locationCollection.getLocationName() + "Lab has shared report "
										+ doctorLabReportCollection.getRecordsLabel() + " with you - Tap to view it!",
								ComponentType.DOCTOR_LAB_REPORTS.getType(),
								doctorLabReportCollection.getId().toString(), null);
					}
				}
			} else {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getDoctorId())) {
					pushNotificationServices.notifyUser(doctorLabReportCollection.getDoctorId().toString(), "",
							ComponentType.REFRESH_DOCTOR_LAB_REPORTS.getType(),
							doctorLabReportCollection.getId().toString(), null);
				}

			}
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while share with doctor");
		}
		return response;
	}

	@Override
	public Boolean updateShareWithPatient(String reportId) {
		Boolean response = false;
		try {
			DoctorLabReportCollection doctorLabReportCollection = doctorLabReportRepository
					.findById(new ObjectId(reportId)).orElse(null);
			if (doctorLabReportCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No report fount with reportId");
			}
			if (doctorLabReportCollection.getShareWithPatient() == null) {
				{
					doctorLabReportCollection.setShareWithPatient(true);
				}
			} else {
				doctorLabReportCollection.setShareWithPatient(!doctorLabReportCollection.getShareWithPatient());
			}
			LocationCollection locationCollection = locationRepository
					.findById(doctorLabReportCollection.getUploadedByLocationId()).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No Lab found with UploadedByLocationId");
			}
			doctorLabReportCollection.setUpdatedTime(new Date());
			doctorLabReportRepository.save(doctorLabReportCollection);
			if (doctorLabReportCollection.getShareWithPatient()) {
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getMobileNumber())) {
					sendSmsToPatient(doctorLabReportCollection.getPatientName(), locationCollection.getLocationName(),
							doctorLabReportCollection.getMobileNumber(), doctorLabReportCollection.getRecordsLabel(),
							doctorLabReportCollection.getUploadedByDoctorId(),
							doctorLabReportCollection.getUploadedByLocationId(),
							doctorLabReportCollection.getUploadedByHospitalId(),
							doctorLabReportCollection.getPatientId());
				}
				if (!DPDoctorUtils.anyStringEmpty(doctorLabReportCollection.getPatientId())) {
					pushNotificationServices.notifyUser(doctorLabReportCollection.getPatientId().toString(),
							locationCollection.getLocationName() + "Lab has shared report "
									+ doctorLabReportCollection.getRecordsLabel() + " with you - Tap to view it!",
							ComponentType.DOCTOR_LAB_REPORTS.getType(), doctorLabReportCollection.getId().toString(),
							null);
				}
			}
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while share with patient");
		}
		return response;
	}

	@Override
	public Boolean DiscardFavouriteDoctor(String id) {
		Boolean response = false;
		try {
			DoctorLabFavouriteDoctorCollection favouriteDoctorCollection = doctorLabFevouriteDoctorRepository
					.findById(new ObjectId(id)).orElse(null);
			if (favouriteDoctorCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No Fevourite Doctor found with Id");
			}

			favouriteDoctorCollection.setDiscarded(!favouriteDoctorCollection.getDiscarded());

			favouriteDoctorCollection.setUpdatedTime(new Date());
			doctorLabFevouriteDoctorRepository.save(favouriteDoctorCollection);
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while discarding Favourite Doctor");
		}
		return response;
	}

	@Override
	public Boolean DiscardDoctorLabReports(String reportId) {
		Boolean response = false;
		try {
			DoctorLabReportCollection doctorLabReportCollection = doctorLabReportRepository
					.findById(new ObjectId(reportId)).orElse(null);
			if (doctorLabReportCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No report found with reportId");
			}
			doctorLabReportCollection.setDiscarded(!doctorLabReportCollection.getDiscarded());
			doctorLabReportCollection.setUpdatedTime(new Date());
			doctorLabReportRepository.save(doctorLabReportCollection);
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while discarding  Doctor lab report");
		}
		return response;
	}

	private void sendSmsToPatient(String patientName, String locationName, String patientMobileNumber,
			String recordName, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId patientId) {

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(doctorId);
		smsTrackDetail.setHospitalId(hospitalId);
		smsTrackDetail.setLocationId(locationId);
		smsTrackDetail.setType(ComponentType.DOCTOR_LAB_REPORTS.getType());
		SMSDetail smsDetail = new SMSDetail();
		if (!DPDoctorUtils.anyStringEmpty(patientId))
			smsDetail.setUserId(patientId);
		smsDetail.setUserName(patientName);
		SMS sms = new SMS();
		if (DPDoctorUtils.anyStringEmpty(recordName))
			recordName = "";
		String message = patientSMSText;
		sms.setSmsText(message.replace("{patientName}", patientName).replace("{labName}", locationName)
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

	private void sendSmsTodoctor(String doctorName, String mobileNumber, String recordName, String labName,
			ObjectId uploadedBydoctorId, ObjectId uploadedBylocationId, ObjectId uploadedByhospitalId,
			ObjectId doctorId) {

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(uploadedBydoctorId);
		smsTrackDetail.setHospitalId(uploadedByhospitalId);
		smsTrackDetail.setLocationId(uploadedBylocationId);
		smsTrackDetail.setType(ComponentType.DOCTOR_LAB_REPORTS.getType());
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(doctorId);
		smsDetail.setUserName(doctorName);
		SMS sms = new SMS();
		if (DPDoctorUtils.anyStringEmpty(recordName))
			recordName = "";
		String message = doctorSMSText;
		sms.setSmsText(message.replace("{doctorName}", doctorName).replace("{labName}", labName).replace("{reportName}",
				recordName));
		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);
		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		smsServices.sendSMS(smsTrackDetail, true);
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	public String downloadReport() {
		String response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse reportResponse = null;

		parameters.put("headerImg", "/home/harish/keys/NRPL&NABLLETTEHEAD-18.png");

		parameters.put("footerImg", "/home/harish/keys/NRPL&NABLLETTERFOOTER18.png");
		parameters.put("headerHeight", 60);

		parameters.put("footerHeight", 60);

		List<DBObject> items = new ArrayList<DBObject>();
		DBObject item = new BasicDBObject();
		item.put("item", "/home/harish/keys/1803136_1-1.jpg");
		items.add(item);
		parameters.put("items", items);

		patientVisitService.generatePrintSetup(parameters, null, null);

		String pdfName = "doctorLab-" + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 0;
		Integer bottonMargin = 0;
		Integer leftMargin = 0;
		Integer rightMargin = 0;
		try {
			reportResponse = jasperReportService.createPDF(ComponentType.DOCTOR_LAB_REPORTS, parameters,
					doctorLabA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reportResponse != null)
			response =

					getFinalImageURL(reportResponse.getPath());
		if (reportResponse != null && reportResponse.getFileSystemResource() != null) {
			if (reportResponse.getFileSystemResource().getFile().exists())
				reportResponse.getFileSystemResource().getFile().delete();
		} else {
			logger.warn("Invoice Id does not exist");
			throw new BusinessException(ServiceError.NotFound, "Id does not exist");
		}

		return response;
	}

	public String createJasperReport(LabPrintSetting labPrintSetting, List<String> imageList) {
		String response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse reportResponse = null;
		int height = 0;

		if (labPrintSetting.getHeaderSetup() != null) {
			parameters.put("headerImg",
					!DPDoctorUtils.anyStringEmpty(labPrintSetting.getHeaderSetup().getImageurl())
							? labPrintSetting.getHeaderSetup().getImageurl()
							: null);
			height = labPrintSetting.getHeaderSetup().getHeight();
		}
		parameters.put("headerHeight", height);
		height = 0;
		if (labPrintSetting.getFooterSetup() != null) {
			parameters.put("footerImg",
					!DPDoctorUtils.anyStringEmpty(labPrintSetting.getFooterSetup().getImageurl())
							? labPrintSetting.getFooterSetup().getImageurl()
							: null);
			height = labPrintSetting.getFooterSetup().getHeight();
		}

		parameters.put("footerHeight", height);
		if (imageList != null && !imageList.isEmpty()) {
			List<DBObject> items = new ArrayList<DBObject>();
			DBObject item = null;
			for (String image : imageList) {
				item = new BasicDBObject();
				item.put("item", image);
				items.add(item);
			}
			parameters.put("items", items);

		}
		patientVisitService.generatePrintSetup(parameters, null, null);
		String pdfName = "doctorLab-" + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 0;
		Integer bottonMargin = 0;
		Integer leftMargin = 0;
		Integer rightMargin = 0;
		try {
			reportResponse = jasperReportService.createPDF(ComponentType.DOCTOR_LAB_REPORTS, parameters,
					doctorLabA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (reportResponse != null && reportResponse.getFileSystemResource() != null) {
			if (reportResponse.getFileSystemResource().getFile().exists())
				reportResponse.getFileSystemResource().getFile().delete();
		} else {
			logger.warn("Invoice Id does not exist");
			throw new BusinessException(ServiceError.NotFound, "Id does not exist");
		}

		if (reportResponse == null)
			return response;
		else {
			return reportResponse.getPath();
		}
	}

}
