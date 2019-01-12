package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentAnalyticData;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticPieChartResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;

public interface AppointmentAnalyticsService {
	public AppointmentAnalyticResponse getAppointmentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate,  String searchTerm, int page,
			int size);

	public List<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticsData(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String searchType, String searchTerm,
			int page, int size);

	public List<AnalyticResponse> getAppointmentAnalytics(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, String queryType, String searchType, String searchTerm,
			int page, int size);

	public DoctorAppointmentAnalyticResponse getAppointmentAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate);

	public List<AppointmentAnalyticGroupWiseResponse> getAppointmentAnalyticPatientGroup(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String state, int page, int size);

	public Integer countAppointmentAnalyticPatientGroup(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state);

	public List<AppointmentAnalyticData> getPatientAppointmentAnalyticsDetail(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state, String searchTerm, int page, int size);

	public Integer countPatientAppointmentAnalyticsDetail(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate, String state, String searchTerm);

	List<DoctorAppointmentAnalyticPieChartResponse> getDoctorAppointmentAnalyticsForPieChart(String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String state, String searchTerm,
			int page, int size);

}
