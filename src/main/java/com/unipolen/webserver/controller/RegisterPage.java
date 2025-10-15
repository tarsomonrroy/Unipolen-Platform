package com.unipolen.webserver.controller;

import com.unipolen.webserver.view.ViewGenerator;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class RegisterPage {
	@Autowired
	Utils utils;

	public @Nonnull Mono<ServerResponse> handle(ServerRequest req) {
		return utils.getUserFromRequest(req).flatMap( user ->
				ok()
					.contentType(MediaType.TEXT_HTML)
					.bodyValue(new ViewGenerator().redirect("/"))
		).switchIfEmpty(
				ok()
					.contentType(MediaType.TEXT_HTML)
					.body(new ViewGenerator().get("registrar/index"), DataBuffer.class)
		);
	}
}
