package com.senomas.ngrest.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.senomas.boot.Audited;
import com.senomas.common.rs.Views;

import id.co.hanoman.avatar.model.config.AuthorityData;

@Entity
@Audited
@Table(name = "APP_ROLE", uniqueConstraints = { @UniqueConstraint(columnNames = "CODE"),
		@UniqueConstraint(columnNames = "NAME") })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	@JsonView({Views.List.class, Audited.Key.class})
	private Long id;

	@NotNull
	@Column(name = "CODE", length = 50)
	@JsonView({Views.List.class, Audited.Detail.class})
	private String code;

	@NotNull
	@Column(name = "NAME", length = 200)
	@JsonView({Views.List.class, Audited.Brief.class})
	private String name;

	@ManyToMany(cascade = {CascadeType.DETACH, CascadeType.REFRESH}, fetch=FetchType.EAGER)
	@JoinTable(name = "APP_ROLE_PRIV", joinColumns = @JoinColumn(name = "FROLE", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "FPRIV", referencedColumnName = "ID"))
	@JsonView({Views.Detail.class, Audited.Detail.class})
	private List<AuthorityData> authorities;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public List<AuthorityData> getAuthorities() {
		return authorities;
	}
	
	public void setAuthorities(List<AuthorityData> authorities) {
		this.authorities = authorities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", code=" + code + ", name=" + name + ", authorities=" + authorities + "]";
	}
}
