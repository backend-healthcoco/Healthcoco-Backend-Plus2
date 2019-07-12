package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Clinic;
import com.dpdocter.beans.Records;

public interface LabService {
	List<Clinic> getClinicWithReportCount(String doctorId, String locationId, String hospitalId);
	
	List<Clinic> getLabWithReportCount(String doctorId, String locationId, String hospitalId);

	List<Records> getReports(String doctorId, String locationId, String hospitalId, String prescribedByDoctorId,
			String prescribedByLocationId, String prescribedByHospitalId, int size, long page);

}
