package com.unipolen.webserver.model.unit;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("public.unit_address")
public class UnitAddress implements Persistable<Long> {

	@Id
	@Column("id")
	private Long id;

	@Column("postal_code")
	private String postalCode;

	@Column("country")
	private String country;

	@Column("state")
	private String state;

	@Column("city")
	private String city;

	@Column("address")
	private String address;

	public UnitAddress(Long id, String postalCode, String country, String state, String city, String address) {
		this.id = id;
		this.postalCode = postalCode;
		this.country = country;
		this.state = state;
		this.city = city;
		this.address = address;
	}

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
