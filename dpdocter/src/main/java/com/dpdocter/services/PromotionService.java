package com.dpdocter.services;

import com.dpdocter.beans.InternalPromoCode;
import com.dpdocter.beans.InternalPromotionGroup;

public interface PromotionService {

	InternalPromotionGroup getPromotionGroup(String promocode);

	void addInternalPromoCode(InternalPromoCode request);

}
