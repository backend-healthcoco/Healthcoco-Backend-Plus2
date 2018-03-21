package com.dpdocter.test;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.reflections.BeanUtil;

//package com.dpdocter.test;
//
//import static org.junit.Assert.assertEquals;
//
//import java.net.UnknownHostException;
//import java.util.Date;
//
//import org.codehaus.jettison.json.JSONException;
//import org.codehaus.jettison.json.JSONObject;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.*;
//
//import com.dpdocter.collections.DrugTypeCollection;
//import com.dpdocter.repository.DrugItemsTestRepository;
//import com.dpdocter.request.DrugTypeAddEditRequest;
//import com.dpdocter.response.DrugTypeAddEditResponse;
//import com.dpdocter.services.PrescriptionServices;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.Mongo;
//import com.mongodb.MongoException;
//import common.util.web.JacksonUtil;
//import common.util.web.Response;
//import junit.framework.TestCase;
//import org.mockito.*;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.test.context.junit4.*;
//import org.springframework.test.context.*;
//import com.github.springtestdbunit.DbUnitTestExecutionListener;
//import org.springframework.test.context.support.*;
//import org.springframework.test.context.transaction.*;
//
//@Configuration
//@ComponentScan(basePackages={"main.java.com.dpdocter.services"})
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(locations = {"classpath:spring/spring-main.xml"})
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
//        DirtiesContextTestExecutionListener.class,
//        TransactionalTestExecutionListener.class,
//        DbUnitTestExecutionListener.class})
//
//public class DrugTypeTests extends TestCase{
//	
//	
//	
//    @Autowired private PrescriptionServices prescriptionServices;
// 	
//	@Test
//	public void testAddDrug()  throws Exception {
//		
//		//		MockHttpServletRequest request = new MockHttpServletRequest();
////        MockHttpServletResponse response = new MockHttpServletResponse();
//		
//		DrugTypeAddEditRequest collection = new DrugTypeAddEditRequest();
//    	collection.setType("SYRUP");
//    	collection.setDoctorId("doctorId");
//    	collection.setHospitalId("hospitalId");
//    	collection.setLocationId("locationId");
////    	collection.setCreatedTime(new Date());
//    	
//    	System.out.println(prescriptionServices);
//    	DrugTypeAddEditResponse response = prescriptionServices.addDrugType(collection);
//	    
//    	System.out.println(response);
//
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
////    DB db;
////
////    DrugItemsTestRepository repository;
////
////    @Before
////    public void setUp() throws UnknownHostException, MongoException {
////	@SuppressWarnings("deprecation")
////	Mongo connection = new Mongo();
////	db = connection.getDB("dpdocter_dp");
////	DBCollection collection = db.getCollection("drug_type_cl");
////	repository = new DrugItemsTestRepository(collection);
////    }
////
////    @Test
////    public void save() {
////	String drugType = null;
////	try {
////		DrugTypeCollection collection =setParameters(null, null, null, new Date(0));
////	    drugType = JacksonUtil.obj2Json(collection);
////	    JSONObject added = repository.add(drugType);
////	    JSONObject retrieved = repository.findBy("type", "SYRUP");
////
////	    assertEquals("save error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////	} catch (JSONException e) {e.printStackTrace();}
////    }
////
////    @Test
////    public void getCustomDrugtype(){
////    	String drugType = null;
////    	try {
////    	    DrugTypeCollection collection = setParameters("D12345","H12345","L12345",new Date(0));
////     
////    	    drugType = JacksonUtil.obj2Json(collection);
////    	    JSONObject added = repository.add(drugType);
////    	    JSONObject retrieved = repository.findBy("D12345","H12345","L12345",null,true);
////
////    	    assertEquals("getCustomDrugtype error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////
////    	} catch (JSONException e) {e.printStackTrace();}
////    }
////    @Test
////    public void getDrugTypeCreatedTime(){
////    	String drugType = null;
////    	try {
////    	    DrugTypeCollection collection = setParameters("D12345","H12345","L12345",new Date(147258369));
////     
////    	    drugType = JacksonUtil.obj2Json(collection);
////    	    JSONObject added = repository.add(drugType);
////    	    JSONObject retrieved = repository.findBy(null,null,null,new Date(147258369),true);
////
////    	    assertEquals("getDrugTypeCreatedTime error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////
////    	} catch (JSONException e) {e.printStackTrace();}
////
////    }
////    @Test
////    public void getDrugTypeCreatedTimeIsDeleted(){
////    	String drugType = null;
////    	try {
////    	    DrugTypeCollection collection = setParameters("D12345","H12345","L12345",new Date(147258369));
////     
////    	    drugType = JacksonUtil.obj2Json(collection);
////    	    JSONObject added = repository.add(drugType);
////    	    JSONObject retrieved = repository.findBy(null,null,null,new Date(147258369),true);
////
////    	    assertEquals("getDrugTypeCreatedTimeIsDeleted error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////
////    	} catch (JSONException e) {e.printStackTrace();}
////
////    }
////
////    @Test
////    public void getCustomDrugTypeCreatedTime(){
////    	String drugType = null;
////    	try {
////    	    DrugTypeCollection collection = setParameters("D12345","H12345","L12345",new Date(147258369));
////     
////    	    drugType = JacksonUtil.obj2Json(collection);
////    	    JSONObject added = repository.add(drugType);
////    	    JSONObject retrieved = repository.findBy("D12345","H12345","L12345",new Date(147258369),true);
////
////    	    assertEquals("getDrugTypeCreatedTime error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////
////    	} catch (JSONException e) {e.printStackTrace();}
////
////    }
////
////    @Test
////    public void getCustomDrugTypeCreatedTimeIsDeleted(){
////    	String drugType = null;
////    	try {
////    	    DrugTypeCollection collection = setParameters("D12345","H12345","L12345",new Date(147258369));
////    
////    	    drugType = JacksonUtil.obj2Json(collection);
////
////    	    JSONObject added = repository.add(drugType);
////    	    JSONObject retrieved = repository.findBy("D12345","H12345","L12345",new Date(147258369),false);
////
////    	    assertEquals("getCustomDrugTypeCreatedTimeIsDeleted error",added !=null ? added.toString() : "", retrieved !=null ?retrieved.toString():"");
////
////    	} catch (JSONException e) {e.printStackTrace();}
////
////    }
////    
////    private DrugTypeCollection setParameters(String doctorId, String hospitalId, String locationId, Date createdTime) {
////    	DrugTypeCollection collection = new DrugTypeCollection();
////    	collection.setType("SYRUP");
////    	collection.setDoctorId(doctorId);
////    	collection.setHospitalId(hospitalId);
////    	collection.setLocationId(locationId);
////    	collection.setCreatedTime(createdTime);
////	    
////		return collection;
////	}
////    
////	@After
////    public void tearDown() {
////	db.dropDatabase();
////	db = null;
////    }
//
// }
public class DrugTypeTests {
	public static void main(String arg[]) {
		List<String> list1 = new ArrayList<String>();
		list1.add("5794afade4b01f1d73f9b7e7");
		list1.add("5794afade4b01f1d73f9b7e8");
		List<ObjectId> list2 = new ArrayList<ObjectId>();
		BeanUtil.map(list1, list2);
		System.out.println(list2.get(0));
		System.out.println(list2.get(1));
	}
}
