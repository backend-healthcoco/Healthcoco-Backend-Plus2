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

import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.Manufacturer;
import com.dpdocter.collections.InventoryItemCollection;
import com.dpdocter.collections.ManufacturerCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.InventoryItemRepository;
import com.dpdocter.repository.ManufacturerRepository;
import com.dpdocter.services.InventoryService;

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

	@Override
	@Transactional
	public InventoryItem addItem(InventoryItem inventoryItem) {
		
		InventoryItem response = null;
		System.out.println();
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
			inventoryItemCollection.setCreatedTime(new Date());
			inventoryItemCollection = inventoryItemRepository.save(inventoryItemCollection);
			if (inventoryItemCollection != null) {
				response = new InventoryItem();
				BeanUtil.map(inventoryItemCollection, response);
			} 
		} catch (Exception e) {
			// TODO: handle exception
			logger.warn("Error while adding inventory item");
			e.printStackTrace();
			
		}
		return response;
		
	}
	
	@Override
	@Transactional
	public List<InventoryItem> getInventoryItemList(String locationId, String hospitalId,String type, String searchTerm, int page, int size)
	{
		List<InventoryItem> response= null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("code").regex("^" + searchTerm, "i"),
						new Criteria("code").regex("^" + searchTerm),
						new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}

			criteria.and("locationId").is(new ObjectId(locationId));
			criteria.and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(type)) 
			{
				criteria.and("type").is(new ObjectId(type));
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			AggregationResults<InventoryItem> aggregationResults = mongoTemplate.aggregate(aggregation,
					InventoryItemCollection.class, InventoryItem.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Getting Inventory items");
			throw new BusinessException(ServiceError.Unknown, "Error Getting Inventory items");
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<Manufacturer> getManufacturerList(String locationId, String hospitalId, String searchTerm, int page, int size)
	{
		System.out.println(searchTerm);
		List<Manufacturer> response= null;
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
	
	

}
