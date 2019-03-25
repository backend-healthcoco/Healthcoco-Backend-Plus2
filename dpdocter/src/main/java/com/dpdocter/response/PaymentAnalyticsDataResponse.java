package com.dpdocter.response;

import java.util.Date;

import com.dpdocter.enums.ModeOfPayment;

public class PaymentAnalyticsDataResponse {

	private String title;
	
	private String doctorName;
	
	private Double cash;
	
	private Double  online;
	
	private Double card;
	
	private Double wallet;
	
	private Date date;

	private Double total;
	
	private ModeOfPayment modeOfPayment;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Double getCash() {
		return cash;
	}

	public void setCash(Double cash) {
		this.cash = cash;
	}

	public Double getOnline() {
		return online;
	}

	public void setOnline(Double online) {
		this.online = online;
	}

	public Double getCard() {
		return card;
	}

	public void setCard(Double card) {
		this.card = card;
	}

	public Double getWallet() {
		return wallet;
	}

	public void setWallet(Double wallet) {
		this.wallet = wallet;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	@Override
	public String toString() {
		return "PaymentAnalyticsDataResponse [title=" + title + ", doctorName=" + doctorName + ", cash=" + cash
				+ ", online=" + online + ", card=" + card + ", wallet=" + wallet + ", date=" + date + ", total=" + total
				+ ", modeOfPayment=" + modeOfPayment +"]";
	}
}
