package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Country;
import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.enums.PackageType;
import com.dpdocter.request.SubscriptionPaymentSignatureRequest;
import com.dpdocter.request.SubscriptionRequest;
import com.dpdocter.response.SubscriptionResponse;

public interface SubscriptionService {
	public List<SubscriptionDetail> addsubscriptionData();

	Subscription getSubscriptionByDoctorId(String doctorId, PackageType packageName, int duration, int newAmount);

	PackageDetailObject getPackageDetailByPackageName(PackageType packageName);

	List<Country> getCountry(int size, int page, Boolean isDiscarded, String searchTerm);

	Integer countCountry(Boolean isDiscarded, String searchTerm);

	List<PackageDetailObject> getPackages(int size, int page, Boolean isDiscarded, String searchTerm);

	Integer countPackages(Boolean isDiscarded, String searchTerm);

	List<Subscription> getSubscriptionHistory(String doctorId, int size, int page, Boolean isDiscarded,
			String searchTerm);

	Integer countSubscriptionHistory(String doctorId, Boolean isDiscarded, String searchTerm);

	public SubscriptionResponse addEditSubscription(SubscriptionRequest request);

	public Boolean verifySignature(SubscriptionPaymentSignatureRequest request);

}
