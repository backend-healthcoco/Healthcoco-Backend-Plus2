package com.dpdocter.services.impl;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ProcedureSheet;
import com.dpdocter.collections.ProcedureSheetCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ProcedureSheetRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditProcedureSheetRequest;
import com.dpdocter.services.ProcedureSheetService;

import common.util.web.DPDoctorUtils;

@Service
public class ProcedureSheetServiceImpl implements ProcedureSheetService{

	@Autowired
	private ProcedureSheetRepository procedureSheetRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public ProcedureSheet addEditProcedureSheet(AddEditProcedureSheetRequest request)
	{
		ProcedureSheet response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				procedureSheetCollection = procedureSheetRepository.findOne(new ObjectId(request.getId()));
			} else {
				procedureSheetCollection = new ProcedureSheetCollection();
				procedureSheetCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				procedureSheetCollection.setCreatedBy(userCollection.getFirstName());
			}
			BeanUtil.map(request, procedureSheetCollection);
			procedureSheetCollection = procedureSheetRepository.save(procedureSheetCollection);
			if (procedureSheetCollection != null) {
				BeanUtil.map(procedureSheetCollection, response);
			} 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
}
