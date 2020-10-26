package com.dpdocter.request;

import com.dpdocter.enums.ModeOfPayment;

public class PaymentDetails {

	private String bankName;
	
	private String branchName;
	
	private String cardNumber;
	
	private String checkNumber;
	
	private Double amount;
	
	private ModeOfPayment paymentMode;

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}



	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public ModeOfPayment getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(ModeOfPayment paymentMode) {
		this.paymentMode = paymentMode;
	}
	
	
		
		
}
