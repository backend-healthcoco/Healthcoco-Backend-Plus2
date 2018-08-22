package com.dpdocter.services;

import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.request.LabPrintContentRequest;

public interface LabPrintServices {

	public LabPrintSetting addEditPrintSetting(LabPrintSetting request);

	public LabPrintSetting getLabPrintSetting(String locationId, String hospitalId);

	public LabPrintSetting setHeaderAndFooterSetup(LabPrintContentRequest request, String type);

}
