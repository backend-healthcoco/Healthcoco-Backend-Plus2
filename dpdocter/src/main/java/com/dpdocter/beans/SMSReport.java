package com.dpdocter.beans;

import java.util.Date;

public class SMSReport {

    private String desc;

    private String status;

    private String number;

    private String date;

    public String getDesc() {
	return desc;
    }

    public void setDesc(String desc) {
	this.desc = desc;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public String getNumber() {
	return number;
    }

    public void setNumber(String number) {
	this.number = number;
    }

    public String getDate() {
	return date;
    }

    public void setDate(String date) {
	this.date = date;
    }

    @Override
    public String toString() {
	return "SMSReport [desc=" + desc + ", status=" + status + ", number=" + number + ", date=" + date + "]";
    }
}
