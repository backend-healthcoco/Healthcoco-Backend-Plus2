package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.Slot;

public class SlotDataResponse {

	List<Slot> slots;
	
	AppointmentSlot appointmentSlot;

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

	@Override
	public String toString() {
		return "SlotDataResponse [slots=" + slots + ", appointmentSlot=" + appointmentSlot + "]";
	}
}
