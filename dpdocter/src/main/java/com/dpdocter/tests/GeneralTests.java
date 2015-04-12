package com.dpdocter.tests;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.beans.Strength;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDeleteRequest;
import com.dpdocter.webservices.PathProxy;

public class GeneralTests {
	@Autowired
	private RecordsRepository recordsRepository;

	public static void main(String args[]) {
		System.out.println(Converter.ObjectToJSON(new DrugDeleteRequest()));
	}
}
