package com.unipolen.webserver.model.role;

import com.unipolen.webserver.model.role.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoleRepository extends ReactiveCrudRepository<Role, Long> {
}
