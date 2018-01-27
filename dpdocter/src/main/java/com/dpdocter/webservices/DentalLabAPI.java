package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.elasticsearch.document.ESDentalWorksDocument;
import com.dpdocter.elasticsearch.services.impl.ESDentalLabServiceImpl;
import com.dpdocter.enums.LabType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.LocationServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.DENTAL_LAB_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.DENTAL_LAB_BASE_URL, description = "Endpoint for dental lab")
public class DentalLabAPI {

	private static Logger logger = Logger.getLogger(DentalLabAPI.class.getName());
	
	@Autowired
	private DentalLabService dentalLabService;
	
	@Autowired
	private LocationServices locationServices;
	
	@Autowired
	private ESDentalLabServiceImpl esDentalLabServiceImpl;
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORKS)
	public Response<DentalWork> addEditDEntalWorks(AddEditCustomWorkRequest request) {
		DentalWork dentalWork = null;
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalWork = new DentalWork();
		dentalWork = dentalLabService.addEditCustomWork(request);
		Response<DentalWork> response = new Response<DentalWork>();
		if(dentalWork != null)
		{
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.GET_DENTAL_WORKS)
	public Response<DentalWork> getDentalWorks(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		Response<DentalWork> response = new Response<DentalWork>();
		response.setDataList(dentalLabService.getCustomWorks(page, size, searchTerm));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	@DELETE
	@ApiOperation(value = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS, notes = PathProxy.DentalLabUrls.DELETE_DENTAL_WORKS)
	public Response<DentalWork> deleteDentalWork(@QueryParam("id") String id,
			@QueryParam("discarded") boolean discarded) {
		
		DentalWork dentalWork = null;
		if (id == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		dentalWork = new DentalWork();
		dentalWork = dentalLabService.deleteCustomWork(id, discarded);
		Response<DentalWork> response = new Response<DentalWork>();
		if(dentalWork != null)
		{
			response.setData(dentalWork);
			ESDentalWorksDocument dentalWorksDocument = new ESDentalWorksDocument();
			BeanUtil.map(dentalWork, dentalWorksDocument);
			esDentalLabServiceImpl.addDentalWorks(dentalWorksDocument);
		}
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE, notes = PathProxy.DentalLabUrls.CHANGE_LAB_TYPE)
	public Response<Boolean> changeLabType(@QueryParam("doctorId") String doctorId,@QueryParam("locationId") String locationId,
			@QueryParam("labType") LabType labType) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.changeLabType(doctorId, locationId, labType));
		return response;
	}

	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setData(dentalLabService.addEditDentalLabDoctorAssociation(request));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_DENTAL_LAB_DOCTOR_ASSOCIATION)
	public Response<DentalLabDoctorAssociation> getDentalLabDoctorAssociation(@QueryParam("locationId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId != null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabDoctorAssociation> response = new Response<DentalLabDoctorAssociation>();
		response.setDataList(dentalLabService.getDentalLabDoctorAssociations(locationId, page, size, searchTerm));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP, notes = PathProxy.DentalLabUrls.ADD_EDIT_DENTAL_WORK_PICKUP)
	public Response<DentalLabPickup> addEditPickupRequest(DentalLabPickupRequest request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<DentalLabPickup> response = new Response<DentalLabPickup>();
		response.setData(dentalLabService.addEditDentalLabPickupRequest(request));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_WORK_ASSOCIAITION)
	public Response<Boolean> addEditRateCardWorkAssociation(List<RateCardDentalWorkAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditRateCardDentalWorkAssociation(request));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS, notes = PathProxy.DentalLabUrls.GET_RATE_CARD_WORKS)
	public Response<RateCardDentalWorkAssociation> getRateCardWorks(@QueryParam("page") int page,@QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm , @QueryParam("rateCardId") String rateCardId ,@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (rateCardId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(dentalLabService.getRateCardWorks(page, size, searchTerm, rateCardId, discarded));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_RATE_CARD_DOCTOR_ASSOCIAITION)
	public Response<Boolean> addEditRateCardDoctorAssociation(List<RateCardDoctorAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditRateCardDoctorAssociation(request));
		return response;
	}
	@Path(value = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_RATE_CARD_DOCTOR_ASSOCIATION)
	public Response<RateCardDentalWorkAssociation> getRateCards(@QueryParam("page") int page,@QueryParam("size") int size,
			@QueryParam("searchTerm") String searchTerm , @QueryParam("doctorId") String doctorId ,@DefaultValue("false") @QueryParam("discarded") Boolean discarded) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(dentalLabService.getRateCards(page, size, searchTerm, doctorId, discarded));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION)
	@POST
	@ApiOperation(value = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION, notes = PathProxy.DentalLabUrls.ADD_EDIT_COLLECTION_BOY_DOCTOR_ASSOCIAITION)
	public Response<Boolean> addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request) {
		if (request == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(dentalLabService.addEditCollectionBoyDoctorAssociation(request));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION, notes = PathProxy.DentalLabUrls.GET_COLLECTION_BOY_DOCTOR_ASSOCIATION)
	public Response<RateCardDentalWorkAssociation> getCBDoctorAssociation(@QueryParam("page") int page,@QueryParam("size") int size,
			 @QueryParam("doctorId") String doctorId , @QueryParam("dentalLabId") String dentalLabId , @QueryParam("collectionBoyId") String collectionBoyId) {
		if (doctorId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<RateCardDentalWorkAssociation> response = new Response<RateCardDentalWorkAssociation>();
		response.setDataList(dentalLabService.getCBAssociatedDoctors(doctorId, dentalLabId, collectionBoyId, size, page));
		return response;
	}
	
	@Path(value = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB)
	@GET
	@ApiOperation(value = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB, notes = PathProxy.DentalLabUrls.GET_CB_LIST_FOR_DENTAL_LAB)
	public Response<Object> getCBListByParentLab(@QueryParam("dentalLabId") String locationId,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {
		if (locationId == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Response<Object> response = new Response<Object>();
		response.setDataList(locationServices.getCollectionBoyList(size, page, locationId, searchTerm, LabType.DENTAL.getType()));
		response.setData(locationServices.getCBCount(locationId, searchTerm ,LabType.DENTAL.getType()));

		return response;
	}
}
