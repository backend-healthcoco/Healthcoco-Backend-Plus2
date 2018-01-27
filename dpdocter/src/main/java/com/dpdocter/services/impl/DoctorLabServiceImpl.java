package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
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
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.collections.DoctorLabDoctorReferenceCollection;
import com.dpdocter.collections.DoctorLabFavouriteDoctorCollection;
import com.dpdocter.collections.DoctorLabReportCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorLabFevouriteDoctorRepository;
import com.dpdocter.repository.DoctorLabReportRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorLabDoctorReferenceRequest;
import com.dpdocter.request.DoctorLabFavouriteDoctorRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.response.DoctorLabFavouriteDoctorResponse;
import com.dpdocter.response.DoctorLabReportResponse;
import com.dpdocter.response.DoctorLabSearchDoctorResponse;
import com.dpdocter.services.DoctorLabService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PushNotificationServices;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
@Transactional
public class DoctorLabServiceImpl implements DoctorLabService {

	@Autowired
	private DoctorLabReportRepository doctorLabReportRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UserRepository userRepository;

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

	@Value(value = "${doctor.welcome.message}")
	private String doctorWelcomeMessage;

	@Value(value = "${mail.signup.request.to}")
	private String mailTo;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${mail.contact.us.welcome.subject}")
	private String doctorWelcomeSubject;

	@Value(value = "${mail.signup.request.subject}")
	private String signupRequestSubject;

	@Value(value = "${image.path}")
	private String imagePath;

	private static Logger logger = Logger.getLogger(DoctorLabServiceImpl.class.getName());

	@Override
	public DoctorLabReport addDoctorLabReport(DoctorLabReport request) {
		DoctorLabReport response = null;
		try {
			DoctorLabReportCollection doctorLabReportCollection = new DoctorLabReportCollection();
			for (RecordsFile file : request.getRecordsFiles()) {
				file.setRecordsUrl(file.getRecordsUrl().replace(imagePath, ""));
				file.setThumbnailUrl(file.getThumbnailUrl().replace(imagePath, ""));

			}
			BeanUtil.map(request, doctorLabReportCollection);
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				DoctorLabReportCollection oldDoctorLabReportCollection = doctorLabReportRepository
						.findOne(doctorLabReportCollection.getId());
				if (oldDoctorLabReportCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "No record found");
				}

				if (request.getCreatedTime() == null) {
					doctorLabReportCollection.setCreatedTime(oldDoctorLabReportCollection.getCreatedTime());
				}
				doctorLabReportCollection.setUpdatedTime(new Date());
				doctorLabReportCollection.setAdminCreatedTime(oldDoctorLabReportCollection.getAdminCreatedTime());
				doctorLabReportCollection.setCreatedBy(oldDoctorLabReportCollection.getCreatedBy());
			} else {
				if (!DPDoctorUtils.anyStringEmpty(request.getUploadedByDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(new ObjectId(request.getUploadedByDoctorId()));
					if (userCollection == null) {
						throw new BusinessException(ServiceError.NoRecord, "No Doctor found by uploadedBydoctorId");
					}

					doctorLabReportCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
							? userCollection.getTitle() : "DR.") + " " + userCollection.getFirstName());
				}
				if (request.getCreatedTime() == null) {
					doctorLabReportCollection.setCreatedTime(new Date());
				}
				doctorLabReportCollection.setAdminCreatedTime(new Date());
				doctorLabReportCollection.setUpdatedTime(new Date());
			}
			doctorLabReportCollection = doctorLabReportRepository.save(doctorLabReportCollection);
			response = new DoctorLabReport();
			BeanUtil.map(doctorLabReportCollection, response);
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
			FileDetails fileDetails = request.getFileDetails();

			Date createdTime = new Date();

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getPatientId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
			}

			if (fileDetails != null) {
				String path = "doctorLabReports" + File.separator
						+ (!DPDoctorUtils.anyStringEmpty(request.getPatientId()) ? request.getPatientId() : "unknown");

				String fileName = fileDetails.getFileName().replaceFirst("." + fileDetails.getFileExtension(), "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "."
						+ fileDetails.getFileExtension();
				String recordfileLabel = fileName;
				Double fileSizeInMB = 0.0;

				recordsFile = new RecordsFile();
				recordsFile.setFileId("file" + DPDoctorUtils.generateRandomId());
				recordsFile.setFileSizeInMB(fileSizeInMB);
				recordsFile.setRecordsUrl(recordPath);
				recordsFile.setThumbnailUrl(fileManager.saveThumbnailAndReturnThumbNailUrl(fileDetails, recordPath));
				recordsFile.setRecordsFileLabel(recordfileLabel);
				recordsFile.setRecordsPath(path);
				recordsFile.setRecordsType(request.getRecordsType());

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}

		} catch (Exception e) {
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
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getPatientId()));
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
	public List<DoctorLabReportResponse> getDoctorLabReport(int page, int size, String patientId, String doctorId,
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
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(
							new Criteria("uploadedByLocation.locationName").regex("^" + searchTerm, "i"),
							new Criteria("uploadedByLocation.locationName").regex("^" + searchTerm),
							new Criteria("uploadedByDoctor.firstName").regex("^" + searchTerm, "i"),
							new Criteria("uploadedByDoctor.firstName").regex("^" + searchTerm),
							new Criteria("patientName").regex("^" + searchTerm, "i"),
							new Criteria("patientName").regex("^" + searchTerm));

				}

			} else {

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
					Fields.field("shareWithDoctor", "$shareWithDoctor"), Fields.field("locationName", "$locationName"),
					Fields.field("uploadedByLocationName", "$uploadedByLocation.locationName"),
					Fields.field("uploadedByDoctorName", "$uploadedByDoctor.firstName"),
					Fields.field("uploadedByDoctorId", "$uploadedByDoctorId"),
					Fields.field("uploadedByLocationId", "$uploadedByLocationId"),
					Fields.field("doctorName", "$doctorName"), Fields.field("createdTime", "$createdTime"),
					Fields.field("createdBy", "$createdBy"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("adminCreatedTime", "$adminCreatedTime"),
					Fields.field("uploadedByHospitalId", "$uploadedByHospitalId")));
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
						Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
						Aggregation.unwind("doctor"), Aggregation.unwind("location"),
						Aggregation.unwind("uploadedByDoctor"), Aggregation.unwind("uploadedByLocation"),
						Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
						Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
						Aggregation.unwind("doctor"), Aggregation.unwind("location"),
						Aggregation.unwind("uploadedByDoctor"), Aggregation.unwind("uploadedByLocation"),
						Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<DoctorLabReportResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DoctorLabReportCollection.class, DoctorLabReportResponse.class);
			response = aggregationResults.getMappedResults();
			for (DoctorLabReportResponse doctorLabReportResponse : response) {
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
					Fields.field("shareWithDoctor", "$shareWithDoctor"), Fields.field("locationName", "$locationName"),
					Fields.field("uploadedByLocationName", "$uploadedByLocation.locationName"),
					Fields.field("uploadedByDoctorName", "$uploadedByDoctor.firstName"),
					Fields.field("uploadedByDoctorId", "$uploadedByDoctorId"),
					Fields.field("uploadedByLocationId", "$uploadedByLocationId"),
					Fields.field("doctorName", "$doctorName"), Fields.field("createdTime", "$createdTime"),
					Fields.field("createdBy", "$createdBy"), Fields.field("updatedTime", "$updatedTime"),
					Fields.field("adminCreatedTime", "$adminCreatedTime"),
					Fields.field("uploadedByHospitalId", "$uploadedByHospitalId")));

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
					Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
					Aggregation.unwind("doctor"), Aggregation.unwind("location"),
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

				UserCollection fevDoctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (fevDoctor == null) {
					throw new BusinessException(ServiceError.NoRecord, "user not Found");
				}
				Criteria criteria = new Criteria("doctorId").is(new ObjectId(request.getDoctorId())).and("locationId")
						.is(new ObjectId(request.getLocationId())).and("hospitalId")
						.is(new ObjectId(request.getHospitalId())).and("favouriteDoctorId")
						.is(new ObjectId(request.getFavouriteDoctorId())).and("favouriteLocationId")
						.is(new ObjectId(request.getLocationId())).and("favouriteHospitalId")
						.is(new ObjectId(request.getHospitalId()));
				favouriteDoctorCollection = mongoTemplate.findOne(new Query(criteria),
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
	public List<DoctorLabFavouriteDoctorResponse> getFavouriteList(int size, int page, String searchTerm,
			String doctorId, String locationId, String hospitalId, String speciality, String city) {
		List<DoctorLabFavouriteDoctorResponse> response = null;
		try {

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(speciality)) {
				criteria = criteria.orOperator(new Criteria("specialities.speciality").regex(searchTerm),
						new Criteria("specialities.superSpeciality").regex(searchTerm));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria = criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("location.locationName").regex("^" + searchTerm, "i"),
						new Criteria("location.locationName").regex("^" + searchTerm));

			}
			CustomAggregationOperation groupOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", "$_id")
							.append("doctorName", new BasicDBObject("$first", "$doctor.firstName"))
							.append("locationName", new BasicDBObject("$first", "$location.locationName"))
							.append("city", new BasicDBObject("$first", "$location.city"))
							.append("speciality", new BasicDBObject("$push", "$specialities.speciality"))
							.append("superSpeciality", new BasicDBObject("$push", "$specialities.speciality"))
							.append("doctorId", new BasicDBObject("$first", "$favouriteDoctorId"))
							.append("locationId", new BasicDBObject("$first", "$favouriteLocationId"))
							.append("hospitalId", new BasicDBObject("$first", "$favouriteHospitalId"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.unwind("specialities"),
						Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
						Aggregation.unwind("specialities"),
						Aggregation.lookup("user_cl", "favouriteDoctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "favouriteLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria), groupOperation,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "doctorName")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.unwind("specialities"),
						Aggregation.lookup("speciality_cl", "specialities", "_id", "specialities"),
						Aggregation.unwind("specialities"),
						Aggregation.lookup("user_cl", "favouriteDoctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "favouriteLocationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria), groupOperation,
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
				boolQueryBuilder.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude))
						.lon(Double.parseDouble(longitude)).distance(30 + "km"));

			}

			if (specialityIdSet != null && !specialityIdSet.isEmpty()) {
				boolQueryBuilder.must(QueryBuilders.termsQuery("specialities", specialityIdSet));
			}

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("firstName").order(SortOrder.ASC))
						.withPageable(new PageRequest(page, size)).build();
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
					doctorSearchResponse.setFirstName(doctorDocument.getFirstName());
					doctorSearchResponse.setLocationName(doctorDocument.getLocationName());
					doctorSearchResponse.setSpecialities(doctorDocument.getSpecialities());
					if (!DPDoctorUtils.anyStringEmpty(doctorId) && !DPDoctorUtils.anyStringEmpty(locationId)
							&& !DPDoctorUtils.anyStringEmpty(hospitalId)
							&& DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getDoctorId())
							&& !DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getLocationId())
							&& !DPDoctorUtils.anyStringEmpty(doctorSearchResponse.getHospitalId())) {
						criteria = new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
								.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
								.and("favouriteDoctorId").is(new ObjectId(doctorSearchResponse.getDoctorId()))
								.and("favouriteLocationId").is(new ObjectId(doctorSearchResponse.getLocationId()))
								.and("favouriteHospitalId").is(new ObjectId(doctorSearchResponse.getHospitalId()));
						fevDoctorCollection = mongoTemplate.findOne(new Query(criteria),
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
			UserCollection userCollection = userRepository.findOne(referenceCollection.getDoctorId());
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "User not found");
			}
			referenceCollection.setCreatedBy(
					(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() : "DR.") + " "
							+ userCollection.getFirstName());
			referenceCollection.setCreatedTime(new Date());
			referenceCollection.setAdminCreatedTime(new Date());
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
					.findOne(new ObjectId(reportId));
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
			doctorLabReportCollection.setUpdatedTime(new Date());
			doctorLabReportRepository.save(doctorLabReportCollection);
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
					.findOne(new ObjectId(reportId));
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
			doctorLabReportCollection.setUpdatedTime(new Date());
			doctorLabReportRepository.save(doctorLabReportCollection);
			response = true;

		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while share with patient");
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
