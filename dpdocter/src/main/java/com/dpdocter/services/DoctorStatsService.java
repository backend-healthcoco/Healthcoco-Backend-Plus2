package com.dpdocter.services;

import com.dpdocter.response.DoctorStatisticsResponse;

public interface DoctorStatsService {

	DoctorStatisticsResponse getDoctorStats(String doctorId, String locationId, String type);
	
}
