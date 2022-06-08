package com.api.rest.webflux.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICrudService<Document> {

	public Flux<Document> findAll();

	public Mono<Document> findById(String id);

	public Mono<Document> save(Document document);

	public Mono<Document> update(Document document);

	public Mono<Void> delete(Document document);

}
