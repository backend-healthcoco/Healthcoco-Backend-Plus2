package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PrescriptionAnalyticDetail;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorPrescriptionAnalyticResponse;
import com.dpdocter.response.DoctorPrescriptionItemAnalyticResponse;

public interface PrescriptionAnalyticsService {

	public DoctorPrescriptionAnalyticResponse getPrescriptionAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate);

	public List<?> getMostPrescripedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<DoctorPrescriptionItemAnalyticResponse> getPrescriptionItemAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String type, String searchTerm);

	public List<DoctorAnalyticPieChartResponse> getPrescriptionAnalyticForPieChart(String doctorId,String locationId, String hospitalId,
			String fromDate, String toDate);

	public Integer countPrescription(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String searchTerm);

	public List<AnalyticResponse> getPrescriptionAnalyticData(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String searchType, String searchTerm);

	public List<PrescriptionAnalyticDetail> getPrescriptionAnalyticDetail(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchTerm);

	public Integer countPrescripedItems(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String type);

	public Integer countPrescriptionItemAnalytic(String doctorId, String locationId, String hospitalId, String fromDate,
			String toDate, String type, String searchTerm);

}
