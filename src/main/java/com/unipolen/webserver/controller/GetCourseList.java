package com.unipolen.webserver.controller;

import com.unipolen.webserver.model.course.Course;
import com.unipolen.webserver.model.course.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Iterator;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class GetCourseList {

	@Autowired Utils utils;
	@Autowired
	CourseRepository courseRepository;

	public @NonNull Mono<ServerResponse> handle(ServerRequest req) {
		return courseRepository.findAll().collectList().flatMap(courseList -> {
			String message = "[";
			for (Iterator<Course> it = courseList.iterator(); it.hasNext();) {
				message += "{";
				Course c = it.next();
				message += "\"name\": \""+c.getName()+"\",";
				message += "\"provider_id\": "+c.getProviderId()+",";
				message += "\"duration_months\": "+c.getDurationMonths()+",";
				message += "\"hours\": "+c.getHours()+",";
				message += "\"url\": \""+c.getUrl()+"\",";
				message += "\"available\": "+c.getAvailable().toString()+",";
				message += "\"degree\": \""+c.getDegree()+"\",";
				message += "\"qualification\": \""+c.getQualification()+"\",";
				message += "\"style\": \""+c.getStyle()+"\"}";
				if (it.hasNext()) message += ",";
			}
			message += "]";

			return ok()
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(message);
		});
	}
}
