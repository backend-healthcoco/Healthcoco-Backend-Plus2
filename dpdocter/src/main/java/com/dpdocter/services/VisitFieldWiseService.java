package com.dpdocter.services;

import common.util.web.Response;

public interface VisitFieldWiseService {

	Response<Object> getComplaintData(String doctorId, String locationId, String hospitalId, String patientId);

	Response<Object> getAdmitCardData(String doctorId, String locationId, String hospitalId, String patientId,
			String type);

	Response<Object> getOperationNotesData(String doctorId, String locationId, String hospitalId, String patientId,
			String type);

	Response<Object> getCountOperationNotesData(String doctorId, String locationId, String hospitalId,
			String patientId);

	Response<Object> getCountAdmitCardData(String doctorId, String locationId, String hospitalId, String patientId);

}
