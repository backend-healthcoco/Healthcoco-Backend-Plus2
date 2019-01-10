package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.GrowthChart;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Vaccine;
import com.dpdocter.collections.GrowthChartCollection;
import com.dpdocter.collections.MasterBabyImmunizationCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.collections.VaccineBrandAssociationCollection;
import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.VaccineStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.GrowthChartRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.VaccineRepository;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.BabyVaccineReminderResponse;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.MasterVaccineResponse;
import com.dpdocter.response.VaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineResponse;
import com.dpdocter.services.PaediatricService;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PaediatricServiceImpl implements PaediatricService{
	
	@Autowired
	private GrowthChartRepository growthChartRepository;
	
	@Autowired
	private VaccineRepository vaccineRepository;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private SMSServices smsServices;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserDeviceRepository userDeviceRepository;
	
	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;
	
	@Override
	@Transactional
	public GrowthChart addEditGrowthChart(GrowthChart growthChart)
	{
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			if(growthChart.getId() != null)
			{
				growthChartCollection = growthChartRepository.findOne(new ObjectId(growthChart.getId()));
			}
			else
			{
				growthChartCollection = new GrowthChartCollection();
			}
			BeanUtil.map(growthChart, growthChartCollection);
			growthChartCollection = growthChartRepository.save(growthChartCollection);
			if(growthChartCollection != null){
				response = new GrowthChart();
				 BeanUtil.map(growthChartCollection, response);
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
	public GrowthChart getGrowthChartById(String id) {
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
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
					Aggregation.newAggregation(
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
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
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				growthChartCollection.setDiscarded(discarded);
				growthChartRepository.save(growthChartCollection);
				response = true;
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
	public VaccineResponse addEditVaccine(VaccineRequest request)
	{
		 VaccineResponse response = null;
		 VaccineCollection vaccineCollection = null;
		try {
			if(request.getId() != null)
			{
				vaccineCollection = vaccineRepository.findOne(new ObjectId(request.getId()));
			}
			else
			{
				vaccineCollection = new VaccineCollection();
			}
			BeanUtil.map(request, vaccineCollection);
			vaccineCollection.setUpdatedTime(new Date());
			vaccineCollection = vaccineRepository.save(vaccineCollection);
			if(vaccineCollection != null){
				response = new VaccineResponse();
				 BeanUtil.map(vaccineCollection, response);
			}
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
					vaccineCollection = vaccineRepository.findOne(new ObjectId(request.getId()));
				} else {
					vaccineCollection = new VaccineCollection();
				}
				BeanUtil.map(request, vaccineCollection);
				vaccineCollection.setUpdatedTime(new Date());
				if(request.getIsUpdatedByPatient() == true)
				{
					vaccineCollection.setCreatedBy("PATIENT");
				}
				else
				{
					if(request.getDoctorId() != null)
					{
						UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
						if(userCollection != null)
						{
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
					vaccineCollection = vaccineRepository.findOne(new ObjectId(id));
					vaccineCollection.setStatus(request.getStatus());
					vaccineCollection.setUpdatedTime(new Date());
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
			vaccineCollection = vaccineRepository.findOne(new ObjectId(id));
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
	public List<VaccineResponse> getVaccineList(String patientId , String doctorId, String locationId, String hospitalId , String updatedTime) {
		List<VaccineResponse> responses = null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
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
		/*	
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("dueDate", "$vaccineResponses.dueDate"))
							.append("vaccineResponses", new BasicDBObject("$push", "$vaccineResponses")).append("dueDate",
									new BasicDBObject("$first", "$diagnosticTest.dueDate"))));
			
			*/
			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vaccineBrand").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
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
			// Criteria criteria = new Criteria();
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
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						 Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria)
						);
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
									new CustomAggregationOperation(new BasicDBObject("$unwind",
											new BasicDBObject("path", "$vaccineBrand")
													.append("preserveNullAndEmptyArrays", true))),
									Aggregation.lookup("master_baby_immunization_cl", "vaccineId", "_id", "vaccine"),
									new CustomAggregationOperation(new BasicDBObject("$unwind",
											new BasicDBObject("path", "$vaccine").append("preserveNullAndEmptyArrays",
													true))),
									Aggregation.match(criteria),
									Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
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
	public List<GroupedVaccineBrandAssociationResponse> getGroupedVaccineBrandAssociation(List<String> vaccineIds)
	{
		List<ObjectId> vaccineObjectIds = null;
		List<GroupedVaccineBrandAssociationResponse> responses = null;
		try {
			Criteria criteria = new Criteria();
			
			
			if(vaccineIds != null)
			{
				vaccineObjectIds = new ArrayList<>();
				for (String id : vaccineIds) {
					vaccineObjectIds.add(new ObjectId(id));
				}
			}
			
			if (vaccineIds != null) {
				criteria.and("vaccineId").in(vaccineObjectIds);
			}
			
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("name", "$vaccine.name"))
							.append("vaccine", new BasicDBObject("$push", "$vaccine")).append("name",
									new BasicDBObject("$first", "$vaccine.name"))
							.append("id",
									new BasicDBObject("$first", "$vaccine.id"))));
			
			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vaccineBrand").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria),aggregationOperation, Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					VaccineBrandAssociationCollection.class, GroupedVaccineBrandAssociationResponse.class).getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
	}

	@Scheduled(cron = "0 30 6 * * ?", zone = "IST")
	@Override
	public void sendBabyVaccineReminder() {
		// TODO Auto-generated method stub
		List<BabyVaccineReminderResponse> response = null;
		
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		Aggregation aggregation = null;
		AggregationOperation aggregationOperation = null;
		
		Criteria criteria = new Criteria("dueDate").gte(DPDoctorUtils.getStartTime(new Date())).lte(DPDoctorUtils.getEndTime(new Date()));

		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("patientName", "$patient.firstName"),
							Fields.field("mobileNumber", "$patient.mobileNumber"),
							Fields.field("doctorName", "$doctor.firstName"),
							Fields.field("locationName", "$location.locationName"),
							Fields.field("clinicNumber", "$location.clinicNumber"),
							Fields.field("vaccines", "$vaccines")));
			
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
					projectList.and("dueDate").extractDayOfMonth().as("day").and("dueDate").extractMonth()
							.as("month").and("dueDate").extractYear().as("year").and("dueDate").extractWeek()
							.as("week"),
					aggregationOperation);
			
			//System.out.println(aggregation);
			AggregationResults<BabyVaccineReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					VaccineCollection.class, BabyVaccineReminderResponse.class);
			response = aggregationResults.getMappedResults();
			
			
			for (BabyVaccineReminderResponse vaccineReminderResponse : response) {

				if (vaccineReminderResponse.getMobileNumber() != null) {

					String vaccineNames = "";
					String dateTime = "";

					for (Vaccine vaccine : vaccineReminderResponse.getVaccines()) {
						vaccineNames = vaccine.getName() + " ";
					}

					dateTime = formatter.print(vaccineReminderResponse.getVaccines().get(0).getDueDate().getTime());

					String message = "Your vaccination for " + vaccineNames + " is due with "
							+ vaccineReminderResponse.getDoctorName()
							+ (vaccineReminderResponse.getLocationName() != ""
									? ", " + vaccineReminderResponse.getLocationName() : "")
							+ (vaccineReminderResponse.getClinicNumber() != ""
									? ", " + vaccineReminderResponse.getClinicNumber() : "")
							+ " @ " + dateTime + ". Download Healthcoco App- " + patientAppBitLink;
					
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
			
			//System.out.println(" response :: " + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	@Scheduled(cron = "0 30 6 * * ?", zone = "IST")
	@Override
	public void sendBirthBabyVaccineReminder() {
		// TODO Auto-generated method stub
		List<BabyVaccineReminderResponse> response = null;
		DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
		
		Aggregation aggregation = null;
		AggregationOperation aggregationOperation = null;
		DateTime previousdate = DPDoctorUtils.getStartTime(new Date()).minusDays(3);
		
		Criteria criteria = new Criteria("dueDate").gte(previousdate).lt(DPDoctorUtils.getStartTime(new Date()));
		
		criteria.and("status").is(VaccineStatus.PLANNED);
		criteria.and("periodTime").is(0);

		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("patientName", "$patient.firstName"),
							Fields.field("mobileNumber", "$patient.mobileNumber"),
							Fields.field("doctorName", "$doctor.firstName"),
							Fields.field("locationName", "$location.locationName"),
							Fields.field("clinicNumber", "$location.clinicNumber"),
							Fields.field("vaccines", "$vaccines")));
			
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
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
					projectList.and("dueDate").extractDayOfMonth().as("day").and("dueDate").extractMonth()
							.as("month").and("dueDate").extractYear().as("year").and("dueDate").extractWeek()
							.as("week"),
					aggregationOperation);
			
			//System.out.println(aggregation);
			AggregationResults<BabyVaccineReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					VaccineCollection.class, BabyVaccineReminderResponse.class);
			response = aggregationResults.getMappedResults();
			
			
			
			for (BabyVaccineReminderResponse vaccineReminderResponse : response) {

				if (vaccineReminderResponse.getMobileNumber() != null) {

					String vaccineNames = "";
					String dateTime = "";

					for (Vaccine vaccine : vaccineReminderResponse.getVaccines()) {
						vaccineNames = vaccine.getName() + " ";
					}

					dateTime = formatter.print(vaccineReminderResponse.getVaccines().get(0).getDueDate().getTime());

					String message = "Your vaccination for " + vaccineNames + " is due with "
							+ vaccineReminderResponse.getDoctorName()
							+ (vaccineReminderResponse.getLocationName() != ""
									? ", " + vaccineReminderResponse.getLocationName() : "")
							+ (vaccineReminderResponse.getClinicNumber() != ""
									? ", " + vaccineReminderResponse.getClinicNumber() : "")
							+ " @ " + dateTime + ". Download Healthcoco App- " + patientAppBitLink;
					;
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
			
			
			//System.out.println(" response :: " + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
/*
	@Scheduled(cron = "0 30 6 * * ?", zone = "IST")
	//@Scheduled(fixedDelay = 15000)
	@Transactional
	@Override
	public void sendBabyVaccineReminder() {
		System.out.println("IN baby vaccine reminder scheduler");
		List<BabyVaccineReminderResponse> response = null;

		Aggregation aggregation = null;
		AggregationOperation aggregationOperation = null;
		
		Criteria criteria = new Criteria("dueDate").gte(DPDoctorUtils.getStartTime(new Date())).lte(DPDoctorUtils.getEndTime(new Date()));

		try {
			ProjectionOperation projectList = new ProjectionOperation(
					Fields.from(Fields.field("patientName", "$patient.firstName"),
							Fields.field("doctorName", "$doctor.firstName"),
							Fields.field("locationName", "$location.locationName"),
							Fields.field("vaccines", "$vaccines")));
			
			aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id",
							new BasicDBObject("day", "$day").append("month", "$month").append("year", "$year")
									.append("patientId", "$patientId"))
											.append("doctorName", new BasicDBObject("$first", "$doctorName"))
											.append("locationName", new BasicDBObject("$first", "$locationName"))
											.append("patientName", new BasicDBObject("$first", "$patientName"))
											.append("vaccines", new BasicDBObject("$push", "$vaccines"))));
			
			
			aggregation = Aggregation.newAggregation(Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
					Aggregation.unwind("doctor"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
					Aggregation.unwind("patient"), Aggregation.lookup("location_cl", "locationId", "_id", "location"),
					Aggregation.unwind("location"), Aggregation.lookup("vaccine_cl", "_id", "_id", "vaccines"),
					Aggregation.unwind("vaccines"), Aggregation.match(criteria),
					projectList.and("dueDate").extractDayOfMonth().as("day").and("dueDate").extractMonth()
							.as("month").and("dueDate").extractYear().as("year").and("dueDate").extractWeek()
							.as("week"),
					aggregationOperation);
			
			//System.out.println(aggregation);
			AggregationResults<BabyVaccineReminderResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					VaccineCollection.class, BabyVaccineReminderResponse.class);
			response = aggregationResults.getMappedResults();
			
			//System.out.println(" response :: " + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

>>>>>>> Stashed changes*/
	
	@Async
	@Transactional
	private void sendVaccinationMessage(String userId)
	{
		UserDeviceCollection userDeviceCollection =userDeviceRepository.findByDeviceId(userId);
		if(userDeviceCollection != null)
		{
			UserCollection userCollection = userRepository.findOne(new ObjectId(userId));
			if (userCollection != null && userCollection.getMobileNumber() != null) {

			

				String message = "Your vaccines can now be tracked on Healthcoco App. Download Healthcoco App- " + patientAppBitLink;
				
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
}
