package com.dpdocter.tests;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.repository.RecordsRepository;

public class GeneralTests {
	@Autowired
	private RecordsRepository recordsRepository;

	public static void main(String args[]) {
		RecordsCollection record = new RecordsCollection();

		record.setCreatedDate(new Date().getTime());
		record.setDeleted(false);
		record.setDescription("For test");
		record.setDoctorId("01DTEST");
		record.setHospitalId("01HTEST");
		record.setId("01TEST");
		record.setLocationId("01LTEST");
		record.setPatientId("01PTEST");
		record.setRecordsLable("For test label");
		record.setRecordsPath("/home/isank/test.pdf");
		record.setRecordsUrl("/home/isank");

		System.out.println(Converter.ObjectToJSON(record));
	}
}
