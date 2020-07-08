package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.PrintSettings;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.enums.PrintFilter;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalLabPrintSettingRepository;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.PrintSettingsService;

import common.util.web.DPDoctorUtils;

@Service
public class PrintSettingsServiceImpl implements PrintSettingsService {

	private static Logger logger = Logger.getLogger(PrintSettingsServiceImpl.class.getName());

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private HospitalRepository hospitalRepository;

	@Autowired
	private DentalLabPrintSettingRepository dentalLabPrintSettingRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private FileManager fileManager;

	@Value(value = "${image.path}")
	private String imagePath;

	@Override
	@Transactional
	public PrintSettings saveSettings(PrintSettings request) {
		PrintSettings response = new PrintSettings();
		PrintSettingsCollection printSettingsCollection = new PrintSettingsCollection();
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getHospitalId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());
			PrintSettingsCollection oldPrintSettingsCollection = null;
			if (request.getId() == null) {
				if (!request.getIsLab()) {
					oldPrintSettingsCollection = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalId(doctorObjectId, locationObjectId,
							hospitalObjectId);
				} else {
					oldPrintSettingsCollection = printSettingsRepository.findByLocationIdAndHospitalId(locationObjectId,
							hospitalObjectId);
				}
				if (oldPrintSettingsCollection != null)
					request.setId(oldPrintSettingsCollection.getId().toString());
			}
			BeanUtil.map(request, printSettingsCollection);
			if (request.getId() == null) {
				printSettingsCollection.setCreatedTime(new Date());
			} else if (oldPrintSettingsCollection == null) {
				oldPrintSettingsCollection = printSettingsRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (oldPrintSettingsCollection != null) {
				printSettingsCollection.setCreatedTime(oldPrintSettingsCollection.getCreatedTime());
				printSettingsCollection.setCreatedBy(oldPrintSettingsCollection.getCreatedBy());
				printSettingsCollection.setDiscarded(oldPrintSettingsCollection.getDiscarded());
				printSettingsCollection.setHospitalUId(oldPrintSettingsCollection.getHospitalUId());

				if (request.getPageSetup() == null) {
					printSettingsCollection.setPageSetup(oldPrintSettingsCollection.getPageSetup());
				}

				if (request.getHeaderSetup() == null) {
					printSettingsCollection.setHeaderSetup(oldPrintSettingsCollection.getHeaderSetup());
				} else if (!DPDoctorUtils
						.anyStringEmpty(printSettingsCollection.getHeaderSetup().getHeaderImageUrl())) {
					printSettingsCollection.getHeaderSetup().setHeaderImageUrl(
							printSettingsCollection.getHeaderSetup().getHeaderImageUrl().replaceAll(imagePath, ""));
				}
				if (request.getFooterSetup() == null) {
					printSettingsCollection.setFooterSetup(oldPrintSettingsCollection.getFooterSetup());
				} else if (!DPDoctorUtils
						.anyStringEmpty(printSettingsCollection.getFooterSetup().getFooterImageUrl())) {
					printSettingsCollection.getFooterSetup().setFooterImageUrl(
							printSettingsCollection.getFooterSetup().getFooterImageUrl().replaceAll(imagePath, ""));
					printSettingsCollection.getFooterSetup()
					.setSignatureUrl(getFinalImageURL(response.getFooterSetup().getSignatureUrl()));
				}
			}

			if (DPDoctorUtils.allStringsEmpty(printSettingsCollection.getHospitalUId())) {
				HospitalCollection hospitalCollection = hospitalRepository
						.findById(new ObjectId(request.getHospitalId())).orElse(null);
				if (hospitalCollection != null) {
					printSettingsCollection.setHospitalUId(hospitalCollection.getHospitalUId());
				}
			}

			LocationCollection locationCollection = locationRepository.findById(new ObjectId(request.getLocationId())).orElse(null);
			if (locationCollection != null) {
				printSettingsCollection.setClinicLogoUrl(locationCollection.getLogoUrl());
				printSettingsCollection.setIsPidHasDate(locationCollection.getIsPidHasDate());
			}

			printSettingsCollection = printSettingsRepository.save(printSettingsCollection);
			BeanUtil.map(printSettingsCollection, response);
			if (response != null) {
				if (response.getHeaderSetup() != null) {
					response.getHeaderSetup()
							.setHeaderImageUrl(getFinalImageURL(response.getHeaderSetup().getHeaderImageUrl()));

				}
				if (response.getFooterSetup() != null) {
					response.getFooterSetup()
							.setFooterImageUrl(getFinalImageURL(response.getFooterSetup().getFooterImageUrl()));
					response.getFooterSetup()
					.setSignatureUrl(getFinalImageURL(response.getFooterSetup().getSignatureUrl()));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while saving settings");
			throw new BusinessException(ServiceError.Unknown, "Error occured while saving settings");
		}
		return response;
	}

	@Override
	@Transactional
	public List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId,
			int page, int size, String updatedTime, Boolean discarded) {
		List<PrintSettings> response = null;
		List<PrintSettingsCollection> printSettingsCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			long createdTimeStamp = Long.parseLong(updatedTime);
			if (doctorObjectId == null) {

				printSettingsCollections = printSettingsRepository.findByLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(locationObjectId, hospitalObjectId,
						new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));

			} else {
				if (locationObjectId == null && hospitalObjectId == null) {
					if (size > 0)
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
								new Date(createdTimeStamp), discards,
								PageRequest.of(page, size, Direction.DESC, "createdTime"));
					else
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId,
								new Date(createdTimeStamp), discards, new Sort(Sort.Direction.DESC, "createdTime"));
				} else {
					if (size > 0)
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
								hospitalObjectId, new Date(createdTimeStamp), discards,
								PageRequest.of(page, size, Direction.DESC, "createdTime"));
					else
						printSettingsCollections = printSettingsRepository.findByDoctorIdAndLocationIdAndHospitalIdAndUpdatedTimeGreaterThanAndDiscardedIn(doctorObjectId, locationObjectId,
								hospitalObjectId, new Date(createdTimeStamp), discards,
								new Sort(Sort.Direction.DESC, "createdTime"));
				}
			}
			if (printSettingsCollections != null) {
				response = new ArrayList<PrintSettings>();

				for (PrintSettingsCollection collection : printSettingsCollections) {
					PrintSettings printSettings = new PrintSettings();
					if (PrintFilter.PAGESETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setFooterSetup(null);
						collection.setHeaderSetup(null);
					}
					if (PrintFilter.HEADERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setFooterSetup(null);
						collection.setPageSetup(null);
					}
					if (PrintFilter.FOOTERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
						collection.setPageSetup(null);
						collection.setHeaderSetup(null);
					}
					BeanUtil.map(collection, printSettings);
					if (printSettings != null) {
						if (printSettings.getHeaderSetup() != null) {
							printSettings.getHeaderSetup().setHeaderImageUrl(
									getFinalImageURL(printSettings.getHeaderSetup().getHeaderImageUrl()));

						}
						if (printSettings.getFooterSetup() != null) {
							printSettings.getFooterSetup().setFooterImageUrl(
									getFinalImageURL(printSettings.getFooterSetup().getFooterImageUrl()));
							printSettings.getFooterSetup()
							.setSignatureUrl(getFinalImageURL(printSettings.getFooterSetup().getSignatureUrl()));
						}
					}
					response.add(printSettings);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Print Settings");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Print Settings");
		}
		return response;

	}

	@Override
	@Transactional
	public String getPrintSettingsGeneralNote(String doctorId, String locationId, String hospitalId) {
		String response = null;
		List<PrintSettings> printSettings = null;
		Aggregation aggregation = null;

		try {

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorObjectId = new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				locationObjectId = new ObjectId(locationId);
				criteria.and("locationId").is(locationObjectId);
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				hospitalObjectId = new ObjectId(hospitalId);
				criteria.and("hospitalId").is(hospitalObjectId);
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<PrintSettings> aggregationResults = mongoTemplate.aggregate(aggregation,
					PrintSettingsCollection.class, PrintSettings.class);

			printSettings = aggregationResults.getMappedResults();
			for (PrintSettings printSetting : printSettings) {
				if (printSetting.getGeneralNotes() != null) {
					response = printSetting.getGeneralNotes();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Print Settings Notes");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Print Settings Notes");
		}
		return response;

	}

	@Override
	@Transactional
	public PrintSettings deletePrintSettings(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PrintSettings response = null;
		try {
			PrintSettingsCollection printSettingsCollection = printSettingsRepository.findById(new ObjectId(id)).orElse(null);
			if (printSettingsCollection != null) {
				if (printSettingsCollection.getDoctorId() != null && printSettingsCollection.getHospitalId() != null
						&& printSettingsCollection.getLocationId() != null) {
					if (printSettingsCollection.getDoctorId().toString().equals(doctorId)
							&& printSettingsCollection.getHospitalId().toString().equals(hospitalId)
							&& printSettingsCollection.getLocationId().toString().equals(locationId)) {
						printSettingsCollection.setDiscarded(discarded);
						printSettingsCollection.setUpdatedTime(new Date());
						printSettingsRepository.save(printSettingsCollection);
						response = new PrintSettings();
						BeanUtil.map(printSettingsCollection, response);
						if (response.getHeaderSetup() != null) {
							response.getHeaderSetup()
									.setHeaderImageUrl(getFinalImageURL(response.getHeaderSetup().getHeaderImageUrl()));

						}
						if (response.getFooterSetup() != null) {
							response.getFooterSetup()
									.setFooterImageUrl(getFinalImageURL(response.getFooterSetup().getFooterImageUrl()));
							response.getFooterSetup()
							.setSignatureUrl(getFinalImageURL(response.getFooterSetup().getSignatureUrl()));
						}
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Doctor Id, Hospital Id, Or Location Id");
				}

			} else {
				logger.warn("Print Settings not found!");
				throw new BusinessException(ServiceError.NoRecord, "Print Settings not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (!DPDoctorUtils.anyStringEmpty(imageURL)) {
			return imagePath + imageURL;
		} else
			return null;

	}

	@Override
	public String uploadFile(FileDetails fileDetails, String type) {
		ImageURLResponse response = null;
		String path = "";
		try {

			fileDetails.setFileName(fileDetails.getFileName() + new Date());
			path = "print/setup" + File.separator + type;
			response = fileManager.saveImageAndReturnImageUrl(fileDetails, path, false);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while uploading Image");
			throw new BusinessException(ServiceError.Unknown, " Error occured while uploading Image");
		}
		return getFinalImageURL(response.getImageUrl().replaceAll(imagePath, ""));
	}

	@Override
	public String uploadSignature(FileDetails fileDetails) {
		ImageURLResponse response = null;
		String path = "";
		try {
			fileDetails.setFileName(fileDetails.getFileName() + new Date());
			path = "print/setup" + File.separator + "signature";
			response = fileManager.saveImageAndReturnImageUrl(fileDetails, path, false);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while uploading Signature");
			throw new BusinessException(ServiceError.Unknown, " Error occured while Signature Image");
		}
		return getFinalImageURL(response.getImageUrl().replaceAll(imagePath, ""));
	}

}
