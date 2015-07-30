package com.dpdocter.tests;

import com.dpdocter.beans.DrugStrengthUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Strength;
import com.dpdocter.request.DrugAddEditRequest;

public class GeneralTests {
    public static void main(String args[]) {
	DrugAddEditRequest request = new DrugAddEditRequest();

	request.setDescription("Test Drug Description");
	request.setDoctorId("5525ef96e4b077dfc168369b");
	request.setDrugName("Test Name");

	DrugType drugType = new DrugType();
	drugType.setId("55b21ea184ae42eacf0f667d");
	drugType.setType("Capsule");

	request.setDrugType(drugType);

	request.setHospitalId("5525ef96e4b077dfc16836a0");
	request.setLocationId("5525ef96e4b077dfc16836a1");

	Strength strength = new Strength();
	DrugStrengthUnit drugStrengthUnit = new DrugStrengthUnit();
	drugStrengthUnit.setId("55b2316c84aeff8658a675ec");
	drugStrengthUnit.setUnit("MG");

	strength.setStrengthUnit(drugStrengthUnit);
	strength.setValue("2");

	request.setStrength(strength);

	System.out.println(Converter.ObjectToJSON(request));
    }
}
