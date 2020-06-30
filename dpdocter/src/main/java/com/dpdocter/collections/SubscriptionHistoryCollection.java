package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;

@Document(collection = "subscription_History_cl")
public class SubscriptionHistoryCollection extends GenericCollection{

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private Date fromDate;

	@Field
	private Date toDate;

	@Field
	private PackageType packageName;

	@Field
	private String amount;
	
	@Field
	private String amountViaCash;

	@Field
	private String amountViaCheque;

	@Field
	private PaymentMode mode;

	@Field
	private String bankName;

	@Field
	private String chequeNo;
	
	@Field
	private String countryCode;

	@Field
	private String branch;

	@Field
	private Date chequeDate;
	
	@Field
	private Boolean isAdvertisement = Boolean.FALSE;

	@Field
	private Boolean discarded = Boolean.FALSE;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public PackageType getPackageName() {
		return packageName;
	}

	public void setPackageName(PackageType packageName) {
		this.packageName = packageName;
	}	

	public String getAmount() {
		return amount;
	}

	public String getAmountViaCash() {
		return amountViaCash;
	}

	public void setAmountViaCash(String amountViaCash) {
		this.amountViaCash = amountViaCash;
	}

	public String getAmountViaCheque() {
		return amountViaCheque;
	}

	public void setAmountViaCheque(String amountViaCheque) {
		this.amountViaCheque = amountViaCheque;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public PaymentMode getMode() {
		return mode;
	}

	public void setMode(PaymentMode mode) {
		this.mode = mode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public Date getChequeDate() {
		return chequeDate;
	}

	public void setChequeDate(Date chequeDate) {
		this.chequeDate = chequeDate;
	}

	public Boolean getIsAdvertisement() {
		return isAdvertisement;
	}

	public void setIsAdvertisement(Boolean isAdvertisement) {
		this.isAdvertisement = isAdvertisement;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}	

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@Override
	public String toString() {
		return "SubscriptionHistoryCollection [id=" + id + ", doctorId=" + doctorId + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", packageName=" + packageName + ", amount=" + amount + ", amountViaCash="
				+ amountViaCash + ", amountViaCheque=" + amountViaCheque + ", mode=" + mode + ", bankName=" + bankName
				+ ", chequeNo=" + chequeNo + ", countryCode=" + countryCode + ", branch=" + branch + ", chequeDate="
				+ chequeDate + ", isAdvertisement=" + isAdvertisement + ", discarded=" + discarded + "]";
	}

	
	

}
