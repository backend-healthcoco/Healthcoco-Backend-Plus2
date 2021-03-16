package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.MobileNumberDetails;
import com.dpdocter.beans.PhotoId;
import com.dpdocter.enums.NmcHcmType;

@Document(collection = "nmc_hcm_cl")
public class NmcHcmCollection extends GenericCollection {

	@Id
	private ObjectId id;
	
	@Field
	private String stateOrUT;
	@Field
	private String district;
	@Field
	private String block;
	@Field
	private String categoryOfFacility;
	@Field
	private String nameOfFacility;
	@Field
	private String locationOfFacility;
	@Field
	private String addressOfFacility;
	@Field
	private String facilityPostalCode;
	@Field
	private String typeOfFacility;
	@Field
	private String otherFacilities;
	@Field
	private String categoryofHealthWorker;
	@Field
	private String otherCategoryOfHealthWorker;
	@Field
	private String hCWsName;
	@Field
	private PhotoId photoId;
	@Field
	private String gender;
	@Field
	private String dateOfBirth;
	@Field
	private String dateOfDay;
	@Field
	private String dateOfMonth;
	@Field
	private String dateOfYear;
	
	
	@Field
	private MobileNumberDetails mobile;
	@Field
	private String postalCode;
	@Field
	private String employeeId;
	@Field
	private String healthWorkerVaccinator;
	
	@Field
	private NmcHcmType type;
	
	@Field
	private boolean discarded=false;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getStateOrUT() {
		return stateOrUT;
	}
	public void setStateOrUT(String stateOrUT) {
		this.stateOrUT = stateOrUT;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getBlock() {
		return block;
	}
	public void setBlock(String block) {
		this.block = block;
	}
	public String getCategoryOfFacility() {
		return categoryOfFacility;
	}
	public void setCategoryOfFacility(String categoryOfFacility) {
		this.categoryOfFacility = categoryOfFacility;
	}
	public String getNameOfFacility() {
		return nameOfFacility;
	}
	public void setNameOfFacility(String nameOfFacility) {
		this.nameOfFacility = nameOfFacility;
	}
	public String getLocationOfFacility() {
		return locationOfFacility;
	}
	public void setLocationOfFacility(String locationOfFacility) {
		this.locationOfFacility = locationOfFacility;
	}
	public String getAddressOfFacility() {
		return addressOfFacility;
	}
	public void setAddressOfFacility(String addressOfFacility) {
		this.addressOfFacility = addressOfFacility;
	}
	public String getFacilityPostalCode() {
		return facilityPostalCode;
	}
	public void setFacilityPostalCode(String facilityPostalCode) {
		this.facilityPostalCode = facilityPostalCode;
	}
	public String getTypeOfFacility() {
		return typeOfFacility;
	}
	public void setTypeOfFacility(String typeOfFacility) {
		this.typeOfFacility = typeOfFacility;
	}
	public String getCategoryofHealthWorker() {
		return categoryofHealthWorker;
	}
	public void setCategoryofHealthWorker(String categoryofHealthWorker) {
		this.categoryofHealthWorker = categoryofHealthWorker;
	}
	public String gethCWsName() {
		return hCWsName;
	}
	public void sethCWsName(String hCWsName) {
		this.hCWsName = hCWsName;
	}
	public PhotoId getPhotoId() {
		return photoId;
	}
	public void setPhotoId(PhotoId photoId) {
		this.photoId = photoId;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public MobileNumberDetails getMobile() {
		return mobile;
	}
	public void setMobile(MobileNumberDetails mobile) {
		this.mobile = mobile;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getHealthWorkerVaccinator() {
		return healthWorkerVaccinator;
	}
	public void setHealthWorkerVaccinator(String healthWorkerVaccinator) {
		this.healthWorkerVaccinator = healthWorkerVaccinator;
	}
	public String getDateOfDay() {
		return dateOfDay;
	}
	public void setDateOfDay(String dateOfDay) {
		this.dateOfDay = dateOfDay;
	}
	public String getDateOfMonth() {
		return dateOfMonth;
	}
	public void setDateOfMonth(String dateOfMonth) {
		this.dateOfMonth = dateOfMonth;
	}
	public String getDateOfYear() {
		return dateOfYear;
	}
	public void setDateOfYear(String dateOfYear) {
		this.dateOfYear = dateOfYear;
	}
	public NmcHcmType getType() {
		return type;
	}
	public void setType(NmcHcmType type) {
		this.type = type;
	}
	public boolean isDiscarded() {
		return discarded;
	}
	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}
	public String getOtherFacilities() {
		return otherFacilities;
	}
	public void setOtherFacilities(String otherFacilities) {
		this.otherFacilities = otherFacilities;
	}
	public String getOtherCategoryOfHealthWorker() {
		return otherCategoryOfHealthWorker;
	}
	public void setOtherCategoryOfHealthWorker(String otherCategoryOfHealthWorker) {
		this.otherCategoryOfHealthWorker = otherCategoryOfHealthWorker;
	}
	
	
	
	
	
}
