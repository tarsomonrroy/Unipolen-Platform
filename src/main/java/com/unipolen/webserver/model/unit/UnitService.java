package com.unipolen.webserver.model.unit;

import com.unipolen.webserver.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class UnitService {

	@Autowired
	R2dbcEntityTemplate r2dbcEntityTemplate;

	public Flux<Unit> getAllWithAddress() {
		return Flux.create(sink -> r2dbcEntityTemplate.select(Unit.class)
				.all().collectList().subscribe(
						units -> {
							//implements counting sort
							Unit[] unitsSorted = new Unit[1+units.stream().max(Comparator.comparing(Unit::getId)).get().getId().intValue()];
							AtomicInteger pending = new AtomicInteger(units.size());

							for (Unit unit: units) {
								unitsSorted[unit.getId().intValue()] = unit;
								r2dbcEntityTemplate.select(UnitAddress.class)
										.matching(query(where("id").is(unit.getAddressId())))
										.first()
										.subscribe(
												unitAddress -> {
													unitsSorted[unit.getId().intValue()].setAddress(unitAddress);
													if (pending.decrementAndGet() <= 0) {
														for (Unit value : unitsSorted) {
															if (value == null) continue;
															sink.next(value);
														}
														sink.complete();
													}
												},
												RuntimeException::new
										);
							}
						}
				));
	}
}
