package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.Slot;

public class SlotDataResponse {

	long date;

	List<Slot> slots;

	AppointmentSlot appointmentSlot;

	private AppointmentSlot onlineConsultationSlot;

	String nextAvailableSlotDate;

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public List<Slot> getSlots() {
		return slots;
	}

	public void setSlots(List<Slot> slots) {
		this.slots = slots;
	}

	public AppointmentSlot getAppointmentSlot() {
		return appointmentSlot;
	}

	public void setAppointmentSlot(AppointmentSlot appointmentSlot) {
		this.appointmentSlot = appointmentSlot;
	}

	public String getNextAvailableSlotDate() {
		return nextAvailableSlotDate;
	}

	public void setNextAvailableSlotDate(String nextAvailableSlotDate) {
		this.nextAvailableSlotDate = nextAvailableSlotDate;
	}

	public AppointmentSlot getOnlineConsultationSlot() {
		return onlineConsultationSlot;
	}

	public void setOnlineConsultationSlot(AppointmentSlot onlineConsultationSlot) {
		this.onlineConsultationSlot = onlineConsultationSlot;
	}

	@Override
	public String toString() {
		return "SlotDataResponse [date=" + date + ", slots=" + slots + ", appointmentSlot=" + appointmentSlot
				+ ", nextAvailableSlotDate=" + nextAvailableSlotDate + "]";
	}
}
