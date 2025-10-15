package com.unipolen.webserver.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("public.user_role")
public class UserRole {

	@Column("role_id")
	private Long roleId;

	@Column("user_id")
	private Long userId;

	public Long getRoleId() {return roleId;}
	public Long getUserId() {return userId;}


	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
