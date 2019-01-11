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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AppointmentAnalyticData;
import com.dpdocter.beans.PatientAnalyticData;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.enums.PrescriptionItems;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.AmountDueAnalyticsDataResponse;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticResponse;
import com.dpdocter.response.AppointmentAverageTimeAnalyticResponse;
import com.dpdocter.response.AppointmentAnalyticGroupWiseResponse;
import com.dpdocter.response.DoctorAppointmentAnalyticResponse;
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
import com.dpdocter.services.AnalyticsService;
import com.dpdocter.services.AppointmentAnalyticsService;
import com.dpdocter.services.PatientAnalyticService;

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

	private static Logger logger = Logger.getLogger(AnalyticsAPI.class.getName());

	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private AppointmentAnalyticsService appointmentAnalyticsService;

	@Autowired
	private PatientAnalyticService patientAnalyticService;

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
		if (type.equalsIgnoreCase(PrescriptionItems.DRUGS.getItem())) {
			if (DPDoctorUtils.anyStringEmpty(doctorId)) {
				logger.warn("Invalid Input");
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
			}
		}
		List<?> objects = analyticsService.getMostPrescribedPrescriptionItems(type, doctorId, locationId, hospitalId,
				fromDate, toDate, queryType, searchType, page, size);

		Response<Object> response = new Response<Object>();
		response.setDataList(objects);
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
		List<AppointmentAverageTimeAnalyticResponse> appointmentAnalyticResponse = appointmentAnalyticsService
				.getAppointmentAverageTimeAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, searchType,
						searchTerm, page, size);

		Response<AppointmentAverageTimeAnalyticResponse> response = new Response<AppointmentAverageTimeAnalyticResponse>();
		response.setDataList(appointmentAnalyticResponse);
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

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_FOR_PIE)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_FOR_PIE, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_PATIENT_GROUP_ANALYTICS_FOR_PIE)
	public Response<AppointmentAnalyticGroupWiseResponse> getAppointmentAnalyticsPatientGroup(
			@QueryParam("doctorId") String doctorId, @PathParam("locationId") String locationId,
			@PathParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("searchType") String searchType,
			@QueryParam("state") String state, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		List<AppointmentAnalyticGroupWiseResponse> appointmentAnalyticResponse = appointmentAnalyticsService
				.getAppointmentAnalyticPatientGroup(doctorId, locationId, hospitalId, fromDate, toDate, state, page,
						size);

		Response<AppointmentAnalyticGroupWiseResponse> response = new Response<AppointmentAnalyticGroupWiseResponse>();
		response.setDataList(appointmentAnalyticResponse);
		response.setCount(appointmentAnalyticsService.countAppointmentAnalyticPatientGroup(doctorId, locationId,
				hospitalId, fromDate, toDate, state, page, size));
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_MAX_TIME_ANALYTICS_DATA)
	public Response<AppointmentAnalyticResponse> getAppointmentAnalytics(@QueryParam("doctorId") String doctorId,
			@PathParam("locationId") String locationId, @PathParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchType") String searchType, @QueryParam("state") String state,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}
		AppointmentAnalyticResponse appointmentAnalyticResponse = appointmentAnalyticsService
				.getAppointmentAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, searchType, searchTerm,
						page, size);

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
				fromDate, toDate, state, searchTerm, page, size);
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
			@QueryParam("toDate") String toDate, @DefaultValue(value = "ALL") @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<InvoiceAnalyticsDataDetailResponse> incomeAnalyticsDataResponse = analyticsService
				.getIncomeDetailsAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, queryType,
						searchType, page, size);

		Response<InvoiceAnalyticsDataDetailResponse> response = new Response<InvoiceAnalyticsDataDetailResponse>();
		response.setDataList(incomeAnalyticsDataResponse);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_PAYMENT_DETAILS_ANALYTICS_DATA)
	public Response<PaymentDetailsAnalyticsDataResponse> getPaymentDetailsAnalyticsData(
			@QueryParam("doctorId") String doctorId, @QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("queryType") String queryType,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(doctorId, locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput,
					"doctorId, locationId, hospitalId should not be empty");
		}
		List<PaymentDetailsAnalyticsDataResponse> paymentDetailsAnalyticsDataResponses = analyticsService
				.getPaymentDetailsAnalyticsData(doctorId, locationId, hospitalId, fromDate, toDate, queryType,
						searchType, page, size);

		Response<PaymentDetailsAnalyticsDataResponse> response = new Response<PaymentDetailsAnalyticsDataResponse>();
		response.setDataList(paymentDetailsAnalyticsDataResponses);
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
		List<AmountDueAnalyticsDataResponse> paymentAnalyticsDataResponses = analyticsService.getAmountDueAnalyticsData(
				doctorId, locationId, hospitalId, fromDate, toDate, queryType, searchType, page, size);

		Response<AmountDueAnalyticsDataResponse> response = new Response<AmountDueAnalyticsDataResponse>();
		response.setDataList(paymentAnalyticsDataResponses);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS_DATA)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS_DATA, notes = PathProxy.AnalyticsUrls.GET_TREATMENTS_ANALYTICS_DATA)
	public Response<TreatmentService> getTreatmentsAnalyticsData(@QueryParam("locationId") String locationId,
			@QueryParam("hospitalId") String hospitalId, @QueryParam("doctorId") String doctorId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchType") String searchType, @QueryParam("page") int page, @QueryParam("size") int size) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}
		List<TreatmentService> objects = analyticsService.getTreatmentsAnalyticsData(doctorId, locationId, hospitalId,
				fromDate, toDate, searchType, page, size);

		Response<TreatmentService> response = new Response<TreatmentService>();
		response.setDataList(objects);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_APPOINTMENT_ANALYTIC)
	public Response<DoctorAppointmentAnalyticResponse> getAppointmentAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
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
		DoctorPatientAnalyticResponse data = analyticsService.getPatientAnalytic(doctorId, locationId, hospitalId,
				fromDate, toDate);
		Response<DoctorPatientAnalyticResponse> response = new Response<DoctorPatientAnalyticResponse>();
		response.setData(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_TREATMENT_ANALYTIC)
	public Response<DoctorTreatmentAnalyticResponse> getTreatmentAnalytic(@QueryParam("page") int page,
			@QueryParam("size") int size, @QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("searchTerm") String searchTerm) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, " locationId, hospitalId should not be empty");
		}

		List<DoctorTreatmentAnalyticResponse> data = analyticsService.getTreatmentAnalytic(page, size, doctorId,
				locationId, hospitalId, fromDate, toDate, searchTerm);
		Response<DoctorTreatmentAnalyticResponse> response = new Response<DoctorTreatmentAnalyticResponse>();
		response.setDataList(data);
		return response;
	}

	@Path(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC)
	@GET
	@ApiOperation(value = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC, notes = PathProxy.AnalyticsUrls.GET_PRESCRIPTION_ANALYTIC)
	public Response<DoctorprescriptionAnalyticResponse> getPrescriptionAnalytic(@QueryParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "locationId, hospitalId should not be empty");
		}

		DoctorprescriptionAnalyticResponse data = analyticsService.getPrescriptionAnalytic(doctorId, locationId,
				hospitalId, fromDate, toDate);
		Response<DoctorprescriptionAnalyticResponse> response = new Response<DoctorprescriptionAnalyticResponse>();
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
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}

		List<DoctorPrescriptionItemAnalyticResponse> data = analyticsService.getPrescriptionItemAnalytic(page, size,
				doctorId, locationId, hospitalId, fromDate, toDate, type, searchTerm);
		Response<DoctorPrescriptionItemAnalyticResponse> response = new Response<DoctorPrescriptionItemAnalyticResponse>();
		response.setDataList(data);
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
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@QueryParam("fromDate") String fromDate, @QueryParam("toDate") String toDate,
			@QueryParam("discarded") boolean discarded, @QueryParam("searchType") String searchType,
			@QueryParam("expenseType") String expenseType, @QueryParam("paymentMode") String paymentMode) {
		if (DPDoctorUtils.allStringsEmpty(locationId, hospitalId)) {
			throw new BusinessException(ServiceError.InvalidInput, "Type, locationId, hospitalId should not be empty");
		}

		List<ExpenseCountResponse> data = analyticsService.getDoctorExpenseAnalytic(doctorId, searchType, locationId,
				hospitalId, discarded, fromDate, toDate, expenseType, paymentMode);
		Response<ExpenseCountResponse> response = new Response<ExpenseCountResponse>();
		response.setDataList(data);
		return response;
	}

}