package com.unipolen.webserver.model.course;

import org.jline.utils.Colors;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("public.course")
public class Course implements Persistable<Long> {
	@Id
	@Column("id")
	private Long id;

	@Column("provider_id")
	private Long providerId;

	@Column("name")
	private String name;

	@Column("duration_months")
	private Integer durationMonths;

	@Column("hours")
	private Integer hours;

	@Column("url")
	private String url;

	@Column("is_available")
	private Boolean isAvailable;

	@Column("style")
	private String style;

	@Column("degree")
	private String degree;

	@Column("qualification")
	private String qualification;

	public Long getId() {return id;}

	@Override
	public boolean isNew() {
		return id == null;
	}

	public Course(Long providerId, String name, Integer durationMonths, Integer hours, String url, Boolean isAvailable, String style, String degree, String qualification) {
		this.providerId = providerId;
		this.name = name;
		this.durationMonths = durationMonths;
		this.hours = hours;
		this.url = url;
		this.isAvailable = isAvailable;
		this.style = style;
		this.degree = degree;
		this.qualification = qualification;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDurationMonths() {
		return durationMonths;
	}

	public void setDurationMonths(Integer durationMonths) {
		this.durationMonths = durationMonths;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getAvailable() {
		return isAvailable;
	}

	public void setAvailable(Boolean available) {
		isAvailable = available;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
}
