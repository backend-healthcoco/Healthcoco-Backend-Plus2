package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.request.FeedbackGetRequest;

public interface FeedbackService {

	AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback);

	PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedback feedback);

	PharmacyFeedback addEditPharmacyFeedback(PharmacyFeedback feedback);

	List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request);

	List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request);

	List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request);

}
