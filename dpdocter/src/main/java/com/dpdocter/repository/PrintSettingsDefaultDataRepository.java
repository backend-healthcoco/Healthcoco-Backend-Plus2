package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.PrintSettingsDefaultDataCollection;

public interface PrintSettingsDefaultDataRepository extends MongoRepository<PrintSettingsDefaultDataCollection, String> {

}
