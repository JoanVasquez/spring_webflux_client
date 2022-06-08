package com.api.rest.webflux.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.api.rest.webflux.documents.Client;
import com.api.rest.webflux.services.ICrudService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

	@Autowired
	private ICrudService<Client> crudService;

	@Value("${config.uploads.path}")
	private String path;

	@SuppressWarnings("deprecation")
	@PostMapping("/saveClientWithPhoto")
	public Mono<ResponseEntity<Client>> saveClientWithPhoto(Client client, @RequestPart FilePart file) {
		client.setPhoto(UUID.randomUUID().toString() + "-"
				+ file.filename().replace(" ", "").replace(":", "").replace("//", ""));

		return file.transferTo(new File(path + client.getPhoto())).then(crudService.save(client))
				.map(c -> ResponseEntity.created(URI.create("/api/clients".concat(c.getId())))
						.contentType(MediaType.APPLICATION_JSON_UTF8).body(c));
	}

	@PostMapping("/upload/{id}")
	public Mono<ResponseEntity<Client>> uploadPhoto(@PathVariable("id") String id, @RequestPart FilePart file) {
		return crudService.findById(id).flatMap(c -> {
			c.setPhoto(UUID.randomUUID().toString() + "-"
					+ file.filename().replace(" ", "").replace(":", "").replace("//", ""));

			return file.transferTo(new File(path + c.getPhoto())).then(crudService.save(c));

		}).map(c -> ResponseEntity.ok(c)).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@SuppressWarnings("deprecation")
	@GetMapping
	public Mono<ResponseEntity<Flux<Client>>> getClients() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(crudService.findAll()));
	}

	@SuppressWarnings("deprecation")
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Client>> getClientById(@PathVariable("id") String id) {
		return crudService.findById(id)
				.map(c -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).body(c))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> saveClient(@Valid @RequestBody Mono<Client> monoClient) {
		Map<String, Object> result = new HashMap<>();

		return monoClient.flatMap(client -> crudService.save(client).map(c -> {
			result.put("client", c);
			result.put("message", "client saved successfully");
			result.put("timestamp", new Date());

			return ResponseEntity.created(URI.create("/api/clients".concat(c.getId()))).body(result);
		})).onErrorResume(t -> Mono.just(t).cast(WebExchangeBindException.class)
				.flatMap(e -> Mono.just(e.getFieldErrors())).flatMapMany(Flux::fromIterable)
				.map(fieldError -> "Field : " + fieldError.getField() + " " + fieldError.getDefaultMessage())
				.collectList().flatMap(list -> {
					result.put("errors", list);
					result.put("timestamp", new Date());
					result.put("status", HttpStatus.BAD_REQUEST.value());

					return Mono.just(ResponseEntity.badRequest().body(result));
				}));
	}

	@SuppressWarnings("deprecation")
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Client>> updateClient(@PathVariable("id") String id, @RequestBody Client client) {
		return crudService.findById(id).flatMap(c -> {
			c.setFirstName(client.getFirstName());
			c.setLastName(client.getLastName());
			c.setAge(client.getAge());
			c.setSalary(client.getSalary());

			return crudService.save(c);
		}).map(c -> ResponseEntity.created(URI.create("/api/clients".concat(c.getId())))
				.contentType(MediaType.APPLICATION_JSON_UTF8).body(c))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> deleteClient(@PathVariable("id") String id) {
		return crudService.findById(id).flatMap(c -> {
			return crudService.delete(c).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

}
