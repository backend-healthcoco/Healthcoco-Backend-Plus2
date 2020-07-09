package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.OnlineConsultationAnalytics;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.OnlineConsultationService;

import common.util.web.DPDoctorUtils;

@Service
public class OnlineConsultationServiceImpl implements OnlineConsultationService {

	private static Logger logger = LogManager.getLogger(OnlineConsultationServiceImpl.class.getName());

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public OnlineConsultationAnalytics getConsultationAnalytics(String fromDate, String toDate, String doctorId,
			String locationId, String type) {
		OnlineConsultationAnalytics response=new OnlineConsultationAnalytics();
		try {
			
			Criteria criteria = new Criteria();
			DateTime fromTime = null;
			DateTime toTime = null;
			Date from = null;
			Date to = null;
			long date = 0;
			

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(fromDate, toDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date(Long.parseLong(toDate));

			} else if (!DPDoctorUtils.anyStringEmpty(fromDate)) {
				from = new Date(Long.parseLong(fromDate));
				to = new Date();
			} else if (!DPDoctorUtils.anyStringEmpty(toDate)) {
				from = new Date(date);
				to = new Date(Long.parseLong(toDate));
			} else {
				from = new Date(date);
				to = new Date();
			}

			fromTime = new DateTime(from);
			toTime = new DateTime(to);

			criteria.and("fromDate").gte(fromTime).lte(toTime)
					.and("type").is(type);

			
			
			criteria.orOperator(new Criteria("state").is(AppointmentState.CONFIRM.toString()),
					new Criteria("state").is(AppointmentState.RESCHEDULE.toString()),
					new Criteria("state").is(AppointmentState.NEW.toString()));

			Criteria criteria2=criteria;
			
			response.setTotalOnlineConsultation(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));
			criteria.and("consultationType").is(ConsultationType.VIDEO.toString());
			response.setTotalVideoConsultation(mongoTemplate.count(new Query(criteria), AppointmentCollection.class));
			criteria2.and("consultationType").is(ConsultationType.CHAT.toString());
			response.setTotalChatConsultation(mongoTemplate.count(new Query(criteria2),AppointmentCollection.class));
			
		}catch (BusinessException e) {
			logger.error("Error while getting online Consultation Analytics " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting online Consultation Analytics " + e.getMessage());

		}
		return response;
	}
	

}
