package com.unipolen.webserver.model.user;

import com.unipolen.webserver.model.UserRole;
import com.unipolen.webserver.model.role.Role;
import com.unipolen.webserver.model.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Component
public class UserService {
	@Autowired
	R2dbcEntityTemplate r2dbcEntityTemplate;

	@Autowired
	RoleRepository roleRepository;

	public Flux<Role> getRolesFromUser(User user) {
		assert user.getId() != null;
		return r2dbcEntityTemplate.select(UserRole.class)
				.matching(query(where("user_id").is(user.getId())))
				.all().flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
	}
}
