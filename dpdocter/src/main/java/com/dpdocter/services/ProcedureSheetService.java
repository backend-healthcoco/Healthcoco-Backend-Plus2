package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.AddEditProcedureSheetRequest;
import com.dpdocter.request.AddEditProcedureSheetStructureRequest;
import com.dpdocter.response.ProcedureSheetResponse;
import com.dpdocter.response.ProcedureSheetStructureResponse;

public interface ProcedureSheetService {

	ProcedureSheetResponse addEditProcedureSheet(AddEditProcedureSheetRequest request);

	ProcedureSheetResponse getProcedureSheet(String id);

	ProcedureSheetResponse discardProcedureSheet(String id, Boolean discarded);

	ProcedureSheetStructureResponse addEditProcedureSheetStructure(AddEditProcedureSheetStructureRequest request);

	ProcedureSheetStructureResponse discardProcedureSheetStructure(String id, Boolean discarded);

	ProcedureSheetStructureResponse getProcedureSheetStructure(String id);

	List<ProcedureSheetResponse> getProcedureSheetList(String doctorId, String hospitalId, String locationId,
			String patientId, String searchTerm, Long from, Long to, Boolean discarded, int page, int size);

	List<ProcedureSheetStructureResponse> getProcedureSheetStructureList(String doctorId, String hospitalId,
			String locationId, String searchTerm, Long from, Long to, Boolean discarded, int page, int size);

}
