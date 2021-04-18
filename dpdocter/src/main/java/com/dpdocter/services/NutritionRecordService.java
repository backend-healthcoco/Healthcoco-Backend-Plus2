package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.NutritionRecord;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;

public interface NutritionRecordService {

	NutritionRecord addNutritionRecord(NutritionRecord request);

	NutritionRecord getNutritionRecord(String recordId);

	RecordsFile uploadNutritionRecord(MultipartFile file, MyFiileRequest request);

	List<NutritionRecord> getNutritionRecord(int page, int size, String patientId, String doctorId, String locationId,
			String hospitalId, String searchTerm, Boolean discarded,Boolean isNutrition);

	NutritionRecord deleteNutritionRecord(String recordId, Boolean discarded);

	public RecordsFile uploadNutritionRecord(MultipartFile file, DoctorLabReportUploadRequest request);

	Boolean updateShareWithPatient(String recordId);
}
