package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Product extends GenericCollection {

	private String productID;
	private String defaultCategoryID;
	private String vendorId;
	private String modelNo;
	private Boolean sellWithoutOptions;
	private String skuID; // Stock keeping unit (eg barcode)
	private String name;
	private String description;
	private Long activeStartDate;
	private Long activeEndDate;
	private String thumbImageURL;
	private String skuDetailPageURL;
	private Float weightOfProduct;
	private Float weightAfterPackaging;
	private Float lengthProduct;
	private Float heightProduct;
	private Float lengthPackaging;
	private Float widthPackaging;
	private Float heightPackaging;
	private Integer quantityPerPack;
	private Boolean discarded;
	private Long discardedDate;
	private Boolean available;
	private Boolean displayed;
	private Boolean giftable;
	private Boolean expired;
	private Boolean discounted;
	private Boolean perishable;
	private Integer perishableDays;
	private String dimensionMeasureUnit;
	private String weightMeasureUnit;
	private Float price;
	private Integer gst;

	public String getProductID() {
		return productID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public String getDefaultCategoryID() {
		return defaultCategoryID;
	}

	public void setDefaultCategoryID(String defaultCategoryID) {
		this.defaultCategoryID = defaultCategoryID;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getModelNo() {
		return modelNo;
	}

	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}

	public Boolean getSellWithoutOptions() {
		return sellWithoutOptions;
	}

	public void setSellWithoutOptions(Boolean sellWithoutOptions) {
		this.sellWithoutOptions = sellWithoutOptions;
	}

	public String getSkuID() {
		return skuID;
	}

	public void setSkuID(String skuID) {
		this.skuID = skuID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getActiveStartDate() {
		return activeStartDate;
	}

	public void setActiveStartDate(Long activeStartDate) {
		this.activeStartDate = activeStartDate;
	}

	public Long getActiveEndDate() {
		return activeEndDate;
	}

	public void setActiveEndDate(Long activeEndDate) {
		this.activeEndDate = activeEndDate;
	}

	public String getThumbImageURL() {
		return thumbImageURL;
	}

	public void setThumbImageURL(String thumbImageURL) {
		this.thumbImageURL = thumbImageURL;
	}

	public String getSkuDetailPageURL() {
		return skuDetailPageURL;
	}

	public void setSkuDetailPageURL(String skuDetailPageURL) {
		this.skuDetailPageURL = skuDetailPageURL;
	}

	public Float getWeightOfProduct() {
		return weightOfProduct;
	}

	public void setWeightOfProduct(Float weightOfProduct) {
		this.weightOfProduct = weightOfProduct;
	}

	public Float getWeightAfterPackaging() {
		return weightAfterPackaging;
	}

	public void setWeightAfterPackaging(Float weightAfterPackaging) {
		this.weightAfterPackaging = weightAfterPackaging;
	}

	public Float getLengthProduct() {
		return lengthProduct;
	}

	public void setLengthProduct(Float lengthProduct) {
		this.lengthProduct = lengthProduct;
	}

	public Float getHeightProduct() {
		return heightProduct;
	}

	public void setHeightProduct(Float heightProduct) {
		this.heightProduct = heightProduct;
	}

	public Float getLengthPackaging() {
		return lengthPackaging;
	}

	public void setLengthPackaging(Float lengthPackaging) {
		this.lengthPackaging = lengthPackaging;
	}

	public Float getWidthPackaging() {
		return widthPackaging;
	}

	public void setWidthPackaging(Float widthPackaging) {
		this.widthPackaging = widthPackaging;
	}

	public Float getHeightPackaging() {
		return heightPackaging;
	}

	public void setHeightPackaging(Float heightPackaging) {
		this.heightPackaging = heightPackaging;
	}

	public Integer getQuantityPerPack() {
		return quantityPerPack;
	}

	public void setQuantityPerPack(Integer quantityPerPack) {
		this.quantityPerPack = quantityPerPack;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Long getDiscardedDate() {
		return discardedDate;
	}

	public void setDiscardedDate(Long discardedDate) {
		this.discardedDate = discardedDate;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Boolean getDisplayed() {
		return displayed;
	}

	public void setDisplayed(Boolean displayed) {
		this.displayed = displayed;
	}

	public Boolean getGiftable() {
		return giftable;
	}

	public void setGiftable(Boolean giftable) {
		this.giftable = giftable;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public Boolean getDiscounted() {
		return discounted;
	}

	public void setDiscounted(Boolean discounted) {
		this.discounted = discounted;
	}

	public Boolean getPerishable() {
		return perishable;
	}

	public void setPerishable(Boolean perishable) {
		this.perishable = perishable;
	}

	public Integer getPerishableDays() {
		return perishableDays;
	}

	public void setPerishableDays(Integer perishableDays) {
		this.perishableDays = perishableDays;
	}

	public String getDimensionMeasureUnit() {
		return dimensionMeasureUnit;
	}

	public void setDimensionMeasureUnit(String dimensionMeasureUnit) {
		this.dimensionMeasureUnit = dimensionMeasureUnit;
	}

	public String getWeightMeasureUnit() {
		return weightMeasureUnit;
	}

	public void setWeightMeasureUnit(String weightMeasureUnit) {
		this.weightMeasureUnit = weightMeasureUnit;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Integer getGst() {
		return gst;
	}

	public void setGst(Integer gst) {
		this.gst = gst;
	}

}
