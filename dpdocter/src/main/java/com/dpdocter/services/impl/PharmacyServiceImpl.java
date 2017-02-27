
package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.json.Jackson;
import com.dpdocter.collections.SearchRequestFromUserCollection;
import com.dpdocter.collections.SearchRequestToPharmacyCollection;
import com.dpdocter.enums.ReplyType;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocaleRepository;
import com.dpdocter.repository.SearchRequestFromUserRepository;
import com.dpdocter.repository.SearchRequestToPharmacyRepository;
import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.PharmacyResponse;
import com.dpdocter.services.PharmacyService;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;

@Service
public class PharmacyServiceImpl implements PharmacyService {

	private static Logger logger = Logger.getLogger(PharmacyServiceImpl.class.getName());

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;

	@Value(value = "${search.pharmacy.request.queue}")
	private String searchPharmacyRequestQueue;

	@Value(value = "${pharmacy.response.queue}")
	private String pharmacyResponseQueue;

	@Autowired
	private SearchRequestFromUserRepository searchRequestFromUserRepository;

	@Autowired
	private LocaleRepository localeRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private SearchRequestToPharmacyRepository searchRequestToPharmacyRepository;

	@Override
	@Transactional
	public Boolean addSearchRequest(UserSearchRequest request) {
		/*
		 * TODO : generate uniqueRequestId then add request in queue and
		 * collection. write two different methods addInQueue and addInDb so
		 * that it can be reuse or else it will be easy to change flow if
		 * required
		 */
		Boolean response = false;
		try {
			addSearchRequestInQueue(request);
			addSearchRequestInCollection(request);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While adding Search Request In Queue");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While adding  Search Request In Queue");
		}

		return response;
	}

	private void addSearchRequestInCollection(UserSearchRequest request) {
		SearchRequestFromUserCollection searchRequestFromUserCollection = new SearchRequestFromUserCollection();
		BeanUtil.map(request, searchRequestFromUserCollection);
		searchRequestFromUserCollection.setCreatedTime(new Date());
		searchRequestFromUserRepository.save(searchRequestFromUserCollection);

	}

	private void addSearchRequestInQueue(UserSearchRequest request) {

		AWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		sqs.setRegion(Region.getRegion(Regions.US_WEST_2));
		/*String searchPharmacyRequestQueueURL = "";

		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(searchPharmacyRequestQueue);
		System.out.println(getQueueUrlRequest);
		if (getQueueUrlRequest != null) {
			searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
		}
		if (DPDoctorUtils.anyStringEmpty(searchPharmacyRequestQueueURL)) {*/
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(searchPharmacyRequestQueue);
			String searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
		//}
		//CreateQueueRequest createQueueRequest = new CreateQueueRequest(searchPharmacyRequestQueue);
		//String searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
		String uniqueRequestId = UniqueIdInitial.PHARMACY_REQUEST.getInitial() + DPDoctorUtils.generateRandomId();
		request.setUniqueRequestId(uniqueRequestId);
		/*SendMessageRequest sendMessageRequest = new SendMessageRequ
		MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
		messageAttributeValue.setStringValue(uniqueRequestId);
		messageAttributeValue.setDataType("String");
		sendMessageRequest.addMessageAttributesEntry("uniqueRequestId", messageAttributeValue);*/
		String messageBody = Jackson.toJsonString(request) ;
		//sendMessageRequest.setMessageBody(Jackson.toJsonString(request));
		sqs.sendMessage(new SendMessageRequest(searchPharmacyRequestQueueURL, messageBody));
	}

	/*@Scheduled(fixedDelay = 9000)
	@Transactional
	private void retrieveUserReuqest() {
		
		 * TODO : get data from queue, generate uniqueResponseId. if pharmacyId
		 * is present then retrieve particular pharmacy and send notification to
		 * pharmacy with prescription request & uniqueRequestId and add in
		 * collection. If pharmacyId is null then search pharmacy using latLong
		 * || location approx 30km and send notification to pharmacy with
		 * presciption request & uniqueRequestId and then add in collection
		 * After sending request to pharmacy remove that particular request from
		 * queue
		 
		AWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		try {
			sqs.setRegion(Region.getRegion(Regions.US_WEST_2));
			String searchPharmacyRequestQueueURL = "";

			AmazonSQS client = new AmazonSQSClient();
			GetQueueUrlRequest request = new GetQueueUrlRequest().withQueueName(
			        "MyQueue").withQueueOwnerAWSAccountId("12345678910");
			GetQueueUrlResult response = client.getQueueUrl(request);
			
			GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(searchPharmacyRequestQueue);
			
			System.out.println(getQueueUrlRequest);
			if (getQueueUrlRequest != null) {
				searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
			}
			//if (DPDoctorUtils.anyStringEmpty(searchPharmacyRequestQueueURL)) {
				CreateQueueRequest createQueueRequest = new CreateQueueRequest(searchPharmacyRequestQueue);
				searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
			//}

			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(searchPharmacyRequestQueue);
			receiveMessageRequest.setMaxNumberOfMessages(10);
			receiveMessageRequest.setWaitTimeSeconds(20);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				System.out.println("  Message");
				System.out.println("    MessageId:     " + message.getMessageId());
				System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
				System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
				System.out.println("    Body:          " + message.getBody());
				for (Entry<String, String> entry : message.getAttributes().entrySet()) {
					System.out.println("  Attribute");
					System.out.println("    Name:  " + entry.getKey());
					System.out.println("    Value: " + entry.getValue());
				}

				UserSearchRequest userSearchRequest = Jackson.fromJsonString(message.getBody(),
						UserSearchRequest.class);
				if (DPDoctorUtils.anyStringEmpty(userSearchRequest.getLocaleId())) {
					BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
							.filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(userSearchRequest.getLatitude())
									.lon(userSearchRequest.getLongitude()).distance("30km"))
							.must(QueryBuilders.matchPhrasePrefixQuery("isLocaleListed", true));
					List<ESUserLocaleDocument> esUserLocaleDocuments = elasticsearchTemplate
							.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withSort(SortBuilders.fieldSort("localeRankingCount").order(SortOrder.DESC))
									.build(), ESUserLocaleDocument.class);
					for (ESUserLocaleDocument esUserLocaleDocument : esUserLocaleDocuments) {
						pushNotificationServices.notifyUser(esUserLocaleDocument.getId(), userSearchRequest,
								RoleEnum.PHARMIST, "You have received request to search Drugs");
						SearchRequestToPharmacyCollection searchRequestToPharmacy = new SearchRequestToPharmacyCollection();
						BeanUtil.map(userSearchRequest, searchRequestToPharmacy);
						String uniqueResponseId = UniqueIdInitial.PHARMACY_RESPONSE.getInitial()
								+ DPDoctorUtils.generateRandomId();
						searchRequestToPharmacy.setUniqueResponseId(uniqueResponseId);
						searchRequestToPharmacyRepository.save(searchRequestToPharmacy);
					}
				} else {
					pushNotificationServices.notifyUser(userSearchRequest.getLocaleId(), userSearchRequest,
							RoleEnum.PHARMIST, "You have received request to search Drugs");
					SearchRequestToPharmacyCollection searchRequestToPharmacy = new SearchRequestToPharmacyCollection();
					BeanUtil.map(userSearchRequest, searchRequestToPharmacy);
					String uniqueResponseId = UniqueIdInitial.PHARMACY_RESPONSE.getInitial()
							+ DPDoctorUtils.generateRandomId();
					searchRequestToPharmacy.setUniqueResponseId(uniqueResponseId);
					searchRequestToPharmacyRepository.save(searchRequestToPharmacy);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
		}
	}

	@Override
	@Transactional
	public Boolean addResponseInQueue(PharmacyResponse request) {
		
		 * TODO : Whenever pharmacy response is received check replyType if
		 * replyType = NO, update it in collection else if replyType = YES add
		 * in response queue, update reply in collection & send notification to
		 * patient data to be send in notification is : uniqueRequestId,
		 * pharmacyId, uniqueResponseId(other details if required) After sending
		 * request to patient remove that particular request from queue
		 
		Boolean response = false;
		String uniqueResponseId = null;
		try {
			if (request.getReplyType().equals(ReplyType.YES)) {
				uniqueResponseId = addPharmacyResponseInQueue(request);
			}
			addPharmacyResponseInCollection(request, uniqueResponseId);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While adding Search Request In Queue");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While adding  Search Request In Queue");
		}

		return response;
	}*/

	@Override
	public Boolean orderDrugs(OrderDrugsRequest request) {
		
		 //* TODO : when patient is request for order than send notification to
		 //* pharmacy. Pharmacy Id we will get in request
		 
		try {
			pushNotificationServices.notifyUser(request.getLocaleId(), null, RoleEnum.PHARMIST, "Keep my order ready");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While ordering drugs");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While ordering drugs");
		}
		return null;
	}

	/*private void addPharmacyResponseInCollection(PharmacyResponse request, String uniqueResponseId) {
		SearchRequestToPharmacyCollection searchRequestToPharmacyCollection = searchRequestToPharmacyRepository
				.findByRequestIdandPharmacyId(request.getUniqueRequestId(), new ObjectId(request.getLocaleId()),
						new ObjectId(request.getUserId()));
		if (searchRequestToPharmacyCollection == null) {
			throw new BusinessException(ServiceError.NoRecord, "Request not found");
		}
		searchRequestToPharmacyCollection.setUniqueResponseId(uniqueResponseId);
		;
		searchRequestToPharmacyCollection.setReplyType(request.getReplyType().getReplyType());
		searchRequestToPharmacyCollection.setUpdatedTime(new Date());
		searchRequestToPharmacyRepository.save(searchRequestToPharmacyCollection);

	}

	private String addPharmacyResponseInQueue(PharmacyResponse request) {
		AWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		sqs.setRegion(Region.getRegion(Regions.US_WEST_2));
		String searchPharmacyRequestQueueURL = null;

		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest().withQueueName(pharmacyResponseQueue);
		
		if(response != null && response.getQueueUrl() != null && !response.getQueueUrl().isEmpty())
		{
			searchPharmacyRequestQueueURL = response.getQueueUrl();
			//searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
		}
		GetQueueUrlResult response = sqs.getQueueUrl(getQueueUrlRequest);
		System.out.println(response);
		if(response != null && response.getQueueUrl() != null && !response.getQueueUrl().isEmpty())
		{
			searchPharmacyRequestQueueURL = response.getQueueUrl();
			//searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
		}
		if (DPDoctorUtils.anyStringEmpty(searchPharmacyRequestQueueURL)) {
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(pharmacyResponseQueue);
			searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
		}
		String uniqueResponseId = UniqueIdInitial.PHARMACY_RESPONSE.getInitial() + DPDoctorUtils.generateRandomId();
		request.setUniqueRequestId(uniqueResponseId);
		String messageBody = Jackson.toJsonString(request) ;
		//sendMessageRequest.setMessageBody(Jackson.toJsonString(request));
		sqs.sendMessage(new SendMessageRequest(searchPharmacyRequestQueueURL, messageBody));
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
		messageAttributeValue.setStringValue(uniqueResponseId);
		messageAttributeValue.setDataType("String");
		sendMessageRequest.addMessageAttributesEntry("uniqueResponseId", messageAttributeValue);
		sendMessageRequest.setMessageBody(Jackson.toJsonString(request));
		sqs.sendMessage(sendMessageRequest);
		return uniqueResponseId;
	}

	@Scheduled(fixedDelay = 900000)
	@Transactional
	public void retrievePharmacyResponse() {
		
		 * TODO : get data from queue, generate uniqueResponseId. if pharmacyId
		 * is present then retrieve particular pharmacy and send notification to
		 * pharmacy with prescription request & uniqueRequestId and add in
		 * collection. If pharmacyId is null then search pharmacy using latLong
		 * || location approx 30km and send notification to pharmacy with
		 * presciption request & uniqueRequestId and then add in collection
		 * After sending request to pharmacy remove that particular request from
		 * queue
		 
		AWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
		AmazonSQS sqs = new AmazonSQSClient(credentials);
		try {
			sqs.setRegion(Region.getRegion(Regions.US_WEST_2));
			String searchPharmacyRequestQueueURL = "";
			
			GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(pharmacyResponseQueue);
			System.out.println(getQueueUrlRequest);
			if (getQueueUrlRequest != null) {
				searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
			}
			searchPharmacyRequestQueueURL = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
			if (DPDoctorUtils.anyStringEmpty(searchPharmacyRequestQueueURL)) {
				CreateQueueRequest createQueueRequest = new CreateQueueRequest(pharmacyResponseQueue);
				searchPharmacyRequestQueueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
			}
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(pharmacyResponseQueue);
			receiveMessageRequest.setMaxNumberOfMessages(10);
			receiveMessageRequest.setWaitTimeSeconds(20);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				System.out.println("  Message");
				System.out.println("    MessageId:     " + message.getMessageId());
				System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
				System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
				System.out.println("    Body:          " + message.getBody());
				for (Entry<String, String> entry : message.getAttributes().entrySet()) {
					System.out.println("  Attribute");
					System.out.println("    Name:  " + entry.getKey());
					System.out.println("    Value: " + entry.getValue());
				}

				PharmacyResponse pharmacyResponse = Jackson.fromJsonString(message.getBody(), PharmacyResponse.class);
				UserSearchRequest userSearchRequest = new UserSearchRequest();
				BeanUtil.map(pharmacyResponse, userSearchRequest);

				if (userSearchRequest.getUserId() == null) {
					pushNotificationServices.notifyUser(userSearchRequest.getUserId(), userSearchRequest,
							RoleEnum.PATIENT, "You got new response for your medicine request");
				}
				
				String messageReceiptHandle =messages.get(0).getReceiptHandle(); 
				sqs.deleteMessage(new DeleteMessageRequest(pharmacyResponseQueue, messageReceiptHandle));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Prescription");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Prescription");
		}
	}
*/
	/*
	 * AWSCredentials credentials = new
	 * BasicAWSCredentials("AKIAIHOF7FWQ2ZPMKKHQ",
	 * "J+ksAueQN+ouU2uhHoO3RpfqhNZg0O0n8c61eT/m"); Credentials with profile
	 * specific and this already set in mail.properties
	 * 
	 * AmazonSQS sqs = new AmazonSQSClient(credentials); Region usWest2 =
	 * Region.getRegion(Regions.US_WEST_2); sqs.setRegion(usWest2);
	 * 
	 * Create Queue CreateQueueRequest createQueueRequest = new
	 * CreateQueueRequest("MyQueue"); String myQueueUrl =
	 * sqs.createQueue(createQueueRequest).getQueueUrl();
	 * 
	 * List Queue if required for (String queueUrl :
	 * sqs.listQueues().getQueueUrls()) { System.out.println("  QueueUrl: " +
	 * queueUrl); }
	 * 
	 * Add in Queue sqs.sendMessage(new SendMessageRequest(myQueueUrl,
	 * "I am in queue"));
	 * 
	 * Receive Message from Queue ReceiveMessageRequest receiveMessageRequest =
	 * new ReceiveMessageRequest(myQueueUrl);
	 * receiveMessageRequest.setMaxNumberOfMessages(10);
	 * receiveMessageRequest.setWaitTimeSeconds(20); List<Message> messages =
	 * sqs.receiveMessage(receiveMessageRequest).getMessages(); for (Message
	 * message : messages) { System.out.println("  Message");
	 * System.out.println("    MessageId:     " + message.getMessageId());
	 * System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
	 * System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
	 * System.out.println("    Body:          " + message.getBody()); for
	 * (Entry<String, String> entry : message.getAttributes().entrySet()) {
	 * System.out.println("  Attribute"); System.out.println("    Name:  " +
	 * entry.getKey()); System.out.println("    Value: " + entry.getValue()); }
	 * }
	 * 
	 * Delete from Queue String messageReceiptHandle =
	 * messages.get(0).getReceiptHandle(); sqs.deleteMessage(new
	 * DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
	 * 
	 * Delete Queue sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
	 */

}
