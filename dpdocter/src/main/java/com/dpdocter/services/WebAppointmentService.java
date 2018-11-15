package com.dpdocter.services;

import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.WebDoctorClinicsResponse;

public interface WebAppointmentService {

	WebDoctorClinicsResponse getClinicsByDoctorSlugURL(String doctorSlugUrl);

	SlotDataResponse getTimeSlots(String doctorId, String locationId, String date);

}
