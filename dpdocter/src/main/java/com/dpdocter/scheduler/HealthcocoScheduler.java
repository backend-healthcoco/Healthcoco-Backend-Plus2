//package com.dpdocter.scheduler;
//
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.TimeZone;
//
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.aggregation.Aggregation;
//import org.springframework.data.mongodb.core.aggregation.AggregationResults;
//import org.springframework.data.mongodb.core.aggregation.Fields;
//import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.dpdocter.beans.SMS;
//import com.dpdocter.beans.SMSAddress;
//import com.dpdocter.beans.SMSDetail;
//import com.dpdocter.collections.DoctorClinicProfileCollection;
//import com.dpdocter.collections.LocationCollection;
//import com.dpdocter.collections.SMSTrackDetail;
//import com.dpdocter.collections.UserCollection;
//import com.dpdocter.enums.RegularCheckUpTypeEnum;
//import com.dpdocter.enums.SMSStatus;
//import com.dpdocter.repository.DoctorRepository;
//import com.dpdocter.repository.LocationRepository;
//import com.dpdocter.repository.UserRepository;
//import com.dpdocter.response.RegularCheckupResponse;
//import com.dpdocter.response.StartEndTImeinDateTime;
//import com.dpdocter.services.SMSServices;
//
//import common.util.web.DateUtil;
//
//@Component
//public class HealthcocoScheduler {
//
//	
//	@Autowired
//	MongoTemplate mongoTemplate;
//	
//	@Autowired 
//	UserRepository userRepository;
//	
//	@Autowired
//	SMSServices smsServices;
//	
//	@Autowired
//	LocationRepository locationRepository;
//	
//	@Autowired
//	DoctorRepository doctorRepository;
//	
//	@Scheduled(cron = "0 0 8 * * ?")
//	private void sendRegularCheckUpSMSByVisit()
//	{
//		List<RegularCheckupResponse > response = new ArrayList<>();
//		List<RegularCheckupResponse> regularCheckupResponses = new ArrayList<>();
//		
//		for(int months = 1; months <= 12 ; months ++)
//		{
//			regularCheckupResponses = getRegularCheckupListbyLastVisitDate(months);
//			response.addAll(regularCheckupResponses);
//		}
//		
//		if (regularCheckupResponses.size() > 0)
//			for (RegularCheckupResponse checkupResponse : regularCheckupResponses) {
//				if (checkupResponse.getLocationId().toString()
//						.equals(checkupResponse.getPatient().getLocationId())) {
//
//					UserCollection userCollection = userRepository
//							.findById(new ObjectId(checkupResponse.getPatient().getUserId())).orElse(null);
//					
//					UserCollection doctorCollection = userRepository.findById(checkupResponse.getDoctorId()).orElse(null);
//					
//					LocationCollection locationCollection = locationRepository.findById(checkupResponse.getLocationId()).orElse(null);				
//					
//					
//					String message = "You have regular check-up scheduled with Dr." + doctorCollection.getFirstName()+ " at " + locationCollection.getLocationName();
//					SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
//					smsTrackDetail.setDoctorId(checkupResponse.getDoctorId());
//					smsTrackDetail.setLocationId(checkupResponse.getLocationId());
//					smsTrackDetail.setHospitalId(checkupResponse.getHospitalId());
//					smsTrackDetail.setType("REGULAR CHECKUP BY VISIT");
//					SMSDetail smsDetail = new SMSDetail();
//					smsDetail.setUserId(userCollection.getId());
//					SMS sms = new SMS();
//					smsDetail.setUserName(checkupResponse.getLocalPatientName());
//					sms.setSmsText(message);
//					/*sms.setSmsText(message.replace("{patientName}", checkupResponse.getLocalPatientName())
//							.replace("{clinicName}", birthdaySMSDetailsForPatient.getLocationName()));*/
//
//					SMSAddress smsAddress = new SMSAddress();
//					smsAddress.setRecipient(userCollection.getMobileNumber());
//					sms.setSmsAddress(smsAddress);
//
//					smsDetail.setSms(sms);
//					smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
//					List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
//					smsDetails.add(smsDetail);
//					smsTrackDetail.setSmsDetails(smsDetails);
//					smsServices.sendSMS(smsTrackDetail, true);
//
//				}
//			}
//		
//		
//	}
//	
//	
//	
//	//@Scheduled(fixedDelay = 15000)
//	@Transactional
//	public List<RegularCheckupResponse> getRegularCheckupListbyLastVisitDate(int months)
//	{
//		//Calendar c = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		//int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//		int days = -(months * 30);
//		
//		StartEndTImeinDateTime dateTime = DateUtil.getDateStartEndTimeinDateTime(TimeZone.getTimeZone("IST"), days);
//		ProjectionOperation projectList = new ProjectionOperation(Fields.from(
//				Fields.field("doctorId", "$patient.doctorId"), Fields.field("locationId", "$locationId"),
//				Fields.field("hospitalId", "$location.hospitalId"),
//				Fields.field("locationName", "$location.locationName"), Fields.field("createdTime", "$createdTime"),
//				Fields.field("patient", "$patient"),
//				Fields.field("localPatientName", "$patient.localPatientName"),
//				Fields.field("visit" , "$visit")));
//		Criteria criteria = new Criteria("discarded").is(false).andOperator( new Criteria("isActivate").is(true),new Criteria("isSendRegularCheckupSMS").is(true),
//				new Criteria("regularCheckUpMonths").is(months),new Criteria("checkUpTypeEnum").is(RegularCheckUpTypeEnum.VISIT.getType()),new Criteria("patient.discarded").is(false), new Criteria("doctorId").ne(null),
//				new Criteria("locationId").ne(null), new Criteria("visit.visitedTime").gte(dateTime.getStartTime()).lte(dateTime.getEndTime()));
//
//		Aggregation aggregation = Aggregation.newAggregation(
//				Aggregation.lookup("location_cl", "locationId", "_id", "location"),Aggregation.unwind("location"),
//				Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patient"), Aggregation.unwind("patient"),
//				Aggregation.lookup("patient_visit_cl", "doctorId", "doctorId", "visit"), Aggregation.unwind("visit"),
//				Aggregation.match(criteria), projectList, Aggregation.sort(Sort.Direction.DESC, "createdTime"));
//		
//		AggregationResults<RegularCheckupResponse> results = mongoTemplate.aggregate(aggregation,
//		DoctorClinicProfileCollection.class, RegularCheckupResponse.class);
//		
//		List<RegularCheckupResponse> checkupResponses = new ArrayList<RegularCheckupResponse>();
//		checkupResponses = results.getMappedResults();
//		return checkupResponses;
//		
//	}
//	
//	
//	//@Scheduled(fixedDelay = 15000000)
//	@Transactional
//	public List<RegularCheckupResponse> getRegularCheckupListbyRegistrationDate( int months)
//	{
//		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		/*int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//		int days = -(months * monthMaxDays);*/
//		
//		List<Integer> days = new ArrayList<>();
//		int today = calendar.get(Calendar.DAY_OF_MONTH);
//		if(calendar.get(Calendar.MONTH) == Calendar.APRIL  || calendar.get(Calendar.MONTH) == Calendar.NOVEMBER || calendar.get(Calendar.MONTH) == Calendar.JUNE || calendar.get(Calendar.MONTH) == Calendar.SEPTEMBER)
//		{
//			if(today == 30)
//			{
//				days.add(today);
//				days.add(today++);
//			}
//			else
//			{
//				days.add(today);
//			}
//		}
//		else if(calendar.get(Calendar.MONTH) == Calendar.FEBRUARY)
//		{
//			int daysInYear = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
//			
//			if (daysInYear > 365) {
//				if (today == 29) {
//					days.add(today);
//					days.add(today + 1);
//					days.add(today + 2);
//				} else {
//					days.add(today);
//				}
//			} else {
//				if (today == 28) {
//					days.add(today);
//					days.add(today + 1);
//					days.add(today + 2);
//					days.add(today + 3);
//				} else {
//					days.add(today);
//				}
//			}
//		}
//		else
//		{
//			days.add(today);
//		}
//		//StartEndTImeinDateTime dateTime = DateUtil.getDateStartEndTimeinDateTime(TimeZone.getTimeZone("IST"), days);
//		ProjectionOperation projectList = new ProjectionOperation(Fields.from(
//				Fields.field("doctorId", "$patient.doctorId"), Fields.field("locationId", "$locationId"),
//				Fields.field("hospitalId", "$location.hospitalId"),
//				Fields.field("locationName", "$location.locationName"), Fields.field("createdTime", "$createdTime"),
//				Fields.field("patient", "$patient"),
//				Fields.field("discarded", "$discarded"),
//				Fields.field("isActivate", "$isActivate"),
//				Fields.field("localPatientName", "$patient.localPatientName"))).andExpression("dayOfMonth(patient.registrationDate)").as("day");
//		Criteria criteria = new Criteria("discarded").is(false).andOperator( new Criteria("isActivate").is(true),new Criteria("isSendRegularCheckupSMS").is(true),
//				new Criteria("regularCheckUpMonths").is(months),new Criteria("checkUpTypeEnum").is(RegularCheckUpTypeEnum.REGISTRATION.getType()),new Criteria("patient.discarded").is(false), new Criteria("doctorId").ne(null),
//				new Criteria("locationId").ne(null), new Criteria("day").in(days));
//
//		Aggregation aggregation = Aggregation.newAggregation(
//				Aggregation.lookup("location_cl", "locationId", "_id", "location"),Aggregation.unwind("location"),
//				Aggregation.lookup("patient_cl", "doctorId", "doctorId", "patient"), Aggregation.unwind("patient"),
//				 projectList, Aggregation.match(criteria),Aggregation.sort(Sort.Direction.DESC, "createdTime"));
//		AggregationResults<RegularCheckupResponse> results = mongoTemplate.aggregate(aggregation,
//		DoctorClinicProfileCollection.class, RegularCheckupResponse.class);
//		
//		
//		List<RegularCheckupResponse> checkupResponses = new ArrayList<RegularCheckupResponse>();
//		checkupResponses = results.getMappedResults();
//		return checkupResponses;
//		
//	}
//	
//	public static void main(String[] args) {
//		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		/*int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//		int days = -(months * monthMaxDays);*/
//		
//		int daysInYear = c.getActualMaximum(Calendar.DAY_OF_YEAR);
//		
//		int today = c.get(Calendar.DAY_OF_MONTH);
//	}
//	
//}
