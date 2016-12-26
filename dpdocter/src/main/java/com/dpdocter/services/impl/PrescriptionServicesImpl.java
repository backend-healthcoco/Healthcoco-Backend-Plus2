package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.DoctorDrug;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDosage;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.Prescription;
import com.dpdocter.beans.PrescriptionAddItem;
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
import com.dpdocter.collections.GenericCodeCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.LabTestCollection;
import com.dpdocter.collections.LocationCollection;
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
import com.dpdocter.elasticsearch.repository.ESDoctorDrugRepository;
import com.dpdocter.elasticsearch.repository.ESGenericCodesAndReactionsRepository;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PrescriptionItems;
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
import com.dpdocter.repository.GenericCodeWithReactionRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.LabTestRepository;
import com.dpdocter.repository.LocationRepository;
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
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugInteractionResposne;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;
import com.dpdocter.response.TestAndRecordDataResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.EmailTackService;
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

import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;

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

	@Value("${send.sms}")
	private Boolean sendSMS;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private ESDoctorDrugRepository esDoctorDrugRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESGenericCodesAndReactionsRepository esGenericCodesAndReactionsRepository;

	@Autowired
	private GenericCodeWithReactionRepository genericCodeWithReactionRepository;

	LoadingCache<String, List<Code>> Cache = CacheBuilder.newBuilder().maximumSize(100) // maximum
																						// 100
																						// records
																						// can
																						// be
																						// cached
			.expireAfterAccess(30, TimeUnit.MINUTES) // cache will expire after
														// 30 minutes of access
			.build(new CacheLoader<String, List<Code>>() { // build the
															// cacheloader

				@Override
				public List<Code> load(String id) throws Exception {
					if (getDataFromElasticSearch(id) != null)
						return getDataFromElasticSearch(id);
					else
						return null;
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
				UserCollection userCollection = userRepository.findOne(drugCollection.getDoctorId());
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
							.findOne(new ObjectId(drugCollection.getDrugType().getId()));
					if (drugTypeCollection != null) {
						DrugType drugType = new DrugType();
						BeanUtil.map(drugTypeCollection, drugType);
						drugCollection.setDrugType(drugType);
					}
				}
			}
			drugCollection = drugRepository.save(drugCollection);
			DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findByDrugIdDoctorIdLocaationIdHospitalId(
					drugCollection.getId(), drugCollection.getDoctorId(), drugCollection.getLocationId(),
					drugCollection.getHospitalId());
			if (doctorDrugCollection == null) {
				doctorDrugCollection = new DoctorDrugCollection(drugCollection.getId(), drugCollection.getDoctorId(),
						drugCollection.getLocationId(), drugCollection.getHospitalId(), 1, false,
						drugCollection.getDuration(), drugCollection.getDosage(), drugCollection.getDosageTime(),
						drugCollection.getDirection(), drugCollection.getGenericNames(), drugCollection.getCreatedBy());
				doctorDrugCollection.setCreatedTime(new Date());
			}
			doctorDrugCollection.setUpdatedTime(new Date());
			doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
			transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
			if (doctorDrugCollection != null) {
				ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
				BeanUtil.map(drugCollection, esDoctorDrugDocument);
				BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
				esDoctorDrugDocument.setId(drugCollection.getId().toString());
				esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While saving drugs", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
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
			DrugCollection drugCollection = drugRepository.findOne(new ObjectId(request.getId()));
			if (drugCollection.getDoctorId() != null && drugCollection.getLocationId() != null
					&& drugCollection.getHospitalId() != null) {
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
								.findOne(new ObjectId(drugCollection.getDrugType().getId()));
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
			DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findByDrugIdDoctorIdLocaationIdHospitalId(
					drugCollection.getId(), new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()));
			if (doctorDrugCollection == null) {
				doctorDrugCollection = new DoctorDrugCollection(drugCollection.getId(),
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()), 1, false, drugCollection.getDuration(),
						drugCollection.getDosage(), drugCollection.getDosageTime(), drugCollection.getDirection(),
						drugCollection.getGenericNames(), drugCollection.getCreatedBy());
				doctorDrugCollection.setCreatedTime(new Date());
			} else {
				doctorDrugCollection.setDirection(request.getDirection());
				doctorDrugCollection.setDosage(request.getDosage());
				doctorDrugCollection.setDosageTime(request.getDosageTime());
				doctorDrugCollection.setDuration(request.getDuration());
			}
			doctorDrugCollection.setUpdatedTime(new Date());
			doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
			transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
			if (doctorDrugCollection != null) {
				ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
				BeanUtil.map(drugCollection, esDoctorDrugDocument);
				BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
				esDoctorDrugDocument.setId(drugCollection.getId().toString());
				esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Editing Drug");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While editing drugs", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
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
			drugCollection = drugRepository.findOne(new ObjectId(drugId));
			if (drugCollection != null) {
				if (drugCollection.getDoctorId() != null && drugCollection.getHospitalId() != null
						&& drugCollection.getLocationId() != null) {
					if (drugCollection.getDoctorId().equals(doctorId)
							&& drugCollection.getHospitalId().equals(hospitalId)
							&& drugCollection.getLocationId().equals(locationId)) {
						drugCollection.setDiscarded(discarded);
						drugCollection.setUpdatedTime(new Date());
						drugCollection = drugRepository.save(drugCollection);
						response = new Drug();
						BeanUtil.map(drugCollection, response);

						DoctorDrugCollection doctorDrugCollection = doctorDrugRepository
								.findByDrugIdDoctorIdLocaationIdHospitalId(drugCollection.getId(),
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
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.NotAuthorized,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Cannot Delete Global Drug");
					throw new BusinessException(ServiceError.NotAuthorized, "Cannot Delete Global Drug");
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
				// TODO Auto-generated catch block
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
			DrugCollection drugCollection = drugRepository.findOne(new ObjectId(drugId));
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
				UserCollection userCollection = userRepository.findOne(templateCollection.getDoctorId());
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
				// TODO Auto-generated catch block
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
			TemplateCollection oldTemplate = templateRepository.findOne(new ObjectId(request.getId()));
			templateCollection.setCreatedBy(oldTemplate.getCreatedBy());
			templateCollection.setCreatedTime(oldTemplate.getCreatedTime());
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
					DrugCollection drugCollection = drugRepository.findOne(new ObjectId(templateItem.getDrugId()));
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
			templateCollection = templateRepository.findOne(new ObjectId(templateId));
			if (templateCollection != null) {
				if (templateCollection.getDoctorId() != null && templateCollection.getHospitalId() != null
						&& templateCollection.getLocationId() != null) {
					if (templateCollection.getDoctorId().equals(doctorId)
							&& templateCollection.getHospitalId().equals(hospitalId)
							&& templateCollection.getLocationId().equals(locationId)) {
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
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime")));

			Aggregation aggregation = Aggregation
					.newAggregation(
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
							Aggregation.match(criteria),
							Aggregation.lookup("drug_cl", "items.drugId", "_id",
									"drug"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind",
											new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays",
													true))),
							projectList,
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("id", "$_id").append("name", new BasicDBObject("$first", "$name"))
											.append("locationId", new BasicDBObject("$first", "$locationId"))
											.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
											.append("doctorId", new BasicDBObject("$first", "$doctorId"))
											.append("discarded", new BasicDBObject("$first", "$discarded"))
											.append("items", new BasicDBObject("$addToSet", "$items"))
											.append("createdTime", new BasicDBObject("$first", "$createdTime"))
											.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
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
	public PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request, Boolean isAppointmentAdd) {
		PrescriptionAddEditResponse response = null;
		try {
			Appointment appointment = null;
			if (isAppointmentAdd) {
				if (request.getAppointmentRequest() != null) {
					appointment = addPrescriptionAppointment(request.getAppointmentRequest());
				}
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

			UserCollection userCollection = userRepository.findOne(prescriptionCollection.getDoctorId());
			Date createdTime = new Date();
			prescriptionCollection.setCreatedTime(createdTime);
			prescriptionCollection.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
			prescriptionCollection
					.setUniqueEmrId(UniqueIdInitial.PRESCRIPTION.getInitial() + DPDoctorUtils.generateRandomId());
			if (prescriptionCollection.getItems() != null) {
				List<PrescriptionItem> items = null;
				for (PrescriptionItem item : prescriptionCollection.getItems()) {
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
								item.setDuration(null);
						} else {
							item.setDuration(null);
						}
						if (items == null)
							items = new ArrayList<PrescriptionItem>();
						items.add(item);
						DoctorDrugCollection doctorDrugCollection = doctorDrugRepository
								.findByDrugIdDoctorIdLocaationIdHospitalId(item.getDrugId(),
										new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
										new ObjectId(request.getHospitalId()));
						if (doctorDrugCollection != null) {
							doctorDrugCollection.setRankingCount(doctorDrugCollection.getRankingCount() + 1);
							doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
							ESDoctorDrugDocument esDoctorDrugDocument = esDoctorDrugRepository
									.findByDrugIdDoctorIdLocaationIdHospitalId(item.getDrugId().toString(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId());
							if (esDoctorDrugDocument != null) {
								esDoctorDrugDocument.setRankingCount(doctorDrugCollection.getRankingCount());
								doctorDrugCollection.setUpdatedTime(new Date());
								esDoctorDrugRepository.save(esDoctorDrugDocument);
							}
						} else {
							DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
							doctorDrugCollection = new DoctorDrugCollection(item.getDrugId(),
									new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
									new ObjectId(request.getHospitalId()), 1, false, drugCollection.getDuration(),
									drugCollection.getDosage(), drugCollection.getDosageTime(),
									drugCollection.getDirection(), drugCollection.getGenericNames(),
									drugCollection.getCreatedBy());
							doctorDrugCollection.setCreatedTime(new Date());
							doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
							transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
							if (doctorDrugCollection != null) {
								ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
								BeanUtil.map(drugCollection, esDoctorDrugDocument);
								BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
								esDoctorDrugDocument.setId(drugCollection.getId().toString());
								esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
							}
						}
					}
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
						if (tests == null)
							tests = new ArrayList<TestAndRecordData>();
						tests.add(new TestAndRecordData(diagnosticTestCollection.getId(), null));
					}
				}
				prescriptionCollection.setDiagnosticTests(tests);
			}

			if (userCollection != null) {
				prescriptionCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
			}
			prescriptionCollection = prescriptionRepository.save(prescriptionCollection);

			if (prescriptionCollection != null) {
				OPDReports opdReports = new OPDReports(String.valueOf(prescriptionCollection.getPatientId()),
						String.valueOf(prescriptionCollection.getId()),
						String.valueOf(prescriptionCollection.getDoctorId()),
						String.valueOf(prescriptionCollection.getLocationId()),
						String.valueOf(prescriptionCollection.getHospitalId()),
						prescriptionCollection.getCreatedTime());
				opdReports = reportsService.submitOPDReport(opdReports);
			}
			response = new PrescriptionAddEditResponse();
			List<TestAndRecordData> prescriptionTest = prescriptionCollection.getDiagnosticTests();
			prescriptionCollection.setDiagnosticTests(null);
			BeanUtil.map(prescriptionCollection, response);
			if (prescriptionTest != null && !prescriptionTest.isEmpty()) {
				List<TestAndRecordDataResponse> tests = new ArrayList<TestAndRecordDataResponse>();
				for (TestAndRecordData data : prescriptionTest) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findOne(data.getTestId());
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
			response.setVisitId(request.getVisitId());
			pushNotificationServices.notifyUser(prescriptionCollection.getPatientId().toString(),
					"Your prescription by " + prescriptionCollection.getCreatedBy() + " is here - Tap to view it!",
					ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString());
			if (sendSMS && DPDoctorUtils.allStringsEmpty(request.getId()))
				sendDownloadAppMessage(prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
						prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId(),
						prescriptionCollection.getCreatedBy());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
		}
		return response;
	}

	private void sendDownloadAppMessage(ObjectId patientId, ObjectId doctorId, ObjectId locationId, ObjectId hospitalId,
			String doctorName) {
		try {
			UserCollection userCollection = userRepository.findByIdAndNotSignedUp(patientId, false);
			PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(patientId,
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

	@Override
	@Transactional
	public PrescriptionAddEditResponseDetails editPrescription(PrescriptionAddEditRequest request) {
		PrescriptionAddEditResponseDetails response = null;
		PrescriptionAddEditResponse prescription = null;
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
			UserCollection userCollection = userRepository.findOne(prescriptionCollection.getDoctorId());
			PrescriptionCollection oldPrescription = prescriptionRepository.findOne(new ObjectId(request.getId()));
			prescriptionCollection.setCreatedBy(oldPrescription.getCreatedBy());
			prescriptionCollection.setCreatedTime(oldPrescription.getCreatedTime());
			prescriptionCollection.setDiscarded(oldPrescription.getDiscarded());
			prescriptionCollection.setInHistory(oldPrescription.getInHistory());
			prescriptionCollection.setUniqueEmrId(oldPrescription.getUniqueEmrId());
			if (prescriptionCollection.getItems() != null) {
				for (PrescriptionItem item : prescriptionCollection.getItems()) {
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
								item.setDuration(null);
						} else {
							item.setDuration(null);
						}
						DoctorDrugCollection doctorDrugCollection = doctorDrugRepository
								.findByDrugIdDoctorIdLocaationIdHospitalId(item.getDrugId(),
										new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
										new ObjectId(request.getHospitalId()));
						if (doctorDrugCollection != null) {
							doctorDrugCollection.setRankingCount(doctorDrugCollection.getRankingCount() + 1);
							doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
							ESDoctorDrugDocument esDoctorDrugDocument = esDoctorDrugRepository
									.findByDrugIdDoctorIdLocaationIdHospitalId(item.getDrugId().toString(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId());
							if (esDoctorDrugDocument != null) {
								esDoctorDrugDocument.setRankingCount(doctorDrugCollection.getRankingCount());
								esDoctorDrugRepository.save(esDoctorDrugDocument);
							}
						} else {
							DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
							doctorDrugCollection = new DoctorDrugCollection(item.getDrugId(),
									new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
									new ObjectId(request.getHospitalId()), 1, false, drugCollection.getDuration(),
									drugCollection.getDosage(), drugCollection.getDosageTime(),
									drugCollection.getDirection(), drugCollection.getGenericNames(),
									drugCollection.getCreatedBy());
							doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
							transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
							if (doctorDrugCollection != null) {
								ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
								BeanUtil.map(drugCollection, esDoctorDrugDocument);
								BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
								esDoctorDrugDocument.setId(drugCollection.getId().toString());
								esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
							}
						}
					}
				}
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
				List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
				if (prescription.getItems() != null && !prescription.getItems().isEmpty()) {
					for (PrescriptionAddItem prescriptionItem : prescription.getItems()) {
						PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
						BeanUtil.map(prescriptionItem, prescriptionItemDetail);
						DrugCollection drugCollection = drugRepository
								.findOne(new ObjectId(prescriptionItem.getDrugId()));
						Drug drug = new Drug();
						if (drugCollection != null)
							BeanUtil.map(drugCollection, drug);
						prescriptionItemDetail.setDrug(drug);
						prescriptionItemDetails.add(prescriptionItemDetail);
					}
				}
				response.setItems(prescriptionItemDetails);
			}
			if (prescriptionTests != null && !prescriptionTests.isEmpty()) {
				List<TestAndRecordDataResponse> tests = new ArrayList<TestAndRecordDataResponse>();
				for (TestAndRecordData data : prescriptionTests) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findOne(data.getTestId());
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
					ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString());
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
			locationCollection = locationRepository.findOne(new ObjectId(locationId));
			prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null
						&& prescriptionCollection.getPatientId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId)
							&& prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId)
							&& prescriptionCollection.getPatientId().equals(patientId)) {
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
											.findOne(prescriptionItem.getDrugId());
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
											.findOne(data.getTestId());
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
								ComponentType.PRESCRIPTIONS.getType(), prescriptionCollection.getId().toString());
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
	public List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId,
			String locationId, String patientId, String updatedTime, boolean isOTPVerified, boolean discarded,
			boolean inHistory) {
		List<Prescription> prescriptions = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

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
					.is(patientObjectId);
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
				pushNotificationServices.notifyUser(patientId, "Global records", null, null);
			}

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
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("diagnosticTests.test", "$test"),
					Fields.field("diagnosticTests.recordId", "$diagnosticTests.recordId")));
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$test")
								.append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$visit")
												.append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$addToSet", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$test")
								.append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$visit")
												.append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$addToSet", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
					PrescriptionCollection.class, Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

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
			Criteria criteria = new Criteria().andOperator(new Criteria("visitId").is(visitId),
					new Criteria("_id").in(prescriptionIds));
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
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("diagnosticTests.test", "$test"),
					Fields.field("diagnosticTests.recordId", "$diagnosticTests.recordId")));
			Aggregation aggregation = Aggregation.newAggregation(
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
					Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$test").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(
							new BasicDBObject("$unwind",
									new BasicDBObject("path", "$visit").append("preserveNullAndEmptyArrays",
											true))),
					projectList, Aggregation.match(criteria),
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$addToSet", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
					PrescriptionCollection.class, Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
	}

	@Override
	@Transactional
	public List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId,
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
					Fields.field("updatedTime", "$updatedTime")));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(
										new BasicDBObject("$unwind",
												new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays",
														true))),
								Aggregation.match(criteria),
								Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
								projectList,
								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("id", "$_id")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("discarded",
														new BasicDBObject("$first", "$discarded"))
												.append("items",
														new BasicDBObject("$addToSet", "$items")
																.append("createdTime",
																		new BasicDBObject("$first", "$createdTime"))
																.append("updatedTime",
																		new BasicDBObject("$first", "$updatedTime"))
																.append("createdBy",
																		new BasicDBObject("$first", "$createdBy"))))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
								Aggregation.skip((page) * size), Aggregation.limit(size));
			}

			else
				aggregation = Aggregation
						.newAggregation(
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.match(criteria),
								Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
								projectList,

								new CustomAggregationOperation(new BasicDBObject("$group",
										new BasicDBObject("id", "$_id")
												.append("name", new BasicDBObject("$first", "$name"))
												.append("locationId", new BasicDBObject("$first", "$locationId"))
												.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
												.append("doctorId", new BasicDBObject("$first", "$doctorId"))
												.append("discarded", new BasicDBObject("$first", "$discarded"))
												.append("items", new BasicDBObject("$addToSet", "$items"))
												.append("createdTime", new BasicDBObject("$first", "$createdTime"))
												.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
												.append("createdBy", new BasicDBObject("$first", "$createdBy")))),
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

			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId);
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
					DrugCollection drugCollection = drugRepository.findOne(new ObjectId(templateItem.getDrugId()));
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
		PrescriptionAddEditResponse prescription = addPrescription(request, true);
		if (prescription != null) {
			response = new PrescriptionAddEditResponseDetails();
			List<TestAndRecordDataResponse> prescriptionTest = prescription.getDiagnosticTests();
			prescription.setDiagnosticTests(null);
			BeanUtil.map(prescription, response);
			response.setDiagnosticTests(prescriptionTest);
			List<PrescriptionItemDetail> prescriptionItemDetails = new ArrayList<PrescriptionItemDetail>();
			if (prescription.getItems() != null) {
				for (PrescriptionAddItem prescriptionItem : prescription.getItems()) {
					PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
					BeanUtil.map(prescriptionItem, prescriptionItemDetail);
					if (prescriptionItem.getDrugId() != null) {
						DrugCollection drugCollection = drugRepository
								.findOne(new ObjectId(prescriptionItem.getDrugId()));
						Drug drug = new Drug();
						if (drugCollection != null)
							BeanUtil.map(drugCollection, drug);
						prescriptionItemDetail.setDrug(drug);
						prescriptionItemDetails.add(prescriptionItemDetail);
					}
				}
			}
			response.setItems(prescriptionItemDetails);
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
				UserCollection userCollection = userRepository.findOne(drugTypeCollection.getDoctorId());
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
			DrugTypeCollection oldDrug = drugTypeRepository.findOne(new ObjectId(request.getId()));
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
			drugTypeCollection = drugTypeRepository.findOne(new ObjectId(drugTypeId));
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
				UserCollection userCollection = userRepository.findOne(drugDosageCollection.getDoctorId());
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
			DrugDosageCollection oldDrugDosage = drugDosageRepository.findOne(new ObjectId(request.getId()));
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
			drugDosageCollection = drugDosageRepository.findOne(new ObjectId(drugDosageId));
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
				UserCollection userCollection = userRepository.findOne(drugDirectionCollection.getDoctorId());
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
			DrugDirectionCollection oldDrugDirection = drugDirectionRepository.findOne(new ObjectId(request.getId()));
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
			drugDirectionCollection = drugDirectionRepository.findOne(new ObjectId(drugDirectionId));
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
				UserCollection userCollection = userRepository.findOne(drugDurationUnitCollection.getDoctorId());
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
					.findOne(new ObjectId(request.getId()));
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
			drugDurationUnitCollection = drugDurationUnitRepository.findOne(new ObjectId(drugDurationUnitId));
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
					Fields.field("advice", "$advice"), Fields.field("time", "$time"),
					Fields.field("fromDate", "$fromDate"), Fields.field("patientId", "$patientId"),
					Fields.field("isFeedbackAvailable", "$isFeedbackAvailable"),
					Fields.field("appointmentId", "$appointmentId"), Fields.field("visitId", "$visit._id"),
					Fields.field("createdTime", "$createdTime"), Fields.field("createdBy", "$createdBy"),
					Fields.field("updatedTime", "$updatedTime"), Fields.field("items.drug", "$drug"),
					Fields.field("items.duration", "$items.duration"), Fields.field("items.dosage", "$items.dosage"),
					Fields.field("items.dosageTime", "$items.dosageTime"),
					Fields.field("items.direction", "$items.direction"),
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("diagnosticTests.test", "$test"),
					Fields.field("diagnosticTests.recordId", "$diagnosticTests.recordId")));
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria("_id").is(new ObjectId(prescriptionId))),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays", true))),
					Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
					Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
					Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$test")
							.append("preserveNullAndEmptyArrays", true))),
					new CustomAggregationOperation(
							new BasicDBObject("$unwind",
									new BasicDBObject("path", "$visit")
											.append("preserveNullAndEmptyArrays",
													true))),
					projectList,
					new CustomAggregationOperation(new BasicDBObject("$group",
							new BasicDBObject("id", "$_id").append("name", new BasicDBObject("$first", "$name"))
									.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
									.append("locationId", new BasicDBObject("$first", "$locationId"))
									.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
									.append("doctorId", new BasicDBObject("$first", "$doctorId"))
									.append("discarded", new BasicDBObject("$first", "$discarded"))
									.append("items", new BasicDBObject("$addToSet", "$items"))
									.append("inHistory", new BasicDBObject("$first", "$inHistory"))
									.append("advice", new BasicDBObject("$first", "$advice"))
									.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
					PrescriptionCollection.class, Prescription.class);
			List<Prescription> prescriptions = aggregationResults.getMappedResults();
			if(prescriptions != null && !prescriptions.isEmpty()){
				prescription = prescriptions.get(0);
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
	public List<?> getPrescriptionItems(String type, String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, Boolean isAdmin,
			String disease, String searchTerm) {

		List<?> response = new ArrayList<Object>();

		switch (PrescriptionItems.valueOf(type.toUpperCase())) {

		case DRUGS: {
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				searchTerm = searchTerm.toUpperCase();
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDrugs(page, size, updatedTime, discarded);
				break;

			case CUSTOM:
				response = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case BOTH:
				response = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;

			case FAVOURITES:
				response = getFavouritesDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DRUGTYPE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDrugType(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDrugType(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDrugType(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
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
				response = getGlobalDrugDurationUnit(page, size, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalDrugDurationUnit(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case LABTEST: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				break;
			case CUSTOM:
				response = getCustomLabTests(page, size, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				break;
			default:
				break;
			}
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

	private List<Object> getCustomLabTests(int page, int size, String locationId, String hospitalId, String updatedTime,
			boolean discarded) {
		List<Object> response = null;
		List<LabTestCollection> labTestCollections = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			if (discarded)
				discards[1] = true;
			long createdTimeStamp = Long.parseLong(updatedTime);

			if (locationId == null && hospitalId == null) {
				labTestCollections = new ArrayList<LabTestCollection>();
			} else {
				if (size > 0)
					labTestCollections = labTestRepository.getCustomLabTests(new ObjectId(hospitalId),
							new ObjectId(locationId), new Date(createdTimeStamp), discards,
							new PageRequest(page, size, Direction.DESC, "updatedTime"));
				else
					labTestCollections = labTestRepository.getCustomLabTests(new ObjectId(hospitalId),
							new ObjectId(locationId), new Date(createdTimeStamp), discards,
							new Sort(Sort.Direction.DESC, "updatedTime"));
			}
			if (!labTestCollections.isEmpty()) {
				response = new ArrayList<Object>();
				for (LabTestCollection labTestCollection : labTestCollections) {
					LabTest labTest = new LabTest();
					BeanUtil.map(labTestCollection, labTest);
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findOne(labTestCollection.getTestId());
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

	private List<Drug> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded) {
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

	private List<Drug> getCustomDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
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

	private List<Drug> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
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

	@SuppressWarnings("unchecked")
	private List<Drug> getFavouritesDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<Drug> response = null;
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

			Aggregation aggregation = null;
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("drug_cl", "drugId", "_id", "drugs"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "rankingCount")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("drug_cl", "drugId", "_id", "drugs"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "rankingCount")));

			AggregationResults<DoctorDrug> results = mongoTemplate.aggregate(aggregation, DoctorDrugCollection.class,
					DoctorDrug.class);
			List<DoctorDrug> doctorDrugs = results.getMappedResults();
			CollectionUtils.collect(doctorDrugs, new BeanToPropertyValueTransformer("drugs"));
			response = (List<Drug>) CollectionUtils.collect(doctorDrugs, new BeanToPropertyValueTransformer("drug"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugType> getGlobalDrugType(int page, int size, String updatedTime, boolean discarded) {
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

	private List<DrugType> getCustomDrugType(int page, int size, String doctorId, String locationId, String hospitalId,
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

	private List<DrugType> getCustomGlobalDrugType(int page, int size, String doctorId, String locationId,
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

	private List<DrugDirection> getGlobalDrugDirection(int page, int size, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDirection> getCustomGlobalDrugDirection(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDirection> response = null;
		try {
			AggregationResults<DrugDirection> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null, null),
					DrugDirectionCollection.class, DrugDirection.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Direction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Direction");
		}
		return response;
	}

	private List<DrugDosage> getGlobalDrugDosage(int page, int size, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDosage> getCustomGlobalDrugDosage(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DrugDosage> response = null;
		try {
			AggregationResults<DrugDosage> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null, null),
					DrugDosageCollection.class, DrugDosage.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug Dosage");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug Dosage");
		}
		return response;
	}

	private List<DrugDurationUnit> getGlobalDrugDurationUnit(int page, int size, String updatedTime,
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

	private List<DrugDurationUnit> getCustomDrugDurationUnit(int page, int size, String doctorId, String locationId,
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

	private List<DrugDurationUnit> getCustomGlobalDrugDurationUnit(int page, int size, String doctorId,
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
		try {
			MailResponse mailResponse = createMailData(prescriptionId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Prescription", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Prescription", body,
					mailResponse.getMailAttachment());
			if (mailResponse.getMailAttachment() != null
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
			prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId)
							&& prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(prescriptionCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
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
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode() : "");

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
			prescriptionCollection = prescriptionRepository.findOne(new ObjectId(prescriptionId));
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDoctorId() != null && prescriptionCollection.getHospitalId() != null
						&& prescriptionCollection.getLocationId() != null) {
					if (prescriptionCollection.getDoctorId().equals(doctorId)
							&& prescriptionCollection.getHospitalId().equals(hospitalId)
							&& prescriptionCollection.getLocationId().equals(locationId)) {

						UserCollection userCollection = userRepository.findOne(prescriptionCollection.getPatientId());
						PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
								prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
								prescriptionCollection.getHospitalId());
						if (patientCollection != null) {
							String prescriptionDetails = "";
							int i = 0;
							if (prescriptionCollection.getItems() != null
									&& !prescriptionCollection.getItems().isEmpty())
								for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
									if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
										DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
										if (drug != null) {
											i++;

											String drugType = drug.getDrugType() != null
													? (!DPDoctorUtils.anyStringEmpty(drug.getDrugType().getType())
															? drug.getDrugType().getType() : "")
													: "";
											String drugName = !DPDoctorUtils.anyStringEmpty(drug.getDrugName())
													? drug.getDrugName() : "";

											String durationValue = prescriptionItem.getDuration() != null
													? (!DPDoctorUtils
															.anyStringEmpty(prescriptionItem.getDuration().getValue())
																	? prescriptionItem.getDuration().getValue() : "")
													: "";
											String durationUnit = prescriptionItem.getDuration() != null
													? (prescriptionItem.getDuration().getDurationUnit() != null
															? prescriptionItem.getDuration().getDurationUnit().getUnit()
															: "")
													: "";

											if (!DPDoctorUtils.anyStringEmpty(durationValue))
												durationValue = "," + durationValue + durationUnit;
											String dosage = !DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
													? "," + prescriptionItem.getDosage() : "";

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
								for (TestAndRecordData testAndRecordData : prescriptionCollection
										.getDiagnosticTests()) {
									testIds.add(testAndRecordData.getTestId());
								}

								Collection<String> tests = CollectionUtils.collect(
										(List<DiagnosticTestCollection>) diagnosticTestRepository.findAll(testIds),
										new BeanToPropertyValueTransformer("testName"));
								prescriptionDetails = prescriptionDetails + " "
										+ tests.toString().replaceAll("\\[", "").replaceAll("\\]", "");
							}

							if (!DPDoctorUtils.anyStringEmpty(prescriptionDetails)) {
								SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

								String patientName = patientCollection.getLocalPatientName() != null
										? patientCollection.getLocalPatientName().split(" ")[0] : "", doctorName = "",
										clinicContactNum = "";

								UserCollection doctor = userRepository.findOne(new ObjectId(doctorId));
								if (doctor != null)
									doctorName = doctor.getTitle() + " " + doctor.getFirstName();

								LocationCollection locationCollection = locationRepository
										.findOne(new ObjectId(locationId));
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
										+ ".");

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
					locationCollection = locationRepository.findOne(labTestCollection.getLocationId());
					if (locationCollection != null)
						labTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					labTestCollection.setCreatedBy("ADMIN");
				}
				DiagnosticTestCollection diagnosticTestCollection = null;
				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findOne(new ObjectId(request.getTest().getId()));
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
				LabTestCollection oldLabTest = labTestRepository.findOne(new ObjectId(request.getId()));
				labTestCollection.setCreatedBy(oldLabTest.getCreatedBy());
				labTestCollection.setCreatedTime(oldLabTest.getCreatedTime());
				labTestCollection.setDiscarded(oldLabTest.getDiscarded());
				DiagnosticTestCollection diagnosticTestCollection = null;

				if (request.getTest().getId() != null)
					diagnosticTestCollection = diagnosticTestRepository
							.findOne(new ObjectId(request.getTest().getId()));
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
			labTestCollection = labTestRepository.findOne(new ObjectId(labTestId));
			if (labTestCollection != null) {
				if (labTestCollection.getHospitalId() != null && labTestCollection.getLocationId() != null) {
					if (labTestCollection.getHospitalId().equals(hospitalId)
							&& labTestCollection.getLocationId().equals(locationId)) {
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
			labTestCollection = labTestRepository.findOne(new ObjectId(labTestId));
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
			LabTestCollection labTestCollection = labTestRepository.findOne(new ObjectId(labTestId));
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
	public List<Prescription> getPrescriptions(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		List<Prescription> prescriptions = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {

			if (discarded)
				discards[1] = true;

			long createdTimestamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria().andOperator(new Criteria("patientId").is(new ObjectId(patientId)),
					new Criteria("updatedTime").gt(new Date(createdTimestamp)));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			
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
					Fields.field("items.instructions", "$items.instructions"),
					Fields.field("diagnosticTests.test", "$test"),
					Fields.field("diagnosticTests.recordId", "$diagnosticTests.recordId")));
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$test")
								.append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$visit")
												.append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$addToSet", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$diagnosticTests").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
						Aggregation.lookup("diagnostic_test_cl", "diagnosticTests.testId", "_id", "test"),
						Aggregation.lookup("patient_visit_cl", "_id", "prescriptionId", "visit"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$drug").append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(new BasicDBObject("$unwind", new BasicDBObject("path", "$test")
								.append("preserveNullAndEmptyArrays", true))),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$visit")
												.append("preserveNullAndEmptyArrays",
														true))),
						projectList,
						new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("id", "$_id")
								.append("name", new BasicDBObject("$first", "$name"))
								.append("uniqueEmrId", new BasicDBObject("$first", "$uniqueEmrId"))
								.append("locationId", new BasicDBObject("$first", "$locationId"))
								.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
								.append("doctorId", new BasicDBObject("$first", "$doctorId"))
								.append("discarded", new BasicDBObject("$first", "$discarded"))
								.append("items", new BasicDBObject("$addToSet", "$items"))
								.append("inHistory", new BasicDBObject("$first", "$inHistory"))
								.append("advice", new BasicDBObject("$first", "$advice"))
								.append("diagnosticTests", new BasicDBObject("$addToSet", "$diagnosticTests"))
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
					PrescriptionCollection.class, Prescription.class);
			prescriptions = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Prescription");
		}
		return prescriptions;
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
							.findOne(diagnosticTestCollection.getLocationId());
					if (locationCollection != null)
						diagnosticTestCollection.setCreatedBy(locationCollection.getLocationName());
				} else {
					diagnosticTestCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosticTestCollection oldDiagnosticTestCollection = diagnosticTestRepository
						.findOne(new ObjectId(request.getId()));
				oldDiagnosticTestCollection.setCreatedBy(oldDiagnosticTestCollection.getCreatedBy());
				oldDiagnosticTestCollection.setCreatedTime(oldDiagnosticTestCollection.getCreatedTime());
				oldDiagnosticTestCollection.setDiscarded(oldDiagnosticTestCollection.getDiscarded());

				diagnosticTestCollection.setUpdatedTime(new Date());
			}
			diagnosticTestCollection = diagnosticTestRepository.save(diagnosticTestCollection);
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
					.findOne(new ObjectId(diagnosticTestId));
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
			diagnosticTestCollection = diagnosticTestRepository.findOne(new ObjectId(diagnosticTestId));
			if (diagnosticTestCollection != null) {
				if (diagnosticTestCollection.getHospitalId() != null
						&& diagnosticTestCollection.getLocationId() != null) {
					if (diagnosticTestCollection.getHospitalId().equals(hospitalId)
							&& diagnosticTestCollection.getLocationId().equals(locationId)) {
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
			diagnosticTestCollection = diagnosticTestRepository.findOne(new ObjectId(diagnosticTestId));
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

	private List<DiagnosticTest> getGlobalDiagnosticTests(int page, int size, String updatedTime, Boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, null, null),
					DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting LabTests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomDiagnosticTests(int page, int size, String locationId, String hospitalId,
			String updatedTime, boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, null, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	private List<DiagnosticTest> getCustomGlobalDiagnosticTests(int page, int size, String locationId,
			String hospitalId, String updatedTime, boolean discarded) {
		List<DiagnosticTest> response = null;
		try {
			AggregationResults<DiagnosticTest> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, null, locationId, hospitalId,
									updatedTime, discarded, null, null, null, null),
							DiagnosticTestCollection.class, DiagnosticTest.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Diagnostic Tests");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnostic Tests");
		}
		return response;
	}

	@Override
	@Transactional
	public PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId) {
		PrescriptionTestAndRecord response = null;
		List<TestAndRecordDataResponse> tests = null;
		try {
			PrescriptionCollection prescriptionCollection = prescriptionRepository
					.findByUniqueIdAndPatientId(uniqueEmrId, new ObjectId(patientId));
			if (prescriptionCollection != null) {
				if (prescriptionCollection.getDiagnosticTests() != null
						&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
					tests = new ArrayList<TestAndRecordDataResponse>();
					for (TestAndRecordData recordData : prescriptionCollection.getDiagnosticTests()) {
						DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
								.findOne(recordData.getTestId());
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
						response.setTests(tests);
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
					.findOne(new ObjectId(prescriptionId));

			if (prescriptionCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						prescriptionCollection.getPatientId(), prescriptionCollection.getLocationId(),
						prescriptionCollection.getHospitalId());
				UserCollection user = userRepository.findOne(prescriptionCollection.getPatientId());

				if (showPH || showPLH || showFH || showDA) {
					historyCollection = historyRepository.findHistory(prescriptionCollection.getLocationId(),
							prescriptionCollection.getHospitalId(), prescriptionCollection.getPatientId());
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
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(PrescriptionCollection prescriptionCollection, PatientCollection patient,
			UserCollection user, HistoryCollection historyCollection, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Boolean isLabPrint) throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
		JasperReportResponse response = null;
		int no = 0;
		Boolean showIntructions = false, showDirection = false;
		if (!isLabPrint && prescriptionCollection.getItems() != null && !prescriptionCollection.getItems().isEmpty())
			for (PrescriptionItem prescriptionItem : prescriptionCollection.getItems()) {
				if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
					DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
					if (drug != null) {
						String drugType = drug.getDrugType() != null
								? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "") : "";
						String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
						drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName;
						String durationValue = prescriptionItem.getDuration() != null
								? (prescriptionItem.getDuration().getValue() != null
										? prescriptionItem.getDuration().getValue() : "")
								: "";
						String durationUnit = prescriptionItem.getDuration() != null ? (prescriptionItem.getDuration()
								.getDurationUnit() != null
										? (!DPDoctorUtils
												.anyStringEmpty(
														prescriptionItem.getDuration().getDurationUnit().getUnit())
																? prescriptionItem.getDuration().getDurationUnit()
																		.getUnit()
																: "")
										: "")
								: "";

						String directions = "";
						if (prescriptionItem.getDirection() != null && !prescriptionItem.getDirection().isEmpty()) {
							showDirection = true;
							if (prescriptionItem.getDirection().get(0).getDirection() != null) {
								if (directions == "")
									directions = directions + (prescriptionItem.getDirection().get(0).getDirection());
								else
									directions = directions + ","
											+ (prescriptionItem.getDirection().get(0).getDirection());
							}
						}
						if (!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())) {
							showIntructions = true;
						}
						String duration = "";
						if (durationValue == "" && durationValue == "")
							duration = "--";
						else
							duration = durationValue + " " + durationUnit;
						no = no + 1;
						PrescriptionJasperDetails prescriptionJasperDetails = new PrescriptionJasperDetails(no,
								drugName,
								!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
										? prescriptionItem.getDosage() : "--",
								duration, directions.isEmpty() ? "--" : directions,
								!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
										? prescriptionItem.getInstructions() : "--");
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
			String dateTime = _12HourSDF.format(_24HourDt) + ", " + sdf.format(prescriptionCollection.getFromDate());
			parameters.put("followUpAppointment", "Next Review on " + dateTime);
		}
		parameters.put("prescriptionItems", prescriptionItems);
		parameters.put("showIntructions", showIntructions);
		parameters.put("showDirection", showDirection);

		parameters.put("prescriptionId", prescriptionCollection.getId().toString());
		parameters.put("advice",
				prescriptionCollection.getAdvice() != null ? prescriptionCollection.getAdvice() : null);
		String labTest = "";
		if (prescriptionCollection.getDiagnosticTests() != null
				&& !prescriptionCollection.getDiagnosticTests().isEmpty()) {
			for (TestAndRecordData tests : prescriptionCollection.getDiagnosticTests()) {
				if (tests.getTestId() != null) {
					DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository
							.findOne(tests.getTestId());
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

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				prescriptionCollection.getDoctorId(), prescriptionCollection.getLocationId(),
				prescriptionCollection.getHospitalId(), ComponentType.ALL.getType());
		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace() : LineSpace.SMALL.name());

		if (historyCollection != null) {
			parameters.put("showHistory", true);
			patientVisitService.includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
		}
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>RxID: </b>" + (prescriptionCollection.getUniqueEmrId() != null
						? prescriptionCollection.getUniqueEmrId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters);
		patientVisitService.generatePrintSetup(parameters, printSettings, prescriptionCollection.getDoctorId());
		String pdfName = (patient != null ? patient.getLocalPatientName() : "") + "PRESCRIPTION-"
				+ prescriptionCollection.getUniqueEmrId() + new Date().getTime();
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getTopMargin() != null
						? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getBottomMargin() != null
						? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
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
			DrugCollection drugCollection = drugRepository.findOne(drugObjectId);
			DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findByDrugIdDoctorIdLocaationIdHospitalId(
					drugObjectId, doctorObjectId, locationObjectId, hospitalObjectId);
			if (drugCollection != null) {
				if (doctorDrugCollection == null) {
					doctorDrugCollection = new DoctorDrugCollection(drugObjectId, doctorObjectId, locationObjectId,
							hospitalObjectId, 1, false, drugCollection.getDuration(), drugCollection.getDosage(),
							drugCollection.getDosageTime(), drugCollection.getDirection(),
							drugCollection.getGenericNames(), drugCollection.getCreatedBy());
					doctorDrugCollection.setCreatedTime(new Date());
					doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
					transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
					if (doctorDrugCollection != null) {
						ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
						BeanUtil.map(drugCollection, esDoctorDrugDocument);
						BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
						esDoctorDrugDocument.setId(drugCollection.getId().toString());
						esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
					}
				}
				response = new Drug();
				BeanUtil.map(drugCollection, response);
			} else {
				logger.error("Invalid drug Id");
				throw new BusinessException(ServiceError.Unknown, "Invalid drug Id");
			}
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
			response = appointmentService.addAppointment(appointment);
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
					UserCollection userCollection = userRepository.findOne(adviceCollection.getDoctorId());
					if (userCollection != null) {
						adviceCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					adviceCollection.setCreatedBy("ADMIN");
				}
			} else {
				AdviceCollection oldAdviceCollection = adviceRepository.findOne(adviceCollection.getId());
				adviceCollection.setCreatedBy(oldAdviceCollection.getCreatedBy());
				adviceCollection.setCreatedTime(oldAdviceCollection.getCreatedTime());
				adviceCollection.setDiscarded(oldAdviceCollection.getDiscarded());
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

	private List<Advice> getCustomGlobalAdvices(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, String disease, String searchTerm, Boolean discarded) {
		List<Advice> response = new ArrayList<Advice>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}

			AggregationResults<Advice> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, searchTerm, null, disease, "advice"),
					AdviceCollection.class, Advice.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advices");
		}
		return response;

	}

	private List<Advice> getGlobalAdvices(int page, int size, String doctorId, String updatedTime, String disease,
			String searchTerm, Boolean discarded) {
		List<Advice> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}

			AggregationResults<Advice> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, searchTerm, null, disease, "advice"), AdviceCollection.class,
					Advice.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}

	private List<Advice> getCustomAdvices(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String disease, String searchTerm, Boolean discarded) {
		List<Advice> response = null;
		try {
			AggregationResults<Advice> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, disease, null, "advice"),
							AdviceCollection.class, Advice.class);
			response = results.getMappedResults();
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
			adviceCollection = adviceRepository.findOne(new ObjectId(adviceId));
			if (adviceCollection != null) {
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

	// @Override
	// public Boolean makeCustomDrugFavourite() {
	// Boolean response = false;
	// List<DrugCollection> drugs = null;
	// try {
	// Criteria criteria = new Criteria("doctorId").ne(null);
	// Aggregation aggregation =
	// Aggregation.newAggregation(Aggregation.match(criteria));
	// AggregationResults<DrugCollection> results =
	// mongoTemplate.aggregate(aggregation, DrugCollection.class,
	// DrugCollection.class);
	// drugs = results.getMappedResults();
	// for (DrugCollection drug : drugs) {
	// DoctorDrugCollection doctorDrugCollection = doctorDrugRepository
	// .findByDrugIdDoctorIdLocaationIdHospitalId(drug.getId(),
	// drug.getDoctorId(),
	// drug.getLocationId(), drug.getHospitalId());
	// if (doctorDrugCollection == null) {
	// doctorDrugCollection = new DoctorDrugCollection(drug.getId(),
	// drug.getDoctorId(),
	// drug.getLocationId(), drug.getHospitalId(), 1, false, drug.getDuration(),
	// drug.getDosage(), drug.getDosageTime(), drug.getDirection(),
	// drug.getGenericNames(), drug.getCreatedBy());
	// doctorDrugCollection.setCreatedTime(new Date());
	// doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
	// transnationalService.addResource(doctorDrugCollection.getId(),
	// Resource.DOCTORDRUG, false);
	// if (doctorDrugCollection != null) {
	// ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
	// BeanUtil.map(drug, esDoctorDrugDocument);
	// BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
	// esDoctorDrugDocument.setId(drug.getId().toString());
	// esPrescriptionService.addDoctorDrug(esDoctorDrugDocument,
	// doctorDrugCollection.getId());
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Making Custom Drugs Favourite");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Making Custom Drugs Favourite");
	// }
	// return response;
	// }

	@Override
	public Drug addFavouriteDrug(DrugAddEditRequest request) {
		Drug response = null;
		DrugCollection drugCollection = new DrugCollection();
		try {
			if (DPDoctorUtils.allStringsEmpty(request.getId())) {
				BeanUtil.map(request, drugCollection);
				UUID drugCode = UUID.randomUUID();
				drugCollection.setDrugCode(drugCode.toString());
				if (!DPDoctorUtils.anyStringEmpty(drugCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(drugCollection.getDoctorId());
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
								.findOne(new ObjectId(drugCollection.getDrugType().getId()));
						if (drugTypeCollection != null) {
							DrugType drugType = new DrugType();
							BeanUtil.map(drugTypeCollection, drugType);
							drugCollection.setDrugType(drugType);
						}
					}
				}
				drugCollection = drugRepository.save(drugCollection);
			} else {
				drugCollection = drugRepository.findOne(new ObjectId(request.getId()));
				if (drugCollection.getDoctorId() != null && drugCollection.getLocationId() != null
						&& drugCollection.getHospitalId() != null) {
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
									.findOne(new ObjectId(drugCollection.getDrugType().getId()));
							if (drugTypeCollection != null) {
								DrugType drugType = new DrugType();
								BeanUtil.map(drugTypeCollection, drugType);
								drugCollection.setDrugType(drugType);
							}
						}
					}
					drugCollection = drugRepository.save(drugCollection);
				}
			}

			DoctorDrugCollection doctorDrugCollection = doctorDrugRepository.findByDrugIdDoctorIdLocaationIdHospitalId(
					drugCollection.getId(), new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
					new ObjectId(request.getHospitalId()));
			if (doctorDrugCollection != null) {
				doctorDrugCollection.setUpdatedTime(new Date());
				doctorDrugCollection.setDuration(request.getDuration());
				doctorDrugCollection.setDosage(request.getDosage());
				doctorDrugCollection.setDosageTime(request.getDosageTime());
				doctorDrugCollection.setDirection(request.getDirection());
			} else {
				doctorDrugCollection = new DoctorDrugCollection(drugCollection.getId(),
						new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()),
						new ObjectId(request.getHospitalId()), 1, false, request.getDuration(), request.getDosage(),
						request.getDosageTime(), request.getDirection(), drugCollection.getGenericNames(),
						drugCollection.getCreatedBy());
				doctorDrugCollection.setUpdatedTime(new Date());
				doctorDrugCollection.setCreatedTime(new Date());
			}
			doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);
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

			transnationalService.addResource(doctorDrugCollection.getId(), Resource.DOCTORDRUG, false);
			if (doctorDrugCollection != null) {
				ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
				BeanUtil.map(drugCollection, esDoctorDrugDocument);
				BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
				esDoctorDrugDocument.setId(drugCollection.getId().toString());
				esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
			}
			response = new Drug();
			BeanUtil.map(drugCollection, response);
			BeanUtil.map(doctorDrugCollection, response);
			response.setId(drugCollection.getId().toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Drug");
		}
		return response;
	}

	@Override
	public Boolean addGenericNameInDrugs() {
		Boolean response = false;

		try {
			List<DrugCollection> drugCollections = drugRepository.findAll();
			for (DrugCollection drugCollection : drugCollections) {
				if (drugCollection.getGenericCodes() != null && !drugCollection.getGenericCodes().isEmpty()) {
					List<GenericCode> genericNames = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("code").in(drugCollection.getGenericCodes()))),
							GenericCodeCollection.class, GenericCode.class).getMappedResults();
					if (genericNames != null && !genericNames.isEmpty()) {
						drugCollection.setGenericNames(genericNames);
						drugCollection.setUpdatedTime(new Date());
						drugCollection = drugRepository.save(drugCollection);

						transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
						ESDrugDocument esDrugDocument = new ESDrugDocument();
						BeanUtil.map(drugCollection, esDrugDocument);
						if (drugCollection.getDrugType() != null) {
							esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
							esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
						}
						esPrescriptionService.addDrug(esDrugDocument);

						List<DoctorDrugCollection> doctorDrugCollections = doctorDrugRepository
								.findByDrugId(drugCollection.getId());
						for (DoctorDrugCollection doctorDrugCollection : doctorDrugCollections) {
							doctorDrugCollection.setGenericNames(genericNames);
							doctorDrugCollection.setUpdatedTime(new Date());
							doctorDrugCollection = doctorDrugRepository.save(doctorDrugCollection);

							ESDoctorDrugDocument esDoctorDrugDocument = new ESDoctorDrugDocument();
							BeanUtil.map(drugCollection, esDoctorDrugDocument);
							BeanUtil.map(doctorDrugCollection, esDoctorDrugDocument);
							esDoctorDrugDocument.setId(drugCollection.getId().toString());
							esPrescriptionService.addDoctorDrug(esDoctorDrugDocument, doctorDrugCollection.getId());
						}
					}
					response = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Making Custom Drugs Favourite");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Making Custom Drugs Favourite");
		}
		return response;
	}

	// @Override
	// public List<DrugInteractionResposne> drugInteraction(List<Drug> request)
	// {
	// List<DrugInteractionResposne> response = null;
	// try {
	// Map<Integer, String> genericCodes = new HashMap<Integer, String>();
	// Map<Drug, Set<String>> drugMap = new LinkedHashMap<Drug, Set<String>>();
	// for (Drug drug : request) {
	// if (drug.getGenericNames() != null && !drug.getGenericNames().isEmpty())
	// {
	// Set<String> codes = new HashSet<String>();
	// for (GenericCode genericNames : drug.getGenericNames()) {
	// if (!codes.contains(genericNames.getCode()))
	// codes.add(genericNames.getCode());
	// if (!genericCodes.containsValue(genericNames.getCode()))
	// genericCodes.put(genericCodes.size(), genericNames.getCode());
	// }
	// drugMap.put(drug, codes);
	// }
	// }
	//
	// boolean[][] codeMatrix = new
	// boolean[genericCodes.size()][genericCodes.size()];
	// for (int row = 0; row < codeMatrix.length; row++) {
	// for (int column = 0; column < codeMatrix[row].length; column++) {
	// if (row != column && !codeMatrix[row][column] &&
	// !codeMatrix[column][row]) {
	// if (!checkGenericContainsInSameDrug(genericCodes.get(row),
	// genericCodes.get(column), drugMap)) {
	// codeMatrix[row][column] = true;
	// codeMatrix[column][row] = true;
	// List<ESGenericCodeWithReaction> esGenericCodeWithReactions =
	// elasticsearchTemplate
	// .queryForList(
	// new NativeSearchQueryBuilder().withQuery(new BoolQueryBuilder()
	// .must(QueryBuilders.matchQuery("codes", genericCodes.get(row)))
	// .must(QueryBuilders.matchQuery("codes", genericCodes.get(column))))
	// .build(),
	// ESGenericCodeWithReaction.class);
	// if (esGenericCodeWithReactions != null &&
	// !esGenericCodeWithReactions.isEmpty()) {
	// if (response == null)
	// response = new ArrayList<>();
	// for (Entry<Drug, Set<String>> entry : drugMap.entrySet()) {
	// if (entry.getValue().contains(genericCodes.get(row))) {
	// for (Entry<Drug, Set<String>> entry2 : drugMap.entrySet()) {
	// if (entry2.getValue().contains(genericCodes.get(column))) {
	// response.add(
	// new DrugInteractionResposne(
	// entry.getKey().getDrugName() + " has "
	// + esGenericCodeWithReactions.get(0)
	// .getReactionType()
	// + " reaction with "
	// + entry2.getKey().getDrugName(),
	// esGenericCodeWithReactions.get(0).getReactionType()));
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While drugInteraction");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// drugInteraction");
	// }
	// return response;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public List<DrugInteractionResposne> drugInteraction(List<Drug> request) {
		List<DrugInteractionResposne> response = null;
		try {
			List<String> genericCodes = new ArrayList<String>();

			for (int k = 0; k < request.size(); k++) {
				Collection<String> codes = CollectionUtils.collect(request.get(k).getGenericNames(),
						new BeanToPropertyValueTransformer("code"));
				for (String word : codes) {
					word = word.toLowerCase();
				}
				genericCodes.addAll(codes);
			}
			Cache.getAll(genericCodes);

			for (int drugCount = 0; drugCount < request.size() - 1; drugCount++) {
				for (int j = drugCount + 1; j < request.size(); j++) {

					Boolean reaction = checkReactionBetweenDrugGenericCodes(request.get(drugCount).getGenericNames(),
							request.get(j).getGenericNames());
					if (reaction) {
						if (response == null)
							response = new ArrayList<DrugInteractionResposne>();
						DrugInteractionResposne interactionResposne = new DrugInteractionResposne(
								request.get(drugCount).getDrugName() + "  reacts with  " + request.get(j).getDrugName(),
								"");
						response.add(interactionResposne);
					}
				}
			}

		} catch (ExecutionException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While drugInteraction");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While drugInteraction");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While drugInteraction");
		}
		return response;
	}

	private Boolean checkReactionBetweenDrugGenericCodes(List<GenericCode> genericCodesOfFirstDrug,
			List<GenericCode> genericCodesOfOtherDrug) {
		Boolean response = false;
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
									return true;
									// if(codes.get(index).getReaction().equalsIgnoreCase("MAJOR"))
									// return "MAJOR";
									// listOfReactions.add(codes.get(index).getReaction());
								}
							}
						}
					}
				}
			}
			// if(listOfReactions.contains("MODERATE"))return "MODERATE";
			// else if(listOfReactions.contains("MINOR"))return "MINOR";

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

	@Override
	public Boolean addGenericsWithReaction() {
		String csvFile = "/home/healthcoco-neha/genericCodesWithReaction.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		try {
			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] codes = line.split(cvsSplitBy);

				Code codeOfId = new Code(codes[0], "");

				ESGenericCodesAndReactions esGenericCodesAndReactionsOfZeroIndex = esGenericCodesAndReactionsRepository
						.findOne(codes[0]);
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
							.findOne(codes[i]);
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

				Collections.sort(codesList, new Comparator<Code>() {
					public int compare(Code one, Code other) {
						return one.getGenericCode().compareTo(other.getGenericCode());
					}
				});
				esGenericCodesAndReactionsOfZeroIndex.setCodes(codesList);
				esGenericCodesAndReactionsOfZeroIndex = esGenericCodesAndReactionsRepository
						.save(esGenericCodesAndReactionsOfZeroIndex);
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

}