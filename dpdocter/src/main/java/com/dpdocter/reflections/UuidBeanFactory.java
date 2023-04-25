package com.dpdocter.reflections;

import java.util.UUID;

import org.dozer.BeanFactory;

public class UuidBeanFactory implements BeanFactory {

	@Override
	public Object createBean(Object sourceBean, Class<?> destinationType, String mapId) {
		if (sourceBean == null) {
			return null;
		}
		UUID source = (UUID) sourceBean;
		UUID destination = new UUID(source.getMostSignificantBits(), source.getLeastSignificantBits());
		return destination;
	}

}
