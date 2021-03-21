package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.LabSubReportJasperDetails;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.LabTestSample;
import com.dpdocter.beans.PatientLabTestSample;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.LabTestPickupCollection;
import com.dpdocter.collections.LabTestSampleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LabReportsRepository;
import com.dpdocter.repository.LabTestPickupRepository;
import com.dpdocter.repository.LabTestSampleRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.request.EditLabReportsRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.LabReportsResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LabReportsService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class LabReportsServiceImpl implements LabReportsService {

	public static final Logger LOGGER = LogManager.getLogger(LabReportsServiceImpl.class);

	@Autowired
	private LocationServices locationServices;

	@Autowired
	LabReportsRepository labReportsRepository;

	@Autowired
	LabTestSampleRepository labTestSampleRepository;

	@Autowired
	LabTestPickupRepository labTestPickupRepository;

	@Autowired
	LocationRepository locationRepository;

//	@Autowired
//	private PatientVisitService patientVisitService;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	SMSServices smsServices;

	@Autowired
	UserRepository userRepository;

	@Autowired
	FileManager fileManager;

	@Autowired
	MongoTemplate mongoTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.labRequisationForm.a4.fileName}")
	private String labRequisationFormA4FileName;

	@Value(value = "${lab.reports.upload}")
	private String labReportUploadMessage;

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Override
	@Transactional
	public LabReports addLabReports(FormDataBodyPart file, LabReportsAddRequest request) {
		LabReports response = null;
		LabReportsCollection labReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "lab-reports";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
			}
			labReportsCollection = labReportsRepository
					.findByLabTestSampleId(new ObjectId(request.getLabTestSampleId()));
			if (labReportsCollection == null) {
				labReportsCollection = new LabReportsCollection();
			}
			if (labReportsCollection.getLabReports() == null) {
				List<ImageURLResponse> responses = new ArrayList<>();
				labReportsCollection.setLabReports(responses);
			}
			BeanUtil.map(request, labReportsCollection);
			labReportsCollection.getLabReports().add(imageURLResponse);
			labReportsCollection.setUploadCounts(labReportsCollection.getUploadCounts() + 1);
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			response = new LabReports();
			BeanUtil.map(labReportsCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public LabReports addLabReportBase64(FileDetails fileDetails, LabReportsAddRequest request) {
		LabReports response = null;
		LabReportsCollection labReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "lab-reports" + File.separator + request.getPatientName();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}
			labReportsCollection = labReportsRepository
					.findByLabTestSampleId(new ObjectId(request.getLabTestSampleId()));
			if (labReportsCollection == null) {
				labReportsCollection = new LabReportsCollection();
			}
			if (labReportsCollection.getLabReports() == null) {
				List<ImageURLResponse> responses = new ArrayList<>();
				labReportsCollection.setLabReports(responses);
			}
			BeanUtil.map(request, labReportsCollection);
			labReportsCollection.getLabReports().add(imageURLResponse);
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			response = new LabReports();
			BeanUtil.map(labReportsCollection, response);

			LabTestPickupCollection labTestPickupCollection = labTestPickupRepository
					.findByLabTestSampleIds(new ObjectId(request.getLabTestSampleId()));
			if (labTestPickupCollection != null) {
				labTestPickupCollection.setStatus("REPORTS UPLOADED");
				labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);
			}

			LabTestSampleCollection labTestSampleCollection = labTestSampleRepository
					.findById(new ObjectId(request.getLabTestSampleId())).orElse(null);
			if (labTestSampleCollection != null) {
				if (labTestSampleCollection.getIsCompleted() == true
						&& !DPDoctorUtils.anyStringEmpty(labTestSampleCollection.getParentLabLocationId())
						&& DPDoctorUtils.anyStringEmpty(labReportsCollection.getSerialNumber())) {
					String serialNumber = reportSerialNumberGenerator(
							labTestSampleCollection.getParentLabLocationId().toString());
					labReportsCollection.setSerialNumber(serialNumber);
				}
				labTestSampleCollection.setStatus("REPORTS UPLOADED");
				labTestSampleCollection = labTestSampleRepository.save(labTestSampleCollection);
				LocationCollection daughterlocationCollection = locationRepository
						.findById(labTestSampleCollection.getDaughterLabLocationId()).orElse(null);
				LocationCollection parentLocationCollection = locationRepository
						.findById(labTestSampleCollection.getParentLabLocationId()).orElse(null);
				String message = labReportUploadMessage;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setType("LAB REPORT UPLOAD");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(daughterlocationCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(daughterlocationCollection.getLocationName());
				message = message.replace("{patientName}", request.getPatientName());
				message = message.replace("{specimenName}", labTestSampleCollection.getSampleType());
				message = message.replace("{parentLab}", parentLocationCollection.getLocationName());
				sms.setSmsText(message);
				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(daughterlocationCollection.getClinicNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private String reportSerialNumberGenerator(String locationId) {
		String generatedId = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			ObjectId locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			LocationCollection location = locationRepository.findById(locationObjectId).orElse(null);
			if (location == null) {
				throw new BusinessException(ServiceError.NoRecord, "Invalid Location Id");
			}
			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long startTimeinMillis = start.getMillis();
			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			Long endTimeinMillis = end.getMillis();
			int reportSize = labReportsRepository.findTodaysCompletedReport(locationObjectId, true,
					new Date(startTimeinMillis), new Date(endTimeinMillis));

			generatedId = String.valueOf((reportSize + 1));
		} catch (BusinessException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generatedId;
	}

	@Override
	@Transactional
	public LabReports addLabReportBase64(FileDetails fileDetails, DoctorLabReportsAddRequest request) {
		LabReports response = null;
		LabReportsCollection labReportsCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());

				String path = "lab-reports" + File.separator + request.getPatientId();

				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, path, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}

			if (labReportsCollection == null) {
				labReportsCollection = new LabReportsCollection();
			}
			if (labReportsCollection.getLabReports() == null) {
				List<ImageURLResponse> responses = new ArrayList<>();
				labReportsCollection.setLabReports(responses);
			}
			BeanUtil.map(request, labReportsCollection);
			labReportsCollection.getLabReports().add(imageURLResponse);
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			response = new LabReports();
			BeanUtil.map(labReportsCollection, response);

			if (labReportsCollection != null) {

				UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);

				if (request.getMobileNumber() != null) {
					LocationCollection daughterlocationCollection = locationRepository
							.findById(labReportsCollection.getLocationId()).orElse(null);
					LocationCollection parentLocationCollection = locationRepository
							.findById(labReportsCollection.getUploadedByLocationId()).orElse(null);
					String message = labReportUploadMessage;

					UserCollection userCollection = userRepository.findById(new ObjectId(request.getPatientId())).orElse(null);
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					smsTrackDetail.setType("LAB REPORT UPLOAD");
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(daughterlocationCollection.getId());
					SMS sms = new SMS();
					smsDetail.setUserName(daughterlocationCollection.getLocationName());
					message = message.replace("{patientName}", userCollection.getFirstName());
					message = message.replace("{specimenName}", request.getTestName());
					message = message.replace("{parentLab}", parentLocationCollection.getLocationName());
					sms.setSmsText(message);
					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(request.getMobileNumber());
					sms.setSmsAddress(smsAddress);
					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					smsServices.sendSMS(smsTrackDetail, true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabReports> getLabReports(String labTestSampleId, String searchTerm, long page, int size) {
		List<LabReports> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm));
			}

			criteria.and("labTestSampleId").is(new ObjectId(labTestSampleId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabReports> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabReportsCollection.class, LabReports.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e + " Error Getting lab Reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting lab reports");
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabReportsResponse> getLabReportsForDoctor(String doctorId, String locationId, String hospitalId,
			String patientId, String searchTerm, long page, int size) {
		List<LabReportsResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patient.patientName").regex("^" + searchTerm, "i"),
						new Criteria("patient.patientName").regex("^" + searchTerm));
			}

			criteria.and("doctorId").is(new ObjectId(doctorId));
			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
			criteria.and("patientId").is(new ObjectId(patientId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabReportsResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabReportsCollection.class, LabReportsResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e + " Error Getting lab Reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting lab reports");
		}
		return response;
	}

	@Override
	@Transactional
	public List<LabReportsResponse> getLabReportsForLab(String doctorId, String locationId, String hospitalId,
			String patientId, String searchTerm, long page, int size) {
		List<LabReportsResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("patient.patientName").regex("^" + searchTerm, "i"),
						new Criteria("patient.patientName").regex("^" + searchTerm));
			}
			criteria.and("uploadedByDoctorId").is(new ObjectId(doctorId));
			criteria.and("uploadedByLocationId").is(new ObjectId(locationId));
			criteria.and("uploadedByHospitalId").is(new ObjectId(hospitalId));
			criteria.and("patientId").is(new ObjectId(patientId));
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
						Aggregation.unwind("patient"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<LabReportsResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					LabReportsCollection.class, LabReportsResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e + " Error Getting lab Reports");
			throw new BusinessException(ServiceError.Unknown, "Error Getting lab reports");
		}
		return response;
	}

	@Override
	@Transactional
	public LabReports editLabReports(EditLabReportsRequest request) {

		LabReports labReports = null;
		try {
			LabReportsCollection labReportsCollection = labReportsRepository
					.findByLabTestSampleId(new ObjectId(request.getId()));
			if (labReportsCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (request.getLabReports() != null && request.getLabReports().isEmpty()) {
				LabTestPickupCollection labTestPickupCollection = labTestPickupRepository
						.findByLabTestSampleIds(labReportsCollection.getLabTestSampleId());
				if (labTestPickupCollection != null) {
					labTestPickupCollection.setStatus("REPORTS PENDING");
					labTestPickupCollection = labTestPickupRepository.save(labTestPickupCollection);

				}
			}
			labReportsCollection.setLabReports(new ArrayList<ImageURLResponse>());
			labReportsCollection.setLabReports(request.getLabReports());
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			labReports = new LabReports();
			BeanUtil.map(labReportsCollection, labReports);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return labReports;

	}

	@Override
	@Transactional
	public LabReportsResponse changePatientShareStatus(String id, Boolean status) {
		LabReportsResponse labReportsResponse = null;
		try {
			LabReportsCollection labReportsCollection = labReportsRepository.findById(new ObjectId(id)).orElse(null);

			if (labReportsCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			labReportsCollection.setIsSharedToPatient(status);
			labReportsCollection = labReportsRepository.save(labReportsCollection);
			labReportsResponse = new LabReportsResponse();
			BeanUtil.map(labReportsCollection, labReportsResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return labReportsResponse;
	}

	@Override
	public String downloadLabreportPrint(List<String> ids) {
		String response = null;
		List<LabTestPickupLookupResponse> labTestPickupLookupResponses = null;
		List<ObjectId> pickupRequestIds = null;
		JasperReportResponse jasperReportResponse = null;
		try {
			pickupRequestIds = new ArrayList<ObjectId>();
			for (String id : ids) {
				if (!DPDoctorUtils.anyStringEmpty(id))
					pickupRequestIds.add(new ObjectId(id));
			}
			labTestPickupLookupResponses = locationServices.getLabTestPickupByIds(pickupRequestIds);
			if (labTestPickupLookupResponses == null || labTestPickupLookupResponses.isEmpty()) {
				throw new BusinessException(ServiceError.NoRecord, " No Lab Report found with ids");
			}

			jasperReportResponse = createJasper(labTestPickupLookupResponses);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e + " Error while getting Lab Report PDF for Parent");
			throw new BusinessException(ServiceError.Unknown, " Error while getting Lab Report PDF for Parent");
		}
		return response;

	}

	private JasperReportResponse createJasper(List<LabTestPickupLookupResponse> labTestPickupLookupResponses)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String locationId = null, hospitalId = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;

		LabSubReportJasperDetails labSubReportJasperDetail = null;
		DBObject labReportItems = null;
		List<DBObject> labreports = new ArrayList<DBObject>();
		List<LabSubReportJasperDetails> labSubReportJasperDetails = null;
		for (LabTestPickupLookupResponse labTestPickupLookupResponse : labTestPickupLookupResponses) {

			labReportItems = new BasicDBObject();

			if (labTestPickupLookupResponse.getParentLab() != null) {
				locationId = labTestPickupLookupResponse.getParentLab().getId();
				hospitalId = labTestPickupLookupResponse.getParentLab().getHospitalId();
				labName = " for " + labTestPickupLookupResponse.getParentLab().getLocationName();
			}

			if (labTestPickupLookupResponse.getDaughterLab() != null) {

				if (!DPDoctorUtils.anyStringEmpty(labTestPickupLookupResponse.getDaughterLab().getLocationName())) {
					labReportItems.put("from",
							"<b>From :- </b>" + labTestPickupLookupResponse.getDaughterLab().getLocationName());
				} else {
					labReportItems.put("from", "<b>From :- </b>");

				}
			} else {
				labReportItems.put("from", "<b>From :- </b>");

			}

			if (!DPDoctorUtils.anyStringEmpty(labTestPickupLookupResponse.getDoctorId())) {
				userCollection = userRepository.findById(new ObjectId(labTestPickupLookupResponse.getDoctorId())).orElse(null);
				if (userCollection != null)
					labReportItems.put("doctor", "<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
				else

					labReportItems.put("doctor", "<b>Doctor :- </b> ");
			} else {
				labReportItems.put("doctor", "<b>Doctor :- </b> ");

			}

			if (labTestPickupLookupResponse.getPatientLabTestSamples() != null
					&& !labTestPickupLookupResponse.getPatientLabTestSamples().isEmpty()) {

				labSubReportJasperDetails = new ArrayList<LabSubReportJasperDetails>();
				for (PatientLabTestSample patientLabTestSample : labTestPickupLookupResponse
						.getPatientLabTestSamples()) {
					labSubReportJasperDetail = new LabSubReportJasperDetails();

					labSubReportJasperDetail.setNo(labSubReportJasperDetails.size() + 1);
					if (!DPDoctorUtils.anyStringEmpty(patientLabTestSample.getPatientName())) {
						labSubReportJasperDetail.setPatientName(patientLabTestSample.getPatientName());
					} else {
						labSubReportJasperDetail.setPatientName("--");
					}
					if (!DPDoctorUtils.anyStringEmpty(patientLabTestSample.getGender())) {
						labSubReportJasperDetail.setGender(patientLabTestSample.getGender());
					} else {
						labSubReportJasperDetail.setGender("--");
					}
					if (patientLabTestSample.getAge() != null) {
						labSubReportJasperDetail.setGender(
								labSubReportJasperDetail.getGender() + "/" + (patientLabTestSample.getAge() > 0
										? patientLabTestSample.getAge().toString() : "--"));
					} else {
						labSubReportJasperDetail.setGender(labSubReportJasperDetail.getGender() + "/" + "--");
					}
					if (patientLabTestSample.getLabTestSamples() != null
							&& !patientLabTestSample.getLabTestSamples().isEmpty()) {
						for (LabTestSample labTestsample : patientLabTestSample.getLabTestSamples()) {

							if (DPDoctorUtils.anyStringEmpty(labSubReportJasperDetail.getTest())) {
								if (!DPDoctorUtils.anyStringEmpty(labTestsample.getSampleType())) {
									labSubReportJasperDetail.setTest(labTestsample.getSampleType());

								} else {
									labSubReportJasperDetail.setTest("--");
								}
								if (labTestsample.getRateCardTestAssociation() != null) {
									if (labTestsample.getRateCardTestAssociation().getDiagnosticTest() != null) {
										labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + "/"
												+ (!DPDoctorUtils.anyStringEmpty(labTestsample
														.getRateCardTestAssociation().getDiagnosticTest().getTestName())
																? labTestsample.getRateCardTestAssociation()
																		.getDiagnosticTest().getTestName()
																: "--"));
									}
								} else {
									labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + "/" + "--");
								}
							} else {
								if (!DPDoctorUtils.anyStringEmpty(labTestsample.getSampleType())) {
									labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + ",<br>"
											+ labTestsample.getSampleType());

								} else {
									labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + ",<br>--");
								}
								if (labTestsample.getRateCardTestAssociation() != null) {
									if (labTestsample.getRateCardTestAssociation().getDiagnosticTest() != null) {
										labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + "/"
												+ (!DPDoctorUtils.anyStringEmpty(labTestsample
														.getRateCardTestAssociation().getDiagnosticTest().getTestName())
																? labTestsample.getRateCardTestAssociation()
																		.getDiagnosticTest().getTestName()
																: "--"));
									}
								} else {
									labSubReportJasperDetail.setTest(labSubReportJasperDetail.getTest() + "/" + "--");
								}

							}
						}
					}
					labSubReportJasperDetails.add(labSubReportJasperDetail);
				}

			}
			labReportItems.put("details", labSubReportJasperDetails);
			labreports.add(labReportItems);
		}
		userCollection = null;

		parameters.put("items", labreports);
		parameters.put("title", "REQUISITION FORM" + labName.toUpperCase());
		parameters.put("date", "<b>Date :- </b>" + simpleDateFormat.format(new Date()));
		String pdfName = locationId + "REQUISATION-FORM" + new Date().getTime();

		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("footerSignature", "");
		parameters.put("bottomSignText", "");
		parameters.put("contentFontSize", 11);
		parameters.put("headerLeftText", "");
		parameters.put("headerRightText", "");
		parameters.put("footerBottomText", "");
		parameters.put("logoURL", "");

		parameters.put("showTableOne", false);

		parameters.put("poweredBy", footerText);
		parameters.put("contentLineSpace", LineSpace.SMALL.name());
		response = jasperReportService.createPDF(ComponentType.LAB_REQUISATION_FORM, parameters,
				labRequisationFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
