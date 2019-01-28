package com.dpdocter.services;

import java.util.List;

<<<<<<< Updated upstream
import com.dpdocter.beans.TreatmentAnalyticDetail;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;

public interface TreatmentAnalyticsService {
	public List<DoctorTreatmentAnalyticResponse> getMostTreatmentAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm);

	public List<TreatmentService> getTreatmentsAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, int page, int size,String searchTerm);

	public List<AnalyticResponse> getTreatmentAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm);

	public List<TreatmentAnalyticDetail> getTreatmentAnalyticDetail(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm);

	public List<DoctorAnalyticPieChartResponse> getTreatmentAnalyticForPieChart(String locationId, String hospitalId,
			String fromDate, String toDate);

	public Integer countTreatments(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate);

	public Integer countTreatmentService(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchTerm);

=======
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

>>>>>>> Stashed changes
}
