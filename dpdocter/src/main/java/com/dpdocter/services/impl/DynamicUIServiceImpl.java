package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.Slot;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.PrescriptionPermissionEnum;
import com.dpdocter.enums.ProfilePermissionEnum;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.services.DynamicUIService;
@Service
public class DynamicUIServiceImpl implements DynamicUIService{

	@Autowired
	DynamicUIRepository dynamicUIRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	DoctorRepository doctorRepository;
	
	@Override
	@Transactional
	public UIPermissions getAllPermissionForDoctor(String doctorId)
	{
		UIPermissions uiPermissions = null;
		Set<String> clinicalNotesPermissionsSet = new HashSet<String>();
		Set<String> prescriptionPermissionsSet = new HashSet<String>();
		Set<String> profilePermissionsSet = new HashSet<String>();
		Set<String> tabPermissionsSet = new HashSet<String>();
		DoctorCollection doctorCollection =doctorRepository.findByUserId(new ObjectId(doctorId));
		if(doctorCollection !=null)
		{
			uiPermissions = new UIPermissions();
			for(ObjectId speciality : doctorCollection.getSpecialities())
			{
				UIPermissions tempPermissions = getAllPermissionBySpeciality(String.valueOf(speciality));
				if(tempPermissions != null)
				{
					clinicalNotesPermissionsSet.addAll(tempPermissions.getClinicalNotesPermissions());
					prescriptionPermissionsSet.addAll(tempPermissions.getPrescriptionPermissions());
					profilePermissionsSet.addAll(tempPermissions.getProfilePermissions());
					tabPermissionsSet.addAll(tempPermissions.getTabPermissions());
				}
				uiPermissions.setClinicalNotesPermissions(new ArrayList<String>(clinicalNotesPermissionsSet));
				uiPermissions.setPrescriptionPermissions(new ArrayList<String>(prescriptionPermissionsSet));
				uiPermissions.setProfilePermissions(new ArrayList<String>(profilePermissionsSet));
				uiPermissions.setTabPermissions(new ArrayList<String>(tabPermissionsSet));
			}
		}
		return uiPermissions;
	}
	
	@Override
	@Transactional
	public DynamicUI getPermissionForDoctor(String doctorId)
	{
		DynamicUI dynamicUI = null;
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		if(dynamicUICollection != null)
		{
			BeanUtil.map(dynamicUICollection, dynamicUI);
			
		}
		
		return dynamicUI;
	}
	
	@Override
	@Transactional
	public DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest)
	{
		DynamicUI dynamicUI = null;
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(dynamicUIRequest.getDoctorId()));
		if(dynamicUICollection != null)
		{
			BeanUtil.map(dynamicUIRequest, dynamicUICollection);
			dynamicUICollection = dynamicUIRepository.save(dynamicUICollection);
			dynamicUI = new DynamicUI();
			BeanUtil.map(dynamicUICollection, dynamicUI);
		}
		else
		{
			dynamicUICollection = new DynamicUICollection();
			BeanUtil.map(dynamicUIRequest, dynamicUICollection);
			dynamicUICollection = dynamicUIRepository.save(dynamicUICollection);
			dynamicUI = new DynamicUI();
			BeanUtil.map(dynamicUICollection, dynamicUI);
		}
		return dynamicUI;
	}
	
	private UIPermissions getAllPermissionBySpeciality(String speciality)
	{
		UIPermissions uiPermissions = null;
		ArrayList<String> clinicalNotesPermission = null;
		ArrayList<String> prescriptionPermission = null;
		ArrayList<String> profilePermission = null;
		switch (SpecialityTypeEnum.valueOf(speciality.toUpperCase())) {
		case OPHTHALMOLOGIST:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			clinicalNotesPermission.add(OpthoPermissionEnums.OPTHO_CLINICAL_NOTES.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			prescriptionPermission.add(OpthoPermissionEnums.OPTHO_RX.getPermissions());
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			break;
		case PEDIATRICIAN:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			profilePermission.add(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			break;
		case GYNAECOLOGIST:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			profilePermission.add(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			break;
		default:
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
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
	    return Arrays.toString(ProfilePermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}
	
	
}
