package com.dpdocter.services;

import com.dpdocter.beans.ProcedureSheet;
import com.dpdocter.request.AddEditProcedureSheetRequest;

public interface ProcedureSheetService {

	ProcedureSheet addEditProcedureSheet(AddEditProcedureSheetRequest request);

}
