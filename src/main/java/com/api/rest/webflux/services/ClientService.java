package com.api.rest.webflux.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.rest.webflux.documents.Client;
import com.api.rest.webflux.repositories.IClientRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientService implements ICrudService<Client> {

	@Autowired
	private IClientRepository clientRepository;

	@Override
	public Flux<Client> findAll() {
		return clientRepository.findAll();
	}

	@Override
	public Mono<Client> findById(String id) {
		return clientRepository.findById(id);
	}

	@Override
	public Mono<Client> save(Client document) {
		return clientRepository.save(document);
	}

	@Override
	public Mono<Client> update(Client document) {
		return null;
	}

	@Override
	public Mono<Void> delete(Client document) {
		return clientRepository.delete(document);
	}

	// @Autowired
}
