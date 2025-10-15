package com.unipolen.webserver.controller;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.model.role.Role;
import com.unipolen.webserver.model.user.UserService;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class AdminQuery {
	@Autowired Utils utils;

	@Autowired
	UserService userService;

	@Autowired
	DatabaseClient r2dbcDatabaseClient;

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

				if (!found)
					return status(HttpStatus.UNAUTHORIZED)
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue("{\"error\": \"Unauthorized: Not an admin\"}");

				return req.formData().flatMap(formData -> {
					try {
						String query = Objects.requireNonNull(formData.getFirst("query"));
						return r2dbcDatabaseClient.sql(query)
								.fetch()
								.all()
								.collectList()
								.flatMap(results -> {

									String message = "[";

									for (Iterator<Map<String, Object>> mit = results.iterator(); mit.hasNext();) {
										message += "{";
										for (Iterator<Map.Entry<String, Object>> eit = mit.next().entrySet().iterator(); eit.hasNext();) {
											Map.Entry<String, Object> e = eit.next();
											message += "\""+e.getKey()+"\":\""+e.getValue()+"\"";
											if (eit.hasNext()) message += ",";
										}
										message += "}";
										if (mit.hasNext()) message += ",";
									}

									message += "]";

									return ok()
											.contentType(MediaType.APPLICATION_JSON)
											.bodyValue(message);
								});
					} catch (Exception e) {
						return status(HttpStatus.INTERNAL_SERVER_ERROR)
								.contentType(MediaType.APPLICATION_JSON)
								.bodyValue("{\"error\": \""+e.getMessage()+"\"}");
					}
				});
			});
		}).switchIfEmpty(
				status(HttpStatus.UNAUTHORIZED)
						.contentType(MediaType.APPLICATION_JSON)
						.bodyValue("{\"error\": \"Unauthorized\"}")
		);
	}
}
