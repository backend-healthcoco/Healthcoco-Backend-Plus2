package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LabPrintDocument;
import com.dpdocter.beans.LabPrintSetting;
import com.dpdocter.request.LabPrintContentRequest;
import com.dpdocter.request.LabPrintDocumentAddEditRequest;

public interface LabPrintServices {

	public LabPrintSetting addEditPrintSetting(LabPrintSetting request);

	public LabPrintSetting getLabPrintSetting(String locationId, String hospitalId);

	public LabPrintSetting setHeaderAndFooterSetup(LabPrintContentRequest request, String type);

	public List<LabPrintDocument> getLabPrintDocuments(int page, int size, String locationId, String doctorId,
			String hospitalId, String searchTerm, Boolean isParent, Long from, Long to, Boolean discarded);

	public LabPrintDocument getLabPrintDocument(String labPrintDocumentId);

	public LabPrintDocument addEditDocument(LabPrintDocumentAddEditRequest request);

	public Boolean deleteLabPrintDocument(String id, boolean discarded);

}
