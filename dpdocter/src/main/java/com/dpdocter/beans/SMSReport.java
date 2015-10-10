package com.dpdocter.beans;

import java.util.Date;

public class SMSReport {

    private String desc;

    private String status;

    private SMSReport number;

    private Date date;

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

    public SMSReport getNumber() {
	return number;
    }

    public void setNumber(SMSReport number) {
	this.number = number;
    }

    public Date getDate() {
	return date;
    }

    public void setDate(Date date) {
	this.date = date;
    }

    @Override
    public String toString() {
	return "SMSReport [desc=" + desc + ", status=" + status + ", number=" + number + ", date=" + date + "]";
    }
}
