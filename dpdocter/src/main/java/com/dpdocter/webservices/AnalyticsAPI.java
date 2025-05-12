package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AppointmentAnalyticData;
import com.dpdocter.beans.OnlineConsultationAnalytics;
import com.dpdocter.beans.OnlineConsultationSettlement;
import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.beans.PatientPaymentDetails;
import com.dpdocter.beans.PaymentSettlements;
import com.dpdocter.beans.PaymentSummary;
import com.dpdocter.beans.PrescriptionAnalyticDetail;
import com.dpdocter.beans.TreatmentAnalyticDetail;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.elasticsearch.response.PatientReferredByAnalyticData;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AllAnalyticResponse;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.AnalyticCountResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentBookedByCountResponse;
import com.dpdocter.response.BookedAndCancelAppointmentCount;
import com.dpdocter.response.DailyReportAnalyticResponse;
import com.dpdocter.response.DischargeSummaryAnalyticsDataResponse;
import com.dpdocter.response.DoctorAnalyticPieChartResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
import com.dpdocter.response.DoctorPatientAnalyticResponse;
import com.dpdocter.response.DoctorPrescriptionAnalyticResponse;
import com.dpdocter.response.DoctorPrescriptionItemAnalyticResponse;
import com.dpdocter.response.DoctorTreatmentAnalyticResponse;
import com.dpdocter.response.DoctorVisitAnalyticResponse;
import com.dpdocter.response.ExpenseAnalyticsDataResponse;
import com.dpdocter.response.ExpenseAnalyticsTypeDataResponse;
import com.dpdocter.response.ExpenseCountResponse;
import com.dpdocter.response.IncomeAnalyticsDataResponse;
import com.dpdocter.response.InvoiceAnalyticsDataDetailResponse;
import com.dpdocter.response.PaymentAnalyticsDataResponse;
import com.dpdocter.response.PaymentDetailsAnalyticsDataResponse;
import com.dpdocter.response.ScheduleAndCheckoutCount;
import com.dpdocter.services.AnalyticsService;
import com.dpdocter.services.AppointmentAnalyticsService;
import com.dpdocter.services.PatientAnalyticService;
import com.dpdocter.services.PrescriptionAnalyticsService;
import com.dpdocter.services.TreatmentAnalyticsService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.ANALYTICS_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ANALYTICS_BASE_URL, description = "")
public class AnalyticsAPI {

	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private AppointmentAnalyticsService appointmentAnalyticsService;

	@Autowired
	private PatientAnalyticService patientAnalyticService;

	@Autowired
	private TreatmentAnalyticsService treatmentAnalyticsService;

	@Autowired
	private PrescriptionAnalyticsService prescriptionAnalyticService;

	@Path(value = PathProxy.AnalyticsUrls.GET_ALL_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_ALL_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_ALL_ANALYTIC)
	public Response<AllAnalyticResponse> getAllAnalyticData(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		AllAnalyticResponse patientAnalyticResponse = analyticsService.getAllAnalyticData(doctorId, locationId,
				hospitalId, fromDate, toDate);

		Response<AllAnalyticResponse> response = new Response<AllAnalyticResponse>();
		response.setData(patientAnalyticResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_DAILY_REPORT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_DAILY_REPORT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_DAILY_REPORT_ANALYTIC)
	public Response<DailyReportAnalyticResponse> getDailyReportAnalytics(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		DailyReportAnalyticResponse patientAnalyticResponse = analyticsService.getDailyReportAnalytics(doctorId,
				locationId, hospitalId, fromDate, toDate);

		Response<DailyReportAnalyticResponse> response = new Response<DailyReportAnalyticResponse>();
		response.setData(patientAnalyticResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTICS_DATA)
	public Response<AnalyticResponse> getPatientAnalyticData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@DefaultValue("NEW_PATIENT") @QueryParam("queryType") String queryType,
			@DefaultValue("DAILY") @QueryParam("searchType") String searchType,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("groupId") String groupId) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}
		List<AnalyticResponse> patientAnalyticResponse = patientAnalyticService.getPatientAnalytic(doctorId, locationId,
				hospitalId, groupId, fromDate, toDate, queryType, searchType, searchTerm);

		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(patientAnalyticResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_DETAIL, notes = PathProxy.AnalyticsUrls.GET_PATIENT_DETAIL)
	public Response<PatientAnalyticData> getPatientDetail(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("toDate") String toDate, @DefaultValue("NEW_PATIENT") @QueryParam("queryType") String queryType,
			@QueryParam("city") String city, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("groupId") String groupId) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}
		List<PatientAnalyticData> patientDataResponse = patientAnalyticService.getPatientData(page, size, doctorId,
				locationId, hospitalId, groupId, fromDate, toDate, queryType, searchTerm, city);

		Response<PatientAnalyticData> response = new Response<PatientAnalyticData>();
		response.setDataList(patientDataResponse);
		response.setCount(patientAnalyticService.getPatientCount(doctorId, locationId, hospitalId, groupId, fromDate,
				toDate, queryType, searchTerm, city));
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS, notes = PathProxy.AnalyticsUrls.GET_MOST_PRESCRIBED_PRESCRIPTION_ITEMS)
	public Response<Object> getMostPrescribedPrescriptionItems(@PathParam("type") String type,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(type, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}

		int count = prescriptionAnalyticService.countPrescripedItems(doctorId, locationId, hospitalId, fromDate, toDate,
				type);
		List<?> objects = null;
		if (count > 0) {
			objects = prescriptionAnalyticService.getMostPrescripedPrescriptionItems(type, doctorId, locationId,
					hospitalId, fromDate, toDate, queryType, searchType, page, size);
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_AVERAGE_TIME_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_AVERAGE_TIME_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_AVERAGE_TIME_ANALYTICS_DATA)
	public Response<AppointmentAverageTimeAnalyticResponse> getAppointmentAverageTimeAnalyticsData(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchType") String searchType,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}
		long count = appointmentAnalyticsService.countPatientAppointmentAnalyticsDetail(doctorId, locationId,
				hospitalId, fromDate, toDate, null, searchTerm);
		List<AppointmentAverageTimeAnalyticResponse> appointmentAnalyticResponse = null;
		if (count > 0) {
			appointmentAnalyticResponse = appointmentAnalyticsService.getAppointmentAverageTimeAnalyticsData(doctorId,
					locationId, hospitalId, fromDate, toDate, searchType, searchTerm, page, size);
		}
		Response<AppointmentAverageTimeAnalyticResponse> response = new Response<AppointmentAverageTimeAnalyticResponse>();
		response.setDataList(appointmentAnalyticResponse);
		response.setCount((int) count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DATA)
	public Response<AnalyticResponse> getAppointmentAnalyticsData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("queryType") String queryType, @QueryParam("searchType") String searchType,
			@QueryParam("state") String state, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		List<AnalyticResponse> appointmentAnalyticResponse = appointmentAnalyticsService.getAppointmentAnalytics(
				doctorId, locationId, hospitalId, fromDate, toDate, state, queryType, searchType, searchTerm, page,
				size);

		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(appointmentAnalyticResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_PIE_CHART)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_PIE_CHART, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_PIE_CHART)
	public Response<AppointmentAnalyticGroupWiseResponse> getAppointmentAnalyticsPatientGroup(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("state") String state, @QueryParam("status") String status,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		List<AppointmentAnalyticGroupWiseResponse> appointmentAnalyticResponse = appointmentAnalyticsService
				.getAppointmentAnalyticPatientGroup(doctorId, locationId, hospitalId, fromDate, toDate, state, page,
						size);

		Response<AppointmentAnalyticGroupWiseResponse> response = new Response<AppointmentAnalyticGroupWiseResponse>();
		response.setDataList(appointmentAnalyticResponse);
		response.setCount(appointmentAnalyticsService.countAppointmentAnalyticPatientGroup(doctorId, locationId,
				hospitalId, fromDate, toDate, state));
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_DOCTOR_APPOINTMENT_ANALYTICS_PIE_CHART)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_DOCTOR_APPOINTMENT_ANALYTICS_PIE_CHART, notes = PathProxy.AnalyticsUrls.GET_DOCTOR_APPOINTMENT_ANALYTICS_PIE_CHART)
	public Response<DoctorAnalyticPieChartResponse> getDoctorAppointmentAnalyticsData(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("state") String state,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}

		List<DoctorAnalyticPieChartResponse> appointmentAnalyticResponse = null;

		appointmentAnalyticResponse = appointmentAnalyticsService.getDoctorAppointmentAnalyticsForPieChart(doctorId,
				locationId, hospitalId, fromDate, toDate, state, searchTerm, page, size);

		Response<DoctorAnalyticPieChartResponse> response = new Response<DoctorAnalyticPieChartResponse>();
		response.setDataList(appointmentAnalyticResponse);

		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA)
	public Response<AppointmentAnalyticResponse> getAppointmentAnalytics(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("state") String state, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		AppointmentAnalyticResponse appointmentAnalyticResponse = appointmentAnalyticsService
				.getAppointmentAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, searchTerm, page,
						size);

		Response<AppointmentAnalyticResponse> response = new Response<AppointmentAnalyticResponse>();
		response.setData(appointmentAnalyticResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DETAIL, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTICS_DETAIL)
	public Response<AppointmentAnalyticData> getAppointmentAnalyticsDetail(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("state") String state, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		List<AppointmentAnalyticData> appointmentAnalyticResponse = null;
		int count = appointmentAnalyticsService.countPatientAppointmentAnalyticsDetail(doctorId, locationId, hospitalId,
				fromDate, toDate, state, searchTerm);
		if (count > 0) {
			appointmentAnalyticResponse = appointmentAnalyticsService.getPatientAppointmentAnalyticsDetail(doctorId,
					locationId, hospitalId, fromDate, toDate, state, searchTerm, page, size);
		}
		Response<AppointmentAnalyticData> response = new Response<AppointmentAnalyticData>();
		response.setDataList(appointmentAnalyticResponse);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTICS_DATA)
	public Response<IncomeAnalyticsDataResponse> getIncomeAnalyticsData(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@DefaultValue(value = "ALL") @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<IncomeAnalyticsDataResponse> incomeAnalyticsDataResponse = analyticsService.getIncomeAnalyticsData(
				doctorId, locationId, hospitalId, fromDate, toDate, queryType, searchType, page, size);

		Response<IncomeAnalyticsDataResponse> response = new Response<IncomeAnalyticsDataResponse>();
		response.setDataList(incomeAnalyticsDataResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_INCOME_DETAILS_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_INCOME_DETAILS_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_INCOME_DETAILS_ANALYTICS_DATA)
	public Response<InvoiceAnalyticsDataDetailResponse> getInvoiceIncomeAnalyticsData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<InvoiceAnalyticsDataDetailResponse> incomeAnalyticsDataResponse = null;
		Integer count = analyticsService.countIncomeDetailsAnalyticsData(doctorId, locationId, hospitalId, fromDate,
				toDate, searchTerm);
		if (count > 0) {
			incomeAnalyticsDataResponse = analyticsService.getIncomeDetailsAnalyticsData(doctorId, locationId,
					hospitalId, fromDate, toDate, searchTerm, page, size);
		}

		Response<InvoiceAnalyticsDataDetailResponse> response = new Response<InvoiceAnalyticsDataDetailResponse>();
		response.setDataList(incomeAnalyticsDataResponse);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA)
	public Response<PaymentDetailsAnalyticsDataResponse> getPaymentDetailsAnalyticsData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("paymentMode") String paymentMode, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<PaymentDetailsAnalyticsDataResponse> paymentDetailsAnalyticsDataResponses = null;
		Integer count = analyticsService.countPaymentDetailsAnalyticsData(doctorId, locationId, hospitalId, fromDate,
				toDate, searchTerm, paymentMode);
		if (count > 0) {
			paymentDetailsAnalyticsDataResponses = analyticsService.getPaymentDetailsAnalyticsData(doctorId, locationId,
					hospitalId, fromDate, toDate, searchTerm, paymentMode, page, size);
		}
		Response<PaymentDetailsAnalyticsDataResponse> response = new Response<PaymentDetailsAnalyticsDataResponse>();
		response.setDataList(paymentDetailsAnalyticsDataResponses);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTICS_DATA)
	public Response<PaymentAnalyticsDataResponse> getPaymentAnalyticsData(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@DefaultValue(value = "ALL") @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<PaymentAnalyticsDataResponse> paymentAnalyticsDataResponses = analyticsService.getPaymentAnalyticsData(
				doctorId, locationId, hospitalId, fromDate, toDate, queryType, searchType, page, size);

		Response<PaymentAnalyticsDataResponse> response = new Response<PaymentAnalyticsDataResponse>();
		response.setDataList(paymentAnalyticsDataResponses);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_AMOUNT_DUE_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_AMOUNT_DUE_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_AMOUNT_DUE_ANALYTICS_DATA)
	public Response<AmountDueAnalyticsDataResponse> getAmountDueAnalyticsData(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@DefaultValue(value = "ALL") @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		Integer count = analyticsService.countAmountDueAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate,
				queryType, searchType);
		List<AmountDueAnalyticsDataResponse> paymentAnalyticsDataResponses = null;
		if (count > 0) {
			paymentAnalyticsDataResponses = analyticsService.getAmountDueAnalyticsData(doctorId, locationId, hospitalId,
					fromDate, toDate, queryType, searchType, page, size);
		}
		Response<AmountDueAnalyticsDataResponse> response = new Response<AmountDueAnalyticsDataResponse>();
		response.setDataList(paymentAnalyticsDataResponses);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS, notes = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS)
	public Response<TreatmentService> getTreatmentsAnalyticsData(@QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate, @QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}

		Response<TreatmentService> response = new Response<TreatmentService>();

		List<TreatmentService> services = null;
		Integer count = 0;
		count = treatmentAnalyticsService.countTreatmentService(doctorId, locationId, hospitalId, fromDate, toDate,
				searchTerm);
		if (count > 0) {
			services = treatmentAnalyticsService.getTreatmentServiceAnalytics(doctorId, locationId, hospitalId,
					fromDate, toDate, page, size, searchTerm);
		}
		response.setDataList(services);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC)
	public Response<DoctorAppointmentAnalyticResponse> getAppointmentAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		DoctorAppointmentAnalyticResponse data = appointmentAnalyticsService.getAppointmentAnalytic(doctorId,
				locationId, hospitalId, fromDate, toDate);
		Response<DoctorAppointmentAnalyticResponse> response = new Response<DoctorAppointmentAnalyticResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PATIENT_ANALYTIC)
	public Response<DoctorPatientAnalyticResponse> getPatientAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}
		DoctorPatientAnalyticResponse data = patientAnalyticService.getPatientAnalytic(doctorId, locationId, hospitalId,
				fromDate, toDate);
		Response<DoctorPatientAnalyticResponse> response = new Response<DoctorPatientAnalyticResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_ANALYTIC)
	public Response<DoctorTreatmentAnalyticResponse> getTreatmentServiceAnalyticWithStatus(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}

		List<DoctorTreatmentAnalyticResponse> data = treatmentAnalyticsService.getTreatmentServiceAnalyticWithStatus(
				page, size, doctorId, locationId, hospitalId, fromDate, toDate, searchTerm);
		Response<DoctorTreatmentAnalyticResponse> response = new Response<DoctorTreatmentAnalyticResponse>();
		response.setDataList(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC)
	public Response<DoctorPrescriptionAnalyticResponse> getPrescriptionAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "hospitalId should not be empty");
		}

		DoctorPrescriptionAnalyticResponse data = prescriptionAnalyticService.getPrescriptionAnalytic(doctorId,
				locationId, hospitalId, fromDate, toDate);
		Response<DoctorPrescriptionAnalyticResponse> response = new Response<DoctorPrescriptionAnalyticResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ITEM_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ITEM_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ITEM_ANALYTIC)
	public Response<DoctorPrescriptionItemAnalyticResponse> getPrescriptionItemAnalytic(@PathParam("type") String type,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, type)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}
		Response<DoctorPrescriptionItemAnalyticResponse> response = new Response<DoctorPrescriptionItemAnalyticResponse>();
		List<DoctorPrescriptionItemAnalyticResponse> data = null;
		int count = prescriptionAnalyticService.countPrescriptionItemAnalytic(doctorId, locationId, hospitalId,
				fromDate, toDate, type, searchTerm);

		data = prescriptionAnalyticService.getPrescriptionItemAnalytic(page, size, doctorId, locationId, hospitalId,
				fromDate, toDate, type, searchTerm);

		response.setDataList(data);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_VISIT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_VISIT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PATIENT_VISIT_ANALYTIC)
	public Response<DoctorVisitAnalyticResponse> getPatintVisitAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}

		DoctorVisitAnalyticResponse data = analyticsService.getVisitAnalytic(doctorId, locationId, hospitalId, fromDate,
				toDate);
		Response<DoctorVisitAnalyticResponse> response = new Response<DoctorVisitAnalyticResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_DOCTOR_EXPENSE_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_DOCTOR_EXPENSE_ANALYTICS, notes = PathProxy.AnalyticsUrls.GET_DOCTOR_EXPENSE_ANALYTICS)
	public Response<ExpenseCountResponse> getPatintVisitAnalytic(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("discarded") boolean discarded, @QueryParam("expenseType") String expenseType,
			@QueryParam("paymentMode") String paymentMode) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}

		ExpenseCountResponse data = analyticsService.getDoctorExpenseAnalytic(doctorId, locationId, hospitalId,
				discarded, fromDate, toDate, expenseType, paymentMode);
		Response<ExpenseCountResponse> response = new Response<ExpenseCountResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_EXPENSE_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_EXPENSE_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_EXPENSE_ANALYTICS_DATA)
	public Response<ExpenseAnalyticsTypeDataResponse> getExpenseAnalyticsTypeData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<ExpenseAnalyticsTypeDataResponse> paymentAnalyticsDataResponses = null;
		paymentAnalyticsDataResponses = analyticsService.getExpenseAnalyticsTypeData(doctorId, locationId, hospitalId,
				fromDate, toDate);
		Response<ExpenseAnalyticsTypeDataResponse> response = new Response<ExpenseAnalyticsTypeDataResponse>();
		response.setDataList(paymentAnalyticsDataResponses);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DATA)
	public Response<AnalyticResponse> getTreatmentAnalyticData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, searchType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					" locationId, hospitalId ,searchType should not be empty");
		}

		List<AnalyticResponse> data = null;

		data = treatmentAnalyticsService.getTreatmentAnalyticData(doctorId, locationId, hospitalId, fromDate, toDate,
				searchType, searchTerm);

		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(data);

		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DETAIL, notes = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTICS_DETAIL)
	public Response<TreatmentAnalyticDetail> getTreatmentAnalyticDetail(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("status") String status) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<TreatmentAnalyticDetail> response = new Response<TreatmentAnalyticDetail>();
		Integer count = 0;
		count = treatmentAnalyticsService.countTreatments(doctorId, locationId, hospitalId, fromDate, toDate,
				searchTerm, status);
		List<TreatmentAnalyticDetail> data = null;
		if (count > 0) {
			data = treatmentAnalyticsService.getTreatmentAnalyticDetail(page, size, doctorId, locationId, hospitalId,
					fromDate, toDate, searchTerm, status);
		}
		response.setDataList(data);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_PIE_CHART)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_PIE_CHART, notes = PathProxy.AnalyticsUrls.GET_TREATMENT_SERVICE_PIE_CHART)
	public Response<DoctorAnalyticPieChartResponse> getTreatmentAnalyticForPieChart(
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("doctorId") String doctorId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<DoctorAnalyticPieChartResponse> response = new Response<DoctorAnalyticPieChartResponse>();
		Integer count = 0;
		count = treatmentAnalyticsService.countTreatments(doctorId, locationId, hospitalId, fromDate, toDate,
				searchTerm, null);
		List<DoctorAnalyticPieChartResponse> data = null;
		if (count > 0) {
			data = treatmentAnalyticsService.getTreatmentAnalyticForPieChart(doctorId, locationId, hospitalId, fromDate,
					toDate);
		}
		response.setDataList(data);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_DOCTOR_PRESCRIPTION_PIE_CHART)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_DOCTOR_PRESCRIPTION_PIE_CHART, notes = PathProxy.AnalyticsUrls.GET_DOCTOR_PRESCRIPTION_PIE_CHART)
	public Response<DoctorAnalyticPieChartResponse> getPrescriptionAnalyticForPieChart(
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("doctorId") String doctorId, @QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<DoctorAnalyticPieChartResponse> response = new Response<DoctorAnalyticPieChartResponse>();
		Integer count = 0;
		count = prescriptionAnalyticService.countPrescription(doctorId, locationId, hospitalId, fromDate, toDate, null);
		List<DoctorAnalyticPieChartResponse> data = null;
		if (count > 0) {
			data = prescriptionAnalyticService.getPrescriptionAnalyticForPieChart(doctorId, locationId, hospitalId,
					fromDate, toDate);
		}
		response.setDataList(data);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DETAIL, notes = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DETAIL)
	public Response<PrescriptionAnalyticDetail> getPrescriptionAnalyticDetail(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<PrescriptionAnalyticDetail> response = new Response<PrescriptionAnalyticDetail>();
		Integer count = 0;
		count = prescriptionAnalyticService.countPrescription(doctorId, locationId, hospitalId, fromDate, toDate,
				searchTerm);
		List<PrescriptionAnalyticDetail> data = null;
		if (count > 0) {
			data = prescriptionAnalyticService.getPrescriptionAnalyticDetail(page, size, doctorId, locationId,
					hospitalId, fromDate, toDate, searchTerm);
		}
		response.setDataList(data);
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DATA, notes = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC_DATA)
	public Response<AnalyticResponse> getPrescriptionAnalyticData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, searchType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					" locationId, hospitalId ,searchType should not be empty");
		}

		List<AnalyticResponse> data = prescriptionAnalyticService.getPrescriptionAnalyticData(doctorId, locationId,
				hospitalId, fromDate, toDate, searchType, searchTerm);

		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTIC_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTIC_DATA, notes = PathProxy.AnalyticsUrls.GET_INCOME_ANALYTIC_DATA)
	public Response<AnalyticResponse> getInvoiceAnalyticData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("searchType") String searchType) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, searchType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					" locationId, hospitalId ,searchType should not be empty");
		}

		List<AnalyticResponse> data = analyticsService.getInvoiceAnalyticData(doctorId, locationId, hospitalId,
				fromDate, toDate, searchType, searchTerm);
		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTIC_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTIC_DATA, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_ANALYTIC_DATA)
	public Response<AnalyticResponse> getReceiptAnalyticData(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("searchType") String searchType,
			@QueryParam("paymentMode") String paymentMode) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, searchType)) {
			throw new BusinessException(ServiceError.InvalidInput,
					" locationId, hospitalId ,searchType should not be empty");
		}

		List<AnalyticResponse> data = analyticsService.getReceiptAnalyticData(doctorId, locationId, hospitalId,
				fromDate, toDate, searchType, searchTerm, paymentMode);
		Response<AnalyticResponse> response = new Response<AnalyticResponse>();
		response.setDataList(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_COUNT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_COUNT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PATIENT_COUNT_ANALYTIC)
	public Response<AnalyticCountResponse> getPatientCountAnalyticData(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("type") String type, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("isVisited") boolean isVisited) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId, type)) {
			throw new BusinessException(ServiceError.InvalidInput,
					" locationId, hospitalId ,searchType should not be empty");
		}
		Response<AnalyticCountResponse> response = new Response<AnalyticCountResponse>();
		Integer count = patientAnalyticService.getPatientCountAnalytic(0, page, doctorId, locationId, hospitalId,
				fromDate, toDate, type, searchTerm, isVisited).size();
		if (count > 0) {
			List<AnalyticCountResponse> data = patientAnalyticService.getPatientCountAnalytic(size, page, doctorId,
					locationId, hospitalId, fromDate, toDate, type, searchTerm, isVisited);
			response.setDataList(data);
		}
		response.setCount(count);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_SCHEDULED_AND_CHECKOUT_COUNT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_SCHEDULED_AND_CHECKOUT_COUNT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_SCHEDULED_AND_CHECKOUT_COUNT_ANALYTIC)
	public Response<ScheduleAndCheckoutCount> getScheduleAndCheckoutCount(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<ScheduleAndCheckoutCount> response = new Response<ScheduleAndCheckoutCount>();
		ScheduleAndCheckoutCount data = appointmentAnalyticsService.getScheduledAndCheckoutCount(doctorId, locationId,
				hospitalId, fromDate, toDate);
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_BOOKED_AND_CANCELLED_APPOINTMENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_BOOKED_AND_CANCELLED_APPOINTMENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_BOOKED_AND_CANCELLED_APPOINTMENT_ANALYTIC)
	public Response<BookedAndCancelAppointmentCount> getBookedAndCancelAppointmentCount(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<BookedAndCancelAppointmentCount> response = new Response<BookedAndCancelAppointmentCount>();
		BookedAndCancelAppointmentCount data = appointmentAnalyticsService.getBookedAndCancelledCount(doctorId,
				locationId, hospitalId, fromDate, toDate);
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_BOOKED_BY_APPOINTMENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_BOOKED_BY_APPOINTMENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_BOOKED_BY_APPOINTMENT_ANALYTIC)
	public Response<AppointmentBookedByCountResponse> getBookedByAppointmentCount(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("state") String state) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		Response<AppointmentBookedByCountResponse> response = new Response<AppointmentBookedByCountResponse>();
		AppointmentBookedByCountResponse data = appointmentAnalyticsService.getAppointmentBookedByCount(doctorId,
				locationId, hospitalId, fromDate, toDate, state);
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_ONLINE_CONSULTATION_ANALYTICS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_ONLINE_CONSULTATION_ANALYTICS, notes = PathProxy.AnalyticsUrls.GET_ONLINE_CONSULTATION_ANALYTICS)
	public Response<OnlineConsultationAnalytics> getPatientAppointments(
			@QueryParam(value = "locationId") String locationId, @QueryParam(value = "doctorId") String doctorId,
			@QueryParam(value = "type") String type, @QueryParam(value = "fromDate") String fromDate,
			@QueryParam(value = "toDate") String toDate) {

		Response<OnlineConsultationAnalytics> response = new Response<OnlineConsultationAnalytics>();
		response.setData(
				appointmentAnalyticsService.getConsultationAnalytics(fromDate, toDate, doctorId, locationId, type));
		return response;

	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_SUMMARY)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_SUMMARY, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_SUMMARY)
	public Response<PaymentSummary> getPaymentSummary(@QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "doctorId") String doctorId, @QueryParam(value = "fromDate") String fromDate,
			@QueryParam(value = "toDate") String toDate) {

		Response<PaymentSummary> response = new Response<PaymentSummary>();

		response.setData(appointmentAnalyticsService.getPaymentSummary(fromDate, toDate, doctorId));

		return response;

	}

	@Path(value = PathProxy.AnalyticsUrls.FETCH_SETTLEMENT)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.FETCH_SETTLEMENT, notes = PathProxy.AnalyticsUrls.FETCH_SETTLEMENT)
	public Response<PaymentSettlements> getSettlements(@QueryParam(value = "day") int day,
			@QueryParam(value = "month") int month, @QueryParam(value = "year") int year) {

		Response<PaymentSettlements> response = new Response<PaymentSettlements>();
		response.setData(appointmentAnalyticsService.fetchSettlement(day, month, year));

		return response;

	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_SETTLEMENT)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_SETTLEMENT, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_SETTLEMENT)
	public Response<OnlineConsultationSettlement> getConsultationSettlement(
			@QueryParam(value = "fromDate") String fromDate, @QueryParam(value = "toDate") String toDate,
			@QueryParam(value = "doctorId") String doctorId, @DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size) {

		Response<OnlineConsultationSettlement> response = new Response<OnlineConsultationSettlement>();
		response.setDataList(appointmentAnalyticsService.getSettlements(fromDate, toDate, doctorId, page, size));
		return response;

	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_PAYMENT_SETTLEMENTS)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_PAYMENT_SETTLEMENTS, notes = PathProxy.AnalyticsUrls.GET_PATIENT_PAYMENT_SETTLEMENTS)
	public Response<PatientPaymentDetails> getPatientSettlement(@QueryParam(value = "doctorId") String doctorId,
			@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size) {

		Response<PatientPaymentDetails> response = new Response<PatientPaymentDetails>();
		response.setDataList(appointmentAnalyticsService.getPatientPaymentDetails(doctorId, page, size));
		return response;

	}

	@Path(value = PathProxy.AnalyticsUrls.GET_DISCHARGE_SUMMARY_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_DISCHARGE_SUMMARY_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_DISCHARGE_SUMMARY_ANALYTICS_DATA)
	public Response<DischargeSummaryAnalyticsDataResponse> getDischargeSummaryAnalyticsData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<DischargeSummaryAnalyticsDataResponse> paymentAnalyticsDataResponses = analyticsService
				.getDischargeSummaryAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, page, size);
		Response<DischargeSummaryAnalyticsDataResponse> response = new Response<DischargeSummaryAnalyticsDataResponse>();
		response.setDataList(paymentAnalyticsDataResponses);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PATIENT_REFFERED_BY_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PATIENT_REFFERED_BY_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PATIENT_REFFERED_BY_ANALYTICS_DATA)
	public Response<PatientReferredByAnalyticData> getPatientRefferedByAnalyticData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @DefaultValue("NEW_PATIENT") @QueryParam("queryType") String queryType,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("referredBy") String referred,
			@DefaultValue("0") @QueryParam(value = "page") int page,
			@DefaultValue("0") @QueryParam(value = "size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}
		List<PatientReferredByAnalyticData> patientAnalyticResponse = patientAnalyticService
				.getPatientReferredByAnalyticData(doctorId, locationId, hospitalId, referred, fromDate, toDate,
						queryType, searchTerm,page,size);

		Response<PatientReferredByAnalyticData> response = new Response<PatientReferredByAnalyticData>();
		response.setDataList(patientAnalyticResponse);
		return response;
	}
}