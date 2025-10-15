package com.unipolen.webserver.model.course;

import com.unipolen.webserver.model.course.Course;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CourseRepository extends ReactiveCrudRepository<Course, Long> {

	@Query("SELECT * FROM public.course WHERE is_available = true")
	public Flux<Course> findAllActive();
}
