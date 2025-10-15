package com.unipolen.webserver.model.role;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("public.role")
public class Role implements Persistable<Long> {

	@Id
	@Column("id")
	private Long id;

	@Column("name")
	private String name;

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public String getValue() {
		return name;
	}

	public void setValue(String name) {
		this.name = name;
	}
}
