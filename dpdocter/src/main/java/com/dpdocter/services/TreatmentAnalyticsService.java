package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.TreatmentService;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;

public interface TreatmentAnalyticsService {
	public List<DoctorTreatmentAnalyticResponse> getTreatmentAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm);

	public List<TreatmentService> getTreatmentsAnalyticsData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, int page, int size);
	
	public List<AnalyticResponse> getTreatmentAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm);

}
