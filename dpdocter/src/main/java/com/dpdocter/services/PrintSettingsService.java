package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PrintSettings;

public interface PrintSettingsService {

    PrintSettings saveSettings(PrintSettings request);

    List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId, int page, int size, String updatedTime,
	    Boolean discarded);

    PrintSettings deletePrintSettings(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	String getPrintSettingsGeneralNote(String doctorId, String locationId, String hospitalId);

}
