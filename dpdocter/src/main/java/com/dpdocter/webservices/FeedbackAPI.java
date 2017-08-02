package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.pharmacyFeedbackRequest;
import com.dpdocter.services.FeedbackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(PathProxy.FEEDBACK_BASE_URL)
@Api(value = PathProxy.FEEDBACK_BASE_URL, description = "Endpoint for feedback API")
public class FeedbackAPI {

	private final Logger logger = Logger.getLogger(FeedbackAPI.class);

	@Autowired
	FeedbackService feedbackService;

	@POST
	@Path(PathProxy.FeedbackUrls.ADD_EDIT_GENERAL_APPOINTMENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_GENERAL_APPOINTMENT_FEEDBACK)
	public Response<AppointmentGeneralFeedback> addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback) {
		Response<AppointmentGeneralFeedback> response = new Response<>();
		AppointmentGeneralFeedback appointmentGeneralFeedback = null;
		try {
			if (feedback == null & DPDoctorUtils.allStringsEmpty(feedback.getDoctorId(), feedback.getPatientId(),
					feedback.getHospitalId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			appointmentGeneralFeedback = feedbackService.addEditAppointmentGeneralFeedback(feedback);
			response.setData(appointmentGeneralFeedback);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(PathProxy.FeedbackUrls.ADD_EDIT_PHARMACY_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_PHARMACY_FEEDBACK)
	public Response<PharmacyFeedback> addEditPharmacyFeedback(pharmacyFeedbackRequest feedback) {
		Response<PharmacyFeedback> response = new Response<>();
		PharmacyFeedback pharmacyFeedback = null;
		try {
			if (feedback == null & DPDoctorUtils.allStringsEmpty(feedback.getLocaleId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			pharmacyFeedback = feedbackService.addEditPharmacyFeedback(feedback);
			response.setData(pharmacyFeedback);
			
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(PathProxy.FeedbackUrls.ADD_EDIT_PRESCRIPTION_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_PRESCRIPTION_FEEDBACK)
	public Response<PrescriptionFeedback> addEditPrescriptionFeedback(PrescriptionFeedback feedback) {
		Response<PrescriptionFeedback> response = new Response<>();
		PrescriptionFeedback prescriptionFeedback = null;
		try {
			if (feedback == null & DPDoctorUtils.allStringsEmpty(feedback.getDoctorId(), feedback.getPatientId(),
					feedback.getHospitalId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			prescriptionFeedback = feedbackService.addEditPrescriptionFeedback(prescriptionFeedback);
			response.setData(prescriptionFeedback);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(PathProxy.FeedbackUrls.GET_GENERAL_APPOINTMENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_GENERAL_APPOINTMENT_FEEDBACK)
	public Response<AppointmentGeneralFeedback> getGeneralAppointmentFeedback(FeedbackGetRequest request) {
		Response<AppointmentGeneralFeedback> response = new Response<>();
		List<AppointmentGeneralFeedback> feedbacks = null;
		try {
			feedbacks = feedbackService.getAppointmentGeneralFeedbackList(request);
			response.setDataList(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(PathProxy.FeedbackUrls.GET_PHARMACY_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_PHARMACY_FEEDBACK)
	public Response<PharmacyFeedback> getPharmacyFeedback(FeedbackGetRequest request) {
		Response<PharmacyFeedback> response = new Response<>();
		List<PharmacyFeedback> feedbacks = null;
		try {
			feedbacks = feedbackService.getPharmacyFeedbackList(request);
			response.setDataList(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@POST
	@Path(PathProxy.FeedbackUrls.GET_PRESCRIPTION_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_PRESCRIPTION_FEEDBACK)
	public Response<PrescriptionFeedback> getPrescriptionFeedback(FeedbackGetRequest request) {
		Response<PrescriptionFeedback> response = new Response<>();
		List<PrescriptionFeedback> feedbacks = null;
		try {
			feedbacks = feedbackService.getPrescriptionFeedbackList(request);
			response.setDataList(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

}
