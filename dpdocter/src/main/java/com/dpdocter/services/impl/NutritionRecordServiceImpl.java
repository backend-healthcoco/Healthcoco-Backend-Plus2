package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
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

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.NutritionRecord;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.collections.NutritionRecordCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.NutritionRecordRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.NutritionRecordService;
import com.dpdocter.services.PushNotificationServices;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class NutritionRecordServiceImpl implements NutritionRecordService {
	private static Logger logger = Logger.getLogger(NutritionRecordServiceImpl.class.getName());
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private FileManager fileManager;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private NutritionRecordRepository nutritionRecordRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	public RecordsFile uploadNutritionRecord(FormDataBodyPart file, MyFiileRequest request) {
		RecordsFile recordsFile = null;
		try {

			Date createdTime = new Date();

			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId()))
						.orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}

			}
			if (file != null) {
				String path = "nuutritionRecord" + File.separator + request.getPatientId();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
				String recordfileLabel = fileName;
				Double fileSizeInMB = 0.0;

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
			throw new BusinessException(ServiceError.Unknown, "error while uploading Nutrition record");

		}
		return recordsFile;

	}

	@Override
	public NutritionRecord addNutritionRecord(NutritionRecord request) {

		NutritionRecord response = null;
		try {
			NutritionRecordCollection recordsCollection = null;
			for (RecordsFile file : request.getRecordsFiles()) {
				file.setRecordsUrl(file.getRecordsUrl().replaceAll(imagePath, ""));
				file.setThumbnailUrl(file.getThumbnailUrl().replaceAll(imagePath, ""));

			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				recordsCollection = nutritionRecordRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (recordsCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "No Nutrition Record found with Id");
				}

				request.setCreatedBy(recordsCollection.getCreatedBy());
				request.setCreatedTime(recordsCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
				request.setUniqueRecordId(recordsCollection.getUniqueRecordId());
				recordsCollection.setRecordsFiles(new ArrayList<RecordsFile>());
				BeanUtil.map(request, recordsCollection);
			} else {

				recordsCollection = new NutritionRecordCollection();
				BeanUtil.map(request, recordsCollection);
				recordsCollection.setCreatedTime(new Date());
				recordsCollection.setUniqueRecordId(
						UniqueIdInitial.NUTRITION_RECORD.getInitial() + DPDoctorUtils.generateRandomId());

				UserCollection userCollection = userRepository.findById(recordsCollection.getDoctorId()).orElse(null);

				if (userCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "No doctor found with doctorId");
				}

				recordsCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());

			}

			recordsCollection = nutritionRecordRepository.save(recordsCollection);

			response = new NutritionRecord();
			BeanUtil.map(recordsCollection, response);
			for (RecordsFile file : response.getRecordsFiles()) {
				file.setRecordsUrl(getFinalImageURL(file.getRecordsUrl()));
				file.setThumbnailUrl(getFinalImageURL(file.getThumbnailUrl()));

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public List<NutritionRecord> getNutritionRecord(int page, int size, String patientId, String doctorId,
			String locationId, String hospitalId, String searchTerm, Boolean discarded, Boolean isNutrition) {
		List<NutritionRecord> response = null;

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
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria = criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (discarded != null) {
				criteria = criteria.and("discarded").is(discarded);
			}
			if (!isNutrition) {
				criteria = criteria.and("shareWithPatient").is(true);
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<NutritionRecord> aggregationResults = mongoTemplate.aggregate(aggregation,
					NutritionRecordCollection.class, NutritionRecord.class);
			response = aggregationResults.getMappedResults();
			for (NutritionRecord nutritionRecord : response) {
				if (nutritionRecord.getRecordsFiles() != null) {
					for (RecordsFile recordsFile : nutritionRecord.getRecordsFiles()) {

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
			throw new BusinessException(ServiceError.Unknown, "error while getting Nutrition Report");

		}
		return response;

	}

	@Override
	public NutritionRecord getNutritionRecord(String recordId) {
		NutritionRecord response;
		try {
			NutritionRecordCollection recordsCollection = nutritionRecordRepository.findById(new ObjectId(recordId))
					.orElse(null);
			response = new NutritionRecord();
			BeanUtil.map(recordsCollection, response);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while view Nutrition Report");

		}
		return response;

	}

	@Override
	public NutritionRecord deleteNutritionRecord(String recordId, Boolean discarded) {
		NutritionRecord response;
		try {
			NutritionRecordCollection recordsCollection = nutritionRecordRepository.findById(new ObjectId(recordId))
					.orElse(null);
			if (recordsCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "No Nutrition Record found with Id");
			}
			recordsCollection.setDiscarded(discarded);
			recordsCollection.setUpdatedTime(new Date());
			response = new NutritionRecord();
			BeanUtil.map(recordsCollection, response);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while view Nutrition Report");

		}
		return response;

	}

	@Override
	public RecordsFile uploadNutritionRecord(DoctorLabReportUploadRequest request) {

		RecordsFile recordsFile = null;
		try {
			Date createdTime = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId()))
						.orElse(null);
				if (userCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Invalid patient Id");
				}
			}
			FileDetails fileDetails = request.getFileDetails();
			recordsFile = new RecordsFile();
			createdTime = new Date();
			String path = "nuutritionRecord" + File.separator
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

		} catch (

		Exception e) {
			logger.error(e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "error while uploading Doctor Lab Report");

		}
		return recordsFile;

	}

	@Override
	public Boolean updateShareWithPatient(String recordId) {
		Boolean response = false;
		try {
			NutritionRecordCollection recordCollection = nutritionRecordRepository.findById(new ObjectId(recordId))
					.orElse(null);
			if (recordCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No record fount with recordId");
			}
			if (recordCollection.getShareWithPatient() == null) {
				{
					recordCollection.setShareWithPatient(true);
				}
			} else {
				recordCollection.setShareWithPatient(!recordCollection.getShareWithPatient());
			}

			recordCollection.setUpdatedTime(new Date());
			nutritionRecordRepository.save(recordCollection);
			if (recordCollection.getShareWithPatient()) {

				if (!DPDoctorUtils.anyStringEmpty(recordCollection.getPatientId())) {
					pushNotificationServices.notifyUser(recordCollection.getPatientId().toString(),
							"Healthcoco has shared " + recordCollection.getRecordsLabel()
									+ " with you - Tap to view it!",
							ComponentType.NUTRITION_RECORD.getType(), recordCollection.getId().toString(), null);
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

}
