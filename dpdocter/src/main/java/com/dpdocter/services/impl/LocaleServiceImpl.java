package com.dpdocter.services.impl;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Locale;
import com.dpdocter.beans.LocaleImage;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.RecommendationsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.services.ESLocaleService;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserResourceFavouriteRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LocaleService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class LocaleServiceImpl implements LocaleService {

	@Autowired
	LocaleRepository localeRepository;

	@Autowired
	ESLocaleService esLocaleService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RecommendationsRepository recommendationsRepository;

	@Autowired
	public MongoTemplate mongoTemplate;

	@Autowired
	FileManager fileManager;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private UserResourceFavouriteRepository userResourceFavouriteRepository;

	private static Logger logger = Logger.getLogger(LoginServiceImpl.class.getName());

	@Override
	@Transactional
	public Locale getLocaleDetailBySlugUrl(String slugUrl) {
		Locale response = null;
		LocaleCollection localeCollection = mongoTemplate.findOne(
				new Query(new Criteria("pharmacySlugUrl").regex("^" + slugUrl + "*", "i")), LocaleCollection.class);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		response = new Locale();
		if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {

			for (LocaleImage image : localeCollection.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}

		}
		if (localeCollection.getAddress() != null) {

			localeCollection
					.setLocaleAddress((!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getStreetAddress())
							? localeCollection.getAddress().getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLandmarkDetails())
									? localeCollection.getAddress().getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLocality())
									? localeCollection.getAddress().getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCity())
									? localeCollection.getAddress().getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getState())
									? localeCollection.getAddress().getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCountry())
									? localeCollection.getAddress().getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getPostalCode())
									? localeCollection.getAddress().getPostalCode() : ""));
		}
		BeanUtil.map(localeCollection, response);

		return response;
	}

	@Override
	@Transactional
	public Locale getLocaleDetails(String id, String userId) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findOne(new ObjectId(id));
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		response = new Locale();
		if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {

			for (LocaleImage image : localeCollection.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}

		}
		if (localeCollection.getAddress() != null) {

			localeCollection
					.setLocaleAddress((!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getStreetAddress())
							? localeCollection.getAddress().getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLandmarkDetails())
									? localeCollection.getAddress().getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLocality())
									? localeCollection.getAddress().getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCity())
									? localeCollection.getAddress().getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getState())
									? localeCollection.getAddress().getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCountry())
									? localeCollection.getAddress().getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getPostalCode())
									? localeCollection.getAddress().getPostalCode() : ""));
		}
		BeanUtil.map(localeCollection, response);
		if (localeCollection != null && !DPDoctorUtils.anyStringEmpty(userId)) {
			ObjectId patientId = new ObjectId(userId);
			RecommendationsCollection recommendationsCollection = recommendationsRepository
					.findByDoctorIdLocationIdAndPatientId(null, localeCollection.getId(), patientId);
			if (recommendationsCollection != null) {
				response.setIsLocaleRecommended(!recommendationsCollection.getDiscarded());
			}

			Integer favCount = userResourceFavouriteRepository.findCount(localeCollection.getId(),
					Resource.PHARMACY.getType(), null, patientId, false);
			if (favCount != null && favCount > 0)
				response.setIsFavourite(true);
		}
		return response;
	}

	@Override
	@Transactional
	public Locale getLocaleDetailsByContactDetails(String contactNumber, String userId) {
		Locale response = null;
		LocaleCollection localeCollection = localeRepository.findByMobileNumber(contactNumber);
		if (localeCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Record for id not found");
		}
		if (localeCollection.getLocaleImages() != null && !localeCollection.getLocaleImages().isEmpty()) {

			for (LocaleImage image : localeCollection.getLocaleImages()) {
				image.setImageUrl(imagePath + image.getImageUrl());
				image.setThumbnailUrl(imagePath + image.getThumbnailUrl());
			}

		}

		if (localeCollection.getAddress() != null) {

			localeCollection
					.setLocaleAddress((!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getStreetAddress())
							? localeCollection.getAddress().getStreetAddress() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLandmarkDetails())
									? localeCollection.getAddress().getLandmarkDetails() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getLocality())
									? localeCollection.getAddress().getLocality() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCity())
									? localeCollection.getAddress().getCity() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getState())
									? localeCollection.getAddress().getState() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getCountry())
									? localeCollection.getAddress().getCountry() + ", " : "")
							+ (!DPDoctorUtils.anyStringEmpty(localeCollection.getAddress().getPostalCode())
									? localeCollection.getAddress().getPostalCode() : ""));
		}
		response = new Locale();
		BeanUtil.map(localeCollection, response);
		if (localeCollection != null && !DPDoctorUtils.anyStringEmpty(userId)) {
			RecommendationsCollection recommendationsCollection = recommendationsRepository
					.findByDoctorIdLocationIdAndPatientId(null, localeCollection.getId(), new ObjectId(userId));
			if (recommendationsCollection != null) {
				response.setIsLocaleRecommended(!recommendationsCollection.getDiscarded());
			}
		}
		return response;
	}

	@Override
	public Locale addEditRecommedation(String localeId, String patientId, RecommendationType type) {
		Locale response = null;

		try {

			ObjectId localeObjectId = new ObjectId(localeId);
			ObjectId patientObjectId = new ObjectId(patientId);
			RecommendationsCollection recommendationsCollection = null;

			LocaleCollection localeCollection = localeRepository.findOne(localeObjectId);

			UserCollection userCollection = userRepository.findOne(patientObjectId);

			if (userCollection != null && localeCollection != null) {
				recommendationsCollection = recommendationsRepository.findByDoctorIdLocationIdAndPatientId(null,
						localeObjectId, patientObjectId);

				if (recommendationsCollection == null) {
					recommendationsCollection = new RecommendationsCollection();
					recommendationsCollection.setLocationId(localeObjectId);
					recommendationsCollection.setPatientId(patientObjectId);
					localeCollection.setNoOfLocaleRecommendation(1);
				} else {
					switch (type) {
					case LIKE:
						if (recommendationsCollection.getDiscarded()) {
							localeCollection
									.setNoOfLocaleRecommendation(localeCollection.getNoOfLocaleRecommendation() + 1);
							recommendationsCollection.setDiscarded(false);
						}
						break;

					case UNLIKE:
						if (!recommendationsCollection.getDiscarded()) {
							localeCollection
									.setNoOfLocaleRecommendation(localeCollection.getNoOfLocaleRecommendation() - 1);
							recommendationsCollection.setDiscarded(true);
						}
						break;

					default:
						break;
					}
				}

				recommendationsCollection = recommendationsRepository.save(recommendationsCollection);
				localeCollection = localeRepository.save(localeCollection);
				response = new Locale();
				BeanUtil.map(localeCollection, response);
				response.setIsLocaleRecommended(!recommendationsCollection.getDiscarded());

			} else {
				throw new BusinessException(ServiceError.Unknown, "Error  location  not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Editing Doctor Profile");
			throw new BusinessException(ServiceError.Unknown, "Error recommending");
		}

		return response;
	}

	@Override
	@Transactional
	public ImageURLResponse addRXImageMultipart(FormDataBodyPart file) {
		try {
			// Boolean response =false;
			// LocaleImageCollection localeImageCollection = null;
			ImageURLResponse imageURLResponse = null;
			Date createdTime = new Date();
			if (file != null) {
				if (!DPDoctorUtils.anyStringEmpty(file.getFormDataContentDisposition().getFileName())) {
					String path = "localeRX";
					FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
					String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
					String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
					String recordPath = path + File.separator + fileName + createdTime.getTime() + "." + fileExtension;
					// String recordLabel = fileName;
					imageURLResponse = new ImageURLResponse();
					imageURLResponse = fileManager.saveImage(file, recordPath, true);
				}
			}
			return imageURLResponse;
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

}
