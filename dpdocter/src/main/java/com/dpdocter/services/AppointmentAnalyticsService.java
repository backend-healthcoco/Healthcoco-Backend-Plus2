package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentAnalyticData;
import com.dpdocter.beans.OnlineConsultationAnalytics;
import com.dpdocter.beans.PaymentSettlements;
import com.dpdocter.beans.PaymentSummary;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentBookedByCountResponse;
import com.dpdocter.response.BookedAndCancelAppointmentCount;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
import com.dpdocter.response.ScheduleAndCheckoutCount;

public interface AppointmentAnalyticsService {
	public AppointmentAnalyticResponse getAppointmentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String searchTerm, int page, int size);

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

	List<DoctorAnalyticPieChartResponse> getDoctorAppointmentAnalyticsForPieChart(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state, String searchTerm, int page, int size);

	public ScheduleAndCheckoutCount getScheduledAndCheckoutCount(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate);

	public AppointmentBookedByCountResponse getAppointmentBookedByCount(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String state);

	public BookedAndCancelAppointmentCount getBookedAndCancelledCount(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate);
	
OnlineConsultationAnalytics getConsultationAnalytics(String fromDate,String toDate,String doctorId,String locationId,String type);

	
	List<PaymentSummary> getPaymentSummary(String fromDate,String toDate,String doctorId,int page,int size );


	List<PaymentSettlements> fetchSettlement(String from, int count);

}
