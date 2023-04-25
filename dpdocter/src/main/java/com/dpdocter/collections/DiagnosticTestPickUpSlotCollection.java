package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.PickUpSlot;
import com.dpdocter.enums.Day;

@Document(collection = "diagnostic_test_pick_up_slot_cl")
public class DiagnosticTestPickUpSlotCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private Map<Day, List<PickUpSlot>> slots;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Map<Day, List<PickUpSlot>> getSlots() {
		return slots;
	}

	public void setSlots(Map<Day, List<PickUpSlot>> slots) {
		this.slots = slots;
	}

	@Override
	public String toString() {
		return "DiagnosticTestPickUpSlotCollection [id=" + id + ", slots=" + slots + "]";
	}
}
