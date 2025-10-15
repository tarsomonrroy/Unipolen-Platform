package com.unipolen.webserver.model.defaults;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("public.defaults")
public class Defaults implements Persistable<String> {

	@Id
	@Column("key")
	private String id;

	@Column("value")
	private String value;

	public String getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
