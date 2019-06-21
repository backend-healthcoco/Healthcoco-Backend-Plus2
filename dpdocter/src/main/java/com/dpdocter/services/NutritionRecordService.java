package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.NutritionRecord;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface NutritionRecordService {

	NutritionRecord addNutritionRecord(NutritionRecord request);

	NutritionRecord getNutritionRecord(String recordId);

	RecordsFile uploadNutritionRecord(FormDataBodyPart file, MyFiileRequest request);

	List<NutritionRecord> getNutritionRecord(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, String searchTerm, Boolean discarded,Boolean isNutrition);

	NutritionRecord deleteNutritionRecord(String recordId, Boolean discarded);

	public RecordsFile uploadNutritionRecord(DoctorLabReportUploadRequest request);

	Boolean updateShareWithPatient(String recordId);
}
