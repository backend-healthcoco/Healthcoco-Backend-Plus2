package com.dpdocter.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dpdocter.beans.TreatmentService;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.DoctorPatientAnalyticResponse;
import com.dpdocter.response.DoctorPrescriptionItemAnalyticResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.DoctorprescriptionAnalyticResponse;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;

@Service
public interface AnalyticsService {

	public DoctorprescriptionAnalyticResponse getPrescriptionAnalytic(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate);
	
	public List<?> getMostPrescribedPrescriptionItems(String type, String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<InvoiceAnalyticsDataDetailResponse> getIncomeDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<IncomeAnalyticsDataResponse> getIncomeAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<PaymentDetailsAnalyticsDataResponse> getPaymentDetailsAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<PaymentAnalyticsDataResponse> getPaymentAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	public List<AmountDueAnalyticsDataResponse> getAmountDueAnalyticsData(String doctorId, String locationId,
			String hospitalId, String fromDate, String toDate, String queryType, String searchType, int page, int size);

	
	public DoctorPatientAnalyticResponse getPatientAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate);

	public List<DoctorPrescriptionItemAnalyticResponse> getPrescriptionItemAnalytic(int page, int size, String doctorId,
			String locationId, String hospitalId, String fromDate, String toDate, String type, String searchTerm);

	public DoctorVisitAnalyticResponse getVisitAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate);

	List<ExpenseCountResponse> getDoctorExpenseAnalytic(String doctorId, String searchType, String locationId,
			String hospitalId, Boolean discarded, String fromDate, String toDate, String expenseType,
			String paymentMode);

}