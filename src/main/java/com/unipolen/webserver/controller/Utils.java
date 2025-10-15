package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.user.User;
import com.unipolen.webserver.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class Utils {

	@Autowired
	UserRepository userRepository;

	public Mono<User> getUserFromRequest(ServerRequest req) {
		HttpCookie authTokenCookie = req.cookies().getFirst("authToken");

		if (authTokenCookie == null) return Mono.empty();

		String authToken = authTokenCookie.getValue();

		return userRepository.findByAuthToken(authToken);
	}
}
