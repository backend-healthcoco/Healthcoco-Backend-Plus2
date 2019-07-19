package com.dpdocter.services;

import com.dpdocter.response.WebAppointmentSlotDataResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;

public interface WebAppointmentService {

	WebAppointmentSlotDataResponse getTimeSlots(String doctorId, String locationId, String hospitalId, String date);

	WebDoctorClinicsResponse getClinicsByDoctorSlugURL(String doctorSlugUrl);
}
