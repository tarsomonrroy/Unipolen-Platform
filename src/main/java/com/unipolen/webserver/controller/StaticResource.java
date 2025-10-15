package com.unipolen.webserver.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class StaticResource {

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		ClassPathResource resource = new ClassPathResource(req.path());
		if (!resource.isFile())
			return notFound()
					.build();

		try {
			return ok()
					.contentType(MediaType.valueOf(Files.probeContentType(resource.getFile().toPath())))
					.cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
					.body(BodyInserters.fromResource(resource));
		} catch (IOException e) {
			return status(500)
					.build();
		}
	}
}
