package com.unipolen.webserver.controller;

import com.unipolen.webserver.App;
import com.unipolen.webserver.model.course.CourseRepository;
import com.unipolen.webserver.model.ModelMapBuilder;
import com.unipolen.webserver.model.course.CourseService;
import com.unipolen.webserver.model.user.User;
import com.unipolen.webserver.view.ViewGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CoursesPage {

	@Autowired Utils utils;

	@Autowired CourseService courseService;

	private @NonNull Mono<ServerResponse> handle(ServerRequest req, int page) {
		return courseService.getPageCount().flatMap(cnt -> {
			if (page < 1 || page > cnt)
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

			return ok()
					.contentType(MediaType.TEXT_HTML)
					.body(new ViewGenerator(
									ModelMapBuilder.create()
											.withDefaults()
											.include(utils.getUserFromRequest(req), User.class)
											.include(courseService.getAllActiveInPage(page), "courses")
											.include(Mono.just(cnt), "pageCnt")
											.include(Mono.just(page), "page")
											.build())
									.get("cursos/index"),
							DataBuffer.class);
		});
	}

	public @NonNull Mono<ServerResponse> handleWithPagination(ServerRequest req) {
		try {
			return handle(req, Integer.parseInt(req.pathVariable("page")));
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	public @NonNull Mono<ServerResponse> handleWithoutPagination(ServerRequest req) {
		return handle(req, 1);
	}
}
