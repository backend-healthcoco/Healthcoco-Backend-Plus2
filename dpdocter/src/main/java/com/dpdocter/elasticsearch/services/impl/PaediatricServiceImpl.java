package com.dpdocter.elasticsearch.services.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.GrowthChart;
import com.dpdocter.collections.GrowthChartCollection;
import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.elasticsearch.repository.VaccineRepository;
import com.dpdocter.elasticsearch.services.PaediatricService;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.GrowthChartRepository;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.VaccineResponse;

public class PaediatricServiceImpl implements PaediatricService{
	
	@Autowired
	private GrowthChartRepository growthChartRepository;
	
	@Autowired
	private VaccineRepository vaccineRepository;

	
	@Override
	@Transactional
	public GrowthChart addEditGrowthChart(GrowthChart growthChart)
	{
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			if(growthChart.getId() != null)
			{
				growthChartCollection = growthChartRepository.findOne(new ObjectId(growthChart.getId()));
			}
			else
			{
				growthChartCollection = new GrowthChartCollection();
			}
			BeanUtil.map(growthChart, growthChartCollection);
			growthChartCollection = growthChartRepository.save(growthChartCollection);
			if(growthChartCollection != null){
				response = new GrowthChart();
				 BeanUtil.map(growthChartCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
			
		}
		return response;
	}
	
	@Override
	@Transactional
	public GrowthChart getGrowthChartById(String id) {
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	
	/*public List<GrowthChart> getGrowthChartById(String patientId, String doctorId, String locationId, String hospitalId, int page, int size) {
		List<GrowthChart> growthCharts = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}*/

	@Override
	@Transactional
	public Boolean discardGrowthChart(String id, Boolean discarded) {
		Boolean response = false;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				growthChartCollection.setDiscarded(discarded);
				growthChartRepository.save(growthChartCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	

	@Override
	@Transactional
	public VaccineResponse addEditVaccine(VaccineRequest request)
	{
		 VaccineResponse response = null;
		 VaccineCollection vaccineCollection = null;
		try {
			if(request.getId() != null)
			{
				vaccineCollection = vaccineRepository.findOne(new ObjectId(request.getId()));
			}
			else
			{
				vaccineCollection = new VaccineCollection();
			}
			BeanUtil.map(request, vaccineCollection);
			vaccineCollection = vaccineRepository.save(vaccineCollection);
			if(vaccineCollection != null){
				response = new VaccineResponse();
				 BeanUtil.map(vaccineCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}
	
	@Override
	@Transactional
	public VaccineResponse getVaccineById(String id) {
		VaccineResponse response = null;
		VaccineCollection vaccineCollection = null;
		try {
			vaccineCollection = vaccineRepository.findOne(new ObjectId(id));
			if (vaccineCollection != null) {
				response = new VaccineResponse();
				BeanUtil.map(vaccineCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	
	
}
