package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.response.PatientTreatmentResponse;

public interface PatientTreatmentServices {

	TreatmentService addEditService(TreatmentService request);

	TreatmentServiceCost addEditServiceCost(TreatmentServiceCost request);

	PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request, Boolean isAppointmentAdd,
			String createdBy, Appointment appointment);

	PatientTreatmentResponse deletePatientTreatment(String treatmentId, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	PatientTreatmentResponse getPatientTreatmentById(String treatmentId);

	TreatmentService deleteService(String treatmentServiceId, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	TreatmentServiceCost deleteServiceCost(String treatmentServiceId, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	List<?> getServices(String type, String range, long page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded);

	PatientTreatmentResponse changePatientTreatmentStatus(String treatmentId, String doctorId, String locationId,
			String hospitalId, Treatment treatment);

	List<PatientTreatmentResponse> getPatientTreatments(long page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory, String status);

	List<PatientTreatment> getPatientTreatmentByIds(List<ObjectId> treatmentId, ObjectId visitId);

	public List<PatientTreatmentResponse> getPatientTreatmentByPatientId(long page, int size, String doctorId,
			String locationId, String hospitalId, String patientId, String updatedTime, Boolean discarded,
			Boolean inHistory, String status);

	void emailPatientTreatment(String treatmentId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	String downloadPatientTreatment(String treatmentId, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA);

	int getTreatmentsCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	public Integer genrateTreatmentCode();

	public TreatmentService addFavouritesToService(TreatmentService request, String createdBy);

	TreatmentService makeServiceFavourite(String serviceId, String doctorId, String locationId, String hospitalId);

	List<TreatmentService> getListBySpeciality(String speciality);

	PatientTreatmentResponse deletePatientTreatmentForWeb(String treatmentId, Boolean discarded);

	public List<TreatmentService> getTreatmentServices(List<ObjectId> idList);
}
