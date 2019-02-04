package com.dpdocter.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;

@Service
public interface AnalyticsService {


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

	public DoctorVisitAnalyticResponse getVisitAnalytic(String doctorId, String locationId, String hospitalId,
			String fromDate, String toDate);

	List<ExpenseCountResponse> getDoctorExpenseAnalytic(String doctorId, String searchType, String locationId,
			String hospitalId, Boolean discarded, String fromDate, String toDate, String expenseType,
			String paymentMode);

}