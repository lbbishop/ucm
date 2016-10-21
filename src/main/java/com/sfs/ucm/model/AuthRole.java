/*
 * South Face Software
 * Copyright 2012, South Face Software, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.sfs.ucm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Authenticated User Roles
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name="authrole")
public class AuthRole extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@NotNull(message = "username is required")
	@Column(name = "user_name", length = 255, nullable = false)
	private String username;

	@NotNull(message = "roleName is required")
	@Column(name = "role_name", length = 255, nullable = false)
	private String roleName;

	@NotNull(message = "roleGroup is required")
	@Column(name = "role_group", length = 255, nullable = false)
	private String roleGroup;

	@ManyToOne(optional = false)
	@JoinColumn(name = "authuser_id")
	private AuthUser authUser;

	@Transient
	private boolean loggedIn;

	/**
	 * Default constructor
	 */
	public AuthRole() {
		super();
		init();
	}

	/**
	 * @param username
	 * @param roleName
	 * @param roleGroup
	 */
	public AuthRole(String username, String roleName, String roleGroup) {
		super();
		this.username = username;
		this.roleName = roleName;
		this.roleGroup = roleGroup;
	}

	/**
	 * class init method
	 */
	private void init() {

		this.loggedIn = false;

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName
	 *            the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * @return the roleGroup
	 */
	public String getRoleGroup() {
		return roleGroup;
	}

	/**
	 * @param roleGroup
	 *            the roleGroup to set
	 */
	public void setRoleGroup(String roleGroup) {
		this.roleGroup = roleGroup;
	}

	/**
	 * @return the authUser
	 */
	public AuthUser getAuthUser() {
		return authUser;
	}

	/**
	 * @param authUser
	 *            the authUser to set
	 */
	public void setAuthUser(AuthUser authUser) {
		this.authUser = authUser;
	}

	/**
	 * @return the loggedIn
	 */
	public boolean isLoggedIn() {
		return loggedIn;
	}

	/**
	 * @param loggedIn
	 *            the loggedIn to set
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthRole other = (AuthRole) obj;
		if (roleName == null) {
			if (other.roleName != null)
				return false;
		}
		else if (!roleName.equals(other.roleName))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthRole [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", roleName=");
		builder.append(roleName);
		builder.append(", roleGroup=");
		builder.append(roleGroup);
		builder.append(", loggedIn=");
		builder.append(loggedIn);
		builder.append("]");
		return builder.toString();
	}

}
