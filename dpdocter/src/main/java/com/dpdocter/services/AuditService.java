package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.AuditTrailData;
import com.dpdocter.enums.AuditActionType;

public interface AuditService {

	public void addAuditData(AuditActionType createAppointment,  String dataViewId,String dataId, String patientId, String doctorId,
			String locationId, String hospitalId);

	public List<AuditTrailData> getAuditTrailAppointmentData(String locationId, String hospitalId, String to,
			String from, int page, int size);

}
