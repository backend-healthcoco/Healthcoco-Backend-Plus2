package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.enums.PackageType;
import com.dpdocter.request.SubscriptionPaymentSignatureRequest;
import com.dpdocter.request.SubscriptionRequest;
import com.dpdocter.response.SubscriptionResponse;
import com.dpdocter.beans.Country;

public interface SubscriptionService {
	public List<SubscriptionDetail> addsubscriptionData();

	// new subscription
	public SubscriptionResponse addEditSubscription(SubscriptionRequest request);

	public Subscription getSubscriptionByDoctorId(String doctorId,PackageType packageName,int duration,int newAmount);

	public PackageDetailObject getPackageDetailByPackageName(PackageType packageName);
	
	public Boolean verifySignature(SubscriptionPaymentSignatureRequest request);

	public List<Country> getCountry(int size, int page, Boolean isDiscarded, String searchTerm);
	
	public Integer countCountry(Boolean isDiscarded, String searchTerm);
	
	public List<PackageDetailObject> getPackages(int size, int page, Boolean isDiscarded, String searchTerm);

	public Integer countPackages(Boolean isDiscarded, String searchTerm);
	
	public List<Subscription> getSubscriptionHistory(String doctorId,int size,int page,Boolean isDiscarded,String searchTerm);
	
	public Integer countSubscriptionHistory(String doctorId,Boolean isDiscarded,String searchTerm);
	
}
