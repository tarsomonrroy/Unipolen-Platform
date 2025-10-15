package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AccountPage {

	@Autowired
	Utils utils;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		return utils.getUserFromRequest(req).flatMap(user -> ok()
				.contentType(MediaType.TEXT_HTML)
				.body(
					new ViewGenerator(
							ModelMapBuilder.create()
									.withDefaults()
									.include(Mono.just(user), "user")
									.build()
					).get("conta/index"), DataBuffer.class
				)).switchIfEmpty(
				ok()
						.contentType(MediaType.TEXT_HTML)
						.bodyValue(new ViewGenerator().redirect("/"))
		);
	}
}
