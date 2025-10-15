package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.user.User;
import com.unipolen.webserver.model.user.UserRepository;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Locale;
import java.util.function.Supplier;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class RegisterAction {
	@Autowired
	Utils utils;

	@Autowired
	UserRepository userRepository;

	@Autowired
	LoginAction loginAction;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {

		Context ctx = new Context();

		return utils.getUserFromRequest(req).flatMap(user ->
				ok()
					.contentType(MediaType.TEXT_HTML)
					.bodyValue(new ViewGenerator().redirect("/"))
		).switchIfEmpty(((Supplier<Mono<ServerResponse>>)() -> {
			return req.formData().flatMap(formData -> {
				String displayName = formData.getFirst("name");
				String email = formData.getFirst("email");
				String password = formData.getFirst("password");

				FormSanitizer sanitizer = new FormSanitizer();

				if (displayName == null || email == null || password == null
						|| !sanitizer.isSafe(displayName) || !sanitizer.isSafe(email) || !sanitizer.isSafe(password)) {

					ctx.setVariable("message", "Entrada Inválida");
					return badRequest()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("registrar/index"), DataBuffer.class);
				}

				String userName = displayName
						.toLowerCase(Locale.ROOT)
						.replace(' ', '_');

				BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
				String passwordHash = encoder.encode(password);

				User user = new User(email, userName, displayName, passwordHash, null, null);

				return userRepository.save(user).flatMap(user0 ->
					ok()
							.contentType(MediaType.TEXT_HTML)
							.bodyValue(new ViewGenerator().redirect("/"))
				).switchIfEmpty(((Supplier<Mono<ServerResponse>>)() -> {
					ctx.setVariable("message", "Entrada Inválida");
					return ok()
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(ctx).get("/registrar/index"), DataBuffer.class);
				}).get());
			}).switchIfEmpty(((Supplier<Mono<ServerResponse>>)() -> {
				ctx.setVariable("message", "Entrada Inválida");
				return badRequest()
						.contentType(MediaType.TEXT_HTML)
						.body(new ViewGenerator(ctx).get("registrar/index"), DataBuffer.class);
			}).get());
		}).get());
	}
}
