package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Slot;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebAppointmentSlotDataResponse;
import com.dpdocter.response.WebClinicResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.WebAppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;
@Service
public class WebAppointmentServiceImpl implements WebAppointmentService{


	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private ESSpecialityRepository esSpecialityRepository;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Value(value = "${image.path}")
	private String imagePath;
	
	@Override
	public WebDoctorClinicsResponse getClinicsByDoctorSlugURL(String doctorSlugUrl) {
		WebDoctorClinicsResponse webDoctorClinicsResponse = null;
		try {
			Criteria criteria = new Criteria("doctorSlugURL").is(doctorSlugUrl);

			List<DoctorClinicProfileLookupResponse> clinicProfileCollections = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.lookup("location_cl", "locationId", "_id", "location"),
							Aggregation.unwind("location"), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"), Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
							Aggregation.unwind("doctor")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();

			if (clinicProfileCollections != null) {
				webDoctorClinicsResponse = new WebDoctorClinicsResponse();
				List<WebClinicResponse> clinicResponses = new ArrayList<WebClinicResponse>();
				for (DoctorClinicProfileLookupResponse doctorDocument : clinicProfileCollections) {
					if (webDoctorClinicsResponse.getDoctorId() == null) {
						webDoctorClinicsResponse.setDoctorId(doctorDocument.getDoctorId().toString());
						webDoctorClinicsResponse.setDoctorSlugURL(doctorDocument.getDoctorSlugURL());
						webDoctorClinicsResponse.setFirstName(
								doctorDocument.getUser().getTitle() + " " + doctorDocument.getUser().getFirstName());
						webDoctorClinicsResponse.setExperience(doctorDocument.getDoctor().getExperience());
						webDoctorClinicsResponse
								.setThumbnailUrl(getFinalImageURL(doctorDocument.getUser().getThumbnailUrl()));

						if (doctorDocument.getDoctor().getSpecialities() != null) {
							HashSet<String> specialities = new HashSet<String>();
							HashSet<String> parentspecialities = new HashSet<String>();
							for (ObjectId specialityId : doctorDocument.getDoctor().getSpecialities()) {
								ESSpecialityDocument specialityCollection = esSpecialityRepository
										.findOne(specialityId.toString());
								if (specialityCollection != null) {
									specialities.add(specialityCollection.getSuperSpeciality());
									parentspecialities.add(specialityCollection.getSpeciality());
								}
							}
							webDoctorClinicsResponse.setSpecialities(new ArrayList<>(specialities));
							webDoctorClinicsResponse.setParentSpecialities(new ArrayList<>(parentspecialities));
						}
					}
					WebClinicResponse clinicResponse = new WebClinicResponse();
					clinicResponse.setCity(doctorDocument.getLocation().getCity());
					clinicResponse.setCountry(doctorDocument.getLocation().getCountry());
					clinicResponse.setLocality(doctorDocument.getLocation().getLocality());
					clinicResponse.setLocationId(doctorDocument.getLocation().getId().toString());
					clinicResponse.setHospitalId(doctorDocument.getLocation().getHospitalId().toString());
					clinicResponse.setLocationName(doctorDocument.getLocation().getLocationName());
					clinicResponse.setPostalCode(doctorDocument.getLocation().getPostalCode());
					clinicResponse.setState(doctorDocument.getLocation().getState());
					clinicResponse.setStreetAddress(doctorDocument.getLocation().getStreetAddress());
					clinicResponse.setConsultationFee(doctorDocument.getConsultationFee());
					clinicResponse.setFacility(doctorDocument.getFacility());
					clinicResponses.add(clinicResponse);
				}
				webDoctorClinicsResponse.setClinics(clinicResponses);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return webDoctorClinicsResponse;
	}

	@Override
	public WebAppointmentSlotDataResponse getTimeSlots(String doctorId, String locationId, String hospitalId, String date) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		List<SlotDataResponse> slotDataResponses = null;
		WebAppointmentSlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			Date dateObj = new Date(Long.parseLong(date));

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				Integer startTime = 0, endTime = 0;
				float slotTime = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));

				Calendar localCalendar = Calendar
						.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				localCalendar.setTime(dateObj);

				response = new WebAppointmentSlotDataResponse();
				response.setDoctorId(doctorId);
				response.setLocationId(locationId);
				response.setDoctorSlugURL(doctorClinicProfileCollection.getDoctorSlugURL());
				response.setHospitalId(hospitalId);
				
				slotDataResponses = new ArrayList<SlotDataResponse>();
				if (doctorClinicProfileCollection.getWorkingSchedules() != null && doctorClinicProfileCollection.getAppointmentSlot() != null) {
					for(int j=0;j<6;j++) {
						String day = sdf.format(localCalendar.getTime());
						slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
						
						SlotDataResponse slotDataResponse = new SlotDataResponse();
						slotDataResponse.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
						slotDataResponse.setDate(localCalendar.getTime().getTime());
						
						slotResponse = new ArrayList<Slot>();
						List<WorkingHours> workingHours = null;
						for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
							if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
								workingHours = workingSchedule.getWorkingHours();
							}
						}
						if (workingHours != null && !workingHours.isEmpty()) {
							int dayOfDate = localCalendar.get(Calendar.DATE);
							int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
							int yearOfDate = localCalendar.get(Calendar.YEAR);

							DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
									.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));

							DateTime end = new DateTime(localCalendar.get(Calendar.YEAR),
									localCalendar.get(Calendar.MONTH) + 1, localCalendar.get(Calendar.DATE), 23, 59, 59,
									DateTimeZone.forTimeZone(
											TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));

							List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository
									.findByDoctorLocationId(doctorObjectId, locationObjectId, start, end,
											new Sort(Direction.ASC, "time.fromTime"));
							int i = 0;
							for (WorkingHours hours : workingHours) {
								startTime = hours.getFromTime();
								endTime = hours.getToTime();

								if(startTime != null && endTime != null) {
									if (bookedSlots != null && !bookedSlots.isEmpty()) {
										while (i < bookedSlots.size()) {
											AppointmentBookedSlotCollection bookedSlot = bookedSlots.get(i);
											if (endTime > startTime) {
												if (bookedSlot.getTime().getFromTime() >= startTime
														|| bookedSlot.getTime().getToTime() >= endTime) {
													if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
														if (bookedSlot.getIsAllDayEvent()) {
															if (bookedSlot.getFromDate().equals(date))
																bookedSlot.getTime().setToTime(719);
															if (bookedSlot.getToDate().equals(date))
																bookedSlot.getTime().setFromTime(0);
														}
													}
													List<Slot> slots = DateAndTimeUtility.sliceTime(startTime,
															bookedSlot.getTime().getFromTime(), Math.round(slotTime), true);
													if (slots != null)
														slotResponse.addAll(slots);

													slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(),
															bookedSlot.getTime().getToTime(), Math.round(slotTime), false);
													if (slots != null)
														slotResponse.addAll(slots);
													startTime = bookedSlot.getTime().getToTime();
													i++;
												} else {
													i++;
													break;
												}
											} else {
												i++;
												break;
											}
										}
									}

									if (endTime > startTime) {
										List<Slot> slots = DateAndTimeUtility.sliceTime(startTime, endTime,
												Math.round(slotTime), true);
										if (slots != null)
											slotResponse.addAll(slots);
									}
								}
							}

							if (appointmentService.checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
									doctorClinicProfileCollection.getTimeZone()))
								for (Slot slot : slotResponse) {
									if (slot.getMinutesOfDay() < appointmentService.getMinutesOfDay(dateObj)) {
										slot.setIsAvailable(false);
										slotResponse.set(slotResponse.indexOf(slot), slot);
									}
								}
							slotDataResponse.setSlots(slotResponse);
						}
					slotDataResponses.add(slotDataResponse);
					localCalendar.add(Calendar.DATE, 1);
					}
				response.setSlots(slotDataResponses);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
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
