package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DietPlan;
import com.dpdocter.beans.DietPlanTemplate;
import com.dpdocter.beans.Language;

import common.util.web.Response;

public interface DietPlansService {

	public DietPlan addEditDietPlan(DietPlan request);

	public DietPlan getDietPlanById(String planId, String languageId);

	public DietPlan discardDietPlan(String planId, Boolean discarded);

	List<DietPlan> getDietPlans(int page, int size, String patientId, String doctorId, String hospitalId,
			String locationId, long updatedTime, boolean discarded);

	public String downloadDietPlan(String planId);

	public Boolean emailDietPlan(String emailAddress, String planId);

	public List<DietPlan> getDietPlansForPatient(int page, int size, String patientId, long updatedTime, boolean discarded);

	public DietPlanTemplate addEditDietPlanTemplate(DietPlanTemplate request);

	public Response<DietPlanTemplate> getDietPlanTemplates(int page, int size, String doctorId, String hospitalId, String locationId,
			long updatedTime, boolean discarded, String gender, String country, Double fromAge, Double toAge,
			String community, String type, String pregnancyCategory, String searchTerm, String foodPreference, List<String> disease, Double bmiFrom, Double bmiTo, String languageId, Double age, Double bmi, boolean allDisease);

	public DietPlanTemplate deleteDietPlanTemplate(String planId, Boolean discarded);

	public DietPlanTemplate getDietPlanTemplateById(String planId, String languageId);

	public DietPlanTemplate updateDietPlanTemplate();

	public Integer countLanguage(Boolean discarded, String searchTerm);

	public List<Language> getLanguages(int size, int page, Boolean discarded, String searchTerm);

}
