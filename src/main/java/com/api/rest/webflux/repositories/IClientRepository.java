package com.api.rest.webflux.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.api.rest.webflux.documents.Client;

public interface IClientRepository extends ReactiveMongoRepository<Client, String>{

}
