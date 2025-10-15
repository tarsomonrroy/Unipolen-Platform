package com.unipolen.webserver.model.activeprocess;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ActiveProcessRepository extends ReactiveCrudRepository<ActiveProcess, Long> {
}
