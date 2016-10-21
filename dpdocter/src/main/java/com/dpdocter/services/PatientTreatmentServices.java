package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.TreatmentServiceCost;
import com.dpdocter.request.PatientTreatmentAddEditRequest;
import com.dpdocter.response.PatientTreatmentResponse;

public interface PatientTreatmentServices {

	TreatmentService addEditService(TreatmentService request);

	TreatmentServiceCost addEditServiceCost(TreatmentServiceCost request);

	PatientTreatmentResponse addEditPatientTreatment(PatientTreatmentAddEditRequest request);

	boolean deletePatientTreatment(String treatmentId, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	PatientTreatmentResponse getPatientTreatmentById(String treatmentId);

	TreatmentService deleteService(String treatmentServiceId, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	TreatmentServiceCost deleteServiceCost(String treatmentServiceId, String doctorId, String locationId,
			String hospitalId, Boolean discarded);

	List<?> getServices(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded);

	PatientTreatmentResponse changePatientTreatmentStatus(String treatmentId, String doctorId, String locationId,
			String hospitalId, Treatment treatment);

	List<PatientTreatmentResponse> getPatientTreatments(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory, String status);

	List<PatientTreatment> getPatientTreatmentByIds(List<ObjectId> treatmentId);

	public List<PatientTreatmentResponse> getPatientTreatmentByPatientId(int page, int size, String doctorId,
			String locationId, String hospitalId, String patientId, String updatedTime, Boolean discarded,
			Boolean inHistory, String status);
}
