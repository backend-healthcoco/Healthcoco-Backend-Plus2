package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.InternalPromoCode;
import com.dpdocter.beans.InternalPromotionGroup;
import com.dpdocter.collections.InternalPromoCodeCollection;
import com.dpdocter.collections.InternalPromotionGroupCollection;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.InternalPromocodeRepository;
import com.dpdocter.repository.InternalPromotionGroupRepository;
import com.dpdocter.services.PromotionService;

@Service
public class PromotionServiceImpl implements PromotionService {

	@Autowired
	private InternalPromocodeRepository internalPromocodeRepository;

	@Autowired
	private InternalPromotionGroupRepository internalPromotionGroupRepository;

	@Override
	@Transactional
	public InternalPromotionGroup getPromotionGroup(String promocode) {
		InternalPromotionGroup response = null;
		InternalPromotionGroupCollection internalPromotionGroupCollection = internalPromotionGroupRepository
				.findByPromoCode(promocode);

		if (internalPromotionGroupCollection != null) {
			response = new InternalPromotionGroup();
			BeanUtil.map(internalPromotionGroupCollection, response);
		}

		return response;
	}

	@Override
	@Transactional
	public void addInternalPromoCode(InternalPromoCode request) {
		InternalPromoCodeCollection internalPromoCodeCollection = null;
		if (request != null) {
			internalPromoCodeCollection = new InternalPromoCodeCollection();
			BeanUtil.map(request, internalPromoCodeCollection);
			internalPromoCodeCollection = internalPromocodeRepository.save(internalPromoCodeCollection);
		}
	}

}
