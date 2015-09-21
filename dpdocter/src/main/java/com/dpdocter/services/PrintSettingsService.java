package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PrintSettings;
import com.dpdocter.beans.PrintSettingsDefaultData;

public interface PrintSettingsService {

    PrintSettingsDefaultData saveDefaultSettings(PrintSettingsDefaultData request);

    List<PrintSettingsDefaultData> getDefaultSettings();

    PrintSettings saveSettings(PrintSettings request);

    List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId, int page, int size, String updatedTime,
	    Boolean discarded);

}
