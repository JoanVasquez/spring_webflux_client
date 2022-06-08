package com.api.rest.webflux.documents;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collation =  "clients")
@NoArgsConstructor
@Getter
@Setter
public class Client {

	@Id
	private String id;

	@NotEmpty
	private String firstName;

	@NotEmpty
	private String lastName;

	@NotNull
	private Integer age;

	@NotNull
	private Double salary;

	private String photo;

}
