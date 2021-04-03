package com.dpdocter.services.v2;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.v2.PatientTreatment;
import com.dpdocter.response.v2.PatientTreatmentResponse;

public interface PatientTreatmentServices {


	List<PatientTreatmentResponse> getPatientTreatments(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified,String from,String to, Boolean discarded,
			Boolean inHistory, String status);

	List<PatientTreatment> getPatientTreatmentByIds(List<ObjectId> treatmentId, ObjectId visitId);

	List<PatientTreatmentResponse> getPatientTreatmentsNEWCODE(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, String from, String to,
			Boolean discarded, Boolean inHistory, String status);

}
