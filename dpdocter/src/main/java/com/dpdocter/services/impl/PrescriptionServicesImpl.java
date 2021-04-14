package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

import com.dpdocter.beans.Advice;
import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.Code;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDosage;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.GenericCodesAndReaction;
import com.dpdocter.beans.Instructions;
import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.NutritionReferral;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.TemplateAddItem;
import com.dpdocter.beans.TemplateItem;
import com.dpdocter.beans.TemplateItemDetail;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.collections.AdviceCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DoctorDrugCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDirectionCollection;
import com.dpdocter.collections.DrugDosageCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.EyePrescriptionCollection;
import com.dpdocter.collections.GenericCodeCollection;
import com.dpdocter.collections.GenericCodesAndReactionsCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.InstructionsCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionReferralCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TemplateCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESDiagnosticTestDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.document.ESGenericCodesAndReactions;
import com.dpdocter.elasticsearch.repository.ESGenericCodesAndReactionsRepository;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FieldAlign;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AdviceRepository;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorDrugRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DrugDirectionRepository;
import com.dpdocter.repository.DrugDosageRepository;
import com.dpdocter.repository.DrugDurationUnitRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.EyePrescriptionRepository;
import com.dpdocter.repository.GenericCodeRepository;
import com.dpdocter.repository.GenericCodesAndReactionsRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.InstructionsRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionReferralRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.TemplateRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.NutritionReferralRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugInteractionResposne;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.EyeTestJasperResponse;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.PrescriptionInventoryBatchResponse;
import com.dpdocter.response.PrescriptionLookupResponse;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.InventoryService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.ReportsService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.BasicDBObject;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;
import common.util.web.Response;

@Service
public class PrescriptionServicesImpl implements PrescriptionServices {

	private static Logger logger = Logger.getLogger(PrescriptionServicesImpl.class.getName());
	@Autowired
	private DoctorRepository doctorRepository;
	@Autowired
	private AdviceRepository adviceRepository;
	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private DoctorDrugRepository doctorDrugRepository;

	@Autowired
	private DrugDirectionRepository drugDirectionRepository;

	@Autowired
	private DrugTypeRepository drugTypeRepository;

	@Autowired
	private DrugDurationUnitRepository drugDurationUnitRepository;

	@Autowired
	private TemplateRepository templateRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private DrugDosageRepository drugDosageRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private LabTestRepository labTestRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	private ESPrescriptionService esPrescriptionService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ReportsService reportsService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private EyePrescriptionRepository eyePrescriptionRepository;

	@Autowired
	private InstructionsRepository instructionsRepository;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private NutritionReferralRepository nutritionReferralRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${Prescription.checkPrescriptionExists}")
	private String checkPrescriptionExists;

	@Value(value = "${jasper.print.prescription.a4.fileName}")
	private String prescriptionA4FileName;

	@Value(value = "${jasper.print.prescription.subreport.a4.fileName}")
	private String prescriptionSubReportA4FileName;

	@Value(value = "${jasper.print.prescription.a5.fileName}")
	private String prescriptionA5FileName;

	@Value(value = "${jasper.print.prescription.subreport.a5.fileName}")
	private String prescriptionSubReportA5FileName;

	@Value(value = "${prescription.add.patient.download.app.message}")
	private String downloadAppMessageToPatient;

	@Value(value = "${prescription.add.patient.download.app.message.hindi}")
	private String downloadAppMessageToPatientInHindi;

	@Value("${send.sms}")
	private Boolean sendSMS;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESGenericCodesAndReactionsRepository esGenericCodesAndReactionsRepository;

	@Autowired
	private GenericCodesAndReactionsRepository genericCodesAndReactionsRepository;

	@Value(value = "${update.generic.codes.data.file}")
	private String UPDATE_GENERIC_CODES_DATA_FILE;

	@Value(value = "${drug.company.data.file}")
	private String DRUG_COMPANY_LIST;

	@Value(value = "${upload.drugs.file}")
	private String UPLOAD_DRUGS;

	@Value(value = "${update.drug.interaction.file}")
	private String UPDATE_DRUG_INTERACTION_DATA_FILE;

	@Autowired
	private GenericCodeRepository genericCodeRepository;

	LoadingCache<String, List<Code>> Cache = CacheBuilder.newBuilder().maximumSize(100)
			// maximum 100 records can be cached
			.expireAfterAccess(30, TimeUnit.MINUTES)
			// cache will expire after 30 minutes of access
			.build(new CacheLoader<String, List<Code>>() { // build the
															// cacheloader

				@Override
				public List<Code> load(String id) throws Exception {
					if (getDataFromElasticSearch(id) != null)
						return getDataFromElasticSearch(id);
					else
						return new ArrayList<Code>();
				}

				public Map<String, List<Code>> loadAll(Iterable<? extends String> keys) {
					return loadDataFromElasticSearch(keys);
				}

			});

	@Override
	@Transactional
	public Drug addDrug(DrugAddEditRequest request) {
		Drug response = null;
		DrugCollection drugCollection = new DrugCollection();
		BeanUtil.map(request, drugCollection);
		UUID drugCode = UUID.randomUUID();
		drugCollection.setDrugCode(drugCode.toString());
		try {
			if (!DPDoctorUtils.anyStringEmpty(drugCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugCollection.getDoctorId()).orElse(null);
				if (userCollection != null)
					drugCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			}
			Date createdTime = new Date();
			drugCollection.setCreatedTime(createdTime);
			if (drugCollection.getDrugType() != null) {
				if (DPDoctorUtils.anyStringEmpty(drugCollection.getDrugType().getId()))
					drugCollection.setDrugType(null);
				else {
					DrugTypeCollection drugTypeCollection = drugTypeRepository
							.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
					if (drugTypeCollection != null) {
						DrugType drugType = new DrugType();
						BeanUtil.map(drugTypeCollection, drugType);
						drugCollection.setDrugType(drugType);
					}
				}
			}

			drugCollection.setRankingCount(1);
			drugCollection = drugRepository.save(drugCollection);
			response = new Drug();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While saving drugs", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public Drug editDrug(DrugAddEditRequest request) {
		Drug response = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (drugCollection.getDoctorId() != null && drugCollection.getLocationId() != null
					&& drugCollection.getHospitalId() != null) {
				drugCollection.setDrugName(request.getDrugName());
				drugCollection.setUpdatedTime(new Date());
				drugCollection.setDuration(request.getDuration());
				drugCollection.setDosage(request.getDosage());
				drugCollection.setDosageTime(request.getDosageTime());
				drugCollection.setDirection(request.getDirection());
				drugCollection.setExplanation(request.getExplanation());
				if (request.getDrugType() != null) {
					if (DPDoctorUtils.anyStringEmpty(request.getDrugType().getId()))
						drugCollection.setDrugType(null);
					else {
						DrugTypeCollection drugTypeCollection = drugTypeRepository
								.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
						if (drugTypeCollection != null) {
							DrugType drugType = new DrugType();
							BeanUtil.map(drugTypeCollection, drugType);
							drugCollection.setDrugType(drugType);
						}
					}
				}
				drugCollection = drugRepository.save(drugCollection);
				transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
				if (drugCollection != null) {
					ESDrugDocument esDrugDocument = new ESDrugDocument();
					BeanUtil.map(drugCollection, esDrugDocument);
					if (drugCollection.getDrugType() != null) {
						esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
						esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
					}
					esPrescriptionService.addDrug(esDrugDocument);
				}
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While editing drugs", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public Drug deleteDrug(String drugId, String doctorId, String hospitalId, String locationId, Boolean discarded) {
		Drug response = null;
		DrugCollection drugCollection = null;
		try {
			drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null
						&& drugCollection.getLocationId() != null) {
					if (drugCollection.getDoctorId().toString().equals(doctorId)
							&& drugCollection.getHospitalId().toString().equals(hospitalId)
							&& drugCollection.getLocationId().toString().equals(locationId)) {
						drugCollection.setDiscarded(discarded);
						drugCollection.setUpdatedTime(new Date());
						drugCollection = drugRepository.save(drugCollection);
						response = new Drug();
						BeanUtil.map(drugCollection, response);

					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					DoctorDrugCollection doctorDrugCollection = doctorDrugRepository
							.findByDrugIdAndDoctorIdAndLocationIdAndHospitalId(drugCollection.getId(),
									new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));

					if (doctorDrugCollection != null) {
						doctorDrugCollection.setDiscarded(discarded);
						doctorDrugCollection.setUpdatedTime(new Date());
						doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
						if (doctorDrugCollection != null) {
							ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
							BeanUtil.map(drugCollection, esDoctorDrugDocument);
							BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
							esDoctorDrugDocument.setId(drugCollection.getId().toString());
							esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
						}
					}
				}
			} else {
				logger.warn("Drug Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting Drug", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug");
		}
		return response;
	}

	@Override
	@Transactional
	public Drug getDrugById(String drugId) {
		Drug drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				drugAddEditResponse = new Drug();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				logger.warn("Drug not found. Please check Drug Id");
				throw new BusinessException(ServiceError.NoRecord, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting drug for drugId:" + drugId,
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}

	@Override
	@Transactional
	public Drug getDrugByDrugCode(String drugCode) {
		Drug drugAddEditResponse = null;
		try {
			DrugCollection drugCollection = drugRepository.findByDrugCode(drugCode);
			if (drugCollection != null) {
				drugAddEditResponse = new Drug();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				logger.warn("Drug not found. Please check Drug Id");
				throw new BusinessException(ServiceError.NoRecord, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			try {
				mailService.sendExceptionMail(
						"Backend Business Exception :: While getting drug for drug code:" + drugCode, e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}

	@Override
	@Transactional
	public TemplateAddEditResponse addTemplate(TemplateAddEditRequest request) {
		TemplateAddEditResponse response = null;
		TemplateCollection templateCollection = new TemplateCollection();
		BeanUtil.map(request, templateCollection);
		try {
			Date createdTime = new Date();
			templateCollection.setCreatedTime(createdTime);
			List<TemplateItem> items = null;
			for (TemplateItem item : templateCollection.getItems()) {
				if (item.getDrugId() != null) {
					List<DrugDirection> directions = null;
					if (item.getDirection() != null && !item.getDirection().isEmpty()) {
						for (DrugDirection drugDirection : item.getDirection()) {
							if (drugDirection != null && !DPDoctorUtils.anyStringEmpty(drugDirection.getId())) {
								if (directions == null)
									directions = new ArrayList<DrugDirection>();
								directions.add(drugDirection);
							}
						}
						item.setDirection(directions);
					}
					if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
						if (item.getDuration().getDurationUnit().getId() == null)
							item.getDuration().setDurationUnit(null);
					}
					if (items == null)
						items = new ArrayList<TemplateItem>();
					items.add(item);
				}
			}
			templateCollection.setItems(items);
			if (!DPDoctorUtils.anyStringEmpty(templateCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(templateCollection.getDoctorId()).orElse(null);
				if (userCollection != null)
					templateCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			}
			templateCollection = templateRepository.save(templateCollection);

			response = new TemplateAddEditResponse();
			BeanUtil.map(templateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Template");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While saving template", e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Template");
		}
		return response;
	}

	@Override
	@Transactional
	public TemplateAddEditResponseDetails editTemplate(TemplateAddEditRequest request) {
		TemplateAddEditResponseDetails response = null;
		TemplateAddEditResponse template = null;
		TemplateCollection templateCollection = new TemplateCollection();
		BeanUtil.map(request, templateCollection);
		try {
			TemplateCollection oldTemplate = templateRepository.findById(new ObjectId(request.getId())).orElse(null);
			if (oldTemplate == null) {
				throw new BusinessException(ServiceError.Unknown, "Error Occurred cause Id not found");
			}
			templateCollection.setCreatedBy(oldTemplate.getCreatedBy());
			templateCollection.setCreatedTime(oldTemplate.getCreatedTime());
			templateCollection.setIsDefault(oldTemplate.getIsDefault());
			templateCollection.setDiscarded(oldTemplate.getDiscarded());
			List<TemplateItem> items = null;
			for (TemplateItem item : templateCollection.getItems()) {
				if (item.getDrugId() != null) {
					List<DrugDirection> directions = null;
					if (item.getDirection() != null && !item.getDirection().isEmpty()) {
						for (DrugDirection drugDirection : item.getDirection()) {
							if (drugDirection != null && !DPDoctorUtils.anyStringEmpty(drugDirection.getId())) {
								if (directions == null)
									directions = new ArrayList<DrugDirection>();
								directions.add(drugDirection);
							}
						}
						item.setDirection(directions);
					}
					if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
						if (item.getDuration().getDurationUnit().getId() == null)
							item.getDuration().setDurationUnit(null);
					}
					if (items == null)
						items = new ArrayList<TemplateItem>();
					items.add(item);
				}
			}
			templateCollection.setItems(items);
			templateCollection = templateRepository.save(templateCollection);
			template = new TemplateAddEditResponse();
			BeanUtil.map(templateCollection, template);
			if (template != null) {
				response = new TemplateAddEditResponseDetails();
				BeanUtil.map(template, response);
				List<TemplateItemDetail> templateItemDetails = new ArrayList<TemplateItemDetail>();
				for (TemplateAddItem templateItem : template.getItems()) {
					TemplateItemDetail templateItemDetail = new TemplateItemDetail();
					BeanUtil.map(templateItem, templateItemDetail);
					DrugCollection drugCollection = drugRepository.findById(new ObjectId(templateItem.getDrugId()))
							.orElse(null);
					Drug drug = new Drug();
					if (drugCollection != null)
						BeanUtil.map(drugCollection, drug);
					templateItemDetail.setDrug(drug);
					templateItemDetails.add(templateItemDetail);
				}
				response.setItems(templateItemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Template");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Template");
		}
		return response;
	}

	@Override
	@Transactional
	public TemplateAddEditResponseDetails deleteTemplate(String templateId, String doctorId, String hospitalId,
			String locationId, Boolean discarded) {
		TemplateAddEditResponseDetails response = null;
		TemplateCollection templateCollection = null;
		try {
			templateCollection = templateRepository.findById(new ObjectId(templateId)).orElse(null);
			if (templateCollection != null) {
				if (templateCollection.getDoctorId() != null && templateCollection.getHospitalId() != null
						&& templateCollection.getLocationId() != null) {
					if (templateCollection.getDoctorId().toString().equals(doctorId)
							&& templateCollection.getHospitalId().toString().equals(hospitalId)
							&& templateCollection.getLocationId().toString().equals(locationId)) {
						templateCollection.setUpdatedTime(new Date());
						templateCollection.setDiscarded(discarded);
						templateCollection = templateRepository.save(templateCollection);
						response = new TemplateAddEditResponseDetails();
						BeanUtil.map(templateCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Template");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Template");
				}
			} else {
				logger.warn("Template Not Found");
				throw new BusinessException(ServiceError.NotFound, "Template Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Template");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Template");
		}
		return response;
	}

	@Override
	@Transactional
	public TemplateAddEditResponseDetails getTemplate(String templateId, String doctorId, String hospitalId,
			String locationId) {
		TemplateAddEditResponseDetails response = null;
		try {
			ObjectId templateObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(templateId))
				templateObjectId = new ObjectId(templateId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			Criteria criteria = new Criteria().andOperator(new Criteria("id").is(templateObjectId),
					new Criteria("doctorId").is(doctorObjectId), new Criteria("locationId").is(locationObjectId),
					new Criteria("hospitalId").is(hospitalObjectId));

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
					Fields.field("doctorId", "$doctorId"), Fields.field("discarded", "$discarded"),
					Fields.field("items.drug", "$drug"), Fields.field("items.duration", "$items.duration"),
					Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("isDefault", "$isDefault"), Fields.field("updatedTime", "$updatedTime")));

			Aggregation aggregation = Aggregation.newAggregation(
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex1"))),
					Aggregation.match(criteria), Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					new CustomAggregationOperation(
							new Document("$unwind",
									new BasicDBObject("path", "$drug")
											.append("preserveNullAndEmptyArrays", true).append("includeArrayIndex",
													"arrayIndex2"))),
					projectList,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("isDefault", new BasicDBObject("$first", "$isDefault"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));

			AggregationResults<TemplateAddEditResponseDetails> groupResults = mongoTemplate.aggregate(aggregation,
					TemplateCollection.class, TemplateAddEditResponseDetails.class);
			List<TemplateAddEditResponseDetails> templateAddEditResponse = groupResults.getMappedResults();
			response = templateAddEditResponse.get(0);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Template");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Template");
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request, Boolean isAppointmentAdd,
			String createdBy, Appointment appointment) {
		PrescriptionAddEditResponse response = null;
		List<PrescriptionItemDetail> itemDetails = null;
		try {
			if (isAppointmentAdd) {
				if (request.getAppointmentRequest() != null) {
					appointment = addPrescriptionAppointment(request.getAppointmentRequest());
				}
			}
			final PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
			List<DiagnosticTest> diagnosticTests = request.getDiagnosticTests();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			request.setDiagnosticTests(null);
			BeanUtil.map(request, prescriptionCollection);

			if (DPDoctorUtils.anyStringEmpty(createdBy)) {
				UserCollection userCollection = userRepository.findById(prescriptionCollection.getDoctorId())
						.orElse(null);
				createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
						+ userCollection.getFirstName();
			}

			Date createdTime = new Date();
			if (request.getCreatedTime() != null) {
				prescriptionCollection.setCreatedTime(request.getCreatedTime());
			} else {
				prescriptionCollection.setCreatedTime(createdTime);
			}
			
			if (request.getFromDate() != null) {
				prescriptionCollection.setFromDate(request.getFromDate());
			} else {
				prescriptionCollection.setFromDate(new Date());
			}
			prescriptionCollection.setAdminCreatedTime(new Date());
			prescriptionCollection.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
			prescriptionCollection
					.setUniqueEmrId(UniqueIdInitial.PRESCRIPTION.getInitial() + DPDoctorUtils.generateRandomId());

			if (prescriptionCollection.getItems() != null) {
				List<PrescriptionItem> items = null;
				DrugCollection drugCollection = null;
				for (PrescriptionItem item : prescriptionCollection.getItems()) {
					PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
					List<DrugDirection> directions = null;

					if (item.getDrugQuantity() != null) {
						item.setAnalyticsDrugQuantity(item.getDrugQuantity());
					} else {
						item.setAnalyticsDrugQuantity(1);
					}

					if (item.getDirection() != null && !item.getDirection().isEmpty()) {
						for (DrugDirection drugDirection : item.getDirection()) {
							if (drugDirection != null && !DPDoctorUtils.anyStringEmpty(drugDirection.getId())) {
								if (directions == null)
									directions = new ArrayList<DrugDirection>();
								directions.add(drugDirection);
							}
						}
						item.setDirection(directions);
					}
					if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
						if (item.getDuration().getDurationUnit().getId() == null)
							item.setDuration(null);
					} else {
						item.setDuration(null);
					}
					if (items == null) {
						items = new ArrayList<PrescriptionItem>();
						itemDetails = new ArrayList<PrescriptionItemDetail>();
					}
					BeanUtil.map(item, prescriptionItemDetail);
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugId())) {
						drugCollection = drugRepository.findById(item.getDrugId()).orElse(null);
					} else {
						drugCollection = new DrugCollection();
					}
					Drug drug = new Drug();
					DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
					if (drugCollection != null) {
						BeanUtil.map(drugCollection, drugAddEditRequest);
					}
					drugAddEditRequest.setDoctorId(request.getDoctorId());
					drugAddEditRequest.setHospitalId(request.getHospitalId());
					drugAddEditRequest.setLocationId(request.getLocationId());
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugName())) {
						drugAddEditRequest.setDrugName(item.getDrugName());
					}
					if (item.getDrugType() != null) {
						drugAddEditRequest.setDrugType(item.getDrugType());
					}
					if (!DPDoctorUtils.anyStringEmpty(item.getInstructions())) {
						drugAddEditRequest.setExplanation(item.getInstructions());
						drugCollection.setExplanation(item.getInstructions());
					}
					if (item.getInventoryQuantity() == null || item.getInventoryQuantity() == 0l) {
						item.setInventoryQuantity(1l);
					}

					drugAddEditRequest.setDirection(item.getDirection());
					drugAddEditRequest.setDuration(item.getDuration());
					drugAddEditRequest.setDosage(item.getDosage());
					drugAddEditRequest.setDosageTime(item.getDosageTime());
					drug = addFavouriteDrug(drugAddEditRequest, drugCollection, createdBy);
					item.setDrugId(new ObjectId(drug.getId()));

					prescriptionItemDetail.setDrug(drug);
					items.add(item);
					itemDetails.add(prescriptionItemDetail);

				}
				prescriptionCollection.setItems(items);
			}
			if (diagnosticTests != null) {
				List<TestAndRecordData> tests = null;
				for (DiagnosticTest diagnosticTest : diagnosticTests) {

					if (diagnosticTest.getId() != null) {
						if (tests == null)
							tests = new ArrayList<TestAndRecordData>();
						tests.add(new TestAndRecordData(new ObjectId(diagnosticTest.getId()), null));
					} else if (diagnosticTest.getTestName() != null) {
						DiagnosticTestCollection diagnosticTestCollection = null;
						diagnosticTestCollection = new DiagnosticTestCollection();
						diagnosticTestCollection.setTestName(diagnosticTest.getTestName());
						diagnosticTestCollection.setLocationId(prescriptionCollection.getLocationId());
						diagnosticTestCollection.setHospitalId(prescriptionCollection.getHospitalId());
						diagnosticTestCollection.setCreatedTime(new Date());

						diagnosticTestCollection.setCreatedBy(createdBy);
						diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);

						transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST,
								false);
						ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
						BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
						esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);
						if (tests == null)
							tests = new ArrayList<TestAndRecordData>();
						tests.add(new TestAndRecordData(diagnosticTestCollection.getId(), null));
					}
				}
				prescriptionCollection.setDiagnosticTests(tests);
			}

			prescriptionCollection.setCreatedBy(createdBy);
			prescriptionRepository.save(prescriptionCollection);

			response = new PrescriptionAddEditResponse();
			List<TestAndRecordData> prescriptionTest = prescriptionCollection.getDiagnosticTests();
			prescriptionCollection.setDiagnosticTests(null);
			BeanUtil.map(prescriptionCollection, response);

			if (prescriptionTest != null && !prescriptionTest.isEmpty()) {
				List<TestAndRecordDataResponse> tests = new ArrayList<TestAndRecordDataResponse>();
				tests = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(
										Aggregation.match(new Criteria("id").is(prescriptionCollection.getId())),
										Aggregation.unwind("diagnosticTests"), Aggregation.lookup("diagnostic_test_cl",
												"diagnosticTests.testId", "_id", "diagnosticTest"),
										Aggregation.unwind("diagnosticTest"),
										new CustomAggregationOperation(new Document("$project",
												new BasicDBObject("test", "$diagnosticTest").append("recordId",
														"$diagnosticTests.recordId")))),
								PrescriptionCollection.class, TestAndRecordDataResponse.class)
						.getMappedResults();
				response.setDiagnosticTests(tests);
			}
			response.setItems(itemDetails);

			final String id = request.getId();
			final Boolean sendNotificationToDoctor = request.getSendNotificationToDoctor();
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {

					OPDReports opdReports = new OPDReports(String.valueOf(prescriptionCollection.getPatientId()),
							String.valueOf(prescriptionCollection.getId()),
							String.valueOf(prescriptionCollection.getDoctorId()),
							String.valueOf(prescriptionCollection.getLocationId()),
							String.valueOf(prescriptionCollection.getHospitalId()),
							prescriptionCollection.getCreatedTime());

					opdReports = reportsService.submitOPDReport(opdReports);

					if (sendNotificationToDoctor == null || sendNotificationToDoctor)
						pushNotificationServices.notifyUser(prescriptionCollection.getDoctorId().toString(), "RX Added",
								ComponentType.PRESCRIPTION_REFRESH.getType(),
								prescriptionCollection.getPatientId().toString(), null);

					pushNotificationServices.notifyUser(prescriptionCollection.getPatientId().toString(),
							"Your prescription by " + prescriptionCollection.getCreatedBy()
									+ " is here - Tap to view it!",
							ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString(), null);

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
		}
		return response;
	}

	private void sendMessage(PrescriptionCollection prescriptionCollection) {
		DoctorCollection doctorCollection = null;
		if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getDoctorId())) {
			doctorCollection = doctorRepository.findByUserId(prescriptionCollection.getDoctorId());
		}
		if (doctorCollection.getIsPrescriptionSMS()) {
			sendDownloadAppMessage(prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
					prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId(),
					prescriptionCollection.getCreatedBy());
			sendDownloadAppMessageInHindi(prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
					prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId(),
					prescriptionCollection.getCreatedBy());
		}
	}

	private void sendDownloadAppMessage(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String doctorName) {
		try {
			UserCollection userCollection = userRepository.findByIdAndSignedUpNot(patientId, false);
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientId,
					locationId, hospitalId);
			if (userCollection != null) {
				String message = downloadAppMessageToPatient;
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(doctorId);
				smsTrackDetail.setLocationId(locationId);
				smsTrackDetail.setHospitalId(hospitalId);
				smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(patientCollection.getLocalPatientName());
				sms.setSmsText(message.replace("{doctorName}", doctorName));

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendDownloadAppMessageInHindi(ObjectId patientId, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId, String doctorName) {
		try {
			UserCollection userCollection = userRepository.findByIdAndSignedUpNot(patientId, false);
			PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(patientId,
					locationId, hospitalId);
			if (userCollection != null) {
				String message = downloadAppMessageToPatientInHindi;
				message = StringEscapeUtils.unescapeJava(message);
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(doctorId);
				smsTrackDetail.setLocationId(locationId);
				smsTrackDetail.setHospitalId(hospitalId);
				smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(userCollection.getId());
				SMS sms = new SMS();
				smsDetail.setUserName(patientCollection.getLocalPatientName());
				sms.setSmsText(message.replace("{doctorName}", doctorName));

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(userCollection.getMobileNumber());
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				sMSServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	@Transactional
	public PrescriptionAddEditResponseDetails editPrescription(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponseDetails response = null;
		PrescriptionAddEditResponse prescription = null;
		List<PrescriptionItemDetail> itemDetails = null;
		try {
			Appointment appointment = null;
			if (request.getAppointmentRequest() != null) {
				appointment = addPrescriptionAppointment(request.getAppointmentRequest());
			}
			PrescriptionCollection prescriptionCollection = new PrescriptionCollection();
			List<DiagnosticTest> diagnosticTests = request.getDiagnosticTests();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			request.setDiagnosticTests(null);
			BeanUtil.map(request, prescriptionCollection);
			UserCollection userCollection = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
			PrescriptionCollection oldPrescription = prescriptionRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			prescriptionCollection.setIsPatientDiscarded(oldPrescription.getIsPatientDiscarded());
			if (request.getFromDate() != null) {
				prescriptionCollection.setFromDate(request.getFromDate());
			} else {
				prescriptionCollection.setFromDate(oldPrescription.getFromDate());
			}
			prescriptionCollection.setCreatedBy(oldPrescription.getCreatedBy());
			prescriptionCollection.setCreatedTime(oldPrescription.getCreatedTime());
			if (request.getCreatedTime() != null) {
				prescriptionCollection.setCreatedTime(request.getCreatedTime());
			} else {
				prescriptionCollection.setCreatedTime(oldPrescription.getCreatedTime());
			}
			prescriptionCollection.setAdminCreatedTime(oldPrescription.getAdminCreatedTime());
			prescriptionCollection.setDiscarded(oldPrescription.getDiscarded());
			prescriptionCollection.setInHistory(oldPrescription.getInHistory());
			prescriptionCollection.setUniqueEmrId(oldPrescription.getUniqueEmrId());
			if (prescriptionCollection.getItems() != null) {
				List<PrescriptionItem> items = null;
				DrugCollection drugCollection = null;
				for (PrescriptionItem item : prescriptionCollection.getItems()) {

					if (item.getInventoryQuantity() != null) {
						item.setInventoryQuantity(item.getInventoryQuantity());
					} else {
						item.setInventoryQuantity(1l);
					}

					PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
					List<DrugDirection> directions = null;
					if (item.getDirection() != null && !item.getDirection().isEmpty()) {
						for (DrugDirection drugDirection : item.getDirection()) {
							if (drugDirection != null && !DPDoctorUtils.anyStringEmpty(drugDirection.getId())) {
								if (directions == null)
									directions = new ArrayList<DrugDirection>();
								directions.add(drugDirection);
							}
						}
						item.setDirection(directions);
					}
					if (item.getDuration() != null && item.getDuration().getDurationUnit() != null) {
						if (item.getDuration().getDurationUnit().getId() == null)
							item.setDuration(null);
					} else {
						item.setDuration(null);
					}
					if (items == null) {
						items = new ArrayList<PrescriptionItem>();
						itemDetails = new ArrayList<PrescriptionItemDetail>();

					}
					BeanUtil.map(item, prescriptionItemDetail);
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugId())) {
						drugCollection = drugRepository.findById(item.getDrugId()).orElse(null);
					} else {
						drugCollection = new DrugCollection();
					}
					Drug drug = new Drug();
					DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
					if (drugCollection != null) {
						BeanUtil.map(drugCollection, drugAddEditRequest);
					}
					drugAddEditRequest.setDoctorId(request.getDoctorId());
					drugAddEditRequest.setHospitalId(request.getHospitalId());
					drugAddEditRequest.setLocationId(request.getLocationId());
					if (!DPDoctorUtils.allStringsEmpty(item.getDrugName())) {
						drugAddEditRequest.setDrugName(item.getDrugName());
					}
					if (item.getDrugType() != null) {
						drugAddEditRequest.setDrugType(item.getDrugType());
					}
					if (!DPDoctorUtils.anyStringEmpty(item.getInstructions())) {
						drugAddEditRequest.setExplanation(item.getInstructions());
						drugCollection.setExplanation(item.getInstructions());
					}
					drugAddEditRequest.setDirection(item.getDirection());
					drugAddEditRequest.setDuration(item.getDuration());
					drugAddEditRequest.setDosage(item.getDosage());
					drugAddEditRequest.setDosageTime(item.getDosageTime());
					drug = addFavouriteDrug(drugAddEditRequest, drugCollection,
							(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
					item.setDrugId(new ObjectId(drug.getId()));

					prescriptionItemDetail.setDrug(drug);
					items.add(item);
					itemDetails.add(prescriptionItemDetail);

				}
				prescriptionCollection.setItems(items);
			}
			if (diagnosticTests != null) {
				List<TestAndRecordData> tests = null;
				for (DiagnosticTest diagnosticTest : diagnosticTests) {
					if (diagnosticTest.getId() != null) {
						if (tests == null)
							tests = new ArrayList<TestAndRecordData>();
						tests.add(new TestAndRecordData(new ObjectId(diagnosticTest.getId()), null));
					} else if (diagnosticTest.getTestName() != null) {
						DiagnosticTestCollection diagnosticTestCollection = null;
						diagnosticTestCollection = new DiagnosticTestCollection();
						diagnosticTestCollection.setTestName(diagnosticTest.getTestName());
						diagnosticTestCollection.setLocationId(prescriptionCollection.getLocationId());
						diagnosticTestCollection.setHospitalId(prescriptionCollection.getHospitalId());
						diagnosticTestCollection.setCreatedTime(new Date());

						if (userCollection != null)
							diagnosticTestCollection.setCreatedBy(
									(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
											+ userCollection.getFirstName());
						diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);

						transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST,
								false);
						ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
						BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
						esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);
						tests.add(new TestAndRecordData(diagnosticTestCollection.getId(), null));
					}
				}
				prescriptionCollection.setDiagnosticTests(tests);
			}
			prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
			prescription = new PrescriptionAddEditResponse();
			List<TestAndRecordData> prescriptionTests = prescriptionCollection.getDiagnosticTests();
			prescriptionCollection.setDiagnosticTests(null);
			BeanUtil.map(prescriptionCollection, prescription);

			if (prescription != null) {
				response = new PrescriptionAddEditResponseDetails();
				BeanUtil.map(prescription, response);
				// List<PrescriptionItemDetail> prescriptionItemDetails = new
				// ArrayList<PrescriptionItemDetail>();
				// if (prescription.getItems() != null &&
				// !prescription.getItems().isEmpty()) {
				// for (PrescriptionAddItem prescriptionItem :
				// prescription.getItems()) {
				// PrescriptionItemDetail prescriptionItemDetail = new
				// PrescriptionItemDetail();
				// BeanUtil.map(prescriptionItem, prescriptionItemDetail);
				// DrugCollection drugCollection = drugRepository
				// .findById(new ObjectId(prescriptionItem.getDrugId()));
				// Drug drug = new Drug();
				// if (drugCollection != null)
				// BeanUtil.map(drugCollection, drug);
				// prescriptionItemDetail.setDrug(drug);
				// prescriptionItemDetails.add(prescriptionItemDetail);
				// }
				// }
				response.setItems(itemDetails);
			}
			if (prescriptionTests != null && !prescriptionTests.isEmpty()) {
				List<TestAndRecordDataResponse> tests = new ArrayList<TestAndRecordDataResponse>();
				for (TestAndRecordData data : prescriptionTests) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(data.getTestId()).orElse(null);
					DiagnosticTest diagnosticTest = new DiagnosticTest();
					if (diagnosticTestCollection != null) {
						BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					}
					if (!DPDoctorUtils.anyStringEmpty(data.getRecordId())) {
						tests.add(new TestAndRecordDataResponse(diagnosticTest, data.getRecordId().toString()));
					} else {
						tests.add(new TestAndRecordDataResponse(diagnosticTest, null));
					}

				}
				response.setDiagnosticTests(tests);
			}
			pushNotificationServices.notifyUser(prescriptionCollection.getPatientId().toString(),
					"Your prescription by " + prescriptionCollection.getCreatedBy() + " has changed - Tap to view it!",
					ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Prescription");
		}
		return response;
	}

	@Override
	@Transactional
	public Prescription deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId,
			String patientId, Boolean discarded) {
		Prescription response = null;
		PrescriptionCollection prescriptionCollection = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null
						&& prescriptionCollection.getPatientId() != null) {
					if (prescriptionCollection.getDoctorId().toString().equals(doctorId)
							&& prescriptionCollection.getHospitalId().toString().equals(hospitalId)
							&& prescriptionCollection.getLocationId().toString().equals(locationId)
							&& prescriptionCollection.getPatientId().toString().equals(patientId)) {
						prescriptionCollection.setDiscarded(discarded);
						prescriptionCollection.setUpdatedTime(new Date());
						prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
						response = new Prescription();
						List<TestAndRecordData> tests = prescriptionCollection.getDiagnosticTests();
						prescriptionCollection.setDiagnosticTests(null);
						BeanUtil.map(prescriptionCollection, response);
						if (prescriptionCollection.getItems() != null) {
							List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
							for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
								PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
								BeanUtil.map(prescriptionItem, prescriptionItemDetails);
								if (prescriptionItem.getDrugId() != null) {
									DrugCollection drugCollection = drugRepository
											.findById(prescriptionItem.getDrugId()).orElse(null);
									Drug drug = new Drug();
									if (drugCollection != null)
										BeanUtil.map(drugCollection, drug);
									prescriptionItemDetails.setDrug(drug);
								}
								prescriptionItemDetailsList.add(prescriptionItemDetails);
							}
							response.setItems(prescriptionItemDetailsList);
						}
						PatientVisitCollection patientVisitCollection = patientVisitRepository
								.findByPrescriptionId(prescriptionCollection.getId());
						if (patientVisitCollection != null)
							response.setVisitId(patientVisitCollection.getId().toString());

						if (tests != null && !tests.isEmpty()) {
							List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
							for (TestAndRecordData data : tests) {
								if (data.getTestId() != null) {
									DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
											.findById(data.getTestId()).orElse(null);
									DiagnosticTest diagnosticTest = new DiagnosticTest();
									if (diagnosticTestCollection != null) {
										BeanUtil.map(diagnosticTestCollection, diagnosticTest);
									}
									if (!DPDoctorUtils.anyStringEmpty(data.getRecordId())) {
										diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
												data.getRecordId().toString()));
									} else {
										diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest, null));
									}

								}
							}
							response.setDiagnosticTests(diagnosticTests);
						}

						pushNotificationServices.notifyUser(patientId,
								"Please discontinue " + prescriptionCollection.getUniqueEmrId() + " prescribed by "
										+ prescriptionCollection.getCreatedBy()
										+ ", for further details please contact "
										+ locationCollection.getLocationName(),
								ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString(), null);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Prescription");
				}
			} else {
				logger.warn("Prescription Not Found");
				throw new BusinessException(ServiceError.NotFound, "Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error Occurred While Deleting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Prescription");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Prescription> getPrescriptions(long page, int size, String doctorId, String hospitalId,
			String locationId, String patientId, String updatedTime, boolean isOTPVerified, boolean discarded,
			boolean inHistory) {
		List<Prescription> prescriptions = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);

		try {
			long createdTimestamp = Long.parseLong(updatedTime);
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId).and("isPatientDiscarded").ne(true);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			} else {
				pushNotificationServices.notifyUser(patientId, "Global records", null, null, null);
			}

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), Fields.field("appointmentRequest", "$appointmentRequest"),
					Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
					Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("tests", "$diagnosticTests"), Fields.field("locationName", "$location.locationName")));
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						new CustomAggregationOperation(
								new Document("$unwind",
										new BasicDBObject("path",
												"$visit").append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$push", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("tests", new BasicDBObject("$first", "$tests"))
								.append("time", new BasicDBObject("$first", "$time"))
								.append("fromDate", new BasicDBObject("$first", "$fromDate"))
								.append("patientId", new BasicDBObject("$first", "$patientId"))
								.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
								.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
								.append("visitId", new BasicDBObject("$first", "$visitId"))
								.append("createdTime", new BasicDBObject("$first", "$createdTime"))
								.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
								.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

			if (prescriptions != null && !prescriptions.isEmpty()) {
				for (Prescription prescription : prescriptions) {
					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}

					if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
						for (PrescriptionItemDetail prescriptionItemDetail : prescription.getItems()) {
							InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
									prescription.getLocationId(), prescription.getHospitalId(),
									prescriptionItemDetail.getDrug().getDrugCode());
							if (inventoryItem != null) {
								InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
										.getInventoryItem(inventoryItem.getId());
								prescriptionItemDetail.setTotalStock(inventoryItemLookupResposne.getTotalStock());
								List<PrescriptionInventoryBatchResponse> inventoryBatchs = null;
								if (inventoryItemLookupResposne.getInventoryBatchs() != null) {
									inventoryBatchs = new ArrayList<>();
									for (InventoryBatch inventoryBatch : inventoryItemLookupResposne
											.getInventoryBatchs()) {
										PrescriptionInventoryBatchResponse response = new PrescriptionInventoryBatchResponse();
										BeanUtil.map(inventoryBatch, response);
										inventoryBatchs.add(response);
									}
								}
								prescriptionItemDetail.setInventoryBatchs(inventoryBatchs);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public List<Prescription> getPrescriptionsByIds(List<ObjectId> prescriptionIds, ObjectId visitId) {
		List<Prescription> prescriptions = null;
		try {
			Criteria criteria = new Criteria("_id").in(prescriptionIds).and("isPatientDiscarded").ne(true);
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("advice", "$advice"), Fields.field("time", "$time"),
					Fields.field("fromDate", "$fromDate"), Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("tests", "$diagnosticTests")));
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex1"))),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true)
									.append("includeArrayIndex", "arrayIndex3"))),
					new CustomAggregationOperation(
							new Document("$unwind",
									new BasicDBObject("path", "$visit")
											.append("preserveNullAndEmptyArrays", true).append("includeArrayIndex",
													"arrayIndex5"))),
					projectList,
					new CustomAggregationOperation(new Document("$group",
							new BasicDBObject("_id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			prescriptions = aggregationResults.getMappedResults();
			if (prescriptions != null && !prescriptions.isEmpty()) {
				for (Prescription prescription : prescriptions) {
					if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
						List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
						for (TestAndRecordData data : prescription.getTests()) {
							if (data.getTestId() != null) {
								DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
										.findById(data.getTestId()).orElse(null);
								DiagnosticTest diagnosticTest = new DiagnosticTest();
								if (diagnosticTestCollection != null) {
									BeanUtil.map(diagnosticTestCollection, diagnosticTest);
								}
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
										(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
												? data.getRecordId().toString()
												: null)));
							}
						}
						prescription.setTests(null);
						prescription.setDiagnosticTests(diagnosticTests);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public List<TemplateAddEditResponseDetails> getTemplates(long page, int size, String doctorId, String hospitalId,
			String locationId, String updatedTime, boolean discarded) {
		List<TemplateAddEditResponseDetails> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
					Fields.field("doctorId", "$doctorId"), Fields.field("discarded", "$discarded"),
					Fields.field("items.drug", "$drug"), Fields.field("items.duration", "$items.duration"),
					Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("isDefault", "$isDefault")));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.match(criteria), Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						new CustomAggregationOperation(
								new Document("$group",
										new BasicDBObject("id", "$_id")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("discarded",
														new BasicDBObject("$first", "$discarded"))
												.append("items", new BasicDBObject("$push", "$items")
														.append("createdTime",
																new BasicDBObject("$first", "$createdTime"))
														.append("updatedTime",
																new BasicDBObject("$first", "$updatedTime"))
														.append("createdBy", new BasicDBObject("$first", "$createdBy"))
														.append("isDefault",
																new BasicDBObject("$first", "$isDefault"))))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			}

			else
				aggregation = Aggregation.newAggregation(
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex1"))),
						Aggregation.match(criteria), Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						new CustomAggregationOperation(new Document("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true)
										.append("includeArrayIndex", "arrayIndex2"))),
						projectList,

						new CustomAggregationOperation(new Document("$group",
								new BasicDBObject("id", "$_id").append("name", new BasicDBObject("$first", "$name"))
										.append("locationId", new BasicDBObject("$first", "$locationId"))
										.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
										.append("doctorId", new BasicDBObject("$first", "$doctorId"))
										.append("discarded", new BasicDBObject("$first", "$discarded"))
										.append("items", new BasicDBObject("$push", "$items"))
										.append("createdTime", new BasicDBObject("$first", "$createdTime"))
										.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
										.append("createdBy", new BasicDBObject("$first", "$createdBy"))
										.append("isDefault", new BasicDBObject("$first", "$isDefault")))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			response = mongoTemplate
					.aggregate(aggregation, TemplateCollection.class, TemplateAddEditResponseDetails.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Template");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Template");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getPrescriptionCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		Integer prescriptionCount = 0;
		try {

			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId)
					.and("isPatientDiscarded").ne(true);
			;
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			prescriptionCount = (int) mongoTemplate.count(new Query(criteria), PrescriptionCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Prescription Count");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription Count");
		}
		return prescriptionCount;
	}

	@Override
	@Transactional
	public TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest request) {
		TemplateAddEditResponseDetails response = null;
		TemplateAddEditResponse template = addTemplate(request);
		if (template != null) {
			response = new TemplateAddEditResponseDetails();
			BeanUtil.map(template, response);
			List<TemplateItemDetail> templateItemDetails = new ArrayList<TemplateItemDetail>();
			for (TemplateAddItem templateItem : template.getItems()) {
				TemplateItemDetail templateItemDetail = new TemplateItemDetail();
				BeanUtil.map(templateItem, templateItemDetail);
				if (templateItem.getDrugId() != null) {
					DrugCollection drugCollection = drugRepository.findById(new ObjectId(templateItem.getDrugId()))
							.orElse(null);
					Drug drug = new Drug();
					if (drugCollection != null)
						BeanUtil.map(drugCollection, drug);
					templateItemDetail.setDrug(drug);
				}
				templateItemDetails.add(templateItemDetail);
			}
			response.setItems(templateItemDetails);
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionAddEditResponseDetails addPrescriptionHandheld(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponseDetails response = null;
		PrescriptionAddEditResponse prescription = addPrescription(request, true, null, null);
		if (prescription != null) {
			response = new PrescriptionAddEditResponseDetails();
			List<TestAndRecordDataResponse> prescriptionTest = prescription.getDiagnosticTests();
			prescription.setDiagnosticTests(null);
			BeanUtil.map(prescription, response);
			response.setDiagnosticTests(prescriptionTest);
		}
		return response;
	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request) {
		DrugTypeAddEditResponse response = null;

		DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
		BeanUtil.map(request, drugTypeCollection);
		try {
			drugTypeCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugTypeCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugTypeCollection.getDoctorId()).orElse(null);
				if (userCollection != null)
					drugTypeCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugTypeCollection.setCreatedBy("ADMIN");
			}
			drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
			response = new DrugTypeAddEditResponse();
			BeanUtil.map(drugTypeCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Type");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request) {

		DrugTypeAddEditResponse response = null;

		DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
		BeanUtil.map(request, drugTypeCollection);
		try {
			DrugTypeCollection oldDrug = drugTypeRepository.findById(new ObjectId(request.getId())).orElse(null);
			drugTypeCollection.setCreatedBy(oldDrug.getCreatedBy());
			drugTypeCollection.setCreatedTime(oldDrug.getCreatedTime());
			drugTypeCollection.setDiscarded(oldDrug.getDiscarded());
			drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
			response = new DrugTypeAddEditResponse();
			BeanUtil.map(drugTypeCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Type");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugTypeAddEditResponse deleteDrugType(String drugTypeId, Boolean discarded) {

		DrugTypeAddEditResponse response = null;
		DrugTypeCollection drugTypeCollection = null;
		try {
			drugTypeCollection = drugTypeRepository.findById(new ObjectId(drugTypeId)).orElse(null);
			if (drugTypeCollection != null) {
				drugTypeCollection.setDiscarded(discarded);
				drugTypeCollection.setUpdatedTime(new Date());
				drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
				response = new DrugTypeAddEditResponse();
				BeanUtil.map(drugTypeCollection, response);
			} else {
				logger.warn("Drug Type Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Type Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Type");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request) {

		DrugDosageAddEditResponse response = null;

		DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
		BeanUtil.map(request, drugDosageCollection);
		try {
			drugDosageCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDosageCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDosageCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDosageCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDosageCollection.setCreatedBy("ADMIN");
			}
			drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
			response = new DrugDosageAddEditResponse();
			BeanUtil.map(drugDosageCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request) {

		DrugDosageAddEditResponse response = null;

		DrugDosageCollection drugDosageCollection = new DrugDosageCollection();
		BeanUtil.map(request, drugDosageCollection);
		try {
			DrugDosageCollection oldDrugDosage = drugDosageRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			drugDosageCollection.setCreatedBy(oldDrugDosage.getCreatedBy());
			drugDosageCollection.setCreatedTime(oldDrugDosage.getCreatedTime());
			drugDosageCollection.setDiscarded(oldDrugDosage.getDiscarded());
			drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
			response = new DrugDosageAddEditResponse();
			BeanUtil.map(drugDosageCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editin Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDosageAddEditResponse deleteDrugDosage(String drugDosageId, Boolean discarded) {
		DrugDosageAddEditResponse response = null;
		DrugDosageCollection drugDosageCollection = null;
		try {
			drugDosageCollection = drugDosageRepository.findById(new ObjectId(drugDosageId)).orElse(null);
			if (drugDosageCollection != null) {
				drugDosageCollection.setDiscarded(discarded);
				drugDosageCollection.setUpdatedTime(new Date());
				drugDosageCollection = drugDosageRepository.save(drugDosageCollection);
				response = new DrugDosageAddEditResponse();
				BeanUtil.map(drugDosageCollection, response);
			} else {
				logger.warn("Drug Dosage Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Dosage");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request) {

		DrugDirectionAddEditResponse response = null;

		DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
		BeanUtil.map(request, drugDirectionCollection);
		try {
			drugDirectionCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDirectionCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDirectionCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDirectionCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDirectionCollection.setCreatedBy("ADMIN");
			}
			drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
			response = new DrugDirectionAddEditResponse();
			BeanUtil.map(drugDirectionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Direction");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request) {

		DrugDirectionAddEditResponse response = null;

		DrugDirectionCollection drugDirectionCollection = new DrugDirectionCollection();
		BeanUtil.map(request, drugDirectionCollection);
		try {
			DrugDirectionCollection oldDrugDirection = drugDirectionRepository.findById(new ObjectId(request.getId()))
					.orElse(null);
			drugDirectionCollection.setCreatedBy(oldDrugDirection.getCreatedBy());
			drugDirectionCollection.setCreatedTime(oldDrugDirection.getCreatedTime());
			drugDirectionCollection.setDiscarded(oldDrugDirection.getDiscarded());
			drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
			response = new DrugDirectionAddEditResponse();
			BeanUtil.map(drugDirectionCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Direction");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugDirectionAddEditResponse deleteDrugDirection(String drugDirectionId, Boolean discarded) {
		DrugDirectionAddEditResponse response = null;
		DrugDirectionCollection drugDirectionCollection = null;
		try {
			drugDirectionCollection = drugDirectionRepository.findById(new ObjectId(drugDirectionId)).orElse(null);
			if (drugDirectionCollection != null) {
				drugDirectionCollection.setDiscarded(discarded);
				drugDirectionCollection.setUpdatedTime(new Date());
				drugDirectionCollection = drugDirectionRepository.save(drugDirectionCollection);
				response = new DrugDirectionAddEditResponse();
				BeanUtil.map(drugDirectionCollection, response);
			} else {
				logger.warn("Drug Dosage Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Dosage Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Direction");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

		DrugDurationUnitAddEditResponse response = null;

		DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
		BeanUtil.map(request, drugDurationUnitCollection);
		try {
			drugDurationUnitCollection.setCreatedTime(new Date());
			if (!DPDoctorUtils.anyStringEmpty(drugDurationUnitCollection.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(drugDurationUnitCollection.getDoctorId())
						.orElse(null);
				if (userCollection != null)
					drugDurationUnitCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
			} else {
				drugDurationUnitCollection.setCreatedBy("ADMIN");
			}
			drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
			response = new DrugDurationUnitAddEditResponse();
			BeanUtil.map(drugDurationUnitCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug Duration Unit");
		}
		return response;
	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request) {

		DrugDurationUnitAddEditResponse response = null;

		DrugDurationUnitCollection drugDurationUnitCollection = new DrugDurationUnitCollection();
		BeanUtil.map(request, drugDurationUnitCollection);
		try {
			DrugDurationUnitCollection oldDrugDuration = drugDurationUnitRepository
					.findById(new ObjectId(request.getId())).orElse(null);
			drugDurationUnitCollection.setCreatedBy(oldDrugDuration.getCreatedBy());
			drugDurationUnitCollection.setCreatedTime(oldDrugDuration.getCreatedTime());
			drugDurationUnitCollection.setDiscarded(oldDrugDuration.getDiscarded());
			drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
			response = new DrugDurationUnitAddEditResponse();
			BeanUtil.map(drugDurationUnitCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Drug Duration Unit");
		}
		return response;

	}

	@Override
	@Transactional
	public DrugDurationUnitAddEditResponse deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded) {
		DrugDurationUnitAddEditResponse response = null;
		DrugDurationUnitCollection drugDurationUnitCollection = null;
		try {
			drugDurationUnitCollection = drugDurationUnitRepository.findById(new ObjectId(drugDurationUnitId))
					.orElse(null);
			if (drugDurationUnitCollection != null) {
				drugDurationUnitCollection.setDiscarded(discarded);
				drugDurationUnitCollection.setUpdatedTime(new Date());
				drugDurationUnitCollection = drugDurationUnitRepository.save(drugDurationUnitCollection);
				response = new DrugDurationUnitAddEditResponse();
				BeanUtil.map(drugDurationUnitCollection, response);
			} else {
				logger.warn("Drug Duration Unit Not Found");
				throw new BusinessException(ServiceError.NotFound, "Drug Duration Unit Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Drug Duration Unit");
		}
		return response;
	}

	@Override
	@Transactional
	public Prescription getPrescriptionById(String prescriptionId) {
		Prescription prescription = null;
		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
					Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
					Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
					Fields.field("discarded", "$discarded"), Fields.field("inHistory", "$inHistory"),
					Fields.field("appointmentRequest", "$appointmentRequest"), Fields.field("advice", "$advice"),
					Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
					Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("items.drugQuantity", "$items.drugQuantity"),
					Fields.field("items.inventoryQuantity", "$items.inventoryQuantity"),
					Fields.field("tests", "$diagnosticTests")));
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(prescriptionId))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
									"appointmentRequest"),
							Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$appointmentRequest")
											.append("preserveNullAndEmptyArrays", true))),

							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$visit").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))));
			AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
					"prescription_cl", Prescription.class);
			List<Prescription> prescriptions = aggregationResults.getMappedResults();

			if (prescriptions != null && !prescriptions.isEmpty()) {
				prescription = prescriptions.get(0);
				if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
					List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData data : prescription.getTests()) {
						if (data.getTestId() != null) {
							DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
									.findById(data.getTestId()).orElse(null);
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							if (diagnosticTestCollection != null) {
								BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							}
							diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
									(!DPDoctorUtils.anyStringEmpty(data.getRecordId()) ? data.getRecordId().toString()
											: null)));
						}
					}
					prescription.setTests(null);
					prescription.setDiagnosticTests(diagnosticTests);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting prescription : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting prescription : " + e.getCause().getMessage());
		}
		return prescription;
	}

	@Override
	@Transactional
	public Response<Object> getPrescriptionItems(String type, String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin,
			String disease, String searchTerm) {
		Response<Object> response = new Response<Object>();
		List<?> dataList = new ArrayList<Object>();

		switch (PrescriptionItems.valueOf(type.toUpperCase())) {

		case DRUGS: {
			List<Drug> drugs = null;
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchTerm = searchTerm.toUpperCase();
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				drugs = getGlobalDrugs(page, size, updatedTime, discarded);
				dataList = addStockToDrug(drugs);
				break;

			case CUSTOM:
				dataList = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case BOTH:
				dataList = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case FAVOURITES:
				dataList = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			response.setDataList(dataList);
			break;
		}
		case DRUGTYPE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				dataList = getGlobalDrugType(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				dataList = getCustomDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				dataList = getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			response.setDataList(dataList);
			break;
		}
		case DRUGDIRECTION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDrugDirection(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDrugDirection(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DRUGDOSAGE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDrugDosage(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDrugDosage(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DRUGDURATIONUNIT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				dataList = getGlobalDrugDurationUnit(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				dataList = getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				dataList = getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			response.setDataList(dataList);
			break;
		}

		case LABTEST: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				break;
			case CUSTOM:
				dataList = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				break;
			default:
				break;
			}
			response.setDataList(dataList);
			break;
		}

		case DIAGNOSTICTEST: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDiagnosticTests(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDiagnosticTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case ADVICE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalAdvices(page, size, doctorId, updatedTime, disease, searchTerm, discarded);
				break;

			case CUSTOM:
				response = getCustomAdvices(page, size, doctorId, locationId, hospitalId, updatedTime, disease,
						searchTerm, discarded);
				break;

			case BOTH:
				response = getCustomGlobalAdvices(page, size, doctorId, locationId, hospitalId, updatedTime, disease,
						searchTerm, discarded);
				break;
			default:
				break;

			}
			break;
		}
		default:
			break;
		}
		return response;

	}

	private List<Object> getCustomLabTests(long page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Object> response = null;
		List<LabTestCollection> labTestCollections = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {
			if (discarded)
				discards.add(true);
			long createdTimeStamp = Long.parseLong(updatedTime);

			if (locationId == null && hospitalId == null) {
				labTestCollections = new ArrayList<LabTestCollection>();
			} else {
				if (size > 0)
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(
									new ObjectId(hospitalId), new ObjectId(locationId), new Date(createdTimeStamp),
									discards, PageRequest.of((int) page, size, Direction.DESC, "updatedTime"));
				else
					labTestCollections = labTestRepository
							.findByHospitalIdAndLocationIdAndUpdatedTimeGreaterThanAndDiscardedIn(
									new ObjectId(hospitalId), new ObjectId(locationId), new Date(createdTimeStamp),
									discards, new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (!labTestCollections.isEmpty()) {
				response = new ArrayList<Object>();
				for (LabTestCollection labTestCollection : labTestCollections) {
					LabTest labTest = new LabTest();
					BeanUtil.map(labTestCollection, labTest);
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(labTestCollection.getTestId()).orElse(null);
					DiagnosticTest diagnosticTest = new DiagnosticTest();
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					labTest.setTest(diagnosticTest);
					response.add(labTest);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	private List<Drug> getGlobalDrugs(long page, int size, String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<Drug> getCustomDrugs(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), DrugCollection.class,
					Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<Drug> getCustomGlobalDrugs(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Drug> response = null;
		try {
			AggregationResults<Drug> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomGlobalAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null, null),
					DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugType> getGlobalDrugType(long page, int size, String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomDrugType(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private List<DrugType> getCustomGlobalDrugType(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugType> response = null;
		try {
			AggregationResults<DrugType> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null, null),
							DrugTypeCollection.class, DrugType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Type");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Type");
		}
		return response;
	}

	private Response<Object> getGlobalDrugDirection(int page, int size, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {

			Criteria criteria = new Criteria("updatedTime").gte(new Date(Long.parseLong(updatedTime))).and("doctorId")
					.is(null).and("locationId").is(null).and("hospitalId").is(null);
			if (!discarded)
				criteria.and("discarded").is(discarded);

			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);
			if (count > 0) {
				AggregationResults<DrugDirection> results = mongoTemplate.aggregate(DPDoctorUtils
						.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
						DrugDirectionCollection.class, DrugDirection.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private Response<Object> getCustomDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			Criteria criteria = new Criteria("updatedTime").gte(new Date(Long.parseLong(updatedTime)));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);
			if (count > 0) {
				AggregationResults<DrugDirection> results = mongoTemplate
						.aggregate(
								DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
										updatedTime, discarded, null, null, null),
								DrugDirectionCollection.class, DrugDirection.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private Response<Object> getCustomGlobalDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {

			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.orOperator(
							new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
									.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
							new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
				} else {
					criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
							new Criteria("doctorId").is(null));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
								.is(new ObjectId(hospitalId)),
						new Criteria("locationId").is(null).and("hospitalId").is(null));
			}
			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDirectionCollection.class);
			if (count > 0) {
				AggregationResults<DrugDirection> results = mongoTemplate.aggregate(
						DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
								updatedTime, discarded, null, null, null, null),
						DrugDirectionCollection.class, DrugDirection.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private Response<Object> getGlobalDrugDosage(int page, int size, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
					.and("locationId").is(null).and("hospitalId").is(null);
			if (!discarded)
				criteria.and("discarded").is(discarded);

			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);
			if (count > 0) {
				AggregationResults<DrugDosage> results = mongoTemplate.aggregate(DPDoctorUtils
						.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
						DrugDosageCollection.class, DrugDosage.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private Response<Object> getCustomDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);
			if (count > 0) {
				AggregationResults<DrugDosage> results = mongoTemplate
						.aggregate(
								DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
										updatedTime, discarded, null, null, null),
								DrugDosageCollection.class, DrugDosage.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private Response<Object> getCustomGlobalDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.orOperator(
							new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
									.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
							new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
				} else {
					criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
							new Criteria("doctorId").is(null));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
								.is(new ObjectId(hospitalId)),
						new Criteria("locationId").is(null).and("hospitalId").is(null));
			}
			Integer count = (int) mongoTemplate.count(new Query(criteria), DrugDosageCollection.class);
			if (count > 0) {
				AggregationResults<DrugDosage> results = mongoTemplate.aggregate(
						DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
								updatedTime, discarded, null, null, null, null),
						DrugDosageCollection.class, DrugDosage.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDurationUnit> getGlobalDrugDurationUnit(long page, int size, String updatedTime,
			boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomDrugDurationUnit(long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Duration Unit");
		}
		return response;
	}

	private List<DrugDurationUnit> getCustomGlobalDrugDurationUnit(long page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDurationUnit> response = null;
		try {
			AggregationResults<DrugDurationUnit> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null, null),
					DrugDurationUnitCollection.class, DrugDurationUnit.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Duration Unit");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug DurationUnit");
		}
		return response;
	}

	@Override
	@Transactional
	public void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		try {
			if (doctorId != null && locationId != null && hospitalId != null) {
				mailResponse = createMailData(prescriptionId, doctorId, locationId, hospitalId);
			} else {
				mailResponse = createMailDataForWeb(prescriptionId, doctorId, locationId, hospitalId);
			}
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Prescription", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Prescription", body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public MailResponse getPrescriptionMailData(String prescriptionId, String doctorId, String locationId,
			String hospitalId) {
		return createMailData(prescriptionId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String prescriptionId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		PrescriptionCollection prescriptionCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().toString().equals(doctorId)
							&& prescriptionCollection.getHospitalId().toString().equals(hospitalId)
							&& prescriptionCollection.getLocationId().toString().equals(locationId)) {

						user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
								prescriptionCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(prescriptionCollection.getDoctorId());
						emailTrackCollection.setHospitalId(prescriptionCollection.getHospitalId());
						emailTrackCollection.setLocationId(prescriptionCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
						emailTrackCollection.setSubject("Prescription");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(prescriptionCollection, patient, user,
								null, false, false, false, false, false);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId))
								.orElse(null);

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						response.setClinicAddress(address);
						response.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						response.setMailRecordCreatedDate(sdf.format(prescriptionCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Prescription Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Prescription Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Boolean smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String mobileNumber, String type) {
		Boolean response = false;
		PrescriptionCollection prescriptionCollection = null;
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(new ObjectId(doctorId))
							&& prescriptionCollection.getHospitalId().equals(new ObjectId(hospitalId))
							&& prescriptionCollection.getLocationId().equals(new ObjectId(locationId))) {

						UserCollection userCollection = userRepository.findById(prescriptionCollection.getPatientId())
								.orElse(null);
						PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
								prescriptionCollection.getHospitalId());
						if (patientCollection != null) {
							String prescriptionDetails = "";
							int i = 0;
							if (prescriptionCollection.getItems() != null
									&& !prescriptionCollection.getItems().isEmpty())
								for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
									if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
										DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId())
												.orElse(null);
										if (drug != null) {
											i++;

											String drugType = drug.getDrugType() != null
													? (!DPDoctorUtils.anyStringEmpty(drug.getDrugType().getType())
															? drug.getDrugType().getType()
															: "")
													: "";
											String drugName = !DPDoctorUtils.anyStringEmpty(drug.getDrugName())
													? drug.getDrugName()
													: "";

											String durationValue = prescriptionItem.getDuration() != null
													? (!DPDoctorUtils
															.anyStringEmpty(prescriptionItem.getDuration().getValue())
																	? prescriptionItem.getDuration().getValue()
																	: "")
													: "";
											String durationUnit = prescriptionItem.getDuration() != null
													? (prescriptionItem.getDuration().getDurationUnit() != null
															? prescriptionItem.getDuration().getDurationUnit().getUnit()
															: "")
													: "";

											if (!DPDoctorUtils.anyStringEmpty(durationValue))
												durationValue = "," + durationValue + durationUnit;
											String dosage = !DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? "," + prescriptionItem.getDosage()
													: "";

											String directions = "";
											if (prescriptionItem.getDirection() != null
													&& !prescriptionItem.getDirection().isEmpty()) {
												for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
													if (!DPDoctorUtils.allStringsEmpty(drugDirection.getDirection()))
														if (directions != "")
															directions = "," + drugDirection.getDirection();
														else
															directions = drugDirection.getDirection();
												}
												if (directions != "")
													directions = "," + directions;
											}

											prescriptionDetails = prescriptionDetails +"\n"+ " " + i + ")" + drugType + " "

													+ drugName + dosage + durationValue + directions;
										}
									}
								}
							if (prescriptionCollection.getDiagnosticTests() != null
									&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
								if (!DPDoctorUtils.anyStringEmpty(prescriptionDetails))
									prescriptionDetails = prescriptionDetails + " and ";
								prescriptionDetails = prescriptionDetails + "Tests :";
								List<ObjectId> testIds = new ArrayList<ObjectId>();
								for (TestAndRecordData testAndRecordData : prescriptionCollection
										.getDiagnosticTests()) {
									testIds.add(testAndRecordData.getTestId());
								}

								Collection<String> tests = CollectionUtils.collect(
										(List<DiagnosticTestCollection>) diagnosticTestRepository.findAllById(testIds),
										new BeanToPropertyValueTransformer("testName"));
								prescriptionDetails = prescriptionDetails + " "
										+ tests.toString().replaceAll("\\[", "").replaceAll("\\]", "");
							}

							if (!DPDoctorUtils.anyStringEmpty(prescriptionDetails)) {
								SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

								String patientName = patientCollection.getLocalPatientName() != null
										? patientCollection.getLocalPatientName().split(" ")[0]
										: "", doctorName = "", clinicContactNum = "";

								UserCollection doctor = userRepository.findById(new ObjectId(doctorId)).orElse(null);
								if (doctor != null)
									doctorName = doctor.getTitle() + " " + doctor.getFirstName();

								LocationCollection locationCollection = locationRepository
										.findById(new ObjectId(locationId)).orElse(null);
								if (locationCollection != null && locationCollection.getClinicNumber() != null)
									clinicContactNum = " " + locationCollection.getClinicNumber();

								smsTrackDetail.setDoctorId(new ObjectId(doctorId));
								smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
								smsTrackDetail.setLocationId(new ObjectId(locationId));
								smsTrackDetail.setType(type);
								SMSDetail smsDetail = new SMSDetail();
								smsDetail.setUserId(prescriptionCollection.getPatientId());
								if (userCollection != null)
									smsDetail.setUserName(patientCollection.getLocalPatientName());
								SMS sms = new SMS();
								sms.setSmsText("Hi " + patientName + ", your prescription "
										+ prescriptionCollection.getUniqueEmrId() + " by " + doctorName + ". "
										+ prescriptionDetails + ". For queries,contact Doctor" + clinicContactNum
										+ ".-Healthcoco");

								SMSAddress smsAddress = new SMSAddress();
								smsAddress.setRecipient(mobileNumber);
								sms.setSmsAddress(smsAddress);

								smsDetail.setSms(sms);
								smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
								List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
								smsDetails.add(smsDetail);
								smsTrackDetail.setSmsDetails(smsDetails);
								smsTrackDetail.setTemplateId("1307161526775042485");
								response = sMSServices.sendSMS(smsTrackDetail, true);
							}
						}
					} else {
						logger.warn("Prescription not found.Please check prescriptionId.");
						throw new BusinessException(ServiceError.NoRecord,
								"Prescription not found.Please check prescriptionId.");
					}
				}
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
	public LabTest addLabTest(LabTest request) {
		LabTest response = null;
		LabTestCollection labTestCollection = new LabTestCollection();
		BeanUtil.map(request, labTestCollection);
		try {
			if (request.getTest() != null) {
				Date createdTime = new Date();
				labTestCollection.setCreatedTime(createdTime);
				LocationCollection locationCollection = null;
				if (!DPDoctorUtils.anyStringEmpty(labTestCollection.getLocationId())) {
					locationCollection = locationRepository.findById(labTestCollection.getLocationId()).orElse(null);
					if (locationCollection != null)
						labTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					labTestCollection.setCreatedBy("ADMIN");
				}
				DiagnosticTestCollection diagnosticTestCollection = null;
				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findById(new ObjectId(request.getTest().getId())).orElse(null);
				if (diagnosticTestCollection == null) {

					if (request.getTest().getTestName() == null) {
						logger.error("Cannot create lab test without diagnostic test");
						throw new BusinessException(ServiceError.Unknown,
								"Cannot create lab test without diagnostic test");
					}
					diagnosticTestCollection = new DiagnosticTestCollection();
					diagnosticTestCollection.setLocationId(new ObjectId(request.getLocationId()));
					diagnosticTestCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					diagnosticTestCollection.setTestName(request.getTest().getTestName());
					diagnosticTestCollection.setCreatedTime(createdTime);
					if (locationCollection != null)
						diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
					diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				}
				transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST, false);
				ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
				esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);

				labTestCollection.setTestId(diagnosticTestCollection.getId());
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
				DiagnosticTest diagnosticTest = new DiagnosticTest();
				if (diagnosticTestCollection != null) {
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					response.setTest(diagnosticTest);
				}
			} else {
				logger.error("Cannot create lab test without diagnostic test");
				throw new BusinessException(ServiceError.Unknown, "Cannot create lab test without diagnostic test");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public LabTest editLabTest(LabTest request) {
		LabTest response = null;
		LabTestCollection labTestCollection = new LabTestCollection();
		BeanUtil.map(request, labTestCollection);
		try {
			if (request.getTest() != null) {
				LabTestCollection oldLabTest = labTestRepository.findById(new ObjectId(request.getId())).orElse(null);
				labTestCollection.setCreatedBy(oldLabTest.getCreatedBy());
				labTestCollection.setCreatedTime(oldLabTest.getCreatedTime());
				labTestCollection.setDiscarded(oldLabTest.getDiscarded());
				DiagnosticTestCollection diagnosticTestCollection = null;

				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findById(new ObjectId(request.getTest().getId())).orElse(null);
				if (diagnosticTestCollection == null) {
					if (request.getTest().getTestName() == null) {
						logger.error("Cannot create lab test without diagnostic test");
						throw new BusinessException(ServiceError.Unknown,
								"Cannot create lab test without diagnostic test");
					}
					diagnosticTestCollection = new DiagnosticTestCollection();
					diagnosticTestCollection.setLocationId(new ObjectId(request.getLocationId()));
					diagnosticTestCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					diagnosticTestCollection.setTestName(request.getTest().getTestName());
					diagnosticTestCollection.setCreatedTime(new Date());
					diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				}
				transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST, false);
				ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
				BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
				esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);

				labTestCollection.setTestId(diagnosticTestCollection.getId());
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
				DiagnosticTest diagnosticTest = new DiagnosticTest();
				if (diagnosticTestCollection != null) {
					BeanUtil.map(diagnosticTestCollection, diagnosticTest);
					response.setTest(diagnosticTest);
				}
			} else {
				logger.error("Cannot create lab test without diagnostic test");
				throw new BusinessException(ServiceError.Unknown, "Cannot create lab test without diagnostic test");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Lab Test");
		}
		return response;

	}

	@Override
	@Transactional
	public LabTest deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded) {
		LabTest response = null;
		LabTestCollection labTestCollection = null;
		try {
			labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				if (labTestCollection.getHospitalId() != null && labTestCollection.getLocationId() != null) {
					if (labTestCollection.getHospitalId().toString().equals(hospitalId)
							&& labTestCollection.getLocationId().toString().equals(locationId)) {
						labTestCollection.setDiscarded(discarded);
						labTestCollection.setUpdatedTime(new Date());
						labTestCollection = labTestRepository.save(labTestCollection);
						response = new LabTest();
						BeanUtil.map(labTestCollection, response);
					} else {
						logger.warn("Invalid Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Lab Test");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Lab Test");
				}
			} else {
				logger.warn("Lab Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
		}
		return response;

	}

	@Override
	@Transactional
	public LabTest deleteLabTest(String labTestId, Boolean discarded) {
		LabTest response = null;
		LabTestCollection labTestCollection = null;
		try {
			labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				labTestCollection.setUpdatedTime(new Date());
				labTestCollection.setDiscarded(discarded);
				labTestCollection = labTestRepository.save(labTestCollection);
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
			} else {
				logger.warn("Lab Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Lab Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public LabTest getLabTestById(String labTestId) {
		LabTest response = null;
		try {
			LabTestCollection labTestCollection = labTestRepository.findById(new ObjectId(labTestId)).orElse(null);
			if (labTestCollection != null) {
				response = new LabTest();
				BeanUtil.map(labTestCollection, response);
			} else {
				logger.warn("Lab Test not found. Please check Lab Test Id");
				throw new BusinessException(ServiceError.NoRecord, "Lab Test not found. Please check Lab Test Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Lab Test");
		}
		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	@Override
	@Transactional
	public List<DiagnosticTestCollection> getDiagnosticTest() {
		List<DiagnosticTestCollection> response = null;
		try {
			response = diagnosticTestRepository.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting LabTests");
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getPrescriptions(String patientId, long page, int size, String updatedTime,
			Boolean discarded) {
		Response<Object> response = new Response<Object>();
		List<Prescription> prescriptions = null;
		List<Boolean> discards = new ArrayList<Boolean>();
		discards.add(false);
		try {

			if (discarded)
				discards.add(true);

			long createdTimestamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria()
					.andOperator(new Criteria("patientId").is(new ObjectId(patientId)),
							new Criteria("updatedTime").gt(new Date(createdTimestamp)))
					.and("isPatientDiscarded").ne(true);
			if (!discarded)
				criteria.and("discarded").is(discarded);

			long count = mongoTemplate.count(new Query(criteria), PrescriptionCollection.class);
			if (count > 0) {
				response.setData(count);
				ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("name", "$name"),
						Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("locationId", "$locationId"),
						Fields.field("hospitalId", "$hospitalId"), Fields.field("doctorId", "$doctorId"),
						Fields.field("discarded", "$discarded"),
						Fields.field("appointmentRequest", "$appointmentRequest"),
						Fields.field("inHistory", "$inHistory"), Fields.field("advice", "$advice"),
						Fields.field("time", "$time"), Fields.field("fromDate", "$fromDate"),
						Fields.field("patientId", "$patientId"),
						Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
						Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
						Fields.field("createdTime", "$createdTime"), Fields.field("locationName", "$locationName"),
						Fields.field("createdBy", "$createdBy"), Fields.field("updatedTime", "$updatedTime"),
						Fields.field("items.drug", "$drug"), Fields.field("items.duration", "$items.duration"),
						Fields.field("items.dosage", "$items.dosage"),
						Fields.field("items.dosageTime", "$items.dosageTime"),
						Fields.field("items.direction", "$items.direction"),
						Fields.field("items.instructions", "$items.instructions"),
						Fields.field("tests", "$diagnosticTests")));

				Aggregation aggregation = null;

				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
									"appointmentRequest"),
							Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$appointmentRequest")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$visit").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
							Aggregation.skip((page) * size), Aggregation.limit(size));

				} else
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
									"appointmentRequest"),

							Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$appointmentRequest")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$visit").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("appointmentRequest", new BasicDBObject("$first", "$appointmentRequest"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$push", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("tests", new BasicDBObject("$first", "$tests"))
									.append("time", new BasicDBObject("$first", "$time"))
									.append("fromDate", new BasicDBObject("$first", "$fromDate"))
									.append("patientId", new BasicDBObject("$first", "$patientId"))
									.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
									.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
									.append("visitId", new BasicDBObject("$first", "$visitId"))
									.append("createdTime", new BasicDBObject("$first", "$createdTime"))
									.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
									.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				AggregationResults<Prescription> aggregationResults = mongoTemplate.aggregate(aggregation,
						"prescription_cl", Prescription.class);
				prescriptions = aggregationResults.getMappedResults();

				if (prescriptions != null && !prescriptions.isEmpty()) {
					for (Prescription prescription : prescriptions) {
						if (prescription.getTests() != null && !prescription.getTests().isEmpty()) {
							List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
							for (TestAndRecordData data : prescription.getTests()) {
								if (data.getTestId() != null) {
									DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
											.findById(data.getTestId()).orElse(null);
									DiagnosticTest diagnosticTest = new DiagnosticTest();
									if (diagnosticTestCollection != null) {
										BeanUtil.map(diagnosticTestCollection, diagnosticTest);
									}
									diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest,
											(!DPDoctorUtils.anyStringEmpty(data.getRecordId())
													? data.getRecordId().toString()
													: null)));
								}
							}
							prescription.setTests(null);
							prescription.setDiagnosticTests(diagnosticTests);
						}
					}
				}
				response.setDataList(prescriptions);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return response;
	}

	@Override
	@Transactional
	public DiagnosticTest addEditDiagnosticTest(DiagnosticTest request) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = new DiagnosticTestCollection();
		BeanUtil.map(request, diagnosticTestCollection);
		try {
			if (request.getId() == null) {
				diagnosticTestCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagnosticTestCollection.getLocationId())) {
					LocationCollection locationCollection = locationRepository
							.findById(diagnosticTestCollection.getLocationId()).orElse(null);
					if (locationCollection != null)
						diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					diagnosticTestCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosticTestCollection oldDiagnosticTestCollection = diagnosticTestRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				oldDiagnosticTestCollection.setCreatedBy(oldDiagnosticTestCollection.getCreatedBy());
				oldDiagnosticTestCollection.setCreatedTime(oldDiagnosticTestCollection.getCreatedTime());
				oldDiagnosticTestCollection.setDiscarded(oldDiagnosticTestCollection.getDiscarded());

				diagnosticTestCollection.setUpdatedTime(new Date());
			}
			diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
			transnationalService.addResource(diagnosticTestCollection.getId(), Resource.DIAGNOSTICTEST, false);
			ESDiagnosticTestDocument diagnosticTestDocument = new ESDiagnosticTestDocument();
			BeanUtil.map(diagnosticTestCollection, diagnosticTestDocument);
			esPrescriptionService.addEditDiagnosticTest(diagnosticTestDocument);
			response = new DiagnosticTest();
			BeanUtil.map(diagnosticTestCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Lab Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Lab Test");
		}
		return response;
	}

	@Override
	@Transactional
	public DiagnosticTest getDiagnosticTest(String diagnosticTestId) {
		DiagnosticTest response = null;
		try {
			DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
					.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				response = new DiagnosticTest();
				BeanUtil.map(diagnosticTestCollection, response);
			} else {
				logger.warn("Diagnostic Test not found. Please check DiagnosticT Test Id");
				throw new BusinessException(ServiceError.NoRecord,
						"Diagnostic Test not found. Please check DiagnosticT Test Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Test");
		}
		return response;

	}

	@Override
	@Transactional
	public DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, String hospitalId, String locationId,
			Boolean discarded) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = null;
		try {
			diagnosticTestCollection = diagnosticTestRepository.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				if (diagnosticTestCollection.getHospitalId() != null
						&& diagnosticTestCollection.getLocationId() != null) {
					if (diagnosticTestCollection.getHospitalId().toString().equals(hospitalId)
							&& diagnosticTestCollection.getLocationId().toString().equals(locationId)) {
						diagnosticTestCollection.setDiscarded(discarded);
						diagnosticTestCollection.setUpdatedTime(new Date());
						diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
						response = new DiagnosticTest();
						BeanUtil.map(diagnosticTestCollection, response);
					} else {
						logger.warn("Invalid Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized, "Invalid Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Lab Test");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Lab Test");
				}
			} else {
				logger.warn("Diagnostic Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Diagnostic Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagnostic Test");
		}
		return response;
	}

	@Override
	@Transactional
	public DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, Boolean discarded) {
		DiagnosticTest response = null;
		DiagnosticTestCollection diagnosticTestCollection = null;
		try {
			diagnosticTestCollection = diagnosticTestRepository.findById(new ObjectId(diagnosticTestId)).orElse(null);
			if (diagnosticTestCollection != null) {
				diagnosticTestCollection.setUpdatedTime(new Date());
				diagnosticTestCollection.setDiscarded(discarded);
				diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
				response = new DiagnosticTest();
				BeanUtil.map(diagnosticTestCollection, response);
			} else {
				logger.warn("Diagnostic Test Not Found");
				throw new BusinessException(ServiceError.NotFound, "Diagnostic Test Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Diagnostic Test");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Diagnostic Test");
		}
		return response;
	}

	private Response<Object> getGlobalDiagnosticTests(int page, int size, String updatedTime, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
					.and("locationId").is(null).and("hospitalId").is(null);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			Integer count = (int) mongoTemplate.count(new Query(criteria), DiagnosticTestCollection.class);
			if (count > 0) {
				AggregationResults<DiagnosticTest> results = mongoTemplate.aggregate(DPDoctorUtils
						.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
						DiagnosticTestCollection.class, DiagnosticTest.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private Response<Object> getCustomDiagnosticTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			Integer count = (int) mongoTemplate.count(new Query(criteria), DiagnosticTestCollection.class);
			if (count > 0) {
				AggregationResults<DiagnosticTest> results = mongoTemplate
						.aggregate(
								DPDoctorUtils.createCustomAggregation(page, size, null, locationId, hospitalId,
										updatedTime, discarded, null, null, null),
								DiagnosticTestCollection.class, DiagnosticTest.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private Response<Object> getCustomGlobalDiagnosticTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {

			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
								.is(new ObjectId(hospitalId)),
						new Criteria("locationId").is(null).and("hospitalId").is(null));
			}
			Integer count = (int) mongoTemplate.count(new Query(criteria), DiagnosticTestCollection.class);
			if (count > 0) {
				AggregationResults<DiagnosticTest> results = mongoTemplate.aggregate(
						DPDoctorUtils.createCustomGlobalAggregation(page, size, null, locationId, hospitalId,
								updatedTime, discarded, null, null, null, null),
						DiagnosticTestCollection.class, DiagnosticTest.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId, String locationId,
			String hospitalId) {
		PrescriptionTestAndRecord response = null;
		List<TestAndRecordDataResponse> tests = null;
		PrescriptionLookupResponse prescriptionCollection = null;
		Boolean isPatientRegistered = true;
		try {
			if (DPDoctorUtils.anyStringEmpty(patientId)) {
				Aggregation aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria("uniqueEmrId").is(uniqueEmrId.toUpperCase())
								.and("isPatientDiscarded").ne(true)),
						Aggregation.lookup("user_cl", "patientId", "_id", "user"), Aggregation.unwind("user"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"),
						new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
								Fields.field("uniqueEmrId", "$uniqueEmrId"), Fields.field("doctorId", "$doctorId"),
								Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
								Fields.field("diagnosticTests", "$diagnosticTests"),
								Fields.field("patientId", "$patientId"), Fields.field("firstName", "$user.firstName"),
								Fields.field("mobileNumber", "$user.mobileNumber"),
								Fields.field("createdBy", "$createdBy"),
								Fields.field("locationName", "$location.locationName"))));
				prescriptionCollection = mongoTemplate
						.aggregate(aggregation, PrescriptionCollection.class, PrescriptionLookupResponse.class)
						.getUniqueMappedResult();

				Integer patientCount = patientRepository.findCount(new ObjectId(prescriptionCollection.getPatientId()),
						new ObjectId(locationId), new ObjectId(hospitalId));
				if (patientCount == null || patientCount == 0)
					isPatientRegistered = false;
			} else {
				prescriptionCollection = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(new Criteria("uniqueEmrId").is(uniqueEmrId)
								.and("patientId").is(new ObjectId(patientId)))),
						PrescriptionCollection.class, PrescriptionLookupResponse.class).getUniqueMappedResult();
			}
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDiagnosticTests() != null
						&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
					tests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData recordData : prescriptionCollection.getDiagnosticTests()) {
						DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
								.findById(recordData.getTestId()).orElse(null);
						if (diagnosticTestCollection != null) {
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							TestAndRecordDataResponse dataResponse;
							if (!DPDoctorUtils.anyStringEmpty(recordData.getRecordId())) {
								dataResponse = new TestAndRecordDataResponse(diagnosticTest,
										recordData.getRecordId().toString());
							} else {
								dataResponse = new TestAndRecordDataResponse(diagnosticTest, null);
							}

							tests.add(dataResponse);
						}
					}
					if (tests != null && !tests.isEmpty()) {
						response = new PrescriptionTestAndRecord();
						response.setUniqueEmrId(prescriptionCollection.getUniqueEmrId());
						response.setPatientId(prescriptionCollection.getPatientId().toString());
						response.setIsPatientRegistered(isPatientRegistered);
						response.setTests(tests);
						response.setFirstName(prescriptionCollection.getFirstName());
						response.setMobileNumber(prescriptionCollection.getMobileNumber());
						response.setDoctorName(prescriptionCollection.getCreatedBy());
						response.setLocationName(prescriptionCollection.getLocationName());
					}
				} else {
					throw new BusinessException(ServiceError.NoRecord, "No test Exists for this prescription");
				}
			} else {
				throw new BusinessException(ServiceError.InvalidInput, checkPrescriptionExists);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Checking Prescription Exists");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Checking Prescription Exists");
		}
		return response;
	}

	@Override
	public String getPrescriptionFile(String prescriptionId, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Boolean isLabPrint) {
		String response = null;
		HistoryCollection historyCollection = null;
		try {
			PrescriptionCollection prescriptionCollection = prescriptionRepository
					.findById(new ObjectId(prescriptionId)).orElse(null);

			if (prescriptionCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				UserCollection user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);

				if (showPH || showPLH || showFH || showDA) {
					List<HistoryCollection> historyCollections = historyRepository
							.findByLocationIdAndHospitalIdAndPatientId(prescriptionCollection.getLocationId(),
									prescriptionCollection.getHospitalId(), prescriptionCollection.getPatientId());
					if (historyCollections != null)
						historyCollection = historyCollections.get(0);
				}
				JasperReportResponse jasperReportResponse = createJasper(prescriptionCollection, patient, user,
						historyCollection, showPH, showPLH, showFH, showDA, isLabPrint);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Prescription Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Prescription PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(PrescriptionCollection prescriptionCollection, PatientCollection patient,
			UserCollection user, HistoryCollection historyCollection, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Boolean isLabPrint) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
		JasperReportResponse response = null;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId(), ComponentType.ALL.getType(),
						PrintSettingType.EMR.getType());
		if (printSettings == null){
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
							prescriptionCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(),new Sort(Sort.Direction.DESC, "updatedTime"));
			if(!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		if (!isLabPrint) {
			int no = 0;
			Boolean showIntructions = false, showDirection = false, showDrugQty = false;
			if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty())
				for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
					if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
						DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId()).orElse(null);
						if (drug != null) {
							String drugType = drug.getDrugType() != null
									? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "")
									: "";
							String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
							String genericName = "";
							if (printSettings.getShowDrugGenericNames() && drug.getGenericNames() != null
									&& !drug.getGenericNames().isEmpty()) {
								for (GenericCode genericCode : drug.getGenericNames()) {
									if (DPDoctorUtils.anyStringEmpty(genericName))
										genericName = genericCode.getName();
									else
										genericName = genericName + "+" + genericCode.getName();
								}
								genericName = "<br><font size='1'><i>" + genericName + "</i></font>";
							}
							if (drug.getDrugTypePlacement() != null) {
								if (drug.getDrugTypePlacement().equalsIgnoreCase("PREFIX")) {
									drugName = (drugType + drugName) == "" ? "--"
											: drugType + " " + drugName + genericName;
								} else if (drug.getDrugTypePlacement().equalsIgnoreCase("SUFFIX")) {
									drugName = (drugType + drugName) == "" ? "--"
											: drugName + " " + drugType + genericName;
								}
							} else {
								drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName + genericName;
							}
							// drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName +
							// genericName;
//							String drugQuantity = "";
//							if (prescriptionItem.getInventoryQuantity() != null
//									&& prescriptionItem.getInventoryQuantity() > 0) {
//								showDrugQty = true;
//								drugQuantity = "" + prescriptionItem.getInventoryQuantity().toString();
//								System.out.println("drugqty" + drugQuantity);
//								drugName = drugName + "<br>" + "<b>QTY: </b>" + drugQuantity;
//								System.out.println("drugName" + drugName);
//							}
							String durationValue = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration().getValue() != null
											? prescriptionItem.getDuration().getValue()
											: "")
									: "";
							String durationUnit = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration().getDurationUnit() != null
											? (!DPDoctorUtils.anyStringEmpty(
													prescriptionItem.getDuration().getDurationUnit().getUnit())
															? prescriptionItem.getDuration().getDurationUnit().getUnit()
															: "")
											: "")
									: "";

							String directions = "";
							if (prescriptionItem.getDirection() != null && !prescriptionItem.getDirection().isEmpty()) {
								showDirection = true;
								if (prescriptionItem.getDirection().get(0).getDirection() != null) {
									if (directions == "")
										directions = directions
												+ (prescriptionItem.getDirection().get(0).getDirection());
									else
										directions = directions + ","
												+ (prescriptionItem.getDirection().get(0).getDirection());
								}
							}
							if (!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())) {
								if (printSettings.getContentSetup() != null) {
									if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
											.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {
										prescriptionItem.setInstructions(
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
														? "<b>Instruction </b>: " + prescriptionItem.getInstructions()
														: null);
									} else {
										prescriptionItem.setInstructions(
												!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
														? prescriptionItem.getInstructions()
														: null);
									}
								} else {
									prescriptionItem.setInstructions(
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: null);
								}

								showIntructions = true;
							}
							String duration = "";
							if (durationValue == "" && durationValue == "")
								duration = "--";
							else
								duration = durationValue + " " + durationUnit;

							PrescriptionJasperDetails prescriptionJasperDetails = null;
							if (printSettings.getContentSetup() != null) {
								if (printSettings.getContentSetup().getInstructionAlign() != null && printSettings
										.getContentSetup().getInstructionAlign().equals(FieldAlign.HORIZONTAL)) {

									prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? prescriptionItem.getDosage()
													: "--",
											duration, directions.isEmpty() ? "--" : directions,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: null,
											genericName);
								} else {
									prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? prescriptionItem.getDosage()
													: "--",
											duration, directions.isEmpty() ? "--" : directions,
											!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
													? prescriptionItem.getInstructions()
													: "--",
											genericName);
								}
							} else {
								prescriptionJasperDetails = new PrescriptionJasperDetails(++no, drugName,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
												? prescriptionItem.getDosage()
												: "--",
										duration, directions.isEmpty() ? "--" : directions,
										!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
												? prescriptionItem.getInstructions()
												: "--",
										genericName);
							}
							if (prescriptionItem.getDrugQuantity() == null) {
								prescriptionJasperDetails.setDrugQuantity("0");
							} else {
								showDrugQty = true;
								prescriptionJasperDetails
										.setDrugQuantity(prescriptionItem.getDrugQuantity().toString());
							}
							prescriptionItems.add(prescriptionJasperDetails);
						}
					}
				}
			if (parameters.get("followUpAppointment") == null
					&& !DPDoctorUtils.anyStringEmpty(prescriptionCollection.getAppointmentId())
					&& prescriptionCollection.getTime() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
				String _24HourTime = String.format("%02d:%02d", prescriptionCollection.getTime().getFromTime() / 60,
						prescriptionCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

				Date _24HourDt = _24HourSDF.parse(_24HourTime);
				String dateTime = _12HourSDF.format(_24HourDt) + ", "
						+ sdf.format(prescriptionCollection.getFromDate());
				parameters.put("followUpAppointment", "Next Review on " + dateTime);
			}
			parameters.put("showDrugQty", showDrugQty);
			parameters.put("prescriptionItems", prescriptionItems);
			parameters.put("showIntructions", showIntructions);
			parameters.put("showDirection", showDirection);
			parameters.put("advice",
					prescriptionCollection.getAdvice() != null ? prescriptionCollection.getAdvice() : null);
		}
		parameters.put("prescriptionId", prescriptionCollection.getId().toString());
		String labTest = "";
		if (prescriptionCollection.getDiagnosticTests() != null
				&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
			for (TestAndRecordData tests : prescriptionCollection.getDiagnosticTests()) {
				if (tests.getTestId() != null) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findById(tests.getTestId()).orElse(null);
					if (diagnosticTestCollection != null) {
						if (DPDoctorUtils.anyStringEmpty(labTest))
							labTest = diagnosticTestCollection.getTestName();
						else
							labTest = labTest + ", " + diagnosticTestCollection.getTestName();
					}
				}
			}
		}
		if (labTest != null && !labTest.isEmpty())
			parameters.put("labTest", labTest);
		else
			parameters.put("labTest", null);

		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());

		if (historyCollection != null) {
			parameters.put("showHistory", true);
			patientVisitService.includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
		}

		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient,
				"<b>RxID: </b>"
						+ (prescriptionCollection.getUniqueEmrId() != null ? prescriptionCollection.getUniqueEmrId()
								: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				prescriptionCollection.getCreatedTime() != null ? prescriptionCollection.getCreatedTime() : new Date(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings, prescriptionCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "PRESCRIPTION-"
				+ prescriptionCollection.getUniqueEmrId() + new Date().getTime();
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getTopMargin() != null
						? printSettings.getPageSetup().getTopMargin()
						: 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getBottomMargin() != null
						? printSettings.getPageSetup().getBottomMargin()
						: 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;

		response = jasperReportService.createPDF(ComponentType.PRESCRIPTIONS, parameters, prescriptionA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""),
				prescriptionSubReportA4FileName);
		return response;
	}

	@Override
	public Drug makeDrugFavourite(String drugId, String doctorId, String locationId, String hospitalId) {
		Drug response = null;

		try {
			ObjectId drugObjectId = new ObjectId(drugId), doctorObjectId = new ObjectId(doctorId),
					locationObjectId = new ObjectId(locationId), hospitalObjectId = new ObjectId(hospitalId);
			DrugCollection originalDrug = drugRepository.findById(drugObjectId).orElse(null);
			if (originalDrug == null) {
				logger.error("Invalid drug Id");
				throw new BusinessException(ServiceError.Unknown, "Invalid drug Id");
			}
			DrugCollection drugCollection = drugRepository.findByDrugCodeAndDoctorId(originalDrug.getDrugCode(),
					doctorObjectId);
			if (drugCollection == null) {
				drugCollection = originalDrug;

				drugCollection.setLocationId(locationObjectId);
				drugCollection.setHospitalId(hospitalObjectId);
				drugCollection.setDoctorId(doctorObjectId);
				drugCollection.setRankingCount(1);
				drugCollection.setId(null);
			} else {
				drugCollection.setLocationId(locationObjectId);
				drugCollection.setHospitalId(hospitalObjectId);
				drugCollection.setRankingCount(drugCollection.getRankingCount() + 1);
				drugCollection.setUpdatedTime(new Date());
			}
			drugCollection = drugRepository.save(drugCollection);
			response = new Drug();
			BeanUtil.map(drugCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While making Drug Favourite");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While making Drug Favourite");
		}
		return response;
	}

	private Appointment addPrescriptionAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment, false);
		} else {
			response = new Appointment();
			BeanUtil.map(appointment, response);
		}
		return response;
	}

	@Override
	public Advice addAdvice(Advice request) {
		Advice response = null;
		try {
			response = new Advice();
			AdviceCollection adviceCollection = new AdviceCollection();
			BeanUtil.map(request, adviceCollection);

			if (DPDoctorUtils.anyStringEmpty(adviceCollection.getId())) {
				adviceCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(adviceCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(adviceCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						adviceCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					adviceCollection.setCreatedBy("ADMIN");
				}
			} else {
				Optional<AdviceCollection> adviceCollectionOptional = adviceRepository
						.findById(adviceCollection.getId());
				if (adviceCollectionOptional.isPresent()) {
					AdviceCollection oldAdviceCollection = adviceCollectionOptional.get();
					adviceCollection.setCreatedBy(oldAdviceCollection.getCreatedBy());
					adviceCollection.setCreatedTime(oldAdviceCollection.getCreatedTime());
					adviceCollection.setDiscarded(oldAdviceCollection.getDiscarded());
				}

			}
			adviceCollection.setDiscarded(false);
			adviceCollection = adviceRepository.save(adviceCollection);

			BeanUtil.map(adviceCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Advice");
		}
		return response;
	}

	private Response<Object> getCustomGlobalAdvices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, String disease, String searchTerm, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(disease))
				criteria.and("diseases").is(disease);

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
					criteria.orOperator(
							new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId")
									.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)),
							new Criteria("doctorId").is(null).and("locationId").is(null).and("hospitalId").is(null));
				} else {
					criteria.orOperator(new Criteria("doctorId").is(new ObjectId(doctorId)),
							new Criteria("doctorId").is(null));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				criteria.orOperator(
						new Criteria("locationId").is(new ObjectId(locationId)).and("hospitalId")
								.is(new ObjectId(hospitalId)),
						new Criteria("locationId").is(null).and("hospitalId").is(null));
			}
			Integer count = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);
			if (count > 0) {
				AggregationResults<Advice> results = mongoTemplate.aggregate(
						DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
								updatedTime, discarded, null, searchTerm, null, disease, "advice"),
						AdviceCollection.class, Advice.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;

	}

	private Response<Object> getGlobalAdvices(int page, int size, String doctorId, String updatedTime, String disease,
			String searchTerm, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp)).and("doctorId").is(null)
					.and("locationId").is(null).and("hospitalId").is(null);
			if (!discarded)
				criteria.and("discarded").is(discarded);

			Integer count = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);
			if (count > 0) {
				AggregationResults<Advice> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
						size, updatedTime, discarded, null, searchTerm, null, disease, "advice"),
						AdviceCollection.class, Advice.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}

	private Response<Object> getCustomAdvices(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String disease, String searchTerm, Boolean discarded) {
		Response<Object> response = new Response<Object>();
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			Integer count = (int) mongoTemplate.count(new Query(criteria), AdviceCollection.class);
			if (count > 0) {
				AggregationResults<Advice> results = mongoTemplate
						.aggregate(
								DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
										updatedTime, discarded, null, disease, null, "advice"),
								AdviceCollection.class, Advice.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}

	@Override
	public Advice deleteAdvice(String adviceId, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Advice response = new Advice();
		AdviceCollection adviceCollection = new AdviceCollection();
		try {
			Optional<AdviceCollection> adviceCollectionOptional = adviceRepository.findById(new ObjectId(adviceId));
			if (adviceCollectionOptional.isPresent()) {
				adviceCollection = adviceCollectionOptional.get();
				if (!DPDoctorUtils.anyStringEmpty(adviceCollection.getDoctorId(), adviceCollection.getHospitalId(),
						adviceCollection.getLocationId())) {
					if (adviceCollection.getDoctorId().toString().equals(doctorId)
							&& adviceCollection.getHospitalId().toString().equals(hospitalId)
							&& adviceCollection.getLocationId().toString().equals(locationId)) {
						adviceCollection.setDiscarded(discarded);
						adviceCollection.setUpdatedTime(new Date());
						adviceCollection = adviceRepository.save(adviceCollection);

						BeanUtil.map(adviceCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					adviceCollection.setDiscarded(discarded);
					adviceCollection.setUpdatedTime(new Date());
					adviceRepository.save(adviceCollection);
					response = new Advice();
					BeanUtil.map(adviceCollection, response);
				}
			} else {
				logger.warn("Advice not found!");
				throw new BusinessException(ServiceError.NoRecord, "Advice not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Deleting Advice");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Advice");
		}
		return response;

	}

	@Override
	public Drug addFavouriteDrug(DrugAddEditRequest request, DrugCollection originalDrug, String createdBy) {
		Drug response = null;
		DrugCollection drugCollection = new DrugCollection();
		try {
			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				BeanUtil.map(request, drugCollection);
				UUID drugCode = UUID.randomUUID();
				drugCollection.setDrugCode(drugCode.toString());

				if (DPDoctorUtils.anyStringEmpty(createdBy)) {
					if (!DPDoctorUtils.anyStringEmpty(drugCollection.getDoctorId())) {
						UserCollection userCollection = userRepository.findById(drugCollection.getDoctorId())
								.orElse(null);
						if (userCollection != null)
							createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName();
					}
				}
				drugCollection.setCreatedBy(createdBy);
				drugCollection.setDrugTypePlacement(request.getDrugTypePlacement());

				Date createdTime = new Date();
				drugCollection.setCreatedTime(createdTime);
				if (drugCollection.getDrugType() != null) {
					if (DPDoctorUtils.anyStringEmpty(drugCollection.getDrugType().getId()))
						drugCollection.setDrugType(null);
					else {
						DrugTypeCollection drugTypeCollection = drugTypeRepository
								.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
						if (drugTypeCollection != null) {
							DrugType drugType = new DrugType();
							BeanUtil.map(drugTypeCollection, drugType);
							drugCollection.setDrugType(drugType);
						}
					}
				}
				drugCollection = drugRepository.save(drugCollection);
			} else {
				if (originalDrug == null)
					originalDrug = drugRepository.findById(new ObjectId(request.getId())).orElse(null);

				if (originalDrug == null) {
					logger.error("Invalid drug Id");
					throw new BusinessException(ServiceError.Unknown, "Invalid drug Id");
				}

				if (originalDrug.getDoctorId() != null
						&& request.getDoctorId().equalsIgnoreCase(originalDrug.getDoctorId().toString())) {
					drugCollection = originalDrug;
				} else {
					drugCollection = drugRepository.findByDrugCodeAndDoctorId(originalDrug.getDrugCode(),
							new ObjectId(request.getDoctorId()));
				}

				if (drugCollection == null) {
					drugCollection = originalDrug;

					drugCollection.setLocationId(new ObjectId(request.getLocationId()));
					drugCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					drugCollection.setDoctorId(new ObjectId(request.getDoctorId()));
					drugCollection.setRankingCount(1);
					drugCollection.setId(null);
				} else {
					drugCollection.setLocationId(new ObjectId(request.getLocationId()));
					drugCollection.setHospitalId(new ObjectId(request.getHospitalId()));
					drugCollection.setRankingCount(drugCollection.getRankingCount() + 1);
					drugCollection.setUpdatedTime(new Date());
				}
				drugCollection.setUpdatedTime(new Date());
				drugCollection.setDuration(request.getDuration());
				drugCollection.setDosage(request.getDosage());
				drugCollection.setDosageTime(request.getDosageTime());
				drugCollection.setDirection(request.getDirection());

				if (drugCollection.getDrugType() != null) {
					if (DPDoctorUtils.anyStringEmpty(drugCollection.getDrugType().getId()))
						drugCollection.setDrugType(null);
					else {
						DrugTypeCollection drugTypeCollection = drugTypeRepository
								.findById(new ObjectId(drugCollection.getDrugType().getId())).orElse(null);
						if (drugTypeCollection != null) {
							DrugType drugType = new DrugType();
							BeanUtil.map(drugTypeCollection, drugType);
							drugCollection.setDrugType(drugType);
						}
					}
				}
				drugCollection = drugRepository.save(drugCollection);
			}

			transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
			if (drugCollection != null) {
				ESDrugDocument esDrugDocument = new ESDrugDocument();
				BeanUtil.map(drugCollection, esDrugDocument);
				if (drugCollection.getDrugType() != null) {
					esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
					esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
				}
				esPrescriptionService.addDrug(esDrugDocument);
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug");
		}
		return response;
	}
	
	@Override
	public Boolean transferGenericDrugs()
	{
		List<GenericCodesAndReactionsCollection> generics=genericCodesAndReactionsRepository.findAll();
		
		for(GenericCodesAndReactionsCollection generic:generics)
		{
			ESGenericCodesAndReactions reaction=esGenericCodesAndReactionsRepository.findById(generic.getGenericCode()).orElse(null);
			if(reaction==null)
			{
				reaction=new ESGenericCodesAndReactions();
				BeanUtil.map(generic, reaction);
				reaction.setId(generic.getGenericCode());
				reaction.setUpdatedTime(new Date());
				esGenericCodesAndReactionsRepository.save(reaction);
			}
			else
			{
				reaction.setCodes(generic.getCodes());
				reaction.setUpdatedTime(new Date());
				esGenericCodesAndReactionsRepository.save(reaction);
			}
		}
		
		return true;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DrugInteractionResposne> drugInteraction(List<Drug> request, String patientId) {
		List<DrugInteractionResposne> response = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				List<Drug> ongoingDrugs = getOngoingDrugs(patientId);
				if (ongoingDrugs != null && !ongoingDrugs.isEmpty())
					request.addAll(ongoingDrugs);
			}
			// if(!DPDoctorUtils.anyStringEmpty(patientId)){
			// Aggregation aggregation =
			// Aggregation.newAggregation(Aggregation.match(new
			// Criteria("patientId").is(new ObjectId(patientId))
			// .and("drugsAndAllergies").ne(null)));
			// List<HistoryCollection> historyCollections =
			// mongoTemplate.aggregate(aggregation, HistoryCollection.class,
			// HistoryCollection.class).getMappedResults();
			// Collection<List<Drug>> drugs =
			// CollectionUtils.collect(historyCollections, new
			// BeanToPropertyValueTransformer("drugsAndAllergies.drugs"));
			//
			// Collection<Drug> drugIds = CollectionUtils.collect(drugs, new
			// BeanToPropertyValueTransformer("Drug"));
			// }
			List<String> genericCodes = new ArrayList<String>();

			for (int k = 0; k < request.size(); k++) {
				if (request.get(k).getGenericNames() != null && !request.get(k).getGenericNames().isEmpty()) {
					Collection<String> codes = CollectionUtils.collect(request.get(k).getGenericNames(),
							new BeanToPropertyValueTransformer("code"));
					if (codes != null && !codes.isEmpty()) {
						for (String word : codes) {
							if (!DPDoctorUtils.anyStringEmpty(word)) {
								word = word.toLowerCase();
								genericCodes.add(word);
							}
						}
					}
				}
			}
			if (genericCodes == null || genericCodes.isEmpty())
				return null;
			Cache.getAll(genericCodes);

			for (int drugCount = 0; drugCount < request.size() - 1; drugCount++) {
				if (request.get(drugCount).getGenericNames() != null
						&& !request.get(drugCount).getGenericNames().isEmpty())
					for (int j = drugCount + 1; j < request.size(); j++) {

						if (request.get(j).getGenericNames() != null && !request.get(j).getGenericNames().isEmpty()) {
							DrugInteractionResposne reaction = checkReactionBetweenDrugGenericCodes(
									request.get(drugCount).getGenericNames(), request.get(j).getGenericNames());
							if (reaction != null) {
								if (response == null)
									response = new ArrayList<DrugInteractionResposne>();
								DrugInteractionResposne interactionResposne = new DrugInteractionResposne(
										request.get(drugCount).getDrugName() + "  reacts with  "
												+ request.get(j).getDrugName(),
										reaction.getReaction(), reaction.getExplanation());
								response.add(interactionResposne);
							}
						}
					}
			}

		} 
//		catch (ExecutionException e) {
//			e.printStackTrace();
//			throw new BusinessException(ServiceError.Unknown, "Error Occurred While drugInteraction");
//		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While drugInteraction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While drugInteraction");
		}
		return response;
	}

	private List<Drug> getOngoingDrugs(String patientId) {
		List<Drug> response = null;
		try {
			ObjectId patientObjectId = new ObjectId(patientId);

			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("name", "$name"), Fields.field("uniqueEmrId", "$uniqueEmrId"),
							Fields.field("locationId", "$locationId"), Fields.field("hospitalId", "$hospitalId"),
							Fields.field("doctorId", "$doctorId"), Fields.field("discarded", "$discarded"),
							Fields.field("inHistory", "$inHistory"), Fields.field("advice", "$advice"),
							Fields.field("patientId", "$patientId"), Fields.field("createdTime", "$createdTime"),
							Fields.field("createdBy", "$createdBy"), Fields.field("updatedTime", "$updatedTime"),
							Fields.field("items.drug", "$drug"), Fields.field("items.duration", "$items.duration")));

			Aggregation aggregation = Aggregation
					.newAggregation(
							Aggregation.match(new Criteria("isActive").is(true).and("items").exists(true)
									.and("patientId").is(patientObjectId)),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							new CustomAggregationOperation(
									new Document("$unwind",
											new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new Document("$group",
									new BasicDBObject("_id", "$_id")
											.append("name", new BasicDBObject("$first", "$name"))
											.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("items", new BasicDBObject("$push", "$items"))
											.append("inHistory", new BasicDBObject("$first", "$inHistory"))
											.append("advice", new BasicDBObject("$first", "$advice"))
											.append("patientId", new BasicDBObject("$first", "$patientId"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
											.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			List<Prescription> prescriptionCollections = mongoTemplate
					.aggregate(aggregation, PrescriptionCollection.class, Prescription.class).getMappedResults();
			for (Prescription prescriptionCollection : prescriptionCollections) {
				for (PrescriptionItemDetail prescriptionItem : prescriptionCollection.getItems()) {
					if (prescriptionItem.getDuration() != null
							&& !DPDoctorUtils.anyStringEmpty(prescriptionItem.getDuration().getValue())
							&& prescriptionItem.getDuration().getDurationUnit() != null) {
						int noOfDays = 0;
						Calendar cal = Calendar.getInstance();
						Date createdTime = prescriptionCollection.getCreatedTime();
						cal.setTime(createdTime);

						switch (prescriptionItem.getDuration().getDurationUnit().getUnit()) {

						case "time(s)":
							break;
						case "year(s)": {
							cal.add(Calendar.YEAR, Integer.parseInt(prescriptionItem.getDuration().getValue()));
							long diff = cal.getTime().getTime() - new Date().getTime();
							noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							break;
						}
						case "month(s)": {
							cal.add(Calendar.MONTH, Integer.parseInt(prescriptionItem.getDuration().getValue()));
							long diff = cal.getTime().getTime() - new Date().getTime();
							noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							break;
						}
						case "week(s)": {
							noOfDays = Integer.parseInt(prescriptionItem.getDuration().getValue()) * 7;
							cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(prescriptionItem.getDuration().getValue()));
							long diff = cal.getTime().getTime() - new Date().getTime();
							noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							break;
						}
						case "day(s)": {
							cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(prescriptionItem.getDuration().getValue()));
							long diff = cal.getTime().getTime() - new Date().getTime();
							noOfDays = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
							break;
						}
						case "hour(s)": {
							break;
						}
						}
						if (noOfDays > 0) {
							if (response == null)
								response = new ArrayList<Drug>();
							response.add(prescriptionItem.getDrug());
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While get Ongoing Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While get Ongoing Drugs");
		}
		return response;
	}

	private DrugInteractionResposne checkReactionBetweenDrugGenericCodes(List<GenericCode> genericCodesOfFirstDrug,
			List<GenericCode> genericCodesOfOtherDrug) {
		DrugInteractionResposne response = null;
		try {
			List<GenericCode> commonCodes = commonCodesBetweenTwoDrugs(genericCodesOfFirstDrug,
					genericCodesOfOtherDrug);
			Comparator<Code> comparator = new Comparator<Code>() {
				public int compare(Code code1, Code code2) {
					return code1.getGenericCode().compareTo(code2.getGenericCode());
				}
			};

			// List<String> listOfReactions = new ArrayList<String>();
			for (int sizeOfGenericCodesinFirstDrug = 0; sizeOfGenericCodesinFirstDrug < genericCodesOfFirstDrug
					.size(); sizeOfGenericCodesinFirstDrug++) {

				if (!commonCodes.contains(genericCodesOfFirstDrug.get(sizeOfGenericCodesinFirstDrug))) {

					List<Code> codes = Cache.get(genericCodesOfFirstDrug.get(sizeOfGenericCodesinFirstDrug).getCode());

					if (codes.size() != 0) {
						for (int j = 0; j < genericCodesOfOtherDrug.size(); j++) {
							if (!commonCodes.contains(genericCodesOfOtherDrug.get(j))) {
								Code code = new Code(genericCodesOfOtherDrug.get(j).getCode(), "");

								int index = Collections.binarySearch(codes, code, comparator);
								if (index >= 0 && index < codes.size()) {
									if (DPDoctorUtils.anyStringEmpty(codes.get(index).getReaction())) {
										response = new DrugInteractionResposne("", "", "");
									} else {
										if (codes.get(index).getReaction().equalsIgnoreCase("MAJOR")) {
											response = new DrugInteractionResposne("", codes.get(index).getReaction(),
													codes.get(index).getExplanation());
										} else if (codes.get(index).getReaction().equalsIgnoreCase("MODERATE")) {
											if (!(response != null && response.getReaction().equalsIgnoreCase("MAJOR")))
												response = new DrugInteractionResposne("",
														codes.get(index).getReaction(),
														codes.get(index).getExplanation());
										} else if (codes.get(index).getReaction().equalsIgnoreCase("MINOR")) {
											if (!(response != null && response.getReaction().equalsIgnoreCase("MAJOR")
													&& response.getReaction().equalsIgnoreCase("MODERATE")))
												response = new DrugInteractionResposne("",
														codes.get(index).getReaction(),
														codes.get(index).getExplanation());
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While drugInteraction");
		}
		return response;
	}

	private List<GenericCode> commonCodesBetweenTwoDrugs(List<GenericCode> first, List<GenericCode> second) {
		List<GenericCode> commonCodes = new ArrayList<GenericCode>();
		for (GenericCode code : first) {
			if (second.contains(code)) {
				commonCodes.add(code);
			}
		}
		return commonCodes;
	}

	public boolean checkGenericContainsInSameDrug(String a, String b, Map<Drug, Set<String>> drugMap) {
		boolean response = false;
		for (Entry<Drug, Set<String>> entry : drugMap.entrySet()) {
			if (entry.getValue().contains(a) && entry.getValue().contains(b)) {
				response = true;
				break;
			}
		}
		return response;
	}

	// @Override
	// public Boolean addGenericsWithReaction() {
	// String csvFile = "/home/ubuntu/AnalgesicsAntipyretics.csv";
	// BufferedReader br = null;
	// String line = "";
	// String cvsSplitBy = ",";
	//
	// try {
	// br = new BufferedReader(new FileReader(csvFile));
	// while ((line = br.readLine()) != null) {
	// String[] codes = line.split(cvsSplitBy);
	// for (int i = 1; i < codes.length; i++) {
	// GenericCodeWithReactionCollection genericCodeWithReactionCollection =
	// genericCodeWithReactionRepository
	// .find(Arrays.asList(codes[0], codes[i]));
	// if (genericCodeWithReactionCollection == null) {
	// genericCodeWithReactionCollection = new
	// GenericCodeWithReactionCollection(
	// Arrays.asList(codes[0], codes[i]));
	// genericCodeWithReactionCollection.setCreatedBy("ADMIN");
	// genericCodeWithReactionCollection.setCreatedTime(new Date());
	// genericCodeWithReactionCollection = genericCodeWithReactionRepository
	// .save(genericCodeWithReactionCollection);
	//
	// ESGenericCodeWithReaction codeWithReaction = new
	// ESGenericCodeWithReaction();
	// BeanUtil.map(genericCodeWithReactionCollection, codeWithReaction);
	// esGenericCodeWithReactionRepository.save(codeWithReaction);
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (br != null) {
	// try {
	// br.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return true;
	// }

	// @Override
	// public Boolean addGenericsWithReaction() {
	// String csvFile = "/home/ubuntu/OnlinegenericCodes.csv";
	// BufferedReader br = null;
	// String line = "";
	// String cvsSplitBy = ",";
	// try {
	// br = new BufferedReader(new FileReader(csvFile));
	// while ((line = br.readLine()) != null) {
	// String[] codes = line.split(cvsSplitBy);
	// if (codes.length > 4 && codes[4] != null && !codes[4].isEmpty() &&
	// (codes[4].equalsIgnoreCase("MAJOR")
	// || codes[4].equalsIgnoreCase("MINOR") ||
	// codes[4].equalsIgnoreCase("MODERATE"))) {
	// String genericCodeOne = codes[0], genericCodeTwo = codes[2];
	//
	// List<GenericCodesAndReactionsCollection>
	// genericCodesAndReactionsCollections = genericCodesAndReactionsRepository
	// .findbyGenericCodes(Arrays.asList(codes[0], codes[1]));
	// for (GenericCodesAndReactionsCollection codesAndReactionsCollection :
	// genericCodesAndReactionsCollections) {
	// if
	// (codesAndReactionsCollection.getGenericCode().equalsIgnoreCase(genericCodeOne))
	// {
	// for (Code code : codesAndReactionsCollection.getCodes()) {
	// if (code.getGenericCode().equalsIgnoreCase(genericCodeTwo)) {
	// code.setReaction(codes[4]);
	// if (codes.length > 5)
	// code.setExplanation(codes[5]);
	// }
	// }
	// genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
	//
	// ESGenericCodesAndReactions esCodesAndReactions = new
	// ESGenericCodesAndReactions();
	// BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
	// esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
	// esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
	// }
	// if
	// (codesAndReactionsCollection.getGenericCode().equalsIgnoreCase(genericCodeTwo))
	// {
	// for (Code code : codesAndReactionsCollection.getCodes()) {
	// if (code.getGenericCode().equalsIgnoreCase(genericCodeOne)) {
	// code.setReaction(codes[4]);
	// if (codes.length > 5)
	// code.setExplanation(codes[5]);
	// }
	// }
	// genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
	//
	// ESGenericCodesAndReactions esCodesAndReactions = new
	// ESGenericCodesAndReactions();
	// BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
	// esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
	// esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
	// }
	// }
	// }
	// }
	//
	// List<GenericCodesAndReactionsCollection> codesAndReactionsCollections =
	// genericCodesAndReactionsRepository
	// .findByReaction("");
	// for (GenericCodesAndReactionsCollection codesAndReactionsCollection :
	// codesAndReactionsCollections) {
	// List<Code> codes = new ArrayList<Code>();
	// for (Code code : codesAndReactionsCollection.getCodes()) {
	// if (!DPDoctorUtils.anyStringEmpty(code.getReaction()))
	// codes.add(code);
	// }
	// if (codes == null || codes.isEmpty()) {
	// esGenericCodesAndReactionsRepository.delete(codesAndReactionsCollection.getGenericCode());
	// genericCodesAndReactionsRepository.delete(codesAndReactionsCollection);
	// } else {
	// codesAndReactionsCollection.setCodes(codes);
	// genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
	//
	// ESGenericCodesAndReactions esCodesAndReactions = new
	// ESGenericCodesAndReactions();
	// BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
	// esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
	// esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (br != null) {
	// try {
	// br.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	//
	// return true;
	// }

	@Override
	public Boolean addGenericsWithReaction() {
		String csvFile = "/home/ubuntu/genericCodesWithReaction.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] codes = line.split(cvsSplitBy);

				Code codeOfId = new Code(codes[0], "");

				ESGenericCodesAndReactions esGenericCodesAndReactionsOfZeroIndex = esGenericCodesAndReactionsRepository
						.findById(codes[0]).orElse(null);
				if (esGenericCodesAndReactionsOfZeroIndex == null) {
					esGenericCodesAndReactionsOfZeroIndex = new ESGenericCodesAndReactions();
					esGenericCodesAndReactionsOfZeroIndex.setId(codes[0]);
				}
				List<Code> codesList = esGenericCodesAndReactionsOfZeroIndex.getCodes();
				if (codesList == null)
					codesList = new ArrayList<Code>();

				for (int i = 1; i < codes.length; i++) {
					Code code = new Code(codes[i], null);
					if (!codesList.contains(code))
						codesList.add(code);

					ESGenericCodesAndReactions esGenericCodesAndReactions = esGenericCodesAndReactionsRepository
							.findById(codes[i]).orElse(null);
					if (esGenericCodesAndReactions == null) {
						esGenericCodesAndReactions = new ESGenericCodesAndReactions();
						esGenericCodesAndReactions.setId(codes[i]);
						esGenericCodesAndReactions.setCodes(Arrays.asList(codeOfId));
					} else {
						List<Code> codesListOfOther = esGenericCodesAndReactions.getCodes();
						if (!codesListOfOther.contains(codeOfId))
							codesListOfOther.add(codeOfId);

						Collections.sort(codesListOfOther, new Comparator<Code>() {
							public int compare(Code one, Code other) {
								return one.getGenericCode().compareTo(other.getGenericCode());
							}
						});
						esGenericCodesAndReactions.setCodes(codesListOfOther);
					}
					esGenericCodesAndReactions = esGenericCodesAndReactionsRepository.save(esGenericCodesAndReactions);
				}

				// Collections.sort(codesList, new Comparator<Code>() {
				// public int compare(Code one, Code other) {
				// return
				// one.getGenericCode().compareTo(other.getGenericCode());
				// }
				// });
				esGenericCodesAndReactionsOfZeroIndex.setCodes(codesList);
				esGenericCodesAndReactionsOfZeroIndex = esGenericCodesAndReactionsRepository
						.save(esGenericCodesAndReactionsOfZeroIndex);
			}

			Iterable<ESGenericCodesAndReactions> esGenericCodesAndReactions = esGenericCodesAndReactionsRepository
					.findAll();
			for (ESGenericCodesAndReactions genericCodesAndReaction : esGenericCodesAndReactions) {
				GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
				codesAndReactionsCollection.setGenericCode(genericCodesAndReaction.getId());
				genericCodesAndReaction.setId(null);
				BeanUtil.map(genericCodesAndReaction, codesAndReactionsCollection);
				codesAndReactionsCollection.setCreatedTime(new Date());
				codesAndReactionsCollection.setUpdatedTime(new Date());
				genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private List<Code> getDataFromElasticSearch(String id) {
		BoolQueryBuilder booleanQueryBuilder = new BoolQueryBuilder();
		booleanQueryBuilder.must(QueryBuilders.termQuery("_id", id));
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(booleanQueryBuilder).build();
		List<ESGenericCodesAndReactions> esGenericCodesAndReactions = elasticsearchTemplate.queryForList(searchQuery,
				ESGenericCodesAndReactions.class);
		if (esGenericCodesAndReactions != null && !esGenericCodesAndReactions.isEmpty())
			return esGenericCodesAndReactions.get(0).getCodes();
		else
			return null;
	}

	protected Map<String, List<Code>> loadDataFromElasticSearch(Iterable<? extends String> keys) {
		BoolQueryBuilder booleanQueryBuilder = new BoolQueryBuilder();
		booleanQueryBuilder.must(QueryBuilders.termsQuery("_id", keys));
		SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(booleanQueryBuilder).build();
		List<ESGenericCodesAndReactions> esGenericCodesAndReactions = elasticsearchTemplate.queryForList(searchQuery,
				ESGenericCodesAndReactions.class);
		HashMap<String, List<Code>> codeWithReactions = new HashMap<String, List<Code>>();

		if (esGenericCodesAndReactions != null && !esGenericCodesAndReactions.isEmpty())
			for (int i = 0; i < esGenericCodesAndReactions.size(); i++) {
				codeWithReactions.put(esGenericCodesAndReactions.get(i).getId(),
						esGenericCodesAndReactions.get(i).getCodes());
			}
		for (String key : keys) {
			if (!codeWithReactions.containsKey(key))
				codeWithReactions.put(key, new ArrayList<Code>());
		}
		return codeWithReactions;
	}

	@Override
	public Boolean addFavouritesToDrug() {
		Boolean response = false;
		try {
			List<DoctorDrugCollection> doctorDrugCollections = doctorDrugRepository.findAll();
			for (DoctorDrugCollection doctorDrugCollection : doctorDrugCollections) {
				DrugCollection drugCollection = drugRepository.findByIdAndDoctorIdAndLocationIdAndHospitalId(
						doctorDrugCollection.getDrugId(), doctorDrugCollection.getDoctorId(),
						doctorDrugCollection.getLocationId(), doctorDrugCollection.getHospitalId());
				if (drugCollection != null) {
					drugCollection.setRankingCount(doctorDrugCollection.getRankingCount());
				} else {
					DrugCollection globalDrugCollection = drugRepository.findById(doctorDrugCollection.getDrugId())
							.orElse(null);
					if (globalDrugCollection != null) {
						drugCollection = new DrugCollection();
						BeanUtil.map(globalDrugCollection, drugCollection);
						BeanUtil.map(doctorDrugCollection, drugCollection);
						drugCollection.setId(null);
					}
				}
				if (drugCollection != null) {
					drugCollection = drugRepository.save(drugCollection);
					transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);

					ESDrugDocument esDrugDocument = new ESDrugDocument();
					BeanUtil.map(drugCollection, esDrugDocument);
					if (drugCollection.getDrugType() != null) {
						esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
						esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
					}
					esPrescriptionService.addDrug(esDrugDocument);
				}
			}
			response = true;
		} catch (Exception e) {
			throw new BusinessException(ServiceError.Unknown, "" + e);
		}
		return response;
	}

	@Override
	public List<GenericCodesAndReaction> getGenericCodeWithReactionForAdmin(long page, int size, String updatedTime,
			Boolean discarded, String searchTerm) {
		List<GenericCodesAndReaction> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria.orOperator(new Criteria("genericCode").is(searchTerm),
						new Criteria("codes.genericCode").is(searchTerm));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("generic_code_cl", "genericCode", "code", "firstGenericCode"),
						Aggregation.unwind("firstGenericCode"), Aggregation.unwind("codes"),
						Aggregation.lookup("generic_code_cl", "codes.genericCode", "code", "secondGenericCode"),
						Aggregation.unwind("secondGenericCode"),
						new ProjectionOperation(Fields.from(Fields.field("reactionType", "$codes.reaction"),
								Fields.field("firstGenericCode", "$firstGenericCode"),
								Fields.field("secondGenericCode", "$secondGenericCode"),
								Fields.field("createdTime", "$createdTime"),
								Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy"))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("generic_code_cl", "genericCode", "code", "firstGenericCode"),
						Aggregation.unwind("firstGenericCode"), Aggregation.unwind("codes"),
						Aggregation.lookup("generic_code_cl", "codes.genericCode", "code", "secondGenericCode"),
						Aggregation.unwind("secondGenericCode"),
						new ProjectionOperation(Fields.from(Fields.field("reactionType", "$codes.reaction"),
								Fields.field("firstGenericCode", "$firstGenericCode"),
								Fields.field("secondGenericCode", "$secondGenericCode"),
								Fields.field("createdTime", "$createdTime"),
								Fields.field("updatedTime", "$updatedTime"), Fields.field("createdBy", "$createdBy"))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			response = mongoTemplate
					.aggregate(aggregation, GenericCodesAndReactionsCollection.class, GenericCodesAndReaction.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Generic codes with reaction");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Boolean addGenericCodeWithReaction(GenericCodesAndReaction request) {
		Boolean response = false, isFirstUpdated = false, isSecondUpdated = false;
		try {

			Iterable<ESGenericCodesAndReactions> esGenericCodesAndReactions = esGenericCodesAndReactionsRepository
					.findAll();
			for (ESGenericCodesAndReactions genericCodesAndReaction : esGenericCodesAndReactions) {
				GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
				codesAndReactionsCollection.setGenericCode(genericCodesAndReaction.getId());
				genericCodesAndReaction.setId(null);
				BeanUtil.map(genericCodesAndReaction, codesAndReactionsCollection);
				codesAndReactionsCollection.setCreatedTime(new Date());
				codesAndReactionsCollection.setUpdatedTime(new Date());
				genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
			}

			if (request.getFirstGenericCode() != null && request.getSecondGenericCode() != null) {
				List<GenericCodesAndReactionsCollection> genericCodesAndReactionsCollections = genericCodesAndReactionsRepository
						.findByGenericCodeIn(Arrays.asList(request.getFirstGenericCode().getCode(),
								request.getSecondGenericCode().getCode()));
				if (genericCodesAndReactionsCollections != null) {
					for (GenericCodesAndReactionsCollection codesAndReactionsCollection : genericCodesAndReactionsCollections) {
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
							List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
							Collection<String> codes = CollectionUtils.collect(codesWithReaction,
									new BeanToPropertyValueTransformer("genericCode"));
							if (codes.contains(request.getSecondGenericCode().getCode())) {
								for (Code code : codesWithReaction) {
									if (code.getGenericCode()
											.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
										code.setReaction(request.getReactionType());
										code.setExplanation(request.getExplanation());
									}
								}
							} else {
								Code code = new Code(request.getSecondGenericCode().getCode(),
										request.getReactionType(), request.getExplanation());
								codesWithReaction.add(code);
							}
							codesAndReactionsCollection.setCodes(codesWithReaction);
							genericCodesAndReactionsRepository.save(codesAndReactionsCollection);

							ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
									.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
							if (esCodesAndReactions == null)
								esCodesAndReactions = new ESGenericCodesAndReactions();
							esCodesAndReactions.setCodes(null);
							BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
							esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
							esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
							isFirstUpdated = true;
						}
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
							List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
							Collection<String> codes = CollectionUtils.collect(codesWithReaction,
									new BeanToPropertyValueTransformer("genericCode"));
							if (codes.contains(request.getFirstGenericCode().getCode())) {
								for (Code code : codesWithReaction) {
									if (code.getGenericCode()
											.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
										code.setReaction(request.getReactionType());
										code.setExplanation(request.getExplanation());
									}
								}
							} else {
								Code code = new Code(request.getFirstGenericCode().getCode(), request.getReactionType(),
										request.getExplanation());
								codesWithReaction.add(code);
							}
							codesAndReactionsCollection.setCodes(codesWithReaction);
							genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
							ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
									.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
							if (esCodesAndReactions == null)
								esCodesAndReactions = new ESGenericCodesAndReactions();
							esCodesAndReactions.setCodes(null);
							BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
							esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
							esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
							isSecondUpdated = true;
						}
					}
				}
				if (!isFirstUpdated) {
					GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
					codesAndReactionsCollection.setGenericCode(request.getFirstGenericCode().getCode());
					codesAndReactionsCollection.setCreatedTime(new Date());
					codesAndReactionsCollection.setUpdatedTime(new Date());
					List<Code> codesWithReaction = new ArrayList<Code>();
					Code code = new Code(request.getSecondGenericCode().getCode(), request.getReactionType(),
							request.getExplanation());
					codesWithReaction.add(code);
					codesAndReactionsCollection.setCodes(codesWithReaction);
					genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
					ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
							.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
					if (esCodesAndReactions == null)
						esCodesAndReactions = new ESGenericCodesAndReactions();
					esCodesAndReactions.setCodes(null);
					BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
					esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
					esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
				}
				if (!isSecondUpdated) {
					GenericCodesAndReactionsCollection codesAndReactionsCollection = new GenericCodesAndReactionsCollection();
					codesAndReactionsCollection.setGenericCode(request.getSecondGenericCode().getCode());
					codesAndReactionsCollection.setCreatedTime(new Date());
					codesAndReactionsCollection.setUpdatedTime(new Date());
					List<Code> codesWithReaction = new ArrayList<Code>();
					Code code = new Code(request.getFirstGenericCode().getCode(), request.getReactionType(),
							request.getExplanation());
					codesWithReaction.add(code);
					codesAndReactionsCollection.setCodes(codesWithReaction);
					genericCodesAndReactionsRepository.save(codesAndReactionsCollection);
					ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
							.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
					if (esCodesAndReactions == null)
						esCodesAndReactions = new ESGenericCodesAndReactions();
					esCodesAndReactions.setCodes(null);
					BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
					esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
					esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
				}
				response = true;
			} else {
				logger.error("Generic code cannot be null");
				throw new BusinessException(ServiceError.Unknown, "Generic code cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Adding Generic code");
		}
		return response;
	}

	@Override
	public Boolean deleteGenericCodeWithReaction(GenericCodesAndReaction request) {
		Boolean response = false;
		try {
			if (request.getFirstGenericCode() != null && request.getSecondGenericCode() != null) {
				List<GenericCodesAndReactionsCollection> genericCodesAndReactionsCollections = genericCodesAndReactionsRepository
						.findByGenericCodeIn(Arrays.asList(request.getFirstGenericCode().getCode(),
								request.getSecondGenericCode().getCode()));
				if (genericCodesAndReactionsCollections != null) {
					for (GenericCodesAndReactionsCollection codesAndReactionsCollection : genericCodesAndReactionsCollections) {
						List<Code> codesWithReaction = codesAndReactionsCollection.getCodes();
						@SuppressWarnings("unchecked")
						Collection<String> codes = CollectionUtils.collect(codesWithReaction,
								new BeanToPropertyValueTransformer("genericCode"));

						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
							if (codes.contains(request.getSecondGenericCode().getCode())) {
								for (Code codeWithReaction : codesWithReaction)
									if (codeWithReaction.getGenericCode()
											.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
										codesWithReaction.remove(codeWithReaction);
										break;
									}
							}
						}
						if (codesAndReactionsCollection.getGenericCode()
								.equalsIgnoreCase(request.getSecondGenericCode().getCode())) {
							if (codes.contains(request.getFirstGenericCode().getCode())) {
								for (Code codeWithReaction : codesWithReaction)
									if (codeWithReaction.getGenericCode()
											.equalsIgnoreCase(request.getFirstGenericCode().getCode())) {
										codesWithReaction.remove(codeWithReaction);
										break;
									}
							}
						}
						codesAndReactionsCollection.setCodes(codesWithReaction);
						codesAndReactionsCollection.setUpdatedTime(new Date());
						genericCodesAndReactionsRepository.save(codesAndReactionsCollection);

						ESGenericCodesAndReactions esCodesAndReactions = esGenericCodesAndReactionsRepository
								.findById(codesAndReactionsCollection.getGenericCode()).orElse(null);
						if (esCodesAndReactions == null)
							esCodesAndReactions = new ESGenericCodesAndReactions();
						esCodesAndReactions.setCodes(null);
						BeanUtil.map(codesAndReactionsCollection, esCodesAndReactions);
						esCodesAndReactions.setId(codesAndReactionsCollection.getGenericCode());
						esGenericCodesAndReactionsRepository.save(esCodesAndReactions);
					}
					response = true;
				}
			} else {
				logger.error("Generic code cannot be null");
				throw new BusinessException(ServiceError.Unknown, "Generic code cannot be null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While deleting Generic code with reaction");
		}
		return response;
	}

	@Override
	public Boolean uploadGenericCodeWithReaction(FormDataBodyPart file) {
		Boolean response = false;
		try {
			String line = null;

			if (file != null) {
				InputStream fis = file.getEntityAs(InputStream.class);
				BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
				int i = 0;
				while ((line = reader.readLine()) != null) {
					if (i != 0) {
						String[] data = line.split(",");
						String firstGenericCode = data[0], firstGenericCodeName = data[1], secondGenericCode = data[2],
								secondGenericCodeName = data[3], reaction = null, explanation = null;
						if (data.length > 4)
							reaction = data[4];
						if (data.length > 5)
							explanation = data[5];

						GenericCodesAndReaction request = new GenericCodesAndReaction();
						GenericCode firstGenericCodeObj = new GenericCode();
						firstGenericCodeObj.setCode(firstGenericCode);
						firstGenericCodeObj.setName(firstGenericCodeName);
						request.setFirstGenericCode(firstGenericCodeObj);

						GenericCode secondGenericCodeObj = new GenericCode();
						secondGenericCodeObj.setCode(secondGenericCode);
						secondGenericCodeObj.setName(secondGenericCodeName);
						request.setSecondGenericCode(secondGenericCodeObj);

						request.setReactionType(reaction);
						request.setExplanation(explanation);
						addGenericCodeWithReaction(request);
						response = true;
					}
					i++;
				}
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
	public EyePrescription addEditEyePrescription(EyePrescription request, Boolean isAppointmentAdd) {

		EyePrescription response = null;
		try {
			Appointment appointment = null;
			if (isAppointmentAdd) {
				if (request.getAppointmentRequest() != null) {
					appointment = addPrescriptionAppointment(request.getAppointmentRequest());
				}
			}
			EyePrescriptionCollection eyePrescriptionCollection = new EyePrescriptionCollection();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			if (request.getAdminCreatedTime() == null) {
				request.setAdminCreatedTime(new Date());
			}
			if (request.getCreatedTime() == null) {
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, eyePrescriptionCollection);

			UserCollection userCollection = userRepository.findById(eyePrescriptionCollection.getDoctorId())
					.orElse(null);
			Date createdTime = new Date();
			eyePrescriptionCollection.setCreatedTime(createdTime);
			eyePrescriptionCollection.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
			eyePrescriptionCollection
					.setUniqueEmrId(UniqueIdInitial.PRESCRIPTION.getInitial() + DPDoctorUtils.generateRandomId());

			if (userCollection != null) {
				eyePrescriptionCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
			}
			eyePrescriptionCollection = eyePrescriptionRepository.save(eyePrescriptionCollection);

			response = new EyePrescription();
			BeanUtil.map(eyePrescriptionCollection, response);
			response.setVisitId(request.getVisitId());
			pushNotificationServices.notifyUser(eyePrescriptionCollection.getPatientId().toString(),
					"Your prescription by " + eyePrescriptionCollection.getCreatedBy() + " is here - Tap to view it!",
					ComponentType.PRESCRIPTIONS.getType(), eyePrescriptionCollection.getId().toString(), null);
			if (sendSMS && DPDoctorUtils.allStringsEmpty(request.getId())) {
				sendDownloadAppMessage(eyePrescriptionCollection.getPatientId(),
						eyePrescriptionCollection.getDoctorId(), eyePrescriptionCollection.getLocationId(),
						eyePrescriptionCollection.getHospitalId(), eyePrescriptionCollection.getCreatedBy());
				sendDownloadAppMessageInHindi(eyePrescriptionCollection.getPatientId(),
						eyePrescriptionCollection.getDoctorId(), eyePrescriptionCollection.getLocationId(),
						eyePrescriptionCollection.getHospitalId(), eyePrescriptionCollection.getCreatedBy());

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Eye Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Eye Prescription");
		}
		return response;
	}

	@Override
	@Transactional
	public EyePrescription editEyePrescription(EyePrescription request) {
		EyePrescription response = null;
		EyePrescriptionCollection eyePrescriptionCollection = eyePrescriptionRepository
				.findById(new ObjectId(request.getId())).orElse(null);
		if (eyePrescriptionCollection == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Record not found");
		}
		if (eyePrescriptionCollection.getAdminCreatedTime() != null) {
			request.setAdminCreatedTime(eyePrescriptionCollection.getAdminCreatedTime());
		}

		BeanUtil.map(request, eyePrescriptionCollection);
		eyePrescriptionCollection = eyePrescriptionRepository.save(eyePrescriptionCollection);
		if (eyePrescriptionCollection != null) {
			response = new EyePrescription();
			BeanUtil.map(eyePrescriptionCollection, response);
		}
		return response;
	}

	@Override
	@Transactional
	public EyePrescription getEyePrescription(String id) {
		EyePrescription response = null;
		EyePrescriptionCollection eyePrescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(id))
				.orElse(null);
		if (eyePrescriptionCollection == null) {
			throw new BusinessException(ServiceError.InvalidInput, "Record not found");
		}
		response = new EyePrescription();
		BeanUtil.map(eyePrescriptionCollection, response);
		return response;
	}

	@Override
	@Transactional
	public List<EyePrescription> getEyePrescriptions(long page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded, Boolean isOTPVerified) {
		List<EyePrescription> eyePrescriptions = null;
		// List<EyeObservationCollection> eyeObservationCollections = null;
		ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
		if (!DPDoctorUtils.anyStringEmpty(patientId))
			patientObjectId = new ObjectId(patientId);
		if (!DPDoctorUtils.anyStringEmpty(doctorId))
			doctorObjectId = new ObjectId(doctorId);
		if (!DPDoctorUtils.anyStringEmpty(locationId))
			locationObjectId = new ObjectId(locationId);
		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
			hospitalObjectId = new ObjectId(hospitalId);

		long createdTimestamp = Long.parseLong(updatedTime);

		Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
				.is(patientObjectId).and("isPatientDiscarded").ne(true);
		if (!discarded)
			criteria.and("discarded").is(discarded);
		// if(inHistory)criteria.and("inHistory").is(inHistory);

		if (!isOTPVerified) {
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);
		}

		Aggregation aggregation = null;

		if (size > 0)
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
					Aggregation.limit(size));
		else
			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

		AggregationResults<EyePrescription> aggregationResults = mongoTemplate.aggregate(aggregation,
				EyePrescriptionCollection.class, EyePrescription.class);
		eyePrescriptions = aggregationResults.getMappedResults();
		return eyePrescriptions;
	}

	@Override
	public int getEyePrescriptionCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int count;
		try {
			if (isOTPVerified)
				count = eyePrescriptionRepository.countByPatientId(patientObjectId);
			else
				count = eyePrescriptionRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return count;
	}

	@Override
	public String downloadEyePrescription(String prescriptionId) {
		String response = null;
		try {
			EyePrescriptionCollection prescriptionCollection = eyePrescriptionRepository
					.findById(new ObjectId(prescriptionId)).orElse(null);

			if (prescriptionCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				UserCollection user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);

				JasperReportResponse jasperReportResponse = createEyePrescriptionJasper(prescriptionCollection, patient,
						user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Lens prescription PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Lens prescription PDF");
		}
		return response;

	}

	private JasperReportResponse createEyePrescriptionJasper(EyePrescriptionCollection prescriptionCollection,
			PatientCollection patient, UserCollection user) throws NumberFormatException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;

		EyeTestJasperResponse eyResponse = new EyeTestJasperResponse();
		if (prescriptionCollection.getLeftEyeTest() != null) {

			BeanUtil.map(prescriptionCollection.getLeftEyeTest(), eyResponse);
			if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getLeftEyeTest().getDistanceSPH())) {
				if (prescriptionCollection.getLeftEyeTest().getDistanceSPH().equalsIgnoreCase("plain")
						|| prescriptionCollection.getLeftEyeTest().getDistanceSPH().equalsIgnoreCase(" plain"))
					eyResponse.setDistanceSPH(String.format("%.2f",
							Double.parseDouble(prescriptionCollection.getLeftEyeTest().getDistanceSPH())));
			}
			if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getLeftEyeTest().getNearSPH())) {
				if (prescriptionCollection.getLeftEyeTest().getNearSPH().equalsIgnoreCase("plain")
						|| prescriptionCollection.getLeftEyeTest().getNearSPH().equalsIgnoreCase(" plain"))
					eyResponse.setNearSPH(String.format("%.2f",
							Double.parseDouble(prescriptionCollection.getLeftEyeTest().getNearSPH())));
			}
			if (prescriptionCollection.getLeftEyeTest().getDistanceCylinder() != null) {
				eyResponse.setDistanceCylinder(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getDistanceCylinder())));
			}
			if (prescriptionCollection.getLeftEyeTest().getDistanceBaseCurve() != null) {
				eyResponse.setDistanceBaseCurve(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getDistanceBaseCurve())));
			}
			if (prescriptionCollection.getLeftEyeTest().getDistanceDiameter() != null) {
				eyResponse.setDistanceDiameter(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getDistanceDiameter())));
			}
			if (prescriptionCollection.getLeftEyeTest().getNearCylinder() != null) {
				eyResponse.setNearCylinder(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getNearCylinder())));
			}
			if (prescriptionCollection.getLeftEyeTest().getNearBaseCurve() != null) {
				eyResponse.setNearBaseCurve(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getNearBaseCurve())));
			}
			if (prescriptionCollection.getLeftEyeTest().getDistanceBaseCurve() != null) {
				eyResponse.setNearDiameter(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getLeftEyeTest().getNearDiameter())));
			}
		}
		parameters.put("leftEyeTest", eyResponse);

		eyResponse = new EyeTestJasperResponse();
		if (prescriptionCollection.getRightEyeTest() != null) {
			BeanUtil.map(prescriptionCollection.getRightEyeTest(), eyResponse);
			if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getRightEyeTest().getDistanceSPH())) {
				if (prescriptionCollection.getRightEyeTest().getDistanceSPH().equalsIgnoreCase("plain")
						|| prescriptionCollection.getRightEyeTest().getDistanceSPH().equalsIgnoreCase(" plain"))
					eyResponse.setDistanceSPH(String.format("%.2f",
							Double.parseDouble(prescriptionCollection.getRightEyeTest().getDistanceSPH())));
			}
			if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getRightEyeTest().getNearSPH())) {
				if (prescriptionCollection.getRightEyeTest().getNearSPH().equalsIgnoreCase("plain")
						|| prescriptionCollection.getRightEyeTest().getNearSPH().equalsIgnoreCase(" plain"))
					eyResponse.setNearSPH(String.format("%.2f",
							Double.parseDouble(prescriptionCollection.getRightEyeTest().getNearSPH())));
			}
			if (prescriptionCollection.getRightEyeTest().getDistanceCylinder() != null) {
				eyResponse.setDistanceCylinder(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getDistanceCylinder())));
			}
			if (prescriptionCollection.getRightEyeTest().getDistanceBaseCurve() != null) {
				eyResponse.setDistanceBaseCurve(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getDistanceBaseCurve())));
			}
			if (prescriptionCollection.getRightEyeTest().getDistanceDiameter() != null) {
				eyResponse.setDistanceDiameter(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getDistanceDiameter())));
			}
			if (prescriptionCollection.getRightEyeTest().getNearCylinder() != null) {
				eyResponse.setNearCylinder(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getNearCylinder())));
			}
			if (prescriptionCollection.getRightEyeTest().getNearBaseCurve() != null) {
				eyResponse.setNearBaseCurve(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getNearBaseCurve())));
			}
			if (prescriptionCollection.getRightEyeTest().getDistanceBaseCurve() != null) {
				eyResponse.setNearDiameter(String.format("%.2f",
						Double.parseDouble(prescriptionCollection.getRightEyeTest().getNearDiameter())));
			}
		}
		parameters.put("rightEyeTest", eyResponse);

		if (!DPDoctorUtils.anyStringEmpty(prescriptionCollection.getType())
				&& prescriptionCollection.getType().equalsIgnoreCase("CONTACT LENS"))
			if ((prescriptionCollection.getLeftEyeTest() != null
					&& DPDoctorUtils.allStringsEmpty(prescriptionCollection.getLeftEyeTest().getDistanceVA(),
							prescriptionCollection.getLeftEyeTest().getNearVA())
					|| (prescriptionCollection.getRightEyeTest() != null
							&& DPDoctorUtils.allStringsEmpty(prescriptionCollection.getRightEyeTest().getDistanceVA(),
									prescriptionCollection.getRightEyeTest().getNearVA()))))
				parameters.put("noOfFields", 5);
			else
				parameters.put("noOfFields", 6);
		else {
			if ((prescriptionCollection.getLeftEyeTest() != null
					&& DPDoctorUtils.allStringsEmpty(prescriptionCollection.getLeftEyeTest().getDistanceVA(),
							prescriptionCollection.getLeftEyeTest().getNearVA())
					|| (prescriptionCollection.getRightEyeTest() != null
							&& DPDoctorUtils.allStringsEmpty(prescriptionCollection.getRightEyeTest().getDistanceVA(),
									prescriptionCollection.getRightEyeTest().getNearVA()))))
				parameters.put("noOfFields", 3);
			else
				parameters.put("noOfFields", 4);
		}
		parameters.put("quality", prescriptionCollection.getQuality());
		parameters.put("type", prescriptionCollection.getType());
		parameters.put("pupilaryDistance",
				prescriptionCollection.getPupilaryDistance() != null
						? prescriptionCollection.getPupilaryDistance() + " mm"
						: null);
		parameters.put("lensType", prescriptionCollection.getLensType());
		parameters.put("usage", prescriptionCollection.getUsage());
		parameters.put("remarks", prescriptionCollection.getRemarks());
		parameters.put("replacementInterval", prescriptionCollection.getReplacementInterval());
		parameters.put("lensColor", prescriptionCollection.getLensColor());
		parameters.put("lensBrand", prescriptionCollection.getLensBrand());
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
						prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId(), ComponentType.ALL.getType(),
						PrintSettingType.EMR.getType());
		if (printSettings == null){
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
							prescriptionCollection.getHospitalId(), ComponentType.ALL.getType(),
							PrintSettingType.DEFAULT.getType(),new Sort(Sort.Direction.DESC, "updatedTime"));
			if(!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient,
				"<b>RxID: </b>"
						+ (prescriptionCollection.getUniqueEmrId() != null ? prescriptionCollection.getUniqueEmrId()
								: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				prescriptionCollection.getUpdatedTime(), printSettings.getHospitalUId(),
				printSettings.getIsPidHasDate());

		patientVisitService.generatePrintSetup(parameters, printSettings, prescriptionCollection.getDoctorId());

		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "PRESCRIPTION-"
				+ prescriptionCollection.getUniqueEmrId() + new Date().getTime();
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getTopMargin() != null
						? printSettings.getPageSetup().getTopMargin()
						: 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getBottomMargin() != null
						? printSettings.getPageSetup().getBottomMargin()
						: 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;

		response = jasperReportService.createPDF(ComponentType.EYE_PRESCRIPTION, parameters,
				"mongo-optho-prescription.jrxml", layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""),
				prescriptionSubReportA4FileName);
		return response;
	}

	@Override
	public void emailEyePrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		EyePrescriptionCollection prescriptionCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			prescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().toString().equals(doctorId)
							&& prescriptionCollection.getHospitalId().toString().equals(hospitalId)
							&& prescriptionCollection.getLocationId().toString().equals(locationId)) {

						user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);
						patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
								prescriptionCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(prescriptionCollection.getDoctorId());
						emailTrackCollection.setHospitalId(prescriptionCollection.getHospitalId());
						emailTrackCollection.setLocationId(prescriptionCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
						emailTrackCollection.setSubject("Prescription");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createEyePrescriptionJasper(prescriptionCollection,
								patient, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findById(new ObjectId(doctorId)).orElse(null);
						LocationCollection locationCollection = locationRepository.findById(new ObjectId(locationId))
								.orElse(null);

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse.setMailRecordCreatedDate(sdf.format(prescriptionCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Prescription Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Prescription Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Prescription", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Prescription", body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updateEyePrescriptionVisitId(String eyePrescriptionId, String visitId) {
		EyePrescriptionCollection eyePrescriptionCollection = eyePrescriptionRepository
				.findById(new ObjectId(eyePrescriptionId)).orElse(null);
		eyePrescriptionCollection.setVisitId(new ObjectId(visitId));
		eyePrescriptionCollection = eyePrescriptionRepository.save(eyePrescriptionCollection);
	}

	@Override
	@Transactional
	public EyePrescription deleteEyePrescription(String prescriptionId, String doctorId, String hospitalId,
			String locationId, String patientId, Boolean discarded) {
		EyePrescription response = null;
		EyePrescriptionCollection eyePrescriptionCollection = null;
		LocationCollection locationCollection = null;
		try {
			locationCollection = locationRepository.findById(new ObjectId(locationId)).orElse(null);
			eyePrescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (eyePrescriptionCollection != null) {
				if (eyePrescriptionCollection.getDoctorId() != null && eyePrescriptionCollection.getHospitalId() != null
						&& eyePrescriptionCollection.getLocationId() != null
						&& eyePrescriptionCollection.getPatientId() != null) {
					if (eyePrescriptionCollection.getDoctorId().toString().equals(doctorId)
							&& eyePrescriptionCollection.getHospitalId().toString().equals(hospitalId)
							&& eyePrescriptionCollection.getLocationId().toString().equals(locationId)
							&& eyePrescriptionCollection.getPatientId().toString().equals(patientId)) {
						eyePrescriptionCollection.setDiscarded(discarded);
						eyePrescriptionCollection.setUpdatedTime(new Date());
						eyePrescriptionCollection = eyePrescriptionRepository.save(eyePrescriptionCollection);
						response = new EyePrescription();

						BeanUtil.map(eyePrescriptionCollection, response);

						pushNotificationServices.notifyUser(patientId,
								"Please discontinue " + eyePrescriptionCollection.getUniqueEmrId() + " prescribed by "
										+ eyePrescriptionCollection.getCreatedBy()
										+ ", for further details please contact "
										+ locationCollection.getLocationName(),
								ComponentType.PRESCRIPTIONS.getType(), eyePrescriptionCollection.getId().toString(),
								null);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Location Id, Or Patient Id");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete EYE Prescription");
				}
			} else {
				logger.warn("Prescription Not Found");
				throw new BusinessException(ServiceError.NotFound, "Eye Prescription Not Found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error Occurred While Deleting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Eye Prescription");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean smsEyePrescription(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String mobileNumber, String type) {
		Boolean response = false;
		EyePrescriptionCollection eyePrescriptionCollection = null;
		try {
			eyePrescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (eyePrescriptionCollection != null) {
				if (eyePrescriptionCollection.getDoctorId() != null && eyePrescriptionCollection.getHospitalId() != null
						&& eyePrescriptionCollection.getLocationId() != null) {
					if (eyePrescriptionCollection.getDoctorId().toString().equals(doctorId)
							&& eyePrescriptionCollection.getHospitalId().toString().equals(hospitalId)
							&& eyePrescriptionCollection.getLocationId().toString().equals(locationId)) {

						UserCollection userCollection = userRepository
								.findById(eyePrescriptionCollection.getPatientId()).orElse(null);
						PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
								eyePrescriptionCollection.getPatientId(), eyePrescriptionCollection.getLocationId(),
								eyePrescriptionCollection.getHospitalId());
						if (patientCollection != null) {

							SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

							String patientName = patientCollection.getLocalPatientName() != null
									? patientCollection.getLocalPatientName().split(" ")[0]
									: "", doctorName = "", clinicContactNum = "";

							UserCollection doctor = userRepository.findById(new ObjectId(doctorId)).orElse(null);
							if (doctor != null)
								doctorName = doctor.getTitle() + " " + doctor.getFirstName();

							LocationCollection locationCollection = locationRepository
									.findById(new ObjectId(locationId)).orElse(null);
							if (locationCollection != null && locationCollection.getClinicNumber() != null)
								clinicContactNum = " " + locationCollection.getClinicNumber();

							smsTrackDetail.setDoctorId(new ObjectId(doctorId));
							smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
							smsTrackDetail.setLocationId(new ObjectId(locationId));
							smsTrackDetail.setType(type);
							SMSDetail smsDetail = new SMSDetail();
							smsDetail.setUserId(eyePrescriptionCollection.getPatientId());
							if (userCollection != null)
								smsDetail.setUserName(patientCollection.getLocalPatientName());
							SMS sms = new SMS();
							sms.setSmsText("Hi " + patientName + ", your eyes prescription "
									+ eyePrescriptionCollection.getUniqueEmrId() + " by " + doctorName + ". "
									+ "For queries,contact Doctor" + clinicContactNum + ".");

							SMSAddress smsAddress = new SMSAddress();
							smsAddress.setRecipient(mobileNumber);
							sms.setSmsAddress(smsAddress);

							smsDetail.setSms(sms);
							smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
							List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
							smsDetails.add(smsDetail);
							smsTrackDetail.setSmsDetails(smsDetails);
							response = sMSServices.sendSMS(smsTrackDetail, true);

						}
					} else {
						logger.warn("Prescription not found.Please check prescriptionId.");
						throw new BusinessException(ServiceError.NoRecord,
								"Prescription not found.Please check prescriptionId.");
					}
				}
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
	public List<Drug> getAllCustomDrug() {
		List<Drug> response = null;
		Aggregation aggregation = null;
		Criteria criteria = new Criteria();
		criteria.and("doctorId").exists(true);
		criteria.and("hospitalId").exists(true);
		criteria.and("locationId").exists(true);
		aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		AggregationResults<Drug> aggregationResults = mongoTemplate.aggregate(aggregation, DrugCollection.class,
				Drug.class);
		response = aggregationResults.getMappedResults();
		return response;
	}

	@Override
	@Transactional
	public Instructions addEditInstructions(Instructions instruction) {
		try {
			InstructionsCollection instructionsCollection = new InstructionsCollection();
			BeanUtil.map(instruction, instructionsCollection);
			if (DPDoctorUtils.anyStringEmpty(instructionsCollection.getId())) {
				instructionsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(instructionsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(instructionsCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						instructionsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					instructionsCollection.setCreatedBy("ADMIN");
				}
			} else {
				InstructionsCollection oldInstructionsCollection = instructionsRepository
						.findById(instructionsCollection.getId()).orElse(null);
				instructionsCollection.setCreatedBy(oldInstructionsCollection.getCreatedBy());
				instructionsCollection.setCreatedTime(oldInstructionsCollection.getCreatedTime());
				instructionsCollection.setDiscarded(oldInstructionsCollection.getDiscarded());
			}
			instructionsCollection = instructionsRepository.save(instructionsCollection);

			BeanUtil.map(instructionsCollection, instruction);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing instruction",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return instruction;
	}

	@Override
	@Transactional
	public Response<Instructions> getInstructions(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		Response<Instructions> response = new Response<Instructions>();
		try {

			long createdTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			Integer count = (int) mongoTemplate.count(new Query(criteria), InstructionsCollection.class);
			if (count > 0) {
				AggregationResults<Instructions> results = mongoTemplate
						.aggregate(
								DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
										updatedTime, discarded, null, null, null),
								InstructionsCollection.class, Instructions.class);
				response.setDataList(results.getMappedResults());
				response.setCount(count);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Instructions");
		}
		return response;
	}

	@Override
	@Transactional
	public Instructions deleteInstructions(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Instructions response = null;
		try {
			InstructionsCollection instructionsCollection = instructionsRepository.findById(new ObjectId(id))
					.orElse(null);
			if (instructionsCollection != null) {
				if (DPDoctorUtils.anyStringEmpty(instructionsCollection.getDoctorId(),
						instructionsCollection.getHospitalId(), instructionsCollection.getLocationId())) {
					if (instructionsCollection.getDoctorId().toString().equals(doctorId)
							&& instructionsCollection.getHospitalId().toString().equals(hospitalId)
							&& instructionsCollection.getLocationId().toString().equals(locationId)) {
						instructionsCollection.setDiscarded(discarded);
						instructionsCollection.setUpdatedTime(new Date());
						instructionsCollection = instructionsRepository.save(instructionsCollection);
						response = new Instructions();
						BeanUtil.map(instructionsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					instructionsCollection.setDiscarded(discarded);
					instructionsCollection.setUpdatedTime(new Date());
					instructionsRepository.save(instructionsCollection);
					response = new Instructions();
					BeanUtil.map(instructionsCollection, response);
				}
			} else {
				logger.warn("Instructions not found!");
				throw new BusinessException(ServiceError.NoRecord, "Instructions not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting instruction",
						e.getMessage());
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	// @Scheduled(cron = "0 0 2 * * *", zone = "IST")
	// @Scheduled(fixedDelay = 1800000)
	@Transactional
	public void updateDrugAndAddOPDForYesterdaysAddedRX() {
		try {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			localCalendar.setTime(new Date());

			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			DateTime start = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			start.minusDays(1);

			DateTime end = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
			end.minusDays(1);

			List<PrescriptionCollection> prescriptionCollections = prescriptionRepository
					.findByCreatedTimeBetween(start, end);
			if (prescriptionCollections != null) {
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {

					OPDReports opdReports = new OPDReports(String.valueOf(prescriptionCollection.getPatientId()),
							String.valueOf(prescriptionCollection.getId()),
							String.valueOf(prescriptionCollection.getDoctorId()),
							String.valueOf(prescriptionCollection.getLocationId()),
							String.valueOf(prescriptionCollection.getHospitalId()),
							prescriptionCollection.getCreatedTime());

					opdReports = reportsService.submitOPDReport(opdReports);

					if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty()) {
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							DrugCollection drugCollection = drugRepository
									.findByIdAndCreatedTime(prescriptionItem.getDrugId(), start);
							if (drugCollection != null) {
								drugCollection.setLocationId(prescriptionCollection.getLocationId());
								drugCollection.setHospitalId(prescriptionCollection.getHospitalId());
								drugCollection.setRankingCount(drugCollection.getRankingCount() + 1);
								drugCollection.setUpdatedTime(new Date());
								drugCollection.setDuration(prescriptionItem.getDuration());
								drugCollection.setDosage(prescriptionItem.getDosage());
								drugCollection.setDosageTime(prescriptionItem.getDosageTime());
								drugCollection.setDirection(prescriptionItem.getDirection());

								drugCollection = drugRepository.save(drugCollection);
								transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
								ESDrugDocument esDrugDocument = new ESDrugDocument();
								BeanUtil.map(drugCollection, esDrugDocument);
								if (drugCollection.getDrugType() != null) {
									esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
									esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
								}
								esPrescriptionService.addDrug(esDrugDocument);
							}

						}

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
	}

	@Override
	public Boolean updateGenericCodes() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		int lineCount = 0;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter("/home/ubuntu/editDrugIsPrescribed");
			fileWriter.append("action,genericCode,Drug,isPrescribed");
			fileWriter.append("\n");

			br = new BufferedReader(new FileReader(UPDATE_GENERIC_CODES_DATA_FILE));
			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
					if (fields.length > 3 && !DPDoctorUtils.anyStringEmpty(fields[3])) {
						String reason = fields[3];
						if (reason.equalsIgnoreCase("SPELLING MISTAKE")) {
							updateSpellingOfGenericCodes(fields[0], fields[2]);
						} else if (reason.equalsIgnoreCase("REPEAT")) {
							removeRepeatedGenericCodes(fields[0], fields[2], fileWriter);
						} else if (reason.equalsIgnoreCase("REMOVE")) {
							removeGenericCodes(fields[0], fileWriter);
						}
					}
				}
				lineCount = lineCount + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new BusinessException(ServiceError.Unknown, e.getMessage());
				}
			}
		}
		return response;
	}

	private void removeGenericCodes(String code, FileWriter fileWriter) {
		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByCode(code);
		if (genericCodeCollection != null) {
			List<DrugCollection> drugCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("genericNames.id").is(genericCodeCollection.getId()))),
					DrugCollection.class, DrugCollection.class).getMappedResults();

			if (drugCollections != null) {
				for (DrugCollection drugCollection : drugCollections) {
					long rxCount = mongoTemplate.count(
							new Query(new Criteria("items.drugId").is(drugCollection.getId())),
							PrescriptionCollection.class);
					if (rxCount > 0) {
						try {
							fileWriter.append("REMOVE," + code + "," + drugCollection.getId().toString() + "," + true);
							fileWriter.append("\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
//					for(GenericCode genericCode : drugCollection.getGenericNames()) {
//						if(genericCode.getId().equalsIgnoreCase(genericCodeCollection.getId().toString())) {
//							
//							drugCollection.getGenericNames().remove(genericCode);
//							drugCollection = drugRepository.save(drugCollection);
//							
//							transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
//							if (drugCollection != null) {
//								ESDrugDocument esDrugDocument = new ESDrugDocument();
//								BeanUtil.map(drugCollection, esDrugDocument);
//								if (drugCollection.getDrugType() != null) {
//									esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
//									esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
//								}
//								esPrescriptionService.addDrug(esDrugDocument);
//							}
//							break;
//						}
//					}
				}

				// remove generic code and reaction
//				ESGenericCodesAndReactions codesAndReactions = esGenericCodesAndReactionsRepository.findById(code);
//				if(codesAndReactions != null) {
//						esGenericCodesAndReactionsRepository.delete(codesAndReactions);
//				}
//				
//				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.nestedQuery("codes",
//						boolQuery().must(QueryBuilders.matchQuery("codes.genericCode", code))));
//				SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
//				List<ESGenericCodesAndReactions> codesAndReactions2 = elasticsearchTemplate.queryForList(searchQuery, ESGenericCodesAndReactions.class);
//				if(codesAndReactions2 != null) {
//					for(ESGenericCodesAndReactions reactions : codesAndReactions2) {
//						for(Code codeFromCodesAndReaction : reactions.getCodes()) {
//							if(codeFromCodesAndReaction.getGenericCode().equalsIgnoreCase(code)) {
//								reactions.getCodes().remove(codeFromCodesAndReaction);
//								esGenericCodesAndReactionsRepository.save(reactions);
//								break;
//							}
//						}
//					}
//				}
			}
		}
	}

	private void removeRepeatedGenericCodes(String code, String similarToCode, FileWriter fileWriter) {
		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByCode(code);
		if (genericCodeCollection != null) {
			GenericCodeCollection similarGenericCodeCollection = genericCodeRepository.findByCode(similarToCode);

			// update drugs
			List<DrugCollection> drugCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("genericNames.id").is(genericCodeCollection.getId()))),
					DrugCollection.class, DrugCollection.class).getMappedResults();

			if (drugCollections != null) {
				for (DrugCollection drugCollection : drugCollections) {
					long rxCount = mongoTemplate.count(
							new Query(new Criteria("items.drugId").is(drugCollection.getId())),
							PrescriptionCollection.class);
					if (rxCount > 0) {
						try {
							fileWriter.append("REPEAT," + code + "," + drugCollection.getId().toString() + "," + true);
							fileWriter.append("\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
//					for(GenericCode genericCode : drugCollection.getGenericNames()) {
//						if(genericCode.getId().equalsIgnoreCase(genericCodeCollection.getId().toString())) {
//							genericCode.setId(similarGenericCodeCollection.getId().toString());
//							genericCode.setCode(similarGenericCodeCollection.getCode());
//							genericCode.setName(similarGenericCodeCollection.getName());
//							drugCollection = drugRepository.save(drugCollection);
//							
//							transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
//							if (drugCollection != null) {
//								ESDrugDocument esDrugDocument = new ESDrugDocument();
//								BeanUtil.map(drugCollection, esDrugDocument);
//								if (drugCollection.getDrugType() != null) {
//									esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
//									esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
//								}
//								esPrescriptionService.addDrug(esDrugDocument);
//							}
//							break;
//						}
//					}
				}
			}

			// update generic code and reactions

//			ESGenericCodesAndReactions codesAndReactions = esGenericCodesAndReactionsRepository.findById(code);
//			if(codesAndReactions != null && codesAndReactions.getCodes() != null && !codesAndReactions.getCodes().isEmpty()) {
//				ESGenericCodesAndReactions similarCodesAndReactions = esGenericCodesAndReactionsRepository.findById(similarToCode);
//				if(similarCodesAndReactions != null) {
//					if(similarCodesAndReactions.getCodes() != null && !similarCodesAndReactions.getCodes().isEmpty()) {
//						Collection<String> codes = CollectionUtils.collect(similarCodesAndReactions.getCodes(), new BeanToPropertyValueTransformer("genericCode"));
//						for(Code codeFromCodesAndReaction : codesAndReactions.getCodes()) {
//							if(!codes.contains(codeFromCodesAndReaction.getGenericCode())) {
//								similarCodesAndReactions.getCodes().add(codeFromCodesAndReaction);
//							}
//						}
//						esGenericCodesAndReactionsRepository.save(similarCodesAndReactions);
//					}else {
//						similarCodesAndReactions.setCodes(codesAndReactions.getCodes());
//						esGenericCodesAndReactionsRepository.save(similarCodesAndReactions);
//					}
//				}else {
//					esGenericCodesAndReactionsRepository.delete(codesAndReactions);
//				}
//			}
//			
//			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.nestedQuery("codes",
//							boolQuery().must(QueryBuilders.matchQuery("codes.genericCode", code))));
//			SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
//			List<ESGenericCodesAndReactions> codesAndReactions2 = elasticsearchTemplate.queryForList(searchQuery, ESGenericCodesAndReactions.class);
//			if(codesAndReactions2 != null) {
//				for(ESGenericCodesAndReactions reactions : codesAndReactions2) {
//					for(Code codeFromCodesAndReaction : reactions.getCodes()) {
//						if(codeFromCodesAndReaction.getGenericCode().equalsIgnoreCase(code)) {
//							codeFromCodesAndReaction.setGenericCode(similarToCode);
//							esGenericCodesAndReactionsRepository.save(reactions);
//							break;
//						}
//					}
//				}
//			}
		}
	}

	private void updateSpellingOfGenericCodes(String code, String name) {
		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByCode(code);
		if (genericCodeCollection != null) {
			genericCodeCollection.setName(name);
			genericCodeCollection = genericCodeRepository.save(genericCodeCollection);

			List<DrugCollection> drugCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("genericNames.id").is(genericCodeCollection.getId()))),
					DrugCollection.class, DrugCollection.class).getMappedResults();
			if (drugCollections != null) {
				for (DrugCollection drugCollection : drugCollections) {
					for (GenericCode genericCode : drugCollection.getGenericNames()) {
						if (genericCode.getId().equalsIgnoreCase(genericCodeCollection.getId().toString())) {
							genericCode.setName(genericCodeCollection.getName());
							drugCollection = drugRepository.save(drugCollection);
							transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
							if (drugCollection != null) {
								ESDrugDocument esDrugDocument = new ESDrugDocument();
								BeanUtil.map(drugCollection, esDrugDocument);
								if (drugCollection.getDrugType() != null) {
									esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
									esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
								}
								esPrescriptionService.addDrug(esDrugDocument);
							}
							break;
						}
					}
				}
			}
		}
	}

	@Override
	public List<Drug> getDrugSubstitutes(String drugId) {
		List<Drug> response = null;
		try {
			DrugCollection drugCollection = drugRepository.findById(new ObjectId(drugId)).orElse(null);
			if (drugCollection != null) {
				if (drugCollection.getGenericNames() != null && !drugCollection.getGenericNames().isEmpty())
					response = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation
									.match(new Criteria("genericNames").all(drugCollection.getGenericNames()))),
							DrugCollection.class, Drug.class).getMappedResults();
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Substitutes");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Substitutes");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public Boolean smsPrescriptionforWeb(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String mobileNumber, String type) {
		Boolean response = false;
		PrescriptionCollection prescriptionCollection = null;
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {
				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				if (patientCollection != null) {
					String prescriptionDetails = "";
					int i = 0;
					if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty())
						for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
							if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
								DrugCollection drug = drugRepository.findById(prescriptionItem.getDrugId())
										.orElse(null);
								if (drug != null) {
									i++;

									String drugType = drug.getDrugType() != null
											? (!DPDoctorUtils.anyStringEmpty(drug.getDrugType().getType())
													? drug.getDrugType().getType()
													: "")
											: "";
									String drugName = !DPDoctorUtils.anyStringEmpty(drug.getDrugName())
											? drug.getDrugName()
											: "";

									String durationValue = prescriptionItem.getDuration() != null
											? (!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDuration().getValue())
													? prescriptionItem.getDuration().getValue()
													: "")
											: "";
									String durationUnit = prescriptionItem.getDuration() != null
											? (prescriptionItem.getDuration().getDurationUnit() != null
													? prescriptionItem.getDuration().getDurationUnit().getUnit()
													: "")
											: "";

									if (!DPDoctorUtils.anyStringEmpty(durationValue))
										durationValue = "," + durationValue + durationUnit;
									String dosage = !DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
											? "," + prescriptionItem.getDosage()
											: "";

									String directions = "";
									if (prescriptionItem.getDirection() != null
											&& !prescriptionItem.getDirection().isEmpty()) {
										for (DrugDirection drugDirection : prescriptionItem.getDirection()) {
											if (!DPDoctorUtils.allStringsEmpty(drugDirection.getDirection()))
												if (directions != "")
													directions = "," + drugDirection.getDirection();
												else
													directions = drugDirection.getDirection();
										}
										if (directions != "")
											directions = "," + directions;
									}
									prescriptionDetails = prescriptionDetails + " " + i + ")" + drugType + " "
											+ drugName + dosage + durationValue + directions;
								}
							}
						}
					if (prescriptionCollection.getDiagnosticTests() != null
							&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
						if (!DPDoctorUtils.anyStringEmpty(prescriptionDetails))
							prescriptionDetails = prescriptionDetails + " and ";
						prescriptionDetails = prescriptionDetails + "Tests :";
						List<ObjectId> testIds = new ArrayList<ObjectId>();
						for (TestAndRecordData testAndRecordData : prescriptionCollection.getDiagnosticTests()) {
							testIds.add(testAndRecordData.getTestId());
						}

						Collection<String> tests = CollectionUtils.collect(
								(List<DiagnosticTestCollection>) diagnosticTestRepository.findAllById(testIds),
								new BeanToPropertyValueTransformer("testName"));
						prescriptionDetails = prescriptionDetails + " "
								+ tests.toString().replaceAll("\\[", "").replaceAll("\\]", "");
					}

					if (!DPDoctorUtils.anyStringEmpty(prescriptionDetails)) {
						SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

						String patientName = patientCollection.getLocalPatientName() != null
								? patientCollection.getLocalPatientName().split(" ")[0]
								: "", doctorName = "", clinicContactNum = "";

						UserCollection doctor = userRepository.findById(prescriptionCollection.getDoctorId())
								.orElse(null);
						if (doctor != null)
							doctorName = doctor.getTitle() + " " + doctor.getFirstName();

						LocationCollection locationCollection = locationRepository
								.findById(prescriptionCollection.getLocationId()).orElse(null);
						if (locationCollection != null && locationCollection.getClinicNumber() != null)
							clinicContactNum = " " + locationCollection.getClinicNumber();

						smsTrackDetail.setDoctorId(prescriptionCollection.getDoctorId());
						smsTrackDetail.setHospitalId(prescriptionCollection.getHospitalId());
						smsTrackDetail.setLocationId(prescriptionCollection.getLocationId());
						smsTrackDetail.setType(type);
						SMSDetail smsDetail = new SMSDetail();
						smsDetail.setUserId(prescriptionCollection.getPatientId());
						if (patientCollection != null)
							smsDetail.setUserName(patientCollection.getLocalPatientName());
						SMS sms = new SMS();
						sms.setSmsText("Hi " + patientName + ", your prescription "
								+ prescriptionCollection.getUniqueEmrId() + " by " + doctorName + ". "
								+ prescriptionDetails + ". For queries,contact Doctor" + clinicContactNum + ".-Healthcoco");

						SMSAddress smsAddress = new SMSAddress();
						smsAddress.setRecipient(mobileNumber);
						sms.setSmsAddress(smsAddress);

						smsDetail.setSms(sms);
						smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
						List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
						smsDetails.add(smsDetail);
						smsTrackDetail.setSmsDetails(smsDetails);
						smsTrackDetail.setTemplateId("1307161526775042485");
						response = sMSServices.sendSMS(smsTrackDetail, true);
					}
				}
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
	public Boolean smsEyePrescriptionForWeb(String prescriptionId, String doctorId, String locationId,
			String hospitalId, String mobileNumber, String type) {
		Boolean response = false;
		EyePrescriptionCollection eyePrescriptionCollection = null;
		try {
			eyePrescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (eyePrescriptionCollection != null) {

				UserCollection userCollection = userRepository.findById(eyePrescriptionCollection.getPatientId())
						.orElse(null);
				PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						eyePrescriptionCollection.getPatientId(), eyePrescriptionCollection.getLocationId(),
						eyePrescriptionCollection.getHospitalId());
				if (patientCollection != null) {

					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					String patientName = patientCollection.getLocalPatientName() != null
							? patientCollection.getLocalPatientName().split(" ")[0]
							: "", doctorName = "", clinicContactNum = "";

					UserCollection doctor = userRepository.findById(eyePrescriptionCollection.getDoctorId())
							.orElse(null);
					if (doctor != null)
						doctorName = doctor.getTitle() + " " + doctor.getFirstName();

					LocationCollection locationCollection = locationRepository
							.findById(eyePrescriptionCollection.getLocationId()).orElse(null);
					if (locationCollection != null && locationCollection.getClinicNumber() != null)
						clinicContactNum = " " + locationCollection.getClinicNumber();

					smsTrackDetail.setDoctorId(eyePrescriptionCollection.getDoctorId());
					smsTrackDetail.setHospitalId(eyePrescriptionCollection.getHospitalId());
					smsTrackDetail.setLocationId(eyePrescriptionCollection.getLocationId());
					smsTrackDetail.setType(type);
					SMSDetail smsDetail = new SMSDetail();
					smsDetail.setUserId(eyePrescriptionCollection.getPatientId());
					if (userCollection != null)
						smsDetail.setUserName(patientCollection.getLocalPatientName());
					SMS sms = new SMS();
					sms.setSmsText("Hi " + patientName + ", your eyes prescription "
							+ eyePrescriptionCollection.getUniqueEmrId() + " by " + doctorName + ". "
							+ "For queries,contact Doctor" + clinicContactNum + ".");

					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(mobileNumber);
					sms.setSmsAddress(smsAddress);

					smsDetail.setSms(sms);
					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
					smsDetails.add(smsDetail);
					smsTrackDetail.setSmsDetails(smsDetails);
					response = sMSServices.sendSMS(smsTrackDetail, true);

				}
			} else {
				logger.error("Prescription not found");
				throw new BusinessException(ServiceError.NotFound, "Prescription not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private MailResponse createMailDataForWeb(String prescriptionId, String doctorId, String locationId,
			String hospitalId) {
		MailResponse response = null;
		PrescriptionCollection prescriptionCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {

				user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(prescriptionCollection.getDoctorId());
				emailTrackCollection.setHospitalId(prescriptionCollection.getHospitalId());
				emailTrackCollection.setLocationId(prescriptionCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
				emailTrackCollection.setSubject("Prescription");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createJasper(prescriptionCollection, patient, user, null,
						false, false, false, false, false);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(prescriptionCollection.getLocationId()).orElse(null);

				response = new MailResponse();
				response.setMailAttachment(mailAttachment);
				response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				response.setClinicAddress(address);
				response.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				response.setMailRecordCreatedDate(sdf.format(prescriptionCollection.getCreatedTime()));
				response.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public void emailEyePrescriptionForWeb(String prescriptionId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		EyePrescriptionCollection prescriptionCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			prescriptionCollection = eyePrescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			if (prescriptionCollection != null) {

				user = userRepository.findById(prescriptionCollection.getPatientId()).orElse(null);
				patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection.setDoctorId(prescriptionCollection.getDoctorId());
				emailTrackCollection.setHospitalId(prescriptionCollection.getHospitalId());
				emailTrackCollection.setLocationId(prescriptionCollection.getLocationId());
				emailTrackCollection.setType(ComponentType.PRESCRIPTIONS.getType());
				emailTrackCollection.setSubject("Prescription");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createEyePrescriptionJasper(prescriptionCollection, patient,
						user);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findById(prescriptionCollection.getDoctorId()).orElse(null);
				LocationCollection locationCollection = locationRepository
						.findById(prescriptionCollection.getLocationId()).orElse(null);

				mailResponse = new MailResponse();
				mailResponse.setMailAttachment(mailAttachment);
				mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				mailResponse.setClinicAddress(address);
				mailResponse.setClinicName(locationCollection.getLocationName());
				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				mailResponse.setMailRecordCreatedDate(sdf.format(prescriptionCollection.getCreatedTime()));
				mailResponse.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Prescription", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress,
					mailResponse.getDoctorName() + " sent you Prescription", body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	@Transactional
	public Prescription deletePrescriptionForWeb(String prescriptionId, String doctorId, String hospitalId,
			String locationId, String patientId, Boolean discarded) {
		Prescription response = null;
		PrescriptionCollection prescriptionCollection = null;
		LocationCollection locationCollection = null;
		try {

			prescriptionCollection = prescriptionRepository.findById(new ObjectId(prescriptionId)).orElse(null);
			locationCollection = locationRepository.findById(prescriptionCollection.getLocationId()).orElse(null);
			if (prescriptionCollection != null) {
				prescriptionCollection.setDiscarded(discarded);
				prescriptionCollection.setUpdatedTime(new Date());
				prescriptionCollection = prescriptionRepository.save(prescriptionCollection);
				response = new Prescription();
				List<TestAndRecordData> tests = prescriptionCollection.getDiagnosticTests();
				prescriptionCollection.setDiagnosticTests(null);
				BeanUtil.map(prescriptionCollection, response);
				if (prescriptionCollection.getItems() != null) {
					List<PrescriptionItemDetail> prescriptionItemDetailsList = new ArrayList<PrescriptionItemDetail>();
					for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
						PrescriptionItemDetail prescriptionItemDetails = new PrescriptionItemDetail();
						BeanUtil.map(prescriptionItem, prescriptionItemDetails);
						if (prescriptionItem.getDrugId() != null) {
							DrugCollection drugCollection = drugRepository.findById(prescriptionItem.getDrugId())
									.orElse(null);
							Drug drug = new Drug();
							if (drugCollection != null)
								BeanUtil.map(drugCollection, drug);
							prescriptionItemDetails.setDrug(drug);
						}
						prescriptionItemDetailsList.add(prescriptionItemDetails);
					}
					response.setItems(prescriptionItemDetailsList);
				}
				PatientVisitCollection patientVisitCollection = patientVisitRepository
						.findByPrescriptionId(prescriptionCollection.getId());
				if (patientVisitCollection != null)
					response.setVisitId(patientVisitCollection.getId().toString());

				if (tests != null && !tests.isEmpty()) {
					List<TestAndRecordDataResponse> diagnosticTests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData data : tests) {
						if (data.getTestId() != null) {
							DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
									.findById(data.getTestId()).orElse(null);
							DiagnosticTest diagnosticTest = new DiagnosticTest();
							if (diagnosticTestCollection != null) {
								BeanUtil.map(diagnosticTestCollection, diagnosticTest);
							}
							if (!DPDoctorUtils.anyStringEmpty(data.getRecordId())) {
								diagnosticTests.add(
										new TestAndRecordDataResponse(diagnosticTest, data.getRecordId().toString()));
							} else {
								diagnosticTests.add(new TestAndRecordDataResponse(diagnosticTest, null));
							}

						}
					}
					response.setDiagnosticTests(diagnosticTests);
				}

				pushNotificationServices.notifyUser(prescriptionCollection.getPatientId().toString(),
						"Please discontinue " + prescriptionCollection.getUniqueEmrId() + " prescribed by "
								+ prescriptionCollection.getCreatedBy() + ", for further details please contact "
								+ locationCollection.getLocationName(),
						ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString(), null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error Occurred While Deleting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Deleting Prescription");
		}
		return response;
	}

	@Override
	public Boolean updateDrugRankingOnBasisOfRanking() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int rankingCount = 75;
		try {
			br = new BufferedReader(new FileReader(DRUG_COMPANY_LIST));

			while ((line = br.readLine()) != null) {

				String companyName = line.split(",")[0];
				List<DrugCollection> drugCollections = mongoTemplate.aggregate(
						Aggregation
								.newAggregation(Aggregation.match(new Criteria("companyName").regex(companyName, "i"))),
						DrugCollection.class, DrugCollection.class).getMappedResults();
				if (drugCollections != null) {
					for (DrugCollection drugCollection : drugCollections) {
						drugCollection.setRankingCount(drugCollection.getRankingCount() + rankingCount);
						drugRepository.save(drugCollection);
					}
				}
				rankingCount = rankingCount--;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred updating ranking on basis of company ranking");
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred updating ranking on basis of company ranking");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadDrugs() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(UPLOAD_DRUGS));
			Map<String, DrugType> drugTypesMap = new HashMap<String, DrugType>();
			List<DrugType> drugTypes = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("doctorId").is(null))),
							DrugTypeCollection.class, DrugType.class)
					.getMappedResults();
			if (drugTypes != null && !drugTypes.isEmpty()) {
				for (DrugType drugType : drugTypes)
					drugTypesMap.put(drugType.getType(), drugType);
			}

			while ((line = br.readLine()) != null) {
				if (lineCount > 0) {
					String[] fields = line.split(",");

					String drugName = fields[0].trim(), drugType = fields[1].trim(), companyName = fields[5].trim();

					if (drugType.equalsIgnoreCase("TABLET"))
						drugType = "TAB";
					if (drugType.equalsIgnoreCase("CAPSULE"))
						drugType = "CAP";
					if (drugType.equalsIgnoreCase("OINTMENT"))
						drugType = "OINT";
					if (drugType.equalsIgnoreCase("SYRUP"))
						drugType = "SYP";

					List<DrugCollection> drugCollections = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(new Criteria("drugName").is(drugName)
									.and("drugType.type").is(drugType).and("companyName").is(companyName))),
							DrugCollection.class, DrugCollection.class).getMappedResults();

					if (drugCollections != null) {
						DrugCollection drugCollection = new DrugCollection();

						String drugCode = generateDrugCode(drugName, drugType);
						drugCollection.setDrugCode(drugCode);

						Date createdTime = new Date();
						drugCollection.setCreatedTime(createdTime);
						drugCollection.setCreatedBy("ADMIN");
						drugCollection.setRankingCount(1);

						drugCollection.setDrugName(drugName);
						drugCollection.setDrugType(drugTypesMap.get(drugType));
						if (drugCollection.getDrugType() == null) {
							DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
							drugTypeCollection.setAdminCreatedTime(new Date());
							drugTypeCollection.setCreatedBy("ADMIN");
							drugTypeCollection.setCreatedTime(new Date());
							drugTypeCollection.setUpdatedTime(new Date());
							drugTypeCollection.setType(drugType);
							drugTypeCollection = drugTypeRepository.save(drugTypeCollection);

							DrugType drugTypeObj = new DrugType();
							BeanUtil.map(drugTypeCollection, drugTypeObj);
							drugCollection.setDrugType(drugTypeObj);
							drugTypesMap.put(drugType, drugTypeObj);
						}
						drugCollection.setCompanyName(companyName);

						if (!DPDoctorUtils.anyStringEmpty(fields[2])) {
							String specialities[] = fields[2].split("\\+");
							drugCollection.setSpecialities(Arrays.asList(specialities));
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[3])) {
							drugCollection.setPackForm(fields[3]);
						}
						if (!DPDoctorUtils.anyStringEmpty(fields[4])) {
							drugCollection.setPackSize(fields[4]);
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[8])
								&& !(fields[8].equalsIgnoreCase("NOT AVAILABLE"))) {
							String genericsList[] = fields[8].split("\\+");

							Map<String, String> generics = new HashMap<String, String>();
							for (String genericName : genericsList) {
								genericName = genericName.replaceAll("\\s", "");
								String key = "", value = null;
								int indexOfStart = genericName.indexOf("("), indexOfEnd = genericName.indexOf(")");
								if (indexOfStart > -1 && indexOfEnd > -1) {
									key = genericName.substring(0, indexOfStart);
									value = genericName.substring(indexOfStart + 1, indexOfEnd);
									if (!DPDoctorUtils.anyStringEmpty(value) && value.equalsIgnoreCase("NA"))
										value = null;
								} else {
									key = genericName;
								}
								generics.put(key, value);
							}
							List<GenericCode> genericCodesFromDB = mongoTemplate.aggregate(
									Aggregation.newAggregation(
											Aggregation.match(new Criteria("name").in(generics.keySet()))),
									GenericCodeCollection.class, GenericCode.class).getMappedResults();
							for (GenericCode genericCode : genericCodesFromDB) {
								genericCode.setStrength(generics.get(genericCode.getName()));
							}

							List<GenericCode> genericCodes = new ArrayList<>();
							genericCodes.addAll(genericCodesFromDB);

							if (generics.size() != genericCodes.size()) {
								for (Entry<String, String> generic : generics.entrySet()) {
									boolean isPresent = false;
									for (GenericCode genericCode : genericCodes)
										if (generic.getKey().equalsIgnoreCase(genericCode.getName()))
											isPresent = true;

									if (!isPresent) {
										GenericCodeCollection genericCodeCollection = new GenericCodeCollection();
										genericCodeCollection.setAdminCreatedTime(new Date());
										genericCodeCollection.setName(generic.getKey());
										genericCodeCollection
												.setCode(generateGenericCode(genericCodeCollection.getName()));
										genericCodeCollection.setCreatedBy("ADMIN");
										genericCodeCollection.setCreatedTime(new Date());
										genericCodeCollection.setUpdatedTime(new Date());
										genericCodeCollection = genericCodeRepository.save(genericCodeCollection);

										GenericCode code = new GenericCode();
										BeanUtil.map(genericCodeCollection, code);
										code.setStrength(generic.getValue());
										genericCodes.add(code);
									}
								}
							}

							drugCollection.setGenericNames(genericCodes);
						}

						if (!DPDoctorUtils.anyStringEmpty(fields[7])) {
							String categories[] = fields[7].split("\\+");
							drugCollection.setCategories(Arrays.asList(categories));
						}

						if (fields.length > 9 && !DPDoctorUtils.anyStringEmpty(fields[9])) {
							drugCollection.setMRP(fields[9] + " INR");
						}

						if (fields.length > 10 && !DPDoctorUtils.anyStringEmpty(fields[10])) {
							drugCollection.setPrizePerPack(fields[10] + " INR");
						}

						if (fields.length > 11 && !DPDoctorUtils.anyStringEmpty(fields[11])) {
							drugCollection.setRxRequired(fields[11]);
						}

						if (fields.length > 12 && !DPDoctorUtils.anyStringEmpty(fields[12])) {
							drugCollection.setUnsafeWith(fields[12]);
						}
						drugCollection = drugRepository.save(drugCollection);

						transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
						if (drugCollection != null) {
							ESDrugDocument esDrugDocument = new ESDrugDocument();
							BeanUtil.map(drugCollection, esDrugDocument);
							if (drugCollection.getDrugType() != null) {
								esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
								esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
							}
							esPrescriptionService.addDrug(esDrugDocument);
						}
					} else {
						System.out.println("Already present: " + lineCount + " .. " + drugName);
					}
				}
				lineCount = lineCount + 1;
				response = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred uploading drugs");
//			throw new BusinessException(ServiceError.Unknown, "Error Occurred uploading drugs");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	private List<Drug> addStockToDrug(List<Drug> drugs) {
		for (Drug drug : drugs) {
			InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(drug.getLocationId(),
					drug.getHospitalId(), drug.getId());
			if (inventoryItem != null) {
				InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
						.getInventoryItem(inventoryItem.getId());
				drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
				drug.setRetailPrice(inventoryItemLookupResposne.getRetailPrice());
			}
		}
		return drugs;
	}

	private String generateDrugCode(String drugName, String drugType) {
		drugName = drugName.replaceAll("[^a-zA-Z0-9]", "");
		String drugCode = null;
		if (drugName.length() > 2)
			drugCode = drugType.substring(0, 2) + drugName.substring(0, 3);
		else
			drugCode = drugType.substring(0, 2) + drugName.substring(0, 2);

		List<DrugCollection> drugCollections = mongoTemplate
				.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(
										new Criteria("doctorId").is(null).and("drugCode").regex("^" + drugCode, "i")),
								Aggregation.sort(new Sort(Direction.DESC, "drugCode")), Aggregation.limit(1)),
						DrugCollection.class, DrugCollection.class)
				.getMappedResults();
		DrugCollection drugCollection = null;
		if (drugCollections != null && !drugCollections.isEmpty())
			drugCollection = drugCollections.get(0);
//		DrugCollection drugCollection = drugRepository.findByStartWithDrugCode(drugCode, null, null, null, new Sort(Sort.Direction.DESC, "drugCode"));
		if (drugName.equalsIgnoreCase("O2")) {
			drugCollection = null;
		}

		if (drugCollection != null) {
			Integer count = Integer.parseInt(drugCollection.getDrugCode().toUpperCase().replace(drugCode, "")) + 1;
			if (count < 1000) {
				drugCode = drugCode + String.format("%04d", count);
			} else {
				drugCode = drugCode + count;
			}
		} else {
			drugCode = drugCode + "0001";
		}

		return drugCode;
	}

	private String generateGenericCode(String genericName) {
		genericName = genericName.replaceAll("[^a-zA-Z0-9]", "");
		String genericCode = "GEN" + genericName.substring(0, 3);

		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByGenericCodeStartsWith(genericCode,
				new Sort(Sort.Direction.DESC, "code"));
		if (genericCodeCollection != null) {
			Integer count = Integer.parseInt(genericCodeCollection.getCode().replace(genericCode, "")) + 1;
			if (count < 1000) {
				genericCode = genericCode + String.format("%04d", count);
			} else {
				genericCode = genericCode + count;
			}
		} else {
			genericCode = genericCode + "0001";
		}

		genericCode = updateIfGenericCodeExist(genericCode);

		return genericCode;
	}

	private String updateIfGenericCodeExist(String genericCode) {
		GenericCodeCollection genericCodeCollection = genericCodeRepository.findByGenericCodeStartsWith(genericCode,
				new Sort(Sort.Direction.DESC, "createdTime"));
		if (genericCodeCollection != null) {
			genericCode = genericCode.substring(0, 6);
			Integer count = Integer.parseInt(genericCodeCollection.getCode().replace(genericCode, "")) + 1;
			if (count < 1000) {
				genericCode = genericCode + String.format("%04d", count);
			} else {
				genericCode = genericCode + count;
			}
			genericCode = updateIfGenericCodeExist(genericCode);
		}

		return genericCode;

	}

	@Override
	public Boolean updateDrugInteraction() {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		int lineCount = 0;
		try {
			br = new BufferedReader(new FileReader(UPDATE_DRUG_INTERACTION_DATA_FILE));

			while ((line = br.readLine()) != null) {
				if (lineCount > 0) {
					String[] fields = line.split(",");

				}
				lineCount = lineCount + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred uploading drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred uploading drugs");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionReferral addNutritionReferral(NutritionReferralRequest request) {
		NutritionReferral response = null;

		try {
			if (request != null) {
				NutritionReferralCollection nutritionReferralCollection = new NutritionReferralCollection();
				BeanUtil.map(request, nutritionReferralCollection);
				nutritionReferralCollection = nutritionReferralRepository.save(nutritionReferralCollection);
				if (nutritionReferralCollection != null) {
					response = new NutritionReferral();
					BeanUtil.map(nutritionReferralCollection, response);
					PatientCollection patientCollection = patientRepository.findByUserIdAndLocationIdAndHospitalId(
							nutritionReferralCollection.getPatientId(), nutritionReferralCollection.getLocationId(),
							nutritionReferralCollection.getHospitalId());
					if (patientCollection != null) {
						patientCollection.setIsNutritionActive(true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public Boolean updatePrescriptionDrugType() {
		Boolean response = false;
		try {
			List<PrescriptionCollection> prescriptionCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("items").ne(null).and("items.drugType").is(null))),
					PrescriptionCollection.class, PrescriptionCollection.class).getMappedResults();

			if (prescriptionCollections != null) {
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
					if (prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty()) {
						List<PrescriptionItem> prescriptionItems = prescriptionCollection.getItems();
						for (PrescriptionItem prescriptionItem : prescriptionItems) {
							DrugCollection drugCollection = drugRepository.findById(prescriptionItem.getDrugId())
									.orElse(null);
							if (drugCollection != null) {
								prescriptionItem.setDrugName(drugCollection.getDrugName());
								prescriptionItem.setDrugType(drugCollection.getDrugType());
							}
							prescriptionCollection.setItems(null);
							prescriptionCollection.setItems(prescriptionItems);
							prescriptionCollection = prescriptionRepository.save(prescriptionCollection);

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
	public List<Drug> getDrugs(List<ObjectId> drugIds) {
		List<Drug> response = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria("id").in(drugIds).and("discarded").is(false);

			aggregation = Aggregation.newAggregation(

					Aggregation.match(criteria), Aggregation.sort(Sort.Direction.DESC, "createdTime"));

			AggregationResults<Drug> results = mongoTemplate.aggregate(aggregation, DrugCollection.class, Drug.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return response;
	}
}