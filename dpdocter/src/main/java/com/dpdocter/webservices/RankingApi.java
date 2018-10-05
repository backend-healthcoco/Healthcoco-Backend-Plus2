package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.RankingCount;
import com.dpdocter.services.RankingAlgorithmsServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.RANKING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.RANKING_BASE_URL, description = "Endpoint for ranking")
public class RankingApi {

//	private static Logger logger = Logger.getLogger(RankingApi.class.getName());
	
	@Autowired
	RankingAlgorithmsServices rankingAlgorithmsServices;
	
	@Path(value = PathProxy.RankingUrls.GET_DOCTORS_RANKING)
	@GET
	@ApiOperation(value = PathProxy.RankingUrls.GET_DOCTORS_RANKING, notes = PathProxy.RankingUrls.GET_DOCTORS_RANKING)
	public Response<RankingCount> getDoctorsRankingCount(@QueryParam("page") long page, @QueryParam("size") int size){
		
		List<RankingCount> rankingCounts = rankingAlgorithmsServices.getDoctorsRankingCount(page, size);
		Response<RankingCount> response = new Response<RankingCount>();
		response.setDataList(rankingCounts);
		return response;
	}
	
	@Path(value = PathProxy.RankingUrls.CALCULATE_RANKING)
	@GET
	@ApiOperation(value = PathProxy.RankingUrls.CALCULATE_RANKING, notes = PathProxy.RankingUrls.CALCULATE_RANKING)
	public Response<Boolean> calculateRankingOfResources(){
		
		rankingAlgorithmsServices.calculateRankingOfResources();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}
}
