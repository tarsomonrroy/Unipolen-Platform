package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.model.user.User;
import com.unipolen.webserver.model.user.UserRepository;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class DeleteAccountAction {

	@Autowired Utils utils;
	@Autowired UserRepository userRepository;
	@Autowired LogoutAction logoutAction;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		return req.formData().flatMap(formData -> {
			String password = formData.getFirst("password");
			FormSanitizer sanitizer = new FormSanitizer();

			return utils.getUserFromRequest(req).flatMap(user -> {
				if (!sanitizer.isSafe(password))
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

				if (!(new BCryptPasswordEncoder().matches(password, user.getPasswordHash())))
					return badRequest()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(
											ModelMapBuilder.create()
													.withDefaults()
													.include(Mono.just(user), User.class)
													.include(Mono.just("Senha Incorreta"), "message")
													.build())
											.get("conta/apagar/index"),
									DataBuffer.class);

				return logoutAction.handle(req).doOnSuccess(response -> {
					userRepository.delete(user).subscribe();
				});
			});
		}).switchIfEmpty(
				Mono.create(sink -> {throw new ResponseStatusException(HttpStatus.BAD_REQUEST);})
		);
	}
}
