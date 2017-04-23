package com.dpdocter.services;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DischargeSummary;

public interface DischargeSummaryService {

	DischargeSummary addEditDischargeSummary(DischargeSummary dischargeSummary);

	// List<DischargeSummary> getAllDischargeSummary();

	List<DischargeSummary> getDischargeSummary(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, String updatedTime);

	Integer getDischargeSummaryCount(String doctorId, String locationId, String hospitalId, String patientId,
			String updatedTime);

	DischargeSummary viewDischargeSummary(String summaryId);

}
