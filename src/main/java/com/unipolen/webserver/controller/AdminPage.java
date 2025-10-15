package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.role.Role;
import com.unipolen.webserver.model.user.UserService;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AdminPage {
	@Autowired Utils utils;

	@Autowired
	UserService userService;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		return utils.getUserFromRequest(req).flatMap(user -> {
			return userService.getRolesFromUser(user).collectList().flatMap(roleList -> {
				boolean found = false;
				for (Role r: roleList) {
					if (r.getValue().equals("admin")) {
						found = true;
						break;
					}
				}

				if (!found) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

				return ok()
						.contentType(MediaType.TEXT_HTML)
						.body(new ViewGenerator().get("/admin/index"), DataBuffer.class);
			});
		}).switchIfEmpty(Mono.create(sink -> {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}));
	}
}
