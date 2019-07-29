package com.dpdocter.integrationTest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.io.UnsupportedEncodingException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.dpdocter.beans.LoginResponse;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.LoginRequest;
import com.dpdocter.services.LoginService;
import com.mongodb.Mongo;

import common.util.web.DPDoctorUtils;
import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import de.flapdoodle.embedmongo.runtime.Network;

@ContextConfiguration(locations = "classpath:spring-context.xml")
@EnableTransactionManagement
@TestExecutionListeners(value = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class })
public class LoginServiceTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private LoginService loginService;

	@Autowired
	private UserRepository userRepository;

	private MongodExecutable mongodExe;
	private MongodProcess mongod;
	private Mongo mongo;

	@SuppressWarnings("deprecation")

//	@BeforeTest(alwaysRun = true)
	public void beforeEach() throws Exception {
		MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
		mongodExe = runtime.prepare(new MongodConfig(Version.V2_1_1, 51567, Network.localhostIsIPv6()));
		mongod = mongodExe.start();
		mongo = new Mongo("127.0.0.1", 51567);
	}

//	@Test
	public void testLogin() throws UnsupportedEncodingException {
		LoginRequest request = new LoginRequest();
		request.setUsername("manishmethani6@gmail.com");
		String password = "admin";
		request.setPassword(DPDoctorUtils.getSHA3SecurePassword(password.toCharArray()));

		LoginResponse loginResponse = loginService.login(request, false, false);

		System.out.println();
		AssertJUnit.assertEquals(loginResponse.getUser().getUserName(), request.getUsername());

	}

	// @Test
	public void testuser() {
		UserCollection userCollection = userRepository.findById(new ObjectId("5794af08e4b01f1d73f9b7c0")).orElse(null);
		System.out.println(userCollection.getFirstName());

	}

//	@AfterTest(alwaysRun = true)
	public void afterEach() throws Exception {
		if (this.mongod != null) {
			this.mongod.stop();
		}
	}

}
