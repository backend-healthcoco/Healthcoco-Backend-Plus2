package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.DailyImprovementFeedback;
import com.dpdocter.beans.PatientFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PatientFeedbackRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.response.PatientFeedbackResponse;
import com.dpdocter.request.PharmacyFeedbackRequest;

public interface FeedbackService {

	AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback);

	PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedbackRequest feedback);

	PharmacyFeedback addEditPharmacyFeedback(PharmacyFeedbackRequest feedback);

	List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request);

	List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request);

	List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request);

	DailyImprovementFeedback addEditDailyImprovementFeedback(DailyImprovementFeedback feedback);

	List<DailyImprovementFeedback> getDailyImprovementFeedbackList(FeedbackGetRequest request);

	PatientFeedback addEditPatientFeedback(PatientFeedbackRequest feedback);

	List<PatientFeedbackResponse> getPatientFeedbackList(FeedbackGetRequest request);

}
