package com.unipolen.webserver.model.activeprocess;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table("public.active_process")
public class ActiveProcess implements Persistable<Long> {

	@Id
	@Column("id")
	private Long id;

	@Column("type")
	private String type;

	@Column("data")
	private JSONObject data;

	@Column("meta")
	private JSONObject meta;

	@Column("created_at")
	private Timestamp createdAt;

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public ActiveProcess(String type, JSONObject data, JSONObject meta, Timestamp createdAt) {
		this.type = type;
		this.data = data;
		this.meta = meta;
		this.createdAt = createdAt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

	public JSONObject getMeta() {
		return meta;
	}

	public void setMeta(JSONObject meta) {
		this.meta = meta;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
}
