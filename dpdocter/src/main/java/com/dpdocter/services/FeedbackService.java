package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AppointmentGeneralFeedback;
import com.dpdocter.beans.PharmacyFeedback;
import com.dpdocter.beans.PrescriptionFeedback;
import com.dpdocter.request.FeedbackGetRequest;
import com.dpdocter.request.PrescriptionFeedbackRequest;
import com.dpdocter.request.pharmacyFeedbackRequest;

public interface FeedbackService {

	AppointmentGeneralFeedback addEditAppointmentGeneralFeedback(AppointmentGeneralFeedback feedback);

	PrescriptionFeedback addEditPrescriptionFeedback(PrescriptionFeedbackRequest feedback);

	PharmacyFeedback addEditPharmacyFeedback(pharmacyFeedbackRequest feedback);

	List<AppointmentGeneralFeedback> getAppointmentGeneralFeedbackList(FeedbackGetRequest request);

	List<PrescriptionFeedback> getPrescriptionFeedbackList(FeedbackGetRequest request);

	List<PharmacyFeedback> getPharmacyFeedbackList(FeedbackGetRequest request);

}
