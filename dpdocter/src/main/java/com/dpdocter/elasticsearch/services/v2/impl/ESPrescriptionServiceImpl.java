package com.dpdocter.elasticsearch.services.v2.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.v2.DrugType;
import com.dpdocter.elasticsearch.beans.v2.DrugDocument;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.services.v2.ESPrescriptionService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.services.InventoryService;

import common.util.web.DPDoctorUtils;

@Service(value = "ESPrescriptionServiceImplV2")
public class ESPrescriptionServiceImpl implements ESPrescriptionService {

	private static Logger logger = Logger.getLogger(ESPrescriptionServiceImpl.class.getName());


	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	TransportClient transportClient;

	@Autowired
	InventoryService inventoryService;



	@Override
	public List<?> searchDrug(String range, int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm, String category, Boolean searchByGenericName) {
		List<?> response = null;
		List<ESDrugDocument> esDrugDocuments = null;
		List<DrugDocument> drugDocuments = null;
		if (page > 0)
			return response;
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			searchTerm = searchTerm.toUpperCase();
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			esDrugDocuments = getGlobalDrugs(page, size, updatedTime, discarded, searchTerm, category,
					searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case CUSTOM:
			esDrugDocuments = getCustomDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case BOTH:
			esDrugDocuments = getCustomGlobalDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrug(esDrugDocuments);
			break;
		case FAVOURITES:
			drugDocuments = getFavouritesDrugs(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm, category, searchByGenericName);
			response = addStockToDrugWeb(drugDocuments);
			break;
		case WEBBOTH:
			drugDocuments = getCustomGlobalDrugsForWeb(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm, category, searchByGenericName);
			response = addStockToDrugWeb(drugDocuments);
			break;
		default:
			break;
		}
		return response;

	}

	private List<DrugDocument> getCustomGlobalDrugsForWeb(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<DrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<DrugDocument>();
			if (doctorId == null)
				response = new ArrayList<DrugDocument>();
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
							"genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
				}

				List<ESDrugDocument> esDrugDocuments = elasticsearchTemplate.queryForList(searchQuery,
						ESDrugDocument.class);

				if (esDrugDocuments != null) {
					esDrugDocuments = new ArrayList<ESDrugDocument>(new LinkedHashSet<ESDrugDocument>(esDrugDocuments));
				}
				response = new ArrayList<DrugDocument>();
				for (ESDrugDocument esDrugDocument : esDrugDocuments) {
					String drugTypeStr = esDrugDocument.getDrugType();
					esDrugDocument.setDrugType(null);
					DrugDocument drugDocument = new DrugDocument();
					BeanUtil.map(esDrugDocument, drugDocument);
					DrugType drugType = new DrugType();
					drugType.setId(esDrugDocument.getDrugTypeId());
					drugType.setType(drugTypeStr);
					drugDocument.setDrugType(drugType);
					response.add(drugDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<DrugDocument> getFavouritesDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<DrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<DrugDocument>();
			if (doctorId == null)
				response = new ArrayList<DrugDocument>();
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					if(size > 0)
						searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
								updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
					else
						searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
								updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					if(size > 0)
						searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
								updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
					else 
						searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
								updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}

				List<ESDrugDocument> esDrugDocuments = elasticsearchTemplate.queryForList(searchQuery,
						ESDrugDocument.class);

				response = new ArrayList<DrugDocument>();
				for (ESDrugDocument esDrugDocument : esDrugDocuments) {
					String drugTypeStr = esDrugDocument.getDrugType();
					esDrugDocument.setDrugType(null);
					DrugDocument drugDocument = new DrugDocument();
					BeanUtil.map(esDrugDocument, drugDocument);
					DrugType drugType = new DrugType();
					drugType.setId(esDrugDocument.getDrugTypeId());
					drugType.setType(drugTypeStr);
					drugDocument.setDrugType(drugType);
					response.add(drugDocument);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			SearchQuery searchQuery = null;

			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
						"genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, page, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
			}

			response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);

			if (response != null)
				response = new ArrayList<ESDrugDocument>(new LinkedHashSet<ESDrugDocument>(response));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getGlobalDrugs(int page, int size, String updatedTime, boolean discarded,
			String searchTerm, String category, Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			SearchQuery searchQuery = null;
			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, page, size, updatedTime, discarded, null,
						searchTerm, null, category, null, "genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, page, size, updatedTime, discarded, null,
						searchTerm, null, category, null, "drugName");
			}
			response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> getCustomDrugs(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		List<ESDrugDocument> response = null;
		try {
			if (page > 0)
				return new ArrayList<ESDrugDocument>();
			if (doctorId == null)
				response = new ArrayList<ESDrugDocument>();
			else {
				SearchQuery searchQuery = null;
				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomQuery(page, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}
				response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private List<ESDrugDocument> addStockToDrug(List<ESDrugDocument> drugs)
	{

		for (ESDrugDocument drug : drugs) {

			if (!DPDoctorUtils.anyStringEmpty(drug.getLocationId(), drug.getHospitalId(), drug.getId())) {
				InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(drug.getLocationId(),
						drug.getHospitalId(), drug.getDrugCode());
				if (inventoryItem != null) {
					InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
							.getInventoryItem(inventoryItem.getId());
					drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
					drug.setRetailPrice(inventoryItemLookupResposne.getRetailPrice());
					drug.setStockingUnit(inventoryItemLookupResposne.getStockingUnit());
				}
			}
		}
		return drugs;
	}

	private List<DrugDocument> addStockToDrugWeb(List<DrugDocument> drugs) {
		List<DrugDocument> response= new ArrayList<>();

		for (DrugDocument drug : drugs) {

			if (!DPDoctorUtils.anyStringEmpty(drug.getLocationId(), drug.getHospitalId(), drug.getDrugCode())) {
				InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(drug.getLocationId(),
						drug.getHospitalId(), drug.getDrugCode());
				if (inventoryItem != null) {
					InventoryItemLookupResposne inventoryItemLookupResposne = inventoryService
							.getInventoryItem(inventoryItem.getId());
					drug.setTotalStock(inventoryItemLookupResposne.getTotalStock());
					drug.setRetailPrice(inventoryItemLookupResposne.getRetailPrice());
					drug.setStockingUnit(inventoryItemLookupResposne.getStockingUnit());
				}
			}
			response.add(drug);
		}
		return response;
	}
	
	@Override
	public Long drugCount(String range, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm, String category, Boolean searchByGenericName) {
		Long response = null;
		if (!DPDoctorUtils.anyStringEmpty(searchTerm))
			searchTerm = searchTerm.toUpperCase();
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalDrugsCount(updatedTime, discarded, searchTerm, category, searchByGenericName);
			break;
		case CUSTOM:
			response = getCustomDrugsCount(doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm, category, searchByGenericName);
			break;
		case BOTH:
			response = getCustomGlobalDrugsCount(doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm, category, searchByGenericName);
			break;
		case FAVOURITES:
			response = getFavouritesDrugsCount(doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm, category, searchByGenericName);
			break;
		case WEBBOTH:
			response = getCustomGlobalDrugsForWebCount(doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm, category, searchByGenericName);
			break;
		default:
			break;
		}
		return response;

	}
	
	private Long getGlobalDrugsCount(String updatedTime, boolean discarded,
			String searchTerm, String category, Boolean searchByGenericName) {
		Long response = null;
		try {
			SearchQuery searchQuery = null;
			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, 0, 0, updatedTime, discarded, null,
						searchTerm, null, category, null, "genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DRUG, 0, 0, updatedTime, discarded, null,
						searchTerm, null, category, null, "drugName");
			}
			//response = elasticsearchTemplate.queryForList(searchQuery, ESDrugDocument.class);
			response = elasticsearchTemplate.count(searchQuery, ESDrugDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	private Long getCustomDrugsCount(String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		Long response = null;
		try {
			if (doctorId == null)
				response = 0l;
			else {
				SearchQuery searchQuery = null;
				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomQuery(0, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomQuery(0, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}
				response = elasticsearchTemplate.count(searchQuery, ESDrugDocument.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}
	
	private Long getCustomGlobalDrugsCount(String doctorId, String locationId,
			String hospitalId, String updatedTime, boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		Long response = null;
		try {
			SearchQuery searchQuery = null;

			if (searchByGenericName) {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, 0, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
						"genericNames.name");
			} else {
				searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, 0, 0, doctorId, locationId,
						hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
			}

			response = elasticsearchTemplate.count(searchQuery, ESDrugDocument.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}
	
	private Long getFavouritesDrugsCount(String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		Long response = null;
		try {
			
			if (doctorId == null)
				response = 0l;
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomQuery(0, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomQuery(0, 0, doctorId, locationId, hospitalId,
							updatedTime, discarded, "rankingCount", searchTerm, category, null, "drugName");
				}

				 response = elasticsearchTemplate.count(searchQuery,
						ESDrugDocument.class);

				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}
	
	private Long getCustomGlobalDrugsForWebCount( String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm, String category,
			Boolean searchByGenericName) {
		Long response = null;
		try {
			
			if (doctorId == null)
				response = 0l;
			else {
				SearchQuery searchQuery = null;

				if (searchByGenericName) {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, 0, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null,
							"genericNames.name");
				} else {
					searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DRUG, 0, 0, doctorId, locationId,
							hospitalId, updatedTime, discarded, null, searchTerm, null, category, null, "drugName");
				}

				response = elasticsearchTemplate.count(searchQuery,ESDrugDocument.class);

				
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}
}
