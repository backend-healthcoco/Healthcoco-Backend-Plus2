package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.DailyImprovementFeedback;
import com.dpdocter.beans.PatientFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.DailyImprovementFeedbackRequest;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientFeedbackReplyRequest;
import com.dpdocter.request.PatientFeedbackRequest;
import com.dpdocter.request.PharmacyFeedbackRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.response.DailyImprovementFeedbackResponse;
import com.dpdocter.response.PatientFeedbackIOSResponse;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.services.FeedbackService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.FEEDBACK_BASE_URL)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

@Api(value = PathProxy.FEEDBACK_BASE_URL, description = "Endpoint for feedback API")
public class FeedbackAPI {

	private final Logger logger = LogManager.getLogger(FeedbackAPI.class);

	@Autowired
	FeedbackService feedbackService;

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_EDIT_GENERAL_APPOINTMENT_FEEDBACK)
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

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_EDIT_PHARMACY_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_PHARMACY_FEEDBACK)
	public Response<PharmacyFeedback> addEditPharmacyFeedback(PharmacyFeedbackRequest feedback) {
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

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_EDIT_PRESCRIPTION_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_PRESCRIPTION_FEEDBACK)
	public Response<PrescriptionFeedback> addEditPrescriptionFeedback(PrescriptionFeedbackRequest feedback) {
		Response<PrescriptionFeedback> response = new Response<>();
		PrescriptionFeedback prescriptionFeedback = null;
		try {
			if (feedback == null & DPDoctorUtils.allStringsEmpty(feedback.getDoctorId(), feedback.getPatientId(),
					feedback.getHospitalId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			prescriptionFeedback = feedbackService.addEditPrescriptionFeedback(feedback);
			response.setData(prescriptionFeedback);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_EDIT_DAILY_IMPROVEMENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_DAILY_IMPROVEMENT_FEEDBACK)
	public Response<DailyImprovementFeedback> addEditDailyImprovementFeedback(
			DailyImprovementFeedbackRequest feedback) {
		Response<DailyImprovementFeedback> response = new Response<>();
		DailyImprovementFeedback dailyImprovementFeedback = null;
		try {
			if (DPDoctorUtils.allStringsEmpty(feedback.getPrescriptionId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			dailyImprovementFeedback = feedbackService.addEditDailyImprovementFeedback(feedback);
			response.setData(dailyImprovementFeedback);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_EDIT_PATIENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_EDIT_PATIENT_FEEDBACK)
	public Response<PatientFeedback> addEditPatientFeedback(PatientFeedbackRequest feedback) {
		Response<PatientFeedback> response = new Response<>();
		PatientFeedback patientFeedback = null;
		try {
			if (feedback == null & DPDoctorUtils.allStringsEmpty(feedback.getDoctorId(), feedback.getPatientId(),
					feedback.getHospitalId())) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid input");
			}
			patientFeedback = feedbackService.addEditPatientFeedback(feedback);
			response.setData(patientFeedback);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping
	(PathProxy.FeedbackUrls.GET_GENERAL_APPOINTMENT_FEEDBACK)
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

	@PostMapping
	(PathProxy.FeedbackUrls.GET_PHARMACY_FEEDBACK)
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

	@PostMapping
	(PathProxy.FeedbackUrls.GET_PRESCRIPTION_FEEDBACK)
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

	@GetMapping
	(PathProxy.FeedbackUrls.GET_DAILY_IMPROVEMENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_DAILY_IMPROVEMENT_FEEDBACK)
	public Response<DailyImprovementFeedbackResponse> getDailyImprovementFeedback( @RequestParam("page") long page, @RequestParam("size") int size,
		    @RequestParam(value = "prescriptionId") String prescriptionId, @RequestParam(value = "doctorId") String doctorId,
		    @RequestParam(value = "locationId") String locationId, @RequestParam(value = "hospitalId") String hospitalId) {
		Response<DailyImprovementFeedbackResponse> response = new Response<>();
		List<DailyImprovementFeedbackResponse> feedbacks = null;
		try {
			feedbacks = feedbackService.getDailyImprovementFeedbackList(prescriptionId, doctorId, locationId,
					hospitalId, page, size);
			response.setDataList(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping
	(PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACK)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACK)
	public Response<PatientFeedbackResponse> getPatientFeedback(FeedbackGetRequest request,
			@RequestParam("type") String type) {
		Response<PatientFeedbackResponse> response = new Response<>();
		List<PatientFeedbackResponse> feedbacks = null;
		try {
			feedbacks = feedbackService.getPatientFeedbackList(request, type);
			response.setDataList(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@PostMapping
	(PathProxy.FeedbackUrls.ADD_PATIENT_FEEDBACK_REPLY)
	@ApiOperation(value = PathProxy.FeedbackUrls.ADD_PATIENT_FEEDBACK_REPLY)
	public Response<PatientFeedbackResponse> addPatientFeedbackReply(PatientFeedbackReplyRequest request) {
		Response<PatientFeedbackResponse> response = new Response<>();
		PatientFeedbackResponse feedbacks = null;
		try {
			feedbacks = feedbackService.addPatientFeedbackReply(request);
			response.setData(feedbacks);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping
	(PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACK_FOR_MOBILE)
	@ApiOperation(value = PathProxy.FeedbackUrls.GET_PATIENT_FEEDBACK_FOR_MOBILE)
	public Response<PatientFeedbackIOSResponse> getPatientFeedback(@MatrixParam("services") List<String> services,
			@RequestParam("size") int size, @RequestParam("page") int page, @RequestParam("patientId") String patientId,
			@RequestParam("doctorId") String doctorId, @RequestParam("localeId") String localeId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded, @RequestParam("isApproved") Boolean isApproved,
			@RequestParam("type") String type) {
		Response<PatientFeedbackIOSResponse> response = new Response<>();
		List<PatientFeedbackIOSResponse> feedbacks = null;
		try {
			Integer count = feedbackService.countPatientFeedbackList(patientId, doctorId, localeId, locationId,
					hospitalId, type, services, discarded, isApproved);
			if (count > 0)
				feedbacks = feedbackService.getPatientFeedbackList(size, page, patientId, doctorId, localeId,
						locationId, hospitalId, type, services, discarded, isApproved);
			response.setDataList(feedbacks);
			response.setCount(count);
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

}
