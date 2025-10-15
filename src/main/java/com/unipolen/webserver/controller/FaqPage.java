package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.model.user.User;
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
public class FaqPage {

	@Autowired Utils utils;

	public @NonNull  Mono<ServerResponse> handle(ServerRequest req) {
		return ok()
				.contentType(MediaType.TEXT_HTML)
				.body(new ViewGenerator(
								ModelMapBuilder.create()
										.withDefaults()
										.include(utils.getUserFromRequest(req), User.class)
										.build())
								.get("faq/index"),
						DataBuffer.class);
	}
}
