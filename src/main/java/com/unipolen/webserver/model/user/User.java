package com.unipolen.webserver.model.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("public.user")
public class User implements Persistable<Long> {
	@Id
	@Column("id")
	private Long id;
	@Column("email")
	private String email;
	@Column("user_name")
	private String userName;
	@Column("display_name")
	private String displayName;
	@Column("password_hash")
	private String passwordHash;
	@Column("auth_token")
	private String authToken;

	@Column("address_id")
	private Long addressId;

	public User(String email, String userName, String displayName, String passwordHash, String authToken, Long addressId) {
		this.email = email;
		this.userName = userName;
		this.displayName = displayName;
		this.passwordHash = passwordHash;
		this.authToken = authToken;
		this.addressId = addressId;
	}

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public void setId(Long id) {this.id = id;}
	public String getEmail() {return email;}
	public void setEmail(String email) {this.email = email;}
	public String getUserName() {return userName;}
	public void setUserName(String userName) {this.userName = userName;}
	public String getDisplayName() {return displayName;}
	public void setDisplayName(String displayName) {this.displayName = displayName;}
	public String getPasswordHash() {return passwordHash;}
	public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
	public String getAuthToken() {return authToken;}
	public void setAuthToken(String authToken) {this.authToken = authToken;}

	@Override
	public String toString() {
		return String.format("User[id=%d, email='%s', userName='%s', displayName='%s', passwordHash='%s', authToken='%s']",
				id, email, userName, displayName, passwordHash, authToken);
	}

}
