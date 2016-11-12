package com.dpdocter.integrationTest;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.collections.ContactUsCollection;
import com.dpdocter.repository.ContactUsRepository;
import com.dpdocter.services.AdminServices;

@ContextConfiguration(locations = "classpath:spring-context.xml")
@EnableTransactionManagement
@TestExecutionListeners(value = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
public class AdminServiceTest extends AbstractTestNGSpringContextTests {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceTest.class);

	@Autowired
	private ContactUsRepository contactUsRepository;

	@Test
	public void test() {

		List<ContactUsCollection> list = contactUsRepository.findAll();
		for (ContactUsCollection contactUscollection : list) {
			System.out.println(contactUscollection.getName());
			System.out.println(contactUscollection.getMobileNumber());

		}

	}

}
