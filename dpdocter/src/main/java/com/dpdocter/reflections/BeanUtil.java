package com.dpdocter.reflections;

import java.util.UUID;

import org.dozer.DozerBeanMapper;
import org.dozer.classmap.RelationshipType;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;

public class BeanUtil {
	private static final DozerBeanMapper MAPPER;

	static {
		MAPPER = new DozerBeanMapper();
		BeanMappingBuilder builder = new BeanMappingBuilder() {
			@Override
			protected void configure() {
				mapping(UUID.class, UUID.class, TypeMappingOptions.oneWay(),
						TypeMappingOptions.beanFactory(UuidBeanFactory.class.getName()),
						TypeMappingOptions.relationshipType(RelationshipType.NON_CUMULATIVE));

			}
		};
		MAPPER.addMapping(builder);
	}

	private BeanUtil() {
	}

	public static <T> void map(T source, T destination) {
		MAPPER.map(source, destination);
	}

}
