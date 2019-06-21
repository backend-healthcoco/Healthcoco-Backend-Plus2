package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.DailyImprovementFeedback;
import com.dpdocter.beans.PatientFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.request.DailyImprovementFeedbackRequest;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientFeedbackReplyRequest;
import com.dpdocter.request.PatientFeedbackRequest;
import com.dpdocter.request.PharmacyFeedbackRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.response.DailyImprovementFeedbackResponse;
import com.dpdocter.response.PatientFeedbackIOSResponse;
import com.dpdocter.response.PatientFeedbackResponse;

public interface FeedbackService {

	AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback);

	PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedbackRequest feedback);

	PharmacyFeedback addEditPharmacyFeedback(PharmacyFeedbackRequest feedback);

	List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request);

	List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request);

	List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request);

	// DailyImprovementFeedback
	// addEditDailyImprovementFeedback(DailyImprovementFeedback feedback);

	PatientFeedback addEditPatientFeedback(PatientFeedbackRequest feedback);

	List<PatientFeedbackResponse> getPatientFeedbackList(FeedbackGetRequest request, String type);

	List<DailyImprovementFeedbackResponse> getDailyImprovementFeedbackList(String prescriptionId, String doctorId,
			String locationId, String hospitalId, int page, int size);

	PatientFeedbackResponse addPatientFeedbackReply(PatientFeedbackReplyRequest request);

	DailyImprovementFeedback addEditDailyImprovementFeedback(DailyImprovementFeedbackRequest feedback);

	public List<PatientFeedbackIOSResponse> getPatientFeedbackList(int size, int page, String patientId,
			String doctorId, String localeId, String locationId, String hospitalId, String type, List<String> services,
			Boolean discarded, Boolean isApproved);

	public Integer countPatientFeedbackList(String patientId, String doctorId, String localeId, String locationId,
			String hospitalId, String type, List<String> services, Boolean discarded, Boolean isApproved);
}
