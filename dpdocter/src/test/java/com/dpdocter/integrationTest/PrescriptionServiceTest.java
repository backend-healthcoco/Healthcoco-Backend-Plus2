package com.dpdocter.integrationTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.PrescriptionServices;

@ContextConfiguration(locations = "classpath:spring-context.xml")
@EnableTransactionManagement
@TestExecutionListeners(value = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
public class PrescriptionServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private DiagnosticTestRepository diagnosisTestRepository;

	private static String doctorId = "5794add2e4b01f1d73f9b74f";
	private static String hospitalId = "5794add2e4b01f1d73f9b750";
	private static String locationId = "5794add3e4b01f1d73f9b751";
	/*
	 * @Test public void addDrugType() { DrugTypeAddEditRequest request = new
	 * DrugTypeAddEditRequest(); request.setDoctorId(doctorId);
	 * request.setHospitalId(hospitalId); request.setLocationId(locationId);
	 * request.setType("TAB"); DrugTypeAddEditResponse response =
	 * prescriptionServices.addDrugType(request);
	 * AssertJUnit.assertEquals(response.getDoctorId(), request.getDoctorId());
	 * AssertJUnit.assertEquals(response.getHospitalId(),
	 * request.getHospitalId());
	 * AssertJUnit.assertEquals(response.getLocationId(),
	 * request.getLocationId()); AssertJUnit.assertEquals(response.getType(),
	 * request.getType()); }
	 */

	/*@Test
	public void adddrugtest() {
		UserCollection userCollection = new UserCollection();
		userCollection.setFirstName("Harish");
		userCollection.setMiddleName("S.");
		userCollection.setLastName("Sendurse");
		userCollection.setIsActive(true);
		userCollection.setIsVerified(true);
		userCollection.setMobileNumber("9564555455");
		userCollection.setUserName("harish.sendurse@healthcoco.com");
		userCollection.setCreatedTime(new Date());
		userCollection = userRepository.save(userCollection);
		DrugAddEditRequest request = new DrugAddEditRequest();
		request.setCompanyName("RBPLUS");
		request.setDrugName("REJOINT");
		DrugType drugType = new DrugType();
		drugType.setCreatedTime(new Date());
		drugType.setUpdatedTime(new Date());
		drugType.setType("TAB");
		request.setDrugType(drugType);
		request.setDosage("0-1-0");
		request.setDoctorId(doctorId);
		request.setHospitalId(hospitalId);
		request.setLocationId(locationId);
		request.setDoctorId(userCollection.getId().toString());
		request.setLocationId(locationId);
		Drug response = prescriptionServices.addDrug(request);
		AssertJUnit.assertEquals(response.getDoctorId(), request.getDoctorId());
		AssertJUnit.assertEquals(response.getDrugName(), request.getDrugName());
		AssertJUnit.assertEquals(response.getDosage(), request.getDosage());

	}
*/
/*	@Test
	public void prescriptionTest() {
		UserCollection userCollection = new UserCollection();
		userCollection.setFirstName("Parag");
		userCollection.setLastName("pakhale");
		userCollection.setIsActive(true);
		userCollection.setIsVerified(true);
		userCollection.setTitle("Dr.");
		userCollection.setMobileNumber("9564555455");
		userCollection.setUserName("parag.pakhale@healthcoco.com");
		userCollection.setCreatedTime(new Date());
		userCollection = userRepository.save(userCollection);
		DrugCollection drugCollection = new DrugCollection();
		drugCollection.setCompanyName("Nicip");
		drugCollection.setDrugName("Crocin");
		DrugType drugType = new DrugType();
		drugType.setCreatedTime(new Date());
		drugType.setUpdatedTime(new Date());
		drugType.setType("TAB");
		drugCollection.setDrugType(drugType);
		drugCollection.setDosage("0-1-0");
		drugCollection.setDoctorId(userCollection.getId());
		drugCollection = drugRepository.save(drugCollection);
		DiagnosticTestCollection testCollection = new DiagnosticTestCollection();
		testCollection.setCreatedBy(doctorId);
		testCollection.setCreatedTime(new Date());
		testCollection.setExplanation("noting to say");
		testCollection.setHospitalId(new ObjectId(hospitalId));
		testCollection.setLocationId(new ObjectId(locationId));
		testCollection.setTestName("bloodTest");
		testCollection.setCode("440245");
		testCollection = diagnosisTestRepository.save(testCollection);
		PrescriptionAddEditRequest request = new PrescriptionAddEditRequest();
		request.setAdvice("Drink daily min 4 lt water");
		request.setCreatedBy(doctorId);
		request.setDoctorId(doctorId);
		request.setName("Abhijit Shukla");
		request.setLocationId(locationId);
		request.setPrescriptionCode("4555456");
		request.setVisitId(hospitalId);
		request.setLocationId(locationId);

		PrescriptionItem item = new PrescriptionItem();
		item.setDosage("0-1-1");
		item.setDrugId(drugCollection.getId().toString());
		List<PrescriptionItem> items = new ArrayList<PrescriptionItem>();
		items.add(item);
		request.setItems(items);
		DiagnosticTest test = new DiagnosticTest();
		test.setCode(testCollection.getCode());
		test.setCreatedBy(testCollection.getCreatedBy());
		test.setTestName(testCollection.getTestName());
		test.setExplanation(testCollection.getExplanation());
		List<DiagnosticTest> tests = new ArrayList<DiagnosticTest>();
		request.setDiagnosticTests(tests);

		PrescriptionAddEditResponse response = prescriptionServices.addPrescription(request);
		AssertJUnit.assertEquals(response.getAdvice(), request.getAdvice());
		AssertJUnit.assertEquals(response.getDoctorId(), request.getDoctorId());
		AssertJUnit.assertEquals(response.getName(), request.getName());
		AssertJUnit.assertEquals(response.getDiagnosticTests(), request.getDiagnosticTests());
		AssertJUnit.assertEquals(response.getDiagnosticTests(), request.getDiagnosticTests());
	}*/

}
