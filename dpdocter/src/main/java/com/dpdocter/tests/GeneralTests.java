package com.dpdocter.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dpdocter.beans.Duration;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.enums.DirectionEnum;
import com.dpdocter.enums.DurationUnitEnum;
import com.dpdocter.request.PrescriptionAddEditRequest;

public class GeneralTests {
	public static void main(String args[]) {
		/*DrugAddEditRequest request = new DrugAddEditRequest();
		request.setDescription("Test Description");
		request.setDoctorId("D12345");
		request.setDrugName("Calpol");
		request.setDrugType(DrugTypeEnum.TABLET);
		request.setGenericNames(Arrays.asList("Calpol", "Paracetamol", "For Fever"));
		request.setHospitalId("H12345");
		request.setLocationId("L12345");
		Strength strength = new Strength();
		strength.setUnit(StrengthUnitEnum.MG);
		strength.setValue("10");
		request.setStrength(strength);*/
		DirectionEnum[] directions = { DirectionEnum.WITH_MILK };
		Duration duration = new Duration();
		duration.setUnit(DurationUnitEnum.MONTH);
		duration.setValue("1");
		/*List<TemplateItem> templateItems = new ArrayList<TemplateItem>();
		TemplateItem item1 = new TemplateItem();
		item1.setDirection(Arrays.asList(directions));
		item1.setDosage("1-1-1");
		item1.setDrugId("");
		item1.setDuration(duration);
		item1.setInstructions("Test Instructions");
		TemplateItem item2 = new TemplateItem();
		item2.setDirection(Arrays.asList(directions));
		item2.setDosage("1-1-1");
		item2.setDrugId("");
		item2.setDuration(duration);
		item2.setInstructions("Test Instructions");
		templateItems.add(item1);
		templateItems.add(item2);
		TemplateAddEditRequest request = new TemplateAddEditRequest();
		request.setDoctorId("D12345");
		request.setHospitalId("H12345");
		request.setLocationId("L12345");
		request.setName("Fever Template");
		request.setItems(templateItems);*/

		List<PrescriptionItem> prescriptionItems = new ArrayList<PrescriptionItem>();
		PrescriptionItem item1 = new PrescriptionItem();
		PrescriptionItem item2 = new PrescriptionItem();
		item1.setDirection(Arrays.asList(directions));
		item1.setDosage("1-1-1");
		item1.setDrugId("553abf5c2736ae76583d8d98");
		item1.setDuration(duration);
		item1.setInstructions("Test Instructions");

		item2.setDirection(Arrays.asList(directions));
		item2.setDosage("1-0-1");
		item2.setDrugId("553abf5c2736ae76583d8d99");
		item2.setDuration(duration);
		item2.setInstructions("Test Instructions");

		prescriptionItems.add(item1);
		prescriptionItems.add(item2);

		PrescriptionAddEditRequest request = new PrescriptionAddEditRequest();
		request.setDoctorId("D12345");
		request.setHospitalId("H12345");
		request.setLocationId("L12345");
		request.setName("Mr. X Prescription");
		request.setPatientId("P12345");
		request.setItems(prescriptionItems);
		System.out.println(Converter.ObjectToJSON(request));

	}
}
