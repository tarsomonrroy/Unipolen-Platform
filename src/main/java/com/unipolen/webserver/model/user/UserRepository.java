package com.unipolen.webserver.model.user;

import com.unipolen.webserver.model.user.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
	Mono<User> findByEmail(String email);

	Mono<User> findByUserName(String userName);

	Mono<User> findByAuthToken(String authToken);

	@Query("SELECT user_name FROM public.user WHERE id = :id")
	Mono<String> findUserNameById(Long id);
}
