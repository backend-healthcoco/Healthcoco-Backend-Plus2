//package com.dpdocter.webservices;
//
//import java.util.List;
//
//import com.dpdocter.request.OrthoEditProgressDatesRequest;
//import com.dpdocter.request.OrthoEditRequest;
//import com.dpdocter.response.OrthoProgressResponse;
//import com.dpdocter.response.OrthoResponse;
//
//public interface OrthoService {
//
//	OrthoResponse editOrthoPlanningDetails(OrthoEditRequest request);
//
//	Boolean deleteOrthoPlanningDetails(String id, Boolean discarded);
//
//	List<OrthoResponse> getOrthoPlanningDetails(long page, int size, String doctorId, String locationId, String hospitalId,
//			String patientId, String updatedTime, Boolean discarded, boolean b);
//
//	OrthoProgressResponse getOrthoProgressById(String id);
//
//	OrthoProgressResponse editOrthoProgressDetailsDates(OrthoEditProgressDatesRequest request);
//
//}
