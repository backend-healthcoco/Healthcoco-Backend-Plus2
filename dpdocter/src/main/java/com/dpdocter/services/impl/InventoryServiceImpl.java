package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.InventorySettings;
import com.dpdocter.beans.InventoryStock;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.InventoryBatchCollection;
import com.dpdocter.collections.InventoryItemCollection;
import com.dpdocter.collections.InventorySettingsCollection;
import com.dpdocter.collections.InventoryStockCollection;
import com.dpdocter.collections.ManufacturerCollection;
import com.dpdocter.elasticsearch.document.ESDrugDocument;
import com.dpdocter.elasticsearch.services.ESPrescriptionService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.InventoryBatchRepository;
import com.dpdocter.repository.InventoryItemRepository;
import com.dpdocter.repository.InventorySettingRepository;
import com.dpdocter.repository.InventoryStockRepository;
import com.dpdocter.repository.ManufacturerRepository;
import com.dpdocter.request.InventorySettingRequest;
import com.dpdocter.response.InventoryItemLookupResposne;
import com.dpdocter.response.InventoryStockLookupResponse;
import com.dpdocter.services.InventoryService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;

@Service
public class InventoryServiceImpl implements InventoryService {

	private static Logger logger = Logger.getLogger(InventoryServiceImpl.class.getName());

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@Autowired
	private ManufacturerRepository manufacturerRepository;

	@Autowired
	private InventoryStockRepository inventoryStockRepository;

	@Autowired
	private InventoryBatchRepository inventoryBatchRepository;

	@Autowired
	private InventorySettingRepository inventorySettingRepository;
	
	@Autowired
	private DrugRepository drugRepository;
	
	@Autowired
	private TransactionalManagementService transnationalService;
	
	@Autowired
	private ESPrescriptionService esPrescriptionService;
	
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;
	
//	@Autowired
//	private PrescriptionServices prescriptionServices;

	@Override
	@Transactional
	public InventoryItem addItem(InventoryItem inventoryItem) {

		InventoryItem response = null;
		try {
			if (inventoryItem.getSaveManufacturer() == true) {
				ManufacturerCollection manufacturerCollection = new ManufacturerCollection();
				manufacturerCollection.setName(inventoryItem.getManufacturer());
				manufacturerCollection.setLocationId(new ObjectId(inventoryItem.getLocationId()));
				manufacturerCollection.setHospitalId(new ObjectId(inventoryItem.getHospitalId()));
				manufacturerCollection.setCreatedTime(new Date());
				manufacturerRepository.save(manufacturerCollection);
			}
			InventoryItemCollection inventoryItemCollection = new InventoryItemCollection();
			BeanUtil.map(inventoryItem, inventoryItemCollection);
			inventoryItemCollection.setDrugType(inventoryItem.getDrugType());
			inventoryItemCollection.setCreatedTime(new Date());
			inventoryItemCollection = inventoryItemRepository.save(inventoryItemCollection);
			if (inventoryItemCollection != null) {
				response = new InventoryItem();
				BeanUtil.map(inventoryItemCollection, response);
			}

			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByLocationId(new ObjectId(inventoryItem.getLocationId()));
			for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
				Drug drug = makeDrugFavourite(inventoryItem.getResourceId(),
						doctorClinicProfileCollection.getDoctorId().toString(),
						doctorClinicProfileCollection.getLocationId().toString(), inventoryItem.getHospitalId());

				transnationalService.addResource(new ObjectId(drug.getId()), Resource.DRUG, false);
				if (drug != null) {
					ESDrugDocument esDrugDocument = new ESDrugDocument();
					BeanUtil.map(drug, esDrugDocument);
					if (drug.getDrugType() != null) {
						esDrugDocument.setDrugTypeId(drug.getDrugType().getId());
						esDrugDocument.setDrugType(drug.getDrugType().getType());
					}
					esPrescriptionService.addDrug(esDrugDocument);
				}
			}

		} catch (Exception e) {
			logger.warn("Error while adding inventory item");
			e.printStackTrace();

		}
		return response;

	}

	@Override
	@Transactional
	public Manufacturer addManufacturer(Manufacturer manufacturer) {

		Manufacturer response = null;
		try {

			ManufacturerCollection manufacturerCollection = new ManufacturerCollection();
			BeanUtil.map(manufacturer, manufacturerCollection);
			manufacturerCollection.setCreatedTime(new Date());
			manufacturerCollection = manufacturerRepository.save(manufacturerCollection);
			if (manufacturerCollection != null) {
				response = new Manufacturer();
				BeanUtil.map(manufacturerCollection, response);
			}
		} catch (Exception e) {
			logger.warn("Error while adding inventory item");
			e.printStackTrace();

		}
		return response;

	}

	@Override
	@Transactional
	public InventoryItemLookupResposne getInventoryItem(String id) {
		InventoryItemLookupResposne response = null;
		List<InventoryBatch> inventoryBatchs = null;
		Aggregation aggregation = null;
		try {
			InventoryItemCollection inventoryItemCollection = inventoryItemRepository.findById(new ObjectId(id)).orElse(null);
			if (inventoryItemCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			response = new InventoryItemLookupResposne();
			BeanUtil.map(inventoryItemCollection, response);
			if (response != null) {

				aggregation = Aggregation.newAggregation(
						Aggregation.match(new Criteria().and("itemId").is(new ObjectId(response.getId()))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
				AggregationResults<InventoryBatch> batchAggregationResults = mongoTemplate.aggregate(aggregation,
						InventoryBatchCollection.class, InventoryBatch.class);
				inventoryBatchs = batchAggregationResults.getMappedResults();
				if (inventoryBatchs != null) {
					Long totalStock = 0l;
					for (InventoryBatch inventoryBatch : inventoryBatchs) {
						totalStock += inventoryBatch.getNoOfItemsLeft();
					}
					response.setTotalStock(totalStock);
				}

				response.setInventoryBatchs(inventoryBatchs);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory item");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory item");
		}
		return response;
	}

	@Override
	@Transactional
	public List<InventoryItemLookupResposne> getInventoryItemList(String locationId, String hospitalId, String type,
			String searchTerm, long page, int size) {
		List<InventoryItemLookupResposne> response = null;
		List<InventoryBatch> inventoryBatchs = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				String search = searchTerm.replaceAll("\\+", " ");
				criteria = criteria.orOperator(new Criteria("code").regex("^" + search, "i"),
						new Criteria("code").regex("^" + search), new Criteria("name").regex("^" + search, "i"),
						new Criteria("name").regex("^" + search));
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<InventoryItemLookupResposne> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryItemCollection.class, InventoryItemLookupResposne.class);
			response = aggregationResults.getMappedResults();
			if (response != null) {
				for (InventoryItemLookupResposne inventoryItem : response) {
					aggregation = Aggregation.newAggregation(
							Aggregation.match(new Criteria().and("itemId").is(new ObjectId(inventoryItem.getId()))),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
					AggregationResults<InventoryBatch> batchAggregationResults = mongoTemplate.aggregate(aggregation,
							InventoryBatchCollection.class, InventoryBatch.class);
					inventoryBatchs = batchAggregationResults.getMappedResults();
					inventoryItem.setInventoryBatchs(inventoryBatchs);
					if (inventoryBatchs != null) {
						Long totalStock = 0l;
						for (InventoryBatch inventoryBatch : inventoryBatchs) {
							totalStock += inventoryBatch.getNoOfItemsLeft();
						}
						inventoryItem.setTotalStock(totalStock);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory items");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory items");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getInventoryItemListCount(String locationId, String hospitalId, String type, String searchTerm) {
		List<InventoryItemLookupResposne> response = null;
		List<InventoryBatch> inventoryBatchs = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("code").regex("^" + searchTerm, "i"),
						new Criteria("code").regex("^" + searchTerm), new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<InventoryItemLookupResposne> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryItemCollection.class, InventoryItemLookupResposne.class);
			response = aggregationResults.getMappedResults();
			if (response != null) {
				for (InventoryItemLookupResposne inventoryItem : response) {
					aggregation = Aggregation.newAggregation(
							Aggregation.match(new Criteria().and("itemId").is(inventoryItem.getId())),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
					AggregationResults<InventoryBatch> batchAggregationResults = mongoTemplate.aggregate(aggregation,
							InventoryBatchCollection.class, InventoryBatch.class);
					inventoryBatchs = batchAggregationResults.getMappedResults();
					inventoryItem.setInventoryBatchs(inventoryBatchs);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory items");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory items");
		}
		return response.size();
	}

	@Override
	@Transactional
	public List<Manufacturer> getManufacturerList(String locationId, String hospitalId, String searchTerm, long page,
			int size) {
		List<Manufacturer> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Manufacturer> aggregationResults = mongoTemplate.aggregate(aggregation,
					ManufacturerCollection.class, Manufacturer.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Manufacturers");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Manufacturers");
		}
		return response;
	}

	@Override
	@Transactional
	public InventoryStock addInventoryStock(InventoryStock inventoryStock) {
		InventoryStock response = null;
		InventoryBatchCollection inventoryBatchCollection = null;
		InventoryItemCollection inventoryItemCollection = null;
		try {

			InventoryStockCollection inventoryStockCollection = new InventoryStockCollection();
			if (inventoryStock.getStockType() != null && inventoryStock.getStockType().equalsIgnoreCase("ADDED")) {
				if (inventoryStock.getInventoryBatch() != null) {
					if (inventoryStock.getInventoryBatch().getId() != null) {

						inventoryBatchCollection = inventoryBatchRepository
								.findById(new ObjectId(inventoryStock.getInventoryBatch().getId())).orElse(null);
						inventoryBatchCollection
								.setNoOfItems(inventoryBatchCollection.getNoOfItems() + inventoryStock.getQuantity());
						inventoryBatchCollection.setNoOfItemsLeft(
								inventoryBatchCollection.getNoOfItemsLeft() + inventoryStock.getQuantity());
						inventoryBatchCollection = inventoryBatchRepository.save(inventoryBatchCollection);

					} else {
						inventoryBatchCollection = new InventoryBatchCollection();
						inventoryBatchCollection.setItemId(new ObjectId(inventoryStock.getItemId()));
						if (inventoryStock.getInventoryBatch().getBatchName() != null) {
							inventoryBatchCollection.setBatchName(inventoryStock.getInventoryBatch().getBatchName());
						} else {
							inventoryBatchCollection.setBatchName("NO NAME");
						}
						inventoryBatchCollection.setCostPrice(inventoryStock.getCostPrice());
						inventoryBatchCollection.setNoOfItems(inventoryStock.getQuantity());
						inventoryBatchCollection.setNoOfItemsLeft(inventoryStock.getQuantity());
						inventoryBatchCollection.setExpiryDate(inventoryStock.getInventoryBatch().getExpiryDate());
						inventoryBatchCollection.setRetailPrice(inventoryStock.getRetailPrice());
						inventoryBatchCollection.setLocationId(new ObjectId(inventoryStock.getLocationId()));
						inventoryBatchCollection.setHospitalId(new ObjectId(inventoryStock.getHospitalId()));
						inventoryBatchCollection.setCreatedTime(new Date());
						inventoryBatchCollection = inventoryBatchRepository.save(inventoryBatchCollection);
					}
				} else {
					inventoryBatchCollection = new InventoryBatchCollection();
					inventoryBatchCollection.setItemId(new ObjectId(inventoryStock.getItemId()));
					inventoryBatchCollection.setBatchName("NO NAME");
					inventoryBatchCollection.setCostPrice(inventoryStock.getCostPrice());
					inventoryBatchCollection.setRetailPrice(inventoryStock.getRetailPrice());
					inventoryBatchCollection.setNoOfItems(inventoryStock.getQuantity());
					inventoryBatchCollection.setNoOfItemsLeft(inventoryStock.getQuantity());
					inventoryBatchCollection.setLocationId(new ObjectId(inventoryStock.getLocationId()));
					inventoryBatchCollection.setHospitalId(new ObjectId(inventoryStock.getHospitalId()));
					inventoryBatchCollection.setCreatedTime(new Date());
					inventoryBatchCollection = inventoryBatchRepository.save(inventoryBatchCollection);
				}
			}

			else if (inventoryStock.getStockType() != null
					&& inventoryStock.getStockType().equalsIgnoreCase("CONSUMED")) {
				if (inventoryStock.getInventoryBatch() == null) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Batch cannot be null while consuming items");
				} else if (inventoryStock.getInventoryBatch().getId() == null) {
					throw new BusinessException(ServiceError.InvalidInput,
							"Batch cannot be null while consuming items");
				}

				inventoryBatchCollection = inventoryBatchRepository
						.findById(new ObjectId(inventoryStock.getInventoryBatch().getId())).orElse(null);
				inventoryBatchCollection.setNoOfItems(inventoryBatchCollection.getNoOfItems());
				inventoryBatchCollection
						.setNoOfItemsLeft(inventoryBatchCollection.getNoOfItemsLeft() - inventoryStock.getQuantity());
				inventoryBatchCollection = inventoryBatchRepository.save(inventoryBatchCollection);
			}
			inventoryStock.setBatchId(inventoryBatchCollection.getId().toString());
			BeanUtil.map(inventoryStock, inventoryStockCollection);
			if (inventoryStockCollection.getItemId() != null) {
				inventoryItemCollection = inventoryItemRepository.findById(inventoryStockCollection.getItemId()).orElse(null);
				if (inventoryItemCollection != null) {
				inventoryStockCollection.setResourceId(inventoryItemCollection.getResourceId());
				}
			}
			inventoryStockCollection.setCreatedTime(new Date());
			inventoryStockCollection = inventoryStockRepository.save(inventoryStockCollection);
			response = new InventoryStock();
			BeanUtil.map(inventoryStockCollection, response);

			if (inventoryStockCollection.getItemId() != null) {
				inventoryItemCollection = inventoryItemRepository
						.findById(inventoryStockCollection.getItemId()).orElse(null);

				if (inventoryItemCollection != null) {
					List<DrugCollection> drugCollections = drugRepository.findByDrugCodeLocationIdHospitalId(
							inventoryItemCollection.getResourceId(), inventoryStockCollection.getLocationId(),
							inventoryStockCollection.getHospitalId());

					for (DrugCollection drugCollection : drugCollections) {
						transnationalService.addResource(drugCollection.getId(), Resource.DRUG, false);
						if (drugCollection != null) {
							ESDrugDocument esDrugDocument = new ESDrugDocument();
							BeanUtil.map(drugCollection, esDrugDocument);
							if (drugCollection.getDrugType() != null) {
								esDrugDocument.setDrugTypeId(drugCollection.getDrugType().getId());
								esDrugDocument.setDrugType(drugCollection.getDrugType().getType());
							}
							esPrescriptionService.addDrug(esDrugDocument);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.warn("Error while creating stock");
			e.printStackTrace();
		}

		return response;
	}

	@Override
	@Transactional
	public List<InventoryStockLookupResponse> getInventoryStockList(String locationId, String hospitalId, String itemId,
			String stockType, String searchTerm, long page, int size) {
		List<InventoryStockLookupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("inventoryItem.name").regex("^" + searchTerm, "i"),
						new Criteria("inventoryItem.name").regex("^" + searchTerm),
						new Criteria("inventoryBatch.batchName").regex("^" + searchTerm, "i"),
						new Criteria("inventoryBatch.batchName").regex("^" + searchTerm));
			}

			if (itemId != null) {
				criteria.and("itemId").is(new ObjectId(itemId));
			}

			if (stockType != null) {
				criteria.and("stockType").is(stockType);
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("inventory_item_cl", "itemId", "_id", "inventoryItem"),
						Aggregation.unwind("inventoryItem"),
						Aggregation.lookup("inventory_batch_cl", "batchId", "_id", "inventoryBatch"),
						Aggregation.unwind("inventoryBatch"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("inventory_item_cl", "itemId", "_id", "inventoryItem"),
						Aggregation.unwind("inventoryItem"),
						Aggregation.lookup("inventory_batch_cl", "batchId", "_id", "inventoryBatch"),
						Aggregation.unwind("inventoryBatch"),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<InventoryStockLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryStockCollection.class, InventoryStockLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory Stock");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getInventoryStockListCount(String locationId, String hospitalId, String itemId, String stockType,
			String searchTerm) {
		List<InventoryStockLookupResponse> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("inventoryItem.name").regex("^" + searchTerm, "i"),
						new Criteria("inventoryItem.name").regex("^" + searchTerm),
						new Criteria("inventoryBatch.batchName").regex("^" + searchTerm, "i"),
						new Criteria("inventoryBatch.batchName").regex("^" + searchTerm));
			}

			if (itemId != null) {
				criteria.and("itemId").is(new ObjectId(itemId));
			}

			if (stockType != null) {
				criteria.and("stockType").is(stockType);
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("inventory_item_cl", "itemId", "_id", "inventoryItem"),
					Aggregation.unwind("inventoryItem"),
					Aggregation.lookup("inventory_batch_cl", "batchId", "_id", "inventoryBatch"),
					Aggregation.unwind("inventoryBatch"),
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<InventoryStockLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryStockCollection.class, InventoryStockLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory Stock");
		}
		return response.size();
	}

	@Override
	@Transactional
	public InventoryBatch addInventoryBatch(InventoryBatch inventoryBatch) {
		InventoryBatch response = null;
		try {
			InventoryBatchCollection inventoryBatchCollection = new InventoryBatchCollection();
			BeanUtil.map(inventoryBatch, inventoryBatchCollection);
			response = new InventoryBatch();
			BeanUtil.map(inventoryBatchCollection, response);
		} catch (Exception e) {
			logger.warn("error while adding batch");
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<InventoryBatch> getInventoryBatchList(String locationId, String hospitalId, String itemId,
			String searchTerm, long page, int size) {
		List<InventoryBatch> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("batchName").regex("^" + searchTerm, "i"),
						new Criteria("batchName").regex("^" + searchTerm));
			}

			if (!DPDoctorUtils.anyStringEmpty(itemId)) {
				criteria = criteria.and("itemId").is(new ObjectId(itemId));
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<InventoryBatch> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryBatchCollection.class, InventoryBatch.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory Batch");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory Batch");
		}
		return response;
	}

	@Override
	@Transactional
	public InventoryItem discardInventoryItem(String id, Boolean discarded) {
		InventoryItem response = null;

		try {
			InventoryItemCollection inventoryItemCollection = inventoryItemRepository.findById(new ObjectId(id)).orElse(null);
			if (inventoryItemCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No record found");
			}
			inventoryItemCollection.setDiscarded(discarded);
			inventoryItemCollection.setUpdatedTime(new Date());
			inventoryItemCollection = inventoryItemRepository.save(inventoryItemCollection);
			response = new InventoryItem();
			BeanUtil.map(inventoryItemCollection, response);
		} catch (Exception e) {
			logger.warn("Error while discarding inventory item");
			e.printStackTrace();
		}

		return response;
	}

	@Override
	@Transactional
	public InventoryBatch discardInventoryBatch(String id, Boolean discarded) {
		InventoryBatch response = null;

		try {
			InventoryBatchCollection inventoryBatchCollection = inventoryBatchRepository.findById(new ObjectId(id)).orElse(null);
			if (inventoryBatchCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No record found");
			}
			inventoryBatchCollection.setDiscarded(discarded);
			inventoryBatchCollection.setUpdatedTime(new Date());
			inventoryBatchCollection = inventoryBatchRepository.save(inventoryBatchCollection);
			response = new InventoryBatch();
			BeanUtil.map(inventoryBatchCollection, response);
		} catch (Exception e) {
			logger.warn("Error while discarding inventory batch");
			e.printStackTrace();
		}

		return response;
	}

	@Override
	@Transactional
	public InventoryStock discardInventoryStock(String id, Boolean discarded) {
		InventoryStock response = null;

		try {
			InventoryStockCollection inventoryStockCollection = inventoryStockRepository.findById(new ObjectId(id)).orElse(null);
			if (inventoryStockCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "No record found");
			}
			inventoryStockCollection.setDiscarded(discarded);
			inventoryStockCollection.setUpdatedTime(new Date());
			inventoryStockCollection = inventoryStockRepository.save(inventoryStockCollection);
			response = new InventoryStock();
			BeanUtil.map(inventoryStockCollection, response);
		} catch (Exception e) {
			logger.warn("Error while discarding inventory stock");
			e.printStackTrace();
		}

		return response;
	}

	

	@Override
	@Transactional
	public InventorySettings addEditInventorySetting(InventorySettingRequest request) {
		InventorySettings response = null;
		InventorySettingsCollection inventorySettingsCollection = null;
		try {
			if (request != null) {
				inventorySettingsCollection = inventorySettingRepository.findByDoctorIdPatientIdHospitalId(new ObjectId(request.getDoctorId()), new ObjectId(request.getLocationId()), new ObjectId(request.getHospitalId()));
			}
			if (inventorySettingsCollection != null) {
				ObjectId oldId = inventorySettingsCollection.getId(); 
				BeanUtil.map(request, inventorySettingsCollection);
				inventorySettingsCollection.setId(oldId);
			} else {
				inventorySettingsCollection = new InventorySettingsCollection();
				BeanUtil.map(request, inventorySettingsCollection);
			}

			inventorySettingRepository.save(inventorySettingsCollection);
			response = new InventorySettings();
			BeanUtil.map(inventorySettingsCollection, response);
		} catch (Exception e) {
			logger.warn("Error while adding/editing inventory setting");
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public InventoryItem getInventoryItemByResourceId(String locationId, String hospitalId, String resourceId)
	{
		InventoryItem response = null;
		InventoryItemCollection inventoryItemCollection = null;
		try {
			inventoryItemCollection = inventoryItemRepository.findByLocationIdHospitalIdResourceId(new ObjectId(locationId), new ObjectId(hospitalId), resourceId);
			if (inventoryItemCollection != null) {
				response = new InventoryItem();
				BeanUtil.map( inventoryItemCollection, response);
			} else {
				logger.warn("Inventory item not found");
			}

		} catch (Exception e) {
			logger.warn("Error while getting inventory setting");
			e.printStackTrace();
		}
		return response;
	}


	@Override
	@Transactional
	public InventorySettings getInventorySetting(String id,String doctorId, String locationId, String hospitalId) {
		InventorySettings response = null;
		InventorySettingsCollection inventorySettingsCollection = null;
		try {
			if (DPDoctorUtils.anyStringEmpty(id)) {
				inventorySettingsCollection = inventorySettingRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				inventorySettingsCollection = inventorySettingRepository.findByDoctorIdPatientIdHospitalId(
						new ObjectId(doctorId), new ObjectId(locationId), new ObjectId(hospitalId));
			}
			if (inventorySettingsCollection != null) {
				response = new InventorySettings();
				BeanUtil.map( inventorySettingsCollection, response);
			} else {
				response = new InventorySettings();
				response.setDoctorId(doctorId);
				response.setLocationId(locationId);
				response.setHospitalId(hospitalId);
				response.setSaveToInventory(false);
				response.setShowInventoryCount(false);
			} 
		} catch (Exception e) {
			logger.warn("Error while getting inventory setting");
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<InventoryBatch> getInventoryBatchByResourceId(String locationId, String hospitalId, String resourceId)
	{
		List<InventoryBatch> inventoryBatchs = null;
		try {
			InventoryItem inventoryItem = getInventoryItemByResourceId(locationId, hospitalId, resourceId);
			if(inventoryItem != null)
			{
				inventoryBatchs = getInventoryBatchList(locationId, hospitalId, inventoryItem.getId(), null, 0, 0);
			}
		} catch (Exception e) {
		}
		return inventoryBatchs;
	}
	

	@Override
	@Transactional
	public InventoryStock getInventoryStockByInvoiceIdResourceId(String locationId, String hospitalId, String resourceId , String invoiceId)
	{
		InventoryStock inventoryStock = null;
		try {
			InventoryStockCollection inventoryStockCollection = inventoryStockRepository.findByLocationIdHospitalIdResourceIdInvoiceId(new ObjectId(locationId), new ObjectId(hospitalId), resourceId, new ObjectId(invoiceId));
			if(inventoryStockCollection != null) {
				inventoryStock = new InventoryStock();
				BeanUtil.map(inventoryStockCollection, inventoryStock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inventoryStock;
	}

	@Override
	@Transactional
	public Long getInventoryStockItemCount(String locationId, String hospitalId, String resourceId , String invoiceId)
	{
		Long quantity = 0l;
		try {
			List<InventoryStockCollection> inventoryStockCollections = inventoryStockRepository.findListByLocationIdHospitalIdResourceIdInvoiceId(new ObjectId(locationId), new ObjectId(hospitalId), resourceId, new ObjectId(invoiceId));
			if(inventoryStockCollections != null) {
				for (InventoryStockCollection inventoryStockCollection : inventoryStockCollections) {
					if(inventoryStockCollection.getStockType().equalsIgnoreCase("CONSUMED"))
					{
						quantity = quantity + inventoryStockCollection.getQuantity();
					}
					else if(inventoryStockCollection.getStockType().equalsIgnoreCase("ADDED"))
					{
						quantity = quantity - inventoryStockCollection.getQuantity();
					}
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quantity;
	}

	@Override
	@Transactional
	public InventoryBatch getInventoryBatchById(String id) {
		InventoryBatch inventoryBatch = null;
		try {
			InventoryBatchCollection inventoryBatchCollection = inventoryBatchRepository.findById(new ObjectId(id)).orElse(null);
			if (inventoryBatchCollection != null) {
				inventoryBatch = new InventoryBatch();
				BeanUtil.map(inventoryBatchCollection, inventoryBatch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inventoryBatch;
	}

	@Transactional
	private Drug makeDrugFavourite(String drugCode, String doctorId, String locationId, String hospitalId) {
		Drug response = null;

		try {
			ObjectId doctorObjectId = new ObjectId(doctorId),
					locationObjectId = new ObjectId(locationId), hospitalObjectId = new ObjectId(hospitalId);
			/*DrugCollection originalDrug = drugRepository.findById(drugObjectId);
			 * drugCollection = drugRepository.findByDrugCode(drugCode);
			if (originalDrug == null) {
				logger.error("Invalid drug Id");
				throw new BusinessException(ServiceError.Unknown, "Invalid drug Id");
			}*/
			DrugCollection originalDrug = drugRepository.findByDrugCode(drugCode);
			if (originalDrug == null) {
				logger.error("Invalid drug Id");
				throw new BusinessException(ServiceError.Unknown, "Invalid drug code");
			}
			DrugCollection drugCollection = drugRepository.findByDrugCode(drugCode, doctorObjectId, locationObjectId, hospitalObjectId);
			if (drugCollection == null) {
				drugCollection = originalDrug;
				drugCollection.setLocationId(locationObjectId);
				drugCollection.setHospitalId(hospitalObjectId);
				drugCollection.setDoctorId(doctorObjectId);
				drugCollection.setRankingCount(1);
				drugCollection.setId(null);
			} else {
				drugCollection.setLocationId(locationObjectId);
				drugCollection.setHospitalId(hospitalObjectId);
				drugCollection.setRankingCount(drugCollection.getRankingCount() + 1);
				drugCollection.setUpdatedTime(new Date());
			}
			drugCollection = drugRepository.save(drugCollection);
			response = new Drug();
			BeanUtil.map(drugCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While making Drug Favourite");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While making Drug Favourite");
		}
		return response;
	}
}
