package com.unipolen.webserver.model.unit;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("public.unit")
public class Unit implements Persistable<Long> {

	@Id
	@Column("id")
	private Long id;

	@Column("name")
	private String name;

	@Column("address_id")
	private Long addressId;
	private UnitAddress address;

	@Column("phone")
	private String phone;

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public Unit(String name, Long addressId) {
		this.name = name;
		this.addressId = addressId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UnitAddress getAddress() {
		return address;
	}

	public void setAddress(UnitAddress address) {
		this.address = address;
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}
}
