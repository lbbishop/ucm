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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.sfs.ucm.data.Literal;
import com.sfs.ucm.util.ModelUtils;

/**
 * ProjectMember
 * 
 * @author lbbishop
 * 
 */
@Entity
@Audited
@Table(name = "projectmember")
public class ProjectMember extends EntityBase implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "User is required")
	@OneToOne
	@JoinColumn(name = "authuser_id", nullable = false)
	private AuthUser authUser;

	@NotNull(message = "Primary Role is required")
	@Size(max = 30)
	@Column(name = "primary_role", length = 30, nullable = false)
	private String primaryRole;

	@ManyToOne
	private Project project;

	/**
	 * Default constructor
	 */
	public ProjectMember() {
		super();
		init();
	}

	/**
	 * Identifier constructor
	 */
	public ProjectMember(int identifier) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);

	}

	/**
	 * Data constructor
	 * @param identifier
	 * @param authUser
	 */
	public ProjectMember(int identifier, AuthUser authUser) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.authUser = authUser;
	}
	
	/**
	 * Data constructor
	 * @param identifier
	 * @param authUser
	 */
	public ProjectMember(int identifier, AuthUser authUser, String primaryRole) {
		super();
		init();
		this.identifier = Integer.valueOf(identifier);
		this.authUser = authUser;
		this.primaryRole = primaryRole;
	}

	/**
	 * class init method
	 */
	private void init() {
		this.authUser = new AuthUser();
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
	 * @return the project
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * @param project
	 *            the project to set
	 */
	public void setProject(Project project) {
		this.project = project;
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
	 * @return the primaryRole
	 */
	public String getPrimaryRole() {
		return primaryRole;
	}

	/**
	 * @param primaryRole
	 *            the primaryRole to set
	 */
	public void setPrimaryRole(String primaryRole) {
		this.primaryRole = primaryRole;
	}

	/**
	 * @return the identifier string (PREFIX concatenated with identifier)
	 */
	public String getArtifact() {
		return ModelUtils.buildArtifactIdentifier(Literal.PREFIX_PROJECTMEMBER.toString(), this.identifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectMember [id=");
		builder.append(id);
		builder.append(", authUser=");
		builder.append(authUser);
		builder.append(", primaryRole=");
		builder.append(primaryRole);
		builder.append("]");
		return builder.toString();
	}

}
