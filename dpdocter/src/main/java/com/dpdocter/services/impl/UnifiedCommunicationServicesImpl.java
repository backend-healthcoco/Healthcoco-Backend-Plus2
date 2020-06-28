package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dpdocter.collections.UnifiedCommunicationDetailsCollection;
import com.dpdocter.enums.ConsultationType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.UnifiedCommunicationDetailsRepository;
import com.dpdocter.services.UnifiedCommunicationServices;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.ChatGrant;
import com.twilio.jwt.accesstoken.VideoGrant;

public class UnifiedCommunicationServicesImpl implements UnifiedCommunicationServices {
	
	private static Logger logger = Logger.getLogger(UnifiedCommunicationServicesImpl.class);

	@Value(value = "${twilio.account.sid}")
	private String TWILIO_ACCOUNT_SID;

	@Value(value = "${twilio.api.key}")
	private String TWILIO_API_KEY;

	@Value(value = "${twilio.api.secret}")
	private String TWILIO_API_SECRET;

	@Value(value = "${service.sid}")
	private String SERVICE_SID;
	
	@Value(value = "${twilio.chat.ttl}")
	private int TWILIO_CHAT_TTL;
	
	@Value(value = "${twilio.video.ttl}")
	private int TWILIO_VIDEO_TTL;
	
	@Autowired
	private UnifiedCommunicationDetailsRepository unifiedCommunicationDetailsRepository;
	
	@Override
	public String createChatAccessToken(String userId) {
		String response = null;
		UnifiedCommunicationDetailsCollection unifiedCommunicationDetailsCollection = null;
		try {
//			List<UnifiedCommunicationDetailsCollection> unifiedCommunicationDetailsCollections = unifiedCommunicationDetailsRepository.findByUserIdAndTypeAndIsExpired(
//					new ObjectId(userId), ConsultationType.CHAT.getType(), false);
//			if(unifiedCommunicationDetailsCollections != null && !unifiedCommunicationDetailsCollections.isEmpty()) {
//				for(UnifiedCommunicationDetailsCollection coll : unifiedCommunicationDetailsCollections) {
//					if(isExpired(coll.getCreatedTime(), TWILIO_CHAT_TTL)) {
//						coll.setIsExpired(true);
//						coll.setUpdatedTime(new Date());
//						unifiedCommunicationDetailsRepository.save(coll);
//					}else {
//						unifiedCommunicationDetailsCollection = coll;
//					}
//				}
//			}
//		    if(unifiedCommunicationDetailsCollection == null) {
		    	ChatGrant grant = new ChatGrant();
			    grant.setServiceSid(SERVICE_SID);

			    AccessToken token = new AccessToken.Builder(TWILIO_ACCOUNT_SID, TWILIO_API_KEY, TWILIO_API_SECRET)
			        .identity(userId).grant(grant).ttl(TWILIO_CHAT_TTL).build();

			    unifiedCommunicationDetailsCollection = new UnifiedCommunicationDetailsCollection(ConsultationType.CHAT,
			    		new ObjectId(userId), TWILIO_CHAT_TTL, false, token.toJwt());
			    
			    unifiedCommunicationDetailsCollection = unifiedCommunicationDetailsRepository.save(unifiedCommunicationDetailsCollection);
			    response = unifiedCommunicationDetailsCollection.getToken();
//		    }
		}catch (Exception e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

	@Override
	public String createVideoAccessToken(String userId, String room) {
		String response = null;
		try {
			
			VideoGrant grant = new VideoGrant().setRoom(room);

		    AccessToken token = new AccessToken.Builder(TWILIO_ACCOUNT_SID, TWILIO_API_KEY, TWILIO_API_SECRET)
		        .identity(userId).grant(grant).ttl(TWILIO_VIDEO_TTL).build();

		    UnifiedCommunicationDetailsCollection unifiedCommunicationDetailsCollection = new UnifiedCommunicationDetailsCollection(
		    		ConsultationType.VIDEO,
		    		new ObjectId(userId), TWILIO_VIDEO_TTL, false, token.toJwt());
		    
		    unifiedCommunicationDetailsCollection = unifiedCommunicationDetailsRepository.save(unifiedCommunicationDetailsCollection);
		    response = unifiedCommunicationDetailsCollection.getToken();
		}catch (Exception e) {
			logger.error("Error : " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error : " + e.getMessage());
		}
		return response;
	}

}
