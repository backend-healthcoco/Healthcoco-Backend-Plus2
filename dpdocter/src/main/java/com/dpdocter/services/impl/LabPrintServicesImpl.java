package com.dpdocter.services.impl;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.LabPrintContentSetup;
import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.collections.LabPrintSettingCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LabPrintSettingRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.LabPrintContentRequest;
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
				labPrintSettingCollection = labPrintSettingRepository.findBylocationIdAndhospitalId(locationObjectId,
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
					.findBylocationIdAndhospitalId(new ObjectId(locationId), new ObjectId(hospitalId));
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

			labPrintSettingCollection = labPrintSettingRepository.findBylocationIdAndhospitalId(locationObjectId,
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

}
