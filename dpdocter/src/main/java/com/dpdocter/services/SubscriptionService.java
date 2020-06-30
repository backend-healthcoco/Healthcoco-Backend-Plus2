package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PackageDetailObject;
import com.dpdocter.beans.Subscription;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.enums.PackageType;

public interface SubscriptionService {
	public List<SubscriptionDetail> addsubscriptionData();

	// new subscription
	public Subscription addEditSubscription(Subscription request);

	public Subscription getSubscriptionByDoctorId(String doctorId,PackageType packageName);

	public PackageDetailObject getPackageDetailByPackageName(PackageType packageName);

}
