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
	
	private void getAllPermissionBySpeciality(SpecialityTypeEnum typeEnum)
	{
		UIPermissions uiPermissions;
		switch (typeEnum) {
		case OPHTHALMOLOGIST:
			uiPermissions = new UIPermissions();
			uiPermissions.setClinicalNotesPermissions(new ArrayList<String>(Arrays.asList(clinicalNotesPermission())));
			
			break;

		default:
			break;
		}
	}
	
	/*private List<String> initailizeGeneralList()
	{
		SpecialityTypeEnum[] specialityTypeEnums = values();
		
		return null;
	}*/
	
	private String[] clinicalNotesPermission() {
	    return Arrays.toString(ClinicalNotesPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	
}
