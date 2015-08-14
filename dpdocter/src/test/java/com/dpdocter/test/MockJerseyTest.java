//package com.dpdocter.test;
//
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.Application;
//import javax.ws.rs.core.MediaType;
//
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.test.JerseyTest;
//import org.junit.Test;
//
//import com.dpdocter.request.DiseaseAddEditRequest;
//import com.dpdocter.webservices.HistoryApi;
//import com.dpdocter.webservices.PathProxy;
//
//public class MockJerseyTest extends JerseyTest {
//    @Override
//    protected Application configure() {
//	return new ResourceConfig(HistoryApi.class);
//    }
//
//    @Test
//    public void test() {
//	String targetPath = PathProxy.HISTORY_BASE_URL + PathProxy.HistoryUrls.ADD_DISEASE;
//	DiseaseAddEditRequest request = new DiseaseAddEditRequest();
//	Entity<DiseaseAddEditRequest> entity = Entity.entity(request, MediaType.APPLICATION_JSON);
//	target(targetPath).request().post(entity);
//    }
//}
