package com.dpdocter.services;

import org.hl7.fhir.r4.model.ResourceType;

public interface HITypeResourceProcessor {

	boolean supports(ResourceType resourceType);

}
