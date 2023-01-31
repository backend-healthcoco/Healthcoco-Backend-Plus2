package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.BabyNote;
import com.dpdocter.beans.Cement;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.Implant;
import com.dpdocter.beans.LabourNote;
import com.dpdocter.beans.OperationNote;
import com.dpdocter.request.AddEditFlowSheetRequest;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.response.FlowsheetResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface DischargeSummaryService {

	DischargeSummaryResponse addEditDischargeSummary(DischargeSummaryRequest dischargeSummary);

	// List<DischargeSummary> getAllDischargeSummary();

	List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, long page, int size, String updatedTime);

	int getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	DischargeSummaryResponse viewDischargeSummary(String summaryId);

	Boolean deleteDischargeSummary(String dischargeSummeryId, String doctorId, String hospitalId,
			String locationId, Boolean discarded);

	String downloadDischargeSummary(String dischargeSummeryId);

	void emailDischargeSummary(String dischargeSummeryId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	DischargeSummaryResponse addMultiVisit(List<String> visitIds);

	public Integer upadateDischargeSummaryData();

	public LabourNote addEditLabourNote(LabourNote labourNote);

	public LabourNote deleteLabourNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	public BabyNote addEditBabyNote(BabyNote babyNote);

	public BabyNote deleteBabyNote(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	public OperationNote deleteOperationNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded);

	public OperationNote addEditOperationNote(OperationNote operationNote);

	public List<?> getDischargeSummaryItems(String type, String range, long page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm);

	public Implant addEditImplant(Implant implant);

	public Implant deleteImplant(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	public Cement addEditCement(Cement cement);

	public Cement deleteCement(String id, String doctorId, String locationId, String hospitalId, Boolean discarded);

	void emailDischargeSummaryForWeb(String dischargeSummeryId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	FlowsheetResponse addEditFlowSheets(AddEditFlowSheetRequest request,Boolean isFlowsheet);

	List<FlowsheetResponse> getFlowSheets(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, String updatedTime, Boolean discarded);

	FlowsheetResponse getFlowSheetsById(String id);

	public String downloadFlowSheet(String id, Boolean byFlowsheetId);

	Diagram addEditDiagram(Diagram diagram);

	String uploadDischargeDiagram(DoctorLabReportUploadRequest request);

	String uploadDischargeSummaryMultipart(FormDataBodyPart file);

	Boolean deleteFlowSheetsById(String id, Boolean discarded);

}
