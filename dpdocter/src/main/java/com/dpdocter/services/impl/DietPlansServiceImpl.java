package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DietPlan;
import com.dpdocter.beans.DietPlanJasperDetail;
import com.dpdocter.beans.DietPlanRecipeAddItem;
import com.dpdocter.beans.DietPlanRecipeItem;
import com.dpdocter.beans.DietPlanTemplate;
import com.dpdocter.beans.DietplanAddItem;
import com.dpdocter.beans.DietplanItem;
import com.dpdocter.beans.Language;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.RecipeAddItem;
import com.dpdocter.beans.RecipeItem;
import com.dpdocter.collections.DietPlanCollection;
import com.dpdocter.collections.DietPlanTemplateCollection;
import com.dpdocter.collections.IngredientCollection;
import com.dpdocter.collections.LanguageCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.RecipeCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DietPlanRepository;
import com.dpdocter.repository.DietPlanTemplateRepository;
import com.dpdocter.repository.IngredientRepository;
import com.dpdocter.repository.RecipeRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.DietPlansService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class DietPlansServiceImpl implements DietPlansService {

	private static Logger logger = Logger.getLogger(DietPlansServiceImpl.class.getName());
	@Autowired
	private DietPlanRepository dietPlanRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PatientVisitService patientVisitService;
	@Autowired
	private JasperReportService jasperReportService;
	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Value(value = "${jasper.print.nutrition.diet.plan.a4.fileName}")
	private String dietPlanA4FileName;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private DietPlanTemplateRepository dietPlanTemplateRepository;
	
	@Autowired
	private RecipeRepository recipeRepository;
	
	@Autowired
	private IngredientRepository ingredientRepository;
	
	@Override
	public DietPlan addEditDietPlan(DietPlan request) {
		DietPlan response = null;
		try {
			DietPlanCollection dietPlanCollection = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				dietPlanCollection = dietPlanRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (dietPlanCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " No Diet Plan found with Id ");
				}
				request.setCreatedBy(dietPlanCollection.getCreatedBy());
				request.setCreatedTime(dietPlanCollection.getCreatedTime());
				request.setUniquePlanId(dietPlanCollection.getUniquePlanId());
				request.setUpdatedTime(new Date());
				dietPlanCollection.setItems(new ArrayList<DietplanItem>());
				BeanUtil.map(request, dietPlanCollection);

			} else {
				dietPlanCollection = new DietPlanCollection();
				BeanUtil.map(request, dietPlanCollection);
				dietPlanCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
				dietPlanCollection.setCreatedTime(new Date());
				dietPlanCollection.setUniquePlanId(
						UniqueIdInitial.DIET_PLAN.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				dietPlanCollection.setUpdatedTime(new Date());

			}
			dietPlanCollection = dietPlanRepository.save(dietPlanCollection);
			response = new DietPlan();
			BeanUtil.map(dietPlanCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public List<DietPlan> getDietPlans(int page, int size, String patientId, String doctorId, String hospitalId,
			String locationId, long updatedTime, boolean discarded) {
		List<DietPlan> response = null;
		try {

			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTime));
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);
			if (!DPDoctorUtils.anyStringEmpty(patientObjectId))
				criteria.and("patientId").is(patientObjectId);

			if (!discarded) {
				criteria.and("discarded").is(discarded);
			}
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			AggregationResults<DietPlan> aggregationResults = mongoTemplate.aggregate(aggregation,
					DietPlanCollection.class, DietPlan.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plans : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plans : " + e.getCause().getMessage());
		}
		return response;
	}
	
	
	@Override
	public List<DietPlan> getDietPlansForPatient(int page, int size, String patientId, long updatedTime, boolean discarded) {
		List<DietPlan> response = null;
		try {

			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTime)).and("discarded").is(discarded);
			ObjectId patientObjectId = null;
			
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(patientObjectId))
				criteria.and("patientId").is(patientObjectId);
			
			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			
			AggregationResults<DietPlan> aggregationResults = mongoTemplate.aggregate(aggregation,
					DietPlanCollection.class, DietPlan.class);
			response = aggregationResults.getMappedResults();
			
			for (DietPlan dietPlan : response) {
				for (DietplanAddItem dietplanAddItem : dietPlan.getItems()) {
					for (DietPlanRecipeAddItem recipeAddItem : dietplanAddItem.getRecipes()) {
						recipeAddItem.setGeneralNutrients(null);
						recipeAddItem.setCarbNutrients(null);
						recipeAddItem.setLipidNutrients(null);
						recipeAddItem.setProteinAminoAcidNutrients(null);
						recipeAddItem.setMineralNutrients(null);
						recipeAddItem.setOtherNutrients(null);
						recipeAddItem.setVitaminNutrients(null);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plans : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plans : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DietPlan getDietPlanById(String planId, String languageId) {
		DietPlan response = null;
		try {
			response = mongoTemplate.findById(new ObjectId(planId), DietPlan.class, "diet_plan_cl");
			
			if(!DPDoctorUtils.anyStringEmpty(languageId)) {
				ObjectId languageObjectid = new ObjectId(languageId);
				for(DietplanAddItem dietplanAddItem : response.getItems()) {
					for(DietPlanRecipeAddItem recipeAddItem : dietplanAddItem.getRecipes()) {
						RecipeCollection recipeCollection = recipeRepository.findById(new ObjectId(recipeAddItem.getId())).orElse(null);

						if(recipeCollection.getMultilingualName() != null)
							recipeAddItem.setName(recipeCollection.getMultilingualName().getOrDefault(languageObjectid, recipeCollection.getName()));
						
						for(RecipeAddItem ingredients : recipeAddItem.getIngredients()) {
							IngredientCollection ingredientCollection = ingredientRepository.findById(new ObjectId(ingredients.getId())).orElse(null);
							if(ingredientCollection.getMultilingualName() != null) 
								ingredients.setName(ingredientCollection.getMultilingualName().getOrDefault(languageObjectid, ingredientCollection.getName()));		
						}
					}	
				}	
			}
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
			DietPlanCollection dietPlanCollection = dietPlanRepository.findById(new ObjectId(planId)).orElse(null);
			if (dietPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Diet Plan not found with Id");
			}
			dietPlanCollection.setAdminCreatedTime(new Date());
			dietPlanCollection.setDiscarded(discarded);
			dietPlanCollection = dietPlanRepository.save(dietPlanCollection);
			response = new DietPlan();
			BeanUtil.map(dietPlanCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public String downloadDietPlan(String planId) {
		String response = null;
		try {
			DietPlanCollection dietPlanCollection = dietPlanRepository.findById(new ObjectId(planId)).orElse(null);
			if (dietPlanCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Diet Plan not found with Id");
			}
			JasperReportResponse jasperReportResponse = createJasper(dietPlanCollection);
			if (jasperReportResponse != null)
				response = getFinalImageURL(jasperReportResponse.getPath());
			if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
				if (jasperReportResponse.getFileSystemResource().getFile().exists())
					jasperReportResponse.getFileSystemResource().getFile().delete();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plan : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plan : " + e.getCause().getMessage());

		}
		return response;
	}

	private JasperReportResponse createJasper(DietPlanCollection dietPlanCollection)
			throws IOException, ParseException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		List<DietPlanJasperDetail> dietPlanItems = null;
		DietPlanJasperDetail detail = null;
		JasperReportResponse response = null;
		String quantity = "";
		if (dietPlanCollection.getItems() != null && !dietPlanCollection.getItems().isEmpty()) {
			dietPlanItems = new ArrayList<DietPlanJasperDetail>();
			for (DietplanItem item : dietPlanCollection.getItems()) {
				detail = new DietPlanJasperDetail();
				detail.setTiming(item.getMealTiming() != null
						? (StringUtils.capitalize(item.getMealTiming().getTime())).replace("_", " ")
						: " ");
				for (DietPlanRecipeItem recipe : item.getRecipes()) {

					if (DPDoctorUtils.anyStringEmpty(detail.getRecipe())) {
						detail.setRecipe("<b>" + StringUtils.capitalize(recipe.getName() + "</b>"));
					} else {
						detail.setRecipe(detail.getRecipe() + "<br>" + "<b>"
								+ StringUtils.capitalize(recipe.getName() + "</b>"));
					}
					if (recipe.getQuantity() != null) {
						quantity = recipe.getQuantity().getValue() + " "
								+ (recipe.getQuantity().getType() != null ? recipe.getQuantity().getType() : "");
					}
					if (DPDoctorUtils.anyStringEmpty(detail.getQuantity())) {

						detail.setQuantity("<b>" + StringUtils.capitalize(quantity) + "</b>");

					} else {
						detail.setQuantity(
								detail.getQuantity() + "<br>" + "<b>" + StringUtils.capitalize(quantity) + "</b>");
					}
					for (RecipeItem recipeItem : recipe.getIngredients()) {

						if (!DPDoctorUtils.anyStringEmpty(recipeItem.getName())) {
							detail.setRecipe(
									detail.getRecipe() + "<br>" + StringUtils.capitalize(recipeItem.getName()));
						}

						if (recipeItem.getQuantity() != null) {
							quantity = recipeItem.getQuantity().getValue() + " "
									+ (recipeItem.getQuantity().getType() != null ? recipeItem.getQuantity().getType()
											: "");
							if (!DPDoctorUtils.anyStringEmpty(quantity)) {
								detail.setQuantity(detail.getQuantity() + "<br>" + StringUtils.capitalize(quantity));
							}
						}

					}

				}
				dietPlanItems.add(detail);
			}
		}
		parameters.put("items", dietPlanItems);
		parameters.put("title", "Diet Chart");
		PrintSettingsCollection printSettings = new PrintSettingsCollection();
		DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
		BeanUtil.map(defaultPrintSettings, printSettings);

		patientVisitService.generatePrintSetup(parameters, printSettings, dietPlanCollection.getDoctorId());

		String pdfName = "diet-plan-" + dietPlanCollection.getUniquePlanId() + new Date().getTime();
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getTopMargin() != null
						? printSettings.getPageSetup().getTopMargin()
						: 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getBottomMargin() != null
						? printSettings.getPageSetup().getBottomMargin()
						: 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;

		response = jasperReportService.createPDF(ComponentType.DIET_PLAN, parameters, dietPlanA4FileName, layout,
				pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	@Transactional
	public Boolean emailDietPlan(String emailAddress, String planId) {
		MailResponse mailResponse = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			mailResponse = createMailData(planId);
			String body = mailBodyGenerator.generateEMREmailBody("", "", "", "", sdf.format(new Date()), "Diet Plan",
					"emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress, "Healthcoco sent you Diet Plan", body,
					mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();

		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return true;
	}

	private MailResponse createMailData(String planId) {
		MailResponse response = null;
		MailAttachment mailAttachment = null;
		try {
			DietPlanCollection dietPlanCollection = dietPlanRepository.findById(new ObjectId(planId)).orElse(null);
			if (dietPlanCollection != null) {
				JasperReportResponse jasperReportResponse = createJasper(dietPlanCollection);
				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());

				response = new MailResponse();
				response.setMailAttachment(mailAttachment);

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("IST"));
				response.setMailRecordCreatedDate(sdf.format(new Date()));

			} else {
				logger.warn("Prescription not found.Please check prescriptionId.");
				throw new BusinessException(ServiceError.NoRecord,
						"Prescription not found.Please check prescriptionId.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
	
	@Override
	public DietPlanTemplate addEditDietPlanTemplate(DietPlanTemplate request) {
		DietPlanTemplate response = null;
		try {
			DietPlanTemplateCollection dietPlanTemplateCollection = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);

			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				dietPlanTemplateCollection = dietPlanTemplateRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (dietPlanTemplateCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, " No Diet Plan Template found with Id ");
				}
				dietPlanTemplateCollection.setCommunities(null);
				dietPlanTemplateCollection.setItems(null);
				dietPlanTemplateCollection.setPregnancyCategory(null);
				dietPlanTemplateCollection.setDiseases(null);
				dietPlanTemplateCollection.setMultilingualTemplateName(null);
				request.setCreatedBy(dietPlanTemplateCollection.getCreatedBy());
				request.setCreatedTime(dietPlanTemplateCollection.getCreatedTime());
				request.setUniquePlanId(dietPlanTemplateCollection.getUniquePlanId());
				request.setUpdatedTime(new Date());
				dietPlanTemplateCollection.setItems(new ArrayList<DietplanItem>());
				BeanUtil.map(request, dietPlanTemplateCollection);

			} else {
				dietPlanTemplateCollection = new DietPlanTemplateCollection();
				
				BeanUtil.map(request, dietPlanTemplateCollection);
				dietPlanTemplateCollection
						.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
								+ userCollection.getFirstName());
				dietPlanTemplateCollection.setCreatedTime(new Date());
				dietPlanTemplateCollection.setUniquePlanId(
						UniqueIdInitial.DIET_PLAN.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				dietPlanTemplateCollection.setUpdatedTime(new Date());

			}
			
			if(dietPlanTemplateCollection.getFromAge() != null) {
				double fromYears = dietPlanTemplateCollection.getFromAge().getYears() 
						+ (double)dietPlanTemplateCollection.getFromAge().getMonths()/12
						+ (double)dietPlanTemplateCollection.getFromAge().getDays()/365; 

				dietPlanTemplateCollection.setFromAgeInYears(fromYears);
			}

			if(dietPlanTemplateCollection.getToAge() != null) {
				double toYears = dietPlanTemplateCollection.getToAge().getYears() 
						+ (double)dietPlanTemplateCollection.getToAge().getMonths()/12
						+ (double)dietPlanTemplateCollection.getToAge().getDays()/365; 

				dietPlanTemplateCollection.setToAgeInYears(toYears);
			}


			dietPlanTemplateCollection = dietPlanTemplateRepository.save(dietPlanTemplateCollection);
			response = new DietPlanTemplate();
			BeanUtil.map(dietPlanTemplateCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while add edit Diet Plan Template : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while add edit Diet Plan Template: " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public Response<DietPlanTemplate> getDietPlanTemplates(int page, int size, String doctorId, String hospitalId, String locationId,
			long updatedTime, boolean discarded, String gender, String country, Double fromAge, Double toAge,
			String community, String type, String pregnancyCategory, String searchTerm,
			String foodPreference, List<String> disease, Double bmiFrom, Double bmiTo, String languageId, Double age, Double bmi, boolean allDisease) {
		Response<DietPlanTemplate> response = new Response<DietPlanTemplate>();
		List<DietPlanTemplate> dietPlanTemplates = null;
		try {

			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTime));
			
			if(allDisease) {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					criteria = criteria.andOperator(new Criteria().orOperator(new Criteria("templateName").regex("^" + searchTerm, "i"),
							new Criteria("templateName").regex(searchTerm)));
				}
			}else {
				if (disease!= null && !disease.isEmpty()) {
					criteria.and("diseases.disease").in(disease);
					if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
						criteria = criteria.andOperator(new Criteria().orOperator(new Criteria("templateName").regex("^" + searchTerm, "i"),
								new Criteria("templateName").regex(searchTerm)));
					}
				}else {
					List<String> emptyArr = new ArrayList<String>();
					if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
						criteria = criteria.andOperator(new Criteria().orOperator(new Criteria("templateName").regex("^" + searchTerm, "i"),
								new Criteria("templateName").regex(searchTerm)), new Criteria().orOperator(new Criteria("diseases").is(null), new Criteria("diseases").is(emptyArr)));
					}
					else criteria.andOperator(new Criteria().orOperator(new Criteria("diseases").is(null), new Criteria("diseases").is(emptyArr)));
				}
			}
			
			if (age != null) {
				fromAge = toAge = age;
			}
			
			if (bmi != null) {
				bmiFrom = bmiTo = bmi;
			}
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));
			
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			
			if (!discarded) 
				criteria.and("discarded").is(discarded);
			
			if (!DPDoctorUtils.anyStringEmpty(gender))
				criteria.and("gender").is(gender);
			
			if (!DPDoctorUtils.anyStringEmpty(country))
				criteria.and("country").is(country);
			
			if (fromAge!= null)
				criteria.and("fromAgeInYears").lte(fromAge);
			
			if (toAge!= null)
				criteria.and("toAgeInYears").gte(toAge);
			
			if (!DPDoctorUtils.anyStringEmpty(community))
				criteria.and("communities.value").is(community);
			
			if (!DPDoctorUtils.anyStringEmpty(type))
				criteria.and("type").is(type);
			
			if (!DPDoctorUtils.anyStringEmpty(pregnancyCategory))
				criteria.and("pregnancyCategory").is(pregnancyCategory);
			
			if (!DPDoctorUtils.anyStringEmpty(foodPreference))
				criteria.and("foodPreference").is(foodPreference);
			
			if (bmiFrom != null)
				criteria.and("bmiFrom").lte(bmiFrom);
			
			if (bmiTo != null)
				criteria.and("bmiTo").gte(bmiTo);
			
			int count = (int) mongoTemplate.count(new Query(criteria), DietPlanTemplateCollection.class);
			if(count > 0) {
				Aggregation aggregation = null;

//				if(!DPDoctorUtils.anyStringEmpty(languageId)) {
//					if (size > 0) {
//						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//								new CustomAggregationOperation(new Document("$set", new BasicDBObject(key, value)))
//								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
//								Aggregation.limit(size));
//					} else {
//						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
//								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
//
//					}
//				}else {
					if (size > 0) {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((long)(page) * size),
								Aggregation.limit(size));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

					}
//				}
				
				AggregationResults<DietPlanTemplate> aggregationResults = mongoTemplate.aggregate(aggregation,
						DietPlanTemplateCollection.class, DietPlanTemplate.class);
				dietPlanTemplates = aggregationResults.getMappedResults();
				response.setDataList(dietPlanTemplates);
			}
			response.setCount(count);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plan Templates : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plan Templates : " + e.getCause().getMessage());
		}
		return response;
	}
	
	@Override
	public DietPlanTemplate getDietPlanTemplateById(String planId, String languageId) {
		DietPlanTemplate response = null;
		try {
			DietPlanTemplateCollection dietPlanTemplateCollection = dietPlanTemplateRepository.findById(new ObjectId(planId)).orElse(null);
			response = new DietPlanTemplate();
			BeanUtil.map(dietPlanTemplateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while getting Diet Plan Template: " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while getting Diet Plan Template: " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public DietPlanTemplate deleteDietPlanTemplate(String planId, Boolean discarded) {
		DietPlanTemplate response = null;
		try {
			DietPlanTemplateCollection dietPlanTemplateCollection = dietPlanTemplateRepository.findById(new ObjectId(planId)).orElse(null);
			if (dietPlanTemplateCollection == null) {
				throw new BusinessException(ServiceError.NotFound, "Diet Plan Template not found with Id");
			}
			dietPlanTemplateCollection.setAdminCreatedTime(new Date());
			dietPlanTemplateCollection.setDiscarded(discarded);
			dietPlanTemplateCollection = dietPlanTemplateRepository.save(dietPlanTemplateCollection);
			response = new DietPlanTemplate();
			BeanUtil.map(dietPlanTemplateCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Diet Plan Template: " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Diet Plan Template: " + e.getCause().getMessage());

		}
		return response;
	}

	@Override
	public DietPlanTemplate updateDietPlanTemplate() {
		try {
			List<DietPlanTemplateCollection> dietPlanTemplateCollections = dietPlanTemplateRepository.findAll();
				for(DietPlanTemplateCollection dietPlanTemplateCollection : dietPlanTemplateCollections) {
					if(dietPlanTemplateCollection.getFromAge() != null) {
						double fromYears = dietPlanTemplateCollection.getFromAge().getYears() 
								+ (double)dietPlanTemplateCollection.getFromAge().getMonths()/12
								+ (double)dietPlanTemplateCollection.getFromAge().getDays()/365; 

						dietPlanTemplateCollection.setFromAgeInYears(fromYears);
					}

					if(dietPlanTemplateCollection.getToAge() != null) {
						double toYears = dietPlanTemplateCollection.getToAge().getYears() 
								+ (double)dietPlanTemplateCollection.getToAge().getMonths()/12
								+ (double)dietPlanTemplateCollection.getToAge().getDays()/365; 

						dietPlanTemplateCollection.setToAgeInYears(toYears);
					}


					dietPlanTemplateCollection = dietPlanTemplateRepository.save(dietPlanTemplateCollection);
				}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error while deleting Diet Plan Template: " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					" Error while deleting Diet Plan Template: " + e.getCause().getMessage());

		}
		return null;
	}
	
	@Override
	public Integer countLanguage(Boolean discarded, String searchTerm) {
		Integer response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
					new Criteria("name").regex("^" + searchTerm));

			response = (int) mongoTemplate.count(new Query(criteria), LanguageCollection.class);
		} catch (BusinessException e) {
			logger.error("Error while counting language " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting language " + e.getMessage());

		}
		return response;
	}
	
	@Override
	public List<Language> getLanguages(int size, int page, Boolean discarded, String searchTerm) {
		List<Language> response = null;
		try {
			Criteria criteria = new Criteria("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("disease").regex("^" + searchTerm, "i"),
						new Criteria("disease").regex("^" + searchTerm));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((long)page * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(aggregation, LanguageCollection.class, Language.class)
					.getMappedResults();
		} catch (BusinessException e) {
			logger.error("Error while getting language " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting actilanguagevity " + e.getMessage());

		}
		return response;

	}

}
