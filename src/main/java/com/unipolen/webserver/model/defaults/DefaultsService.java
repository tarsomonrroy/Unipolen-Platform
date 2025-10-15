package com.unipolen.webserver.model.defaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public final class DefaultsService {

	@Autowired
	R2dbcEntityTemplate r2dbcEntityTemplate;

	public Flux<Defaults> getAll() {
		return r2dbcEntityTemplate.select(Defaults.class)
				.all();
	}

	public Mono<Map<String, Defaults>> getAllNamed() {
		return r2dbcEntityTemplate.select(Defaults.class)
				.all().collectMap(Defaults::getId);
	}

	public Mono<Map<String, String>> getMappings() {
		return r2dbcEntityTemplate.select(Defaults.class)
				.all().collectMap(Defaults::getId, Defaults::getValue);
	}
}
