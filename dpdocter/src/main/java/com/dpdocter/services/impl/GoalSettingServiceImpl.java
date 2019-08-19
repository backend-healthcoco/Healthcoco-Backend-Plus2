package com.dpdocter.services.impl;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ConsultationCall;
import com.dpdocter.beans.GoalSetting;
import com.dpdocter.collections.ConsultationCallCollection;
import com.dpdocter.collections.GoalSettingCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.ConsultationCallRepository;
import com.dpdocter.repository.GoalSettingsRepository;
import com.dpdocter.services.GoalSettingService;

import common.util.web.DPDoctorUtils;

@Service
public class GoalSettingServiceImpl implements GoalSettingService {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	GoalSettingsRepository goalSettingsRepository;
	
	@Autowired
	ConsultationCallRepository consultationCallRepository;
	
	@Override
	@Transactional
	public GoalSetting addEditGoalSetting(GoalSetting goalSetting) {
		GoalSetting response = null;
		GoalSettingCollection goalSettingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(goalSetting.getId())) {
				goalSettingCollection = goalSettingsRepository
						.findOne(new ObjectId(goalSetting.getId()));
				if(goalSettingCollection == null)
				{
					goalSettingCollection = new GoalSettingCollection();
					goalSetting.setCreatedTime(new Date());
				}
				
				BeanUtil.map(goalSetting, goalSettingCollection);
				goalSettingCollection = goalSettingsRepository.save(goalSettingCollection);
				response = new GoalSetting();
				BeanUtil.map(goalSettingCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.InvalidInput , " Id cannot be null");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}

		return response;
	}
	
	@Override
	@Transactional
	public GoalSetting getGoalSetting(String patientId) {
		GoalSetting response = null;
		GoalSettingCollection goalSettingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				goalSettingCollection = goalSettingsRepository
						.findByPatientId(new ObjectId(patientId));
				if(goalSettingCollection == null)
				{
					throw new BusinessException(ServiceError.NoRecord , "Record not found");
				}
				
				response = new GoalSetting();
				BeanUtil.map(goalSettingCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.InvalidInput , "Patient Id cannot be null");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}

		return response;
	}

	
	@Override
	@Transactional
	public ConsultationCall addEditConsultationCall(ConsultationCall consultationCall) {
		ConsultationCall response = null;
		ConsultationCallCollection consultationCallCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(consultationCall.getId())) {
				consultationCallCollection = consultationCallRepository
						.findOne(new ObjectId(consultationCall.getId()));
				if(consultationCallCollection == null)
				{
					consultationCallCollection = new ConsultationCallCollection();
					consultationCall.setCreatedTime(new Date());
				}
				
				BeanUtil.map(consultationCall, consultationCallCollection);
				consultationCallCollection = consultationCallRepository.save(consultationCallCollection);
				response = new ConsultationCall();
				BeanUtil.map(consultationCallCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.InvalidInput , " Id cannot be null");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}

		return response;
	}
	
	@Override
	@Transactional
	public ConsultationCall getConsultationCall(String id) {
		ConsultationCall response = null;
		ConsultationCallCollection goalSettingCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				goalSettingCollection = consultationCallRepository
						.findOne(new ObjectId(id));
				if(goalSettingCollection == null)
				{
					throw new BusinessException(ServiceError.NoRecord , "Record not found");
				}
				
				response = new ConsultationCall();
				BeanUtil.map(goalSettingCollection, response);
			}
			else
			{
				throw new BusinessException(ServiceError.InvalidInput , "Patient Id cannot be null");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}

		return response;
	}
	
	
}
