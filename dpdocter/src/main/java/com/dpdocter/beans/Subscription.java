package com.dpdocter.beans;


 import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;
import com.dpdocter.enums.PaymentMode;
 
public class Subscription extends GenericCollection {

	private String id;
	
	private String doctorId;
	
	private Date fromDate;
	
	private Date toDate;
	
	private PackageType packageName;
	
	private String amount;
	
	private String amountViaCash;
	
	private String amountViaCheque;
	
	private PaymentMode mode;
	
	private String bankName;
	
	private String countryCode;
	
	private String chequeNo;
	
	private String branch;
	
	private Date chequeDate;
	
	private Boolean isAdvertisement = Boolean.FALSE;
	
	private Boolean discarded=Boolean.FALSE;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
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
		return "Subscription [id=" + id + ", doctorId=" + doctorId + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", packageName=" + packageName + ", amount=" + amount + ", amountViaCash=" + amountViaCash
				+ ", amountViaCheque=" + amountViaCheque + ", mode=" + mode + ", bankName=" + bankName
				+ ", countryCode=" + countryCode + ", chequeNo=" + chequeNo + ", branch=" + branch + ", chequeDate="
				+ chequeDate + ", isAdvertisement=" + isAdvertisement + ", discarded=" + discarded + "]";
	}

	
	
	
}
