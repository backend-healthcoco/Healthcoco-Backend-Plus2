package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.BirthAchievement;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.GrowthChart;
import com.dpdocter.beans.PatientDetails;
import com.dpdocter.beans.PrintSettingsText;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Vaccine;
import com.dpdocter.collections.BirthAchievementCollection;
import com.dpdocter.collections.GrowthChartCollection;
import com.dpdocter.collections.MasterBabyImmunizationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.collections.VaccineBrandAssociationCollection;
import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.elasticsearch.response.GrowthChartGraphResponse;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.FONTSTYLE;
import com.dpdocter.enums.FieldAlign;
import com.dpdocter.enums.LineSpace;
import com.dpdocter.enums.PrintSettingType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.VaccineStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BirthAchievementRepository;
import com.dpdocter.repository.GrowthChartRepository;
import com.dpdocter.repository.MasterBabyImmunizationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.VaccineRepository;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.BabyVaccineReminderResponse;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MasterVaccineResponse;
import com.dpdocter.response.PatientVaccineGroupedResponse;
import com.dpdocter.response.VaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineResponse;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PaediatricService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PaediatricServiceImpl implements PaediatricService {
	private static Logger logger = Logger.getLogger(PaediatricServiceImpl.class.getName());

	@Autowired
	private GrowthChartRepository growthChartRepository;

	@Autowired
	private BirthAchievementRepository birthAchievementRepository;

	@Autowired
	private VaccineRepository vaccineRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSServices smsServices;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Autowired
	private MasterBabyImmunizationRepository masterBabyImmunizationRepository;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.vaccination.a4.fileName}")
	private String vaccinationA4FileName;

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Override
	@Transactional
	public GrowthChart addEditGrowthChart(GrowthChart growthChart) {
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			if (growthChart.getId() != null) {
				growthChartCollection = growthChartRepository.findById(new ObjectId(growthChart.getId())).orElse(null);
			} else {
				growthChartCollection = new GrowthChartCollection();
			}
			BeanUtil.map(growthChart, growthChartCollection);
			growthChartCollection = growthChartRepository.save(growthChartCollection);
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			}
			if (growthChart.getDoctorId() != null) {
				pushNotificationServices.notifyUser(growthChart.getDoctorId(), "Growth chart updated",
						ComponentType.REFRESH_GROWTH_CHART.getType(), null, null);
			}
			pushNotificationServices.notifyUser(growthChart.getPatientId(), "Growth chart updated",
					ComponentType.REFRESH_GROWTH_CHART.getType(), growthChart.getPatientId(), null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	@Override
	@Transactional
	public GrowthChart getGrowthChartById(String id) {
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findById(new ObjectId(id)).orElse(null);
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	/*
	 * public List<GrowthChart> getGrowthChartById(String patientId, String
	 * doctorId, String locationId, String hospitalId, int page, int size) {
	 * List<GrowthChart> growthCharts = null; GrowthChartCollection
	 * growthChartCollection = null; try { growthChartCollection =
	 * growthChartRepository.findById(new ObjectId(id)); if (growthChartCollection
	 * != null) { response = new GrowthChart(); BeanUtil.map(growthChartCollection,
	 * response); } else { throw new BusinessException(ServiceError.NoRecord,
	 * "Record not found"); } } catch (Exception e) { // TODO: handle exception
	 * e.printStackTrace(); throw e;
	 * 
	 * } return response; }
	 */

	@Override
	@Transactional
	public List<GrowthChart> getGrowthChartList(String patientId, String doctorId, String locationId, String hospitalId,
			String updatedTime) {
		List<GrowthChart> responses = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			criteria.and("discarded").is(false);

			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					GrowthChartCollection.class, GrowthChart.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}

	@Override
	@Transactional
	public Boolean discardGrowthChart(String id, Boolean discarded) {
		Boolean response = false;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findById(new ObjectId(id)).orElse(null);
			if (growthChartCollection != null) {
				growthChartCollection.setDiscarded(discarded);
				growthChartRepository.save(growthChartCollection);
				response = true;
				if (growthChartCollection.getDoctorId() != null) {
					pushNotificationServices.notifyUser(growthChartCollection.getDoctorId().toString(),
							"Growth chart discarded", ComponentType.REFRESH_GROWTH_CHART.getType(), null, null);
				}
				pushNotificationServices.notifyUser(growthChartCollection.getPatientId().toString(),
						"Growth chart discarded", ComponentType.REFRESH_GROWTH_CHART.getType(),
						growthChartCollection.getPatientId().toString(), null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	@Override
	@Transactional
	public VaccineResponse addEditVaccine(VaccineRequest request) {
		VaccineResponse response = null;
		VaccineCollection vaccineCollection = null;
		try {
			if (request.getId() != null) {
				vaccineCollection = vaccineRepository.findById(new ObjectId(request.getId())).orElse(null);
			} else {
				vaccineCollection = new VaccineCollection();
			}
			BeanUtil.map(request, vaccineCollection);
			vaccineCollection = vaccineRepository.save(vaccineCollection);
			if (vaccineCollection != null) {
				response = new VaccineResponse();
				BeanUtil.map(vaccineCollection, response);
			}
			if (request.getDoctorId() != null) {
				pushNotificationServices.notifyUser(request.getDoctorId(), "Vaccination updated",
						ComponentType.REFRESH_VACCINATION.getType(), null, null);
			}
			pushNotificationServices.notifyUser(request.getPatientId(), "Vaccination updated",
					ComponentType.REFRESH_VACCINATION.getType(), request.getPatientId(), null);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditMultipleVaccine(List<VaccineRequest> requests) {
		Boolean response = false;
		VaccineCollection vaccineCollection = null;
		try {
			for (VaccineRequest request : requests) {
				if (request.getId() != null) {
					vaccineCollection = vaccineRepository.findById(new ObjectId(request.getId())).orElse(null);
				} else {
					vaccineCollection = new VaccineCollection();
				}
				BeanUtil.map(request, vaccineCollection);
				vaccineCollection.setUpdatedTime(new Date());
				if (request.getIsUpdatedByPatient() == true) {
					vaccineCollection.setCreatedBy("PATIENT");
				} else {
					if (request.getDoctorId() != null) {
						UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
								.orElse(null);
						if (userCollection != null) {
							vaccineCollection.setCreatedBy(userCollection.getFirstName());
						}
					}
				}

				sendVaccinationMessage(request.getPatientId());

				vaccineCollection = vaccineRepository.save(vaccineCollection);
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditMultipleVaccineStatus(MultipleVaccineEditRequest request) {
		Boolean response = false;
		VaccineCollection vaccineCollection = null;
		try {
			for (String id : request.getIds()) {
				if (id != null) {
					vaccineCollection = vaccineRepository.findById(new ObjectId(id)).orElse(null);
					vaccineCollection.setStatus(request.getStatus());
					vaccineCollection = vaccineRepository.save(vaccineCollection);
					response = true;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	@Override
	@Transactional
	public VaccineResponse getVaccineById(String id) {
		VaccineResponse response = null;
		VaccineCollection vaccineCollection = null;
		try {
			vaccineCollection = vaccineRepository.findById(new ObjectId(id)).orElse(null);
			if (vaccineCollection != null) {
				response = new VaccineResponse();
				BeanUtil.map(vaccineCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	@Override
	@Transactional
	public List<VaccineResponse> getVaccineList(String patientId, String doctorId, String locationId, String hospitalId,
			String updatedTime) {
		List<VaccineResponse> responses = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$vaccineBrand").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.lookup("master_baby_immunization_cl", "vaccineId", "_id", "vaccine"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$vaccine").append("preserveNullAndEmptyArrays", true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "vaccine.id"))),
					VaccineCollection.class, VaccineResponse.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}

	@Override
	@Transactional
	public List<MasterVaccineResponse> getMasterVaccineList(String searchTerm, Boolean isChartVaccine, int page,
			int size) {
		List<MasterVaccineResponse> responses = null;
		try {
			Aggregation aggregation = null;

			Criteria criteria = new Criteria();

			if (isChartVaccine != null) {
				criteria.and("isChartVaccine").is(isChartVaccine);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			}

			AggregationResults<MasterVaccineResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					MasterBabyImmunizationCollection.class, MasterVaccineResponse.class);
			responses = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}

	@Override
	@Transactional
	public List<VaccineBrandAssociationResponse> getVaccineBrandAssociation(String vaccineId, String vaccineBrandId) {

		List<VaccineBrandAssociationResponse> responses = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(vaccineId)) {
				criteria.and("vaccineId").is(new ObjectId(vaccineId));
			}

			if (!DPDoctorUtils.anyStringEmpty(vaccineBrandId)) {
				criteria.and("vaccineBrandId").is(new ObjectId(vaccineBrandId));
			}

			responses = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(
									Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
									new CustomAggregationOperation(
											new Document("$unwind",
													new BasicDBObject("path", "$vaccineBrand")
															.append("preserveNullAndEmptyArrays", true))),
									Aggregation.lookup("master_baby_immunization_cl", "vaccineId", "_id", "vaccine"),
									new CustomAggregationOperation(
											new Document("$unwind",
													new BasicDBObject("path", "$vaccine")
															.append("preserveNullAndEmptyArrays", true))),
									Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC,
											"id"))/*
													 * Aggregation.sort(new Sort(Direction.DESC, "createdTime"))
													 */),
							VaccineBrandAssociationCollection.class, VaccineBrandAssociationResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
	}

	@Override
	@Transactional
	public List<GroupedVaccineBrandAssociationResponse> getGroupedVaccineBrandAssociation(List<String> vaccineIds) {
		List<ObjectId> vaccineObjectIds = null;
		List<GroupedVaccineBrandAssociationResponse> responses = null;
		try {
			Criteria criteria = new Criteria();

			if (vaccineIds != null) {
				vaccineObjectIds = new ArrayList<>();
				for (String id : vaccineIds) {
					vaccineObjectIds.add(new ObjectId(id));
				}
			}

			if (vaccineIds != null) {
				criteria.and("vaccineId").in(vaccineObjectIds);
			}

			AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("name", "$vaccine.name"))
							.append("vaccine", new BasicDBObject("$push", "$vaccine"))
							.append("name", new BasicDBObject("$first", "$vaccine.name"))
							.append("id", new BasicDBObject("$first", "$vaccine.id"))));

			responses = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(
									Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
									new CustomAggregationOperation(new Document("$unwind",
											new BasicDBObject("path", "$vaccineBrand")
													.append("preserveNullAndEmptyArrays", true))),
									Aggregation.match(criteria), aggregationOperation,
									Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
							VaccineBrandAssociationCollection.class, GroupedVaccineBrandAssociationResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
	}

	@Override
	@Transactional
	public List<PatientVaccineGroupedResponse> getPatientGroupedVaccines(String patientId) {
		List<PatientVaccineGroupedResponse> responses = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId));

			AggregationOperation aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", new BasicDBObject("duration", "$duration"))
							.append("vaccines", new BasicDBObject("$push", "$vaccine"))
							.append("duration", new BasicDBObject("$first", "$duration"))
							.append("periodTime", new BasicDBObject("$first", "$periodTime"))));
			aggregation = Aggregation.newAggregation(Aggregation.lookup("vaccine_cl", "_id", "_id", "vaccine"),
					Aggregation.unwind("vaccine"),
					Aggregation.lookup("vaccine_brand_cl", "vaccine.vaccineBrandId", "_id", "vaccine.vaccineBrand"),
					new CustomAggregationOperation(new Document("$unwind",
							new BasicDBObject("path", "$vaccine.vaccineBrand").append("preserveNullAndEmptyArrays",
									true))),
					Aggregation.match(criteria), aggregationOperation,
					Aggregation.sort(new Sort(Direction.ASC, "periodTime")));

			responses = mongoTemplate
					.aggregate(aggregation, VaccineCollection.class, PatientVaccineGroupedResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return responses;
	}

	public List<VaccineResponse> getPatientGroupedVaccinesByPeriodTime(String patientId, Integer periodTime) {
		List<VaccineResponse> responses = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId));
			if (periodTime != null)
				criteria.and("periodTime").is(periodTime);

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")));

			responses = mongoTemplate.aggregate(aggregation, VaccineCollection.class, VaccineResponse.class)
					.getMappedResults();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return responses;
	}

	@Scheduled(cron = "0 30 6 * * ?", zone = "IST")
	@Override
	// @Scheduled(fixedDelay = 25000)
	public void sendBabyVaccineReminder() {
		// TODO Auto-generated method stub
		List<BabyVaccineReminderResponse> response = null;

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		Aggregation aggregation = null;
		AggregationOperation aggregationOperation = null;

		Criteria criteria = new Criteria("dueDate").gte(DPDoctorUtils.getStartTimeUTC(new Date()))
				.lte(DPDoctorUtils.getEndTimeUTC(new Date()));

		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					Fields.field("patientName", "$patient.firstName"),
					Fields.field("mobileNumber", "$patient.mobileNumber"),
					Fields.field("doctorName", "$doctor.firstName"),
					Fields.field("locationName", "$location.locationName"),
					Fields.field("clinicNumber", "$location.clinicNumber"), Fields.field("vaccines", "$vaccines")));

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("patientId", "$patientId"))
							.append("doctorName", new BasicDBObject("$first", "$doctorName"))
							.append("locationName", new BasicDBObject("$first", "$locationName"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("clinicNumber", new BasicDBObject("$first", "$clinicNumber"))
							.append("vaccines", new BasicDBObject("$push", "$vaccines"))));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
					Aggregation.unwind("patient"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("vaccine_cl", "_id", "_id", "vaccines"),
					Aggregation.unwind("vaccines"), Aggregation.match(criteria),
					projectList.and("dueDate").extractDayOfMonth().as("day").and("dueDate").extractMonth().as("month")
							.and("dueDate").extractYear().as("year").and("dueDate").extractWeek().as("week"),
					aggregationOperation);

			AggregationResults<BabyVaccineReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					VaccineCollection.class, BabyVaccineReminderResponse.class);
			response = aggregationResults.getMappedResults();

			for (BabyVaccineReminderResponse vaccineReminderResponse : response) {

				if (vaccineReminderResponse.getMobileNumber() != null) {

					String vaccineNames = "";
					String dateTime = "";
					List<String> vaccineList = new ArrayList<>();
					for (Vaccine vaccine : vaccineReminderResponse.getVaccines()) {
						vaccineList.add(vaccine.getName());
					}
					vaccineNames = StringUtils.join(vaccineList, ",");

					dateTime = formatter.print(new DateTime());

					String message = "Your vaccination for " + vaccineNames + " is due with "
							+ vaccineReminderResponse.getDoctorName()
							+ (vaccineReminderResponse.getLocationName() != ""
									? ", " + vaccineReminderResponse.getLocationName()
									: "")
							+ ((vaccineReminderResponse.getClinicNumber() != ""
									|| vaccineReminderResponse.getClinicNumber() != null)
											? ", " + vaccineReminderResponse.getClinicNumber()
											: "");
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					smsTrackDetail.setType("Vaccination_SMS");
					SMSDetail smsDetail = new SMSDetail();
					SMS sms = new SMS();
					sms.setSmsText(message);

					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(vaccineReminderResponse.getMobileNumber());
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
	}

	@Scheduled(cron = "0 45 6 * * ?", zone = "IST")
	@Override
	// @Scheduled(fixedDelay = 15000)
	public void sendBirthBabyVaccineReminder() {
		// TODO Auto-generated method stub
		List<BabyVaccineReminderResponse> response = null;
		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

		Aggregation aggregation = null;
		AggregationOperation aggregationOperation = null;
		DateTime previousdate = DPDoctorUtils.getStartTimeUTC(new Date()).minusDays(3);

		Criteria criteria = new Criteria("dueDate").gte(previousdate).lte(DPDoctorUtils.getEndTimeUTC(new Date()));

		criteria.and("status").is(VaccineStatus.PLANNED);
		criteria.and("periodTime").is(0);

		try {
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(
					Fields.field("patientName", "$patient.firstName"),
					Fields.field("mobileNumber", "$patient.mobileNumber"),
					Fields.field("doctorName", "$doctor.firstName"),
					Fields.field("locationName", "$location.locationName"),
					Fields.field("clinicNumber", "$location.clinicNumber"), Fields.field("vaccines", "$vaccines")));

			aggregationOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("patientId", "$patientId"))
							.append("doctorName", new BasicDBObject("$first", "$doctorName"))
							.append("locationName", new BasicDBObject("$first", "$locationName"))
							.append("patientName", new BasicDBObject("$first", "$patientName"))
							.append("mobileNumber", new BasicDBObject("$first", "$mobileNumber"))
							.append("clinicNumber", new BasicDBObject("$first", "$clinicNumber"))
							.append("vaccines", new BasicDBObject("$push", "$vaccines"))));

			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
					Aggregation.unwind("patient"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("vaccine_cl", "_id", "_id", "vaccines"),
					Aggregation.unwind("vaccines"), Aggregation.match(criteria),
					projectList.and("dueDate").extractDayOfMonth().as("day").and("dueDate").extractMonth().as("month")
							.and("dueDate").extractYear().as("year").and("dueDate").extractWeek().as("week"),
					aggregationOperation);

			AggregationResults<BabyVaccineReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					VaccineCollection.class, BabyVaccineReminderResponse.class);
			response = aggregationResults.getMappedResults();

			for (BabyVaccineReminderResponse vaccineReminderResponse : response) {

				if (vaccineReminderResponse.getMobileNumber() != null) {

					String vaccineNames = "";
					String dateTime = "";
					List<String> vaccineList = new ArrayList<>();
					for (Vaccine vaccine : vaccineReminderResponse.getVaccines()) {
						vaccineList.add(vaccine.getName());
					}
					vaccineNames = StringUtils.join(vaccineList, ",");

					// dateTime =
					// formatter.print(vaccineReminderResponse.getVaccines().get(0).getDueDate().getTime());
					dateTime = formatter.print(new DateTime());

					String message = "Your vaccination for " + vaccineNames + " is due with "
							+ vaccineReminderResponse.getDoctorName()
							+ (vaccineReminderResponse.getLocationName() != ""
									? ", " + vaccineReminderResponse.getLocationName()
									: "")
							+ ((vaccineReminderResponse.getClinicNumber() != ""
									|| vaccineReminderResponse.getClinicNumber() != null)
											? ", " + vaccineReminderResponse.getClinicNumber()
											: "")
							+ " on " + dateTime + ". Download Healthcoco App- " + patientAppBitLink;
					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

					smsTrackDetail.setType("Vaccination_SMS");
					SMSDetail smsDetail = new SMSDetail();
					SMS sms = new SMS();
					sms.setSmsText(message);

					SMSAddress smsAddress = new SMSAddress();
					smsAddress.setRecipient(vaccineReminderResponse.getMobileNumber());
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
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Async
	@Transactional
	private void sendVaccinationMessage(String userId) {
		UserDeviceCollection userDeviceCollection = userDeviceRepository.findByDeviceId(userId);
		if (userDeviceCollection != null) {
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			if (userCollection != null && userCollection.getMobileNumber() != null) {

				String message = "Your vaccines can now be tracked on Healthcoco App. Download Healthcoco App- "
						+ patientAppBitLink;

				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

				smsTrackDetail.setType("Vaccination_SMS");
				SMSDetail smsDetail = new SMSDetail();
				SMS sms = new SMS();
				sms.setSmsText(message);

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

		}
	}

	@Override
	@Transactional
	public Boolean updateOldPatientData() {
		Boolean status = false;
		List<VaccineCollection> vaccineCollections = vaccineRepository.findAll();
		for (VaccineCollection vaccineCollection : vaccineCollections) {
			if (vaccineCollection.getVaccineId() != null) {
				MasterBabyImmunizationCollection babyImmunizationCollection = masterBabyImmunizationRepository
						.findById(vaccineCollection.getVaccineId()).orElse(null);
				if (babyImmunizationCollection != null) {
					vaccineCollection.setName(babyImmunizationCollection.getName());
					vaccineCollection.setDuration(babyImmunizationCollection.getDuration());
					vaccineCollection.setPeriodTime(babyImmunizationCollection.getPeriodTime());
					vaccineCollection.setLongName(babyImmunizationCollection.getLongName());
					vaccineCollection = vaccineRepository.save(vaccineCollection);
				}

			}
			status = true;
		}
		return status;
	}

	@Override
	@Transactional
	public BirthAchievement addEditBirthAchievement(BirthAchievement birthAchievement) {
		BirthAchievement response = null;
		BirthAchievementCollection birthAchievementCollection = null;
		try {
			if (birthAchievement.getId() != null) {
				birthAchievementCollection = birthAchievementRepository.findById(new ObjectId(birthAchievement.getId()))
						.orElse(null);
			} else {
				birthAchievementCollection = new BirthAchievementCollection();
				birthAchievement.setCreatedTime(new Date());
			}
			BeanUtil.map(birthAchievement, birthAchievementCollection);
			birthAchievementCollection = birthAchievementRepository.save(birthAchievementCollection);
			if (birthAchievementCollection != null) {
				response = new BirthAchievement();
				BeanUtil.map(birthAchievementCollection, response);
			}
			if (birthAchievement.getDoctorId() != null) {
				pushNotificationServices.notifyUser(birthAchievement.getDoctorId(), "Growth chart updated",
						ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType(), null, null);
			}
			pushNotificationServices.notifyUser(birthAchievement.getPatientId(), "Baby achievement updated",
					ComponentType.REFRESH_BABY_ACHIEVEMENTS.getType(), birthAchievement.getPatientId(), null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	@Override
	@Transactional
	public BirthAchievement getBirthAchievementById(String id) {
		BirthAchievement response = null;
		BirthAchievementCollection birthAchievementCollection = null;
		try {
			birthAchievementCollection = birthAchievementRepository.findById(new ObjectId(id)).orElse(null);
			if (birthAchievementCollection != null) {
				response = new BirthAchievement();
				BeanUtil.map(birthAchievementCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}

	@Override
	@Transactional
	public List<BirthAchievement> getBirthAchievementList(String patientId, String updatedTime, int page, int size) {
		List<BirthAchievement> responses = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "id")), Aggregation.skip((long) (page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.ASC, "id")));
			}

			responses = mongoTemplate.aggregate(aggregation, BirthAchievementCollection.class, BirthAchievement.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}

	@Transactional
	@Override
	public Boolean updateImmunisationChart(String patientId, Long vaccineStartDate) {
		List<VaccineCollection> vaccineCollections = null;
		Boolean status = false;
		vaccineCollections = vaccineRepository.findByPatientId(new ObjectId(patientId));

		for (VaccineCollection vaccineCollection : vaccineCollections) {
			if (vaccineCollection.getPeriodTime() != null && vaccineCollection.getStatus() != VaccineStatus.GIVEN) {
				DateTime dueDate = new DateTime(vaccineStartDate);
				dueDate = dueDate.plusWeeks(vaccineCollection.getPeriodTime());
				vaccineCollection.setDueDate(dueDate.toDate());
			}
		}

		vaccineRepository.saveAll(vaccineCollections);
		status = true;
		return status;
	}

	@Override
	@Transactional
	public List<GrowthChartGraphResponse> getGrowthChartList(String patientId, String updatedTime) {
		List<GrowthChartGraphResponse> responses = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}

			criteria.and("discarded").is(false);

			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					GrowthChartCollection.class, GrowthChartGraphResponse.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}

	@Override
	public String downloadVaccineById(Integer periodTime, String patientId, String doctorId, String locationId,
			String hospitalId) {
		String response = null;
		try {

			UserCollection user = userRepository.findById(new ObjectId(patientId)).orElse(null);
			PatientCollection patient = patientRepository.findByUserIdAndLocationIdAndHospitalId(
					new ObjectId(patientId), new ObjectId(locationId), new ObjectId(hospitalId));

			List<VaccineResponse> vaccineGroupedResponses = getPatientGroupedVaccinesByPeriodTime(patientId,
					periodTime);

			JasperReportResponse jasperReportResponse = createJasper(patient, user, PrintSettingType.DEFAULT.getType(),
					doctorId, locationId, hospitalId, vaccineGroupedResponses);
			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Vaccination PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Vaccination PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(PatientCollection patient, UserCollection user, String printSettingType,
			String doctorId, String locationId, String hospitalId, List<VaccineResponse> vaccineResponses) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd MMM, yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		Boolean showTitle = false;
		PrintSettingsCollection printSettings = null;
		printSettings = printSettingsRepository
				.findByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(new ObjectId(doctorId),
						new ObjectId(locationId), new ObjectId(hospitalId), ComponentType.ALL.getType(),
						printSettingType);
		if (printSettings == null) {
			List<PrintSettingsCollection> printSettingsCollections = printSettingsRepository
					.findListByDoctorIdAndLocationIdAndHospitalIdAndComponentTypeAndPrintSettingType(
							new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId),
							ComponentType.ALL.getType(), PrintSettingType.DEFAULT.getType(),
							new Sort(Sort.Direction.DESC, "updatedTime"));
			if (!DPDoctorUtils.isNullOrEmptyList(printSettingsCollections))
				printSettings = printSettingsCollections.get(0);
		}
		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		showTitle = true;
		parameters.put("VaccinationTitle", "Vaccination List :");
		List<DBObject> dbObjects = new ArrayList<DBObject>();
		for (VaccineResponse vaccineResponse : vaccineResponses) {
			DBObject dbObject = new BasicDBObject();
			if (!DPDoctorUtils.allStringsEmpty(vaccineResponse.getName()))
				dbObject.put("name", vaccineResponse.getName());
			if (!DPDoctorUtils.allStringsEmpty(vaccineResponse.getNote()))
				dbObject.put("note", vaccineResponse.getNote());
			else
				dbObject.put("note", "--");
			if (vaccineResponse.getGivenDate() != null)
				dbObject.put("givenDate", simpleDateFormat.format(vaccineResponse.getGivenDate()));
			else
				dbObject.put("givenDate", "--");

			if (vaccineResponse.getStatus() != null)
				dbObject.put("status", vaccineResponse.getStatus().getStatus());
			if (!DPDoctorUtils.allStringsEmpty(vaccineResponse.getDuration()) && vaccineResponse.getDueDate() != null)
				dbObject.put("duration", "Duration: " + vaccineResponse.getDuration() + " ( " + "  Due Date: "
						+ simpleDateFormat.format(vaccineResponse.getDueDate()) + " ) ");
			parameters.put("duration", "Duration: " + vaccineResponse.getDuration() + " ( " + "  Due Date: "
					+ simpleDateFormat.format(vaccineResponse.getDueDate()) + " ) ");
			if (vaccineResponse.getDueDate() != null)
				dbObject.put("dueDate", simpleDateFormat.format(vaccineResponse.getDueDate()));
			dbObjects.add(dbObject);
		}
		parameters.put("vaccination", dbObjects);
		showTitle = false;
		parameters.put("contentLineSpace",
				(printSettings != null && !DPDoctorUtils.anyStringEmpty(printSettings.getContentLineStyle()))
						? printSettings.getContentLineSpace()
						: LineSpace.SMALL.name());
		generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails()
						: null),
				patient, patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		generatePrintSetup(parameters, printSettings, new ObjectId(doctorId));
		String pdfName = (user != null ? user.getFirstName() : "") + "PEADIATRIC-" + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		try {
			response = jasperReportService.createPDF(ComponentType.VACCINATION, parameters, vaccinationA4FileName,
					layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	public void generatePatientDetails(PatientDetails patientDetails, PatientCollection patientCard, String firstName,
			String mobileNumber, Map<String, Object> parameters, String hospitalUId, Boolean isPidHasDate) {
		String age = null,
				gender = (patientCard != null && patientCard.getGender() != null ? patientCard.getGender() : null),
				patientLeftText = "", patientRightText = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("IST"));

		if (patientDetails == null) {
			patientDetails = new PatientDetails();
		}

		if (patientDetails.getShowPatientDetailsInCertificate() != null
				&& patientDetails.getShowPatientDetailsInCertificate()) {
			List<String> patientDetailList = new ArrayList<String>();
			patientDetailList.add("<b>Patient Name: " + firstName.toUpperCase() + "</b>");

			if (!DPDoctorUtils.anyStringEmpty(patientDetails.getPIDKey())) {
				if (patientDetails.getPIDKey().equalsIgnoreCase("false")) {

					if (isPidHasDate != null && !isPidHasDate)
						patientDetails.setPIDKey("PNUM");
					else
						patientDetails.setPIDKey("UHID");
				}
				if (isPidHasDate != null && !isPidHasDate && !DPDoctorUtils.anyStringEmpty(patientCard.getPNUM()))
					patientDetailList.add("<b>" + patientDetails.getPIDKey() + ": </b>"
							+ (patientCard != null && patientCard.getPNUM() != null ? patientCard.getPNUM() : "--"));

				else if (patientCard != null)
					patientDetailList.add("<b>" + patientDetails.getPIDKey() + ": </b>"
							+ (patientCard.getPID() != null ? patientCard.getPID() : "--"));
			} else {
				if (isPidHasDate != null && !isPidHasDate && !DPDoctorUtils.anyStringEmpty(patientCard.getPNUM()))
					patientDetailList.add("<b>Patient ID: </b>"
							+ (patientCard != null && patientCard.getPNUM() != null ? patientCard.getPNUM() : "--"));
				else
					patientDetailList.add("<b>Patient ID: </b>"
							+ (patientCard != null && patientCard.getPID() != null ? patientCard.getPID() : "--"));
			}

			if (patientCard != null && patientCard.getDob() != null && patientCard.getDob().getAge() != null) {
				Age ageObj = patientCard.getDob().getAge();
				if (ageObj.getYears() > 14)
					age = ageObj.getYears() + "yrs";
				else {
					if (ageObj.getYears() > 0)
						age = ageObj.getYears() + "yrs";
					else {
						if (ageObj.getYears() > 0)
							age = ageObj.getYears() + "yrs";
						if (ageObj.getMonths() > 0) {
							if (DPDoctorUtils.anyStringEmpty(age))
								age = ageObj.getMonths() + "months";
							else
								age = age + " " + ageObj.getMonths() + " months";
						}
						if (ageObj.getDays() > 0) {
							if (DPDoctorUtils.anyStringEmpty(age))
								age = ageObj.getDays() + "days";
							else
								age = age + " " + ageObj.getDays() + "days";
						}
					}
				}

				if (patientDetails.getShowDOB()) {
					if (!DPDoctorUtils.anyStringEmpty(age, gender))
						patientDetailList.add("<b>Age | Gender: </b>" + age + " | " + gender);
					else if (!DPDoctorUtils.anyStringEmpty(age))
						patientDetailList.add("<b>Age | Gender: </b>" + age + " | --");
					else if (!DPDoctorUtils.anyStringEmpty(gender))
						patientDetailList.add("<b>Age | Gender: </b>-- | " + gender);
				}
			}

			if (patientDetails.getShowDOB()) {
				patientDetailList
						.add("<b>Mobile: </b>" + (mobileNumber != null && mobileNumber != null ? mobileNumber : "--"));
			} else {
				patientDetailList
						.add("<b>Mobile: </b>" + (mobileNumber != null && mobileNumber != null ? mobileNumber : "--"));
			}

			if (patientDetails.getShowBloodGroup() && patientCard != null
					&& !DPDoctorUtils.anyStringEmpty(patientCard.getBloodGroup())) {
				patientDetailList.add("<b>Blood Group: </b>" + patientCard.getBloodGroup());
			}
			if (patientDetails.getShowCity() && patientCard != null && !DPDoctorUtils
					.anyStringEmpty(patientCard.getAddress() != null ? patientCard.getAddress().getCity() : null)) {
				patientDetailList.add("<b>City: </b>" + patientCard.getAddress().getCity());
			}
			if (patientDetails.getShowReferedBy() && patientCard != null && patientCard.getReferredBy() != null) {
				ReferencesCollection referencesCollection = referenceRepository.findById(patientCard.getReferredBy())
						.orElse(null);
				if (referencesCollection != null && !DPDoctorUtils.allStringsEmpty(referencesCollection.getReference()))
					patientDetailList.add("<b>Referred By: </b>" + referencesCollection.getReference());

			} else if (parameters.get("referredby") != null)
				patientDetailList.add("<b>Referred By: </b>" + parameters.get("referredby").toString());

			if (patientDetails.getShowHospitalId() != null && patientDetails.getShowHospitalId()
					&& !DPDoctorUtils.anyStringEmpty(hospitalUId)) {
				patientDetailList.add("<b>Hospital Id: </b>" + hospitalUId);
			}

			boolean isBold = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null
					? containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), patientDetails.getStyle().getFontStyle())
					: false;
			boolean isItalic = patientDetails.getStyle() != null && patientDetails.getStyle().getFontStyle() != null
					? containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), patientDetails.getStyle().getFontStyle())
					: false;
			String fontSize = patientDetails.getStyle() != null && patientDetails.getStyle().getFontSize() != null
					? patientDetails.getStyle().getFontSize()
					: "";

			for (int i = 0; i < patientDetailList.size(); i++) {
				String text = patientDetailList.get(i);
				if (!DPDoctorUtils.anyStringEmpty(text)) {
					if (isItalic)
						text = "<i>" + text + "</i>";
					if (isBold)
						text = "<b>" + text + "</b>";
					text = "<span style='font-size:" + fontSize + "'>" + text + "</span>";

					if (i % 2 == 0) {
						if (!DPDoctorUtils.anyStringEmpty(patientLeftText))
							patientLeftText = patientLeftText + "<br>" + text;
						else
							patientLeftText = text;
					} else {
						if (!DPDoctorUtils.anyStringEmpty(patientRightText))
							patientRightText = patientRightText + "<br>" + text;
						else
							patientRightText = text;
					}
				}
			}
			parameters.put("patientLeftText", patientLeftText);
			parameters.put("patientRightText", patientRightText);
		}
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	public void generatePrintSetup(Map<String, Object> parameters, PrintSettingsCollection printSettings,
			ObjectId doctorId) {
		parameters.put("printSettingsId",
				(printSettings != null && printSettings.getId() != null) ? printSettings.getId().toString() : "");
		String headerLeftText = "", headerRightText = "", footerBottomText = "", logoURL = "", footerSignature = "",
				poweredBy = "", bottomSignText = "", footerImageUrl = "", signatureUrl = "", headerImageUrl = "";
		int headerLeftTextLength = 0, headerRightTextLength = 0, footerHeight = 0, headerHeight = 0;
		Integer contentFontSize = 10;
		if (printSettings != null) {
			if (printSettings.getContentSetup() != null) {
				contentFontSize = !DPDoctorUtils.anyStringEmpty(printSettings.getContentSetup().getFontSize())
						? Integer.parseInt(printSettings.getContentSetup().getFontSize().replaceAll("pt", ""))
						: 10;
				if (printSettings.getContentSetup().getInstructionAlign() != null) {
					parameters.put("instructionAlign",
							printSettings.getContentSetup().getInstructionAlign().getAlign());
				} else {
					parameters.put("instructionAlign", FieldAlign.VERTICAL.getAlign());
				}
			}
			if (printSettings.getHeaderSetup() != null && printSettings.getHeaderSetup().getCustomHeader()
					&& !printSettings.getHeaderSetup().getShowHeaderImage()) {
				parameters.put("headerHtml", printSettings.getHeaderSetup().getHeaderHtml());
				if (printSettings.getHeaderSetup().getTopLeftText() != null)
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopLeftText()) {
						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerLeftTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerLeftText.isEmpty())
								headerLeftText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerLeftText = headerLeftText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}
				if (printSettings.getHeaderSetup().getTopRightText() != null
						&& !printSettings.getHeaderSetup().getShowHeaderImage())
					for (PrintSettingsText str : printSettings.getHeaderSetup().getTopRightText()) {

						boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
						boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());

						if (!DPDoctorUtils.anyStringEmpty(str.getText())) {
							headerRightTextLength++;
							String text = str.getText();
							if (isItalic)
								text = "<i>" + text + "</i>";
							if (isBold)
								text = "<b>" + text + "</b>";

							if (headerRightText.isEmpty())
								headerRightText = "<span style='font-size:" + str.getFontSize() + "'>" + text
										+ "</span>";
							else
								headerRightText = headerRightText + "<br/>" + "<span style='font-size:"
										+ str.getFontSize() + "'>" + text + "</span>";
						}
					}
			}

			if (printSettings.getHeaderSetup() != null) {
				if (printSettings.getHeaderSetup().getCustomHeader() && printSettings.getHeaderSetup().getCustomLogo()
						&& printSettings.getClinicLogoUrl() != null
						&& !printSettings.getHeaderSetup().getShowHeaderImage()) {
					logoURL = getFinalImageURL(printSettings.getClinicLogoUrl());
				} else if (!DPDoctorUtils.anyStringEmpty(printSettings.getHeaderSetup().getHeaderImageUrl())
						&& printSettings.getHeaderSetup().getShowHeaderImage()) {
					headerImageUrl = getFinalImageURL(printSettings.getHeaderSetup().getHeaderImageUrl());
					headerHeight = printSettings.getHeaderSetup().getHeaderHeight();
				}
			}

			if (printSettings.getFooterSetup() != null && printSettings.getFooterSetup().getCustomFooter()
					&& printSettings.getFooterSetup().getBottomText() != null
					&& !printSettings.getFooterSetup().getShowImageFooter()) {
				for (PrintSettingsText str : printSettings.getFooterSetup().getBottomText()) {
					boolean isBold = containsIgnoreCase(FONTSTYLE.BOLD.getStyle(), str.getFontStyle());
					boolean isItalic = containsIgnoreCase(FONTSTYLE.ITALIC.getStyle(), str.getFontStyle());
					String text = str.getText();
					if (!DPDoctorUtils.allStringsEmpty(text)) {
						if (isItalic)
							text = "<i>" + text + "</i>";
						if (isBold)
							text = "<b>" + text + "</b>";

						if (footerBottomText.isEmpty())
							footerBottomText = "<span style='font-size:" + str.getFontSize() + "'>" + text + "</span>";
						else
							footerBottomText = footerBottomText + "" + "<span style='font-size:" + str.getFontSize()
									+ "'>" + text + "</span>";
					}
				}
			}

			if (printSettings.getFooterSetup() != null) {
				if (printSettings.getFooterSetup().getShowSignature() && !DPDoctorUtils.anyStringEmpty(doctorId)) {
					UserCollection doctorUser = userRepository.findById(doctorId).orElse(null);
					if (doctorUser != null)
						footerSignature = doctorUser.getTitle() + " " + doctorUser.getFirstName();
				}

//				if (printSettings.getFooterSetup().getShowPoweredBy()) {
//					parameters.put("poweredBy", "<font color='#9d9fa0'>" + footerText + "</font>");
//				}
				if (printSettings.getFooterSetup().getShowBottomSignText()
						&& !DPDoctorUtils.anyStringEmpty(printSettings.getFooterSetup().getBottomSignText())) {
					parameters.put("bottomSignText", printSettings.getFooterSetup().getBottomSignText());
				}
				if (printSettings.getFooterSetup().getShowImageFooter()
						&& printSettings.getFooterSetup().getShowImageFooter()) {
					footerImageUrl = getFinalImageURL(printSettings.getFooterSetup().getFooterImageUrl());
				}
				if (printSettings.getFooterSetup().getShowSignatureBox()) {
					signatureUrl = getFinalImageURL(printSettings.getFooterSetup().getSignatureUrl());
				}
				if (printSettings.getFooterSetup().getShowImageFooter()
						&& !DPDoctorUtils.anyStringEmpty(printSettings.getFooterSetup().getFooterImageUrl())) {
					footerImageUrl = getFinalImageURL(printSettings.getFooterSetup().getFooterImageUrl());
					footerHeight = printSettings.getFooterSetup().getFooterHeight();
				}
				if (printSettings.getFooterSetup().getShowSignatureBox()
						&& !DPDoctorUtils.anyStringEmpty(printSettings.getFooterSetup().getSignatureUrl())) {
					signatureUrl = getFinalImageURL(printSettings.getFooterSetup().getSignatureUrl());
				}
			}

		}
		parameters.put("footerImage", footerImageUrl);
		parameters.put("signatureImage", signatureUrl);
		parameters.put("headerImage", headerImageUrl);
		parameters.put("footerHeight", footerHeight);
		parameters.put("headerHeight", headerHeight);
		parameters.put("footerSignature", footerSignature);
//		parameters.put("poweredBy", poweredBy);
		parameters.put("poweredBy", "<font color='#9d9fa0'>" + footerText + "</font>");
		parameters.put("bottomSignText", bottomSignText);
		parameters.put("contentFontSize", contentFontSize);
		parameters.put("headerLeftText", headerLeftText);
		parameters.put("headerRightText", headerRightText);
		parameters.put("footerBottomText", footerBottomText);
		parameters.put("logoURL", logoURL);
		if (headerLeftTextLength > 2 || headerRightTextLength > 2) {
			parameters.put("showTableOne", true);
		} else {
			parameters.put("showTableOne", false);
		}
	}

}
