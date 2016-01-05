package com.dpdocter.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.dpdocter.collections.AppointmentWorkFlowCollection;

public interface AppointmentWorkFlowRepository extends MongoRepository<AppointmentWorkFlowCollection, String> {

}
