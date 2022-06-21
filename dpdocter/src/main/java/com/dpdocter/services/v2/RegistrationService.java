package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.response.v2.ClinicDoctorResponse;

public interface RegistrationService {

	List<ClinicDoctorResponse> getUsers(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, String role, Boolean active, String userState);

}
