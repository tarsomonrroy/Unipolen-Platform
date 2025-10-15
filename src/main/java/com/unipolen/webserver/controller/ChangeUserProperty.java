package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.model.user.User;
import com.unipolen.webserver.model.user.UserRepository;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ChangeUserProperty {
	@Autowired Utils utils;

	@Autowired UserRepository userRepository;

	private static final HashMap<String, String> fieldToLabel;
	private static final HashMap<String, Function<User, String>> fieldToCurrentValueGetter;
	private static final HashMap<String, Consumer<Pair<User, String>>> fieldToCurrentValueSetter;
	private static final HashMap<String, String> fieldToName;

	static {
		fieldToLabel = new HashMap<>();
		fieldToLabel.put("email", "Digite o novo endereço de email:");
		fieldToLabel.put("nome", "Digite o novo nome:");
		fieldToLabel.put("senha", "Digite a nova senha:");
		fieldToLabel.put("nome-usuario", "Digite o novo nome de usuário:");

		fieldToCurrentValueGetter = new HashMap<>();
		fieldToCurrentValueGetter.put("email", User::getEmail);
		fieldToCurrentValueGetter.put("nome", User::getDisplayName);
		fieldToCurrentValueGetter.put("senha", user -> null);
		fieldToCurrentValueGetter.put("nome-usuario", User::getUserName);

		fieldToCurrentValueSetter = new HashMap<>();
		fieldToCurrentValueSetter.put("email", pair -> pair.getFirst().setEmail(pair.getSecond()));
		fieldToCurrentValueSetter.put("nome", pair -> pair.getFirst().setDisplayName(pair.getSecond()));
		fieldToCurrentValueSetter.put("senha", pair -> pair.getFirst().setPasswordHash(
				new BCryptPasswordEncoder().encode(pair.getSecond())
		));
		fieldToCurrentValueSetter.put("nome-usuario", pair -> pair.getFirst().setUserName(pair.getSecond()));

		fieldToName = new HashMap<>();
		fieldToName.put("email", "email");
		fieldToName.put("nome", "nome");
		fieldToName.put("senha", "senha");
		fieldToName.put("nome-usuario", "nome de usuário");
	}

	public @NonNull Mono<ServerResponse> displayPage(ServerRequest req) {
		String field = req.pathVariable("field");
		if (!fieldToLabel.containsKey(field))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		return utils.getUserFromRequest(req).flatMap(user ->
			ok()
					.contentType(MediaType.TEXT_HTML)
					.body(
							new ViewGenerator(
									ModelMapBuilder.create()
											.withDefaults()
											.include(Mono.just(field), "field")
											.include(Mono.just(fieldToLabel.get(field)), "label")
											.include(Mono.justOrEmpty(fieldToCurrentValueGetter.get(field).apply(user)), "currentValue")
											.include(Mono.just(fieldToName.get(field)), "fieldName")
											.include(Mono.just(user), "user")
											.build()
							).get("conta/alterar/index"), DataBuffer.class
					)
		).switchIfEmpty(
				ServerResponse.status(HttpStatus.UNAUTHORIZED)
						.contentType(MediaType.TEXT_HTML)
						.bodyValue(new ViewGenerator().redirect("/"))
		);
	}

	public @NonNull Mono<ServerResponse> handleChangeRequest(ServerRequest req) {
		String field = req.pathVariable("field");
		if (!fieldToLabel.containsKey(field))
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		return req.formData().flatMap(formData -> {
			String newValue = formData.getFirst("new-value");
			String password = formData.getFirst("password");
			FormSanitizer sanitizer = new FormSanitizer();

			return utils.getUserFromRequest(req).flatMap(user -> {

				if (newValue == null || newValue.replace(" ", "").isEmpty()
						|| !sanitizer.isSafe(newValue) || !sanitizer.isSafe(password)) {
					return ServerResponse.status(HttpStatus.BAD_REQUEST)
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(
									ModelMapBuilder.create()
											.withDefaults()
											.include(Mono.just(field), "field")
											.include(Mono.just(fieldToLabel.get(field)), "label")
											.include(Mono.justOrEmpty(fieldToCurrentValueGetter.get(field).apply(user)), "currentValue")
											.include(Mono.just(fieldToName.get(field)), "fieldName")
											.include(Mono.just(user), "user")
											.include(Mono.just("Entrada Inválida"), "message")
											.build()
							).get("/conta/alterar/index"), DataBuffer.class);
				}

				if (!(new BCryptPasswordEncoder().matches(password, user.getPasswordHash())))
					return ServerResponse.status(HttpStatus.BAD_REQUEST)
							.contentType(MediaType.TEXT_HTML)
							.body(new ViewGenerator(
									ModelMapBuilder.create()
											.withDefaults()
											.include(Mono.just(field), "field")
											.include(Mono.just(fieldToLabel.get(field)), "label")
											.include(Mono.justOrEmpty(fieldToCurrentValueGetter.get(field).apply(user)), "currentValue")
											.include(Mono.just(fieldToName.get(field)), "fieldName")
											.include(Mono.just(user), "user")
											.include(Mono.just("Senha Incorreta"), "message")
											.build()
							).get("/conta/alterar/index"), DataBuffer.class);

				fieldToCurrentValueSetter.get(field).accept(Pair.of(user, newValue));

				return userRepository.save(user)
						.onErrorReturn(new User(null,null,null,null,null,null))
						.flatMap(userUpdated -> {
							if (userUpdated.isNew())
								return ServerResponse.status(HttpStatus.BAD_REQUEST)
										.contentType(MediaType.TEXT_HTML)
										.body(new ViewGenerator(
												ModelMapBuilder.create()
														.withDefaults()
														.include(Mono.just(field), "field")
														.include(Mono.just(fieldToLabel.get(field)), "label")
														.include(Mono.justOrEmpty(fieldToCurrentValueGetter.get(field).apply(user)), "currentValue")
														.include(Mono.just(fieldToName.get(field)), "fieldName")
														.include(Mono.just(user), "user")
														.include(Mono.just("Falha ao atualizar " + fieldToName.get(field)), "message")
														.build()
										).get("/conta/alterar/index"), DataBuffer.class);

							return ok()
									.contentType(MediaType.TEXT_HTML)
									.bodyValue(new ViewGenerator().redirect("/conta"));

						}).switchIfEmpty(
								ServerResponse.status(HttpStatus.BAD_REQUEST)
										.contentType(MediaType.TEXT_HTML)
										.body(new ViewGenerator(
												ModelMapBuilder.create()
														.withDefaults()
														.include(Mono.just(field), "field")
														.include(Mono.just(fieldToLabel.get(field)), "label")
														.include(Mono.justOrEmpty(fieldToCurrentValueGetter.get(field).apply(user)), "currentValue")
														.include(Mono.just(fieldToName.get(field)), "fieldName")
														.include(Mono.just(user), "user")
														.include(Mono.just("Falha ao atualizar " + fieldToName.get(field)), "message")
														.build()
										).get("/conta/alterar/index"), DataBuffer.class)
				);

			}).switchIfEmpty(Mono.create(sink -> {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}));
		}).switchIfEmpty(Mono.create(sink -> {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}));
	}
}
