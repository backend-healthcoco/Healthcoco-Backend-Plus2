package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DynamicUIRequest;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.DynamicUIEnum;
import com.dpdocter.enums.HistoryPermissionEnum;
import com.dpdocter.enums.PrescriptionPermissionEnum;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.services.DynamicUIService;
@Service
public class DynamicUIServiceImpl implements DynamicUIService{

	@Autowired
	DynamicUIRepository dynamicUIRepository;
	
	@Transactional
	public UIPermissions getAllPermissionForDoctor(String doctorId)
	{
		return null;
	}
	
	@Transactional
	public void getPermissionForDoctor(String doctorId)
	{
		
	}
	
	@Transactional
	public void postPermissions(DynamicUIRequest dynamicUIRequest)
	{
		
	}
	
	private UIPermissions getAllPermissionBySpeciality(SpecialityTypeEnum typeEnum)
	{
		UIPermissions uiPermissions = null;
		ArrayList<String> clinicalNotesPermission = null;
		ArrayList<String> prescriptionPermission = null;
		ArrayList<String> historyPermission = null;
		switch (typeEnum) {
		case OPHTHALMOLOGIST:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			historyPermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setHistoryPermissions(historyPermission);
			break;

		default:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			historyPermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setHistoryPermissions(historyPermission);
			break;
		}
		return uiPermissions;
	}
	
	/*private List<String> initailizeGeneralList()
	{
		SpecialityTypeEnum[] specialityTypeEnums = values();
		
		return null;
	}*/
	
	private String[] clinicalNotesPermission() {
	    return Arrays.toString(ClinicalNotesPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	private String[] prescriptionPermission() {
	    return Arrays.toString(PrescriptionPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	private String[] historyPermission() {
	    return Arrays.toString(HistoryPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	
}
