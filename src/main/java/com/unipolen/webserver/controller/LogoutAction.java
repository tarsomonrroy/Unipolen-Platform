package com.unipolen.webserver.controller;

import com.unipolen.webserver.WebConfig;
import com.unipolen.webserver.model.user.UserRepository;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class LogoutAction {
	@Autowired
	Utils utils;
	@Autowired
	UserRepository userRepository;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		return utils.getUserFromRequest(req).flatMap(user -> {
			user.setAuthToken(null);
			userRepository.save(user).subscribe(
					user1 -> {},
					error -> {}
			);
			return updateCookies();
		}).switchIfEmpty(updateCookies());
	}

	private Mono<ServerResponse> updateCookies() {

		ResponseCookie authCookie = ResponseCookie.from("authToken")
				.path("/")
				.domain(WebConfig.DOMAIN)
				.httpOnly(true)
				.maxAge(0)
				.secure(true)
				.build();

		return ok()
				.contentType(MediaType.TEXT_HTML)
				.cookie(authCookie)
				.bodyValue(new ViewGenerator().redirect("/"));
	}
}
