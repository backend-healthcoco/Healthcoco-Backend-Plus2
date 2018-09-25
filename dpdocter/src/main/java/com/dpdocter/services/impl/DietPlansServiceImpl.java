package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.DietPlanRepository;
import com.dpdocter.services.DietPlansService;

@Service
public class DietPlansServiceImpl implements DietPlansService {
	private static Logger logger = Logger.getLogger(DietPlansServiceImpl.class.getName());
	@Autowired
	private DietPlanRepository dietPlanRepository;

	@Override
	public DietPlan addEditDietPlan(DietPlan request) {
		DietPlan response = null;
		try {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<DietPlan> getDietPlans(int page, int size, String doctorId, String hospitalId, String locationId,
			String updatedTime, boolean discarded) {
		List<DietPlan> response = null;
		try {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plans : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plans : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public DietPlan getDietPlanById(String planId) {
		DietPlan response = null;
		try {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public DietPlan discardDietPlan(String planId, Boolean discarded) {
		DietPlan response = null;
		try {
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

}
