package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanUtils;
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
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.PrescriptionAndAdvice;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemAndAdvice;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.beans.PrescriptionJasperDetails;
import com.dpdocter.beans.User;
import com.dpdocter.collections.ConsentFormCollection;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DischargeSummaryRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;

import common.util.web.DPDoctorUtils;

@Service
public class DischargeSummaryServiceImpl implements DischargeSummaryService {

	private static Logger logger = Logger.getLogger(DischargeSummaryServiceImpl.class.getName());

	@Autowired
	private DischargeSummaryRepository dischargeSummaryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private MailService mailService;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Value(value = "${jasper.print.dischargeSummary.a4.fileName}")
	private String dischargeSummaryReportA4FileName;

	@Value(value = "${image.path}")
	private String imagePath;

	@Transactional
	@Override
	public DischargeSummaryResponse addEditDischargeSummary(DischargeSummaryRequest dischargeSummary) {

		DischargeSummaryResponse response = null;
		try {
			List<PrescriptionItemDetail> itemDetails = null;
			PrescriptionItemAndAdvice itemAndAdvice = null;
			DischargeSummaryCollection dischargeSummaryCollection = null;
			UserCollection doctor = userRepository.findOne(new ObjectId(dischargeSummary.getDoctorId()));

			if (dischargeSummary.getId() == null) {
				dischargeSummaryCollection = new DischargeSummaryCollection();
				dischargeSummary.setCreatedTime(new Date());
				dischargeSummaryCollection.setCreatedBy(doctor.getFirstName());
				dischargeSummary.setDischargeId(
						UniqueIdInitial.DISCHARGE_SUMMARY.getInitial() + "-" + DPDoctorUtils.generateRandomId());
			} else {
				dischargeSummaryCollection = dischargeSummaryRepository.findOne(new ObjectId(dischargeSummary.getId()));
			}

			if (dischargeSummaryCollection != null) {
				BeanUtil.map(dischargeSummary, dischargeSummaryCollection);

				if (dischargeSummary.getPrescriptions() != null) {
					PrescriptionAndAdvice prescription = new PrescriptionAndAdvice();
					BeanUtil.map(dischargeSummary.getPrescriptions(), prescription);
					itemAndAdvice = new PrescriptionItemAndAdvice();
					List<PrescriptionItem> items = null;
					for (PrescriptionItem item : prescription.getItems()) {
						PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
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
							if (items == null) {
								items = new ArrayList<PrescriptionItem>();
								itemDetails = new ArrayList<PrescriptionItemDetail>();
							}
							BeanUtil.map(item, prescriptionItemDetail);
							DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
							Drug drug = new Drug();

							if (drugCollection != null) {
								BeanUtil.map(drugCollection, drug);
								DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
								BeanUtil.map(drugCollection, drugAddEditRequest);
								drugAddEditRequest.setDoctorId(doctor.getId().toString());
								drugAddEditRequest.setHospitalId(dischargeSummary.getHospitalId());
								drugAddEditRequest.setLocationId(dischargeSummary.getLocationId());
								drugAddEditRequest.setDirection(item.getDirection());
								drugAddEditRequest.setDuration(item.getDuration());
								drugAddEditRequest.setDosage(item.getDosage());
								drugAddEditRequest.setDosageTime(item.getDosageTime());
								prescriptionServices.addFavouriteDrug(drugAddEditRequest);
							}

							prescriptionItemDetail.setDrug(drug);
							items.add(item);
							itemDetails.add(prescriptionItemDetail);
						}
					}
					itemAndAdvice.setItems(itemDetails);
					prescription.setItems(items);
					if (dischargeSummary.getPrescriptions().getAdvice() != null) {
						prescription.setAdvice(dischargeSummaryCollection.getPrescriptions().getAdvice());
						itemAndAdvice.setAdvice(dischargeSummaryCollection.getPrescriptions().getAdvice());

					}
					dischargeSummaryCollection.setPrescriptions(prescription);
				}

				dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);
				response = new DischargeSummaryResponse();

				BeanUtil.map(dischargeSummaryCollection, response);
				response.setPrescriptions(itemAndAdvice);

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid  discharge summary Id  ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding  discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding discharge summary : " + e.getCause().getMessage());

		}
		return response;
	}

	/*
	 * @Transactional
	 * 
	 * @Override public List<DischargeSummary> getAllDischargeSummary() {
	 * List<DischargeSummary> response = null; DischargeSummary dischargeSummary
	 * = null; List<DischargeSummaryCollection> dischargeSummaryCollections =
	 * null;
	 * 
	 * dischargeSummaryCollections = dischargeSummaryRepository.findAll(); for
	 * (DischargeSummaryCollection dischargeSummaryCollection :
	 * dischargeSummaryCollections) { dischargeSummary = new DischargeSummary();
	 * BeanUtil.map(dischargeSummaryCollection, dischargeSummary);
	 * response.add(dischargeSummary); }
	 * 
	 * return response; }
	 */

	@Override
	@Transactional
	public List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime) {
		List<DischargeSummaryResponse> response = null;
		try {
			DischargeSummaryResponse summaryResponse = null;
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DischargeSummaryCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					DischargeSummaryCollection.class, DischargeSummaryCollection.class);
			List<DischargeSummaryCollection> dischargeSummaryCollections = aggregationResults.getMappedResults();
			response = new ArrayList<DischargeSummaryResponse>();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {
				summaryResponse = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, summaryResponse);

				if (dischargeSummaryCollection.getPrescriptions() != null) {
					List<PrescriptionItemDetail> items = new ArrayList<PrescriptionItemDetail>();
					if (dischargeSummaryCollection.getPrescriptions().getItems() != null
							&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
						for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
							PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
							BeanUtil.map(item, prescriptionItemDetail);
							if (item.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
								Drug drug = new Drug();
								BeanUtil.map(drugCollection, drug);
								prescriptionItemDetail.setDrug(drug);
							}
							items.add(prescriptionItemDetail);

						}
					summaryResponse.getPrescriptions().setItems(items);
				}
				response.add(summaryResponse);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting discharge summary : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DischargeSummaryResponse viewDischargeSummary(String dischargeSummeryId) {
		DischargeSummaryResponse response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {
				response = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, response);

				if (dischargeSummaryCollection.getPrescriptions() != null) {
					List<PrescriptionItemDetail> items = new ArrayList<PrescriptionItemDetail>();

					if (dischargeSummaryCollection.getPrescriptions().getItems() != null
							&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
						for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
							PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();

							BeanUtil.map(item, prescriptionItemDetail);

							if (item.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
								Drug drug = new Drug();
								BeanUtil.map(drugCollection, drug);
								prescriptionItemDetail.setDrug(drug);
							}
							items.add(prescriptionItemDetail);

						}
					response.getPrescriptions().setItems(items);
				}

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid discharge summaryId ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while view discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while view discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public int getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
				response = dischargeSummaryRepository.countByPatientId(patientObjectId);
			else
				response = dischargeSummaryRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while count discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while count discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public DischargeSummaryResponse deleteDischargeSummary(String dischargeSummeryId, String doctorId,
			String hospitalId, String locationId, Boolean discarded) {
		DischargeSummaryResponse response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(dischargeSummaryCollection.getDoctorId(),
						dischargeSummaryCollection.getHospitalId(), dischargeSummaryCollection.getLocationId())) {
					if (dischargeSummaryCollection.getDoctorId().toString().equals(doctorId)
							&& dischargeSummaryCollection.getHospitalId().toString().equals(hospitalId)
							&& dischargeSummaryCollection.getLocationId().toString().equals(locationId)) {
						dischargeSummaryCollection.setDiscarded(discarded);
						dischargeSummaryCollection.setUpdatedTime(new Date());
						dischargeSummaryRepository.save(dischargeSummaryCollection);
						response = new DischargeSummaryResponse();
						BeanUtil.map(dischargeSummaryCollection, response);
						if (dischargeSummaryCollection.getPrescriptions() != null) {
							List<PrescriptionItemDetail> items = new ArrayList<PrescriptionItemDetail>();

							if (dischargeSummaryCollection.getPrescriptions().getItems() != null
									&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
								for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
									PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();

									BeanUtil.map(item, prescriptionItemDetail);

									if (item.getDrugId() != null) {
										DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
										Drug drug = new Drug();
										BeanUtil.map(drugCollection, drug);
										prescriptionItemDetail.setDrug(drug);
									}
									items.add(prescriptionItemDetail);

								}
							response.getPrescriptions().setItems(items);
						}
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				}
			} else {
				logger.warn("Discharge Summary not found!");
				throw new BusinessException(ServiceError.NoRecord, "Discharge Summary not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public String downloadDischargeSummary(String dischargeSummeryId) {
		String response = null;

		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {

				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getLocationId(),
						dischargeSummaryCollection.getHospitalId());

				UserCollection user = userRepository.findOne(dischargeSummaryCollection.getPatientId());
				JasperReportResponse jasperReportResponse = createJasper(dischargeSummaryCollection, patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Exception in download Discharge Summary ");
		}
		return response;
	}

	private JasperReportResponse createJasper(DischargeSummaryCollection dischargeSummaryCollection,
			PatientCollection patient, UserCollection user) throws NumberFormatException, IOException {
		JasperReportResponse response = null;
		List<PrescriptionJasperDetails> prescriptionItems = new ArrayList<PrescriptionJasperDetails>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Boolean show = false;
		if (dischargeSummaryCollection.getPrescriptions() != null) {
			show = true;
			int no = 0;
			Boolean showIntructions = false, showDirection = false;
			if (dischargeSummaryCollection.getPrescriptions().getItems() != null
					&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty()) {
				for (PrescriptionItem prescriptionItem : dischargeSummaryCollection.getPrescriptions().getItems()) {
					if (prescriptionItem != null && prescriptionItem.getDrugId() != null) {
						DrugCollection drug = drugRepository.findOne(prescriptionItem.getDrugId());
						if (drug != null) {
							String drugType = drug.getDrugType() != null
									? (drug.getDrugType().getType() != null ? drug.getDrugType().getType() + " " : "")
									: "";
							String drugName = drug.getDrugName() != null ? drug.getDrugName() : "";
							drugName = (drugType + drugName) == "" ? "--" : drugType + " " + drugName;
							String durationValue = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration().getValue() != null
											? prescriptionItem.getDuration().getValue() : "")
									: "";
							String durationUnit = prescriptionItem.getDuration() != null
									? (prescriptionItem.getDuration()
											.getDurationUnit() != null
													? (!DPDoctorUtils.anyStringEmpty(
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
										directions = directions
												+ (prescriptionItem.getDirection().get(0).getDirection());
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

							String genericName = "";
							if (drug.getGenericNames() != null && !drug.getGenericNames().isEmpty()) {
								for (GenericCode genericCode : drug.getGenericNames()) {
									if (DPDoctorUtils.anyStringEmpty(genericName))
										genericName = genericCode.getName();
									else
										genericName = genericName + "+" + genericCode.getName();
								}
							}

							PrescriptionJasperDetails prescriptionJasperDetails = new PrescriptionJasperDetails(no,
									drugName,
									!DPDoctorUtils.anyStringEmpty(prescriptionItem.getDosage())
											? prescriptionItem.getDosage() : "--",
									duration, directions.isEmpty() ? "--" : directions,
									!DPDoctorUtils.anyStringEmpty(prescriptionItem.getInstructions())
											? prescriptionItem.getInstructions() : "--",
									genericName);
							prescriptionItems.add(prescriptionJasperDetails);
						}
					}
				}

				parameters.put("prescriptionItems", prescriptionItems);

				parameters.put("showIntructions", showIntructions);
				parameters.put("showDirection", showDirection);
			}
		}
		parameters.put("showPrescription", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPrescriptions().getAdvice())) {
			show = true;
			parameters.put("advice", dischargeSummaryCollection.getPrescriptions().getAdvice());
		}
		parameters.put("showAdvice", show);

		show = false;
		if (dischargeSummaryCollection.getAdmissionDate() != null) {
			show = true;
			parameters.put("dOA",
					"<b>DOA:-</b>" + simpleDateFormat.format(dischargeSummaryCollection.getAdmissionDate()));
		}
		parameters.put("showDOA", show);
		show = false;
		if (dischargeSummaryCollection.getDischargeDate() != null) {
			show = true;
			parameters.put("dOD",
					"<b>DOD:-</b>" + simpleDateFormat.format(dischargeSummaryCollection.getDischargeDate()));
		}
		parameters.put("showDOD", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getBabyNotes())) {
			show = true;
			parameters.put("babyNotes", dischargeSummaryCollection.getBabyNotes());
		}
		parameters.put("showBabyNotes", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getBabyWeight())) {
			show = true;
			parameters.put("babyWeight", dischargeSummaryCollection.getBabyWeight());
		}
		parameters.put("showBabyWeight", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getComplaints())) {
			show = true;
			parameters.put("complaints", dischargeSummaryCollection.getComplaints());
		}
		parameters.put("showcompl", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getEcho())) {
			show = true;
			parameters.put("echo", dischargeSummaryCollection.getEcho());
		}
		parameters.put("showecho", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getConditionsAtDischarge())) {
			show = true;
			parameters.put("condition", dischargeSummaryCollection.getConditionsAtDischarge());
		}
		parameters.put("showcondition", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getDiagnosis())) {
			show = true;
			parameters.put("diagnosis", dischargeSummaryCollection.getDiagnosis());
		}
		parameters.put("showDiagnosis", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getFamilyHistory())) {
			show = true;
			parameters.put("familyHistory", dischargeSummaryCollection.getFamilyHistory());
		}
		parameters.put("showfH", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getHolter())) {
			show = true;
			parameters.put("holter", dischargeSummaryCollection.getHolter());
		}
		parameters.put("showholter", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getEcgDetails())) {
			show = true;
			parameters.put("ecgDetails", dischargeSummaryCollection.getEcgDetails());
		}
		parameters.put("showEcg", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getGeneralExamination())) {
			show = true;
			parameters.put("generalExam", dischargeSummaryCollection.getGeneralExamination());
		}
		parameters.put("showGExam", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getIndicationOfUSG())) {
			show = true;
			parameters.put("indicationOfUSG", dischargeSummaryCollection.getIndicationOfUSG());
		}
		parameters.put("showINUSG", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getxRayDetails())) {
			show = true;
			parameters.put("xRayDetails", dischargeSummaryCollection.getxRayDetails());
		}
		parameters.put("showXD", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getHistoryOfPresentComplaints())) {
			show = true;
			parameters.put("historyOfPresentComplaints", dischargeSummaryCollection.getHistoryOfPresentComplaints());
		}
		parameters.put("showHPC", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getObservation())) {
			show = true;
			parameters.put("observation", dischargeSummaryCollection.getObservation());
		}
		parameters.put("showObserv", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getInvestigation())) {
			show = true;
			parameters.put("investigation", dischargeSummaryCollection.getInvestigation());
		}
		parameters.put("showINV", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getLabourNotes())) {
			show = true;
			parameters.put("labourNotes", dischargeSummaryCollection.getLabourNotes());
		}
		parameters.put("showLN", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getMenstrualHistory())) {
			show = true;
			parameters.put("menstrualHistory", dischargeSummaryCollection.getMenstrualHistory());
		}
		parameters.put("showMH", show);
		show = false;
		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getObstetricHistory())) {
			show = true;
			parameters.put("obstetricHistory", dischargeSummaryCollection.getObstetricHistory());
		}
		parameters.put("showOH", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPastHistory())) {
			show = true;
			parameters.put("pastHistory", dischargeSummaryCollection.getPastHistory());
		}
		parameters.put("showPH", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getOperationNotes())) {
			show = true;
			parameters.put("operationNotes", dischargeSummaryCollection.getOperationNotes());
		}
		parameters.put("showON", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getTreatmentsGiven())) {
			show = true;
			parameters.put("treatmentGiven", dischargeSummaryCollection.getTreatmentsGiven());
		}
		parameters.put("showTG", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSystemicExamination())) {
			show = true;
			parameters.put("systemExam", dischargeSummaryCollection.getSystemicExamination());
		}
		parameters.put("showSExam", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getSummary())) {
			show = true;
			parameters.put("summary", dischargeSummaryCollection.getSummary());
		}
		parameters.put("showSum", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPa())) {
			show = true;
			parameters.put("pa", dischargeSummaryCollection.getPa());
		}
		parameters.put("showPA", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPs())) {
			show = true;
			parameters.put("ps", dischargeSummaryCollection.getPs());
		}
		parameters.put("showPS", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPv())) {
			show = true;
			parameters.put("pv", dischargeSummaryCollection.getPv());
		}
		parameters.put("showPV", show);
		show = false;

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPersonalHistory())) {
			show = true;
			parameters.put("pesonalHistory", dischargeSummaryCollection.getPersonalHistory());
		}
		parameters.put("showPersonalHistory", show);

		if (!DPDoctorUtils.allStringsEmpty(dischargeSummaryCollection.getPresentComplaints())) {
			show = true;
			parameters.put("presentComplaints", dischargeSummaryCollection.getPresentComplaints());
		}
		parameters.put("showpresentComplaints", show);
		show = false;

		if (dischargeSummaryCollection.getNextReview() != null) {
			show = true;
			parameters.put("fromDate", "<b>From :</b>"
					+ simpleDateFormat.format(dischargeSummaryCollection.getNextReview().getFromDate()));
			parameters.put("toDate",
					"<b>To:</b>" + simpleDateFormat.format(dischargeSummaryCollection.getNextReview().getToDate()));
		}
		parameters.put("showNextReview", show);
		show = false;

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				dischargeSummaryCollection.getDoctorId(), dischargeSummaryCollection.getLocationId(),
				dischargeSummaryCollection.getHospitalId(), ComponentType.ALL.getType());
		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace() : LineSpace.SMALL.name());
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>DIS-ID: </b>" + (dischargeSummaryCollection.getDischargeId() != null
						? dischargeSummaryCollection.getDischargeId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters);
		patientVisitService.generatePrintSetup(parameters, printSettings, dischargeSummaryCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "DISCHARGE-SUMMARY-"
				+ dischargeSummaryCollection.getDischargeId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.DISCHARGE_SUMMARY, parameters,
				dischargeSummaryReportA4FileName, layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;

	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public void emailDischargeSummary(String dischargeSummeryId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		DischargeSummaryCollection dischargeSummaryCollection = null;
		MailAttachment mailAttachment = null;
		UserCollection user = null;
		PatientCollection patient = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			dischargeSummaryCollection = dischargeSummaryRepository.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {
				if (dischargeSummaryCollection.getDoctorId() != null
						&& dischargeSummaryCollection.getHospitalId() != null
						&& dischargeSummaryCollection.getLocationId() != null) {
					if (dischargeSummaryCollection.getDoctorId().equals(doctorId)
							&& dischargeSummaryCollection.getHospitalId().equals(hospitalId)
							&& dischargeSummaryCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(dischargeSummaryCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
								dischargeSummaryCollection.getPatientId(), dischargeSummaryCollection.getLocationId(),
								dischargeSummaryCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(dischargeSummaryCollection.getDoctorId());
						emailTrackCollection.setHospitalId(dischargeSummaryCollection.getHospitalId());
						emailTrackCollection.setLocationId(dischargeSummaryCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.DISCHARGE_SUMMARY.getType());
						emailTrackCollection.setSubject("Discharge Summary");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(dischargeSummaryCollection, patient,
								user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
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
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse.setMailRecordCreatedDate(sdf.format(dischargeSummaryCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("DischargeSummary Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								" DischargeSummary  Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Discharge Summary  not found.Please check summaryId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Discharge Summary not found.Please check summaryId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Consent Form", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Discharge Summary", body,
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

}
