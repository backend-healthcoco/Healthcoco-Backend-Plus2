package com.dpdocter.response;

import java.util.List;
import java.util.Map;

import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.collections.GenericCollection;

public class ProcedureSheetResponse extends GenericCollection{

	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private ProcedureConsentForm procedureConsentForm;
	private List<ImageURLResponse> diagrams;
	private Map<String, ProcedureConsentFormFields> procedureSheetFields;
	private Boolean discarded = false;
	
}
