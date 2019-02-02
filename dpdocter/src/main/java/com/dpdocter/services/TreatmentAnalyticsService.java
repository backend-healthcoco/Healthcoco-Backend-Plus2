package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.TreatmentAnalyticDetail;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;

public interface TreatmentAnalyticsService {
		
	public List<AnalyticResponse> getTreatmentAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm);

	Integer countTreatmentService(String doctorId, String locationId, String hospitalId, String fromDate, String toDate,
			String searchTerm);

	List<TreatmentService> getTreatmentsAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, int page, int size, String searchTerm);

	List<DoctorTreatmentAnalyticResponse> getMostTreatmentAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm);

	List<TreatmentAnalyticDetail> getTreatmentAnalyticDetail(int page, int size, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm);

	List<DoctorAnalyticPieChartResponse> getTreatmentAnalyticForPieChart(String locationId, String hospitalId,
			String fromDate, String toDate);

	Integer countTreatments(String doctorId, String locationId, String hospitalId, String fromDate, String toDate,String searchTerm);

}
