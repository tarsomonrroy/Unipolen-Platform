package com.unipolen.webserver.model.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public final class CourseService {

	@Autowired
	R2dbcEntityTemplate r2dbcEntityTemplate;

	public Mono<Long> getPageCount() {
		return r2dbcEntityTemplate.select(Course.class)
				.count()
				.flatMap(cnt -> Mono.just(1+cnt/15L)).switchIfEmpty(Mono.just(0L));
	}

	public Flux<Course> getAllActiveInPage(int idx) {
		idx--;
		return r2dbcEntityTemplate.select(Course.class)
				.matching(
						query(where("is_available").isTrue())
								.offset(idx*15)
								.limit(15)
								.sort(by("id")))
				.all();
	}
}
