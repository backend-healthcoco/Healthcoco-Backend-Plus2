package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.MedicineTreatmentSheetRequest;
import com.dpdocter.response.MedicineTreatmentSheetResponse;

public interface MedicineTreatmentService {


	MedicineTreatmentSheetResponse addEditMedicinetreatmentSheet(MedicineTreatmentSheetRequest request);

	Boolean deleteMedicineSheet(String medicineSheetId, String doctorId, String hospitalId, String locationId,
			Boolean discarded);

	MedicineTreatmentSheetResponse getMedicineSheetById(String id);

	List<MedicineTreatmentSheetResponse> getMedicineSheet(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, Boolean discarded);

}
