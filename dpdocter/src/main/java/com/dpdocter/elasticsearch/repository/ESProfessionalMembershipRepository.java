package com.dpdocter.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.dpdocter.elasticsearch.document.ESProfessionalMembershipDocument;

public interface ESProfessionalMembershipRepository
		extends ElasticsearchRepository<ESProfessionalMembershipDocument, String> {

}
