package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "inventory_item_cl")
public class InventoryItemCollection extends GenericCollection{
	
	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private String type;
	@Field
	private String code;
	@Field
	private Long StockingUnit;
	@Field
	private String manufacturer;
	@Field
	private ObjectId resourceId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private Boolean discarded;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getStockingUnit() {
		return StockingUnit;
	}
	public void setStockingUnit(Long stockingUnit) {
		StockingUnit = stockingUnit;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public ObjectId getResourceId() {
		return resourceId;
	}
	public void setResourceId(ObjectId resourceId) {
		this.resourceId = resourceId;
	}
	public ObjectId getLocationId() {
		return locationId;
	}
	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}
	public ObjectId getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	

}
