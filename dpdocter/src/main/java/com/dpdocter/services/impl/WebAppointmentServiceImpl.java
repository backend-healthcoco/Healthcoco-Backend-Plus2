package com.dpdocter.services.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Slot;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.repository.ESSpecialityRepository;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebClinicResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.WebAppointmentService;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;

@Service
public class WebAppointmentServiceImpl implements WebAppointmentService {

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

	@Override
	public WebDoctorClinicsResponse getClinicsByDoctorSlugURL(String doctorSlugUrl) {
		WebDoctorClinicsResponse webDoctorClinicsResponse = null;
		try {

			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.matchQuery("doctorSlugUrl", doctorSlugUrl));

			List<ESDoctorDocument> esDoctorDocuments = elasticsearchTemplate.queryForList(
					new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESDoctorDocument.class);

			if (esDoctorDocuments != null) {
				webDoctorClinicsResponse = new WebDoctorClinicsResponse();
				List<WebClinicResponse> clinicResponses = new ArrayList<WebClinicResponse>();
				for (ESDoctorDocument doctorDocument : esDoctorDocuments) {
					if (webDoctorClinicsResponse.getDoctorId() == null) {
						webDoctorClinicsResponse.setDoctorId(doctorDocument.getUserId());
						webDoctorClinicsResponse.setDoctorSlugURL(doctorDocument.getDoctorSlugURL());
						webDoctorClinicsResponse
								.setFirstName(doctorDocument.getTitle() + " " + doctorDocument.getFirstName());
						if (doctorDocument.getSpecialities() != null) {
							HashSet<String> specialities = new HashSet<String>();
							HashSet<String> parentspecialities = new HashSet<String>();
							for (String specialityId : doctorDocument.getSpecialities()) {
								ESSpecialityDocument specialityCollection = esSpecialityRepository
										.findOne(specialityId);
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
					clinicResponse.setCity(doctorDocument.getCity());
					clinicResponse.setCountry(doctorDocument.getCountry());
					clinicResponse.setLocality(doctorDocument.getLocality());
					clinicResponse.setLocationId(doctorDocument.getLocationId());
					clinicResponse.setLocationName(doctorDocument.getLocationName());
					clinicResponse.setPostalCode(doctorDocument.getPostalCode());
					clinicResponse.setState(doctorDocument.getState());
					clinicResponse.setStreetAddress(doctorDocument.getStreetAddress());
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
	public SlotDataResponse getTimeSlots(String doctorId, String locationId, String date) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		SlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				Integer startTime = 0, endTime = 0;
				float slotTime = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				String day = sdf.format(date);
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
					response = new SlotDataResponse();
					response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
					slotResponse = new ArrayList<Slot>();
					List<WorkingHours> workingHours = null;
					for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
						if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
							workingHours = workingSchedule.getWorkingHours();
						}
					}
					if (workingHours != null && !workingHours.isEmpty()) {
						Date dateObj = new Date(Long.parseLong(date));
						Calendar localCalendar = Calendar
								.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
						localCalendar.setTime(dateObj);
						int dayOfDate = localCalendar.get(Calendar.DATE);
						int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
						int yearOfDate = localCalendar.get(Calendar.YEAR);

						DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));

						localCalendar.add(Calendar.DAY_OF_MONTH, 6);
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

						if (appointmentService.checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
								doctorClinicProfileCollection.getTimeZone()))
							for (Slot slot : slotResponse) {
								if (slot.getMinutesOfDay() < appointmentService.getMinutesOfDay(dateObj)) {
									slot.setIsAvailable(false);
									slotResponse.set(slotResponse.indexOf(slot), slot);
								}
							}
					}
					response.setSlots(slotResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

}
