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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalStage;
import com.dpdocter.beans.DentalStagejasperBean;
import com.dpdocter.beans.DentalToothNumber;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.DentalWorkInvoiceJasperResponse;
import com.dpdocter.beans.DentalWorksAmount;
import com.dpdocter.beans.DentalWorksInvoice;
import com.dpdocter.beans.DentalWorksInvoiceItem;
import com.dpdocter.beans.DentalWorksReceipt;
import com.dpdocter.beans.DentalWorksSample;
import com.dpdocter.beans.DoctorSignUp;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.InseptionReportJasperBean;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.LocationAndAccessControl;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.User;
import com.dpdocter.collections.CRNCollection;
import com.dpdocter.collections.CollectionBoyCollection;
import com.dpdocter.collections.CollectionBoyDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabDoctorAssociationCollection;
import com.dpdocter.collections.DentalLabPickupCollection;
import com.dpdocter.collections.DentalWorkCollection;
import com.dpdocter.collections.DentalWorksAmountCollection;
import com.dpdocter.collections.DentalWorksInvoiceCollection;
import com.dpdocter.collections.DentalWorksReceiptCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DynamicCollectionBoyAllocationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.RateCardDentalWorkAssociationCollection;
import com.dpdocter.collections.RateCardDoctorAssociationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TaxCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LabType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CRNRepository;
import com.dpdocter.repository.CollectionBoyDoctorAssociationRepository;
import com.dpdocter.repository.CollectionBoyRepository;
import com.dpdocter.repository.DentalLabDoctorAssociationRepository;
import com.dpdocter.repository.DentalLabTestPickupRepository;
import com.dpdocter.repository.DentalWorkRepository;
import com.dpdocter.repository.DentalWorksAmountRepository;
import com.dpdocter.repository.DentalWorksInvoiceRepository;
import com.dpdocter.repository.DentalWorksReceiptRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DynamicCollectionBoyAllocationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.RateCardDentalWorkAssociationRepository;
import com.dpdocter.repository.RateCardDoctorAssociationRepository;
import com.dpdocter.repository.TaxRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditTaxRequest;
import com.dpdocter.request.DentalLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalLabPickupChangeStatusRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.request.DentalStageRequest;
import com.dpdocter.request.DentalWorksSampleRequest;
import com.dpdocter.request.DoctorSignupRequest;
import com.dpdocter.request.UpdateDentalStagingRequest;
import com.dpdocter.request.UpdateETARequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;
import com.dpdocter.response.DentalWorksInvoiceResponse;
import com.dpdocter.response.DentalWorksReceiptResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.TaxResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.SignUpService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class DentalLabServiceImpl implements DentalLabService {

	@Autowired
	private DentalWorkRepository dentalWorkRepository;

	@Autowired
	private DentalLabTestPickupRepository dentalLabTestPickupRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private CRNRepository crnRepository;

	@Autowired
	private RateCardDentalWorkAssociationRepository rateCardDentalWorkAssociationRepository;

	@Autowired
	private RateCardDoctorAssociationRepository rateCardDoctorAssociationRepository;

	@Autowired
	private DentalLabDoctorAssociationRepository dentalLabDoctorAssociationRepository;

	@Autowired
	private CollectionBoyDoctorAssociationRepository collectionBoyDoctorAssociationRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private CollectionBoyRepository collectionBoyRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Autowired
	private DynamicCollectionBoyAllocationRepository dynamicCollectionBoyAllocationRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private TaxRepository taxRepository;

	@Autowired
	private SignUpService signUpService;

	@Autowired
	private DentalWorksInvoiceRepository dentalWorksInvoiceRepository;

	@Autowired
	private DentalWorksReceiptRepository dentalWorksReceiptRepository;

	@Autowired
	private DentalWorksAmountRepository dentalWorksAmountRepository;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Value("${collection.boy.notification}")
	private String COLLECTION_BOY_NOTIFICATION;

	@Value("${dental.lab.coping.trial.message}")
	private String COPING_TRIAL_NOTIFICATION;

	@Value("${dental.lab.bisque.trial.message}")
	private String BISQUE_TRIAL_NOTIFICATION;

	@Value("${dental.lab.finished.message}")
	private String FINISHED_LAB_NOTIFICATION;

	@Value("${dental.lab.cancelled.message}")
	private String CANCELLED_NOTIFICATION;

	@Value("${dental.lab.accepted.message}")
	private String ACCEPTED_NOTIFICATION;

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.dental.works.reports.fileName}")
	private String dentalWorksFormA4FileName;

	@Value(value = "${jasper.print.dental.works.invoice.fileName}")
	private String dentalWorksInvoiceA4FileName;

	@Value(value = "${jasper.print.dental.Inspection.reports.fileName}")
	private String dentalInspectionReportA4FileName;

	@Value(value = "${dental.lab.add.request.to.doctor}")
	private String dentalLabSMSToDoctor;

	@Value("${dental.lab.coping.trial.message.cb}")
	private String COPING_TRIAL_NOTIFICATION_CB;

	@Value("${dental.lab.bisque.trial.message.cb}")
	private String BISQUE_TRIAL_NOTIFICATION_CB;

	@Value("${dental.lab.finished.message.cb}")
	private String FINISHED_LAB_NOTIFICATION_CB;

	@Value(value = "${jasper.print.receipt.a4.fileName}")
	private String receiptA4FileName;

	@Value(value = "${jasper.print.receipt.a5.fileName}")
	private String receiptA5FileName;

	@Autowired
	private PatientVisitService patientVisitService;

	private static Logger logger = Logger.getLogger(DentalLabServiceImpl.class.getName());

	@Override
	@Transactional
	public DentalWork addEditCustomWork(AddEditCustomWorkRequest request) {
		DentalWork response = null;
		try {
			DentalWorkCollection dentalWorkCollection = new DentalWorkCollection();
			BeanUtil.map(request, dentalWorkCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getId())) {
				dentalWorkCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(dentalWorkCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(dentalWorkCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						dentalWorkCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					dentalWorkCollection.setCreatedBy("ADMIN");
				}
			} else {
				DentalWorkCollection oldDentalWorkCollection = dentalWorkRepository
						.findById(dentalWorkCollection.getId()).orElse(null);
				dentalWorkCollection.setCreatedBy(oldDentalWorkCollection.getCreatedBy());
				dentalWorkCollection.setCreatedTime(oldDentalWorkCollection.getCreatedTime());
				dentalWorkCollection.setDiscarded(oldDentalWorkCollection.getDiscarded());
			}
			dentalWorkCollection = dentalWorkRepository.save(dentalWorkCollection);
			response = new DentalWork();
			BeanUtil.map(dentalWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWork> getCustomWorks(long page, int size, String searchTerm) {
		List<DentalWork> customWorks = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("workName").regex("^" + searchTerm, "i"),
						new Criteria("workName").regex("^" + searchTerm));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalWork> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorkCollection.class, DentalWork.class);
			customWorks = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting custom works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting custom works");
		}
		return customWorks;
	}

	@Override
	@Transactional
	public DentalWork deleteCustomWork(String id, boolean discarded) {
		DentalWork response = null;
		DentalWorkCollection customWorkCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				customWorkCollection = dentalWorkRepository.findById(new ObjectId(id)).orElse(null);
			}
			if (customWorkCollection != null) {
				customWorkCollection.setDiscarded(discarded);
				customWorkCollection.setUpdatedTime(new Date());
				customWorkCollection = dentalWorkRepository.save(customWorkCollection);
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Record not found");
			}
			response = new DentalWork();
			BeanUtil.map(customWorkCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean changeLabType(String doctorId, String locationId, LabType labType) {
		Boolean response = false;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			doctorClinicProfileCollection = doctorClinicProfileRepository
					.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
			if (doctorClinicProfileCollection != null) {
				doctorClinicProfileCollection.setLabType(labType.getType());
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalLabDoctorAssociation addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		DentalLabDoctorAssociation response = null;
		try {
			DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
			BeanUtil.map(request, dentalLabDoctorAssociationCollection);
			if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
				dentalLabDoctorAssociationCollection.setCreatedTime(new Date());

			} else {
				DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
						.findById(dentalLabDoctorAssociationCollection.getId()).orElse(null);
				dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
				dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
			}
			dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
					.save(dentalLabDoctorAssociationCollection);
			response = new DentalLabDoctorAssociation();
			BeanUtil.map(dentalLabDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request) {
		Boolean response = null;
		try {
			for (DentalLabDoctorAssociation dentalLabDoctorAssociation : request) {
				DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
				BeanUtil.map(dentalLabDoctorAssociation, dentalLabDoctorAssociationCollection);
				if (DPDoctorUtils.anyStringEmpty(dentalLabDoctorAssociationCollection.getId())) {
					dentalLabDoctorAssociationCollection.setCreatedTime(new Date());

				} else {
					DentalLabDoctorAssociationCollection oldDentalLabDoctorAssociation = dentalLabDoctorAssociationRepository
							.findById(dentalLabDoctorAssociationCollection.getId()).orElse(null);
					dentalLabDoctorAssociationCollection.setCreatedBy(oldDentalLabDoctorAssociation.getCreatedBy());
					dentalLabDoctorAssociationCollection.setCreatedTime(oldDentalLabDoctorAssociation.getCreatedTime());
				}
				dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
						.save(dentalLabDoctorAssociationCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociations(String locationId,
			String doctorId, long page, int size, String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> responses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria = new Criteria().and("dentalLabLocationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}
			criteria.and("isActive").is(true);

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DentalLabDoctorAssociationCollection.class,
					DentalLabDoctorAssociationLookupResponse.class);
			responses = aggregationResults.getMappedResults();

			for (DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : responses) {
				doctorAssociationLookupResponse.getDoctor()
						.setLocationId(doctorAssociationLookupResponse.getLocationId());
				doctorAssociationLookupResponse.getDoctor()
						.setHospitalId(doctorAssociationLookupResponse.getHospitalId());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return responses;
	}

	@Override
	@Transactional
	public List<Location> getDentalLabDoctorAssociationsForDoctor(String doctorId, int page, int size,
			String searchTerm) {
		List<DentalLabDoctorAssociationLookupResponse> customWorks = null;
		List<Location> locations = new ArrayList<>();
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria = new Criteria().and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm));
			}
			criteria.and("dentalLab.isDentalWorksLab").is(true);
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<DentalLabDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, DentalLabDoctorAssociationCollection.class,
					DentalLabDoctorAssociationLookupResponse.class);
			customWorks = aggregationResults.getMappedResults();

			for (DentalLabDoctorAssociationLookupResponse doctorAssociationLookupResponse : customWorks) {
				locations.add(doctorAssociationLookupResponse.getDentalLab());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error getting dental lab doctor association");
			throw new BusinessException(ServiceError.Unknown, "Error getting dental lab doctor association");
		}
		return locations;
	}

	@Override
	@Transactional
	public DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request) {
		DentalLabPickup response = null;
		DentalLabPickupCollection dentalLabPickupCollection = null;
		String requestId = null;
		String locationInitials = "";
		Integer serialNo = null;
		Integer count = 1;
		List<DentalWorksSample> dentalWorksSamples = new ArrayList<>();
		List<DentalStage> dentalStages = null;

		try {
			LocationCollection locationCollection = locationRepository
					.findById(new ObjectId(request.getDentalLabLocationId())).orElse(null);
			if (locationCollection != null) {
				String locationName = locationCollection.getLocationName();

				for (String firstChar : locationName.split(" ")) {

					locationInitials += firstChar.charAt(0);

				}
			}
			if (request.getId() != null) {
				dentalLabPickupCollection = dentalLabTestPickupRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				if (dentalLabPickupCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalLabPickupCollection);
				for (DentalWorksSampleRequest dentalWorksSampleRequest : request.getDentalWorksSamples()) {
					DentalWorksSample dentalWorksSample = new DentalWorksSample();
					BeanUtil.map(dentalWorksSampleRequest, dentalWorksSample);
					dentalWorksSample.setDentalToothNumbers(dentalWorksSampleRequest.getDentalToothNumbers());
					if (dentalWorksSampleRequest.getDentalStagesForDoctor() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest
								.getDentalStagesForDoctor()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForDoctor(dentalStages);
					}

					if (dentalWorksSampleRequest.getDentalStagesForLab() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest.getDentalStagesForLab()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForLab(dentalStages);
					}
					dentalWorksSamples.add(dentalWorksSample);
				}

				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_LAB_PICKUP_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
				request.setCrn(saveCRN(request.getDentalLabLocationId(), requestId, 5));
				dentalLabPickupCollection = new DentalLabPickupCollection();
				BeanUtil.map(request, dentalLabPickupCollection);
				dentalLabPickupCollection.setRequestId(requestId);
				serialNo = dentalLabTestPickupRepository.findTodaysCompletedReport(
						new ObjectId(request.getDentalLabLocationId()), DPDoctorUtils.getFormTime(new Date()),
						DPDoctorUtils.getToTime(new Date()));
				for (DentalWorksSampleRequest dentalWorksSampleRequest : request.getDentalWorksSamples()) {
					String uniqueWorkId = locationInitials + getInitials(request.getPatientName())
							+ currentDateGenerator() + DPDoctorUtils.getPrefixedNumber(serialNo + 1)
							+ DPDoctorUtils.getPrefixedNumber(count);
					dentalWorksSampleRequest.setUniqueWorkId(uniqueWorkId);
					DentalWorksSample dentalWorksSample = new DentalWorksSample();
					BeanUtil.map(dentalWorksSampleRequest, dentalWorksSample);
					dentalWorksSample.setDentalToothNumbers(dentalWorksSampleRequest.getDentalToothNumbers());
					if (dentalWorksSampleRequest.getDentalStagesForDoctor() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest
								.getDentalStagesForDoctor()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForDoctor(dentalStages);
					}

					if (dentalWorksSampleRequest.getDentalStagesForLab() != null) {
						dentalStages = new ArrayList<>();
						for (DentalStageRequest dentalStageRequest : dentalWorksSampleRequest.getDentalStagesForLab()) {
							DentalStage dentalStage = new DentalStage();
							BeanUtil.map(dentalStageRequest, dentalStage);
							dentalStages.add(dentalStage);
						}
						dentalWorksSample.setDentalStagesForLab(dentalStages);
					}
					dentalWorksSamples.add(dentalWorksSample);

				}
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabPickupCollection.setCrn(saveCRN(request.getDentalLabLocationId(), requestId, 5));
				dentalLabPickupCollection.setSerialNumber(String.valueOf(serialNo + 1));
				dentalLabPickupCollection.setCreatedTime(new Date());
				dentalLabPickupCollection.setIsCompleted(false);
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setUpdatedTime(new Date());
			}

			DynamicCollectionBoyAllocationCollection dynamicCollectionBoyAllocationCollection = dynamicCollectionBoyAllocationRepository
					.findByAssignorIdAndAssigneeId(new ObjectId(request.getDentalLabLocationId()),
							new ObjectId(request.getDoctorId()));
			if (dynamicCollectionBoyAllocationCollection != null
					&& (dynamicCollectionBoyAllocationCollection.getFromTime() <= System.currentTimeMillis()
							&& System.currentTimeMillis() <= dynamicCollectionBoyAllocationCollection.getToTime())) {
				dentalLabPickupCollection
						.setCollectionBoyId(dynamicCollectionBoyAllocationCollection.getCollectionBoyId());

				CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
						.findById(dynamicCollectionBoyAllocationCollection.getCollectionBoyId()).orElse(null);
				pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null, null,
						RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);

			} else {
				CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.findByDentalLabIdAndDoctorIdAndIsActive(new ObjectId(request.getDentalLabLocationId()),
								new ObjectId(request.getDoctorId()), true);
				if (collectionBoyDoctorAssociationCollection != null) {
					dentalLabPickupCollection
							.setCollectionBoyId(collectionBoyDoctorAssociationCollection.getCollectionBoyId());

					CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
							.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId()).orElse(null);
					pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(), null, null,
							RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
				}
			}
			if (request.getRequestCreatedBy() != null) {
				if (request.getRequestCreatedBy().equals("DENTAL_LAB")) {
					List<UserDeviceCollection> userDeviceCollections = userDeviceRepository
							.findByUserIds(new ObjectId(request.getDoctorId()));
					if (userDeviceCollections != null) {
						String message = locationCollection.getLocationName() + " has created a request for you.";
						pushNotificationServices.notifyUser(request.getDoctorId(), message,
								ComponentType.DENTAL_WORKS.getType(), null, userDeviceCollections);
					} else {
						sendDownloadAppMessage(new ObjectId(request.getDoctorId()),
								locationCollection.getLocationName());
					}
				}
			}

			dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			response = new DentalLabPickup();
			BeanUtil.map(dentalLabPickupCollection, response);
			if (locationCollection != null) {
				Location location = new Location();
				BeanUtil.map(locationCollection, location);
				response.setDentalLab(location);
			}
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	private String saveCRN(String locationId, String requestId, Integer length) {
		CRNCollection crnCollection = new CRNCollection();
		String crnNumber = DPDoctorUtils.randomString(length);
		crnCollection.setCrnNumber(crnNumber);
		crnCollection.setLocationId(new ObjectId(locationId));
		crnCollection.setRequestId(requestId);
		crnCollection.setCreatedAt(System.currentTimeMillis());
		crnCollection.setIsUsed(false);
		crnCollection = crnRepository.save(crnCollection);
		return crnNumber;
	}

	@Override
	@Transactional
	public Boolean addEditRateCardDentalWorkAssociation(List<RateCardDentalWorkAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		RateCardDentalWorkAssociationCollection rateCardDentalWorkAssociationCollection = null;
		try {
			for (RateCardDentalWorkAssociation rateCardDentalWorkAssociation : request) {
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.findByLocationIdAndDentalWorkIdAndRateCardId(
								new ObjectId(rateCardDentalWorkAssociation.getLocationId()),
								new ObjectId(rateCardDentalWorkAssociation.getDentalWorkId()),
								new ObjectId(rateCardDentalWorkAssociation.getRateCardId()));
				if (rateCardDentalWorkAssociationCollection == null) {
					rateCardDentalWorkAssociationCollection = new RateCardDentalWorkAssociationCollection();
				} else {
					oldId = rateCardDentalWorkAssociationCollection.getId();
				}
				BeanUtil.map(rateCardDentalWorkAssociation, rateCardDentalWorkAssociationCollection);
				rateCardDentalWorkAssociationCollection.setId(oldId);
				rateCardDentalWorkAssociationCollection = rateCardDentalWorkAssociationRepository
						.save(rateCardDentalWorkAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm,
			String dentalLabId, String doctorId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository
					.getByDentalLabIdAndDoctorId(new ObjectId(dentalLabId), new ObjectId(doctorId));
			if (rateCardDoctorAssociationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Association not found");
			} else {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
							new Criteria("dentalWork.workName").regex("^" + searchTerm));
				}
				criteria.and("rateCardId").is(rateCardDoctorAssociationCollection.getRateCardId());
				criteria.and("isAvailable").is(true);
				// criteria.and("discarded").is(discarded);

			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDentalWorkAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDentalWorkAssociationCollection.class, RateCardDentalWorkAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public List<RateCardDentalWorkAssociation> getRateCardWorks(long page, int size, String searchTerm,
			String rateCardId, Boolean discarded) {
		List<RateCardDentalWorkAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalWork.workName").regex("^" + searchTerm, "i"),
						new Criteria("dentalWork.workName").regex("^" + searchTerm),
						new Criteria("dentalWork.workName").regex(searchTerm + ".*"));
			}
			criteria.and("rateCardId").is(new ObjectId(rateCardId));
			criteria.and("isAvailable").is(true);
			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("dental_work_cl", "dentalWorkId", "_id", "dentalWork"),
						Aggregation.unwind("dentalWork"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}

			AggregationResults<RateCardDentalWorkAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDentalWorkAssociationCollection.class, RateCardDentalWorkAssociation.class);

			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public RateCardDoctorAssociation addEditRateCardDoctorAssociation(RateCardDoctorAssociation request) {
		RateCardDoctorAssociation response = null;
		ObjectId oldId = null;
		RateCardDoctorAssociationCollection rateCardDoctorAssociationCollection = null;
		try {

			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository.getByDentalLabIdAndDoctorId(
					new ObjectId(request.getDentalLabId()), new ObjectId(request.getDoctorId()));
			if (rateCardDoctorAssociationCollection == null) {
				rateCardDoctorAssociationCollection = new RateCardDoctorAssociationCollection();
			} else {
				oldId = rateCardDoctorAssociationCollection.getId();
			}

			BeanUtil.map(request, rateCardDoctorAssociationCollection);
			rateCardDoctorAssociationCollection.setId(oldId);
			rateCardDoctorAssociationCollection = rateCardDoctorAssociationRepository
					.save(rateCardDoctorAssociationCollection);
			response = new RateCardDoctorAssociation();
			BeanUtil.map(rateCardDoctorAssociationCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<RateCardDoctorAssociation> getRateCards(int page, int size, String searchTerm, String doctorId,
			String dentalLabId, Boolean discarded) {
		List<RateCardDoctorAssociation> rateCardTests = null;

		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("rateCard.name").regex("^" + searchTerm, "i"),
						new Criteria("rateCard.name").regex("^" + searchTerm));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			criteria.and("discarded").is(discarded);

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("rate_card_cl", "rateCardId", "_id", "rateCard"),
						Aggregation.unwind("rateCard"), Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			}
			AggregationResults<RateCardDoctorAssociation> aggregationResults = mongoTemplate.aggregate(aggregation,
					RateCardDoctorAssociationCollection.class, RateCardDoctorAssociation.class);
			rateCardTests = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting rate card works");
			throw new BusinessException(ServiceError.Unknown, "Error Getting rate card works");
		}
		return rateCardTests;
	}

	@Override
	@Transactional
	public Boolean addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request) {
		Boolean response = null;
		CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = null;
		try {
			for (CollectionBoyDoctorAssociation collectionBoyDoctorAssociation : request) {
				if (DPDoctorUtils.anyStringEmpty(collectionBoyDoctorAssociation.getCollectionBoyId(),
						collectionBoyDoctorAssociation.getDentalLabId(),
						collectionBoyDoctorAssociation.getDoctorId())) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Input - Doctor & Dental Lab ID cannot be null");
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.findByDentalLabIdAndDoctorId(new ObjectId(collectionBoyDoctorAssociation.getDentalLabId()),
								new ObjectId(collectionBoyDoctorAssociation.getDoctorId()));
				if (collectionBoyDoctorAssociationCollection == null) {
					collectionBoyDoctorAssociationCollection = new CollectionBoyDoctorAssociationCollection();
					BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
				} else {
					if (!collectionBoyDoctorAssociationCollection.getCollectionBoyId()
							.equals(new ObjectId(collectionBoyDoctorAssociation.getCollectionBoyId()))
							&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId()).orElse(null);
						UserCollection userCollection = userRepository
								.findById(collectionBoyDoctorAssociationCollection.getDoctorId()).orElse(null);
						throw new BusinessException(ServiceError.Unknown,
								"Collection boy " + collectionBoyCollection.getName() + " is already assigned to Dr. "
										+ userCollection.getFirstName()
										+ ". Please select another lab / collection boy");
					}
					ObjectId oldId = collectionBoyDoctorAssociationCollection.getId();
					BeanUtil.map(collectionBoyDoctorAssociation, collectionBoyDoctorAssociationCollection);
					collectionBoyDoctorAssociationCollection.setId(oldId);
				}
				collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
						.save(collectionBoyDoctorAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<CBDoctorAssociationLookupResponse> getCBAssociatedDoctors(String doctorId, String dentalLabId,
			String collectionBoyId, int size, int page) {
		List<CBDoctorAssociationLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabId").is(new ObjectId(dentalLabId));
			}
			if (!DPDoctorUtils.anyStringEmpty(collectionBoyId)) {
				criteria.and("collectionBoyId").is(new ObjectId(collectionBoyId));
			}
			criteria.and("isActive").is(true);
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("location_cl", "dentalLabId", "_id", "dentalLab"),
						Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						Aggregation.unwind("doctor"), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<CBDoctorAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, CollectionBoyDoctorAssociationCollection.class,
					CBDoctorAssociationLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
		}
		return lookupResponses;
	}

	@Override
	@Transactional

	public List<DentalLabPickupResponse> getRequests(String dentalLabId, String doctorId, Long from, Long to,
			String searchTerm, String status, Boolean isAcceptedAtLab, Boolean isCompleted, Boolean isCollectedAtDoctor,
			int size, long page, Long fromETA, Long toETA, Boolean isTrailsRequired) {

		List<DentalLabPickupResponse> response = null;
		List<DentalLabPickupLookupResponse> lookupResponses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabId)) {
				criteria.and("dentalLabLocationId").is(new ObjectId(dentalLabId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isAcceptedAtLab != null) {
				criteria.and("isAcceptedAtLab").is(isAcceptedAtLab);
			}

			if (isCollectedAtDoctor != null) {
				criteria.and("isCollectedAtDoctor").is(isCollectedAtDoctor);
			}

			if (isCompleted != null) {
				criteria.and("isCompleted").is(isCompleted);
			}

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				criteria.and("status").is(status);
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}

			if (fromETA != null && toETA != null) {
				criteria.and("dentalWorksSamples.etaInDate").gte(fromETA).lte(toETA);
			}

			CustomAggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("patientId", new BasicDBObject("$first", "$patientId"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("dentalWorksSamples", new BasicDBObject("$push", "$dentalWorksSamples"))
							.append("gender", new BasicDBObject("$first", "$gender"))
							.append("age", new BasicDBObject("$first", "$age"))
							.append("crn", new BasicDBObject("$first", "$crn"))
							.append("pickupTime", new BasicDBObject("$first", "$pickupTime"))
							.append("deliveryTime", new BasicDBObject("$first", "$deliveryTime"))
							.append("status", new BasicDBObject("$first", "$status"))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
							.append("dentalLabLocationId", new BasicDBObject("$first", "$dentalLabLocationId"))
							.append("dentalLabHospitalId", new BasicDBObject("$first", "$dentalLabHospitalId"))
							.append("invoiceId", new BasicDBObject("$first", "$invoiceId"))
							.append("uniqueInvoiceId", new BasicDBObject("$first", "$uniqueInvoiceId"))
							.append("numberOfSamplesRequested",
									new BasicDBObject("$first", "$numberOfSamplesRequested"))
							.append("numberOfSamplesPicked", new BasicDBObject("$first", "$numberOfSamplesPicked"))
							.append("requestId", new BasicDBObject("$first", "$requestId"))
							.append("isCompleted", new BasicDBObject("$first", "$isCompleted"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))
							.append("isAcceptedAtLab", new BasicDBObject("$first", "$isAcceptedAtLab"))
							.append("isCollectedAtDoctor", new BasicDBObject("$first", "$isCollectedAtDoctor"))
							.append("collectionBoyId", new BasicDBObject("$first", "$collectionBoyId"))
							.append("serialNumber", new BasicDBObject("$first", "$serialNumber"))
							.append("reasonForCancel", new BasicDBObject("$first", "$reasonForCancel"))
							.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
							.append("collectionBoy", new BasicDBObject("$first", "$collectionBoy"))
							.append("dentalLab", new BasicDBObject("$first", "$dentalLab"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("doctor", new BasicDBObject("$first", "$doctor"))
							.append("feedBackRating", new BasicDBObject("$first", "$feedBackRating"))
							.append("feedBackComment", new BasicDBObject("$first", "$feedBackComment"))));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("dentalLab.locationName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex("^" + searchTerm, "i"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex("^" + searchTerm),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + "$", "i"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + "$"),
						new Criteria("dentalWorksSamples.uniqueWorkId").regex(searchTerm + ".*"));
			}

			/* (SEVEN) */
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.unwind("dentalWorksSamples"),
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",

								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.unwind("dentalWorksSamples"),
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),

						Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.match(criteria), aggregationOperation,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			lookupResponses = aggregationResults.getMappedResults();

			if (lookupResponses != null) {
				response = new ArrayList<>();

				for (DentalLabPickupLookupResponse dentalLabPickupLookupResponse : lookupResponses) {
					List<DentalStageRequest> dentalStageRequestsForDoctor = null;
					List<DentalStageRequest> dentalStageRequestsForLab = null;
					List<DentalWorksSampleRequest> dentalWorksSampleRequests = new ArrayList<>();
					DentalLabPickupResponse dentalLabPickupResponse = new DentalLabPickupResponse();
					BeanUtil.map(dentalLabPickupLookupResponse, dentalLabPickupResponse);

					for (DentalWorksSample dentalWorksSample : dentalLabPickupLookupResponse.getDentalWorksSamples()) {
						DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
						BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
						if (dentalWorksSample.getDentalStagesForDoctor() != null) {
							dentalStageRequestsForDoctor = new ArrayList<>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForDoctor.add(dentalStageRequest);
							}
						}
						if (dentalWorksSample.getDentalStagesForLab() != null) {
							dentalStageRequestsForLab = new ArrayList<>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForLab.add(dentalStageRequest);
							}
						}
						dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
						dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
						dentalWorksSampleRequests.add(dentalWorksSampleRequest);
						dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);
						response.add(dentalLabPickupResponse);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	private String currentDateGenerator() {

		String currentDate = null;
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			currentDate = DPDoctorUtils.getPrefixedNumber(currentDay) + DPDoctorUtils.getPrefixedNumber(currentMonth)
					+ DPDoctorUtils.getPrefixedNumber(currentYear % 100);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return currentDate;
	}

	private String getInitials(String inputString) {
		String response = "";
		for (String firstChar : inputString.split(" ")) {
			response += firstChar.charAt(0);
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean changeStatus(DentalLabPickupChangeStatusRequest request) {
		DentalLabPickupCollection dentalLabPickupCollection = null;
		Boolean response = null;

		try {
			dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(request.getDentalLabPickupId())).orElse(null);
			if (dentalLabPickupCollection != null) {
				if (request.getStatus() != null) {
					dentalLabPickupCollection.setStatus(request.getStatus());

					if (request.getStatus().equals("ACCEPTED")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {

							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									ACCEPTED_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);

						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
										.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId())
										.orElse(null);
								if (collectionBoyCollection != null
										&& collectionBoyCollection.getDiscarded() == false) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(),
											dentalLabPickupCollection.getId().toString(), null,
											RoleEnum.DENTAL_COLLECTION_BOY, COLLECTION_BOY_NOTIFICATION);
								}
							}
						}
					} else if (request.getStatus().equals("OUT_FOR_COLLECTION")) {

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									"Work is out for collection!", ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}
					} else if (request.getStatus().equals("COPING_TRIAL")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {

							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {

							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
										.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId())
										.orElse(null);
								if (collectionBoyCollection != null
										&& collectionBoyCollection.getDiscarded() == false) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION);
								}
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {

							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
										.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId())
										.orElse(null);
								if (collectionBoyCollection != null
										&& collectionBoyCollection.getDiscarded() == false) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION);
								}
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {

							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
										.findById(collectionBoyDoctorAssociationCollection.getCollectionBoyId())
										.orElse(null);
								if (collectionBoyCollection != null
										&& collectionBoyCollection.getDiscarded() == false) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, FINISHED_LAB_NOTIFICATION);
								}
							}
						}
					} else if (request.getStatus().equals("WORK_RECEIVED")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									"work received", ComponentType.REFRESH.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

					}

				}
				if (request.getIsCollectedAtDoctor() != null) {
					dentalLabPickupCollection.setIsCollectedAtDoctor(request.getIsCollectedAtDoctor());
				}
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				if (request.getIsAcceptedAtLab() != null) {
					dentalLabPickupCollection.setIsAcceptedAtLab(request.getIsAcceptedAtLab());
				}
				if (request.getFeedbackRating() != null) {
					dentalLabPickupCollection.setFeedBackRating(request.getFeedbackRating());
				}
				if (request.getFeedbackComment() != null) {
					dentalLabPickupCollection.setFeedBackComment(request.getFeedbackComment());
				}
				if (request.getDiscarded() != null) {
					dentalLabPickupCollection.setDiscarded(request.getDiscarded());
				}
				dentalLabPickupCollection.setUpdatedTime(new Date());
				dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public ImageURLResponse addDentalImage(FormDataBodyPart file) {
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "dental-images";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageURLResponse;
	}

	@Override
	@Transactional
	public ImageURLResponse addDentalImageBase64(FileDetails fileDetails) {
		ImageURLResponse imageURLResponse = null;
		try {
			Date createdTime = new Date();

			if (fileDetails != null) {
				// String path = "lab-reports";
				// String recordLabel = fileDetails.getFileName();
				fileDetails.setFileName(fileDetails.getFileName() + createdTime.getTime());
				String path = "dental-images";
				String recordPath = path + File.separator + fileDetails.getFileName() + System.currentTimeMillis() + "."
						+ fileDetails.getFileExtension();
				imageURLResponse = fileManager.saveImageAndReturnImageUrl(fileDetails, recordPath, true);
				if (imageURLResponse != null) {
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageURLResponse;
	}

	@Override
	@Transactional
	public Boolean updateDentalStageForDoctor(UpdateDentalStagingRequest request) {
		Boolean response = false;

		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(request.getRequestId())).orElse(null);
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();

				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {

					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						List<DentalStage> dentalStages = new ArrayList<>();
						if (request.getDentalStages() != null) {
							for (DentalStageRequest dentalStageRequest : request.getDentalStages()) {
								DentalStage dentalStage = new DentalStage();
								BeanUtil.map(dentalStageRequest, dentalStage);
								dentalStages.add(dentalStage);
							}
							dentalWorksSample.setDentalStagesForDoctor(dentalStages);
						}
						dentalWorksSample.setProcessStatus(request.getProcessStatus());

					}
				}
				dentalLabPickupCollection.setStatus(request.getStatus());
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				if (request.getStatus() != null) {
					if (request.getStatus().equals("COPING_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
										dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
										dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, FINISHED_LAB_NOTIFICATION_CB);
								}
							}
						}
					}
				}
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateDentalStageForLab(UpdateDentalStagingRequest request) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(request.getRequestId())).orElse(null);
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();
				dentalLabPickupCollection.setStatus(request.getStatus());

				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {
					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						List<DentalStage> dentalStages = new ArrayList<>();
						if (request.getDentalStages() != null) {
							for (DentalStageRequest dentalStageRequest : request.getDentalStages()) {
								DentalStage dentalStage = new DentalStage();
								BeanUtil.map(dentalStageRequest, dentalStage);
								dentalStages.add(dentalStage);
							}
							dentalWorksSample.setDentalStagesForLab(dentalStages);
						}
						dentalWorksSample.setProcessStatus(request.getProcessStatus());

					}
				}

				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				if (request.getIsCompleted() != null) {
					dentalLabPickupCollection.setIsCompleted(request.getIsCompleted());
				}
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				if (request.getStatus() != null && request.getIsTrialChanged() == true) {
					if (request.getStatus().equals("COPING_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									COPING_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
										dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, COPING_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("BISQUE_TRIAL")) {
						List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
								.findByLocationId(dentalLabPickupCollection.getDentalLabId());
						for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
							pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									BISQUE_TRIAL_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
								.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
						CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
								.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
										dentalLabPickupCollection.getDentalLabId(),
										dentalLabPickupCollection.getDoctorId(),
										dentalLabPickupCollection.getCollectionBoyId());
						if (collectionBoyDoctorAssociationCollection != null
								&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
							if (collectionBoyCollection != null) {
								pushNotificationServices.notifyPharmacy(collectionBoyCollection.getUserId().toString(),
										null, null, RoleEnum.DENTAL_COLLECTION_BOY, BISQUE_TRIAL_NOTIFICATION_CB);
							}
						}
					} else if (request.getStatus().equals("FINISHED_LAB")) {
						if (dentalLabPickupCollection.getDoctorId() != null) {
							pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
									FINISHED_LAB_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
									dentalLabPickupCollection.getId().toString(), null);
						}

						if (dentalLabPickupCollection.getCollectionBoyId() != null) {
							CollectionBoyCollection collectionBoyCollection = collectionBoyRepository
									.findById(dentalLabPickupCollection.getCollectionBoyId()).orElse(null);
							CollectionBoyDoctorAssociationCollection collectionBoyDoctorAssociationCollection = collectionBoyDoctorAssociationRepository
									.findByDentalLabIdAndDoctorIdAndCollectionBoyId(
											dentalLabPickupCollection.getDentalLabId(),
											dentalLabPickupCollection.getDoctorId(),
											dentalLabPickupCollection.getCollectionBoyId());
							if (collectionBoyDoctorAssociationCollection != null
									&& collectionBoyDoctorAssociationCollection.getIsActive() == true) {
								if (collectionBoyCollection != null) {
									pushNotificationServices.notifyPharmacy(
											collectionBoyCollection.getUserId().toString(), null, null,
											RoleEnum.DENTAL_COLLECTION_BOY, FINISHED_LAB_NOTIFICATION_CB);
								}
							}
						}
					}
				}
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean cancelRequest(String requestId, String reasonOfCancellation, String cancelledBy) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(requestId)).orElse(null);
			if (dentalLabPickupCollection != null) {
				// dentalLabPickupCollection.setStatus("CANCELLED");
				dentalLabPickupCollection.setReasonForCancel(reasonOfCancellation);
				dentalLabPickupCollection.setCancelledBy(cancelledBy);
				dentalLabPickupCollection.setDiscarded(true);
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				if (cancelledBy.equalsIgnoreCase("DOCTOR")) {
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(dentalLabPickupCollection.getDentalLabId());

					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								CANCELLED_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
								dentalLabPickupCollection.getId().toString(), null);
					}
				} else if (cancelledBy.equalsIgnoreCase("DENTAL_WORKS_LAB")) {
					pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
							CANCELLED_NOTIFICATION, ComponentType.DENTAL_WORKS.getType(),
							dentalLabPickupCollection.getId().toString(), null);
				}

				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateETA(UpdateETARequest request) {
		Boolean response = false;

		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(request.getRequestId())).orElse(null);
			if (dentalLabPickupCollection != null) {
				List<DentalWorksSample> dentalWorksSamples = dentalLabPickupCollection.getDentalWorksSamples();
				for (DentalWorksSample dentalWorksSample : dentalWorksSamples) {
					if (dentalWorksSample.getUniqueWorkId().equals(request.getUniqueWorkId())) {
						dentalWorksSample.setEtaInHour(request.getEtaInHour());
						dentalWorksSample.setEtaInDate(request.getEtaInDate());

					}
				}
				dentalLabPickupCollection.setDentalWorksSamples(dentalWorksSamples);
				dentalLabTestPickupRepository.save(dentalLabPickupCollection);
				pushNotificationServices.notifyUser(dentalLabPickupCollection.getDoctorId().toString(),
						"ETA for work has been updated.", ComponentType.DENTAL_WORKS.getType(),
						dentalLabPickupCollection.getId().toString(), null);

				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean discardRequest(String requestId, Boolean discarded) {
		Boolean response = false;
		try {
			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(requestId)).orElse(null);
			if (dentalLabPickupCollection != null) {
				dentalLabPickupCollection.setDiscarded(discarded);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalLabPickupResponse getRequestById(String id) {
		DentalLabPickupResponse dentalLabPickupResponse = null;
		DentalLabPickupLookupResponse dentalLabPickupLookupResponse = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			criteria.and("_id").in(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
					Aggregation.unwind("dentalLab"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"),
					Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria));
			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			dentalLabPickupLookupResponse = aggregationResults.getUniqueMappedResult();

			if (dentalLabPickupLookupResponse != null) {
				List<DentalStageRequest> dentalStageRequestsForDoctor = null;
				List<DentalStageRequest> dentalStageRequestsForLab = null;
				List<DentalWorksSampleRequest> dentalWorksSampleRequests = new ArrayList<>();
				dentalLabPickupResponse = new DentalLabPickupResponse();
				BeanUtil.map(dentalLabPickupLookupResponse, dentalLabPickupResponse);

				for (DentalWorksSample dentalWorksSample : dentalLabPickupLookupResponse.getDentalWorksSamples()) {
					DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
					BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
					if (dentalWorksSample.getDentalStagesForDoctor() != null) {
						dentalStageRequestsForDoctor = new ArrayList<>();
						for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
							DentalStageRequest dentalStageRequest = new DentalStageRequest();
							BeanUtil.map(dentalStage, dentalStageRequest);
							dentalStageRequestsForDoctor.add(dentalStageRequest);
						}
					}
					if (dentalWorksSample.getDentalStagesForLab() != null) {
						dentalStageRequestsForLab = new ArrayList<>();
						for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
							DentalStageRequest dentalStageRequest = new DentalStageRequest();
							BeanUtil.map(dentalStage, dentalStageRequest);
							dentalStageRequestsForLab.add(dentalStageRequest);
						}
					}
					dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
					dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
					dentalWorksSampleRequests.add(dentalWorksSampleRequest);
					dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Dental Lab Report By Ids");
		}
		return dentalLabPickupResponse;
	}

	@Override
	@Transactional
	public List<DentalLabPickupResponse> getRequestByIds(List<ObjectId> ids) {
		List<DentalLabPickupResponse> dentalLabPickupResponses = null;
		List<DentalLabPickupLookupResponse> dentalLabPickupLookupResponse = null;
		DentalLabPickupResponse dentalLabPickupResponse = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			criteria.and("_id").in(ids);

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("collection_boy_cl", "collectionBoyId", "_id", "collectionBoy"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$collectionBoy").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(criteria));
			AggregationResults<DentalLabPickupLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalLabPickupCollection.class, DentalLabPickupLookupResponse.class);
			dentalLabPickupLookupResponse = aggregationResults.getMappedResults();

			if (dentalLabPickupLookupResponse != null && !dentalLabPickupLookupResponse.isEmpty()) {
				List<DentalStageRequest> dentalStageRequestsForDoctor = null;
				List<DentalStageRequest> dentalStageRequestsForLab = null;
				List<DentalWorksSampleRequest> dentalWorksSampleRequests = null;
				dentalLabPickupResponses = new ArrayList<DentalLabPickupResponse>();
				for (DentalLabPickupLookupResponse labPickupLookupResponse : dentalLabPickupLookupResponse) {
					dentalWorksSampleRequests = new ArrayList<DentalWorksSampleRequest>();
					dentalLabPickupResponse = new DentalLabPickupResponse();
					BeanUtil.map(labPickupLookupResponse, dentalLabPickupResponse);

					for (DentalWorksSample dentalWorksSample : labPickupLookupResponse.getDentalWorksSamples()) {
						dentalStageRequestsForDoctor = null;
						DentalWorksSampleRequest dentalWorksSampleRequest = new DentalWorksSampleRequest();
						BeanUtil.map(dentalWorksSample, dentalWorksSampleRequest);
						if (dentalWorksSample.getDentalStagesForDoctor() != null) {
							dentalStageRequestsForDoctor = new ArrayList<DentalStageRequest>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForDoctor()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForDoctor.add(dentalStageRequest);
							}
						}
						if (dentalWorksSample.getDentalStagesForLab() != null) {
							dentalStageRequestsForLab = new ArrayList<DentalStageRequest>();
							for (DentalStage dentalStage : dentalWorksSample.getDentalStagesForLab()) {
								DentalStageRequest dentalStageRequest = new DentalStageRequest();
								BeanUtil.map(dentalStage, dentalStageRequest);
								dentalStageRequestsForLab.add(dentalStageRequest);
							}
						}
						dentalWorksSampleRequest.setDentalStagesForDoctor(dentalStageRequestsForDoctor);
						dentalWorksSampleRequest.setDentalStagesForLab(dentalStageRequestsForLab);
						dentalWorksSampleRequests.add(dentalWorksSampleRequest);
						dentalLabPickupResponse.setDentalWorksSamples(dentalWorksSampleRequests);

					}
					dentalLabPickupResponses.add(dentalLabPickupResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Dental Lab Report By Ids");
		}
		return dentalLabPickupResponses;
	}

	@Override
	public String downloadDentalLabReportPrint(String id, Boolean isInspectionReport) {
		String response = null;
		DentalLabPickupResponse dentalLabPickupResponse = null;
		JasperReportResponse jasperReportResponse = null;
		try {

			dentalLabPickupResponse = getRequestById(id);
			if (dentalLabPickupResponse == null) {
				throw new BusinessException(ServiceError.NoRecord, " No Lab Report found with ids");
			}
			if (isInspectionReport)
				jasperReportResponse = createInspectionReportJasper(dentalLabPickupResponse);
			else
				jasperReportResponse = createJasper(dentalLabPickupResponse);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Lab Report PDF for Parent");
			throw new BusinessException(ServiceError.Unknown, " Error while getting Lab Report PDF for Parent");
		}
		return response;

	}

	@Override
	public String downloadMultipleInspectionReportPrint(List<String> requestId) {
		String response = null;
		List<DentalLabPickupResponse> dentalLabPickupResponse = null;
		JasperReportResponse jasperReportResponse = null;
		List<ObjectId> objectIdList = null;
		try {
			if (requestId != null && !requestId.isEmpty()) {
				objectIdList = new ArrayList<ObjectId>();
				for (String id : requestId) {
					if (!DPDoctorUtils.anyStringEmpty(id)) {
						objectIdList.add(new ObjectId(id));
					}
				}
			}
			dentalLabPickupResponse = getRequestByIds(objectIdList);
			if (dentalLabPickupResponse == null || dentalLabPickupResponse.isEmpty()) {
				throw new BusinessException(ServiceError.NoRecord, " No Lab Report found with ids");
			}

			jasperReportResponse = createMultipleInspectionReportJasper(dentalLabPickupResponse);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Lab Report PDF for Parent");
			throw new BusinessException(ServiceError.Unknown, " Error while getting Lab Report PDF for Parent");
		}
		return response;

	}

	private JasperReportResponse createJasper(DentalLabPickupResponse dentalLabPickupResponse)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String locationId = null;// hospitalId = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;

		if (dentalLabPickupResponse.getDentalLab() != null) {
			locationId = dentalLabPickupResponse.getDentalLab().getId();
			labName = dentalLabPickupResponse.getDentalLab().getLocationName();
			parameters.put("dentalLab", "<b>Dental Lab :- </b> " + labName);
		} else {
			parameters.put("dentalLab", "<b>Dental Lab :- </b> ");
		}
		if (dentalLabPickupResponse.getDoctor() != null) {
			parameters.put("doctor", "<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
		} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
			userCollection = userRepository.findById(new ObjectId(dentalLabPickupResponse.getDoctorId())).orElse(null);
			if (userCollection != null)
				parameters.put("doctor", "<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
			else
				parameters.put("doctor", "<b>Doctor :- </b> ");
		} else {
			parameters.put("doctor", "<b>Doctor :- </b> ");

		}

		if (dentalLabPickupResponse.getPatientName() != null) {
			parameters.put("patientName", "<b>Patient Name :- </b> " + dentalLabPickupResponse.getPatientName());
		} else {
			parameters.put("patientName", "<b>Patient Name :- </b>  -- ");
		}

		if (dentalLabPickupResponse.getGender() != null) {
			parameters.put("gender", "<b>Gender :- </b> " + dentalLabPickupResponse.getGender());
		} else {
			parameters.put("gender", "<b>Gender :- </b> --");
		}

		if (dentalLabPickupResponse.getAge() != null) {
			parameters.put("age", "<b>Age :- </b> " + dentalLabPickupResponse.getGender());
		} else {
			parameters.put("age", "<b>Age :- </b> --");
		}

		if (dentalLabPickupResponse.getRequestId() != null) {
			parameters.put("requestId", "<b>Id :- </b> " + dentalLabPickupResponse.getRequestId());
		} else {
			parameters.put("requestId", "<b>Id :- </b>  --");
		}

		if (dentalLabPickupResponse.getDentalWorksSamples() != null
				&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
			String toothNumbers = "<br>";

			DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getCollarAndMetalDesign())) {
				parameters.put("collarAndMetalDesign",
						"<b>Collar and Metal Design :- </b> " + dentalWorksSample.getCollarAndMetalDesign());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getOcclusalStaining())) {
				parameters.put("occlusalStaining",
						"<b>Occlusal Design :- </b> " + dentalWorksSample.getOcclusalStaining());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getInstructions())) {
				parameters.put("instructions", "<b>Instructions :- </b> " + dentalWorksSample.getInstructions());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getPonticDesign())) {
				parameters.put("ponticDesign", "<b>Pontic Desing :- </b> " + dentalWorksSample.getPonticDesign());
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
				parameters.put("shade", "<b>Shade :- </b> " + dentalWorksSample.getShade());
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getUniqueWorkId())) {
				parameters.put("uniqueWorkId", "<b>Unique Work Id :- </b> " + dentalWorksSample.getUniqueWorkId());
			}
			if (dentalWorksSample.getDentalWork() != null) {
				parameters.put("dentalWork", "<b>Work Name :- </b> " + dentalWorksSample.getDentalWork().getWorkName());
			}

			if (dentalWorksSample.getDentalImages() != null) {
				parameters.put("dentalImages", dentalWorksSample.getDentalImages());
			}
			if (dentalWorksSample.getDentalWorkCardValues() != null) {
				parameters.put("dentalWorkCards", dentalWorksSample.getDentalWorkCardValues());
			}
			if (dentalWorksSample.getDentalStagesForDoctor() != null) {
				parameters.put("dentalStages", dentalWorksSample.getDentalStagesForDoctor());
			}

			if (dentalWorksSample.getMaterial() != null) {
				parameters.put("material",
						"<b>Material :- </b> " + StringUtils.join(dentalWorksSample.getMaterial(), ','));
			}

			if (dentalWorksSample.getEtaInDate() != null) {
				parameters.put("eta",
						"<b>ETA :- </b> " + simpleDateFormat.format(new Date(dentalWorksSample.getEtaInDate())));

			}

			if (dentalWorksSample.getProcessStatus() != null) {
				parameters.put("processingStatus", "<b> Status :- </b> " + dentalWorksSample.getProcessStatus());

			}
			if (dentalWorksSample.getDentalToothNumbers() != null) {
				for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
					toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
							+ dentalToothNumber.getType() + "<br>";
				}
				parameters.put("toothNumbers", "<b>ToothNumber :- </b> " + toothNumbers);

			}
			if (dentalWorksSample.getDentalStagesForDoctor() != null
					&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
				dentalStage = new ArrayList<DentalStagejasperBean>();
				DentalStagejasperBean stagejasperBean = null;
				for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {
					stagejasperBean = new DentalStagejasperBean();
					if (dentalStageRequest.getDeliveryTime() != null)
						stagejasperBean.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));

					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getAuthorisedPerson()))
						stagejasperBean.setInspectedBy(dentalStageRequest.getAuthorisedPerson());

					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage()))
						stagejasperBean.setProcess(dentalStageRequest.getStage());
					dentalStage.add(stagejasperBean);
				}

			}

			parameters.put("items", dentalStage);

		}

		parameters.put("title", "DENTAL WORKS REPORT");

		parameters.put("date", "<b>Date :- </b>" + simpleDateFormat.format(new Date()));

		String pdfName = locationId + "DENTAL-WORKS" + new Date().getTime();

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
		response = jasperReportService.createPDF(ComponentType.DENTAL_WORKS, parameters, dentalWorksFormA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private JasperReportResponse createInspectionReportJasper(DentalLabPickupResponse dentalLabPickupResponse)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String copingStage = "COPING";
		String bisqueStage = "BISQUE";
		String finalStage = "FINAL";
		String workId = " ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;
		if (dentalLabPickupResponse.getDentalLab() != null) {
			labName = dentalLabPickupResponse.getDentalLab().getLocationName();
			parameters.put("dentalLab", "<b>Dental Lab :- </b> " + labName);
		} else {
			parameters.put("dentalLab", "<b>Dental Lab :- </b> ");
		}

		if (dentalLabPickupResponse.getDoctor() != null) {
			parameters.put("doctor", "<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
		} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
			userCollection = userRepository.findById(new ObjectId(dentalLabPickupResponse.getDoctorId())).orElse(null);
			if (userCollection != null)
				parameters.put("doctor", "<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
			else

				parameters.put("doctor", "<b>Doctor :- </b> ");
		} else {
			parameters.put("doctor", "<b>Doctor :- </b> ");

		}
		if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getPatientName()))
			parameters.put("patientName", dentalLabPickupResponse.getPatientName());
		else
			parameters.put("patientName", "--");

		if (dentalLabPickupResponse.getDentalWorksSamples() != null
				&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
			String toothNumbers = "";

			DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

			if (dentalWorksSample.getDentalStagesForDoctor() != null
					&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
				for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {
					if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {
						if (dentalStageRequest.getStage().equalsIgnoreCase("COPING")) {
							copingStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								copingStage = copingStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}
						if (dentalStageRequest.getStage().equalsIgnoreCase("BISQUE")) {
							bisqueStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								bisqueStage = bisqueStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}
						if (dentalStageRequest.getStage().equalsIgnoreCase("FINAL")) {
							finalStage = dentalStageRequest.getStage();
							if (dentalStageRequest.getPickupTime() != null) {
								finalStage = finalStage + "("
										+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")";
							}
						}

					}

				}

			}

			if (!DPDoctorUtils.allStringsEmpty(dentalWorksSample.getUniqueWorkId())) {
				workId = dentalWorksSample.getUniqueWorkId();
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
				parameters.put("shade", dentalWorksSample.getShade());
			} else {
				parameters.put("shade", "--");
			}

			if (dentalWorksSample.getRateCardDentalWorkAssociation() != null) {
				if (dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork() != null) {
					parameters.put("dentalWork",
							!DPDoctorUtils.anyStringEmpty(
									dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName())
											? dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork()
													.getWorkName()
											: "--");
				} else {
					parameters.put("dentalWork", "--");
				}
			} else {
				parameters.put("dentalWork", "--");
			}
			if (dentalWorksSample.getMaterial() != null) {
				parameters.put("material", StringUtils.join(dentalWorksSample.getMaterial(), ','));
			} else {
				parameters.put("material", "--");
			}
			if (dentalWorksSample.getDentalToothNumbers() != null) {
				for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
					toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
							+ dentalToothNumber.getType() + "<br>";
				}
				parameters.put("toothNumbers", toothNumbers);

			} else {
				parameters.put("toothNumbers", "--");
			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getStatus())) {
				parameters.put("status", dentalLabPickupResponse.getStatus().replace('_', ' '));
			} else {
				parameters.put("status", "--");
			}
			dentalStage = new ArrayList<DentalStagejasperBean>();
			DentalStagejasperBean stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Model Preparation");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Wax Pattern");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Casting/Metal Finishing");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Buildup");
			dentalStage.add(stagejasperBean);
			stagejasperBean = new DentalStagejasperBean();
			stagejasperBean.setProcess("Final Finishing");
			dentalStage.add(stagejasperBean);

			if (dentalWorksSample.getDentalStagesForLab() != null
					&& !dentalWorksSample.getDentalStagesForLab().isEmpty()) {
				for (DentalStagejasperBean dentalStagejasperBean : dentalStage) {
					for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForLab()) {

						userCollection = null;

						if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {

							if (dentalStagejasperBean.getProcess().equalsIgnoreCase(dentalStageRequest.getStage())) {

								if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStaffId())) {
									userCollection = userRepository
											.findById(new ObjectId(dentalStageRequest.getStaffId())).orElse(null);
									if (userCollection != null) {
										dentalStagejasperBean.setInspectedBy(
												(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
														? userCollection.getTitle()
														: "") + " " + userCollection.getFirstName());
									}
								}
								if (dentalStageRequest.getDeliveryTime() != null) {
									dentalStagejasperBean
											.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));
								}

							}
						}
					}
				}
			}

			parameters.put("items", dentalStage);
		}
		if (!DPDoctorUtils.anyStringEmpty(workId)) {
			parameters.put("requestId", "<b>Work Id :- </b> " + workId);
		} else {
			parameters.put("requestId", "");
		}
		parameters.put("title", "INSPECTION REPORT");
		parameters.put("copingStage", copingStage);
		parameters.put("bisqueStage", bisqueStage);
		parameters.put("finalStage", finalStage);
		parameters.put("date",
				"<b>Work Date :- </b>" + simpleDateFormat.format(dentalLabPickupResponse.getUpdatedTime()));

		String pdfName = dentalLabPickupResponse.getId() + "-DENTAL-INSPECTION-REPORT-" + new Date().getTime();
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
		response = jasperReportService.createPDF(ComponentType.DENTAL_LAB_INSPECTION_REPORT, parameters,
				dentalWorksFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private JasperReportResponse createMultipleInspectionReportJasper(
			List<DentalLabPickupResponse> dentalLabPickupResponses) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		String labName = "";
		String locationId = null;// hospitalId = null;
		String workId = " ";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		UserCollection userCollection = null;
		List<DentalStagejasperBean> dentalStage = null;
		List<InseptionReportJasperBean> jasperBeans = new ArrayList<InseptionReportJasperBean>();
		InseptionReportJasperBean jasperBean = null;
		for (DentalLabPickupResponse dentalLabPickupResponse : dentalLabPickupResponses) {
			jasperBean = new InseptionReportJasperBean();

			if (dentalLabPickupResponse.getDentalLab() != null) {
				locationId = dentalLabPickupResponse.getDentalLab().getId();
				labName = dentalLabPickupResponse.getDentalLab().getLocationName();
				jasperBean.setDentalLab("<b>Dental Lab :- </b> " + labName);
			} else {
				jasperBean.setDentalLab("<b>Dental Lab :- </b> ");
			}

			if (dentalLabPickupResponse.getDoctor() != null) {
				jasperBean.setDoctor("<b>Doctor :- </b>Dr. " + dentalLabPickupResponse.getDoctor().getFirstName());
			} else if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getDoctorId())) {
				userCollection = userRepository.findById(new ObjectId(dentalLabPickupResponse.getDoctorId()))
						.orElse(null);
				if (userCollection != null)
					jasperBean.setDoctor("<b>Doctor :- </b>Dr. " + userCollection.getFirstName());
				else

					jasperBean.setDoctor("<b>Doctor :- </b> ");
			} else {
				jasperBean.setDoctor("<b>Doctor :- </b> ");

			}
			if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getPatientName()))
				jasperBean.setPatientName(dentalLabPickupResponse.getPatientName());
			else
				jasperBean.setPatientName("--");

			if (dentalLabPickupResponse.getDentalWorksSamples() != null
					&& !dentalLabPickupResponse.getDentalWorksSamples().isEmpty()) {
				String toothNumbers = "";

				DentalWorksSampleRequest dentalWorksSample = dentalLabPickupResponse.getDentalWorksSamples().get(0);

				if (dentalWorksSample.getDentalStagesForDoctor() != null
						&& !dentalWorksSample.getDentalStagesForDoctor().isEmpty()) {
					for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForDoctor()) {

						if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {
							if (dentalStageRequest.getStage().equalsIgnoreCase("COPING")) {
								jasperBean.setCopingStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setCopingStage(jasperBean.getCopingStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}
							if (dentalStageRequest.getStage().equalsIgnoreCase("BISQUE")) {
								jasperBean.setBisqueStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setBisqueStage(jasperBean.getBisqueStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}
							if (dentalStageRequest.getStage().equalsIgnoreCase("FINAL")) {
								jasperBean.setFinalStage(dentalStageRequest.getStage());
								if (dentalStageRequest.getPickupTime() != null) {
									jasperBean.setFinalStage(jasperBean.getFinalStage() + "("
											+ simpleDateFormat.format(dentalStageRequest.getPickupTime()) + ")");
								}
							}

						}

					}

				}

				if (!DPDoctorUtils.allStringsEmpty(dentalWorksSample.getUniqueWorkId())) {
					workId = dentalWorksSample.getUniqueWorkId();
				}

				if (!DPDoctorUtils.anyStringEmpty(dentalWorksSample.getShade())) {
					jasperBean.setShade(dentalWorksSample.getShade());
				} else {
					jasperBean.setShade("--");
				}

				if (dentalWorksSample.getRateCardDentalWorkAssociation() != null) {
					if (dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork() != null) {
						jasperBean.setDentalWork(!DPDoctorUtils.anyStringEmpty(
								dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName())
										? dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork()
												.getWorkName()
										: "--");
					} else {
						jasperBean.setDentalWork("--");
					}
				} else {
					jasperBean.setDentalWork("--");
				}
				if (dentalWorksSample.getMaterial() != null) {
					jasperBean.setMaterial(StringUtils.join(dentalWorksSample.getMaterial(), ','));
				} else {
					jasperBean.setMaterial("--");
				}
				if (dentalWorksSample.getDentalToothNumbers() != null) {
					for (DentalToothNumber dentalToothNumber : dentalWorksSample.getDentalToothNumbers()) {
						toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
								+ dentalToothNumber.getType();
					}
					jasperBean.setToothNumbers(toothNumbers);

				} else {
					jasperBean.setToothNumbers("--");
				}
				if (!DPDoctorUtils.anyStringEmpty(dentalLabPickupResponse.getStatus())) {
					jasperBean.setStatus(dentalLabPickupResponse.getStatus().replace('_', ' '));
				} else {
					jasperBean.setStatus("--");
				}
				dentalStage = new ArrayList<DentalStagejasperBean>();
				DentalStagejasperBean stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Model Preparation");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Wax Pattern");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Casting/Metal Finishing");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Buildup");
				dentalStage.add(stagejasperBean);
				stagejasperBean = new DentalStagejasperBean();
				stagejasperBean.setProcess("Final Finishing");
				dentalStage.add(stagejasperBean);

				if (dentalWorksSample.getDentalStagesForLab() != null
						&& !dentalWorksSample.getDentalStagesForLab().isEmpty()) {
					for (DentalStagejasperBean dentalStagejasperBean : dentalStage) {
						for (DentalStageRequest dentalStageRequest : dentalWorksSample.getDentalStagesForLab()) {

							userCollection = null;

							if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStage())) {

								if (dentalStagejasperBean.getProcess()
										.equalsIgnoreCase(dentalStageRequest.getStage())) {

									if (!DPDoctorUtils.anyStringEmpty(dentalStageRequest.getStaffId())) {
										userCollection = userRepository
												.findById(new ObjectId(dentalStageRequest.getStaffId())).orElse(null);
										if (userCollection != null) {
											dentalStagejasperBean.setInspectedBy(
													(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
															? userCollection.getTitle()
															: "") + " " + userCollection.getFirstName());
										}
									}
									if (dentalStageRequest.getDeliveryTime() != null) {
										dentalStagejasperBean
												.setDate(simpleDateFormat.format(dentalStageRequest.getDeliveryTime()));
									}

								}
							}
						}
					}
				}

				jasperBean.setItems(dentalStage);
			}
			if (!DPDoctorUtils.anyStringEmpty(workId)) {
				jasperBean.setRequestId("<b>Work Id :- </b> " + workId);
			} else {
				jasperBean.setRequestId("");
			}

			jasperBean.setDate(
					"<b>Work Date :- </b>" + simpleDateFormat.format(dentalLabPickupResponse.getUpdatedTime()));
			jasperBeans.add(jasperBean);

		}
		String pdfName = locationId + "DENTAL-INSPECTION-REPORT-" + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("details", jasperBeans);
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
		response = jasperReportService.createPDF(ComponentType.MULTIPLE_INSPECTION_REPORT, parameters,
				dentalWorksFormA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private void sendDownloadAppMessage(ObjectId doctorId, String locationName) {
		try {
			UserCollection userCollection = userRepository.findById(doctorId).orElse(null);

			if (userCollection != null) {
				String message = dentalLabSMSToDoctor;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(doctorId);
				smsTrackDetail.setType("APP_LINK_THROUGH_DENTAL_LAB");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(userCollection.getFirstName());
				sms.setSmsText(message.replace("{clinicName}", locationName));

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
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
		}

	}

	@Override
	@Transactional
	public TaxResponse addEditTax(AddEditTaxRequest request) {
		TaxResponse response = null;
		try {
			TaxCollection taxCollection = new TaxCollection();
			BeanUtil.map(request, taxCollection);
			if (DPDoctorUtils.anyStringEmpty(taxCollection.getId())) {
				taxCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(taxCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(taxCollection.getDoctorId()).orElse(null);
					if (userCollection != null) {
						taxCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					taxCollection.setCreatedBy("ADMIN");
				}
			} else {
				TaxCollection oldTaxCollection = taxRepository.findById(taxCollection.getId()).orElse(null);
				taxCollection.setCreatedBy(oldTaxCollection.getCreatedBy());
				taxCollection.setCreatedTime(oldTaxCollection.getCreatedTime());
				taxCollection.setDiscarded(oldTaxCollection.getDiscarded());
			}
			taxCollection = taxRepository.save(taxCollection);
			response = new TaxResponse();
			BeanUtil.map(taxCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional

	public Boolean dentalLabDoctorRegistration(DentalLabDoctorRegistrationRequest request) {
		Boolean response = false;
		LocationAndAccessControl locationAndAccessControl = null;

		try {
			DoctorSignupRequest doctorSignupRequest = new DoctorSignupRequest();
			BeanUtil.map(request, doctorSignupRequest);
			DoctorSignUp doctorSignUp = signUpService.doctorSignUp(doctorSignupRequest);
			if (doctorSignUp != null) {
				DentalLabDoctorAssociationCollection dentalLabDoctorAssociationCollection = new DentalLabDoctorAssociationCollection();
				dentalLabDoctorAssociationCollection
						.setDentalLabLocationId(new ObjectId(request.getDentalLablocationId()));
				dentalLabDoctorAssociationCollection
						.setDentalLabHospitalId(new ObjectId(request.getDentalLabHospitalId()));
				dentalLabDoctorAssociationCollection.setDoctorId(new ObjectId(doctorSignUp.getUser().getId()));
				if (doctorSignUp.getHospital() != null) {
					if (doctorSignUp.getHospital().getLocationsAndAccessControl().size() > 0) {
						locationAndAccessControl = doctorSignUp.getHospital().getLocationsAndAccessControl().get(0);
						dentalLabDoctorAssociationCollection
								.setLocationId(new ObjectId(locationAndAccessControl.getId()));
						dentalLabDoctorAssociationCollection
								.setHospitalId(new ObjectId(locationAndAccessControl.getHospitalId()));
					}
				}
				dentalLabDoctorAssociationCollection.setIsActive(true);
				dentalLabDoctorAssociationCollection = dentalLabDoctorAssociationRepository
						.save(dentalLabDoctorAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public String downloadDentalWorkInvoice(String invoiceId) {

		String response = null;
		JasperReportResponse jasperReportResponse = null;
		DentalWorksInvoiceCollection dentalWorksInvoiceCollection = null;
		try {

			dentalWorksInvoiceCollection = dentalWorksInvoiceRepository.findById(new ObjectId(invoiceId)).orElse(null);

			if (dentalWorksInvoiceCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, " No Dental Work Invoivce found with id");
			}

			jasperReportResponse = createDentalWorkInvoiceJasper(dentalWorksInvoiceCollection);

			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Dental Work Invoice PDF for Parent");
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Dental Work Invoice PDF for Parent");
		}
		return response;

	}

	private JasperReportResponse createDentalWorkInvoiceJasper(
			DentalWorksInvoiceCollection dentalWorksInvoiceCollection) throws NumberFormatException, IOException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		PrintSettingsCollection printSettings = null;
		ObjectId locationId = dentalWorksInvoiceCollection.getLocationId();
		ObjectId doctorId = dentalWorksInvoiceCollection.getDoctorId();
		Double grantTotal = 0.0;
		String doctorName = "";
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		String toothNumbers = "";
		List<DentalWorkInvoiceJasperResponse> dentalWorkInvoiceJasperResponses = new ArrayList<DentalWorkInvoiceJasperResponse>();
		DentalWorkInvoiceJasperResponse dentalWorkInvoiceJasperResponse = null;
		for (DentalWorksInvoiceItem dentalWorksInvoiceItem : dentalWorksInvoiceCollection
				.getDentalWorksInvoiceItems()) {
			toothNumbers = "";
			dentalWorkInvoiceJasperResponse = new DentalWorkInvoiceJasperResponse();
			dentalWorkInvoiceJasperResponse.setOrderDate(dentalWorksInvoiceItem.getCreatedTime() != null
					? simpleDateFormat.format(dentalWorksInvoiceItem.getCreatedTime())
					: "--");
			dentalWorkInvoiceJasperResponse.setPatientName(dentalWorksInvoiceCollection.getPatientName());
			dentalWorkInvoiceJasperResponse.setMaterial(dentalWorksInvoiceItem.getWorkName());
			dentalWorkInvoiceJasperResponse.setRate(dentalWorksInvoiceItem.getFinalCost());
			if (dentalWorksInvoiceItem.getDentalToothNumbers() != null
					&& !dentalWorksInvoiceItem.getDentalToothNumbers().isEmpty()) {
				for (DentalToothNumber dentalToothNumber : dentalWorksInvoiceItem.getDentalToothNumbers()) {
					toothNumbers = toothNumbers + StringUtils.join(dentalToothNumber.getToothNumber(), ',') + " - "
							+ dentalToothNumber.getType();
				}
				dentalWorkInvoiceJasperResponse.setTeethNo(toothNumbers);

			} else {
				dentalWorkInvoiceJasperResponse.setTeethNo("--");
			}
			dentalWorkInvoiceJasperResponse.setTotal(dentalWorksInvoiceCollection.getTotalCost());
			dentalWorkInvoiceJasperResponse.setsNo(1);
			dentalWorkInvoiceJasperResponses.add(dentalWorkInvoiceJasperResponse);
		}
		parameters.put("items", dentalWorkInvoiceJasperResponses);
		LocationCollection location = locationRepository.findById(locationId).orElse(null);
		UserCollection doctor = userRepository.findById(doctorId).orElse(null);

		doctorName = "<b>" + (!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() : "") + " "
				+ doctor.getFirstName() + "</b><br>" + location.getLocationName() + ",<br>" + location.getCity()
				+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? ",<br>" + location.getState() : "");
		parameters.put("title", "INVOICE");
		grantTotal = dentalWorksInvoiceCollection.getTotalCost();
		parameters.put("grandTotal", "Total : " + grantTotal + " INR");
		parameters.put("doctor", doctorName);
		parameters.put("invoiceId", "<b>InvoiceId : </b>" + dentalWorksInvoiceCollection.getUniqueInvoiceId());
		parameters.put("date", "<b>Date : </b>" + simpleDateFormat.format(new Date()));

		printSettings = printSettingsRepository.findByLocationIdAndHospitalId(
				dentalWorksInvoiceCollection.getLocationId(), dentalWorksInvoiceCollection.getHospitalId());

		if (printSettings == null) {

			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);

		}

		patientVisitService.generatePrintSetup(parameters, printSettings, null);
		parameters.put("followUpAppointment", null);

		String pdfName = "DENTALINVOICE-" + dentalWorksInvoiceCollection.getUniqueInvoiceId() + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("followUpAppointment", null);
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
		response = jasperReportService.createPDF(ComponentType.DENTAL_WORK_INVOICE, parameters,
				dentalWorksInvoiceA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@Override
	@Transactional
	public DentalWorksInvoice addEditInvoice(DentalWorksInvoice request) {
		DentalWorksInvoice response = null;
		List<DentalWorksInvoiceItem> invoiceItems = null;
		DentalWorksAmountCollection dentalWorksAmountCollection = null;
		try {
			DentalWorksInvoiceCollection dentalWorksInvoiceCollection = new DentalWorksInvoiceCollection();
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, dentalWorksInvoiceCollection);

				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getDentalLabLocationId())).orElse(null);
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				dentalWorksInvoiceCollection.setUniqueInvoiceId(locationCollection.getInvoiceInitial()
						+ ((int) mongoTemplate.count(new Query(new Criteria("dentalLabLocationId")
								.is(dentalWorksInvoiceCollection.getDentalLabLocationId()).and("dentalLabHospitalId")
								.is(dentalWorksInvoiceCollection.getDentalLabHospitalId())),
								DentalWorksInvoiceCollection.class) + 1));

				dentalWorksInvoiceCollection.setBalanceAmount(request.getGrandTotal());
				if (dentalWorksInvoiceCollection.getInvoiceDate() == null)
					dentalWorksInvoiceCollection.setInvoiceDate(new Date());
				if (request.getCreatedTime() == null) {
					dentalWorksInvoiceCollection.setCreatedTime(new Date());
				}
				dentalWorksInvoiceCollection.setAdminCreatedTime(new Date());

				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
								new ObjectId(request.getHospitalId()), new ObjectId(request.getDentalLabLocationId()),
								new ObjectId(request.getDentalLabHospitalId()));

				if (dentalWorksAmountCollection == null) {
					dentalWorksAmountCollection = new DentalWorksAmountCollection();
					BeanUtil.map(request, dentalWorksAmountCollection);
					dentalWorksAmountCollection.setRemainingAmount(request.getTotalCost());
				} else {
					dentalWorksAmountCollection.setRemainingAmount(
							dentalWorksAmountCollection.getRemainingAmount() + request.getTotalCost());
				}

				dentalWorksAmountRepository.save(dentalWorksAmountCollection);

			} else {
				dentalWorksInvoiceCollection = dentalWorksInvoiceRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				Double OldCost = dentalWorksInvoiceCollection.getTotalCost();
				BeanUtil.map(request, dentalWorksInvoiceCollection);
				if (request.getCreatedTime() != null) {
					dentalWorksInvoiceCollection.setCreatedTime(request.getCreatedTime());
				}

				dentalWorksInvoiceCollection.setUpdatedTime(new Date());
				dentalWorksInvoiceCollection.setTotalCost(request.getTotalCost());
				dentalWorksInvoiceCollection.setTotalDiscount(request.getTotalDiscount());
				dentalWorksInvoiceCollection.setTotalTax(request.getTotalTax());
				dentalWorksInvoiceCollection.setGrandTotal(request.getGrandTotal());

				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
								new ObjectId(request.getHospitalId()), new ObjectId(request.getDentalLabLocationId()),
								new ObjectId(request.getDentalLabHospitalId()));

				if (dentalWorksAmountCollection != null) {
					dentalWorksAmountCollection
							.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount() - OldCost);
					dentalWorksAmountCollection.setRemainingAmount(
							dentalWorksAmountCollection.getRemainingAmount() + request.getTotalCost());

				}
				dentalWorksAmountRepository.save(dentalWorksAmountCollection);

			}

			DentalLabPickupCollection dentalLabPickupCollection = dentalLabTestPickupRepository
					.findById(new ObjectId(request.getDentalWorksId())).orElse(null);
			if (dentalLabPickupCollection != null) {
				invoiceItems = new ArrayList<DentalWorksInvoiceItem>();
				for (DentalWorksSample dentalWorksSample : dentalLabPickupCollection.getDentalWorksSamples()) {
					DentalWorksInvoiceItem dentalWorksInvoiceItem = new DentalWorksInvoiceItem();
					dentalWorksInvoiceItem.setCost(dentalWorksSample.getCost());
					dentalWorksInvoiceItem.setWorkId(
							new ObjectId(dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWorkId()));

					dentalWorksInvoiceItem.setDentalToothNumbers(dentalWorksSample.getDentalToothNumbers());
					dentalWorksInvoiceItem.setWorkName(
							dentalWorksSample.getRateCardDentalWorkAssociation().getDentalWork().getWorkName());
					dentalWorksInvoiceItem.setCreatedTime(dentalLabPickupCollection.getCreatedTime());
					invoiceItems.add(dentalWorksInvoiceItem);
				}
			}

			dentalWorksInvoiceCollection.setDentalWorksInvoiceItems(invoiceItems);
			dentalWorksInvoiceCollection = dentalWorksInvoiceRepository.save(dentalWorksInvoiceCollection);
			dentalLabPickupCollection.setInvoiceId(dentalWorksInvoiceCollection.getId());
			dentalLabPickupCollection.setUniqueInvoiceId(dentalWorksInvoiceCollection.getUniqueInvoiceId());
			dentalLabPickupCollection = dentalLabTestPickupRepository.save(dentalLabPickupCollection);
			response = new DentalWorksInvoice();
			BeanUtil.map(dentalWorksInvoiceCollection, response);
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while adding invoice" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksReceipt addEditReceipt(DentalWorksReceipt request) {
		DentalWorksReceipt response = null;
		DentalWorksAmountCollection dentalWorksAmountCollection = null;
		Double oldCost = 0.0;

		try {
			DentalWorksReceiptCollection dentalWorksReceiptCollection = new DentalWorksReceiptCollection();

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, dentalWorksReceiptCollection);

				LocationCollection locationCollection = locationRepository
						.findById(new ObjectId(request.getDentalLabLocationId())).orElse(null);
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				dentalWorksReceiptCollection.setUniqueReceiptId(locationCollection.getReceiptInitial()
						+ ((int) mongoTemplate.count(new Query(new Criteria("dentalLabLocationId")
								.is(dentalWorksReceiptCollection.getDentalLabLocationId()).and("dentalLabHospitalId")
								.is(dentalWorksReceiptCollection.getDentalLabHospitalId())),
								DentalWorksReceiptCollection.class) + 1));
				if (dentalWorksReceiptCollection.getReceivedDate() == null) {
					dentalWorksReceiptCollection.setReceivedDate(System.currentTimeMillis());
				}
				dentalWorksReceiptCollection.setCreatedTime(new Date());
				dentalWorksReceiptCollection.setAdminCreatedTime(new Date());
				dentalWorksReceiptCollection.setUpdatedTime(new Date());

			} else {
				dentalWorksReceiptCollection = dentalWorksReceiptRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
				oldCost = dentalWorksReceiptCollection.getAmountPaid();
				BeanUtil.map(request, dentalWorksReceiptCollection);
				dentalWorksReceiptCollection.setUpdatedTime(new Date());
			}

			dentalWorksAmountCollection = dentalWorksAmountRepository
					.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
							new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
							new ObjectId(request.getHospitalId()), new ObjectId(request.getDentalLabLocationId()),
							new ObjectId(request.getDentalLabHospitalId()));

			if (dentalWorksAmountCollection != null) {
				dentalWorksAmountCollection.setRemainingAmount(
						dentalWorksAmountCollection.getRemainingAmount() - request.getAmountPaid() + oldCost);
				dentalWorksAmountRepository.save(dentalWorksAmountCollection);
				dentalWorksReceiptCollection.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount());
				dentalWorksReceiptCollection = dentalWorksReceiptRepository.save(dentalWorksReceiptCollection);
			}

			response = new DentalWorksReceipt();
			BeanUtil.map(dentalWorksReceiptCollection, response);
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while adding receipt" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding receipt" + e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWorksInvoiceResponse> getInvoices(String doctorId, String locationId, String hospitalId,
			String dentalLabLocationId, String dentalLabHospitalId, Long from, Long to, String searchTerm, int size,
			long page) {

		List<DentalWorksInvoiceResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabLocationId)) {
				criteria.and("dentalLabLocationId").is(new ObjectId(dentalLabLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalLabHospitalId)) {
				criteria.and("dentalLabHospitalId").is(new ObjectId(dentalLabHospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("dentalLab.locationName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("uniqueInvoiceId").regex("^" + searchTerm, "i"),
						new Criteria("uniqueInvoiceId").regex("^" + searchTerm),
						new Criteria("uniqueInvoiceId").regex(searchTerm + "$", "i"),
						new Criteria("uniqueInvoiceId").regex(searchTerm + "$"),
						new Criteria("uniqueInvoiceId").regex(searchTerm + ".*"));
			}

			/* (SEVEN) */
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinic").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));

			else
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",

								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinic").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$doctor").append("preserveNullAndEmptyArrays", true))),

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalWorksInvoiceResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorksInvoiceCollection.class, DentalWorksInvoiceResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalWorksReceiptResponse> getReceipts(String doctorId, String locationId, String hospitalId,
			String dentalLabLocationId, String dentalLabHospitalId, Long from, Long to, String searchTerm, int size,
			long page) {

		List<DentalWorksReceiptResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabLocationId)) {
				criteria.and("dentalLabLocationId").is(new ObjectId(dentalLabLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalLabHospitalId)) {
				criteria.and("dentalLabHospitalId").is(new ObjectId(dentalLabHospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("dentalLab.locationName").regex("^" + searchTerm, "i"),
						new Criteria("dentalLab.locationName").regex("^" + searchTerm),
						new Criteria("dentalLab.locationName").regex(searchTerm + ".*"),
						new Criteria("doctor.firstName").regex("^" + searchTerm, "i"),
						new Criteria("doctor.firstName").regex("^" + searchTerm),
						new Criteria("doctor.firstName").regex(searchTerm + ".*"),
						new Criteria("patientName").regex("^" + searchTerm, "i"),
						new Criteria("patientName").regex("^" + searchTerm),
						new Criteria("patientName").regex(searchTerm + ".*"),
						new Criteria("uniqueReceiptId").regex("^" + searchTerm, "i"),
						new Criteria("uniqueReceiptId").regex("^" + searchTerm),
						new Criteria("uniqueReceiptId").regex(searchTerm + "$", "i"),
						new Criteria("uniqueReceiptId").regex(searchTerm + "$"),
						new Criteria("uniqueReceiptId").regex(searchTerm + ".*"));
			}

			/* (SEVEN) */
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinic").append("preserveNullAndEmptyArrays", true))),
						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						// Aggregation.unwind("dentalWorksSamples.dentalStagesForDoctor"),
						Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$dentalLab").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$clinic").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalWorksReceiptResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorksReceiptCollection.class, DentalWorksReceiptResponse.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksInvoiceResponse getInvoiceById(String id) {
		DentalWorksInvoiceResponse response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			criteria.and("_id").in(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
					Aggregation.unwind("dentalLab"), Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
					Aggregation.unwind("clinic"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria));
			AggregationResults<DentalWorksInvoiceResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorksInvoiceCollection.class, DentalWorksInvoiceResponse.class);
			response = aggregationResults.getUniqueMappedResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Invoice By Ids");
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksReceiptResponse getReceiptById(String id) {
		DentalWorksReceiptResponse response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			criteria.and("_id").in(new ObjectId(id));

			aggregation = Aggregation.newAggregation(
					Aggregation.lookup("location_cl", "dentalLabLocationId", "_id", "dentalLab"),
					Aggregation.unwind("dentalLab"), Aggregation.lookup("location_cl", "locationId", "_id", "clinic"),
					Aggregation.unwind("clinic"), Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.match(criteria));
			AggregationResults<DentalWorksReceiptResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorksReceiptCollection.class, DentalWorksReceiptResponse.class);
			response = aggregationResults.getUniqueMappedResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Receipt By Ids");
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksInvoice discardInvoice(String id, Boolean discarded) {
		DentalWorksInvoice response = null;
		DentalWorksInvoiceCollection dentalWorksInvoiceCollection = null;
		DentalWorksAmountCollection dentalWorksAmountCollection = null;
		try {
			dentalWorksInvoiceCollection = dentalWorksInvoiceRepository.findById(new ObjectId(id)).orElse(null);
			if (dentalWorksInvoiceCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			dentalWorksInvoiceCollection.setDiscarded(discarded);
			dentalWorksInvoiceCollection = dentalWorksInvoiceRepository.save(dentalWorksInvoiceCollection);

			if (discarded.equals(Boolean.TRUE)) {
				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								dentalWorksInvoiceCollection.getDoctorId(),
								dentalWorksInvoiceCollection.getLocationId(),
								dentalWorksInvoiceCollection.getHospitalId(),
								dentalWorksInvoiceCollection.getDentalLabLocationId(),
								dentalWorksInvoiceCollection.getDentalLabHospitalId());

				if (dentalWorksAmountCollection != null) {
					dentalWorksAmountCollection.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount()
							- dentalWorksInvoiceCollection.getTotalCost());
				}
				dentalWorksAmountRepository.save(dentalWorksAmountCollection);

			} else if (discarded.equals(Boolean.FALSE)) {
				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								dentalWorksInvoiceCollection.getDoctorId(),
								dentalWorksInvoiceCollection.getLocationId(),
								dentalWorksInvoiceCollection.getHospitalId(),
								dentalWorksInvoiceCollection.getDentalLabLocationId(),
								dentalWorksInvoiceCollection.getDentalLabHospitalId());

				if (dentalWorksAmountCollection != null) {
					dentalWorksAmountCollection.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount()
							+ dentalWorksInvoiceCollection.getTotalCost());
					dentalWorksAmountRepository.save(dentalWorksAmountCollection);
				}
			}

			response = new DentalWorksInvoice();
			BeanUtil.map(dentalWorksInvoiceCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Invoice By Id");
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksReceipt discardReceipt(String id, Boolean discarded) {
		DentalWorksReceipt response = null;
		DentalWorksReceiptCollection dentalWorksReceiptCollection = null;
		DentalWorksAmountCollection dentalWorksAmountCollection = null;
		try {
			dentalWorksReceiptCollection = dentalWorksReceiptRepository.findById(new ObjectId(id)).orElse(null);
			if (dentalWorksReceiptCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			dentalWorksReceiptCollection.setDiscarded(discarded);
			dentalWorksReceiptRepository.save(dentalWorksReceiptCollection);

			if (discarded.equals(Boolean.TRUE)) {
				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								dentalWorksReceiptCollection.getDoctorId(),
								dentalWorksReceiptCollection.getLocationId(),
								dentalWorksReceiptCollection.getHospitalId(),
								dentalWorksReceiptCollection.getDentalLabLocationId(),
								dentalWorksReceiptCollection.getDentalLabHospitalId());

				if (dentalWorksAmountCollection != null) {
					dentalWorksAmountCollection.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount()
							+ dentalWorksReceiptCollection.getAmountPaid());
				}
				dentalWorksAmountRepository.save(dentalWorksAmountCollection);

			} else if (discarded.equals(Boolean.FALSE)) {
				dentalWorksAmountCollection = dentalWorksAmountRepository
						.findByDoctorIdAndLocationIdAndHospitalIdAndDentalLabLocationIdAndDentalLabHospitalId(
								dentalWorksReceiptCollection.getDoctorId(),
								dentalWorksReceiptCollection.getLocationId(),
								dentalWorksReceiptCollection.getHospitalId(),
								dentalWorksReceiptCollection.getDentalLabLocationId(),
								dentalWorksReceiptCollection.getDentalLabHospitalId());

				if (dentalWorksAmountCollection != null) {
					dentalWorksAmountCollection.setRemainingAmount(dentalWorksAmountCollection.getRemainingAmount()
							- dentalWorksReceiptCollection.getAmountPaid());
					dentalWorksAmountRepository.save(dentalWorksAmountCollection);
				}

			}
			response = new DentalWorksReceipt();
			BeanUtil.map(dentalWorksReceiptCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, " Error while getting Invoice By Id");
		}
		return response;
	}

	@Override
	@Transactional
	public DentalWorksAmount getAmount(String doctorId, String locationId, String hospitalId,
			String dentalLabLocationId, String dentalLabHospitalId) {

		DentalWorksAmount response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(dentalLabLocationId)) {
				criteria.and("dentalLabLocationId").is(new ObjectId(dentalLabLocationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(dentalLabHospitalId)) {
				criteria.and("dentalLabHospitalId").is(new ObjectId(dentalLabHospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalWorksAmount> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalWorksAmountCollection.class, DentalWorksAmount.class);
			response = aggregationResults.getUniqueMappedResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public String downloadDentalLabReceipt(String receiptId) {
		String response = null;
		JasperReportResponse jasperReportResponse = null;

		try {
			DentalWorksReceiptResponse receiptResponse = getReceiptById(receiptId);
			if (receiptResponse == null) {
				throw new BusinessException(ServiceError.NoRecord, " No Dental Work receipt found with id");
			}
			jasperReportResponse = createJasperForDentalLabReceipt(receiptResponse);
			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while download dental Lab Receipt");
		}
		return response;
	}

	private JasperReportResponse createJasperForDentalLabReceipt(DentalWorksReceiptResponse dentalWorksReceiptResponse)
			throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String userName = "";
		User user = dentalWorksReceiptResponse.getDoctor();
		if (!DPDoctorUtils.allStringsEmpty(user.getTitle())) {
			userName = user.getTitle();
		}
		String content = "<br>Received with thanks from &nbsp;&nbsp;<b>" + userName + user.getFirstName()
				+ "</b>. a sum of Rupees:- " + dentalWorksReceiptResponse.getAmountPaid() + "<br> By <b>"
				+ dentalWorksReceiptResponse.getModeOfPayment() + "</b> towords professional charge &nbsp;&nbsp;&nbsp;";
		parameters.put("content", content);
		parameters.put("paid", "Rs.&nbsp;" + dentalWorksReceiptResponse.getAmountPaid());
		parameters.put("receiptId", "<b>receiptId : </b>" + dentalWorksReceiptResponse.getUniqueReceiptId());
		parameters.put("date", "<b>Date : </b>" + simpleDateFormat.format(new Date()));

		LocationCollection location = locationRepository
				.findById(new ObjectId(dentalWorksReceiptResponse.getLocationId())).orElse(null);
		String doctorName = "<b>" + (!DPDoctorUtils.anyStringEmpty(user.getTitle()) ? user.getTitle() : "") + " "
				+ user.getFirstName() + "</b><br>" + location.getLocationName() + ",<br>" + location.getCity()
				+ (!DPDoctorUtils.anyStringEmpty(location.getState()) ? ",<br>" + location.getState() : "");
		parameters.put("doctor", doctorName);

		PrintSettingsCollection printSettings = printSettingsRepository.findByLocationIdAndHospitalId(
				new ObjectId(dentalWorksReceiptResponse.getDentalLabLocationId()),
				new ObjectId(dentalWorksReceiptResponse.getDentalLabHospitalId()));

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		patientVisitService.generatePrintSetup(parameters, printSettings, null);
		parameters.put("followUpAppointment", null);
		String pdfName = "DENTAL-RECEIPT-" + dentalWorksReceiptResponse.getUniqueReceiptId() + new Date().getTime();
		String layout = "PORTRAIT";
		String pageSize = "A4";
		Integer topMargin = 20;
		Integer bottonMargin = 20;
		Integer leftMargin = 20;
		Integer rightMargin = 20;
		parameters.put("followUpAppointment", null);
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
		response = jasperReportService.createPDF(ComponentType.DENTAL_WORK_RECEIPT, parameters, receiptA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

}
