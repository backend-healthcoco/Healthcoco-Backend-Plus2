package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.PrintSettings;

public interface PrintSettingsService {

	PrintSettings saveSettings(PrintSettings request, String printSettingType);

	List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId, int page,
			int size, String updatedTime, Boolean discarded);

	PrintSettings deletePrintSettings(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	String getPrintSettingsGeneralNote(String doctorId, String locationId, String hospitalId);

	public String uploadFile(MultipartFile file, String type);

	String uploadSignature(MultipartFile file);

	PrintSettings getSettingByType(String printFilter, String doctorId, String locationId, String hospitalId,
			Boolean discarded, String printSettingType);

	Boolean putSettingByType();

	String createBlankPrint(String patientId, String locationId, String hospitalId, String doctorId);

}
