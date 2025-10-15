package com.unipolen.webserver.controller;

import com.unipolen.webserver.WebConfig;
import com.unipolen.webserver.model.user.UserRepository;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.function.Supplier;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class LoginAction {

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {

		Context ctx = new Context();

		return utils.getUserFromRequest(req).flatMap(user ->
				ok()
				.contentType(MediaType.TEXT_HTML)
				.bodyValue(new ViewGenerator().redirect("/"))
		).switchIfEmpty(((Supplier<Mono<ServerResponse>>) () -> {
			return req.formData().flatMap(formData -> {

				String email = formData.getFirst("email");
				String password = formData.getFirst("password");

				FormSanitizer sanitizer = new FormSanitizer();

				if (!sanitizer.isSafe(email) || !sanitizer.isSafe(password)) {
					ctx.setVariable("user", null);
					ctx.setVariable("message", "Entrada Inválida");
					return badRequest()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("login/index"), DataBuffer.class);
				}


				if (email == null || password == null) {
					ctx.setVariable("user", null);
					ctx.setVariable("message", "Entrada Inválida");
					return badRequest()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("login/index"), DataBuffer.class);
				}

				return userRepository.findByEmail(email).flatMap(user -> {
					BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
					if (encoder.matches(password, user.getPasswordHash())) {



						String authToken = encoder.encode(user.getId().toString());
						ResponseCookie authCookie = ResponseCookie.from("authToken")
								.domain(WebConfig.DOMAIN)
								.httpOnly(true)
								.maxAge(48 * 3600) //2 days
								.path("/")
								.secure(true)
								.value(authToken)
								.build();

						user.setAuthToken(authToken);
						userRepository.save(user).subscribe();


						return ok()
								.contentType(MediaType.TEXT_HTML)
								.cookie(authCookie)
								.bodyValue(new ViewGenerator().redirect("/"));
					}

					ctx.setVariable("user", null);
					ctx.setVariable("message", "Entrada Inválida");

					return ok()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("login/index"), DataBuffer.class);
				}).switchIfEmpty(((Supplier<Mono<ServerResponse>>) () -> {
					ctx.setVariable("user", null);
					ctx.setVariable("message", "Entrada Inválida");
					return ok()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("login/index"), DataBuffer.class);
				}).get());
			});
		}).get());
	}
}
