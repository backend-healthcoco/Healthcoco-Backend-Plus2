package com.dpdocter.services.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.LabPrintContentSetup;
import com.dpdocter.beans.LabPrintDocument;
import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.collections.LabPrintDocumentsCollection;
import com.dpdocter.collections.LabPrintSettingCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LabPrintDocumentsRepository;
import com.dpdocter.repository.LabPrintSettingRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.LabPrintContentRequest;
import com.dpdocter.request.LabPrintDocumentAddEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.LabPrintServices;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class LabPrintServicesImpl implements LabPrintServices {

	private static Logger logger = Logger.getLogger(LabPrintServicesImpl.class.getName());

	@Autowired
	private LabPrintSettingRepository labPrintSettingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LabPrintDocumentsRepository labPrintDocumentsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private FileManager fileManager;

	@Override
	public LabPrintSetting addEditPrintSetting(LabPrintSetting request) {
		LabPrintSetting response = null;
		LabPrintSettingCollection labPrintSettingCollection = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				labPrintSettingCollection = labPrintSettingRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (labPrintSettingCollection == null) {
				labPrintSettingCollection = labPrintSettingRepository.findByLocationIdAndHospitalId(locationObjectId,
						hospitalObjectId);
			}

			if (request.getFooterSetup() != null) {
				request.getFooterSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(request.getFooterSetup().getImageurl())
								? request.getFooterSetup().getImageurl().replace(imagePath, "")
								: "");

			}
			if (request.getHeaderSetup() != null) {
				request.getHeaderSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(request.getHeaderSetup().getImageurl())
								? request.getHeaderSetup().getImageurl().replace(imagePath, "")
								: "");

			}
			BeanUtil.map(request, labPrintSettingCollection);

			if (labPrintSettingCollection == null) {
				labPrintSettingCollection = new LabPrintSettingCollection();
				request.setCreatedTime(new Date());
			}

			UserCollection userCollection = userRepository.findById(doctorObjectId).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found with Id ");
			}
			labPrintSettingCollection.setCreatedBy(
					(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
			labPrintSettingCollection = labPrintSettingRepository.save(labPrintSettingCollection);
			response = new LabPrintSetting();
			BeanUtil.map(labPrintSettingCollection, response);

			if (response.getFooterSetup() != null) {
				response.getFooterSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getFooterSetup().getImageurl())
								? getFinalImageURL(response.getFooterSetup().getImageurl())
								: "");

			}
			if (response.getHeaderSetup() != null) {
				response.getHeaderSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getHeaderSetup().getImageurl())
								? getFinalImageURL(response.getHeaderSetup().getImageurl())
								: "");

			}

		} catch (BusinessException e) {
			e.printStackTrace();
			logger.error(e + " Error occured while saving Lab print settings");
			throw new BusinessException(ServiceError.Unknown, " Error occured while saving Lab print settings");
		}

		return response;

	}

	@Override
	public LabPrintSetting getLabPrintSetting(String locationId, String hospitalId) {
		LabPrintSetting response = null;
		try {
			LabPrintSettingCollection labPrintSettingCollection = labPrintSettingRepository
					.findByLocationIdAndHospitalId(new ObjectId(locationId), new ObjectId(hospitalId));
			if (labPrintSettingCollection != null) {
				response = new LabPrintSetting();
				BeanUtil.map(labPrintSettingCollection, response);

				if (response.getFooterSetup() != null) {
					response.getFooterSetup()
							.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getFooterSetup().getImageurl())
									? getFinalImageURL(response.getFooterSetup().getImageurl())
									: "");

				}
				if (response.getHeaderSetup() != null) {
					response.getHeaderSetup()
							.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getHeaderSetup().getImageurl())
									? getFinalImageURL(response.getHeaderSetup().getImageurl())
									: "");

				}
			}

		} catch (BusinessException e) {

			e.printStackTrace();
			logger.error(e + " Error occured while getting Lab print settings");
			throw new BusinessException(ServiceError.Unknown, " Error occured while getting Lab print settings");

		}
		return response;
	}

	@Override
	public LabPrintSetting setHeaderAndFooterSetup(LabPrintContentRequest request, String type) {
		LabPrintSetting response = null;
		LabPrintSettingCollection labPrintSettingCollection = null;
		String path = "";
		try {
			ImageURLResponse imageURLResponse = new ImageURLResponse();
			LabPrintContentSetup contentSetup = null;
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
				doctorObjectId = new ObjectId(request.getDoctorId());
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId())) {
				locationObjectId = new ObjectId(request.getLocationId());
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId())) {
				hospitalObjectId = new ObjectId(request.getHospitalId());
			}

			labPrintSettingCollection = labPrintSettingRepository.findByLocationIdAndHospitalId(locationObjectId,
					hospitalObjectId);
			if (labPrintSettingCollection == null) {
				labPrintSettingCollection = new LabPrintSettingCollection();
				labPrintSettingCollection.setDoctorId(doctorObjectId);
				labPrintSettingCollection.setLocationId(locationObjectId);
				labPrintSettingCollection.setHospitalId(hospitalObjectId);
				labPrintSettingCollection.setCreatedTime(new Date());
			}
			if (request.getFileDetails() != null) {
				request.getFileDetails().setFileName(request.getFileDetails().getFileName() + new Date());
				path = "lab/print/setup" + File.separator + request.getLocationId();
				imageURLResponse = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path, false);

			}
			if (type.equals("FOOTER")) {
				contentSetup = labPrintSettingCollection.getFooterSetup();
			} else if (type.equals("HEADER")) {
				labPrintSettingCollection.getHeaderSetup();
			}

			if (contentSetup == null) {
				contentSetup = new LabPrintContentSetup();
			}
			if (request.getFileDetails() != null) {
				contentSetup.setImageurl(
						imageURLResponse != null ? imageURLResponse.getImageUrl().replace(imagePath, "") : "");
			}
			if (request.getHeight() > 0)
				contentSetup.setHeight(request.getHeight());

			if (type.equals("FOOTER")) {
				labPrintSettingCollection.setFooterSetup(contentSetup);
			} else if (type.equals("HEADER")) {
				labPrintSettingCollection.setHeaderSetup(contentSetup);
			}

			UserCollection userCollection = userRepository.findById(doctorObjectId).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor not found with Id ");
			}
			labPrintSettingCollection.setCreatedBy(
					(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
			labPrintSettingCollection = labPrintSettingRepository.save(labPrintSettingCollection);
			response = new LabPrintSetting();
			BeanUtil.map(labPrintSettingCollection, response);
			if (response.getFooterSetup() != null) {
				response.getFooterSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getFooterSetup().getImageurl())
								? getFinalImageURL(response.getFooterSetup().getImageurl())
								: "");
			}
			if (response.getHeaderSetup() != null) {
				response.getHeaderSetup()
						.setImageurl(!DPDoctorUtils.anyStringEmpty(response.getHeaderSetup().getImageurl())
								? getFinalImageURL(response.getHeaderSetup().getImageurl())
								: "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Lab print settings");
			throw new BusinessException(ServiceError.Unknown, " Error occured while getting Lab print settings");
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
	public LabPrintDocument addEditDocument(LabPrintDocumentAddEditRequest request) {
		LabPrintDocument response = null;
		List<ImageURLResponse> imResponses = null;
		UserCollection doctor = null;

		try {
			/*
			 * LabPrintDocumentsCollection documentsCollection = new
			 * LabPrintDocumentsCollection(); LabPrintDocumentsCollection
			 * olddocumentsCollection = null; if
			 * (!DPDoctorUtils.anyStringEmpty(request.getId())) { olddocumentsCollection =
			 * labPrintDocumentsRepository.findOne(new ObjectId(request.getId())); if
			 * (olddocumentsCollection == null) { throw new
			 * BusinessException(ServiceError.NoRecord,
			 * " No Lab Print Document Present for Id"); }
			 * 
			 * } doctor = userRepository.findOne(new ObjectId(request.getDoctorId())); if
			 * (doctor == null) { throw new BusinessException(ServiceError.NoRecord,
			 * " No Doctor for DoctorId"); } BeanUtil.map(request, documentsCollection); if
			 * (DPDoctorUtils.anyStringEmpty(request.getId())) {
			 * documentsCollection.setCreatedBy(olddocumentsCollection.getCreatedBy());
			 * documentsCollection.setCreatedTime(olddocumentsCollection.getCreatedTime());
			 * } else { documentsCollection.setCreatedBy( (doctor.getTitle() != null ?
			 * doctor.getTitle() + " " : "") + doctor.getFirstName());
			 * documentsCollection.setCreatedTime(new Date()); } if
			 * (request.getFileDetails() != null && !request.getFileDetails().isEmpty()) {
			 * imResponses = new ArrayList<ImageURLResponse>(); for (FileDetails fileDetails
			 * : request.getFileDetails()) {
			 * fileDetails.setFileName(fileDetails.getFileName() + new Date().getTime());
			 * String path = "records" + File.separator + request.getLocationId();
			 * imResponses.addAll(fileManager.convertPdfToImage(fileDetails, path, true)); }
			 * } if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
			 * List<ImageURLResponse> imageURLResponses = new ArrayList<ImageURLResponse>();
			 * for (ImageURLResponse imageURLResponse : request.getDocuments()) {
			 * imageURLResponse.setImageUrl(imageURLResponse.getImageUrl().replaceAll(
			 * imagePath, ""));
			 * imageURLResponse.setThumbnailUrl(imageURLResponse.getThumbnailUrl().
			 * replaceAll(imagePath, "")); }
			 * imageURLResponses.addAll(request.getDocuments());
			 * 
			 * if (imResponses != null && !imResponses.isEmpty()) {
			 * imageURLResponses.addAll(imResponses); }
			 * 
			 * documentsCollection.setDocuments(new ArrayList<ImageURLResponse>());
			 * documentsCollection.setDocuments(imageURLResponses);
			 * 
			 * } else { if (imResponses != null && !imResponses.isEmpty()) {
			 * 
			 * documentsCollection.setDocuments(new ArrayList<ImageURLResponse>());
			 * documentsCollection.setDocuments(imResponses); }
			 * 
			 * } documentsCollection =
			 * labPrintDocumentsRepository.save(documentsCollection); response = new
			 * LabPrintDocument(); BeanUtil.map(documentsCollection, response);
			 * 
			 * if (response.getDocuments() != null && !response.getDocuments().isEmpty()) {
			 * for (ImageURLResponse imageURLResponse : response.getDocuments()) {
			 * imageURLResponse.setImageUrl(getFinalImageURL(imageURLResponse.getImageUrl())
			 * ); imageURLResponse.setThumbnailUrl(getFinalImageURL(imageURLResponse.
			 * getThumbnailUrl())); } }
			 */

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while add Edit Lab print document");
			throw new BusinessException(ServiceError.Unknown, " Error occured while add Edit Lab print document");
		}
		return response;

	}

	@Override
	public LabPrintDocument getLabPrintDocument(String labPrintDocumentId) {
		LabPrintDocument response = null;
		try {

			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(labPrintDocumentId))),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location"),
					Aggregation.lookup("location_cl", "locationId", "_id", "uploadedByLocation"),
					Aggregation.unwind("uploadedByLocation"),
					Aggregation.lookup("user_cl", "locationId", "_id", "doctor"), Aggregation.unwind("location"),
					Aggregation.lookup("user_cl", "locationId", "_id", "uploadedByDoctor"),
					Aggregation.unwind("uploadedByDoctor"));
			response = mongoTemplate.aggregate(aggregation, LabPrintDocumentsCollection.class, LabPrintDocument.class)
					.getUniqueMappedResult();

			if (response != null)
				if (response.getDocuments() != null && !response.getDocuments().isEmpty()) {
					for (ImageURLResponse imageURLResponse : response.getDocuments()) {
						imageURLResponse.setImageUrl(getFinalImageURL(imageURLResponse.getImageUrl()));
						imageURLResponse.setThumbnailUrl(getFinalImageURL(imageURLResponse.getThumbnailUrl()));
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Edit Lab print document");
			throw new BusinessException(ServiceError.Unknown, " Error occured while getting Edit Lab print document");
		}
		return response;
	}

	@Override
	public List<LabPrintDocument> getLabPrintDocuments(int page, int size, String locationId, String doctorId,
			String hospitalId, String searchTerm, Boolean isParent, Long from, Long to, Boolean discarded) {
		List<LabPrintDocument> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (from != 0 && to != 0) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else if (from != 0) {
				criteria.and("updatedTime").gte(new Date(from));
			} else if (to != 0) {
				criteria.and("updatedTime").lte(DPDoctorUtils.getEndTime(new Date(to)));
			}
			ObjectId locationObjectId = new ObjectId(locationId);

			if (isParent) {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(new Criteria("patientName").regex(searchTerm, "i"),
							new Criteria("uploadedByLocation.locationName").regex(searchTerm, "i"));
				}
				criteria.and("uploadedByLocationId").is(locationObjectId).and("uploadedByHospitalId")
						.is(new ObjectId(hospitalId));
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("uploadedByDoctorId").is(new ObjectId(doctorId));
				}
			} else {

				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(new Criteria("patientName").regex(searchTerm, "i"),
							new Criteria("location.locationName").regex(searchTerm, "i"));
				}
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(new ObjectId(hospitalId));
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					criteria.and("doctorId").is(new ObjectId(doctorId));
				}
			}
			criteria.and("discarded").is(discarded);
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("location_cl", "uploadedByLocationId", "_id", "uploadedByLocation"),
						Aggregation.unwind("uploadedByLocation"),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("location"),
						Aggregation.lookup("user_cl", "uploadedByDoctorId", "_id", "uploadedByDoctor"),
						Aggregation.unwind("uploadedByDoctor"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						Aggregation.lookup("location_cl", "locationId", "_id", "uploadedByLocation"),
						Aggregation.unwind("uploadedByLocation"),
						Aggregation.lookup("user_cl", "locationId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("user_cl", "locationId", "_id", "uploadedByDoctor"),
						Aggregation.unwind("uploadedByDoctor"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, LabPrintDocumentsCollection.class, LabPrintDocument.class)
					.getMappedResults();

			if (response != null)
				for (LabPrintDocument document : response) {
					if (document.getDocuments() != null && !document.getDocuments().isEmpty()) {
						for (ImageURLResponse imageURLResponse : document.getDocuments()) {
							imageURLResponse.setImageUrl(getFinalImageURL(imageURLResponse.getImageUrl()));
							imageURLResponse.setThumbnailUrl(getFinalImageURL(imageURLResponse.getThumbnailUrl()));
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Edit Lab print document");
			throw new BusinessException(ServiceError.Unknown, " Error occured while getting Edit Lab print document");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean deleteLabPrintDocument(String id, boolean discarded) {
		Boolean response = false;
		LabPrintDocumentsCollection LabPrintDocumentsCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				LabPrintDocumentsCollection = labPrintDocumentsRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (LabPrintDocumentsCollection != null) {
				LabPrintDocumentsCollection.setDiscarded(discarded);
				LabPrintDocumentsCollection = labPrintDocumentsRepository.save(LabPrintDocumentsCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e + " Error occured while discarding Lab print document");
			throw new BusinessException(ServiceError.Unknown, " Error occured while discarding Lab print document");
		}
		return response;
	}
}
